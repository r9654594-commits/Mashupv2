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
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public final class Clamp01Helper {
   public static float clamp01(
      float f
   ) {
      return (float)clamp(
         0.0,
         1.0,
         (double)f
      );
   }

   public static double getRandom(
      double d,
      double d2
   ) {
      if (d
         == d2
         )
       {
         return d;
      } else {
         if (d
            > d2
            )
          {
            double x = d;
            d = d2;
            d2 = x;
         }

         return ThreadLocalRandom.current().nextDouble()
               * (
                  d2
                     - d
               )
            + d;
      }
   }

   public static float calculateDelta(
      float f,
      float f2
   ) {
      return f
         - f2;
   }

   public static double round(
      double d,
      int n
   ) {
      double d2 = Math.pow(
         10.0,
         (double)n
      );
      return (double)Math.round(
            d
               * d2
         )
         / d2;
   }

   public static Number round(
      double d,
      double d2
   ) {
      if (d2
         <= 0.0) {
         throw new IllegalArgumentException(
            "Increment must be greater than zero"
         );
      } else {
         double x = (double)Math.round(
               d
                  / d2
            )
            * d2;
         BigDecimal val = BigDecimal.valueOf(
            x
         );
         val = val.setScale(
            2, RoundingMode.HALF_UP
         );
         return val.doubleValue();
      }
   }

   public static String formatTime(
      long time
   ) {
      long time2 = time
         / 3600000L;
      long time3 = time
         % 3600000L
         / 60000L;
      long time4 = time
         % 360000L
         % 60000L
         / 1000L;
      String var8 = "%02d:%02d:%02d";
      Object[] var10001 = new Object[3];
      var10001[0] = time2;
      var10001[1] = time3;
      var10001[2] = time4;
      return String.format(var8, var10001);
   }

   public static float slerp(
      float f,
      float f2,
      float f3
   ) {
      f3 = Math.max(
         0.0F,
         Math.min(
            1.0F,
            f3
         )
      );
      float x = (float)Math.toRadians(
         (double)f
      );
      float x2 = (float)Math.toRadians(
         (double)f2
      );
      float x3 = (float)Math.cos(
               (double)x
            )
            * (float)Math.cos(
               (double)x2
            )
         + (float)Math.sin(
               (double)x
            )
            * (float)Math.sin(
               (double)x2
            );
      float x4 = (float)Math.acos(
         (double)x3
      );
      if (Math.abs(
            x4
         )
         < 0.001F) {
         return f;
      } else {
         float x5 = (float)(
            Math.sin(
                  (double)(
                     (
                           1.0F
                              - f3
                        )
                        * x4
                  )
               )
               / Math.sin(
                  (double)x4
               )
         );
         float x6 = (float)(
            Math.sin(
                  (double)(
                     f3
                        * x4
                  )
               )
               / Math.sin(
                  (double)x4
               )
         );
         float x7 = f
               * x5
            + f2
               * x6;
         return (float)MathHelper.clamp(
            MathHelper.wrapDegrees(
               Math.toDegrees(
                  (double)x7
               )
            ),
            (double)f,
            (double)f2
         );
      }
   }

   public static double round(
      double d,
      int n,
      double d2
   ) {
      double d3 = d2
         / 2.0;
      double d4 = Math.floor(
            d
               / d2
         )
         * d2;
      return d
            >= d4
               + d3
         ? new BigDecimal(
               Math.ceil(
                     d
                        / d2
                  )
                  * d2
            )
            .setScale(
               n,
               RoundingMode.HALF_UP
            )
            .doubleValue()
         : new BigDecimal(
               d4
            )
            .setScale(
               n,
               RoundingMode.HALF_UP
            )
            .doubleValue();
   }

   public static double step(
      double d,
      double d2
   ) {
      double d3 = (double)Math.round(
            d
               / d2
         )
         * d2;
      d3 *= 1000.0;
      d3 = (double)(
         (int)d3
      );
      return d3
         / 1000.0;
   }

   public static double getDistance(
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6
   ) {
      double dist = d4
         - d;
      double dist2 = d5
         - d2;
      double dist3 = d6
         - d3;
      return (double)MathHelper.sqrt(
         (float)(
            dist
                  * dist
               + dist2
                  * dist2
               + dist3
                  * dist3
         )
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

   public static int clamp(
      int n,
      int n2,
      int n3
   ) {
      return Math.max(
         n,
         Math.min(
            n2,
            n3
         )
      );
   }

   public static float normalize(
      float f,
      float f2,
      float f3
   ) {
      return (
            f
               - f2
         )
         / (
            f3
               - f2
         );
   }

   public static double interporate(
      double d,
      double d2,
      double d3
   ) {
      return d2
         + d
            * (
               d3
                  - d2
            );
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
            * f3;
   }

   public static float easeOutExpo(
      float f
   ) {
      return f
            == 1.0F
         ? 1.0F
         : (float)(
            1.0
               - Math.pow(
                  2.0,
                  (double)(
                     -10.0F
                        * f
                  )
               )
         );
   }

   @Generated
   private Clamp01Helper() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
