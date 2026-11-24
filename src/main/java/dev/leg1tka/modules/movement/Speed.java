package dev.leg1tka.modules.movement;



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
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class Speed extends Module implements IMinecraft {
   public static Speed INSTANCE = new Speed();
   private final ModeSetting mod = new ModeSetting(
      "Режим", "Entity: grim", "Entity: grim"
   );
   private final NumberSetting spd = new NumberSetting("Скорость", 1.05F, 1.0F, 1.6F, 0.1F);
   private final NumberSetting rad = new NumberSetting("Радиус", 1.0F, 0.01F, 3.0F, 0.1F);
   private final NumberSetting prd = new NumberSetting("Предикт", 1.0F, 0.0F, 5.0F, 0.1F);
   private final BooleanSetting shw = new BooleanSetting("Показать предикт", true);
   private final ModeSetting shd;
   private final BooleanSetting ely;
   private final HasmaskHelper ovl;
   private Box sbox;
   private LivingEntity stgt;
   private int th1;
   private int th2;

   public Speed() {
      super("Speed", "Ускорение при столкновении с целью", Category.MOVEMENT);
      this.shd = new ModeSetting("Шейдер", "Красивый", new String[]{"Красивый", "Обычный"}).visible(this.shw::isState);
      this.ely = new BooleanSetting("Только на элитре", false);
      this.ovl = new HasmaskHelper();
      this.th1 = -1;
      this.th2 = -1;
      this.addSettings(new Setting[]{
         this.mod,
         this.spd,
         this.rad,
         this.prd,
         this.shw,
         this.shd,
         this.ely
      });
   }

   @EventLink
   private void onUpdate(
      EventUpdate val4
   ) {
      if (mc.player != null && mc.world != null) {
         this.collisionSpeed();
      }
   }

   private void collisionSpeed() {
      Aura val = ModuleManager.aura;
      if (val
            != null
         && val.isEnable()
         )
       {
         LivingEntity entity2 = val.getTarget();
         if (entity2
               != null
            && entity2
               != mc.player) {
            if (!this.ely.isState() || mc.player.isGliding()) {
               Box box = mc.player
                  .getBoundingBox()
                  .expand(this.rad.getValue().doubleValue());
               boolean flag = false;
               if (mc.player.isGliding()
                  || entity2.getBoundingBox()
                     .intersects(
                        box
                     )) {
                  if (mc.player.isGliding()) {
                     Vec3d pos = this.getPredictedPos(
                        entity2
                     );
                     double d = mc.player
                        .getEyePos()
                        .distanceTo(
                           pos
                        );
                     double d2 = mc.player
                        .getEyePos()
                        .distanceTo(
                           entity2.getBoundingBox()
                              .getCenter()
                        );
                     if (d
                           <= 2.5
                        || d2
                           <= 2.5) {
                        flag = true;
                     }
                  } else {
                     flag = true;
                  }
               }

               if (flag
                  )
                {
                  Vec3d velocity = this.calculateVelocity(
                     entity2
                  );
                  mc.player
                     .setVelocity(
                        velocity
                     );
               }
            }
         }
      }
   }

   @NotNull
   private Vec3d calculateVelocity(
      LivingEntity val
   ) {
      Vec3d pos = this.getPredictedPos(
         val
      );
      double d = pos.x
         - mc.player.getX();
      double d2 = pos.z
         - mc.player.getZ();
      float f = (float)(
         Math.toDegrees(
               Math.atan2(
                  d2,
                  d
               )
            )
            - 90.0
      );
      double d3 = Math.toRadians(
         (double)f
      );
      double d4 = 0.072
         * this.spd.getValue().doubleValue();
      Vec3d vec = mc.player
         .getVelocity();
      return new Vec3d(
         vec.x
            + -Math.sin(
                  d3
               )
               * d4,
         vec.y,
         vec.z
            + Math.cos(
                  d3
               )
               * d4
      );
   }

   private Vec3d getPredictedPos(
      LivingEntity val
   ) {
      double d = this.prd
         .getValue()
         .doubleValue();
      if (d
         <= 0.001) {
         return val.getPos();
      } else if (mc.player != null
         && mc.player.isGliding()
         && val.isGliding()
         )
       {
         Vec3d pos = GetsmartpredictedpositionHelper.predict(
            val,
            val.getPos(),
            (int)Math.round(
               d
            )
         );
         return new Vec3d(
            pos.x,
            mc.player.getY(),
            pos.z
         );
      } else {
         double d2 = val.getX()
            - val.prevX;
         double d3 = val.getZ()
            - val.prevZ;
         return val.getPos()
            .add(
               d2
                  * d,
               0.0,
               d3
                  * d
            );
      }
   }

   private Box buildPredictedBox(
      LivingEntity val
   ) {
      Box box = val.getBoundingBox();
      double d = this.prd
         .getValue()
         .doubleValue();
      if (d
         <= 0.001) {
         return box;
      } else if (mc.player != null
         && mc.player.isGliding()
         && val.isGliding()
         )
       {
         float f = 1.0F;
         if (mc.getRenderTickCounter() != null) {
            f = mc.getRenderTickCounter()
               .getTickDelta(true);
         }

         Vec3d pos = GetsmartpredictedpositionHelper.getElytraAimPointRender(
            val,
            f
         );
         return box.offset(
            pos.subtract(
               box.getCenter()
            )
         );
      } else {
         double d2 = val.getX()
            - val.prevX;
         double d3 = val.getZ()
            - val.prevZ;
         return box.offset(
            d2
               * d,
            0.0,
            d3
               * d
         );
      }
   }

   private Box smoothBox(
      LivingEntity val,
      Box val2
   ) {
      if (this.sbox != null
         && this.stgt
            == val
         && !(
            this.sbox
                  .getCenter()
                  .squaredDistanceTo(
                     val2.getCenter()
                  )
               > 144.0
         )) {
         double d = Math.sqrt(
            this.sbox
               .getCenter()
               .squaredDistanceTo(
                  val2.getCenter()
               )
         );
         double d2 = MathHelper.clamp(
            0.12
               + d
                  * 0.04,
            0.12,
            0.35
         );
         this.sbox = this.lerpBox(
            this.sbox,
            val2,
            d2
         );
         return this.sbox;
      } else {
         this.sbox = val2;
         this.stgt = val;
         return val2;
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

   @EventLink(
      priority = -100
   )
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.player != null && mc.world != null && this.shw.isState()) {
         Aura val2 = ModuleManager.aura;
         if (val2
               != null
            && val2.isEnable()
            )
          {
            LivingEntity entity2 = val2.getTarget();
            if (entity2
                  != null
               && entity2
                  != mc.player
               && entity2.isAlive()
               )
             {
               Box box = this.buildPredictedBox(
                  entity2
               );
               Box box2 = this.smoothBox(
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
               if (this.shd.is("Красивый")) {
                  this.updateColors();
                  this.ovl
                     .writeMask(
                        matrix,
                        box3
                     );
               } else {
                  this.ovl.clearMask();
                  this.renderNormalBox(
                     matrix,
                     box3
                  );
               }
            } else {
               this.resetState();
            }
         } else {
            this.resetState();
         }
      } else {
         this.resetState();
      }
   }

   @EventLink(
      priority = 200
   )
   public void onRender2D(
      EventRender.Default val4
   ) {
      if (this.ovl.hasMask() && this.shw.isState() && this.shd.is("Красивый")) {
         int color2 = ColorUtil.interpolateColor(
            this.th1, -1, 0.38F
         );
         int color3 = ColorUtil.interpolateColor(
            this.th2, -1, 0.42F
         );
         this.ovl
            .renderOverlay(
               Color1Helper.predict(
                  color2,
                  color3
               )
            );
      }
   }

   private void renderNormalBox(
      Matrix4f val,
      Box val2
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(1.5F);
      int n = -520093697;
      int n2 = n
            >> 16
         & 255;
      int n3 = n
            >> 8
         & 255;
      int n4 = n
         & 255;
      int n5 = n
            >> 24
         & 255;
      float x2 = (float)val2.minX;
      float y2 = (float)val2.minY;
      float f = (float)val2.minZ;
      float x3 = (float)val2.maxX;
      float x4 = (float)val2.maxY;
      float x5 = (float)val2.maxZ;
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      buffer.vertex(
            val,
            x2,
            y2,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            y2,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            y2,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            y2,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            y2,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            y2,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            y2,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            y2,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            x4,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            x4,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            x4,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            x4,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            x4,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            x4,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            x4,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            x4,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            y2,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            x4,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            y2,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            x4,
            f
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            y2,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x3,
            x4,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            y2,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
         );
      buffer.vertex(
            val,
            x2,
            x4,
            x5
         )
         .color(
            n2,
            n3,
            n4,
            n5
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

   private void updateColors() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null) {
         ThemeColors val = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         if (val
            == null) {
            this.th1 = ColorUtil.getThemeColor(0);
            this.th2 = ColorUtil.getThemeColor(180);
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
               this.th1 = color2;
               this.th2 = color2;
            } else {
               this.th1 = ColorUtil.getThemeColor();
               this.th2 = ColorUtil.getThemeColor(180);
            }
         }
      } else {
         this.th1 = ColorUtil.getThemeColor(0);
         this.th2 = ColorUtil.getThemeColor(180);
      }
   }

   private void resetState() {
      this.sbox = null;
      this.stgt = null;
      this.ovl.clearMask();
   }

   @Override
   public void onDisable() {
      this.resetState();
      this.ovl.destroy();
      super.onDisable();
   }
}
