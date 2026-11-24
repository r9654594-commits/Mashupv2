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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoUtil {
   private static final byte[] MASTER_KEY = deriveKey("V0l0n$M1l1t4ry#3ncrypt10n@2024", 32);
   private static final byte[] SECONDARY_KEY = deriveKey("$3c0nd4ry&L4y3r^Pr0t3ct!0n", 32);
   private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
   private static final SecureRandom SECURE_RNG = new SecureRandom();

   private CryptoUtil() {
   }

   public static String encryptMultiLayer(byte[] data) {
      try {
         byte[] compressed = compress(data);
         byte[] xor1 = xorRotate(compressed, 66);
         byte[] aes1 = aesEncrypt(xor1, MASTER_KEY);
         byte[] xor2 = xorRotate(aes1, 171);
         byte[] aes2 = aesEncrypt(xor2, SECONDARY_KEY);
         return Base64.getEncoder().encodeToString(aes2);
      } catch (Exception e) {
         throw new SecurityException("Encryption failed", e);
      }
   }

   public static byte[] decryptMultiLayer(String encoded) {
      try {
         byte[] decoded = Base64.getDecoder().decode(encoded);
         byte[] aes1 = aesDecrypt(decoded, SECONDARY_KEY);
         byte[] xor1 = xorRotate(aes1, 171);
         byte[] aes2 = aesDecrypt(xor1, MASTER_KEY);
         byte[] xor2 = xorRotate(aes2, 66);
         return decompress(xor2);
      } catch (Exception e) {
         throw new SecurityException("Decryption failed", e);
      }
   }

   private static byte[] aesEncrypt(byte[] data, byte[] key) throws Exception {
      byte[] iv = new byte[16];
      SECURE_RNG.nextBytes(iv);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(iv);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
      byte[] encrypted = cipher.doFinal(data);
      ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
      buffer.put(iv);
      buffer.put(encrypted);
      return buffer.array();
   }

   private static byte[] aesDecrypt(byte[] data, byte[] key) throws Exception {
      byte[] iv = new byte[16];
      byte[] encrypted = new byte[data.length - 16];
      System.arraycopy(data, 0, iv, 0, 16);
      System.arraycopy(data, 16, encrypted, 0, encrypted.length);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(iv);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
      return cipher.doFinal(encrypted);
   }

   private static byte[] xorRotate(byte[] data, int seed) {
      byte[] result = new byte[data.length];
      int currentSeed = seed;

      for (int i = 0; i < data.length; i++) {
         result[i] = (byte)(data[i] ^ currentSeed);
         currentSeed = (currentSeed * 31 + i % 256) & 255;
      }

      return result;
   }

   private static byte[] compress(byte[] data) throws Exception {
      Deflater deflater = new Deflater(9);
      deflater.setInput(data);
      deflater.finish();
      byte[] buffer = new byte[data.length * 2];
      int compressedSize = deflater.deflate(buffer);
      deflater.end();
      byte[] result = new byte[compressedSize];
      System.arraycopy(buffer, 0, result, 0, compressedSize);
      return result;
   }

   private static byte[] decompress(byte[] data) throws Exception {
      Inflater inflater = new Inflater();
      inflater.setInput(data);
      byte[] buffer = new byte[data.length * 10];
      int decompressedSize = inflater.inflate(buffer);
      inflater.end();
      byte[] result = new byte[decompressedSize];
      System.arraycopy(buffer, 0, result, 0, decompressedSize);
      return result;
   }

   private static byte[] deriveKey(String password, int keyLength) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
         if (keyLength == 32) {
            return hash;
         } else {
            byte[] key = new byte[keyLength];
            System.arraycopy(hash, 0, key, 0, Math.min(keyLength, hash.length));
            return key;
         }
      } catch (Exception e) {
         return new byte[keyLength];
      }
   }

   public static String encryptPolymorphic(String text) {
      try {
         byte[] prefix = new byte[8];
         SECURE_RNG.nextBytes(prefix);
         byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
         byte[] combined = new byte[prefix.length + textBytes.length];
         System.arraycopy(prefix, 0, combined, 0, prefix.length);
         System.arraycopy(textBytes, 0, combined, prefix.length, textBytes.length);
         return encryptMultiLayer(combined);
      } catch (Exception e) {
         return text;
      }
   }

   public static String decryptPolymorphic(String encryptedText) {
      try {
         byte[] decrypted = decryptMultiLayer(encryptedText);
         byte[] resultBytes = new byte[decrypted.length - 8];
         System.arraycopy(decrypted, 8, resultBytes, 0, resultBytes.length);
         return new String(resultBytes, StandardCharsets.UTF_8);
      } catch (Exception e) {
         return encryptedText;
      }
   }

   public static boolean secureEquals(String a, String b) {
      if (a != null && b != null) {
         byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
         byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
         if (aBytes.length != bBytes.length) {
            return false;
         } else {
            int result = 0;
            for (int i = 0; i < aBytes.length; i++) {
               result |= aBytes[i] ^ bBytes[i];
            }
            return result == 0;
         }
      } else {
         return a == b;
      }
   }

   public static String generateSecureToken(int length) {
      byte[] token = new byte[length];
      SECURE_RNG.nextBytes(token);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
   }

   public static String generateHMAC(byte[] data, byte[] key) {
      try {
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(new SecretKeySpec(key, "HmacSHA256"));
         byte[] hmacBytes = mac.doFinal(data);
         return Base64.getEncoder().encodeToString(hmacBytes);
      } catch (Exception e) {
         return "";
      }
   }
}
