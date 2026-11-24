package dev.leg1tka.modules.render;



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
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventRender;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TargetESP extends Module {
   public static TargetESP INSTANCE = new TargetESP();
   private static final float GHOST_ALPHA_MULT = 0.6F;
   private static final float CELKA_SPEED_MULT = 1.2F;
   private static final float SCALE_FACTOR = 0.007F;
   static final long CUBE_ATTACH_LIFE_MS = 560L;
   static final long CUBE_FADE_LIFE_MS = 320L;
   static final int MAX_CUBE_PARTICLES = 72;
   public static final byte[][] CUBE_EDGES = new byte[][]{
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
   private final ModeSetting mode;
   private final NumberSetting size;
   private final NumberSetting ringRadius;
   private final NumberSetting ringSpeed;
   private final NumberSetting rotateSpeed;
   private final BooleanSetting hurtColor;
   private final NumberSetting bmwGhostCount;
   private final NumberSetting bmwGhostLife;
   private final NumberSetting bmwStrengthXZ;
   private final NumberSetting bmwStrengthY;
   private float appearValue;
   private float scaleValue;
   private float rotProgress;
   private float rotFrom;
   private float rotTo;
   private long lastRotateUpdate;
   private LivingEntity lastTarget;
   private LivingEntity lastHandledTarget;
   private Vec3d lastTargetPos;
   private float lastTargetHeight;
   private float lastTargetWidth;
   private final CopyOnWriteArrayList<ShouldremoveHelper> bmwPoints;
   private float crystalRotationAngle;
   private float crystalAnimation;
   private float spawnAccumulator;
   private long lastCubeTime;
   private final ArrayList<UpdateHelper_4> cubeParticles;
   private final ArrayList<UpdateHelper_4> renderCubeParticles;
   private static final float SPAWN_INTERVAL = 0.022F;
   private static final int PARTICLES_PER_SPAWN = 1;
   private double ghostAge;
   private final Map<LivingEntity, List<Vec3d>> phantomTrail1;
   private final Map<LivingEntity, List<Vec3d>> phantomTrail2;
   private final Map<LivingEntity, List<Vec3d>> phantomTrail3;
   private int ghost3LastHurtTime;
   private long ghost3LastHurtTimeMs;
   private long nursultanTimestamp;

   public TargetESP() {
      super("TargetESP", Category.RENDER);
      String var1 = "TargetESP";
      String var3 = "Режим";
      String var13 = "Ромб";
      String[] var10005 = new String[7];
      var10005[0] = "Ромб";
      var10005[1] = "Кубы";
      int var10007 = 2;
      var10005[var10007] = "Кольцо";
      var10007 = 3;
      var10005[var10007] = "Души";
      var10007 = 4;
      var10005[var10007] = "Кристаллы";
      var10007 = 5;
      var10005[var10007] = "Целестиал";
      var10007 = 6;
      var10005[var10007] = "Призраки";
      this.mode = new ModeSetting(var3, var13, var10005);
      this.size = new NumberSetting(
         "Размер", 1.15F, 0.6F, 2.5F, 0.05F
      );
      this.ringRadius = new NumberSetting("Радиус кольца", 0.5F, 0.3F, 1.5F, 0.05F);
      this.ringSpeed = new NumberSetting(
         "Скорость кольца", 1.0F, 0.3F, 3.0F, 0.1F
      );
      this.rotateSpeed = new NumberSetting(
         "Скорость вращения", 1.2F, 0.2F, 4.0F, 0.05F
      );
      this.hurtColor = new BooleanSetting("Окрашивание при ударе", true);
      this.bmwGhostCount = new NumberSetting(
         "Кол-во призраков", 3.0F, 2.0F, 5.0F, 1.0F
      );
      this.bmwGhostLife = new NumberSetting(
         "Время жизни (мс)", 350.0F, 150.0F, 500.0F, 25.0F
      );
      this.bmwStrengthXZ = new NumberSetting(
         "Цикл XZ",
         2000.0F,
         1000.0F,
         5000.0F,
         100.0F
      );
      this.bmwStrengthY = new NumberSetting(
         "Цикл Y",
         1700.0F,
         1000.0F,
         5000.0F,
         100.0F
      );
      this.appearValue = 0.0F;
      this.scaleValue = 0.0F;
      this.rotProgress = 0.0F;
      this.rotFrom = -280.0F;
      this.rotTo = 280.0F;
      this.lastRotateUpdate = System.currentTimeMillis();
      this.lastTarget = null;
      this.lastHandledTarget = null;
      this.lastTargetPos = null;
      this.lastTargetHeight = 1.8F;
      this.lastTargetWidth = 0.6F;
      this.bmwPoints = new CopyOnWriteArrayList<>();
      this.crystalRotationAngle = 0.0F;
      this.crystalAnimation = 0.0F;
      this.spawnAccumulator = 0.0F;
      this.lastCubeTime = 0L;
      this.cubeParticles = new ArrayList<>();
      this.renderCubeParticles = new ArrayList<>();
      this.ghostAge = 0.0;
      this.phantomTrail1 = new WeakHashMap<>();
      this.phantomTrail2 = new WeakHashMap<>();
      this.phantomTrail3 = new WeakHashMap<>();
      this.ghost3LastHurtTime = 0;
      this.ghost3LastHurtTimeMs = 0L;
      this.nursultanTimestamp = System.currentTimeMillis();
      this.size.visible(this::isImageMode);
      this.rotateSpeed.visible(this::isImageMode);
      this.bmwGhostCount
         .visible(
            () -> this.mode.is("Райдер")
         );
      this.bmwGhostLife
         .visible(
            () -> this.mode.is("Райдер")
         );
      this.bmwStrengthXZ
         .visible(
            () -> this.mode.is("Райдер")
         );
      this.bmwStrengthY
         .visible(
            () -> this.mode.is("Райдер")
         );
      this.ringRadius
         .visible(
            () -> this.mode.is("Кольцо")
         );
      this.ringSpeed
         .visible(
            () -> this.mode.is("Кольцо")
         );
      this.addSettings(new Setting[]{
         this.mode,
         this.size,
         this.rotateSpeed,
         this.hurtColor,
         this.ringRadius,
         this.ringSpeed,
         this.bmwGhostCount,
         this.bmwGhostLife,
         this.bmwStrengthXZ,
         this.bmwStrengthY
      });
   }

   @Override
   public void onDisable() {
      this.appearValue = 0.0F;
      this.scaleValue = 0.0F;
      this.lastTarget = null;
      this.lastHandledTarget = null;
      this.lastTargetPos = null;
      this.rotProgress = 0.0F;
      this.rotFrom = -280.0F;
      this.rotTo = 280.0F;
      this.bmwPoints.clear();
      this.crystalRotationAngle = 0.0F;
      this.crystalAnimation = 0.0F;
      this.spawnAccumulator = 0.0F;
      this.lastCubeTime = 0L;
      this.cubeParticles.clear();
      this.renderCubeParticles.clear();
      this.ghostAge = 0.0;
      this.phantomTrail1.clear();
      this.phantomTrail2.clear();
      this.phantomTrail3.clear();
      this.ghost3LastHurtTime = 0;
      this.ghost3LastHurtTimeMs = 0L;
      super.onDisable();
   }

   private boolean isImageMode() {
      ModeSetting var10000 = this.mode;
      if (!var10000.is("Картинка 1")) {
         var10000 = this.mode;
         if (!var10000.is("Ромб")) {
            return false;
         }
      }

      return true;
   }

   private Identifier getCaptureTexture() {
      ModeSetting var10000 = this.mode;
      return var10000.is("Ромб")
         ? ClientIdentifier.of("textures/targetesp/targetesp_3.png")
         : ClientIdentifier.of("textures/targetesp/targetesp_2.png");
   }

   private Identifier getBloomTexture() {
      return ClientIdentifier.of("textures/targetesp/bloom.png");
   }

   private int getESPColor() {
      int color2 = ColorUtil.getThemeColor();
      if ((
            color2
                  >> 24
               & 255
         )
         == 0) {
         color2 |= -16777216;
      }

      return color2;
   }

   private float animateTo(
      float val,
      float val2,
      float val3
   ) {
      if (val
         < val2
         )
       {
         val = Math.min(
            val
               + val3,
            val2
         );
      } else if (val
         > val2
         )
       {
         val = Math.max(
            val
               - val3,
            val2
         );
      }

      return val;
   }

   private float getDistanceScale(
      Vec3d val,
      double val2,
      double val3,
      double val4
   ) {
      double d = val2
         - val.x;
      double d2 = val3
         - val.y;
      double d3 = val4
         - val.z;
      double d4 = Math.sqrt(
         d
               * d
            + d2
               * d2
            + d3
               * d3
      );
      return (float)Math.max(
         0.1,
         d4
            * 0.007F
      );
   }

   @EventLink(
      priority = -100
   )
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc != null && mc.player != null && mc.world != null) {
         Aura val2 = ModuleManager.aura;
         boolean flag = val2
               != null
            && val2.isEnable();
         LivingEntity entity2 = flag
            ? val2.getTarget()
            : null;
         boolean flag2 = entity2
               != null
            && entity2.isAlive();
         float f = 0.05F;
         this.appearValue = this.animateTo(
            this.appearValue,
            flag2
               ? 1.0F
               : 0.0F,
            f
         );
         this.scaleValue = this.animateTo(
            this.scaleValue,
            flag2
               ? 1.0F
               : 0.5F,
            f
         );
         if (flag2
            )
          {
            this.lastTarget = entity2;
            this.lastHandledTarget = entity2;
         }

         ModeSetting var10000 = this.mode;
         if (var10000.is("Кристаллы")) {
            float f2 = flag2
               ? 0.07F
               : 0.045F;
            this.crystalAnimation = this.animateTo(
               this.crystalAnimation,
               flag2
                  ? 1.0F
                  : 0.0F,
               f2
            );
            if (flag2
               )
             {
               this.crystalRotationAngle += 0.8F;
            }
         }

         if (this.appearValue <= 0.001F
            && !flag2
            )
          {
            var10000 = this.mode;
            if (!var10000.is("Кристаллы")
               || this.crystalAnimation <= 0.001F) {
               this.lastTarget = null;
               this.lastTargetPos = null;
               return;
            }
         }

         if (flag2
            && entity2
               != null) {
            float f3 = val.getTickDelta();
            this.lastTargetPos = new Vec3d(
               MathHelper.lerp(
                  (double)f3,
                  entity2.lastRenderX,
                  entity2.getX()
               ),
               MathHelper.lerp(
                  (double)f3,
                  entity2.lastRenderY,
                  entity2.getY()
               ),
               MathHelper.lerp(
                  (double)f3,
                  entity2.lastRenderZ,
                  entity2.getZ()
               )
            );
            this.lastTargetHeight = entity2.getHeight();
            this.lastTargetWidth = entity2.getWidth();
         }

         if (this.lastTargetPos != null) {
            var10000 = this.mode;
            if (var10000.is("Райдер")) {
               if (flag2
                  && entity2
                     != null) {
                  this.addBMWGhosts(
                     entity2,
                     val.getTickDelta(),
                     Math.max(1, Math.round(this.bmwGhostCount.getValue().floatValue())),
                     Math.max(1, Math.round(this.bmwGhostLife.getValue().floatValue())),
                     this.getESPColor()
                  );
               }

               this.bmwPoints.removeIf(ShouldremoveHelper::shouldRemove);
               this.drawBMW3D(
                  val
               );
            } else {
               var10000 = this.mode;
               if (var10000.is("Кристаллы")) {
                  LivingEntity entity3 = flag2
                     ? entity2
                     : this.lastTarget;
                  if ((
                        entity3
                              != null
                           || this.lastTargetPos != null
                     )
                     && this.crystalAnimation > 0.01F) {
                     this.renderCrystals3D(
                        val.getMatrices(),
                        entity3,
                        val.getTickDelta()
                     );
                  }
               } else {
                  if (this.isImageMode()) {
                     this.renderMarker3D(
                        val
                     );
                  }

                  var10000 = this.mode;
                  if (var10000.is("Души")) {
                     this.drawSouls3D(
                        val
                     );
                  }

                  var10000 = this.mode;
                  if (var10000.is("Кольцо")) {
                     this.drawRing3D(
                        val
                     );
                  }

                  var10000 = this.mode;
                  if (var10000.is("Кубы")) {
                     this.renderCubes(
                        val,
                        entity2,
                        flag2
                     );
                  }

                  var10000 = this.mode;
                  if (var10000.is("Целестиал")) {
                     this.ghostAge++;
                     this.drawGhosts2_3D(
                        val
                     );
                  }

                  var10000 = this.mode;
                  if (var10000.is("Призраки")) {
                     this.drawNursultanPrizrak_3D(
                        val
                     );
                  }
               }
            }
         }
      }
   }

   private void renderCubes(
      Event3DRender val,
      LivingEntity val2,
      boolean val3
   ) {
      long val4 = System.currentTimeMillis();
      if (this.lastCubeTime == 0L) {
         this.lastCubeTime = val4;
      }

      float f = Math.min(
         (float)(
               val4
                  - this.lastCubeTime
            )
            / 1000.0F,
         0.1F
      );
      this.lastCubeTime = val4;
      if (Float.isFinite(
            f
         )
         && mc.gameRenderer != null
         && mc.gameRenderer.getCamera() != null) {
         if (val3
            && val2
               != null) {
            this.lastTarget = val2;
            this.spawnAccumulator += f;

            while (this.spawnAccumulator >= 0.022F) {
               this.spawnAccumulator -= 0.022F;
               if (this.cubeParticles.size() >= 72) {
                  break;
               }

               for (int i = 0;
                  i
                     < 1;
                  i++
               ) {
                  double d = Math.random()
                     * 360.0;
                  double d2 = Math.cos(
                        Math.toRadians(
                           d
                        )
                     )
                     * 0.7;
                  double d3 = 0.02
                     + Math.random() * 0.1;
                  double d4 = Math.sin(
                        Math.toRadians(
                           d
                        )
                     )
                     * 0.7;
                  this.cubeParticles
                     .add(
                        new UpdateHelper_4(
                           val2,
                           d2,
                           d3,
                           d4
                        )
                     );
               }
            }
         } else {
            this.spawnAccumulator = 0.0F;
         }

         this.renderCubeParticles.clear();

         for (int i2 = this.cubeParticles
                  .size()
               - 1;
            i2
               >= 0;
            i2--
         ) {
            UpdateHelper_4 val5 = this.cubeParticles
               .get(
                  i2
               );

            try {
               val5.update(
                  f,
                  val4,
                  val3
                     ? val2
                     : null
               );
               if (val5.shouldRemove(
                  val4
               )) {
                  this.cubeParticles
                     .remove(
                        i2
                     );
               } else {
                  this.renderCubeParticles
                     .add(
                        val5
                     );
               }
            } catch (Throwable var29) {
               this.cubeParticles
                  .remove(
                     i2
                  );
            }
         }

         if (!this.renderCubeParticles.isEmpty()) {
            float f2 = val.getTickDelta();
            MatrixStack matrices = val.getMatrices();
            Vec3d vec = mc.gameRenderer
               .getCamera()
               .getPos();
            LivingEntity entity2 = val3
               ? val2
               : this.lastTarget;
            float f3 = this.getHurtPC(
               entity2
            );
            int color2 = this.getESPColor();
            int color3 = ColorUtil.rgb(
               255,
               3,
               3
            );
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.blendFunc(770, 1);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            BufferBuilder buffer = Tessellator.getInstance()
               .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            boolean flag = false;
            int n = 0;

            for (int i3 = this.renderCubeParticles
                  .size();
               n
                  < i3;
               n++
            ) {
               UpdateHelper_4 val6 = this.renderCubeParticles
                  .get(
                     n
                  );

               try {
                  int color4 = val6.getRenderColor(
                     color2,
                     color3,
                     f3,
                     val4
                  );
                  if ((
                           color4
                                 >> 24
                              & 255
                        )
                        > 0
                     && val6.appendCubeFaces(
                        buffer,
                        matrices,
                        vec,
                        f2,
                        color4
                     )) {
                     flag = true;
                  }
               } catch (Throwable var28) {
               }
            }

            if (flag
               )
             {
               BufferRenderer.drawWithGlobalProgram(
                  buffer.end()
               );
            }

            BufferBuilder buffer2 = Tessellator.getInstance()
               .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            boolean flag2 = false;
            int n2 = 0;

            for (int i4 = this.renderCubeParticles
                  .size();
               n2
                  < i4;
               n2++
            ) {
               UpdateHelper_4 val7 = this.renderCubeParticles
                  .get(
                     n2
                  );

               try {
                  int color5 = val7.getRenderColor(
                     color2,
                     color3,
                     f3,
                     val4
                  );
                  if ((
                           color5
                                 >> 24
                              & 255
                        )
                        > 0
                     && val7.appendCubeLines(
                        buffer2,
                        matrices,
                        vec,
                        f2,
                        color5
                     )) {
                     flag2 = true;
                  }
               } catch (Throwable var27) {
               }
            }

            if (flag2
               )
             {
               BufferRenderer.drawWithGlobalProgram(
                  buffer2.end()
               );
            }

            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            RenderSystem.setShaderTexture(0, this.getBloomTexture());
            BufferBuilder buffer3 = Tessellator.getInstance()
               .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            boolean flag3 = false;
            float f4 = mc.gameRenderer
               .getCamera()
               .getYaw();
            float f5 = mc.gameRenderer
               .getCamera()
               .getPitch();
            int n3 = 0;

            for (int i5 = this.renderCubeParticles
                  .size();
               n3
                  < i5;
               n3++
            ) {
               UpdateHelper_4 val8 = this.renderCubeParticles
                  .get(
                     n3
                  );

               try {
                  int color6 = val8.getRenderColor(
                     color2,
                     color3,
                     f3,
                     val4
                  );
                  if (val8.appendBloom(
                     buffer3,
                     matrices,
                     vec,
                     f4,
                     f5,
                     f2,
                     color6,
                     val4
                  )) {
                     flag3 = true;
                  }
               } catch (Throwable var26) {
               }
            }

            if (flag3
               )
             {
               BufferRenderer.drawWithGlobalProgram(
                  buffer3.end()
               );
            }

            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
         }
      }
   }

   private void drawRing3D(
      Event3DRender val
   ) {
      if (!(this.appearValue <= 0.001F) && this.lastTargetPos != null) {
         float f = mc.getRenderTickCounter()
            .getTickDelta(true);
         LivingEntity entity2 = this.lastTarget;
         Vec3d vec;
         float f2;
         if (entity2
               != null
            && entity2.isAlive()
            )
          {
            vec = new Vec3d(
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderX,
                  entity2.getX()
               ),
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderY,
                  entity2.getY()
               ),
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderZ,
                  entity2.getZ()
               )
            );
            f2 = entity2.getHeight();
         } else {
            vec = this.lastTargetPos;
            f2 = this.lastTargetHeight;
         }

         Vec3d vec2 = mc.gameRenderer
            .getCamera()
            .getPos();
         double d = vec.x
            - vec2.x;
         double d2 = vec.y
            - vec2.y;
         double d3 = vec.z
            - vec2.z;
         double d4 = 2000.0
            / (double)this.ringSpeed.get();
         double d5 = (double)(
            System.currentTimeMillis()
               % (long)d4
         );
         boolean flag = d5
            > d4
               / 2.0;
         double d6 = d5
            / (
               d4
                  / 2.0
            );
         if (flag
            )
          {
            d6--;
         } else {
            d6 = 1.0
               - d6;
         }

         d6 = d6
               < 0.5
            ? 2.0
               * d6
               * d6
            : 1.0
               - Math.pow(
                     -2.0
                           * d6
                        + 2.0,
                     2.0
                  )
                  / 2.0;
         double d7 = (double)f2
            / 1.2
            * (
               d6
                     > 0.5
                  ? 1.0
                     - d6
                  : d6
            )
            * (double)(
               flag
                  ? -1
                  : 1
            );
         int color2 = this.getESPColor();
         float f3 = this.getHurtPC(
            entity2
         );
         int color3 = ColorUtil.rgb(
            255,
            3,
            3
         );
         int n = this.overCol(
            color2,
            color3,
            f3
         );
         int n2 = this.setAlpha(
            n,
            0.88235295F * this.appearValue
         );
         int n3 = this.setAlpha(
            n,
            0.003921569F * this.appearValue
         );
         int n4 = this.setAlpha(
            n,
            this.appearValue
         );
         double d8 = (double)this.ringRadius
            .get();
         MatrixStack matrices = val.getMatrices();
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         RenderSystem.depthMask(false);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

         for (int i = 0;
            i
               <= 360;
            i++
         ) {
            double d9 = Math.toRadians(
               (double)i
            );
            float f4 = (float)(
               d
                  + Math.cos(
                        d9
                     )
                     * d8
            );
            float f5 = (float)(
               d3
                  + Math.sin(
                        d9
                     )
                     * d8
            );
            float f6 = (float)(
               d2
                  + (double)f2
                     * d6
            );
            float f7 = (float)(
               d2
                  + (double)f2
                     * d6
                  + d7
            );
            buffer.vertex(
                  matrix,
                  f4,
                  f6,
                  f5
               )
               .color(
                  n2
               );
            buffer.vertex(
                  matrix,
                  f4,
                  f7,
                  f5
               )
               .color(
                  n3
               );
         }

         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         RenderSystem.lineWidth(1.5F);
         BufferBuilder buffer2 = Tessellator.getInstance()
            .begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

         for (int i2 = 0;
            i2
               <= 360;
            i2++
         ) {
            double d10 = Math.toRadians(
               (double)i2
            );
            float f8 = (float)(
               d
                  + Math.cos(
                        d10
                     )
                     * d8
            );
            float f9 = (float)(
               d3
                  + Math.sin(
                        d10
                     )
                     * d8
            );
            float f10 = (float)(
               d2
                  + (double)f2
                     * d6
            );
            buffer2.vertex(
                  matrix,
                  f8,
                  f10,
                  f9
               )
               .color(
                  n4
               );
         }

         BufferRenderer.drawWithGlobalProgram(
            buffer2.end()
         );
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
      }
   }

   private int setAlpha(
      int val,
      float val2
   ) {
      val2 = Math.max(
         0.0F,
         Math.min(
            1.0F,
            val2
         )
      );
      return val
            & 16777215
         | (int)(
               val2
                  * 255.0F
            )
            << 24;
   }

   private void drawTriangle3D(
      Event3DRender val
   ) {
      if (!(this.appearValue <= 0.001F) && this.lastTargetPos != null) {
         float f = mc.getRenderTickCounter()
            .getTickDelta(true);
         LivingEntity entity2 = this.lastTarget;
         Vec3d vec;
         float f2;
         if (entity2
               != null
            && entity2.isAlive()
            )
          {
            vec = new Vec3d(
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderX,
                  entity2.getX()
               ),
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderY,
                  entity2.getY()
               ),
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderZ,
                  entity2.getZ()
               )
            );
            f2 = entity2.getHeight();
         } else {
            vec = this.lastTargetPos;
            f2 = this.lastTargetHeight;
         }

         Vec3d vec2 = mc.gameRenderer
            .getCamera()
            .getPos();
         double d = vec.x
            - vec2.x;
         double d2 = vec.y
            - vec2.y
            + (double)f2
            + 0.8;
         double d3 = vec.z
            - vec2.z;
         int color2 = this.getESPColor();
         float f3 = this.getHurtPC(
            entity2
         );
         int color3 = ColorUtil.rgb(
            255,
            3,
            3
         );
         int n = this.overCol(
            color2,
            color3,
            f3
         );
         float f4 = 0.55F;
         float f5 = 0.75F;
         MatrixStack matrices = val.getMatrices();
         matrices.push();
         matrices.translate(
            d,
            d2,
            d3
         );
         matrices.multiply(
            RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw())
         );
         matrices.multiply(
            RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch())
         );
         Matrix4f matrix = matrices.peek()
            .getPositionMatrix();
         RenderSystem.depthMask(false);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         int n2 = this.setAlpha(
            n,
            0.95F * this.appearValue
         );
         int n3 = this.setAlpha(
            n,
            0.7F * this.appearValue
         );
         int n4 = this.setAlpha(
            n,
            0.3F * this.appearValue
         );

         for (int i = 0;
            i
               < 5;
            i++
         ) {
            float f6 = (float)i
               * 0.015F;
            float f7 = 1.0F
               - (float)i
                  * 0.15F;
            int n5 = this.setAlpha(
               n,
               0.95F
                  * this.appearValue
                  * f7
            );
            int n6 = this.setAlpha(
               n,
               0.7F
                  * this.appearValue
                  * f7
            );
            int n7 = this.setAlpha(
               n,
               0.3F
                  * this.appearValue
                  * f7
            );
            BufferBuilder buffer = Tessellator.getInstance()
               .begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
            buffer.vertex(
                  matrix,
                  -f4,
                  f6,
                  f6
               )
               .color(
                  n5
               );
            buffer.vertex(
                  matrix,
                  f4,
                  f6,
                  f6
               )
               .color(
                  n5
               );
            buffer.vertex(
                  matrix,
                  0.0F,
                  -f5,
                  f6
               )
               .color(
                  n7
               );
            buffer.vertex(
                  matrix,
                  -f4,
                  f6,
                  -f6
               )
               .color(
                  n5
               );
            buffer.vertex(
                  matrix,
                  0.0F,
                  -f5,
                  -f6
               )
               .color(
                  n7
               );
            buffer.vertex(
                  matrix,
                  f4,
                  f6,
                  -f6
               )
               .color(
                  n5
               );
            BufferRenderer.drawWithGlobalProgram(
               buffer.end()
            );
         }

         int n8 = 6;

         for (int i2 = 0;
            i2
               < n8;
            i2++
         ) {
            float f8 = (float)i2
               / (float)(
                  n8
                     - 1
               );
            float f9 = f8
               * f5;
            float f10 = f4
               * (
                  1.0F
                     - f8
               );
            float f11 = 0.6F
               * (
                  1.0F
                     - f8
                        * 0.5F
               );
            int n9 = this.setAlpha(
               n,
               f11
                  * this.appearValue
            );
            RenderSystem.lineWidth(2.5F);
            BufferBuilder buffer2 = Tessellator.getInstance()
               .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            buffer2.vertex(
                  matrix,
                  -f10,
                  -f9,
                  0.0F
               )
               .color(
                  n9
               );
            buffer2.vertex(
                  matrix,
                  f10,
                  -f9,
                  0.0F
               )
               .color(
                  n9
               );
            BufferRenderer.drawWithGlobalProgram(
               buffer2.end()
            );
         }

         RenderSystem.lineWidth(2.5F);
         BufferBuilder buffer3 = Tessellator.getInstance()
            .begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
         int n10 = this.setAlpha(
            n,
            0.9F * this.appearValue
         );
         buffer3.vertex(
               matrix,
               -f4,
               0.0F,
               0.0F
            )
            .color(
               n10
            );
         buffer3.vertex(
               matrix,
               f4,
               0.0F,
               0.0F
            )
            .color(
               n10
            );
         buffer3.vertex(
               matrix,
               0.0F,
               -f5,
               0.0F
            )
            .color(
               n10
            );
         buffer3.vertex(
               matrix,
               -f4,
               0.0F,
               0.0F
            )
            .color(
               n10
            );
         BufferRenderer.drawWithGlobalProgram(
            buffer3.end()
         );

         for (int i3 = 0;
            i3
               < 3;
            i3++
         ) {
            float f12 = 1.0F
               + (float)i3
                  * 0.08F;
            float f13 = 0.25F
               * (
                  1.0F
                     - (float)i3
                        * 0.3F
               );
            int n11 = this.setAlpha(
               n,
               f13
                  * this.appearValue
            );
            BufferBuilder buffer4 = Tessellator.getInstance()
               .begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
            buffer4.vertex(
                  matrix,
                  -f4
                     * f12,
                  0.02F
                     * (float)i3,
                  0.0F
               )
               .color(
                  n11
               );
            buffer4.vertex(
                  matrix,
                  f4
                     * f12,
                  0.02F
                     * (float)i3,
                  0.0F
               )
               .color(
                  n11
               );
            buffer4.vertex(
                  matrix,
                  0.0F,
                  -f5
                     * f12,
                  0.0F
               )
               .color(
                  n11
               );
            BufferRenderer.drawWithGlobalProgram(
               buffer4.end()
            );
         }

         matrices.pop();
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
      }
   }

   private void drawGhosts2_3D(
      Event3DRender val
   ) {
      if (!(this.appearValue <= 0.001F) && this.lastTargetPos != null) {
         LivingEntity entity2 = this.lastTarget;
         if (entity2
               != null
            && entity2.isAlive()
            )
          {
            float f = 1.0F
               - (float)Math.pow((double)(1.0F - Math.min(this.appearValue, 1.0F)), 3.0);
            if (f
               != 0.0F) {
               float f2 = val.getTickDelta();
               Vec3d vec = new Vec3d(
                  MathHelper.lerp(
                     (double)f2,
                     entity2.lastRenderX,
                     entity2.getX()
                  ),
                  MathHelper.lerp(
                     (double)f2,
                     entity2.lastRenderY,
                     entity2.getY()
                  ),
                  MathHelper.lerp(
                     (double)f2,
                     entity2.lastRenderZ,
                     entity2.getZ()
                  )
               );
               Vec3d vec2 = mc.gameRenderer
                  .getCamera()
                  .getPos();
               float f3 = mc.gameRenderer
                  .getCamera()
                  .getYaw();
               float f4 = mc.gameRenderer
                  .getCamera()
                  .getPitch();
               double d = vec.x
                  - vec2.x;
               double d2 = vec.y
                  - vec2.y;
               double d3 = vec.z
                  - vec2.z;
               double d4 = this.ghostAge
                  - 0.5;
               float f5 = (float)d4
                  * 0.6F;
               float f6 = (float)Math.toRadians(
                  (double)(
                     (float)d4
                        * -2.0F
                  )
               );
               float width = entity2.getWidth()
                  * 1.0F;
               float height = entity2.getHeight()
                  / 1.6F;
               float height2 = entity2.getHeight()
                  / 4.5F;
               int n = 68;
               float f7 = 0.0F;
               float f8 = 0.00615F;
               int color2 = this.getESPColor();
               float f9 = this.getHurtPC(
                  entity2
               );
               int color3 = ColorUtil.rgb(
                  255,
                  3,
                  3
               );
               RenderSystem.enableBlend();
               RenderSystem.blendFunc(770, 1);
               RenderSystem.disableCull();
               RenderSystem.disableDepthTest();
               RenderSystem.depthMask(false);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
               RenderSystem.setShaderTexture(0, this.getBloomTexture());
               MatrixStack matrices = val.getMatrices();

               for (int i = 0;
                  i
                     < 4;
                  i++
               ) {
                  for (int i2 = 0;
                     i2
                        < n;
                     i2++
                  ) {
                     float f10 = f5
                        + (float)i
                           * 90.0F
                        - (float)i2;
                     float f11 = (float)Math.toRadians(
                        (double)f10
                     );
                     float f12 = (float)Math.sin(
                           (double)f11
                        )
                        * width;
                     float f13 = (float)Math.cos(
                           (double)f11
                        )
                        * width;
                     float f14 = height
                        + (float)(
                           Math.cos(
                                 Math.toRadians(
                                    (double)(
                                       f10
                                          * 2.0F
                                    )
                                 )
                              )
                              * (double)height2
                        );
                     float f15 = f12
                           * (float)Math.cos(
                              (double)f6
                           )
                        - f13
                           * (float)Math.sin(
                              (double)f6
                           );
                     float f16 = f12
                           * (float)Math.sin(
                              (double)f6
                           )
                        + f13
                           * (float)Math.cos(
                              (double)f6
                           );
                     float f17 = (float)(
                           n
                              - i2
                        )
                        / (float)n;
                     float f18 = f7
                        + f8
                           * (float)(
                              n
                                 - i2
                           );
                     int n2 = this.overCol(
                        color2,
                        color3,
                        f9
                     );
                     int n3 = this.multAlpha(
                        n2,
                        f17
                           * f
                           * 0.85F
                     );
                     if ((
                           n3
                                 >> 24
                              & 255
                        )
                        > 0) {
                        double d5 = vec.x
                           + (double)f15;
                        double d6 = vec.y
                           + (double)f14;
                        double d7 = vec.z
                           + (double)f16;
                        this.drawStaticBillboard(
                           matrices,
                           vec2,
                           d5,
                           d6,
                           d7,
                           f18,
                           n3,
                           0.0F
                        );
                        this.drawStaticBillboard(
                           matrices,
                           vec2,
                           d5,
                           d6,
                           d7,
                           f18,
                           n3,
                           0.0F
                        );
                     }
                  }
               }

               RenderSystem.depthMask(true);
               RenderSystem.enableDepthTest();
               RenderSystem.enableCull();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableBlend();
            }
         }
      }
   }

   private void drawGhosts3_3D(
      Event3DRender val
   ) {
      if (!(this.appearValue <= 0.001F) && this.lastTargetPos != null) {
         LivingEntity entity2 = this.lastTarget;
         if (entity2
               != null
            && entity2.isAlive()
            )
          {
            float f = 1.0F
               - (float)Math.pow((double)(1.0F - Math.min(this.appearValue, 1.0F)), 3.0);
            if (f
               != 0.0F) {
               float f2 = val.getTickDelta();
               Vec3d vec = new Vec3d(
                  MathHelper.lerp(
                     (double)f2,
                     entity2.lastRenderX,
                     entity2.getX()
                  ),
                  MathHelper.lerp(
                     (double)f2,
                     entity2.lastRenderY,
                     entity2.getY()
                  ),
                  MathHelper.lerp(
                     (double)f2,
                     entity2.lastRenderZ,
                     entity2.getZ()
                  )
               );
               Vec3d vec2 = mc.gameRenderer
                  .getCamera()
                  .getPos();
               double d = vec.x;
               double d2 = vec.y;
               double d3 = vec.z;
               float f3 = 0.3F;
               float f4 = 0.7F;
               int n = 55;
               double d4 = (double)System.currentTimeMillis()
                  / 350.0;
               float f5 = MathHelper.clamp(
                  (float)entity2.hurtTime
                     / 10.0F,
                  0.0F,
                  1.0F
               );
               float f6 = (float)Math.sin(
                  (double)entity2.hurtTime
                     * (Math.PI / 10)
               );
               float[] var10000 = new float[3];
               var10000[0] = entity2.getHeight()
                  * 0.85F;
               var10000[1] = entity2.getHeight()
                  * 0.55F;
               var10000[2] = entity2.getHeight()
                  * 0.25F;
               float[] val2 = var10000;
               double[] var58 = new double[3];
               var58[0] = Math.toRadians(0.0);
               var58[1] = Math.toRadians(120.0);
               var58[2] = Math.toRadians(240.0);
               double[] val3 = var58;
               List[] var59 = new List[3];
               var59[0] = this.phantomTrail1
                  .computeIfAbsent(
                     entity2,
                     val4 -> new ArrayList<>()
                  );
               var59[1] = this.phantomTrail2
                  .computeIfAbsent(
                     entity2,
                     val5 -> new ArrayList<>()
                  );
               var59[2] = this.phantomTrail3
                  .computeIfAbsent(
                     entity2,
                     val6 -> new ArrayList<>()
                  );
               List[] val7 = var59;
               RenderSystem.enableBlend();
               RenderSystem.blendFunc(770, 1);
               RenderSystem.disableCull();
               RenderSystem.disableDepthTest();
               RenderSystem.depthMask(false);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
               RenderSystem.setShaderTexture(0, this.getBloomTexture());
               MatrixStack matrices = val.getMatrices();
               int color2 = this.getESPColor();

               for (int i = 0;
                  i
                     < 3;
                  i++
               ) {
                  float f7 = 0.16F;
                  double d5 = Math.sin(
                        d4
                              * 1.5
                           + (double)i
                              * 2.0
                     )
                     * (double)f7;
                  double d6 = (double)val2[i]
                     + d5;
                  double d7 = d4
                     * 1.3;
                  double d8 = val3[i]
                     + d7;
                  double d9 = Math.cos(
                        d8
                     )
                     * 0.7F;
                  double d10 = Math.sin(
                        d8
                     )
                     * 0.7F;
                  Vec3d vec3 = new Vec3d(
                     d
                        + d9,
                     d2
                        + d6,
                     d3
                        + d10
                  );
                  List<Vec3d> vec4 = (List<Vec3d>)val7[i];
                  vec4.add(
                     0,
                     vec3
                  );

                  while (
                     vec4.size()
                        > 55
                  ) {
                     vec4.remove(
                        vec4.size()
                           - 1
                     );
                  }

                  for (int i2 = 0;
                     i2
                        < vec4.size();
                     i2++
                  ) {
                     Vec3d vec5 = vec4.get(
                        i2
                     );
                     float f8 = (float)i2
                        / 55.0F;
                     float f9 = (float)Math.pow(
                        (double)(
                           1.0F
                              - f8
                        ),
                        1.5
                     );
                     float f10 = 0.3F
                        * (
                           1.0F
                              - f8
                                 * 0.3F
                        );
                     int n2 = MathHelper.clamp(
                        (int)(
                           255.0F
                              * f
                              * f9
                        ),
                        0,
                        255
                     );
                     int n3;
                     if (f5
                        > 0.05F) {
                        int n4 = 255;
                        int n5 = 80
                           + (int)(
                              80.0F
                                 * (
                                    1.0F
                                       - f5
                                 )
                           );
                        int n6 = 80
                           + (int)(
                              80.0F
                                 * (
                                    1.0F
                                       - f5
                                 )
                           );
                        n3 = ColorUtil.rgba(
                           n4,
                           n5,
                           n6,
                           n2
                        );
                     } else {
                        n3 = ColorUtil.rgba(
                           ColorUtil.red(
                              color2
                           ),
                           ColorUtil.green(
                              color2
                           ),
                           ColorUtil.blue(
                              color2
                           ),
                           n2
                        );
                     }

                     int color3 = ColorUtil.rgba(
                        255,
                        80,
                        80,
                        n2
                     );
                     float f11 = MathHelper.clamp(
                        f6
                           * f5,
                        0.0F,
                        1.0F
                     );
                     int n7 = this.overCol(
                        n3,
                        color3,
                        f11
                     );
                     int color4 = ColorUtil.rgba(
                        ColorUtil.red(
                           n7
                        ),
                        ColorUtil.green(
                           n7
                        ),
                        ColorUtil.blue(
                           n7
                        ),
                        n2
                     );
                     float f12 = f10
                        * 1.3F;
                     int n8 = MathHelper.clamp(
                        (int)(
                           (float)n2
                              * 0.4F
                        ),
                        0,
                        255
                     );
                     int color5 = ColorUtil.rgba(
                        ColorUtil.red(
                           color4
                        ),
                        ColorUtil.green(
                           color4
                        ),
                        ColorUtil.blue(
                           color4
                        ),
                        n8
                     );
                     this.drawStaticBillboard(
                        matrices,
                        vec2,
                        vec5.x,
                        vec5.y,
                        vec5.z,
                        f12,
                        color5,
                        0.0F
                     );
                     this.drawStaticBillboard(
                        matrices,
                        vec2,
                        vec5.x,
                        vec5.y,
                        vec5.z,
                        f10,
                        color4,
                        0.0F
                     );
                  }
               }

               RenderSystem.depthMask(true);
               RenderSystem.enableDepthTest();
               RenderSystem.enableCull();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableBlend();
            }
         }
      }
   }

   @EventLink(
      priority = -100
   )
   public void onRender2D(
      EventRender.Default val
   ) {
      ModeSetting var10000 = this.mode;
      if (var10000.is("Кристаллы")
         && !(this.crystalAnimation <= 0.001F)
         && this.lastTargetPos != null) {
         LivingEntity entity2 = this.lastTarget
                  != null
               && this.lastTarget.isAlive()
            ? this.lastTarget
            : null;
         this.drawCrystalGlow2D(
            val.getContext()
               .getMatrices(),
            entity2
         );
      }
   }

   private int multAlpha(
      int val,
      float val2
   ) {
      int n = (int)(
         (float)(
               val
                     >> 24
                  & 255
            )
            * val2
      );
      n = Math.max(
         0,
         Math.min(
            255,
            n
         )
      );
      return n
            << 24
         | val
            & 16777215;
   }

   private int replAlpha(
      int val,
      int val2
   ) {
      val2 = Math.max(
         0,
         Math.min(
            255,
            val2
         )
      );
      return val2
            << 24
         | val
            & 16777215;
   }

   public static int overCol(
      int val,
      int val2,
      float val3
   ) {
      val3 = Math.max(
         0.0F,
         Math.min(
            1.0F,
            val3
         )
      );
      int n = val
            >> 16
         & 255;
      int n2 = val
            >> 8
         & 255;
      int n3 = val
         & 255;
      int n4 = val
            >> 24
         & 255;
      int n5 = val2
            >> 16
         & 255;
      int n6 = val2
            >> 8
         & 255;
      int n7 = val2
         & 255;
      int n8 = val2
            >> 24
         & 255;
      int n9 = (int)(
         (float)n
            + (float)(
                  n5
                     - n
               )
               * val3
      );
      int n10 = (int)(
         (float)n2
            + (float)(
                  n6
                     - n2
               )
               * val3
      );
      int n11 = (int)(
         (float)n3
            + (float)(
                  n7
                     - n3
               )
               * val3
      );
      int n12 = (int)(
         (float)n4
            + (float)(
                  n8
                     - n4
               )
               * val3
      );
      return n12
            << 24
         | n9
            << 16
         | n10
            << 8
         | n11;
   }

   private float getHurtPC(
      LivingEntity val
   ) {
      if (this.hurtColor.isState()
         && val
            != null) {
         float f = mc
               != null
            ? mc.getRenderTickCounter().getTickDelta(true)
            : 0.0F;
         float f2 = MathHelper.clamp(
            (float)val.hurtTime
               - f,
            0.0F,
            10.0F
         );
         float f3 = f2
            / 10.0F;
         return f3
            * f3
            * (
               3.0F
                  - 2.0F
                     * f3
            );
      } else {
         return 0.0F;
      }
   }

   private void drawBillboard(
      MatrixStack val,
      Vec3d val2,
      double val3,
      double val4,
      double val5,
      float val6,
      int val7,
      float val8
   ) {
      float f = this.getDistanceScale(
         val2,
         val3,
         val4,
         val5
      );
      float f2 = val6
         * f
         * 0.5F;
      this.drawBillboardInternal(
         val,
         val2,
         val3,
         val4,
         val5,
         f2,
         val7,
         val8
      );
   }

   private void drawStaticBillboard(
      MatrixStack val,
      Vec3d val2,
      double val3,
      double val4,
      double val5,
      float val6,
      int val7,
      float val8
   ) {
      float f = val6
         * 0.5F;
      this.drawBillboardInternal(
         val,
         val2,
         val3,
         val4,
         val5,
         f,
         val7,
         val8
      );
   }

   private void drawBillboardInternal(
      MatrixStack val,
      Vec3d val2,
      double val3,
      double val4,
      double val5,
      float val6,
      int val7,
      float val8
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
      if (n4
         > 0) {
         val.push();
         val.translate(
            val3
               - val2.x,
            val4
               - val2.y,
            val5
               - val2.z
         );
         val.multiply(
            RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw())
         );
         val.multiply(
            RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch())
         );
         if (val8
            != 0.0F) {
            val.multiply(
               RotationAxis.POSITIVE_Z
                  .rotationDegrees(
                     val8
                  )
            );
         }

         Matrix4f matrix = val.peek()
            .getPositionMatrix();
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(
               matrix,
               -val6,
               -val6,
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
               -val6,
               val6,
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
               val6,
               val6,
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
               val6,
               -val6,
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

   private void renderMarker3D(
      Event3DRender val
   ) {
      if (this.lastTargetPos != null && !(this.appearValue <= 0.001F)) {
         Vec3d vec = mc.gameRenderer
            .getCamera()
            .getPos();
         double d = this.lastTargetPos
            .x;
         double d2 = this.lastTargetPos
               .y
            + (double)((this.lastTargetHeight + 0.4F) * 0.5F);
         double d3 = this.lastTargetPos
            .z;
         float f = this.size
               .getValue()
               .floatValue()
            * 12.0F;
         float f2 = f
            * this.scaleValue;
         long val2 = System.currentTimeMillis();
         float x2 = Math.max(
            0.001F,
            (float)(
                  val2
                     - this.lastRotateUpdate
               )
               / 1000.0F
         );
         this.lastRotateUpdate = val2;
         float x3 = Math.max(
            0.35F, 2.2F / this.rotateSpeed.getValue().floatValue()
         );

         for (this.rotProgress += x2
               / x3;
            this.rotProgress >= 1.0F;
            this.rotTo = this.rotTo > 0.0F ? -280.0F : 280.0F
         ) {
            this.rotProgress--;
            this.rotFrom = this.rotTo;
         }

         float f3 = (float)EasingFunctions.SINE_IN_OUT
            .ease((double)this.rotProgress);
         float f4 = MathHelper.lerp(
            f3,
            this.rotFrom,
            this.rotTo
         );
         float f5 = this.getHurtPC(
            this.lastTarget
         );
         int n = this.multAlpha(
            this.getESPColor(), this.appearValue
         );
         int n2 = this.multAlpha(
            ColorUtil.rgb(
               255,
               3,
               3
            ),
            this.appearValue
         );
         int n3 = this.overCol(
            n,
            n2,
            f5
         );
         RenderSystem.enableBlend();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.getCaptureTexture());
         this.drawBillboard(
            val.getMatrices(),
            vec,
            d,
            d2,
            d3,
            f2,
            n3,
            f4
         );
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private void drawSouls3D(
      Event3DRender val
   ) {
      if (!(this.appearValue <= 0.001F) && this.lastTargetPos != null) {
         float f = mc.getRenderTickCounter()
            .getTickDelta(true);
         LivingEntity entity2 = this.lastTarget;
         Vec3d vec;
         float f2;
         if (entity2
               != null
            && entity2.isAlive()
            )
          {
            vec = new Vec3d(
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderX,
                  entity2.getX()
               ),
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderY,
                  entity2.getY()
               ),
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderZ,
                  entity2.getZ()
               )
            );
            f2 = entity2.getHeight();
         } else {
            vec = this.lastTargetPos;
            f2 = this.lastTargetHeight;
         }

         Vec3d vec2 = mc.gameRenderer
            .getCamera()
            .getPos();
         double d = vec.x;
         double d2 = vec.y
            + (double)(
               f2
                  / 2.0F
            );
         double d3 = vec.z;
         double d4 = 0.7;
         float f3 = 4.0F;
         long val2 = System.currentTimeMillis();
         float f4 = this.getHurtPC(
            entity2
         );
         int color2 = this.getESPColor();
         int color3 = ColorUtil.rgb(
            255,
            3,
            3
         );
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.getBloomTexture());
         MatrixStack matrices = val.getMatrices();

         for (int i = 0;
            i
               < 20;
            i++
         ) {
            float f5 = 1.0F
               - (float)i
                  / 20.0F
                  * 0.7F;
            double d5 = 0.15
               * (
                  (double)val2
                     - (double)i
                        * 10.0
               )
               / 25.0;
            double d6 = Math.sin(
                  d5
               )
               * d4;
            double d7 = Math.cos(
                  d5
               )
               * d4;
            double d8 = d
               + d6;
            double d9 = d2
               + d7;
            double d10 = d3
               - d7;
            float f6 = f3
               * f5;
            float f7 = this.appearValue
               * 0.6F;
            int n = this.multAlpha(
               color2,
               f7
                  * this.appearValue
            );
            int n2 = this.multAlpha(
               color3,
               f7
                  * this.appearValue
            );
            int n3 = this.overCol(
               n,
               n2,
               f4
            );
            this.drawStaticBillboard(
               matrices,
               vec2,
               d8,
               d9,
               d10,
               f6
                  * 0.12F,
               n3,
               0.0F
            );
            int n4 = this.multAlpha(
               n3,
               0.45F
            );
            this.drawStaticBillboard(
               matrices,
               vec2,
               d8,
               d9,
               d10,
               f6
                  * 0.21F,
               n4,
               0.0F
            );
         }

         for (int i2 = 0;
            i2
               < 20;
            i2++
         ) {
            float f8 = 1.0F
               - (float)i2
                  / 20.0F
                  * 0.7F;
            double d11 = 0.15
               * (
                  (double)val2
                     - (double)i2
                        * 10.0
               )
               / 25.0;
            double d12 = Math.sin(
                  d11
               )
               * d4;
            double d13 = Math.cos(
                  d11
               )
               * d4;
            double d14 = d
               - d12;
            double d15 = d2
               + d12;
            double d16 = d3
               - d13;
            float f9 = f3
               * f8;
            float f10 = this.appearValue
               * 0.6F;
            int n5 = this.multAlpha(
               color2,
               f10
                  * this.appearValue
            );
            int n6 = this.multAlpha(
               ColorUtil.rgb(
                  235,
                  7,
                  7
               ),
               f10
                  * this.appearValue
            );
            int n7 = this.overCol(
               n5,
               n6,
               f4
            );
            this.drawStaticBillboard(
               matrices,
               vec2,
               d14,
               d15,
               d16,
               f9
                  * 0.12F,
               n7,
               0.0F
            );
            int n8 = this.multAlpha(
               n7,
               0.45F
            );
            this.drawStaticBillboard(
               matrices,
               vec2,
               d14,
               d15,
               d16,
               f9
                  * 0.21F,
               n8,
               0.0F
            );
         }

         for (int i3 = 0;
            i3
               < 20;
            i3++
         ) {
            float f11 = 1.0F
               - (float)i3
                  / 20.0F
                  * 0.7F;
            double d17 = 0.15
               * (
                  (double)val2
                     - (double)i3
                        * 10.0
               )
               / 25.0;
            double d18 = Math.sin(
                  d17
               )
               * d4;
            double d19 = Math.cos(
                  d17
               )
               * d4;
            double d20 = d
               - d18;
            double d21 = d2
               - d18;
            double d22 = d3
               + d19;
            float f12 = f3
               * f11;
            float f13 = this.appearValue
               * 0.6F;
            int n9 = this.multAlpha(
               color2,
               f13
                  * this.appearValue
            );
            int n10 = this.multAlpha(
               color3,
               f13
                  * this.appearValue
            );
            int n11 = this.overCol(
               n9,
               n10,
               f4
            );
            this.drawStaticBillboard(
               matrices,
               vec2,
               d20,
               d21,
               d22,
               f12
                  * 0.12F,
               n11,
               0.0F
            );
            int n12 = this.multAlpha(
               n11,
               0.45F
            );
            this.drawStaticBillboard(
               matrices,
               vec2,
               d20,
               d21,
               d22,
               f12
                  * 0.21F,
               n12,
               0.0F
            );
         }

         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
      }
   }

   private void addBMWGhosts(
      LivingEntity val,
      float f24,
      int val2,
      int val3,
      int val4
   ) {
      float f = 0.7F;
      float height = val.getHeight();
      int n = (int)this.bmwStrengthXZ
         .getValue()
         .floatValue();
      int n2 = (int)this.bmwStrengthY
         .getValue()
         .floatValue();
      long val5 = System.currentTimeMillis();
      float f2 = (float)(
            val5
               % (long)n
         )
         / (float)n;
      float f3 = f2
         * 360.0F;
      float f4 = (float)(
            val5
               % (long)n2
         )
         / (float)n2;
      float f5 = 0.5F
         - 0.5F
            * MathHelper.cos(
               f4
                  * (float) (Math.PI * 2)
            );

      for (int i = 0;
         i
            < val2;
         i++
      ) {
         float f6 = (float)i
            / (float)val2;
         double d = Math.toRadians(
            (double)MathHelper.wrapDegrees(
               f6
                     * 360.0F
                  + f3
            )
         );
         float f7 = -(
               (float)Math.sin(
                  d
               )
            )
            * f;
         float f8 = height
            * f5;
         float f9 = (float)Math.cos(
               d
            )
            * f;
         this.bmwPoints
            .add(
               new ShouldremoveHelper(
                  f7,
                  f8,
                  f9,
                  val3,
                  val4
               )
            );
      }
   }

   private void drawBMW3D(
      Event3DRender val
   ) {
      if (!this.bmwPoints.isEmpty() && !(this.appearValue <= 0.001F)) {
         LivingEntity entity2 = this.lastTarget
               != null
            ? this.lastTarget
            : this.lastHandledTarget;
         if (entity2
               != null
            || this.lastTargetPos != null) {
            float f = mc.getRenderTickCounter()
               .getTickDelta(true);
            Vec3d vec;
            if (entity2
                  != null
               && entity2.isAlive()
               )
             {
               vec = new Vec3d(
                  MathHelper.lerp(
                     (double)f,
                     entity2.lastRenderX,
                     entity2.getX()
                  ),
                  MathHelper.lerp(
                     (double)f,
                     entity2.lastRenderY,
                     entity2.getY()
                  ),
                  MathHelper.lerp(
                     (double)f,
                     entity2.lastRenderZ,
                     entity2.getZ()
                  )
               );
            } else {
               vec = this.lastTargetPos;
            }

            if (vec
               != null) {
               Vec3d vec2 = mc.gameRenderer
                  .getCamera()
                  .getPos();
               float f2 = this.getHurtPC(
                  entity2
               );
               float f3 = 6.0F;
               RenderSystem.disableDepthTest();
               RenderSystem.enableBlend();
               RenderSystem.depthMask(false);
               RenderSystem.disableCull();
               RenderSystem.blendFunc(770, 1);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
               RenderSystem.setShaderTexture(0, this.getBloomTexture());
               MatrixStack matrices = val.getMatrices();

               for (ShouldremoveHelper val2 : this.bmwPoints) {
                  float f4 = val2.getTimeProgress();
                  float f5 = 1.0F
                     - f4
                        * 0.6F;
                  double d = vec.x
                     + (double)val2.x;
                  double d2 = vec.y
                     + (double)val2.y;
                  double d3 = vec.z
                     + (double)val2.z;
                  float f6 = f3
                     * f5;
                  int n = (int)(
                     255.0F
                        * this.appearValue
                        * f5
                        * 0.8F
                  );
                  n = Math.max(
                     0,
                     Math.min(
                        255,
                        n
                     )
                  );
                  int n2 = this.replAlpha(
                     val2.baseColor,
                     n
                  );
                  int n3 = this.replAlpha(
                     ColorUtil.rgb(
                        255,
                        3,
                        3
                     ),
                     n
                  );
                  int n4 = this.overCol(
                     n2,
                     n3,
                     f2
                  );
                  this.drawBillboard(
                     matrices,
                     vec2,
                     d,
                     d2,
                     d3,
                     f6,
                     n4,
                     0.0F
                  );
               }

               RenderSystem.enableCull();
               RenderSystem.enableDepthTest();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableBlend();
               RenderSystem.depthMask(true);
            }
         }
      }
   }

   private void drawCelka3D(
      Event3DRender val
   ) {
      if (!(this.appearValue <= 0.001F) && this.lastTargetPos != null) {
         float f = mc.getRenderTickCounter()
            .getTickDelta(true);
         LivingEntity entity2 = this.lastTarget;
         Vec3d vec;
         if (entity2
               != null
            && entity2.isAlive()
            )
          {
            vec = new Vec3d(
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderX,
                  entity2.getX()
               ),
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderY,
                  entity2.getY()
               ),
               MathHelper.lerp(
                  (double)f,
                  entity2.lastRenderZ,
                  entity2.getZ()
               )
            );
            float height = entity2.getHeight();
         } else {
            vec = this.lastTargetPos;
            float height2 = this.lastTargetHeight;
         }

         Vec3d vec2 = mc.gameRenderer
            .getCamera()
            .getPos();
         double d = vec.x;
         double d2 = vec.y;
         double d3 = vec.z;
         double d4 = (double)System.currentTimeMillis()
            / 384.61539872299335
            * 1.2F;
         double d5 = (double)System.currentTimeMillis()
            / 666.6666666666666
            * 1.2F;
         int color2 = this.getESPColor();
         float f2 = 4.0F;
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.getBloomTexture());
         MatrixStack matrices = val.getMatrices();
         float f3 = 0.65F;

         for (int i = 0;
            i
               < 4;
            i++
         ) {
            for (int i2 = 0;
               i2
                  < 20;
               i2++
            ) {
               float f4 = (float)i2
                  / 20.0F;
               float f5 = 1.0F
                  - f4
                     * 0.55F;
               double d6 = d4
                  - (double)i2
                     * 0.05;
               double d7 = d5
                  - (double)i2
                     * 0.05;
               double d8 = (
                     Math.sin(
                           d7
                        )
                        + 1.0
                  )
                  * 0.5;
               double d9 = Math.toRadians(
                  (double)i
                        * 90.0
                     + d6
                        * 50.0
                        % 360.0
               );
               double d10 = Math.cos(
                     d9
                  )
                  * (double)f3;
               double d11 = Math.sin(
                     d9
                  )
                  * (double)f3;
               double d12 = i
                        % 2
                     == 0
                  ? 0.1
                     + 1.7
                        * d8
                  : 1.8
                     - 1.7
                        * d8;
               double d13 = d
                  + d10;
               double d14 = d2
                  + d12;
               double d15 = d3
                  + d11;
               float f6 = f2
                  * f5;
               int n = (int)(
                  255.0F * this.appearValue * 0.6F
               );
               int n2 = this.replAlpha(
                  color2,
                  n
               );
               this.drawBillboard(
                  matrices,
                  vec2,
                  d13,
                  d14,
                  d15,
                  f6,
                  n2,
                  0.0F
               );
               int n3 = this.multAlpha(
                  n2,
                  0.45F
               );
               this.drawBillboard(
                  matrices,
                  vec2,
                  d13,
                  d14,
                  d15,
                  f6
                     * 1.75F,
                  n3,
                  0.0F
               );
            }

            f3 *= -1.0F;
         }

         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
      }
   }

   private void drawNursultanPrizrak_3D(
      Event3DRender val
   ) {
      if (!(this.appearValue <= 0.001F) && this.lastTargetPos != null) {
         LivingEntity entity2 = this.lastTarget;
         if (entity2
               != null
            && entity2.isAlive()
            )
          {
            MatrixStack matrices = val.getMatrices();
            Vec3d vec = mc.gameRenderer
               .getCamera()
               .getPos();
            long val2 = System.currentTimeMillis();
            float f = (float)(
                  val2
                     - this.nursultanTimestamp
               )
               / 120.0F;
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            RenderSystem.setShaderTexture(0, this.getBloomTexture());
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 1, 0, 1);
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            double d = MathHelper.lerp(
                  (double)val.getTickDelta(),
                  entity2.lastRenderX,
                  entity2.getX()
               )
               - vec.x;
            double d2 = MathHelper.lerp(
                  (double)val.getTickDelta(),
                  entity2.lastRenderY,
                  entity2.getY()
               )
               - vec.y;
            double d3 = MathHelper.lerp(
                  (double)val.getTickDelta(),
                  entity2.lastRenderZ,
                  entity2.getZ()
               )
               - vec.z;
            int n = 3;
            int n2 = 12;
            int n3 = 3
               * n;
            matrices.push();
            BufferBuilder buffer = Tessellator.getInstance()
               .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            int color2 = this.getESPColor();
            int n4 = color2
                  >> 16
               & 255;
            int n5 = color2
                  >> 8
               & 255;
            int n6 = color2
               & 255;

            for (int i = 0;
               i
                  < n3;
               i += n
            ) {
               for (int i2 = 0;
                  i2
                     < n2;
                  i2++
               ) {
                  float f2 = f
                     + (float)i2
                        * 0.1F;
                  int n7 = (int)Math.pow(
                     (double)i,
                     2.0
                  );
                  matrices.push();
                  matrices.translate(
                     d
                        + (double)(
                           0.8F
                              * MathHelper.sin(
                                 f2
                                    + (float)n7
                              )
                        ),
                     d2
                        + 0.5
                        + (double)(
                           0.3F
                              * MathHelper.sin(
                                 f
                                    + (float)i2
                                       * 0.2F
                              )
                        )
                        + (double)(
                           0.2F
                              * (float)i
                        ),
                     d3
                        + (double)(
                           0.8F
                              * MathHelper.cos(
                                 f2
                                    - (float)n7
                              )
                        )
                  );
                  float f3 = this.appearValue
                     * (
                        0.005F
                           + (float)i2
                              / 2000.0F
                     );
                  matrices.scale(
                     f3,
                     f3,
                     f3
                  );
                  matrices.multiply(
                     mc.gameRenderer.getCamera().getRotation()
                  );
                  int n8 = -25;
                  int n9 = 50;
                  int n10 = (int)(
                     this.appearValue * 600.0F
                  );
                  n10 = Math.max(
                     0,
                     Math.min(
                        255,
                        n10
                     )
                  );
                  int color3 = ColorUtil.rgba(
                     n4,
                     n5,
                     n6,
                     n10
                  );
                  Matrix4f matrix = matrices.peek()
                     .getPositionMatrix();
                  buffer.vertex(
                        matrix,
                        (float)n8,
                        (float)(
                           n8
                              + n9
                        ),
                        0.0F
                     )
                     .texture(0.0F, 1.0F)
                     .color(
                        color3
                     );
                  buffer.vertex(
                        matrix,
                        (float)(
                           n8
                              + n9
                        ),
                        (float)(
                           n8
                              + n9
                        ),
                        0.0F
                     )
                     .texture(1.0F, 1.0F)
                     .color(
                        color3
                     );
                  buffer.vertex(
                        matrix,
                        (float)(
                           n8
                              + n9
                        ),
                        (float)n8,
                        0.0F
                     )
                     .texture(1.0F, 0.0F)
                     .color(
                        color3
                     );
                  buffer.vertex(
                        matrix,
                        (float)n8,
                        (float)n8,
                        0.0F
                     )
                     .texture(0.0F, 0.0F)
                     .color(
                        color3
                     );
                  matrices.pop();
               }
            }

            BufferRenderer.drawWithGlobalProgram(
               buffer.end()
            );
            matrices.pop();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.blendFunc(
               770,
               771
            );
            RenderSystem.enableCull();
         }
      }
   }

   private void renderCrystals3D(
      MatrixStack val,
      LivingEntity val2,
      float val3
   ) {
      if (this.lastTargetPos != null && !(this.crystalAnimation <= 0.01F)) {
         Vec3d vec = mc.gameRenderer
            .getCamera()
            .getPos();
         int color2 = ColorUtil.getThemeColor();
         int n = this.multAlpha(
            color2,
            this.crystalAnimation
         );
         int n2 = this.multAlpha(
            color2,
            this.crystalAnimation * 0.28F
         );
         float f = this.getHurtPC(
            val2
         );
         if (f
            > 0.0F) {
            int n3 = this.multAlpha(
               ColorUtil.rgb(
                  255,
                  3,
                  3
               ),
               this.crystalAnimation
            );
            n = this.overCol(
               n,
               n3,
               f
            );
            n2 = this.overCol(
               n2,
               this.multAlpha(
                  n3,
                  0.65F
               ),
               f
            );
         }

         float f2 = val2
               != null
            ? val2.getWidth()
            : this.lastTargetWidth;
         float f3 = val2
               != null
            ? val2.getHeight()
            : this.lastTargetHeight;
         float f4 = f2
            * 1.5F;
         Vec3d vec2;
         if (val2
               != null
            && val2.isAlive()
            )
          {
            vec2 = new Vec3d(
               MathHelper.lerp(
                  (double)val3,
                  val2.lastRenderX,
                  val2.getX()
               ),
               MathHelper.lerp(
                  (double)val3,
                  val2.lastRenderY,
                  val2.getY()
               ),
               MathHelper.lerp(
                  (double)val3,
                  val2.lastRenderZ,
                  val2.getZ()
               )
            );
         } else {
            vec2 = this.lastTargetPos;
         }

         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         float f5 = 1.2F
            - 0.5F * this.crystalAnimation;
         val.push();
         val.translate(
            vec2.x
               - vec.x,
            vec2.y
               - vec.y,
            vec2.z
               - vec.z
         );
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

         for (int i = 0;
            i
               < 360;
            i += 20
         ) {
            float f6 = (float)Math.toRadians(
               (double)(
                  (float)i
                     + this.crystalRotationAngle
               )
            );
            float f7 = (float)(
               Math.sin(
                     (double)f6
                  )
                  * (double)f4
                  * (double)f5
            );
            float f8 = (float)(
               Math.cos(
                     (double)f6
                  )
                  * (double)f4
                  * (double)f5
            );
            float f9 = 0.1F;
            float f10 = 0.1F
               + f3
                  * Math.abs(
                     MathHelper.sin(
                        (float)i
                     )
                  );
            float f11 = f3
               / 2.0F;
            float f12 = -f7;
            float f13 = f11
               - f10;
            float f14 = -f8;
            float f15 = (float)Math.sqrt(
               (double)(
                  f12
                        * f12
                     + f13
                        * f13
                     + f14
                        * f14
               )
            );
            if (!(
               f15
                  < 0.001F
            )) {
               f12 /= f15;
               f13 /= f15;
               f14 /= f15;
               val.push();
               val.translate(
                  f7,
                  f10,
                  f8
               );
               Vector3f vec3f = new Vector3f(
                  0.0F, 1.0F, 0.0F
               );
               Vector3f vec3f2 = new Vector3f(
                  f12,
                  f13,
                  f14
               );
               Vector3f vec3f3 = new Vector3f();
               vec3f.cross(
                  vec3f2,
                  vec3f3
               );
               float f16 = vec3f3.length();
               if (f16
                  >= 0.001F) {
                  vec3f3.div(
                     f16
                  );
                  float x2 = Math.max(
                     -1.0F,
                     Math.min(
                        1.0F,
                        vec3f.dot(
                           vec3f2
                        )
                     )
                  );
                  float f17 = (float)Math.acos(
                     (double)x2
                  );
                  val.multiply(
                     new Quaternionf()
                        .setAngleAxis(
                           f17,
                           vec3f3.x,
                           vec3f3.y,
                           vec3f3.z
                        )
                  );
               }

               this.renderCrystalShape(
                  buffer,
                  val.peek()
                     .getPositionMatrix(),
                  f9,
                  n
               );
               val.pop();
            }
         }

         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         val.pop();
         float f18 = 4.5F
            + f2
               * 3.0F;
         float f19 = f18
            * 1.28F;
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.getBloomTexture());

         for (int i2 = 0;
            i2
               < 360;
            i2 += 20
         ) {
            float f20 = (float)Math.toRadians(
               (double)(
                  (float)i2
                     + this.crystalRotationAngle
               )
            );
            float f21 = (float)(
               Math.sin(
                     (double)f20
                  )
                  * (double)f4
                  * (double)f5
            );
            float f22 = (float)(
               Math.cos(
                     (double)f20
                  )
                  * (double)f4
                  * (double)f5
            );
            float f23 = 0.1F
               + f3
                  * Math.abs(
                     MathHelper.sin(
                        (float)i2
                     )
                  );
            double d = vec2.x
               + (double)f21;
            double d2 = vec2.y
               + (double)f23;
            double d3 = vec2.z
               + (double)f22;
            this.drawBillboard(
               val,
               vec,
               d,
               d2,
               d3,
               f19,
               this.multAlpha(
                  n2,
                  0.24F
               ),
               this.crystalRotationAngle
                  + (float)i2
            );
            this.drawBillboard(
               val,
               vec,
               d,
               d2,
               d3,
               f18,
               n2,
               -(
                  this.crystalRotationAngle
                     + (float)i2
                        * 0.5F
               )
            );
         }

         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
      }
   }

   private void renderCrystalShape(
      BufferBuilder val,
      Matrix4f val2,
      float val3,
      int val4
   ) {
      int n = val4
            >> 16
         & 255;
      int n2 = val4
            >> 8
         & 255;
      int n3 = val4
         & 255;
      int n4 = val4
            >> 24
         & 255;
      float f = 0.34F
         * val3
         / 0.1F;
      float f2 = 1.15F
         * val3
         / 0.1F;
      f = 0.06F;
      f2 = 0.2F;
      this.tri(
         val,
         val2,
         0.0F,
         f2,
         0.0F,
         f,
         0.0F,
         0.0F,
         0.0F,
         0.0F,
         f,
         n,
         n2,
         n3,
         n4
      );
      this.tri(
         val,
         val2,
         0.0F,
         f2,
         0.0F,
         0.0F,
         0.0F,
         f,
         -f,
         0.0F,
         0.0F,
         n,
         n2,
         n3,
         n4
      );
      this.tri(
         val,
         val2,
         0.0F,
         f2,
         0.0F,
         -f,
         0.0F,
         0.0F,
         0.0F,
         0.0F,
         -f,
         n,
         n2,
         n3,
         n4
      );
      this.tri(
         val,
         val2,
         0.0F,
         f2,
         0.0F,
         0.0F,
         0.0F,
         -f,
         f,
         0.0F,
         0.0F,
         n,
         n2,
         n3,
         n4
      );
      this.tri(
         val,
         val2,
         0.0F,
         -f2,
         0.0F,
         f,
         0.0F,
         0.0F,
         0.0F,
         0.0F,
         f,
         n,
         n2,
         n3,
         n4
      );
      this.tri(
         val,
         val2,
         0.0F,
         -f2,
         0.0F,
         0.0F,
         0.0F,
         f,
         -f,
         0.0F,
         0.0F,
         n,
         n2,
         n3,
         n4
      );
      this.tri(
         val,
         val2,
         0.0F,
         -f2,
         0.0F,
         -f,
         0.0F,
         0.0F,
         0.0F,
         0.0F,
         -f,
         n,
         n2,
         n3,
         n4
      );
      this.tri(
         val,
         val2,
         0.0F,
         -f2,
         0.0F,
         0.0F,
         0.0F,
         -f,
         f,
         0.0F,
         0.0F,
         n,
         n2,
         n3,
         n4
      );
   }

   private float[] project2D(
      double d23,
      double d24,
      double val16
   ) {
      return null;
   }

   private double getScale(
      double val,
      double val2,
      double val3
   ) {
      Vec3d vec = mc.gameRenderer
         .getCamera()
         .getPos();
      double d = val
         - vec.x;
      double d2 = val2
         - vec.y;
      double d3 = val3
         - vec.z;
      double d4 = Math.sqrt(
         d
               * d
            + d2
               * d2
            + d3
               * d3
      );
      return Math.max(
         0.5,
         8.0
            / Math.max(
               0.1,
               d4
            )
      );
   }

   private void drawTexturedRect2D(
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

   private void drawCrystalGlow2D(
      MatrixStack matrices2,
      LivingEntity val16
   ) {
   }

   private void tri(
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
      int val13,
      int val14,
      int val15
   ) {
      val.vertex(
            val2,
            val3,
            val4,
            val5
         )
         .color(
            val12,
            val13,
            val14,
            val15
         );
      val.vertex(
            val2,
            val6,
            val7,
            val8
         )
         .color(
            val12,
            val13,
            val14,
            val15
         );
      val.vertex(
            val2,
            val9,
            val10,
            val11
         )
         .color(
            val12,
            val13,
            val14,
            val15
         );
   }
}
