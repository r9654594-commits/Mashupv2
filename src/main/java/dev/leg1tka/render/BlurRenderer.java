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


import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
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

public class BlurRenderer implements IMinecraft {
   private static final Supplier<SimpleFramebuffer> CAPTURE = Suppliers.memoize(
      () -> {
         SimpleFramebuffer val = new SimpleFramebuffer(
            1920,
            1024,
            false
         );
         setLinear(
            val
         );
         return val;
      }
   );

   private static void setLinear(
      Framebuffer val
   ) {
      RenderSystem.bindTexture(
         val.getColorAttachment()
      );
      RenderSystem.texParameter(
         3553,
         10241,
         9729
      );
      RenderSystem.texParameter(
         3553,
         10240,
         9729
      );
      RenderSystem.bindTexture(0);
   }

   public void render(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6
   ) {
      if (!(
            f3
               <= 0.5F
         )
         && !(
            f4
               <= 0.5F
         )) {
         Framebuffer val = mc.getFramebuffer();
         if (val
               != null
            && val.getColorAttachment()
               > 0) {
            SimpleFramebuffer val2 = (SimpleFramebuffer)CAPTURE.get();
            if (val2.textureWidth
                  != val.textureWidth
               || val2.textureHeight
                  != val.textureHeight
               )
             {
               val2.resize(
                  val.textureWidth,
                  val.textureHeight
               );
               setLinear(
                  val2
               );
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            val2.beginWrite(
               false
            );
            val.draw(
               val2.textureWidth,
               val2.textureHeight
            );
            val.beginWrite(
               false
            );
            RenderSystem.setShaderTexture(
               0,
               val2.getColorAttachment()
            );
            ShaderProgram val3 = mc.getShaderLoader()
               .getOrCreateProgram(ClientShaders.blur);
            pushUniforms(
               val3,
               f3,
               f4,
               f5,
               f6
            );
            Matrix4f matrix = matrices.peek()
               .getPositionMatrix();
            BufferBuilder buffer = Tessellator.getInstance()
               .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buffer.vertex(
                  matrix,
                  f,
                  f2,
                  0.0F
               )
               .color(1.0F, 1.0F, 1.0F, 1.0F);
            buffer.vertex(
                  matrix,
                  f,
                  f2
                     + f4,
                  0.0F
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
               .color(1.0F, 1.0F, 1.0F, 1.0F);
            buffer.vertex(
                  matrix,
                  f
                     + f3,
                  f2,
                  0.0F
               )
               .color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(ClientShaders.blur);
            BufferRenderer.drawWithGlobalProgram(
               buffer.end()
            );
            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
         }
      }
   }

   private static void pushUniforms(
      ShaderProgram val,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      GlUniform val2 = val.getUniform(
         "Size"
      );
      GlUniform val3 = val.getUniform(
         "Radius"
      );
      GlUniform val4 = val.getUniform(
         "Smoothness"
      );
      GlUniform val5 = val.getUniform(
         "BlurRadius"
      );
      if (val2
         != null) {
         val2.set(
            f,
            f2
         );
      }

      if (val3
         != null) {
         val3.set(
            f3,
            f3,
            f3,
            f3
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
            f4
         );
      }
   }
}
