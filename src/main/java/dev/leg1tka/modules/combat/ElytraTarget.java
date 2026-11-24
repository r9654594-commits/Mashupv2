package dev.leg1tka.modules.combat;



import dev.leg1tka.modules.combat.Aura;
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
import dev.leg1tka.api.events.implement.EventRender;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class ElytraTarget extends Module {
   public static ElytraTarget INSTANCE = new ElytraTarget();
   private static final Identifier GLOW_TEXTURE = ClientIdentifier.of("textures/trajectories/glow.png");
   private static final float LINE_GLOW_THICKNESS = 0.055F;
   private static final float LINE_GLOW_LINE_U = 0.4F;
   private static final int TRAJECTORY_SAMPLES = 14;
   private final HasmaskHelper overlayRenderer = new HasmaskHelper();
   private Box smoothedPredictionBox;
   private LivingEntity smoothedTarget;
   private int cachedThemeColor1 = -1;
   private int cachedThemeColor2 = -1;
   public final BooleanSetting predictate = new BooleanSetting("Предикт", true);
   public final NumberSetting predictValue = new NumberSetting("Предикт значение", 3.0F, 1.0F, 5.0F, 0.1F)
      .visible(() -> this.predictate.isState());
   public final NumberSetting elytraHitDistance = new NumberSetting("Elytra Hit Distance", 4.0F, 2.0F, 8.0F, 0.1F)
      .visible(() -> this.predictate.isState());
   public final BooleanSetting smartElytraChase = new BooleanSetting("Smart Elytra Chase", true).visible(() -> this.predictate.isState());
   public final NumberSetting chaseForward = new NumberSetting("Chase Forward", 2.85F, 0.0F, 5.0F, 0.05F)
      .visible(() -> this.predictate.isState() && this.smartElytraChase.isState());
   public final NumberSetting chaseLateral = new NumberSetting("Chase Lateral", 2.05F, 0.0F, 5.0F, 0.05F)
      .visible(() -> this.predictate.isState() && this.smartElytraChase.isState());
   public final BooleanSetting showPredictPoint = new BooleanSetting("Показать предикт точку", true)
      .visible(() -> this.predictate.isState());
   public final BooleanSetting elytraTurnaround = new BooleanSetting("Разворот на элитрах", true).visible(() -> this.predictate.isState());
   public final BooleanSetting useResolver = new BooleanSetting("Резольвер (Elytra)", true).visible(() -> this.predictate.isState());
   public final ModeSetting boxMode;
   public final BooleanSetting trajectory;

   public ElytraTarget() {
      super("ElytraTarget", "Таргетит игрока на элитрах", Category.COMBAT);
      this.boxMode = new ModeSetting("Бокс", "Шейдер", new String[]{"Шейдер", "Обычный"});
      this.trajectory = new BooleanSetting("Траектория", true);
      this.addSettings(new Setting[]{
         this.predictate,
         this.predictValue,
         this.elytraHitDistance,
         this.smartElytraChase,
         this.chaseForward,
         this.chaseLateral,
         this.showPredictPoint,
         this.elytraTurnaround,
         this.useResolver,
         this.boxMode,
         this.trajectory
      });
   }

   @EventLink(
      priority = -100
   )
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.player == null || mc.world == null) {
         this.resetPredictionState();
      } else if (!mc.player.isGliding()) {
         this.resetPredictionState();
      } else {
         Aura val2 = ModuleManager.aura;
         if (val2
               != null
            && val2.isEnable()
            )
          {
            LivingEntity entity2 = val2.getTarget();
            if (entity2
                  != null
               && entity2.isAlive()
               && entity2.isGliding()
               )
             {
               Box box = this.buildPredictedBox(
                  entity2
               );
               Box box2 = this.smoothPredictionBox(
                  entity2,
                  box
               );
               Vec3d vec = val.getCamera()
                  .getPos();
               Box box3 = box2.offset(
                  -vec.x,
                  -vec.y,
                  -vec.z
               );
               Matrix4f matrix = val.getMatrices()
                  .peek()
                  .getPositionMatrix();
               this.updateCachedThemeColors();
               if (this.boxMode.is("Шейдер")) {
                  this.overlayRenderer
                     .writeMask(
                        matrix,
                        box3
                     );
               } else {
                  this.renderWireframeBox(
                     val.getMatrices(),
                     box3
                  );
               }

               if (this.trajectory.isState()) {
                  this.renderFlightTrajectory(
                     matrix,
                     vec,
                     box2,
                     entity2,
                     val.getTickDelta()
                  );
               }
            } else {
               this.resetPredictionState();
            }
         } else {
            this.resetPredictionState();
         }
      }
   }

   private void renderWireframeBox(
      MatrixStack val,
      Box val2
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(1.5F);
      int color2 = ColorUtil.setAlphaColor(
         -1,
         220
      );
      int n = color2
            >> 16
         & 255;
      int n2 = color2
            >> 8
         & 255;
      int n3 = color2
         & 255;
      int n4 = color2
            >> 24
         & 255;
      float x2 = (float)val2.minX;
      float y2 = (float)val2.minY;
      float f = (float)val2.minZ;
      float x3 = (float)val2.maxX;
      float x4 = (float)val2.maxY;
      float x5 = (float)val2.maxZ;
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      buffer.vertex(
            matrix,
            x2,
            y2,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            y2,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            y2,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            y2,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            y2,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            y2,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            y2,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            y2,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            x4,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            x4,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            x4,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            x4,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            x4,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            x4,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            x4,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            x4,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            y2,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            x4,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            y2,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            x4,
            f
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            y2,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x3,
            x4,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            y2,
            x5
         )
         .color(
            n,
            n2,
            n3,
            n4
         );
      buffer.vertex(
            matrix,
            x2,
            x4,
            x5
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
      RenderSystem.lineWidth(1.0F);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   @EventLink(
      priority = 200
   )
   public void onRender2D(
      EventRender.Default val8
   ) {
      if (this.overlayRenderer.hasMask()) {
         if (this.boxMode.is("Шейдер")) {
            int color2 = ColorUtil.interpolateColor(
               this.cachedThemeColor1, -1, 0.38F
            );
            int color3 = ColorUtil.interpolateColor(
               this.cachedThemeColor2, -1, 0.42F
            );
            this.overlayRenderer
               .renderOverlay(
                  Color1Helper.predict(
                     color2,
                     color3
                  )
               );
         }
      }
   }

   @Override
   public void onDisable() {
      this.resetPredictionState();
      this.overlayRenderer.destroy();
      super.onDisable();
   }

   private void resetPredictionState() {
      this.smoothedPredictionBox = null;
      this.smoothedTarget = null;
      this.overlayRenderer.clearMask();
   }

   private void renderFlightTrajectory(
      Matrix4f val,
      Vec3d val2,
      Box val3,
      LivingEntity val4,
      float val5
   ) {
      Vec3d vec = mc.player
         .getLerpedPos(
            val5
         )
         .add(0.0, (double)mc.player.getHeight() * 0.5, 0.0);
      Vec3d vec2 = val3.getCenter();
      if (!(
         vec.squaredDistanceTo(
               vec2
            )
            < 0.04
      )) {
         Vec3d[] vec3 = this.buildFlightPath(
            vec,
            vec2,
            val4
         );
         if (vec3.length
            >= 2) {
            int color2 = ColorUtil.setAlphaColor(
               this.cachedThemeColor1, 200
            );
            int color3 = ColorUtil.setAlphaColor(
               ColorUtil.interpolateColor(this.cachedThemeColor2, -1, 0.3F),
               235
            );
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 1);
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, GLOW_TEXTURE);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            BufferBuilder buffer = Tessellator.getInstance()
               .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            for (int i = 0;
               i
                  < vec3.length
                     - 1;
               i++
            ) {
               float f = (float)i
                  / (float)Math.max(
                     1,
                     vec3.length
                        - 2
                  );
               int color4 = ColorUtil.interpolateColor(
                  color2,
                  color3,
                  f
               );
               this.addGlowSegment(
                  buffer,
                  val,
                  val2,
                  vec3[i],
                  vec3[i
                     + 1],
                  color4,
                  0.055F
               );
            }

            BufferRenderer.drawWithGlobalProgram(
               buffer.end()
            );
            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
         }
      }
   }

   private Vec3d[] buildFlightPath(
      Vec3d val,
      Vec3d val2,
      LivingEntity val3
   ) {
      Vec3d vec = val2.subtract(
         val
      );
      double d = vec.length();
      if (d
         < 0.05) {
         Vec3d[] var10000 = new Vec3d[2];
         var10000[0] = val;
         var10000[1] = val2;
         return var10000;
      } else {
         Vec3d vec2 = mc.player
            .getVelocity();
         Vec3d velocity = val3.getVelocity();
         Vec3d vec3 = val.add(
               vec2.multiply(
                  Math.min(
                     d
                        * 0.42,
                     3.0
                  )
               )
            )
            .add(
               0.0,
               Math.min(
                  d
                     * 0.14,
                  4.0
               ),
               0.0
            );
         Vec3d vec4 = val2.subtract(
            velocity.multiply(
               Math.min(
                  d
                     * 0.32,
                  2.5
               )
            )
         );
         Vec3d[] vec5 = new Vec3d[14];

         for (int i = 0;
            i
               < 14;
            i++
         ) {
            float f = (float)i
               / 13.0F;
            vec5[i] = this.cubicBezier(
               val,
               vec3,
               vec4,
               val2,
               f
            );
         }

         return vec5;
      }
   }

   private Vec3d cubicBezier(
      Vec3d val,
      Vec3d val2,
      Vec3d val3,
      Vec3d val4,
      float val5
   ) {
      double d = 1.0
         - (double)val5;
      double d2 = d
         * d;
      double d3 = (double)(
         val5
            * val5
      );
      return val.multiply(
            d2
               * d
         )
         .add(
            val2.multiply(
               3.0
                  * d2
                  * (double)val5
            )
         )
         .add(
            val3.multiply(
               3.0
                  * d
                  * d3
            )
         )
         .add(
            val4.multiply(
               d3
                  * (double)val5
            )
         );
   }

   private void addGlowSegment(
      BufferBuilder val,
      Matrix4f val2,
      Vec3d val3,
      Vec3d val4,
      Vec3d val5,
      int val6,
      float val7
   ) {
      this.addGlowSegment(
         val,
         val2,
         val4.subtract(
            val3
         ),
         val5.subtract(
            val3
         ),
         val6,
         val7
      );
   }

   private void addGlowSegment(
      BufferBuilder val,
      Matrix4f val2,
      Vec3d val3,
      Vec3d val4,
      int val5,
      float val6
   ) {
      Vec3d vec = val4.subtract(
         val3
      );
      if (!(
         vec.lengthSquared()
            <= 1.0E-6
      )) {
         Vec3d vec2 = vec.normalize();
         double d = (double)val6
            * 0.22;
         val3 = val3.subtract(
            vec2.multiply(
               d
            )
         );
         val4 = val4.add(
            vec2.multiply(
               d
            )
         );
         vec = val4.subtract(
            val3
         );
         Vec3d vec3 = val3.add(
               val4
            )
            .multiply(0.5);
         Vec3d vec4 = vec3.negate();
         if (vec4.lengthSquared()
            <= 1.0E-6) {
            vec4 = new Vec3d(
               0.0, 1.0, 0.0
            );
         }

         Vec3d vec5 = vec.crossProduct(
            vec4
         );
         if (vec5.lengthSquared()
            <= 1.0E-6) {
            vec5 = vec.crossProduct(
               new Vec3d(0.0, 1.0, 0.0)
            );
            if (vec5.lengthSquared()
               <= 1.0E-6) {
               vec5 = vec.crossProduct(
                  new Vec3d(1.0, 0.0, 0.0)
               );
            }
         }

         vec5 = vec5.normalize()
            .multiply(
               (double)val6
                  * 0.48
            );
         Vec3d vec6 = val3.add(
            vec5
         );
         Vec3d vec7 = val3.subtract(
            vec5
         );
         Vec3d vec8 = val4.subtract(
            vec5
         );
         Vec3d vec9 = val4.add(
            vec5
         );
         float[] val7 = ColorUtil.rgba(
            val5
         );
         val.vertex(
               val2,
               (float)vec6.x,
               (float)vec6.y,
               (float)vec6.z
            )
            .texture(0.4F, 0.0F)
            .color(
               val7[0],
               val7[1],
               val7[2],
               val7[3]
            );
         val.vertex(
               val2,
               (float)vec7.x,
               (float)vec7.y,
               (float)vec7.z
            )
            .texture(0.4F, 1.0F)
            .color(
               val7[0],
               val7[1],
               val7[2],
               val7[3]
            );
         val.vertex(
               val2,
               (float)vec8.x,
               (float)vec8.y,
               (float)vec8.z
            )
            .texture(0.4F, 1.0F)
            .color(
               val7[0],
               val7[1],
               val7[2],
               val7[3]
            );
         val.vertex(
               val2,
               (float)vec9.x,
               (float)vec9.y,
               (float)vec9.z
            )
            .texture(0.4F, 0.0F)
            .color(
               val7[0],
               val7[1],
               val7[2],
               val7[3]
            );
      }
   }

   private Box smoothPredictionBox(
      LivingEntity val,
      Box val2
   ) {
      if (this.smoothedPredictionBox != null
         && this.smoothedTarget
            == val
         && !(
            this.smoothedPredictionBox
                  .getCenter()
                  .squaredDistanceTo(
                     val2.getCenter()
                  )
               > 144.0
         )) {
         double d = Math.sqrt(
            this.smoothedPredictionBox
               .getCenter()
               .squaredDistanceTo(
                  val2.getCenter()
               )
         );
         double d2 = MathHelper.clamp(
            0.08
               + d
                  * 0.035,
            0.08,
            0.18
         );
         this.smoothedPredictionBox = this.lerpBox(
            this.smoothedPredictionBox,
            val2,
            d2
         );
         return this.smoothedPredictionBox;
      } else {
         this.smoothedPredictionBox = val2;
         this.smoothedTarget = val;
         return val2;
      }
   }

   private Box buildPredictedBox(
      LivingEntity val
   ) {
      Box box = val.getBoundingBox();
      if (!this.predictate.isState()) {
         return box;
      } else {
         float f = 1.0F;
         if (mc.getRenderTickCounter() != null) {
            f = mc.getRenderTickCounter()
               .getTickDelta(true);
         }

         Vec3d pos = GetsmartpredictedpositionHelper.getElytraAimPointRender(
            val,
            f
         );
         Vec3d vec = pos.subtract(
            box.getCenter()
         );
         return box.offset(
            vec
         );
      }
   }

   private Box lerpBox(
      Box val,
      Box val2,
      double val3
   ) {
      return new Box(
         MathHelper.lerp(
            val3,
            val.minX,
            val2.minX
         ),
         MathHelper.lerp(
            val3,
            val.minY,
            val2.minY
         ),
         MathHelper.lerp(
            val3,
            val.minZ,
            val2.minZ
         ),
         MathHelper.lerp(
            val3,
            val.maxX,
            val2.maxX
         ),
         MathHelper.lerp(
            val3,
            val.maxY,
            val2.maxY
         ),
         MathHelper.lerp(
            val3,
            val.maxZ,
            val2.maxZ
         )
      );
   }

   private void updateCachedThemeColors() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null) {
         ThemeColors val = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         if (val
            == null) {
            this.cachedThemeColor1 = ColorUtil.getThemeColor(0);
            this.cachedThemeColor2 = ColorUtil.getThemeColor(180);
         } else {
            if (!"Rainbow"
               .equals(
                  val.getName()
               )) {
               int color2 = val.color
                        != null
                     && val.color.length
                        > 0
                  ? val.color[0]
                  : ColorUtil.getThemeColor(0);
               this.cachedThemeColor1 = color2;
               this.cachedThemeColor2 = color2;
            } else {
               this.cachedThemeColor1 = ColorUtil.getThemeColor();
               this.cachedThemeColor2 = ColorUtil.getThemeColor(180);
            }
         }
      } else {
         this.cachedThemeColor1 = ColorUtil.getThemeColor(0);
         this.cachedThemeColor2 = ColorUtil.getThemeColor(180);
      }
   }
}
