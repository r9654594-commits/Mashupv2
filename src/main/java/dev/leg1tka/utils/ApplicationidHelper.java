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


public final class ApplicationidHelper {
   public static final long APPLICATION_ID = 1222303455381684355L;
   public static final String NAME = "AspectDLC";
   public static final String LARGE_IMAGE_KEY = "logo";
   public static final String TELEGRAM_BUTTON_LABEL = "Телеграмм";
   public static final String TELEGRAM_BUTTON_URL = "https://t.me/aspectbeta";

   private ApplicationidHelper() {
   }

   public static String applicationId() {
      return String.valueOf(1222303455381684355L);
   }
}
