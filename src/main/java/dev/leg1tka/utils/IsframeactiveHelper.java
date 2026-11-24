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


import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.AspectDLC;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public final class IsframeactiveHelper implements IMinecraft {
   public static final IsframeactiveHelper INSTANCE = new IsframeactiveHelper();
   private final HasmaskHelper shader = new HasmaskHelper();
   private Framebuffer maskBuffer;
   private int fbWidth = -1;
   private int fbHeight = -1;
   private boolean frameActive;
   private boolean hasMask;

   private IsframeactiveHelper() {
   }

   public boolean isFrameActive() {
      return this.frameActive;
   }

   public void beginFrame() {
      this.ensureMaskBuffer();
      if (this.maskBuffer != null) {
         this.frameActive = true;
         this.hasMask = false;
         this.maskBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         this.maskBuffer.clear();
      }
   }

   public void appendMask(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8
   ) {
      if (this.frameActive
         && this.maskBuffer != null
         && !(
            f3
               <= 0.5F
         )
         && !(
            f4
               <= 0.5F
         )) {
         this.maskBuffer.beginWrite(false);
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         Render2D.drawRoundedRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            f6,
            f7,
            f8,
            -1
         );
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         mc.getFramebuffer().beginWrite(false);
         this.hasMask = true;
         Render2D.resetGuiRenderState();
      }
   }

   public void renderPanelOverlay(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      HudRectStyle style
   ) {
      if (this.frameActive
         && this.hasMask
         && this.maskBuffer != null
         && !(
            f3
               <= 0.5F
         )
         && !(
            f4
               <= 0.5F
         )) {
         int color = this.resolveThemeColor1();
         int color2 = this.resolveThemeColor2();
         float alpha = this.resolveHudAlpha();
         HudRectStyle style2 = style
               != null
            ? style
            : HudRectStyle.SHADER;
         Color1Helper color3 = style2
               == HudRectStyle.THREADS
            ? Color1Helper.hudThreads(
               color,
               color2,
               alpha
            )
            : Color1Helper.hud(
               color,
               color2,
               alpha
            );
         this.renderPanelOverlay(
            matrices,
            f,
            f2,
            f3,
            f4,
            this.maskBuffer,
            color3,
            false
         );
      }
   }

   private void renderPanelOverlay(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      Framebuffer val,
      Color1Helper color3,
      boolean flag
   ) {
      ShaderProgram val2 = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.blockOverlay);
      if (val2
         != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(ClientShaders.blockOverlay);
         RenderSystem.setShaderTexture(
            0,
            val.getColorAttachment()
         );
         float width = (float)Math.max(
            1, mc.getWindow().getFramebufferWidth()
         );
         float width2 = (float)Math.max(
            1, mc.getWindow().getFramebufferHeight()
         );
         float width3 = (float)mc.getWindow()
            .getScaleFactor();
         float width4 = f
            * width3;
         float width5 = f2
            * width3;
         float width6 = f3
            * width3;
         float width7 = f4
            * width3;
         float width8 = width4
            / width;
         float width9 = 1.0F
            - width5
               / width2;
         float width10 = (
               width4
                  + width6
            )
            / width;
         float width11 = 1.0F
            - (
                  width5
                     + width7
               )
               / width2;
         GlUniform val3 = val2.getUniform(
            "texelSize"
         );
         if (val3
            != null) {
            val3.set(
               1.0F
                  / width,
               1.0F
                  / width2
            );
         }

         val3 = val2.getUniform(
            "color"
         );
         if (val3
            != null) {
            val3.set(
               ColorUtil.redf(
                  color3.color1()
               ),
               ColorUtil.greenf(
                  color3.color1()
               ),
               ColorUtil.bluef(
                  color3.color1()
               )
            );
         }

         val3 = val2.getUniform(
            "color2"
         );
         if (val3
            != null) {
            val3.set(
               ColorUtil.redf(
                  color3.color2()
               ),
               ColorUtil.greenf(
                  color3.color2()
               ),
               ColorUtil.bluef(
                  color3.color2()
               )
            );
         }

         val3 = val2.getUniform(
            "time"
         );
         if (val3
            != null) {
            val3.set(
               (float)(System.currentTimeMillis() % 100000L) / 1000.0F
            );
         }

         val3 = val2.getUniform(
            "speed"
         );
         if (val3
            != null) {
            val3.set(
               color3.waveSpeed()
            );
         }

         val3 = val2.getUniform(
            "scale"
         );
         if (val3
            != null) {
            val3.set(
               color3.waveScale()
            );
         }

         val3 = val2.getUniform(
            "outline"
         );
         if (val3
            != null) {
            val3.set(
               color3.outline()
            );
         }

         val3 = val2.getUniform(
            "glow"
         );
         if (val3
            != null) {
            val3.set(
               color3.glow()
            );
         }

         val3 = val2.getUniform(
            "fill"
         );
         if (val3
            != null) {
            val3.set(
               color3.fill()
            );
         }

         val3 = val2.getUniform(
            "alpha"
         );
         if (val3
            != null) {
            val3.set(
               color3.alpha()
            );
         }

         val3 = val2.getUniform(
            "outlineOnly"
         );
         if (val3
            != null) {
            val3.set(
               flag
                  ? 1.0F
                  : 0.0F
            );
         }

         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(
               matrix,
               f,
               f2,
               0.0F
            )
            .texture(
               width8,
               width9
            )
            .color(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.vertex(
               matrix,
               f,
               f2
                  + f4,
               0.0F
            )
            .texture(
               width8,
               width11
            )
            .color(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2
                  + f4,
               0.0F
            )
            .texture(
               width10,
               width11
            )
            .color(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2,
               0.0F
            )
            .texture(
               width10,
               width9
            )
            .color(1.0F, 1.0F, 1.0F, 1.0F);
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         Render2D.resetGuiRenderState();
      }
   }

   public void appendMask(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5
   ) {
      this.appendMask(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f5,
         f5,
         f5
      );
   }

   public void finishFrame() {
      this.frameActive = false;
      this.hasMask = false;
   }

   public void destroy() {
      this.frameActive = false;
      this.hasMask = false;
      this.shader.destroy();
      if (this.maskBuffer != null) {
         this.maskBuffer.delete();
         this.maskBuffer = null;
      }

      this.fbWidth = -1;
      this.fbHeight = -1;
   }

   private void ensureMaskBuffer() {
      int width = mc.getWindow()
         .getFramebufferWidth();
      int width2 = mc.getWindow()
         .getFramebufferHeight();
      if (this.maskBuffer == null
         || this.fbWidth
            != width
         || this.fbHeight
            != width2
         )
       {
         if (this.maskBuffer != null) {
            this.maskBuffer.delete();
         }

         this.maskBuffer = new SimpleFramebuffer(
            width,
            width2,
            false
         );
         this.fbWidth = width;
         this.fbHeight = width2;
      }
   }

   private float resolveHudAlpha() {
      return 0.5882353F;
   }

   private int resolveThemeColor1() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null) {
         ThemeColors color2 = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         if (color2
               != null
            && color2.color
               != null
            && color2.color.length
               != 0) {
            return !"Rainbow"
                  .equals(
                     color2.getName()
                  )
               ? color2.color[0]
               : ColorUtil.getThemeColor();
         } else {
            return ColorUtil.getThemeColor(0);
         }
      } else {
         return ColorUtil.getThemeColor(0);
      }
   }

   private int resolveThemeColor2() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null) {
         ThemeColors color2 = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         if (color2
            == null) {
            return ColorUtil.getThemeColor(180);
         } else {
            if ("Rainbow"
               .equals(
                  color2.getName()
               )) {
               return ColorUtil.getThemeColor(180);
            } else {
               return color2.color
                        != null
                     && color2.color.length
                        > 0
                  ? color2.color[0]
                  : ColorUtil.getThemeColor(0);
            }
         }
      } else {
         return ColorUtil.getThemeColor(180);
      }
   }
}
