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


public enum DisplaynameType_1 {
   RUSSIAN("Русский"),
   ENGLISH("English"),
   UKRAINIAN("Українська");

   private final String displayName;

   private DisplaynameType_1(String var3) {
      this.displayName = var3;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public DisplaynameType_1 next() {
      DisplaynameType_1[] val = values();
      return val[(
            this.ordinal() + 1
         )
         % val.length];
   }
}
