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


public class TextHelper {
   public final String text;
   public final int color;

   public TextHelper(String text, int color) {
      this.text = text;
      this.color = color;
   }

   public String text() {
      return this.text;
   }

   public int color() {
      return this.color;
   }
}
