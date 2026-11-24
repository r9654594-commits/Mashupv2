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
import dev.leg1tka.api.events.implement.EventAttackEntity;
import dev.leg1tka.api.events.implement.EventPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class HitMarker extends Module {
   public static HitMarker INSTANCE = new HitMarker();
   private final NumberSetting size;
   private final NumberSetting fadeInTime;
   private final NumberSetting displayTime;
   private final NumberSetting fadeOutTime;
   private final BooleanSetting glow;
   private final BooleanSetting scaleAnim;
   private final BooleanSetting ghostEffect;
   private final BooleanSetting totemEffect;
   private final ArrayList<IsdeadHelper_2> hitMarkers;
   private final ArrayList<IsdeadHelper_3> hitGhosts;
   private final List<IsdeadHelper> totemSpirals;
   private static final float GLOW_INTENSITY = 0.38F;

   public HitMarker() {
      super("HitMarker", Category.RENDER);
      String var1 = "HitMarker";
      this.size = new NumberSetting("Размер", 0.5F, 0.1F, 2.0F, 0.05F);
      this.fadeInTime = new NumberSetting("Время появления", 100.0F, 50.0F, 500.0F, 10.0F);
      this.displayTime = new NumberSetting("Время показа", 300.0F, 100.0F, 1000.0F, 50.0F);
      this.fadeOutTime = new NumberSetting("Время исчезновения", 200.0F, 50.0F, 500.0F, 10.0F);
      this.glow = new BooleanSetting("Свечение", true);
      this.scaleAnim = new BooleanSetting("Анимация масштаба", true);
      this.ghostEffect = new BooleanSetting("Эффект силуэта", true);
      this.totemEffect = new BooleanSetting("Эффекты при тотеме", true);
      this.hitMarkers = new ArrayList<>();
      this.hitGhosts = new ArrayList<>();
      this.totemSpirals = new CopyOnWriteArrayList<>();
      this.addSettings(new Setting[]{
         this.size,
         this.fadeInTime,
         this.displayTime,
         this.fadeOutTime,
         this.glow,
         this.scaleAnim,
         this.ghostEffect,
         this.totemEffect
      });
   }

   @Override
   public void onDisable() {
      this.hitMarkers.clear();
      this.hitGhosts.clear();
      this.totemSpirals.clear();
      super.onDisable();
   }

   private Identifier getTexture() {
      return ClientIdentifier.of("textures/cross/cross.png");
   }

   private Identifier getGlowTexture() {
      return ClientIdentifier.of("textures/targetesp/bloom.png");
   }

   @EventLink
   public void onAttack(
      EventAttackEntity val
   ) {
      if (mc.player != null && mc.world != null) {
         Entity entity2 = val.getTarget();
         if (entity2
            != null) {
            synchronized (this.hitMarkers) {
               this.hitMarkers
                  .add(
                     new IsdeadHelper_2(
                        this.resolveHitPosition(
                           val.getPlayer(),
                           entity2
                        ),
                        System.currentTimeMillis(),
                        (long)this.fadeInTime.get(),
                        (long)this.displayTime.get(),
                        (long)this.fadeOutTime.get()
                     )
                  );
            }

            if (this.ghostEffect.isState()
               && entity2 instanceof LivingEntity val2
               )
             {
               float f = mc.getRenderTickCounter()
                  .getTickDelta(true);
               double d = MathHelper.lerp(
                  (double)f,
                  entity2.prevX,
                  entity2.getX()
               );
               double d2 = MathHelper.lerp(
                  (double)f,
                  entity2.prevY,
                  entity2.getY()
               );
               double d3 = MathHelper.lerp(
                  (double)f,
                  entity2.prevZ,
                  entity2.getZ()
               );
               Vec3d velocity = entity2.getVelocity();
               float f2 = (float)Math.sqrt(
                  velocity.x
                        * velocity.x
                     + velocity.z
                        * velocity.z
               );
               Vec3d vec;
               if (f2
                  > 0.01F) {
                  vec = new Vec3d(
                     velocity.x
                        / (double)f2,
                     0.0,
                     velocity.z
                        / (double)f2
                  );
               } else {
                  Vec3d vec2 = new Vec3d(
                     entity2.getX()
                        - val.getPlayer()
                           .getX(),
                     0.0,
                     entity2.getZ()
                        - val.getPlayer()
                           .getZ()
                  );
                  double d4 = vec2.length();
                  vec = d4
                        > 0.01
                     ? vec2.multiply(
                        1.0
                           / d4
                     )
                     : new Vec3d(0.0, 0.0, 1.0);
               }

               float f3 = MathHelper.lerp(
                  f,
                  ((LivingEntity)entity2)
                     .prevBodyYaw,
                  ((LivingEntity)entity2)
                     .bodyYaw
               );
               float f4 = f3;
               float f5 = MathHelper.lerp(
                  f,
                  entity2.prevPitch,
                  entity2.getPitch()
               );
               float f6 = val2.limbAnimator
                  .getPos(
                     f
                  );
               float f7 = val2.limbAnimator
                  .getSpeed(
                     f
                  );
               boolean flag = entity2.isSneaking();
               synchronized (this.hitGhosts) {
                  this.hitGhosts
                     .add(
                        new IsdeadHelper_3(
                           new Vec3d(
                              d,
                              d2,
                              d3
                           ),
                           vec,
                           f3,
                           f4
                              - f3,
                           f5,
                           f6,
                           f7,
                           flag,
                           System.currentTimeMillis()
                        )
                     );
               }
            }
         }
      }
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (this.totemEffect.isState()) {
         if (mc.world != null && mc.player != null) {
            if (val.getType()
               == EventPacket.Type.RECEIVE) {
               if (val.getPacket() instanceof EntityStatusS2CPacket val2
                  )
                {
                  if (val2.getStatus()
                     == 35) {
                     mc.execute(
                        () -> {
                           if (mc.world != null) {
                              Entity entity2 = val2.getEntity(
                                 mc.world
                              );
                              if (entity2 instanceof LivingEntity val3
                                 )
                               {
                                 float f = mc.getRenderTickCounter()
                                    .getTickDelta(true);
                                 double var5 = MathHelper.lerp(
                                    (double)f,
                                    entity2.prevX,
                                    entity2.getX()
                                 );
                                 double var7 = MathHelper.lerp(
                                       (double)f,
                                       entity2.prevY,
                                       entity2.getY()
                                    )
                                    + (double)entity2.getHeight()
                                       * 0.6;
                                 double var9 = MathHelper.lerp(
                                    (double)f,
                                    entity2.prevZ,
                                    entity2.getZ()
                                 );
                                 this.totemSpirals.add(new IsdeadHelper(new Vec3d(var5, var7, var9), System.currentTimeMillis()));
                              }
                           }
                        }
                     );
                  }
               }
            }
         }
      }
   }

   private Vec3d resolveHitPosition(
      Entity val,
      Entity val2
   ) {
      Vec3d vec = new Vec3d(
         val2.getX(),
         val2.getY()
            + (double)val2.getHeight()
               / 2.0,
         val2.getZ()
      );
      if (val
         == null) {
         return vec;
      } else {
         Vec3d pos = val.getCameraPosVec(
            1.0F
         );
         Vec3d vec2 = val.getRotationVec(
            1.0F
         );
         Vec3d vec3 = val2.getBoundingBox()
            .getCenter();
         double d = Math.max(
            pos.distanceTo(
                  vec3
               )
               + 1.0,
            6.0
         );
         Vec3d vec4 = pos.add(
            vec2.multiply(
               d
            )
         );
         Optional<Vec3d> vec5 = val2.getBoundingBox().raycast(
               pos,
               vec4
            );
         return vec5.isPresent()
            ? vec5.get()
            : pos.add(
               vec2.multiply(
                  pos.distanceTo(
                     vec3
                  )
               )
            );
      }
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.player != null && mc.world != null) {
         synchronized (this.hitMarkers) {
            this.hitMarkers.removeIf(IsdeadHelper_2::isDead);
         }

         synchronized (this.hitGhosts) {
            this.hitGhosts.removeIf(IsdeadHelper_3::isDead);
         }

         Vec3d vec = mc.gameRenderer
            .getCamera()
            .getPos();
         MatrixStack matrices = val.getMatrices();
         if (!this.hitMarkers.isEmpty()) {
            synchronized (this.hitMarkers) {
               this.hitMarkers.clear();
            }
         }

         if (this.ghostEffect.isState() && !this.hitGhosts.isEmpty()) {
            ArrayList<IsdeadHelper_3> list;
            synchronized (this.hitGhosts) {
               list = new ArrayList<>(
                  this.hitGhosts
               );
            }

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

            for (IsdeadHelper_3 val2 : list) {
               float f4 = val2.getProgress();
               float f5 = val2.getAlpha();
               if (!(
                  f5
                     <= 0.0F
               )) {
                  double d = (double)f4
                     * 2.5;
                  double d2 = -val2.moveDir
                        .x
                     * d;
                  double d3 = -val2.moveDir
                        .z
                     * d;
                  double d4 = Math.sin(
                        (double)f4
                           * Math.PI
                     )
                     * 0.6;
                  double d5 = val2.position
                        .x
                     + d2
                     - vec.x;
                  double d6 = val2.position
                        .y
                     + d4
                     - vec.y;
                  double d7 = val2.position
                        .z
                     + d3
                     - vec.z;
                  float f6 = 1.0F
                     + 0.25F * MathHelper.sin((float)((double)System.currentTimeMillis() / 80.0));
                  float f7 = f5
                     * 0.38F
                     * f6;
                  matrices.push();
                  matrices.translate(
                     d5,
                     d6,
                     d7
                  );
                  matrices.multiply(
                     RotationAxis.POSITIVE_Y
                        .rotationDegrees(
                           -val2.bodyYaw
                        )
                  );
                  this.renderGhostGlow(
                     matrices,
                     f,
                     f2,
                     f3,
                     f7,
                     f5,
                     val2
                  );
                  matrices.pop();
               }
            }
         }

         if (this.totemEffect.isState() && !this.totemSpirals.isEmpty()) {
            this.totemSpirals.removeIf(IsdeadHelper::isDead);

            for (IsdeadHelper val3 : this.totemSpirals) {
               this.renderTotemSpiral(
                  matrices,
                  vec,
                  val3
               );
            }
         }
      }
   }

   private void renderTotemSpiral(
      MatrixStack val,
      Vec3d val2,
      IsdeadHelper val3
   ) {
      float f = val3.getProgress();
      float f2 = val3.getAlpha();
      if (!(
         f2
            <= 0.01F
      )) {
         long val4 = System.currentTimeMillis();
         float f3 = (float)(
            val3.origin
                  .x
               - val2.x
         );
         float f4 = (float)(
            val3.origin
                  .y
               - val2.y
         );
         float f5 = (float)(
            val3.origin
                  .z
               - val2.z
         );
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         RenderSystem.lineWidth(1.6F);
         GL11.glEnable(2848);
         val.push();
         val.translate(
            f3,
            f4,
            f5
         );
         int n = 6;

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
            float f7;
            float f8;
            if (f
               < 0.35F) {
               f8 = f
                  / 0.35F;
               f7 = 1.8F
                  + (float)Math.sin(
                        (double)f8
                           * Math.PI
                           * 0.5
                     )
                     * 0.5F;
            } else {
               f8 = 1.0F
                  - (
                        f
                           - 0.35F
                     )
                     / 0.65F;
               f7 = 1.8F
                  * f8;
            }

            boolean flag = i
                  % 2
               == 0;
            int n2 = flag
               ? 255
               : 80;
            int n3 = flag
               ? 220
               : 255;
            int n4 = flag
               ? 30
               : 60;
            int n5 = (int)(
               f2
                  * 255.0F
            );
            int n6 = 32;
            BufferBuilder buffer = Tessellator.getInstance()
               .begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            Matrix4f matrix = val.peek()
               .getPositionMatrix();
            float f9 = (float)(
                  (double)val4
                     / 400.0
               )
               * (
                  flag
                     ? 1.0F
                     : -1.0F
               );

            for (int i2 = 0;
               i2
                  <= n6;
               i2++
            ) {
               float f10 = (float)i2
                  / (float)n6;
               float f11 = f6
                  + f10
                     * (float) (Math.PI * 4)
                  + f9;
               float f12 = f7
                  * f10;
               float f13 = MathHelper.cos(
                     f11
                  )
                  * f12;
               float f14 = MathHelper.sin(
                     f11
                  )
                  * f12;
               float f15 = MathHelper.sin(
                     f10
                           * (float) Math.PI
                           * 3.0F
                        + f9
                           * 0.5F
                  )
                  * 0.4F
                  * f8;
               float f16 = MathHelper.clamp(
                  1.0F
                     - Math.abs(
                           f10
                              - 0.5F
                        )
                        * 2.0F,
                  0.1F,
                  1.0F
               );
               int n7 = (int)(
                  (float)n5
                     * f16
               );
               buffer.vertex(
                     matrix,
                     f13,
                     f15,
                     f14
                  )
                  .color(
                     n2,
                     n3,
                     n4,
                     n7
                  );
            }

            BufferRenderer.drawWithGlobalProgram(
               buffer.end()
            );
            if (f8
               > 0.1F) {
               BufferBuilder buffer2 = Tessellator.getInstance()
                  .begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

               for (int i3 = 0;
                  i3
                     <= n6;
                  i3++
               ) {
                  float f17 = (float)i3
                     / (float)n6;
                  float f18 = f6
                     + f17
                        * (float) (Math.PI * 4)
                     + f9
                     + 0.15F;
                  float f19 = f7
                     * f17
                     * 0.7F;
                  float f20 = MathHelper.cos(
                        f18
                     )
                     * f19;
                  float f21 = MathHelper.sin(
                        f18
                     )
                     * f19;
                  float f22 = MathHelper.sin(
                        f17
                              * (float) Math.PI
                              * 3.0F
                           + f9
                              * 0.5F
                           + 1.0F
                     )
                     * 0.25F
                     * f8;
                  int n8 = (int)(
                     (float)n5
                        * 0.5F
                        * MathHelper.clamp(
                           1.0F
                              - Math.abs(
                                    f17
                                       - 0.5F
                                 )
                                 * 2.0F,
                           0.0F,
                           1.0F
                        )
                  );
                  buffer2.vertex(
                        matrix,
                        f20,
                        f22,
                        f21
                     )
                     .color(
                        255,
                        255,
                        200,
                        n8
                     );
               }

               BufferRenderer.drawWithGlobalProgram(
                  buffer2.end()
               );
            }
         }

         if (f
               > 0.15F
            && f
               < 0.6F) {
            float f23 = 1.0F
               - Math.abs(
                  (
                        f
                           - 0.35F
                     )
                     / 0.25F
               );
            f23 = MathHelper.clamp(
               f23,
               0.0F,
               1.0F
            );
            float f24 = 0.35F
               * f23;
            int n9 = (int)(
               f2
                  * f23
                  * 180.0F
            );
            BufferBuilder buffer3 = Tessellator.getInstance()
               .begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            Matrix4f matrix2 = val.peek()
               .getPositionMatrix();
            int n10 = 24;

            for (int i4 = 0;
               i4
                  <= n10;
               i4++
            ) {
               float f25 = (float)i4
                  / (float)n10
                  * (float) (Math.PI * 2);
               float f26 = MathHelper.cos(
                     f25
                  )
                  * f24;
               float f27 = MathHelper.sin(
                     f25
                  )
                  * f24;
               buffer3.vertex(
                     matrix2,
                     f26,
                     0.0F,
                     f27
                  )
                  .color(
                     255,
                     255,
                     180,
                     n9
                  );
            }

            BufferRenderer.drawWithGlobalProgram(
               buffer3.end()
            );
         }

         val.pop();
         GL11.glDisable(2848);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private void renderGhostGlow(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      float f28,
      IsdeadHelper_3 val6
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
      float f3 = Math.min(
         1.0F,
         val6.limbSwingAmount
      );
      float f4 = MathHelper.cos(
            val6.limbSwing
               * 0.6662F
         )
         * 1.4F
         * f3;
      float f5 = MathHelper.cos(
            val6.limbSwing
                  * 0.6662F
               + (float) Math.PI
         )
         * 1.4F
         * f3;
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
      );
      val.pop();
      float f6 = 3.0F
         * f;
      val.push();
      val.translate(
         -4.0F
               * f
            - f6
               / 2.0F,
         22.0F
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
      this.renderGlowBox(
         val,
         -f6
            / 2.0F,
         -10.0F
            * f,
         -2.0F
            * f,
         f6,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         val5
      );
      val.pop();
      val.push();
      val.translate(
         4.0F
               * f
            + f6
               / 2.0F,
         22.0F
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
      this.renderGlowBox(
         val,
         -f6
            / 2.0F,
         -10.0F
            * f,
         -2.0F
            * f,
         f6,
         12.0F
            * f,
         4.0F
            * f,
         val2,
         val3,
         val4,
         val5
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
               f4
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
               -f4
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
      this.renderGlowFace(
         val.peek()
            .getPositionMatrix(),
         f,
         f2,
         f3
            + val7
               / 2.0F
            + 0.01F,
         x2,
         n,
         n2,
         n3,
         n4
      );
      this.renderGlowFace(
         val.peek()
            .getPositionMatrix(),
         f,
         f2,
         f3
            - val7
               / 2.0F
            - 0.01F,
         x2,
         n,
         n2,
         n3,
         n4
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
      this.renderGlowFace(
         val.peek()
            .getPositionMatrix(),
         0.0F,
         0.0F,
         val7
               / 2.0F
            + 0.01F,
         x2,
         n,
         n2,
         n3,
         n4
      );
      this.renderGlowFace(
         val.peek()
            .getPositionMatrix(),
         0.0F,
         0.0F,
         -val7
               / 2.0F
            - 0.01F,
         x2,
         n,
         n2,
         n3,
         n4
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
      this.renderGlowFace(
         val.peek()
            .getPositionMatrix(),
         0.0F,
         0.0F,
         val6
               / 2.0F
            + 0.01F,
         x2,
         n,
         n2,
         n3,
         n4
      );
      this.renderGlowFace(
         val.peek()
            .getPositionMatrix(),
         0.0F,
         0.0F,
         -val6
               / 2.0F
            - 0.01F,
         x2,
         n,
         n2,
         n3,
         n4
      );
      val.pop();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      this.renderSolidBox(
         val.peek()
            .getPositionMatrix(),
         val2,
         val3,
         val4,
         val5,
         val6,
         val7,
         n,
         n2,
         n3,
         (int)(
            (float)n4
               * 0.35F
         )
      );
      RenderSystem.setShaderTexture(0, this.getGlowTexture());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
   }

   private void renderGlowFace(
      Matrix4f val,
      float val2,
      float val3,
      float val4,
      float val5,
      int val6,
      int val7,
      int val8,
      int val9
   ) {
      float f = val5
         / 2.0F;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      buffer.vertex(
            val,
            val2
               - f,
            val3
               - f,
            val4
         )
         .texture(0.0F, 0.0F)
         .color(
            val6,
            val7,
            val8,
            val9
         );
      buffer.vertex(
            val,
            val2
               - f,
            val3
               + f,
            val4
         )
         .texture(0.0F, 1.0F)
         .color(
            val6,
            val7,
            val8,
            val9
         );
      buffer.vertex(
            val,
            val2
               + f,
            val3
               + f,
            val4
         )
         .texture(1.0F, 1.0F)
         .color(
            val6,
            val7,
            val8,
            val9
         );
      buffer.vertex(
            val,
            val2
               + f,
            val3
               - f,
            val4
         )
         .texture(1.0F, 0.0F)
         .color(
            val6,
            val7,
            val8,
            val9
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void renderSolidBox(
      Matrix4f val,
      float val2,
      float val3,
      float val4,
      float val5,
      float val6,
      float val7,
      int val8,
      int val9,
      int val10,
      int val11
   ) {
      float f = val2
         + val5;
      float f2 = val3
         + val6;
      float f3 = val4
         + val7;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      buffer.vertex(
            val,
            val2,
            val3,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            val3,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            f2,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            f2,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            val3,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            val3,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            f2,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            f2,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            val3,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            val3,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            f2,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            f2,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            val3,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            val3,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            f2,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            f2,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            f2,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            f2,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            f2,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            f2,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            val3,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            val2,
            val3,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            val3,
            val4
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      buffer.vertex(
            val,
            f,
            val3,
            f3
         )
         .color(
            val8,
            val9,
            val10,
            val11
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }
}
