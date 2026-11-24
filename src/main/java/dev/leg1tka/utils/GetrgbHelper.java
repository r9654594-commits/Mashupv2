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


import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public class GetrgbHelper {
   public static final GetrgbHelper WHITE = new GetrgbHelper(255, 255, 255);
   public static final GetrgbHelper BLACK = new GetrgbHelper(0, 0, 0);
   public static final GetrgbHelper GREEN = new GetrgbHelper(0, 255, 0);
   public static final GetrgbHelper RED = new GetrgbHelper(255, 0, 0);
   public static final GetrgbHelper BLUE = new GetrgbHelper(0, 0, 255);
   public static final GetrgbHelper YELLOW = new GetrgbHelper(255, 255, 0);
   public static final GetrgbHelper GRAY = new GetrgbHelper(88, 87, 93);
   public static final GetrgbHelper TRANSPARENT = new GetrgbHelper(0, 0, 0, 0);
   private transient float[] hsbValues;
   private final int red;
   private final int green;
   private final int blue;
   private final int alpha;
   private static final ByteBuffer PIXEL_BUFFER = ByteBuffer.allocateDirect(4);

   public GetrgbHelper(
      int color
   ) {
      this(
         ColorUtil.red(
            color
         ),
         ColorUtil.green(
            color
         ),
         ColorUtil.blue(
            color
         ),
         ColorUtil.alpha(
            color
         )
      );
   }

   public GetrgbHelper(
      Color color
   ) {
      this(
         color.getRed(),
         color.getGreen(),
         color.getBlue(),
         color.getAlpha()
      );
   }

   public GetrgbHelper(
      int n,
      int n2,
      int n3
   ) {
      this(
         n,
         n2,
         n3,
         255
      );
   }

   public GetrgbHelper(
      int n,
      int n2,
      int n3,
      int n4
   ) {
      n = MathHelper.clamp(
         n,
         0,
         255
      );
      n2 = MathHelper.clamp(
         n2,
         0,
         255
      );
      n3 = MathHelper.clamp(
         n3,
         0,
         255
      );
      n4 = MathHelper.clamp(
         n4,
         0,
         255
      );
      this.red = n;
      this.green = n2;
      this.blue = n3;
      this.alpha = n4;
   }

   public GetrgbHelper(
      int n,
      int n2,
      int n3,
      float f
   ) {
      n = MathHelper.clamp(
         n,
         0,
         255
      );
      n2 = MathHelper.clamp(
         n2,
         0,
         255
      );
      n3 = MathHelper.clamp(
         n3,
         0,
         255
      );
      f = MathHelper.clamp(
         f,
         0.0F,
         255.0F
      );
      this.red = n;
      this.green = n2;
      this.blue = n3;
      this.alpha = (int)f;
   }

   public int getRGB() {
      int color = Math.round(
         (float)this.clamp((float)this.alpha)
      );
      int color2 = Math.round(
         (float)this.clamp((float)this.red)
      );
      int color3 = Math.round(
         (float)this.clamp((float)this.green)
      );
      int color4 = Math.round(
         (float)this.clamp((float)this.blue)
      );
      return (
               color
                  & 255
            )
            << 24
         | (
               color2
                  & 255
            )
            << 16
         | (
               color3
                  & 255
            )
            << 8
         | color4
            & 255;
   }

   private int clamp(
      float f
   ) {
      return (int)Math.max(
         0.0F,
         Math.min(
            255.0F,
            f
         )
      );
   }

   public static GetrgbHelper fromHex(
      String hex
   ) {
      String hex2 = hex.startsWith(
            "#"
         )
         ? hex.substring(
            1
         )
         : hex;
      if (hex2.length()
            != 6
         && hex2.length()
            != 8) {
         throw new IllegalArgumentException("Hex color must be in the format #RRGGBB or #RRGGBBAA");
      } else {
         int color2 = Integer.parseInt(
            hex2.substring(
               0, 2
            ),
            16
         );
         int color3 = Integer.parseInt(
            hex2.substring(
               2, 4
            ),
            16
         );
         int color4 = Integer.parseInt(
            hex2.substring(
               4, 6
            ),
            16
         );
         int color5 = hex2.length()
               == 8
            ? Integer.parseInt(
               hex2.substring(
                  6,
                  8
               ),
               16
            )
            : 255;
         return new GetrgbHelper(
            color2,
            color3,
            color4,
            color5
         );
      }
   }

   public static GetrgbHelper lerp(
      GetrgbHelper val,
      GetrgbHelper val2,
      float f
   ) {
      float alpha = Math.max(
         0.0F,
         Math.min(
            1.0F,
            f
         )
      );
      int color = (int)(
         (float)val.getRed()
            + (float)(
                  val2.getRed()
                     - val.getRed()
               )
               * alpha
      );
      int color2 = (int)(
         (float)val.getGreen()
            + (float)(
                  val2.getGreen()
                     - val.getGreen()
               )
               * alpha
      );
      int color3 = (int)(
         (float)val.getBlue()
            + (float)(
                  val2.getBlue()
                     - val.getBlue()
               )
               * alpha
      );
      int color4 = (int)(
         (float)val.getAlpha()
            + (float)(
                  val2.getAlpha()
                     - val.getAlpha()
               )
               * alpha
      );
      return new GetrgbHelper(
         color,
         color2,
         color3,
         color4
      );
   }

   public static GetrgbHelper fromInt(
      int n
   ) {
      int color = n
            >> 24
         & 255;
      int color2 = n
            >> 16
         & 255;
      int color3 = n
            >> 8
         & 255;
      int color4 = n
         & 255;
      return new GetrgbHelper(
         color2,
         color3,
         color4,
         color
      );
   }

   public GetrgbHelper withAlpha(
      float f
   ) {
      return new GetrgbHelper(
         this.red,
         this.green,
         this.blue,
         (int)f
      );
   }

   public GetrgbHelper withAlpha(
      int n
   ) {
      return new GetrgbHelper(
         this.red,
         this.green,
         this.blue,
         n
      );
   }

   public GetrgbHelper mulAlpha(
      float f
   ) {
      return this.withAlpha(
         (int)(
            (float)this.alpha
               * f
         )
      );
   }

   public GetrgbHelper mix(
      GetrgbHelper val,
      float f
   ) {
      f = Math.min(
         1.0F,
         Math.max(
            0.0F,
            f
         )
      );
      return new GetrgbHelper(
         (int)DirectionHelper.interpolate(
            (double)this.getRed(),
            (double)val.getRed(),
            (double)f
         ),
         (int)DirectionHelper.interpolate(
            (double)this.getGreen(),
            (double)val.getGreen(),
            (double)f
         ),
         (int)DirectionHelper.interpolate(
            (double)this.getBlue(),
            (double)val.getBlue(),
            (double)f
         ),
         (int)DirectionHelper.interpolate(
            (double)this.getAlpha(),
            (double)val.getAlpha(),
            (double)f
         )
      );
   }

   public GetrgbHelper darker(
      float f
   ) {
      f = MathHelper.clamp(
         f,
         0.0F,
         1.0F
      );
      return new GetrgbHelper(
         (int)(
            (float)this.red
               * (
                  1.0F
                     - f
               )
         ),
         (int)(
            (float)this.green
               * (
                  1.0F
                     - f
               )
         ),
         (int)(
            (float)this.blue
               * (
                  1.0F
                     - f
               )
         ),
         this.alpha
      );
   }

   public static GetrgbHelper fromHSB(
      float f,
      float f2,
      float f3
   ) {
      if (f2
         == 0.0F) {
         int color = (int)(
            f3
                  * 255.0F
               + 0.5F
         );
         return new GetrgbHelper(
            color,
            color,
            color
         );
      } else {
         float f4 = (
               f
                  - (float)Math.floor(
                     (double)f
                  )
            )
            * 6.0F;
         float f5 = f4
            - (float)Math.floor(
               (double)f4
            );
         float f6 = f3
            * (
               1.0F
                  - f2
            );
         float f7 = f3
            * (
               1.0F
                  - f2
                     * f5
            );
         float f8 = f3
            * (
               1.0F
                  - f2
                     * (
                        1.0F
                           - f5
                     )
            );
         float f9 = 0.0F;
         float f10 = 0.0F;
         float f11 = 0.0F;
         switch ((int)f4) {
            case 0:
               f9 = f3;
               f10 = f8;
               f11 = f6;
               break;
            case 1:
               f9 = f7;
               f10 = f3;
               f11 = f6;
               break;
            case 2:
               f9 = f6;
               f10 = f3;
               f11 = f8;
               break;
            case 3:
               f9 = f6;
               f10 = f7;
               f11 = f3;
               break;
            case 4:
               f9 = f8;
               f10 = f6;
               f11 = f3;
               break;
            case 5:
               f9 = f3;
               f10 = f6;
               f11 = f7;
         }

         return new GetrgbHelper(
            (int)(
               f9
                  * 255.0F
            ),
            (int)(
               f10
                  * 255.0F
            ),
            (int)(
               f11
                  * 255.0F
            )
         );
      }
   }

   public float getHue() {
      return this.getHSBValues()[0];
   }

   public float getSaturation() {
      return this.getHSBValues()[2];
   }

   public float getBrightness() {
      return this.getHSBValues()[1];
   }

   private float[] getHSBValues() {
      if (this.hsbValues == null) {
         this.hsbValues = this.calculateHSB();
      }

      return this.hsbValues;
   }

   private float[] calculateHSB() {
      float x = (float)this.red
         / 255.0F;
      float x2 = (float)this.green
         / 255.0F;
      float x3 = (float)this.blue
         / 255.0F;
      float x4 = Math.max(
         x,
         Math.max(
            x2,
            x3
         )
      );
      float x5 = Math.min(
         x,
         Math.min(
            x2,
            x3
         )
      );
      float x6 = x4
         - x5;
      float x7 = 0.0F;
      if (x6
         != 0.0F) {
         if (x4
            == x
            )
          {
            x7 = (
                  x2
                     - x3
               )
               / x6;
         } else if (x4
            == x2
            )
          {
            x7 = (
                     x3
                        - x
                  )
                  / x6
               + 2.0F;
         } else {
            x7 = (
                     x
                        - x2
                  )
                  / x6
               + 4.0F;
         }

         x7 /= 6.0F;
         if (x7
            < 0.0F) {
            x7++;
         }
      }

      float x8 = x4
            == 0.0F
         ? 0.0F
         : x6
            / x4;
      float[] var10000 = new float[3];
      var10000[0] = x7;
      var10000[1] = x8;
      var10000[2] = x4;
      return var10000;
   }

   public GetrgbHelper brighter(
      float f
   ) {
      f = MathHelper.clamp(
         f,
         0.0F,
         1.0F
      );
      return new GetrgbHelper(
         (int)(
            (float)this.red
               + (255.0F - (float)this.red)
                  * f
         ),
         (int)(
            (float)this.green
               + (255.0F - (float)this.green)
                  * f
         ),
         (int)(
            (float)this.blue
               + (255.0F - (float)this.blue)
                  * f
         ),
         this.alpha
      );
   }

   @Override
   public boolean equals(
      Object val
   ) {
      if (this
         == val
         )
       {
         return true;
      } else if (val
            != null
         && this.getClass()
            == val.getClass()
         )
       {
         GetrgbHelper val2 = (GetrgbHelper)val;
         return Float.compare(
                  (float)this.red,
                  (float)val2.red
               )
               == 0
            && Float.compare(
                  (float)this.green,
                  (float)val2.green
               )
               == 0
            && Float.compare(
                  (float)this.blue,
                  (float)val2.blue
               )
               == 0
            && Float.compare(
                  (float)this.alpha,
                  (float)val2.alpha
               )
               == 0;
      } else {
         return false;
      }
   }

   public float difference(
      GetrgbHelper val
   ) {
      return Math.abs(
            this.getHue()
               - val.getHue()
         )
         + Math.abs(
            this.getBrightness()
               - val.getBrightness()
         )
         + Math.abs(
            this.getSaturation()
               - val.getSaturation()
         );
   }

   @Override
   public int hashCode() {
      Object[] var10000 = new Object[4];
      var10000[0] = this.red;
      var10000[1] = this.green;
      var10000[2] = this.blue;
      var10000[3] = this.alpha;
      return Objects.hash(var10000);
   }

   @Generated
   public int getRed() {
      return this.red;
   }

   @Generated
   public int getGreen() {
      return this.green;
   }

   @Generated
   public int getBlue() {
      return this.blue;
   }

   @Generated
   public int getAlpha() {
      return this.alpha;
   }
}
