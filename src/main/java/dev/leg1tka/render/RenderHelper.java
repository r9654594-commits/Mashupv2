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



import dev.leg1tka.modules.render.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public final class RenderHelper implements IMinecraft {
   public static final RenderHelper INSTANCE = new RenderHelper();

   private RenderHelper() {
   }

   public void render(
      MatrixStack matrices,
      Matrix4f matrix
   ) {
      WorldTweaks val = WorldTweaks.INSTANCE;
      if (val
            != null
         && val.isAuroraEnabled()
         )
       {
         int color = val.getAuroraColor1();
         int color2 = val.getAuroraColor2();
         float x = val.getAuroraSpeed();
         float x2 = val.getAuroraIntensity();
         float x3 = val.getAuroraScale();
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         Matrix4f matrix2 = matrices.peek()
            .getPositionMatrix();
         this.drawProceduralAurora(
            matrix2,
            color,
            color2,
            x,
            x2,
            x3
         );
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private void drawProceduralAurora(
      Matrix4f matrix,
      int n,
      int n2,
      float f,
      float f2,
      float f3
   ) {
      float alpha = (float)(
            System.currentTimeMillis() % 100000L
         )
         / 1000.0F
         * f;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      float alpha2 = 200.0F;
      int i = 64;
      int i2 = 24;

      for (int i3 = 0;
         i3
            < i2;
         i3++
      ) {
         float alpha3 = (float) (Math.PI / 2)
            * (float)i3
            / (float)i2;
         float alpha4 = (float) (Math.PI / 2)
            * (float)(
               i3
                  + 1
            )
            / (float)i2;
         float alpha5 = alpha2
            * (float)Math.sin(
               (double)alpha3
            );
         float alpha6 = alpha2
            * (float)Math.cos(
               (double)alpha3
            );
         float alpha7 = alpha2
            * (float)Math.sin(
               (double)alpha4
            );
         float alpha8 = alpha2
            * (float)Math.cos(
               (double)alpha4
            );

         for (int i4 = 0;
            i4
               < i;
            i4++
         ) {
            float alpha9 = (float) (Math.PI * 2)
               * (float)i4
               / (float)i;
            float alpha10 = (float) (Math.PI * 2)
               * (float)(
                  i4
                     + 1
               )
               / (float)i;
            float alpha11 = alpha6
               * (float)Math.cos(
                  (double)alpha9
               );
            float alpha12 = alpha6
               * (float)Math.sin(
                  (double)alpha9
               );
            float alpha13 = alpha6
               * (float)Math.cos(
                  (double)alpha10
               );
            float alpha14 = alpha6
               * (float)Math.sin(
                  (double)alpha10
               );
            float alpha15 = alpha8
               * (float)Math.cos(
                  (double)alpha9
               );
            float alpha16 = alpha8
               * (float)Math.sin(
                  (double)alpha9
               );
            float alpha17 = alpha8
               * (float)Math.cos(
                  (double)alpha10
               );
            float alpha18 = alpha8
               * (float)Math.sin(
                  (double)alpha10
               );
            float alpha19 = (float)Math.sin(
                  (double)alpha9
                        * 2.0
                        * (double)f3
                     + (double)alpha
               )
               * 0.3F;
            float alpha20 = (float)Math.sin(
                  (double)alpha10
                        * 2.0
                        * (double)f3
                     + (double)alpha
               )
               * 0.3F;
            float alpha21 = (float)Math.sin(
                  (double)alpha9
                        * 4.0
                        * (double)f3
                     - (double)alpha
                        * 0.7
               )
               * 0.2F;
            float alpha22 = (float)Math.sin(
                  (double)alpha10
                        * 4.0
                        * (double)f3
                     - (double)alpha
                        * 0.7
               )
               * 0.2F;
            float alpha23 = (float)Math.sin(
                  (double)alpha3
                        * 3.0
                     + (double)alpha
                        * 0.5
               )
               * 0.15F;
            float alpha24 = (float)Math.sin(
                  (double)alpha4
                        * 3.0
                     + (double)alpha
                        * 0.5
               )
               * 0.15F;
            float alpha25 = (
                  alpha19
                     + alpha21
                     + alpha23
                     + 1.0F
               )
               * 0.5F;
            float alpha26 = (
                  alpha20
                     + alpha22
                     + alpha23
                     + 1.0F
               )
               * 0.5F;
            float alpha27 = (
                  alpha19
                     + alpha21
                     + alpha24
                     + 1.0F
               )
               * 0.5F;
            float alpha28 = (
                  alpha20
                     + alpha22
                     + alpha24
                     + 1.0F
               )
               * 0.5F;
            int i5 = ColorUtil.interpolateColor(
               n,
               n2,
               alpha25
            );
            int i6 = ColorUtil.interpolateColor(
               n,
               n2,
               alpha26
            );
            int i7 = ColorUtil.interpolateColor(
               n,
               n2,
               alpha27
            );
            int i8 = ColorUtil.interpolateColor(
               n,
               n2,
               alpha28
            );
            float alpha29 = alpha3
               / (float) (Math.PI / 2)
               * f2
               * 0.7F;
            float alpha30 = alpha4
               / (float) (Math.PI / 2)
               * f2
               * 0.7F;
            alpha29 *= 0.5F
               + alpha25
                  * 0.5F;
            alpha30 *= 0.5F
               + alpha27
                  * 0.5F;
            i5 = ColorUtil.setAlphaColor(
               i5,
               (int)(
                  alpha29
                     * 255.0F
               )
            );
            i6 = ColorUtil.setAlphaColor(
               i6,
               (int)(
                  alpha29
                     * 255.0F
               )
            );
            i7 = ColorUtil.setAlphaColor(
               i7,
               (int)(
                  alpha30
                     * 255.0F
               )
            );
            i8 = ColorUtil.setAlphaColor(
               i8,
               (int)(
                  alpha30
                     * 255.0F
               )
            );
            buffer.vertex(
                  matrix,
                  alpha11,
                  alpha5,
                  alpha12
               )
               .color(
                  i5
               );
            buffer.vertex(
                  matrix,
                  alpha15,
                  alpha7,
                  alpha16
               )
               .color(
                  i7
               );
            buffer.vertex(
                  matrix,
                  alpha17,
                  alpha7,
                  alpha18
               )
               .color(
                  i8
               );
            buffer.vertex(
                  matrix,
                  alpha13,
                  alpha5,
                  alpha14
               )
               .color(
                  i6
               );
         }
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      BufferBuilder buffer2 = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (int i9 = 0;
         i9
            < i2;
         i9++
      ) {
         float alpha31 = (float) (Math.PI / 2)
            * (float)i9
            / (float)i2;
         float alpha32 = (float) (Math.PI / 2)
            * (float)(
               i9
                  + 1
            )
            / (float)i2;
         float alpha33 = alpha2
            * (float)Math.sin(
               (double)alpha31
            )
            * 0.98F;
         float alpha34 = alpha2
            * (float)Math.cos(
               (double)alpha31
            )
            * 0.98F;
         float alpha35 = alpha2
            * (float)Math.sin(
               (double)alpha32
            )
            * 0.98F;
         float alpha36 = alpha2
            * (float)Math.cos(
               (double)alpha32
            )
            * 0.98F;

         for (int i10 = 0;
            i10
               < i;
            i10++
         ) {
            float alpha37 = (float) (Math.PI * 2)
               * (float)i10
               / (float)i;
            float alpha38 = (float) (Math.PI * 2)
               * (float)(
                  i10
                     + 1
               )
               / (float)i;
            float alpha39 = alpha34
               * (float)Math.cos(
                  (double)alpha37
               );
            float alpha40 = alpha34
               * (float)Math.sin(
                  (double)alpha37
               );
            float alpha41 = alpha34
               * (float)Math.cos(
                  (double)alpha38
               );
            float alpha42 = alpha34
               * (float)Math.sin(
                  (double)alpha38
               );
            float alpha43 = alpha36
               * (float)Math.cos(
                  (double)alpha37
               );
            float alpha44 = alpha36
               * (float)Math.sin(
                  (double)alpha37
               );
            float alpha45 = alpha36
               * (float)Math.cos(
                  (double)alpha38
               );
            float alpha46 = alpha36
               * (float)Math.sin(
                  (double)alpha38
               );
            float alpha47 = (float)Math.sin(
                  (double)alpha37
                        * 3.0
                        * (double)f3
                     - (double)alpha
                        * 1.3
               )
               * 0.4F;
            float alpha48 = (float)Math.sin(
                  (double)alpha38
                        * 3.0
                        * (double)f3
                     - (double)alpha
                        * 1.3
               )
               * 0.4F;
            float alpha49 = (float)Math.cos(
                  (double)alpha31
                        * 5.0
                     + (double)alpha
                        * 0.8
               )
               * 0.3F;
            float alpha50 = (float)Math.cos(
                  (double)alpha32
                        * 5.0
                     + (double)alpha
                        * 0.8
               )
               * 0.3F;
            float alpha51 = (
                  alpha47
                     + alpha49
                     + 1.0F
               )
               * 0.5F;
            float alpha52 = (
                  alpha48
                     + alpha49
                     + 1.0F
               )
               * 0.5F;
            float alpha53 = (
                  alpha47
                     + alpha50
                     + 1.0F
               )
               * 0.5F;
            float alpha54 = (
                  alpha48
                     + alpha50
                     + 1.0F
               )
               * 0.5F;
            int i11 = ColorUtil.interpolateColor(
               n2,
               n,
               alpha51
            );
            int i12 = ColorUtil.interpolateColor(
               n2,
               n,
               alpha52
            );
            int i13 = ColorUtil.interpolateColor(
               n2,
               n,
               alpha53
            );
            int i14 = ColorUtil.interpolateColor(
               n2,
               n,
               alpha54
            );
            float alpha55 = alpha31
               / (float) (Math.PI / 2)
               * f2
               * 0.4F;
            float alpha56 = alpha32
               / (float) (Math.PI / 2)
               * f2
               * 0.4F;
            alpha55 *= 0.3F
               + alpha51
                  * 0.7F;
            alpha56 *= 0.3F
               + alpha53
                  * 0.7F;
            i11 = ColorUtil.setAlphaColor(
               i11,
               (int)(
                  alpha55
                     * 255.0F
               )
            );
            i12 = ColorUtil.setAlphaColor(
               i12,
               (int)(
                  alpha55
                     * 255.0F
               )
            );
            i13 = ColorUtil.setAlphaColor(
               i13,
               (int)(
                  alpha56
                     * 255.0F
               )
            );
            i14 = ColorUtil.setAlphaColor(
               i14,
               (int)(
                  alpha56
                     * 255.0F
               )
            );
            buffer2.vertex(
                  matrix,
                  alpha39,
                  alpha33,
                  alpha40
               )
               .color(
                  i11
               );
            buffer2.vertex(
                  matrix,
                  alpha43,
                  alpha35,
                  alpha44
               )
               .color(
                  i13
               );
            buffer2.vertex(
                  matrix,
                  alpha45,
                  alpha35,
                  alpha46
               )
               .color(
                  i14
               );
            buffer2.vertex(
                  matrix,
                  alpha41,
                  alpha33,
                  alpha42
               )
               .color(
                  i12
               );
         }
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer2.end()
      );
      BufferBuilder buffer3 = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      int i15 = 8;

      for (int i16 = 0;
         i16
            < i15;
         i16++
      ) {
         float alpha57 = (float) (Math.PI * 2)
               * (float)i16
               / (float)i15
            + alpha
               * 0.3F;
         float alpha58 = 0.15F;

         for (int i17 = 0;
            i17
               < i2;
            i17++
         ) {
            float alpha59 = (float) (Math.PI / 2)
               * (float)i17
               / (float)i2;
            float alpha60 = (float) (Math.PI / 2)
               * (float)(
                  i17
                     + 1
               )
               / (float)i2;
            float alpha61 = alpha2
               * (float)Math.sin(
                  (double)alpha59
               )
               * 0.96F;
            float alpha62 = alpha2
               * (float)Math.cos(
                  (double)alpha59
               )
               * 0.96F;
            float alpha63 = alpha2
               * (float)Math.sin(
                  (double)alpha60
               )
               * 0.96F;
            float alpha64 = alpha2
               * (float)Math.cos(
                  (double)alpha60
               )
               * 0.96F;
            float alpha65 = alpha62
               * (float)Math.cos(
                  (double)(
                     alpha57
                        - alpha58
                  )
               );
            float alpha66 = alpha62
               * (float)Math.sin(
                  (double)(
                     alpha57
                        - alpha58
                  )
               );
            float alpha67 = alpha62
               * (float)Math.cos(
                  (double)(
                     alpha57
                        + alpha58
                  )
               );
            float alpha68 = alpha62
               * (float)Math.sin(
                  (double)(
                     alpha57
                        + alpha58
                  )
               );
            float alpha69 = alpha64
               * (float)Math.cos(
                  (double)(
                     alpha57
                        - alpha58
                  )
               );
            float alpha70 = alpha64
               * (float)Math.sin(
                  (double)(
                     alpha57
                        - alpha58
                  )
               );
            float alpha71 = alpha64
               * (float)Math.cos(
                  (double)(
                     alpha57
                        + alpha58
                  )
               );
            float alpha72 = alpha64
               * (float)Math.sin(
                  (double)(
                     alpha57
                        + alpha58
                  )
               );
            float alpha73 = (float)Math.sin(
                     (double)alpha
                           * 2.0
                        + (double)i16
                           * 0.5
                  )
                  * 0.3F
               + 0.7F;
            float alpha74 = alpha59
               / (float) (Math.PI / 2)
               * f2
               * 0.5F
               * alpha73;
            float alpha75 = alpha60
               / (float) (Math.PI / 2)
               * f2
               * 0.5F
               * alpha73;
            int i18 = ColorUtil.interpolateColor(
               n,
               n2,
               0.5F
            );
            int i19 = ColorUtil.setAlphaColor(
               i18,
               (int)(
                  alpha74
                     * 255.0F
               )
            );
            int i20 = ColorUtil.setAlphaColor(
               i18,
               (int)(
                  alpha75
                     * 255.0F
               )
            );
            buffer3.vertex(
                  matrix,
                  alpha65,
                  alpha61,
                  alpha66
               )
               .color(
                  i19
               );
            buffer3.vertex(
                  matrix,
                  alpha69,
                  alpha63,
                  alpha70
               )
               .color(
                  i20
               );
            buffer3.vertex(
                  matrix,
                  alpha71,
                  alpha63,
                  alpha72
               )
               .color(
                  i20
               );
            buffer3.vertex(
                  matrix,
                  alpha67,
                  alpha61,
                  alpha68
               )
               .color(
                  i19
               );
         }
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer3.end()
      );
   }
}
