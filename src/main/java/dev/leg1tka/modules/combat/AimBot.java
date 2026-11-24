package dev.leg1tka.modules.combat;


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

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventGameUpdate;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventTickPre;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class AimBot extends Module {
   public static AimBot INSTANCE = new AimBot();
   private final ModeSetting aimMode;
   private final MultiBooleanSetting aimItems;
   private final MultiBooleanSetting targetTypes;
   private final NumberSetting range;
   private final NumberSetting aimTime;
   private final BooleanSetting silentRotations;
   private final BooleanSetting showCrosshair;
   private final NumberSetting crosshairSize;
   private final BooleanSetting smartMode;
   private LivingEntity target;
   private boolean isAiming;
   private float aimProgress;
   private Angle targetRotation;
   private Packet<?> pendingShootPacket;
   private boolean isSendingPending;
   private Angle pendingRotation;
   private int delayTicks;

   public AimBot() {
      super("AimBot", Category.COMBAT);
      String var1 = "AimBot";
      String var3 = "Режим наведения";
      String var14 = "Всегда";
      this.aimMode = new ModeSetting(var3, var14, new String[]{"Всегда", "Когда стреляешь"});
      String var5 = "Наводить из предмета";
      BooleanSetting[] var15 = new BooleanSetting[3];
      var15[0] = new BooleanSetting("Лук", true);
      var15[1] = new BooleanSetting("Арбалет", true);
      int var10006 = 2;
      var15[var10006] = new BooleanSetting("Трезубец", true);
      this.aimItems = new MultiBooleanSetting(var5, var15);
      String var7 = "Типы целей";
      BooleanSetting[] var16 = new BooleanSetting[5];
      var16[0] = new BooleanSetting("Игроки", true);
      var16[1] = new BooleanSetting("В броне", true);
      var10006 = 2;
      var16[var10006] = new BooleanSetting("Без брони", false);
      var10006 = 3;
      var16[var10006] = new BooleanSetting("Мобы", false);
      var10006 = 4;
      var16[var10006] = new BooleanSetting("Зомби", false);
      this.targetTypes = new MultiBooleanSetting(var7, var16);
      this.range = new NumberSetting("Дистанция", 40.0F, 10.0F, 100.0F, 1.0F);
      this.aimTime = new NumberSetting("Время наводки (тики)", 10.0F, 0.0F, 40.0F, 1.0F)
         .visible(
            () -> this.aimMode.is("Всегда")
         );
      this.silentRotations = new BooleanSetting("Тихие повороты", true);
      this.showCrosshair = new BooleanSetting("Показать прицел", true);
      this.crosshairSize = new NumberSetting("Размер прицела", 1.0F, 0.3F, 3.0F, 0.1F);
      this.smartMode = new BooleanSetting("Умный режим", false);
      this.target = null;
      this.isAiming = false;
      this.aimProgress = 0.0F;
      this.targetRotation = null;
      this.pendingShootPacket = null;
      this.isSendingPending = false;
      this.pendingRotation = null;
      this.delayTicks = 0;
      this.addSettings(new Setting[]{
         this.aimMode,
         this.aimItems,
         this.targetTypes,
         this.range,
         this.aimTime,
         this.silentRotations,
         this.showCrosshair,
         this.crosshairSize,
         this.smartMode
      });
   }

   private Identifier getCrosshairTexture() {
      return ClientIdentifier.of("textures/cross/hit.png");
   }

   private boolean isValidAimItem(
      ItemStack val
   ) {
      if (val.isEmpty()
         )
       {
         return false;
      } else {
         Item item2 = val.getItem();
         MultiBooleanSetting var10000 = this.aimItems;
         if (var10000.is("Лук")
            && item2 instanceof BowItem
            )
          {
            return true;
         } else {
            var10000 = this.aimItems;
            if (var10000.is("Арбалет")
               && item2 instanceof CrossbowItem
               )
             {
               return true;
            } else {
               var10000 = this.aimItems;
               return var10000.is("Трезубец")
                  && item2 instanceof TridentItem;
            }
         }
      }
   }

   private boolean isHoldingBowOrCrossbow() {
      return this.isValidAimItem(mc.player.getMainHandStack()) || this.isValidAimItem(mc.player.getOffHandStack());
   }

   private boolean isUsingBowOrCrossbow() {
      if (!this.isHoldingBowOrCrossbow()) {
         return false;
      } else {
         ItemStack stack = mc.player
            .getMainHandStack();
         ItemStack stack2 = mc.player
            .getOffHandStack();
         if (stack.getItem() instanceof CrossbowItem
            && CrossbowItem.isCharged(
               stack
            )) {
            return true;
         } else {
            return stack2.getItem() instanceof CrossbowItem
                  && CrossbowItem.isCharged(
                     stack2
                  )
               ? true
               : mc.player.isUsingItem();
         }
      }
   }

   private boolean isValidTarget(
      LivingEntity val
   ) {
      if (val
         == mc.player) {
         return false;
      } else if (!val.isAlive()
         || val.getHealth()
            <= 0.0F) {
         return false;
      } else if (val instanceof PlayerEntity
         )
       {
         MultiBooleanSetting var7 = this.targetTypes;
         if (!var7.is("Игроки")) {
            return false;
         } else if (AspectDLC.INSTANCE
            .friendStorage
            .isFriend(
               val.getName()
                  .getString()
            )) {
            return false;
         } else {
            boolean flag = false;
            PlayerEntity player2 = (PlayerEntity)val;

            for (ItemStack stack : player2.getArmorItems()) {
               if (!stack.isEmpty()
                  )
                {
                  flag = true;
                  break;
               }
            }

            var7 = this.targetTypes;
            if (var7.is("В броне")
               && flag
               )
             {
               return true;
            } else {
               var7 = this.targetTypes;
               if (var7.is("Без брони")
                  && !flag
                  )
                {
                  return true;
               } else {
                  var7 = this.targetTypes;
                  if (!var7.is("В броне")) {
                     var7 = this.targetTypes;
                     if (!var7.is("Без брони")) {
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      } else if (val instanceof ZombieEntity
         )
       {
         MultiBooleanSetting var6 = this.targetTypes;
         return var6.is("Зомби");
      } else if (val instanceof HostileEntity
         )
       {
         MultiBooleanSetting var10000 = this.targetTypes;
         return var10000.is("Мобы");
      } else {
         return false;
      }
   }

   private LivingEntity findBestTarget() {
      List<LivingEntity> entity2 = new ArrayList<>();
      Box box = mc.player
         .getBoundingBox()
         .expand((double)this.range.getValue().floatValue());

      for (LivingEntity entity3 : mc.world
         .getEntitiesByClass(
            LivingEntity.class,
            box,
            val -> true
         )) {
         if (this.isValidTarget(
            entity3
         )) {
            double d = (double)mc.player
               .distanceTo(
                  entity3
               );
            if (!(
               d
                  > (double)this.range.getValue().floatValue()
            )) {
               entity2.add(
                  entity3
               );
            }
         }
      }

      if (entity2.isEmpty()
         )
       {
         return null;
      } else {
         entity2.sort(
            Comparator.comparingDouble(
               val2 -> (double)mc.player
                     .distanceTo(
                        val2
                     )
            )
         );
         return entity2.get(
            0
         );
      }
   }

   private Angle calculateBowRotation(
      LivingEntity val
   ) {
      Vec3d vec = mc.player
         .getEyePos();
      Vec3d vec2 = val.getBoundingBox()
         .getCenter();
      if (this.smartMode.isState()
         && (double)mc.player
               .distanceTo(
                  val
               )
            <= 4.0) {
         vec2 = val.getPos()
            .add(
               0.0,
               (double)val.getHeight()
                  * 0.2,
               0.0
            );
      }

      double d = vec2.x
         - vec.x;
      double d2 = vec2.y
         - vec.y;
      double d3 = vec2.z
         - vec.z;
      double d4 = Math.sqrt(
         d
               * d
            + d3
               * d3
      );
      float f = (float)Math.toDegrees(
            Math.atan2(
               d3,
               d
            )
         )
         - 90.0F;
      float f2 = (float)(
         -Math.toDegrees(
            Math.atan2(
               d2,
               d4
            )
         )
      );
      return new Angle(
         f,
         f2
      );
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (this.showCrosshair.isState() && this.target != null && this.isAiming) {
         float f = val.getTickDelta();
         double d = MathHelper.lerp(
               (double)f,
               this.target.lastRenderY,
               this.target.getY()
            )
            + (double)this.target.getHeight() / 2.0;
         if (this.smartMode.isState() && (double)mc.player.distanceTo(this.target) <= 4.0) {
            d = MathHelper.lerp(
                  (double)f,
                  this.target.lastRenderY,
                  this.target.getY()
               )
               + (double)this.target.getHeight() * 0.2;
         }

         Vec3d vec = new Vec3d(
            MathHelper.lerp(
               (double)f,
               this.target.lastRenderX,
               this.target.getX()
            ),
            d,
            MathHelper.lerp(
               (double)f,
               this.target.lastRenderZ,
               this.target.getZ()
            )
         );
         Vec3d vec2 = mc.gameRenderer
            .getCamera()
            .getPos();
         MatrixStack matrices = val.getMatrices();
         double d2 = vec.x
            - vec2.x;
         double d3 = vec.y
            - vec2.y;
         double d4 = vec.z
            - vec2.z;
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShaderTexture(0, this.getCrosshairTexture());
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         matrices.push();
         matrices.translate(
            d2,
            d3,
            d4
         );
         matrices.multiply(
            RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw())
         );
         matrices.multiply(
            RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch())
         );
         float f2 = this.crosshairSize
               .get()
            * 0.5F;
         int n = (int)(
            255.0F * this.aimProgress
         );
         int color2 = ColorUtil.getThemeColor();
         int n2 = color2
               >> 16
            & 255;
         int n3 = color2
               >> 8
            & 255;
         int n4 = color2
            & 255;
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(
               matrix,
               -f2,
               -f2,
               0.0F
            )
            .texture(0.0F, 1.0F)
            .color(
               n2,
               n3,
               n4,
               n
            );
         buffer.vertex(
               matrix,
               -f2,
               f2,
               0.0F
            )
            .texture(0.0F, 0.0F)
            .color(
               n2,
               n3,
               n4,
               n
            );
         buffer.vertex(
               matrix,
               f2,
               f2,
               0.0F
            )
            .texture(1.0F, 0.0F)
            .color(
               n2,
               n3,
               n4,
               n
            );
         buffer.vertex(
               matrix,
               f2,
               -f2,
               0.0F
            )
            .texture(1.0F, 1.0F)
            .color(
               n2,
               n3,
               n4,
               n
            );
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         matrices.pop();
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   @EventLink
   public void onGameUpdate(
      EventGameUpdate val4
   ) {
      if (mc.player != null && mc.world != null) {
         this.isAiming = this.isUsingBowOrCrossbow();
         if (this.isAiming) {
            LivingEntity entity2 = this.findBestTarget();
            if (entity2
               != null) {
               if (this.target
                  != entity2
                  )
                {
                  this.target = entity2;
                  this.aimProgress = 0.0F;
               }

               Angle val = this.calculateBowRotation(
                  this.target
               );
               float f = 1.0F
                  / Math.max(1.0F, this.aimTime.getValue().floatValue());
               this.aimProgress = Math.min(
                  this.aimProgress
                     + f,
                  1.0F
               );
               float y2 = mc.player
                  .getYaw();
               float y3 = mc.player
                  .getPitch();
               float yaw = val.getYaw();
               float pitch = val.getPitch();
               float f2 = MathHelper.wrapDegrees(
                  yaw
                     - y2
               );
               float f3 = pitch
                  - y3;
               float f4 = f2
                  * this.aimProgress;
               float f5 = f3
                  * this.aimProgress;
               this.targetRotation = new Angle(
                  y2
                     + f4,
                  y3
                     + f5
               );
            }
         } else {
            this.target = null;
            this.targetRotation = null;
            this.aimProgress = 0.0F;
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventGameUpdate val4
   ) {
      if (this.target != null && this.isAiming && this.targetRotation != null) {
         ModeSetting var10000 = this.aimMode;
         if (var10000.is("Когда стреляешь")) {
            return;
         }

         if (this.silentRotations.isState()) {
            float f = MouseGCD.getGCD();
            float f2 = this.targetRotation
               .getYaw();
            float f3 = this.targetRotation
               .getPitch();
            f2 -= (
                  f2
                     - mc.player.getYaw()
               )
               % f;
            f3 -= (
                  f3
                     - mc.player.getPitch()
               )
               % f;
            RotationStorage.update(
               new Angle(
                  f2,
                  f3
               ),
               180.0F,
               180.0F,
               45.0F,
               45.0F,
               0,
               2,
               false
            );
         } else {
            mc.player.setYaw(this.targetRotation.getYaw());
            mc.player.setPitch(this.targetRotation.getPitch());
         }
      }
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (val.getType()
         == EventPacket.Type.SEND) {
         if (!this.isSendingPending) {
            if (mc.player != null && this.target != null && this.isAiming) {
               ModeSetting var10000 = this.aimMode;
               if (var10000.is("Когда стреляешь")) {
                  if (val.getPacket() instanceof PlayerActionC2SPacket val2
                     )
                   {
                     if (val2.getAction()
                        == Action.RELEASE_USE_ITEM) {
                        this.pendingRotation = this.calculateBowRotation(this.target);
                        this.pendingShootPacket = val2;
                        this.applyPendingRotation();
                        this.delayTicks = 1;
                        val.cancel();
                     }
                  } else if (val.getPacket() instanceof PlayerInteractItemC2SPacket val3
                     )
                   {
                     ItemStack stack = mc.player
                        .getMainHandStack();
                     ItemStack stack2 = mc.player
                        .getOffHandStack();
                     if (stack.getItem() instanceof CrossbowItem
                           && CrossbowItem.isCharged(
                              stack
                           )
                        || stack2.getItem() instanceof CrossbowItem
                           && CrossbowItem.isCharged(
                              stack2
                           )) {
                        this.pendingRotation = this.calculateBowRotation(this.target);
                        this.pendingShootPacket = val3;
                        this.applyPendingRotation();
                        this.delayTicks = 1;
                        val.cancel();
                     }
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onTickPre(
      EventTickPre val4
   ) {
      if (this.pendingShootPacket != null && this.delayTicks > 0) {
         this.delayTicks--;
         if (this.delayTicks <= 0) {
            this.isSendingPending = true;
            mc.getNetworkHandler().sendPacket(this.pendingShootPacket);
            this.isSendingPending = false;
            this.pendingShootPacket = null;
            this.pendingRotation = null;
         }
      }
   }

   private void applyPendingRotation() {
      if (this.pendingRotation != null) {
         if (this.silentRotations.isState()) {
            float f = MouseGCD.getGCD();
            float f2 = this.pendingRotation
               .getYaw();
            float f3 = this.pendingRotation
               .getPitch();
            f2 -= (
                  f2
                     - mc.player.getYaw()
               )
               % f;
            f3 -= (
                  f3
                     - mc.player.getPitch()
               )
               % f;
            RotationStorage.update(
               new Angle(
                  f2,
                  f3
               ),
               360.0F,
               360.0F,
               45.0F,
               45.0F,
               1,
               2,
               false
            );
         } else {
            mc.player.setYaw(this.pendingRotation.getYaw());
            mc.player.setPitch(this.pendingRotation.getPitch());
         }
      }
   }

   public LivingEntity getTarget() {
      return this.target;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.target = null;
      this.isAiming = false;
      this.aimProgress = 0.0F;
      this.targetRotation = null;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.target = null;
      this.isAiming = false;
      this.aimProgress = 0.0F;
      this.targetRotation = null;
   }
}
