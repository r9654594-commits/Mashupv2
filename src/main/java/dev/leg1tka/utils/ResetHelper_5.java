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

public class ResetHelper_5 extends AuraRotation {
   private final Aura aura;
   private final GetmultiplierHelper hitboxHelper = new GetmultiplierHelper();
   private LivingEntity lastTarget;
   private float lastOutputYaw;
   private float lastOutputPitch;
   private float smoothedYawSpeed;
   private float smoothedPitchSpeed;
   private boolean initialized;
   private float breathingOffset;
   private int tickCounter;
   private int frameCounter;
   private float currentYaw;
   private float currentPitch;
   private float yawVelocity;
   private float pitchVelocity;
   private boolean rotationActive;
   private float jitterYaw;
   private float jitterPitch;
   private float driftX;
   private float driftY;
   private float driftZ;

   public ResetHelper_5(
      Aura val
   ) {
      this.aura = val;
   }

   public void reset() {
      this.lastTarget = null;
      this.smoothedYawSpeed = 0.0F;
      this.smoothedPitchSpeed = 0.0F;
      this.breathingOffset = 0.0F;
      this.tickCounter = 0;
      this.frameCounter = 0;
      this.rotationActive = false;
      this.currentYaw = 0.0F;
      this.currentPitch = 0.0F;
      this.yawVelocity = 0.0F;
      this.pitchVelocity = 0.0F;
      this.jitterYaw = 0.0F;
      this.jitterPitch = 0.0F;
      this.driftX = 0.0F;
      this.driftY = 0.0F;
      this.driftZ = 0.0F;
      this.initialized = mc.player != null;
      if (mc.player != null) {
         this.lastOutputYaw = mc.player.getYaw();
         this.lastOutputPitch = mc.player.getPitch();
      } else {
         this.lastOutputYaw = 0.0F;
         this.lastOutputPitch = 0.0F;
      }
   }

   public void onAttack() {
   }

   private float getGCDValue() {
      return MouseGCD.getGCDValue();
   }

   private float tanhClamp(
      float f,
      float f2
   ) {
      if (f2
         <= 0.0F) {
         return 0.0F;
      } else {
         double d = (double)(
            f
               / f2
         );
         return (float)(
            (double)f2
               * Math.tanh(
                  d
               )
         );
      }
   }

   private float epochWave(
      float f,
      boolean flag,
      float f2
   ) {
      float y = (float)(
            System.currentTimeMillis() % 1000L
         )
         / f2;
      return (float)(
            flag
               ? Math.cos(
                  (double)y
               )
               : Math.sin(
                  (double)y
               )
         )
         * f;
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null
         && entity
            != null) {
         if (mc.player.isBlocking()) {
            this.rotate = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
            this.lastOutputYaw = this.rotate.x;
            this.lastOutputPitch = this.rotate.y;
            this.rotationActive = false;
         } else {
            if (!this.initialized) {
               this.lastOutputYaw = mc.player.getYaw();
               this.lastOutputPitch = mc.player.getPitch();
               this.currentYaw = this.lastOutputYaw;
               this.currentPitch = this.lastOutputPitch;
               this.yawVelocity = 0.0F;
               this.pitchVelocity = 0.0F;
               this.rotationActive = true;
               this.initialized = true;
            }

            if (this.lastTarget
               != entity
               )
             {
               this.lastTarget = entity;
               this.smoothedYawSpeed = 0.0F;
               this.smoothedPitchSpeed = 0.0F;
               this.tickCounter = 0;
               this.frameCounter = 0;
               this.lastOutputYaw = mc.player.getYaw();
               this.lastOutputPitch = mc.player.getPitch();
               this.currentYaw = this.lastOutputYaw;
               this.currentPitch = this.lastOutputPitch;
               this.yawVelocity = 0.0F;
               this.pitchVelocity = 0.0F;
               this.rotationActive = true;
            }

            this.tickCounter++;
            this.frameCounter++;
            this.breathingOffset = (float)(Math.sin((double)this.tickCounter * 0.09) * 0.08 * 0.5);
            Vec3d vec = GetpointHelper.getMultipoint(
               entity,
               128.0
            );
            if (this.shouldUseElytraPredict(
               entity
            )) {
               vec = getPredictedPoint(
                  entity,
                  vec
               );
            }

            this.driftX = MathHelper.clamp(this.driftX * 0.965F + (float)(Math.random() - 0.5) * 0.16F, -0.33F, 0.33F);
            this.driftZ = MathHelper.clamp(this.driftZ * 0.965F + (float)(Math.random() - 0.5) * 0.16F, -0.33F, 0.33F);
            this.driftY = MathHelper.clamp(this.driftY * 0.98F + (float)(Math.random() - 0.5) * 0.02F, -0.06F, 0.06F);
            vec = vec.add(
               (double)this.driftX, (double)this.driftY, (double)this.driftZ
            );
            Vec2f rot = RayTraceUtil.getRotations(
               vec
            );
            float yaw = rot.x;
            float yaw2 = rot.y;
            float yaw3 = Math.abs(
               MathHelper.wrapDegrees(
                  yaw
                     - this.lastOutputYaw
               )
            );
            float yaw4 = mc.player
                  .isGliding()
               ? 0.38F
               : 0.28F;
            if (yaw3
               > 90.0F) {
               yaw4 += 0.22F;
            } else if (yaw3
               > 45.0F) {
               yaw4 += 0.13F;
            } else if (yaw3
               > 20.0F) {
               yaw4 += 0.06F;
            }

            float yaw5 = Math.abs(
               yaw2
                  - this.lastOutputPitch
            );
            float yaw6;
            if (yaw3
               > 60.0F) {
               yaw6 = yaw4;
            } else if (yaw3
               > 30.0F) {
               yaw6 = yaw4
                  * 0.8F;
            } else if (yaw3
               > 10.0F) {
               yaw6 = yaw4
                  * 0.6F;
            } else {
               yaw6 = yaw4
                  * 0.4F;
            }

            this.smoothedYawSpeed = this.smoothedYawSpeed
               + (
                     yaw6
                        - this.smoothedYawSpeed
                  )
                  * 0.2F;
            float yaw7 = MathHelper.clamp(
               this.smoothedYawSpeed,
               0.0F,
               yaw4
            );
            float yaw8 = mc.player
                  .isGliding()
               ? 0.24F
               : 0.18F;
            float yaw9;
            if (yaw5
               > 15.0F) {
               yaw9 = yaw8;
            } else if (yaw5
               > 6.0F) {
               yaw9 = yaw8
                  * 0.6F;
            } else {
               yaw9 = yaw8
                  * 0.3F;
            }

            this.smoothedPitchSpeed = this.smoothedPitchSpeed
               + (
                     yaw9
                        - this.smoothedPitchSpeed
                  )
                  * 0.14F;
            float yaw10 = MathHelper.clamp(
               this.smoothedPitchSpeed,
               0.0F,
               yaw8
            );
            if (this.frameCounter <= 3) {
               yaw7 *= 0.2F
                  * (float)this.frameCounter;
               yaw10 *= 0.2F
                  * (float)this.frameCounter;
            }

            if (!this.rotationActive) {
               this.lastOutputYaw = mc.player.getYaw();
               this.lastOutputPitch = mc.player.getPitch();
               this.currentYaw = this.lastOutputYaw;
               this.currentPitch = this.lastOutputPitch;
               this.yawVelocity = 0.0F;
               this.pitchVelocity = 0.0F;
               this.rotationActive = true;
            }

            float yaw11 = MathHelper.wrapDegrees(
               yaw
                  - this.currentYaw
            );
            float yaw12 = yaw2
               - this.currentPitch;
            ThreadLocalRandom val = ThreadLocalRandom.current();
            float yaw13 = 0.9F
               + val.nextFloat()
                  * 0.2F;
            float yaw14 = (
                  mc.player.isGliding() ? 150.0F : 120.0F
               )
               * yaw13;
            float yaw15 = (
                  mc.player.isGliding() ? 24.0F : 18.0F
               )
               * yaw13;
            yaw11 = this.tanhClamp(
               yaw11,
               yaw14
            );
            yaw12 = this.tanhClamp(
               yaw12,
               yaw15
            );
            boolean flag = System.currentTimeMillis()
                  % 500L
               >= 350L;
            float yaw16 = flag
               ? 0.0F
               : 1.0F;
            float yaw17 = yaw7
               * (0.95F + this.breathingOffset * 0.4F)
               * yaw16
               * 1.4F;
            float yaw18 = yaw10
               * 0.55F
               * yaw16;
            float yaw19 = yaw11
               * yaw17;
            float yaw20 = yaw12
               * yaw18;
            float yaw21 = 0.09F
               + val.nextFloat()
                  * 0.03F;
            float yaw22 = 0.12F
               + val.nextFloat()
                  * 0.04F;
            float yaw23 = Math.abs(
               yaw11
            );
            float yaw24 = Math.abs(
               yaw12
            );
            float yaw25 = yaw23
                  < 3.0F
               ? 0.5F
                  + 0.5F
                     * (
                        yaw23
                           / 3.0F
                     )
               : 1.0F;
            float yaw26 = yaw24
                  < 3.0F
               ? 0.5F
                  + 0.5F
                     * (
                        yaw24
                           / 3.0F
                     )
               : 1.0F;
            float yaw27 = yaw21
               * yaw25;
            float yaw28 = yaw22
               * yaw26;
            this.yawVelocity = this.yawVelocity
               + (
                     yaw19
                        - this.yawVelocity
                  )
                  * yaw27;
            this.pitchVelocity = this.pitchVelocity
               + (
                     yaw20
                        - this.pitchVelocity
                  )
                  * yaw28;
            boolean flag2 = yaw3
                  < 15.0F
               && yaw5
                  < 10.0F;
            boolean flag3 = flag2
               && mc.player.getAttackCooldownProgress(1.0F) > 0.8F;
            if (flag3
               )
             {
               this.yawVelocity *= 0.75F;
               this.pitchVelocity *= 0.75F;
               this.yawVelocity = this.tanhClamp(this.yawVelocity, 22.0F);
               this.pitchVelocity = this.tanhClamp(this.pitchVelocity, 11.0F);
            } else {
               this.yawVelocity = this.tanhClamp(this.yawVelocity, 38.0F);
               this.pitchVelocity = this.tanhClamp(this.pitchVelocity, 22.0F);
            }

            this.currentYaw = this.currentYaw + this.yawVelocity;
            this.currentPitch = this.currentPitch + this.pitchVelocity;
            this.currentPitch = MathHelper.clamp(this.currentPitch, -89.0F, 89.0F);
            float yaw29 = this.getGCDValue();
            double x2 = yaw29
                  > 0.0F
               ? (double)yaw29
               : 0.005;
            float yaw30 = Math.abs(
               this.yawVelocity
            );
            float yaw31 = Math.abs(
               this.pitchVelocity
            );
            float yaw32 = Math.max(
               0.1F,
               1.0F
                  - yaw30
                     / 5.0F
            );
            float yaw33 = Math.max(
               0.1F,
               1.0F
                  - yaw31
                     / 2.0F
            );
            double x3 = x2
               * 0.16
               * (double)yaw32;
            double x4 = x2
               * 0.12
               * (double)yaw33;
            float yaw34 = 0.88F;
            this.jitterYaw = this.jitterYaw
                  * yaw34
               + (float)(
                     val.nextGaussian()
                        * x3
                  )
                  * (
                     1.0F
                        - yaw34
                  );
            this.jitterPitch = this.jitterPitch
                  * yaw34
               + (float)(
                     val.nextGaussian()
                        * x4
                  )
                  * (
                     1.0F
                        - yaw34
                  );
            float yaw35 = this.currentYaw
               + this.jitterYaw;
            float yaw36 = this.currentPitch
               + this.jitterPitch;
            if (mc.player.getAttackCooldownProgress(1.0F) > 0.85F) {
               float yaw37 = (
                     val.nextFloat()
                           * 2.0F
                        - 1.0F
                  )
                  * yaw29
                  * 0.12F;
               float yaw38 = (
                     val.nextFloat()
                           * 2.0F
                        - 1.0F
                  )
                  * yaw29
                  * 0.08F;
               yaw35 += yaw37;
               yaw36 += yaw38;
            }

            float yaw39 = this.hitboxHelper
               .getMultiplier(
                  entity
               );
            yaw35 += this.epochWave(
               (0.4F + (float)Math.random() * 0.1F)
                  * yaw39
                  * yaw16,
               false,
               19.0F
            );
            yaw36 += this.epochWave(
               (0.05F + (float)Math.random() * 0.0F)
                  * yaw39
                  * yaw16,
               false,
               32.0F
            );
            float yaw40 = this.lastOutputYaw;
            float yaw41 = this.lastOutputPitch;
            if (yaw29
               > 0.0F) {
               float yaw42 = yaw35
                  - this.lastOutputYaw;
               float yaw43 = yaw36
                  - this.lastOutputPitch;
               int i = Math.round(
                  yaw42
                     / yaw29
               );
               int i2 = Math.round(
                  yaw43
                     / yaw29
               );
               int i3 = Math.max(
                  1,
                  Math.round(
                     22.0F
                        / yaw29
                  )
               );
               i2 = MathHelper.clamp(
                  i2,
                  -i3,
                  i3
               );
               yaw40 = this.lastOutputYaw
                  + (float)i
                     * yaw29;
               yaw41 = this.lastOutputPitch
                  + (float)i2
                     * yaw29;
            } else {
               yaw40 = yaw35;
               yaw41 = yaw36;
            }

            yaw41 = MathHelper.clamp(
               yaw41,
               -89.0F,
               89.0F
            );
            if (yaw29
               > 0.0F) {
               if (Math.abs(
                     MathHelper.wrapDegrees(
                        this.currentYaw
                           - yaw40
                     )
                  )
                  > yaw29
                     * 4.0F) {
                  this.currentYaw = yaw40;
                  this.yawVelocity *= 0.92F;
               }

               if (Math.abs(
                     this.currentPitch
                        - yaw41
                  )
                  > yaw29
                     * 4.0F) {
                  this.currentPitch = yaw41;
                  this.pitchVelocity *= 0.92F;
               }
            }

            Angle val2 = new Angle(
               yaw40,
               yaw41
            );
            float yaw44 = mc.player
                     .isGliding()
                  && entity.isGliding()
               ? 360.0F
               : 180.0F;
            RotationStorage.update(
               val2,
               yaw44,
               yaw44,
               yaw44,
               yaw44,
               0,
               1,
               false
            );
            this.rotate = new Vec2f(
               val2.getYaw(),
               val2.getPitch()
            );
            this.lastOutputYaw = val2.getYaw();
            this.lastOutputPitch = val2.getPitch();
         }
      }
   }
}
