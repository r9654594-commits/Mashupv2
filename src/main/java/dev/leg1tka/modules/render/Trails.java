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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Trails extends Module {
   public static Trails INSTANCE = new Trails();
   private final NumberSetting duration = new NumberSetting(
      "Длительность", 300.0F, 100.0F, 1000.0F, 10.0F
   );
   private final List<PosHolder_3> points = new ArrayList<>();

   public Trails() {
      super(
         "Trails",
         "Красивый след за игроком",
         Category.RENDER
      );
      this.addSettings(new Setting[]{this.duration});
   }

   @Override
   public void onDisable() {
      this.points.clear();
      super.onDisable();
   }

   @EventLink
   public void onRender(
      Event3DRender val
   ) {
      if (mc.options.getPerspective() != Perspective.FIRST_PERSON) {
         if (mc.player != null && mc.world != null) {
            long val2 = System.currentTimeMillis();
            this.points
               .removeIf(
                  val3 -> (float)(
                           val2
                              - val3.time
                        )
                        > this.duration.get()
               );
            Vec3d pos2 = this.interpolatePlayerPosition(
               val.getTickDelta()
            );
            this.points
               .add(
                  new PosHolder_3(
                     pos2
                  )
               );
            this.render3DPoints(
               val.getMatrices()
            );
         }
      }
   }

   private Vec3d interpolatePlayerPosition(
      float val
   ) {
      return new Vec3d(
         MathHelper.lerp(
            (double)val,
            mc.player.prevX,
            mc.player.getX()
         ),
         MathHelper.lerp(
            (double)val,
            mc.player.prevY,
            mc.player.getY()
         ),
         MathHelper.lerp(
            (double)val,
            mc.player.prevZ,
            mc.player.getZ()
         )
      );
   }

   private Vec3d interpolatePlayerPosition(
      PlayerEntity val,
      float val2
   ) {
      return new Vec3d(
         MathHelper.lerp(
            (double)val2,
            val.prevX,
            val.getX()
         ),
         MathHelper.lerp(
            (double)val2,
            val.prevY,
            val.getY()
         ),
         MathHelper.lerp(
            (double)val2,
            val.prevZ,
            val.getZ()
         )
      );
   }

   private void render3DPoints(
      MatrixStack val
   ) {
      if (this.points.size() >= 2) {
         this.startRendering();
         val.push();
         Vec3d vec = mc.gameRenderer
            .getCamera()
            .getPos();
         val.translate(
            -vec.x,
            -vec.y,
            -vec.z
         );
         Matrix4f matrix = val.peek()
            .getPositionMatrix();
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
         BufferBuilder buffer = Tessellator.getInstance()
            .begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
         int n = 0;

         for (PosHolder_3 val2 : this.points) {
            float f4 = (float)n
               / (float)this.points.size()
               * 0.7F;
            int n2 = (int)(
               f4
                  * 255.0F
            );
            buffer.vertex(
                  matrix,
                  (float)val2.pos
                     .x,
                  (float)(
                     val2.pos
                           .y
                        + (double)mc.player.getHeight()
                  ),
                  (float)val2.pos
                     .z
               )
               .color(
                  (int)(
                     f
                        * 255.0F
                  ),
                  (int)(
                     f2
                        * 255.0F
                  ),
                  (int)(
                     f3
                        * 255.0F
                  ),
                  n2
               );
            buffer.vertex(
                  matrix,
                  (float)val2.pos
                     .x,
                  (float)val2.pos
                     .y,
                  (float)val2.pos
                     .z
               )
               .color(
                  (int)(
                     f
                        * 255.0F
                  ),
                  (int)(
                     f2
                        * 255.0F
                  ),
                  (int)(
                     f3
                        * 255.0F
                  ),
                  n2
               );
            n++;
         }

         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         RenderSystem.lineWidth(2.0F);
         this.renderLineStrip(
            matrix,
            this.points,
            true,
            f,
            f2,
            f3
         );
         this.renderLineStrip(
            matrix,
            this.points,
            false,
            f,
            f2,
            f3
         );
         val.pop();
         this.stopRendering();
      }
   }

   private void renderLineStrip(
      Matrix4f val,
      List<PosHolder_3> val2,
      boolean val3,
      float val4,
      float val5,
      float val6
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
      int n = 0;

      for (PosHolder_3 val7 : val2) {
         float f = Math.min(
            (float)n
               / (float)val2.size()
               * 1.5F,
            1.0F
         );
         int n2 = (int)(
            f
               * 255.0F
         );
         float f2 = val3
            ? (float)(
               val7.pos
                     .y
                  + (double)mc.player.getHeight()
            )
            : (float)val7.pos
               .y;
         buffer.vertex(
               val,
               (float)val7.pos
                  .x,
               f2,
               (float)val7.pos
                  .z
            )
            .color(
               (int)(
                  val4
                     * 255.0F
               ),
               (int)(
                  val5
                     * 255.0F
               ),
               (int)(
                  val6
                     * 255.0F
               ),
               n2
            );
         n++;
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void startRendering() {
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
   }

   private void stopRendering() {
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }
}
