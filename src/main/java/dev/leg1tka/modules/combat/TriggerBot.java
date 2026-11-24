package dev.leg1tka.modules.combat;
import dev.leg1tka.modules.movement.Sprint;
import dev.leg1tka.modules.player.AutoEat;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventMoveInput;
import dev.leg1tka.api.events.implement.EventUpdate;
import lombok.Generated;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.AxeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public class TriggerBot extends Module {
   public static TriggerBot INSTANCE = new TriggerBot();
   private final NumberSetting range = new NumberSetting(
      "Дистанция атаки",
      3.0F,
      0.0F,
      6.0F,
      0.05F
   );
   private final MultiBooleanSetting options;
   private final MultiBooleanSetting targets;
   private LivingEntity target;
   private final FinishedHelper attackTimer;
   private boolean needSprintReset;
   private boolean sprintResetDone;
   private int sprintResetTicks;

   public TriggerBot() {
      super(
         "TriggerBot",
         "Автоматически атакует при наведении на цель",
         Category.COMBAT
      );
      String var10003 = "Опции";
      BooleanSetting[] var10004 = new BooleanSetting[5];
      var10004[0] = new BooleanSetting(
         "Умные криты",
         true
      );
      var10004[1] = new BooleanSetting(
         "Сброс спринта",
         true
      );
      var10004[2] = new BooleanSetting(
         "Проверка на наведение",
         true
      );
      var10004[3] = new BooleanSetting(
         "Отжимать щит",
         false
      );
      var10004[4] = new BooleanSetting(
         "Ломать щит",
         true
      );
      this.options = new MultiBooleanSetting(var10003, var10004);
      var10003 = "Таргеты";
      var10004 = new BooleanSetting[4];
      var10004[0] = new BooleanSetting(
         "Игроки",
         true
      );
      var10004[1] = new BooleanSetting(
         "Невидимки",
         true
      );
      var10004[2] = new BooleanSetting(
         "Мирные", false
      );
      var10004[3] = new BooleanSetting(
         "Мобы",
         true
      );
      this.targets = new MultiBooleanSetting(var10003, var10004);
      this.attackTimer = new FinishedHelper();
      this.needSprintReset = false;
      this.sprintResetDone = false;
      this.sprintResetTicks = 0;
      this.addSettings(new Setting[]{
         this.range,
         this.options,
         this.targets
      });
   }

   @EventLink
   public void onMoveInput(
      EventMoveInput val
   ) {
      if (this.needSprintReset) {
         val.setForward(
            0.0F
         );
         val.setStrafe(
            0.0F
         );
         this.needSprintReset = false;
         this.sprintResetDone = true;
         this.sprintResetTicks = 0;
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      if (mc.player != null && mc.world != null) {
         if (AutoEat.shouldSuppressCombat()) {
            this.target = null;
            this.resetSprintState();
         } else {
            if (this.sprintResetDone) {
               this.sprintResetTicks++;
            }

            this.target = this.getTargetUnderCrosshair();
            if (this.target != null) {
               this.processAttack();
            } else {
               this.resetSprintState();
            }
         }
      }
   }

   private void processAttack() {
      if (this.shouldAttack()) {
         if (this.options
               .is(
                  "Сброс спринта"
               )
            && mc.player.isSprinting()
            && !this.sprintResetDone
            && !this.shouldSkipSprintResetInWater()) {
            this.needSprintReset = true;
         } else if (!this.options
               .is(
                  "Сброс спринта"
               )
            || !this.sprintResetDone
            || this.sprintResetTicks >= 1) {
            this.attack();
            this.sprintResetDone = false;
            this.sprintResetTicks = 0;
         }
      }
   }

   private LivingEntity getTargetUnderCrosshair() {
      Vec3d vec = mc.player
         .getCameraPosVec(1.0F);
      Vec3d vec2 = mc.player
         .getRotationVec(1.0F);
      float f = this.range
         .getValue()
         .floatValue();
      Vec3d vec3 = vec.add(
         vec2.multiply(
            (double)f
         )
      );
      EntityHitResult entityHit = ProjectileUtil.raycast(
         mc.player,
         vec,
         vec3,
         mc.player
            .getBoundingBox()
            .expand(
               (double)f
            ),
         val -> val
                  != mc.player
               && val.isAlive()
               && val instanceof LivingEntity,
         (double)(
            f
               * f
         )
      );
      return entityHit
               != null
            && entityHit.getEntity() instanceof LivingEntity val2
            && this.isValidTarget(
               val2
            )
         ? val2
         : null;
   }

   private void attack() {
        if (this.target == null || mc.player == null || !mc.player.canSee(this.target)) return;
      if (this.options
            .is(
               "Отжимать щит"
            )
         && mc.player.isBlocking()) {
         mc.interactionManager.stopUsingItem(mc.player);
      }

      label19: {
         if (this.target instanceof PlayerEntity val
            && val.isBlocking()
            && this.options
               .is(
                  "Ломать щит"
               )) {
            this.shieldBreak(
               val
            );
            break label19;
         }

         mc.interactionManager.attackEntity(mc.player, this.target);
      }

      mc.player.swingHand(Hand.MAIN_HAND);
      this.attackTimer.reset();
   }

   private void shieldBreak(
      PlayerEntity val
   ) {
      int slot = this.findAxeSlot();
      if (slot
         != -1) {
         int n = mc.player
            .getInventory()
            .selectedSlot;
         mc.player.getInventory().selectedSlot = slot;
         mc.interactionManager
            .attackEntity(
               mc.player,
               val
            );
         mc.player.swingHand(Hand.MAIN_HAND);
         mc.player.getInventory().selectedSlot = n;
      } else {
         mc.interactionManager
            .attackEntity(
               mc.player,
               val
            );
      }
   }

   private int findAxeSlot() {
      for (int i = 0;
         i
            < 9;
         i++
      ) {
         if (mc.player
            .getInventory()
            .getStack(
               i
            )
            .getItem() instanceof AxeItem) {
            return i;
         }
      }

      return -1;
   }

   private boolean isValidTarget(
      LivingEntity val
   ) {
      if (val
            != null
         && val
            != mc.player) {
         if (!val.isAlive()
            || val.getHealth()
               <= 0.0F) {
            return false;
         } else if (val instanceof ArmorStandEntity
            )
          {
            return false;
         } else {
            if (val instanceof PlayerEntity val2
               )
             {
               if (!this.targets
                  .is(
                     "Игроки"
                  )) {
                  return false;
               }

               if (val2.hasStatusEffect(
                     StatusEffects.INVISIBILITY
                  )
                  && !this.targets
                     .is(
                        "Невидимки"
                     )) {
                  return false;
               }

               if (AspectDLC.INSTANCE
                  .friendStorage
                  .isFriend(
                     val.getName()
                        .getString()
                  )) {
                  return false;
               }
            } else if (val instanceof PassiveEntity
               || val instanceof CodEntity
               )
             {
               if (!this.targets
                  .is(
                     "Мирные"
                  )) {
                  return false;
               }
            } else if (val instanceof HostileEntity
               && !this.targets
                  .is(
                     "Мобы"
                  )) {
               return false;
            }

            return mc.player
                     .getEyePos()
                     .distanceTo(
                        val.getBoundingBox()
                           .getCenter()
                     )
                  > (double)this.range.getValue().floatValue()
               ? false
               : mc.player
                     .canSee(
                        val
                     );
         }
      } else {
         return false;
      }
   }

   private boolean shouldAttack() {
      if (mc.player.getAttackCooldownProgress(1.5F) < GetaicooldownHelper.getAICooldown()) {
         return false;
      } else {
         if (this.options
            .is(
               "Проверка на наведение"
            )) {
            Vec3d vec = mc.player
               .getCameraPosVec(1.0F);
            Vec3d vec2 = mc.player
               .getRotationVec(1.0F);
            float f = this.range
               .getValue()
               .floatValue();
            Vec3d vec3 = vec.add(
               vec2.multiply(
                  (double)f
               )
            );
            EntityHitResult entityHit = ProjectileUtil.raycast(
               mc.player,
               vec,
               vec3,
               mc.player
                  .getBoundingBox()
                  .expand(
                     (double)f
                  ),
               val -> val
                        != mc.player
                     && val.isAlive(),
               (double)(
                  f
                     * f
               )
            );
            if (entityHit
                  == null
               || entityHit.getEntity()
                  != this.target) {
               return false;
            }
         }

         return !this.options
               .is(
                  "Умные криты"
               )
            || GetaicooldownHelper.canCritical(this.target);
      }
   }

   private void resetSprintState() {
      this.sprintResetDone = false;
      this.sprintResetTicks = 0;
   }

   private boolean shouldSkipSprintResetInWater() {
      return mc.player != null
         && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater())
         && Sprint.INSTANCE != null
         && Sprint.INSTANCE.shouldKeepSprintInWater();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.target = null;
      this.needSprintReset = false;
      this.sprintResetDone = false;
      this.sprintResetTicks = 0;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.needSprintReset = false;
      this.sprintResetDone = false;
      this.sprintResetTicks = 0;
   }

   @Generated
   public LivingEntity getTarget() {
      return this.target;
   }
}
