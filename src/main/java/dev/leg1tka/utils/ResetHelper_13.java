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

public class ResetHelper_13 extends AuraRotation implements IMinecraft {
   private final Aura aura;
   private float pacc = 1.0F;
   private float lyaw;
   private float lpitch;
   private int ltid = -2147483648;

   public ResetHelper_13(
      Aura val
   ) {
      this.aura = val;
   }

   public void reset() {
      this.pacc = 1.0F;
      this.ltid = -2147483648;
      if (mc.player != null) {
         this.lyaw = mc.player.getYaw();
         this.lpitch = mc.player.getPitch();
      }
   }

   public void onAttack() {
      this.aura.getAttackTimer().reset();
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null) {
         if (entity
               == null
            || entity.getId()
               != this.ltid) {
            this.pacc = 1.0F;
            this.ltid = entity
                  != null
               ? entity.getId()
               : -2147483648;
            this.lyaw = mc.player.getYaw();
            this.lpitch = mc.player.getPitch();
            if (entity
               == null) {
               return;
            }
         }

         Vec3d vec = RaycastUtil.getMultipoint(
            entity,
            Math.max(
               6.0,
               (double)mc.player
                     .distanceTo(
                        entity
                     )
                  + 2.5
            )
         );
         if (vec
            == null) {
            vec = entity.getBoundingBox()
               .getCenter();
         }

         if (this.shouldUseElytraPredict(
            entity
         )) {
            vec = getPredictedPoint(
               entity,
               vec
            );
         }

         Vec3d vec2 = vec.subtract(
            mc.player.getEyePos()
         );
         double dist = Math.hypot(
            vec2.x,
            vec2.z
         );
         float yaw = (float)(
               Math.atan2(
                     vec2.z,
                     vec2.x
                  )
                  * (180.0 / Math.PI)
            )
            - 90.0F;
         float yaw2 = (float)(
            -(
               Math.atan2(
                     vec2.y,
                     dist
                  )
                  * (180.0 / Math.PI)
            )
         );
         float yaw3 = MathHelper.wrapDegrees(
            yaw
               - this.lyaw
         );
         float yaw4 = MathHelper.wrapDegrees(
            yaw2
               - this.lpitch
         );
         float yaw5 = (float)ThreadLocalRandom.current()
            .nextDouble(65.0, 75.0);
         float yaw6;
         if (mc.player.isGliding()) {
            yaw6 = 180.0F;
         } else {
            if (Math.abs(
                  yaw4
               )
               < 1.0F) {
               this.pacc = 1.0F;
            } else {
               this.pacc = Math.min(this.pacc * 1.45F, 10.0F);
            }

            float yaw7 = (float)ThreadLocalRandom.current()
               .nextDouble(-1.0, 1.0);
            yaw6 = this.pacc
               + yaw7;
         }

         float yaw8 = MathHelper.clamp(
            yaw3,
            -yaw5,
            yaw5
         );
         float yaw9 = MathHelper.clamp(
            yaw4,
            -yaw6,
            yaw6
         );
         float yaw10 = this.lyaw
            + yaw8;
         float yaw11 = MathHelper.clamp(
            this.lpitch
               + yaw9,
            -90.0F,
            90.0F
         );
         double dist2 = (Double)mc.options
            .getMouseSensitivity()
            .getValue();
         double dist3 = dist2
               * 0.6
            + 0.2;
         double dist4 = dist3
            * dist3
            * dist3
            * 1.2;
         if (dist4
            > 0.0) {
            float yaw12 = MathHelper.wrapDegrees(
               yaw10
                  - this.lyaw
            );
            yaw10 = this.lyaw
               + (float)(
                  (double)Math.round(
                        (double)yaw12
                           / dist4
                     )
                     * dist4
               );
            float yaw13 = yaw11
               - this.lpitch;
            yaw11 = this.lpitch
               + (float)(
                  (double)Math.round(
                        (double)yaw13
                           / dist4
                     )
                     * dist4
               );
         }

         yaw11 = MathHelper.clamp(
            yaw11,
            -90.0F,
            90.0F
         );
         this.lyaw = yaw10;
         this.lpitch = yaw11;
         this.rotate = new Vec2f(
            yaw10,
            yaw11
         );
         RotationStorage.update(
            new Angle(
               yaw10,
               yaw11
            ),
            360.0F,
            360.0F,
            360.0F,
            360.0F,
            0,
            1,
            Aura.clientLook.isState()
         );
      }
   }
}
