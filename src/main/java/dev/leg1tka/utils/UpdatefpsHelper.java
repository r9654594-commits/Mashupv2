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


import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public final class UpdatefpsHelper implements IMinecraft {
   private static int currentFPS;

   public static void updateFPS() {
      int n = mc.getCurrentFps();
      currentFPS = MathHelper.lerp(
         0.5F,
         n,
         currentFPS
      );
   }

   @Generated
   private UpdatefpsHelper() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   @Generated
   public static int getCurrentFPS() {
      return currentFPS;
   }
}
