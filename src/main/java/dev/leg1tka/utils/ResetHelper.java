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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ResetHelper extends AuraRotation {
   private final Aura aura;
   private final FinishedHelper strikeTimer = new FinishedHelper();
   private float lastYaw;
   private float lastPitch;

   public ResetHelper(
      Aura val
   ) {
      this.aura = val;
   }

   public void reset() {
      this.strikeTimer.reset();
      AuraTransition.INSTANCE.reset();
      if (mc.player != null) {
         this.lastYaw = mc.player.getYaw();
         this.lastPitch = mc.player.getPitch();
      } else {
         this.lastYaw = 0.0F;
         this.lastPitch = 0.0F;
      }
   }

   public void onStrike() {
      this.strikeTimer.reset();
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null
         && entity
            != null) {
         if (!AuraTransition.INSTANCE.handleTransitions()) {
            float yaw = this.lastYaw;
            float yaw2 = this.lastPitch;
            Vec3d vec = this.resolveAimPoint(
               entity
            );
            if (this.shouldUseElytraPredict(
               entity
            )) {
               vec = getPredictedPoint(
                  entity,
                  vec
               );
            }

            Vec2f rot = RayTraceUtil.getRotations(
               vec
            );
            float yaw3 = rot.x;
            float yaw4 = rot.y;
            float yaw5 = MathHelper.wrapDegrees(
               yaw3
                  - yaw
            );
            float yaw6 = yaw4
               - yaw2;
            float yaw7 = (float)Math.hypot(
               (double)Math.abs(
                  yaw5
               ),
               (double)Math.abs(
                  yaw6
               )
            );
            boolean flag = this.canStrike(
               entity
            );
            float yaw8 = mc.player
               .distanceTo(
                  entity
               );
            float yaw9 = flag
               ? 0.87F
               : 0.56F;
            if (yaw8
                  > 0.0F
               && yaw8
                  < 0.66F) {
               float yaw10 = MathHelper.clamp(
                  yaw8
                     / 1.5F
                     * 0.35F,
                  0.1F,
                  0.6F
               );
               yaw9 = flag
                  ? 0.85F
                  : Math.min(
                     yaw9,
                     yaw10
                  );
            }

            float yaw11 = yaw7
                  > 1.0E-4F
               ? Math.abs(
                     yaw5
                        / yaw7
                  )
                  * 180.0F
               : 180.0F;
            float yaw12 = yaw7
                  > 1.0E-4F
               ? Math.abs(
                     yaw6
                        / yaw7
                  )
                  * 180.0F
               : 180.0F;
            float yaw13 = 0.0F;
            float yaw14 = 0.0F;
            if (!flag
               )
             {
               long time = System.currentTimeMillis();
               yaw13 = (float)(
                  (double)this.randomLerp(18.0F, 27.0F)
                     * Math.sin(
                        (double)time
                           / 50.0
                     )
               );
               yaw14 = (float)(
                  (double)this.randomLerp(15.0F, 22.0F)
                     * Math.sin(
                        (double)time
                           / 13.0
                     )
               );
            }

            boolean flag2 = !this.aura
                  .isEnable()
               || this.aura.getTarget() == null;
            if (flag2
               && this.strikeTimer.finished(1000L)) {
               yaw9 = 0.35F;
               yaw13 = 0.0F;
               yaw14 = 0.0F;
            }

            float yaw15 = MathHelper.clamp(
               yaw5,
               -yaw11,
               yaw11
            );
            float yaw16 = MathHelper.clamp(
               yaw6,
               -yaw12,
               yaw12
            );
            float yaw17 = MathHelper.lerp(
                  yaw9,
                  yaw,
                  yaw
                     + yaw15
               )
               + yaw13;
            float yaw18 = MathHelper.lerp(
                  yaw9,
                  yaw2,
                  yaw2
                     + yaw16
               )
               + yaw14;
            float yaw19 = MouseGCD.getGCDValue();
            if (yaw19
               > 0.0F) {
               yaw17 = yaw
                  + (float)Math.round(
                        (
                              yaw17
                                 - yaw
                           )
                           / yaw19
                     )
                     * yaw19;
               yaw18 = yaw2
                  + (float)Math.round(
                        (
                              yaw18
                                 - yaw2
                           )
                           / yaw19
                     )
                     * yaw19;
            }

            yaw18 = MathHelper.clamp(
               yaw18,
               -89.0F,
               89.0F
            );
            this.lastYaw = yaw17;
            this.lastPitch = yaw18;
            AuraTransition var10000 = AuraTransition.INSTANCE;
            float[] floats = var10000.process(
               yaw17,
               yaw18,
               entity,
               "lgangle"
            );
            float yaw20 = flag
               ? 360.0F
               : 45.0F;
            RotationStorage.update(
               new Angle(
                  floats[0],
                  floats[1]
               ),
               yaw20,
               yaw20,
               yaw20,
               yaw20,
               0,
               1,
               Aura.clientLook.isState()
            );
         }
      }
   }

   private boolean canStrike(
      LivingEntity entity
   ) {
      return this.aura.getTarget() != null
            && entity
               == this.aura.getTarget()
         ? mc.player.getAttackCooldownProgress(1.0F) >= 0.9F && this.aura.getWhiteRiseTicksToAttack() <= 1
         : false;
   }

   private Vec3d resolveAimPoint(
      LivingEntity entity
   ) {
      ThreadLocalRandom val = ThreadLocalRandom.current();
      Vec3d vec = GetclosestpointHelper.getClosestPoint(
         entity
      );
      if (vec
         == null) {
         vec = this.getPredictedBox(
               entity
            )
            .getCenter();
      }

      Vec3d vec2 = new Vec3d(
         (
               val.nextDouble()
                  - 0.5
            )
            * 0.02,
         val.nextDouble()
            * 0.07,
         (
               val.nextDouble()
                  - 0.5
            )
            * 0.02
      );
      return vec.add(
         vec2
      );
   }

   private float randomLerp(
      float f,
      float f2
   ) {
      return MathHelper.lerp(
         ThreadLocalRandom.current().nextFloat(),
         f,
         f2
      );
   }
}
