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
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ResetHelper_4 extends AuraRotation implements IMinecraft {
   private LivingEntity tgt;
   private float lyaw;
   private float lpit;
   private float acc;
   private boolean back;

   public void reset() {
      this.tgt = null;
      this.acc = 0.0F;
      this.back = false;
      if (mc.player != null) {
         this.lyaw = mc.player.getYaw();
         this.lpit = mc.player.getPitch();
      }

      AuraTransition.INSTANCE.reset();
   }

   public void onAttack() {
      this.back = true;
      this.acc = Math.min(this.acc, 0.0F);
   }

   private Vec3d pt(
      LivingEntity entity
   ) {
      if (entity.isGliding()
         && ModuleManager.elytraTarget != null
         && ModuleManager.elytraTarget.isEnable()) {
         return entity.getBoundingBox()
            .getCenter();
      } else {
         Vec3d vec = RaycastUtil.getPoint(
            entity
         );
         if (vec
            == null) {
            vec = entity.getPos()
               .add(
                  0.0,
                  (double)entity.getHeight()
                     * 0.72,
                  0.0
               );
         }

         return vec
               != null
            ? vec
            : entity.getBoundingBox()
               .getCenter();
      }
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null
         && entity
            != null) {
         if (!AuraTransition.INSTANCE.handleTransitions()) {
            if (this.tgt
               != entity
               )
             {
               this.tgt = entity;
               this.lyaw = mc.player.getYaw();
               this.lpit = mc.player.getPitch();
               this.acc = 0.0F;
               this.back = false;
            }

            Vec3d vec = this.pt(
               entity
            );
            Vec3d vec2 = mc.player
               .getEyePos();
            Vec3d vec3 = vec.subtract(
               vec2
            );
            float yaw = (float)MathHelper.wrapDegrees(
               Math.toDegrees(
                     Math.atan2(
                        vec3.z,
                        vec3.x
                     )
                  )
                  - 90.0
            );
            float yaw2 = (float)(
               -Math.toDegrees(
                  Math.atan2(
                     vec3.y,
                     vec3.horizontalLength()
                  )
               )
            );
            yaw2 = MathHelper.clamp(
               yaw2,
               -90.0F,
               90.0F
            );
            if (mc.player.isGliding()
               && entity.isGliding()
               )
             {
               if (this.back) {
                  if (this.acc >= -0.02F) {
                     float yaw3 = Math.abs(
                              MathHelper.wrapDegrees(
                                 yaw
                                    - this.lyaw
                              )
                           )
                           > 80.0F
                        ? 0.1F
                        : 0.01F;
                     this.acc -= yaw3;
                  }

                  if (this.acc <= -0.02F) {
                     this.back = false;
                  }
               } else {
                  this.acc += 0.004F;
                  boolean flag = RaytraceHelper.rayTrace(
                     mc.player.getRotationVector(this.lpit, this.lyaw),
                     1488.0,
                     entity.getBoundingBox()
                  );
                  if (this.acc >= 0.17F
                     || flag
                     )
                   {
                     this.back = true;
                  }
               }
            } else if (this.back) {
               if (this.acc >= -0.01F) {
                  float yaw4 = Math.abs(
                           MathHelper.wrapDegrees(
                              yaw
                                 - this.lyaw
                           )
                        )
                        > 80.0F
                     ? 0.1F
                     : 0.01F;
                  this.acc -= yaw4;
               }

               if (this.acc <= -0.01F) {
                  this.back = false;
               }
            } else {
               this.acc += 0.004F;
               boolean flag2 = RaytraceHelper.rayTrace(
                  mc.player.getRotationVector(this.lpit, this.lyaw),
                  999.0,
                  entity.getBoundingBox()
                     .expand(-0.5)
               );
               if (this.acc >= 0.18F
                  || flag2
                  )
                {
                  this.back = true;
               }
            }

            float yaw5 = MathHelper.wrapDegrees(
               yaw
                  - this.lyaw
            );
            float yaw6 = yaw2
               - this.lpit;
            float yaw7 = Math.max(
               this.acc, 0.0F
            );
            float yaw8 = this.lyaw
               + yaw5
                  * Math.min(
                     Math.max(
                        yaw7,
                        0.0F
                     ),
                     1.0F
                  );
            float yaw9 = this.lpit
               + yaw6
                  * Math.min(
                     Math.max(
                        yaw7
                           / 2.0F,
                        0.0F
                     ),
                     1.0F
                  );
            yaw8 = MouseGCD.getFixedDelta(
               yaw8,
               this.lyaw
            );
            yaw9 = MouseGCD.getFixedDelta(
               yaw9,
               this.lpit
            );
            RotationStorage.update(
               new Angle(
                  yaw8,
                  yaw9
               ),
               360.0F,
               360.0F,
               360.0F,
               360.0F,
               0,
               1,
               Aura.clientLook.isState()
            );
            this.lyaw = yaw8;
            this.lpit = yaw9;
            Aura.INSTANCE.currentRotations = new Vec2f(this.lyaw, this.lpit);
            Aura.INSTANCE.targetRotations = new Vec2f(
               yaw8,
               yaw9
            );
         }
      }
   }
}
