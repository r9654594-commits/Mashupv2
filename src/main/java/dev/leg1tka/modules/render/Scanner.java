package dev.leg1tka.modules.render;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventChunkReload;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Scanner extends Module {
   public static Scanner INSTANCE = new Scanner();
   private final NumberSetting dur = new NumberSetting("Длительность", 5.6F, 0.8F, 10.0F, 0.1F);
   private final NumberSetting alp = new NumberSetting("Яркость", 1.0F, 0.1F, 1.0F, 0.01F);
   private final NumberSetting wid = new NumberSetting("Ширина", 1.0F, 0.35F, 2.2F, 0.05F);
   private final NumberSetting shp = new NumberSetting("Резкость", 24.0F, 4.0F, 80.0F, 1.0F);
   private Framebuffer buf;
   private int fbw = -1;
   private int fbh = -1;
   private long start;
   private Vec3d ctr = Vec3d.ZERO;

   public Scanner() {
      super(
         "Scanner",
         "Сканирует новые чанки",
         Category.RENDER
      );
      this.addSettings(new Setting[]{
         this.dur,
         this.alp,
         this.wid,
         this.shp
      });
   }

   @Override
   public void onEnable() {
      if (mc.player != null) {
         this.ping(mc.player.getPos());
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.start = 0L;
      this.delBuf();
      super.onDisable();
   }

   @EventLink
   public void onChunkReload(
      EventChunkReload val25
   ) {
      if (mc.player != null) {
         this.ping(mc.player.getPos());
      }
   }

   public void renderFromMixin(
      Matrix4f val,
      Matrix4f val2,
      Vec3d val3
   ) {
      if (mc.player != null && mc.world != null && this.start > 0L) {
         float f = this.dur
               .get()
            * 1000.0F;
         float f2 = (float)(
            System.currentTimeMillis() - this.start
         );
         if (f2
            >= f
            )
          {
            this.start = 0L;
         } else {
            Framebuffer val4 = mc.getFramebuffer();
            this.ensBuf(
               val4.textureWidth,
               val4.textureHeight
            );
            if (this.buf != null) {
               this.buf
                  .copyDepthFrom(
                     val4
                  );
               Matrix4f matrix = new Matrix4f(
                     val
                  )
                  .invert();
               Matrix4f matrix2 = new Matrix4f(
                     val2
                  )
                  .invert();
               float f3 = mc.gameRenderer
                  .getFarPlaneDistance();
               float f4 = MathHelper.clamp(
                  f2
                     / f,
                  0.0F,
                  1.0F
               );
               float f5 = this.lerp(
                  1.0F,
                  f3,
                  (float)EasingFunctions.QUINT_OUT
                     .ease(
                        (double)f4
                     )
               );
               float f6 = this.lerp(
                  1.0F,
                  f3,
                  (float)EasingFunctions.QUART_IN_OUT
                     .ease(
                        (double)f4
                     )
               );
               float f7 = MathHelper.lerp(
                  0.85F,
                  f5,
                  f6
               );
               float f8 = 1.0F
                  - f4;
               float f9 = (
                     f8
                           > 0.5F
                        ? 1.0F
                           - f8
                        : f8
                  )
                  * 2.0F;
               f9 = Math.min(
                  f9
                     * 1.75F,
                  1.0F
               );
               float f10 = MathHelper.clamp(
                  this.alp.get()
                     * f9,
                  0.0F,
                  1.0F
               );
               int color = ColorUtil.getThemeColor(
                  0
               );
               int color2 = ColorUtil.getThemeColor(
                  90
               );
               int color3 = ColorUtil.getThemeColor(
                  180
               );
               int color4 = ColorUtil.getThemeColor(
                  270
               );
               float f11 = MathHelper.clamp(
                  6.0F
                     + f7
                        * 0.18F
                        * this.wid.get(),
                  4.0F,
                  Math.max(
                     10.0F,
                     f3
                        * 0.42F
                  )
               );
               float f12 = this.shp
                  .get();
               this.renderPass(
                  matrix,
                  matrix2,
                  val3,
                  val4,
                  f7,
                  f11,
                  f12,
                  this.applyAlpha(
                     color,
                     f10
                  ),
                  this.applyAlpha(
                     color2,
                     f10
                  ),
                  this.applyAlpha(
                     color3,
                     f10
                  ),
                  this.applyAlpha(
                     color4,
                     f10
                  )
               );
               RenderSystem.defaultBlendFunc();
            }
         }
      }
   }

   private void renderPass(
      Matrix4f val,
      Matrix4f val2,
      Vec3d val3,
      Framebuffer val4,
      float val5,
      float val6,
      float val7,
      int val8,
      int val9,
      int val10,
      int val11
   ) {
      if (!(
            val5
               <= 0.001F
         )
         && !(
            val6
               <= 0.001F
         )) {
         ShaderProgram val12 = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.scanEffect);
         GlUniform val13 = val12.getUniform(
            "invViewMat"
         );
         GlUniform val14 = val12.getUniform(
            "invProjMat"
         );
         GlUniform val15 = val12.getUniform(
            "pos"
         );
         GlUniform val16 = val12.getUniform(
            "center"
         );
         GlUniform val17 = val12.getUniform(
            "radius"
         );
         GlUniform val18 = val12.getUniform(
            "width"
         );
         GlUniform val19 = val12.getUniform(
            "sharpness"
         );
         GlUniform val20 = val12.getUniform(
            "outerColor"
         );
         GlUniform val21 = val12.getUniform(
            "midColor"
         );
         GlUniform val22 = val12.getUniform(
            "innerColor"
         );
         GlUniform val23 = val12.getUniform(
            "scanlineColor"
         );
         GlUniform val24 = val12.getUniform(
            "DebugMode"
         );
         if (val13
            != null) {
            val13.set(
               val
            );
         }

         if (val14
            != null) {
            val14.set(
               val2
            );
         }

         if (val15
            != null) {
            val15.set(
               (float)val3.x,
               (float)val3.y,
               (float)val3.z
            );
         }

         if (val16
            != null) {
            val16.set(
               (float)this.ctr.x, (float)this.ctr.y, (float)this.ctr.z
            );
         }

         if (val17
            != null) {
            val17.set(
               val5
            );
         }

         if (val18
            != null) {
            val18.set(
               val6
            );
         }

         if (val19
            != null) {
            val19.set(
               val7
            );
         }

         if (val20
            != null) {
            this.setCol(
               val20,
               val8
            );
         }

         if (val21
            != null) {
            this.setCol(
               val21,
               val9
            );
         }

         if (val22
            != null) {
            this.setCol(
               val22,
               val10
            );
         }

         if (val23
            != null) {
            this.setCol(
               val23,
               val11
            );
         }

         if (val24
            != null) {
            val24.set(
               0
            );
         }

         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.disableDepthTest();
         RenderSystem.disableCull();
         RenderSystem.depthMask(false);
         int n = this.buf
            .getDepthAttachment();
         if (n
            == 0) {
            n = mc.getFramebuffer()
               .getDepthAttachment();
         }

         RenderSystem.bindTexture(
            n
         );
         GL11.glTexParameteri(
            3553,
            34892,
            0
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
         val4.beginWrite(
            false
         );
         RenderSystem.setShaderTexture(
            0,
            n
         );
         RenderSystem.setShader(ClientShaders.scanEffect);
         this.drawQuad();
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
      }
   }

   private void drawQuad() {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      buffer.vertex(
            -1.0F, -1.0F, 0.0F
         )
         .texture(0.0F, 0.0F);
      buffer.vertex(
            -1.0F, 1.0F, 0.0F
         )
         .texture(0.0F, 1.0F);
      buffer.vertex(
            1.0F, 1.0F, 0.0F
         )
         .texture(1.0F, 1.0F);
      buffer.vertex(
            1.0F, -1.0F, 0.0F
         )
         .texture(1.0F, 0.0F);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void ensBuf(
      int val,
      int val2
   ) {
      if (this.buf == null
         || this.fbw
            != val
         || this.fbh
            != val2
         )
       {
         this.delBuf();
         this.buf = new SimpleFramebuffer(
            val,
            val2,
            true
         );
         this.fbw = val;
         this.fbh = val2;
      }
   }

   private void delBuf() {
      if (this.buf != null) {
         this.buf.delete();
         this.buf = null;
      }

      this.fbw = -1;
      this.fbh = -1;
   }

   private void ping(
      Vec3d val
   ) {
      this.start = System.currentTimeMillis();
      this.ctr = val;
   }

   private void setCol(
      GlUniform val,
      int val2
   ) {
      int n = val2
            >> 24
         & 255;
      int n2 = val2
            >> 16
         & 255;
      int n3 = val2
            >> 8
         & 255;
      int n4 = val2
         & 255;
      if (n
         == 0) {
         n = 255;
      }

      val.set(
         (float)n2
            / 255.0F,
         (float)n3
            / 255.0F,
         (float)n4
            / 255.0F,
         (float)n
            / 255.0F
      );
   }

   private int applyAlpha(
      int val,
      float val2
   ) {
      int n = val
            >> 24
         & 255;
      if (n
         == 0) {
         n = 255;
      }

      n = (int)(
         (float)n
            * MathHelper.clamp(
               val2,
               0.0F,
               1.0F
            )
      );
      return val
            & 16777215
         | n
            << 24;
   }

   private float lerp(
      float val,
      float val2,
      float val3
   ) {
      return val
         + (
               val2
                  - val
            )
            * val3;
   }
}
