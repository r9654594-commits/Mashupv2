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
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Gappletracker extends Module {
   public static Gappletracker INSTANCE = new Gappletracker();
   private final BooleanSetting golden;
   private final BooleanSetting enchanted;
   private final NumberSetting iconSize;
   private final Map<String, ItemStack> eatingMap;
   private final Matrix4f lastProjMatrix;
   private final Quaternionf lastCameraRot;
   private Vec3d lastCameraPos;
   private float lastTickDelta;
   private int lastSW;
   private int lastSH;
   private boolean hasProjection;
   private final Vector3f projScratch;
   private final Vector4f clipScratch;

   public Gappletracker() {
      super("Gappletracker", Category.RENDER);
      String var1 = "GappleTracker";
      this.golden = new BooleanSetting("Золотое яблоко", true);
      this.enchanted = new BooleanSetting("Зачарованное яблоко", true);
      this.iconSize = new NumberSetting("Размер иконки", 12.0F, 6.0F, 24.0F, 0.5F);
      this.eatingMap = new HashMap<>();
      this.lastProjMatrix = new Matrix4f();
      this.lastCameraRot = new Quaternionf();
      this.lastCameraPos = Vec3d.ZERO;
      this.lastTickDelta = 1.0F;
      this.hasProjection = false;
      this.projScratch = new Vector3f();
      this.clipScratch = new Vector4f();
      this.addSettings(new Setting[]{
         this.golden,
         this.enchanted,
         this.iconSize
      });
   }

   @Override
   public void onDisable() {
      this.eatingMap.clear();
      this.hasProjection = false;
      super.onDisable();
   }

   @EventLink(
      priority = 100
   )
   public void onRender3D(
      Event3DRender val
   ) {
      this.hasProjection = true;
      this.lastProjMatrix
         .set(
            val.getProjectionMatrix()
         );
      this.lastCameraPos = val.getCamera()
         .getPos();
      this.lastCameraRot
         .set(
            val.getCamera()
               .getRotation()
         );
      this.lastTickDelta = val.getTickDelta();
      this.lastSW = mc.getWindow().getScaledWidth();
      this.lastSH = mc.getWindow().getScaledHeight();
   }

   @EventLink
   public void onRender2D(
      EventRender.Default val
   ) {
      if (this.hasProjection && mc.world != null && mc.player != null) {
         MatrixStack matrices = val.getContext()
            .getMatrices();
         float f = this.iconSize
            .get();
         int color = ColorUtil.getThemeColor();
         int color2 = ColorUtil.rgba(
            8,
            8,
            10,
            210
         );
         int color3 = ColorUtil.setAlphaColor(
            color,
            230
         );

         for (PlayerEntity player2 : mc.world
            .getPlayers()) {
            if (player2
                  != mc.player
               && player2.isAlive()
               )
             {
               ItemStack stack = null;
               if (player2.isUsingItem()
                  )
                {
                  ItemStack stack2 = player2.getActiveItem();
                  if (!stack2.isEmpty()
                     )
                   {
                     boolean flag = stack2.getItem()
                        == Items.GOLDEN_APPLE;
                     boolean flag2 = stack2.getItem()
                        == Items.ENCHANTED_GOLDEN_APPLE;
                     if (flag
                           && this.golden.isState()
                        || flag2
                           && this.enchanted.isState()) {
                        stack = stack2;
                     }
                  }
               }

               String str = player2.getUuidAsString();
               if (stack
                  == null) {
                  this.eatingMap
                     .remove(
                        str
                     );
               } else {
                  this.eatingMap
                     .put(
                        str,
                        stack
                     );
                  double d = MathHelper.lerp(
                     (double)this.lastTickDelta,
                     player2.prevX,
                     player2.getX()
                  );
                  double d2 = MathHelper.lerp(
                        (double)this.lastTickDelta,
                        player2.prevY,
                        player2.getY()
                     )
                     + (double)player2.getHeight()
                        * 0.4;
                  double d3 = MathHelper.lerp(
                     (double)this.lastTickDelta,
                     player2.prevZ,
                     player2.getZ()
                  );
                  float[] val2 = this.worldToScreen(
                     d,
                     d2,
                     d3
                  );
                  if (val2
                     != null) {
                     float f2 = val2[0];
                     float f3 = val2[1];
                     float f4 = f
                           * 0.5F
                        + 4.0F;
                     Render2D.drawRingArc(
                        matrices,
                        f2,
                        f3,
                        f4
                           + 2.5F,
                        3.5F,
                        0.0F,
                        360.0F,
                        color3
                     );
                     Render2D.drawRingArc(
                        matrices,
                        f2,
                        f3,
                        f4,
                        f4
                           - 0.5F,
                        0.0F,
                        360.0F,
                        ColorUtil.rgba(0, 0, 0, 200)
                     );

                     for (float f5 = 0.0F;
                        f5
                           < f4
                              - 1.0F;
                        f5 += 0.5F
                     ) {
                        Render2D.drawRingArc(
                           matrices,
                           f2,
                           f3,
                           f5
                              + 0.5F,
                           0.5F,
                           0.0F,
                           360.0F,
                           color2
                        );
                     }

                     float f6 = f
                        / 16.0F;
                     matrices.push();
                     matrices.translate(
                        f2
                           - f
                              * 0.5F,
                        f3
                           - f
                              * 0.5F,
                        0.0F
                     );
                     matrices.scale(
                        f6,
                        f6,
                        1.0F
                     );
                     RenderSystem.enableDepthTest();
                     val.getContext()
                        .drawItem(
                           stack,
                           0,
                           0
                        );
                     RenderSystem.disableDepthTest();
                     matrices.pop();
                  }
               }
            }
         }
      }
   }

   private float[] worldToScreen(
      double val,
      double val2,
      double val3
   ) {
      double d = val
         - this.lastCameraPos.x;
      double d2 = val2
         - this.lastCameraPos.y;
      double d3 = val3
         - this.lastCameraPos.z;
      Quaternionf quaternion = new Quaternionf(
            this.lastCameraRot
         )
         .conjugate();
      this.projScratch
         .set(
            (float)d,
            (float)d2,
            (float)d3
         );
      quaternion.transform(
         this.projScratch
      );
      this.clipScratch.set(this.projScratch.x, this.projScratch.y, this.projScratch.z, 1.0F);
      this.lastProjMatrix.transform(this.clipScratch);
      if (this.clipScratch.w <= 0.0F) {
         return null;
      } else {
         float f = this.clipScratch
               .x
            / this.clipScratch.w;
         float f2 = this.clipScratch
               .y
            / this.clipScratch.w;
         if (!(
               f
                  < -1.0F
            )
            && !(
               f
                  > 1.0F
            )
            && !(
               f2
                  < -1.0F
            )
            && !(
               f2
                  > 1.0F
            )) {
            float f3 = (
                  f
                     + 1.0F
               )
               * 0.5F
               * (float)this.lastSW;
            float f4 = (
                  1.0F
                     - f2
               )
               * 0.5F
               * (float)this.lastSH;
            float[] var10000 = new float[2];
            var10000[0] = f3;
            var10000[1] = f4;
            return var10000;
         } else {
            return null;
         }
      }
   }
}
