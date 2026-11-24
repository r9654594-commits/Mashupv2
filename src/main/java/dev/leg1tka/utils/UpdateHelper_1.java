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


import net.minecraft.util.math.MathHelper;

public final class UpdateHelper_1 implements IMinecraft {
   private static float lastYaw;
   private static float lastPitch;
   private static double lastX;
   private static double lastY;
   private static double lastZ;
   private static boolean initialized;
   private static float motionAmount;
   private static float motionVelX;
   private static float motionVelY;

   private UpdateHelper_1() {
   }

   public static void update() {
      if (mc != null && mc.player != null) {
         float yaw = mc.player
            .getYaw();
         float yaw2 = mc.player
            .getPitch();
         double x = mc.player
            .getX();
         double x2 = mc.player
            .getY();
         double x3 = mc.player
            .getZ();
         if (!initialized) {
            lastYaw = yaw;
            lastPitch = yaw2;
            lastX = x;
            lastY = x2;
            lastZ = x3;
            initialized = true;
         } else {
            float yaw3 = MathHelper.wrapDegrees(
               yaw
                  - lastYaw
            );
            float yaw4 = yaw2
               - lastPitch;
            double x4 = x
               - lastX;
            double x5 = x2
               - lastY;
            double x6 = x3
               - lastZ;
            float yaw5 = (float)Math.sqrt(
               (double)(
                  yaw3
                        * yaw3
                     + yaw4
                        * yaw4
               )
            );
            float yaw6 = (float)Math.sqrt(
                  x4
                        * x4
                     + x5
                        * x5
                     + x6
                        * x6
               )
               * 20.0F;
            float yaw7 = MathHelper.clamp(
               yaw5
                     * 0.055F
                  + yaw6
                     * 0.42F,
               0.0F,
               1.35F
            );
            motionAmount = motionAmount * 0.78F
               + yaw7
                  * 0.22F;
            motionAmount = MathHelper.clamp(motionAmount, 0.0F, 1.0F);
            float yaw8 = yaw3
                  * 4.2E-4F
               + (float)x4
                  * 0.0018F;
            float yaw9 = -yaw4
                  * 4.2E-4F
               + (float)x5
                  * 0.0024F
               - (float)x6
                  * 6.0E-4F;
            motionVelX = motionVelX * 0.72F
               + yaw8
                  * 0.28F;
            motionVelY = motionVelY * 0.72F
               + yaw9
                  * 0.28F;
            lastYaw = yaw;
            lastPitch = yaw2;
            lastX = x;
            lastY = x2;
            lastZ = x3;
         }
      } else {
         motionAmount *= 0.9F;
         motionVelX *= 0.85F;
         motionVelY *= 0.85F;
      }
   }

   public static float getMotionAmount() {
      return motionAmount;
   }

   public static float getMotionVelX() {
      return motionVelX;
   }

   public static float getMotionVelY() {
      return motionVelY;
   }

   public static float getBlurOffsetBoost() {
      return 1.0F + motionAmount * 3.2F;
   }
}
