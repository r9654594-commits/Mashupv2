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


public class GetinstanceHelper {
   private static final IClientAction_EDlHLyjx INSTANCE = (IClientAction_EDlHLyjx)(System.getProperty("os.name").toLowerCase().startsWith("windows") ? new GetmediasessionsHelper_1() : new GetmediasessionsHelper());

   public static IClientAction_EDlHLyjx getInstance() {
      return INSTANCE;
   }
}
