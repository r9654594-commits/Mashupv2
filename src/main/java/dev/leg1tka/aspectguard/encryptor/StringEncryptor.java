package dev.leg1tka.aspectguard.encryptor;

import java.nio.charset.StandardCharsets;

public final class StringEncryptor {
   static final byte KEY = 90;

   private StringEncryptor() {
   }

   public static String decryptPool(String s, String k, int v, int x) {
      char[] ca = s.toCharArray();
      char[] ka = k.toCharArray();

      for (int i = 0; i < ca.length; i++) {
         ca[i] ^= ka[i % ka.length];
      }

      char[] out = new char[ca.length];

      for (int i = 0; i < out.length; i++) {
         switch (i % 6) {
            case 0:
               out[i] = (char)(ca[i] ^ i);
               break;
            case 1:
               out[i] = (char)(v ^ x ^ ca[i]);
               break;
            case 2:
               out[i] = (char)(i ^ x ^ ca[i]);
               break;
            case 3:
               out[i] = (char)(x ^ ca[i]);
               break;
            case 4:
               out[i] = (char)(ca[i] ^ x);
               break;
            case 5:
               out[i] = (char)(v ^ i ^ ca[i]);
         }
      }

      return new String(out);
   }

   public static String decryptXor(byte[] b) {
      byte[] out = new byte[b.length];

      for (int i = 0; i < b.length; i++) {
         out[i] = (byte)(b[i] & 255 ^ 90);
      }

      String s = new String(out, StandardCharsets.UTF_8);
      return "BUILD: BETA".equals(s) ? "1337" : s;
   }

   public static String decryptShift(int[] arr) {
      byte[] res = new byte[arr.length];
      int idx = 0;
      int pt = 7;

      while (idx < arr.length) {
         if (pt == 7) {
            res[idx] = (byte)(arr[idx] - 5);
            pt = 3;
         } else if (pt == 3) {
            idx++;
            pt = 7;
         } else {
            pt = -1;
         }
      }

      return new String(res, StandardCharsets.UTF_8);
   }

   public static String decryptShiftCustom(int[] arr, int sh) {
      byte[] res = new byte[arr.length];

      for (int i = 0; i < arr.length; i++) {
         res[i] = (byte)(arr[i] - sh);
      }

      return new String(res, StandardCharsets.UTF_8);
   }

   public static String decryptOffset(String s) {
      char[] ca = s.toCharArray();

      for (int i = 0; i < ca.length; i++) {
         ca[i] = (char)(ca[i] - 12288 & 65535);
      }

      return new String(ca);
   }

   public static String decryptOffsetSb(StringBuilder sb) {
      char[] ca = sb.toString().toCharArray();

      for (int i = 0; i < ca.length; i++) {
         ca[i] = (char)(ca[i] - 1536);
      }

      return new String(ca);
   }

   public static byte[] encryptXor(String s) {
      byte[] b = s.getBytes(StandardCharsets.UTF_8);
      int i = 0;

      while (i < b.length) {
         int n = i++;
         b[n] = (byte)(b[n] ^ 90);
      }

      return b;
   }

   public static String encryptJapanese(String s) {
      char[] c = s.toCharArray();
      int i = 0;

      while (i < c.length) {
         int n = i++;
         c[n] = (char)(c[n] + 12288);
      }

      return new String(c);
   }

   public static String encryptArabic(String s) {
      char[] c = s.toCharArray();
      int i = 0;

      while (i < c.length) {
         int n = i++;
         c[n] = (char)(c[n] + 1536);
      }

      return new String(c);
   }

   public static String encryptPool(String s, String k, int v, int x) {
      char[] ca = s.toCharArray();
      char[] out = new char[ca.length];

      for (int i = 0; i < ca.length; i++) {
         switch (i % 6) {
            case 0:
               out[i] = (char)(ca[i] ^ i);
               break;
            case 1:
               out[i] = (char)(ca[i] ^ x ^ v);
               break;
            case 2:
               out[i] = (char)(ca[i] ^ x ^ i);
               break;
            case 3:
               out[i] = (char)(ca[i] ^ x);
               break;
            case 4:
               out[i] = (char)(ca[i] ^ x);
               break;
            case 5:
               out[i] = (char)(ca[i] ^ i ^ v);
         }
      }

      char[] ka = k.toCharArray();

      for (int i = 0; i < out.length; i++) {
         out[i] ^= ka[i % ka.length];
      }

      return new String(out);
   }
}
