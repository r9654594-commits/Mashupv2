package dev.leg1tka.animation;
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
import dev.leg1tka.utils.*;


public interface Easing {
   public Easing SMOOTH_STEP = (val, val2, val3, val4) -> {
      float f = val3
            * val
            / val4
         + val2;
      return (float)(
         -2.0
               * Math.pow(
                  (double)f,
                  3.0
               )
            + 3.0
               * Math.pow(
                  (double)f,
                  2.0
               )
      );
   };
   public Easing BOTH_CUBIC = (val, val2, val3, val4) -> {
      float f = val3
            * val
            / val4
         + val2;
      return (double)f
            < 0.5
         ? 4.0F
            * f
            * f
            * f
         : (float)(
            1.0
               - Math.pow(
                     (double)(
                        -2.0F
                              * f
                           + 2.0F
                     ),
                     3.0
                  )
                  / 2.0
         );
   };
   public Easing LINEAR = (val, val2, val3, val4) -> val3
            * val
            / val4
         + val2;
   public Easing QUAD_IN = (val5, val6, val7, val8) -> {
      float var4;
      return val7
            * (
               var4 = val5
                  / val8
            )
            * var4
         + val6;
   };
   public Easing QUAD_OUT = (val, val2, val3, val4) -> {
      float var4;
      return -val3
            * (
               var4 = val
                  / val4
            )
            * (var4 - 2.0F)
         + val2;
   };
   public Easing QUAD_IN_OUT = (val, val2, val3, val4) -> {
      float var4;
      return (
               var4 = val
                  / (
                     val4
                        / 2.0F
                  )
            )
            < 1.0F
         ? val3
               / 2.0F
               * var4
               * var4
            + val2
         : -val3
               / 2.0F
               * (--var4 * (var4 - 2.0F) - 1.0F)
            + val2;
   };
   public Easing CUBIC_IN = (val, val2, val3, val4) -> {
      float var4;
      return val3
            * (
               var4 = val
                  / val4
            )
            * var4
            * var4
         + val2;
   };
   public Easing CUBIC_OUT = (val, val2, val3, val4) -> {
      float var4;
      return val3
            * (
               (
                        var4 = val
                              / val4
                           - 1.0F
                     )
                     * var4
                     * var4
                  + 1.0F
            )
         + val2;
   };
   public Easing CUBIC_IN_OUT = (val, val2, val3, val4) -> {
      float var4;
      float var5;
      return (
               var4 = val
                  / (
                     val4
                        / 2.0F
                  )
            )
            < 1.0F
         ? val3
               / 2.0F
               * var4
               * var4
               * var4
            + val2
         : val3
               / 2.0F
               * ((var5 = var4 - 2.0F) * var5 * var5 + 2.0F)
            + val2;
   };
   public Easing QUARTIC_IN = (val, val2, val3, val4) -> {
      float var4;
      return val3
            * (
               var4 = val
                  / val4
            )
            * var4
            * var4
            * var4
         + val2;
   };
   public Easing QUARTIC_OUT = (val, val2, val3, val4) -> {
      float var4;
      return -val3
            * (
               (
                        var4 = val
                              / val4
                           - 1.0F
                     )
                     * var4
                     * var4
                     * var4
                  - 1.0F
            )
         + val2;
   };
   public Easing QUARTIC_IN_OUT = (val, val2, val3, val4) -> {
      float var4;
      float var5;
      return (
               var4 = val
                  / (
                     val4
                        / 2.0F
                  )
            )
            < 1.0F
         ? val3
               / 2.0F
               * var4
               * var4
               * var4
               * var4
            + val2
         : -val3
               / 2.0F
               * ((var5 = var4 - 2.0F) * var5 * var5 * var5 - 2.0F)
            + val2;
   };
   public Easing QUINTIC_IN = (val, val2, val3, val4) -> {
      float var4;
      return val3
            * (
               var4 = val
                  / val4
            )
            * var4
            * var4
            * var4
            * var4
         + val2;
   };
   public Easing QUINTIC_OUT = (val, val2, val3, val4) -> {
      float var4;
      return val3
            * (
               (
                        var4 = val
                              / val4
                           - 1.0F
                     )
                     * var4
                     * var4
                     * var4
                     * var4
                  + 1.0F
            )
         + val2;
   };
   public Easing QUINTIC_IN_OUT = (val, val2, val3, val4) -> {
      float var4;
      float var5;
      return (
               var4 = val
                  / (
                     val4
                        / 2.0F
                  )
            )
            < 1.0F
         ? val3
               / 2.0F
               * var4
               * var4
               * var4
               * var4
               * var4
            + val2
         : val3
               / 2.0F
               * ((var5 = var4 - 2.0F) * var5 * var5 * var5 * var5 + 2.0F)
            + val2;
   };
   public Easing SINE_IN = (val, val2, val3, val4) -> -val3
            * (float)Math.cos(
               (double)(
                     val
                        / val4
                  )
                  * (Math.PI / 2)
            )
         + val3
         + val2;
   public Easing SINE_OUT = (val5, val6, val7, val8) -> val7
            * (float)Math.sin(
               (double)(
                     val5
                        / val8
                  )
                  * (Math.PI / 2)
            )
         + val6;
   public Easing SINE_IN_OUT = (val9, val10, val11, val12) -> -val11
            / 2.0F
            * (
               (float)Math.cos(
                     Math.PI
                        * (double)val9
                        / (double)val12
                  )
                  - 1.0F
            )
         + val10;
   public Easing EXPO_IN = (val13, val14, val15, val16) -> val13
            == 0.0F
         ? val14
         : val15
               * (float)Math.pow(
                  2.0,
                  (double)(
                     10.0F
                        * (
                           val13
                                 / val16
                              - 1.0F
                        )
                  )
               )
            + val14;
   public Easing EXPO_OUT = (val17, val18, val19, val20) -> val17
            == val20
         ? val18
            + val19
         : val19
               * (
                  -(
                        (float)Math.pow(
                           2.0,
                           (double)(
                              -10.0F
                                 * val17
                                 / val20
                           )
                        )
                     )
                     + 1.0F
               )
            + val18;
   public Easing EXPO_IN_OUT = (val21, val22, val23, val24) -> {
      if (val21
         == 0.0F) {
         return val22;
      } else if (val21
         == val24
         )
       {
         return val22
            + val23;
      } else {
         float var4;
         return (
                  var4 = val21
                     / (
                        val24
                           / 2.0F
                     )
               )
               < 1.0F
            ? val23
                  / 2.0F
                  * (float)Math.pow(2.0, (double)(10.0F * (var4 - 1.0F)))
               + val22
            : val23
                  / 2.0F
                  * (-((float)Math.pow(2.0, (double)(-10.0F * --var4))) + 2.0F)
               + val22;
      }
   };
   public Easing CIRC_IN = (val, val2, val3, val4) -> {
      float var4;
      return -val3
            * (
               (float)Math.sqrt(
                     (double)(
                        1.0F
                           - (
                                 var4 = val
                                    / val4
                              )
                              * var4
                     )
                  )
                  - 1.0F
            )
         + val2;
   };
   public Easing CIRC_OUT = (val, val2, val3, val4) -> {
      float var4;
      return val3
            * (float)Math.sqrt(
               (double)(
                  1.0F
                     - (
                           var4 = val
                                 / val4
                              - 1.0F
                        )
                        * var4
               )
            )
         + val2;
   };
   public Easing CIRC_IN_OUT = (val, val2, val3, val4) -> {
      float var4;
      float var5;
      return (
               var4 = val
                  / (
                     val4
                        / 2.0F
                  )
            )
            < 1.0F
         ? -val3
               / 2.0F
               * ((float)Math.sqrt((double)(1.0F - var4 * var4)) - 1.0F)
            + val2
         : val3
               / 2.0F
               * ((float)Math.sqrt((double)(1.0F - (var5 = var4 - 2.0F) * var5)) + 1.0F)
            + val2;
   };
   public BackOutEasing ELASTIC_IN = new EaseHelper_4();
   public BackOutEasing ELASTIC_OUT = new EaseHelper_1();
   public BackOutEasing ELASTIC_IN_OUT = new EaseHelper_5();
   public OvershootEasing BACK_IN = new EaseHelper_3();
   public OvershootEasing BACK_OUT = new EaseHelper();
   public OvershootEasing BACK_OUT_BIGGY_OVERSHOOT = new EaseHelper(5.70158F);
   public OvershootEasing BACK_IN_OUT = new EaseHelper_2();
   public Easing BOUNCE_OUT = (val, val2, val3, val4) -> {
      if ((
            val = val
               / val4
         )
         < 0.36363637F) {
         return val3
               * 7.5625F
               * val
               * val
            + val2;
      } else if (val
         < 0.72727275F) {
         float var7;
         return val3
               * (
                  7.5625F
                        * (
                           var7 = val
                              - 0.54545456F
                        )
                        * var7
                     + 0.75F
               )
            + val2;
      } else {
         float var5;
         float var6;
         return val
               < 0.90909094F
            ? val3
                  * (
                     7.5625F
                           * (
                              var5 = val
                                 - 0.8181818F
                           )
                           * var5
                        + 0.9375F
                  )
               + val2
            : val3
                  * (
                     7.5625F
                           * (
                              var6 = val
                                 - 0.95454544F
                           )
                           * var6
                        + 0.984375F
                  )
               + val2;
      }
   };
   public Easing BOUNCE_IN = (field_103, field_104, field_105, field_106) -> field_105
         - BOUNCE_OUT.ease(
            field_106
               - field_103,
            0.0F,
            field_105,
            field_106
         )
         + field_104;
   public Easing BOUNCE_IN_OUT = (field_107, field_108, field_109, field_110) -> field_107
            < field_110
               / 2.0F
         ? BOUNCE_IN.ease(
                  field_107
                     * 2.0F,
                  0.0F,
                  field_109,
                  field_110
               )
               * 0.5F
            + field_108
         : BOUNCE_OUT.ease(
                  field_107
                        * 2.0F
                     - field_110,
                  0.0F,
                  field_109,
                  field_110
               )
               * 0.5F
            + field_109
               * 0.5F
            + field_108;

   public float ease(float var1, float var2, float var3, float var4);
}
