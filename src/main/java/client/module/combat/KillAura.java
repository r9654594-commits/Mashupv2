package client.module.combat;

import client.module.Category;
import client.module.Module;
import client.setting.BooleanSetting;
import client.setting.ListSetting;
import client.setting.SliderSetting;
import client.util.KeyBindings;
import client.util.TargetSelector;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import java.util.Arrays;
import java.util.List;

public class KillAura extends Module {
    private final SliderSetting range;
    private final BooleanSetting onlyPlayers;
    private final BooleanSetting autoBlock;
    private final ListSetting rotationMode;
    private final SliderSetting rotationSpeed;
    
    private int attackCooldown = 0;
    private float currentYaw = 0;
    private float currentPitch = 0;

    public KillAura() {
        super("KillAura", Category.COMBAT);
        
        this.range = new SliderSetting("", "Дистанция", 3.0, 1.0, 6.0, 0.1);
        this.onlyPlayers = new BooleanSetting("", "Только игроки", true);
        this.autoBlock = new BooleanSetting("", "Авто-блок", false);
        
        this.rotationMode = new ListSetting("", "Ротация", 
            Arrays.asList("Shard", "SpookyTime", "FunTime", "SkyTime"), 
            List.of("Shard"), false);
            
        this.rotationSpeed = new SliderSetting("", "Скорость ротации", 50.0, 1.0, 100.0, 1.0);
        
        this.addSettings(this.range, this.onlyPlayers, this.autoBlock, this.rotationMode, this.rotationSpeed);
    }

    @Override
    public void onEnable() {
        this.attackCooldown = 0;
        if (this.player() != null) {
            this.currentYaw = this.player().getYaw();
            this.currentPitch = this.player().getPitch();
        }
    }

    @Override
    public void onDisable() {
        KeyBindings.update4();
    }

    @Override
    public void update3() {
        if (this.notInGame() || this.player() == null) return;

        LivingEntity target = null;
        double closestDist = Double.MAX_VALUE;
        double maxRange = this.range.getValue();

        for (LivingEntity entity : this.world().getEntitiesByClass(LivingEntity.class, this.player().getBoundingBox().expand(maxRange), e -> e != this.player() && e.isAlive())) {
            if (this.onlyPlayers.isFlag3() && !(entity instanceof PlayerEntity)) continue;
            if (this.isFriend(entity)) continue;

            double dist = this.player().distanceTo(entity);
            if (dist <= maxRange && dist < closestDist) {
                closestDist = dist;
                target = entity;
            }
        }

        if (target != null) {
            float[] targetRot = TargetSelector.getRotations(target);
            String mode = (String)this.rotationMode.getList3().getFirst();
            
            float speed = (float) (this.rotationSpeed.getValue() / 10.0f);
            
            // Обработка разных ротаций
            switch (mode) {
                case "Shard":
                    // Плавно ведем камеру
                    this.currentYaw += (targetRot[0] - this.currentYaw) / speed;
                    this.currentPitch += (targetRot[1] - this.currentPitch) / speed;
                    break;
                case "SpookyTime":
                    // Очень плавно (античит)
                    this.currentYaw += (targetRot[0] - this.currentYaw) / (speed * 2.0f);
                    this.currentPitch += (targetRot[1] - this.currentPitch) / (speed * 2.0f);
                    break;
                case "FunTime":
                    // Быстрый снап с микро-задержкой
                    if (Math.abs(targetRot[0] - this.currentYaw) > 5.0f) {
                        this.currentYaw = targetRot[0];
                    }
                    this.currentPitch = targetRot[1];
                    break;
                case "SkyTime":
                    // Мгновенно
                    this.currentYaw = targetRot[0];
                    this.currentPitch = targetRot[1];
                    break;
            }

            this.player().setYaw(this.currentYaw);
            this.player().setPitch(this.currentPitch);

            // Бьем
            if (this.attackCooldown <= 0) {
                if (this.autoBlock.isFlag3() && this.player().getOffHandStack().getItem() != net.minecraft.item.Items.AIR) {
                    KeyBindings.update6(); 
                }
                
                if (this.player().getAttackCooldownProgress(0.0F) >= 1.0F) {
                    this.client().interactionManager.attackEntity(this.player(), target);
                    this.player().swingHand(Hand.MAIN_HAND);
                    
                    // Разная задержка удара под сервера
                    if (mode.equals("Shard")) this.attackCooldown = 8;
                    else if (mode.equals("SpookyTime")) this.attackCooldown = 12;
                    else if (mode.equals("FunTime")) this.attackCooldown = 5;
                    else this.attackCooldown = 3;
                }
            }
        } else {
            // Если цели нет, плавно возвращаем камеру
            if (this.player() != null) {
                this.currentYaw = this.player().getYaw();
                this.currentPitch = this.player().getPitch();
            }
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }
}