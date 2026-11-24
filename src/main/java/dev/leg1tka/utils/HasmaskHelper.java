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
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public final class HasmaskHelper implements IMinecraft {
   private Framebuffer maskBuffer;
   private int fbWidth = -1;
   private int fbHeight = -1;
   private boolean hasMask;

   public boolean hasMask() {
      return this.hasMask;
   }

   public void clearMask() {
      this.hasMask = false;
   }

   public void destroy() {
      this.clearMask();
      if (this.maskBuffer != null) {
         this.maskBuffer.delete();
         this.maskBuffer = null;
      }

      this.fbWidth = -1;
      this.fbHeight = -1;
   }

   public void writeMask(
      Matrix4f matrix,
      Box box
   ) {
      this.ensureMaskBuffer();
      if (this.maskBuffer != null) {
         this.hasMask = true;
         this.maskBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         this.maskBuffer.clear();
         this.copyMainDepthToMask();
         this.maskBuffer.beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         this.drawMaskBox(
            matrix,
            box
         );
         RenderSystem.depthMask(true);
         RenderSystem.disableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         mc.getFramebuffer().beginWrite(false);
      }
   }

   public void renderOverlay(
      Color1Helper color
   ) {
      if (this.hasMask && this.maskBuffer != null) {
         this.renderOverlayFromMask(
            this.maskBuffer,
            color
         );
      }
   }

   public void renderOverlayFromMask(
      Framebuffer val,
      Color1Helper color3
   ) {
      if (val
            != null
         && val.getColorAttachment()
            != 0) {
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
            this.setUniform(
               val2,
               "texelSize",
               1.0F / (float)Math.max(1, mc.getWindow().getFramebufferWidth()),
               1.0F / (float)Math.max(1, mc.getWindow().getFramebufferHeight())
            );
            this.setUniform(
               val2,
               "color",
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
            this.setUniform(
               val2,
               "color2",
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
            this.setUniform(
               val2,
               "time",
               (float)(System.currentTimeMillis() % 100000L) / 1000.0F
            );
            this.setUniform(
               val2,
               "speed",
               color3.waveSpeed()
            );
            this.setUniform(
               val2,
               "scale",
               color3.waveScale()
            );
            this.setUniform(
               val2,
               "outline",
               color3.outline()
            );
            this.setUniform(
               val2,
               "glow",
               color3.glow()
            );
            this.setUniform(
               val2,
               "fill",
               color3.fill()
            );
            this.setUniform(
               val2,
               "alpha",
               color3.alpha()
            );
            this.setUniform(
               val2,
               "outlineOnly",
               0.0F
            );
            this.drawFullscreenQuad();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderTexture(0, 0);
         }
      }
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
            true
         );
         this.fbWidth = width;
         this.fbHeight = width2;
      }
   }

   private void copyMainDepthToMask() {
      if (this.maskBuffer != null) {
         int width = GL11.glGetInteger(
            36010
         );
         int width2 = GL11.glGetInteger(
            36006
         );
         int width3 = mc.getWindow()
            .getFramebufferWidth();
         int width4 = mc.getWindow()
            .getFramebufferHeight();
         GL30.glBindFramebuffer(36008, mc.getFramebuffer().fbo);
         GL30.glBindFramebuffer(36009, this.maskBuffer.fbo);
         GL30.glBlitFramebuffer(
            0,
            0,
            width3,
            width4,
            0,
            0,
            width3,
            width4,
            256,
            9728
         );
         GL30.glBindFramebuffer(
            36008,
            width
         );
         GL30.glBindFramebuffer(
            36009,
            width2
         );
      }
   }

   private void drawMaskBox(
      Matrix4f matrix,
      Box box
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      int color2 = -1;
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.minY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.minY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.minY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.minY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.maxY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.maxY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.maxY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.maxY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.minY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.minY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.maxY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.maxY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.minY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.maxY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.maxY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.minY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.minY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.maxY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.maxY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.minX,
            (float)box.minY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.minY,
            (float)box.minZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.minY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.maxY,
            (float)box.maxZ
         )
         .color(
            color2
         );
      buffer.vertex(
            matrix,
            (float)box.maxX,
            (float)box.maxY,
            (float)box.minZ
         )
         .color(
            color2
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void drawFullscreenQuad() {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float width = (float)Math.max(
         mc.getWindow().getScaledWidth(), 1
      );
      float width2 = (float)Math.max(
         mc.getWindow().getScaledHeight(), 1
      );
      buffer.vertex(
            0.0F, 0.0F, 0.0F
         )
         .texture(0.0F, 1.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            0.0F,
            width2,
            0.0F
         )
         .texture(0.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            width,
            width2,
            0.0F
         )
         .texture(1.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            width,
            0.0F,
            0.0F
         )
         .texture(1.0F, 1.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void setUniform(
      ShaderProgram val,
      String str,
      float f
   ) {
      GlUniform val2 = val.getUniform(
         str
      );
      if (val2
         != null) {
         val2.set(
            f
         );
      }
   }

   private void setUniform(
      ShaderProgram val,
      String str,
      float f,
      float f2
   ) {
      GlUniform val2 = val.getUniform(
         str
      );
      if (val2
         != null) {
         val2.set(
            f,
            f2
         );
      }
   }

   private void setUniform(
      ShaderProgram val,
      String str,
      float f,
      float f2,
      float f3
   ) {
      GlUniform val2 = val.getUniform(
         str
      );
      if (val2
         != null) {
         val2.set(
            f,
            f2,
            f3
         );
      }
   }
}
