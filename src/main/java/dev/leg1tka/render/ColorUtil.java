package dev.leg1tka.render;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;




import com.mojang.blaze3d.systems.RenderSystem;

import dev.leg1tka.AspectDLC;


import java.awt.Color;

import net.minecraft.text.MutableText;

import net.minecraft.text.Style;

import net.minecraft.text.Text;

import net.minecraft.text.TextColor;

import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;



public class ColorUtil {

   public static final Color green = new Color(36, 218, 118);

   public static final Color yellow = new Color(255, 196, 67);

   public static final Color orange = new Color(255, 134, 0);

   public static final Color red = new Color(239, 72, 54);

   public static final Color Blues = new Color(125, 217, 250);



   public static int red(

      int color

   ) {

      return color

            >> 16

         & 255;

   }



   public static int green(

      int color

   ) {

      return color

            >> 8

         & 255;

   }



   public static float redf(

      int color

   ) {

      return (float)red(

            color

         )

         / 255.0F;

   }



   public static float greenf(

      int color

   ) {

      return (float)green(

            color

         )

         / 255.0F;

   }



   public static float bluef(

      int color

   ) {

      return (float)blue(

            color

         )

         / 255.0F;

   }



   public static float alphaf(

      int color

   ) {

      return (float)alpha(

            color

         )

         / 255.0F;

   }



   public static int getColor(

      int color,

      int val

   ) {

      return getColor(

         color,

         color,

         color,

         val

      );

   }



   public static int gradient(

      int r,

      int g,

      float ratio

   ) {

      ratio = MathHelper.clamp(

         ratio,

         0.0F,

         1.0F

      );

      int r2 = MathHelper.lerp(

         ratio,

         red(

            r

         ),

         red(

            g

         )

      );

      int r3 = MathHelper.lerp(

         ratio,

         green(

            r

         ),

         green(

            g

         )

      );

      int r4 = MathHelper.lerp(

         ratio,

         blue(

            r

         ),

         blue(

            g

         )

      );

      int r5 = MathHelper.lerp(

         ratio,

         alpha(

            r

         ),

         alpha(

            g

         )

      );

      return rgba(

         r2,

         r3,

         r4,

         r5

      );

   }



   public static int toColor(

      String str

   ) {

      if (str

            != null

         && str.length()

            == 7

         && str.startsWith(

            "#"

         )) {

         try {

            int val = Integer.parseInt(

               str.substring(

                  1

               ),

               16

            );

            return Integer.parseInt("-16777216")

               | val;

         } catch (NumberFormatException var2) {

            return Integer.parseInt("-16777216");

         }

      } else {

         return Integer.parseInt("-16777216");

      }

   }



   public static int applyAlpha(

      int color,

      float alpha

   ) {

      int a = color

            >> 24

         & 255;

      int a2 = (int)(

         (float)a

            * Math.max(

               0.0F,

               Math.min(

                  1.0F,

                  alpha

               )

            )

      );

      return color

            & 16777215

         | a2

            << 24;

   }



   public static int r(

      int color

   ) {

      return color

            >> 16

         & 255;

   }



   public static int g(

      int color

   ) {

      return color

            >> 8

         & 255;

   }



   public static int b(

      int color

   ) {

      return color

         & 255;

   }



   public static int a(

      int color

   ) {

      return color

            >> 24

         & 255;

   }



   public static int hexToRgb(

      String hex

   ) {

      if (hex.startsWith(

         "#"

      )) {

         hex = hex.substring(

            1

         );

      }



      if (hex.length()

         != 6) {

         throw new IllegalArgumentException(

            "Недопустимый формат HEX: "

               + hex

         );

      } else {

         int val = Integer.parseInt(

            hex.substring(

               0, 2

            ),

            16

         );

         int val2 = Integer.parseInt(

            hex.substring(

               2, 4

            ),

            16

         );

         int val3 = Integer.parseInt(

            hex.substring(

               4, 6

            ),

            16

         );

         return rgb(

            val,

            val2,

            val3

         );

      }

   }



   public static int getThemeColor() {

      return !AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")

         ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0]

         : getThemeColor(0);

   }



   public static int getThemeColor(

      int color

   ) {

      return AspectDLC.INSTANCE

         .themeStorage

         .getThemes()

         .getTheme()

         .getColor(

            color

         );

   }



   public static int getThemeStaticColor() {

      return AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0];

   }



   public static int rainbow(

      int param1,

      int param2,

      float f3,

      float f4,

      float f5

   ) {

      int r = (int)(

         (

               System.currentTimeMillis()

                     / (long)param1

                  + (long)param2

            )

            % 360L

      );

      float f = (float)r

         / 360.0F;

      int r2 = Color.HSBtoRGB(

         f,

         f3,

         f4

      );

      return getColor(

         red(

            r2

         ),

         green(

            r2

         ),

         blue(

            r2

         ),

         Math.max(

            0,

            Math.min(

               255,

               (int)(

                  f5

                     * 255.0F

               )

            )

         )

      );

   }



   public static int interpolate(

      int r,

      int g,

      double d3

   ) {

      d3 = (double)(

         (float)DirectionHelper.clamp(

            0.0,

            1.0,

            d3

         )

      );

      return getColor(

         DirectionHelper.ler1p(

            red(

               r

            ),

            red(

               g

            ),

            d3

         ),

         DirectionHelper.ler1p(

            green(

               r

            ),

            green(

               g

            ),

            d3

         ),

         DirectionHelper.ler1p(

            blue(

               r

            ),

            blue(

               g

            ),

            d3

         ),

         DirectionHelper.ler1p(

            alpha(

               r

            ),

            alpha(

               g

            ),

            d3

         )

      );

   }



   public static int[] genGradientForText(

      int r,

      int g,

      int b

   ) {

      int[] colors = new int[b];



      for (int i = 0;

         i

            < b;

         i++

      ) {

         double val = (double)i

            / (double)(

               b

                  - 1

            );

         colors[i] = interpolate(

            r,

            g,

            val

         );

      }



      return colors;

   }



   public static int blue(

      int color

   ) {

      return color

         & 255;

   }



   public static int overCol(

      int r,

      int g,

      float f3

   ) {

      return getColor(

         (float)red(

                  r

               )

               * (

                  1.0F

                     - f3

               )

            + (float)red(

                  g

               )

               * f3,

         (float)green(

                  r

               )

               * (

                  1.0F

                     - f3

               )

            + (float)green(

                  g

               )

               * f3,

         (float)blue(

                  r

               )

               * (

                  1.0F

                     - f3

               )

            + (float)blue(

                  g

               )

               * f3,

         (float)alpha(

                  r

               )

               * (

                  1.0F

                     - f3

               )

            + (float)alpha(

                  g

               )

               * f3

      );

   }



   public static int darken(

      int color,

      float factor

   ) {

      float[] hsv = getColorT(

         color

      );

      float[] hsv2 = Color.RGBtoHSB(

         (int)(

            hsv[0]

               * 255.0F

         ),

         (int)(

            hsv[1]

               * 255.0F

         ),

         (int)(

            hsv[Integer.parseInt(

                  "2"

               )]

               * 255.0F

         ),

         null

      );

      hsv2[Integer.parseInt(

         "2"

      )] *= factor;

      hsv2[Integer.parseInt(

         "2"

      )] = Math.max(

         0.0F,

         Math.min(

            1.0F,

            hsv2[Integer.parseInt(

               "2"

            )]

         )

      );

      int val = Color.HSBtoRGB(

         hsv2[0],

         hsv2[1],

         hsv2[Integer.parseInt(

            "2"

         )]

      );

      return applyOpacity(

         val,

         (float)(

            (int)(

               hsv[Integer.parseInt(

                     "3"

                  )]

                  * 255.0F

            )

         )

      );

   }



   public static int multDark(

      int color,

      float factor

   ) {

      return getColor(

         (float)red(

               color

            )

            * factor,

         (float)green(

               color

            )

            * factor,

         (float)blue(

               color

            )

            * factor,

         (float)alpha(

            color

         )

      );

   }



   public static int overCol(

      int color,

      int val

   ) {

      return overCol(

         color,

         val,

         0.5F

      );

   }



   public static int alpha(

      int color

   ) {

      return color

            >> 24

         & 255;

   }



   public static int multAlpha(

      int color,

      float alpha

   ) {

      return getColor(

         (float)red(

            color

         ),

         (float)green(

            color

         ),

         (float)blue(

            color

         ),

         (float)alpha(

               color

            )

            * alpha

      );

   }



   public static int replAlpha(

      int color,

      int alpha

   ) {

      alpha = Math.max(

         0,

         Math.min(

            255,

            alpha

         )

      );

      return alpha

            << 24

         | color

            & 16777215;

   }



   public static Color random() {

      return new Color(Color.HSBtoRGB((float)Math.random(), (float)(0.75 + Math.random() / 4.0), (float)(0.75 + Math.random() / 4.0)));

   }



   public static int getColor(

      float f1,

      float f2,

      float f3,

      float f4

   ) {

      return new Color(

            (int)f1,

            (int)f2,

            (int)f3,

            (int)f4

         )

         .getRGB();

   }



   public static float[] getRGBAf(

      int color

   ) {

      float[] var10000 = new float[4];

      var10000[0] = redf(

         color

      );

      var10000[1] = greenf(

         color

      );

      var10000[2] = bluef(

         color

      );

      var10000[3] = alphaf(

         color

      );

      return var10000;

   }



   public static float[] getRGBAf1(

      int color

   ) {

      float[] var10000 = new float[4];

      var10000[0] = (float)red(

            color

         )

         / 255.0F;

      var10000[1] = (float)green(

            color

         )

         / 255.0F;

      var10000[2] = (float)blue(

            color

         )

         / 255.0F;

      var10000[3] = (float)alpha(

            color

         )

         / 255.0F;

      return var10000;

   }



   public static Color interpolateTwoColors(

      int param1,

      int param2,

      Color color,

      Color color2,

      boolean arg5

   ) {

      int n = 0;

      if (param1

         == 0) {

         n = param2

            % 360;

      } else {

         n = (int)(

            (

                  System.currentTimeMillis()

                        / (long)param1

                     + (long)param2

               )

               % 360L

         );

      }



      n = (

            n

                  >= 180

               ? 360

                  - n

               : n

         )

         * 2;

      return arg5

         ? interpolateColorHue(

            color,

            color2,

            (float)n

               / 360.0F

         )

         : interpolateColorC(

            color,

            color2,

            (float)n

               / 360.0F

         );

   }



   public static Color interpolateTwoColors(

      int r,

      int g,

      Color color,

      Color color2

   ) {

      return interpolateTwoColors(

         r,

         g,

         color,

         color2,

         false

      );

   }



   public static Color astolfo(

      float f1,

      float f2,

      float f3,

      float f4

   ) {

      float f = 1800.0F;

      float f5 = (float)(

            System.currentTimeMillis()

               % (long)(

                  (int)f

               )

         )

         + (

               f2

                  - f1

            )

            * f4;



      while (

         f5

            > f

      ) {

         f5 -= f;

      }



      f5 /= f;

      if (f5

         > 1.0F) {

         f5 = 1.0F

            - (

               f5

                  - 1.0F

            );

      }



      return Color.getHSBColor(

         ++f5,

         f3,

         1.0F

      );

   }



   private static int calculateHueDegrees(

      int color,

      int val

   ) {

      long time = System.currentTimeMillis();

      long time2 = (

            time

                  / (long)color

               + (long)val

         )

         % 360L;

      return (int)time2;

   }



   public static void setColor(

      Color color,

      float f2

   ) {

      float f = (float)color.getRed()

         / 255.0F;

      float f3 = (float)color.getGreen()

         / 255.0F;

      float f4 = (float)color.getBlue()

         / 255.0F;

      RenderSystem.setShaderColor(

         f,

         f3,

         f4,

         f2

      );

   }



   public static int rgb(

      int r,

      int g,

      int b

   ) {

      return Integer.parseInt("-16777216")

         | r

            << 16

         | g

            << 8

         | b;

   }



   public static int rgba(

      int r,

      int g,

      int b,

      int a

   ) {

      return a

            << 24

         | r

            << 16

         | g

            << 8

         | b;

   }



   public static float[] rgba(

      int color

   ) {

      float[] var10000 = new float[4];

      var10000[0] = (float)(

            color

                  >> 16

               & 255

         )

         / 255.0F;

      var10000[1] = (float)(

            color

                  >> 8

               & 255

         )

         / 255.0F;

      var10000[2] = (float)(

            color

               & 255

         )

         / 255.0F;

      var10000[3] = (float)(

            color

                  >> 24

               & 255

         )

         / 255.0F;

      return var10000;

   }



   public static int rgba(

      double d1,

      double d2,

      double d3,

      double d4

   ) {

      return rgba(

         (int)d1,

         (int)d2,

         (int)d3,

         (int)d4

      );

   }



   public static int getRed(

      int color

   ) {

      return color

            >> 16

         & 255;

   }



   public static int getGreen(

      int color

   ) {

      return color

            >> 8

         & 255;

   }



   public static int interpolate(

      int r,

      int g,

      float ratio

   ) {

      float[] hsv = rgba(

         r

      );

      float[] hsv2 = rgba(

         g

      );

      return rgba(

         (int)DirectionHelper.interpolate(

            hsv[0]

               * 255.0F,

            hsv2[0]

               * 255.0F,

            ratio

         ),

         (int)DirectionHelper.interpolate(

            hsv[1]

               * 255.0F,

            hsv2[1]

               * 255.0F,

            ratio

         ),

         (int)DirectionHelper.interpolate(

            hsv[Integer.parseInt(

                  "2"

               )]

               * 255.0F,

            hsv2[Integer.parseInt(

                  "2"

               )]

               * 255.0F,

            ratio

         ),

         (int)DirectionHelper.interpolate(

            hsv[Integer.parseInt(

                  "3"

               )]

               * 255.0F,

            hsv2[Integer.parseInt(

                  "3"

               )]

               * 255.0F,

            ratio

         )

      );

   }



   public static int interpolateColor(

      int r,

      int g,

      float ratio

   ) {

      ratio = Math.min(

         1.0F,

         Math.max(

            0.0F,

            ratio

         )

      );

      int r2 = getRed(

         r

      );

      int r3 = getGreen(

         r

      );

      int r4 = getBlue(

         r

      );

      int r5 = getAlpha(

         r

      );

      int r6 = getRed(

         g

      );

      int r7 = getGreen(

         g

      );

      int r8 = getBlue(

         g

      );

      int r9 = getAlpha(

         g

      );

      int r10 = interpolateInt(

         r2,

         r6,

         (double)ratio

      );

      int r11 = interpolateInt(

         r3,

         r7,

         (double)ratio

      );

      int r12 = interpolateInt(

         r4,

         r8,

         (double)ratio

      );

      int r13 = interpolateInt(

         r5,

         r9,

         (double)ratio

      );

      return r13

            << 24

         | r10

            << 16

         | r11

            << 8

         | r12;

   }



   public static MutableText gradient(

      String str,

      int g,

      int b

   ) {

      MutableText text = Text.empty();



      for (int i = 0;

         i

            < str.length();

         i++

      ) {

         int i2 = interpolateColor(

            g,

            b,

            (float)i

               / (float)str.length()

         );

         MutableText text2 = Text.literal(

               String.valueOf(

                  str.charAt(

                     i

                  )

               )

            )

            .setStyle(

               Style.EMPTY

                  .withColor(

                     TextColor.fromRgb(

                        i2

                     )

                  )

            );

         text.append(

            text2

         );

      }



      return text;

   }



   public static Text replace(

      Text text,

      String str,

      String str2

   ) {

      if (text

            != null

         && str

            != null

         && str2

            != null) {

         String str3 = text.getString();

         String str4 = str3.replace(

            str,

            str2

         );

         return Text.literal(

            str4

         );

      } else {

         return text;

      }

   }



   public static int gradient(

      int r,

      int g,

      int... arg3

   ) {

      int n = (int)(

         (

               System.currentTimeMillis()

                     / (long)r

                  + (long)g

            )

            % 360L

      );

      n = (

            n

                  > 180

               ? 360

                  - n

               : n

         )

         + 180;

      int n2 = (int)(

         (float)n

            / 360.0F

            * (float)arg3.length

      );

      if (n2

         == arg3.length

         )

       {

         n2--;

      }



      int n3 = arg3[n2];

      int n4 = arg3[n2

            == arg3.length

               - 1

         ? 0

         : n2

            + 1];

      return interpolateColor(

         n3,

         n4,

         (float)n

               / 360.0F

               * (float)arg3.length

            - (float)n2

      );

   }



   public static int themeGradient(

      int r,

      int g,

      float ratio

   ) {

      int n = getThemeColor();

      int[] var10002 = new int[2];

      var10002[0] = n;

      var10002[1] = darken(

         n,

         ratio

      );

      return gradient(

         r,

         g,

         var10002

      );

   }



   public static int getBlue(

      int color

   ) {

      return color

         & 255;

   }



   public static int getAlpha(

      int color

   ) {

      return color

            >> 24

         & 255;

   }



   public static int getColor(

      int r,

      int g,

      int b,

      int a

   ) {

      int color = 0;

      color |= a

         << 24;

      color |= r

         << 16;

      color |= g

         << 8;

      return color

         | b;

   }



   public static int getColor(

      int color

   ) {

      return getColor(

         color,

         color,

         color,

         255

      );

   }



   public static float[] getColorA(

      int color

   ) {

      float[] var10000 = new float[4];

      var10000[0] = (float)red(

            color

         )

         / 255.0F;

      var10000[1] = (float)green(

            color

         )

         / 255.0F;

      var10000[2] = (float)blue(

            color

         )

         / 255.0F;

      var10000[3] = alphaf(

         color

      );

      return var10000;

   }



   public static float[] getColorT(

      int color

   ) {

      float[] var10000 = new float[4];

      var10000[0] = (float)red(

            color

         )

         / 255.0F;

      var10000[1] = (float)green(

            color

         )

         / 255.0F;

      var10000[2] = (float)blue(

            color

         )

         / 255.0F;

      var10000[3] = alphaf(

         color

      );

      return var10000;

   }



   public static void setColor(

      double d1,

      double d2,

      double d3,

      double d4

   ) {

      GL11.glColor4d(

         d1,

         d2,

         d3,

         d4

      );

   }



   public static int setAlphaColor(

      int color,

      int alpha

   ) {

      return color

            & 16777215

         | alpha

            << 24;

   }



   public static float lerp(

      float ratio,

      float ratio2,

      float ratio3

   ) {

      return ratio

         + ratio3

            * (

               ratio2

                  - ratio

            );

   }



   public static Color interpolateColorC(

      Color color,

      Color color2,

      float ratio

   ) {

      ratio = Math.min(

         1.0F,

         Math.max(

            0.0F,

            ratio

         )

      );

      return new Color(

         interpolateInt(

            color.getRed(),

            color2.getRed(),

            (double)ratio

         ),

         interpolateInt(

            color.getGreen(),

            color2.getGreen(),

            (double)ratio

         ),

         interpolateInt(

            color.getBlue(),

            color2.getBlue(),

            (double)ratio

         ),

         interpolateInt(

            color.getAlpha(),

            color2.getAlpha(),

            (double)ratio

         )

      );

   }



   public static Double interpolate(

      double d1,

      double d2,

      double d3

   ) {

      return d1

         + (

               d2

                  - d1

            )

            * d3;

   }



   public static float interpolateFloat(

      float ratio,

      float ratio2,

      double d3

   ) {

      return interpolate(

            (double)ratio,

            (double)ratio2,

            (double)(

               (float)d3

            )

         )

         .floatValue();

   }



   public static int interpolateInt(

      int r,

      int g,

      double d3

   ) {

      return interpolate(

         r,

         g,

         (float)d3

      );

   }



   public static Color interpolateColorHue(

      Color color,

      Color color2,

      float ratio

   ) {

      ratio = Math.min(

         1.0F,

         Math.max(

            0.0F,

            ratio

         )

      );

      float[] hsv = Color.RGBtoHSB(

         color.getRed(),

         color.getGreen(),

         color.getBlue(),

         null

      );

      float[] hsv2 = Color.RGBtoHSB(

         color2.getRed(),

         color2.getGreen(),

         color2.getBlue(),

         null

      );

      Color color3 = Color.getHSBColor(

         interpolateFloat(

            hsv[0],

            hsv2[0],

            (double)ratio

         ),

         interpolateFloat(

            hsv[1],

            hsv2[1],

            (double)ratio

         ),

         interpolateFloat(

            hsv[Integer.parseInt(

               "2"

            )],

            hsv2[Integer.parseInt(

               "2"

            )],

            (double)ratio

         )

      );

      return new Color(

         color3.getRed(),

         color3.getGreen(),

         color3.getBlue(),

         interpolateInt(

            color.getAlpha(),

            color2.getAlpha(),

            (double)ratio

         )

      );

   }



   public static void setColor(

      Color color

   ) {

      if (color

         == null) {

         color = Color.white;

      }



      setColor(

         (double)(

            (float)color.getRed()

               / 255.0F

         ),

         (double)(

            (float)color.getGreen()

               / 255.0F

         ),

         (double)(

            (float)color.getBlue()

               / 255.0F

         ),

         (double)(

            (float)color.getAlpha()

               / 255.0F

         )

      );

   }



   public static void setColor(

      int color

   ) {

      setColor(

         color,

         (float)(

               color

                     >> 24

                  & 255

            )

            / 255.0F

      );

   }



   public static void setColor(

      int color,

      float f2

   ) {

      float f = (float)(

            color

                  >> 16

               & 255

         )

         / 255.0F;

      float f3 = (float)(

            color

                  >> 8

               & 255

         )

         / 255.0F;

      float f4 = (float)(

            color

               & 255

         )

         / 255.0F;

      RenderSystem.setShaderColor(

         f,

         f3,

         f4,

         f2

      );

   }



   public static int applyOpacity(

      int color,

      float f2

   ) {

      return rgba(

         (double)getRed(

            color

         ),

         (double)getGreen(

            color

         ),

         (double)getBlue(

            color

         ),

         (double)(

            (float)getAlpha(

                  color

               )

               * f2

               / 255.0F

         )

      );

   }



   public static int reFactorColor(

      int color,

      float f2

   ) {

      return rgba(

         (double)(

            extractRedf(

                  color

               )

               * f2

         ),

         (double)(

            extractGreenf(

                  color

               )

               * f2

         ),

         (double)(

            extractBluef(

                  color

               )

               * f2

         ),

         (double)extractAlphaf(

            color

         )

      );

   }



   public static float extractRedf(

      int color

   ) {

      return (float)(

            color

                  >> 16

               & 255

         )

         / 255.0F;

   }



   public static int extractRed(

      int color

   ) {

      return color

            >> 16

         & 255;

   }



   public static float extractBluef(

      int color

   ) {

      return (float)(

            color

               & 255

         )

         / 255.0F;

   }



   public static int extractBlue(

      int color

   ) {

      return color

         & 255;

   }



   public static float extractGreenf(

      int color

   ) {

      return (float)(

            color

                  >> 8

               & 255

         )

         / 255.0F;

   }



   public static int extractGreen(

      int color

   ) {

      return color

            >> 8

         & 255;

   }



   public static float extractAlphaf(

      int color

   ) {

      return (float)(

            color

                  >> 24

               & 255

         )

         / 255.0F;

   }



   public static int extractAlpha(

      int color

   ) {

      return color

            >> 24

         & 255;

   }



   public static int lighten(

      int color,

      float f2

   ) {

      return 0;

   }



   private static ThemeColors currentTheme() {

      return AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null

         ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme()

         : null;

   }



   public static int getColorMain() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-15921904")

         : val.colorMain;

   }



   public static int getColorVisualModules() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? getThemeColor()

         : val.colorVisualModules;

   }



   public static int getColorText() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-1513240")

         : val.colorText;

   }



   public static int getColorInactiveText() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-7829351")

         : val.colorInactiveText;

   }



   public static int getColorHeaderBg() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-16119283")

         : val.colorHeaderBg;

   }



   public static int getColorHeaderText() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? getThemeColor()

         : val.colorHeaderText;

   }



   public static int getColorSlider() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? getThemeColor()

         : val.colorSlider;

   }



   public static int getColorSliderCircle() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-1")

         : val.colorSliderCircle;

   }



   public static int getColorSliderWindow() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-14013899")

         : val.colorSliderWindow;

   }



   public static int getColorIndicator() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? getThemeColor()

         : val.colorIndicator;

   }



   public static int getColorInactiveIndicator() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-14013899")

         : val.colorInactiveIndicator;

   }



   public static int getColorButton() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? getThemeColor()

         : val.colorButton;

   }



   public static int getColorInactiveButton() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-14013899")

         : val.colorInactiveButton;

   }



   public static int getColorSeparator() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-14013899")

         : val.colorSeparator;

   }



   public static int getColorField() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-15066590")

         : val.colorField;

   }



   public static int getColorInactiveField() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-15921904")

         : val.colorInactiveField;

   }



   public static int getColorTooltipText() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-1")

         : val.colorTooltipText;

   }



   public static int getColorWindowBg() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? Integer.parseInt("-15921904")

         : val.colorWindowBg;

   }



   public static int getColorIcons() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? getThemeColor()

         : val.colorIcons;

   }



   public static int getColorClient() {

      ThemeColors val = currentTheme();

      return val

            == null

         ? getThemeColor()

         : val.colorClient;

   }
}

