package dev.leg1tka.render;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public final class ThreadRenderer implements IMinecraft {
   private static final float LINE_SPEED = 1.4F;
   private static final float LINE_JITTER = 0.55F;
   private static final float PANEL_DEPTH = 0.35F;
   private static final int STRANDS = 3;
   private static final int SAMPLES = 12;
   private static final double[][] FNRM = new double[][]{
      {0.0, 1.0, 0.0}, {0.0, -1.0, 0.0}, {0.0, 0.0, -1.0}, {0.0, 0.0, 1.0}, {-1.0, 0.0, 0.0}, {1.0, 0.0, 0.0}
   };
   private static final double[][][] FBAS = new double[][][]{
      {{1.0, 0.0, 0.0}, {0.0, 0.0, 1.0}},
      {{1.0, 0.0, 0.0}, {0.0, 0.0, 1.0}},
      {{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}},
      {{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}},
      {{0.0, 0.0, 1.0}, {0.0, 1.0, 0.0}},
      {{0.0, 0.0, 1.0}, {0.0, 1.0, 0.0}}
   };
   private static final int[][] FNBR = new int[][]{{2, 3, 4, 5}, {2, 3, 4, 5}, {0, 1, 4, 5}, {0, 1, 4, 5}, {0, 1, 2, 3}, {0, 1, 2, 3}};
   private static final double[] bp = new double[39];
   private static final double[] ov = new double[3];

   private ThreadRenderer() {
   }

   public static void draw(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n,
      long time
   ) {
      if (!(
            f3
               < 22.0F
         )
         && !(
            f4
               < 12.0F
         )) {
         int color = Math.max(
            22,
            Math.min(
               255,
               132
            )
         );
         matrices.push();
         matrices.translate(
            f,
            f2,
            0.0F
         );
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         ScissorStack.push();
         ScissorStack.setFromComponentCoordinates(
            (double)f,
            (double)f2,
            (double)f3,
            (double)f4
         );

         try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            web(
               matrix,
               f3,
               f4,
               time,
               n,
               color
            );
         } finally {
            ScissorStack.unset();
            ScissorStack.pop();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            matrices.pop();
            Render2D.resetGuiRenderState();
         }
      }
   }

   private static void web(
      Matrix4f matrix,
      float f,
      float f2,
      long time,
      int n,
      int n2
   ) {
      float alpha = (float)(
            System.currentTimeMillis() % 100000L
         )
         / 1000.0F
         * 1.4F;
      float alpha2 = 0.85F;
      float alpha3 = 8.025F;

      for (int i = 0;
         i
            < 6;
         i++
      ) {
         int[] colors = FNBR[i];
         double[] val = FNRM[i];
         double[] val2 = FBAS[i][0];
         double[] val3 = FBAS[i][1];

         for (int i2 = 0;
            i2
               < 3;
            i2++
         ) {
            int i3 = i
                  * 1000
               + i2
                  * 53;
            int i4 = colors[i2
               % colors.length];
            double x = (double)alpha
                  * (
                     0.95
                        + rand01(
                              time,
                              i3
                                 + 1
                           )
                           * 0.55
                  )
               + (double)i2
                  * 0.83
               + (double)i
                  * 1.11;
            double x2 = cl(
               0.5
                  + Math.sin(
                        x
                              * 1.37
                           + rand01(
                                 time,
                                 i3
                                    + 2
                              )
                              * 6.2831853
                     )
                     * 0.38
            );
            epnt(
               i,
               i4,
               x2,
               0.08,
               (double)f,
               (double)f2
            );
            double x3 = ov[0];
            double x4 = ov[1];
            double x5 = ov[2];
            fpnt(
               i,
               cl(
                  0.5
                     + (
                           rand01(
                                 time,
                                 i3
                                    + 3
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               cl(
                  0.5
                     + (
                           rand01(
                                 time,
                                 i3
                                    + 4
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               0.08,
               (double)f,
               (double)f2
            );
            double x6 = ov[0];
            double x7 = ov[1];
            double x8 = ov[2];
            fpnt(
               i4,
               cl(
                  0.5
                     + (
                           rand01(
                                 time,
                                 i3
                                    + 5
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               cl(
                  0.5
                     + (
                           rand01(
                                 time,
                                 i3
                                    + 6
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               0.08,
               (double)f,
               (double)f2
            );
            double x9 = ov[0];
            double x10 = ov[1];
            double x11 = ov[2];
            double[] val4 = FBAS[i4][0];
            double[] val5 = FBAS[i4][1];
            double[] val6 = FNRM[i4];
            double x12 = (double)alpha3
               * (
                  0.7
                     + rand01(
                        time,
                        i3
                           + 7
                     )
               )
               * Math.sin(
                  x
                        * 1.9
                     + rand01(
                           time,
                           i3
                              + 8
                        )
                        * 6.2831853
               );
            double x13 = (double)alpha3
               * (
                  0.7
                     + rand01(
                        time,
                        i3
                           + 9
                     )
               )
               * Math.cos(
                  x
                        * 1.7
                     + rand01(
                           time,
                           i3
                              + 10
                        )
                        * 6.2831853
               );
            double x14 = x3
               - x6;
            double x15 = x4
               - x7;
            double x16 = x5
               - x8;
            double x17 = x6
               + x14
                  * 0.38
               + val2[0]
                  * x12
               + val3[0]
                  * -x12
                  * 0.55;
            double x18 = x7
               + x15
                  * 0.38
               + val2[1]
                  * x12
               + val3[1]
                  * -x12
                  * 0.55;
            double x19 = x8
               + x16
                  * 0.38
               + val2[2]
                  * x12
               + val3[2]
                  * -x12
                  * 0.55;
            double x20 = x6
               + x14
                  * 0.76
               + val2[0]
                  * -x12
                  * 0.65
               + val3[0]
                  * x12
                  * 0.4;
            double x21 = x7
               + x15
                  * 0.76
               + val2[1]
                  * -x12
                  * 0.65
               + val3[1]
                  * x12
                  * 0.4;
            double x22 = x8
               + x16
                  * 0.76
               + val2[2]
                  * -x12
                  * 0.65
               + val3[2]
                  * x12
                  * 0.4;
            double x23 = x9
               - x3;
            double x24 = x10
               - x4;
            double x25 = x11
               - x5;
            double x26 = x3
               + x23
                  * 0.24
               + val4[0]
                  * x13
               + val5[0]
                  * x13
                  * 0.45;
            double x27 = x4
               + x24
                  * 0.24
               + val4[1]
                  * x13
               + val5[1]
                  * x13
                  * 0.45;
            double x28 = x5
               + x25
                  * 0.24
               + val4[2]
                  * x13
               + val5[2]
                  * x13
                  * 0.45;
            double x29 = x3
               + x23
                  * 0.62
               + val4[0]
                  * -x13
                  * 0.7
               + val5[0]
                  * -x13
                  * 0.35;
            double x30 = x4
               + x24
                  * 0.62
               + val4[1]
                  * -x13
                  * 0.7
               + val5[1]
                  * -x13
                  * 0.35;
            double x31 = x5
               + x25
                  * 0.62
               + val4[2]
                  * -x13
                  * 0.7
               + val5[2]
                  * -x13
                  * 0.35;
            int i5 = Math.max(
               18,
               Math.min(
                  255,
                  (int)(
                     (double)n2
                        * (
                           0.74
                              + 0.26
                                 * Math.sin(
                                    x
                                       * 2.6
                                 )
                        )
                  )
               )
            );
            int i6 = ColorUtil.setAlphaColor(
               n,
               i5
            );
            ribbon(
               matrix,
               x6,
               x7,
               x8,
               x17,
               x18,
               x19,
               x20,
               x21,
               x22,
               x3,
               x4,
               x5,
               val[0],
               val[1],
               val[2],
               i6,
               alpha2
            );
            ribbon(
               matrix,
               x3,
               x4,
               x5,
               x26,
               x27,
               x28,
               x29,
               x30,
               x31,
               x9,
               x10,
               x11,
               val6[0],
               val6[1],
               val6[2],
               i6,
               alpha2
            );
         }
      }
   }

   private static void ribbon(
      Matrix4f matrix,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      double d7,
      double d8,
      double d9,
      double d10,
      double d11,
      double d12,
      double d13,
      double d14,
      double d15,
      int n,
      float f
   ) {
      for (int i = 0;
         i
            <= 12;
         i++
      ) {
         double x = (double)i
            / 12.0;
         double x2 = 1.0
            - x;
         double x3 = x2
            * x2;
         double x4 = x
            * x;
         double x5 = x3
            * x2;
         double x6 = 3.0
            * x3
            * x;
         double x7 = 3.0
            * x2
            * x4;
         double x8 = x4
            * x;
         int i2 = i
            * 3;
         bp[i2] = d
               * x5
            + d4
               * x6
            + d7
               * x7
            + d10
               * x8;
         bp[i2
            + 1] = d2
               * x5
            + d5
               * x6
            + d8
               * x7
            + d11
               * x8;
         bp[i2
            + 2] = d3
               * x5
            + d6
               * x6
            + d9
               * x7
            + d12
               * x8;
      }

      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (int i3 = 0;
         i3
            < 12;
         i3++
      ) {
         int i4 = i3
            * 3;
         int i5 = i4
            + 3;
         double x9 = bp[i4];
         double x10 = bp[i4
            + 1];
         double x11 = bp[i4
            + 2];
         double x12 = bp[i5];
         double x13 = bp[i5
            + 1];
         double x14 = bp[i5
            + 2];
         double x15 = x12
            - x9;
         double x16 = x13
            - x10;
         double x17 = x14
            - x11;
         if (!(
            x15
                     * x15
                  + x16
                     * x16
                  + x17
                     * x17
               < 1.0E-6
         )) {
            double x18 = d14
                  * x17
               - d15
                  * x16;
            double x19 = d15
                  * x15
               - d13
                  * x17;
            double x20 = d13
                  * x16
               - d14
                  * x15;
            double x21 = x18
                  * x18
               + x19
                  * x19
               + x20
                  * x20;
            if (!(
               x21
                  < 1.0E-6
            )) {
               double x22 = (double)f
                  / Math.sqrt(
                     x21
                  );
               x18 *= x22;
               x19 *= x22;
               x20 *= x22;
               buffer.vertex(
                     matrix,
                     (float)(
                        x9
                           + x18
                     ),
                     (float)(
                        x10
                           + x19
                     ),
                     (float)(
                        x11
                           + x20
                     )
                  )
                  .color(
                     n
                  );
               buffer.vertex(
                     matrix,
                     (float)(
                        x9
                           - x18
                     ),
                     (float)(
                        x10
                           - x19
                     ),
                     (float)(
                        x11
                           - x20
                     )
                  )
                  .color(
                     n
                  );
               buffer.vertex(
                     matrix,
                     (float)(
                        x12
                           - x18
                     ),
                     (float)(
                        x13
                           - x19
                     ),
                     (float)(
                        x14
                           - x20
                     )
                  )
                  .color(
                     n
                  );
               buffer.vertex(
                     matrix,
                     (float)(
                        x12
                           + x18
                     ),
                     (float)(
                        x13
                           + x19
                     ),
                     (float)(
                        x14
                           + x20
                     )
                  )
                  .color(
                     n
                  );
            }
         }
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private static void epnt(
      int n,
      int n2,
      double d,
      double d2,
      double d3,
      double d4
   ) {
      double d5 = Double.NaN;
      double d6 = Double.NaN;
      double d7 = Double.NaN;
      switch (n) {
         case 0:
            d6 = d4
               - d2;
            break;
         case 1:
            d6 = d2;
            break;
         case 2:
            d7 = d2;
            break;
         case 3:
            d7 = 0.35F
               - d2;
            break;
         case 4:
            d5 = d2;
            break;
         default:
            d5 = d3
               - d2;
      }

      switch (n2) {
         case 0:
            d6 = d4
               - d2;
            break;
         case 1:
            d6 = d2;
            break;
         case 2:
            d7 = d2;
            break;
         case 3:
            d7 = 0.35F
               - d2;
            break;
         case 4:
            d5 = d2;
            break;
         default:
            d5 = d3
               - d2;
      }

      double d8 = cl(
         d
      );
      ov[0] = Double.isNaN(
            d5
         )
         ? d3
            * d8
         : d5;
      ov[1] = Double.isNaN(
            d6
         )
         ? d4
            * d8
         : d6;
      ov[2] = Double.isNaN(
            d7
         )
         ? 0.35F
            * d8
         : d7;
   }

   private static void fpnt(
      int n,
      double d,
      double d2,
      double d3,
      double d4,
      double d5
   ) {
      d = cl(
         d
      );
      d2 = cl(
         d2
      );
      switch (n) {
         case 0:
            ov[0] = d4
               * d;
            ov[1] = d5
               - d3;
            ov[2] = 0.35F
               * d2;
            break;
         case 1:
            ov[0] = d4
               * d;
            ov[1] = d3;
            ov[2] = 0.35F
               * d2;
            break;
         case 2:
            ov[0] = d4
               * d;
            ov[1] = d5
               * d2;
            ov[2] = d3;
            break;
         case 3:
            ov[0] = d4
               * d;
            ov[1] = d5
               * d2;
            ov[2] = 0.35F
               - d3;
            break;
         case 4:
            ov[0] = d3;
            ov[1] = d5
               * d2;
            ov[2] = 0.35F
               * d;
            break;
         default:
            ov[0] = d4
               - d3;
            ov[1] = d5
               * d2;
            ov[2] = 0.35F
               * d;
      }
   }

   private static double rand01(
      long time,
      int n
   ) {
      long time2 = time
         + -7046029254386353131L
            * (
               (long)n
                  + 1L
            );
      time2 ^= time2
         >>> 30;
      time2 *= -4658895280553007687L;
      time2 ^= time2
         >>> 27;
      time2 *= -7723592293110705685L;
      time2 ^= time2
         >>> 31;
      return (double)(
            time2
               & 16777215L
         )
         / 1.6777216E7;
   }

   private static double cl(
      double d
   ) {
      return Math.max(
         0.0,
         Math.min(
            1.0,
            d
         )
      );
   }

   public static long seedForRect(
      float f,
      float f2,
      float f3,
      float f4
   ) {
      return (long)(
               f
                  * 31.7F
            )
            << 32
         ^ (long)(
               f2
                  * 17.3F
            )
            << 16
         ^ (long)(
            f3
                  * 11.1F
               + f4
                  * 7.9F
         );
   }
}
