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
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class KillEffect extends Module {
   public static KillEffect INSTANCE = new KillEffect();
   private static final Identifier GLOW_TEX = ClientIdentifier.of("textures/particle/bloom.png");
   private static final float DURATION = 1.5F;
   private static final float HEIGHT = 4.0F;
   private static final float MAX_RADIUS = 1.0F;
   private static final int SLICES = 40;
   private final Map<Entity, Vec3d> trackedEntities = new IdentityHashMap<>();
   private final List<PositionHolder_1> effects = new ArrayList<>();

   public KillEffect() {
      super(
         "KillEffect",
         "Эффект при исчезновении цели",
         Category.RENDER
      );
   }

   @Override
   public void onDisable() {
      this.trackedEntities.clear();
      this.effects.clear();
      super.onDisable();
   }

   @EventLink
   public void onAttack(
      EventAttackEntity val
   ) {
      if (mc.player != null && mc.world != null) {
         Entity entity2 = val.getTarget();
         if (entity2 instanceof LivingEntity
            && entity2
               != mc.player) {
            this.trackedEntities
               .put(
                  entity2,
                  entity2.getPos()
               );
         }
      }
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.world != null && mc.player != null) {
         long val2 = System.currentTimeMillis();
         Iterator<Entry<Entity, Vec3d>> vec = this.trackedEntities
            .entrySet()
            .iterator();

         while (
            vec.hasNext()
         ) {
            Entry<Entity, Vec3d> vec2 = vec.next();
            Entity entity2 = vec2.getKey();
            if (!entity2.isRemoved()
               && entity2.isAlive()
               )
             {
               vec2.setValue(
                  entity2.getPos()
               );
            } else {
               this.effects
                  .add(
                     new PositionHolder_1(
                        vec2.getValue(),
                        val2
                     )
                  );
               vec.remove();
            }
         }

         RenderSystem.enableBlend();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, GLOW_TEX);
         Iterator<PositionHolder_1> val3 = this.effects
            .iterator();

         while (
            val3.hasNext()
         ) {
            PositionHolder_1 val4 = val3.next();
            float f = (float)(
                  val2
                     - val4.startTime
               )
               / 1500.0F;
            if (f
               >= 1.0F) {
               val3.remove();
            } else {
               this.renderEffect(
                  val.getMatrices(),
                  val4,
                  mc.gameRenderer.getCamera().getPos(),
                  f
               );
            }
         }

         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private void renderEffect(
      MatrixStack val,
      PositionHolder_1 val2,
      Vec3d val3,
      float val4
   ) {
      int color2 = ColorUtil.getThemeColor();
      float f = (float)(
            color2
                  >> 16
               & 255
         )
         / 255.0F;
      float f2 = (float)(
            color2
                  >> 8
               & 255
         )
         / 255.0F;
      float f3 = (float)(
            color2
               & 255
         )
         / 255.0F;
      float f4 = val4
            < 0.15F
         ? val4
            / 0.15F
         : (
            val4
                  > 0.75F
               ? (
                     1.0F
                        - val4
                  )
                  / 0.25F
               : 1.0F
         );
      float f5 = 0.1F;

      for (int i = 0;
         i
            < 40;
         i++
      ) {
         float f6 = (float)i
            / 40.0F;
         float f7 = f6
            * 4.0F;
         float f8 = 1.0F
            * MathHelper.sin(
               (float)(
                  Math.PI
                     * (double)f6
               )
            );
         float f9 = (
               1.0F
                  - Math.abs(
                        2.0F
                              * f6
                           - 1.0F
                     )
                     * 0.25F
            )
            * f4;
         Vec3d pos = val2.position
            .add(
               0.0,
               (double)f7,
               0.0
            );
         this.renderGlow(
            val,
            val3,
            pos,
            f8
               * 2.1F,
            f,
            f2,
            f3,
            f9
               * 0.22F
         );
         this.renderGlow(
            val,
            val3,
            pos,
            f8
               * 1.15F,
            f,
            f2,
            f3,
            f9
               * 0.48F
         );
         this.renderGlow(
            val,
            val3,
            pos,
            f8
               * 0.55F,
            f,
            f2,
            f3,
            f9
               * 0.85F
         );
      }

      for (int i2 = 0;
         i2
            < 10;
         i2++
      ) {
         float f10 = (float)i2
            / 10.0F;
         float f11 = 1.0F
            - f10;
         float f12 = 3.6F
            * f11;
         float f13 = f11
            * f11
            * f4
            * 0.38F;
         Vec3d pos2 = val2.position
            .add(
               0.0,
               (double)(
                  f10
                     * 0.45F
               ),
               0.0
            );
         this.renderGlow(
            val,
            val3,
            pos2,
            f12,
            f,
            f2,
            f3,
            f13
         );
         this.renderGlow(
            val,
            val3,
            pos2,
            f12
               * 0.35F,
            f,
            f2,
            f3,
            f13
               * 1.7F
         );
      }
   }

   private void renderGlow(
      MatrixStack val,
      Vec3d val2,
      Vec3d val3,
      float val4,
      float val5,
      float val6,
      float val7,
      float val8
   ) {
      if (!(
         val8
            <= 0.01F
      )) {
         val.push();
         val.translate(
            val3.x
               - val2.x,
            val3.y
               - val2.y,
            val3.z
               - val2.z
         );
         val.multiply(
            RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw())
         );
         val.multiply(
            RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch())
         );
         Matrix4f matrix = val.peek()
            .getPositionMatrix();
         float f = val4
            * 0.5F;
         int n = Math.min(
            255,
            (int)(
               val5
                  * 255.0F
            )
         );
         int n2 = Math.min(
            255,
            (int)(
               val6
                  * 255.0F
            )
         );
         int n3 = Math.min(
            255,
            (int)(
               val7
                  * 255.0F
            )
         );
         int n4 = Math.min(
            255,
            (int)(
               val8
                  * 255.0F
            )
         );
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
               n,
               n2,
               n3,
               n4
            );
         buffer.vertex(
               matrix,
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
               matrix,
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
               matrix,
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
         val.pop();
      }
   }
}
