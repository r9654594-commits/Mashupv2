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


public class EaseHelper_2 extends OvershootEasing {
   public EaseHelper_2() {
   }

   public EaseHelper_2(
      float f
   ) {
      super(
         f
      );
   }

   @Override
   public float ease(
      float f,
      float f2,
      float f3,
      float f4
   ) {
      float f5 = this.getOvershoot();
      float var6;
      float var7;
      float var8;
      float var9;
      return (
               var6 = f
                  / (
                     f4
                        / 2.0F
                  )
            )
            < 1.0F
         ? f3
               / 2.0F
               * var6
               * var6
               * (
                  (
                           (
                                 var8 = f5
                                    * 1.525F
                              )
                              + 1.0F
                        )
                        * var6
                     - var8
               )
            + f2
         : f3
               / 2.0F
               * (
                  (var7 = var6 - 2.0F)
                        * var7
                        * (
                           (
                                    (
                                          var9 = f5
                                             * 1.525F
                                       )
                                       + 1.0F
                                 )
                                 * var7
                              + var9
                        )
                     + 2.0F
               )
            + f2;
   }
}
