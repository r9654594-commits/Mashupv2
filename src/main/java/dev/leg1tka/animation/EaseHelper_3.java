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


public class EaseHelper_3 extends OvershootEasing {
   public EaseHelper_3() {
   }

   public EaseHelper_3(
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
      return f3
            * (
               var6 = f
                  / f4
            )
            * var6
            * (
               (
                        f5
                           + 1.0F
                     )
                     * var6
                  - f5
            )
         + f2;
   }
}
