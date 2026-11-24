package dev.leg1tka.rotation;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class AuraTransition implements IMinecraft {
   public static final AuraTransition INSTANCE = new AuraTransition();
   private RotationSmoothingMode mode = RotationSmoothingMode.DISABLED;
   private boolean isReturningToOriginal = false;
   private boolean isSmoothStarting = false;
   private float transStartYaw = 0.0F;
   private float transStartPitch = 0.0F;
   private float transTargetYaw = 0.0F;
   private float transTargetPitch = 0.0F;
   private float originalPlayerYaw = 0.0F;
   private float originalPlayerPitch = 0.0F;
   private long transitionStartTime = 0L;
   private long transitionNoiseSeed = 0L;
   private static final long TRANSITION_DURATION = 1200L;
   private boolean testEvasionActive = false;
   private int testEvasionPhase = 0;
   private long testEvasionStart = 0L;
   private long testEvasionNextAt = 0L;
   private float testOffsetYaw = 0.0F;
   private float testOffsetPitch = 0.0F;
   private float testFromYaw = 0.0F;
   private float testFromPitch = 0.0F;
   private float testToYaw = 0.0F;
   private float testToPitch = 0.0F;
   private static final long TEST_HOLD_MS = 120L;
   private static final long TEST_RETURN_MS = 180L;
   private double noiseT = 0.0;

   public void setMode(String modeName) {
      if ("Плавная".equalsIgnoreCase(modeName)) {
         this.mode = RotationSmoothingMode.SMOOTH;
      } else if ("Испытательная".equalsIgnoreCase(modeName)) {
         this.mode = RotationSmoothingMode.TEST;
      } else {
         this.mode = RotationSmoothingMode.DISABLED;
      }
   }

   public void setMode(
      boolean flag,
      boolean flag2
   ) {
      RotationSmoothingMode val;
      if (flag2
         )
       {
         val = RotationSmoothingMode.TEST;
      } else if (flag
         )
       {
         val = RotationSmoothingMode.SMOOTH;
      } else {
         val = RotationSmoothingMode.DISABLED;
      }

      this.mode = val;
   }

   public RotationSmoothingMode getMode() {
      return this.mode;
   }

   public void startSmoothStart() {
      if (mc.player != null && this.mode != RotationSmoothingMode.DISABLED) {
         this.originalPlayerYaw = mc.player.getYaw();
         this.originalPlayerPitch = mc.player.getPitch();
         this.transStartYaw = this.originalPlayerYaw;
         this.transStartPitch = this.originalPlayerPitch;
         this.transTargetYaw = this.originalPlayerYaw;
         this.transTargetPitch = this.originalPlayerPitch;
         this.transitionStartTime = System.currentTimeMillis();
         this.transitionNoiseSeed = System.currentTimeMillis();
         this.isSmoothStarting = true;
         this.isReturningToOriginal = false;
      }
   }

   public void startSmoothReturn() {
      if (mc.player != null && this.mode != RotationSmoothingMode.DISABLED) {
         this.transStartYaw = mc.player.getYaw();
         this.transStartPitch = mc.player.getPitch();
         this.transTargetYaw = this.originalPlayerYaw + (float)((Math.random() - 0.5) * 2.0);
         this.transTargetPitch = this.originalPlayerPitch + (float)((Math.random() - 0.5) * 1.2);
         this.transTargetYaw = MathHelper.wrapDegrees(this.transTargetYaw);
         this.transTargetPitch = MathHelper.clamp(this.transTargetPitch, -90.0F, 90.0F);
         this.transitionStartTime = System.currentTimeMillis();
         this.transitionNoiseSeed = System.currentTimeMillis();
         this.isReturningToOriginal = true;
         this.isSmoothStarting = false;
      }
   }

   public void triggerTestEvasion(
      float f,
      float f2
   ) {
      if (!this.testEvasionActive) {
         this.testOffsetYaw = (ThreadLocalRandom.current().nextFloat() - 0.5F) * 10.0F;
         this.testOffsetPitch = (ThreadLocalRandom.current().nextFloat() - 0.5F) * 5.0F;
         this.testFromYaw = f;
         this.testFromPitch = f2;
         this.testToYaw = f
            + this.testOffsetYaw;
         this.testToPitch = MathHelper.clamp(
            f2
               + this.testOffsetPitch,
            -90.0F,
            90.0F
         );
         this.testEvasionStart = System.currentTimeMillis();
         this.testEvasionPhase = 1;
         this.testEvasionActive = true;
      }
   }

   public boolean handleTransitions() {
      if (this.mode == RotationSmoothingMode.DISABLED) {
         return false;
      } else if (this.isSmoothStarting) {
         this.handleSmoothTransition(true);
         return true;
      } else if (this.isReturningToOriginal) {
         this.handleSmoothTransition(false);
         return true;
      } else {
         return false;
      }
   }

   public float[] process(
      float f,
      float f2,
      LivingEntity entity,
      String str
   ) {
      if (mc.player == null) {
         float[] var11 = new float[2];
         var11[0] = f;
         var11[1] = f2;
         return var11;
      } else {
         float yaw = mc.player
            .getYaw();
         float yaw2 = mc.player
            .getPitch();
         long time = System.currentTimeMillis();
         if (this.isSmoothStarting) {
            this.transTargetYaw = f;
            this.transTargetPitch = f2;
         }

         if (this.mode == RotationSmoothingMode.DISABLED) {
            float[] var10000 = new float[2];
            var10000[0] = f;
            var10000[1] = f2;
            return var10000;
         } else {
            if (this.mode == RotationSmoothingMode.TEST) {
               if (!this.testEvasionActive
                  && time
                     >= this.testEvasionNextAt) {
                  float yaw3 = (float)Math.hypot(
                     (double)MathHelper.wrapDegrees(
                        f
                           - yaw
                     ),
                     (double)(
                        f2
                           - yaw2
                     )
                  );
                  if (yaw3
                     < 12.0F) {
                     this.triggerTestEvasion(
                        yaw,
                        yaw2
                     );
                     this.scheduleNextTestEvasion();
                  }
               }

               if (this.testEvasionActive) {
                  float[] floats = this.tickTestEvasion(
                     f,
                     f2,
                     yaw,
                     yaw2,
                     time
                  );
                  if (floats
                     != null) {
                     f = floats[0];
                     f2 = floats[1];
                  }
               }
            }

            return this.addMicroNoise(
               f,
               f2,
               str
            );
         }
      }
   }

   public void reset() {
      this.isSmoothStarting = false;
      this.isReturningToOriginal = false;
      this.testEvasionActive = false;
      this.testEvasionPhase = 0;
      this.noiseT = 0.0;
      this.scheduleNextTestEvasion();
   }

   public boolean isTransitionActive() {
      return this.isSmoothStarting || this.isReturningToOriginal;
   }

   public boolean isTestEvasionActive() {
      return this.testEvasionActive;
   }

   private void handleSmoothTransition(
      boolean flag
   ) {
      if (mc.player != null) {
         long time = System.currentTimeMillis();
         long time2 = time
            - this.transitionStartTime;
         if (time2
            >= 1200L) {
            if (!flag
               )
             {
               RotationStorage.update(new Angle(this.transTargetYaw, this.transTargetPitch), 180.0F, 180.0F, 0.0F, 0, 1);
            }

            this.isReturningToOriginal = false;
            this.isSmoothStarting = false;
         } else {
            double y = (double)time2
               / 1200.0;
            y = y
                  < 0.5
               ? 8.0
                  * y
                  * y
                  * y
                  * y
               : 1.0
                  - Math.pow(
                        -2.0
                              * y
                           + 2.0,
                        4.0
                     )
                     / 2.0;
            float yaw = (float)(
               (double)this.transStartYaw
                  + (double)MathHelper.wrapDegrees(this.transTargetYaw - this.transStartYaw)
                     * y
            );
            float yaw2 = (float)(
               (double)this.transStartPitch
                  + (double)(this.transTargetPitch - this.transStartPitch)
                     * y
            );
            float yaw3 = (float)(
               1.0
                  - y
                     * 0.7
            );
            float yaw4 = NoiseHelper.noise(
               (float)(
                  (double)this.transitionNoiseSeed * 6.0E-4
                     + (double)time2
                        * 0.0012
               )
            );
            float yaw5 = NoiseHelper.noise(
               (float)(
                  (double)this.transitionNoiseSeed * 9.0E-4
                     + (double)time2
                        * 0.0018
               )
            );
            yaw += yaw4
               * 0.8F
               * yaw3;
            yaw2 += yaw5
               * 0.6F
               * yaw3;
            yaw = MathHelper.wrapDegrees(
               yaw
            );
            yaw2 = MathHelper.clamp(
               yaw2,
               -90.0F,
               90.0F
            );
            float yaw6 = Math.abs(
               MathHelper.wrapDegrees(
                  yaw
                     - (
                        flag
                           ? this.originalPlayerYaw
                           : this.transTargetYaw
                     )
               )
            );
            float yaw7 = Math.abs(
               yaw2
                  - (
                     flag
                        ? this.originalPlayerPitch
                        : this.transTargetPitch
                  )
            );
            float yaw8 = Math.min(
               90.0F,
               yaw6
                     * 0.8F
                  + 15.0F
            );
            float yaw9 = Math.min(
               90.0F,
               yaw7
                     * 0.6F
                  + 10.0F
            );
            RotationStorage.update(
               new Angle(
                  yaw,
                  yaw2
               ),
               yaw8,
               yaw9,
               yaw8,
               yaw9,
               0,
               1,
               false
            );
         }
      }
   }

   private float[] tickTestEvasion(
      float f,
      float f2,
      float f3,
      float f4,
      long time
   ) {
      long time2 = time
         - this.testEvasionStart;
      if (this.testEvasionPhase == 1) {
         if (time2
            < 120L) {
            float yaw = smoothStep(
               (float)time2
                  / 120.0F
            );
            float yaw2 = MathHelper.lerp(
               yaw,
               this.testFromYaw,
               this.testToYaw
            );
            float yaw3 = MathHelper.lerp(
               yaw,
               this.testFromPitch,
               this.testToPitch
            );
            float[] var15 = new float[2];
            var15[0] = yaw2;
            var15[1] = MathHelper.clamp(
               yaw3,
               -90.0F,
               90.0F
            );
            return var15;
         }

         this.testEvasionPhase = 2;
         this.testEvasionStart = time;
         time2 = 0L;
      }

      if (this.testEvasionPhase == 2) {
         if (time2
            < 180L) {
            float yaw4 = smoothStep(
               (float)time2
                  / 180.0F
            );
            float yaw5 = MathHelper.lerp(
               yaw4,
               this.testToYaw,
               f
            );
            float yaw6 = MathHelper.lerp(
               yaw4,
               this.testToPitch,
               f2
            );
            float[] var10000 = new float[2];
            var10000[0] = yaw5;
            var10000[1] = MathHelper.clamp(
               yaw6,
               -90.0F,
               90.0F
            );
            return var10000;
         }

         this.testEvasionActive = false;
         this.testEvasionPhase = 0;
      }

      return null;
   }

   private void scheduleNextTestEvasion() {
      this.testEvasionNextAt = System.currentTimeMillis() + 4000L + (long)(ThreadLocalRandom.current().nextDouble() * 5000.0);
   }

   private float[] addMicroNoise(
      float f,
      float f2,
      String str
   ) {
      this.noiseT = this.noiseT + 0.08 + ThreadLocalRandom.current().nextDouble() * 0.04;
      String var5 = str.toLowerCase();
      int var6 = -1;
      switch (var5.hashCode()) {
         case -755922751:
            if (var5.equals("shardac/test")) {
               var6 = 1;
            }
            break;
         case -420368863:
            if (var5.equals("wellmine")) {
               var6 = 3;
            }
            break;
         case 102851513:
            if (var5.equals("legit")) {
               var6 = 2;
            }
            break;
         case 2054216832:
            if (var5.equals("shardac")) {
               var6 = 0;
            }
      }
      float x = switch (var6) {
         case 0, 1 -> 0.06F;
         case 2 -> 0.15F;
         case 3 -> 0.25F;
         default -> 0.2F;
      };
      float x2 = NoiseHelper.noise(
               (float)(this.noiseT * 1.3)
            )
            * 0.18F
         + NoiseHelper.noise((float)(this.noiseT * 2.7)) * 0.09F;
      float x3 = NoiseHelper.noise(
               (float)(this.noiseT * 1.1 + 5.0)
            )
            * 0.12F
         + NoiseHelper.noise((float)(this.noiseT * 2.3 + 3.0)) * 0.06F;
      float[] var9 = new float[2];
      var9[0] = f
         + x2
            * x;
      var9[1] = MathHelper.clamp(
         f2
            + x3
               * x
               * 0.6F,
         -90.0F,
         90.0F
      );
      return var9;
   }

   private static float smoothStep(
      float f
   ) {
      f = MathHelper.clamp(
         f,
         0.0F,
         1.0F
      );
      return f
         * f
         * (
            3.0F
               - 2.0F
                  * f
         );
   }
}
