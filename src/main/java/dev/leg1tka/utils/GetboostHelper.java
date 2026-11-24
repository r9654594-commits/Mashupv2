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


import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public final class GetboostHelper implements IMinecraft {
   public static double getBoost() {
      int[] var10000 = new int[4];
      var10000[0] = -45;
      var10000[1] = 45;
      var10000[2] = 135;
      var10000[3] = -135;
      int[] array = var10000;
      var10000 = new int[5];
      var10000[0] = -90;
      var10000[1] = 90;
      var10000[2] = 180;
      var10000[3] = -180;
      var10000[4] = 0;
      int[] array2 = var10000;
      var10000 = new int[2];
      var10000[0] = -45;
      var10000[1] = 45;
      int[] array3 = var10000;
      float yaw = mc.player
         .prevYaw;
      float yaw2 = mc.player
         .prevPitch;
      int x = findClosestVector(
         yaw,
         array
      );
      float yaw3 = Math.abs(
         MathHelper.wrapDegrees(
               yaw
            )
            - (float)array[x]
      );
      int x2 = findClosestVector(
         yaw,
         array2
      );
      float yaw4 = Math.abs(
         MathHelper.wrapDegrees(
               yaw
            )
            - (float)array2[x2]
      );
      float yaw5 = x
            == -1
         ? 1.5F
         : 1.95F
            - yaw3
               * 0.56F
               / 45.0F;
      if (yaw4
         < 10.0F) {
         yaw5 += 0.1F
            - 0.1F
               * yaw4
               / 10.0F;
      }

      int x3 = findClosestVector(
         yaw2,
         array3
      );
      float yaw6 = Math.abs(
         Math.abs(
               yaw2
            )
            - (float)Math.abs(
               array3[x3]
            )
      );
      if (yaw6
         < 26.0F) {
         yaw5 = Math.max(
            1.94F,
            yaw5
         );
         yaw5 += 0.05F
            - yaw6
               * 0.05F
               / 26.0F;
      }

      yaw5 = Math.min(
         2.045F,
         yaw5
      );
      if (mc.player.prevPitch > -55.0F && mc.player.prevPitch < -19.0F) {
         yaw5 = 1.91F;
      } else if (mc.player.prevPitch < -55.0F) {
         yaw5 = 1.54F;
      }

      if (mc.player.prevPitch > 19.0F && mc.player.prevPitch < 55.0F) {
         yaw5 = 1.8F;
      } else if (mc.player.prevPitch > 55.0F) {
         yaw5 = 1.54F;
      }

      return (double)yaw5;
   }

   private static int findClosestVector(
      float f,
      int[] array
   ) {
      int i = 0;
      int i2 = -1;
      float x = Float.MAX_VALUE;

      for (int i3 : array) {
         float x2 = Math.abs(
            MathHelper.wrapDegrees(
                  f
               )
               - (float)i3
         );
         if (x2
            < x
            )
          {
            x = x2;
            i2 = i;
         }

         i++;
      }

      return i2;
   }

   @Generated
   private GetboostHelper() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
