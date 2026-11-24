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



public abstract class CurveHelper {
   private static final double EPSILON = 1.0E-12;
   public static final CurveHelper DISCRETE = new CurveHelper() {
      @Override
      protected double curve(
         double d
      ) {
         return Math.abs(
                  d
                     - 1.0
               )
               < 1.0E-12
            ? 1.0
            : 0.0;
      }

      @Override
      public String toString() {
         return "Interpolator.DISCRETE";
      }
   };
   public static final CurveHelper LINEAR = new CurveHelper() {
      @Override
      protected double curve(
         double d
      ) {
         return d;
      }

      @Override
      public String toString() {
         return "Interpolator.LINEAR";
      }
   };
   public static final CurveHelper EASE_BOTH = new CurveHelper() {
      @Override
      protected double curve(
         double d
      ) {
         return CurveHelper.clamp(
            d
                  < 0.2
               ? 3.125
                  * d
                  * d
               : (
                  d
                        > 0.8
                     ? -3.125
                           * d
                           * d
                        + 6.25
                           * d
                        - 2.125
                     : 1.25
                           * d
                        - 0.125
               )
         );
      }

      @Override
      public String toString() {
         return "Interpolator.EASE_BOTH";
      }
   };
   public static final CurveHelper EASE_IN = new CurveHelper() {
      private static final double S1 = 2.7777777777777777;
      private static final double S3 = 1.1111111111111112;
      private static final double S4 = 0.1111111111111111;

      @Override
      protected double curve(
         double d
      ) {
         return CurveHelper.clamp(
            d
                  < 0.2
               ? 2.7777777777777777
                  * d
                  * d
               : 1.1111111111111112
                     * d
                  - 0.1111111111111111
         );
      }

      @Override
      public String toString() {
         return "Interpolator.EASE_IN";
      }
   };
   public static final CurveHelper EASE_OUT = new CurveHelper() {
      private static final double S1 = -2.7777777777777777;
      private static final double S2 = 5.555555555555555;
      private static final double S3 = -1.7777777777777777;
      private static final double S4 = 1.1111111111111112;

      @Override
      protected double curve(
         double d
      ) {
         return CurveHelper.clamp(
            d
                  > 0.8
               ? -2.7777777777777777
                     * d
                     * d
                  + 5.555555555555555
                     * d
                  + -1.7777777777777777
               : 1.1111111111111112
                  * d
         );
      }

      @Override
      public String toString() {
         return "Interpolator.EASE_OUT";
      }
   };

   protected CurveHelper() {
   }

   public boolean interpolate(
      boolean flag,
      boolean flag2,
      double d
   ) {
      return Math.abs(
               this.curve(
                     d
                  )
                  - 1.0
            )
            < 1.0E-12
         ? flag2
         : flag;
   }

   public double interpolate(
      double d,
      double d2,
      double d3
   ) {
      return d
         + (
               d2
                  - d
            )
            * this.curve(
               d3
            );
   }

   public int interpolate(
      int n,
      int n2,
      double d
   ) {
      return n
         + (int)Math.round(
            (double)(
                  n2
                     - n
               )
               * this.curve(
                  d
               )
         );
   }

   public long interpolate(
      long time,
      long time2,
      double d
   ) {
      return time
         + Math.round(
            (double)(
                  time2
                     - time
               )
               * this.curve(
                  d
               )
         );
   }

   private static double clamp(
      double d
   ) {
      return d
            < 0.0
         ? 0.0
         : (
            d
                  > 1.0
               ? 1.0
               : d
         );
   }

   protected abstract double curve(double var1);
}
