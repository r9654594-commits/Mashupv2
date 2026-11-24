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
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ResetHelper_8 extends AuraRotation {
   private final Aura aura;
   private float speedAcceleration = 0.0F;
   private boolean back = false;
   private float backAcceleration = 0.0F;
   private boolean returning = false;
   private long snapBackStartMs = -1L;
   private float lastYaw;
   private float lastPitch;
   private int lastTargetId = -2147483648;
   private int tickCounter = 0;
   private float jitterOffset = 0.0F;

   public ResetHelper_8(
      Aura val
   ) {
      this.aura = val;
      if (mc.player != null) {
         this.lastYaw = mc.player.getYaw();
         this.lastPitch = mc.player.getPitch();
      }
   }

   public void reset() {
      this.speedAcceleration = 0.0F;
      this.backAcceleration = 0.0F;
      this.back = false;
      this.returning = false;
      this.snapBackStartMs = -1L;
      this.tickCounter = 0;
      this.jitterOffset = 0.0F;
      this.lastTargetId = -2147483648;
      if (mc.player != null) {
         this.lastYaw = mc.player.getYaw();
         this.lastPitch = mc.player.getPitch();
      }
   }

   public void onAttack() {
      this.aura.getAttackTimer().reset();
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null) {
         boolean flag = entity
               != null
            && this.aura.getTarget() != null;
         if (flag
            )
          {
            this.returning = false;
            this.snapBackStartMs = -1L;
            this.backAcceleration = 0.0F;
            if (entity.getId()
               != this.lastTargetId) {
               this.lastTargetId = entity.getId();
               this.speedAcceleration = 0.0F;
               this.back = false;
               this.tickCounter = 0;
            }

            this.rotateToTarget(
               entity
            );
         } else {
            this.lastTargetId = -2147483648;
            this.speedAcceleration = 0.0F;
            this.back = false;
            this.snapBack();
         }
      }
   }

   private void rotateToTarget(
      LivingEntity target
   ) {
      this.tickCounter++;
      this.jitterOffset = (float)(Math.sin((double)this.tickCounter * 0.19) * 0.1 + (ThreadLocalRandom.current().nextDouble() * 0.06 - 0.03)) * 0.65F;
      double dist = (double)mc.player
            .distanceTo(
               target
            )
         + 2.5;
      Vec3d vec = RaycastUtil.getMultipoint(
         target,
         Math.max(
            6.0,
            dist
         )
      );
      if (vec
         == null) {
         vec = target.getBoundingBox()
            .getCenter();
      }

      if (this.shouldUseElytraPredict(
         target
      )) {
         vec = getPredictedPoint(
            target,
            vec
         );
      }

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
         > 0.9F;
      if (!this.back) {
         float yaw4 = 0.006F;
         if (yaw3
            > 55.0F) {
            yaw4 += 0.02F;
         } else if (yaw3
            > 25.0F) {
            yaw4 += 0.01F;
         } else {
            yaw4 += 0.005F;
         }

         if (flag
            )
          {
            yaw4 += 0.015F;
         }

         this.speedAcceleration = this.speedAcceleration
            + yaw4
               * (1.5F + this.jitterOffset);
         if (this.speedAcceleration >= 0.24F) {
            this.back = true;
         }
      } else {
         float yaw5 = flag
            ? 0.048F
            : 0.009F;
         this.speedAcceleration = this.speedAcceleration
            - yaw5
               * (2.0F + this.jitterOffset);
         if (this.speedAcceleration <= -0.04F) {
            this.back = false;
         }
      }

      float yaw6 = MathHelper.clamp(
         this.speedAcceleration, 0.0F, mc.player.isGliding() ? 0.4F : 0.28F
      );
      if (flag
         )
       {
         yaw6 = Math.min(
            yaw6
               + 0.12F,
            mc.player.isGliding() ? 0.48F : 0.36F
         );
      }

      yaw6 += this.jitterOffset
         * 0.4F;
      if (this.tickCounter % 8 == 0) {
         yaw6 += 0.025F;
      }

      float yaw7 = MathHelper.wrapDegrees(
         yaw
            - this.lastYaw
      );
      float yaw8 = yaw2
         - this.lastPitch;
      float yaw9 = mc.player
            .isGliding()
         ? 45.0F
         : (
            flag
               ? 32.0F
               : 22.0F
         );
      float yaw10 = mc.player
            .isGliding()
         ? 14.0F
         : (
            flag
               ? 5.5F
               : 3.2F
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
         * (0.88F + this.jitterOffset * 0.35F);
      float yaw12 = yaw6
         * 0.3F;
      float yaw13 = this.lastYaw
         + yaw7
            * yaw11;
      float yaw14 = this.lastPitch
         + yaw8
            * yaw12;
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
      this.lastYaw = yaw13;
      this.lastPitch = yaw14;
      RotationStorage.update(
         new Angle(
            yaw13,
            yaw14
         ),
         360.0F,
         360.0F,
         360.0F,
         360.0F,
         0,
         1,
         Aura.clientLook.isState()
      );
      this.rotate = new Vec2f(
         yaw13,
         yaw14
      );
   }

   private void snapBack() {
      if (mc.player != null) {
         float yaw = mc.player
            .getYaw();
         float yaw2 = mc.player
            .getPitch();
         float yaw3 = MathHelper.wrapDegrees(
            yaw
               - this.lastYaw
         );
         float yaw4 = yaw2
            - this.lastPitch;
         float yaw5 = (float)Math.hypot(
            (double)yaw3,
            (double)yaw4
         );
         if (yaw5
            < 0.5F) {
            this.lastYaw = yaw;
            this.lastPitch = yaw2;
            this.returning = false;
         } else {
            if (!this.returning) {
               this.returning = true;
               this.snapBackStartMs = System.currentTimeMillis();
               this.backAcceleration = 0.0F;
            }

            float yaw6 = (float)(
               System.currentTimeMillis() - this.snapBackStartMs
            );
            float yaw7 = MathHelper.clamp(
               1.0F
                  - yaw6
                     / 800.0F,
               0.0F,
               1.0F
            );
            if (yaw7
               <= 0.0F) {
               this.lastYaw = yaw;
               this.lastPitch = yaw2;
               this.returning = false;
            } else {
               this.backAcceleration = MathHelper.clamp(this.backAcceleration + 0.08F, 0.0F, 0.6F);
               float yaw8 = this.backAcceleration
                  * yaw7;
               float yaw9 = (float)(
                  Math.sin(
                        (double)yaw6
                           / 55.0
                     )
                     * 1.4F
                     * (double)yaw7
               );
               float yaw10 = (float)(
                  Math.cos(
                        (double)yaw6
                           / 55.0
                     )
                     * 0.6F
                     * (double)yaw7
               );
               float yaw11 = this.lastYaw
                  + yaw3
                     * yaw8
                  + yaw9;
               float yaw12 = this.lastPitch
                  + yaw4
                     * yaw8
                  + yaw10;
               float yaw13 = MouseGCD.getGCDValue();
               if (yaw13
                  > 0.0F) {
                  yaw11 = this.lastYaw
                     + (float)Math.round(
                           (
                                 yaw11
                                    - this.lastYaw
                              )
                              / yaw13
                        )
                        * yaw13;
                  yaw12 = this.lastPitch
                     + (float)Math.round(
                           (
                                 yaw12
                                    - this.lastPitch
                              )
                              / yaw13
                        )
                        * yaw13;
               }

               yaw12 = MathHelper.clamp(
                  yaw12,
                  -89.0F,
                  89.0F
               );
               this.lastYaw = yaw11;
               this.lastPitch = yaw12;
               RotationStorage.update(
                  new Angle(
                     yaw11,
                     yaw12
                  ),
                  360.0F,
                  360.0F,
                  360.0F,
                  360.0F,
                  0,
                  1,
                  Aura.clientLook.isState()
               );
               this.rotate = new Vec2f(
                  yaw11,
                  yaw12
               );
            }
         }
      }
   }
}
