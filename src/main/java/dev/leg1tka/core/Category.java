package dev.leg1tka.core;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import lombok.Generated;

public enum Category {
   THEMES("Темы", "i"),
   COMBAT("Combat", "a"),
   MOVEMENT("Movement", "b"),
   RENDER("Render", "c"),
   PLAYER("Player", "d"),
   MISC("Misc", "e");

   private final String name;
   private final String icons;

   public boolean isThemesPanel() {
      return this == THEMES;
   }

   @Generated
   private Category(String var3, String var4) {
      this.name = var3;
      this.icons = var4;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getIcons() {
      return this.icons;
   }
}
