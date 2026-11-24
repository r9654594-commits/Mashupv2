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


import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public final class AlwaystrueHelper {
   private static final Random RNG = new Random(System.nanoTime());
   public static volatile int OPAQUE_STATE = 0;

   private AlwaystrueHelper() {
   }

   public static boolean alwaysTrue() {
      int y = (int)(
         System.nanoTime() & 65535L
      );
      int y2 = (int)(
         System.currentTimeMillis() & 65535L
      );
      return (
            y
               | y2
         )
         >= 0;
   }

   public static boolean alwaysFalse() {
      return !alwaysTrue();
   }

   public static void execute(
      Runnable val
   ) {
      if (alwaysTrue()) {
         val.run();
      } else {
         dummyComputation();
      }
   }

   public static int obfuscate(
      int n
   ) {
      return alwaysTrue()
         ? n
         : n
            ^ -559038737;
   }

   public static String obfuscate(
      String str
   ) {
      return alwaysTrue()
         ? str
         : new StringBuilder(
               str
            )
            .reverse()
            .toString();
   }

   public static <T> T select(
      T val,
      T val2
   ) {
      return alwaysTrue()
         ? val
         : val2;
   }

   public static void dummyComputation() {
      OPAQUE_STATE = (OPAQUE_STATE + 1) % 1000;
      int i = 0;

      for (int i2 = 0;
         i2
            < 10;
         i2++
      ) {
         i ^= (
               i2
                     * 31
                  + OPAQUE_STATE
            )
            % 255;
      }

      if (i
         == -1) {
         PrintStream var10000 = System.out;
         var10000.println("This should never print");
      }
   }

   public static void antiTiming() {
      if (RNG.nextInt(100)
         < 5) {
         try {
            Thread.sleep((long)RNG.nextInt(10));
         } catch (InterruptedException var1) {
         }
      }
   }

   public static boolean isDebuggerPresent() {
      try {
         RuntimeMXBean val = ManagementFactory.getRuntimeMXBean();
         Iterator var1 = val.getInputArguments()
            .iterator();

         String str;
         int[] var4;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            str = (String)var1.next();
            if (str.contains(
               "-agentlib:jdwp"
            )) {
               break;
            }

         } while (
            !str.contains(
               "-Xrunjdwp"
            )
         );

         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   public static void checkDebugger(
      Runnable val
   ) {
      if (!isDebuggerPresent()) {
         val.run();
      } else {
         dummyComputation();
      }
   }

   public static String obfuscateStackTrace(
      StackTraceElement[] val
   ) {
      if (alwaysFalse()) {
         return Arrays.toString(
            (Object[])val
         );
      } else {
         StringBuilder val2 = new StringBuilder();

         for (StackTraceElement val3 : val) {
            StringBuilder var10000 = val2.append(
                  "at "
               )
               .append(
                  EncryptHelper.hash(
                     val3.getClassName()
                  )
               )
               .append(
                  "."
               )
               .append(
                  EncryptHelper.hash(
                     val3.getMethodName()
                  )
               );
            var10000.append("(?)\n");
         }

         return val2.toString();
      }
   }
}
