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
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class HitBubbles extends Module {
   public static HitBubbles INSTANCE = new HitBubbles();
   private static final long LIFE_MS = 1600L;
   private final CopyOnWriteArrayList<PosHelper_2> bubbles = new CopyOnWriteArrayList<>();
   private final Identifier bubbleTexture = ClientIdentifier.of("textures/hitbubble/bubble.png");

   public HitBubbles() {
      super(
         "HitBubbles",
         "Круг при ударе игрока",
         Category.RENDER
      );
   }

   @Override
   public void onDisable() {
      this.bubbles.clear();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val7
   ) {
      long val = System.currentTimeMillis();
      this.bubbles
         .removeIf(
            val2 -> val
                     - val2.spawnTime()
                  >= 1600L
         );
   }

   @EventLink
   public void onAttack(
      EventAttackEntity val
   ) {
      if (val
            != null
         && val.getTarget()
            != null) {
         if (val.getTarget() instanceof LivingEntity val2
            )
          {
            if (val.getPlayer()
               != null) {
               Vec3d vec = this.getHitSideDirection(
                  val2,
                  val.getPlayer()
                     .getPos()
               );
               Vec3d pos2 = this.getHitPosition(
                  val2,
                  vec
               );
               float f = (float)Math.toDegrees(
                  Math.atan2(
                     vec.x,
                     vec.z
                  )
               );
               this.bubbles
                  .add(
                     new PosHelper_2(
                        pos2,
                        System.currentTimeMillis(),
                        (float)(Math.random() * 360.0),
                        f
                     )
                  );
            }
         }
      }
   }

   @EventLink
   public void onWorldRender(
      Event3DRender val
   ) {
      if (!this.bubbles.isEmpty() && mc.player != null) {
         MatrixStack matrices = val.getMatrices();
         Vec3d vec = val.getCamera()
            .getPos();
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.bubbleTexture);
         long val2 = System.currentTimeMillis();

         for (PosHelper_2 val3 : this.bubbles) {
            this.renderSingleBubble(
               matrices,
               vec,
               val3,
               val2
            );
         }

         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private void renderSingleBubble(
      MatrixStack val,
      Vec3d val2,
      PosHelper_2 val3,
      long val4
   ) {
      float f = (float)(
            val4
               - val3.spawnTime()
         )
         / 1600.0F;
      if (!(
         f
            >= 1.0F
      )) {
         float x2 = Math.max(
            0.0F,
            Math.min(
               1.0F,
               f
                  / 0.22F
            )
         );
         float x3 = Math.max(
            0.0F,
            Math.min(
               1.0F,
               (
                     f
                        - 0.225F
                  )
                  / 0.4F
            )
         );
         float f2 = x2
            * x2
            * (
               3.0F
                  - 2.0F
                     * x2
            );
         float f3 = 1.0F
            - x3
               * x3;
         float f4 = 0.02F
            + 1.55F
               * f2
               * f3;
         float f5 = 1.0F
            - x3
               * x3
               * x3;
         float f6 = (float)(
                  val4
                     - val3.spawnTime()
               )
               / 1.5F
            + val3.spinSeed();
         Vec3d pos2 = val3.pos()
            .subtract(
               val2
            );
         int color2 = ColorUtil.multAlpha(
            ColorUtil.getThemeColor(),
            f5
         );
         val.push();
         val.translate(
            pos2.x,
            pos2.y,
            pos2.z
         );
         val.multiply(
            RotationAxis.POSITIVE_Y
               .rotationDegrees(
                  val3.sideYaw()
               )
         );
         val.multiply(
            RotationAxis.POSITIVE_X.rotationDegrees(-210.0F)
         );
         val.multiply(
            RotationAxis.POSITIVE_Z
               .rotationDegrees(
                  f6
               )
         );
         this.drawTexturedQuad(
            val,
            -f4
               * 0.5F,
            -f4
               * 0.5F,
            f4,
            f4,
            color2
         );
         val.pop();
      }
   }

   private void drawTexturedQuad(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      int val6
   ) {
      int n = val6
            >> 16
         & 255;
      int n2 = val6
            >> 8
         & 255;
      int n3 = val6
         & 255;
      int n4 = val6
            >> 24
         & 255;
      if (n4
         > 0) {
         Matrix4f matrix = val.peek()
            .getPositionMatrix();
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(
               matrix,
               val2,
               val3,
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
               matrix,
               val2,
               val3
                  + val5,
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
               matrix,
               val2
                  + val4,
               val3
                  + val5,
               0.0F
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
                  + val4,
               val3,
               0.0F
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
   }

   private Vec3d getHitSideDirection(
      LivingEntity val,
      Vec3d val2
   ) {
      Vec3d vec = val2.subtract(
         val.getPos()
      );
      vec = new Vec3d(
         vec.x,
         0.0,
         vec.z
      );
      if (vec.lengthSquared()
         < 1.0E-4) {
         Vec3d vec2 = val.getRotationVector();
         vec = new Vec3d(
            vec2.x,
            0.0,
            vec2.z
         );
      }

      if (vec.lengthSquared()
         < 1.0E-4) {
         vec = new Vec3d(
            0.0, 0.0, 1.0
         );
      }

      return vec.normalize();
   }

   private Vec3d getHitPosition(
      LivingEntity val,
      Vec3d val2
   ) {
      Vec3d vec = new Vec3d(
         val.getX(),
         val.getY()
            + (double)val.getHeight()
            + 0.18,
         val.getZ()
      );
      return vec.add(
         val2.multiply(
            0.1
         )
      );
   }
}
