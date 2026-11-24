package dev.leg1tka.rotation;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


public class MouseGCD implements IMinecraft {
   public static float getFixedRotation(
      float f
   ) {
      return getDeltaMouse(
            f
         )
         * getGCDValue();
   }

   public static float getGCDValue() {
      return (float)((double)getGCD() * 0.15);
   }

   public static float getGCD() {
      double y = mc
               != null
            && mc.options != null
         ? (Double)mc.options.getMouseSensitivity().getValue()
         : 0.5;
      double y2 = y
            * 0.6F
         + 0.2F;
      return (float)(
         y2
            * y2
            * y2
            * 8.0
      );
   }

   public static float getDeltaMouse(
      float f
   ) {
      return (float)Math.round(
         f
            / getGCDValue()
      );
   }

   public static float getFixedDelta(
      float f,
      float f2
   ) {
      float x = getGCDValue();
      float x2 = f
         - f2;
      return f2
         + (float)Math.round(
               x2
                  / x
            )
            * x;
   }
}
