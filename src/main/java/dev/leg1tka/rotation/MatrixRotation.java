package dev.leg1tka.rotation;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;



import dev.leg1tka.modules.combat.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MatrixRotation extends AuraRotation {
   private final Aura aura;
   private float acceleration;
   private boolean isBack;
   private LivingEntity lastTarget;
   private float lastYaw;
   private float lastPitch;
   private boolean initialized;

   public MatrixRotation(
      Aura val
   ) {
      this.aura = val;
   }

   public void reset() {
      this.acceleration = 0.0F;
      this.isBack = false;
      this.lastTarget = null;
      this.initialized = false;
      AuraTransition.INSTANCE.reset();
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (entity
            != null
         && mc.player != null) {
         if (!AuraTransition.INSTANCE.handleTransitions()) {
            if (!this.initialized) {
               this.lastYaw = mc.player.getYaw();
               this.lastPitch = mc.player.getPitch();
               this.initialized = true;
            }

            if (entity
               != this.lastTarget) {
               this.lastTarget = entity;
               this.acceleration = 0.0F;
               this.isBack = false;
            }

            Vec3d vec = this.getAimPoint(
               entity
            );
            Vec2f rot = RayTraceUtil.getRotations(
               vec
            );
            Angle val = new Angle(
               rot.x,
               rot.y
            );
            boolean flag = RaytraceHelper.rayTrace(
               mc.player.getRotationVector(),
               6.0,
               entity.getBoundingBox()
                  .expand(-0.12)
            );
            if (mc.player.isGliding()) {
               this.updateElytra(
                  val,
                  flag
               );
            } else {
               this.updateGround(
                  val,
                  flag,
                  entity
               );
            }
         }
      }
   }

   private void updateGround(
      Angle val,
      boolean flag,
      LivingEntity entity
   ) {
      float yaw = Math.abs(
         MathHelper.wrapDegrees(
            val.getYaw()
               - this.lastYaw
         )
      );
      double dist = (double)mc.player
         .distanceTo(
            entity
         );
      float yaw2 = 1.0F;
      if ((
            dist
                  < 0.7
               || yaw
                  > 85.0F
         )
         && yaw
            > 40.0F) {
         yaw2 = 0.5F;
      }

      if (!flag
         )
       {
         float yaw3 = MathHelper.clamp(
            yaw
               / 20.0F,
            0.6F,
            1.0F
         );
         this.acceleration += 0.0045F
            * yaw3;
         if (this.acceleration >= 0.16F) {
            this.isBack = true;
         }
      } else if (this.isBack) {
         this.acceleration -= 0.0095F;
         if (this.acceleration <= 0.0F) {
            this.isBack = false;
            this.acceleration = 0.0F;
         }
      } else {
         this.acceleration = MathHelper.clamp(this.acceleration - 0.0065F, 0.08F, 0.16F);
      }

      float yaw4 = MathHelper.clamp(
         this.acceleration, 0.14F, 0.8F
      );
      if (flag
         )
       {
         yaw4 = MathHelper.clamp(
            yaw4
               * 0.48F,
            0.1F,
            0.38F
         );
      }

      if (mc.player.hurtTime > 0) {
         yaw4 *= 0.75F;
      }

      boolean flag2 = Math.abs(
            mc.player.input.movementSideways
         )
         > 0.08F;
      if (!flag
         && flag2
         )
       {
         yaw4 = MathHelper.clamp(
            yaw4
               * 1.3F,
            yaw4,
            0.58F
         );
      }

      yaw4 *= yaw2;
      float yaw5 = MathHelper.wrapDegrees(
         val.getYaw()
            - this.lastYaw
      );
      float yaw6 = val.getPitch()
         - this.lastPitch;
      float yaw7 = 0.68F;
      float yaw8 = (float)Math.floor(
         (double)(
            Math.abs(
                  yaw6
               )
               / yaw7
         )
      );
      float yaw9 = yaw8
         * yaw7
         * Math.signum(
            yaw6
         );
      float yaw10 = this.lastYaw
         + yaw5
            * yaw4;
      float yaw11 = this.lastPitch
         + yaw9
            * yaw4;
      if (!mc.player.isOnGround()) {
         yaw11 = this.lastPitch
            + (
                  yaw11
                     - this.lastPitch
               )
               * 0.45F;
      }

      float yaw12 = MouseGCD.getGCDValue();
      yaw10 -= (
            yaw10
               - this.lastYaw
         )
         % yaw12;
      yaw11 -= (
            yaw11
               - this.lastPitch
         )
         % yaw12;
      yaw11 = MathHelper.clamp(
         yaw11,
         -89.0F,
         89.0F
      );
      this.applyRotation(
         yaw10,
         yaw11,
         flag,
         yaw,
         yaw2
      );
   }

   private void updateElytra(
      Angle val,
      boolean flag
   ) {
      float yaw = Math.abs(
         MathHelper.wrapDegrees(
            val.getYaw()
               - this.lastYaw
         )
      );
      if (!flag
         )
       {
         this.acceleration += yaw
               > 45.0F
            ? 0.003F
            : 0.0055F;
      } else if (this.isBack) {
         this.acceleration -= yaw
               > 45.0F
            ? 0.035F
            : 0.009F;
         if (this.acceleration <= 0.0F) {
            this.isBack = false;
            this.acceleration = 0.0F;
         }
      } else {
         this.acceleration = MathHelper.clamp(this.acceleration - 0.005F, 0.06F, 0.2F);
         if (this.acceleration >= 0.18F) {
            this.isBack = true;
         }
      }

      float yaw2 = MathHelper.clamp(
         this.acceleration, 0.1F, 0.85F
      );
      if (flag
         )
       {
         yaw2 *= 0.55F;
      }

      float yaw3 = MathHelper.wrapDegrees(
         val.getYaw()
            - this.lastYaw
      );
      float yaw4 = val.getPitch()
         - this.lastPitch;
      float yaw5 = this.lastYaw
         + yaw3
            * yaw2;
      float yaw6 = this.lastPitch
         + yaw4
            * yaw2
            * 0.55F;
      float yaw7 = MouseGCD.getGCDValue();
      yaw5 -= (
            yaw5
               - this.lastYaw
         )
         % yaw7;
      yaw6 -= (
            yaw6
               - this.lastPitch
         )
         % yaw7;
      yaw6 = MathHelper.clamp(
         yaw6,
         -89.0F,
         89.0F
      );
      float yaw8 = flag
         ? 55.0F
         : MathHelper.clamp(
            75.0F
               + yaw
                  * 0.4F,
            75.0F,
            120.0F
         );
      this.sendRotation(
         yaw5,
         yaw6,
         yaw8,
         yaw8
      );
   }

   private void applyRotation(
      float f,
      float f2,
      boolean flag,
      float f3,
      float f4
   ) {
      float y = flag
         ? 52.0F
         : MathHelper.clamp(
            68.0F
               + f3
                  * 0.5F,
            68.0F,
            108.0F
         );
      if (f4
         < 1.0F) {
         y *= 0.7F;
      }

      this.sendRotation(
         f,
         f2,
         y,
         y
      );
   }

   private void sendRotation(
      float f,
      float f2,
      float f3,
      float f4
   ) {
      float[] floats = AuraTransition.INSTANCE
         .process(
            f,
            f2,
            this.lastTarget,
            "holyworld"
         );
      RotationStorage.update(
         new Angle(
            floats[0],
            floats[1]
         ),
         f3,
         f4,
         f3,
         f4,
         0,
         1,
         Aura.clientLook.isState()
      );
      this.lastYaw = floats[0];
      this.lastPitch = floats[1];
      this.rotate = new Vec2f(
         floats[0],
         floats[1]
      );
   }

   private Vec3d getAimPoint(
      LivingEntity entity
   ) {
      Vec3d vec = RaycastUtil.getMultipoint(
         entity,
         128.0
      );
      if (vec
         == null) {
         vec = entity.getBoundingBox()
            .getCenter();
      }

      return this.shouldUseElytraPredict(
            entity
         )
         ? getPredictedPoint(
            entity,
            vec
         )
         : vec;
   }
}
