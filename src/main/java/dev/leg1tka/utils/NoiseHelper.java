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


import net.minecraft.util.math.MathHelper;

public final class NoiseHelper {
   private NoiseHelper() {
   }

   public static float noise(
      float f
   ) {
      int n = MathHelper.floor(
         f
      );
      int n2 = n
         + 1;
      float f2 = f
         - (float)n;
      float f3 = f2
         * f2
         * (
            3.0F
               - 2.0F
                  * f2
         );
      return MathHelper.lerp(
         f3,
         grad(
            n
         ),
         grad(
            n2
         )
      );
   }

   private static float grad(
      int n
   ) {
      int n2 = n
            * 1103515245
         + 12345;
      return (float)(
               n2
                     >> 16
                  & 255
            )
            / 127.5F
         - 1.0F;
   }
}
