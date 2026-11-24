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


import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GlowRenderer {
   private static GlowRenderer instance;
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private Framebuffer glowBuffer;
   private int lastWidth;
   private int lastHeight;
   private float glowRadius = 10.0F;
   private float glowIntensity = 1.0F;
   private Color glowColor = Color.WHITE;
   private Matrix4f savedProjection;
   private int savedFbo;
   private static final int RINGS = 6;
   private static final int ANGLES_PER_RING = 12;

   public static GlowRenderer getInstance() {
      if (instance == null) {
         instance = new GlowRenderer();
      }

      return instance;
   }

   private void checkFramebuffers() {
      int width = mc.getWindow()
         .getFramebufferWidth();
      int width2 = mc.getWindow()
         .getFramebufferHeight();
      if (this.glowBuffer == null
         || this.lastWidth
            != width
         || this.lastHeight
            != width2
         )
       {
         if (this.glowBuffer != null) {
            this.glowBuffer.delete();
         }

         this.glowBuffer = new SimpleFramebuffer(
            width,
            width2,
            false
         );
         this.lastWidth = width;
         this.lastHeight = width2;
      }
   }

   public void begin(
      float f,
      Color color
   ) {
      this.begin(
         f,
         1.0F,
         color
      );
   }

   public void begin(
      float f,
      float f2,
      Color color
   ) {
      this.checkFramebuffers();
      this.glowRadius = f;
      this.glowIntensity = f2;
      this.glowColor = color;
      this.savedProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
      this.savedFbo = GL11.glGetInteger(36006);
      GL30.glBindFramebuffer(36160, this.glowBuffer.fbo);
      GL11.glViewport(0, 0, this.lastWidth, this.lastHeight);
      RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
      RenderSystem.clear(16384);
      RenderSystem.setProjectionMatrix(this.savedProjection, ProjectionType.ORTHOGRAPHIC);
   }

   public void end(
      MatrixStack matrices,
      IClientAction_QR6v5Fi4 val
   ) {
      GL30.glBindFramebuffer(36160, this.savedFbo);
      GL11.glViewport(0, 0, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
      RenderSystem.setProjectionMatrix(this.savedProjection, ProjectionType.ORTHOGRAPHIC);
      this.renderGlow(
         matrices
      );
      if (val
         != null) {
         val.render();
      }
   }

   private float gaussian(
      float f,
      float f2
   ) {
      return (float)Math.exp(
         (double)(
            -(
                  f
                     * f
               )
               / (
                  2.0F
                     * f2
                     * f2
               )
         )
      );
   }

   private void renderGlow(
      MatrixStack matrices
   ) {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(770, 1);
      RenderSystem.disableDepthTest();
      int i = mc.getWindow()
         .getScaledWidth();
      int i2 = mc.getWindow()
         .getScaledHeight();
      RenderSystem.setShaderTexture(0, this.glowBuffer.getColorAttachment());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      GL11.glTexParameteri(
         3553,
         10241,
         9729
      );
      GL11.glTexParameteri(
         3553,
         10240,
         9729
      );
      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
      float width = (float)this.glowColor
            .getRed()
         / 255.0F;
      float width2 = (float)this.glowColor
            .getGreen()
         / 255.0F;
      float width3 = (float)this.glowColor
            .getBlue()
         / 255.0F;
      float width4 = (float)this.glowColor
            .getAlpha()
         / 255.0F
         * this.glowIntensity;
      float width5 = this.glowRadius
         * 0.4F;
      float[] floats = new float[6];
      float width6 = 0.0F;

      for (int i3 = 0;
         i3
            < 6;
         i3++
      ) {
         float width7 = this.glowRadius
            * (float)(
               i3
                  + 1
            )
            / 6.0F;
         floats[i3] = this.gaussian(
            width7,
            width5
         );
         width6 += floats[i3];
      }

      for (int i4 = 0;
         i4
            < 6;
         i4++
      ) {
         floats[i4] /= width6;
      }

      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (int i5 = 0;
         i5
            < 6;
         i5++
      ) {
         float width8 = this.glowRadius
            * (float)(
               i5
                  + 1
            )
            / 6.0F;
         float width9 = width4
            * floats[i5]
            * 0.7F;
         if (!(
            width9
               < 0.001F
         )) {
            width9 = Math.min(
               width9,
               1.0F
            );

            for (int i6 = 0;
               i6
                  < 12;
               i6++
            ) {
               float width10 = (float)(
                     (double)i6
                        * 2.0
                        * Math.PI
                  )
                  / 12.0F;
               float width11 = (float)Math.cos(
                     (double)width10
                  )
                  * width8;
               float width12 = (float)Math.sin(
                     (double)width10
                  )
                  * width8;
               buffer.vertex(
                     matrix,
                     width11,
                     width12,
                     0.0F
                  )
                  .texture(0.0F, 1.0F)
                  .color(
                     width,
                     width2,
                     width3,
                     width9
                  );
               buffer.vertex(
                     matrix,
                     width11,
                     (float)i2
                        + width12,
                     0.0F
                  )
                  .texture(0.0F, 0.0F)
                  .color(
                     width,
                     width2,
                     width3,
                     width9
                  );
               buffer.vertex(
                     matrix,
                     (float)i
                        + width11,
                     (float)i2
                        + width12,
                     0.0F
                  )
                  .texture(1.0F, 0.0F)
                  .color(
                     width,
                     width2,
                     width3,
                     width9
                  );
               buffer.vertex(
                     matrix,
                     (float)i
                        + width11,
                     width12,
                     0.0F
                  )
                  .texture(1.0F, 1.0F)
                  .color(
                     width,
                     width2,
                     width3,
                     width9
                  );
               if (i5
                  > 0) {
                  float width13 = (float)(
                        (
                              (double)i6
                                 + 0.5
                           )
                           * 2.0
                           * Math.PI
                     )
                     / 12.0F;
                  float width14 = width8
                     * 0.6F;
                  float width15 = (float)Math.cos(
                        (double)width13
                     )
                     * width14;
                  float width16 = (float)Math.sin(
                        (double)width13
                     )
                     * width14;
                  float width17 = width9
                     * 0.5F;
                  buffer.vertex(
                        matrix,
                        width15,
                        width16,
                        0.0F
                     )
                     .texture(0.0F, 1.0F)
                     .color(
                        width,
                        width2,
                        width3,
                        width17
                     );
                  buffer.vertex(
                        matrix,
                        width15,
                        (float)i2
                           + width16,
                        0.0F
                     )
                     .texture(0.0F, 0.0F)
                     .color(
                        width,
                        width2,
                        width3,
                        width17
                     );
                  buffer.vertex(
                        matrix,
                        (float)i
                           + width15,
                        (float)i2
                           + width16,
                        0.0F
                     )
                     .texture(1.0F, 0.0F)
                     .color(
                        width,
                        width2,
                        width3,
                        width17
                     );
                  buffer.vertex(
                        matrix,
                        (float)i
                           + width15,
                        width16,
                        0.0F
                     )
                     .texture(1.0F, 1.0F)
                     .color(
                        width,
                        width2,
                        width3,
                        width17
                     );
               }
            }
         }
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      GL11.glTexParameteri(
         3553,
         10241,
         9728
      );
      GL11.glTexParameteri(
         3553,
         10240,
         9728
      );
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
   }

   public static void startGlow(
      float f,
      int n,
      IClientAction_QR6v5Fi4 val,
      MatrixStack matrices
   ) {
      startGlow(
         f,
         1.0F,
         n,
         val,
         matrices
      );
   }

   public static void startGlow(
      float f,
      float f2,
      int n,
      IClientAction_QR6v5Fi4 val,
      MatrixStack matrices
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
      if (color
         == 0) {
         color = 255;
      }

      GlowRenderer val2 = getInstance();
      val2.begin(
         f,
         f2,
         new Color(
            color2,
            color3,
            color4,
            color
         )
      );
      val.render();
      val2.end(
         matrices,
         val
      );
   }

   public void cleanup() {
      if (this.glowBuffer != null) {
         this.glowBuffer.delete();
         this.glowBuffer = null;
      }
   }
}
