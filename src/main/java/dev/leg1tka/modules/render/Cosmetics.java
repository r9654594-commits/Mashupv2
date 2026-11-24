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
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Cosmetics extends Module {
   public static Cosmetics INSTANCE = new Cosmetics();
   private static final float PI_STEP = 0.06981317F;
   private static final float WING_SCALE = 1.0F;
   private static final float FLAP_SPEED = 1.6F;
   private static final float FLAP_AMPLITUDE = 25.0F;
   private static final int NIMBUS_ARMS = 2;
   private static final int NIMBUS_SEGMENTS = 17;
   private static final float NIMBUS_RADIUS = 0.45F;
   private static final float NIMBUS_BASE_SIZE = 0.23F;
   private static final double NIMBUS_STEP_RADIANS = 0.11;
   private static final int NIMBUS_MAX_ALPHA = 255;
   private static final int NIMBUS_ALPHA_FALLOFF = 9;
   private static final float NIMBUS_SPEED = 170.0F;
   private static final float CLASSIC_WING_DEFAULT_SPREAD = 8.0F;
   private static final int CLASSIC_WING_DEFAULT_ALPHA = 220;
   private static final XHolder[] CLASSIC_WING_SHAPE = new XHolder[]{
      new XHolder(0.08F, 0.1F, 0.88F),
      new XHolder(0.28F, 0.34F, 0.78F),
      new XHolder(0.56F, 0.82F, 0.62F),
      new XHolder(0.86F, 0.3F, 0.52F),
      new XHolder(1.14F, 0.46F, 0.4F),
      new XHolder(1.24F, 0.04F, 0.3F),
      new XHolder(1.02F, -0.18F, 0.28F),
      new XHolder(1.18F, -0.64F, 0.22F),
      new XHolder(0.86F, -0.46F, 0.2F),
      new XHolder(0.8F, -0.98F, 0.14F),
      new XHolder(0.54F, -0.74F, 0.16F),
      new XHolder(0.3F, -1.16F, 0.12F),
      new XHolder(0.1F, -0.54F, 0.18F)
   };
   private final MultiBooleanSetting cosmetics;
   private final BooleanSetting butterflyWingAnimation;
   private final NumberSetting butterflyWingSize;
   private final BooleanSetting classicWingAnimation;
   private final NumberSetting classicWingSize;
   private float selfClassicBodyYaw;
   private boolean selfClassicBodyYawInitialized;
   private boolean lastButterflySelected;
   private boolean lastClassicSelected;

   public Cosmetics() {
      super("Cosmetics", Category.RENDER);
      String var1 = "Cosmetics";
      String var3 = "Косметика";
      BooleanSetting[] var10004 = new BooleanSetting[5];
      var10004[0] = new BooleanSetting("Нимб", true);
      var10004[1] = new BooleanSetting("Крылья", true);
      int var10006 = 2;
      var10004[var10006] = new BooleanSetting("Крылья 2", false);
      var10006 = 3;
      var10004[var10006] = new BooleanSetting("Китайская шляпа", true);
      var10006 = 4;
      var10004[var10006] = new BooleanSetting("Дилдо воркиза", false);
      this.cosmetics = new MultiBooleanSetting(var3, var10004);
      this.butterflyWingAnimation = new BooleanSetting("Анимация крыльев", true)
         .visible(
            () -> this.cosmetics.is("Крылья")
         );
      this.butterflyWingSize = new NumberSetting("Размер", 1.0F, 0.65F, 1.8F, 0.05F)
         .visible(
            () -> this.cosmetics.is("Крылья")
         );
      this.classicWingAnimation = new BooleanSetting("Анимация крыльев", true)
         .visible(
            () -> this.cosmetics.is("Крылья 2")
         );
      this.classicWingSize = new NumberSetting("Размер", 1.0F, 0.65F, 1.8F, 0.05F)
         .visible(
            () -> this.cosmetics.is("Крылья 2")
         );
      this.addSettings(new Setting[]{
         this.cosmetics,
         this.butterflyWingAnimation,
         this.butterflyWingSize,
         this.classicWingAnimation,
         this.classicWingSize
      });
   }

   @Override
   public void onDisable() {
      this.selfClassicBodyYawInitialized = false;
      this.lastButterflySelected = false;
      this.lastClassicSelected = false;
      super.onDisable();
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.player != null && mc.world != null) {
         this.syncWingSelectionState();
         MultiBooleanSetting var10000 = this.cosmetics;
         if (var10000.is("Нимб")) {
            this.renderNimbus(
               val
            );
         }

         var10000 = this.cosmetics;
         boolean flag = var10000.is(
            "Крылья"
         );
         var10000 = this.cosmetics;
         boolean flag2 = var10000.is(
            "Крылья 2"
         );
         var10000 = this.cosmetics;
         boolean flag3 = var10000.is(
            "Дилдо воркиза"
         );
         if (flag
            || flag2
            || flag3
            )
          {
            float f = val.getTickDelta();
            MatrixStack matrices = val.getMatrices();
            Vec3d vec = val.getCamera()
               .getPos();

            for (PlayerEntity player2 : mc.world
               .getPlayers()) {
               if (this.shouldRenderCosmeticForPlayer(
                     player2
                  )
                  && (
                     player2
                           != mc.player
                        || !mc.options.getPerspective().isFirstPerson()
                  )) {
                  if (flag
                     )
                   {
                     this.renderButterflyWings(
                        player2,
                        f,
                        matrices,
                        vec
                     );
                  }

                  if (flag2
                     )
                   {
                     this.renderClassicWings(
                        player2,
                        f,
                        matrices,
                        vec
                     );
                  }

                  if (flag3
                     )
                   {
                     this.renderBackDildo(
                        player2,
                        f,
                        matrices,
                        vec
                     );
                  }
               }
            }
         }
      }
   }

   private void renderButterflyWings(
      PlayerEntity val,
      float val2,
      MatrixStack val3,
      Vec3d val4
   ) {
      if (!val.isGliding()
         && val.getPose()
            != EntityPose.SWIMMING
         && !val.isInSwimmingPose()
         )
       {
         Vec3d velocity = val.getVelocity();
         float f = MathHelper.lerp(
            val2,
            val.prevBodyYaw,
            val.bodyYaw
         );
         float f2 = f
            * (float) (Math.PI / 180.0);
         Vec3d vec = new Vec3d(
            (double)(
               -MathHelper.sin(
                  f2
               )
            ),
            0.0,
            (double)MathHelper.cos(
               f2
            )
         );
         Vec3d vec2 = new Vec3d(
            vec.z,
            0.0,
            -vec.x
         );
         float f3 = (float)(
            velocity.x
                  * vec.x
               + velocity.z
                  * vec.z
         );
         float f4 = (float)(
            velocity.x
                  * vec2.x
               + velocity.z
                  * vec2.z
         );
         float y2 = (float)velocity.y;
         boolean flag = this.butterflyWingAnimation
            .isState();
         float f5 = flag
            ? MathHelper.clamp(
               -f3
                     * 140.0F
                  - y2
                     * 48.0F,
               -24.0F,
               26.0F
            )
            : 0.0F;
         float f6 = flag
            ? MathHelper.clamp(
               f4
                  * 90.0F,
               -10.0F,
               10.0F
            )
            : 0.0F;
         float f7 = flag
            ? MathHelper.clamp(
               Math.abs(
                        f3
                     )
                     * 0.95F
                  + Math.abs(
                        f4
                     )
                     * 0.65F
                  + Math.abs(
                        y2
                     )
                     * 0.75F,
               0.0F,
               1.7F
            )
            : 0.0F;
         float f8 = (
                  (float)val.age
                     + val2
               )
               * 0.22F
               * 1.6F
            + f7
               * 0.4F;
         float f9 = flag
            ? MathHelper.sin(
               f8
            )
            : 0.0F;
         float f10 = flag
            ? MathHelper.cos(
               f8
            )
            : 0.0F;
         float f11 = 18.0F
            + f7
               * 5.0F;
         float f12 = 13.0F
            + f5
               * 0.3F
            + f10
               * 4.0F;
         float f13 = f9
               * 25.0F
            + f6
               * 0.75F;
         EntityPose entity2 = val.getPose();
         boolean flag2 = val.isGliding();
         boolean flag3 = entity2
               == EntityPose.SWIMMING
            || flag2;
         if (flag3
            )
          {
            f11 -= 4.0F;
            f12 -= 6.0F;
            f13 *= 0.72F;
         }

         if (val.isSneaking()
            )
          {
            f11 -= 3.0F;
            f12 += 8.0F;
         }

         double d = MathHelper.lerp(
               (double)val2,
               val.prevX,
               val.getX()
            )
            - val4.x;
         double d2 = MathHelper.lerp(
               (double)val2,
               val.prevY,
               val.getY()
            )
            - val4.y;
         double d3 = MathHelper.lerp(
               (double)val2,
               val.prevZ,
               val.getZ()
            )
            - val4.z;
         val3.push();
         val3.translate(
            d,
            d2,
            d3
         );
         val3.multiply(
            RotationAxis.POSITIVE_Y
               .rotationDegrees(
                  -f
               )
         );
         this.applyBackPoseTransform(
            val3,
            val,
            val2,
            entity2,
            flag2
         );
         int color2 = this.resolveCosmeticThemeColor();
         int color3 = ColorUtil.setAlphaColor(
            color2,
            132
         );
         int color4 = ColorUtil.setAlphaColor(
            ColorUtil.darken(
               color2,
               0.85F
            ),
            102
         );
         int color5 = ColorUtil.setAlphaColor(
            ColorUtil.darken(
               color2,
               0.58F
            ),
            214
         );
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.blendFunc(
            770,
            771
         );
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         float f14 = 1.0F
            * this.butterflyWingSize.get();
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         this.renderButterflyWing(
            buffer,
            val3,
            1.0F,
            f11,
            f12,
            f13,
            f14,
            color3,
            color4
         );
         this.renderButterflyWing(
            buffer,
            val3,
            -1.0F,
            f11,
            f12,
            f13,
            f14,
            color3,
            color4
         );
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         RenderSystem.lineWidth(1.9F);
         BufferBuilder buffer2 = Tessellator.getInstance()
            .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
         this.renderButterflyWingOutline(
            buffer2,
            val3,
            1.0F,
            f11,
            f12,
            f13,
            f14,
            color5
         );
         this.renderButterflyWingOutline(
            buffer2,
            val3,
            -1.0F,
            f11,
            f12,
            f13,
            f14,
            color5
         );
         BufferRenderer.drawWithGlobalProgram(
            buffer2.end()
         );
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         val3.pop();
      }
   }

   private void renderClassicWings(
      PlayerEntity val,
      float val2,
      MatrixStack val3,
      Vec3d val4
   ) {
      if (val.isAlive()
         && !val.isInvisible()
         )
       {
         if (!val.isGliding()
            && val.getPose()
               != EntityPose.SWIMMING
            && !val.isInSwimmingPose()
            )
          {
            double d = MathHelper.lerp(
                  (double)val2,
                  val.prevX,
                  val.getX()
               )
               - val4.x;
            double d2 = MathHelper.lerp(
                  (double)val2,
                  val.prevY,
                  val.getY()
               )
               - val4.y;
            double d3 = MathHelper.lerp(
                  (double)val2,
                  val.prevZ,
                  val.getZ()
               )
               - val4.z;
            float yaw = this.resolveClassicBodyYaw(
               val,
               val2
            );
            Vec3d velocity = val.getVelocity();
            float f = yaw
               * (float) (Math.PI / 180.0);
            Vec3d vec = new Vec3d(
               (double)(
                  -MathHelper.sin(
                     f
                  )
               ),
               0.0,
               (double)MathHelper.cos(
                  f
               )
            );
            Vec3d vec2 = new Vec3d(
               vec.z,
               0.0,
               -vec.x
            );
            float f2 = (float)(
               velocity.x
                     * vec.x
                  + velocity.z
                     * vec.z
            );
            float f3 = (float)(
               velocity.x
                     * vec2.x
                  + velocity.z
                     * vec2.z
            );
            float y2 = (float)velocity.y;
            boolean flag = this.classicWingAnimation
               .isState();
            float f4 = flag
               ? MathHelper.clamp(
                  -f2
                        * 140.0F
                     - y2
                        * 48.0F,
                  -24.0F,
                  26.0F
               )
               : 0.0F;
            float f5 = flag
               ? MathHelper.clamp(
                  f3
                     * 90.0F,
                  -10.0F,
                  10.0F
               )
               : 0.0F;
            float f6 = flag
               ? MathHelper.clamp(
                  Math.abs(
                           f2
                        )
                        * 0.95F
                     + Math.abs(
                           f3
                        )
                        * 0.65F
                     + Math.abs(
                           y2
                        )
                        * 0.75F,
                  0.0F,
                  1.7F
               )
               : 0.0F;
            float f7 = (
                     (float)val.age
                        + val2
                  )
                  * 0.22F
                  * 1.6F
               + f6
                  * 0.4F;
            float f8 = flag
               ? MathHelper.sin(
                  f7
               )
               : 0.0F;
            float f9 = flag
               ? MathHelper.cos(
                  f7
               )
               : 0.0F;
            float f10 = 18.0F
               + f6
                  * 5.0F;
            float f11 = 13.0F
               + f4
                  * 0.3F
               + f9
                  * 4.0F;
            float f12 = f8
                  * 25.0F
               + f5
                  * 0.75F;
            EntityPose entity2 = val.getPose();
            boolean flag2 = val.isGliding();
            boolean flag3 = entity2
                  == EntityPose.SWIMMING
               || flag2;
            if (flag3
               )
             {
               f10 -= 4.0F;
               f11 -= 6.0F;
               f12 *= 0.72F;
            }

            if (val.isSneaking()
               )
             {
               f10 -= 3.0F;
               f11 += 8.0F;
            }

            PretranslateyHolder val5 = this.resolveClassicWingPose(
               val,
               val2,
               entity2
            );
            float f13 = f10
               * val5.openMultiplier;
            float f14 = val5.scaleMultiplier
               * this.classicWingSize.get();
            float pitch = val5.sidePitch
               + f11
                  * 0.18F;
            float f15 = val5.sideRoll
               + f12
                  * 0.2F;
            int color2 = this.resolveCosmeticThemeColor();
            int color3 = ColorUtil.setAlphaColor(
               color2,
               220
            );
            int color4 = ColorUtil.setAlphaColor(
               ColorUtil.interpolate(
                  color2,
                  -1,
                  0.28F
               ),
               Math.round(48.4F)
            );
            int color5 = ColorUtil.setAlphaColor(
               ColorUtil.interpolate(
                  color2,
                  -1,
                  0.55F
               ),
               Math.round(57.199997F)
            );
            int color6 = ColorUtil.setAlphaColor(
               ColorUtil.darken(
                  color2,
                  0.62F
               ),
               Math.round(136.4F)
            );
            int color7 = ColorUtil.setAlphaColor(
               ColorUtil.interpolate(
                  color2,
                  -1,
                  0.28F
               ),
               Math.round(44.0F)
            );
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.blendFunc(
               770,
               771
            );
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            val3.push();
            val3.translate(
               d,
               d2,
               d3
            );
            val3.multiply(
               RotationAxis.POSITIVE_Y
                  .rotationDegrees(
                     180.0F
                        - yaw
                  )
            );
            if (val5.preTranslateY
                  != 0.0F
               || val5.preTranslateZ
                  != 0.0F) {
               val3.translate(
                  0.0F,
                  val5.preTranslateY,
                  val5.preTranslateZ
               );
            }

            if (val5.pitchRotation
               != 0.0F) {
               val3.multiply(
                  RotationAxis.POSITIVE_X
                     .rotationDegrees(
                        val5.pitchRotation
                     )
               );
            }

            if (val5.rollRotation
               != 0.0F) {
               val3.multiply(
                  RotationAxis.POSITIVE_Z
                     .rotationDegrees(
                        val5.rollRotation
                     )
               );
            }

            val3.translate(
               0.0F,
               val5.anchorY,
               val5.anchorZ
            );
            val3.scale(
               f14,
               f14,
               f14
            );
            this.renderClassicWingSide(
               val3,
               -1.0F,
               f13,
               pitch,
               f15,
               color3,
               color4,
               color5,
               color6,
               color7,
               val5
            );
            this.renderClassicWingSide(
               val3,
               1.0F,
               f13,
               pitch,
               f15,
               color3,
               color4,
               color5,
               color6,
               color7,
               val5
            );
            val3.pop();
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
         }
      }
   }

   private void renderNimbus(
      Event3DRender val
   ) {
      if (mc.player != null && mc.world != null && !mc.options.getPerspective().isFirstPerson()) {
         float f = val.getTickDelta();
         Vec3d vec = val.getCamera()
            .getPos();
         double d = MathHelper.lerp(
            (double)f,
            mc.player.prevX,
            mc.player.getX()
         );
         double d2 = MathHelper.lerp(
               (double)f,
               mc.player.prevY,
               mc.player.getY()
            )
            + (double)mc.player.getHeight()
            + 0.1;
         double d3 = MathHelper.lerp(
            (double)f,
            mc.player.prevZ,
            mc.player.getZ()
         );
         int color2 = this.resolveCosmeticThemeColor();
         long val2 = System.currentTimeMillis();
         double d4 = 0.0029670597283903604;
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.getNimbusTexture());
         MatrixStack matrices = val.getMatrices();

         for (int i = 0;
            i
               < 2;
            i++
         ) {
            double d5 = d4
                  * (double)val2
               + (double)i
                  * Math.PI;

            for (int i2 = 0;
               i2
                  < 17;
               i2++
            ) {
               double d6 = d5
                  - (double)i2
                     * 0.11;
               double d7 = Math.cos(
                     d6
                  )
                  * 0.45F;
               double d8 = Math.sin(
                     d6
                  )
                  * 0.45F;
               float f2 = (float)i2
                  / (float)Math.max(1, 16);
               float f3 = 0.23F
                  * (
                     1.0F
                        - f2
                           * 0.7F
                  );
               int n = MathHelper.clamp(
                  255
                     - i2
                        * 9,
                  0,
                  255
               );
               int color3 = ColorUtil.setAlphaColor(
                  color2,
                  n
               );
               this.renderNimbusBillboard(
                  matrices,
                  val.getCamera()
                     .getYaw(),
                  val.getCamera()
                     .getPitch(),
                  d
                     - vec.x
                     + d7,
                  d2
                     - vec.y,
                  d3
                     - vec.z
                     + d8,
                  f3,
                  color3
               );
            }
         }

         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   public void renderChinaHat(
      MatrixStack val,
      VertexConsumerProvider val19,
      PlayerEntity val2,
      ModelWithHead val3
   ) {
      if (this.isEnable()) {
         MultiBooleanSetting var10000 = this.cosmetics;
         if (var10000.is("Китайская шляпа")) {
            if (mc.player != null && mc.world != null) {
               if (!this.shouldRenderCosmeticForPlayer(
                  val2
               )) {
                  return;
               }

               if (val2
                     == mc.player
                  && mc.options.getPerspective().isFirstPerson()) {
                  return;
               }

               double d = val2.getBoundingBox()
                     .maxX
                  - val2.getBoundingBox()
                     .minX;
               float f = val2.getEquippedStack(
                        EquipmentSlot.HEAD
                     )
                     .isEmpty()
                  ? 0.415F
                  : 0.48F;
               val.push();
               val3.getHead()
                  .rotate(
                     val
                  );
               RenderSystem.enableBlend();
               RenderSystem.enableDepthTest();
               RenderSystem.disableCull();
               RenderSystem.defaultBlendFunc();
               RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.lineWidth(2.0F);
               GL11.glEnable(2848);
               GL11.glHint(
                  3154,
                  4354
               );
               val.translate(
                  0.0F,
                  -f,
                  0.0F
               );
               val.multiply(
                  RotationAxis.NEGATIVE_Z.rotationDegrees(180.0F)
               );
               val.multiply(
                  RotationAxis.POSITIVE_Y.rotationDegrees(90.0F)
               );
               Matrix4f matrix = val.peek()
                  .getPositionMatrix();
               Tessellator tessellator = Tessellator.getInstance();
               BufferBuilder buffer = tessellator.begin(
                  DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR
               );
               float f2 = 0.0F;
               int color2 = this.resolveCosmeticThemeColor();
               int color3 = ColorUtil.setAlphaColor(
                  color2,
                  125
               );
               int color4 = ColorUtil.setAlphaColor(
                  ColorUtil.darken(
                     color2,
                     0.5F
                  ),
                  180
               );

               for (int i = 0;
                  i
                     <= 180;
                  i++
               ) {
                  float f3 = (float)i
                     * 0.06981317F;
                  float f4 = (float)(
                     (double)MathHelper.sin(
                           f3
                        )
                        * d
                  );
                  float f5 = (float)(
                     (double)MathHelper.cos(
                           f3
                        )
                        * d
                  );
                  buffer.vertex(
                        matrix,
                        f4,
                        f2,
                        f5
                     )
                     .color(
                        color3
                     );
                  buffer.vertex(
                        matrix,
                        0.0F,
                        0.3F,
                        0.0F
                     )
                     .color(
                        color2
                     );
               }

               BufferRenderer.drawWithGlobalProgram(
                  buffer.end()
               );
               RenderSystem.depthMask(false);
               buffer = tessellator.begin(
                  DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR
               );
               float f6 = 0.0F;
               float f7 = 0.0F;
               boolean flag = false;

               for (int i2 = 0;
                  i2
                     <= 180;
                  i2++
               ) {
                  float f8 = (float)i2
                     * 0.06981317F;
                  float f9 = (float)(
                     (double)MathHelper.sin(
                           f8
                        )
                        * d
                  );
                  float f10 = (float)(
                     (double)MathHelper.cos(
                           f8
                        )
                        * d
                  );
                  buffer.vertex(
                        matrix,
                        f9,
                        f2,
                        f10
                     )
                     .color(
                        color4
                     );
                  if (!flag
                     )
                   {
                     f6 = f9;
                     f7 = f10;
                     flag = true;
                  }
               }

               if (flag
                  )
                {
                  buffer.vertex(
                        matrix,
                        f6,
                        f2,
                        f7
                     )
                     .color(
                        color4
                     );
               }

               BufferRenderer.drawWithGlobalProgram(
                  buffer.end()
               );
               RenderSystem.depthMask(true);
               RenderSystem.enableCull();
               RenderSystem.defaultBlendFunc();
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               GL11.glDisable(2848);
               val.pop();
               return;
            }

            return;
         }
      }
   }

   private Identifier getNimbusTexture() {
      return ClientIdentifier.of("textures/targetesp/bloom.png");
   }

   private void renderNimbusBillboard(
      MatrixStack val,
      float val2,
      float val3,
      double val4,
      double val5,
      double val6,
      float val7,
      int val8
   ) {
      int n = val8
            >> 24
         & 255;
      if (n
         > 0) {
         int n2 = val8
               >> 16
            & 255;
         int n3 = val8
               >> 8
            & 255;
         int n4 = val8
            & 255;
         float f = val7
            * 0.5F;
         val.push();
         val.translate(
            val4,
            val5,
            val6
         );
         val.multiply(
            RotationAxis.POSITIVE_Y
               .rotationDegrees(
                  -val2
               )
         );
         val.multiply(
            RotationAxis.POSITIVE_X
               .rotationDegrees(
                  val3
               )
         );
         Matrix4f matrix = val.peek()
            .getPositionMatrix();
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(
               matrix,
               -f,
               -f,
               0.0F
            )
            .texture(0.0F, 1.0F)
            .color(
               n2,
               n3,
               n4,
               n
            );
         buffer.vertex(
               matrix,
               -f,
               f,
               0.0F
            )
            .texture(0.0F, 0.0F)
            .color(
               n2,
               n3,
               n4,
               n
            );
         buffer.vertex(
               matrix,
               f,
               f,
               0.0F
            )
            .texture(1.0F, 0.0F)
            .color(
               n2,
               n3,
               n4,
               n
            );
         buffer.vertex(
               matrix,
               f,
               -f,
               0.0F
            )
            .texture(1.0F, 1.0F)
            .color(
               n2,
               n3,
               n4,
               n
            );
         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         val.pop();
      }
   }

   private boolean shouldRenderCosmeticForPlayer(
      PlayerEntity val
   ) {
      return mc.player == null
            || val
               == null
         ? false
         : val.isAlive()
            && !val.isSpectator()
            && !val.isInvisible();
   }

   private int getStableThemeColor() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null) {
         ThemeColors val = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         return val
                  != null
               && val.color
                  != null
               && val.color.length
                  != 0
            ? val.color[0]
            : ColorUtil.getThemeColor(0);
      } else {
         return ColorUtil.getThemeColor(0);
      }
   }

   private int resolveCosmeticThemeColor() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null) {
         ThemeColors val = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         if (val
            == null) {
            return ColorUtil.getThemeColor();
         } else {
            return "Rainbow"
                  .equals(
                     val.getName()
                  )
               ? ColorUtil.getThemeColor()
               : this.getStableThemeColor();
         }
      } else {
         return ColorUtil.getThemeColor();
      }
   }

   private void syncWingSelectionState() {
      MultiBooleanSetting var10000 = this.cosmetics;
      boolean flag = var10000.is(
         "Крылья"
      );
      var10000 = this.cosmetics;
      boolean flag2 = var10000.is(
         "Крылья 2"
      );
      if (flag
         && flag2
         )
       {
         if (flag
               != this.lastButterflySelected
            && flag2
               == this.lastClassicSelected) {
            var10000 = this.cosmetics;
            var10000.set("Крылья 2", false);
            flag2 = false;
         } else {
            var10000 = this.cosmetics;
            var10000.set("Крылья", false);
            flag = false;
         }
      }

      this.lastButterflySelected = flag;
      this.lastClassicSelected = flag2;
   }

   private void applyBackPoseTransform(
      MatrixStack val,
      PlayerEntity val2,
      float val3,
      EntityPose val4,
      boolean val5
   ) {
      if (val5
         )
       {
         float pitch = val2.getPitch(
            val3
         );
         float f = MathHelper.clamp(
            pitch,
            -65.0F,
            65.0F
         );
         val.translate(
            0.0F, 0.3F, 0.0F
         );
         val.multiply(
            RotationAxis.POSITIVE_X
               .rotationDegrees(
                  -(
                     90.0F
                        + f
                  )
               )
         );
         val.translate(
            0.0F, -0.15F, 0.12F
         );
      } else if (val4
         == EntityPose.SWIMMING) {
         float pitch2 = val2.getPitch(
            val3
         );
         float f2 = MathHelper.clamp(
            pitch2,
            -65.0F,
            65.0F
         );
         val.translate(
            0.0F, 0.3F, 0.0F
         );
         val.multiply(
            RotationAxis.POSITIVE_X
               .rotationDegrees(
                  -(
                     90.0F
                        + f2
                  )
               )
         );
         val.translate(
            0.0F, -0.15F, 0.12F
         );
      } else {
         if (val2.isSneaking()
            )
          {
            val.translate(
               0.0F, 1.15F, 0.0F
            );
            val.multiply(
               RotationAxis.POSITIVE_X.rotationDegrees(24.0F)
            );
            val.translate(
               0.0F, 0.0F, 0.08F
            );
         } else {
            val.translate(
               0.0F, 1.3F, 0.08F
            );
         }
      }
   }

   private float resolveClassicBodyYaw(
      PlayerEntity val,
      float val2
   ) {
      float f = MathHelper.lerpAngleDegrees(
         val2,
         val.prevBodyYaw,
         val.bodyYaw
      );
      if (val
         != mc.player) {
         return f;
      } else if (!this.selfClassicBodyYawInitialized) {
         this.selfClassicBodyYaw = f;
         this.selfClassicBodyYawInitialized = true;
         return this.selfClassicBodyYaw;
      } else {
         float f2 = MathHelper.wrapDegrees(
            f
               - this.selfClassicBodyYaw
         );
         this.selfClassicBodyYaw = this.selfClassicBodyYaw
            + MathHelper.clamp(
               f2,
               -14.0F,
               14.0F
            );
         return this.selfClassicBodyYaw;
      }
   }

   private PretranslateyHolder resolveClassicWingPose(
      PlayerEntity val,
      float val2,
      EntityPose val3
   ) {
      float pitch = val.getPitch(
         val2
      );
      if (val.isGliding()
         )
       {
         float f = MathHelper.clamp(
            pitch,
            -65.0F,
            65.0F
         );
         return new PretranslateyHolder(
            1.18F,
            0.1F,
            0.0F,
            0.0F,
            -(
               90.0F
                  + f
            ),
            0.0F,
            0.76F,
            0.92F,
            0.1F,
            0.58F,
            0.05F,
            0.0F,
            0.06F,
            -5.0F,
            -2.0F,
            0.13F
         );
      } else if (val3
            == EntityPose.SWIMMING
         || val.isInSwimmingPose()
         )
       {
         float f2 = MathHelper.clamp(
            pitch,
            -65.0F,
            65.0F
         );
         float f3 = val.isInSwimmingPose()
            ? 1.1F
            : 1.18F;
         float f4 = val.isInSwimmingPose()
            ? 0.18F
            : 0.12F;
         return new PretranslateyHolder(
            f3,
            f4,
            0.18F,
            0.48F,
            -(
               90.0F
                  + f2
            ),
            0.0F,
            0.84F,
            0.96F,
            0.12F,
            0.7F,
            0.03F,
            0.0F,
            0.01F,
            -7.0F,
            -3.0F,
            0.16F
         );
      } else {
         return val.isSneaking()
            ? new PretranslateyHolder(0.0F, 0.0F, 0.96F, 0.1F, 18.0F, 0.0F, 1.0F, 1.0F, 0.18F, 4.5F, 0.06F, 0.0F, 0.02F, -11.0F, -4.0F, 0.12F)
            : new PretranslateyHolder(0.0F, 0.0F, 1.18F, 0.1F, 0.0F, 0.0F, 1.0F, 1.0F, 0.18F, 4.5F, 0.06F, 0.0F, 0.02F, -11.0F, -4.0F, 0.12F);
      }
   }

   private void renderClassicWingSide(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      int val6,
      int val7,
      int val8,
      int val9,
      int val10,
      PretranslateyHolder val11
   ) {
      val.push();
      val.translate(
         val2
            * val11.sideOffset,
         val11.sideYOffset,
         val11.sideZOffset
      );
      val.multiply(
         RotationAxis.POSITIVE_Y
            .rotationDegrees(
               val2
                  * val3
            )
      );
      val.multiply(
         RotationAxis.POSITIVE_Z
            .rotationDegrees(
               val2
                  * val5
            )
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               val4
            )
      );
      RenderSystem.blendFunc(770, 1);
      this.drawClassicWingLayer(
         val,
         val2,
         1.22F,
         val7,
         ColorUtil.setAlphaColor(
            val7,
            0
         )
      );
      this.drawClassicWingLayer(
         val,
         val2,
         0.84F,
         val8,
         ColorUtil.setAlphaColor(
            val8,
            0
         )
      );
      RenderSystem.blendFunc(
         770,
         771
      );
      this.drawClassicWingLayer(
         val,
         val2,
         1.0F,
         val6,
         ColorUtil.setAlphaColor(
            val6,
            10
         )
      );
      RenderSystem.blendFunc(770, 1);
      this.drawClassicWingOutline(
         val,
         val2,
         1.0F,
         val9
      );
      this.drawClassicWingRibs(
         val,
         val2,
         0.96F,
         val10
      );
      val.pop();
   }

   private void drawClassicWingLayer(
      MatrixStack val,
      float val2,
      float val3,
      int val4,
      int val5
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

      for (int i = 0;
         i
            < CLASSIC_WING_SHAPE.length;
         i++
      ) {
         XHolder val6 = CLASSIC_WING_SHAPE[i];
         XHolder val7 = CLASSIC_WING_SHAPE[(
               i
                  + 1
            )
            % CLASSIC_WING_SHAPE.length];
         this.vertex(
            buffer,
            matrix,
            0.0F,
            0.0F,
            0.0F,
            val4
         );
         this.vertex(
            buffer,
            matrix,
            val2
               * val6.x
               * val3,
            val6.y
               * val3,
            0.0F,
            this.applyClassicWingPointAlpha(
               val5,
               val6.alphaMultiplier
            )
         );
         this.vertex(
            buffer,
            matrix,
            val2
               * val7.x
               * val3,
            val7.y
               * val3,
            0.0F,
            this.applyClassicWingPointAlpha(
               val5,
               val7.alphaMultiplier
            )
         );
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void drawClassicWingOutline(
      MatrixStack val,
      float val2,
      float val3,
      int val4
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      RenderSystem.lineWidth(1.35F);
      GL11.glEnable(2848);

      for (int i = 0;
         i
            < CLASSIC_WING_SHAPE.length;
         i++
      ) {
         XHolder val5 = CLASSIC_WING_SHAPE[i];
         XHolder val6 = CLASSIC_WING_SHAPE[(
               i
                  + 1
            )
            % CLASSIC_WING_SHAPE.length];
         this.addLine(
            buffer,
            matrix,
            val2
               * val5.x
               * val3,
            val5.y
               * val3,
            0.0F,
            val2
               * val6.x
               * val3,
            val6.y
               * val3,
            0.0F,
            val4
         );
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      GL11.glDisable(2848);
   }

   private void drawClassicWingRibs(
      MatrixStack val,
      float val2,
      float val3,
      int val4
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      int[] var10000 = new int[5];
      var10000[0] = 2;
      var10000[1] = 4;
      var10000[2] = 7;
      var10000[3] = 9;
      var10000[4] = 11;
      int[] val5 = var10000;
      RenderSystem.lineWidth(0.9F);

      for (int i : val5) {
         XHolder val6 = CLASSIC_WING_SHAPE[i];
         this.vertex(
            buffer,
            matrix,
            0.0F,
            0.0F,
            0.0F,
            ColorUtil.setAlphaColor(
               val4,
               Math.max(
                  8,
                  (int)(
                     (float)(
                           val4
                                 >> 24
                              & 255
                        )
                        * 0.75F
                  )
               )
            )
         );
         this.vertex(
            buffer,
            matrix,
            val2
               * val6.x
               * val3,
            val6.y
               * val3,
            0.0F,
            this.applyClassicWingPointAlpha(
               val4,
               val6.alphaMultiplier
            )
         );
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private int applyClassicWingPointAlpha(
      int val,
      float val2
   ) {
      int n = val
            >> 24
         & 255;
      return ColorUtil.setAlphaColor(
         val,
         Math.max(
            0,
            Math.min(
               255,
               (int)(
                  (float)n
                     * val2
               )
            )
         )
      );
   }

   private void vertex(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      int val6
   ) {
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            val6
         );
   }

   private void renderButterflyWing(
      BufferBuilder val,
      MatrixStack val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      int val8,
      int val9
   ) {
      float f = 0.12F
         * val7;
      float f2 = 1.52F
         * val7;
      float f3 = 0.64F
         * val7;
      float f4 = 1.14F
         * val7;
      float f5 = 0.39F
         * val7;
      val2.push();
      val2.translate(
         0.15F
            * val3,
         0.0F,
         -0.17F
      );
      val2.multiply(
         RotationAxis.POSITIVE_Y
            .rotationDegrees(
               val3
                  * val4
            )
      );
      val2.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               val5
            )
      );
      val2.multiply(
         RotationAxis.POSITIVE_Z
            .rotationDegrees(
               val3
                  * val6
            )
      );
      Matrix4f matrix = val2.peek()
         .getPositionMatrix();
      this.addDoubleSidedGradientTriangle(
         val,
         matrix,
         val3
            * f,
         0.02F,
         -0.01F,
         val3
            * (
               f
                  + f2
                     * 0.22F
            ),
         f3
            * 0.98F,
         -0.06F,
         val3
            * (
               f
                  + f2
                     * 0.88F
            ),
         f3
            * 0.6F,
         -0.13F,
         val8,
         val9
      );
      this.addDoubleSidedGradientTriangle(
         val,
         matrix,
         val3
            * f,
         0.02F,
         -0.01F,
         val3
            * (
               f
                  + f2
                     * 0.88F
            ),
         f3
            * 0.6F,
         -0.13F,
         val3
            * (
               f
                  + f2
            ),
         f3
            * 0.12F,
         -0.17F,
         val8,
         val9
      );
      this.addDoubleSidedGradientTriangle(
         val,
         matrix,
         val3
            * f,
         -0.03F,
         -0.03F,
         val3
            * (
               f
                  + f4
                     * 0.26F
            ),
         -f5
            * 0.96F,
         -0.11F,
         val3
            * (
               f
                  + f4
                     * 0.84F
            ),
         -f5
            * 0.54F,
         -0.18F,
         val9,
         val8
      );
      this.addDoubleSidedGradientTriangle(
         val,
         matrix,
         val3
            * f,
         -0.03F,
         -0.03F,
         val3
            * (
               f
                  + f4
                     * 0.84F
            ),
         -f5
            * 0.54F,
         -0.18F,
         val3
            * (
               f
                  + f4
            ),
         -f5
            * 0.12F,
         -0.21F,
         val9,
         val8
      );
      val2.pop();
   }

   private void renderButterflyWingOutline(
      BufferBuilder val,
      MatrixStack val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      int val8
   ) {
      float f = 0.12F
         * val7;
      float f2 = 1.52F
         * val7;
      float f3 = 0.64F
         * val7;
      float f4 = 1.14F
         * val7;
      float f5 = 0.39F
         * val7;
      val2.push();
      val2.translate(
         0.15F
            * val3,
         0.0F,
         -0.17F
      );
      val2.multiply(
         RotationAxis.POSITIVE_Y
            .rotationDegrees(
               val3
                  * val4
            )
      );
      val2.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               val5
            )
      );
      val2.multiply(
         RotationAxis.POSITIVE_Z
            .rotationDegrees(
               val3
                  * val6
            )
      );
      Matrix4f matrix = val2.peek()
         .getPositionMatrix();
      this.addLine(
         val,
         matrix,
         val3
            * f,
         0.02F,
         -0.01F,
         val3
            * (
               f
                  + f2
                     * 0.22F
            ),
         f3
            * 0.98F,
         -0.06F,
         val8
      );
      this.addLine(
         val,
         matrix,
         val3
            * (
               f
                  + f2
                     * 0.22F
            ),
         f3
            * 0.98F,
         -0.06F,
         val3
            * (
               f
                  + f2
                     * 0.88F
            ),
         f3
            * 0.6F,
         -0.13F,
         val8
      );
      this.addLine(
         val,
         matrix,
         val3
            * (
               f
                  + f2
                     * 0.88F
            ),
         f3
            * 0.6F,
         -0.13F,
         val3
            * (
               f
                  + f2
            ),
         f3
            * 0.12F,
         -0.17F,
         val8
      );
      this.addLine(
         val,
         matrix,
         val3
            * f,
         -0.03F,
         -0.03F,
         val3
            * (
               f
                  + f4
                     * 0.26F
            ),
         -f5
            * 0.96F,
         -0.11F,
         val8
      );
      this.addLine(
         val,
         matrix,
         val3
            * (
               f
                  + f4
                     * 0.26F
            ),
         -f5
            * 0.96F,
         -0.11F,
         val3
            * (
               f
                  + f4
                     * 0.84F
            ),
         -f5
            * 0.54F,
         -0.18F,
         val8
      );
      this.addLine(
         val,
         matrix,
         val3
            * (
               f
                  + f4
                     * 0.84F
            ),
         -f5
            * 0.54F,
         -0.18F,
         val3
            * (
               f
                  + f4
            ),
         -f5
            * 0.12F,
         -0.21F,
         val8
      );
      this.addLine(
         val,
         matrix,
         val3
            * f,
         -0.01F,
         -0.02F,
         val3
            * (
               f
                  + f2
                     * 0.6F
            ),
         0.08F,
         -0.08F,
         val8
      );
      val2.pop();
   }

   private void addDoubleSidedQuad(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      float val12,
      float val13,
      float val14,
      int val15,
      int val16,
      int val17,
      int val18
   ) {
      this.addQuad(
         val,
         val2,
         val3,
         val4,
         val5,
         val6,
         val7,
         val8,
         val9,
         val10,
         val11,
         val12,
         val13,
         val14,
         val15,
         val16,
         val17,
         val18
      );
      this.addQuad(
         val,
         val2,
         val12,
         val13,
         val14,
         val9,
         val10,
         val11,
         val6,
         val7,
         val8,
         val3,
         val4,
         val5,
         val15,
         val16,
         val17,
         val18
      );
   }

   private void addDoubleSidedGradientQuad(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      float val12,
      float val13,
      float val14,
      int val15,
      int val16
   ) {
      int n = val15
            >> 16
         & 255;
      int n2 = val15
            >> 8
         & 255;
      int n3 = val15
         & 255;
      int n4 = val15
            >> 24
         & 255;
      int n5 = val16
            >> 16
         & 255;
      int n6 = val16
            >> 8
         & 255;
      int n7 = val16
         & 255;
      int n8 = val16
            >> 24
         & 255;
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val12,
            val13,
            val14
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val12,
            val13,
            val14
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
   }

   private void addDoubleSidedGradientTriangle(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      int val12,
      int val13
   ) {
      int n = val12
            >> 16
         & 255;
      int n2 = val12
            >> 8
         & 255;
      int n3 = val12
         & 255;
      int n4 = val12
            >> 24
         & 255;
      int n5 = val13
            >> 16
         & 255;
      int n6 = val13
            >> 8
         & 255;
      int n7 = val13
         & 255;
      int n8 = val13
            >> 24
         & 255;
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            n5,
            n6,
            n7,
            n8
         );
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
   }

   private void renderWingBoneLine(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      float val12,
      int val13,
      int val14
   ) {
      float f = val6
         - val3;
      float f2 = val7
         - val4;
      float x2 = Math.max(
         1.0E-4F,
         (float)Math.sqrt(
            (double)(
               f
                     * f
                  + f2
                     * f2
            )
         )
      );
      float f3 = -f2
         / x2
         * val12;
      float f4 = f
         / x2
         * val12;
      int n = val13
            >> 16
         & 255;
      int n2 = val13
            >> 8
         & 255;
      int n3 = val13
         & 255;
      int n4 = val13
            >> 24
         & 255;
      int n5 = val14
            >> 16
         & 255;
      int n6 = val14
            >> 8
         & 255;
      int n7 = val14
         & 255;
      int n8 = val14
            >> 24
         & 255;
      this.addDoubleSidedQuad(
         val,
         val2,
         val3
            + f3,
         val4
            + f4,
         val5,
         val3
            - f3,
         val4
            - f4,
         val5,
         val6
            - f3,
         val7
            - f4,
         val8,
         val6
            + f3,
         val7
            + f4,
         val8,
         n,
         n2,
         n3,
         n4
      );
      float f5 = val9
         - val6;
      float f6 = val10
         - val7;
      float x3 = Math.max(
         1.0E-4F,
         (float)Math.sqrt(
            (double)(
               f5
                     * f5
                  + f6
                     * f6
            )
         )
      );
      float f7 = -f6
         / x3
         * val12;
      float f8 = f5
         / x3
         * val12;
      this.addDoubleSidedQuad(
         val,
         val2,
         val6
            + f7,
         val7
            + f8,
         val8,
         val6
            - f7,
         val7
            - f8,
         val8,
         val9
            - f7,
         val10
            - f8,
         val11,
         val9
            + f7,
         val10
            + f8,
         val11,
         n5,
         n6,
         n7,
         n8
      );
   }

   private void addQuad(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      float val12,
      float val13,
      float val14,
      int val15,
      int val16,
      int val17,
      int val18
   ) {
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            val15,
            val16,
            val17,
            val18
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            val15,
            val16,
            val17,
            val18
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            val15,
            val16,
            val17,
            val18
         );
      val.vertex(
            val2,
            val12,
            val13,
            val14
         )
         .color(
            val15,
            val16,
            val17,
            val18
         );
   }

   private void addLine(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      int val9
   ) {
      int n = val9
            >> 16
         & 255;
      int n2 = val9
            >> 8
         & 255;
      int n3 = val9
         & 255;
      int n4 = val9
            >> 24
         & 255;
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
   }

   private void renderBackDildo(
      PlayerEntity val,
      float val2,
      MatrixStack val3,
      Vec3d val4
   ) {
      if (!val.isGliding()
         && val.getPose()
            != EntityPose.SWIMMING
         && !val.isInSwimmingPose()
         )
       {
         double d = MathHelper.lerp(
               (double)val2,
               val.prevX,
               val.getX()
            )
            - val4.x;
         double d2 = MathHelper.lerp(
               (double)val2,
               val.prevY,
               val.getY()
            )
            - val4.y;
         double d3 = MathHelper.lerp(
               (double)val2,
               val.prevZ,
               val.getZ()
            )
            - val4.z;
         float f = MathHelper.lerp(
            val2,
            val.prevBodyYaw,
            val.bodyYaw
         );
         Vec3d velocity = val.getVelocity();
         float f2 = (float)Math.sqrt(
            velocity.x
                  * velocity.x
               + velocity.z
                  * velocity.z
         );
         float f3 = (
               (float)val.age
                  + val2
            )
            * 0.25F;
         float f4 = MathHelper.sin(
               f3
            )
            * f2
            * 35.0F;
         float f5 = MathHelper.cos(
               f3
                  * 0.5F
            )
            * f2
            * 12.0F;
         val3.push();
         val3.translate(
            d,
            d2,
            d3
         );
         val3.multiply(
            RotationAxis.POSITIVE_Y
               .rotationDegrees(
                  -f
               )
         );
         if (val.isSneaking()
            )
          {
            val3.translate(
               0.0F, 1.0F, 0.0F
            );
            val3.multiply(
               RotationAxis.POSITIVE_X.rotationDegrees(28.0F)
            );
            val3.translate(
               0.0F, 0.0F, 0.35F
            );
         } else {
            val3.translate(
               0.0F, 1.2F, 0.35F
            );
         }

         val3.multiply(
            RotationAxis.POSITIVE_X
               .rotationDegrees(
                  -10.0F
                     + f4
               )
         );
         val3.multiply(
            RotationAxis.POSITIVE_Z
               .rotationDegrees(
                  f5
               )
         );
         int color2 = this.resolveCosmeticThemeColor();
         int color3 = ColorUtil.setAlphaColor(
            ColorUtil.interpolate(
               color2,
               -18751,
               0.6F
            ),
            220
         );
         int color4 = ColorUtil.setAlphaColor(
            ColorUtil.interpolate(
               color2,
               -38476,
               0.7F
            ),
            230
         );
         int color5 = ColorUtil.setAlphaColor(
            ColorUtil.darken(
               color2,
               0.4F
            ),
            200
         );
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.blendFunc(
            770,
            771
         );
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         this.renderDildoShape(
            val3,
            color3,
            color4,
            color5
         );
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         val3.pop();
      }
   }

   private void renderDildoShape(
      MatrixStack val,
      int val2,
      int val3,
      int val4
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      float f = 0.55F;
      float f2 = 0.1F;
      float f3 = 0.18F;
      float f4 = 0.145F;
      int n = 32;
      float[] var10000 = new float[4];
      var10000[0] = -0.15F;
      var10000[1] = -0.35F;
      var10000[2] = -0.55F;
      var10000[3] = -0.75F;
      float[] val5 = var10000;
      float f5 = 0.025F;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (int i = 0;
         i
            < n;
         i++
      ) {
         float f6 = (float)(
            (double)i
               * Math.PI
               * 2.0
               / (double)n
         );
         float f7 = (float)(
            (double)(
                  i
                     + 1
               )
               * Math.PI
               * 2.0
               / (double)n
         );
         float f8 = MathHelper.cos(
               f6
            )
            * f2;
         float f9 = MathHelper.sin(
               f6
            )
            * f2;
         float f10 = MathHelper.cos(
               f7
            )
            * f2;
         float f11 = MathHelper.sin(
               f7
            )
            * f2;
         int color2 = ColorUtil.darken(
            val2,
            0.8F
         );
         this.addQuadGradient(
            buffer,
            matrix,
            f8,
            0.0F,
            f9,
            val2,
            f10,
            0.0F,
            f11,
            val2,
            f10,
            -f,
            f11,
            color2,
            f8,
            -f,
            f9,
            color2
         );
      }

      for (float f12 : val5) {
         int color3 = ColorUtil.darken(
            val2,
            0.6F
         );

         for (int i2 = 0;
            i2
               < n;
            i2++
         ) {
            float f13 = (float)(
               (double)i2
                  * Math.PI
                  * 2.0
                  / (double)n
            );
            float f14 = (float)(
               (double)(
                     i2
                        + 1
                  )
                  * Math.PI
                  * 2.0
                  / (double)n
            );
            float f15 = MathHelper.cos(
                  f13
               )
               * (
                  f2
                     + 0.01F
               );
            float f16 = MathHelper.sin(
                  f13
               )
               * (
                  f2
                     + 0.01F
               );
            float f17 = MathHelper.cos(
                  f14
               )
               * (
                  f2
                     + 0.01F
               );
            float f18 = MathHelper.sin(
                  f14
               )
               * (
                  f2
                     + 0.01F
               );
            this.addQuad(
               buffer,
               matrix,
               f15,
               f12
                  - f5,
               f16,
               f17,
               f12
                  - f5,
               f18,
               f17,
               f12
                  + f5,
               f18,
               f15,
               f12
                  + f5,
               f16,
               color3
            );
         }
      }

      int color4 = ColorUtil.darken(
         val2,
         0.5F
      );

      for (int i3 = 0;
         i3
            < n;
         i3++
      ) {
         float f19 = (float)(
            (double)i3
               * Math.PI
               * 2.0
               / (double)n
         );
         float f20 = (float)(
            (double)(
                  i3
                     + 1
               )
               * Math.PI
               * 2.0
               / (double)n
         );
         float f21 = MathHelper.cos(
               f19
            )
            * f2;
         float f22 = MathHelper.sin(
               f19
            )
            * f2;
         float f23 = MathHelper.cos(
               f20
            )
            * f2;
         float f24 = MathHelper.sin(
               f20
            )
            * f2;
         this.addTriangle(
            buffer,
            matrix,
            0.0F,
            -f,
            0.0F,
            f21,
            -f,
            f22,
            f23,
            -f,
            f24,
            color4
         );
      }

      int color5 = ColorUtil.interpolate(
         val2,
         val3,
         0.5F
      );

      for (int i4 = 0;
         i4
            < n;
         i4++
      ) {
         float f25 = (float)(
            (double)i4
               * Math.PI
               * 2.0
               / (double)n
         );
         float f26 = (float)(
            (double)(
                  i4
                     + 1
               )
               * Math.PI
               * 2.0
               / (double)n
         );
         float f27 = MathHelper.cos(
               f25
            )
            * f2;
         float f28 = MathHelper.sin(
               f25
            )
            * f2;
         float f29 = MathHelper.cos(
               f26
            )
            * f2;
         float f30 = MathHelper.sin(
               f26
            )
            * f2;
         float f31 = MathHelper.cos(
               f25
            )
            * f4;
         float f32 = MathHelper.sin(
               f25
            )
            * f4;
         float f33 = MathHelper.cos(
               f26
            )
            * f4;
         float f34 = MathHelper.sin(
               f26
            )
            * f4;
         this.addQuad(
            buffer,
            matrix,
            f27,
            0.0F,
            f28,
            f29,
            0.0F,
            f30,
            f33,
            0.05F,
            f34,
            f31,
            0.05F,
            f32,
            color5
         );
      }

      for (int i5 = 0;
         i5
            < n;
         i5++
      ) {
         float f35 = (float)(
            (double)i5
               * Math.PI
               * 2.0
               / (double)n
         );
         float f36 = (float)(
            (double)(
                  i5
                     + 1
               )
               * Math.PI
               * 2.0
               / (double)n
         );
         float f37 = MathHelper.cos(
               f35
            )
            * f4;
         float f38 = MathHelper.sin(
               f35
            )
            * f4;
         float f39 = MathHelper.cos(
               f36
            )
            * f4;
         float f40 = MathHelper.sin(
               f36
            )
            * f4;
         int color6 = ColorUtil.interpolate(
            val3,
            -1,
            0.3F
         );
         this.addTriangleGradient(
            buffer,
            matrix,
            0.0F,
            f3,
            0.0F,
            color6,
            f39,
            0.05F,
            f40,
            val3,
            f37,
            0.05F,
            f38,
            val3
         );
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      RenderSystem.lineWidth(2.5F);
      BufferBuilder buffer2 = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

      for (int i6 = 0;
         i6
            < n;
         i6++
      ) {
         float f41 = (float)(
            (double)i6
               * Math.PI
               * 2.0
               / (double)n
         );
         float f42 = (float)(
            (double)(
                  i6
                     + 1
               )
               * Math.PI
               * 2.0
               / (double)n
         );
         float f43 = MathHelper.cos(
               f41
            )
            * f2;
         float f44 = MathHelper.sin(
               f41
            )
            * f2;
         float f45 = MathHelper.cos(
               f42
            )
            * f2;
         float f46 = MathHelper.sin(
               f42
            )
            * f2;
         this.addLine(
            buffer2,
            matrix,
            f43,
            0.0F,
            f44,
            f45,
            0.0F,
            f46,
            val4
         );
         this.addLine(
            buffer2,
            matrix,
            f43,
            -f,
            f44,
            f45,
            -f,
            f46,
            val4
         );
         if (i6
               % 4
            == 0) {
            this.addLine(
               buffer2,
               matrix,
               f43,
               0.0F,
               f44,
               f43,
               -f,
               f44,
               val4
            );
         }
      }

      for (float f47 : val5) {
         for (int i7 = 0;
            i7
               < n;
            i7++
         ) {
            float f48 = (float)(
               (double)i7
                  * Math.PI
                  * 2.0
                  / (double)n
            );
            float f49 = (float)(
               (double)(
                     i7
                        + 1
                  )
                  * Math.PI
                  * 2.0
                  / (double)n
            );
            float f50 = MathHelper.cos(
                  f48
               )
               * (
                  f2
                     + 0.01F
               );
            float f51 = MathHelper.sin(
                  f48
               )
               * (
                  f2
                     + 0.01F
               );
            float f52 = MathHelper.cos(
                  f49
               )
               * (
                  f2
                     + 0.01F
               );
            float f53 = MathHelper.sin(
                  f49
               )
               * (
                  f2
                     + 0.01F
               );
            this.addLine(
               buffer2,
               matrix,
               f50,
               f47,
               f51,
               f52,
               f47,
               f53,
               ColorUtil.darken(
                  val4,
                  0.7F
               )
            );
         }
      }

      for (int i8 = 0;
         i8
            < n;
         i8++
      ) {
         float f54 = (float)(
            (double)i8
               * Math.PI
               * 2.0
               / (double)n
         );
         float f55 = (float)(
            (double)(
                  i8
                     + 1
               )
               * Math.PI
               * 2.0
               / (double)n
         );
         float f56 = MathHelper.cos(
               f54
            )
            * f4;
         float f57 = MathHelper.sin(
               f54
            )
            * f4;
         float f58 = MathHelper.cos(
               f55
            )
            * f4;
         float f59 = MathHelper.sin(
               f55
            )
            * f4;
         this.addLine(
            buffer2,
            matrix,
            f56,
            0.05F,
            f57,
            f58,
            0.05F,
            f59,
            val4
         );
         if (i8
               % 4
            == 0) {
            this.addLine(
               buffer2,
               matrix,
               f56,
               0.05F,
               f57,
               0.0F,
               f3,
               0.0F,
               val4
            );
         }
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer2.end()
      );
   }

   private void addQuadGradient(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      int val6,
      float val7,
      float val8,
      float val9,
      int val10,
      float val11,
      float val12,
      float val13,
      int val14,
      float val15,
      float val16,
      float val17,
      int val18
   ) {
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            val6
                  >> 16
               & 255,
            val6
                  >> 8
               & 255,
            val6
               & 255,
            val6
                  >> 24
               & 255
         );
      val.vertex(
            val2,
            val7,
            val8,
            val9
         )
         .color(
            val10
                  >> 16
               & 255,
            val10
                  >> 8
               & 255,
            val10
               & 255,
            val10
                  >> 24
               & 255
         );
      val.vertex(
            val2,
            val11,
            val12,
            val13
         )
         .color(
            val14
                  >> 16
               & 255,
            val14
                  >> 8
               & 255,
            val14
               & 255,
            val14
                  >> 24
               & 255
         );
      val.vertex(
            val2,
            val15,
            val16,
            val17
         )
         .color(
            val18
                  >> 16
               & 255,
            val18
                  >> 8
               & 255,
            val18
               & 255,
            val18
                  >> 24
               & 255
         );
   }

   private void addTriangleGradient(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      int val6,
      float val7,
      float val8,
      float val9,
      int val10,
      float val11,
      float val12,
      float val13,
      int val14
   ) {
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            val6
                  >> 16
               & 255,
            val6
                  >> 8
               & 255,
            val6
               & 255,
            val6
                  >> 24
               & 255
         );
      val.vertex(
            val2,
            val7,
            val8,
            val9
         )
         .color(
            val10
                  >> 16
               & 255,
            val10
                  >> 8
               & 255,
            val10
               & 255,
            val10
                  >> 24
               & 255
         );
      val.vertex(
            val2,
            val11,
            val12,
            val13
         )
         .color(
            val14
                  >> 16
               & 255,
            val14
                  >> 8
               & 255,
            val14
               & 255,
            val14
                  >> 24
               & 255
         );
      val.vertex(
            val2,
            val11,
            val12,
            val13
         )
         .color(
            val14
                  >> 16
               & 255,
            val14
                  >> 8
               & 255,
            val14
               & 255,
            val14
                  >> 24
               & 255
         );
   }

   private void addQuad(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      float val12,
      float val13,
      float val14,
      int val15
   ) {
      int n = val15
            >> 16
         & 255;
      int n2 = val15
            >> 8
         & 255;
      int n3 = val15
         & 255;
      int n4 = val15
            >> 24
         & 255;
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val12,
            val13,
            val14
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
   }

   private void addTriangle(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      int val12
   ) {
      int n = val12
            >> 16
         & 255;
      int n2 = val12
            >> 8
         & 255;
      int n3 = val12
         & 255;
      int n4 = val12
            >> 24
         & 255;
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
   }
}
