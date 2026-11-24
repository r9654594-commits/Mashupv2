package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;



import dev.leg1tka.modules.combat.*;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ResetHelper_12 extends AuraRotation {
   private final Aura aura;
   private float wellmineSpeedAcceleration = 0.0F;
   private boolean wellmineBack = false;
   private int wellmineTargetId = -2147483648;
   private Vec3d lastWellmineAimPoint = null;
   private float wellmineFilteredYawStep = 0.0F;
   private float wellmineFilteredPitchStep = 0.0F;
   private float wellmineNoiseYaw = 0.0F;
   private float wellmineNoisePitch = 0.0F;

   public ResetHelper_12(
      Aura val
   ) {
      this.aura = val;
   }

   public void reset() {
      this.wellmineSpeedAcceleration = 0.0F;
      this.wellmineBack = false;
      this.wellmineTargetId = -2147483648;
      this.lastWellmineAimPoint = null;
      this.wellmineFilteredYawStep = 0.0F;
      this.wellmineFilteredPitchStep = 0.0F;
      this.wellmineNoiseYaw = 0.0F;
      this.wellmineNoisePitch = 0.0F;
      AuraTransition.INSTANCE.reset();
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null
         && entity
            != null) {
         if (!AuraTransition.INSTANCE.handleTransitions()) {
            if (entity.getId()
               != this.wellmineTargetId) {
               this.wellmineTargetId = entity.getId();
               this.wellmineSpeedAcceleration = 0.0F;
               this.wellmineBack = false;
               this.lastWellmineAimPoint = null;
               this.wellmineFilteredYawStep = 0.0F;
               this.wellmineFilteredPitchStep = 0.0F;
            }

            Vec3d vec = this.getAimPoint(
               entity
            );
            this.lastWellmineAimPoint = vec;
            Vec3d vec2 = vec.subtract(
               mc.player.getEyePos()
            );
            float yaw = (float)Math.toDegrees(
               Math.atan2(
                  -vec2.x,
                  vec2.z
               )
            );
            float yaw2 = (float)MathHelper.clamp(
               -Math.toDegrees(
                  Math.atan2(
                     vec2.y,
                     Math.hypot(
                        vec2.x,
                        vec2.z
                     )
                  )
               ),
               -90.0,
               90.0
            );
            float yaw3 = mc.player
               .getYaw();
            float yaw4 = mc.player
               .getPitch();
            if (!this.wellmineBack) {
               if (this.wellmineSpeedAcceleration >= 1.0F) {
                  this.wellmineSpeedAcceleration = 0.0F;
               } else {
                  float yaw5 = Math.abs(
                     MathHelper.wrapDegrees(
                        yaw
                           - yaw3
                     )
                  );
                  this.wellmineSpeedAcceleration += yaw5
                        > 40.0F
                     ? 0.0025F
                     : 0.005F;
               }

               boolean flag = RaytraceHelper.rayTraceSingleEntity(
                  yaw3,
                  yaw4,
                  6.0,
                  entity
               );
               if (this.wellmineSpeedAcceleration >= 0.18F
                  || flag
                  )
                {
                  this.wellmineBack = true;
               }
            } else {
               if (this.wellmineSpeedAcceleration >= -0.01F) {
                  float yaw6 = Math.abs(
                     MathHelper.wrapDegrees(
                        yaw
                           - yaw3
                     )
                  );
                  this.wellmineSpeedAcceleration -= yaw6
                        > 40.0F
                     ? 0.04F
                     : 0.01F;
               }

               if (this.wellmineSpeedAcceleration <= -0.01F) {
                  this.wellmineBack = false;
               }
            }

            float yaw7 = (float)ThreadLocalRandom.current()
               .nextDouble(-0.75, 0.75);
            float yaw8 = (float)ThreadLocalRandom.current()
               .nextDouble(-0.75, 0.75);
            yaw += yaw7;
            yaw2 += yaw8;
            float yaw9 = Math.min(
               Math.max(this.wellmineSpeedAcceleration, -1.0F), 1.0F
            );
            float yaw10 = MathHelper.wrapDegrees(
                  yaw
                     - yaw3
               )
               * yaw9;
            float yaw11 = (
                  yaw2
                     - yaw4
               )
               * (
                  yaw9
                     / 2.0F
               );
            float yaw12 = yaw3
               + yaw10;
            float yaw13 = MathHelper.clamp(
               yaw4
                  + yaw11,
               -89.0F,
               89.0F
            );
            float yaw14 = MouseGCD.getGCDValue();
            float yaw15 = MathHelper.wrapDegrees(
               yaw12
                  - yaw3
            );
            float yaw16 = yaw13
               - yaw4;
            yaw15 -= yaw15
               % yaw14;
            yaw16 -= yaw16
               % yaw14;
            Angle val = new Angle(
               yaw3
                  + yaw15,
               MathHelper.clamp(
                  yaw4
                     + yaw16,
                  -89.0F,
                  89.0F
               )
            );
            float[] floats = AuraTransition.INSTANCE
               .process(
                  val.getYaw(),
                  val.getPitch(),
                  entity,
                  "wellmine"
               );
            RotationStorage.update(
               new Angle(
                  floats[0],
                  floats[1]
               ),
               360.0F,
               360.0F,
               360.0F,
               360.0F,
               1,
               15,
               Aura.clientLook.isState()
            );
         }
      }
   }

   private Vec3d getAimPoint(
      LivingEntity entity
   ) {
      double dist = (double)mc.player
            .distanceTo(
               entity
            )
         + 2.5;
      Vec3d vec = RaycastUtil.getMultipoint(
         entity,
         Math.max(
            6.0,
            dist
         )
      );
      return vec
            != null
         ? vec
         : entity.getBoundingBox()
            .getCenter();
   }
}
