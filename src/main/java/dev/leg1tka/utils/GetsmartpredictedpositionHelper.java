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


import lombok.Generated;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class GetsmartpredictedpositionHelper implements IMinecraft {
   private static final double DEFAULT_DAMPING = 0.98;
   private static final float DEFAULT_YAW_MULTIPLIER = 1.5F;

   public static Vec3d getSmartPredictedPosition(
      Entity entity,
      double d,
      float f,
      double d2,
      float f2
   ) {
      Vec3d vec = entity.getPos();
      Vec3d vec2 = entity.getVelocity();
      if (Math.hypot(
               entity.prevX
                  - entity.getX(),
               entity.prevZ
                  - entity.getZ()
            )
            * 20.0
         <= 20.0) {
         return vec.add(
            0.0,
            (double)entity.getHeight(),
            0.0
         );
      } else {
         float yaw = entity.prevYaw;
         float yaw2 = entity.getYaw();
         float yaw3 = Math.abs(
            yaw2
               - yaw
         );
         float yaw4 = Math.min(
            yaw3
               / 25.0F,
            3.0F
         );
         double x = 1.0
            - (double)yaw4
               * 0.9;

         for (int i = 0;
            i
               < (int)d;
            i++
         ) {
            float yaw5 = (float)i
               / (float)d;
            double x2 = 1.0
               - (
                     1.0
                        - d2
                  )
                  * (
                     1.0
                        - (double)yaw5
                  );
            vec2 = rotateVelocityByYaw(
               vec2,
               yaw,
               yaw2,
               (float)(
                  (double)f2
                     * x
               )
            );
            vec = vec.add(
               vec2
            );
            vec2 = vec2.multiply(
               x2
            );
         }

         double x3 = d
            - (double)(
               (int)d
            );
         if (x3
            > 0.0) {
            float yaw6 = (float)(
               1.0
                  - x3
            );
            double x4 = 1.0
               - (
                     1.0
                        - d2
                  )
                  * (
                     1.0
                        - (double)yaw6
                  );
            vec2 = rotateVelocityByYaw(
               vec2,
               yaw,
               yaw2,
               (float)(
                  (double)f2
                     * x
               )
            );
            vec = vec.add(
               vec2.multiply(
                  x3
                     * x4
               )
            );
         }

         vec = applyGlideChaseBias(
            entity,
            vec,
            d
         );
         return vec.add(
            0.0,
            (double)entity.getHeight(),
            0.0
         );
      }
   }

   private static Vec3d applyGlideChaseBias(
      Entity entity,
      Vec3d vec,
      double d
   ) {
      if (mc.player != null
         && entity
            != null
         && mc.player.isGliding()
         && entity instanceof LivingEntity val
         && val.isGliding()
         )
       {
         Vec3d vec2 = mc.player
            .getVelocity();
         if (vec2.lengthSquared()
            < 0.01) {
            vec2 = mc.player
               .getRotationVector();
         }

         if (vec2.lengthSquared()
            < 0.01) {
            return vec;
         }

         Vec3d vec3 = new Vec3d(
               vec2.x,
               0.0,
               vec2.z
            )
            .normalize();
         Vec3d vec4 = new Vec3d(
            -vec3.z,
            0.0,
            vec3.x
         );
         Vec3d vec5 = entity.getPos()
            .subtract(mc.player.getPos());
         double x2 = vec5.dotProduct(
            vec4
         );
         double x3 = vec5.dotProduct(
            vec3
         );
         Vec3d vec6 = entity.getVelocity();
         double x4 = vec6.dotProduct(
            vec4
         );
         double x5 = Math.max(
            -1.35,
            Math.min(
               1.35,
               x2
                  * 0.32
            )
         );
         double x6 = Math.max(
            -1.1,
            Math.min(
               1.1,
               x4
                  * (
                     0.9
                        + d
                           * 0.12
                  )
            )
         );
         double x7 = x3
               > 0.0
            ? Math.min(
               x3
                  * 0.045,
               0.45
            )
            : 0.0;
         return vec.add(
               vec4.multiply(
                  x5
                     + x6
               )
            )
            .add(
               vec3.multiply(
                  x7
               )
            );
      }

      return vec;
   }

   private static Vec3d rotateVelocityByYaw(
      Vec3d vec,
      float f,
      float f2,
      float f3
   ) {
      float yaw = (
            f2
               - f
         )
         * f3;
      if (Math.abs(
            yaw
         )
         < 0.001F) {
         return vec;
      } else {
         double x2 = Math.toRadians(
            (double)yaw
         );
         double x3 = Math.cos(
            x2
         );
         double x4 = Math.sin(
            x2
         );
         double x5 = vec.x
               * x3
            - vec.z
               * x4;
         double x6 = vec.x
               * x4
            + vec.z
               * x3;
         return new Vec3d(
            x5,
            vec.y,
            x6
         );
      }
   }

   public static Vec3d getPredicted(
      Entity entity,
      double d
   ) {
      return getSmartPredictedPosition(
         entity,
         d,
         0.0F,
         0.98,
         1.5F
      );
   }

   public static Vec3d getPredicted(
      Entity entity,
      double d,
      double d2,
      double d3
   ) {
      return getSmartPredictedPosition(
         entity,
         d,
         0.0F,
         0.98,
         1.5F,
         d2,
         d3
      );
   }

   public static Vec3d getPredictedRender(
      Entity entity,
      double d,
      float f
   ) {
      return getSmartPredictedPosition(
         entity,
         d,
         f,
         0.98,
         1.5F
      );
   }

   public static Vec3d getPredictedRender(
      Entity entity,
      double d,
      float f,
      double d2,
      double d3
   ) {
      return getSmartPredictedPosition(
         entity,
         d,
         f,
         0.98,
         1.5F,
         d2,
         d3
      );
   }

   public static Vec3d predict(
      LivingEntity entity,
      double d
   ) {
      return getPredicted(
         entity,
         d
      );
   }

   private static Vec3d getSmartPredictedPosition(
      Entity entity,
      double d,
      float f,
      double d2,
      float f2,
      double d3,
      double d4
   ) {
      Vec3d vec = getSmartPredictedPosition(
         entity,
         d,
         f,
         d2,
         f2
      );
      return applyGlideChaseBias(
         entity,
         vec,
         d,
         d3,
         d4
      );
   }

   private static Vec3d applyGlideChaseBias(
      Entity entity,
      Vec3d vec,
      double d,
      double d2,
      double d3
   ) {
      if (mc.player != null
         && entity
            != null
         && mc.player.isGliding()
         && entity instanceof LivingEntity val
         && val.isGliding()
         )
       {
         Vec3d vec2 = mc.player
            .getVelocity();
         if (vec2.lengthSquared()
            < 0.01) {
            vec2 = mc.player
               .getRotationVector();
         }

         if (vec2.lengthSquared()
            < 0.01) {
            return vec;
         }

         Vec3d vec3 = new Vec3d(
               vec2.x,
               0.0,
               vec2.z
            )
            .normalize();
         Vec3d vec4 = new Vec3d(
            -vec3.z,
            0.0,
            vec3.x
         );
         Vec3d vec5 = entity.getPos()
            .subtract(mc.player.getPos());
         double x2 = vec5.dotProduct(
            vec4
         );
         double x3 = vec5.dotProduct(
            vec3
         );
         Vec3d vec6 = entity.getVelocity();
         Vec3d vec7 = new Vec3d(
            vec6.x,
            0.0,
            vec6.z
         );
         double x4 = vec6.dotProduct(
            vec4
         );
         double x5 = vec6.dotProduct(
            vec3
         );
         double x6 = vec2.dotProduct(
            vec3
         );
         double x7 = x5
            - x6;
         float yaw = MathHelper.wrapDegrees(
            entity.getYaw()
               - entity.prevYaw
         );
         double x8 = Math.max(
            -3.4,
            Math.min(
               3.4,
               x2
                  * 0.58
                  * d2
            )
         );
         double x9 = Math.max(
            -2.8,
            Math.min(
               2.8,
               x4
                  * (
                     1.25
                        + d
                           * 0.3
                  )
                  * d2
            )
         );
         double x10 = x3
               > 0.0
            ? Math.min(
               x3
                  * 0.22
                  * d3,
               4.1
                  * d3
            )
            : 0.0;
         double x11 = Math.max(
            0.0,
            Math.min(
               x5
                  * 0.52
                  * d3,
               2.4
                  * d3
            )
         );
         double x12 = x7
               > 0.0
            ? Math.min(
               x7
                  * (
                     1.55
                        + d
                           * 0.24
                  )
                  * d3,
               3.0
                  * d3
            )
            : 0.0;
         Vec3d vec8 = Vec3d.ZERO;
         if (vec7.lengthSquared()
               > 0.01
            && Math.abs(
                  yaw
               )
               > 2.0F) {
            Vec3d vec9 = vec7.normalize();
            Vec3d vec10 = new Vec3d(
                  -vec9.z,
                  0.0,
                  vec9.x
               )
               .multiply(
                  (double)Math.signum(
                     yaw
                  )
               );
            double x13 = Math.min(
                  (double)Math.abs(
                        yaw
                     )
                     / 16.0,
                  2.6
               )
               * (
                  0.7
                     + d
                        * 0.14
               );
            vec8 = vec10.multiply(
                  x13
                     * d2
               )
               .add(
                  vec9.multiply(
                     x13
                        * 1.15
                        * d3
                  )
               );
         }

         return vec.add(
               vec4.multiply(
                  x8
                     + x9
               )
            )
            .add(
               vec3.multiply(
                  x10
                     + x11
                     + x12
               )
            )
            .add(
               vec8
            );
      }

      return vec;
   }

   public static Vec3d bypasselytrahacking(
      LivingEntity entity
   ) {
      return getElytraAimPoint(
         entity,
         entity.getPos()
      );
   }

   public static Vec3d predict(
      LivingEntity entity,
      Vec3d vec,
      int n
   ) {
      return predict(
         entity,
         (double)n
      );
   }

   public static boolean isTargetHoveringOnElytra(
      LivingEntity target
   ) {
      if (target
            != null
         && mc.player != null
         && mc.player.isGliding()
         && target.isGliding()
         )
       {
         Vec3d vec = target.getVelocity();
         Vec3d vec2 = vec.subtract(
            mc.player.getVelocity()
         );
         return vec.length()
               < 0.38
            || vec2.length()
               < 0.2;
      } else {
         return false;
      }
   }

   public static boolean isTargetLeavingOnElytra(
      LivingEntity target
   ) {
      if (target
            != null
         && mc.player != null
         && mc.player.isGliding()
         && target.isGliding()
         )
       {
         Vec3d vec = target.getVelocity()
            .subtract(mc.player.getVelocity());
         Vec3d vec2 = target.getVelocity();
         Vec3d vec3 = mc.player
            .getVelocity();
         if (vec.length()
               < 0.01
            || vec2.length()
               < 0.04) {
            return false;
         } else if (isTargetHoveringOnElytra(
            target
         )) {
            return false;
         } else {
            Vec3d vec4 = vec.normalize();
            double dist = vec4.dotProduct(
               vec2.normalize()
            );
            double dist2 = vec2.subtract(
                  vec3
               )
               .normalize()
               .dotProduct(
                  vec4
               );
            double dist3 = (double)mc.player
               .distanceTo(
                  target
               );
            return dist
                  > 0.72
               && dist2
                  > 0.18
               && vec2.y
                  > 0.55
               && dist3
                  > 2.2;
         }
      } else {
         return false;
      }
   }

   public static boolean isFastTargetLeavingOnElytra(
      LivingEntity target
   ) {
      if (target
            != null
         && mc.player != null
         && mc.player.isGliding()
         && target.isGliding()
         )
       {
         Vec3d vec = target.getVelocity()
            .subtract(mc.player.getVelocity());
         Vec3d vec2 = target.getVelocity();
         Vec3d vec3 = mc.player
            .getVelocity();
         if (vec.length()
               < 0.01
            || vec2.length()
               < 0.09) {
            return false;
         } else if (isTargetHoveringOnElytra(
            target
         )) {
            return false;
         } else {
            Vec3d vec4 = vec.normalize();
            double dist = vec4.dotProduct(
               vec2.normalize()
            );
            double dist2 = vec2.subtract(
                  vec3
               )
               .normalize()
               .dotProduct(
                  vec4
               );
            double dist3 = (double)mc.player
               .distanceTo(
                  target
               );
            double dist4 = vec2.length();
            double dist5 = vec3.length();
            return dist
                  > 0.84
               && dist2
                  > 0.42
               && dist4
                  > 1.05
               && dist4
                     - dist5
                  > 0.14
               && dist3
                  > 2.7;
         }
      } else {
         return false;
      }
   }

   public static boolean shouldOvertake(
      LivingEntity entity
   ) {
      return entity
               != null
            && mc.player != null
            && mc.player.isGliding()
            && entity.isGliding()
         ? isFastTargetLeavingOnElytra(
               entity
            )
            || isTargetLeavingOnElytra(
               entity
            )
         : false;
   }

   public static Vec3d getElytraAimPoint(
      LivingEntity entity,
      Vec3d vec
   ) {
      if (entity
            != null
         && entity.isGliding()
         && ModuleManager.elytraTarget != null) {
         double x = ModuleManager.elytraTarget
            .predictValue
            .getValue()
            .doubleValue();
         boolean flag = ModuleManager.elytraTarget
            .smartElytraChase
            .isState();
         double x2 = ModuleManager.elytraTarget
            .chaseLateral
            .getValue()
            .doubleValue();
         double x3 = ModuleManager.elytraTarget
            .chaseForward
            .getValue()
            .doubleValue();
         if (isFastTargetLeavingOnElytra(
            entity
         )) {
            double x4 = 0.0;
            if (entity instanceof PlayerEntity
               )
             {
               PlayerListEntry val = mc.getNetworkHandler()
                  .getPlayerListEntry(
                     entity.getUuid()
                  );
               if (val
                  != null) {
                  x4 = Math.min(
                     (double)val.getLatency()
                        / 50.0,
                     5.0
                  );
               }
            }

            double x5 = Math.min(
               1.3
                  + x4
                     * 0.45,
               2.6
            );
            double x6 = Math.max(
               x
                  - 0.5,
               1.0
            );
            Vec3d vec2;
            if (flag
               )
             {
               vec2 = getPredicted(
                  entity,
                  x6,
                  x2,
                  x3
               );
            } else {
               vec2 = getPredicted(
                  entity,
                  x6
               );
            }

            Vec3d vec3 = entity.getPos()
               .add(
                  0.0,
                  (double)entity.getHeight()
                     / 2.0,
                  0.0
               );
            Vec3d vec4 = vec3.add(
               entity.getVelocity()
                  .multiply(
                     x5
                  )
            );
            return vec2.multiply(
                  0.65
               )
               .add(
                  vec4.multiply(
                     0.35
                  )
               );
         } else {
            if (isTargetLeavingOnElytra(
               entity
            )) {
               x += 2.5;
            }

            return flag
               ? getPredicted(
                  entity,
                  x,
                  x2,
                  x3
               )
               : getPredicted(
                  entity,
                  x
               );
         }
      } else {
         return entity
               != null
            ? entity.getEyePos()
            : Vec3d.ZERO;
      }
   }

   public static Vec3d getElytraAimPointRender(
      LivingEntity entity,
      float f
   ) {
      if (entity
            != null
         && entity.isGliding()
         && ModuleManager.elytraTarget != null) {
         double x = ModuleManager.elytraTarget
            .predictValue
            .getValue()
            .doubleValue();
         boolean flag = ModuleManager.elytraTarget
            .smartElytraChase
            .isState();
         double x2 = ModuleManager.elytraTarget
            .chaseLateral
            .getValue()
            .doubleValue();
         double x3 = ModuleManager.elytraTarget
            .chaseForward
            .getValue()
            .doubleValue();
         if (isFastTargetLeavingOnElytra(
            entity
         )) {
            double x4 = 0.0;
            if (entity instanceof PlayerEntity
               )
             {
               PlayerListEntry val = mc.getNetworkHandler()
                  .getPlayerListEntry(
                     entity.getUuid()
                  );
               if (val
                  != null) {
                  x4 = Math.min(
                     (double)val.getLatency()
                        / 50.0,
                     5.0
                  );
               }
            }

            double x5 = Math.min(
               1.3
                  + x4
                     * 0.45,
               2.6
            );
            double x6 = Math.max(
               x
                  - 0.5,
               1.0
            );
            Vec3d vec;
            if (flag
               )
             {
               vec = getPredictedRender(
                  entity,
                  x6,
                  f,
                  x2,
                  x3
               );
            } else {
               vec = getPredictedRender(
                  entity,
                  x6,
                  f
               );
            }

            Vec3d vec2 = entity.getPos()
               .add(
                  0.0,
                  (double)entity.getHeight()
                     / 2.0,
                  0.0
               );
            Vec3d vec3 = vec2.add(
               entity.getVelocity()
                  .multiply(
                     x5
                  )
            );
            return vec.multiply(
                  0.65
               )
               .add(
                  vec3.multiply(
                     0.35
                  )
               );
         } else {
            if (isTargetLeavingOnElytra(
               entity
            )) {
               x += 2.5;
            }

            return flag
               ? getPredictedRender(
                  entity,
                  x,
                  f,
                  x2,
                  x3
               )
               : getPredictedRender(
                  entity,
                  x,
                  f
               );
         }
      } else {
         return entity
               != null
            ? entity.getEyePos()
            : Vec3d.ZERO;
      }
   }

   @Generated
   private GetsmartpredictedpositionHelper() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
