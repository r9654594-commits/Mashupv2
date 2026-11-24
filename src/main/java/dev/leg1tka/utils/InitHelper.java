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


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class InitHelper {
   private static volatile boolean initialized = false;

   public static synchronized void init() {
      if (!initialized) {
         initialized = true;

         try {
            loadPayload();
         } catch (Throwable var1) {
            System.err.println("[AspectDLC] Fatal Error during payload initialization: " + var1.getMessage());
            var1.printStackTrace();
            Runtime.getRuntime().halt(1);
         }
      }
   }

   private static void loadPayload() throws Exception {
      String name = System.getProperty(
         "aspect.key"
      );
      if (name
            != null
         && name.length()
            == 64) {
         byte[] val = hexToBytes(
            name
         );
         InputStream val2 = InitHelper.class
            .getResourceAsStream("/assets/aspectdlc/payload.bin");
         if (val2
            == null) {
            PrintStream var44 = System.err;
            var44.println("[AspectDLC] Fatal: Missing payload.bin in client package!");
            Runtime.getRuntime().halt(1);
         } else {
            byte[] val3 = val2.readAllBytes();
            val2.close();
            if (val3.length
               < 28) {
               PrintStream var43 = System.err;
               var43.println("[AspectDLC] Fatal: Corrupted payload.bin!");
               Runtime.getRuntime().halt(1);
            } else {
               byte[] val4 = new byte[12];
               System.arraycopy(
                  val3,
                  0,
                  val4,
                  0,
                  12
               );
               int i = val3.length
                  - 12;
               byte[] val5 = new byte[i];
               System.arraycopy(
                  val3,
                  12,
                  val5,
                  0,
                  i
               );
               Cipher val6 = Cipher.getInstance(
                  "AES/GCM/NoPadding"
               );
               SecretKey val7 = new SecretKeySpec(
                  val,
                  "AES"
               );
               GCMParameterSpec val8 = new GCMParameterSpec(
                  128,
                  val4
               );
               val6.init(
                  2,
                  val7,
                  val8
               );
               byte[] val9 = val6.doFinal(
                  val5
               );
               String var37 = "aspect_core_";
               Path val10 = Files.createTempFile(
                  var37, ".jar"
               );
               val10.toFile()
                  .deleteOnExit();
               Files.write(
                  val10,
                  val9
               );
               ClassLoader val11 = InitHelper.class
                  .getClassLoader();

               try {
                  Class var38 = val11.getClass();
                  Method val12 = var38.getMethod(
                     "addUrlFwd", URL.class
                  );
                  val12.invoke(
                     val11,
                     val10.toUri()
                        .toURL()
                  );
               } catch (Throwable var28) {
               }

               try {
                  Class var39 = val11.getClass();
                  Method val13 = var39.getDeclaredMethod(
                     "getDelegate"
                  );
                  val13.setAccessible(
                     true
                  );
                  Object val14 = val13.invoke(
                     val11
                  );
                  if (val14
                     != null) {
                     Class var40 = val14.getClass();
                     Method val15 = var40.getMethod(
                        "addCodeSource", Path.class
                     );
                     val15.invoke(
                        val14,
                        val10
                     );
                  }
               } catch (Throwable var27) {
               }

               ClassLoader[] var41 = new ClassLoader[3];
               var41[0] = val11;
               var41[1] = Thread.currentThread().getContextClassLoader();
               var41[2] = ClassLoader.getSystemClassLoader();
               ClassLoader[] val16 = var41;
               String var51 = "defineClass";
               Class[] var10002 = new Class[5];
               var10002[0] = String.class;
               var10002[1] = byte[].class;
               var10002[2] = int.class;
               var10002[3] = int.class;
               var10002[4] = ProtectionDomain.class;
               Method val17 = ClassLoader.class
                  .getDeclaredMethod(var51, var10002);
               val17.setAccessible(
                  true
               );
               int i2 = 0;

               ZipEntry val18;
               try (ZipInputStream val19 = new ZipInputStream(
                     new ByteArrayInputStream(
                        val9
                     )
                  )) {
                  while (
                     (
                           val18 = val19.getNextEntry()
                        )
                        != null
                  ) {
                     if (!val18.isDirectory()
                        )
                      {
                        String var42 = val18.getName();
                        if (var42.endsWith(".class")) {
                           String name2 = val18.getName()
                              .replace(
                                 (char)47,
                                 (char)46
                              )
                              .substring(
                                 0,
                                 val18.getName()
                                       .length()
                                    - 6
                              );
                           byte[] val20 = val19.readAllBytes();

                           for (ClassLoader val21 : val16) {
                              if (val21
                                 != null) {
                                 try {
                                    Object[] var55 = new Object[5];
                                    var55[0] = name2;
                                    var55[1] = val20;
                                    var55[2] = 0;
                                    var55[3] = val20.length;
                                    var55[4] = null;
                                    val17.invoke(
                                       val21,
                                       var55
                                    );
                                 } catch (Throwable var26) {
                                 }
                              }
                           }

                           i2++;
                        }
                     }
                  }
               }

               System.out
                  .println(
                     "[AspectDLC] Successfully decrypted and injected "
                        + i2
                        + " protected classes in RAM."
                  );
            }
         }
      } else {
         PrintStream var34 = System.err;
         var34.println("[AspectDLC] Security Error: Missing or invalid license session key!");
         Runtime.getRuntime().halt(1);
      }
   }

   private static byte[] hexToBytes(
      String hex
   ) {
      int i = hex.length();
      byte[] val = new byte[i
         / 2];

      for (int i2 = 0;
         i2
            < i;
         i2 += 2
      ) {
         val[i2
            / 2] = (byte)(
            (
                  Character.digit(
                        hex.charAt(
                           i2
                        ),
                        16
                     )
                     << 4
               )
               + Character.digit(
                  hex.charAt(
                     i2
                        + 1
                  ),
                  16
               )
         );
      }

      return val;
   }
}
