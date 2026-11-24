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
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class LineGlyphes extends Module {
   public static LineGlyphes INSTANCE = new LineGlyphes();
   private static final Vec3i[] AXIS_DIRECTIONS = new Vec3i[]{
      new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 1, 0), new Vec3i(0, -1, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1)
   };
   private final NumberSetting glyphsCount = new NumberSetting("Количество Линий", 70.0F, 10.0F, 200.0F, 1.0F);
   public final BooleanSetting slowSpeed = new BooleanSetting("Медленная Скорость", false);
   private final BooleanSetting applyStippleLines = new BooleanSetting("Пунктирные Линии", false);
   private final NumberSetting stippleStepPixels = new NumberSetting("Шаг Пунктира", 3.0F, 0.5F, 20.0F, 0.5F)
      .visible(() -> this.applyStippleLines.isState());
   private final BooleanSetting linesGlowing = new BooleanSetting("Свечение", false);
   private final Random rand = new Random(93882L);
   private final List<UpdateHelper_2> glyphs = new ArrayList<>();

   public LineGlyphes() {
      super("LineGlyphes", "Анимированные линии по миру", Category.RENDER);
      this.addSettings(new Setting[]{
         this.glyphsCount,
         this.slowSpeed,
         this.applyStippleLines,
         this.stippleStepPixels,
         this.linesGlowing
      });
   }

   @Override
   public void onDisable() {
      this.glyphs.clear();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val9
   ) {
      if (mc.player != null && mc.world != null) {
         this.updateGlyphs();
         this.maintainGlyphCount();
      } else {
         this.glyphs.clear();
      }
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.player != null
         && mc.world != null
         && val.getCamera()
            != null) {
         this.drawAllGlyphs(
            val
         );
      }
   }

   private void drawAllGlyphs(
      Event3DRender val
   ) {
      if (!this.glyphs.isEmpty()) {
         List val2 = new ArrayList<>();

         for (UpdateHelper_2 val3 : this.glyphs) {
            if (val3.getAlpha()
                  > 0.01F
               && val3.getPointCount()
                  >= 2) {
               val2.add(
                  val3
               );
            }
         }

         if (!val2.isEmpty()
            )
          {
            MatrixStack matrices = val.getMatrices();
            Vec3d vec = val.getCamera()
               .getPos();
            matrices.push();
            matrices.translate(
               -vec.x,
               -vec.y,
               -vec.z
            );

            try {
               this.setupRenderState();
               if (this.linesGlowing.isState()) {
                  this.setAdditiveBlend();
                  this.drawGlyphPass(
                     matrices,
                     val2,
                     val.getTickDelta(),
                     4.0F,
                     0.08F
                  );
                  this.drawGlyphPass(
                     matrices,
                     val2,
                     val.getTickDelta(),
                     2.5F,
                     0.15F
                  );
                  this.drawGlyphPass(
                     matrices,
                     val2,
                     val.getTickDelta(),
                     1.5F,
                     0.25F
                  );
               }

               this.setNormalBlend();
               this.drawGlyphPass(
                  matrices,
                  val2,
                  val.getTickDelta(),
                  1.0F,
                  1.0F
               );
            } finally {
               this.restoreRenderState();
               matrices.pop();
            }
         }
      }
   }

   private void drawGlyphPass(
      MatrixStack val,
      List<UpdateHelper_2> val2,
      float val3,
      float val4,
      float val5
   ) {
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      boolean flag = this.applyStippleLines
         .isState();
      float x2 = Math.max(
         0.04F, this.stippleStepPixels.get() * 0.08F
      );
      float f = x2
         * 0.85F;
      RenderSystem.lineWidth(
         Math.min(
            1.15F
               * val4,
            12.0F
         )
      );
      List<XHolder_2> list = new ArrayList<>();
      int n = 0;

      for (UpdateHelper_2 val6 : val2) {
         float f2 = val6.getAlpha()
            * val5;
         if (!(
            f2
               <= 0.01F
         )) {
            List<Vec3d> pos = val6.getSmoothedPositions(
               val3
            );
            int count = pos.size();
            if (count
               >= 2) {
               for (int i = 0;
                  i
                     < count
                        - 1;
                  i++
               ) {
                  Vec3d vec = pos.get(
                     i
                  );
                  Vec3d vec2 = pos.get(
                     i
                        + 1
                  );
                  float f3 = f2
                     * (
                        0.25F
                           + (float)i
                              / (float)count
                              / 1.75F
                     );
                  int color2 = this.getThemeColorWithAlpha(
                     f3
                  );
                  n += 180;
                  if (flag
                     )
                   {
                     this.collectDashedSegment(
                        list,
                        vec,
                        vec2,
                        color2,
                        x2,
                        f
                     );
                  } else {
                     this.collectSegment(
                        list,
                        vec,
                        vec2,
                        color2
                     );
                  }
               }
            }
         }
      }

      float f4 = 0.018F
         * val4;
      int n2 = 0;

      for (UpdateHelper_2 val7 : val2) {
         float f5 = val7.getAlpha()
            * val5;
         if (!(
            f5
               <= 0.01F
         )) {
            List<Vec3d> pos2 = val7.getSmoothedPositions(
               val3
            );
            int count2 = pos2.size();
            if (count2
               >= 2) {
               for (int i2 = 0;
                  i2
                     < count2;
                  i2++
               ) {
                  Vec3d vec3 = pos2.get(
                     i2
                  );
                  float f6 = f5
                     * (
                        0.25F
                           + (float)i2
                              / (float)count2
                              / 1.75F
                     );
                  int color3 = this.getThemeColorWithAlpha(
                     f6
                  );
                  n2 += 180;
                  this.collectPointCross(
                     list,
                     vec3,
                     color3,
                     f4
                  );
               }
            }
         }
      }

      if (!list.isEmpty()
         )
       {
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder buffer = tessellator.begin(
            DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR
         );

         for (XHolder_2 val8 : list) {
            buffer.vertex(
                  matrix,
                  val8.x,
                  val8.y,
                  val8.z
               )
               .color(
                  val8.r,
                  val8.g,
                  val8.b,
                  val8.a
               );
         }

         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
      }

      RenderSystem.lineWidth(1.0F);
   }

   private int getThemeColorWithAlpha(
      float val
   ) {
      int color2 = ColorUtil.getThemeColor();
      float f = MathHelper.clamp(
         val,
         0.0F,
         1.0F
      );
      int n = color2
            >> 16
         & 255;
      int n2 = color2
            >> 8
         & 255;
      int n3 = color2
         & 255;
      int n4 = (int)(
         f
            * 255.0F
      );
      return n4
            << 24
         | n
            << 16
         | n2
            << 8
         | n3;
   }

   private void collectSegment(
      List<XHolder_2> val,
      Vec3d val2,
      Vec3d val3,
      int val4
   ) {
      val.add(
         new XHolder_2(
            (float)val2.x,
            (float)val2.y,
            (float)val2.z,
            val4
         )
      );
      val.add(
         new XHolder_2(
            (float)val3.x,
            (float)val3.y,
            (float)val3.z,
            val4
         )
      );
   }

   private void collectDashedSegment(
      List<XHolder_2> val,
      Vec3d val2,
      Vec3d val3,
      int val4,
      float val5,
      float val6
   ) {
      double d = val3.x
         - val2.x;
      double d2 = val3.y
         - val2.y;
      double d3 = val3.z
         - val2.z;
      double d4 = Math.sqrt(
         d
               * d
            + d2
               * d2
            + d3
               * d3
      );
      if (!(
         d4
            < 1.0E-4
      )) {
         double d5 = d
            / d4;
         double d6 = d2
            / d4;
         double d7 = d3
            / d4;
         double d8 = 0.0;

         for (boolean flag = true;
            d8
               < d4;
            flag = !flag
         ) {
            double d9 = flag
               ? (double)val5
               : (double)val6;
            double d10 = Math.min(
               d4,
               d8
                  + d9
            );
            if (flag
               )
             {
               float f = (float)(
                  val2.x
                     + d5
                        * d8
               );
               float f2 = (float)(
                  val2.y
                     + d6
                        * d8
               );
               float f3 = (float)(
                  val2.z
                     + d7
                        * d8
               );
               float f4 = (float)(
                  val2.x
                     + d5
                        * d10
               );
               float f5 = (float)(
                  val2.y
                     + d6
                        * d10
               );
               float f6 = (float)(
                  val2.z
                     + d7
                        * d10
               );
               val.add(
                  new XHolder_2(
                     f,
                     f2,
                     f3,
                     val4
                  )
               );
               val.add(
                  new XHolder_2(
                     f4,
                     f5,
                     f6,
                     val4
                  )
               );
            }

            d8 = d10;
         }
      }
   }

   private void collectPointCross(
      List<XHolder_2> val,
      Vec3d val2,
      int val3,
      float val4
   ) {
      float x2 = (float)val2.x;
      float y2 = (float)val2.y;
      float f = (float)val2.z;
      val.add(
         new XHolder_2(
            x2
               - val4,
            y2,
            f,
            val3
         )
      );
      val.add(
         new XHolder_2(
            x2
               + val4,
            y2,
            f,
            val3
         )
      );
      val.add(
         new XHolder_2(
            x2,
            y2
               - val4,
            f,
            val3
         )
      );
      val.add(
         new XHolder_2(
            x2,
            y2
               + val4,
            f,
            val3
         )
      );
      val.add(
         new XHolder_2(
            x2,
            y2,
            f
               - val4,
            val3
         )
      );
      val.add(
         new XHolder_2(
            x2,
            y2,
            f
               + val4,
            val3
         )
      );
   }

   private void setupRenderState() {
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      GL11.glEnable(2848);
      GL11.glHint(
         3154,
         4354
      );
   }

   private void setAdditiveBlend() {
      RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ONE, DstFactor.ZERO);
   }

   private void setNormalBlend() {
      RenderSystem.defaultBlendFunc();
   }

   private void restoreRenderState() {
      RenderSystem.lineWidth(1.0F);
      GL11.glDisable(2848);
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void updateGlyphs() {
      Iterator<UpdateHelper_2> val = this.glyphs
         .iterator();

      while (
         val.hasNext()
      ) {
         UpdateHelper_2 val2 = val.next();
         val2.update();
         if (val2.isDead()
            )
          {
            val.remove();
         }
      }
   }

   private void maintainGlyphCount() {
      int count = this.maxGlyphCount();

      int n;
      for (n = this.countActiveGlyphs();
         n
            < count;
         n++
      ) {
         this.glyphs
            .add(
               new UpdateHelper_2(
                  this,
                  this.randomGlyphSpawnPos(),
                  this.randInt(
                     7,
                     12
                  )
               )
            );
      }

      if (n
         > count
         )
       {
         for (UpdateHelper_2 val : this.glyphs) {
            if (n
               <= count
               )
             {
               break;
            }

            if (!val.isRemoving()
               )
             {
               val.setWantToRemove();
               n--;
            }
         }
      }
   }

   private int countActiveGlyphs() {
      int n = 0;

      for (UpdateHelper_2 val : this.glyphs) {
         if (!val.isRemoving()
            )
          {
            n++;
         }
      }

      return n;
   }

   private int maxGlyphCount() {
      return Math.max(1, Math.round(this.glyphsCount.get()));
   }

   private Vec3i randomGlyphSpawnPos() {
      int n = 6;
      int n2 = 24;
      int n3 = 0;
      int n4 = 12;
      Vec3d pos = this.getCameraPos();

      for (int i = 0;
         i
            < 16;
         i++
      ) {
         int n5 = this.randInt(
            6, 25
         );
         float y2 = mc.player
               != null
            ? mc.player.getYaw()
            : 0.0F;
         float f = y2
            + this.randFloat(-135.0F, 135.0F);
         float f2 = (float)Math.toRadians(
            (double)f
         );
         int n6 = (int)(
            -(
               MathHelper.sin(
                     f2
                  )
                  * (float)n5
            )
         );
         int n7 = this.randInt(
            0, 13
         );
         int n8 = (int)(
            MathHelper.cos(
                  f2
               )
               * (float)n5
         );
         Vec3i val = new Vec3i(
            (int)Math.floor(
                  pos.x
               )
               + n6,
            (int)Math.floor(
                  pos.y
               )
               + n7,
            (int)Math.floor(
                  pos.z
               )
               + n8
         );
         if (this.isSpawnPosFree(
            val
         )) {
            return val;
         }
      }

      return new Vec3i(
         (int)Math.floor(
            pos.x
         ),
         (int)Math.floor(
            pos.y
         ),
         (int)Math.floor(
            pos.z
         )
      );
   }

   public boolean isSpawnPosFree(
      Vec3i val
   ) {
      if (mc.world == null) {
         return true;
      } else {
         BlockPos pos = new BlockPos(
            val.getX(),
            val.getY(),
            val.getZ()
         );
         BlockState state = mc.world
            .getBlockState(
               pos
            );
         if (state.isAir()
            )
          {
            return true;
         } else {
            return !state.getFluidState()
                  .isEmpty()
               ? false
               : state.getCollisionShape(
                     mc.world,
                     pos
                  )
                  .isEmpty();
         }
      }
   }

   private Vec3d getCameraPos() {
      Camera camera = mc.gameRenderer
            != null
         ? mc.gameRenderer.getCamera()
         : null;
      if (camera
         != null) {
         return camera.getPos();
      } else {
         return mc.player != null ? mc.player.getPos() : Vec3d.ZERO;
      }
   }

   public Vec3i randomAxisDirection() {
      return AXIS_DIRECTIONS[this.rand.nextInt(AXIS_DIRECTIONS.length)];
   }

   public Vec3i nextOrthogonalDirection(
      Vec3i val
   ) {
      for (int i = 0;
         i
            < 12;
         i++
      ) {
         Vec3i val2 = this.randomAxisDirection();
         int n = val2.getX()
               * val.getX()
            + val2.getY()
               * val.getY()
            + val2.getZ()
               * val.getZ();
         if (n
            == 0) {
            return val2;
         }
      }

      return this.randomAxisDirection();
   }

   public int randInt(
      int val,
      int val2
   ) {
      return val2
            <= val
         ? val
         : this.rand
               .nextInt(
                  val2
                     - val
               )
            + val;
   }

   private float randFloat(
      float val,
      float val2
   ) {
      return val2
            <= val
         ? val
         : val
            + this.rand.nextFloat()
               * (
                  val2
                     - val
               );
   }

   public float moveAdvanceFromTicks(
      int val,
      int val2,
      float val3
   ) {
      if (val
         <= 0) {
         return 1.0F;
      } else {
         float f = 1.0F
            - (
                  (float)val2
                     - val3
               )
               / (float)val;
         return MathHelper.clamp(
            f,
            0.0F,
            1.0F
         );
      }
   }

   public static double lerp(
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
}
