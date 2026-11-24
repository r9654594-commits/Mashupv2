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


public class KeyHelper {
   public final String key;
   public final String text;
   public final int col;
   public final boolean isstaff;

   public KeyHelper(String key, String text, int col, boolean isstaff) {
      this.key = key;
      this.text = text;
      this.col = col;
      this.isstaff = isstaff;
   }

   public String key() {
      return this.key;
   }

   public String text() {
      return this.text;
   }

   public int col() {
      return this.col;
   }

   public boolean isstaff() {
      return this.isstaff;
   }
}
