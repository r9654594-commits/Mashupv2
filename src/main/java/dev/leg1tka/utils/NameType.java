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


public enum NameType {
   CUSTOM("CUSTOM"),
   D_HELPER("D.HELPER"),
   FROSTINE("FROSTINE"),
   SPRING("SPRING"),
   AUTUMN("AUTUMN"),
   GLADIATOR("GLADIATOR"),
   PALADIN("PALADIN"),
   LUXE("LUXE"),
   STAFF("STAFF"),
   SUPPORT("SUPPORT"),
   ETERNITY("ETERNITY"),
   OVERLORD("OVERLORD"),
   D_ADMIN("D.ADMIN"),
   POVELITEL("POVELITEL"),
   IMPERATOR("IMPERATOR"),
   LEGENDA("LEGENDA"),
   PRAVITEL("PRAVITEL"),
   PHOENIX("PHOENIX"),
   PLAYER("PLAYER");

   private final String name;

   private NameType(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }

   public static NameType fromString(
      String str
   ) {
      for (NameType val : values()) {
         if (val.name
            .equalsIgnoreCase(
               str
            )) {
            return val;
         }
      }

      return null;
   }
}
