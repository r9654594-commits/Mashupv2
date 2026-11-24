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
import lombok.Generated;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.lwjgl.opengl.GL30;

public class KawaseBlur implements IMinecraft {
   private static KawaseBlur instance;
   private static Framebuffer buffer1;
   private static Framebuffer buffer2;
   private int lastWidth = -1;
   private int lastHeight = -1;
   private long lastUpdateTime = 0L;
   private boolean requestedThisFrame = true;
   private float blurOffset = 1.0F;
   private final int iterations = 4;

   public static KawaseBlur getInstance() {
      if (instance == null) {
         instance = new KawaseBlur();
      }

      return instance;
   }

   public void beginFrame() {
      boolean flag = this.requestedThisFrame;
      this.requestedThisFrame = false;
      if (flag
         )
       {
         this.draw();
      }
   }

   public void request() {
      this.requestedThisFrame = true;
   }

   private void draw() {
      long time = System.currentTimeMillis();
      if (time
            - this.lastUpdateTime
         >= 16L) {
         this.lastUpdateTime = time;
         int i = mc.getWindow()
            .getFramebufferWidth();
         int i2 = mc.getWindow()
            .getFramebufferHeight();
         if (buffer1 == null
            || buffer2 == null
            || this.lastWidth
               != i
            || this.lastHeight
               != i2
            )
          {
            if (buffer1 != null) {
               buffer1.delete();
            }

            if (buffer2 != null) {
               buffer2.delete();
            }

            buffer1 = new SimpleFramebuffer(
               i,
               i2,
               false
            );
            buffer2 = new SimpleFramebuffer(
               i,
               i2,
               false
            );
            this.setLinearFiltering(buffer1);
            this.setLinearFiltering(buffer2);
            this.lastWidth = i;
            this.lastHeight = i2;
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         ShaderProgram val = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.kawaseDown);
         ShaderProgram val2 = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.kawaseUp);
         buffer1.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         buffer1.clear();
         buffer1.beginWrite(true);
         RenderSystem.setShader(ClientShaders.kawaseDown);
         mc.getFramebuffer().beginRead();
         RenderSystem.setShaderTexture(0, mc.getFramebuffer().getColorAttachment());
         this.setKawaseUniforms(
            val,
            i,
            i2
         );
         this.drawQuad((float)mc.getWindow().getScaledWidth(), (float)mc.getWindow().getScaledHeight());
         mc.getFramebuffer().endRead();
         buffer1.endWrite();
         Framebuffer[] var10000 = new Framebuffer[2];
         var10000[0] = buffer1;
         var10000[1] = buffer2;
         Framebuffer[] val3 = var10000;

         for (int i3 = 1;
            i3
               < 4;
            i3++
         ) {
            int i4 = (
                  i3
                     + 1
               )
               % 2;
            int i5 = i3
               % 2;
            Framebuffer val4 = val3[i4];
            Framebuffer val5 = val3[i5];
            val5.setClearColor(
               0.0F, 0.0F, 0.0F, 0.0F
            );
            val5.clear();
            val5.beginWrite(
               true
            );
            RenderSystem.setShader(ClientShaders.kawaseDown);
            val4.beginRead();
            RenderSystem.setShaderTexture(
               0,
               val4.getColorAttachment()
            );
            this.setKawaseUniforms(
               val,
               val4.textureWidth,
               val4.textureHeight
            );
            this.drawQuad((float)mc.getWindow().getScaledWidth(), (float)mc.getWindow().getScaledHeight());
            val4.endRead();
            val5.endWrite();
         }

         for (int i6 = 0;
            i6
               < 4;
            i6++
         ) {
            int i7 = i6
               % 2;
            int i8 = (
                  i6
                     + 1
               )
               % 2;
            Framebuffer val6 = val3[i7];
            Framebuffer val7 = val3[i8];
            val7.setClearColor(
               0.0F, 0.0F, 0.0F, 0.0F
            );
            val7.clear();
            val7.beginWrite(
               true
            );
            RenderSystem.setShader(ClientShaders.kawaseUp);
            val6.beginRead();
            RenderSystem.setShaderTexture(
               0,
               val6.getColorAttachment()
            );
            this.setKawaseUniforms(
               val2,
               val6.textureWidth,
               val6.textureHeight
            );
            this.drawQuad((float)mc.getWindow().getScaledWidth(), (float)mc.getWindow().getScaledHeight());
            val6.endRead();
            val7.endWrite();
         }

         RenderSystem.disableBlend();
         mc.getFramebuffer().beginWrite(true);
         RenderSystem.setShaderTexture(0, 0);
      }
   }

   private void setLinearFiltering(
      Framebuffer val
   ) {
      RenderSystem.bindTexture(
         val.getColorAttachment()
      );
      GL30.glTexParameteri(
         3553,
         10241,
         9729
      );
      GL30.glTexParameteri(
         3553,
         10240,
         9729
      );
      RenderSystem.bindTexture(0);
   }

   private void setKawaseUniforms(
      ShaderProgram val,
      int n,
      int n2
   ) {
      GlUniform val2 = val.getUniform(
         "Resolution"
      );
      GlUniform val3 = val.getUniform(
         "Offset"
      );
      GlUniform val4 = val.getUniform(
         "Saturation"
      );
      GlUniform val5 = val.getUniform(
         "TintIntensity"
      );
      GlUniform val6 = val.getUniform(
         "TintColor"
      );
      if (val2
         != null) {
         val2.set(
            1.0F
               / (float)n,
            1.0F
               / (float)n2
         );
      }

      if (val3
         != null) {
         val3.set(
            this.blurOffset
         );
      }

      if (val4
         != null) {
         val4.set(
            1.0F
         );
      }

      if (val5
         != null) {
         val5.set(
            0.0F
         );
      }

      if (val6
         != null) {
         val6.set(
            1.0F, 1.0F, 1.0F
         );
      }
   }

   private void drawQuad(
      float f,
      float f2
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      buffer.vertex(
            0.0F, 0.0F, 0.0F
         )
         .texture(0.0F, 1.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            0.0F,
            f2,
            0.0F
         )
         .texture(0.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            f,
            f2,
            0.0F
         )
         .texture(1.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            f,
            0.0F,
            0.0F
         )
         .texture(1.0F, 1.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   public static int getTexture() {
      getInstance().request();
      return buffer1 != null ? buffer1.getColorAttachment() : 0;
   }

   @Generated
   public static Framebuffer getBuffer1() {
      return buffer1;
   }

   @Generated
   public static Framebuffer getBuffer2() {
      return buffer2;
   }

   @Generated
   public void setBlurOffset(
      float f
   ) {
      this.blurOffset = f;
   }
}
