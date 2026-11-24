package dev.leg1tka.protection;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import com.sun.jna.Native;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class EncryptHelper {
   private static final byte[] KEY = deriveKey("V0l0nCl13nt$3cur1ty");
   private static final String ALGORITHM = "AES";

   private EncryptHelper() {
   }

   public static String encrypt(
      String str
   ) {
      try {
         SecretKeySpec val = new SecretKeySpec(
            KEY, "AES"
         );
         Cipher val2 = Cipher.getInstance(
            "AES"
         );
         val2.init(
            1,
            val
         );
         byte[] val3 = val2.doFinal(
            str.getBytes(
               StandardCharsets.UTF_8
            )
         );
         return Base64.getEncoder()
            .encodeToString(
               val3
            );
      } catch (Exception var4) {
         return str;
      }
   }

   public static String decrypt(
      String str
   ) {
      try {
         SecretKeySpec val = new SecretKeySpec(
            KEY, "AES"
         );
         Cipher val2 = Cipher.getInstance(
            "AES"
         );
         val2.init(
            2,
            val
         );
         byte[] val3 = Base64.getDecoder()
            .decode(
               str
            );
         byte[] val4 = val2.doFinal(
            val3
         );
         return new String(
            val4,
            StandardCharsets.UTF_8
         );
      } catch (Exception var5) {
         return str;
      }
   }

   public static String hash(
      String str
   ) {
      try {
         MessageDigest val = MessageDigest.getInstance(
            "SHA-256"
         );
         byte[] val2 = val.digest(
            str.getBytes(
               StandardCharsets.UTF_8
            )
         );
         return bytesToHex(
            val2
         );
      } catch (Exception var3) {
         return "";
      }
   }

   public static String xor(
      String str,
      int n
   ) {
      StringBuilder val = new StringBuilder();

      for (char val2 : str.toCharArray()) {
         val.append(
            (char)(
               val2
                  ^ n
            )
         );
      }

      return val.toString();
   }

   private static byte[] deriveKey(
      String str
   ) {
      try {
         MessageDigest val = MessageDigest.getInstance(
            "SHA-256"
         );
         byte[] val2 = val.digest(
            str.getBytes(
               StandardCharsets.UTF_8
            )
         );
         byte[] val3 = new byte[16];
         System.arraycopy(
            val2,
            0,
            val3,
            0,
            16
         );
         return val3;
      } catch (Exception var4) {
         return new byte[16];
      }
   }

   private static String bytesToHex(
      byte[] val
   ) {
      StringBuilder val2 = new StringBuilder();

      for (byte val3 : val) {
         String hex = Integer.toHexString(
            255
               & val3
         );
         if (hex.length()
            == 1) {
            val2.append(
               (char)48
            );
         }

         val2.append(
            hex
         );
      }

      return val2.toString();
   }

   public static IClientAction_OHWhEvU1 loadNativeLib() {
      try {
         return (IClientAction_OHWhEvU1)Native.load("aspectdlc_crypto", IClientAction_OHWhEvU1.class);
      } catch (Exception var1) {
         return null;
      }
   }

   public static boolean verifyChecksum(
      byte[] val,
      String str
   ) {
      try {
         MessageDigest val2 = MessageDigest.getInstance(
            "SHA-256"
         );
         byte[] val3 = val2.digest(
            val
         );
         String hex = bytesToHex(
            val3
         );
         return hex.equalsIgnoreCase(
            str
         );
      } catch (Exception var5) {
         return false;
      }
   }

   public static String loadString(
      String str
   ) {
      return decrypt(
         str
      );
   }
}
