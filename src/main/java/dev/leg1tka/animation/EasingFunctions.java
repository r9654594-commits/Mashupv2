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


import lombok.Generated;

public final class EasingFunctions {
   public static final double c1 = 1.70158;
   public static final double c2 = 2.5949095;
   public static final double c3 = 2.70158;
   public static final double c4 = Math.PI * 2.0 / 3.0;
   public static final double c5 = Math.PI * 4.0 / 9.0;
   public static final EasingFunction LINEAR = val3 -> val3;
   public static final EasingFunction QUAD_IN = powIn(2);
   public static final EasingFunction QUAD_OUT = powOut(2);
   public static final EasingFunction QUAD_IN_OUT = powIN_OUT(2.0);
   public static final EasingFunction CUBIC_IN = powIn(3);
   public static final EasingFunction CUBIC_OUT = powOut(3);
   public static final EasingFunction CUBIC_IN_OUT = powIN_OUT(3.0);
   public static final EasingFunction QUART_IN = powIn(4);
   public static final EasingFunction QUART_OUT = powOut(4);
   public static final EasingFunction QUART_IN_OUT = powIN_OUT(4.0);
   public static final EasingFunction QUINT_IN = powIn(5);
   public static final EasingFunction QUINT_OUT = powOut(5);
   public static final EasingFunction QUINT_IN_OUT = powIN_OUT(5.0);
   public static final EasingFunction SINE_IN = val4 -> 1.0
         - Math.cos(
            val4
               * Math.PI
               / 2.0
         );
   public static final EasingFunction SINE_OUT = val5 -> Math.sin(
         val5
            * Math.PI
            / 2.0
      );
   public static final EasingFunction SINE_IN_OUT = val6 -> -(
            Math.cos(
                  Math.PI
                     * val6
               )
               - 1.0
         )
         / 2.0;
   public static final EasingFunction CIRC_IN = val -> 1.0
         - Math.sqrt(
            1.0
               - Math.pow(
                  val,
                  2.0
               )
         );
   public static final EasingFunction CIRC_OUT = val7 -> Math.sqrt(
         1.0
            - Math.pow(
               val7
                  - 1.0,
               2.0
            )
      );
   public static final EasingFunction CIRC_IN_OUT = val2 -> val2
            < 0.5
         ? (
               1.0
                  - Math.sqrt(
                     1.0
                        - Math.pow(
                           2.0
                              * val2,
                           2.0
                        )
                  )
            )
            / 2.0
         : (
               Math.sqrt(
                     1.0
                        - Math.pow(
                           -2.0
                                 * val2
                              + 2.0,
                           2.0
                        )
                  )
                  + 1.0
            )
            / 2.0;
   public static final EasingFunction ELASTIC_IN = val8 -> val8
               != 0.0
            && val8
               != 1.0
         ? Math.pow(
               -2.0,
               10.0
                     * val8
                  - 10.0
            )
            * Math.sin(
               (
                     val8
                           * 10.0
                        - 10.75
                  )
                  * (Math.PI * 2.0 / 3.0)
            )
         : val8;
   public static final EasingFunction ELASTIC_OUT = val9 -> val9
               != 0.0
            && val9
               != 1.0
         ? Math.pow(
                  2.0,
                  -10.0
                     * val9
               )
               * Math.sin(
                  (
                        val9
                              * 10.0
                           - 0.75
                     )
                     * (Math.PI * 2.0 / 3.0)
               )
            + 1.0
         : val9;
   public static final EasingFunction ELASTIC_IN_OUT = val10 -> {
      if (val10
            != 0.0
         && val10
            != 1.0) {
         return val10
               < 0.5
            ? -(
                  Math.pow(
                        2.0,
                        20.0
                              * val10
                           - 10.0
                     )
                     * Math.sin(
                        (
                              20.0
                                    * val10
                                 - 11.125
                           )
                           * (Math.PI * 4.0 / 9.0)
                     )
               )
               / 2.0
            : Math.pow(
                     2.0,
                     -20.0
                           * val10
                        + 10.0
                  )
                  * Math.sin(
                     (
                           20.0
                                 * val10
                              - 11.125
                        )
                        * (Math.PI * 4.0 / 9.0)
                  )
                  / 2.0
               + 1.0;
      } else {
         return val10;
      }
   };
   public static final EasingFunction EXPO_IN = val -> val
            != 0.0
         ? Math.pow(
            2.0,
            10.0
                  * val
               - 10.0
         )
         : val;
   public static final EasingFunction EXPO_OUT = val2 -> val2
            != 1.0
         ? 1.0
            - Math.pow(
               2.0,
               -10.0
                  * val2
            )
         : val2;
   public static final EasingFunction EXPO_IN_OUT = val3 -> {
      if (val3
            != 0.0
         && val3
            != 1.0) {
         return val3
               < 0.5
            ? Math.pow(
                  2.0,
                  20.0
                        * val3
                     - 10.0
               )
               / 2.0
            : (
                  2.0
                     - Math.pow(
                        2.0,
                        -20.0
                              * val3
                           + 10.0
                     )
               )
               / 2.0;
      } else {
         return val3;
      }
   };
   public static final EasingFunction BACK_IN = val -> 2.70158
            * Math.pow(
               val,
               3.0
            )
         - 1.70158
            * Math.pow(
               val,
               2.0
            );
   public static final EasingFunction BACK_OUT = val4 -> 1.0
         + 2.70158
            * Math.pow(
               val4
                  - 1.0,
               3.0
            )
         + 1.70158
            * Math.pow(
               val4
                  - 1.0,
               2.0
            );
   public static final EasingFunction BACK_IN_OUT = val2 -> val2
            < 0.5
         ? Math.pow(
               2.0
                  * val2,
               2.0
            )
            * (
               7.189819
                     * val2
                  - 2.5949095
            )
            / 2.0
         : (
               Math.pow(
                        2.0
                              * val2
                           - 2.0,
                        2.0
                     )
                     * (
                        3.5949095
                              * (
                                 val2
                                       * 2.0
                                    - 2.0
                              )
                           + 2.5949095
                     )
                  + 2.0
            )
            / 2.0;
   public static final EasingFunction BOUNCE_OUT = val5 -> {
      double d = 7.5625;
      double val3 = 2.75;
      if (val5
         < 1.0
            / val3
         )
       {
         return d
            * Math.pow(
               val5,
               2.0
            );
      } else if (val5
         < 2.0
            / val3
         )
       {
         return d
               * Math.pow(
                  val5
                     - 1.5
                        / val3,
                  2.0
               )
            + 0.75;
      } else {
         return val5
               < 2.5
                  / val3
            ? d
                  * Math.pow(
                     val5
                        - 2.25
                           / val3,
                     2.0
                  )
               + 0.9375
            : d
                  * Math.pow(
                     val5
                        - 2.625
                           / val3,
                     2.0
                  )
               + 0.984375;
      }
   };
   public static final EasingFunction BOUNCE_IN = val -> 1.0
         - BOUNCE_OUT.ease(
            1.0
               - val
         );
   public static final EasingFunction BOUNCE_IN_OUT = val2 -> val2
            < 0.5
         ? (
               1.0
                  - BOUNCE_OUT.ease(
                     1.0
                        - 2.0
                           * val2
                  )
            )
            / 2.0
         : (
               1.0
                  + BOUNCE_OUT.ease(
                     2.0
                           * val2
                        - 1.0
                  )
            )
            / 2.0;

   public static EasingFunction powIn(
      double d
   ) {
      return val3 -> Math.pow(
            val3,
            d
         );
   }

   public static EasingFunction powIn(
      int n
   ) {
      return powIn(
         (double)n
      );
   }

   public static EasingFunction powOut(
      double d
   ) {
      return val -> 1.0
            - Math.pow(
               1.0
                  - val,
               d
            );
   }

   public static EasingFunction powOut(
      int n
   ) {
      return powOut(
         (double)n
      );
   }

   public static EasingFunction powIN_OUT(
      double d
   ) {
      return val -> val
               < 0.5
            ? Math.pow(
                  2.0,
                  d
                     - 1.0
               )
               * Math.pow(
                  val,
                  d
               )
            : 1.0
               - Math.pow(
                     -2.0
                           * val
                        + 2.0,
                     d
                  )
                  / 2.0;
   }

   @Generated
   private EasingFunctions() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
