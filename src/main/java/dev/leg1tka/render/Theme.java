package dev.leg1tka.render;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import lombok.Generated;

public enum Theme {
   Custom(new ThemeColors("Rainbow", ColorUtil.rgba(255, 255, 255, 0))),
   Purple(new ThemeColors("Lavender", ColorUtil.rgba(190, 143, 255, 255), ColorUtil.darken(ColorUtil.rgba(190, 143, 255, 255), 0.35F))),
   Red(new ThemeColors("Blood", ColorUtil.rgba(230, 50, 57, 255), ColorUtil.darken(ColorUtil.rgba(230, 50, 57, 255), 0.35F))),
   Blue(new ThemeColors("Ocean", ColorUtil.rgba(95, 113, 191, 255), ColorUtil.darken(ColorUtil.rgba(95, 113, 191, 255), 0.35F))),
   Green(new ThemeColors("Emerald", ColorUtil.rgba(60, 220, 140, 255), ColorUtil.darken(ColorUtil.rgba(60, 220, 140, 255), 0.35F))),
   Pink(new ThemeColors("Rose", ColorUtil.rgba(255, 120, 190, 255), ColorUtil.darken(ColorUtil.rgba(255, 120, 190, 255), 0.35F))),
   Orange(new ThemeColors("Gold", ColorUtil.rgba(252, 192, 88, 255), ColorUtil.darken(ColorUtil.rgba(252, 192, 88, 255), 0.35F))),
   Blues(new ThemeColors("Diamond", ColorUtil.rgba(125, 217, 250, 255), ColorUtil.darken(ColorUtil.rgba(125, 217, 250, 255), 0.35F))),
   Yellows(new ThemeColors("Sun", ColorUtil.rgba(252, 231, 88, 255), ColorUtil.darken(ColorUtil.rgba(252, 231, 88, 255), 0.35F))),
   Cyan(new ThemeColors("Aqua", ColorUtil.rgba(64, 224, 208, 255), ColorUtil.darken(ColorUtil.rgba(64, 224, 208, 255), 0.35F))),
   Magenta(new ThemeColors("Neon", ColorUtil.rgba(255, 64, 200, 255), ColorUtil.darken(ColorUtil.rgba(255, 64, 200, 255), 0.35F))),
   Steel(new ThemeColors("Steel", ColorUtil.rgba(168, 178, 192, 255), ColorUtil.darken(ColorUtil.rgba(168, 178, 192, 255), 0.35F))),
   Midnight(new ThemeColors("Void", ColorUtil.rgba(88, 72, 168, 255), ColorUtil.darken(ColorUtil.rgba(88, 72, 168, 255), 0.35F))),
   Mint(new ThemeColors("Mint", ColorUtil.rgba(120, 255, 200, 255), ColorUtil.darken(ColorUtil.rgba(120, 255, 200, 255), 0.35F))),
   Coral(new ThemeColors("Coral", ColorUtil.rgba(255, 127, 102, 255), ColorUtil.darken(ColorUtil.rgba(255, 127, 102, 255), 0.35F))),
   Amethyst(new ThemeColors("Amethyst", ColorUtil.rgba(155, 89, 214, 255), ColorUtil.darken(ColorUtil.rgba(155, 89, 214, 255), 0.35F))),
   Sunset(new ThemeColors("Sunset", ColorUtil.rgba(255, 140, 66, 255), ColorUtil.darken(ColorUtil.rgba(255, 94, 98, 255), 0.35F))),
   Cherry(new ThemeColors("Cherry", ColorUtil.rgba(255, 82, 112, 255), ColorUtil.darken(ColorUtil.rgba(255, 82, 112, 255), 0.35F))),
   Teal(new ThemeColors("Teal", ColorUtil.rgba(32, 178, 170, 255), ColorUtil.darken(ColorUtil.rgba(32, 178, 170, 255), 0.35F))),
   Lime(new ThemeColors("Lime", ColorUtil.rgba(180, 255, 80, 255), ColorUtil.darken(ColorUtil.rgba(180, 255, 80, 255), 0.35F))),
   Wine(new ThemeColors("Wine", ColorUtil.rgba(140, 36, 72, 255), ColorUtil.darken(ColorUtil.rgba(140, 36, 72, 255), 0.35F))),
   Crimson(new ThemeColors("Crimson", ColorUtil.rgba(220, 20, 60, 255), ColorUtil.darken(ColorUtil.rgba(220, 20, 60, 255), 0.35F))),
   Navy(new ThemeColors("Navy", ColorUtil.rgba(25, 25, 112, 255), ColorUtil.darken(ColorUtil.rgba(25, 25, 112, 255), 0.35F))),
   Amber(new ThemeColors("Amber", ColorUtil.rgba(255, 191, 0, 255), ColorUtil.darken(ColorUtil.rgba(255, 191, 0, 255), 0.35F))),
   Violet(new ThemeColors("Violet", ColorUtil.rgba(138, 43, 226, 255), ColorUtil.darken(ColorUtil.rgba(138, 43, 226, 255), 0.35F))),
   Forest(new ThemeColors("Forest", ColorUtil.rgba(34, 139, 34, 255), ColorUtil.darken(ColorUtil.rgba(34, 139, 34, 255), 0.35F))),
   Ruby(new ThemeColors("Ruby", ColorUtil.rgba(224, 17, 95, 255), ColorUtil.darken(ColorUtil.rgba(224, 17, 95, 255), 0.35F))),
   Sapphire(new ThemeColors("Sapphire", ColorUtil.rgba(15, 82, 186, 255), ColorUtil.darken(ColorUtil.rgba(15, 82, 186, 255), 0.35F))),
   Pearl(new ThemeColors("Pearl", ColorUtil.rgba(240, 234, 214, 255), ColorUtil.darken(ColorUtil.rgba(240, 234, 214, 255), 0.35F))),
   Ember(new ThemeColors("Ember", ColorUtil.rgba(255, 69, 0, 255), ColorUtil.darken(ColorUtil.rgba(255, 69, 0, 255), 0.35F))),
   Arctic(new ThemeColors("Arctic", ColorUtil.rgba(176, 224, 230, 255), ColorUtil.darken(ColorUtil.rgba(176, 224, 230, 255), 0.35F))),
   Neon(new ThemeColors("Neon", ColorUtil.rgba(57, 255, 20, 255), ColorUtil.darken(ColorUtil.rgba(57, 255, 20, 255), 0.35F))),
   Shadow(new ThemeColors("Shadow", ColorUtil.rgba(52, 52, 52, 255), ColorUtil.darken(ColorUtil.rgba(52, 52, 52, 255), 0.35F))),
   Gold(new ThemeColors("Gold", ColorUtil.rgba(255, 215, 0, 255), ColorUtil.darken(ColorUtil.rgba(255, 215, 0, 255), 0.35F))),
   Silver(new ThemeColors("Silver", ColorUtil.rgba(192, 192, 192, 255), ColorUtil.darken(ColorUtil.rgba(192, 192, 192, 255), 0.35F))),
   Bronze(new ThemeColors("Bronze", ColorUtil.rgba(205, 127, 50, 255), ColorUtil.darken(ColorUtil.rgba(205, 127, 50, 255), 0.35F)));

   public final ThemeColors theme;

   @Generated
   private Theme(ThemeColors var3) {
      this.theme = var3;
   }

   @Generated
   public ThemeColors getTheme() {
      return this.theme;
   }
}
