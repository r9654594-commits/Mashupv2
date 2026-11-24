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


import dev.leg1tka.api.events.implement.EventMoveInput;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class HasplayermovementHelper implements IMinecraft {
   public static boolean hasPlayerMovement() {
      return mc.player.input.movementForward != 0.0F || mc.player.input.movementSideways != 0.0F;
   }

   public static double[] calculateDirection(
      double d
   ) {
      float yaw = mc.player
         .input
         .movementForward;
      float yaw2 = mc.player
         .input
         .movementSideways;
      float yaw3 = mc.player
         .getYaw();
      if (yaw
         != 0.0F) {
         if (yaw2
            > 0.0F) {
            yaw3 += yaw
                  > 0.0F
               ? -45.0F
               : 45.0F;
         } else if (yaw2
            < 0.0F) {
            yaw3 += yaw
                  > 0.0F
               ? 45.0F
               : -45.0F;
         }

         yaw2 = 0.0F;
         yaw = yaw
               > 0.0F
            ? 1.0F
            : -1.0F;
      }

      double y = Math.sin(
         Math.toRadians(
            (double)(
               yaw3
                  + 90.0F
            )
         )
      );
      double y2 = Math.cos(
         Math.toRadians(
            (double)(
               yaw3
                  + 90.0F
            )
         )
      );
      double y3 = (double)yaw
            * d
            * y2
         + (double)yaw2
            * d
            * y;
      double y4 = (double)yaw
            * d
            * y
         - (double)yaw2
            * d
            * y2;
      double[] var10000 = new double[2];
      var10000[0] = y3;
      var10000[1] = y4;
      return var10000;
   }

   public static double getSpeedSqrt(
      Entity entity
   ) {
      double x = entity.getX()
         - entity.prevX;
      double x2 = entity.getY()
         - entity.prevY;
      double x3 = entity.getZ()
         - entity.prevZ;
      return Math.sqrt(
         x
               * x
            + x3
               * x3
            + x2
               * x2
      );
   }

   public static void setVelocity(
      double d
   ) {
      double[] val = calculateDirection(
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
      double[] val = calculateDirection(
         d
      );
      Objects.requireNonNull(mc.player)
         .setVelocity(
            val[0],
            d2,
            val[1]
         );
   }

   public static double getDegreesRelativeToView(
      Vec3d vec,
      float f
   ) {
      float x2 = (float)Math.atan2(
         -vec.x,
         vec.z
      );
      double x3 = Math.toRadians(
         (double)MathHelper.wrapDegrees(
            f
         )
      );
      return Math.toDegrees(
         MathHelper.wrapDegrees(
            (double)x2
               - x3
         )
      );
   }

   public static PlayerInput getDirectionalInputForDegrees(
      PlayerInput val,
      double d,
      float f
   ) {
      boolean flag = val.forward();
      boolean flag2 = val.backward();
      boolean flag3 = val.left();
      boolean flag4 = val.right();
      if (d
            >= (double)(
               -90.0F
                  + f
            )
         && d
            <= (double)(
               90.0F
                  - f
            )) {
         flag = true;
      } else if (d
            < (double)(
               -90.0F
                  - f
            )
         || d
            > (double)(
               90.0F
                  + f
            )) {
         flag2 = true;
      }

      if (d
            >= (double)(
               0.0F
                  + f
            )
         && d
            <= (double)(
               180.0F
                  - f
            )) {
         flag4 = true;
      } else if (d
            >= (double)(
               -180.0F
                  + f
            )
         && d
            <= (double)(
               0.0F
                  - f
            )) {
         flag3 = true;
      }

      return new PlayerInput(
         flag,
         flag2,
         flag3,
         flag4,
         val.jump(),
         val.sneak(),
         val.sprint()
      );
   }

   public static void fixMovementFocus(
      EventMoveInput event,
      float f
   ) {
      float x = event.getForward();
      float x2 = event.getStrafe();
      if (x
            != 0.0F
         || x2
            != 0.0F) {
         double x3 = MathHelper.wrapDegrees(
            Math.toDegrees(
               direction(
                  f,
                  x,
                  x2
               )
            )
         );
         float x4 = 0.0F;
         float x5 = 0.0F;
         float x6 = Float.MAX_VALUE;

         for (float x7 = -1.0F;
            x7
               <= 1.0F;
            x7++
         ) {
            for (float x8 = -1.0F;
               x8
                  <= 1.0F;
               x8++
            ) {
               if (x7
                     != 0.0F
                  || x8
                     != 0.0F) {
                  double x9 = MathHelper.wrapDegrees(
                     Math.toDegrees(
                        direction(
                           f,
                           x7,
                           x8
                        )
                     )
                  );
                  float x10 = Math.abs(
                     MathHelper.wrapDegrees(
                        (float)(
                           x3
                              - x9
                        )
                     )
                  );
                  if (x10
                     < x6
                     )
                   {
                     x6 = x10;
                     x4 = x7;
                     x5 = x8;
                  }
               }
            }
         }

         event.setForward(
            x4
         );
         event.setStrafe(
            x5
         );
      }
   }

   public static void fixMovementFree(
      EventMoveInput event
   ) {
      float yaw = event.getForward();
      float yaw2 = event.getStrafe();
      double x = MathHelper.wrapDegrees(
         Math.toDegrees(
            direction(
               mc.player.isGliding() ? mc.player.getYaw() : FreeLookStorage.getFreeYaw(),
               yaw,
               yaw2
            )
         )
      );
      if (yaw
            != 0.0F
         || yaw2
            != 0.0F) {
         float yaw3 = 0.0F;
         float yaw4 = 0.0F;
         float yaw5 = Float.MAX_VALUE;

         for (float yaw6 = -1.0F;
            yaw6
               <= 1.0F;
            yaw6++
         ) {
            for (float yaw7 = -1.0F;
               yaw7
                  <= 1.0F;
               yaw7++
            ) {
               if (yaw7
                     != 0.0F
                  || yaw6
                     != 0.0F) {
                  double x2 = MathHelper.wrapDegrees(
                     Math.toDegrees(
                        direction(
                           mc.player.getYaw(),
                           yaw6,
                           yaw7
                        )
                     )
                  );
                  double x3 = Math.abs(
                     x
                        - x2
                  );
                  if (x3
                     < (double)yaw5
                     )
                   {
                     yaw5 = (float)x3;
                     yaw3 = yaw6;
                     yaw4 = yaw7;
                  }
               }
            }
         }

         event.setForward(
            yaw3
         );
         event.setStrafe(
            yaw4
         );
      }
   }

   public static double direction(
      float f,
      float f2,
      float f3
   ) {
      if (f2
         < 0.0F) {
         f += 180.0F;
      }

      float f4 = 1.0F;
      if (f2
         < 0.0F) {
         f4 = -0.5F;
      }

      if (f2
         > 0.0F) {
         f4 = 0.5F;
      }

      if (f3
         > 0.0F) {
         f -= 90.0F
            * f4;
      }

      if (f3
         < 0.0F) {
         f += 90.0F
            * f4;
      }

      return Math.toRadians(
         (double)f
      );
   }

   public static PlayerInput getDirectionalInputForDegrees(
      PlayerInput val,
      double d
   ) {
      return getDirectionalInputForDegrees(
         val,
         d,
         20.0F
      );
   }

   @Generated
   private HasplayermovementHelper() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
