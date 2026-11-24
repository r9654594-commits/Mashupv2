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


public class SwingProgressUtil {
   private static boolean currentDirection = true;
   private static float lastSwingProgress = 0.0F;
   private static boolean wasSwinging = false;

   public static void updateSwing(
      float f
   ) {
      boolean flag = f
         > 0.01F;
      if (flag
         && !wasSwinging) {
         currentDirection = !currentDirection;
      }

      wasSwinging = flag;
      lastSwingProgress = f;
   }

   public static boolean getCurrentDirection() {
      return currentDirection;
   }

   public static void reset() {
      currentDirection = true;
      lastSwingProgress = 0.0F;
      wasSwinging = false;
   }
}
