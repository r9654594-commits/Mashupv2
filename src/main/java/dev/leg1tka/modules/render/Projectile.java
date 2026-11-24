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
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class Projectile extends Module {
   private final Font impactFont = Fonts.getFont(
      "sf_regular", 14
   );
   private static final ItemStack PEARL_STACK = new ItemStack(Items.ENDER_PEARL);
   public static Projectile INSTANCE = new Projectile();
   private final NumberSetting size = new NumberSetting("Размер", 1.2F, 0.6F, 2.4F, 0.1F);
   private final List<PosHelper> impactPoints = new ArrayList<>();
   private final Matrix4f lastProjectionMatrix = new Matrix4f();
   private final Quaternionf lastCameraRotation = new Quaternionf();
   private Vec3d lastCameraPos = Vec3d.ZERO;
   private boolean hasMatrices;

   public Projectile() {
      super("Projectile", "Траектория жемчуга эндера", Category.RENDER);
      this.addSettings(new Setting[]{this.size});
   }

   private List<Vec3d> simulate(
      EnderPearlEntity val,
      float val7
   ) {
      List val2 = new ArrayList<>();
      if (mc.world != null && mc.player != null) {
         Vec3d pos2 = val.getPos();
         Vec3d velocity = val.getVelocity();
         val2.add(
            pos2
         );
         double d = 0.03;
         double d2 = 0.99;
         int n = 400;
         Vec3d vec = pos2;
         Vec3d vec2 = velocity;

         for (int i = 0;
            i
               < n;
            i++
         ) {
            Vec3d vec3 = vec.add(
               vec2
            );
            RaycastContext val3 = new RaycastContext(
               vec,
               vec3,
               ShapeType.COLLIDER,
               FluidHandling.NONE,
               mc.player
            );
            BlockHitResult blockHit = mc.world
               .raycast(
                  val3
               );
            if (blockHit.getType()
               != Type.MISS) {
               val2.add(
                  blockHit.getPos()
               );
               break;
            }

            val2.add(
               vec3
            );
            vec2 = new Vec3d(
               vec2.x
                  * d2,
               vec2.y
                     * d2
                  - d,
               vec2.z
                  * d2
            );
            vec = vec3;
            if (vec2.length()
                  < 0.01
               && i
                  > 10) {
               break;
            }
         }

         return val2;
      } else {
         return val2;
      }
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.world != null && mc.player != null) {
         this.impactPoints.clear();
         this.hasMatrices = true;
         this.lastProjectionMatrix
            .set(
               val.getProjectionMatrix()
            );
         this.lastCameraPos = val.getCamera()
            .getPos();
         this.lastCameraRotation
            .set(
               val.getCamera()
                  .getRotation()
            );
         MatrixStack matrices = val.getMatrices();
         Camera camera = val.getCamera();
         Vec3d pos2 = camera.getPos();
         float f = val.getTickDelta();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
         RenderSystem.lineWidth(Math.max(1.0F, this.size.get() * 2.0F));
         GL11.glEnable(2848);
         Box box = mc.player
            .getBoundingBox()
            .expand(128.0);

         for (EnderPearlEntity entity2 : mc.world
            .getEntitiesByClass(
               EnderPearlEntity.class,
               box,
               Entity::isAlive
            )) {
            List<Vec3d> vec = this.simulate(
               entity2,
               f
            );
            if (vec.size()
               >= 2) {
               float f2 = (float)(
                     vec.size()
                        - 1
                  )
                  / 20.0F;
               Vec3d vec2 = vec.get(
                  vec.size()
                     - 1
               );
               this.impactPoints
                  .add(
                     new PosHelper(
                        vec2,
                        f2,
                        PEARL_STACK
                     )
                  );
               matrices.push();
               matrices.translate(
                  -pos2.x,
                  -pos2.y,
                  -pos2.z
               );
               BufferBuilder buffer = Tessellator.getInstance()
                  .begin(DrawMode.LINES, VertexFormats.LINES);
               int color2 = ColorUtil.getThemeColor();

               for (int i = 1;
                  i
                     < vec.size();
                  i++
               ) {
                  int n = MathHelper.clamp(
                     (int)(
                        MathHelper.clamp(
                              (float)i
                                 / 25.0F,
                              0.0F,
                              1.0F
                           )
                           * 255.0F
                     ),
                     0,
                     255
                  );
                  int color3 = ColorUtil.setAlphaColor(
                     color2,
                     n
                  );
                  this.vertexLine(
                     matrices,
                     buffer,
                     vec.get(
                        i
                           - 1
                     ),
                     vec.get(
                        i
                     ),
                     color3
                  );
               }

               BufferRenderer.drawWithGlobalProgram(
                  buffer.end()
               );
               matrices.pop();
            }
         }

         RenderSystem.lineWidth(1.0F);
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         GL11.glDisable(2848);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      }
   }

   @EventLink
   public void onRender2D(
      EventRender.Default val
   ) {
      if (this.hasMatrices && !this.impactPoints.isEmpty() && mc.player != null) {
         DrawContext context = val.getContext();
         MatrixStack matrices = context.getMatrices();
         Font font = this.impactFont;
         if (font
            != null) {
            for (PosHelper val2 : this.impactPoints) {
               Vec3d vec = this.projectToScreen(
                  val2.pos
               );
               if (vec
                     != null
                  && vec.z
                     < 1.0) {
                  String text = String.format(
                           "%.1f",
                           val2.seconds
                        )
                        .replace(
                           (char)44,
                           (char)46
                        )
                     + " сек";
                  float width = font.getWidth(
                     text
                  );
                  float f = width
                     + 17.0F;
                  float f2 = 10.75F;
                  float x2 = (float)vec.x
                     - f
                        / 2.0F;
                  float y2 = (float)vec.y
                     - f2
                     - 4.0F;
                  Render2D.drawRoundedRect(
                     matrices,
                     x2,
                     y2,
                     f,
                     f2,
                     1.5F,
                     ColorUtil.rgba(0, 0, 0, 120)
                  );
                  Render2D.drawHudItem(
                     context,
                     val2.stack(),
                     x2
                        + 2.0F,
                     y2
                        + 1.4F,
                     0.5F,
                     100.0F
                  );
                  font.drawString(
                     matrices,
                     text,
                     x2
                        + 13.0F,
                     y2
                        + 2.0F,
                     ColorUtil.rgba(
                        255,
                        255,
                        255,
                        255
                     )
                  );
               }
            }
         }
      }
   }

   private void vertexLine(
      MatrixStack val,
      VertexConsumer val2,
      Vec3d val3,
      Vec3d val4,
      int val5
   ) {
      Entry val6 = val.peek();
      Vector3f vec3f = this.getNormal(
         val3,
         val4
      );
      val2.vertex(
            val6,
            val3.toVector3f()
         )
         .color(
            val5
         )
         .normal(
            val6,
            vec3f.x(),
            vec3f.y(),
            vec3f.z()
         );
      val2.vertex(
            val6,
            val4.toVector3f()
         )
         .color(
            val5
         )
         .normal(
            val6,
            vec3f.x(),
            vec3f.y(),
            vec3f.z()
         );
   }

   private Vector3f getNormal(
      Vec3d val,
      Vec3d val2
   ) {
      float f = (float)(
         val2.x
            - val.x
      );
      float f2 = (float)(
         val2.y
            - val.y
      );
      float f3 = (float)(
         val2.z
            - val.z
      );
      float f4 = MathHelper.sqrt(
         f
               * f
            + f2
               * f2
            + f3
               * f3
      );
      return f4
            <= 0.0F
         ? new Vector3f(0.0F, 1.0F, 0.0F)
         : new Vector3f(
            f
               / f4,
            f2
               / f4,
            f3
               / f4
         );
   }

   private Vec3d projectToScreen(
      Vec3d val
   ) {
      if (mc.player == null) {
         return null;
      } else {
         Vector3f vec3f = new Vector3f(
            (float)(
               val.x
                  - this.lastCameraPos.x
            ),
            (float)(
               val.y
                  - this.lastCameraPos.y
            ),
            (float)(
               val.z
                  - this.lastCameraPos.z
            )
         );
         vec3f.rotate(
            new Quaternionf(this.lastCameraRotation).conjugate()
         );
         Vector4f vec4f = new Vector4f(
            vec3f.x,
            vec3f.y,
            vec3f.z,
            1.0F
         );
         this.lastProjectionMatrix
            .transform(
               vec4f
            );
         if (vec4f.w()
            <= 1.0E-5F) {
            return null;
         } else {
            float f = 1.0F
               / vec4f.w();
            float f2 = (
                  vec4f.x()
                        * f
                     + 1.0F
               )
               * (float)mc.getWindow().getScaledWidth()
               / 2.0F;
            float f3 = (
                  1.0F
                     - vec4f.y()
                        * f
               )
               * (float)mc.getWindow().getScaledHeight()
               / 2.0F;
            float f4 = vec4f.z()
               * f;
            return !Float.isNaN(
                     f2
                  )
                  && !Float.isNaN(
                     f3
                  )
                  && !Float.isInfinite(
                     f2
                  )
                  && !Float.isInfinite(
                     f3
                  )
               ? new Vec3d(
                  (double)f2,
                  (double)f3,
                  (double)f4
               )
               : null;
         }
      }
   }
}
