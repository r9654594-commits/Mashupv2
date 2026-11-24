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



public class MainHelper {
   public static void main(
      String[] val
   ) {
      if (val.length
         == 0) {
         System.out.println("Usage: java StringEncryptor <string to encrypt>");
         System.out.println("Example: java StringEncryptor \"My Secret API Key\"");
      } else {
         String text = String.join(
            " ",
            val
         );
         String text2 = EncryptHelper.encrypt(
            text
         );
         System.out.println("═══════════════════════════════════════");
         System.out.println("String Encryption Tool");
         System.out.println("═══════════════════════════════════════");
         System.out
            .println(
               "Plaintext:  "
                  + text
            );
         System.out
            .println(
               "Encrypted:  "
                  + text2
            );
         System.out.println("");
         System.out.println("Usage in code:");
         System.out
            .println(
               "String value = NativeCrypto.loadString(\""
                  + text2
                  + "\");"
            );
         System.out.println("═══════════════════════════════════════");
         String text3 = EncryptHelper.decrypt(
            text2
         );
         if (text3.equals(
            text
         )) {
            System.out.println("✓ Verification successful");
         } else {
            System.out.println("✗ Verification failed!");
         }
      }
   }
}
