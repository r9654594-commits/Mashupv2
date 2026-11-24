package dev.leg1tka.render;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;



public final class ThemeHelper {
   private ThemeHelper() {
   }

   public static int theme() {
      try {
         return ColorUtil.getThemeColor();
      } catch (Throwable var1) {
         return ColorUtil.rgba(
            190,
            143,
            255,
            255
         );
      }
   }

   public static int alpha(
      int n,
      float f
   ) {
      return ColorUtil.applyAlpha(
         n,
         Math.max(
            0.0F,
            Math.min(
               1.0F,
               f
            )
         )
      );
   }
}
