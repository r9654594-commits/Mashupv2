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
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ResetHelper_7 extends AuraRotation implements IMinecraft {
   private LivingEntity tgt;
   private float lyaw;
   private float lpit;
   private float acc;
   private boolean back;
   private boolean hit;
   private float slow;
   private Vec3d lpt;
   private LivingEntity ltgt;
   private int ltck;
   private final Random rnd = new Random();

   public void reset() {
      this.tgt = null;
      this.lyaw = 0.0F;
      this.lpit = 0.0F;
      this.acc = 0.0F;
      this.back = false;
      this.hit = false;
      this.slow = 1.0F;
      this.lpt = null;
      this.ltgt = null;
      this.ltck = 0;
      if (mc.player != null) {
         this.lyaw = mc.player.getYaw();
         this.lpit = mc.player.getPitch();
      }

      AuraTransition.INSTANCE.reset();
   }

   public void onAttack() {
      this.hit = true;
      this.back = true;
      this.acc = -0.04F;
   }

   private float clamp(
      float f
   ) {
      float f2 = 90.0F;
      float f3 = 0.0F;
      return MathHelper.clamp(
         f,
         -f3,
         f2
      );
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
         int i = 1;
         if (this.ltgt
               == entity
            && this.lpt != null
            && this.ltck
               < i
            )
          {
            this.ltck++;
            return this.lpt;
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

            this.ltgt = entity;
            this.lpt = vec;
            this.ltck = 0;
            return vec
                  != null
               ? vec
               : entity.getBoundingBox()
                  .getCenter();
         }
      }
   }

   private float hslow(
      Box box,
      Vec3d vec
   ) {
      boolean flag = box.expand(
               -0.2
            )
            .contains(
               vec
            )
         || box.expand(
               0.3
            )
            .contains(
               vec
            );
      float x = flag
         ? 0.96F
         : 1.0F;
      float x2 = flag
         ? 1.0F
         : 0.16F;
      this.slow = this.slow
         + (
               x
                  - this.slow
            )
            * x2;
      this.slow = MathHelper.clamp(this.slow, 0.96F, 1.0F);
      return this.slow;
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
               this.hit = false;
               this.slow = 1.0F;
               this.lpt = null;
               this.ltgt = null;
               this.ltck = 0;
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
            yaw2 = this.clamp(
               yaw2
            );
            Box box = entity.getBoundingBox();
            boolean flag = RaytraceHelper.rayTrace(
               mc.player.getRotationVector(this.lpit, this.lyaw),
               2000.0,
               box.expand(
                  -0.2
               )
            );
            float yaw3 = MathHelper.wrapDegrees(
               yaw
                  - this.lyaw
            );
            float yaw4 = yaw2
               - this.lpit;
            float yaw5 = this.lyaw;
            float yaw6 = this.lpit;
            float yaw7 = mc.player
               .getAttackCooldownProgress(0.5F);
            boolean flag2 = yaw7
                  >= 0.65F
               || !mc.player.isOnGround() && mc.player.getVelocity().y < 0.0 && mc.player.fallDistance > 0.0F;
            if (this.hit
               && flag2
               )
             {
               this.hit = false;
               this.back = false;
               this.acc = 0.0F;
            }

            if (this.hit
               && !flag2
               )
             {
               this.acc = Math.min(this.acc, 0.0F);
            } else if (this.back) {
               float yaw8 = -0.1F;
               float yaw9 = Math.abs(
                        MathHelper.wrapDegrees(
                           yaw
                              - this.lyaw
                        )
                     )
                     > 30.0F
                  ? 0.01F
                  : 0.1F;
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
               this.acc += 0.003F;
               if (this.acc >= 0.1F
                  || flag
                  )
                {
                  this.back = true;
               }
            }

            float yaw10 = Math.max(
                  this.acc, 0.0F
               )
               * this.hslow(
                  box,
                  vec2
               );
            yaw5 = this.lyaw
               + yaw3
                  * Math.min(
                     Math.max(
                        yaw10
                           * 2.5F,
                        0.0F
                     ),
                     1.0F
                  );
            yaw6 = this.lpit
               + yaw4
                  * Math.min(
                     Math.max(
                        yaw10
                           * 2.5F,
                        0.0F
                     ),
                     1.0F
                  );
            float yaw11 = 30.0F;
            float yaw12 = 9.0F;
            int x2 = 1;
            RotationStorage.update(
               new Angle(
                  yaw5,
                  yaw6
               ),
               360.0F,
               360.0F,
               yaw11,
               yaw12,
               x2,
               1,
               Aura.clientLook.isState()
            );
            this.lyaw = yaw5;
            this.lpit = yaw6;
            Aura.INSTANCE.currentRotations = new Vec2f(this.lyaw, this.lpit);
            Aura.INSTANCE.targetRotations = new Vec2f(
               yaw5,
               yaw6
            );
         }
      }
   }
}
