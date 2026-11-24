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
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public final class RenderskyHelper implements IMinecraft {
   public static final RenderskyHelper INSTANCE = new RenderskyHelper();

   private RenderskyHelper() {
   }

   public void renderSky() {
      WorldTweaks val = WorldTweaks.INSTANCE;
      if (val
            != null
         && val.isShaderEnabled()
         )
       {
         String var3 = val.getShaderMode();
         int color = -1;
         switch (var3.hashCode()) {
            case -2073142414:
               if (var3.equals("Caustic")) {
                  color = 0;
               }
               break;
            case -1901891230:
               if (var3.equals("Plasma")) {
                  color = 3;
               }
               break;
            case -1807340593:
               if (var3.equals("Summer")) {
                  color = 2;
               }
               break;
            case 80085222:
               if (var3.equals("Space")) {
                  color = 1;
               }
               break;
            case 1325473565:
               if (var3.equals("Balatro")) {
                  color = 4;
               }
         }
         ShaderProgramKey val2 = switch (color) {
            case 0 -> ClientShaders.skyCaustic;
            case 1 -> ClientShaders.skySpace;
            case 2 -> ClientShaders.skySummer;
            case 3 -> ClientShaders.skyPlasma;
            case 4 -> ClientShaders.skyBalatro;
            default -> ClientShaders.skyNebula;
         };
         ShaderProgram val3 = mc.getShaderLoader()
            .getOrCreateProgram(
               val2
            );
         if (val3
            != null) {
            color = val.getShaderColor();
            float x = ColorUtil.rgba(
                  color
               )[0]
               / 255.0F;
            float x2 = ColorUtil.rgba(
                  color
               )[1]
               / 255.0F;
            float x3 = ColorUtil.rgba(
                  color
               )[2]
               / 255.0F;
            float x4 = Math.max(
               0.0F,
               Math.min(
                  1.0F,
                  val.getShaderOpacity()
               )
            );
            GlUniform val4 = val3.getUniform(
               "Time"
            );
            if (val4
               != null) {
               val4.set(
                  (float)(System.currentTimeMillis() % 1000000L) / 1000.0F
               );
            }

            GlUniform val5 = val3.getUniform(
               "Accent"
            );
            if (val5
               != null) {
               val5.set(
                  x,
                  x2,
                  x3
               );
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.setShaderColor(
               1.0F,
               1.0F,
               1.0F,
               x4
            );
            RenderSystem.setShader(
               val2
            );
            BufferBuilder buffer = Tessellator.getInstance()
               .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            this.drawSkybox(
               buffer,
               new Matrix4f(),
               100.0F
            );
            BufferRenderer.drawWithGlobalProgram(
               buffer.end()
            );
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
         }
      }
   }

   public void close() {
   }

   private void drawSkybox(
      VertexConsumer consumer,
      Matrix4f matrix,
      float f
   ) {
      this.face(
         consumer,
         matrix,
         1,
         -f,
         f,
         -f,
         -f,
         f,
         f,
         f,
         f,
         f,
         f,
         f,
         -f
      );
      this.face(
         consumer,
         matrix,
         0,
         -f,
         -f,
         f,
         -f,
         -f,
         -f,
         f,
         -f,
         -f,
         f,
         -f,
         f
      );
      this.face(
         consumer,
         matrix,
         2,
         f,
         f,
         -f,
         f,
         -f,
         -f,
         -f,
         -f,
         -f,
         -f,
         f,
         -f
      );
      this.face(
         consumer,
         matrix,
         4,
         -f,
         f,
         f,
         -f,
         -f,
         f,
         f,
         -f,
         f,
         f,
         f,
         f
      );
      this.face(
         consumer,
         matrix,
         3,
         -f,
         f,
         -f,
         -f,
         -f,
         -f,
         -f,
         -f,
         f,
         -f,
         f,
         f
      );
      this.face(
         consumer,
         matrix,
         5,
         f,
         f,
         f,
         f,
         -f,
         f,
         f,
         -f,
         -f,
         f,
         f,
         -f
      );
   }

   private void face(
      VertexConsumer consumer,
      Matrix4f matrix,
      int n,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      float f12
   ) {
      int color2 = n
         % 3;
      int color3 = n
         / 3;
      float x = (float)color2
         / 3.0F;
      float x2 = (float)(
            color2
               + 1
         )
         / 3.0F;
      float x3 = (float)color3
         / 2.0F;
      float x4 = (float)(
            color3
               + 1
         )
         / 2.0F;
      consumer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .texture(
            x,
            x3
         )
         .color(
            255,
            255,
            255,
            255
         );
      consumer.vertex(
            matrix,
            f4,
            f5,
            f6
         )
         .texture(
            x,
            x4
         )
         .color(
            255,
            255,
            255,
            255
         );
      consumer.vertex(
            matrix,
            f7,
            f8,
            f9
         )
         .texture(
            x2,
            x4
         )
         .color(
            255,
            255,
            255,
            255
         );
      consumer.vertex(
            matrix,
            f10,
            f11,
            f12
         )
         .texture(
            x2,
            x3
         )
         .color(
            255,
            255,
            255,
            255
         );
   }
}
