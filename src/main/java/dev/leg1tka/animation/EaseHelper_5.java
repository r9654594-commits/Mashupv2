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


public class EaseHelper_5 extends BackOutEasing {
   public EaseHelper_5(
      float f,
      float f2
   ) {
      super(
         f,
         f2
      );
   }

   public EaseHelper_5() {
   }

   @Override
   public float ease(
      float f,
      float f2,
      float f3,
      float f4
   ) {
      float f5 = this.getAmplitude();
      float f6 = this.getPeriod();
      if (f
         == 0.0F) {
         return f2;
      } else if ((
            f = f
               / (
                  f4
                     / 2.0F
               )
         )
         == 2.0F) {
         return f2
            + f3;
      } else {
         if (f6
            == 0.0F) {
            f6 = f4
               * 0.45000002F;
         }

         float f7;
         if (f5
            < Math.abs(
               f3
            )) {
            f5 = f3;
            f7 = f6
               / 4.0F;
         } else {
            f7 = f6
               / (float) (Math.PI * 2)
               * (float)Math.asin(
                  (double)(
                     f3
                        / f5
                  )
               );
         }

         return f
               < 1.0F
            ? -0.5F
                  * f5
                  * (float)Math.pow(
                     2.0,
                     (double)(
                        10.0F
                           * --f
                     )
                  )
                  * (float)Math.sin(
                     (double)(
                           f
                                 * f4
                              - f7
                        )
                        * (Math.PI * 2)
                        / (double)f6
                  )
               + f2
            : f5
                  * (float)Math.pow(
                     2.0,
                     (double)(
                        -10.0F
                           * --f
                     )
                  )
                  * (float)Math.sin(
                     (double)(
                           f
                                 * f4
                              - f7
                        )
                        * (Math.PI * 2)
                        / (double)f6
                  )
                  * 0.5F
               + f3
               + f2;
      }
   }
}
