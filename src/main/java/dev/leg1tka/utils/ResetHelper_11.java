package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;



import dev.leg1tka.modules.combat.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ResetHelper_11 extends AuraRotation implements IMinecraft {
   private final Aura aura;
   private LivingEntity trackedTarget;
   private float lastYaw;
   private float lastPitch;
   private float speedAcceleration;
   private boolean back;
   private boolean initialized;
   private float jitterOffset;
   private int tickCounter;

   public ResetHelper_11(
      Aura val
   ) {
      this.aura = val;
   }

   public void reset() {
      this.trackedTarget = null;
      this.speedAcceleration = 0.0F;
      this.back = false;
      this.jitterOffset = 0.0F;
      this.tickCounter = 0;
      this.initialized = mc.player != null;
      AuraTransition.INSTANCE.reset();
      if (mc.player != null) {
         this.lastYaw = mc.player.getYaw();
         this.lastPitch = mc.player.getPitch();
      } else {
         this.lastYaw = 0.0F;
         this.lastPitch = 0.0F;
      }
   }

   public void onAttack() {
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null
         && entity
            != null) {
         if (!AuraTransition.INSTANCE.handleTransitions()) {
            if (mc.player.isBlocking()) {
               this.rotate = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
               this.lastYaw = this.rotate.x;
               this.lastPitch = this.rotate.y;
            } else {
               if (!this.initialized) {
                  this.lastYaw = mc.player.getYaw();
                  this.lastPitch = mc.player.getPitch();
                  this.initialized = true;
               }

               if (this.trackedTarget
                  != entity
                  )
                {
                  this.trackedTarget = entity;
                  this.speedAcceleration = 0.0F;
                  this.back = false;
                  this.tickCounter = 0;
               }

               this.tickCounter++;
               this.jitterOffset = (float)((Math.sin((double)this.tickCounter * 0.17) * 0.12 + (Math.random() * 0.08 - 0.04)) * 0.7F);
               Vec3d vec = RaycastUtil.getMultipoint(
                  entity,
                  128.0
               );
               Vec2f rot = RayTraceUtil.getRotations(
                  vec
               );
               float yaw = rot.x;
               float yaw2 = rot.y;
               float yaw3 = Math.abs(
                  MathHelper.wrapDegrees(
                     yaw
                        - this.lastYaw
                  )
               );
               boolean flag = mc.player
                        .getAttackCooldownProgress(1.0F)
                     > 0.9F
                  && this.aura.getWhiteRiseTicksToAttack() <= 1;
               if (!this.back) {
                  float yaw4 = 0.0055F;
                  if (yaw3
                     > 60.0F) {
                     yaw4 += 0.028800001F;
                  } else if (yaw3
                     > 30.0F) {
                     yaw4 += 0.014400001F;
                  } else {
                     yaw4 += 0.0072000003F;
                  }

                  if (flag
                     )
                   {
                     yaw4 += 0.012857143F;
                  }

                  this.speedAcceleration = this.speedAcceleration
                     + yaw4
                        * (1.6F + this.jitterOffset);
                  if (this.speedAcceleration >= 0.22F) {
                     this.back = true;
                  }
               } else {
                  float yaw5 = flag
                     ? 0.045F
                     : 0.008F;
                  this.speedAcceleration = this.speedAcceleration
                     - yaw5
                        * (2.1F + this.jitterOffset);
                  if (this.speedAcceleration <= -0.04F) {
                     this.back = false;
                  }
               }

               float yaw6 = MathHelper.clamp(
                  this.speedAcceleration, 0.0F, mc.player.isGliding() ? 0.38F : 0.26F
               );
               if (flag
                  )
                {
                  yaw6 = Math.min(
                     yaw6
                        + 0.1F,
                     mc.player.isGliding() ? 0.46F : 0.34F
                  );
               }

               yaw6 += this.jitterOffset
                  * 0.5F;
               if (this.tickCounter % 7 == 0) {
                  yaw6 += 0.03F;
               }

               float yaw7 = MathHelper.wrapDegrees(
                  yaw
                     - this.lastYaw
               );
               float yaw8 = yaw2
                  - this.lastPitch;
               float yaw9 = mc.player
                     .isGliding()
                  ? 42.0F
                  : (
                     flag
                        ? 28.0F
                        : 20.0F
                  );
               float yaw10 = mc.player
                     .isGliding()
                  ? 12.0F
                  : (
                     flag
                        ? 4.5F
                        : 2.8F
                  );
               yaw7 = MathHelper.clamp(
                  yaw7,
                  -yaw9,
                  yaw9
               );
               yaw8 = MathHelper.clamp(
                  yaw8,
                  -yaw10,
                  yaw10
               );
               float yaw11 = yaw6
                  * 0.28F;
               float yaw12 = yaw6
                  * (0.85F + this.jitterOffset * 0.4F);
               float yaw13 = this.lastYaw
                  + yaw7
                     * yaw12;
               float yaw14 = this.lastPitch
                  + yaw8
                     * yaw11;
               float yaw15 = MouseGCD.getGCDValue();
               if (yaw15
                  > 0.0F) {
                  yaw13 = this.lastYaw
                     + (float)Math.round(
                           (
                                 yaw13
                                    - this.lastYaw
                              )
                              / yaw15
                        )
                        * yaw15;
                  yaw14 = this.lastPitch
                     + (float)Math.round(
                           (
                                 yaw14
                                    - this.lastPitch
                              )
                              / yaw15
                        )
                        * yaw15;
               }

               yaw14 = MathHelper.clamp(
                  yaw14,
                  -89.0F,
                  89.0F
               );
               float[] floats = AuraTransition.INSTANCE
                  .process(
                     yaw13,
                     yaw14,
                     entity,
                     "whiterise"
                  );
               Angle val = new Angle(
                  floats[0],
                  floats[1]
               );
               float yaw16 = mc.player
                        .isGliding()
                     && entity.isGliding()
                  ? 360.0F
                  : 45.0F;
               RotationStorage.update(
                  val,
                  yaw16,
                  yaw16,
                  yaw16,
                  yaw16,
                  0,
                  1,
                  Aura.clientLook.isState()
               );
               this.rotate = new Vec2f(
                  val.getYaw(),
                  val.getPitch()
               );
               this.lastYaw = val.getYaw();
               this.lastPitch = val.getPitch();
            }
         }
      }
   }

   private Vec3d getAimPoint(
      LivingEntity entity
   ) {
      Vec3d vec = RaycastUtil.getPoint(
         entity
      );
      if (vec
         == null) {
         vec = entity.getBoundingBox()
            .getCenter();
      }

      return this.shouldUseElytraPredict(
            entity
         )
         ? getPredictedPoint(
            entity,
            vec
         )
         : vec;
   }
}
