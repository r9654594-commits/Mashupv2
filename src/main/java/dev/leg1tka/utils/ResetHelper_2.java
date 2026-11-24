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
import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventGameUpdate;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ResetHelper_2 extends AuraRotation implements IMinecraft {
   private final Aura aura;
   private final GetmultiplierHelper hitboxHelper = new GetmultiplierHelper();
   private LivingEntity target;
   private float lastOutputYaw;
   private float lastOutputPitch;
   private float speedProgress;
   private boolean speedReversing;
   private boolean initialized;
   private float randomJitterPhase;
   private int tickCounter;
   private float speedTarget;
   private float speedMin;
   private float speedScale;
   private float currentYaw;
   private float currentPitch;
   private float yawVelocity;
   private float pitchVelocity;
   private boolean active;
   private int freezeTicks;
   private float freezeProgress;
   private float prevYawVel;
   private float prevPitchVel;
   private float prevYawMove;
   private float prevPitchMove;
   private float smoothYawAccel;
   private float smoothPitchAccel;
   private float noiseYaw;
   private float noisePitch;
   private float driftYaw;
   private final int[] periodArray;
   private int periodIndex;
   private long nextShuffleTime;
   private float extraYawOffset;
   private float extraPitchOffset;
   private int stateCounter;
   private int directionSign;
   private float baseOffset;
   private boolean returning;
   private long returnStartTime;

   public ResetHelper_2() {
      this(null);
   }

   public ResetHelper_2(
      Aura val
   ) {
      int[] var10001 = new int[2];
      var10001[0] = 16;
      var10001[1] = 21;
      this.periodArray = var10001;
      this.stateCounter = -1;
      this.aura = val;
      this.directionSign = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
      this.shufflePeriods();
   }

   public void reset() {
      EventInvoker.unregister(this);
      this.target = null;
      this.returning = false;
      this.returnStartTime = 0L;
      this.speedProgress = 0.0F;
      this.speedReversing = false;
      this.randomJitterPhase = 0.0F;
      this.tickCounter = 0;
      this.active = false;
      this.currentYaw = 0.0F;
      this.currentPitch = 0.0F;
      this.yawVelocity = 0.0F;
      this.pitchVelocity = 0.0F;
      this.freezeTicks = 0;
      this.freezeProgress = 1.0F;
      this.prevYawVel = 0.0F;
      this.prevPitchVel = 0.0F;
      this.smoothYawAccel = 0.0F;
      this.smoothPitchAccel = 0.0F;
      this.noiseYaw = 0.0F;
      this.noisePitch = 0.0F;
      this.driftYaw = 0.0F;
      this.extraYawOffset = 0.0F;
      this.extraPitchOffset = 0.0F;
      this.stateCounter = -1;
      this.shufflePeriods();
      this.initialized = mc.player != null;
      if (mc.player != null) {
         this.lastOutputYaw = mc.player.getYaw();
         this.lastOutputPitch = mc.player.getPitch();
      } else {
         this.lastOutputYaw = 0.0F;
         this.lastOutputPitch = 0.0F;
      }
   }

   public boolean isActive() {
      return this.active;
   }

   public void onAttack() {
   }

   public void returnToCamera() {
      if (mc.player != null && this.initialized && FreeLookStorage.isActive() && !this.returning) {
         EventInvoker.unregister(this);
         EventInvoker.register(this);
         this.returnStartTime = System.currentTimeMillis();
         this.target = null;
         this.returning = true;
         this.freezeTicks = 0;
         this.freezeProgress = 1.0F;
      }
   }

   @EventLink
   public void onReturnTick(
      EventGameUpdate event
   ) {
      if (this.returning) {
         if (this.aura != null && this.aura.isEnable() && this.aura.getTarget() != null) {
            this.returning = false;
            this.returnStartTime = 0L;
            EventInvoker.unregister(this);
         } else if (System.currentTimeMillis() - this.returnStartTime >= 2000L) {
            if (RotationStorage.instance != null) {
               RotationStorage.instance.stopRotation();
            }

            this.reset();
         } else {
            this.updateRotations(null);
         }
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

   private void shufflePeriods() {
      ThreadLocalRandom val = ThreadLocalRandom.current();

      for (int i = this.periodArray.length
            - 1;
         i
            > 0;
         i--
      ) {
         int i2 = val.nextInt(
            i
               + 1
         );
         int i3 = this.periodArray[i];
         this.periodArray[i] = this.periodArray[i2];
         this.periodArray[i2] = i3;
      }

      this.periodIndex = 0;
      this.nextShuffleTime = System.currentTimeMillis() + 6000L;
   }

   private float getNextPeriod() {
      long time = System.currentTimeMillis();

      while (
         time
            >= this.nextShuffleTime
      ) {
         int i = this.periodArray[this.periodIndex];
         this.periodIndex++;
         if (this.periodIndex == this.periodArray.length) {
            ThreadLocalRandom val = ThreadLocalRandom.current();

            for (int i2 = this.periodArray.length
                  - 1;
               i2
                  > 0;
               i2--
            ) {
               int i3 = val.nextInt(
                  i2
                     + 1
               );
               int i4 = this.periodArray[i2];
               this.periodArray[i2] = this.periodArray[i3];
               this.periodArray[i3] = i4;
            }

            if (this.periodArray[0]
               == i
               )
             {
               int i5 = val.nextInt(
                  1, this.periodArray.length
               );
               int i6 = this.periodArray[0];
               this.periodArray[0] = this.periodArray[i5];
               this.periodArray[i5] = i6;
            }

            this.periodIndex = 0;
         }

         this.nextShuffleTime += 6000L;
      }

      return (float)this.periodArray[this.periodIndex];
   }

   private float getGCD() {
      return MouseGCD.getGCDValue();
   }

   private float compressAngle(
      float f,
      float f2,
      ThreadLocalRandom val
   ) {
      if (f2
         <= 0.0F) {
         return 0.0F;
      } else {
         float x = Math.signum(
            f
         );
         float x2 = Math.abs(
            f
         );
         float x3 = f2
            * (
               0.85F
                  + val.nextFloat()
                     * 0.3F
            );
         float x4 = x3
            * (
               0.5F
                  + val.nextFloat()
                     * 0.15F
            );
         if (x2
            <= x4
            )
          {
            return f;
         } else {
            float x5 = x2
               - x4;
            float x6 = x3
               - x4;
            float x7 = x6
               * (
                  x5
                     / (
                        x5
                           + x6
                     )
               );
            float x8 = Math.min(
               x5
                  * 0.1F,
               x6
                  * 0.8F
            );
            return x
               * (
                  x4
                     + x7
                     + x8
               );
         }
      }
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null) {
         boolean flag = entity
            == null;
         if (!flag
            || this.active) {
            if (!flag
               )
             {
               this.returning = false;
               this.returnStartTime = 0L;
            } else {
               this.returning = true;
            }

            if (!flag
               && mc.player.isBlocking()) {
               this.rotate = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
               this.lastOutputYaw = this.rotate.x;
               this.lastOutputPitch = this.rotate.y;
               this.active = false;
               return;
            }

            ThreadLocalRandom val = ThreadLocalRandom.current();
            if (!this.initialized) {
               this.speedProgress = val.nextFloat()
                  * 0.05F;
               this.speedReversing = false;
               this.tickCounter = 0;
               this.speedTarget = 0.18F
                  + val.nextFloat()
                     * 0.1F;
               this.speedMin = -0.02F
                  - val.nextFloat()
                     * 0.05F;
               this.speedScale = 0.8F
                  + val.nextFloat()
                     * 0.4F;
               this.lastOutputYaw = mc.player.getYaw();
               this.lastOutputPitch = mc.player.getPitch();
               this.currentYaw = this.lastOutputYaw;
               this.currentPitch = this.lastOutputPitch;
               this.yawVelocity = 0.0F;
               this.pitchVelocity = 0.0F;
               this.active = true;
               this.freezeTicks = 0;
               this.freezeProgress = 1.0F;
               this.stateCounter = -1;
               this.extraYawOffset = 0.0F;
               this.extraPitchOffset = 0.0F;
               this.initialized = true;
            }

            this.tickCounter++;
            this.randomJitterPhase = this.randomJitterPhase * 0.85F
               + (float)val.nextGaussian()
                  * 0.045F;
            boolean flag2 = !flag
               && System.currentTimeMillis() % 500L >= 370L;
            float yaw = flag2
               ? 0.0F
               : 1.0F;
            float yaw2;
            float yaw3;
            if (flag
               )
             {
               yaw2 = FreeLookStorage.getFreeYaw();
               yaw3 = FreeLookStorage.getFreePitch();
               this.speedProgress = 0.35F;
               this.speedReversing = false;
               this.freezeTicks = 0;
               this.freezeProgress = 1.0F;
            } else {
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

               Vec2f rot = RayTraceUtil.getRotations(
                  vec
               );
               yaw2 = rot.x;
               yaw3 = rot.y;
            }

            float yaw4 = Math.abs(
               MathHelper.wrapDegrees(
                  yaw2
                     - this.lastOutputYaw
               )
            );
            boolean flag3 = !flag
               && mc.player.getAttackCooldownProgress(1.0F) > 0.9F;
            if (flag
               )
             {
               this.speedProgress = 0.35F;
               this.speedReversing = false;
            } else if (!this.speedReversing) {
               float yaw5 = 0.0055F;
               yaw5 += (
                     0.004F
                        + 0.012F
                           * (
                              1.0F
                                 - (float)Math.exp(
                                    (double)(
                                       -yaw4
                                          / 35.0F
                                    )
                                 )
                           )
                  )
                  * 1.8F;
               yaw5 += val.nextFloat()
                  * 0.0025F;
               if (flag3
                  )
                {
                  yaw5 += 0.012857143F;
               }

               this.speedProgress = this.speedProgress
                  + yaw5
                     * (1.6F + this.randomJitterPhase);
               if (this.speedProgress >= this.speedTarget) {
                  this.speedReversing = true;
                  this.speedMin = -0.02F
                     - val.nextFloat()
                        * 0.05F;
               }
            } else {
               float yaw6 = (
                     flag3
                        ? 0.045F
                        : 0.008F
                  )
                  * (
                     0.85F
                        + val.nextFloat()
                           * 0.3F
                  );
               this.speedProgress = this.speedProgress
                  - yaw6
                     * (2.1F + this.randomJitterPhase);
               if (this.speedProgress <= this.speedMin) {
                  this.speedReversing = false;
                  this.speedTarget = 0.18F
                     + val.nextFloat()
                        * 0.1F;
               }
            }

            float yaw7 = MathHelper.clamp(
               this.speedProgress, 0.0F, mc.player.isGliding() ? 0.456F : 0.312F
            );
            if (flag3
               )
             {
               yaw7 = Math.min(
                  yaw7
                     + 0.1F,
                  mc.player.isGliding() ? 0.552F : 0.408F
               );
            }

            yaw7 += this.randomJitterPhase
               * 0.25F;
            if (!flag
               && this.tickCounter <= 3) {
               yaw7 *= 0.2F
                  * (float)this.tickCounter
                  * this.speedScale;
            }

            if (!this.active) {
               this.lastOutputYaw = mc.player.getYaw();
               this.lastOutputPitch = mc.player.getPitch();
               this.currentYaw = this.lastOutputYaw;
               this.currentPitch = this.lastOutputPitch;
               this.yawVelocity = 0.0F;
               this.pitchVelocity = 0.0F;
               this.active = true;
            }

            float yaw8 = MathHelper.wrapDegrees(
               yaw2
                  - this.currentYaw
            );
            float yaw9 = yaw3
               - this.currentPitch;
            if (!flag
               )
             {
               if (this.freezeTicks > 0) {
                  this.freezeTicks--;
               } else if (val.nextFloat()
                     < 0.03F
                  && this.tickCounter > 4
                  && !flag3
                  && yaw4
                     < 10.0F) {
                  this.freezeTicks = val.nextInt(
                        3
                     )
                     + 2;
               }

               if (this.freezeTicks > 0) {
                  this.freezeProgress = this.freezeProgress + (0.0F - this.freezeProgress) * 0.35F;
               } else {
                  this.freezeProgress = this.freezeProgress + (1.0F - this.freezeProgress) * 0.35F;
               }
            } else {
               this.freezeTicks = 0;
               this.freezeProgress = 1.0F;
            }

            float yaw10 = 0.9F
               + val.nextFloat()
                  * 0.2F;
            float yaw11 = (
                  mc.player.isGliding()
                     ? 86.4F
                     : (
                        flag3
                           ? 38.4F
                           : 28.8F
                     )
               )
               * yaw10;
            float yaw12 = (
                  mc.player.isGliding()
                     ? 14.4F
                     : (
                        flag3
                           ? 5.4F
                           : 3.36F
                     )
               )
               * yaw10;
            yaw8 = this.compressAngle(
               yaw8,
               yaw11,
               val
            );
            yaw9 = this.compressAngle(
               yaw9,
               yaw12,
               val
            );
            float yaw13 = yaw7
               * (0.94F + this.randomJitterPhase * 0.92F)
               * yaw;
            float yaw14 = Math.min(
                  yaw7
                     * 0.836F,
                  yaw13
                     * 0.96F
               )
               * yaw;
            if (flag
               )
             {
               yaw13 = yaw7
                  * 1.2F;
               yaw14 = yaw7
                  * 1.2F;
            }

            float yaw15 = yaw8
               * yaw13;
            float yaw16 = yaw9
               * yaw14;
            float yaw17 = 0.12F
               + val.nextFloat()
                  * 0.05F;
            float yaw18 = 0.14F
               + val.nextFloat()
                  * 0.06F;
            float yaw19 = (
                     yaw15
                        - this.yawVelocity
                  )
                  * yaw17
               + this.prevYawVel;
            float yaw20 = 0.7F
               + Math.abs(this.yawVelocity) * 0.2F;
            yaw19 = MathHelper.clamp(
               yaw19,
               this.smoothYawAccel
                  - yaw20,
               this.smoothYawAccel
                  + yaw20
            );
            this.yawVelocity += yaw19;
            this.smoothYawAccel = yaw19;
            this.pitchVelocity = this.pitchVelocity
               + (
                     yaw16
                        - this.pitchVelocity
                  )
                  * yaw18
               + this.prevPitchVel;
            this.pitchVelocity = this.compressAngle(
               this.pitchVelocity,
               4.8F,
               val
            );
            if (!flag2
               )
             {
               if (this.stateCounter == 0) {
                  this.yawVelocity = this.yawVelocity + this.extraYawOffset;
                  this.pitchVelocity = this.pitchVelocity + this.extraPitchOffset;
                  this.extraYawOffset = 0.0F;
                  this.extraPitchOffset = 0.0F;
                  this.stateCounter = -1;
               } else if (this.stateCounter > 0) {
                  this.stateCounter--;
               }
            }

            float yaw21 = (
                  this.yawVelocity + this.prevYawMove
               )
               * 0.5F;
            float yaw22 = (
                  this.pitchVelocity + this.prevPitchMove
               )
               * 0.5F;
            this.prevYawMove = this.yawVelocity;
            this.prevPitchMove = this.pitchVelocity;
            this.currentYaw += yaw21;
            this.currentPitch += yaw22;
            this.currentPitch = MathHelper.clamp(this.currentPitch, -89.0F, 89.0F);
            if (!flag
               )
             {
               float yaw23 = this.hitboxHelper
                  .getMultiplier(
                     entity
                  );
               this.currentYaw = this.currentYaw
                  + this.epochWave(
                     (1.1F + (float)Math.random() * 0.0F)
                        * yaw23
                        * yaw,
                     false,
                     18.0F
                  );
               this.currentPitch = this.currentPitch
                  + this.epochWave(
                     (0.05F + (float)Math.random() * 0.0F)
                        * yaw23
                        * yaw,
                     false,
                     this.getNextPeriod()
                  );
            }

            float yaw24 = this.getGCD();
            double x2 = yaw24
                  > 0.0F
               ? (double)yaw24
               : 0.003;
            double x3 = 0.0;
            double x4 = 0.0;
            if (!flag
               )
             {
               float yaw25 = Math.abs(
                  this.yawVelocity
               );
               float yaw26 = Math.abs(
                  this.pitchVelocity
               );
               float yaw27 = Math.min(
                  0.0F,
                  0.0F
                     + yaw25
                        * 0.0F
               );
               float yaw28 = Math.min(
                  0.0F,
                  0.0F
                     + yaw26
                        * 0.0F
               );
               double x5 = x2
                  * 0.3
                  * (double)yaw27;
               double x6 = x2
                  * 0.2
                  * (double)yaw28;
               x3 = val.nextGaussian()
                  * x5;
               x4 = val.nextGaussian()
                  * x6;
            }

            float yaw29 = (float)(
               (double)this.currentYaw
                  + x3
            );
            float yaw30 = (float)(
               (double)this.currentPitch
                  + x4
            );
            float yaw31 = this.lastOutputYaw;
            float yaw32 = this.lastOutputPitch;
            if (yaw24
               > 0.0F) {
               double x7 = (double)yaw29
                  - (double)this.lastOutputYaw;
               double x8 = (double)yaw30
                  - (double)this.lastOutputPitch;
               if (!flag
                  )
                {
                  x8 = (double)this.compressAngle(
                     (float)x8,
                     4.8F,
                     val
                  );
               }

               int i = (int)Math.round(
                  x7
                     / (double)yaw24
               );
               int i2 = (int)Math.round(
                  x8
                     / (double)yaw24
               );
               yaw31 = this.lastOutputYaw
                  + (float)(
                     (double)i
                        * (double)yaw24
                  );
               yaw32 = this.lastOutputPitch
                  + (float)(
                     (double)i2
                        * (double)yaw24
                  );
            } else {
               yaw31 = yaw29;
               yaw32 = yaw30;
            }

            yaw32 = MathHelper.clamp(
               yaw32,
               -89.0F,
               89.0F
            );
            if (yaw24
                  > 0.0F
               && !flag
               )
             {
               if (Math.abs(
                     MathHelper.wrapDegrees(
                        this.currentYaw
                           - yaw31
                     )
                  )
                  > yaw24
                     * 3.0F) {
                  this.currentYaw = this.currentYaw
                     + (
                           yaw31
                              - this.currentYaw
                        )
                        * 0.5F;
                  this.yawVelocity *= 0.4F;
               }

               if (Math.abs(
                     this.currentPitch
                        - yaw32
                  )
                  > yaw24
                     * 3.0F) {
                  this.currentPitch = this.currentPitch
                     + (
                           yaw32
                              - this.currentPitch
                        )
                        * 0.5F;
                  this.pitchVelocity *= 0.4F;
               }
            }

            float yaw33;
            if (!flag
               )
             {
               this.driftYaw = this.driftYaw
                  + (float)(
                     val.nextGaussian()
                        * 2.0E-5
                  );
               this.driftYaw = this.driftYaw >= 0.0F
                  ? MathHelper.clamp(this.driftYaw, 7.0E-4F, 0.0016F)
                  : MathHelper.clamp(this.driftYaw, -0.0016F, -7.0E-4F);
               float yaw34 = yaw31
                  - this.lastOutputYaw;
               yaw33 = this.lastOutputYaw
                  + yaw34
                     * (1.0F + this.driftYaw);
            } else {
               yaw33 = yaw31;
            }

            Angle val2 = new Angle(
               yaw33,
               yaw32
            );
            float yaw35;
            float yaw36;
            if (flag
               )
             {
               yaw36 = this.aura
                        != null
                     && this.aura.sc3ReturnSpeedYaw != null
                  ? this.aura.sc3ReturnSpeedYaw.getValue().floatValue()
                  : 32.0F;
               yaw35 = this.aura
                        != null
                     && this.aura.sc3ReturnSpeedPitch != null
                  ? this.aura.sc3ReturnSpeedPitch.getValue().floatValue()
                  : 30.0F;
            } else {
               float yaw37 = mc.player
                        .isGliding()
                     && entity.isGliding()
                  ? 360.0F
                  : 45.0F;
               yaw36 = yaw37;
               yaw35 = yaw37;
            }

            RotationStorage.update(
               val2,
               yaw36,
               yaw35,
               yaw36,
               yaw35,
               flag
                  ? 0
                  : 10,
               1,
               false
            );
            this.rotate = new Vec2f(
               val2.getYaw(),
               val2.getPitch()
            );
            this.lastOutputYaw = val2.getYaw();
            this.lastOutputPitch = val2.getPitch();
            if (flag
               )
             {
               float yaw38 = Math.abs(
                  MathHelper.wrapDegrees(
                     yaw2
                        - this.lastOutputYaw
                  )
               );
               float yaw39 = Math.abs(
                  yaw3
                     - this.lastOutputPitch
               );
               float yaw40 = Math.max(
                  yaw24
                     * 2.0F,
                  1.0F
               );
               if (yaw38
                     <= yaw40
                  && yaw39
                     <= yaw40
                  )
                {
                  this.returning = false;
                  this.active = false;
                  this.yawVelocity = 0.0F;
                  this.pitchVelocity = 0.0F;
                  this.lastOutputYaw = yaw2;
                  this.lastOutputPitch = yaw3;
                  this.currentYaw = yaw2;
                  this.currentPitch = yaw3;
                  if (RotationStorage.instance != null) {
                     RotationStorage.instance.stopRotation();
                  }

                  this.reset();
               }
            }
         }
      }
   }
}
