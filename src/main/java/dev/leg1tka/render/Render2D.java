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
import java.awt.Color;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public final class Render2D implements IMinecraft {
   private static final ConcurrentHashMap<String, Identifier> skinCache = new ConcurrentHashMap<>();
   private static final UUID DEFAULT_SKIN_UUID = new UUID(0L, 0L);

   private static int getHudBackgroundAlpha() {
      return 150;
   }

   private static int applyHudAlpha(
      int n
   ) {
      int color = ColorUtil.getAlpha(
         n
      );
      int color2 = (
            color
                  <= 0
               ? 255
               : color
         )
         * getHudBackgroundAlpha()
         / 255;
      return ColorUtil.setAlphaColor(
         n,
         MathHelper.clamp(
            color2,
            0,
            255
         )
      );
   }

   public static boolean isLiquidGlassHudEnabled() {
      return false;
   }

   public static boolean isLiquidGlassHudActive() {
      return false;
   }

   public static boolean isLiquidGlassClickGuiActive() {
      return false;
   }

   public static HudRectStyle getDefaultHudRectStyle() {
      return HudRectStyle.SHADER;
   }

   public static void drawClickGuiPanel(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      int n2
   ) {
      if (isLiquidGlassClickGuiActive()) {
         drawHudLiquidGlass(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            n,
            true,
            getDefaultHudRectStyle()
         );
      } else {
         drawRoundedRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            ColorUtil.applyAlpha(
               ColorUtil.darken(
                  n,
                  0.07F
               ),
               0.72F
            )
         );
         if ((
               n2
                     >> 24
                  & 255
            )
            > 0) {
            drawRoundedRect(
               matrices,
               f,
               f2,
               f3,
               f4,
               f5,
               n2
            );
         }
      }
   }

   public static void drawClickGuiModuleBackground(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      boolean flag,
      int n,
      int n2
   ) {
      if (isLiquidGlassClickGuiActive()) {
         drawDefaultHudThemedPanel(
            matrices,
            f,
            f2,
            f3,
            f4,
            4.0F,
            5.0F,
            n,
            getDefaultHudRectStyle()
         );
      } else {
         if (flag
            )
          {
            drawRoundedRect(
               matrices,
               f
                  - 0.5F,
               f2
                  - 0.5F,
               f3
                  + 1.0F,
               f4
                  + 1.0F,
               5.0F,
               ColorUtil.applyAlpha(
                  ColorUtil.darken(
                     n,
                     0.17F
                  ),
                  0.78F
               )
            );
            drawGradientRect(
               matrices,
               f
                  + 0.5F,
               f2,
               f3
                  - 1.0F,
               f4,
               4.0F,
               ColorUtil.applyAlpha(
                  ColorUtil.darken(
                     n,
                     0.15F
                  ),
                  0.78F
               ),
               ColorUtil.applyAlpha(
                  ColorUtil.darken(
                     n,
                     0.1F
                  ),
                  0.78F
               ),
               false
            );
         } else {
            drawRoundedRect(
               matrices,
               f
                  - 0.5F,
               f2
                  - 0.5F,
               f3
                  + 1.0F,
               f4
                  + 1.0F,
               5.0F,
               ColorUtil.applyAlpha(
                  ColorUtil.darken(
                     n,
                     0.1F
                  ),
                  0.74F
               )
            );
            drawGradientRect(
               matrices,
               f
                  + 0.5F,
               f2,
               f3
                  - 1.0F,
               f4,
               4.0F,
               ColorUtil.applyAlpha(
                  ColorUtil.darken(
                     n,
                     0.09F
                  ),
                  0.74F
               ),
               ColorUtil.applyAlpha(
                  ColorUtil.darken(
                     n,
                     0.08F
                  ),
                  0.74F
               ),
               false
            );
         }

         if ((
               n2
                     >> 24
                  & 255
            )
            > 0) {
            drawRoundedRect(
               matrices,
               f
                  + 0.5F,
               f2,
               f3
                  - 1.0F,
               f4,
               4.0F,
               n2
            );
         }
      }
   }

   public static void drawBuffer(
      BufferBuilder buffer
   ) {
      if (buffer
         != null) {
         try {
            BufferRenderer.drawWithGlobalProgram(
               buffer.end()
            );
         } catch (IllegalStateException var2) {
         }
      }
   }

   public static void resetGuiRenderState() {
      mc.getFramebuffer().beginWrite(false);
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
   }

   public static void drawHudLiquidGlass(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      boolean flag
   ) {
      drawHudLiquidGlass(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f5,
         f5,
         f5,
         n,
         flag,
         HudRectStyle.SHADER
      );
   }

   public static void drawHudLiquidGlass(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      boolean flag,
      HudRectStyle style
   ) {
      drawHudLiquidGlass(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f5,
         f5,
         f5,
         n,
         flag,
         style
      );
   }

   public static void drawHudLiquidGlass(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n
   ) {
      drawHudLiquidGlass(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         f7,
         f8,
         n,
         true,
         HudRectStyle.SHADER
      );
   }

   public static void drawHudLiquidGlass(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n,
      boolean flag
   ) {
      drawHudLiquidGlass(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         f7,
         f8,
         n,
         flag,
         HudRectStyle.SHADER
      );
   }

   public static void drawHudLiquidGlass(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n,
      boolean flag,
      HudRectStyle style
   ) {
      if (isLiquidGlassHudActive()
         && !(
            f3
               <= 0.5F
         )
         && !(
            f4
               <= 0.5F
         )) {
         HudRectStyle style2 = style
               != null
            ? style
            : HudRectStyle.SHADER;
         drawHudLiquidGlassUnderlay(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            f6,
            f7,
            f8,
            n,
            style2
         );
         if (flag
            && IsframeactiveHelper.INSTANCE.isFrameActive()) {
            IsframeactiveHelper val = IsframeactiveHelper.INSTANCE;
            val.appendMask(
               matrices,
               f,
               f2,
               f3,
               f4,
               f5,
               f6,
               f7,
               f8
            );
            val.renderPanelOverlay(
               matrices,
               f,
               f2,
               f3,
               f4,
               style2
            );
         }

         if (style2
               == HudRectStyle.THREADS
            && f3
               >= 22.0F
            && f4
               >= 12.0F) {
            long time = ThreadRenderer.seedForRect(
               f,
               f2,
               f3,
               f4
            );
            ThreadRenderer.draw(
               matrices,
               f,
               f2,
               f3,
               f4,
               n,
               time
            );
         }
      }
   }

   private static void drawHudLiquidGlassUnderlay(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n,
      HudRectStyle style
   ) {
      KawaseBlur.getInstance().setBlurOffset(UpdateHelper_1.getBlurOffsetBoost());
      KawaseBlur.getInstance().request();
      int color = ColorUtil.setAlphaColor(
         ColorUtil.interpolateColor(
            n,
            ColorUtil.getThemeColor(),
            0.4F
         ),
         255
      );
      int color2 = ColorUtil.rgba(
         245,
         248,
         255,
         255
      );
      float alpha = MathHelper.clamp(
         (float)getHudBackgroundAlpha() / 255.0F * 0.94F, 0.3F, 1.0F
      );
      float alpha2 = UpdateHelper_1.getMotionAmount();
      float alpha3 = 0.016F
         + alpha2
            * 0.052F;
      float alpha4 = style
            == HudRectStyle.THREADS
         ? alpha
            * 0.58F
         : alpha
            * 0.78F;
      drawLiquidGlass(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         f7,
         f8,
         color2,
         alpha4,
         2.75F,
         color,
         0.34F,
         false,
         0.52F,
         alpha3,
         0.15F,
         false
      );
      resetGuiRenderState();
   }

   public static void drawHudSurface(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      int n2
   ) {
      if (isLiquidGlassHudActive()) {
         drawHudLiquidGlass(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            n2,
            true
         );
      } else {
         drawRoundedRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            applyHudAlpha(
               n
            )
         );
      }
   }

   public static void drawHudItem(
      DrawContext context,
      ItemStack stack,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      if (context
            != null
         && stack
            != null
         && !stack.isEmpty()
         )
       {
         MatrixStack matrices = context.getMatrices();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         matrices.push();
         matrices.translate(
            f,
            f2,
            f4
         );
         matrices.scale(
            f3,
            f3,
            1.0F
         );
         context.drawItem(
            stack,
            0,
            0
         );
         matrices.pop();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(true);
      }
   }

   public static void drawGradient6Rect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      ShaderProgram val = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.gradient6Rect);
      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
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
         "LeftTopColor"
      );
      GlUniform val6 = val.getUniform(
         "LeftBottomColor"
      );
      GlUniform val7 = val.getUniform(
         "CenterTopColor"
      );
      GlUniform val8 = val.getUniform(
         "CenterBottomColor"
      );
      GlUniform val9 = val.getUniform(
         "RightTopColor"
      );
      GlUniform val10 = val.getUniform(
         "RightBottomColor"
      );
      if (val2
         != null) {
         val2.set(
            f3,
            f4
         );
      }

      if (val3
         != null) {
         val3.set(
            f5,
            f5,
            f5,
            f5
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
         int color2 = n
               >> 24
            & 255;
         if (color2
            == 0) {
            color2 = 255;
         }

         val5.set(
            (float)(
                  n
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n
                     & 255
               )
               / 255.0F,
            (float)color2
               / 255.0F
         );
      }

      if (val6
         != null) {
         int color3 = n2
               >> 24
            & 255;
         if (color3
            == 0) {
            color3 = 255;
         }

         val6.set(
            (float)(
                  n2
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n2
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n2
                     & 255
               )
               / 255.0F,
            (float)color3
               / 255.0F
         );
      }

      if (val7
         != null) {
         int color4 = n3
               >> 24
            & 255;
         if (color4
            == 0) {
            color4 = 255;
         }

         val7.set(
            (float)(
                  n3
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n3
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n3
                     & 255
               )
               / 255.0F,
            (float)color4
               / 255.0F
         );
      }

      if (val8
         != null) {
         int color5 = n4
               >> 24
            & 255;
         if (color5
            == 0) {
            color5 = 255;
         }

         val8.set(
            (float)(
                  n4
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n4
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n4
                     & 255
               )
               / 255.0F,
            (float)color5
               / 255.0F
         );
      }

      if (val9
         != null) {
         int color6 = n5
               >> 24
            & 255;
         if (color6
            == 0) {
            color6 = 255;
         }

         val9.set(
            (float)(
                  n5
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n5
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n5
                     & 255
               )
               / 255.0F,
            (float)color6
               / 255.0F
         );
      }

      if (val10
         != null) {
         int color7 = n6
               >> 24
            & 255;
         if (color7
            == 0) {
            color7 = 255;
         }

         val10.set(
            (float)(
                  n6
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n6
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n6
                     & 255
               )
               / 255.0F,
            (float)color7
               / 255.0F
         );
      }

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
      RenderSystem.setShader(ClientShaders.gradient6Rect);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      RenderSystem.disableBlend();
   }

   public static void drawShadow(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      ShaderProgram val = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.shadowRect);
      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
      float x = f3
         + f6
            * 2.0F;
      float x2 = f4
         + f6
            * 2.0F;
      float x3 = f
         - f6;
      float x4 = f2
         - f6;
      GlUniform val2 = val.getUniform(
         "Size"
      );
      GlUniform val3 = val.getUniform(
         "Softness"
      );
      GlUniform val4 = val.getUniform(
         "Radius"
      );
      GlUniform val5 = val.getUniform(
         "TopLeftColor"
      );
      GlUniform val6 = val.getUniform(
         "TopRightColor"
      );
      GlUniform val7 = val.getUniform(
         "BottomLeftColor"
      );
      GlUniform val8 = val.getUniform(
         "BottomRightColor"
      );
      if (val2
         != null) {
         val2.set(
            x,
            x2
         );
      }

      if (val3
         != null) {
         val3.set(
            f6
         );
      }

      if (val4
         != null) {
         val4.set(
            f5
         );
      }

      if (val5
         != null) {
         int color = n
               >> 24
            & 255;
         if (color
            == 0) {
            color = 255;
         }

         val5.set(
            (float)(
                  n
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n
                     & 255
               )
               / 255.0F,
            (float)color
               / 255.0F
         );
      }

      if (val6
         != null) {
         int color2 = n2
               >> 24
            & 255;
         if (color2
            == 0) {
            color2 = 255;
         }

         val6.set(
            (float)(
                  n2
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n2
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n2
                     & 255
               )
               / 255.0F,
            (float)color2
               / 255.0F
         );
      }

      if (val7
         != null) {
         int color3 = n3
               >> 24
            & 255;
         if (color3
            == 0) {
            color3 = 255;
         }

         val7.set(
            (float)(
                  n3
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n3
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n3
                     & 255
               )
               / 255.0F,
            (float)color3
               / 255.0F
         );
      }

      if (val8
         != null) {
         int color4 = n4
               >> 24
            & 255;
         if (color4
            == 0) {
            color4 = 255;
         }

         val8.set(
            (float)(
                  n4
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n4
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n4
                     & 255
               )
               / 255.0F,
            (float)color4
               / 255.0F
         );
      }

      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      buffer.vertex(
            matrix,
            x3,
            x4,
            0.0F
         )
         .texture(0.0F, 0.0F);
      buffer.vertex(
            matrix,
            x3,
            x4
               + x2,
            0.0F
         )
         .texture(0.0F, 1.0F);
      buffer.vertex(
            matrix,
            x3
               + x,
            x4
               + x2,
            0.0F
         )
         .texture(1.0F, 1.0F);
      buffer.vertex(
            matrix,
            x3
               + x,
            x4,
            0.0F
         )
         .texture(1.0F, 0.0F);
      RenderSystem.setShader(ClientShaders.shadowRect);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      RenderSystem.disableBlend();
   }

   public static void drawShadow(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      drawShadow(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         10.0F,
         n,
         n2,
         n3,
         n4
      );
   }

   public static void drawShadow(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n
   ) {
      drawShadow(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         n,
         n,
         n,
         n
      );
   }

   public static void drawShadow(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n
   ) {
      drawShadow(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         10.0F,
         n,
         n,
         n,
         n
      );
   }

   public static void drawShadow(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      drawShadow(
         matrices,
         f,
         f2,
         f3,
         f4,
         0.0F,
         10.0F,
         n,
         n,
         n,
         n
      );
   }

   public static void drawShadow(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n,
      int n2
   ) {
      drawShadow(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         n,
         n,
         n2,
         n2
      );
   }

   public static void drawShadowHorizontal(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n,
      int n2
   ) {
      drawShadow(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         n,
         n2,
         n,
         n2
      );
   }

   public static void drawShadow(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n
   ) {
      drawShadow(
         matrices,
         f
            + f7,
         f2
            + f8,
         f3,
         f4,
         f5,
         f6,
         n,
         n,
         n,
         n
      );
   }

   public static void drawShadow(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      drawShadow(
         matrices,
         f
            + f7,
         f2
            + f8,
         f3,
         f4,
         f5,
         f6,
         n,
         n2,
         n3,
         n4
      );
   }

   public static void drawShadowOuter(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      ShaderProgram val = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.shadowOuter);
      if (val
         == null) {
         RenderSystem.disableBlend();
      } else {
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         float x = f3
            + f6
               * 2.0F;
         float x2 = f4
            + f6
               * 2.0F;
         float x3 = f
            - f6;
         float x4 = f2
            - f6;
         GlUniform val2 = val.getUniform(
            "Size"
         );
         GlUniform val3 = val.getUniform(
            "Softness"
         );
         GlUniform val4 = val.getUniform(
            "Radius"
         );
         if (val2
            != null) {
            val2.set(
               x,
               x2
            );
         }

         if (val3
            != null) {
            val3.set(
               f6
            );
         }

         if (val4
            != null) {
            val4.set(
               f5
            );
         }

         int i = n
               >> 24
            & 255;
         if (i
            == 0) {
            i = 255;
         }

         float x5 = (float)(
               n
                     >> 16
                  & 255
            )
            / 255.0F;
         float x6 = (float)(
               n
                     >> 8
                  & 255
            )
            / 255.0F;
         float x7 = (float)(
               n
                  & 255
            )
            / 255.0F;
         float x8 = (float)i
            / 255.0F;
         String[] var10000 = new String[4];
         var10000[0] = "TopLeftColor";
         var10000[1] = "TopRightColor";
         int var10002 = 2;
         var10000[var10002] = "BottomLeftColor";
         var10002 = 3;
         var10000[var10002] = "BottomRightColor";
         String[] val5 = var10000;

         for (String text : val5) {
            GlUniform val6 = val.getUniform(
               text
            );
            if (val6
               != null) {
               val6.set(
                  x5,
                  x6,
                  x7,
                  x8
               );
            }
         }

         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
         buffer.vertex(
               matrix,
               x3,
               x4,
               0.0F
            )
            .texture(0.0F, 0.0F);
         buffer.vertex(
               matrix,
               x3,
               x4
                  + x2,
               0.0F
            )
            .texture(0.0F, 1.0F);
         buffer.vertex(
               matrix,
               x3
                  + x,
               x4
                  + x2,
               0.0F
            )
            .texture(1.0F, 1.0F);
         buffer.vertex(
               matrix,
               x3
                  + x,
               x4,
               0.0F
            )
            .texture(1.0F, 0.0F);
         RenderSystem.setShader(ClientShaders.shadowOuter);
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         RenderSystem.disableBlend();
      }
   }

   public static void drawShadow6(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      ShaderProgram val = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.shadow6Rect);
      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
      float x = f3
         + f6
            * 2.0F;
      float x2 = f4
         + f6
            * 2.0F;
      float x3 = f
         - f6;
      float x4 = f2
         - f6;
      GlUniform val2 = val.getUniform(
         "Size"
      );
      GlUniform val3 = val.getUniform(
         "Softness"
      );
      GlUniform val4 = val.getUniform(
         "Radius"
      );
      GlUniform val5 = val.getUniform(
         "LeftTopColor"
      );
      GlUniform val6 = val.getUniform(
         "LeftBottomColor"
      );
      GlUniform val7 = val.getUniform(
         "CenterTopColor"
      );
      GlUniform val8 = val.getUniform(
         "CenterBottomColor"
      );
      GlUniform val9 = val.getUniform(
         "RightTopColor"
      );
      GlUniform val10 = val.getUniform(
         "RightBottomColor"
      );
      if (val2
         != null) {
         val2.set(
            x,
            x2
         );
      }

      if (val3
         != null) {
         val3.set(
            f6
         );
      }

      if (val4
         != null) {
         val4.set(
            f5
         );
      }

      if (val5
         != null) {
         int color = n
               >> 24
            & 255;
         if (color
            == 0) {
            color = 255;
         }

         val5.set(
            (float)(
                  n
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n
                     & 255
               )
               / 255.0F,
            (float)color
               / 255.0F
         );
      }

      if (val6
         != null) {
         int color2 = n2
               >> 24
            & 255;
         if (color2
            == 0) {
            color2 = 255;
         }

         val6.set(
            (float)(
                  n2
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n2
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n2
                     & 255
               )
               / 255.0F,
            (float)color2
               / 255.0F
         );
      }

      if (val7
         != null) {
         int color3 = n3
               >> 24
            & 255;
         if (color3
            == 0) {
            color3 = 255;
         }

         val7.set(
            (float)(
                  n3
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n3
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n3
                     & 255
               )
               / 255.0F,
            (float)color3
               / 255.0F
         );
      }

      if (val8
         != null) {
         int color4 = n4
               >> 24
            & 255;
         if (color4
            == 0) {
            color4 = 255;
         }

         val8.set(
            (float)(
                  n4
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n4
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n4
                     & 255
               )
               / 255.0F,
            (float)color4
               / 255.0F
         );
      }

      if (val9
         != null) {
         int color5 = n5
               >> 24
            & 255;
         if (color5
            == 0) {
            color5 = 255;
         }

         val9.set(
            (float)(
                  n5
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n5
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n5
                     & 255
               )
               / 255.0F,
            (float)color5
               / 255.0F
         );
      }

      if (val10
         != null) {
         int color6 = n6
               >> 24
            & 255;
         if (color6
            == 0) {
            color6 = 255;
         }

         val10.set(
            (float)(
                  n6
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n6
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n6
                     & 255
               )
               / 255.0F,
            (float)color6
               / 255.0F
         );
      }

      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      buffer.vertex(
            matrix,
            x3,
            x4,
            0.0F
         )
         .texture(0.0F, 0.0F);
      buffer.vertex(
            matrix,
            x3,
            x4
               + x2,
            0.0F
         )
         .texture(0.0F, 1.0F);
      buffer.vertex(
            matrix,
            x3
               + x,
            x4
               + x2,
            0.0F
         )
         .texture(1.0F, 1.0F);
      buffer.vertex(
            matrix,
            x3
               + x,
            x4,
            0.0F
         )
         .texture(1.0F, 0.0F);
      RenderSystem.setShader(ClientShaders.shadow6Rect);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      RenderSystem.disableBlend();
   }

   public static void drawTexture(
      MatrixStack matrices,
      Identifier id,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderTexture(
         0,
         id
      );
      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
      int color2 = n
            >> 24
         & 255;
      if (color2
         == 0) {
         color2 = 255;
      }

      float x = (float)(
            n
                  >> 16
               & 255
         )
         / 255.0F;
      float x2 = (float)(
            n
                  >> 8
               & 255
         )
         / 255.0F;
      float x3 = (float)(
            n
               & 255
         )
         / 255.0F;
      float x4 = (float)color2
         / 255.0F;
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      buffer.vertex(
            matrix,
            f,
            f2,
            0.0F
         )
         .texture(
            f5,
            f6
         )
         .color(
            x,
            x2,
            x3,
            x4
         );
      buffer.vertex(
            matrix,
            f,
            f2
               + f4,
            0.0F
         )
         .texture(
            f5,
            f8
         )
         .color(
            x,
            x2,
            x3,
            x4
         );
      buffer.vertex(
            matrix,
            f
               + f3,
            f2
               + f4,
            0.0F
         )
         .texture(
            f7,
            f8
         )
         .color(
            x,
            x2,
            x3,
            x4
         );
      buffer.vertex(
            matrix,
            f
               + f3,
            f2,
            0.0F
         )
         .texture(
            f7,
            f6
         )
         .color(
            x,
            x2,
            x3,
            x4
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.disableBlend();
   }

   public static void drawImage(
      MatrixStack matrices,
      Identifier id,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      drawTexture(
         matrices,
         id,
         f,
         f2,
         f3,
         f4,
         0.0F,
         0.0F,
         1.0F,
         1.0F,
         n
      );
   }

   public static void drawImage(
      MatrixStack matrices,
      String str,
      String str2,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      drawImage(
         matrices,
         Identifier.of(
            str,
            str2
         ),
         f,
         f2,
         f3,
         f4,
         n
      );
   }

   public static void drawSprite(
      MatrixStack matrices,
      Sprite val,
      float f,
      float f2,
      float f3,
      int n
   ) {
      drawTexture(
         matrices,
         val.getAtlasId(),
         f,
         f2,
         f3,
         f3,
         val.getMinU(),
         val.getMinV(),
         val.getMaxU(),
         val.getMaxV(),
         n
      );
   }

   public static void drawPlayerHead(
      MatrixStack matrices,
      PlayerEntity player,
      float f,
      float f2,
      float f3,
      float f4,
      float f5
   ) {
      if (player
         != null) {
         Identifier texture = getSkinTexture(
            player
         );
         drawHeadInternal(
            matrices,
            texture,
            f,
            f2,
            f3,
            f4,
            1.0F,
            f5
         );
      }
   }

   public static void drawPlayerHead(
      MatrixStack matrices,
      String str,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      drawPlayerHead(
         matrices,
         str,
         f,
         f2,
         f3,
         f4,
         1.0F,
         0.0F
      );
   }

   public static void drawPlayerHead(
      MatrixStack matrices,
      String str,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         Identifier texture = getSkinTextureByName(
            str
         );
         drawHeadInternal(
            matrices,
            texture,
            f,
            f2,
            f3,
            f4,
            f5,
            f6
         );
      }
   }

   public static void drawPlayerHead(
      MatrixStack matrices,
      UUID val,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      drawPlayerHead(
         matrices,
         val,
         f,
         f2,
         f3,
         f4,
         1.0F,
         0.0F
      );
   }

   public static void drawPlayerHead(
      MatrixStack matrices,
      UUID val,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6
   ) {
      if (val
         != null) {
         Identifier texture = getSkinTextureByUUID(
            val
         );
         drawHeadInternal(
            matrices,
            texture,
            f,
            f2,
            f3,
            f4,
            f5,
            f6
         );
      }
   }

   public static void drawPlayerHead(
      MatrixStack matrices,
      PlayerListEntry val,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      drawPlayerHead(
         matrices,
         val,
         f,
         f2,
         f3,
         f4,
         1.0F,
         0.0F
      );
   }

   public static void drawPlayerHead(
      MatrixStack matrices,
      PlayerListEntry val,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6
   ) {
      if (val
         != null) {
         Identifier texture2 = val.getSkinTextures()
            .texture();
         if (texture2
            == null) {
            texture2 = DefaultSkinHelper.getSkinTextures(
                  val.getProfile()
                     .getId()
               )
               .texture();
         }

         drawHeadInternal(
            matrices,
            texture2,
            f,
            f2,
            f3,
            f4,
            f5,
            f6
         );
      }
   }

   public static void drawPlayerHead(
      MatrixStack matrices,
      Identifier id,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      drawHeadInternal(
         matrices,
         id,
         f,
         f2,
         f3,
         f4,
         1.0F,
         0.0F
      );
   }

   private static void drawHeadInternal(
      MatrixStack matrices,
      Identifier id,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6
   ) {
      if (id
         == null) {
         id = DefaultSkinHelper.getSkinTextures(
               DEFAULT_SKIN_UUID
            )
            .texture();
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderTexture(
         0,
         id
      );
      ShaderProgram val = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.face);
      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
      GlUniform val2 = val.getUniform(
         "location"
      );
      GlUniform val3 = val.getUniform(
         "size"
      );
      GlUniform val4 = val.getUniform(
         "radius"
      );
      GlUniform val5 = val.getUniform(
         "alpha"
      );
      GlUniform val6 = val.getUniform(
         "u"
      );
      GlUniform val7 = val.getUniform(
         "v"
      );
      GlUniform val8 = val.getUniform(
         "w"
      );
      GlUniform val9 = val.getUniform(
         "h"
      );
      GlUniform val10 = val.getUniform(
         "hurtPercent"
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
            f3
         );
      }

      if (val4
         != null) {
         val4.set(
            f4
         );
      }

      if (val5
         != null) {
         val5.set(
            f5
         );
      }

      if (val6
         != null) {
         val6.set(
            0.125F
         );
      }

      if (val7
         != null) {
         val7.set(
            0.125F
         );
      }

      if (val8
         != null) {
         val8.set(
            0.125F
         );
      }

      if (val9
         != null) {
         val9.set(
            0.125F
         );
      }

      if (val10
         != null) {
         val10.set(
            f6
         );
      }

      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      buffer.vertex(
            matrix,
            f,
            f2,
            0.0F
         )
         .texture(0.0F, 0.0F);
      buffer.vertex(
            matrix,
            f,
            f2
               + f3,
            0.0F
         )
         .texture(0.0F, 1.0F);
      buffer.vertex(
            matrix,
            f
               + f3,
            f2
               + f3,
            0.0F
         )
         .texture(1.0F, 1.0F);
      buffer.vertex(
            matrix,
            f
               + f3,
            f2,
            0.0F
         )
         .texture(1.0F, 0.0F);
      RenderSystem.setShader(ClientShaders.face);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      drawHeadOverlay(
         matrices,
         id,
         f,
         f2,
         f3,
         f4,
         f5,
         f6
      );
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.disableBlend();
   }

   private static void drawHeadOverlay(
      MatrixStack matrices,
      Identifier id,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6
   ) {
      RenderSystem.setShaderTexture(
         0,
         id
      );
      ShaderProgram val = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.face);
      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
      GlUniform val2 = val.getUniform(
         "location"
      );
      GlUniform val3 = val.getUniform(
         "size"
      );
      GlUniform val4 = val.getUniform(
         "radius"
      );
      GlUniform val5 = val.getUniform(
         "alpha"
      );
      GlUniform val6 = val.getUniform(
         "u"
      );
      GlUniform val7 = val.getUniform(
         "v"
      );
      GlUniform val8 = val.getUniform(
         "w"
      );
      GlUniform val9 = val.getUniform(
         "h"
      );
      GlUniform val10 = val.getUniform(
         "hurtPercent"
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
            f3
         );
      }

      if (val4
         != null) {
         val4.set(
            f4
         );
      }

      if (val5
         != null) {
         val5.set(
            f5
         );
      }

      if (val6
         != null) {
         val6.set(
            0.625F
         );
      }

      if (val7
         != null) {
         val7.set(
            0.125F
         );
      }

      if (val8
         != null) {
         val8.set(
            0.125F
         );
      }

      if (val9
         != null) {
         val9.set(
            0.125F
         );
      }

      if (val10
         != null) {
         val10.set(
            f6
         );
      }

      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      buffer.vertex(
            matrix,
            f,
            f2,
            0.0F
         )
         .texture(0.0F, 0.0F);
      buffer.vertex(
            matrix,
            f,
            f2
               + f3,
            0.0F
         )
         .texture(0.0F, 1.0F);
      buffer.vertex(
            matrix,
            f
               + f3,
            f2
               + f3,
            0.0F
         )
         .texture(1.0F, 1.0F);
      buffer.vertex(
            matrix,
            f
               + f3,
            f2,
            0.0F
         )
         .texture(1.0F, 0.0F);
      RenderSystem.setShader(ClientShaders.face);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private static Identifier getSkinTexture(
      PlayerEntity player
   ) {
      if (mc.getNetworkHandler() == null) {
         return DefaultSkinHelper.getSkinTextures(
               player.getUuid()
            )
            .texture();
      } else {
         PlayerListEntry val = mc.getNetworkHandler()
            .getPlayerListEntry(
               player.getUuid()
            );
         return val
               != null
            ? val.getSkinTextures()
               .texture()
            : DefaultSkinHelper.getSkinTextures(
                  player.getUuid()
               )
               .texture();
      }
   }

   private static Identifier getSkinTextureByName(
      String str
   ) {
      String name = str.toLowerCase(
         Locale.ROOT
      );
      Identifier texture2 = skinCache.get(
         name
      );
      if (texture2
         != null) {
         return texture2;
      } else {
         if (mc.getNetworkHandler() != null) {
            for (PlayerListEntry val : mc.getNetworkHandler()
               .getPlayerList()) {
               if (val.getProfile()
                  .getName()
                  .equalsIgnoreCase(
                     str
                  )) {
                  Identifier texture3 = val.getSkinTextures()
                     .texture();
                  skinCache.put(
                     name,
                     texture3
                  );
                  return texture3;
               }
            }
         }

         if (mc.world != null) {
            for (PlayerEntity player : mc.world
               .getPlayers()) {
               if (player.getName()
                  .getString()
                  .equalsIgnoreCase(
                     str
                  )) {
                  Identifier texture4 = getSkinTexture(
                     player
                  );
                  skinCache.put(
                     name,
                     texture4
                  );
                  return texture4;
               }
            }
         }

         Identifier texture5 = DefaultSkinHelper.getSkinTextures(
               UUID.nameUUIDFromBytes(
                  str.getBytes()
               )
            )
            .texture();
         skinCache.put(
            name,
            texture5
         );
         return texture5;
      }
   }

   private static Identifier getSkinTextureByUUID(
      UUID val
   ) {
      String text = val.toString();
      if (skinCache.containsKey(
         text
      )) {
         return skinCache.get(
            text
         );
      } else {
         if (mc.getNetworkHandler() != null) {
            PlayerListEntry val2 = mc.getNetworkHandler()
               .getPlayerListEntry(
                  val
               );
            if (val2
               != null) {
               Identifier texture2 = val2.getSkinTextures()
                  .texture();
               skinCache.put(
                  text,
                  texture2
               );
               return texture2;
            }
         }

         if (mc.world != null) {
            PlayerEntity player = mc.world
               .getPlayerByUuid(
                  val
               );
            if (player
               != null) {
               Identifier texture3 = getSkinTexture(
                  player
               );
               skinCache.put(
                  text,
                  texture3
               );
               return texture3;
            }
         }

         return DefaultSkinHelper.getSkinTextures(
               val
            )
            .texture();
      }
   }

   public static void clearSkinCache() {
      skinCache.clear();
   }

   public static void removeSkinFromCache(
      String str
   ) {
      skinCache.remove(
         str.toLowerCase(
            Locale.ROOT
         )
      );
   }

   public static void drawRoundedRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n
   ) {
      drawRoundedRect(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f5,
         f5,
         f5,
         n
      );
   }

   public static void drawDefaultHudElementRects(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      drawDefaultHudElementRects(
         matrices,
         f,
         f2,
         f3,
         f4,
         n,
         HudRectStyle.SHADER
      );
   }

   public static void drawDefaultHudElementRects(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n,
      HudRectStyle style
   ) {
      HudRectStyle style2 = style
            != null
         ? style
         : HudRectStyle.SHADER;
      if (isLiquidGlassHudActive()) {
         drawHudLiquidGlass(
            matrices,
            f,
            f2,
            f3,
            f4,
            3.0F,
            3.5F,
            3.5F,
            3.0F,
            n,
            true,
            style2
         );
         if (style2
            != HudRectStyle.THREADS) {
            drawHudLiquidGlass(
               matrices,
               f
                  + f3
                  - 14.5F,
               f2
                  + 3.0F,
               10.0F,
               10.0F,
               2.0F,
               n,
               true,
               style2
            );
         }
      } else {
         drawDefaultHudThemedPanel(
            matrices,
            f,
            f2,
            f3,
            f4,
            3.0F,
            3.5F,
            n,
            style2
         );
      }
   }

   public static void drawHudSquarePattern(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      if (!(
            f3
               <= 6.0F
         )
         && !(
            f4
               <= 6.0F
         )) {
         float alpha = f
            - 1.0F;
         float alpha2 = f2
            + 1.0F;
         float alpha3 = Math.max(
            1.0F,
            f3
               - 2.0F
         );
         float alpha4 = Math.max(
            1.0F,
            f4
               - 2.0F
         );
         float alpha5 = (float)(
               n
                     >>> 24
                  & 255
            )
            / 255.0F;
         if (!(
            alpha5
               <= 0.001F
         )) {
            if (alpha4
               <= 20.0F) {
               float[][] var50 = new float[14][];
               float[] var107 = new float[3];
               var107[0] = 0.05F;
               var107[1] = 0.08F;
               var107[2] = 8.6F;
               var50[0] = var107;
               var107 = new float[3];
               var107[0] = 0.92F;
               var107[1] = 0.1F;
               var107[2] = 8.8F;
               var50[1] = var107;
               int var72 = 2;
               var107 = new float[3];
               var107[0] = 0.16F;
               var107[1] = 0.78F;
               var107[2] = 6.3F;
               var50[var72] = var107;
               var72 = 3;
               var107 = new float[3];
               var107[0] = 0.77F;
               var107[1] = 0.8F;
               var107[2] = 6.5F;
               var50[var72] = var107;
               var72 = 4;
               var107 = new float[3];
               var107[0] = 0.31F;
               var107[1] = 0.18F;
               var107[2] = 6.0F;
               var50[var72] = var107;
               var72 = 5;
               var107 = new float[3];
               var107[0] = 0.58F;
               var107[1] = 0.74F;
               var107[2] = 5.8F;
               var50[var72] = var107;
               var72 = 6;
               var107 = new float[3];
               var107[0] = 0.45F;
               var107[1] = 0.45F;
               var107[2] = 5.1F;
               var50[var72] = var107;
               var72 = 7;
               var107 = new float[3];
               var107[0] = 0.86F;
               var107[1] = 0.46F;
               var107[2] = 5.3F;
               var50[var72] = var107;
               var72 = 8;
               var107 = new float[3];
               var107[0] = 0.23F;
               var107[1] = 0.52F;
               var107[2] = 4.9F;
               var50[var72] = var107;
               var72 = 9;
               var107 = new float[3];
               var107[0] = 0.67F;
               var107[1] = 0.3F;
               var107[2] = 5.0F;
               var50[var72] = var107;
               var72 = 10;
               var107 = new float[3];
               var107[0] = 0.11F;
               var107[1] = 0.34F;
               var107[2] = 5.5F;
               var50[var72] = var107;
               var72 = 11;
               var107 = new float[3];
               var107[0] = 0.38F;
               var107[1] = 0.7F;
               var107[2] = 5.2F;
               var50[var72] = var107;
               var72 = 12;
               var107 = new float[3];
               var107[0] = 0.72F;
               var107[1] = 0.16F;
               var107[2] = 5.7F;
               var50[var72] = var107;
               var72 = 13;
               var107 = new float[3];
               var107[0] = 0.95F;
               var107[1] = 0.68F;
               var107[2] = 5.1F;
               var50[var72] = var107;
               float[][] val = var50;
               float alpha6 = Math.min(
                  (float)val.length,
                  3.7F
                     + Math.max(
                        0.0F,
                        (
                              alpha3
                                 - 84.0F
                           )
                           / 32.0F
                     )
               );
               int i = ColorUtil.setAlphaColor(
                  ColorUtil.darken(
                     n,
                     0.62F
                  ),
                  Math.max(
                     0,
                     Math.min(
                        255,
                        (int)(
                           82.0F
                              * alpha5
                        )
                     )
                  )
               );
               ScissorStack.push();
               ScissorStack.setFromComponentCoordinates(
                  (double)alpha,
                  (double)alpha2,
                  (double)alpha3,
                  (double)alpha4
               );

               try {
                  for (int i2 = 0;
                     i2
                        < val.length;
                     i2++
                  ) {
                     float alpha7 = alpha6
                        - (float)i2;
                     if (!(
                        alpha7
                           <= 0.0F
                     )) {
                        float alpha8 = Math.max(
                           0.0F,
                           Math.min(
                              1.0F,
                              alpha7
                           )
                        );
                        alpha8 = alpha8
                           * alpha8
                           * (
                              3.0F
                                 - 2.0F
                                    * alpha8
                           );
                        if (!(
                           alpha8
                              <= 0.02F
                        )) {
                           float alpha9 = val[i2][2];
                           float alpha10 = alpha
                              + 0.8F
                              + val[i2][0]
                                 * Math.max(
                                    1.0F,
                                    alpha3
                                       - alpha9
                                       + 1.6F
                                 );
                           float alpha11 = alpha2
                              - 1.2F
                              + val[i2][1]
                                 * Math.max(
                                    1.0F,
                                    alpha4
                                       - alpha9
                                       + 2.4F
                                 );
                           int i3 = Math.max(
                              0,
                              Math.min(
                                 255,
                                 (int)(
                                    86.0F
                                       * alpha8
                                       * alpha5
                                 )
                              )
                           );
                           if (i3
                              > 0) {
                              int i4 = ColorUtil.setAlphaColor(
                                 i,
                                 i3
                              );
                              drawRoundedRectOutline(
                                 matrices,
                                 alpha10,
                                 alpha11,
                                 alpha9,
                                 alpha9,
                                 0.0F,
                                 0.5F,
                                 i4,
                                 i4,
                                 i4,
                                 i4
                              );
                           }
                        }
                     }
                  }
               } finally {
                  ScissorStack.unset();
                  ScissorStack.pop();
               }
            } else {
               float[][] var10000 = new float[24][];
               float[] var10003 = new float[3];
               var10003[0] = 0.05F;
               var10003[1] = 4.0F;
               var10003[2] = 9.6F;
               var10000[0] = var10003;
               var10003 = new float[3];
               var10003[0] = 0.87F;
               var10003[1] = 4.0F;
               var10003[2] = 9.2F;
               var10000[1] = var10003;
               int var10002 = 2;
               var10003 = new float[3];
               var10003[0] = 0.5F;
               var10003[1] = 8.0F;
               var10003[2] = 7.4F;
               var10000[var10002] = var10003;
               var10002 = 3;
               var10003 = new float[3];
               var10003[0] = 0.18F;
               var10003[1] = 13.0F;
               var10003[2] = 6.2F;
               var10000[var10002] = var10003;
               var10002 = 4;
               var10003 = new float[3];
               var10003[0] = 0.72F;
               var10003[1] = 13.0F;
               var10003[2] = 6.0F;
               var10000[var10002] = var10003;
               var10002 = 5;
               var10003 = new float[3];
               var10003[0] = 0.07F;
               var10003[1] = 21.0F;
               var10003[2] = 5.6F;
               var10000[var10002] = var10003;
               var10002 = 6;
               var10003 = new float[3];
               var10003[0] = 0.91F;
               var10003[1] = 21.0F;
               var10003[2] = 5.8F;
               var10000[var10002] = var10003;
               var10002 = 7;
               var10003 = new float[3];
               var10003[0] = 0.24F;
               var10003[1] = 30.0F;
               var10003[2] = 5.4F;
               var10000[var10002] = var10003;
               var10002 = 8;
               var10003 = new float[3];
               var10003[0] = 0.66F;
               var10003[1] = 30.0F;
               var10003[2] = 5.5F;
               var10000[var10002] = var10003;
               var10002 = 9;
               var10003 = new float[3];
               var10003[0] = 0.04F;
               var10003[1] = 38.0F;
               var10003[2] = 6.8F;
               var10000[var10002] = var10003;
               var10002 = 10;
               var10003 = new float[3];
               var10003[0] = 0.9F;
               var10003[1] = 38.0F;
               var10003[2] = 7.0F;
               var10000[var10002] = var10003;
               var10002 = 11;
               var10003 = new float[3];
               var10003[0] = 0.15F;
               var10003[1] = 47.0F;
               var10003[2] = 5.4F;
               var10000[var10002] = var10003;
               var10002 = 12;
               var10003 = new float[3];
               var10003[0] = 0.78F;
               var10003[1] = 47.0F;
               var10003[2] = 5.5F;
               var10000[var10002] = var10003;
               var10002 = 13;
               var10003 = new float[3];
               var10003[0] = 0.08F;
               var10003[1] = 56.0F;
               var10003[2] = 5.1F;
               var10000[var10002] = var10003;
               var10002 = 14;
               var10003 = new float[3];
               var10003[0] = 0.92F;
               var10003[1] = 56.0F;
               var10003[2] = 5.2F;
               var10000[var10002] = var10003;
               var10002 = 15;
               var10003 = new float[3];
               var10003[0] = 0.23F;
               var10003[1] = 65.0F;
               var10003[2] = 5.8F;
               var10000[var10002] = var10003;
               var10002 = 16;
               var10003 = new float[3];
               var10003[0] = 0.69F;
               var10003[1] = 65.0F;
               var10003[2] = 5.9F;
               var10000[var10002] = var10003;
               var10002 = 17;
               var10003 = new float[3];
               var10003[0] = 0.52F;
               var10003[1] = 71.0F;
               var10003[2] = 7.2F;
               var10000[var10002] = var10003;
               var10002 = 18;
               var10003 = new float[3];
               var10003[0] = 0.06F;
               var10003[1] = 74.0F;
               var10003[2] = 7.6F;
               var10000[var10002] = var10003;
               var10002 = 19;
               var10003 = new float[3];
               var10003[0] = 0.88F;
               var10003[1] = 74.0F;
               var10003[2] = 7.4F;
               var10000[var10002] = var10003;
               var10002 = 20;
               var10003 = new float[3];
               var10003[0] = 0.14F;
               var10003[1] = 85.0F;
               var10003[2] = 5.7F;
               var10000[var10002] = var10003;
               var10002 = 21;
               var10003 = new float[3];
               var10003[0] = 0.82F;
               var10003[1] = 85.0F;
               var10003[2] = 5.8F;
               var10000[var10002] = var10003;
               var10002 = 22;
               var10003 = new float[3];
               var10003[0] = 0.09F;
               var10003[1] = 97.0F;
               var10003[2] = 6.5F;
               var10000[var10002] = var10003;
               var10002 = 23;
               var10003 = new float[3];
               var10003[0] = 0.9F;
               var10003[1] = 98.0F;
               var10003[2] = 6.6F;
               var10000[var10002] = var10003;
               float[][] val2 = var10000;
               int i5 = 10;
               float alpha12 = Math.max(
                  0.0F,
                  alpha4
                     - 24.0F
               );
               float alpha13 = Math.min(
                  (float)val2.length,
                  (float)i5
                     + alpha12
                        / 10.0F
               );
               float alpha14 = Math.max(
                  0.0F,
                  Math.min(
                     1.0F,
                     (
                           alpha4
                              - 10.0F
                        )
                        / 16.0F
                  )
               );
               alpha14 = alpha14
                  * alpha14
                  * (
                     3.0F
                        - 2.0F
                           * alpha14
                  );
               int i6 = ColorUtil.setAlphaColor(
                  ColorUtil.darken(
                     n,
                     0.72F
                  ),
                  Math.max(
                     0,
                     Math.min(
                        255,
                        (int)(
                           40.0F
                              * alpha5
                        )
                     )
                  )
               );
               ScissorStack.push();
               ScissorStack.setFromComponentCoordinates(
                  (double)alpha,
                  (double)alpha2,
                  (double)alpha3,
                  (double)alpha4
               );

               try {
                  for (int i7 = 0;
                     i7
                        < val2.length;
                     i7++
                  ) {
                     float alpha15 = alpha13
                        - (float)i7;
                     if (!(
                        alpha15
                           <= 0.0F
                     )) {
                        float alpha16 = Math.max(
                           0.0F,
                           Math.min(
                              1.0F,
                              alpha15
                           )
                        );
                        alpha16 = alpha16
                           * alpha16
                           * (
                              3.0F
                                 - 2.0F
                                    * alpha16
                           );
                        alpha16 *= alpha14;
                        if (!(
                           alpha16
                              <= 0.015F
                        )) {
                           float alpha17 = val2[i7][2];
                           float alpha18 = alpha
                              + 2.0F
                              + val2[i7][0]
                                 * Math.max(
                                    1.0F,
                                    alpha3
                                       - alpha17
                                       - 4.0F
                                 );
                           float alpha19 = alpha2
                              + val2[i7][1];
                           float alpha20 = alpha2
                              + alpha4
                              - 1.0F;
                           if (!(
                              alpha19
                                 >= alpha20
                           )) {
                              if (alpha19
                                    + alpha17
                                 > alpha20
                                 )
                               {
                                 float alpha21 = Math.max(
                                    0.0F,
                                    Math.min(
                                       1.0F,
                                       (
                                             alpha20
                                                - alpha19
                                          )
                                          / Math.max(
                                             1.0F,
                                             alpha17
                                          )
                                    )
                                 );
                                 alpha21 = alpha21
                                    * alpha21
                                    * (
                                       3.0F
                                          - 2.0F
                                             * alpha21
                                    );
                                 alpha16 *= alpha21;
                                 if (alpha16
                                    <= 0.015F) {
                                    continue;
                                 }
                              }

                              int i8 = Math.max(
                                 0,
                                 Math.min(
                                    255,
                                    (int)(
                                       58.0F
                                          * alpha16
                                          * alpha5
                                    )
                                 )
                              );
                              if (i8
                                 > 0) {
                                 int i9 = ColorUtil.setAlphaColor(
                                    i6,
                                    i8
                                 );
                                 drawRoundedRectOutline(
                                    matrices,
                                    alpha18,
                                    alpha19,
                                    alpha17,
                                    alpha17,
                                    0.0F,
                                    0.55F,
                                    i9,
                                    i9,
                                    i9,
                                    i9
                                 );
                              }
                           }
                        }
                     }
                  }
               } finally {
                  ScissorStack.unset();
                  ScissorStack.pop();
               }
            }
         }
      }
   }

   public static void drawDefaultHudInfoBox(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      int n,
      int n2
   ) {
      if (isLiquidGlassHudActive()) {
         int color = ColorUtil.interpolateColor(
            n,
            n2,
            0.5F
         );
         drawHudLiquidGlass(
            matrices,
            f
               - 0.25F,
            f2
               - 1.25F,
            f3
               + 0.5F,
            9.0F,
            1.3F,
            color,
            true
         );
      } else {
         drawRoundedRect(
            matrices,
            f
               - 0.25F,
            f2
               - 1.25F,
            f3
               + 0.5F,
            9.0F,
            1.3F,
            applyHudAlpha(
               n
            )
         );
         drawRoundedRect(
            matrices,
            f,
            f2
               - 1.0F,
            f3,
            8.5F,
            1.0F,
            applyHudAlpha(
               n2
            )
         );
      }
   }

   public static void drawDefaultHudPanel(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n,
      int n2,
      int n3
   ) {
      drawDefaultHudPanel(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         n,
         n2,
         n3,
         true
      );
   }

   public static void drawDefaultHudPanel(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n,
      int n2,
      int n3,
      boolean flag
   ) {
      drawDefaultHudPanel(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         n,
         n2,
         n3,
         flag,
         HudRectStyle.SHADER
      );
   }

   public static void drawDefaultHudPanel(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n,
      int n2,
      int n3,
      boolean flag,
      HudRectStyle style
   ) {
      HudRectStyle style2 = style
            != null
         ? style
         : HudRectStyle.SHADER;
      if (isLiquidGlassHudActive()
         && flag
         )
       {
         int color = ColorUtil.interpolateColor(
            n2,
            n3,
            0.5F
         );
         drawHudLiquidGlass(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            f6,
            f6,
            f5,
            color,
            true,
            style2
         );
      } else if (isLiquidGlassHudEnabled()
         && !flag
         )
       {
         drawRoundedRect(
            matrices,
            f
               - 0.5F,
            f2
               - 0.5F,
            f3
               + 1.0F,
            f4
               + 1.0F,
            f6,
            applyHudAlpha(
               n
            )
         );
         drawGradientRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            applyHudAlpha(
               n2
            ),
            applyHudAlpha(
               n3
            )
         );
         if (style2
               == HudRectStyle.THREADS
            && f3
               >= 22.0F
            && f4
               >= 12.0F) {
            int color2 = ColorUtil.interpolateColor(
               n2,
               n3,
               0.5F
            );
            long time = ThreadRenderer.seedForRect(
               f,
               f2,
               f3,
               f4
            );
            ThreadRenderer.draw(
               matrices,
               f,
               f2,
               f3,
               f4,
               color2,
               time
            );
         }
      } else {
         drawRoundedRect(
            matrices,
            f
               - 0.5F,
            f2
               - 0.5F,
            f3
               + 1.0F,
            f4
               + 1.0F,
            f6,
            applyHudAlpha(
               n
            )
         );
         drawGradientRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            applyHudAlpha(
               n2
            ),
            applyHudAlpha(
               n3
            )
         );
         if (style2
               == HudRectStyle.THREADS
            && f3
               >= 22.0F
            && f4
               >= 12.0F) {
            int color3 = ColorUtil.interpolateColor(
               n2,
               n3,
               0.5F
            );
            long time2 = ThreadRenderer.seedForRect(
               f,
               f2,
               f3,
               f4
            );
            ThreadRenderer.draw(
               matrices,
               f,
               f2,
               f3,
               f4,
               color3,
               time2
            );
         }
      }
   }

   public static void drawDefaultHudThemedPanel(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n
   ) {
      drawDefaultHudThemedPanel(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         n,
         HudRectStyle.SHADER
      );
   }

   public static void drawDefaultHudThemedPanel(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n,
      HudRectStyle style
   ) {
      HudRectStyle style2 = style
            != null
         ? style
         : HudRectStyle.SHADER;
      if (isLiquidGlassHudActive()) {
         drawHudLiquidGlass(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            f6,
            f6,
            f5,
            n,
            true,
            style2
         );
      } else {
         drawDefaultHudPanel(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            f6,
            ColorUtil.rgba(0, 0, 0, 255),
            ColorUtil.rgba(0, 0, 0, 255),
            ColorUtil.rgba(0, 0, 0, 255)
         );
         if (style2
               == HudRectStyle.THREADS
            && f3
               >= 22.0F
            && f4
               >= 12.0F) {
            long time = ThreadRenderer.seedForRect(
               f,
               f2,
               f3,
               f4
            );
            ThreadRenderer.draw(
               matrices,
               f,
               f2,
               f3,
               f4,
               n,
               time
            );
         }
      }
   }

   public static void drawWaveHudHeader(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      int n,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6
   ) {
      drawShadow6(
         matrices,
         f,
         f2,
         f3,
         f4,
         f6,
         f7,
         n,
         n2,
         n3,
         n4,
         n5,
         n6
      );
      drawGradient6Rect(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         n,
         n2,
         n3,
         n4,
         n5,
         n6
      );
   }

   public static void drawWaveHudPanel(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n,
      float f5,
      float f6,
      float f7,
      float f8,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6,
      int n7
   ) {
      if (isLiquidGlassHudActive()) {
         int color = ColorUtil.interpolateColor(
            n2,
            n7,
            0.5F
         );
         float x = f2
            + f5;
         float x2 = f4
            - f5;
         if (x2
            > 0.5F) {
            drawHudLiquidGlass(
               matrices,
               f,
               x,
               f3,
               x2,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               color,
               true
            );
         }

         drawWaveHudHeader(
            matrices,
            f,
            f2,
            f3,
            f5,
            f6,
            f7,
            f8,
            n2,
            n3,
            n4,
            n5,
            n6,
            n7
         );
      } else {
         drawRoundedRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            0.0F,
            n
         );
         drawWaveHudHeader(
            matrices,
            f,
            f2,
            f3,
            f5,
            f6,
            f7,
            f8,
            n2,
            n3,
            n4,
            n5,
            n6,
            n7
         );
      }
   }

   public static void drawTargetHudWaveFrame(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7
   ) {
      drawRoundedRect(
         matrices,
         f,
         f2,
         f3,
         f4,
         0.0F,
         ColorUtil.applyAlpha(
            ColorUtil.rgba(
               40,
               40,
               40,
               255
            ),
            f7
         )
      );
      drawRoundedRect(
         matrices,
         f
            + f5,
         f2
            + f5,
         f3
            - f5
               * 2.0F,
         f4
            - f5
               * 2.0F,
         0.0F,
         ColorUtil.applyAlpha(
            ColorUtil.rgba(
               20,
               20,
               20,
               255
            ),
            f7
         )
      );
      drawRoundedRect(
         matrices,
         f
            + f5
            + 2.0F,
         f2
            + f5
            + 2.0F,
         f6,
         f6,
         0.0F,
         ColorUtil.applyAlpha(
            ColorUtil.rgba(
               40,
               40,
               40,
               255
            ),
            f7
         )
      );
      drawRoundedRect(
         matrices,
         f
            + f5
            + 3.0F,
         f2
            + f5
            + 3.0F,
         f6
            - 2.0F,
         f6
            - 2.0F,
         0.0F,
         ColorUtil.applyAlpha(
            ColorUtil.rgba(
               25,
               25,
               25,
               255
            ),
            f7
         )
      );
   }

   public static void drawTargetHudDefaultPlaceholder(
      MatrixStack matrices,
      float f,
      float f2,
      float f3
   ) {
      drawRoundedRect(
         matrices,
         f
            - 1.0F,
         f2
            - 1.0F,
         22.0F,
         22.0F,
         1.0F,
         ColorUtil.applyAlpha(
            ColorUtil.rgba(
               21,
               21,
               21,
               255
            ),
            f3
         )
      );
   }

   public static void drawTargetHudHealthBars(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      int n2,
      float f6
   ) {
      drawRoundedRect(
         matrices,
         f,
         f2,
         f3,
         5.5F,
         1.25F,
         ColorUtil.applyAlpha(
            ColorUtil.darken(
               n,
               0.5F
            ),
            f6
               * 0.8F
         )
      );
      drawRoundedRect(
         matrices,
         f,
         f2,
         f3
            * f4,
         5.5F,
         1.25F,
         ColorUtil.applyAlpha(
            ColorUtil.darken(
               n,
               0.8F
            ),
            f6
               * 0.8F
         )
      );
      drawGradientRect(
         matrices,
         f,
         f2,
         f3
            * f5,
         5.5F,
         1.25F,
         ColorUtil.applyAlpha(
            n,
            f6
         ),
         ColorUtil.applyAlpha(
            n2,
            f6
         ),
         true
      );
   }

   public static void drawTargetHudGoldenBars(
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
      int color = ColorUtil.rgba(
         255,
         215,
         0,
         255
      );
      drawRoundedRect(
         matrices,
         f,
         f2,
         f3
            * f5,
         f4,
         1.25F,
         ColorUtil.applyAlpha(
            ColorUtil.darken(
               color,
               0.65F
            ),
            f7
               * f8
               * 0.8F
         )
      );
      drawGradientRect(
         matrices,
         f,
         f2,
         f3
            * f6,
         f4,
         1.25F,
         ColorUtil.applyAlpha(
            ColorUtil.darken(
               color,
               0.55F
            ),
            f7
               * f8
         ),
         ColorUtil.applyAlpha(
            color,
            f7
               * f8
         ),
         true
      );
   }

   public static void drawTargetHudHeartBase(
      MatrixStack matrices,
      float f,
      float f2,
      float f3
   ) {
      drawRoundedRect(
         matrices,
         f,
         f2,
         6.2F,
         4.5F,
         0.0F,
         ColorUtil.applyAlpha(
            ColorUtil.rgba(0, 0, 0, 255),
            f3
         )
      );
   }

   public static void drawTargetHudHeartFill(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      int n,
      int n2
   ) {
      drawShadow(
         matrices,
         f
            + 1.0F,
         f2
            + 1.0F,
         f3,
         2.0F,
         0.0F,
         8.0F,
         n2
      );
      drawRoundedRect(
         matrices,
         f,
         f2,
         f3
            + 1.2F,
         4.5F,
         0.0F,
         n
      );
   }

   public static void drawKeyStrokeRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n
   ) {
      if (isLiquidGlassHudActive()) {
         drawHudLiquidGlass(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            ColorUtil.getThemeColor(),
            true
         );
      } else {
         drawRoundedRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            n
         );
      }
   }

   public static void drawRoundedRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      ShaderProgram val = RenderSystem.setShader(
         ClientShaders.roundedRect
      );
      if (val
         == null) {
         drawRoundedRectFallback(
            matrices,
            f,
            f2,
            f3,
            f4,
            n
         );
      } else {
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         GlUniform val2 = val.getUniform(
            "Size"
         );
         GlUniform val3 = val.getUniform(
            "Radius"
         );
         GlUniform val4 = val.getUniform(
            "Smoothness"
         );
         float x = 1.0F;
         if (val2
            != null) {
            val2.set(
               f3,
               f4
            );
         }

         if (val3
            != null) {
            val3.set(
               f5,
               f8,
               f6,
               f7
            );
         }

         if (val4
            != null) {
            val4.set(
               x
            );
         }

         int color2 = n
               >> 24
            & 255;
         if (color2
            == 0) {
            color2 = 255;
         }

         float x2 = (float)(
               n
                     >> 16
                  & 255
            )
            / 255.0F;
         float x3 = (float)(
               n
                     >> 8
                  & 255
            )
            / 255.0F;
         float x4 = (float)(
               n
                  & 255
            )
            / 255.0F;
         float x5 = (float)color2
            / 255.0F;
         float x6 = -x
               / 2.0F
            + x
               * 2.0F;
         float x7 = x
               / 2.0F
            + x;
         float x8 = f
            - x6
               / 2.0F;
         float x9 = f2
            - x7
               / 2.0F;
         float x10 = f3
            + x6;
         float x11 = f4
            + x7;
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         buffer.vertex(
               matrix,
               x8,
               x9,
               0.0F
            )
            .color(
               x2,
               x3,
               x4,
               x5
            );
         buffer.vertex(
               matrix,
               x8,
               x9
                  + x11,
               0.0F
            )
            .color(
               x2,
               x3,
               x4,
               x5
            );
         buffer.vertex(
               matrix,
               x8
                  + x10,
               x9
                  + x11,
               0.0F
            )
            .color(
               x2,
               x3,
               x4,
               x5
            );
         buffer.vertex(
               matrix,
               x8
                  + x10,
               x9,
               0.0F
            )
            .color(
               x2,
               x3,
               x4,
               x5
            );
         drawBuffer(
            buffer
         );
         RenderSystem.disableBlend();
      }
   }

   private static void drawRoundedRectFallback(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
      int color2 = n
            >> 24
         & 255;
      if (color2
         == 0) {
         color2 = 255;
      }

      float x = (float)(
            n
                  >> 16
               & 255
         )
         / 255.0F;
      float x2 = (float)(
            n
                  >> 8
               & 255
         )
         / 255.0F;
      float x3 = (float)(
            n
               & 255
         )
         / 255.0F;
      float x4 = (float)color2
         / 255.0F;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      buffer.vertex(
            matrix,
            f,
            f2,
            0.0F
         )
         .color(
            x,
            x2,
            x3,
            x4
         );
      buffer.vertex(
            matrix,
            f,
            f2
               + f4,
            0.0F
         )
         .color(
            x,
            x2,
            x3,
            x4
         );
      buffer.vertex(
            matrix,
            f
               + f3,
            f2
               + f4,
            0.0F
         )
         .color(
            x,
            x2,
            x3,
            x4
         );
      buffer.vertex(
            matrix,
            f
               + f3,
            f2,
            0.0F
         )
         .color(
            x,
            x2,
            x3,
            x4
         );
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      drawBuffer(
         buffer
      );
   }

   public static void drawRoundCircle(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      int n
   ) {
      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
      drawRoundedRect(
         matrices,
         f
            - f3
               / 2.0F,
         f2
            - f3
               / 2.0F,
         f3,
         f3,
         f3
               / 2.0F
            - 0.5F,
         n
      );
   }

   public static void drawRingArc(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n
   ) {
      if (!(
            f3
               <= 0.0F
         )
         && !(
            f4
               <= 0.0F
         )) {
         float x = f3
            / 2.0F;
         float x2 = (float)Math.toRadians(
            (double)f5
         );
         float x3 = (float)Math.toRadians(
            (double)f6
         );
         float x4 = (float) (Math.PI * 2);
         if (x2
            < 0.0F) {
            x2 += x4;
         }

         if (x3
            < 0.0F) {
            x3 += x4;
         }

         while (
            x3
               < x2
         ) {
            x3 += x4;
         }

         if (x3
               - x2
            <= 1.0E-4F) {
            x3 = x2
               + x4;
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         ShaderProgram val = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.ringArc);
         GlUniform val2 = val.getUniform(
            "Size"
         );
         GlUniform val3 = val.getUniform(
            "Radius"
         );
         GlUniform val4 = val.getUniform(
            "Thickness"
         );
         GlUniform val5 = val.getUniform(
            "StartAngle"
         );
         GlUniform val6 = val.getUniform(
            "EndAngle"
         );
         GlUniform val7 = val.getUniform(
            "Smoothness"
         );
         GlUniform val8 = val.getUniform(
            "ColorModulator"
         );
         if (val2
            != null) {
            val2.set(
               f3,
               f3
            );
         }

         if (val3
            != null) {
            val3.set(
               x
            );
         }

         if (val4
            != null) {
            val4.set(
               f4
            );
         }

         if (val5
            != null) {
            val5.set(
               x2
            );
         }

         if (val6
            != null) {
            val6.set(
               x3
            );
         }

         if (val7
            != null) {
            val7.set(
               Math.min(
                  1.0F,
                  f4
                     * 0.5F
               )
            );
         }

         if (val8
            != null) {
            val8.set(
               1.0F, 1.0F, 1.0F, 1.0F
            );
         }

         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         int color2 = n
               >> 24
            & 255;
         if (color2
            == 0) {
            color2 = 255;
         }

         float x5 = (float)(
               n
                     >> 16
                  & 255
            )
            / 255.0F;
         float x6 = (float)(
               n
                     >> 8
                  & 255
            )
            / 255.0F;
         float x7 = (float)(
               n
                  & 255
            )
            / 255.0F;
         float x8 = (float)color2
            / 255.0F;
         buffer.vertex(
               matrix,
               f,
               f2,
               0.0F
            )
            .color(
               x5,
               x6,
               x7,
               x8
            );
         buffer.vertex(
               matrix,
               f,
               f2
                  + f3,
               0.0F
            )
            .color(
               x5,
               x6,
               x7,
               x8
            );
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2
                  + f3,
               0.0F
            )
            .color(
               x5,
               x6,
               x7,
               x8
            );
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2,
               0.0F
            )
            .color(
               x5,
               x6,
               x7,
               x8
            );
         RenderSystem.setShader(ClientShaders.ringArc);
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         RenderSystem.disableBlend();
      }
   }

   public static void drawGradientRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      ShaderProgram val = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.gradientRect);
      if (val
         == null) {
         drawRoundedRectFallback(
            matrices,
            f,
            f2,
            f3,
            f4,
            n
         );
      } else {
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
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
            "ColorModulator"
         );
         GlUniform val6 = val.getUniform(
            "TopLeftColor"
         );
         GlUniform val7 = val.getUniform(
            "BottomLeftColor"
         );
         GlUniform val8 = val.getUniform(
            "TopRightColor"
         );
         GlUniform val9 = val.getUniform(
            "BottomRightColor"
         );
         if (val2
            != null) {
            val2.set(
               f3,
               f4
            );
         }

         if (val3
            != null) {
            val3.set(
               f5,
               f8,
               f6,
               f7
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
               1.0F, 1.0F, 1.0F, 1.0F
            );
         }

         int color2 = n
               >> 24
            & 255;
         if (val6
            != null) {
            val6.set(
               (float)(
                     n
                           >> 16
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n
                           >> 8
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n
                        & 255
                  )
                  / 255.0F,
               (float)color2
                  / 255.0F
            );
         }

         int color3 = n3
               >> 24
            & 255;
         if (val7
            != null) {
            val7.set(
               (float)(
                     n3
                           >> 16
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n3
                           >> 8
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n3
                        & 255
                  )
                  / 255.0F,
               (float)color3
                  / 255.0F
            );
         }

         int color4 = n2
               >> 24
            & 255;
         if (val8
            != null) {
            val8.set(
               (float)(
                     n2
                           >> 16
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n2
                           >> 8
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n2
                        & 255
                  )
                  / 255.0F,
               (float)color4
                  / 255.0F
            );
         }

         int color5 = n4
               >> 24
            & 255;
         if (val9
            != null) {
            val9.set(
               (float)(
                     n4
                           >> 16
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n4
                           >> 8
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n4
                        & 255
                  )
                  / 255.0F,
               (float)color5
                  / 255.0F
            );
         }

         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(
               matrix,
               f,
               f2,
               0.0F
            )
            .texture(0.0F, 0.0F)
            .color(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.vertex(
               matrix,
               f,
               f2
                  + f4,
               0.0F
            )
            .texture(0.0F, 1.0F)
            .color(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2
                  + f4,
               0.0F
            )
            .texture(1.0F, 1.0F)
            .color(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2,
               0.0F
            )
            .texture(1.0F, 0.0F)
            .color(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShader(ClientShaders.gradientRect);
         drawBuffer(
            buffer
         );
      }
   }

   public static void drawGradientRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      drawGradientRect(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f5,
         f5,
         f5,
         n,
         n2,
         n3,
         n4
      );
   }

   public static void drawGradientRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      int n2
   ) {
      drawGradientRect(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f5,
         f5,
         f5,
         n,
         n,
         n2,
         n2
      );
   }

   public static void drawGradientRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n,
      int n2
   ) {
      drawGradientRect(
         matrices,
         f,
         f2,
         f3,
         f4,
         0.0F,
         0.0F,
         0.0F,
         0.0F,
         n,
         n,
         n2,
         n2
      );
   }

   public static void drawGradientRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      int n2,
      boolean flag
   ) {
      if (flag
         )
       {
         drawGradientRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            f5,
            f5,
            f5,
            n,
            n2,
            n,
            n2
         );
      } else {
         drawGradientRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            f5,
            f5,
            f5,
            f5,
            n,
            n,
            n2,
            n2
         );
      }
   }

   public static void drawRoundedRectOutline(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      int n
   ) {
      drawRoundedRectOutline(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         f7,
         f8,
         f9,
         n,
         n,
         n,
         n
      );
   }

   public static void drawRoundedRectOutline(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      drawRoundedRectOutline(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f5,
         f5,
         f5,
         f6,
         n,
         n2,
         n3,
         n4
      );
   }

   public static void drawRoundedRectOutline(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      if (!(
         f9
            <= 0.0F
      )) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         ShaderProgram val = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.roundedRectOutline);
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
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
            "ColorModulator"
         );
         GlUniform val6 = val.getUniform(
            "Outline"
         );
         GlUniform val7 = val.getUniform(
            "TopLeftColor"
         );
         GlUniform val8 = val.getUniform(
            "BottomLeftColor"
         );
         GlUniform val9 = val.getUniform(
            "TopRightColor"
         );
         GlUniform val10 = val.getUniform(
            "BottomRightColor"
         );
         if (val2
            != null) {
            val2.set(
               f3,
               f4
            );
         }

         if (val3
            != null) {
            val3.set(
               f5,
               f8,
               f6,
               f7
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
               1.0F, 1.0F, 1.0F, 1.0F
            );
         }

         if (val6
            != null) {
            val6.set(
               f9
            );
         }

         if (val7
            != null) {
            int color2 = n
                  >> 24
               & 255;
            if (color2
               == 0) {
               color2 = 255;
            }

            val7.set(
               (float)(
                     n
                           >> 16
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n
                           >> 8
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n
                        & 255
                  )
                  / 255.0F,
               (float)color2
                  / 255.0F
            );
         }

         if (val8
            != null) {
            int color3 = n3
                  >> 24
               & 255;
            if (color3
               == 0) {
               color3 = 255;
            }

            val8.set(
               (float)(
                     n3
                           >> 16
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n3
                           >> 8
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n3
                        & 255
                  )
                  / 255.0F,
               (float)color3
                  / 255.0F
            );
         }

         if (val9
            != null) {
            int color4 = n2
                  >> 24
               & 255;
            if (color4
               == 0) {
               color4 = 255;
            }

            val9.set(
               (float)(
                     n2
                           >> 16
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n2
                           >> 8
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n2
                        & 255
                  )
                  / 255.0F,
               (float)color4
                  / 255.0F
            );
         }

         if (val10
            != null) {
            int color5 = n4
                  >> 24
               & 255;
            if (color5
               == 0) {
               color5 = 255;
            }

            val10.set(
               (float)(
                     n4
                           >> 16
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n4
                           >> 8
                        & 255
                  )
                  / 255.0F,
               (float)(
                     n4
                        & 255
                  )
                  / 255.0F,
               (float)color5
                  / 255.0F
            );
         }

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
         RenderSystem.setShader(ClientShaders.roundedRectOutline);
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         RenderSystem.disableBlend();
      }
   }

   public static void drawBlur(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n
   ) {
      if (KawaseBlur.getBuffer2() != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         ShaderProgram val = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.roundedTexture);
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
            "ColorModulator"
         );
         if (val2
            != null) {
            val2.set(
               f3,
               f4
            );
         }

         if (val3
            != null) {
            val3.set(
               f5,
               f8,
               f6,
               f7
            );
         }

         if (val4
            != null) {
            val4.set(
               0.5F
            );
         }

         if (val5
            != null) {
            val5.set(
               1.0F, 1.0F, 1.0F, 1.0F
            );
         }

         RenderSystem.setShaderTexture(0, KawaseBlur.getTexture());
         RenderSystem.setShader(ClientShaders.roundedTexture);
         int color2 = mc.getWindow()
            .getScaledWidth();
         int color3 = mc.getWindow()
            .getScaledHeight();
         float width = f
            / (float)color2;
         float width2 = (
               (float)color3
                  - f2
            )
            / (float)color3;
         float width3 = (
               f
                  + f3
            )
            / (float)color2;
         float width4 = (
               (float)color3
                  - f2
                  - f4
            )
            / (float)color3;
         int color4 = n
               >> 24
            & 255;
         if (color4
            == 0) {
            color4 = 255;
         }

         float width5 = (float)(
               n
                     >> 16
                  & 255
            )
            / 255.0F;
         float width6 = (float)(
               n
                     >> 8
                  & 255
            )
            / 255.0F;
         float width7 = (float)(
               n
                  & 255
            )
            / 255.0F;
         float width8 = (float)color4
            / 255.0F;
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(
               matrix,
               f,
               f2,
               0.0F
            )
            .texture(
               width,
               width2
            )
            .color(
               width5,
               width6,
               width7,
               width8
            );
         buffer.vertex(
               matrix,
               f,
               f2
                  + f4,
               0.0F
            )
            .texture(
               width,
               width4
            )
            .color(
               width5,
               width6,
               width7,
               width8
            );
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2
                  + f4,
               0.0F
            )
            .texture(
               width3,
               width4
            )
            .color(
               width5,
               width6,
               width7,
               width8
            );
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2,
               0.0F
            )
            .texture(
               width3,
               width2
            )
            .color(
               width5,
               width6,
               width7,
               width8
            );
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         RenderSystem.setShaderTexture(0, 0);
         RenderSystem.disableBlend();
      }
   }

   public static void drawBlur(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n
   ) {
      drawBlur(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f5,
         f5,
         f5,
         n
      );
   }

   public static void startGlow(
      float f,
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

      GlowRenderer.getInstance()
         .begin(
            f,
            new Color(
               color2,
               color3,
               color4,
               color
            )
         );
      val.render();
      GlowRenderer.getInstance()
         .end(
            matrices,
            val
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

      GlowRenderer.getInstance()
         .begin(
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
      GlowRenderer.getInstance()
         .end(
            matrices,
            val
         );
   }

   public static void drawBlur(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      int n
   ) {
      KawaseBlur.getInstance().request();
      if (KawaseBlur.getBuffer2() != null) {
         KawaseBlur.getInstance()
            .setBlurOffset(
               f9
            );
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         ShaderProgram val = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.roundedTexture);
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
            "ColorModulator"
         );
         if (val2
            != null) {
            val2.set(
               f3,
               f4
            );
         }

         if (val3
            != null) {
            val3.set(
               f5,
               f8,
               f6,
               f7
            );
         }

         if (val4
            != null) {
            val4.set(
               0.5F
            );
         }

         if (val5
            != null) {
            val5.set(
               1.0F, 1.0F, 1.0F, 1.0F
            );
         }

         RenderSystem.setShaderTexture(0, KawaseBlur.getTexture());
         RenderSystem.setShader(ClientShaders.roundedTexture);
         int color2 = mc.getWindow()
            .getScaledWidth();
         int color3 = mc.getWindow()
            .getScaledHeight();
         float width = f
            / (float)color2;
         float width2 = (
               (float)color3
                  - f2
            )
            / (float)color3;
         float width3 = (
               f
                  + f3
            )
            / (float)color2;
         float width4 = (
               (float)color3
                  - f2
                  - f4
            )
            / (float)color3;
         int color4 = n
               >> 24
            & 255;
         if (color4
            == 0) {
            color4 = 255;
         }

         float width5 = (float)(
               n
                     >> 16
                  & 255
            )
            / 255.0F;
         float width6 = (float)(
               n
                     >> 8
                  & 255
            )
            / 255.0F;
         float width7 = (float)(
               n
                  & 255
            )
            / 255.0F;
         float width8 = (float)color4
            / 255.0F;
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(
               matrix,
               f,
               f2,
               0.0F
            )
            .texture(
               width,
               width2
            )
            .color(
               width5,
               width6,
               width7,
               width8
            );
         buffer.vertex(
               matrix,
               f,
               f2
                  + f4,
               0.0F
            )
            .texture(
               width,
               width4
            )
            .color(
               width5,
               width6,
               width7,
               width8
            );
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2
                  + f4,
               0.0F
            )
            .texture(
               width3,
               width4
            )
            .color(
               width5,
               width6,
               width7,
               width8
            );
         buffer.vertex(
               matrix,
               f
                  + f3,
               f2,
               0.0F
            )
            .texture(
               width3,
               width2
            )
            .color(
               width5,
               width6,
               width7,
               width8
            );
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         RenderSystem.setShaderTexture(0, 0);
         RenderSystem.disableBlend();
      }
   }

   public static void drawBlur(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n
   ) {
      drawBlur(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f5,
         f5,
         f5,
         f6,
         n
      );
   }

   public static void drawLiquidGlass(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n,
      float f9,
      float f10,
      int n2,
      float f11,
      boolean flag,
      float f12,
      float f13,
      float f14,
      boolean flag2
   ) {
      int color2;
      if (flag2
         )
       {
         color2 = mc.getFramebuffer()
            .getColorAttachment();
      } else {
         KawaseBlur.getInstance().request();
         if (KawaseBlur.getBuffer1() == null) {
            return;
         }

         color2 = KawaseBlur.getTexture();
         if (color2
            == 0) {
            return;
         }
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.setShaderTexture(
         0,
         color2
      );
      ShaderProgram val = mc.getShaderLoader()
         .getOrCreateProgram(ClientShaders.liquidGlass);
      GlUniform val2 = val.getUniform(
         "GlobalAlpha"
      );
      GlUniform val3 = val.getUniform(
         "Size"
      );
      GlUniform val4 = val.getUniform(
         "Radius"
      );
      GlUniform val5 = val.getUniform(
         "Smoothness"
      );
      GlUniform val6 = val.getUniform(
         "FresnelPower"
      );
      GlUniform val7 = val.getUniform(
         "FresnelColor"
      );
      GlUniform val8 = val.getUniform(
         "FresnelAlpha"
      );
      GlUniform val9 = val.getUniform(
         "BaseAlpha"
      );
      GlUniform val10 = val.getUniform(
         "FresnelInvert"
      );
      GlUniform val11 = val.getUniform(
         "FresnelMix"
      );
      GlUniform val12 = val.getUniform(
         "DistortStrength"
      );
      GlUniform val13 = val.getUniform(
         "CornerSmoothness"
      );
      GlUniform val14 = val.getUniform(
         "MotionVector"
      );
      GlUniform val15 = val.getUniform(
         "MotionStrength"
      );
      float width = UpdateHelper_1.getMotionAmount();
      float width2 = width
         * (
            0.0045F
               + f13
                  * 1.35F
         );
      float width3 = UpdateHelper_1.getMotionVelX();
      float width4 = UpdateHelper_1.getMotionVelY();
      if (val2
         != null) {
         val2.set(
            f9
         );
      }

      if (val3
         != null) {
         val3.set(
            f3,
            f4
         );
      }

      if (val4
         != null) {
         val4.set(
            f5,
            f8,
            f6,
            f7
         );
      }

      if (val5
         != null) {
         val5.set(
            0.5F
         );
      }

      if (val6
         != null) {
         val6.set(
            f10
         );
      }

      int color3 = n2
            >> 24
         & 255;
      if (color3
         == 0) {
         color3 = 255;
      }

      if (val7
         != null) {
         val7.set(
            (float)(
                  n2
                        >> 16
                     & 255
               )
               / 255.0F,
            (float)(
                  n2
                        >> 8
                     & 255
               )
               / 255.0F,
            (float)(
                  n2
                     & 255
               )
               / 255.0F
         );
      }

      if (val8
         != null) {
         val8.set(
            (float)color3
               / 255.0F
         );
      }

      if (val9
         != null) {
         val9.set(
            f11
         );
      }

      if (val10
         != null) {
         val10.set(
            flag
               ? 1
               : 0
         );
      }

      if (val11
         != null) {
         val11.set(
            f12
         );
      }

      if (val12
         != null) {
         val12.set(
            f13
         );
      }

      if (val13
         != null) {
         val13.set(
            f14
         );
      }

      if (val14
         != null) {
         val14.set(
            width3,
            width4
         );
      }

      if (val15
         != null) {
         val15.set(
            width2
         );
      }

      Matrix4f matrix = matrices.peek()
         .getPositionMatrix();
      float width5 = (float)mc.getWindow()
         .getFramebufferWidth();
      float width6 = (float)mc.getWindow()
         .getFramebufferHeight();
      float width7 = (float)mc.getWindow()
         .getScaleFactor();
      float width8 = f
         * width7;
      float width9 = f2
         * width7;
      float width10 = f3
         * width7;
      float width11 = f4
         * width7;
      float width12 = width8
         / width5;
      float width13 = 1.0F
         - width9
            / width6;
      float width14 = (
            width8
               + width10
         )
         / width5;
      float width15 = 1.0F
         - (
               width9
                  + width11
            )
            / width6;
      int color4 = n
            >> 24
         & 255;
      if (color4
         == 0) {
         color4 = 255;
      }

      float width16 = (float)(
            n
                  >> 16
               & 255
         )
         / 255.0F;
      float width17 = (float)(
            n
                  >> 8
               & 255
         )
         / 255.0F;
      float width18 = (float)(
            n
               & 255
         )
         / 255.0F;
      float width19 = (float)color4
         / 255.0F;
      RenderSystem.setShader(ClientShaders.liquidGlass);
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      buffer.vertex(
            matrix,
            f,
            f2,
            0.0F
         )
         .texture(
            width12,
            width13
         )
         .color(
            width16,
            width17,
            width18,
            width19
         );
      buffer.vertex(
            matrix,
            f,
            f2
               + f4,
            0.0F
         )
         .texture(
            width12,
            width15
         )
         .color(
            width16,
            width17,
            width18,
            width19
         );
      buffer.vertex(
            matrix,
            f
               + f3,
            f2
               + f4,
            0.0F
         )
         .texture(
            width14,
            width15
         )
         .color(
            width16,
            width17,
            width18,
            width19
         );
      buffer.vertex(
            matrix,
            f
               + f3,
            f2,
            0.0F
         )
         .texture(
            width14,
            width13
         )
         .color(
            width16,
            width17,
            width18,
            width19
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   @Generated
   private Render2D() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
