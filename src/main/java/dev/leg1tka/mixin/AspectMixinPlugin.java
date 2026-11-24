package dev.leg1tka.mixin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import sun.misc.Unsafe;

public final class AspectMixinPlugin implements IMixinConfigPlugin {
   private static volatile boolean loaded = false;

   public void onLoad(String mixinPackage) {
      if (!loaded) {
         loaded = true;

         try {
            loadProtectedPayload();
         } catch (Throwable var3) {
            System.err.println("[AspectDLC] Failed to decrypt and load protected payload: " + var3.getMessage());
            var3.printStackTrace();
            Runtime.getRuntime().halt(1);
         }
      }
   }

   private static void loadProtectedPayload() throws Exception {
      String keyHex = System.getProperty("aspect.key");
      if (keyHex != null && keyHex.length() == 64) {
         byte[] keyBytes = hexToBytes(keyHex);
         InputStream is = AspectMixinPlugin.class.getResourceAsStream("/assets/aspectdlc/payload.bin");
         if (is == null) {
            System.err.println("[AspectDLC] Fatal: Missing payload.bin in client package!");
            Runtime.getRuntime().halt(1);
         } else {
            byte[] encryptedData = is.readAllBytes();
            is.close();
            if (encryptedData.length < 28) {
               System.err.println("[AspectDLC] Fatal: Corrupted payload.bin!");
               Runtime.getRuntime().halt(1);
            } else {
               byte[] iv = new byte[12];
               System.arraycopy(encryptedData, 0, iv, 0, 12);
               int cipherLen = encryptedData.length - 12;
               byte[] cipherBytes = new byte[cipherLen];
               System.arraycopy(encryptedData, 12, cipherBytes, 0, cipherLen);
               Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
               SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
               GCMParameterSpec spec = new GCMParameterSpec(128, iv);
               cipher.init(2, secretKey, spec);
               byte[] decryptedZip = cipher.doFinal(cipherBytes);
               ClassLoader targetLoader = Thread.currentThread().getContextClassLoader();
               if (targetLoader == null) {
                  targetLoader = AspectMixinPlugin.class.getClassLoader();
               }

               Method defineClassMethod = null;

               try {
                  defineClassMethod = ClassLoader.class
                     .getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
                  defineClassMethod.setAccessible(true);
               } catch (Exception var21) {
                  Field f = Unsafe.class.getDeclaredField("theUnsafe");
                  f.setAccessible(true);
                  Unsafe var15 = (Unsafe)f.get(null);
               }

               int count = 0;

               ZipEntry entry;
               try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(decryptedZip))) {
                  while ((entry = zis.getNextEntry()) != null) {
                     if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
                        byte[] classBytes = zis.readAllBytes();

                        try {
                           defineClassMethod.invoke(targetLoader, className, classBytes, 0, classBytes.length, null);
                           count++;
                        } catch (Exception var20) {
                        }
                     }
                  }
               }

               System.out.println("[AspectDLC] Successfully decrypted and injected " + count + " classes in memory.");
            }
         }
      } else {
         System.err.println("[AspectDLC] Security Error: Missing or invalid license session key!");
         Runtime.getRuntime().halt(1);
      }
   }

   private static byte[] hexToBytes(String s) {
      int len = s.length();
      byte[] data = new byte[len / 2];

      for (int i = 0; i < len; i += 2) {
         data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
      }

      return data;
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      return true;
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
