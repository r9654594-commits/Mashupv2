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

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.events.implement.EventUpdate;
import dev.leg1tka.mixin.WorldRendererAccessor;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShaderEsp extends Module {
   public static ShaderEsp INSTANCE = new ShaderEsp();
   private static final float EPSILON = 0.001F;
   private static final long OUTLINE_RETRY_DELAY_MS = 3000L;
   private static final double MAX_RANGE = 256.0;
   private static final float FILL_ALPHA = 0.7F;
   private static final int FILL_MIN_ITERATIONS = 2;
   private static final float GLOW_VALUE = 0.55F;
   private static final float WIDTH_VALUE = 0.9F;
   private final MultiBooleanSetting targets;
   private final BooleanSetting fill;
   private final List<Framebuffer> bloomBuffers;
   private Framebuffer depthCopyBuffer;
   private int bloomWidth;
   private int bloomHeight;
   private boolean outlineReady;
   private boolean hasOutlineTargetsCached;
   private long nextOutlineRetryAt;

   public ShaderEsp() {
      super(
         "ShaderESP",
         "Красивая обводка энтити",
         Category.RENDER
      );
      this.targets = new MultiBooleanSetting("Цели", new BooleanSetting[]{
         new BooleanSetting(
         "Игроки",
         true
      ),
         new BooleanSetting(
         "Кристаллы",
         true
      ),
         new BooleanSetting(
         "Предметы",
         false
      ),
         new BooleanSetting(
         "Себя",
         false
      )
      });
      this.fill = new BooleanSetting(
         "Заливка",
         false
      );
      this.bloomBuffers = new ArrayList<>();
      this.bloomWidth = -1;
      this.bloomHeight = -1;
      this.addSettings(new Setting[]{
         this.targets,
         this.fill
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.outlineReady = false;
      this.nextOutlineRetryAt = 0L;
      this.tryEnsureOutlineProcessor();
   }

   @Override
   public void onDisable() {
      for (Framebuffer val : this.bloomBuffers) {
         val.delete();
      }

      this.bloomBuffers.clear();
      if (this.depthCopyBuffer != null) {
         this.depthCopyBuffer.delete();
         this.depthCopyBuffer = null;
      }

      this.bloomWidth = -1;
      this.bloomHeight = -1;
      this.outlineReady = false;
      this.hasOutlineTargetsCached = false;
      this.nextOutlineRetryAt = 0L;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val7
   ) {
      if (this.isEnable()) {
         if (mc.world != null && mc.worldRenderer != null) {
            this.hasOutlineTargetsCached = this.hasOutlineTargets();
            if (!this.hasOutlineTargetsCached) {
               this.outlineReady = false;
            } else {
               if (!this.outlineReady && System.currentTimeMillis() >= this.nextOutlineRetryAt) {
                  this.tryEnsureOutlineProcessor();
               }
            }
         } else {
            this.outlineReady = false;
            this.hasOutlineTargetsCached = false;
         }
      }
   }

   @EventLink(
      priority = 200
   )
   public void onRender2D(
      EventRender.Default val7
   ) {
      if (this.isEnable() && mc.world != null && mc.player != null && mc.worldRenderer != null) {
         boolean flag = true;
         boolean flag2 = this.fill
            .isState();
         if (flag
            || flag2
            )
          {
            if (this.hasOutlineTargetsCached) {
               if (this.tryEnsureOutlineProcessor()) {
                  Framebuffer val = this.getOutlineSourceFramebuffer();
                  if (val
                        != null
                     && val.getColorAttachment()
                        != 0) {
                     Framebuffer val2 = mc.getFramebuffer();
                     this.ensureDepthCopyBuffer(
                        val2.textureWidth,
                        val2.textureHeight
                     );
                     int n = Math.max(
                        1, Math.min(8, (int)Math.ceil(1.125))
                     );
                     int n2 = 0;
                     if (flag2
                        )
                      {
                        int n3 = Math.max(
                           2,
                           Math.min(
                              6,
                              n
                                 + 1
                           )
                        );
                        n2 = this.runKawaseBloom(
                           val.getColorAttachment(),
                           n3
                        );
                     }

                     int n4 = flag
                        ? this.runKawaseBloom(
                           val.getColorAttachment(),
                           n
                        )
                        : n2;
                     int color3 = this.getOutlineColor();
                     val2.beginWrite(
                        false
                     );
                     RenderSystem.enableBlend();
                     RenderSystem.disableDepthTest();
                     RenderSystem.colorMask(true, true, true, false);
                     if (flag2
                        )
                      {
                        ShaderProgram val3 = mc.getShaderLoader()
                           .getOrCreateProgram(ClientShaders.shaderEspFill);
                        if (val3
                           != null) {
                           RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
                           RenderSystem.setShader(ClientShaders.shaderEspFill);
                           RenderSystem.setShaderTexture(
                              0,
                              val.getColorAttachment()
                           );
                           RenderSystem.setShaderTexture(
                              1,
                              n2
                                    == 0
                                 ? n4
                                 : n2
                           );
                           this.setUniform(
                              val3,
                              "color",
                              ColorUtil.redf(
                                 color3
                              ),
                              ColorUtil.greenf(
                                 color3
                              ),
                              ColorUtil.bluef(
                                 color3
                              )
                           );
                           this.setUniform(
                              val3,
                              "alpha",
                              0.7F
                           );
                           this.setUniform(
                              val3,
                              "time",
                              (float)(System.currentTimeMillis() % 100000L) / 1000.0F
                           );
                           this.drawFullscreenQuad();
                        }
                     }

                     if (flag
                        )
                      {
                        ShaderProgram val4 = mc.getShaderLoader()
                           .getOrCreateProgram(ClientShaders.shaderEspGlow);
                        if (val4
                           != null) {
                           RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
                           RenderSystem.setShader(ClientShaders.shaderEspGlow);
                           RenderSystem.setShaderTexture(
                              0,
                              n4
                           );
                           RenderSystem.setShaderTexture(
                              1,
                              val.getColorAttachment()
                           );
                           this.setUniform(
                              val4,
                              "color",
                              ColorUtil.redf(
                                 color3
                              ),
                              ColorUtil.greenf(
                                 color3
                              ),
                              ColorUtil.bluef(
                                 color3
                              )
                           );
                           this.setUniform(
                              val4,
                              "color2",
                              ColorUtil.redf(
                                 color3
                              ),
                              ColorUtil.greenf(
                                 color3
                              ),
                              ColorUtil.bluef(
                                 color3
                              )
                           );
                           this.setUniform(
                              val4,
                              "exposure",
                              0.05075F
                           );
                           this.setUniform(
                              val4,
                              "time",
                              (float)(System.currentTimeMillis() % 100000L) / 1000.0F
                           );
                           this.setUniform(
                              val4,
                              "animate",
                              1.0F
                           );
                           this.drawFullscreenQuadWithDepthTest(
                              val2,
                              val
                           );
                        }
                     }

                     RenderSystem.colorMask(true, true, true, true);
                     RenderSystem.disableDepthTest();
                     RenderSystem.disableBlend();
                     RenderSystem.defaultBlendFunc();
                     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                     RenderSystem.setShaderTexture(0, 0);
                     RenderSystem.setShaderTexture(1, 0);
                     val2.beginWrite(
                        true
                     );
                  }
               }
            }
         }
      }
   }

   private void drawFullscreenQuadWithDepthTest(
      Framebuffer val,
      Framebuffer val2
   ) {
      if (this.depthCopyBuffer == null) {
         this.drawFullscreenQuad();
      } else {
         GL30.glBindFramebuffer(
            36008,
            val.fbo
         );
         GL30.glBindFramebuffer(36009, this.depthCopyBuffer.fbo);
         GL30.glBlitFramebuffer(
            0,
            0,
            val.textureWidth,
            val.textureHeight,
            0,
            0,
            this.depthCopyBuffer.textureWidth,
            this.depthCopyBuffer.textureHeight,
            256,
            9728
         );
         GL30.glBindFramebuffer(
            36008,
            val2.fbo
         );
         GL30.glBindFramebuffer(
            36009,
            val.fbo
         );
         GL30.glBlitFramebuffer(
            0,
            0,
            val2.textureWidth,
            val2.textureHeight,
            0,
            0,
            val.textureWidth,
            val.textureHeight,
            256,
            9728
         );
         val.beginWrite(
            false
         );
         RenderSystem.enableDepthTest();
         RenderSystem.depthFunc(515);
         RenderSystem.depthMask(false);
         this.drawFullscreenQuad();
         RenderSystem.depthMask(true);
         RenderSystem.disableDepthTest();
         GL30.glBindFramebuffer(36008, this.depthCopyBuffer.fbo);
         GL30.glBindFramebuffer(
            36009,
            val.fbo
         );
         GL30.glBlitFramebuffer(
            0,
            0,
            this.depthCopyBuffer.textureWidth,
            this.depthCopyBuffer.textureHeight,
            0,
            0,
            val.textureWidth,
            val.textureHeight,
            256,
            9728
         );
         val.beginWrite(
            false
         );
      }
   }

   private void ensureDepthCopyBuffer(
      int val,
      int val2
   ) {
      if (this.depthCopyBuffer != null
         && (
            this.depthCopyBuffer.textureWidth
                  != val
               || this.depthCopyBuffer.textureHeight
                  != val2
         )) {
         this.depthCopyBuffer.delete();
         this.depthCopyBuffer = null;
      }

      if (this.depthCopyBuffer == null) {
         this.depthCopyBuffer = new SimpleFramebuffer(
            val,
            val2,
            true
         );
      }
   }

   private boolean tryEnsureOutlineProcessor() {
      if (mc.world != null && mc.worldRenderer != null) {
         Framebuffer val = this.getOutlineSourceFramebuffer();
         if (val
               != null
            && val.getColorAttachment()
               != 0) {
            this.outlineReady = true;
            return true;
         } else {
            if (this.outlineReady) {
               this.outlineReady = false;
            }

            if (System.currentTimeMillis() < this.nextOutlineRetryAt) {
               return false;
            } else {
               try {
                  mc.worldRenderer.loadEntityOutlinePostProcessor();
                  val = this.getOutlineSourceFramebuffer();
                  this.outlineReady = val
                        != null
                     && val.getColorAttachment()
                        != 0;
                  if (!this.outlineReady) {
                     this.nextOutlineRetryAt = System.currentTimeMillis() + 3000L;
                  }

                  return this.outlineReady;
               } catch (Throwable var3) {
                  this.outlineReady = false;
                  this.nextOutlineRetryAt = System.currentTimeMillis() + 3000L;
                  return false;
               }
            }
         }
      } else {
         this.outlineReady = false;
         return false;
      }
   }

   private Framebuffer getOutlineSourceFramebuffer() {
      if (mc.worldRenderer instanceof WorldRendererAccessor val
         )
       {
         Framebuffer val2 = val.AspectDLC$getEntityOutlineFramebufferRaw();
         if (val2
               != null
            && val2.getColorAttachment()
               != 0) {
            return val2;
         }
      }

      return mc.worldRenderer.getEntityOutlinesFramebuffer();
   }

   public boolean shouldOutline(
      Entity val
   ) {
      if (!this.isEnable()
         || val
            == null
         || mc.player == null
         || mc.world == null) {
         return false;
      } else if (!val.isAlive()
         )
       {
         return false;
      } else if (val.isRemoved()
         )
       {
         return false;
      } else if (val
            == mc.player
         && !this.targets
            .is(
               "Себя"
            )) {
         return false;
      } else if (val.squaredDistanceTo(
            mc.player
         )
         > 65536.0) {
         return false;
      } else if (val instanceof PlayerEntity
         )
       {
         return this.targets
            .is(
               "Игроки"
            );
      } else if (val instanceof EndCrystalEntity
         )
       {
         return this.targets
            .is(
               "Кристаллы"
            );
      } else {
         return val instanceof ItemEntity
            ? this.targets
               .is(
                  "Предметы"
               )
            : false;
      }
   }

   private boolean hasOutlineTargets() {
      if (mc.world != null && mc.player != null) {
         for (Entity entity2 : mc.world
            .getEntities()) {
            if (this.shouldOutline(
               entity2
            )) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public int getOutlineColor() {
      return ColorUtil.setAlphaColor(ColorUtil.getThemeColor(), 255)
         & 16777215;
   }

   private int runKawaseBloom(
      int val,
      int val2
   ) {
      this.ensureBloomBuffers(
         val2
      );
      if (this.bloomBuffers.isEmpty()) {
         return val;
      } else {
         int n = val;
         ShaderProgram val3 = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.shaderHandsKawaseDown);
         ShaderProgram val4 = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.shaderHandsKawaseUp);
         if (val3
               != null
            && val4
               != null) {
            for (int i = 0;
               i
                  < val2;
               i++
            ) {
               Framebuffer val5 = this.bloomBuffers
                  .get(
                     i
                  );
               val5.setClearColor(
                  0.0F, 0.0F, 0.0F, 0.0F
               );
               val5.clear();
               val5.beginWrite(
                  true
               );
               RenderSystem.setShader(ClientShaders.shaderHandsKawaseDown);
               RenderSystem.setShaderTexture(
                  0,
                  n
               );
               this.setHandsKawaseUniforms(
                  val3,
                  val5.textureWidth,
                  val5.textureHeight,
                  1.0F
                     + (float)i
               );
               this.drawFullscreenQuad();
               n = val5.getColorAttachment();
            }

            for (int i2 = val2
                  - 1;
               i2
                  >= 1;
               i2--
            ) {
               Framebuffer val6 = this.bloomBuffers
                  .get(
                     i2
                        - 1
                  );
               val6.setClearColor(
                  0.0F, 0.0F, 0.0F, 0.0F
               );
               val6.clear();
               val6.beginWrite(
                  true
               );
               RenderSystem.setShader(ClientShaders.shaderHandsKawaseUp);
               RenderSystem.setShaderTexture(
                  0,
                  n
               );
               this.setHandsKawaseUniforms(
                  val4,
                  val6.textureWidth,
                  val6.textureHeight,
                  1.0F
                     + (float)i2
               );
               this.setUniform(
                  val4,
                  "color",
                  1.0F,
                  1.0F,
                  1.0F
               );
               this.drawFullscreenQuad();
               n = val6.getColorAttachment();
            }

            mc.getFramebuffer().beginWrite(true);
            return n;
         } else {
            return val;
         }
      }
   }

   private void ensureBloomBuffers(
      int val
   ) {
      int n = mc.getWindow()
         .getFramebufferWidth();
      int n2 = mc.getWindow()
         .getFramebufferHeight();
      if (this.bloomWidth
            != n
         || this.bloomHeight
            != n2
         )
       {
         for (Framebuffer val2 : this.bloomBuffers) {
            val2.delete();
         }

         this.bloomBuffers.clear();
         this.bloomWidth = n;
         this.bloomHeight = n2;
      }

      while (
         this.bloomBuffers.size()
            > val
      ) {
         int n3 = this.bloomBuffers
               .size()
            - 1;
         this.bloomBuffers
            .get(
               n3
            )
            .delete();
         this.bloomBuffers
            .remove(
               n3
            );
      }

      for (int i = 0;
         i
            < val;
         i++
      ) {
         int n4 = Math.max(
            2,
            n
               >> i
                  + 1
         );
         int n5 = Math.max(
            2,
            n2
               >> i
                  + 1
         );
         if (i
            >= this.bloomBuffers.size()) {
            Framebuffer val3 = new SimpleFramebuffer(
               n4,
               n5,
               false
            );
            this.setLinearFiltering(
               val3
            );
            this.bloomBuffers
               .add(
                  val3
               );
         } else {
            Framebuffer val4 = this.bloomBuffers
               .get(
                  i
               );
            if (val4.textureWidth
                  != n4
               || val4.textureHeight
                  != n5
               )
             {
               val4.delete();
               SimpleFramebuffer var12 = new SimpleFramebuffer(
                  n4,
                  n5,
                  false
               );
               this.setLinearFiltering(var12);
               this.bloomBuffers
                  .set(
                     i,
                     var12
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

   private void setUniform(
      ShaderProgram val,
      String val2,
      float val3
   ) {
      GlUniform val4 = val.getUniform(
         val2
      );
      if (val4
         != null) {
         val4.set(
            val3
         );
      }
   }

   private void setUniform(
      ShaderProgram val,
      String val2,
      float val3,
      float val4
   ) {
      GlUniform val5 = val.getUniform(
         val2
      );
      if (val5
         != null) {
         val5.set(
            val3,
            val4
         );
      }
   }

   private void setUniform(
      ShaderProgram val,
      String val2,
      float val3,
      float val4,
      float val5
   ) {
      GlUniform val6 = val.getUniform(
         val2
      );
      if (val6
         != null) {
         val6.set(
            val3,
            val4,
            val5
         );
      }
   }

   private void setHandsKawaseUniforms(
      ShaderProgram val,
      int val2,
      int val3,
      float val4
   ) {
      this.setUniform(
         val,
         "uSize",
         (float)Math.max(
            1,
            val2
         ),
         (float)Math.max(
            1,
            val3
         )
      );
      this.setUniform(
         val,
         "uOffset",
         val4,
         val4
      );
      this.setUniform(
         val,
         "uHalfPixel",
         0.5F
            / (float)Math.max(
               1,
               val2
            ),
         0.5F
            / (float)Math.max(
               1,
               val3
            )
      );
   }

   private void drawFullscreenQuad() {
      float x = (float)Math.max(
         mc.getWindow().getScaledWidth(), 1
      );
      float x2 = (float)Math.max(
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
            x2,
            0.0F
         )
         .texture(0.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            x,
            x2,
            0.0F
         )
         .texture(1.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            x,
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
