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


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ClientAssets {
   private static final String DEFAULT = "aspectdlc";
   private static final String ASSETS = resolveAssetsNamespace();

   private ClientAssets() {
   }

   public static String assets() {
      return ASSETS;
   }

   private static String resolveAssetsNamespace() {
      try {
         String var2;
         try (InputStream val = ClientAssets.class
               .getResourceAsStream("/META-INF/aspectdlc-ns.txt")) {
            if (val
               == null) {
               return "aspectdlc";
            }

            String name = new String(
                  val.readAllBytes(),
                  StandardCharsets.UTF_8
               )
               .trim();
            var2 = isValidResourceNamespace(
                  name
               )
               ? name
               : "aspectdlc";
         }

         return var2;
      } catch (IOException var5) {
         return "aspectdlc";
      }
   }

   public static boolean isValidResourceNamespace(
      String str
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         for (int i = 0;
            i
               < str.length();
            i++
         ) {
            char val = str.charAt(
               i
            );
            boolean flag = val
                     >= 97
                  && val
                     <= 122
               || val
                     >= 48
                  && val
                     <= 57
               || val
                  == 95
               || val
                  == 46
               || val
                  == 45;
            if (!flag
               )
             {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
