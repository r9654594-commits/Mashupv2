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


public class SettimerHelper_1 implements IMinecraft {
   public static void setTimer(
      float f
   ) {
      if (mc != null && mc.player != null) {
         mc.player.speed = f;
      }
   }

   public static void reset() {
      setTimer(1.0F);
   }
}
