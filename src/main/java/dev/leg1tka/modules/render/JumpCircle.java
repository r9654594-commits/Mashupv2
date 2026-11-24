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
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class JumpCircle extends Module {
   public static JumpCircle INSTANCE = new JumpCircle();
   private static final float MAX_LIFETIME_MS = 1850.0F;
   private static final float ROTATION_SPEED = 120.0F;
   private static final float PULSE_SPEED = 7.0F;
   private static final float PULSE_SCALE = 0.06F;
   private static final float PULSE_ALPHA = 0.12F;
   private static final int MAX_CIRCLES = 8;
   private final NumberSetting radius = new NumberSetting("Радиус", 1.85F, 0.5F, 4.0F, 0.1F);
   private final NumberSetting speed = new NumberSetting("Скорость", 1.2F, 1.0F, 5.0F, 0.1F);
   private final NumberSetting fadeSpeed = new NumberSetting("Скорость исчезновения", 1.5F, 1.0F, 5.0F, 0.5F);
   private final List<PosHelper_3> circles = new ArrayList<>();
   private final Identifier circleTexture = ClientIdentifier.of("textures/jumpcircle/circle.png");
   private boolean wasOnGround = true;

   public JumpCircle() {
      super("JumpCircle", "Круг при прыжке", Category.RENDER);
      this.addSettings(new Setting[]{
         this.radius,
         this.speed,
         this.fadeSpeed
      });
   }

   @Override
   public void onEnable() {
      if (mc.player != null) {
         this.wasOnGround = mc.player.isOnGround();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.circles.clear();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val9
   ) {
      if (mc.player != null && mc.world != null) {
         boolean flag = mc.player
            .isOnGround();
         if (this.wasOnGround
            && !flag
            )
          {
            Vec3d vec = new Vec3d(
               mc.player.getX(), Math.floor(mc.player.getY()) + 0.001, mc.player.getZ()
            );
            this.circles
               .add(
                  new PosHelper_3(
                     vec,
                     System.currentTimeMillis()
                  )
               );

            while (this.circles.size() > 8) {
               this.circles.remove(0);
            }
         }

         this.wasOnGround = flag;
         long val = System.currentTimeMillis();
         float f = this.getLifeTimeMs();
         Iterator<PosHelper_3> val2 = this.circles
            .iterator();

         while (
            val2.hasNext()
         ) {
            PosHelper_3 val3 = val2.next();
            if (val
                  - val3.startTimeMs
               > (long)f
               )
             {
               val2.remove();
            }
         }
      }
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (!this.circles.isEmpty()) {
         long val2 = System.currentTimeMillis();
         Vec3d vec = val.getCamera()
            .getPos();
         MatrixStack matrices = val.getMatrices();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.circleTexture);

         for (PosHelper_3 val3 : this.circles) {
            float f = this.getProgress(
               val2,
               val3
            );
            if (!(
               f
                  >= 1.0F
            )) {
               float f2 = this.getAlpha(
                  f
               );
               if (!(
                  f2
                     <= 0.01F
               )) {
                  this.renderGlowCircle(
                     matrices,
                     vec,
                     val3,
                     f,
                     f2,
                     val2
                  );
               }
            }
         }

         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   private float getLifeTimeMs() {
      return 1850.0F / Math.max(0.25F, this.speed.get());
   }

   private float getProgress(
      long val,
      PosHelper_3 val2
   ) {
      return (float)(
            val
               - val2.startTimeMs
         )
         / this.getLifeTimeMs();
   }

   private float getAlpha(
      float val
   ) {
      float f = MathHelper.clamp(
         val
            * this.fadeSpeed.get(),
         0.0F,
         1.0F
      );
      return 1.0F
         - f;
   }

   private void renderGlowCircle(
      MatrixStack val,
      Vec3d val2,
      PosHelper_3 val3,
      float val4,
      float val5,
      long val6
   ) {
      float f = (float)(
            val6
               - val3.startTimeMs
         )
         / 1000.0F;
      float f2 = easeOutCubic(
         val4
      );
      float f3 = Math.min(
         f2
            * this.radius.get(),
         this.radius.get()
      );
      float f4 = f
         * 120.0F
         * this.speed.get();
      f4 += (float)Math.sin(
            (double)val4
               * Math.PI
               * 2.0
         )
         * 30.0F;
      float f5 = (float)Math.sin(
         (double)(
            f
               * 7.0F
               * this.speed.get()
         )
      );
      float f6 = 1.0F
         + f5
            * 0.06F;
      float f7 = MathHelper.clamp(
         val5
            * (
               1.0F
                  + f5
                     * 0.12F
            ),
         0.0F,
         1.0F
      );
      float f8 = MathHelper.clamp(
         f7
            * 1.25F,
         0.0F,
         1.0F
      );
      float f9 = f3
         * f6;
      int color2 = this.getStableThemeColor();
      int color3 = this.getStableThemeSecondaryColor();
      int color4 = ColorUtil.setAlphaColor(
         color2,
         (int)(
            255.0F
               * f8
         )
      );
      int color5 = ColorUtil.setAlphaColor(
         color3,
         (int)(
            255.0F
               * f8
         )
      );
      int color6 = ColorUtil.setAlphaColor(
         ColorUtil.darken(
            color2,
            0.65F
         ),
         (int)(
            255.0F
               * MathHelper.clamp(
                  f8
                     * 0.9F,
                  0.0F,
                  1.0F
               )
         )
      );
      int color7 = ColorUtil.setAlphaColor(
         ColorUtil.darken(
            color3,
            0.65F
         ),
         (int)(
            255.0F
               * MathHelper.clamp(
                  f8
                     * 0.9F,
                  0.0F,
                  1.0F
               )
         )
      );
      val.push();
      val.translate(
         val3.pos
               .x
            - val2.x,
         val3.pos
               .y
            - val2.y,
         val3.pos
               .z
            - val2.z
      );
      val.multiply(
         RotationAxis.POSITIVE_X.rotationDegrees(90.0F)
      );
      val.multiply(
         RotationAxis.POSITIVE_Z
            .rotationDegrees(
               f4
            )
      );
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      float f10 = f9
         * 0.5F;
      float f11 = f9
         * 1.08F;
      float f12 = f11
         * 0.5F;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      this.addTexturedQuad(
         buffer,
         matrix,
         -f10,
         -f10,
         f10,
         f10,
         color4,
         color5
      );
      this.addTexturedQuad(
         buffer,
         matrix,
         -f12,
         -f12,
         f12,
         f12,
         color6,
         color7
      );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      val.pop();
   }

   private void addTexturedQuad(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      float val6,
      int val7,
      int val8
   ) {
      int n = val7
            >> 16
         & 255;
      int n2 = val7
            >> 8
         & 255;
      int n3 = val7
         & 255;
      int n4 = val7
            >> 24
         & 255;
      int n5 = val8
            >> 16
         & 255;
      int n6 = val8
            >> 8
         & 255;
      int n7 = val8
         & 255;
      int n8 = val8
            >> 24
         & 255;
      val.vertex(
            val2,
            val3,
            val4,
            0.0F
         )
         .texture(0.0F, 1.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val3,
            val6,
            0.0F
         )
         .texture(0.0F, 0.0F)
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val5,
            val6,
            0.0F
         )
         .texture(1.0F, 0.0F)
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val5,
            val4,
            0.0F
         )
         .texture(1.0F, 1.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
   }

   private int getStableThemeColor() {
      return !AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtil.getThemeColor();
   }

   private int getStableThemeSecondaryColor() {
      return !AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtil.getThemeColor(180);
   }

   private static float easeOutCubic(
      float val
   ) {
      float f = 1.0F
         - val;
      return 1.0F
         - f
            * f
            * f;
   }
}
