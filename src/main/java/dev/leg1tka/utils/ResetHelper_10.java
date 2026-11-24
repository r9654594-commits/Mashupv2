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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ResetHelper_10 extends AuraRotation implements IMinecraft {
   private LivingEntity tgt;
   private float lyaw;
   private float lpit;
   private float acc;
   private boolean back;
   private Vec3d lpos;
   private Vec3d lvel;
   private long lswtime;
   private Vec3d reactpos;

   public void reset() {
      this.tgt = null;
      this.acc = 0.0F;
      this.back = false;
      this.lpos = null;
      this.lvel = null;
      this.lswtime = 0L;
      this.reactpos = null;
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
               this.lpos = null;
               this.lvel = null;
               this.lswtime = 0L;
               this.reactpos = null;
            }

            Vec3d vec = entity.getPos();
            Vec3d vec2 = entity.getVelocity();
            if (this.lpos != null && this.lvel != null) {
               Vec3d vec3 = vec2.subtract(
                  this.lvel
               );
               if (vec3.horizontalLengthSquared()
                  > 0.005) {
                  this.lswtime = System.currentTimeMillis();
                  this.reactpos = this.lpos;
               }
            }

            this.lpos = vec;
            this.lvel = vec2;
            Vec3d vec4 = this.pt(
               entity
            );
            if (this.reactpos != null) {
               long time = System.currentTimeMillis()
                  - this.lswtime;
               long time2 = 20L;
               if (time
                  < time2
                  )
                {
                  float yaw = (float)time
                     / (float)time2;
                  vec4 = this.reactpos
                     .lerp(
                        vec4,
                        (double)(
                           yaw
                              * yaw
                        )
                     );
               } else {
                  this.reactpos = null;
               }
            }

            Vec3d vec5 = mc.player
               .getEyePos();
            Vec3d vec6 = vec4.subtract(
               vec5
            );
            float yaw2 = (float)MathHelper.wrapDegrees(
               Math.toDegrees(
                     Math.atan2(
                        vec6.z,
                        vec6.x
                     )
                  )
                  - 90.0
            );
            float yaw3 = (float)(
               -Math.toDegrees(
                  Math.atan2(
                     vec6.y,
                     vec6.horizontalLength()
                  )
               )
            );
            yaw3 = MathHelper.clamp(
               yaw3,
               -90.0F,
               90.0F
            );
            Box box = entity.getBoundingBox();
            boolean flag = RaytraceHelper.rayTrace(
               mc.player.getRotationVector(this.lpit, this.lyaw),
               100.0,
               box.expand(
                  -0.22
               )
            );
            float yaw4 = MathHelper.wrapDegrees(
               yaw2
                  - this.lyaw
            );
            float yaw5 = yaw3
               - this.lpit;
            float yaw6 = this.lyaw;
            float yaw7 = this.lpit;
            if (flag
               )
             {
               yaw6 = this.lyaw;
               yaw7 = this.lpit;
            } else {
               if (this.back) {
                  float yaw8 = 0.0F;
                  float yaw9 = Math.abs(
                           MathHelper.wrapDegrees(
                              yaw2
                                 - this.lyaw
                           )
                        )
                        > 55.0F
                     ? 0.01F
                     : 0.001F;
                  if (this.acc
                     >= yaw8
                     )
                   {
                     this.acc -= yaw9;
                  }

                  if (this.acc
                     <= yaw8
                     )
                   {
                     this.back = false;
                  }
               } else {
                  this.acc += 0.005F;
                  if (this.acc >= 0.1F) {
                     this.back = true;
                  }
               }

               float yaw10 = Math.max(
                  this.acc, 0.0F
               );
               yaw6 = this.lyaw
                  + yaw4
                     * Math.min(
                        Math.max(
                           yaw10
                              * 2.5F,
                           0.0F
                        ),
                        1.0F
                     );
               yaw7 = this.lpit
                  + yaw5
                     * Math.min(
                        Math.max(
                           yaw10
                              * 2.5F,
                           0.0F
                        ),
                        1.0F
                     );
            }

            yaw6 = MouseGCD.getFixedDelta(
               yaw6,
               this.lyaw
            );
            yaw7 = MouseGCD.getFixedDelta(
               yaw7,
               this.lpit
            );
            float yaw11 = 13.0F;
            float yaw12 = 18.0F;
            int x2 = 1;
            RotationStorage.update(
               new Angle(
                  yaw6,
                  yaw7
               ),
               360.0F,
               360.0F,
               yaw11,
               yaw12,
               x2,
               1,
               Aura.clientLook.isState()
            );
            this.lyaw = yaw6;
            this.lpit = yaw7;
            Aura.INSTANCE.currentRotations = new Vec2f(this.lyaw, this.lpit);
            Aura.INSTANCE.targetRotations = new Vec2f(
               yaw6,
               yaw7
            );
         }
      }
   }
}
