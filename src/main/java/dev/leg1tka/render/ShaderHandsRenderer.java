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
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.AspectDLC;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShaderHandsRenderer implements IMinecraft {
   private static final float EPSILON = 0.001F;
   private static ShaderHandsRenderer instance;
   private Framebuffer beforeBuffer;
   private Framebuffer afterBuffer;
   private Framebuffer maskBuffer;
   private final List<Framebuffer> bloomBuffers = new ArrayList<>();
   private int width = -1;
   private int height = -1;
   private boolean hasBeforeCapture;
   private boolean pendingComposite;
   private int configuredBeforeDepthTex = -1;
   private int configuredAfterDepthTex = -1;

   public static ShaderHandsRenderer getInstance() {
      if (instance == null) {
         instance = new ShaderHandsRenderer();
      }

      return instance;
   }

   public void captureBeforeHands() {
      ShaderHands hand = this.getModule();
      if (!this.isEffectEnabled(
         hand
      )) {
         this.invalidateState();
      } else {
         this.ensureBuffers();
         if (this.beforeBuffer != null) {
            this.copyMainFramebuffer(this.beforeBuffer);
            this.hasBeforeCapture = true;
         }
      }
   }

   public void captureAfterHands() {
      ShaderHands hand = this.getModule();
      if (!this.isEffectEnabled(
         hand
      )) {
         this.invalidateState();
      } else {
         this.ensureBuffers();
         if (this.beforeBuffer != null && this.afterBuffer != null && this.maskBuffer != null) {
            if (this.hasBeforeCapture) {
               this.copyMainFramebuffer(this.afterBuffer);
               this.pendingComposite = true;
            }
         }
      }
   }

   public void renderOverlayIfPending() {
      if (this.pendingComposite) {
         this.ensureBuffers();
         if (this.beforeBuffer != null && this.afterBuffer != null && this.maskBuffer != null) {
            ShaderHands hand = this.getModule();
            if (!this.isEffectEnabled(
               hand
            )) {
               this.invalidateState();
            } else {
               ShaderProgram val = mc.getShaderLoader()
                  .getOrCreateProgram(ClientShaders.shaderHandsMaskDiff);
               if (val
                  == null) {
                  this.invalidateState();
               } else {
                  this.maskBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                  this.maskBuffer.clear();
                  this.maskBuffer.beginWrite(false);
                  RenderSystem.disableDepthTest();
                  RenderSystem.disableBlend();
                  RenderSystem.setShader(ClientShaders.shaderHandsMaskDiff);
                  RenderSystem.setShaderTexture(0, this.beforeBuffer.getColorAttachment());
                  RenderSystem.setShaderTexture(1, this.afterBuffer.getColorAttachment());
                  int color3 = this.beforeBuffer
                     .getDepthAttachment();
                  int color4 = this.afterBuffer
                     .getDepthAttachment();
                  if (color3
                        != 0
                     && color3
                        != this.configuredBeforeDepthTex) {
                     this.configureDepthTexture(
                        color3
                     );
                     this.configuredBeforeDepthTex = color3;
                  }

                  if (color4
                        != 0
                     && color4
                        != this.configuredAfterDepthTex) {
                     this.configureDepthTexture(
                        color4
                     );
                     this.configuredAfterDepthTex = color4;
                  }

                  RenderSystem.setShaderTexture(
                     2,
                     color3
                  );
                  RenderSystem.setShaderTexture(
                     3,
                     color4
                  );
                  this.drawFullscreenQuad();
                  RenderSystem.enableDepthTest();
                  float alpha2 = hand.glow
                     .get();
                  float alpha3 = hand.fill
                     .get();
                  float alpha4 = hand.alpha
                     .get();
                  float alpha5 = hand.outline
                     .get();
                  boolean flag = alpha2
                     > 0.001F;
                  boolean flag2 = alpha3
                        > 0.001F
                     && alpha4
                        > 0.001F;
                  String var10000 = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName();
                  int color5 = var10000.equals(
                        "Rainbow"
                     )
                     ? ColorUtil.getThemeColor(0)
                     : AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0];
                  ModeSetting var17 = hand.mode;
                  if (var17.is("Красивый")) {
                     this.renderPrettyMode(
                        hand,
                        color5,
                        color5,
                        alpha2,
                        alpha3,
                        alpha4,
                        alpha5
                     );
                     this.invalidateState();
                  } else {
                     int color6 = 0;
                     if (flag
                        )
                      {
                        int color7 = Math.max(
                           3,
                           Math.min(
                              8,
                              4
                                 + Math.round(
                                    alpha5
                                       * 0.7F
                                 )
                           )
                        );
                        color6 = this.runKawaseBloom(
                           color7
                        );
                     }

                     mc.getFramebuffer().beginWrite(true);
                     RenderSystem.enableBlend();
                     RenderSystem.colorMask(true, true, true, false);
                     RenderSystem.disableDepthTest();
                     ShaderProgram val2 = flag
                        ? mc.getShaderLoader().getOrCreateProgram(ClientShaders.shaderHandsGlow)
                        : null;
                     if (val2
                        != null) {
                        RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
                        RenderSystem.setShader(ClientShaders.shaderHandsGlow);
                        RenderSystem.setShaderTexture(
                           0,
                           color6
                        );
                        RenderSystem.setShaderTexture(1, this.maskBuffer.getColorAttachment());
                        this.setUniform(
                           val2,
                           "color",
                           ColorUtil.redf(
                              color5
                           ),
                           ColorUtil.greenf(
                              color5
                           ),
                           ColorUtil.bluef(
                              color5
                           )
                        );
                        this.setUniform(
                           val2,
                           "color2",
                           ColorUtil.redf(
                              color5
                           ),
                           ColorUtil.greenf(
                              color5
                           ),
                           ColorUtil.bluef(
                              color5
                           )
                        );
                        this.setUniform(
                           val2,
                           "exposure",
                           1.0F
                              + alpha2
                                 * 1.8F
                        );
                        this.drawFullscreenQuad();
                     }

                     if (flag2
                        )
                      {
                        ShaderProgram val3 = mc.getShaderLoader()
                           .getOrCreateProgram(ClientShaders.shaderHandsOverlay);
                        if (val3
                           == null) {
                           this.restoreCompositeState();
                           this.invalidateState();
                           return;
                        }

                        RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
                        RenderSystem.setShader(ClientShaders.shaderHandsOverlay);
                        RenderSystem.setShaderTexture(0, this.maskBuffer.getColorAttachment());
                        this.setUniform(
                           val3,
                           "color",
                           ColorUtil.redf(
                              color5
                           ),
                           ColorUtil.greenf(
                              color5
                           ),
                           ColorUtil.bluef(
                              color5
                           )
                        );
                        this.setUniform(
                           val3,
                           "fill",
                           alpha3
                        );
                        this.setUniform(
                           val3,
                           "alpha",
                           alpha4
                        );
                        this.drawFullscreenQuad();
                     }

                     this.restoreCompositeState();
                     this.invalidateState();
                  }
               }
            }
         }
      }
   }

   public void invalidateState() {
      this.hasBeforeCapture = false;
      this.pendingComposite = false;
      this.configuredBeforeDepthTex = -1;
      this.configuredAfterDepthTex = -1;
   }

   private int runKawaseBloom(
      int n
   ) {
      this.ensureBloomBuffers(
         n
      );
      if (this.bloomBuffers.isEmpty()) {
         return this.maskBuffer.getColorAttachment();
      } else {
         int i = this.maskBuffer
            .getColorAttachment();
         ShaderProgram val = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.shaderHandsKawaseDown);
         ShaderProgram val2 = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.shaderHandsKawaseUp);
         if (val
               != null
            && val2
               != null) {
            for (int i2 = 0;
               i2
                  < n;
               i2++
            ) {
               Framebuffer val3 = this.bloomBuffers
                  .get(
                     i2
                  );
               val3.setClearColor(
                  0.0F, 0.0F, 0.0F, 0.0F
               );
               val3.clear();
               val3.beginWrite(
                  true
               );
               RenderSystem.setShader(ClientShaders.shaderHandsKawaseDown);
               RenderSystem.setShaderTexture(
                  0,
                  i
               );
               this.setHandsKawaseUniforms(
                  val,
                  val3.textureWidth,
                  val3.textureHeight,
                  1.0F
                     + (float)i2
               );
               this.drawFullscreenQuad();
               i = val3.getColorAttachment();
            }

            for (int i3 = n
                  - 1;
               i3
                  >= 1;
               i3--
            ) {
               Framebuffer val4 = this.bloomBuffers
                  .get(
                     i3
                        - 1
                  );
               val4.setClearColor(
                  0.0F, 0.0F, 0.0F, 0.0F
               );
               val4.clear();
               val4.beginWrite(
                  true
               );
               RenderSystem.setShader(ClientShaders.shaderHandsKawaseUp);
               RenderSystem.setShaderTexture(
                  0,
                  i
               );
               this.setHandsKawaseUniforms(
                  val2,
                  val4.textureWidth,
                  val4.textureHeight,
                  1.0F
                     + (float)i3
               );
               this.setUniform(
                  val2,
                  "color",
                  1.0F,
                  1.0F,
                  1.0F
               );
               this.drawFullscreenQuad();
               i = val4.getColorAttachment();
            }

            mc.getFramebuffer().beginWrite(true);
            return i;
         } else {
            return i;
         }
      }
   }

   private void copyMainFramebuffer(
      Framebuffer val
   ) {
      int width2 = GL11.glGetInteger(
         36010
      );
      int width3 = GL11.glGetInteger(
         36006
      );
      GL30.glBindFramebuffer(36008, mc.getFramebuffer().fbo);
      GL30.glBindFramebuffer(
         36009,
         val.fbo
      );
      GL30.glBlitFramebuffer(
         0,
         0,
         this.width,
         this.height,
         0,
         0,
         this.width,
         this.height,
         16640,
         9728
      );
      GL30.glBindFramebuffer(
         36008,
         width2
      );
      GL30.glBindFramebuffer(
         36009,
         width3
      );
      mc.getFramebuffer().beginWrite(true);
   }

   private void configureDepthTexture(
      int n
   ) {
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
      RenderSystem.bindTexture(0);
   }

   private void ensureBuffers() {
      int i = mc.getWindow()
         .getFramebufferWidth();
      int i2 = mc.getWindow()
         .getFramebufferHeight();
      if (i
            != this.width
         || i2
            != this.height
         || this.beforeBuffer == null
         || this.afterBuffer == null
         || this.maskBuffer == null) {
         if (this.beforeBuffer != null) {
            this.beforeBuffer.delete();
         }

         if (this.afterBuffer != null) {
            this.afterBuffer.delete();
         }

         if (this.maskBuffer != null) {
            this.maskBuffer.delete();
         }

         for (Framebuffer val : this.bloomBuffers) {
            val.delete();
         }

         this.bloomBuffers.clear();
         this.beforeBuffer = new SimpleFramebuffer(
            i,
            i2,
            true
         );
         this.afterBuffer = new SimpleFramebuffer(
            i,
            i2,
            true
         );
         this.maskBuffer = new SimpleFramebuffer(
            i,
            i2,
            true
         );
         this.width = i;
         this.height = i2;
         this.configuredBeforeDepthTex = -1;
         this.configuredAfterDepthTex = -1;
      }
   }

   private void ensureBloomBuffers(
      int n
   ) {
      while (
         this.bloomBuffers.size()
            > n
      ) {
         int i = this.bloomBuffers
               .size()
            - 1;
         this.bloomBuffers
            .get(
               i
            )
            .delete();
         this.bloomBuffers
            .remove(
               i
            );
      }

      for (int i2 = 0;
         i2
            < n;
         i2++
      ) {
         int i3 = Math.max(
            2,
            this.width
               >> i2
                  + 1
         );
         int i4 = Math.max(
            2,
            this.height
               >> i2
                  + 1
         );
         if (i2
            >= this.bloomBuffers.size()) {
            Framebuffer val = new SimpleFramebuffer(
               i3,
               i4,
               false
            );
            this.setLinearFiltering(
               val
            );
            this.bloomBuffers
               .add(
                  val
               );
         } else {
            Framebuffer val2 = this.bloomBuffers
               .get(
                  i2
               );
            if (val2.textureWidth
                  != i3
               || val2.textureHeight
                  != i4
               )
             {
               val2.delete();
               SimpleFramebuffer var8 = new SimpleFramebuffer(
                  i3,
                  i4,
                  false
               );
               this.setLinearFiltering(var8);
               this.bloomBuffers
                  .set(
                     i2,
                     var8
                  );
            }
         }
      }
   }

   private void setLinearFiltering(
      Framebuffer val
   ) {
      RenderSystem.bindTexture(
         val.getColorAttachment()
      );
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
      RenderSystem.bindTexture(0);
   }

   private ShaderHands getModule() {
      return AspectDLC.INSTANCE != null && ModuleManager.INSTANCE != null ? ModuleManager.shaderHands : null;
   }

   private void renderPrettyMode(
      ShaderHands hand,
      int n,
      int n2,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      ShaderProgram val = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.blockOverlay);
      if (val
         != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(ClientShaders.blockOverlay);
         RenderSystem.setShaderTexture(0, this.maskBuffer.getColorAttachment());
         this.setUniform(
            val,
            "texelSize",
            1.0F / (float)Math.max(1, mc.getWindow().getFramebufferWidth()),
            1.0F / (float)Math.max(1, mc.getWindow().getFramebufferHeight())
         );
         this.setUniform(
            val,
            "color",
            ColorUtil.redf(
               n
            ),
            ColorUtil.greenf(
               n
            ),
            ColorUtil.bluef(
               n
            )
         );
         this.setUniform(
            val,
            "color2",
            ColorUtil.redf(
               n2
            ),
            ColorUtil.greenf(
               n2
            ),
            ColorUtil.bluef(
               n2
            )
         );
         this.setUniform(
            val,
            "time",
            (float)(System.currentTimeMillis() % 100000L) / 1000.0F
         );
         this.setUniform(
            val,
            "speed",
            hand.waveSpeed
               .get()
         );
         this.setUniform(
            val,
            "scale",
            hand.waveScale
               .get()
         );
         this.setUniform(
            val,
            "outline",
            f4
         );
         this.setUniform(
            val,
            "glow",
            f
         );
         this.setUniform(
            val,
            "fill",
            f2
         );
         this.setUniform(
            val,
            "alpha",
            f3
         );
         this.setUniform(
            val,
            "outlineOnly",
            0.0F
         );
         this.drawFullscreenQuad();
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         this.restoreCompositeState();
      }
   }

   private void restoreCompositeState() {
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.setShaderTexture(1, 0);
      RenderSystem.setShaderTexture(2, 0);
      RenderSystem.setShaderTexture(3, 0);
      mc.getFramebuffer().beginWrite(true);
   }

   private boolean isEffectEnabled(
      ShaderHands hand
   ) {
      if (hand
            != null
         && hand.isEnable()
         )
       {
         boolean flag = hand.glow
               .get()
            > 0.001F;
         boolean flag2 = hand.fill
                  .get()
               > 0.001F
            && hand.alpha
                  .get()
               > 0.001F;
         return flag
            || flag2;
      } else {
         return false;
      }
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

   private void setHandsKawaseUniforms(
      ShaderProgram val,
      int n,
      int n2,
      float f
   ) {
      this.setUniform(
         val,
         "uSize",
         (float)Math.max(
            1,
            n
         ),
         (float)Math.max(
            1,
            n2
         )
      );
      this.setUniform(
         val,
         "uOffset",
         f,
         f
      );
      this.setUniform(
         val,
         "uHalfPixel",
         0.5F
            / (float)Math.max(
               1,
               n
            ),
         0.5F
            / (float)Math.max(
               1,
               n2
            )
      );
   }

   private void drawFullscreenQuad() {
      float width = (float)Math.max(
         mc.getWindow().getScaledWidth(), 1
      );
      float width2 = (float)Math.max(
         mc.getWindow().getScaledHeight(), 1
      );
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
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
}
