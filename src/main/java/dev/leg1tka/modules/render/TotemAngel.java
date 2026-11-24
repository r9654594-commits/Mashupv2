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
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class TotemAngel extends Module {
   public static TotemAngel INSTANCE = new TotemAngel();
   private final ModeSetting mode = new ModeSetting(
      "Режим", "Angel", "Angel"
   );
   private final BooleanSetting visuals = new BooleanSetting("Визуал", true);
   private final BooleanSetting chatInfo = new BooleanSetting("Чат инфо", true);
   private final NumberSetting riseHeight = new NumberSetting("Высота", 4.0F, 0.2F, 10.0F, 0.1F);
   private final NumberSetting duration = new NumberSetting("Длительность", 3.0F, 0.2F, 6.0F, 0.1F);
   private final MultiBooleanSetting renderModes = new MultiBooleanSetting("Режим", new BooleanSetting("Ангел", true));
   private static final float WING_SCALE = 1.0F;
   private static final float FLAP_SPEED = 1.6F;
   private static final float FLAP_AMPLITUDE = 25.0F;
   private static final float GLOW_INTENSITY = 0.1F;
   private static final float HALO_SIZE = 0.4F;
   private static final Identifier SPARKLE_TEXTURE = ClientIdentifier.of("textures/particle/sparkle.png");
   private static final int GREEN_COLOR = -13238485;
   private static final int YELLOW_COLOR = -3797;
   private final List<PositionHolder> ghosts = new CopyOnWriteArrayList<>();
   private final List<OriginHolder> sphereEffects = new CopyOnWriteArrayList<>();
   private final Map<Integer, Long> recentSphereSpawns = new ConcurrentHashMap<>();

   public TotemAngel() {
      super(
         "TotemPop", "Отображает эффект и пишет в чат при срабатывании тотема", Category.RENDER
      );
      this.addSettings(new Setting[]{
         this.renderModes,
         this.mode.visible(() -> false),
         this.visuals.visible(() -> false),
         this.chatInfo,
         this.riseHeight,
         this.duration
      });
   }

   @Override
   public void onDisable() {
      this.ghosts.clear();
      super.onDisable();
   }

   private Identifier getGlowTexture() {
      return ClientIdentifier.of("textures/targetesp/bloom.png");
   }

   private Identifier getSkinTexture() {
      return ClientIdentifier.of("textures/skin/skin.png");
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (mc.world != null
         && mc.player != null
         && val.getType()
            == EventPacket.Type.RECEIVE) {
         if (val.getPacket() instanceof EntityStatusS2CPacket val2
            && val2.getStatus()
               == 35) {
            mc.execute(
               () -> this.handleTotemPopPacket(
                     val2
                  )
            );
         }
      }
   }

   private void handleTotemPopPacket(
      EntityStatusS2CPacket val
   ) {
      if (mc.world != null && mc.player != null) {
         if (val.getEntity(
            mc.world
         ) instanceof AbstractClientPlayerEntity val2
            )
          {
            if (this.renderModes.is("Ангел")) {
               this.addGhost(
                  val2
               );
            }

            if (this.chatInfo.isState()
               && val2
                  != mc.player) {
               String name = val2.getName()
                  .getString();
               ChatUtil.sendMessage(
                  name
                     + " §7снёс тотем!"
               );
            }
         }
      }
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.world != null && mc.player != null) {
         if (this.renderModes.is("Ангел") && !this.ghosts.isEmpty()) {
            this.renderGhosts(
               val.getMatrices(),
               val.getTickDelta()
            );
         }
      }
   }

   private void addGhost(
      AbstractClientPlayerEntity val
   ) {
      float f = mc.getRenderTickCounter()
         .getTickDelta(true);
      double d = MathHelper.lerp(
         (double)f,
         val.lastRenderX,
         val.getX()
      );
      double d2 = MathHelper.lerp(
         (double)f,
         val.lastRenderY,
         val.getY()
      );
      double d3 = MathHelper.lerp(
         (double)f,
         val.lastRenderZ,
         val.getZ()
      );
      float f2 = MathHelper.lerp(
         f,
         val.prevBodyYaw,
         val.bodyYaw
      );
      float f3 = MathHelper.lerp(
         f,
         val.prevHeadYaw,
         val.headYaw
      );
      float f4 = MathHelper.lerp(
         f,
         val.prevPitch,
         val.getPitch()
      );
      float f5 = val.limbAnimator
         .getPos(
            f
         );
      float f6 = val.limbAnimator
         .getSpeed(
            f
         );
      boolean flag = val.isSneaking();
      float height = val.getHeight();
      this.ghosts
         .add(
            new PositionHolder(
               new Vec3d(
                  d,
                  d2,
                  d3
               ),
               f2,
               f3
                  - f2,
               f4,
               f5,
               f6,
               flag,
               height,
               System.currentTimeMillis()
            )
         );
   }

   private void addSphereEffect(
      AbstractClientPlayerEntity val
   ) {
      if (val
            != null
         && val
            != mc.player) {
         long val2 = System.currentTimeMillis();
         this.recentSphereSpawns
            .entrySet()
            .removeIf(
               val3 -> val2
                        - val3.getValue()
                     > 1000L
            );
         Long val4 = this.recentSphereSpawns
            .get(
               val.getId()
            );
         if (val4
               == null
            || val2
                  - val4
               >= 120L) {
            this.recentSphereSpawns
               .put(
                  val.getId(),
                  val2
               );
            double d = val.getY()
               + (double)val.getHeight()
                  * 0.62;
            List val5 = new ArrayList<>(
               64
            );
            ThreadLocalRandom val6 = ThreadLocalRandom.current();

            for (int i = 0;
               i
                  < 64;
               i++
            ) {
               double d2 = val6.nextDouble(
                  0.0, Math.PI * 2
               );
               double d3 = val6.nextDouble(
                  -0.8, 0.8
               );
               Vec3d vec = new Vec3d(
                     Math.cos(
                           d2
                        )
                        * Math.cos(
                           d3
                        ),
                     Math.sin(
                              d3
                           )
                           * 0.62
                        + val6.nextDouble(
                           -0.12, 0.24
                        ),
                     Math.sin(
                           d2
                        )
                        * Math.cos(
                           d3
                        )
                  )
                  .normalize();
               val5.add(
                  new DirectionHolder(
                     vec,
                     val6.nextFloat(
                        0.28F, 1.08F
                     ),
                     val6.nextFloat(
                        0.85F, 1.55F
                     ),
                     val6.nextFloat(
                        1.05F, 1.85F
                     ),
                     val6.nextFloat(
                        0.95F, 1.45F
                     ),
                     val6.nextFloat(
                        0.0F, 1.0F
                     ),
                     val6.nextBoolean()
                        ? -13238485
                        : -3797
                  )
               );
            }

            this.sphereEffects
               .add(
                  new OriginHolder(
                     new Vec3d(
                        val.getX(),
                        d,
                        val.getZ()
                     ),
                     val2,
                     val6.nextFloat(
                        0.0F, 360.0F
                     ),
                     val5,
                     this.createSphereOrbitLines()
                  )
               );
         }
      }
   }

   private void renderGhosts(
      MatrixStack val,
      float val19
   ) {
      Vec3d vec = mc.gameRenderer
         .getCamera()
         .getPos();
      long val2 = System.currentTimeMillis();
      List val3 = new ArrayList<>();
      int color2 = ColorUtil.getThemeColor();
      float f = ColorUtil.redf(
         color2
      );
      float f2 = ColorUtil.greenf(
         color2
      );
      float f3 = ColorUtil.bluef(
         color2
      );

      for (PositionHolder val4 : this.ghosts) {
         float f4 = (float)(
               val2
                  - val4.startTime
            )
            / (this.duration.get() * 1000.0F);
         if (f4
            >= 1.0F) {
            val3.add(
               val4
            );
         } else {
            double d = (double)(
               this.riseHeight.get()
                  * this.easeOutCubic(
                     f4
                  )
            );
            float f5 = (
                  1.0F
                     - this.easeInCubic(
                        f4
                     )
               )
               * 0.85F;
            double d2 = val4.position
                  .x
               - vec.x;
            double d3 = val4.position
                  .y
               - vec.y
               + d;
            double d4 = val4.position
                  .z
               - vec.z;
            val.push();
            val.translate(
               d2,
               d3,
               d4
            );
            val.multiply(
               RotationAxis.POSITIVE_Y
                  .rotationDegrees(
                     -val4.bodyYaw
                  )
            );
            this.renderGlowingPlayerModel(
               val,
               f,
               f2,
               f3,
               f5,
               val4
            );
            this.renderColoredPlayerModel(
               val,
               f,
               f2,
               f3,
               f5,
               val4
            );
            this.renderHalo(
               val,
               val4,
               color2,
               f5
            );
            val.pop();
         }
      }

      if (!val3.isEmpty()
         )
       {
         this.ghosts
            .removeAll(
               val3
            );
      }
   }

   private void renderSphereEffects(
      MatrixStack val,
      Vec3d val2
   ) {
      long val3 = System.currentTimeMillis();
      float f = this.duration
            .get()
         * 1000.0F;
      this.sphereEffects
         .removeIf(
            val4 -> (float)(
                     val3
                        - val4.startTime
                  )
                  >= f
         );
      if (!this.sphereEffects.isEmpty()) {
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         this.renderSphereParticles(
            val,
            val2,
            val3,
            f
         );
         this.renderSphereArcs(
            val,
            val2,
            val3,
            f
         );
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private void renderSphereParticles(
      MatrixStack val,
      Vec3d val2,
      long val3,
      float val4
   ) {
      RenderSystem.blendFunc(770, 1);
      RenderSystem.setShaderTexture(0, SPARKLE_TEXTURE);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      float f = mc.gameRenderer
         .getCamera()
         .getYaw();
      float f2 = mc.gameRenderer
         .getCamera()
         .getPitch();
      float f3 = 1.18F;
      float f4 = 0.28F;

      for (OriginHolder val5 : this.sphereEffects) {
         float f5 = (float)(
               val3
                  - val5.startTime
            )
            / val4;
         float f6 = MathHelper.clamp(
            1.0F
               - f5,
            0.0F,
            1.0F
         );
         float f7 = this.easeOutQuad(
            Math.min(
               1.0F,
               f5
                  * 1.12F
            )
         );

         for (DirectionHolder direction2 : val5.particles) {
            float f8 = MathHelper.clamp(
               f5
                     * direction2.timeScale
                  + direction2.progressOffset
                     * 0.1F,
               0.0F,
               1.0F
            );
            float f9 = this.easeOutQuad(
               f8
            );
            float f10 = (
                  0.34F
                     + f9
                        * (
                           1.2F
                              + direction2.spread
                                 * 1.05F
                        )
                     + f7
                        * 0.32F
               )
               * f3;
            float f11 = (float)val3
                  * 0.0012F
                  * direction2.rotationScale
               + direction2.progressOffset
                  * 5.4F;
            double d = (double)(
               (
                     1.0F
                        - f8
                  )
                  * 0.18F
            );
            double d2 = Math.cos(
                  (double)f11
               )
               * d
               * (double)direction2.swirlAmount;
            double d3 = Math.sin(
                     (double)(
                        f11
                           * 1.3F
                     )
                  )
                  * d
                  * 0.75
                  * (double)direction2.swirlAmount
               + (double)(
                  f8
                     * 0.08F
               );
            double d4 = Math.sin(
                  (double)f11
               )
               * d
               * (double)direction2.swirlAmount;
            double d5 = (double)(
               f8
                  * f8
                  * 0.14F
            );
            Vec3d vec = val5.origin
               .add(
                  direction2.direction
                     .multiply(
                        (double)f10
                     )
               )
               .add(
                  d2,
                  d3
                     - d5,
                  d4
               );
            double d6 = vec.x
               - val2.x;
            double d7 = vec.y
               - val2.y;
            double d8 = vec.z
               - val2.z;
            int n = this.setAlpha(
               direction2.color,
               (int)(
                  255.0F
                     * f6
                     * (
                        0.5F
                           + 0.5F
                              * (
                                 1.0F
                                    - f8
                              )
                     )
               )
            );
            float f12 = f4
               * (
                  0.68F
                     + direction2.spread
                        * 0.34F
               )
               * (
                  0.7F
                     + f6
                        * 0.52F
               );
            val.push();
            val.translate(
               d6,
               d7,
               d8
            );
            val.multiply(
               RotationAxis.POSITIVE_Y
                  .rotationDegrees(
                     -f
                  )
            );
            val.multiply(
               RotationAxis.POSITIVE_X
                  .rotationDegrees(
                     f2
                  )
            );
            this.drawSphereBillboard(
               val.peek()
                  .getPositionMatrix(),
               f12,
               n
            );
            val.pop();
         }
      }
   }

   private void renderSphereArcs(
      MatrixStack val,
      Vec3d val2,
      long val3,
      float val4
   ) {
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(1.05F);
      GL11.glEnable(2848);
      GL11.glHint(
         3154,
         4354
      );

      for (OriginHolder val5 : this.sphereEffects) {
         float f = (float)(
               val3
                  - val5.startTime
            )
            / val4;
         float f2 = MathHelper.clamp(
            1.0F
               - f,
            0.0F,
            1.0F
         );
         float f3 = this.easeOutQuad(
            Math.min(
               1.0F,
               f
                  * 1.25F
            )
         );
         float f4 = (float)(
               val3
                  - val5.startTime
            )
            / 1000.0F;
         float f5 = 1.18F
            * (
               0.78F
                  + f3
                     * 0.1F
            );
         double d = val5.origin
               .x
            - val2.x;
         double d2 = val5.origin
               .y
            - val2.y;
         double d3 = val5.origin
               .z
            - val2.z;

         for (RadiusxHolder val6 : val5.orbitLines) {
            val.push();
            val.translate(
               d,
               d2,
               d3
            );
            val.multiply(
               RotationAxis.POSITIVE_Y
                  .rotationDegrees(
                     val5.baseRotation
                        + val6.baseYaw
                        + f4
                           * val6.speedDeg
                  )
            );
            val.multiply(
               RotationAxis.POSITIVE_X
                  .rotationDegrees(
                     val6.tiltX
                  )
            );
            val.multiply(
               RotationAxis.POSITIVE_Z
                  .rotationDegrees(
                     val6.tiltZ
                  )
            );
            this.drawSphereOrbitArc(
               val,
               val6.radiusX
                  * f5,
               val6.radiusZ
                  * f5,
               val6.yOffset,
               val6.startDeg,
               val6.arcDeg,
               f2
                  * val6.alphaMul,
               val6.startColor,
               val6.endColor
            );
            val.pop();
         }
      }

      GL11.glDisable(2848);
   }

   private void drawSphereOrbitArc(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      int val8,
      int val9
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      int n = 28;
      float f = (float)Math.toRadians(
         (double)val5
      );
      float f2 = (float)Math.toRadians(
         (double)(
            val5
               + val6
         )
      );
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

      for (int i = 0;
         i
            <= n;
         i++
      ) {
         float f3 = (float)i
            / (float)n;
         float f4 = MathHelper.lerp(
            f3,
            f,
            f2
         );
         float f5 = MathHelper.cos(
               f4
            )
            * val2;
         float f6 = MathHelper.sin(
               f4
            )
            * val3;
         float f7 = val4
            + MathHelper.sin(
                  f4
                     * 1.35F
               )
               * 0.01F;
         float f8 = MathHelper.clamp(
            1.0F
               - Math.abs(
                     f3
                        - 0.5F
                  )
                  * 2.0F,
            0.0F,
            1.0F
         );
         int n2 = this.fadeLerp(
            val8,
            val9,
            f3,
            val7
               * (
                  0.22F
                     + 0.78F
                        * f8
               )
         );
         buffer.vertex(
               matrix,
               f5,
               f7,
               f6
            )
            .color(
               n2
            );
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      BufferBuilder buffer2 = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

      for (int i2 = 0;
         i2
            <= n;
         i2++
      ) {
         float f9 = (float)i2
            / (float)n;
         float f10 = MathHelper.lerp(
            f9,
            f
               + 0.015F,
            f2
               - 0.012F
         );
         float f11 = MathHelper.cos(
               f10
            )
            * (
               val2
                  + 0.012F
            );
         float f12 = MathHelper.sin(
               f10
            )
            * (
               val3
                  + 0.012F
            );
         float f13 = val4
            + 0.004F
            + MathHelper.sin(
                  f10
                        * 1.35F
                     + 0.9F
               )
               * 0.008F;
         float f14 = MathHelper.clamp(
            1.0F
               - Math.abs(
                     f9
                        - 0.5F
                  )
                  * 2.0F,
            0.0F,
            1.0F
         );
         int n3 = this.fadeLerp(
            val8,
            val9,
            f9,
            val7
               * 0.14F
               * f14
         );
         buffer2.vertex(
               matrix,
               f11,
               f13,
               f12
            )
            .color(
               n3
            );
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer2.end()
      );
   }

   private List<RadiusxHolder> createSphereOrbitLines() {
      List val = new ArrayList<>(
         5
      );
      val.add(
         new RadiusxHolder(
            1.02F,
            0.66F,
            0.2F,
            196.0F,
            156.0F,
            14.0F,
            -12.0F,
            54.0F,
            0.46F,
            -13238485,
            -13238485
         )
      );
      val.add(
         new RadiusxHolder(
            0.92F,
            0.6F,
            0.16F,
            188.0F,
            148.0F,
            14.0F,
            -12.0F,
            54.0F,
            0.22F,
            -13238485,
            -13238485
         )
      );
      val.add(
         new RadiusxHolder(
            0.86F,
            0.54F,
            -0.12F,
            122.0F,
            112.0F,
            78.0F,
            4.0F,
            -68.0F,
            0.65F,
            -3797,
            -3797
         )
      );
      val.add(
         new RadiusxHolder(
            0.74F,
            0.46F,
            -0.02F,
            314.0F,
            88.0F,
            62.0F,
            -18.0F,
            76.0F,
            0.58F,
            -13238485,
            -3797
         )
      );
      val.add(
         new RadiusxHolder(
            0.68F,
            0.34F,
            0.0F,
            202.0F,
            44.0F,
            8.0F,
            52.0F,
            -44.0F,
            0.18F,
            -13238485,
            -13238485
         )
      );
      return val;
   }

   private void drawSphereBillboard(
      Matrix4f val,
      float val2,
      int val3
   ) {
      float f = val2
         * 0.5F;
      int n = val3
            >> 16
         & 255;
      int n2 = val3
            >> 8
         & 255;
      int n3 = val3
         & 255;
      int n4 = val3
            >> 24
         & 255;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      buffer.vertex(
            val,
            -f,
            -f,
            0.0F
         )
         .texture(0.0F, 1.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            val,
            -f,
            f,
            0.0F
         )
         .texture(0.0F, 0.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            val,
            f,
            f,
            0.0F
         )
         .texture(1.0F, 0.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            val,
            f,
            -f,
            0.0F
         )
         .texture(1.0F, 1.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private int fadeLerp(
      int val,
      int val2,
      float val3,
      float val4
   ) {
      int n = val
            >> 16
         & 255;
      int n2 = val
            >> 8
         & 255;
      int n3 = val
         & 255;
      int n4 = val2
            >> 16
         & 255;
      int n5 = val2
            >> 8
         & 255;
      int n6 = val2
         & 255;
      int n7 = MathHelper.lerp(
         val3,
         n,
         n4
      );
      int n8 = MathHelper.lerp(
         val3,
         n2,
         n5
      );
      int n9 = MathHelper.lerp(
         val3,
         n3,
         n6
      );
      int n10 = MathHelper.clamp(
         (int)(
            255.0F
               * val4
         ),
         0,
         255
      );
      return n10
            << 24
         | n7
            << 16
         | n8
            << 8
         | n9;
   }

   private int setAlpha(
      int val,
      int val2
   ) {
      return MathHelper.clamp(
               val2,
               0,
               255
            )
            << 24
         | val
            & 16777215;
   }

   private float easeOutQuad(
      float val
   ) {
      float f = 1.0F
         - val;
      return 1.0F
         - f
            * f;
   }

   private void renderSkinPlayerModel(
      MatrixStack val,
      float val2,
      PositionHolder val3
   ) {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(
         770,
         771
      );
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.setShaderTexture(0, this.getSkinTexture());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      float f = 0.0625F;
      float f2 = val3.sneaking
         ? 0.25F
         : 0.0F;
      float f3 = val3.limbSwing;
      float f4 = Math.min(
         1.0F,
         val3.limbSwingAmount
      );
      float f5 = MathHelper.cos(
            f3
               * 0.6662F
         )
         * 1.4F
         * f4;
      float f6 = MathHelper.cos(
            f3
                  * 0.6662F
               + (float) Math.PI
         )
         * 2.0F
         * f4;
      int n = (int)(
         val2
            * 255.0F
      );
      val.push();
      val.translate(
         0.0F,
         24.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_Y
            .rotationDegrees(
               val3.netHeadYaw
            )
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               val3.headPitch
            )
      );
      this.renderSkinBox(
         val,
         -4.0F
            * f,
         -8.0F
            * f,
         -4.0F
            * f,
         8.0F
            * f,
         8.0F
            * f,
         8.0F
            * f,
         8,
         8,
         16,
         16,
         64,
         64,
         n
      );
      val.pop();
      val.push();
      if (val3.sneaking
         )
       {
         val.translate(
            0.0F,
            12.0F
               * f,
            0.0F
         );
         val.multiply(
            RotationAxis.POSITIVE_X.rotationDegrees(28.0F)
         );
         val.translate(
            0.0F,
            -12.0F
               * f,
            0.0F
         );
      }

      this.renderSkinBox(
         val,
         -4.0F
            * f,
         12.0F
               * f
            - f2,
         -2.0F
            * f,
         8.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         20,
         20,
         28,
         32,
         64,
         64,
         n
      );
      val.pop();
      val.push();
      val.translate(
         -5.0F
            * f,
         22.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               f6
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderSkinBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         44,
         20,
         48,
         32,
         64,
         64,
         n
      );
      val.pop();
      val.push();
      val.translate(
         5.0F
            * f,
         22.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               -f6
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderSkinBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         36,
         52,
         40,
         64,
         64,
         64,
         n
      );
      val.pop();
      val.push();
      val.translate(
         -2.0F
            * f,
         12.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               f5
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderSkinBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         4,
         20,
         8,
         32,
         64,
         64,
         n
      );
      val.pop();
      val.push();
      val.translate(
         2.0F
            * f,
         12.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               -f5
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderSkinBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         20,
         52,
         24,
         64,
         64,
         64,
         n
      );
      val.pop();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
   }

   private void renderSkinBox(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      int val8,
      int val9,
      int val10,
      int val11,
      int val12,
      int val13,
      int val14
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      float f = val2
         + val5;
      float f2 = val3
         + val6;
      float f3 = val4
         + val7;
      float f4 = val5
         * 16.0F;
      float f5 = val6
         * 16.0F;
      float f6 = val7
         * 16.0F;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float f7 = (float)val8
         / (float)val12;
      float f8 = (float)val9
         / (float)val13;
      float f9 = (float)val10
         / (float)val12;
      float f10 = (float)val11
         / (float)val13;
      float f11 = (
            (float)val8
               + f6
         )
         / (float)val12;
      float f12 = (
            (float)val8
               + f6
               + f4
         )
         / (float)val12;
      float f13 = (
            (float)val9
               + f6
         )
         / (float)val13;
      float f14 = (
            (float)val9
               + f6
               + f5
         )
         / (float)val13;
      buffer.vertex(
            matrix,
            val2,
            val3,
            f3
         )
         .texture(
            f11,
            f14
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            f3
         )
         .texture(
            f12,
            f14
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .texture(
            f12,
            f13
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            f3
         )
         .texture(
            f11,
            f13
         )
         .color(
            255,
            255,
            255,
            val14
         );
      float f15 = (
            (float)val8
               + f6
               + f4
               + f6
         )
         / (float)val12;
      float f16 = (
            (float)val8
               + f6
               + f4
               + f6
               + f4
         )
         / (float)val12;
      float f17 = (
            (float)val9
               + f6
         )
         / (float)val13;
      float f18 = (
            (float)val9
               + f6
               + f5
         )
         / (float)val13;
      buffer.vertex(
            matrix,
            f,
            val3,
            val4
         )
         .texture(
            f15,
            f18
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            val2,
            val3,
            val4
         )
         .texture(
            f16,
            f18
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            val4
         )
         .texture(
            f16,
            f17
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            val4
         )
         .texture(
            f15,
            f17
         )
         .color(
            255,
            255,
            255,
            val14
         );
      float f19 = (
            (float)val8
               + f6
         )
         / (float)val12;
      float f20 = (
            (float)val8
               + f6
               + f4
         )
         / (float)val12;
      float f21 = (float)val9
         / (float)val13;
      float f22 = (
            (float)val9
               + f6
         )
         / (float)val13;
      buffer.vertex(
            matrix,
            val2,
            f2,
            val4
         )
         .texture(
            f19,
            f21
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            f3
         )
         .texture(
            f19,
            f22
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .texture(
            f20,
            f22
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            val4
         )
         .texture(
            f20,
            f21
         )
         .color(
            255,
            255,
            255,
            val14
         );
      float f23 = (
            (float)val8
               + f6
               + f4
         )
         / (float)val12;
      float f24 = (
            (float)val8
               + f6
               + f4
               + f4
         )
         / (float)val12;
      float f25 = (float)val9
         / (float)val13;
      float f26 = (
            (float)val9
               + f6
         )
         / (float)val13;
      buffer.vertex(
            matrix,
            val2,
            val3,
            f3
         )
         .texture(
            f23,
            f25
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            val2,
            val3,
            val4
         )
         .texture(
            f23,
            f26
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            val4
         )
         .texture(
            f24,
            f26
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            f3
         )
         .texture(
            f24,
            f25
         )
         .color(
            255,
            255,
            255,
            val14
         );
      float f27 = (float)val8
         / (float)val12;
      float f28 = (
            (float)val8
               + f6
         )
         / (float)val12;
      float f29 = (
            (float)val9
               + f6
         )
         / (float)val13;
      float f30 = (
            (float)val9
               + f6
               + f5
         )
         / (float)val13;
      buffer.vertex(
            matrix,
            val2,
            val3,
            val4
         )
         .texture(
            f27,
            f30
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            val2,
            val3,
            f3
         )
         .texture(
            f28,
            f30
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            f3
         )
         .texture(
            f28,
            f29
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            val4
         )
         .texture(
            f27,
            f29
         )
         .color(
            255,
            255,
            255,
            val14
         );
      float f31 = (
            (float)val8
               + f6
               + f4
         )
         / (float)val12;
      float f32 = (
            (float)val8
               + f6
               + f4
               + f6
         )
         / (float)val12;
      float f33 = (
            (float)val9
               + f6
         )
         / (float)val13;
      float f34 = (
            (float)val9
               + f6
               + f5
         )
         / (float)val13;
      buffer.vertex(
            matrix,
            f,
            val3,
            f3
         )
         .texture(
            f31,
            f34
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            val4
         )
         .texture(
            f32,
            f34
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            val4
         )
         .texture(
            f32,
            f33
         )
         .color(
            255,
            255,
            255,
            val14
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .texture(
            f31,
            f33
         )
         .color(
            255,
            255,
            255,
            val14
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void renderWings(
      MatrixStack val,
      PositionHolder val2,
      float val3,
      float f35,
      int val4,
      float val5
   ) {
      float y2 = (float)System.currentTimeMillis()
            / 50.0F
            * 0.22F
            * 1.6F
         + val3
            * 2.0F;
      float f = MathHelper.sin(
         y2
      );
      float f2 = MathHelper.cos(
         y2
      );
      float f3 = 18.0F
         + val3
            * 15.0F;
      float f4 = 13.0F
         + f2
            * 4.0F;
      float f5 = f
         * 25.0F;
      if (val2.sneaking
         )
       {
         f3 -= 3.0F;
         f4 += 8.0F;
      }

      int color2 = ColorUtil.setAlphaColor(
         val4,
         (int)(
            132.0F
               * val5
         )
      );
      int color3 = ColorUtil.setAlphaColor(
         ColorUtil.darken(
            val4,
            0.85F
         ),
         (int)(
            102.0F
               * val5
         )
      );
      int color4 = ColorUtil.setAlphaColor(
         ColorUtil.darken(
            val4,
            0.7F
         ),
         (int)(
            190.0F
               * val5
         )
      );
      int color5 = ColorUtil.setAlphaColor(
         ColorUtil.darken(
            val4,
            0.52F
         ),
         (int)(
            175.0F
               * val5
         )
      );
      int color6 = ColorUtil.setAlphaColor(
         ColorUtil.darken(
            val4,
            0.58F
         ),
         (int)(
            150.0F
               * val5
         )
      );
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.blendFunc(
         770,
         771
      );
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      val.push();
      float f6 = val2.sneaking
         ? 0.25F
         : 0.0F;
      val.translate(
         0.0F,
         1.3F
            - f6,
         -0.08F
      );
      if (val2.sneaking
         )
       {
         val.multiply(
            RotationAxis.POSITIVE_X.rotationDegrees(24.0F)
         );
      }

      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      this.renderButterflyWing(
         buffer,
         val,
         1.0F,
         f3,
         f4,
         f5,
         1.0F,
         color2,
         color3,
         color4,
         color5,
         color6
      );
      this.renderButterflyWing(
         buffer,
         val,
         -1.0F,
         f3,
         f4,
         f5,
         1.0F,
         color2,
         color3,
         color4,
         color5,
         color6
      );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.blendFunc(770, 1);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      int color7 = ColorUtil.setAlphaColor(
         val4,
         (int)(
            72.0F
               * val5
         )
      );
      int color8 = ColorUtil.setAlphaColor(
         ColorUtil.darken(
            val4,
            0.82F
         ),
         (int)(
            66.0F
               * val5
         )
      );
      BufferBuilder buffer2 = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      this.renderButterflyGlow(
         buffer2,
         val,
         1.0F,
         f3,
         f4,
         f5,
         1.0F,
         color7,
         color8
      );
      this.renderButterflyGlow(
         buffer2,
         val,
         -1.0F,
         f3,
         f4,
         f5,
         1.0F,
         color7,
         color8
      );
      BufferRenderer.drawWithGlobalProgram(
         buffer2.end()
      );
      val.pop();
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
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
      int val9,
      int val10,
      int val11,
      int val12
   ) {
      float f = 0.12F
         * val7;
      float f2 = 1.5F
         * val7;
      float f3 = 0.61F
         * val7;
      float f4 = 1.1F
         * val7;
      float f5 = 0.35F
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
      this.addDoubleSidedGradientQuad(
         val,
         matrix,
         val3
            * f,
         0.02F,
         0.0F,
         val3
            * (
               f
                  + f2
                     * 0.18F
            ),
         f3
            * 0.95F,
         -0.06F,
         val3
            * (
               f
                  + f2
            ),
         f3
            * 0.3F,
         -0.13F,
         val3
            * (
               f
                  + f2
                     * 0.2F
            ),
         0.06F,
         -0.03F,
         val8,
         val9
      );
      this.addDoubleSidedGradientQuad(
         val,
         matrix,
         val3
            * f,
         -0.01F,
         -0.02F,
         val3
            * (
               f
                  + f4
                     * 0.18F
            ),
         -f5
            * 0.94F,
         -0.1F,
         val3
            * (
               f
                  + f4
            ),
         -f5
            * 0.36F,
         -0.17F,
         val3
            * (
               f
                  + f4
                     * 0.6F
            ),
         -0.1F,
         -0.07F,
         val9,
         val8
      );
      this.addDoubleSidedQuad(
         val,
         matrix,
         val3
            * f,
         0.012F,
         0.01F,
         val3
            * f,
         -0.032F,
         -0.01F,
         val3
            * (
               f
                  + f2
                     * 0.56F
            ),
         -0.008F,
         -0.08F,
         val3
            * (
               f
                  + f2
                     * 0.56F
            ),
         0.008F,
         -0.04F,
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
      this.renderWingBoneLine(
         val,
         matrix,
         val3
            * f,
         0.0F,
         -0.02F,
         val3
            * (
               f
                  + f2
                     * 0.22F
            ),
         f3
            * 0.82F,
         -0.07F,
         val3
            * (
               f
                  + f2
            ),
         f3
            * 0.3F,
         -0.13F,
         0.016F
            * val7,
         val12,
         val12
      );
      this.renderWingBoneLine(
         val,
         matrix,
         val3
            * f,
         0.012F,
         -0.008F,
         val3
            * (
               f
                  + f2
                     * 0.36F
            ),
         f3
            * 0.56F,
         -0.065F,
         val3
            * (
               f
                  + f2
                     * 0.86F
            ),
         f3
            * 0.26F,
         -0.115F,
         0.012F
            * val7,
         val11,
         val12
      );
      this.renderWingBoneLine(
         val,
         matrix,
         val3
            * f,
         -0.02F,
         -0.04F,
         val3
            * (
               f
                  + f4
                     * 0.22F
            ),
         -f5
            * 0.84F,
         -0.11F,
         val3
            * (
               f
                  + f4
            ),
         -f5
            * 0.34F,
         -0.18F,
         0.009F
            * val7,
         val12,
         val12
      );
      this.renderWingBoneLine(
         val,
         matrix,
         val3
            * f,
         -0.004F,
         -0.018F,
         val3
            * (
               f
                  + f4
                     * 0.34F
            ),
         -f5
            * 0.52F,
         -0.085F,
         val3
            * (
               f
                  + f4
                     * 0.88F
            ),
         -f5
            * 0.3F,
         -0.145F,
         0.01F
            * val7,
         val12,
         val11
      );
      val2.pop();
   }

   private void renderButterflyGlow(
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
      float f2 = 1.5F
         * val7;
      float f3 = 0.61F
         * val7;
      float f4 = 1.1F
         * val7;
      float f5 = 0.35F
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
      this.renderWingBoneLine(
         val,
         matrix,
         val3
            * f,
         0.0F,
         -0.02F,
         val3
            * (
               f
                  + f2
                     * 0.2F
            ),
         f3
            * 0.86F,
         -0.08F,
         val3
            * (
               f
                  + f2
            ),
         f3
            * 0.3F,
         -0.16F,
         0.02F
            * val7,
         val8,
         val9
      );
      this.renderWingBoneLine(
         val,
         matrix,
         val3
            * f,
         -0.02F,
         -0.05F,
         val3
            * (
               f
                  + f4
                     * 0.2F
            ),
         -f5
            * 0.86F,
         -0.13F,
         val3
            * (
               f
                  + f4
            ),
         -f5
            * 0.32F,
         -0.2F,
         0.018F
            * val7,
         val9,
         val8
      );
      this.renderWingBoneLine(
         val,
         matrix,
         val3
            * f,
         0.012F,
         -0.008F,
         val3
            * (
               f
                  + f2
                     * 0.36F
            ),
         f3
            * 0.56F,
         -0.07F,
         val3
            * (
               f
                  + f2
                     * 0.84F
            ),
         f3
            * 0.25F,
         -0.125F,
         0.016F
            * val7,
         val8,
         val9
      );
      val2.pop();
   }

   private void renderHalo(
      MatrixStack val,
      PositionHolder val2,
      int val3,
      float val4
   ) {
      float f = val2.sneaking
         ? 0.25F
         : 0.0F;
      float y2 = (float)System.currentTimeMillis()
         / 30.0F
         % 360.0F;
      val.push();
      val.translate(
         0.0F,
         1.9F
            - f,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X.rotationDegrees(15.0F)
      );
      val.multiply(
         RotationAxis.POSITIVE_Y
            .rotationDegrees(
               y2
            )
      );
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.blendFunc(770, 1);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      int color2 = ColorUtil.setAlphaColor(
         val3,
         (int)(
            200.0F
               * val4
         )
      );
      int color3 = ColorUtil.setAlphaColor(
         val3,
         (int)(
            100.0F
               * val4
         )
      );
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      this.renderHaloRing(
         matrix,
         0.4F,
         0.03F,
         color2
      );
      this.renderHaloRing(
         matrix,
         0.42000002F,
         0.05F,
         color3
      );
      this.renderHaloRing(
         matrix,
         0.38F,
         0.02F,
         color3
      );
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      val.pop();
   }

   private void renderHaloRing(
      Matrix4f val,
      float val2,
      float val3,
      int val4
   ) {
      int n = 36;
      float f = (float)(
         (Math.PI * 2)
            / (double)n
      );
      int n2 = val4
            >> 16
         & 255;
      int n3 = val4
            >> 8
         & 255;
      int n4 = val4
         & 255;
      int n5 = val4
            >> 24
         & 255;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (int i = 0;
         i
            < n;
         i++
      ) {
         float f2 = (float)i
            * f;
         float f3 = (float)(
               i
                  + 1
            )
            * f;
         float f4 = MathHelper.cos(
               f2
            )
            * (
               val2
                  - val3
                     / 2.0F
            );
         float f5 = MathHelper.sin(
               f2
            )
            * (
               val2
                  - val3
                     / 2.0F
            );
         float f6 = MathHelper.cos(
               f2
            )
            * (
               val2
                  + val3
                     / 2.0F
            );
         float f7 = MathHelper.sin(
               f2
            )
            * (
               val2
                  + val3
                     / 2.0F
            );
         float f8 = MathHelper.cos(
               f3
            )
            * (
               val2
                  - val3
                     / 2.0F
            );
         float f9 = MathHelper.sin(
               f3
            )
            * (
               val2
                  - val3
                     / 2.0F
            );
         float f10 = MathHelper.cos(
               f3
            )
            * (
               val2
                  + val3
                     / 2.0F
            );
         float f11 = MathHelper.sin(
               f3
            )
            * (
               val2
                  + val3
                     / 2.0F
            );
         buffer.vertex(
               val,
               f4,
               0.01F,
               f5
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f6,
               0.01F,
               f7
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f10,
               0.01F,
               f11
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f8,
               0.01F,
               f9
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f4,
               -0.01F,
               f5
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f8,
               -0.01F,
               f9
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f10,
               -0.01F,
               f11
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f6,
               -0.01F,
               f7
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f6,
               -0.01F,
               f7
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f10,
               -0.01F,
               f11
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f10,
               0.01F,
               f11
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f6,
               0.01F,
               f7
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f4,
               0.01F,
               f5
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f8,
               0.01F,
               f9
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f8,
               -0.01F,
               f9
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
         buffer.vertex(
               val,
               f4,
               -0.01F,
               f5
            )
            .color(
               n2,
               n3,
               n4,
               n5
            );
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
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

   private void renderGlowingPlayerModel(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      PositionHolder val6
   ) {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(770, 1);
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.disableCull();
      RenderSystem.setShaderTexture(0, this.getGlowTexture());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      float f = 0.0625F;
      float f2 = val6.sneaking
         ? 0.25F
         : 0.0F;
      float f3 = val6.limbSwing;
      float f4 = Math.min(
         1.0F,
         val6.limbSwingAmount
      );
      float f5 = MathHelper.cos(
            f3
               * 0.6662F
         )
         * 1.4F
         * f4;
      float f6 = MathHelper.cos(
            f3
                  * 0.6662F
               + (float) Math.PI
         )
         * 1.4F
         * f4;
      val.push();
      val.translate(
         0.0F,
         24.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_Y
            .rotationDegrees(
               val6.netHeadYaw
            )
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               val6.headPitch
            )
      );
      this.renderGlowBox(
         val,
         -4.0F
            * f,
         -8.0F
            * f,
         -4.0F
            * f,
         8.0F
            * f,
         8.0F
            * f,
         8.0F
            * f,
         val2,
         val3,
         val4,
         val5
            * 0.1F
      );
      val.pop();
      val.push();
      if (val6.sneaking
         )
       {
         val.translate(
            0.0F,
            12.0F
               * f,
            0.0F
         );
         val.multiply(
            RotationAxis.POSITIVE_X.rotationDegrees(25.0F)
         );
         val.translate(
            0.0F,
            -12.0F
               * f,
            0.0F
         );
      }

      this.renderGlowBox(
         val,
         -4.0F
            * f,
         12.0F
               * f
            - f2,
         -2.0F
            * f,
         8.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         val5
            * 0.1F
      );
      val.pop();
      float f7 = 3.0F
         * f;
      val.push();
      val.translate(
         -4.0F
               * f
            - f7
               / 2.0F,
         22.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               f6
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderGlowBox(
         val,
         -f7
            / 2.0F,
         -10.0F
            * f,
         -2.0F
            * f,
         f7,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         val5
            * 0.1F
      );
      val.pop();
      val.push();
      val.translate(
         4.0F
               * f
            + f7
               / 2.0F,
         22.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               -f6
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderGlowBox(
         val,
         -f7
            / 2.0F,
         -10.0F
            * f,
         -2.0F
            * f,
         f7,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         val5
            * 0.1F
      );
      val.pop();
      val.push();
      val.translate(
         -2.0F
            * f,
         12.0F
            * f,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               f5
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderGlowBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         val5
            * 0.1F
      );
      val.pop();
      val.push();
      val.translate(
         2.0F
            * f,
         12.0F
            * f,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               -f5
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderGlowBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         val5
            * 0.1F
      );
      val.pop();
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
   }

   private void renderGlowBox(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11
   ) {
      float f = val2
         + val5
            / 2.0F;
      float f2 = val3
         + val6
            / 2.0F;
      float f3 = val4
         + val7
            / 2.0F;
      float x2 = Math.max(
            val5,
            Math.max(
               val6,
               val7
            )
         )
         * 1.8F;
      this.renderGlowSprite(
         val,
         f,
         f2,
         f3
            + val7
               / 2.0F
            + 0.01F,
         x2,
         val5,
         val6,
         val8,
         val9,
         val10,
         val11
      );
      this.renderGlowSprite(
         val,
         f,
         f2,
         f3
            - val7
               / 2.0F
            - 0.01F,
         x2,
         val5,
         val6,
         val8,
         val9,
         val10,
         val11
      );
      val.push();
      val.translate(
         f,
         f2,
         f3
      );
      val.multiply(
         RotationAxis.POSITIVE_Y.rotationDegrees(90.0F)
      );
      this.renderGlowSpriteRotated(
         val,
         0.0F,
         0.0F,
         val7
               / 2.0F
            + 0.01F,
         x2,
         val7,
         val6,
         val8,
         val9,
         val10,
         val11
      );
      this.renderGlowSpriteRotated(
         val,
         0.0F,
         0.0F,
         -val7
               / 2.0F
            - 0.01F,
         x2,
         val7,
         val6,
         val8,
         val9,
         val10,
         val11
      );
      val.pop();
      val.push();
      val.translate(
         f,
         f2,
         f3
      );
      val.multiply(
         RotationAxis.POSITIVE_X.rotationDegrees(90.0F)
      );
      this.renderGlowSpriteRotated(
         val,
         0.0F,
         0.0F,
         val6
               / 2.0F
            + 0.01F,
         x2,
         val5,
         val7,
         val8,
         val9,
         val10,
         val11
      );
      this.renderGlowSpriteRotated(
         val,
         0.0F,
         0.0F,
         -val6
               / 2.0F
            - 0.01F,
         x2,
         val5,
         val7,
         val8,
         val9,
         val10,
         val11
      );
      val.pop();
      float f4 = val11
         * 0.4F;
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      this.renderSolidBox(
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
         f4
      );
      RenderSystem.setShaderTexture(0, this.getGlowTexture());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
   }

   private void renderGlowSprite(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      float f35,
      float f36,
      float val6,
      float val7,
      float val8,
      float val9
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      int n = (int)(
         val6
            * 255.0F
      );
      int n2 = (int)(
         val7
            * 255.0F
      );
      int n3 = (int)(
         val8
            * 255.0F
      );
      int n4 = (int)(
         MathHelper.clamp(
               val9,
               0.0F,
               1.0F
            )
            * 255.0F
      );
      float f = val5
         / 2.0F;
      float f2 = val5
         / 2.0F;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      buffer.vertex(
            matrix,
            val2
               - f,
            val3
               - f2,
            val4
         )
         .texture(0.0F, 0.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2
               - f,
            val3
               + f2,
            val4
         )
         .texture(0.0F, 1.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2
               + f,
            val3
               + f2,
            val4
         )
         .texture(1.0F, 1.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2
               + f,
            val3
               - f2,
            val4
         )
         .texture(1.0F, 0.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void renderGlowSpriteRotated(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      float f35,
      float f36,
      float val6,
      float val7,
      float val8,
      float val9
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      int n = (int)(
         val6
            * 255.0F
      );
      int n2 = (int)(
         val7
            * 255.0F
      );
      int n3 = (int)(
         val8
            * 255.0F
      );
      int n4 = (int)(
         MathHelper.clamp(
               val9,
               0.0F,
               1.0F
            )
            * 255.0F
      );
      float f = val5
         / 2.0F;
      float f2 = val5
         / 2.0F;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      buffer.vertex(
            matrix,
            val2
               - f,
            val3
               - f2,
            val4
         )
         .texture(0.0F, 0.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2
               - f,
            val3
               + f2,
            val4
         )
         .texture(0.0F, 1.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2
               + f,
            val3
               + f2,
            val4
         )
         .texture(1.0F, 1.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2
               + f,
            val3
               - f2,
            val4
         )
         .texture(1.0F, 0.0F)
         .color(
            n,
            n2,
            n3,
            n4
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void renderSolidBox(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8,
      float val9,
      float val10,
      float val11
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      float f = val2
         + val5;
      float f2 = val3
         + val6;
      float f3 = val4
         + val7;
      int n = (int)(
         val8
            * 255.0F
      );
      int n2 = (int)(
         val9
            * 255.0F
      );
      int n3 = (int)(
         val10
            * 255.0F
      );
      int n4 = (int)(
         MathHelper.clamp(
               val11,
               0.0F,
               1.0F
            )
            * 255.0F
      );
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      buffer.vertex(
            matrix,
            val2,
            val3,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            val3,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            val3,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            val3,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            f2,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            val3,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            val2,
            val3,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            val4
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            f,
            val3,
            f3
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void renderColoredPlayerModel(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      PositionHolder val6
   ) {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(
         770,
         771
      );
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      float f = 0.0625F;
      float f2 = val6.sneaking
         ? 0.25F
         : 0.0F;
      float f3 = val6.limbSwing;
      float f4 = Math.min(
         1.0F,
         val6.limbSwingAmount
      );
      float f5 = MathHelper.cos(
            f3
               * 0.6662F
         )
         * 1.4F
         * f4;
      float f6 = MathHelper.cos(
            f3
                  * 0.6662F
               + (float) Math.PI
         )
         * 2.0F
         * f4;
      float f7 = val5
         * 0.8F;
      val.push();
      val.translate(
         0.0F,
         24.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_Y
            .rotationDegrees(
               val6.netHeadYaw
            )
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               val6.headPitch
            )
      );
      this.renderSolidBox(
         val,
         -4.0F
            * f,
         -8.0F
            * f,
         -4.0F
            * f,
         8.0F
            * f,
         8.0F
            * f,
         8.0F
            * f,
         val2,
         val3,
         val4,
         f7
      );
      val.pop();
      val.push();
      if (val6.sneaking
         )
       {
         val.translate(
            0.0F,
            12.0F
               * f,
            0.0F
         );
         val.multiply(
            RotationAxis.POSITIVE_X.rotationDegrees(28.0F)
         );
         val.translate(
            0.0F,
            -12.0F
               * f,
            0.0F
         );
      }

      this.renderSolidBox(
         val,
         -4.0F
            * f,
         12.0F
               * f
            - f2,
         -2.0F
            * f,
         8.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         f7
      );
      val.pop();
      val.push();
      val.translate(
         -5.0F
            * f,
         22.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               f6
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderSolidBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         f7
      );
      val.pop();
      val.push();
      val.translate(
         5.0F
            * f,
         22.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               -f6
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderSolidBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         f7
      );
      val.pop();
      val.push();
      val.translate(
         -2.0F
            * f,
         12.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               f5
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderSolidBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         f7
      );
      val.pop();
      val.push();
      val.translate(
         2.0F
            * f,
         12.0F
               * f
            - f2,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_X
            .rotationDegrees(
               -f5
                  * (180.0F / (float)Math.PI)
            )
      );
      this.renderSolidBox(
         val,
         -2.0F
            * f,
         -12.0F
            * f,
         -2.0F
            * f,
         4.0F
            * f,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         f7
      );
      val.pop();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
   }

   private float easeOutCubic(
      float val
   ) {
      return 1.0F
         - (float)Math.pow(
            1.0
               - (double)val,
            3.0
         );
   }

   private float easeInCubic(
      float val
   ) {
      return val
         * val
         * val;
   }
}
