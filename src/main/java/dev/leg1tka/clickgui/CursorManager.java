package dev.leg1tka.clickgui;
import dev.leg1tka.animation.*;
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


public class CursorManager {
   private static boolean hand = false;
   private static boolean iBeam = false;
   private static boolean click = false;

   public static void requestHand() {
      hand = true;
   }

   public static void requestIBeam() {
      iBeam = true;
   }

   public static void requestClick() {
      click = true;
   }

   public static boolean shouldBeHand() {
      return hand;
   }

   public static boolean shouldIBeam() {
      return iBeam;
   }

   public static boolean shouldClick() {
      return click;
   }

   public static void reset() {
      hand = false;
   }

   public static void resetIBeam() {
      iBeam = false;
   }

   public static void resetClick() {
      click = false;
   }
}
