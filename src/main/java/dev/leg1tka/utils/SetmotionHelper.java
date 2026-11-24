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


import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SetmotionHelper {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public static void setMotion(
      double d
   ) {
      if (mc.player != null) {
         double y2 = (double)mc.player
            .input
            .movementForward;
         double y3 = (double)mc.player
            .input
            .movementSideways;
         float yaw = mc.player
            .getYaw();
         if (y2
               == 0.0
            && y3
               == 0.0) {
            mc.player.setVelocity(0.0, mc.player.getVelocity().y, 0.0);
         } else {
            if (y2
               != 0.0) {
               if (y3
                  > 0.0) {
                  yaw += (float)(
                     y2
                           > 0.0
                        ? -45
                        : 45
                  );
               } else if (y3
                  < 0.0) {
                  yaw += (float)(
                     y2
                           > 0.0
                        ? 45
                        : -45
                  );
               }

               y3 = 0.0;
               if (y2
                  > 0.0) {
                  y2 = 1.0;
               } else if (y2
                  < 0.0) {
                  y2 = -1.0;
               }
            }

            double y4 = y2
                  * d
                  * (double)MathHelper.cos(
                     (float)Math.toRadians(
                        (double)(
                           yaw
                              + 90.0F
                        )
                     )
                  )
               + y3
                  * d
                  * (double)MathHelper.sin(
                     (float)Math.toRadians(
                        (double)(
                           yaw
                              + 90.0F
                        )
                     )
                  );
            double y5 = y2
                  * d
                  * (double)MathHelper.sin(
                     (float)Math.toRadians(
                        (double)(
                           yaw
                              + 90.0F
                        )
                     )
                  )
               - y3
                  * d
                  * (double)MathHelper.cos(
                     (float)Math.toRadians(
                        (double)(
                           yaw
                              + 90.0F
                        )
                     )
                  );
            mc.player
               .setVelocity(
                  y4,
                  mc.player.getVelocity().y,
                  y5
               );
         }
      }
   }

   public static double getSpeed() {
      if (mc.player == null) {
         return 0.0;
      } else {
         Vec3d vec = mc.player
            .getVelocity();
         return Math.sqrt(
            vec.x
                  * vec.x
               + vec.z
                  * vec.z
         );
      }
   }

   public static void setVelocity(
      double d
   ) {
      double[] val = HasplayermovementHelper.calculateDirection(
         d
      );
      Objects.requireNonNull(mc.player)
         .setVelocity(
            val[0],
            mc.player.getVelocity().getY(),
            val[1]
         );
   }

   public static void setVelocity(
      double d,
      double d2
   ) {
      double[] val = HasplayermovementHelper.calculateDirection(
         d
      );
      Objects.requireNonNull(mc.player)
         .setVelocity(
            val[0],
            d2,
            val[1]
         );
   }

   public static void strafe() {
      strafe(getSpeed());
   }

   public static void strafe(
      double d
   ) {
      if (mc.player != null) {
         float yaw = mc.player
            .getYaw();
         double y2 = (double)mc.player
            .input
            .movementForward;
         double y3 = (double)mc.player
            .input
            .movementSideways;
         if (y2
               == 0.0
            && y3
               == 0.0) {
            mc.player.setVelocity(0.0, mc.player.getVelocity().y, 0.0);
         } else {
            if (y2
               != 0.0) {
               if (y3
                  > 0.0) {
                  yaw += y2
                        > 0.0
                     ? -45.0F
                     : 45.0F;
               } else if (y3
                  < 0.0) {
                  yaw += y2
                        > 0.0
                     ? 45.0F
                     : -45.0F;
               }

               y3 = 0.0;
               y2 = y2
                     > 0.0
                  ? 1.0
                  : -1.0;
            }

            double y4 = Math.toRadians(
               (double)(
                  yaw
                     + 90.0F
               )
            );
            double y5 = y2
                  * d
                  * Math.cos(
                     y4
                  )
               + y3
                  * d
                  * Math.sin(
                     y4
                  );
            double y6 = y2
                  * d
                  * Math.sin(
                     y4
                  )
               - y3
                  * d
                  * Math.cos(
                     y4
                  );
            mc.player
               .setVelocity(
                  y5,
                  mc.player.getVelocity().y,
                  y6
               );
         }
      }
   }

   public static boolean isMoving() {
      return mc.player == null ? false : mc.player.input.movementForward != 0.0F || mc.player.input.movementSideways != 0.0F;
   }
}
