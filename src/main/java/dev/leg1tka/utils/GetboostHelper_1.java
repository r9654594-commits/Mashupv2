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


import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class GetboostHelper_1 {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static final float BASE_HORIZONTAL = 1.61F;
   private static final float BASE_VERTICAL = 1.5F;
   private static final float LONY_STRAIGHT_BPS = 60.0F;
   private static final float LONY_GRIEF_DIAGONAL_BPS = 50.0F;
   private static final float LONY_HVH_DIAGONAL_BPS = 100.0F;
   private static final float DIAGONAL_YAW_THRESHOLD = 22.5F;
   private static final float[] YAW_TABLE = new float[]{
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.62F,
      1.62F,
      1.62F,
      1.63F,
      1.63F,
      1.64F,
      1.65F,
      1.65F,
      1.66F,
      1.67F,
      1.68F,
      1.69F,
      1.7F,
      1.71F,
      1.72F,
      1.73F,
      1.73F,
      1.75F,
      1.76F,
      1.78F,
      1.79F,
      1.81F,
      1.83F,
      1.85F,
      1.87F,
      1.89F,
      1.91F,
      1.93F,
      1.95F,
      1.98F,
      2.01F,
      2.03F,
      2.06F,
      2.09F,
      2.12F,
      2.16F,
      2.19F,
      2.23F,
      2.27F,
      2.31F,
      2.35F,
      2.31F,
      2.27F,
      2.23F,
      2.19F,
      2.16F,
      2.12F,
      2.09F,
      2.06F,
      2.03F,
      2.01F,
      1.98F,
      1.95F,
      1.93F,
      1.89F,
      1.87F,
      1.85F,
      1.83F,
      1.81F,
      1.79F,
      1.78F,
      1.76F,
      1.75F,
      1.73F,
      1.72F,
      1.71F,
      1.7F,
      1.69F,
      1.68F,
      1.67F,
      1.66F,
      1.65F,
      1.64F,
      1.63F,
      1.63F,
      1.63F,
      1.62F,
      1.62F,
      1.62F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F
   };
   private static final float[] PITCH_TABLE = new float[]{
      1.61F,
      1.61F,
      1.61F,
      1.62F,
      1.62F,
      1.62F,
      1.63F,
      1.63F,
      1.64F,
      1.65F,
      1.65F,
      1.66F,
      1.67F,
      1.68F,
      1.69F,
      1.7F,
      1.71F,
      1.72F,
      1.73F,
      1.73F,
      1.75F,
      1.76F,
      1.78F,
      1.79F,
      1.81F,
      1.83F,
      1.85F,
      1.87F,
      1.89F,
      1.91F,
      1.93F,
      1.95F,
      1.98F,
      2.01F,
      2.03F,
      2.06F,
      2.09F,
      2.12F,
      2.16F,
      2.19F,
      2.23F,
      2.24F,
      2.21F,
      2.21F,
      2.21F,
      2.23F,
      2.23F,
      2.19F,
      2.16F,
      2.12F,
      2.09F,
      2.06F,
      2.03F,
      2.01F,
      1.98F,
      1.95F,
      1.93F,
      1.89F,
      1.87F,
      1.85F,
      1.83F,
      1.81F,
      1.79F,
      1.78F,
      1.76F,
      1.75F,
      1.73F,
      1.72F,
      1.71F,
      1.7F,
      1.69F,
      1.68F,
      1.67F,
      1.66F,
      1.65F,
      1.64F,
      1.63F,
      1.63F,
      1.63F,
      1.62F,
      1.62F,
      1.62F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F,
      1.61F
   };

   public static Vec3d getBoost(
      LivingEntity entity
   ) {
      return buildLonyBoost(
         entity,
         50.0F
      );
   }

   public static Vec3d getBoostLonyHvh(
      LivingEntity entity
   ) {
      return buildLonyBoost(
         entity,
         100.0F
      );
   }

   private static Vec3d buildLonyBoost(
      LivingEntity entity,
      float f
   ) {
      float yaw = getRageSpeed(
         entity
      );
      if (isNearDiagonal(
         entity
      )) {
         float yaw2 = f
            / 20.0F;
         if (f
            > 50.0F) {
            float yaw3 = f
               / 50.0F;
            yaw = Math.min(
               yaw
                  * yaw3,
               yaw2
            );
         } else {
            yaw = Math.min(
               yaw,
               yaw2
            );
         }
      } else {
         yaw = Math.min(
            yaw,
            3.0F
         );
      }

      Vec3d vec = entity.getRotationVector();
      Vec3d vec2 = Vec3d.fromPolar(
            entity.getPitch(),
            entity.getYaw()
         )
         .multiply(
            (double)yaw
         );
      float yaw4 = entity.getPitch()
         * (float) (Math.PI / 180.0);
      double x2 = Math.sqrt(
         vec.x
               * vec.x
            + vec.z
               * vec.z
      );
      double x3 = vec2.horizontalLength();
      boolean flag = entity.getVelocity()
            .y
         <= 0.0;
      double x4 = flag
            && entity.hasStatusEffect(
               StatusEffects.SLOW_FALLING
            )
         ? Math.min(
            entity.getFinalGravity(),
            0.01
         )
         : entity.getFinalGravity();
      double x5 = MathHelper.square(
         Math.cos(
            (double)yaw4
         )
      );
      vec2 = vec2.add(
         0.0,
         x4
            * (
               -1.0
                  + x5
                     * 0.75
            ),
         0.0
      );
      if (vec2.y
            < 0.0
         && x2
            > 0.0) {
         double x6 = vec2.y
            * -0.1
            * x5;
         vec2 = vec2.add(
            vec.x
               * x6
               / x2,
            x6,
            vec.z
               * x6
               / x2
         );
      }

      if (yaw4
            < 0.0F
         && x2
            > 0.0) {
         double x7 = x3
            * (double)(
               -MathHelper.sin(
                  yaw4
               )
            )
            * 0.04;
         vec2 = vec2.add(
            -vec.x
               * x7
               / x2,
            x7
               * 3.2,
            -vec.z
               * x7
               / x2
         );
      }

      if (x2
         > 0.0) {
         vec2 = vec2.add(
            (
                  vec.x
                        / x2
                        * x3
                     - vec2.x
               )
               * 0.1,
            0.0,
            (
                  vec.z
                        / x2
                        * x3
                     - vec2.z
               )
               * 0.1
         );
      }

      double x8 = vec2.length();
      return new Vec3d(
            x8,
            x8,
            x8
         )
         .multiply(0.99, 0.98, 0.99);
   }

   private static boolean isNearDiagonal(
      LivingEntity entity
   ) {
      float yaw = Math.abs(
         (
               entity.getYaw()
                  - 360.0F
            )
            % 360.0F
      );
      float[] var10000 = new float[4];
      var10000[0] = 45.0F;
      var10000[1] = 135.0F;
      var10000[2] = 225.0F;
      var10000[3] = 315.0F;
      float[] floats = var10000;
      float yaw2 = Float.MAX_VALUE;

      for (float yaw3 : floats) {
         yaw2 = Math.min(
            yaw2,
            Math.abs(
               yaw
                  - yaw3
            )
         );
      }

      return yaw2
         <= 22.5F;
   }

   private static float getRageSpeed(
      LivingEntity entity
   ) {
      float yaw = Math.abs(
         MathHelper.wrapDegrees(
            entity.getYaw()
         )
      );
      float yaw2 = foldYaw(
         yaw
      );
      float yaw3 = Math.abs(
         clampPitch(
            entity.getPitch()
         )
      );
      if (yaw3
            >= 70.0F
         && yaw3
            <= 90.0F) {
         return 1.615F;
      } else {
         float yaw4 = YAW_TABLE[Math.min(
            (int)Math.ceil(
               (double)yaw2
            ),
            90
         )];
         int x = Math.min(
            (int)Math.ceil(
               (double)yaw3
            ),
            PITCH_TABLE.length - 1
         );
         float yaw5 = PITCH_TABLE[x];
         float yaw6 = yaw3
               >= 75.0F
            ? yaw5
            : Math.max(
               yaw4,
               yaw5
            );
         return Math.max(
            yaw6,
            yaw3
                  >= 75.0F
               ? 1.5F
               : 1.61F
         );
      }
   }

   private static float foldYaw(
      float f
   ) {
      float yaw = f
            > 180.0F
         ? 360.0F
            - f
         : f;
      return yaw
            > 90.0F
         ? 180.0F
            - yaw
         : yaw;
   }

   private static float clampPitch(
      float f
   ) {
      return Math.max(
         -90.0F,
         Math.min(
            90.0F,
            f
         )
      );
   }

   public static Vec3d getBoostAntiTarget(
      LivingEntity target,
      float f
   ) {
      float yaw = Math.abs(
         (
               target.getYaw()
                  - 360.0F
            )
            % 360.0F
      );
      float yaw2 = target.getPitch();
      float yaw3 = Math.abs(
         yaw2
      );
      float yaw4 = 0.0F;
      if (yaw3
            >= 30.0F
         && yaw3
            <= 50.0F) {
         yaw4 = 0.15F;
      } else if (yaw3
            >= 25.0F
         && yaw3
            <= 55.0F) {
         yaw4 = 0.1F;
      } else if (yaw3
            >= 20.0F
         && yaw3
            <= 60.0F) {
         yaw4 = 0.05F;
      }

      float yaw5 = f
         + yaw4;
      float[] var10000 = new float[4];
      var10000[0] = 45.0F;
      var10000[1] = 135.0F;
      var10000[2] = 225.0F;
      var10000[3] = 315.0F;
      float[] floats = var10000;
      float yaw6 = 9999.0F;

      for (float yaw7 : floats) {
         float yaw8 = Math.abs(
            yaw
               - yaw7
         );
         if (yaw8
            < yaw6
            )
          {
            yaw6 = yaw8;
         }
      }

      if (yaw6
         < 15.0F) {
         yaw5 += 0.1F;
      } else if (yaw6
         < 25.0F) {
         yaw5 += 0.05F;
      }

      yaw5 = Math.min(
         yaw5,
         2.8F
      );
      return new Vec3d(
         (double)yaw5,
         (double)yaw5,
         (double)yaw5
      );
   }

   public static Vec3d getBoostAntiTargetFast(
      LivingEntity target
   ) {
      float yaw = Math.abs(
         (
               target.getYaw()
                  - 360.0F
            )
            % 360.0F
      );
      float yaw2 = target.getPitch();
      float yaw3 = Math.abs(
         yaw2
      );
      float yaw4 = 2.5F;
      float yaw5 = 2.3F;
      if (yaw3
            >= 35.0F
         && yaw3
            <= 50.0F) {
         yaw4 = 2.7F;
         yaw5 = 2.5F;
      } else if (yaw3
            >= 30.0F
         && yaw3
            <= 55.0F) {
         yaw4 = 2.6F;
         yaw5 = 2.4F;
      }

      float[] var10000 = new float[4];
      var10000[0] = 45.0F;
      var10000[1] = 135.0F;
      var10000[2] = 225.0F;
      var10000[3] = 315.0F;
      float[] floats = var10000;
      float yaw6 = 9999.0F;

      for (float yaw7 : floats) {
         float yaw8 = Math.abs(
            yaw
               - yaw7
         );
         if (yaw8
            < yaw6
            )
          {
            yaw6 = yaw8;
         }
      }

      if (yaw6
         < 20.0F) {
         yaw4 += 0.15F;
      }

      return new Vec3d(
         (double)yaw4,
         (double)yaw5,
         (double)yaw4
      );
   }

   public static Vec3d getBoostAntiTargetWithAura(
      LivingEntity target,
      float f,
      float f2,
      float f3
   ) {
      float y = Math.abs(
         f
      );
      float y2 = f3;
      float var6;
      if (y
            >= 38.0F
         && y
            <= 52.0F) {
         y2 = Math.min(
            f3
               + 0.2F,
            2.7F
         );
         var6 = Math.min(
            f3
               + 0.15F,
            2.5F
         );
      } else if (y
            >= 30.0F
         && y
            <= 60.0F) {
         y2 = Math.min(
            f3
               + 0.1F,
            2.6F
         );
         var6 = Math.min(
            f3
               + 0.1F,
            2.4F
         );
      } else if (y
            >= 25.0F
         && y
            <= 65.0F) {
         var6 = f3
            - 0.05F;
      } else {
         y2 = f3
            - 0.1F;
         var6 = f3
            - 0.15F;
      }

      return new Vec3d(
         (double)y2,
         (double)var6,
         (double)y2
      );
   }

   public static Vec3d getBoostslime(
      LivingEntity entity
   ) {
      return getBoostCustom(
         entity,
         42.0F
      );
   }

   public static Vec3d getBoostbravo(
      LivingEntity entity
   ) {
      return getBoostCustom(
         entity,
         39.0F
      );
   }

   public static Vec3d getBoostrw(
      LivingEntity entity
   ) {
      return getBoostCustom(
         entity,
         33.2F
      );
   }

   public static Vec3d getBoostCustom(
      LivingEntity entity,
      float f
   ) {
      float yaw = f
         / 20.0F;
      float yaw2 = Math.abs(
         (
               entity.getYaw()
                  - 360.0F
            )
            % 360.0F
      );
      float yaw3 = entity.getPitch();
      float yaw4 = Math.min(
         yaw
            * 0.7F,
         1.67F
      );
      float[] var10000 = new float[4];
      var10000[0] = 45.0F;
      var10000[1] = 135.0F;
      var10000[2] = 225.0F;
      var10000[3] = 315.0F;
      float[] floats = var10000;
      float yaw5 = 9999.0F;

      for (float yaw6 : floats) {
         float yaw7 = Math.abs(
            yaw2
               - yaw6
         );
         if (yaw7
            < yaw5
            )
          {
            yaw5 = yaw7;
         }
      }

      float yaw8 = 1.0F
         - yaw5
            / 45.0F;
      yaw8 = Math.max(
         0.0F,
         Math.min(
            1.0F,
            yaw8
         )
      );
      float yaw9 = getPitchFactor(
         yaw3
      );
      float yaw10 = yaw8
         * yaw9;
      float yaw11 = yaw4
         + (
               yaw
                  - yaw4
            )
            * yaw10;
      Vec3d vec = entity.getRotationVector();
      Vec3d vec2 = Vec3d.fromPolar(
            yaw3,
            entity.getYaw()
         )
         .multiply(
            (double)yaw11
         );
      float yaw12 = yaw3
         * (float) (Math.PI / 180.0);
      double x2 = Math.sqrt(
         vec.x
               * vec.x
            + vec.z
               * vec.z
      );
      double x3 = vec2.horizontalLength();
      boolean flag = entity.getVelocity()
            .y
         <= 0.0;
      double x4 = flag
            && entity.hasStatusEffect(
               StatusEffects.SLOW_FALLING
            )
         ? Math.min(
            entity.getFinalGravity(),
            0.01
         )
         : entity.getFinalGravity();
      double x5 = MathHelper.square(
         Math.cos(
            (double)yaw12
         )
      );
      vec2 = vec2.add(
         0.0,
         x4
            * (
               -1.0
                  + x5
                     * 0.75
            ),
         0.0
      );
      if (vec2.y
            < 0.0
         && x2
            > 0.0) {
         double x6 = vec2.y
            * -0.1
            * x5;
         vec2 = vec2.add(
            vec.x
               * x6
               / x2,
            x6,
            vec.z
               * x6
               / x2
         );
      }

      if (yaw12
            < 0.0F
         && x2
            > 0.0) {
         double x7 = x3
            * (double)(
               -MathHelper.sin(
                  yaw12
               )
            )
            * 0.04;
         vec2 = vec2.add(
            -vec.x
               * x7
               / x2,
            x7
               * 3.2,
            -vec.z
               * x7
               / x2
         );
      }

      if (x2
         > 0.0) {
         vec2 = vec2.add(
            (
                  vec.x
                        / x2
                        * x3
                     - vec2.x
               )
               * 0.1,
            0.0,
            (
                  vec.z
                        / x2
                        * x3
                     - vec2.z
               )
               * 0.1
         );
      }

      double x8 = vec2.length();
      return new Vec3d(
            x8,
            x8,
            x8
         )
         .multiply(0.99, 0.98, 0.99);
   }

   public static Vec3d getBoostFixedBps(
      LivingEntity entity,
      float f
   ) {
      float x = f
         / 20.0F;
      return new Vec3d(
            (double)x,
            (double)x,
            (double)x
         )
         .multiply(0.99, 0.98, 0.99);
   }

   private static float getPitchFactor(
      float f
   ) {
      float pitch = Math.abs(
         f
      );
      if (pitch
         <= 5.0F) {
         return 1.0F;
      } else if (pitch
         <= 15.0F) {
         return 0.95F;
      } else if (pitch
         <= 25.0F) {
         return 0.85F;
      } else if (pitch
         <= 35.0F) {
         return 0.75F;
      } else if (pitch
         <= 45.0F) {
         return 0.65F;
      } else if (pitch
         <= 55.0F) {
         return 0.55F;
      } else if (pitch
         <= 65.0F) {
         return 0.45F;
      } else {
         return pitch
               <= 75.0F
            ? 0.35F
            : 0.25F;
      }
   }

   private GetboostHelper_1() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
