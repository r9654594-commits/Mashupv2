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
import dev.leg1tka.mixin.LivingEntityRendererAccessor;
import java.awt.Color;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Chams extends Module {
   public static final Chams INSTANCE = new Chams();
   public static final String TARGET_PLAYERS = "Игроков";
   public static final String TARGET_FRIENDS = "Друзей";
   public static final String TARGET_SELF = "Себя";
   private static final int DEFAULT_FILL_ALPHA = 130;
   private static final float DEFAULT_LINE_WIDTH = 0.5F;
   private static final float CLIENT_FILL_SATURATION = 1.18F;
   private static final float CLIENT_FILL_BRIGHTNESS = 1.12F;
   private static final float CLIENT_OUTLINE_SATURATION = 1.12F;
   private static final float CLIENT_OUTLINE_BRIGHTNESS = 1.08F;
   private static final float MIN_PULSE_ALPHA = 0.65F;
   private static final float PULSE_SWING = 0.35F;
   private static final int FRIEND_FILL_COLOR = new Color(85, 255, 85, 60).getRGB();
   private static final int FRIEND_OUTLINE_COLOR = new Color(100, 255, 100, 255).getRGB();
   private static final long OUTLINE_RETRY_DELAY_MS = 3000L;
   private final MultiBooleanSetting rendering;
   private final BooleanSetting waves;
   private final NumberSetting waveSpeedX;
   private final NumberSetting waveSpeedY;
   private final NumberSetting waveScale;
   private final NumberSetting waveDensity;
   private final NumberSetting waveGlow;
   private final BooleanSetting glow;
   private final NumberSetting glowIntensity;
   private final NumberSetting glowLayers;
   private final BooleanSetting pulse;
   private final NumberSetting pulseSpeed;
   private final BooleanSetting hideOriginal;
   private final BooleanSetting hideItemsAndCape;
   private final long startTime;
   private boolean outlineAssistReady;
   private long nextOutlineRetryAt;

   private Chams() {
      super("Chams", "Chams", Category.RENDER);
      String var3 = "Отображать";
      BooleanSetting[] var10004 = new BooleanSetting[3];
      var10004[0] = new BooleanSetting("Игроков", true);
      var10004[1] = new BooleanSetting("Друзей", true);
      int var10006 = 2;
      var10004[var10006] = new BooleanSetting("Себя", false);
      this.rendering = new MultiBooleanSetting(var3, var10004);
      this.waves = new BooleanSetting("Волны", true);
      this.waveSpeedX = new NumberSetting("Скорость X", 0.22F, 0.0F, 1.5F, 0.01F).visible(this.waves::isState);
      this.waveSpeedY = new NumberSetting("Скорость Y", 0.15F, 0.0F, 1.5F, 0.01F).visible(this.waves::isState);
      this.waveScale = new NumberSetting("Размер волн", 1.35F, 0.2F, 4.0F, 0.05F).visible(this.waves::isState);
      this.waveDensity = new NumberSetting("Плотность волн", 1.15F, 0.5F, 3.0F, 0.05F).visible(this.waves::isState);
      this.waveGlow = new NumberSetting("Сила волн", 1.0F, 0.2F, 3.0F, 0.05F).visible(this.waves::isState);
      this.glow = new BooleanSetting("Свечение", true);
      this.glowIntensity = new NumberSetting("Сила свечения", 2.0F, 1.0F, 5.0F, 0.1F).visible(this.glow::isState);
      this.glowLayers = new NumberSetting("Слои свечения", 3.0F, 1.0F, 6.0F, 1.0F).visible(this.glow::isState);
      this.pulse = new BooleanSetting("Пульсирование", false);
      this.pulseSpeed = new NumberSetting("Скорость пульсации", 2.0F, 0.5F, 5.0F, 0.1F).visible(this.pulse::isState);
      this.hideOriginal = new BooleanSetting("Скрыть оригинал", false);
      this.hideItemsAndCape = new BooleanSetting("Скрывать предметы и плащ", false);
      this.startTime = System.currentTimeMillis();
      this.addSettings(new Setting[]{
         this.rendering,
         this.waves,
         this.waveSpeedX,
         this.waveSpeedY,
         this.waveScale,
         this.waveDensity,
         this.waveGlow,
         this.glow,
         this.glowIntensity,
         this.glowLayers,
         this.pulse,
         this.pulseSpeed,
         this.hideOriginal,
         this.hideItemsAndCape
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.outlineAssistReady = false;
      this.nextOutlineRetryAt = 0L;
      this.tryEnsureOutlineProcessor();
   }

   @Override
   public void onDisable() {
      this.outlineAssistReady = false;
      this.nextOutlineRetryAt = 0L;
      super.onDisable();
   }

   @EventLink(
      priority = 100
   )
   public void onRender3D(
      Event3DRender val
   ) {
      if (this.isEnable() && mc.world != null && mc.player != null) {
         if (this.hasOutlineAssistTargets() && !this.outlineAssistReady && System.currentTimeMillis() >= this.nextOutlineRetryAt) {
            this.tryEnsureOutlineProcessor();
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);

         for (PlayerEntity player2 : mc.world
            .getPlayers()) {
            if (this.affects(
                  player2
               )
               && (
                  player2
                        != mc.player
                     || mc.options.getPerspective() != Perspective.FIRST_PERSON
               )) {
               this.renderManualPlayer(
                  val,
                  player2
               );
            }
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         RenderSystem.lineWidth(1.0F);
      }
   }

   private void renderManualPlayer(
      Event3DRender val,
      PlayerEntity val2
   ) {
      if (val2 instanceof AbstractClientPlayerEntity val3
         )
       {
         if ((Object)mc.getEntityRenderDispatcher().getRenderer(
               val2
            ) instanceof PlayerEntityRenderer val4
            )
          {
            PlayerEntityRenderState var6 = val4.createRenderState();
            val4.updateRenderState(
               val3,
               var6,
               val.getTickDelta()
            );
            PlayerEntityModel player2 = (PlayerEntityModel)val4.getModel();
            player2.setAngles(
               var6
            );
            MatrixStack matrices = val.getMatrices();
            matrices.push();
            this.setupModelMatrix(
               matrices,
               var6,
               val4,
               val.getCamera()
                  .getPos(),
               val2,
               val.getTickDelta()
            );
            int var9 = this.resolveFillColor(
               val2
            );
            int var10 = this.resolveOutlineColor(
               val2
            );
            this.renderShaderFillModel(
               matrices,
               player2,
               0.0F,
               var9
            );
            this.renderOutlineModel(
               matrices,
               player2,
               0.0F,
               var10
            );
            matrices.pop();
         }
      }
   }

   private void setupModelMatrix(
      MatrixStack val,
      PlayerEntityRenderState val2,
      PlayerEntityRenderer val3,
      Vec3d val4,
      PlayerEntity val5,
      float val6
   ) {
      Vec3d pos = val5.getLerpedPos(
         val6
      );
      double d = pos.x
         - val4.x;
      double d2 = pos.y
         - val4.y;
      double d3 = pos.z
         - val4.z;
      val.translate(
         d,
         d2,
         d3
      );
      if (val2.sleepingDirection
         != null) {
         float height = val2.standingEyeHeight
            - 0.1F;
         val.translate(
            (float)(
                  -val2.sleepingDirection
                     .getOffsetX()
               )
               * height,
            0.0F,
            (float)(
                  -val2.sleepingDirection
                     .getOffsetZ()
               )
               * height
         );
      }

      float f = val2.baseScale;
      val.scale(
         f,
         f,
         f
      );
      LivingEntityRendererAccessor entity2 = (LivingEntityRendererAccessor)val3;
      entity2.AspectDLC$setupTransforms(
         val2,
         val,
         val2.bodyYaw,
         f
      );
      val.scale(
         -1.0F, -1.0F, 1.0F
      );
      entity2.AspectDLC$scale(
         val2,
         val
      );
      val.translate(
         0.0F, -1.501F, 0.0F
      );
   }

   private void renderShaderFillModel(
      MatrixStack val,
      BipedEntityModel<?> val2,
      float val3,
      int val4
   ) {
      if (!this.waves.isState()) {
         this.renderSolidFillModel(
            val,
            val2,
            val3,
            val4
         );
      } else {
         ShaderProgram val5 = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.chamsFill);
         if (val5
            != null) {
            RenderSystem.setShader(ClientShaders.chamsFill);
            this.setUniform(
               val5,
               "time",
               this.waves.isState() ? (float)(System.currentTimeMillis() - this.startTime) / 1000.0F : 0.0F
            );
            this.setUniform(
               val5,
               "speedX",
               this.waveSpeedX.get()
            );
            this.setUniform(
               val5,
               "speedY",
               this.waveSpeedY.get()
            );
            this.setUniform(
               val5,
               "scale",
               this.waveScale.get()
            );
            this.setUniform(
               val5,
               "density",
               this.waveDensity.get()
            );
            this.setUniform(
               val5,
               "glowStrength",
               this.waveGlow.get()
            );
            BufferBuilder buffer = Tessellator.getInstance()
               .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            ModelPart val6 = val2.getRootPart();
            this.renderFillPart(
               val,
               buffer,
               val6,
               val2.head,
               -4.0F,
               -8.0F,
               -4.0F,
               8.0F,
               8.0F,
               8.0F,
               val3,
               val4
            );
            this.renderFillPart(
               val,
               buffer,
               val6,
               val2.body,
               -4.0F,
               0.0F,
               -2.0F,
               8.0F,
               12.0F,
               4.0F,
               val3,
               val4
            );
            this.renderFillPart(
               val,
               buffer,
               val6,
               val2.rightArm,
               -3.0F,
               -2.0F,
               -2.0F,
               4.0F,
               12.0F,
               4.0F,
               val3,
               val4
            );
            this.renderFillPart(
               val,
               buffer,
               val6,
               val2.leftArm,
               -1.0F,
               -2.0F,
               -2.0F,
               4.0F,
               12.0F,
               4.0F,
               val3,
               val4
            );
            this.renderFillPart(
               val,
               buffer,
               val6,
               val2.rightLeg,
               -2.0F,
               0.0F,
               -2.0F,
               4.0F,
               12.0F,
               4.0F,
               val3,
               val4
            );
            this.renderFillPart(
               val,
               buffer,
               val6,
               val2.leftLeg,
               -2.0F,
               0.0F,
               -2.0F,
               4.0F,
               12.0F,
               4.0F,
               val3,
               val4
            );
            BufferRenderer.drawWithGlobalProgram(
               buffer.end()
            );
         }
      }
   }

   private void renderSolidFillModel(
      MatrixStack val,
      BipedEntityModel<?> val2,
      float val3,
      int val4
   ) {
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      ModelPart val5 = val2.getRootPart();
      this.renderSolidFillPart(
         val,
         buffer,
         val5,
         val2.head,
         -4.0F,
         -8.0F,
         -4.0F,
         8.0F,
         8.0F,
         8.0F,
         val3,
         val4
      );
      this.renderSolidFillPart(
         val,
         buffer,
         val5,
         val2.body,
         -4.0F,
         0.0F,
         -2.0F,
         8.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      this.renderSolidFillPart(
         val,
         buffer,
         val5,
         val2.rightArm,
         -3.0F,
         -2.0F,
         -2.0F,
         4.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      this.renderSolidFillPart(
         val,
         buffer,
         val5,
         val2.leftArm,
         -1.0F,
         -2.0F,
         -2.0F,
         4.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      this.renderSolidFillPart(
         val,
         buffer,
         val5,
         val2.rightLeg,
         -2.0F,
         0.0F,
         -2.0F,
         4.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      this.renderSolidFillPart(
         val,
         buffer,
         val5,
         val2.leftLeg,
         -2.0F,
         0.0F,
         -2.0F,
         4.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void renderSolidFillPart(
      MatrixStack val,
      BufferBuilder val2,
      ModelPart val3,
      ModelPart val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      int val12
   ) {
      val.push();
      val3.rotate(
         val
      );
      val4.rotate(
         val
      );
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      float f = 0.0625F;
      float f2 = val11
         * f;
      float f3 = val5
            * f
         - f2;
      float f4 = val6
            * f
         - f2;
      float f5 = val7
            * f
         - f2;
      float f6 = (
               val5
                  + val8
            )
            * f
         + f2;
      float f7 = (
               val6
                  + val9
            )
            * f
         + f2;
      float f8 = (
               val7
                  + val10
            )
            * f
         + f2;
      this.addSolidQuad(
         val2,
         matrix,
         f3,
         f7,
         f5,
         f3,
         f7,
         f8,
         f6,
         f7,
         f8,
         f6,
         f7,
         f5,
         val12
      );
      this.addSolidQuad(
         val2,
         matrix,
         f3,
         f4,
         f8,
         f3,
         f4,
         f5,
         f6,
         f4,
         f5,
         f6,
         f4,
         f8,
         val12
      );
      this.addSolidQuad(
         val2,
         matrix,
         f3,
         f4,
         f5,
         f3,
         f7,
         f5,
         f6,
         f7,
         f5,
         f6,
         f4,
         f5,
         val12
      );
      this.addSolidQuad(
         val2,
         matrix,
         f6,
         f4,
         f8,
         f6,
         f7,
         f8,
         f3,
         f7,
         f8,
         f3,
         f4,
         f8,
         val12
      );
      this.addSolidQuad(
         val2,
         matrix,
         f3,
         f4,
         f8,
         f3,
         f7,
         f8,
         f3,
         f7,
         f5,
         f3,
         f4,
         f5,
         val12
      );
      this.addSolidQuad(
         val2,
         matrix,
         f6,
         f4,
         f5,
         f6,
         f7,
         f5,
         f6,
         f7,
         f8,
         f6,
         f4,
         f8,
         val12
      );
      val.pop();
   }

   private void renderFillPart(
      MatrixStack val,
      BufferBuilder val2,
      ModelPart val3,
      ModelPart val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      int val12
   ) {
      val.push();
      val3.rotate(
         val
      );
      val4.rotate(
         val
      );
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      float f = 0.0625F;
      float f2 = val11
         * f;
      float f3 = val5
            * f
         - f2;
      float f4 = val6
            * f
         - f2;
      float f5 = val7
            * f
         - f2;
      float f6 = (
               val5
                  + val8
            )
            * f
         + f2;
      float f7 = (
               val6
                  + val9
            )
            * f
         + f2;
      float f8 = (
               val7
                  + val10
            )
            * f
         + f2;
      this.addQuad(
         val2,
         matrix,
         f3,
         f7,
         f5,
         f3,
         f7,
         f8,
         f6,
         f7,
         f8,
         f6,
         f7,
         f5,
         val12
      );
      this.addQuad(
         val2,
         matrix,
         f3,
         f4,
         f8,
         f3,
         f4,
         f5,
         f6,
         f4,
         f5,
         f6,
         f4,
         f8,
         val12
      );
      this.addQuad(
         val2,
         matrix,
         f3,
         f4,
         f5,
         f3,
         f7,
         f5,
         f6,
         f7,
         f5,
         f6,
         f4,
         f5,
         val12
      );
      this.addQuad(
         val2,
         matrix,
         f6,
         f4,
         f8,
         f6,
         f7,
         f8,
         f3,
         f7,
         f8,
         f3,
         f4,
         f8,
         val12
      );
      this.addQuad(
         val2,
         matrix,
         f3,
         f4,
         f8,
         f3,
         f7,
         f8,
         f3,
         f7,
         f5,
         f3,
         f4,
         f5,
         val12
      );
      this.addQuad(
         val2,
         matrix,
         f6,
         f4,
         f5,
         f6,
         f7,
         f5,
         f6,
         f7,
         f8,
         f6,
         f4,
         f8,
         val12
      );
      val.pop();
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
      int color2 = ColorUtil.red(
         val15
      );
      int color3 = ColorUtil.green(
         val15
      );
      int color4 = ColorUtil.blue(
         val15
      );
      int color5 = ColorUtil.alpha(
         val15
      );
      float f = this.waveU(
         val3,
         val4,
         val5
      );
      float f2 = this.waveV(
         val3,
         val4,
         val5
      );
      float f3 = this.waveU(
         val6,
         val7,
         val8
      );
      float f4 = this.waveV(
         val6,
         val7,
         val8
      );
      float f5 = this.waveU(
         val9,
         val10,
         val11
      );
      float f6 = this.waveV(
         val9,
         val10,
         val11
      );
      float f7 = this.waveU(
         val12,
         val13,
         val14
      );
      float f8 = this.waveV(
         val12,
         val13,
         val14
      );
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .texture(
            f,
            f2
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .texture(
            f3,
            f4
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .texture(
            f5,
            f6
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
      val.vertex(
            val2,
            val12,
            val13,
            val14
         )
         .texture(
            f7,
            f8
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
   }

   private void addSolidQuad(
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
      int color2 = ColorUtil.red(
         val15
      );
      int color3 = ColorUtil.green(
         val15
      );
      int color4 = ColorUtil.blue(
         val15
      );
      int color5 = ColorUtil.alpha(
         val15
      );
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
      val.vertex(
            val2,
            val12,
            val13,
            val14
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
   }

   private float waveU(
      float val,
      float f9,
      float val2
   ) {
      return val
            * 1.15F
         + val2
            * 0.72F;
   }

   private float waveV(
      float val,
      float val2,
      float val3
   ) {
      return val2
            * 1.05F
         - val3
            * 0.38F
         + val
            * 0.18F;
   }

   private void renderOutlineModel(
      MatrixStack val,
      BipedEntityModel<?> val2,
      float val3,
      int val4
   ) {
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      GL11.glEnable(2848);
      GL11.glHint(
         3154,
         4354
      );
      RenderSystem.lineWidth(0.5F);
      if (this.glow.isState()) {
         RenderSystem.blendFuncSeparate(770, 1, 1, 0);
         int n = Math.max(
            1, Math.round(this.glowLayers.get())
         );
         float x2 = Math.max(
            1.0F, this.glowIntensity.get()
         );

         for (int i = n;
            i
               >= 1;
            i--
         ) {
            float f = val3
               + (float)i
                  * 0.5F
                  * x2;
            float f2 = 1.0F
               / (float)(
                  i
                     + 1
               )
               * 0.7F;
            int n2 = Math.max(
               1,
               Math.min(
                  255,
                  Math.round(
                     (float)ColorUtil.alpha(
                           val4
                        )
                        * f2
                  )
               )
            );
            this.drawOutlineParts(
               val,
               val2,
               f,
               this.withAlpha(
                  val4,
                  n2
               )
            );
         }
      }

      RenderSystem.defaultBlendFunc();
      this.drawOutlineParts(
         val,
         val2,
         val3,
         val4
      );
      GL11.glDisable(2848);
   }

   private void drawOutlineParts(
      MatrixStack val,
      BipedEntityModel<?> val2,
      float val3,
      int val4
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      ModelPart val5 = val2.getRootPart();
      this.renderPartOutlineLines(
         val,
         buffer,
         val5,
         val2.head,
         -4.0F,
         -8.0F,
         -4.0F,
         8.0F,
         8.0F,
         8.0F,
         val3,
         val4
      );
      this.renderPartOutlineLines(
         val,
         buffer,
         val5,
         val2.body,
         -4.0F,
         0.0F,
         -2.0F,
         8.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      this.renderPartOutlineLines(
         val,
         buffer,
         val5,
         val2.rightArm,
         -3.0F,
         -2.0F,
         -2.0F,
         4.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      this.renderPartOutlineLines(
         val,
         buffer,
         val5,
         val2.leftArm,
         -1.0F,
         -2.0F,
         -2.0F,
         4.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      this.renderPartOutlineLines(
         val,
         buffer,
         val5,
         val2.rightLeg,
         -2.0F,
         0.0F,
         -2.0F,
         4.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      this.renderPartOutlineLines(
         val,
         buffer,
         val5,
         val2.leftLeg,
         -2.0F,
         0.0F,
         -2.0F,
         4.0F,
         12.0F,
         4.0F,
         val3,
         val4
      );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void renderPartOutlineLines(
      MatrixStack val,
      BufferBuilder val2,
      ModelPart val3,
      ModelPart val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11,
      int val12
   ) {
      val.push();
      val3.rotate(
         val
      );
      val4.rotate(
         val
      );
      float f = 0.0625F;
      float f2 = val11
         * f;
      float f3 = val5
            * f
         - f2;
      float f4 = val6
            * f
         - f2;
      float f5 = val7
            * f
         - f2;
      float f6 = (
               val5
                  + val8
            )
            * f
         + f2;
      float f7 = (
               val6
                  + val9
            )
            * f
         + f2;
      float f8 = (
               val7
                  + val10
            )
            * f
         + f2;
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      this.addLine(
         val2,
         matrix,
         f3,
         f4,
         f5,
         f6,
         f4,
         f5,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f6,
         f4,
         f5,
         f6,
         f4,
         f8,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f6,
         f4,
         f8,
         f3,
         f4,
         f8,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f3,
         f4,
         f8,
         f3,
         f4,
         f5,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f3,
         f7,
         f5,
         f6,
         f7,
         f5,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f6,
         f7,
         f5,
         f6,
         f7,
         f8,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f6,
         f7,
         f8,
         f3,
         f7,
         f8,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f3,
         f7,
         f8,
         f3,
         f7,
         f5,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f3,
         f4,
         f5,
         f3,
         f7,
         f5,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f6,
         f4,
         f5,
         f6,
         f7,
         f5,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f6,
         f4,
         f8,
         f6,
         f7,
         f8,
         val12
      );
      this.addLine(
         val2,
         matrix,
         f3,
         f4,
         f8,
         f3,
         f7,
         f8,
         val12
      );
      val.pop();
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
      int color2 = ColorUtil.red(
         val9
      );
      int color3 = ColorUtil.green(
         val9
      );
      int color4 = ColorUtil.blue(
         val9
      );
      int color5 = ColorUtil.alpha(
         val9
      );
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            color2,
            color3,
            color4,
            color5
         );
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

   public boolean affects(
      PlayerEntity val
   ) {
      if (!this.isEnable()
         || val
            == null
         || !val.isAlive()
         )
       {
         return false;
      } else if (val
         == mc.player) {
         MultiBooleanSetting var3 = this.rendering;
         return var3.is("Себя") && mc.options.getPerspective() != Perspective.FIRST_PERSON;
      } else if (this.isFriend(
         val
      )) {
         MultiBooleanSetting var2 = this.rendering;
         return var2.is("Друзей");
      } else {
         MultiBooleanSetting var10000 = this.rendering;
         return var10000.is("Игроков");
      }
   }

   public boolean shouldHideBaseModel(
      PlayerEntity val
   ) {
      return this.hideOriginal.isState()
         && this.affects(
            val
         );
   }

   public boolean shouldHideItemsAndCape(
      PlayerEntity val
   ) {
      return this.hideItemsAndCape.isState()
         && this.affects(
            val
         );
   }

   public boolean shouldUseOutlineAssist(
      PlayerEntity val
   ) {
      return this.affects(
         val
      );
   }

   public boolean shouldHideOutlineFramebuffer() {
      return this.isEnable() && this.hasOutlineAssistTargets();
   }

   public int resolveFillColor(
      PlayerEntity val
   ) {
      return this.applyPulse(
         this.baseFillColor(
            val
         )
      );
   }

   public int resolveOutlineColor(
      PlayerEntity val
   ) {
      return this.applyPulse(
         this.baseOutlineColor(
            val
         )
      );
   }

   private int baseFillColor(
      PlayerEntity val
   ) {
      return this.isFriend(
            val
         )
         ? FRIEND_FILL_COLOR
         : this.vividWithAlpha(ColorUtil.getThemeColor(), 1.18F, 1.12F, 130);
   }

   private int baseOutlineColor(
      PlayerEntity val
   ) {
      return this.isFriend(
            val
         )
         ? FRIEND_OUTLINE_COLOR
         : this.vividWithAlpha(ColorUtil.getThemeColor(), 1.12F, 1.08F, 255);
   }

   private int applyPulse(
      int val
   ) {
      if (!this.pulse.isState()) {
         return val;
      } else {
         float f = (float)(
               System.currentTimeMillis() - this.startTime
            )
            / 1000.0F;
         float f2 = (float)(
            (
                  Math.sin(
                        (double)(
                              f
                                 * this.pulseSpeed.get()
                           )
                           * Math.PI
                     )
                     + 1.0
               )
               * 0.5
         );
         float f3 = 0.65F
            + 0.35F
               * f2;
         return ColorUtil.multAlpha(
            val,
            f3
         );
      }
   }

   private int vividWithAlpha(
      int val,
      float val2,
      float val3,
      int val4
   ) {
      float[] val5 = Color.RGBtoHSB(
         ColorUtil.red(
            val
         ),
         ColorUtil.green(
            val
         ),
         ColorUtil.blue(
            val
         ),
         null
      );
      float f = MathHelper.clamp(
         val5[1]
            * val2,
         0.0F,
         1.0F
      );
      float f2 = MathHelper.clamp(
         Math.max(
               val5[2],
               0.8F
            )
            * val3,
         0.0F,
         1.0F
      );
      int color2 = Color.HSBtoRGB(
         val5[0],
         f,
         f2
      );
      return ColorUtil.rgba(
         ColorUtil.red(
            color2
         ),
         ColorUtil.green(
            color2
         ),
         ColorUtil.blue(
            color2
         ),
         val4
      );
   }

   private int withAlpha(
      int val,
      int val2
   ) {
      return val
            & 16777215
         | (
               val2
                  & 255
            )
            << 24;
   }

   private boolean isFriend(
      PlayerEntity val
   ) {
      return AspectDLC.INSTANCE != null
         && AspectDLC.INSTANCE.friendStorage != null
         && AspectDLC.INSTANCE
            .friendStorage
            .isFriend(
               val.getName()
                  .getString()
            );
   }

   private boolean hasOutlineAssistTargets() {
      if (this.isEnable() && mc.world != null && mc.player != null) {
         for (PlayerEntity player2 : mc.world
            .getPlayers()) {
            if (this.shouldUseOutlineAssist(
               player2
            )) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean tryEnsureOutlineProcessor() {
      if (mc.worldRenderer == null) {
         this.outlineAssistReady = false;
         return false;
      } else if (mc.worldRenderer.getEntityOutlinesFramebuffer() != null) {
         this.outlineAssistReady = true;
         return true;
      } else {
         try {
            mc.worldRenderer.loadEntityOutlinePostProcessor();
         } catch (Exception var2) {
         }

         this.outlineAssistReady = mc.worldRenderer.getEntityOutlinesFramebuffer() != null;
         if (!this.outlineAssistReady) {
            this.nextOutlineRetryAt = System.currentTimeMillis() + 3000L;
         }

         return this.outlineAssistReady;
      }
   }
}
