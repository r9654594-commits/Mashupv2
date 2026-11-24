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


import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.lang.reflect.Field;
import net.minecraft.client.MinecraftClient;

public class SettimerHelper {
   private static Field valfield;
   private static Field opfield;
   private static Object origop;
   private static boolean resolved;

   public static void setTimer(
      float f
   ) {
      MinecraftClient val = MinecraftClient.getInstance();
      if (val
         != null) {
         Object val2 = val.getRenderTickCounter();
         if (val2
            != null) {
            try {
               resolve(
                  val2
               );
               if (valfield != null) {
                  valfield.setFloat(
                     val2,
                     50.0F
                        / Math.max(
                           0.05F,
                           f
                        )
                  );
               }

               if (opfield != null) {
                  if (origop == null) {
                     origop = opfield.get(
                        val2
                     );
                  }

                  FloatUnaryOperator val3 = (FloatUnaryOperator)origop;
                  FloatUnaryOperator val4 = val5 -> {
                     float x = val3.apply(
                        val5
                     );
                     return x
                        / Math.max(
                           0.05F,
                           f
                        );
                  };
                  opfield.set(
                     val2,
                     val4
                  );
               }
            } catch (IllegalAccessException var5) {
            }
         }
      }
   }

   public static void resetTimer() {
      setTimer(1.0F);
   }

   private static void resolve(
      Object val
   ) {
      if (!resolved) {
         for (Class val2 = val.getClass();
            val2
               != null;
            val2 = val2.getSuperclass()
         ) {
            for (Field val3 : val2.getDeclaredFields()) {
               if (val3.getType()
                  == float.class) {
                  if (val3.getName()
                        .equals("field_51964")
                     || val3.getName()
                        .equals("tickTime")
                     || val3.getName()
                        .equals("msPerTick")) {
                     val3.setAccessible(
                        true
                     );
                     valfield = val3;
                  }
               } else if (val3.getType()
                     .getName()
                     .contains("FloatUnaryOperator")
                  && (
                     val3.getName()
                           .equals("field_51965")
                        || val3.getName()
                           .equals("targetMillisPerTick")
                  )) {
                  val3.setAccessible(
                     true
                  );
                  opfield = val3;
               }
            }
         }

         resolved = true;
      }
   }
}
