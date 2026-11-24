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
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventAttackEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Cubes extends Module {
   public static Cubes INSTANCE = new Cubes();
   private static final Identifier GLOW_TEX = ClientIdentifier.of("textures/particle/bloom.png");
   private static final float SPAWN_RADIUS = 12.0F;
   private static final float PARTICLE_SIZE = 0.18F;
   private static final float PARTICLE_SPEED = 0.25F;
   private static final float GLOW_INTENSITY = 1.7F;
   private static final float MAX_RENDER_DISTANCE_SQ = 900.0F;
   private static final byte[][] CUBE_EDGES = new byte[][]{
      {-1, -1, -1, 1, -1, -1},
      {1, -1, -1, 1, -1, 1},
      {1, -1, 1, -1, -1, 1},
      {-1, -1, 1, -1, -1, -1},
      {-1, 1, -1, 1, 1, -1},
      {1, 1, -1, 1, 1, 1},
      {1, 1, 1, -1, 1, 1},
      {-1, 1, 1, -1, 1, -1},
      {-1, -1, -1, -1, 1, -1},
      {1, -1, -1, 1, 1, -1},
      {1, -1, 1, 1, 1, 1},
      {-1, -1, 1, -1, 1, 1}
   };
   private static final byte[][] TRIANGLE_EDGES = new byte[][]{{0, 1}, {0, 2}, {0, 3}, {0, 4}, {1, 2}, {2, 3}, {3, 4}, {4, 1}};
   private static final float[] GLOW_SCALES = new float[]{10.0F, 6.0F, 3.5F};
   private static final float[] GLOW_ALPHA_SCALES = new float[]{0.06F, 0.14F, 0.25F};
   private final ModeSetting animation;
   private final ModeSetting shape;
   private final NumberSetting count;
   private final NumberSetting size;
   private final NumberSetting speed;
   private final List<XHolder_4> cubes;
   private final List<XHolder_4> visibleCubes;
   private final Random random;
   private boolean lastAttackPressed;
   private float cr;
   private float cg;
   private float cb;
   private int updateCounter;

   public Cubes() {
      super(
         "Cubes",
         "3D Кубы по миру",
         Category.RENDER
      );
      String var10003 = "Анимация";
      String var10004 = "Разлет";
      String[] var10005 = new String[2];
      var10005[0] = "Разлет";
      var10005[1] = "Падение";
      this.animation = new ModeSetting(var10003, var10004, var10005);
      var10003 = "Форма";
      var10004 = "Кубы";
      var10005 = new String[2];
      var10005[0] = "Кубы";
      var10005[1] = "Треугольники";
      this.shape = new ModeSetting(var10003, var10004, var10005);
      this.count = new NumberSetting(
         "Количество",
         30.0F,
         5.0F,
         100.0F,
         1.0F
      );
      this.size = new NumberSetting(
         "Размер",
         1.0F,
         0.1F,
         3.0F,
         0.1F
      );
      this.speed = new NumberSetting(
         "Скорость",
         1.0F,
         0.1F,
         5.0F,
         0.1F
      );
      this.cubes = new ArrayList<>();
      this.visibleCubes = new ArrayList<>();
      this.random = new Random();
      this.updateCounter = 0;
      this.addSettings(new Setting[]{
         this.animation,
         this.shape,
         this.count,
         this.size,
         this.speed
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.cubes.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.cubes.clear();
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.player != null && mc.world != null) {
         boolean flag = mc.options
            .attackKey
            .isPressed();
         if (flag
            && !this.lastAttackPressed) {
            this.applyHitImpulseFromCrosshair(
               val.getCamera()
            );
         }

         this.lastAttackPressed = flag;
         this.updateCounter++;
         if (this.updateCounter % 2 == 0) {
            this.updateCubes();
         }

         this.renderCubes(
            val
         );
      }
   }

   @EventLink
   public void onAttack(
      EventAttackEntity val7
   ) {
      if (mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
         this.applyHitImpulseFromCrosshair(mc.gameRenderer.getCamera());
      }
   }

   private void applyHitImpulseFromCrosshair(
      Camera val
   ) {
      if (!this.cubes.isEmpty()
         && val
            != null) {
         Vec3d pos = val.getPos();
         float f = (float)Math.toRadians(
            (double)val.getYaw()
         );
         float f2 = (float)Math.toRadians(
            (double)val.getPitch()
         );
         double d = (double)(
            -MathHelper.sin(
                  f
               )
               * MathHelper.cos(
                  f2
               )
         );
         double d2 = (double)(
            -MathHelper.sin(
               f2
            )
         );
         double d3 = (double)(
            MathHelper.cos(
                  f
               )
               * MathHelper.cos(
                  f2
               )
         );
         XHolder_4 val2 = null;
         double d4 = Double.MAX_VALUE;
         int n = 0;

         for (int i = this.cubes
               .size();
            n
               < i;
            n++
         ) {
            XHolder_4 val3 = this.cubes
               .get(
                  n
               );
            double d5 = val3.x
               - pos.x;
            double d6 = val3.y
               - pos.y;
            double d7 = val3.z
               - pos.z;
            double d8 = d5
                  * d
               + d6
                  * d2
               + d7
                  * d3;
            if (!(
                  d8
                     < 0.0
               )
               && !(
                  d8
                     > 128.0
               )) {
               double d9 = pos.x
                  + d
                     * d8;
               double d10 = pos.y
                  + d2
                     * d8;
               double d11 = pos.z
                  + d3
                     * d8;
               double d12 = val3.x
                  - d9;
               double d13 = val3.y
                  - d10;
               double d14 = val3.z
                  - d11;
               double d15 = d12
                     * d12
                  + d13
                     * d13
                  + d14
                     * d14;
               if (!(
                     d15
                        > 1.32
                  )
                  && !(
                     d8
                        >= d4
                  )) {
                  d4 = d8;
                  val2 = val3;
               }
            }
         }

         if (val2
            != null) {
            double d16 = 0.08
               * (double)this.speed.get();
            val2.vx = (float)(
               (double)val2.vx
                  + d
                     * d16
            );
            val2.vy = (float)(
               (double)val2.vy
                  + d2
                     * d16
                  + 0.02
            );
            val2.vz = (float)(
               (double)val2.vz
                  + d3
                     * d16
            );
         }
      }
   }

   private void updateCubes() {
      int count2 = (int)this.count
         .get();
      int n = this.cubes
         .size();
      if (n
         < count2
         )
       {
         int n2 = Math.min(
            count2
               - n,
            5
         );

         for (int i = 0;
            i
               < n2;
            i++
         ) {
            this.cubes.add(this.spawnCube());
         }
      } else if (n
         > count2
         )
       {
         this.cubes
            .subList(
               count2,
               n
            )
            .clear();
      }

      float f = 0.25F
         * this.speed.get();
      float f2 = 12.0F;
      boolean flag = this.animation
         .is(
            "Падение"
         );
      Vec3d vec = mc.player
         .getPos();
      double d = (double)(
            f2
               * f2
         )
         * 6.25;

      for (int i2 = this.cubes
               .size()
            - 1;
         i2
            >= 0;
         i2--
      ) {
         XHolder_4 val = this.cubes
            .get(
               i2
            );
         if (flag
            )
          {
            val.wobblePhase += 0.06F
               * f;
            val.x = val.x
               + (double)(
                  val.vx
                     * f
               )
               + Math.sin(
                     (double)(
                        val.wobblePhase
                           + val.wobbleOffset
                     )
                  )
                  * 0.0024F
                  * (double)f;
            val.y = val.y
               + (double)(
                  val.vy
                     * f
               );
            val.z = val.z
               + (double)(
                  val.vz
                     * f
               )
               + Math.cos(
                     (double)(
                        val.wobblePhase
                              * 0.8F
                           + val.wobbleOffset
                     )
                  )
                  * 0.002F
                  * (double)f;
            val.vy = Math.max(
               val.vy
                  - 8.0E-5F
                     * f,
               -0.032F
            );
            val.rotX = val.rotX
               + val.rotSpeedX
                  * 0.2F
                  * f;
            val.rotY = val.rotY
               + val.rotSpeedY
                  * 0.2F
                  * f;
            val.rotZ = val.rotZ
               + val.rotSpeedZ
                  * 0.2F
                  * f;
         } else {
            val.x = val.x
               + (double)(
                  val.vx
                     * f
               );
            val.y = val.y
               + (double)(
                  val.vy
                     * f
               );
            val.z = val.z
               + (double)(
                  val.vz
                     * f
               );
            val.rotX = val.rotX
               + val.rotSpeedX
                  * f;
            val.rotY = val.rotY
               + val.rotSpeedY
                  * f;
            val.rotZ = val.rotZ
               + val.rotSpeedZ
                  * f;
            val.vx *= 0.995F;
            val.vy *= 0.995F;
            val.vz *= 0.995F;
         }

         val.life--;
         double d2 = val.x
            - vec.x;
         double d3 = val.y
            - vec.y;
         double d4 = val.z
            - vec.z;
         double d5 = d2
               * d2
            + d3
               * d3
            + d4
               * d4;
         if (val.life
               <= 0
            || d5
               > d
            || flag
               && val.y
                  < vec.y
                     - 2.5) {
            this.cubes
               .remove(
                  i2
               );
            this.cubes.add(this.spawnCube());
         }
      }
   }

   private void renderCubes(
      Event3DRender val
   ) {
      if (mc.player != null) {
         MatrixStack matrices = val.getMatrices();
         Vec3d vec = val.getCamera()
            .getPos();
         Camera camera = val.getCamera();
         float f = 0.18F
            * this.size.get();
         float f2 = 1.7F;
         int color2 = ColorUtil.getThemeColor();
         this.cr = (float)(
               color2
                     >> 16
                  & 255
            )
            / 255.0F;
         this.cg = (float)(
               color2
                     >> 8
                  & 255
            )
            / 255.0F;
         this.cb = (float)(
               color2
                  & 255
            )
            / 255.0F;
         this.visibleCubes.clear();
         float f3 = (float)Math.toRadians(
            (double)camera.getYaw()
         );
         float f4 = (float)Math.toRadians(
            (double)camera.getPitch()
         );
         double d = (double)(
            -MathHelper.sin(
                  f3
               )
               * MathHelper.cos(
                  f4
               )
         );
         double d2 = (double)(
            -MathHelper.sin(
               f4
            )
         );
         double d3 = (double)(
            MathHelper.cos(
                  f3
               )
               * MathHelper.cos(
                  f4
               )
         );
         int n = 0;

         for (int i = this.cubes
               .size();
            n
               < i;
            n++
         ) {
            XHolder_4 val2 = this.cubes
               .get(
                  n
               );
            double d4 = val2.x
               - vec.x;
            double d5 = val2.y
               - vec.y;
            double d6 = val2.z
               - vec.z;
            double d7 = d4
                  * d4
               + d5
                  * d5
               + d6
                  * d6;
            if (!(
                  d7
                     > 900.0
               )
               && !(
                  d4
                           * d
                        + d5
                           * d2
                        + d6
                           * d3
                     < -1.0
               )) {
               val2.renderAlpha = this.getAlpha(
                  val2
               );
               if (!(
                  val2.renderAlpha
                     < 0.01F
               )) {
                  this.visibleCubes
                     .add(
                        val2
                     );
               }
            }
         }

         if (!this.visibleCubes.isEmpty()) {
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            RenderSystem.setShaderTexture(0, GLOW_TEX);
            this.drawGlowBatch(
               matrices,
               camera,
               vec,
               f,
               f2
            );
            RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            boolean flag = this.shape
               .is(
                  "Кубы"
               );
            boolean flag2 = this.shape
               .is(
                  "Треугольники"
               );
            if (flag
               )
             {
               this.drawCubeFacesBatch(
                  matrices,
                  vec,
                  f
               );
            }

            if (flag2
               )
             {
               this.drawTriangleFacesBatch(
                  matrices,
                  vec,
                  f
               );
            }

            RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
            if (flag
               )
             {
               this.drawCubeDashedEdgesBatch(
                  matrices,
                  vec,
                  f
               );
            } else if (flag2
               )
             {
               this.drawTriangleDashedEdgesBatch(
                  matrices,
                  vec,
                  f
               );
            }

            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
         }
      }
   }

   private void drawGlowBatch(
      MatrixStack val,
      Camera val2,
      Vec3d val3,
      float val4,
      float val5
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      int n = 0;

      for (int i = this.visibleCubes
            .size();
         n
            < i;
         n++
      ) {
         XHolder_4 val6 = this.visibleCubes
            .get(
               n
            );
         float f = val6.renderAlpha;
         val.push();
         val.translate(
            val6.x
               - val3.x,
            val6.y
               - val3.y,
            val6.z
               - val3.z
         );
         val.multiply(
            RotationAxis.POSITIVE_Y
               .rotationDegrees(
                  -val2.getYaw()
               )
         );
         val.multiply(
            RotationAxis.POSITIVE_X
               .rotationDegrees(
                  val2.getPitch()
               )
         );
         Matrix4f matrix = val.peek()
            .getPositionMatrix();

         for (int i2 = 0;
            i2
               < 3;
            i2++
         ) {
            float f2 = val4
               * GLOW_SCALES[i2]
               * val5;
            float f3 = f
               * GLOW_ALPHA_SCALES[i2]
               * val5;
            float f4 = f2
               * 0.5F;
            buffer.vertex(
                  matrix,
                  -f4,
                  f4,
                  0.0F
               )
               .texture(0.0F, 1.0F)
               .color(
                  this.cr,
                  this.cg,
                  this.cb,
                  f3
               );
            buffer.vertex(
                  matrix,
                  f4,
                  f4,
                  0.0F
               )
               .texture(1.0F, 1.0F)
               .color(
                  this.cr,
                  this.cg,
                  this.cb,
                  f3
               );
            buffer.vertex(
                  matrix,
                  f4,
                  -f4,
                  0.0F
               )
               .texture(1.0F, 0.0F)
               .color(
                  this.cr,
                  this.cg,
                  this.cb,
                  f3
               );
            buffer.vertex(
                  matrix,
                  -f4,
                  -f4,
                  0.0F
               )
               .texture(0.0F, 0.0F)
               .color(
                  this.cr,
                  this.cg,
                  this.cb,
                  f3
               );
         }

         val.pop();
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private float getAlpha(
      XHolder_4 val
   ) {
      float f = MathHelper.clamp(
         (float)val.life
            / (float)val.maxLife,
         0.0F,
         1.0F
      );
      float f2 = Math.min(
         1.0F,
         (float)(
               val.maxLife
                  - val.life
            )
            / 20.0F
      );
      return f
         * f2;
   }

   private void drawCubeFacesBatch(
      MatrixStack val,
      Vec3d val2,
      float val3
   ) {
      if (this.hasFaceRenderableParticles()) {
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         int n = 0;

         for (int i = this.visibleCubes
               .size();
            n
               < i;
            n++
         ) {
            XHolder_4 val4 = this.visibleCubes
               .get(
                  n
               );
            float f = val4.renderAlpha
               * 0.4F;
            if (!(
               f
                  < 0.01F
            )) {
               val.push();
               val.translate(
                  val4.x
                     - val2.x,
                  val4.y
                     - val2.y,
                  val4.z
                     - val2.z
               );
               val.multiply(
                  RotationAxis.POSITIVE_X
                     .rotationDegrees(
                        val4.rotX
                     )
               );
               val.multiply(
                  RotationAxis.POSITIVE_Y
                     .rotationDegrees(
                        val4.rotY
                     )
               );
               val.multiply(
                  RotationAxis.POSITIVE_Z
                     .rotationDegrees(
                        val4.rotZ
                     )
               );
               this.appendCubeFaces(
                  buffer,
                  val.peek()
                     .getPositionMatrix(),
                  val3,
                  f
               );
               val.pop();
            }
         }

         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
      }
   }

   private void drawTriangleFacesBatch(
      MatrixStack val,
      Vec3d val2,
      float val3
   ) {
      if (this.hasFaceRenderableParticles()) {
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
         int n = 0;

         for (int i = this.visibleCubes
               .size();
            n
               < i;
            n++
         ) {
            XHolder_4 val4 = this.visibleCubes
               .get(
                  n
               );
            float f = val4.renderAlpha
               * 0.4F;
            if (!(
               f
                  < 0.01F
            )) {
               val.push();
               val.translate(
                  val4.x
                     - val2.x,
                  val4.y
                     - val2.y,
                  val4.z
                     - val2.z
               );
               val.multiply(
                  RotationAxis.POSITIVE_X
                     .rotationDegrees(
                        val4.rotX
                     )
               );
               val.multiply(
                  RotationAxis.POSITIVE_Y
                     .rotationDegrees(
                        val4.rotY
                     )
               );
               val.multiply(
                  RotationAxis.POSITIVE_Z
                     .rotationDegrees(
                        val4.rotZ
                     )
               );
               this.appendTriangleFaces(
                  buffer,
                  val.peek()
                     .getPositionMatrix(),
                  val3,
                  f
               );
               val.pop();
            }
         }

         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
      }
   }

   private boolean hasFaceRenderableParticles() {
      int n = 0;

      for (int i = this.visibleCubes
            .size();
         n
            < i;
         n++
      ) {
         if (this.visibleCubes
                  .get(
                     n
                  )
               .renderAlpha
            >= 0.025F) {
            return true;
         }
      }

      return false;
   }

   private void appendCubeFaces(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4
   ) {
      val.vertex(
            val2,
            -val3,
            -val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            -val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            -val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            -val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            -val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            -val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            -val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            -val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            -val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            -val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            val3,
            val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            -val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            -val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            val3,
            val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -val3,
            val3,
            -val3
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
   }

   private void appendTriangleFaces(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4
   ) {
      float f = -val3;
      float f2 = val3
         * 0.866F;
      val.vertex(
            val2,
            0.0F,
            val3,
            0.0F
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -f2,
            f,
            f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            f2,
            f,
            f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            0.0F,
            val3,
            0.0F
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            f2,
            f,
            f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            f2,
            f,
            -f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            0.0F,
            val3,
            0.0F
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            f2,
            f,
            -f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -f2,
            f,
            -f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            0.0F,
            val3,
            0.0F
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -f2,
            f,
            -f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -f2,
            f,
            f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -f2,
            f,
            f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            f2,
            f,
            f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            f2,
            f,
            -f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -f2,
            f,
            f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            f2,
            f,
            -f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
      val.vertex(
            val2,
            -f2,
            f,
            -f2
         )
         .color(
            this.cr,
            this.cg,
            this.cb,
            val4
         );
   }

   private void drawCubeDashedEdgesBatch(
      MatrixStack val,
      Vec3d val2,
      float val3
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      int n = 0;
      int n2 = 0;

      for (int i = this.visibleCubes
            .size();
         n2
            < i;
         n2++
      ) {
         XHolder_4 val4 = this.visibleCubes
            .get(
               n2
            );
         float f = val4.renderAlpha;
         val.push();
         val.translate(
            val4.x
               - val2.x,
            val4.y
               - val2.y,
            val4.z
               - val2.z
         );
         val.multiply(
            RotationAxis.POSITIVE_X
               .rotationDegrees(
                  val4.rotX
               )
         );
         val.multiply(
            RotationAxis.POSITIVE_Y
               .rotationDegrees(
                  val4.rotY
               )
         );
         val.multiply(
            RotationAxis.POSITIVE_Z
               .rotationDegrees(
                  val4.rotZ
               )
         );
         n += this.appendCubeDashedEdges(
            buffer,
            val.peek()
               .getPositionMatrix(),
            val3,
            f
         );
         val.pop();
      }

      if (n
         > 0) {
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
      }
   }

   private void drawTriangleDashedEdgesBatch(
      MatrixStack val,
      Vec3d val2,
      float val3
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      int n = 0;
      int n2 = 0;

      for (int i = this.visibleCubes
            .size();
         n2
            < i;
         n2++
      ) {
         XHolder_4 val4 = this.visibleCubes
            .get(
               n2
            );
         float f = val4.renderAlpha;
         val.push();
         val.translate(
            val4.x
               - val2.x,
            val4.y
               - val2.y,
            val4.z
               - val2.z
         );
         val.multiply(
            RotationAxis.POSITIVE_X
               .rotationDegrees(
                  val4.rotX
               )
         );
         val.multiply(
            RotationAxis.POSITIVE_Y
               .rotationDegrees(
                  val4.rotY
               )
         );
         val.multiply(
            RotationAxis.POSITIVE_Z
               .rotationDegrees(
                  val4.rotZ
               )
         );
         n += this.appendTriangleDashedEdges(
            buffer,
            val.peek()
               .getPositionMatrix(),
            val3,
            f
         );
         val.pop();
      }

      if (n
         > 0) {
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
      }
   }

   private int appendCubeDashedEdges(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4
   ) {
      int color2 = colorToInt(
         Math.min(1.0F, this.cr * 1.5F),
         Math.min(1.0F, this.cg * 1.5F),
         Math.min(1.0F, this.cb * 1.5F),
         val4
      );
      float f = val3
         * 0.3F;
      float f2 = val3
         * 0.25F;
      int n = 0;

      for (byte[] val5 : CUBE_EDGES) {
         float f3 = (float)val5[0]
            * val3;
         float f4 = (float)val5[1]
            * val3;
         float f5 = (float)val5[2]
            * val3;
         float f6 = (float)val5[3]
            * val3;
         float f7 = (float)val5[4]
            * val3;
         float f8 = (float)val5[5]
            * val3;
         float f9 = f6
            - f3;
         float f10 = f7
            - f4;
         float f11 = f8
            - f5;
         float f12 = MathHelper.sqrt(
            f9
                  * f9
               + f10
                  * f10
               + f11
                  * f11
         );
         if (!(
            f12
               < 0.001F
         )) {
            float f13 = f9
               / f12;
            float f14 = f10
               / f12;
            float f15 = f11
               / f12;
            float f16 = 0.0F;

            for (boolean flag = true;
               f16
                  < f12;
               flag = !flag
            ) {
               float f17 = flag
                  ? f
                  : f2;
               float f18 = Math.min(
                  f16
                     + f17,
                  f12
               );
               if (flag
                  )
                {
                  val.vertex(
                        val2,
                        f3
                           + f13
                              * f16,
                        f4
                           + f14
                              * f16,
                        f5
                           + f15
                              * f16
                     )
                     .color(
                        color2
                     );
                  val.vertex(
                        val2,
                        f3
                           + f13
                              * f18,
                        f4
                           + f14
                              * f18,
                        f5
                           + f15
                              * f18
                     )
                     .color(
                        color2
                     );
                  n++;
               }

               f16 = f18;
            }
         }
      }

      return n;
   }

   private int appendTriangleDashedEdges(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4
   ) {
      int color2 = colorToInt(
         Math.min(1.0F, this.cr * 1.5F),
         Math.min(1.0F, this.cg * 1.5F),
         Math.min(1.0F, this.cb * 1.5F),
         val4
      );
      float f = val3
         * 0.3F;
      float f2 = val3
         * 0.25F;
      int n = 0;
      float f3 = val3;
      float f4 = -val3;
      float f5 = val3
         * 0.866F;

      for (byte[] val5 : TRIANGLE_EDGES) {
         float x2 = this.trianglePointX(
            val5[0],
            f5
         );
         float f6 = val5[0]
               == 0
            ? f3
            : f4;
         float f7 = this.trianglePointZ(
            val5[0],
            f5
         );
         float x3 = this.trianglePointX(
            val5[1],
            f5
         );
         float f8 = val5[1]
               == 0
            ? f3
            : f4;
         float f9 = this.trianglePointZ(
            val5[1],
            f5
         );
         float f10 = x3
            - x2;
         float f11 = f8
            - f6;
         float f12 = f9
            - f7;
         float f13 = MathHelper.sqrt(
            f10
                  * f10
               + f11
                  * f11
               + f12
                  * f12
         );
         if (!(
            f13
               < 0.001F
         )) {
            float f14 = f10
               / f13;
            float f15 = f11
               / f13;
            float f16 = f12
               / f13;
            float f17 = 0.0F;

            for (boolean flag = true;
               f17
                  < f13;
               flag = !flag
            ) {
               float f18 = flag
                  ? f
                  : f2;
               float f19 = Math.min(
                  f17
                     + f18,
                  f13
               );
               if (flag
                  )
                {
                  val.vertex(
                        val2,
                        x2
                           + f14
                              * f17,
                        f6
                           + f15
                              * f17,
                        f7
                           + f16
                              * f17
                     )
                     .color(
                        color2
                     );
                  val.vertex(
                        val2,
                        x2
                           + f14
                              * f19,
                        f6
                           + f15
                              * f19,
                        f7
                           + f16
                              * f19
                     )
                     .color(
                        color2
                     );
                  n++;
               }

               f17 = f19;
            }
         }
      }

      return n;
   }

   private float trianglePointX(
      int val,
      float val2
   ) {
      return switch (val) {
         case 1, 4 -> -val2;
         case 2, 3 -> val2;
         default -> 0.0F;
      };
   }

   private float trianglePointZ(
      int val,
      float val2
   ) {
      return switch (val) {
         case 1, 2 -> val2;
         case 3, 4 -> -val2;
         default -> 0.0F;
      };
   }

   private XHolder_4 spawnCube() {
      float f = 12.0F;
      boolean flag = this.animation
         .is(
            "Падение"
         );
      int n = flag
         ? 260
            + this.random.nextInt(220)
         : 420
            + this.random.nextInt(420);
      double d = mc.player
            .getX()
         + (this.random.nextDouble() * 2.0 - 1.0)
            * (double)f;
      double d2 = flag
         ? mc.player.getY()
            + 4.0
            + this.random.nextDouble()
               * (double)f
               * 0.55
         : mc.player.getY()
            + 2.0
            + this.random.nextDouble()
               * (double)f
               * 0.8;
      double d3 = mc.player
            .getZ()
         + (this.random.nextDouble() * 2.0 - 1.0)
            * (double)f;
      float f2 = this.speed
         .get();
      float f3;
      float f4;
      float f5;
      if (flag
         )
       {
         f3 = (
               this.random.nextFloat() - 0.5F
            )
            * 0.008F
            * f2;
         f4 = (
               -0.012F - this.random.nextFloat() * 0.012F
            )
            * f2;
         f5 = (
               this.random.nextFloat() - 0.5F
            )
            * 0.008F
            * f2;
      } else {
         float f6 = this.random
               .nextFloat()
            * 360.0F;
         float f7 = (
               0.01F + this.random.nextFloat() * 0.02F
            )
            * f2;
         f3 = -MathHelper.sin(
               (float)Math.toRadians(
                  (double)f6
               )
            )
            * f7;
         f5 = MathHelper.cos(
               (float)Math.toRadians(
                  (double)f6
               )
            )
            * f7;
         f4 = (
               this.random.nextFloat() - 0.5F
            )
            * 0.01F
            * f2;
      }

      return new XHolder_4(
         d,
         d2,
         d3,
         f3,
         f4,
         f5,
         this.random.nextFloat() * 360.0F,
         this.random.nextFloat() * 360.0F,
         this.random.nextFloat() * 360.0F,
         (this.random.nextFloat() - 0.5F) * 1.5F,
         (this.random.nextFloat() - 0.5F) * 1.5F,
         (this.random.nextFloat() - 0.5F) * 1.5F,
         n,
         (float)(this.random.nextDouble() * Math.PI * 2.0),
         this.random.nextFloat() * 10.0F
      );
   }

   private static int colorToInt(
      float val,
      float val2,
      float val3,
      float val4
   ) {
      return (int)(
               val4
                  * 255.0F
            )
            << 24
         | (int)(
               val
                  * 255.0F
            )
            << 16
         | (int)(
               val2
                  * 255.0F
            )
            << 8
         | (int)(
            val3
               * 255.0F
         );
   }
}
