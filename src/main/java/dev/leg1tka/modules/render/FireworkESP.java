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

import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventRender;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FireworkESP extends Module {
   public static FireworkESP INSTANCE = new FireworkESP();
   private final NumberSetting interval;
   private final NumberSetting lifetime;
   private final Matrix4f lastProjectionMatrix;
   private final Quaternionf lastCameraRotation;
   private Vec3d lastCameraPos;
   private float lastTickDelta;
   private final Map<Integer, LastspawntimeHolder> fireworks;

   public FireworkESP() {
      super("FireworkESP", Category.RENDER);
      String var1 = "FireworkESP";
      this.interval = new NumberSetting(
         "Интервал (мс)", 100.0F, 10.0F, 1000.0F, 10.0F
      );
      this.lifetime = new NumberSetting(
         "Время жизни (мс)", 1000.0F, 100.0F, 5000.0F, 100.0F
      );
      this.lastProjectionMatrix = new Matrix4f();
      this.lastCameraRotation = new Quaternionf();
      this.lastCameraPos = Vec3d.ZERO;
      this.fireworks = new HashMap<>();
      this.addSettings(new Setting[]{
         this.interval,
         this.lifetime
      });
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.fireworks.clear();
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
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
      this.lastTickDelta = val.getTickDelta();
      if (mc.world != null) {
         long val2 = System.currentTimeMillis();
         this.fireworks
            .entrySet()
            .removeIf(
               val3 -> {
                  Entity entity2 = mc.world
                     .getEntityById(
                        val3.getKey()
                     );
                  boolean flag = entity2
                        == null
                     || !entity2.isAlive();
                  val3.getValue()
                     .points
                     .removeIf(
                        val4 -> (float)(
                                 val2
                                    - val4.timestamp
                              )
                              > this.lifetime.get()
                     );
                  return flag
                     && val3.getValue()
                        .points
                        .isEmpty();
               }
            );

         for (Entity entity3 : mc.world
            .getEntities()) {
            if (entity3 instanceof FireworkRocketEntity
               )
             {
               FireworkRocketEntity entity4 = (FireworkRocketEntity)entity3;
               if (entity3.isAlive()
                  )
                {
                  LastspawntimeHolder val5 = this.fireworks
                     .computeIfAbsent(
                        entity3.getId(),
                        val6 -> new LastspawntimeHolder()
                     );
                  if (!val5.ownerResolved
                     )
                   {
                     val5.isFriend = this.resolveIsFriend(
                        entity4
                     );
                     val5.ownerResolved = true;
                  }

                  if ((float)(
                        val2
                           - val5.lastSpawnTime
                     )
                     >= this.interval.get()) {
                     Vec3d vec = new Vec3d(
                        MathHelper.lerp(
                           (double)this.lastTickDelta,
                           entity3.lastRenderX,
                           entity3.getX()
                        ),
                        MathHelper.lerp(
                              (double)this.lastTickDelta,
                              entity3.lastRenderY,
                              entity3.getY()
                           )
                           + 0.5,
                        MathHelper.lerp(
                           (double)this.lastTickDelta,
                           entity3.lastRenderZ,
                           entity3.getZ()
                        )
                     );
                     float f = (float)entity3.age
                        / 20.0F;
                     val5.points
                        .add(
                           new PosHolder(
                              vec,
                              val2,
                              f
                           )
                        );
                     val5.lastSpawnTime = val2;
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onRender2D(
      EventRender.Default val
   ) {
      if (mc.player != null && mc.world != null) {
         MatrixStack matrices = val.getContext()
            .getMatrices();
         ItemStack stack = new ItemStack(
            Items.FIREWORK_ROCKET
         );
         long val2 = System.currentTimeMillis();
         boolean flag = Render2D.isLiquidGlassHudActive();
         if (flag
            )
          {
            IsframeactiveHelper.INSTANCE.beginFrame();
         }

         try {
            for (Entry<Integer, LastspawntimeHolder> val3 : this.fireworks
               .entrySet()) {
               LastspawntimeHolder val4 = val3.getValue();

               for (PosHolder val5 : val4.points) {
                  Vec3d vec = this.worldToScreen(
                     val5.pos
                  );
                  if (vec
                     != null) {
                     float f = 1.0F
                        - (float)(
                              val2
                                 - val5.timestamp
                           )
                           / this.lifetime.get();
                     f = MathHelper.clamp(
                        f,
                        0.0F,
                        1.0F
                     );
                     this.renderIconRect(
                        val,
                        matrices,
                        stack,
                        vec,
                        f,
                        val4.isFriend
                     );
                  }
               }

               Entity entity2 = mc.world
                  .getEntityById(
                     val3.getKey()
                  );
               if (entity2 instanceof FireworkRocketEntity
                  && entity2.isAlive()
                  )
                {
                  Vec3d vec2 = new Vec3d(
                     MathHelper.lerp(
                        (double)this.lastTickDelta,
                        entity2.lastRenderX,
                        entity2.getX()
                     ),
                     MathHelper.lerp(
                           (double)this.lastTickDelta,
                           entity2.lastRenderY,
                           entity2.getY()
                        )
                        + 0.5,
                     MathHelper.lerp(
                        (double)this.lastTickDelta,
                        entity2.lastRenderZ,
                        entity2.getZ()
                     )
                  );
                  Vec3d vec3 = this.worldToScreen(
                     vec2
                  );
                  if (vec3
                     != null) {
                     this.renderIconRect(
                        val,
                        matrices,
                        stack,
                        vec3,
                        1.0F,
                        val4.isFriend
                     );
                  }
               }
            }
         } finally {
            if (flag
               )
             {
               IsframeactiveHelper.INSTANCE.finishFrame();
            }
         }
      }
   }

   private void renderIconRect(
      EventRender.Default val,
      MatrixStack val2,
      ItemStack val3,
      Vec3d val4,
      float val5,
      boolean val6
   ) {
      float f = 0.75F;
      float f2 = 16.0F
         * f;
      float f3 = 4.0F;
      float f4 = f2
         + f3
            * 2.0F;
      float f5 = 4.0F;
      float f6 = 0.45F
         + 0.55F
            * val5;
      int n = (int)(
         220.0F
            * val5
      );
      if (n
         > 5) {
         int n2 = val6
            ? n
                  << 24
               | 1722906
            : n
                  << 24
               | 657930;
         val2.push();
         val2.translate(
            val4.x,
            val4.y,
            0.0
         );
         val2.scale(
            f6,
            f6,
            1.0F
         );
         Render2D.drawRoundedRect(
            val2,
            -f4
               * 0.5F,
            -f4
               * 0.5F,
            f4,
            f4,
            f5,
            n2
         );
         if (val6
            )
          {
            Render2D.drawRoundedRectOutline(
               val2,
               -f4
                  * 0.5F,
               -f4
                  * 0.5F,
               f4,
               f4,
               f5,
               f5,
               f5,
               f5,
               1.0F,
               n
                     << 24
                  | 5635925,
               n
                     << 24
                  | 5635925,
               n
                     << 24
                  | 5635925,
               n
                     << 24
                  | 5635925
            );
         }

         val2.push();
         val2.translate(
            -f2
               * 0.5F,
            -f2
               * 0.5F,
            0.0F
         );
         val2.scale(
            f,
            f,
            1.0F
         );
         val.getContext()
            .drawItem(
               val3,
               0,
               0
            );
         val2.pop();
         val2.pop();
      }
   }

   private boolean resolveIsFriend(
      FireworkRocketEntity val
   ) {
      if (mc.world != null && AspectDLC.INSTANCE.friendStorage != null) {
         Vec3d pos2 = val.getPos();
         double d = 64.0;

         for (PlayerEntity player2 : mc.world
            .getPlayers()) {
            if (player2
               != mc.player) {
               double dist = player2.squaredDistanceTo(
                  pos2.x,
                  pos2.y,
                  pos2.z
               );
               if (dist
                  < d
                  )
                {
                  d = dist;
                  if (AspectDLC.INSTANCE
                     .friendStorage
                     .isFriend(
                        player2.getName()
                           .getString()
                     )) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private Vec3d worldToScreen(
      Vec3d val
   ) {
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
      float f = vec4f.w;
      if (f
         <= 1.0E-5F) {
         return null;
      } else {
         float x2 = vec4f.x
            / f;
         float y2 = vec4f.y
            / f;
         float f2 = vec4f.z
            / f;
         float f3 = (
               x2
                     * 0.5F
                  + 0.5F
            )
            * (float)mc.getWindow().getScaledWidth();
         float f4 = (
               1.0F
                  - (
                     y2
                           * 0.5F
                        + 0.5F
                  )
            )
            * (float)mc.getWindow().getScaledHeight();
         return !Float.isNaN(
                  f3
               )
               && !Float.isNaN(
                  f4
               )
               && !Float.isInfinite(
                  f3
               )
               && !Float.isInfinite(
                  f4
               )
            ? new Vec3d(
               (double)f3,
               (double)f4,
               (double)f2
            )
            : null;
      }
   }
}
