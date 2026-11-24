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
import dev.leg1tka.api.events.implement.EventRender;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class BlockOverlay extends Module {
   public static BlockOverlay INSTANCE = new BlockOverlay();
   private final ModeSetting mode;
   private final NumberSetting waveSpeed;
   private final NumberSetting waveScale;
   private final NumberSetting lineSpeed;
   private final NumberSetting lineJitter;
   private final NumberSetting outline;
   private final NumberSetting glow;
   private final NumberSetting fill;
   private final NumberSetting alpha;
   private final NumberSetting smooth;
   private Framebuffer maskBuffer;
   private int fbWidth;
   private int fbHeight;
   private boolean hasMask;
   private BlockPos lastBlockPos;
   private Box displayBox;
   private Box targetBox;
   private int cachedThemeColor1;
   private int cachedThemeColor2;

   public BlockOverlay() {
      super(
         "BlockOverlay",
         "Block overlay shader",
         Category.RENDER
      );
      this.mode = new ModeSetting("Режим", "Шейдер", new String[]{"Шейдер", "Нитки"});
      this.waveSpeed = new NumberSetting(
            "Скорость волн",
            1.2F,
            0.1F,
            5.0F,
            0.1F
         )
         .visible(
            () -> this.mode
                  .is(
                     "Шейдер"
                  )
         );
      this.waveScale = new NumberSetting(
            "Частота волн",
            1.0F,
            1.0F,
            3.0F,
            0.1F
         )
         .visible(
            () -> this.mode
                  .is(
                     "Шейдер"
                  )
         );
      this.lineSpeed = new NumberSetting(
            "Скорость нитей",
            1.4F,
            0.1F,
            5.0F,
            0.1F
         )
         .visible(
            () -> this.mode
                  .is(
                     "Нитки"
                  )
         );
      this.lineJitter = new NumberSetting(
            "Изгиб нитей",
            0.55F,
            0.0F,
            1.5F,
            0.01F
         )
         .visible(
            () -> this.mode
                  .is(
                     "Нитки"
                  )
         );
      this.outline = new NumberSetting(
         "Ширина обводки",
         1.1F,
         0.1F,
         5.0F,
         0.1F
      );
      this.glow = new NumberSetting(
         "Сила свечения",
         1.0F,
         0.0F,
         5.0F,
         0.1F
      );
      this.fill = new NumberSetting(
         "Заливка",
         0.6F,
         0.0F,
         1.0F,
         0.01F
      );
      this.alpha = new NumberSetting(
         "Прозрачность",
         1.0F,
         0.0F,
         1.0F,
         0.01F
      );
      this.smooth = new NumberSetting(
         "Плавность",
         0.24F,
         0.05F,
         0.6F,
         0.01F
      );
      this.fbWidth = -1;
      this.fbHeight = -1;
      this.cachedThemeColor1 = -1;
      this.cachedThemeColor2 = -1;
      this.addSettings(new Setting[]{
         this.mode,
         this.waveSpeed,
         this.waveScale,
         this.lineSpeed,
         this.lineJitter,
         this.outline,
         this.glow,
         this.fill,
         this.alpha,
         this.smooth
      });
   }

   @Override
   public void onDisable() {
      this.hasMask = false;
      this.lastBlockPos = null;
      this.displayBox = null;
      this.targetBox = null;
      super.onDisable();
   }

   @EventLink(
      priority = -100
   )
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc != null && mc.world != null && mc.player != null) {
         Box box = this.getTargetedBlockBox();
         if (box
            == null) {
            this.hasMask = false;
            this.lastBlockPos = null;
            this.displayBox = null;
            this.targetBox = null;
         } else {
            if (this.displayBox != null && this.targetBox != null && this.lastBlockPos != null) {
               this.targetBox = box;
               this.displayBox = this.lerpBox(this.displayBox, this.targetBox, this.smooth.get());
            } else {
               this.displayBox = box;
               this.targetBox = box;
            }

            this.lastBlockPos = BlockPos.ofFloored(
               box.minX,
               box.minY,
               box.minZ
            );
            this.updateCachedThemeColors();
            Vec3d vec = val.getCamera()
               .getPos();
            Box box2 = this.displayBox
               .offset(
                  -vec.x,
                  -vec.y,
                  -vec.z
               );
            Matrix4f matrix = val.getMatrices()
               .peek()
               .getPositionMatrix();
            this.ensureMaskBuffer();
            if (this.maskBuffer != null) {
               this.hasMask = true;
               this.maskBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               this.maskBuffer.clear();
               this.copyMainDepthToMask();
               this.maskBuffer.beginWrite(false);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableCull();
               RenderSystem.enableDepthTest();
               RenderSystem.depthMask(false);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
               this.drawMaskBox(
                  matrix,
                  box2
               );
               RenderSystem.depthMask(true);
               RenderSystem.disableDepthTest();
               RenderSystem.enableCull();
               RenderSystem.disableBlend();
               mc.getFramebuffer().beginWrite(false);
               if (this.mode
                  .is(
                     "Нитки"
                  )) {
                  this.drawAnimatedWeb(
                     matrix,
                     box2
                  );
               }
            }
         }
      }
   }

   @EventLink(
      priority = 200
   )
   public void onRender2D(
      EventRender.Default val10
   ) {
      if (this.hasMask && this.maskBuffer != null) {
         if (!this.mode
            .is(
               "Нитки"
            )) {
            ShaderProgram val = mc.getShaderLoader()
               .getOrCreateProgram(ClientShaders.blockOverlay);
            if (val
               != null) {
               boolean flag = this.mode
                  .is(
                     "Нитки"
                  );
               int color3 = this.cachedThemeColor1;
               int color4 = this.cachedThemeColor2;
               mc.getFramebuffer().beginWrite(false);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.enableDepthTest();
               RenderSystem.setShader(ClientShaders.blockOverlay);
               RenderSystem.setShaderTexture(0, this.maskBuffer.getColorAttachment());
               this.setUniform(
                  val,
                  "texelSize",
                  1.0F / (float)Math.max(1, mc.getWindow().getFramebufferWidth()),
                  1.0F / (float)Math.max(1, mc.getWindow().getFramebufferHeight())
               );
               this.setUniform(
                  val,
                  "color",
                  ColorUtil.redf(
                     color3
                  ),
                  ColorUtil.greenf(
                     color3
                  ),
                  ColorUtil.bluef(
                     color3
                  )
               );
               this.setUniform(
                  val,
                  "color2",
                  ColorUtil.redf(
                     color4
                  ),
                  ColorUtil.greenf(
                     color4
                  ),
                  ColorUtil.bluef(
                     color4
                  )
               );
               this.setUniform(
                  val,
                  "time",
                  (float)(System.currentTimeMillis() % 100000L) / 1000.0F
               );
               this.setUniform(
                  val,
                  "speed",
                  this.waveSpeed.get()
               );
               this.setUniform(
                  val,
                  "scale",
                  this.waveScale.get()
               );
               this.setUniform(
                  val,
                  "outline",
                  this.outline.get()
               );
               this.setUniform(
                  val,
                  "glow",
                  flag
                     ? 0.0F
                     : this.glow.get()
               );
               this.setUniform(
                  val,
                  "fill",
                  flag
                     ? 0.0F
                     : this.fill.get()
               );
               this.setUniform(
                  val,
                  "alpha",
                  flag
                     ? 1.0F
                     : this.alpha.get()
               );
               this.setUniform(
                  val,
                  "outlineOnly",
                  flag
                     ? 1.0F
                     : 0.0F
               );
               this.drawFullscreenQuad();
               RenderSystem.enableDepthTest();
               RenderSystem.disableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.setShaderTexture(0, 0);
            }
         }
      }
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

   private void setUniform(
      ShaderProgram val,
      String val2,
      float val3,
      float val4
   ) {
      GlUniform val5 = val.getUniform(
         val2
      );
      if (val5
         != null) {
         val5.set(
            val3,
            val4
         );
      }
   }

   private void setUniform(
      ShaderProgram val,
      String val2,
      float val3,
      float val4,
      float val5
   ) {
      GlUniform val6 = val.getUniform(
         val2
      );
      if (val6
         != null) {
         val6.set(
            val3,
            val4,
            val5
         );
      }
   }

   private void ensureMaskBuffer() {
      int n = mc.getWindow()
         .getFramebufferWidth();
      int n2 = mc.getWindow()
         .getFramebufferHeight();
      if (this.maskBuffer == null
         || this.fbWidth
            != n
         || this.fbHeight
            != n2
         )
       {
         if (this.maskBuffer != null) {
            this.maskBuffer.delete();
         }

         this.maskBuffer = new SimpleFramebuffer(
            n,
            n2,
            true
         );
         this.fbWidth = n;
         this.fbHeight = n2;
      }
   }

   private Box getTargetedBlockBox() {
      HitResult hitResult = mc.crosshairTarget;
      if (hitResult instanceof BlockHitResult val
         && hitResult.getType()
            == Type.BLOCK) {
         BlockPos pos = val.getBlockPos();
         if (pos
            == null) {
            return null;
         }

         if (mc.world
            .getBlockState(
               pos
            )
            .isAir()) {
            return null;
         }

         VoxelShape val2 = mc.world
            .getBlockState(
               pos
            )
            .getOutlineShape(
               mc.world,
               pos
            );
         Box box = val2.isEmpty()
            ? new Box(
               pos
            )
            : val2.getBoundingBox()
               .offset(
                  pos
               );
         return box.expand(
            0.002
         );
      }

      return null;
   }

   private Box lerpBox(
      Box val,
      Box val2,
      float val3
   ) {
      return new Box(
         val.minX
            + (
                  val2.minX
                     - val.minX
               )
               * (double)val3,
         val.minY
            + (
                  val2.minY
                     - val.minY
               )
               * (double)val3,
         val.minZ
            + (
                  val2.minZ
                     - val.minZ
               )
               * (double)val3,
         val.maxX
            + (
                  val2.maxX
                     - val.maxX
               )
               * (double)val3,
         val.maxY
            + (
                  val2.maxY
                     - val.maxY
               )
               * (double)val3,
         val.maxZ
            + (
                  val2.maxZ
                     - val.maxZ
               )
               * (double)val3
      );
   }

   private void drawAnimatedWeb(
      Matrix4f val,
      Box val2
   ) {
      int n = 5;
      int n2 = 18;
      float f = (float)(
            System.currentTimeMillis() % 100000L
         )
         / 1000.0F
         * this.lineSpeed.get();
      float f2 = 0.0025F;
      float f3 = 0.06F
         + this.lineJitter.get() * 0.2F;
      int n3 = Math.max(
         20,
         Math.min(255, (int)(this.alpha.get() * 210.0F))
      );
      int color3 = this.cachedThemeColor1;
      long val3 = this.lastBlockPos
            != null
         ? this.lastBlockPos.asLong()
         : 1L;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      this.drawFilledBox(
         val,
         val2,
         ColorUtil.setAlphaColor(
            color3,
            (int)(this.alpha.get() * this.fill.get() * 170.0F)
         )
      );

      for (int i = 0;
         i
            < 6;
         i++
      ) {
         int[] val4 = this.faceNeighbors(
            i
         );

         for (int i2 = 0;
            i2
               < n;
            i2++
         ) {
            int n4 = i
                  * 1000
               + i2
                  * 53;
            int n5 = val4[i2
               % val4.length];
            double d = (double)f
                  * (
                     0.95
                        + this.rand01(
                              val3,
                              n4
                                 + 1
                           )
                           * 0.55
                  )
               + (double)i2
                  * 0.83
               + (double)i
                  * 1.11;
            double d2 = this.clamp01(
               0.5
                  + Math.sin(
                        d
                              * 1.37
                           + this.rand01(
                                 val3,
                                 n4
                                    + 2
                              )
                              * 6.2831853
                     )
                     * 0.38
            );
            Vec3d vec = this.edgePoint(
               val2,
               i,
               n5,
               d2,
               0.0015
            );
            Vec3d vec2 = this.facePoint(
               val2,
               i,
               this.clamp01(
                  0.5
                     + (
                           this.rand01(
                                 val3,
                                 n4
                                    + 3
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               this.clamp01(
                  0.5
                     + (
                           this.rand01(
                                 val3,
                                 n4
                                    + 4
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               0.0015
            );
            Vec3d vec3 = this.facePoint(
               val2,
               n5,
               this.clamp01(
                  0.5
                     + (
                           this.rand01(
                                 val3,
                                 n4
                                    + 5
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               this.clamp01(
                  0.5
                     + (
                           this.rand01(
                                 val3,
                                 n4
                                    + 6
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               0.0015
            );
            Vec3d[] vec4 = this.faceBasis(
               i
            );
            Vec3d[] vec5 = this.faceBasis(
               n5
            );
            Vec3d vec6 = this.faceNormal(
               i
            );
            Vec3d vec7 = this.faceNormal(
               n5
            );
            double d3 = (double)f3
               * (
                  0.7
                     + this.rand01(
                        val3,
                        n4
                           + 7
                     )
               )
               * Math.sin(
                  d
                        * 1.9
                     + this.rand01(
                           val3,
                           n4
                              + 8
                        )
                        * 6.2831853
               );
            double d4 = (double)f3
               * (
                  0.7
                     + this.rand01(
                        val3,
                        n4
                           + 9
                     )
               )
               * Math.cos(
                  d
                        * 1.7
                     + this.rand01(
                           val3,
                           n4
                              + 10
                        )
                        * 6.2831853
               );
            Vec3d vec8 = vec.subtract(
               vec2
            );
            Vec3d vec9 = vec2.add(
                  vec8.multiply(
                     0.38
                  )
               )
               .add(
                  vec4[0]
                     .multiply(
                        d3
                     )
               )
               .add(
                  vec4[1]
                     .multiply(
                        -d3
                           * 0.55
                     )
               );
            Vec3d vec10 = vec2.add(
                  vec8.multiply(
                     0.76
                  )
               )
               .add(
                  vec4[0]
                     .multiply(
                        -d3
                           * 0.65
                     )
               )
               .add(
                  vec4[1]
                     .multiply(
                        d3
                           * 0.4
                     )
               );
            Vec3d vec11 = vec3.subtract(
               vec
            );
            Vec3d vec12 = vec.add(
                  vec11.multiply(
                     0.24
                  )
               )
               .add(
                  vec5[0]
                     .multiply(
                        d4
                     )
               )
               .add(
                  vec5[1]
                     .multiply(
                        d4
                           * 0.45
                     )
               );
            Vec3d vec13 = vec.add(
                  vec11.multiply(
                     0.62
                  )
               )
               .add(
                  vec5[0]
                     .multiply(
                        -d4
                           * 0.7
                     )
               )
               .add(
                  vec5[1]
                     .multiply(
                        -d4
                           * 0.35
                     )
               );
            int n6 = Math.max(
               18,
               Math.min(
                  255,
                  (int)(
                     (double)n3
                        * (
                           0.74
                              + 0.26
                                 * Math.sin(
                                    d
                                       * 2.6
                                 )
                        )
                  )
               )
            );
            int color4 = ColorUtil.setAlphaColor(
               color3,
               n6
            );
            this.drawBezierRibbon(
               val,
               vec2,
               vec9,
               vec10,
               vec,
               vec6,
               n2,
               color4,
               f2
            );
            this.drawBezierRibbon(
               val,
               vec,
               vec12,
               vec13,
               vec3,
               vec7,
               n2,
               color4,
               f2
            );
         }
      }

      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   private void copyMainDepthToMask() {
      if (this.maskBuffer != null) {
         int n = GL11.glGetInteger(
            36010
         );
         int n2 = GL11.glGetInteger(
            36006
         );
         int n3 = mc.getWindow()
            .getFramebufferWidth();
         int n4 = mc.getWindow()
            .getFramebufferHeight();
         GL30.glBindFramebuffer(36008, mc.getFramebuffer().fbo);
         GL30.glBindFramebuffer(36009, this.maskBuffer.fbo);
         GL30.glBlitFramebuffer(
            0,
            0,
            n3,
            n4,
            0,
            0,
            n3,
            n4,
            256,
            9728
         );
         GL30.glBindFramebuffer(
            36008,
            n
         );
         GL30.glBindFramebuffer(
            36009,
            n2
         );
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

   private void drawBezierRibbon(
      Matrix4f val,
      Vec3d val2,
      Vec3d val3,
      Vec3d val4,
      Vec3d val5,
      Vec3d val6,
      int val7,
      int val8,
      float val9
   ) {
      Vec3d[] vec = new Vec3d[val7
         + 1];

      for (int i = 0;
         i
            <= val7;
         i++
      ) {
         float f = (float)i
            / (float)val7;
         vec[i] = this.cubicBezier(
            val2,
            val3,
            val4,
            val5,
            f
         );
      }

      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (int i2 = 0;
         i2
            < val7;
         i2++
      ) {
         Vec3d vec2 = vec[i2];
         Vec3d vec3 = vec[i2
            + 1];
         Vec3d vec4 = vec3.subtract(
            vec2
         );
         if (!(
            vec4.lengthSquared()
               < 1.0E-6
         )) {
            Vec3d vec5 = val6.crossProduct(
                  vec4
               )
               .normalize()
               .multiply(
                  (double)val9
               );
            Vec3d vec6 = vec2.add(
               vec5
            );
            Vec3d vec7 = vec2.subtract(
               vec5
            );
            Vec3d vec8 = vec3.add(
               vec5
            );
            Vec3d vec9 = vec3.subtract(
               vec5
            );
            buffer.vertex(
                  val,
                  (float)vec6.x,
                  (float)vec6.y,
                  (float)vec6.z
               )
               .color(
                  val8
               );
            buffer.vertex(
                  val,
                  (float)vec7.x,
                  (float)vec7.y,
                  (float)vec7.z
               )
               .color(
                  val8
               );
            buffer.vertex(
                  val,
                  (float)vec9.x,
                  (float)vec9.y,
                  (float)vec9.z
               )
               .color(
                  val8
               );
            buffer.vertex(
                  val,
                  (float)vec8.x,
                  (float)vec8.y,
                  (float)vec8.z
               )
               .color(
                  val8
               );
         }
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
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
               int color3 = val.color
                        != null
                     && val.color.length
                        > 0
                  ? val.color[0]
                  : ColorUtil.getThemeColor(0);
               this.cachedThemeColor1 = color3;
               this.cachedThemeColor2 = color3;
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

   private int[] faceNeighbors(
      int val
   ) {
      int[] var10000;
      switch (val) {
         case 0:
         case 1:
            var10000 = new int[4];
            var10000[0] = 2;
            var10000[1] = 3;
            var10000[2] = 4;
            var10000[3] = 5;
            break;
         case 2:
         case 3:
            var10000 = new int[4];
            var10000[0] = 0;
            var10000[1] = 1;
            var10000[2] = 4;
            var10000[3] = 5;
            break;
         default:
            var10000 = new int[4];
            var10000[0] = 0;
            var10000[1] = 1;
            var10000[2] = 2;
            var10000[3] = 3;
      }

      return var10000;
   }

   private Vec3d[] faceBasis(
      int val
   ) {
      Vec3d[] var10000;
      switch (val) {
         case 0:
         case 1:
            var10000 = new Vec3d[2];
            var10000[0] = new Vec3d(1.0, 0.0, 0.0);
            var10000[1] = new Vec3d(0.0, 0.0, 1.0);
            break;
         case 2:
         case 3:
            var10000 = new Vec3d[2];
            var10000[0] = new Vec3d(1.0, 0.0, 0.0);
            var10000[1] = new Vec3d(0.0, 1.0, 0.0);
            break;
         default:
            var10000 = new Vec3d[2];
            var10000[0] = new Vec3d(0.0, 0.0, 1.0);
            var10000[1] = new Vec3d(0.0, 1.0, 0.0);
      }

      return var10000;
   }

   private Vec3d faceNormal(
      int val
   ) {
      return switch (val) {
         case 0 -> new Vec3d(0.0, 1.0, 0.0);
         case 1 -> new Vec3d(0.0, -1.0, 0.0);
         case 2 -> new Vec3d(0.0, 0.0, -1.0);
         case 3 -> new Vec3d(0.0, 0.0, 1.0);
         case 4 -> new Vec3d(-1.0, 0.0, 0.0);
         default -> new Vec3d(1.0, 0.0, 0.0);
      };
   }

   private Vec3d edgePoint(
      Box val,
      int val2,
      int val3,
      double val4,
      double val5
   ) {
      double d = Double.NaN;
      double d2 = Double.NaN;
      double d3 = Double.NaN;
      double[] val6 = this.faceFixedCoords(
         val,
         val2,
         val5
      );
      if (!Double.isNaN(
         val6[0]
      )) {
         d = val6[0];
      }

      if (!Double.isNaN(
         val6[1]
      )) {
         d2 = val6[1];
      }

      if (!Double.isNaN(
         val6[2]
      )) {
         d3 = val6[2];
      }

      double[] val7 = this.faceFixedCoords(
         val,
         val3,
         val5
      );
      if (!Double.isNaN(
         val7[0]
      )) {
         d = val7[0];
      }

      if (!Double.isNaN(
         val7[1]
      )) {
         d2 = val7[1];
      }

      if (!Double.isNaN(
         val7[2]
      )) {
         d3 = val7[2];
      }

      double d4 = this.clamp01(
         val4
      );
      if (Double.isNaN(
         d
      )) {
         d = this.lerp(
            val.minX,
            val.maxX,
            d4
         );
      }

      if (Double.isNaN(
         d2
      )) {
         d2 = this.lerp(
            val.minY,
            val.maxY,
            d4
         );
      }

      if (Double.isNaN(
         d3
      )) {
         d3 = this.lerp(
            val.minZ,
            val.maxZ,
            d4
         );
      }

      return new Vec3d(
         d,
         d2,
         d3
      );
   }

   private double[] faceFixedCoords(
      Box val,
      int val2,
      double val3
   ) {
      double[] var10000;
      switch (val2) {
         case 0:
            var10000 = new double[3];
            var10000[0] = Double.NaN;
            var10000[1] = val.maxY
               - val3;
            var10000[2] = Double.NaN;
            break;
         case 1:
            var10000 = new double[3];
            var10000[0] = Double.NaN;
            var10000[1] = val.minY
               + val3;
            var10000[2] = Double.NaN;
            break;
         case 2:
            var10000 = new double[3];
            var10000[0] = Double.NaN;
            var10000[1] = Double.NaN;
            var10000[2] = val.minZ
               + val3;
            break;
         case 3:
            var10000 = new double[3];
            var10000[0] = Double.NaN;
            var10000[1] = Double.NaN;
            var10000[2] = val.maxZ
               - val3;
            break;
         case 4:
            var10000 = new double[3];
            var10000[0] = val.minX
               + val3;
            var10000[1] = Double.NaN;
            var10000[2] = Double.NaN;
            break;
         default:
            var10000 = new double[3];
            var10000[0] = val.maxX
               - val3;
            var10000[1] = Double.NaN;
            var10000[2] = Double.NaN;
      }

      return var10000;
   }

   private Vec3d facePoint(
      Box val,
      int val2,
      double val3,
      double val4,
      double val5
   ) {
      val3 = this.clamp01(
         val3
      );
      val4 = this.clamp01(
         val4
      );

      return switch (val2) {
         case 0 -> new Vec3d(
         this.lerp(
            val.minX,
            val.maxX,
            val3
         ),
         val.maxY
            - val5,
         this.lerp(
            val.minZ,
            val.maxZ,
            val4
         )
      );
         case 1 -> new Vec3d(
         this.lerp(
            val.minX,
            val.maxX,
            val3
         ),
         val.minY
            + val5,
         this.lerp(
            val.minZ,
            val.maxZ,
            val4
         )
      );
         case 2 -> new Vec3d(
         this.lerp(
            val.minX,
            val.maxX,
            val3
         ),
         this.lerp(
            val.minY,
            val.maxY,
            val4
         ),
         val.minZ
            + val5
      );
         case 3 -> new Vec3d(
         this.lerp(
            val.minX,
            val.maxX,
            val3
         ),
         this.lerp(
            val.minY,
            val.maxY,
            val4
         ),
         val.maxZ
            - val5
      );
         case 4 -> new Vec3d(
         val.minX
            + val5,
         this.lerp(
            val.minY,
            val.maxY,
            val4
         ),
         this.lerp(
            val.minZ,
            val.maxZ,
            val3
         )
      );
         default -> new Vec3d(
         val.maxX
            - val5,
         this.lerp(
            val.minY,
            val.maxY,
            val4
         ),
         this.lerp(
            val.minZ,
            val.maxZ,
            val3
         )
      );
      };
   }

   private double rand01(
      long val,
      int val2
   ) {
      long val3 = val
         + -7046029254386353131L
            * (
               (long)val2
                  + 1L
            );
      val3 ^= val3
         >>> 30;
      val3 *= -4658895280553007687L;
      val3 ^= val3
         >>> 27;
      val3 *= -7723592293110705685L;
      val3 ^= val3
         >>> 31;
      return (double)(
            val3
               & 16777215L
         )
         / 1.6777216E7;
   }

   private double lerp(
      double val,
      double val2,
      double val3
   ) {
      return val
         + (
               val2
                  - val
            )
            * val3;
   }

   private double clamp01(
      double val
   ) {
      return Math.max(
         0.0,
         Math.min(
            1.0,
            val
         )
      );
   }

   private void drawFilledBox(
      Matrix4f val,
      Box val2,
      int val3
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void drawMaskBox(
      Matrix4f val,
      Box val2
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      int n = -1;
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            n
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            n
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void drawFullscreenQuad() {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float x2 = (float)Math.max(
         mc.getWindow().getScaledWidth(), 1
      );
      float x3 = (float)Math.max(
         mc.getWindow().getScaledHeight(), 1
      );
      buffer.vertex(
            0.0F, 0.0F, 0.0F
         )
         .texture(0.0F, 1.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            0.0F,
            x3,
            0.0F
         )
         .texture(0.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            x2,
            x3,
            0.0F
         )
         .texture(1.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            x2,
            0.0F,
            0.0F
         )
         .texture(1.0F, 1.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }
}
