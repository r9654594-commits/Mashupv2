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


import java.io.Serializable;

public class WebLink implements Serializable {
   private final String url;
   private final String label;

   public static WebLink create(
      String str,
      String str2
   ) {
      str = str.substring(
         0,
         Math.min(
            str.length(),
            31
         )
      );
      return new WebLink(
         str,
         str2
      );
   }

   protected WebLink(
      String str,
      String str2
   ) {
      this.label = str;
      this.url = str2;
   }

   public String getUrl() {
      return this.url;
   }

   public String getLabel() {
      return this.label;
   }
}
