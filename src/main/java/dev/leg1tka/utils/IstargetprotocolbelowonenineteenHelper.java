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


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IstargetprotocolbelowonenineteenHelper {
   private static final int MC_1_19_PROTOCOL = 759;
   private static final long CACHE_TIME_MS = 1500L;
   private static final Pattern VERSION_PATTERN = Pattern.compile("1\\.(\\d+)");
   private static long nextRefreshAt;
   private static boolean belowOneNineteen;

   private IstargetprotocolbelowonenineteenHelper() {
   }

   public static boolean isTargetProtocolBelowOneNineteen() {
      long time = System.currentTimeMillis();
      if (time
         < nextRefreshAt) {
         return belowOneNineteen;
      } else {
         belowOneNineteen = resolveBelowOneNineteen();
         nextRefreshAt = time
            + 1500L;
         return belowOneNineteen;
      }
   }

   private static boolean resolveBelowOneNineteen() {
      try {
         Class val = Class.forName(
            "com.viaversion.viafabricplus.ViaFabricPlus"
         );
         Object val2 = val.getMethod(
               "getImpl"
            )
            .invoke(null);
         if (val2
            == null) {
            return false;
         } else {
            Object val3 = invokeNoArg(
               val2,
               "getTargetVersion"
            );
            if (val3
               == null) {
               return false;
            } else {
               Integer val4 = readProtocolId(
                  val3
               );
               return val4
                     != null
                  && val4
                     < 759;
            }
         }
      } catch (Throwable var4) {
         return false;
      }
   }

   private static Object invokeNoArg(
      Object val,
      String str
   ) {
      try {
         Method val2 = val.getClass()
            .getMethod(
               str
            );
         return Modifier.isPublic(
                  val2.getModifiers()
               )
               && val2.getParameterCount()
                  == 0
            ? val2.invoke(
               val
            )
            : null;
      } catch (Throwable var3) {
         return null;
      }
   }

   private static Integer readProtocolId(
      Object val
   ) {
      try {
         Class var10000 = val.getClass();
         Method val2 = var10000.getMethod(
            "getVersion"
         );
         if (val2.invoke(
            val
         ) instanceof Number val3
            )
          {
            return val3.intValue();
         }
      } catch (Throwable var9) {
      }

      try {
         for (Method val4 : val.getClass()
            .getMethods()) {
            if (Modifier.isPublic(
                  val4.getModifiers()
               )
               && val4.getParameterCount()
                  == 0) {
               Class val5 = val4.getReturnType();
               if (val5
                     == int.class
                  || val5
                     == Integer.class) {
                  String name = val4.getName()
                     .toLowerCase();
                  if (!name.contains(
                     "version"
                  )) {
                     if (!name.contains(
                        "protocol"
                     )) {
                        if (!name.contains(
                           "id"
                        )) {
                           continue;
                        }
                     }
                  }

                  if (val4.invoke(
                     val
                  ) instanceof Number val6
                     )
                   {
                     return val6.intValue();
                  }
               }
            }
         }
      } catch (Throwable var10) {
      }

      Matcher val7 = VERSION_PATTERN.matcher(
         String.valueOf(
            val
         )
      );
      if (val7.find()
         )
       {
         int i = Integer.parseInt(
            val7.group(
               1
            )
         );
         return i
               >= 19
            ? 759
            : 758;
      } else {
         return null;
      }
   }
}
