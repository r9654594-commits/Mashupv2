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


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DirectionHelper implements IMinecraft {
   public static ValidaterandomHelper fastRandomize = new ValidaterandomHelper();

   public static double direction(
      float f,
      double d,
      double d2
   ) {
      if (d
         < 0.0) {
         f += 180.0F;
      }

      float f2 = 1.0F;
      if (d
         < 0.0) {
         f2 = -0.5F;
      } else if (d
         > 0.0) {
         f2 = 0.5F;
      }

      if (d2
         > 0.0) {
         f -= 90.0F
            * f2;
      }

      if (d2
         < 0.0) {
         f += 90.0F
            * f2;
      }

      return Math.toRadians(
         (double)f
      );
   }

   public static float randomNew(
      double d,
      double d2
   ) {
      return d
            > d2
         ? (float)(
            (double)fastRandomize.nextFloat()
                  * (
                     d
                        - d2
                  )
               + d2
         )
         : (float)(
            (double)fastRandomize.nextFloat()
                  * (
                     d2
                        - d
                  )
               + d
         );
   }

   public static double getBps(
      Entity entity
   ) {
      double x = entity.getX()
         - entity.prevX;
      double x2 = entity.getY()
         - entity.prevY;
      double x3 = entity.getZ()
         - entity.prevZ;
      double x4 = Math.sqrt(
         x
               * x
            + x2
               * x2
            + x3
               * x3
      );
      return x4
         * 20.0;
   }

   public static float calculateBPS() {
      if (mc.player == null) {
         return 0.0F;
      } else {
         double x = mc.player
               .getX()
            - mc.player.prevX;
         double x2 = mc.player
               .getY()
            - mc.player.prevY;
         double x3 = mc.player
               .getZ()
            - mc.player.prevZ;
         double x4 = Math.sqrt(
            x
                  * x
               + x2
                  * x2
               + x3
                  * x3
         );
         float x5 = 1.0F;
         float x6 = (float)(
            x4
               * (double)x5
               * 20.0
         );
         return (float)Math.round(
               x6
                  * 10.0F
            )
            / 10.0F;
      }
   }

   public static double getTargetCompensatedSpeed(
      Entity entity
   ) {
      double y = 1.5;
      if (entity
         == null) {
         return 1.5;
      } else {
         double y2 = (double)calculateBPS();
         double y3 = 0.00342;
         double y4 = y2
            * 0.00342;
         return 1.5
            + y4;
      }
   }

   public static float random(
      float f,
      float f2
   ) {
      SecureRandom val = new SecureRandom();
      double x = val.nextDouble();
      double x2 = val.nextDouble();
      double x3 = val.nextGaussian()
         * 0.02F;
      double x4 = Math.pow(
         x,
         1.0
            + val.nextDouble()
               * 0.7
      );
      double x5 = (
            x2
                  * 0.8
               + 0.1
         )
         * (
            Math.log1p(
                     x
                        * 3.0
                  )
                  * 0.5
               + 0.5
         );
      return (float)(
         (double)f
            + (double)(
                  f2
                     - f
               )
               * x4
               * x5
            + x3
      );
   }

   public static float randomInRange(
      float f,
      float f2
   ) {
      return (float)ThreadLocalRandom.current()
         .nextDouble(
            (double)f,
            (double)f2
         );
   }

   public static int randomInRange(
      int n,
      int n2
   ) {
      return ThreadLocalRandom.current()
         .nextInt(
            n,
            n2
               + 1
         );
   }

   public static double randomBest(
      double d,
      double d2
   ) {
      return ThreadLocalRandom.current().nextDouble()
            * (
               d2
                  - d
            )
         + d;
   }

   public static boolean isHovered(
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6
   ) {
      return d5
            >= d
         && d6
            >= d2
         && d5
            < d
               + d3
         && d6
            < d2
               + d4;
   }

   public static float interpolate(
      float f,
      float f2,
      float f3
   ) {
      return f
         + (
               f2
                  - f
            )
            * f3;
   }

   public static Vec3d interpolate(
      Vec3d vec,
      Vec3d vec2,
      float f
   ) {
      return new Vec3d(
         interpolate(
            vec.getX(),
            vec2.getX(),
            (double)f
         ),
         interpolate(
            vec.getY(),
            vec2.getY(),
            (double)f
         ),
         interpolate(
            vec.getZ(),
            vec2.getZ(),
            (double)f
         )
      );
   }

   public static Vec3d interpolate(
      Entity entity,
      float f
   ) {
      double x = MathHelper.lerp(
         (double)f,
         entity.prevX,
         entity.getX()
      );
      double x2 = MathHelper.lerp(
         (double)f,
         entity.prevY,
         entity.getY()
      );
      double x3 = MathHelper.lerp(
         (double)f,
         entity.prevZ,
         entity.getZ()
      );
      return new Vec3d(
         x,
         x2,
         x3
      );
   }

   public static double interpolate(
      double d,
      double d2,
      double d3
   ) {
      return d2
         + (
               d
                  - d2
            )
            * d3;
   }

   public static float round(
      float f
   ) {
      return (float)Math.round(
            f
               * 10.0F
         )
         / 10.0F;
   }

   public static double round(
      double d,
      double d2
   ) {
      double d3 = (double)Math.round(
            d
               / d2
         )
         * d2;
      BigDecimal val = new BigDecimal(
         d3
      );
      val = val.setScale(
         2, RoundingMode.HALF_UP
      );
      return val.doubleValue();
   }

   public static float lerp(
      float f,
      float f2,
      float f3
   ) {
      return f
         + (
               f2
                  - f
            )
            * clamp(
               f3,
               0.0F,
               1.0F
            );
   }

   public static float clamp(
      float f,
      float f2,
      float f3
   ) {
      return f
            <= f2
         ? f2
         : Math.min(
            f,
            f3
         );
   }

   public static double clamp(
      double d,
      double d2,
      double d3
   ) {
      return Math.max(
         d,
         Math.min(
            d2,
            d3
         )
      );
   }

   public static <T extends Number> T ler1p(
      T val,
      T val2,
      double d
   ) {
      double x = val.doubleValue();
      double x2 = val2.doubleValue();
      double x3 = x
         + d
            * (
               x2
                  - x
            );
      if (val instanceof Integer
         )
       {
         return (T)(Object)(int)Math.round(
            x3
         );
      } else if (val instanceof Double
         )
       {
         return (T)(Object)x3;
      } else if (val instanceof Float
         )
       {
         return (T)(Object)(float)x3;
      } else if (val instanceof Long
         )
       {
         return (T)(Object)Math.round(
            x3
         );
      } else if (val instanceof Short
         )
       {
         return (T)(Object)(short)(
            (int)Math.round(
               x3
            )
         );
      } else if (val instanceof Byte
         )
       {
         return (T)(Object)(byte)(
            (int)Math.round(
               x3
            )
         );
      } else {
         throw new IllegalArgumentException(
            "Unsupported type: "
               + val.getClass()
                  .getSimpleName()
         );
      }
   }
}
