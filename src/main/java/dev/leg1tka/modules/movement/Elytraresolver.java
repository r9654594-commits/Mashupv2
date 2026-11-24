package dev.leg1tka.modules.movement;


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

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class Elytraresolver extends Module {
   public static Elytraresolver INSTANCE = new Elytraresolver();
   private final NumberSetting distance = new NumberSetting(
      "Дистанция отлета",
      6.0F,
      4.0F,
      8.0F,
      0.1F
   );
   private final BooleanSetting autoFirework = new BooleanSetting(
      "Авто-Фейерверк",
      true
   );
   private static final float MIN_HEIGHT = 4.0F;
   private boolean escaping;
   private Vec3d escapePos;
   private long escapeStartTime;
   private int returnFireworkTicks = -1;
   private Vec3d lastEscapeDirection;

   public Elytraresolver() {
      super(
         "ElytraResolver",
         "Отлет на элитрах",
         Category.MOVEMENT
      );
      this.addSettings(new Setting[]{
         this.distance,
         this.autoFirework
      });
      INSTANCE = this;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.escaping = false;
      this.escapePos = null;
      this.returnFireworkTicks = -1;
      this.lastEscapeDirection = null;
   }

   public void onAuraAttack() {
      if (this.isEnable() && mc.player != null && mc.player.isGliding()) {
         Vec3d vec = this.calculateSmartEscape(
            mc.player.getPos(), this.distance.get()
         );
         if (vec
            != null) {
            this.escapePos = vec;
            this.escaping = true;
            this.escapeStartTime = System.currentTimeMillis();
            if (this.autoFirework.isState()) {
               this.useFirework();
            }
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val6
   ) {
      if (mc.player != null && mc.world != null && mc.player.isGliding()) {
         if (this.returnFireworkTicks > 0) {
            this.returnFireworkTicks--;
         } else if (this.returnFireworkTicks == 0) {
            if (this.autoFirework.isState()) {
               this.useFirework();
            }

            this.returnFireworkTicks = -1;
         }

         if (this.escaping && this.escapePos != null) {
            double d = mc.player
               .getPos()
               .distanceTo(this.escapePos);
            if (d
                  < 2.0
               || System.currentTimeMillis() - this.escapeStartTime > 1000L) {
               this.escaping = false;
               if (this.autoFirework.isState()) {
                  this.returnFireworkTicks = 2;
               }
            }
         }
      } else {
         this.escaping = false;
         this.returnFireworkTicks = -1;
      }
   }

   public boolean isEscaping() {
      return this.isEnable() && this.escaping && this.escapePos != null && mc.player != null && mc.player.isGliding();
   }

   public Vec3d getEscapePos() {
      return this.escapePos;
   }

   private Vec3d calculateSmartEscape(
      Vec3d val,
      float val2
   ) {
      Vec3d vec = mc.player
         .getRotationVector();
      Vec3d vec2 = mc.player
         .getVelocity();
      Vec3d[] vec3 = this.generateSmartDirections(
         vec,
         vec2
      );
      List<PosHolder_1> list = new ArrayList<>();

      for (Vec3d vec4 : vec3) {
         Vec3d vec5 = val.add(
            vec4.multiply(
               (double)val2
            )
         );
         if (!(
            vec5.y
               < val.y
                  + 4.0
         )) {
            RaycastContext val3 = new RaycastContext(
               val,
               vec5,
               ShapeType.COLLIDER,
               FluidHandling.NONE,
               mc.player
            );
            BlockHitResult blockHit = mc.world
               .raycast(
                  val3
               );
            double d = (double)val2;
            Vec3d vec6 = vec5;
            if (blockHit.getType()
               != Type.MISS) {
               double d2 = blockHit.getPos()
                  .distanceTo(
                     val
                  );
               if (!(
                  d2
                     > 2.0
               )) {
                  continue;
               }

               d = d2;
               vec6 = blockHit.getPos()
                  .add(
                     vec4.multiply(
                        -1.0
                     )
                  );
            }

            double d3 = this.calculateEscapeScore(
               vec4,
               vec,
               vec2,
               d,
               vec6
            );
            list.add(
               new PosHolder_1(
                  vec6,
                  d,
                  d3
               )
            );
         }
      }

      if (list.isEmpty()
         )
       {
         return null;
      } else {
         list.sort(
            Comparator.comparingDouble(
               val4 -> -val4.score
            )
         );
         this.lastEscapeDirection = list.get(
               0
            )
            .pos
            .subtract(
               val
            )
            .normalize();
         return list.get(
               0
            )
            .pos;
      }
   }

   private Vec3d[] generateSmartDirections(
      Vec3d val,
      Vec3d val2
   ) {
      Vec3d vec = new Vec3d(
            -val.x,
            0.0,
            -val.z
         )
         .normalize();
      Vec3d vec2 = new Vec3d(
            -val.z,
            0.0,
            val.x
         )
         .normalize();
      Vec3d vec3 = vec2.multiply(
         -1.0
      );
      Vec3d vec4 = new Vec3d(
         0.0, 1.0, 0.0
      );
      List<Vec3d> vec5 = new ArrayList<>();
      vec5.add(
         vec.add(
               vec4
            )
            .normalize()
      );
      vec5.add(
         vec.add(
               vec2
            )
            .add(
               vec4
            )
            .normalize()
      );
      vec5.add(
         vec.add(
               vec3
            )
            .add(
               vec4
            )
            .normalize()
      );
      vec5.add(
         vec2.add(
               vec4
            )
            .normalize()
      );
      vec5.add(
         vec3.add(
               vec4
            )
            .normalize()
      );
      vec5.add(
         vec.add(
               vec2.multiply(
                  0.5
               )
            )
            .add(
               vec4.multiply(
                  1.5
               )
            )
            .normalize()
      );
      vec5.add(
         vec.add(
               vec3.multiply(
                  0.5
               )
            )
            .add(
               vec4.multiply(
                  1.5
               )
            )
            .normalize()
      );
      vec5.add(
         vec.add(
               vec4.multiply(
                  2.0
               )
            )
            .normalize()
      );
      vec5.add(
         vec2.add(
               vec4.multiply(
                  1.5
               )
            )
            .normalize()
      );
      vec5.add(
         vec3.add(
               vec4.multiply(
                  1.5
               )
            )
            .normalize()
      );
      vec5.add(
         vec.multiply(
               0.7
            )
            .add(
               vec2.multiply(
                  0.3
               )
            )
            .add(
               vec4.multiply(
                  1.2
               )
            )
            .normalize()
      );
      vec5.add(
         vec.multiply(
               0.7
            )
            .add(
               vec3.multiply(
                  0.3
               )
            )
            .add(
               vec4.multiply(
                  1.2
               )
            )
            .normalize()
      );
      vec5.add(
         vec.multiply(
               0.5
            )
            .add(
               vec4.multiply(
                  1.8
               )
            )
            .normalize()
      );
      vec5.add(
         vec2.multiply(
               0.8
            )
            .add(
               vec4.multiply(
                  1.3
               )
            )
            .normalize()
      );
      vec5.add(
         vec3.multiply(
               0.8
            )
            .add(
               vec4.multiply(
                  1.3
               )
            )
            .normalize()
      );
      if (val2.lengthSquared()
         > 0.01) {
         Vec3d vec6 = new Vec3d(
               -val2.z,
               0.0,
               val2.x
            )
            .normalize();
         vec5.add(
            vec6.add(
                  vec4
               )
               .normalize()
         );
         vec5.add(
            vec6.multiply(
                  -1.0
               )
               .add(
                  vec4
               )
               .normalize()
         );
         vec5.add(
            vec6.add(
                  vec4.multiply(
                     1.5
                  )
               )
               .normalize()
         );
         vec5.add(
            vec6.multiply(
                  -1.0
               )
               .add(
                  vec4.multiply(
                     1.5
                  )
               )
               .normalize()
         );
      }

      return vec5.toArray(
         new Vec3d[0]
      );
   }

   private double calculateEscapeScore(
      Vec3d val,
      Vec3d val2,
      Vec3d val3,
      double val4,
      Vec3d val5
   ) {
      double d = 0.0;
      double d2 = -val.dotProduct(
         new Vec3d(
               val2.x,
               0.0,
               val2.z
            )
            .normalize()
      );
      d += d2
         * 30.0;
      d += val.y
         * 25.0;
      d += val4
         * 2.0;
      if (val3.lengthSquared()
         > 0.01) {
         Vec3d vec = val3.normalize();
         double d3 = Math.abs(
            val.dotProduct(
               new Vec3d(
                  -vec.z,
                  0.0,
                  vec.x
               )
            )
         );
         d += d3
            * 15.0;
      }

      if (this.lastEscapeDirection != null) {
         double d4 = val.dotProduct(
            this.lastEscapeDirection
         );
         if (d4
            > 0.7) {
            d -= 20.0;
         }
      }

      double d5 = val5.y
         - (double)mc.world.getBottomY();
      if (d5
         < 10.0) {
         d -= (
               10.0
                  - d5
            )
            * 5.0;
      }

      return d;
   }

   private void useFirework() {
      if (mc.player != null) {
         int slot = SearchitemHelper.getItemSlot(
            Items.FIREWORK_ROCKET
         );
         if (slot
            != -1) {
            SearchitemHelper.swapAndUseHvH(Items.FIREWORK_ROCKET);
         }
      }
   }
}
