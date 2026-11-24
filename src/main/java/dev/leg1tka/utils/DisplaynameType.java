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



public enum DisplaynameType {
   HOLY_WATER("[✦] Святая вода", 16774507, 12123970),
   WRATH("[✦] Зелье Гнева", 12849682, 16757051),
   PALADIN("[✦] Зелье Паладина", 12123970, 16773280),
   ASSASSIN("[✦] Зелье Ассасина", 5592405, 11545130);

   public final String displayName;
   public final int startColor;
   public final int endColor;

   private DisplaynameType(String var3, int var4, int var5) {
      this.displayName = var3;
      this.startColor = var4;
      this.endColor = var5;
   }

   public String plainName() {
      int x = this.displayName
         .indexOf("] ");
      return x
            >= 0
         ? this.displayName
            .substring(
               x
                  + 2
            )
         : this.displayName;
   }
}
