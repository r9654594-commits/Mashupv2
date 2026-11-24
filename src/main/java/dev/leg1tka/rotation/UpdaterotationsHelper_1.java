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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class UpdaterotationsHelper_1 extends AuraRotation {
   private static int ticksSinceLastJerk = 0;
   private static int nextJerkTick = 8;
   private static float yawSpeedMultiplier = 0.0F;
   private static float pitchSpeedMultiplier = 0.0F;
   private static float accelerationSpeed = 0.15F;
   private static float decelerationSpeed = 0.35F;
   private final Aura aura;

   public UpdaterotationsHelper_1(
      Aura val
   ) {
      this.aura = val;
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      Vec3d vec = entity.getPos()
         .add(
            0.0,
            (double)(
               entity.getHeight()
                  * 0.6F
            ),
            0.0
         );
      Angle val = new Angle(
         RayTraceUtil.getRotations(
               vec
            )
            .x,
         RayTraceUtil.getRotations(
               vec
            )
            .y
      );
      Angle val2 = new Angle(
         mc.player.getYaw(), mc.player.getPitch()
      );
      Angle val3 = this.process(
         val2,
         val,
         vec,
         entity
      );
      RotationStorage.update(
         val3,
         360.0F,
         360.0F,
         360.0F,
         360.0F,
         1,
         1,
         Aura.clientLook.isState()
      );
   }

   public Angle process(
      Angle val,
      Angle val2,
      Vec3d vec,
      Entity entity
   ) {
      Angle val3 = this.calculateDelta(
         val,
         val2
      );
      float yaw = val3.getYaw();
      float yaw2 = val3.getPitch();
      float yaw3 = this.aura
            .range
            .get()
         + 1.0F;
      boolean flag = entity
         != null;
      EntityHitResult val4 = ShouldblockhitcancelHelper.raytraceEntity(
         (double)yaw3,
         val,
         false
      );
      boolean flag2 = flag
         && val4
            != null
         && val4.getEntity()
            == entity;
      float yaw4 = 0.03F;
      if (Math.abs(
            yaw
         )
         > 15.0F) {
         yaw4 = DirectionHelper.randomInRange(
            0.06F, 0.32F
         );
         yaw *= 1.5F;
      }

      if (!flag2
         )
       {
         yawSpeedMultiplier = DirectionHelper.interpolate(
            yawSpeedMultiplier,
            1.0F,
            yaw4
         );
         pitchSpeedMultiplier = DirectionHelper.interpolate(
            pitchSpeedMultiplier,
            1.0F,
            yaw4
         );
      } else {
         float yaw5 = 0.55F;
         yawSpeedMultiplier = DirectionHelper.interpolate(
            yawSpeedMultiplier,
            0.0F,
            yaw5
         );
         pitchSpeedMultiplier = DirectionHelper.interpolate(
            pitchSpeedMultiplier,
            0.0F,
            yaw5
         );
      }

      float yaw6 = DirectionHelper.randomInRange(
            38.0F, 48.0F
         )
         / (mc.player.isGliding() ? 3.0F : 1.0F);
      float yaw7 = DirectionHelper.randomInRange(
            12.0F, 19.0F
         )
         / (mc.player.isGliding() ? 3.0F : 1.0F);
      float yaw8 = MathHelper.clamp(
         Math.abs(
               yaw
            )
            / 15.0F,
         0.2F,
         1.0F
      );
      float yaw9 = yawSpeedMultiplier
         * yawSpeedMultiplier
         * (3.0F - 2.0F * yawSpeedMultiplier);
      float yaw10 = pitchSpeedMultiplier
         * pitchSpeedMultiplier
         * (3.0F - 2.0F * pitchSpeedMultiplier);
      float yaw11 = yaw6
         * yaw9
         * yaw8;
      float yaw12 = yaw7
         * yaw10;
      if (Math.abs(
            yaw
         )
         > 5.0F) {
         float yaw13 = (float)Math.sin(
               (double)System.currentTimeMillis() / 20.0
            )
            * 5.0F;
         yaw += yaw13;
      }

      if (Math.abs(
            yaw2
         )
         > 3.0F) {
         float yaw14 = (float)Math.cos(
               (double)System.currentTimeMillis() / 20.0
            )
            * 155.5F;
         yaw2 += yaw14;
      }

      ticksSinceLastJerk++;
      if (ticksSinceLastJerk >= nextJerkTick) {
         boolean flag3 = Math.abs(
               yaw
            )
            > 15.0F;
         if (!flag3
            && flag2
            )
          {
            float yaw15 = DirectionHelper.randomInRange(
               0.4F, 0.8F
            );
            float yaw16 = DirectionHelper.randomInRange(
               0.5F, 0.9F
            );
            yaw *= yaw15;
            yaw2 *= yaw16;
         }

         ticksSinceLastJerk = 0;
         nextJerkTick = DirectionHelper.randomInRange(
            4, 10
         );
      }

      float yaw17 = MathHelper.clamp(
         yaw,
         -yaw11,
         yaw11
      );
      float yaw18 = MathHelper.clamp(
         yaw2,
         -yaw12,
         yaw12
      );
      if (flag2
         && Math.abs(
               yaw17
            )
            < 0.2F) {
         yaw17 = 0.0F;
      }

      return new Angle(
         val.getYaw()
            + yaw17,
         val.getPitch()
            + yaw18
      );
   }

   private Angle calculateDelta(
      Angle val,
      Angle val2
   ) {
      float yaw = MathHelper.wrapDegrees(
         val2.getYaw()
            - val.getYaw()
      );
      float yaw2 = val2.getPitch()
         - val.getPitch();
      return new Angle(
         yaw,
         yaw2
      );
   }
}
