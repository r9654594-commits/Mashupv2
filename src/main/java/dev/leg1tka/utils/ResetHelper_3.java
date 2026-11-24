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

public class ResetHelper_3 extends AuraRotation implements IMinecraft {
   private LivingEntity tgt;
   private float lyaw;
   private float lpit;
   private float acc;
   private boolean back;
   private float slow;
   private float shoy;
   private float shop;
   private float shpy;
   private float shpp;
   private float jtoy;
   private float jtop;
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
      this.slow = 1.0F;
      this.shoy = 0.0F;
      this.shop = 0.0F;
      this.shpy = 0.0F;
      this.shpp = 0.0F;
      this.jtoy = 0.0F;
      this.jtop = 0.0F;
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
   }

   private float clamp(
      float f
   ) {
      float pitch = 90.0F
         * (Aura.INSTANCE.sc3MaxPitchDownPercent.get() / 100.0F);
      float pitch2 = 90.0F
         * (Aura.INSTANCE.sc3MaxPitchUpPercent.get() / 100.0F);
      return MathHelper.clamp(
         f,
         -pitch2,
         pitch
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
         int i = Math.max(
            1, (int)Aura.INSTANCE.sc3PointLockTicks.get()
         );
         if (Aura.INSTANCE.sc3PointLock.isState()
            && this.ltgt
               == entity
            && this.lpt != null
            && this.ltck
               < i
            )
          {
            this.ltck++;
            return this.lpt;
         } else {
            String str = Aura.INSTANCE
               .sc3PointMode
               .getCurrent();

            Vec3d vec = switch (str) {
               case "Грудь" -> entity.getPos()
                  .add(
                     0.0,
                     (double)entity.getHeight()
                        * 0.72,
                     0.0
                  );
               case "Голова" -> entity.getPos()
                  .add(
                     0.0,
                     (double)entity.getHeight()
                        * 0.92,
                     0.0
                  );
               default -> {
                  Vec3d vec2 = RaycastUtil.getNearestPoint(
                     entity
                  );
                  yield RaycastUtil.getNearestVisiblePoint(
                     entity,
                     vec2
                           != null
                        ? vec2
                        : entity.getBoundingBox()
                           .getCenter(),
                     (double)Aura.INSTANCE.range.get()
                  );
               }
            };
            if (Aura.INSTANCE.sc3Multipoint.isState()) {
               String str2 = Aura.INSTANCE
                  .sc3MultipointMode
                  .getCurrent();

               vec = switch (str2) {
                  case "Агрессивный" -> RaycastUtil.getPoint2(
                     entity
                  );
                  case "Мягкий" -> RaycastUtil.getPoint(
                     entity
                  );
                  default -> vec;
               };
            }

            if (Aura.INSTANCE.sc3PointLock.isState()) {
               this.ltgt = entity;
               this.lpt = vec;
               this.ltck = 0;
            }

            return vec
                  != null
               ? vec
               : entity.getBoundingBox()
                  .getCenter();
         }
      }
   }

   private float atrn(
      float f
   ) {
      if (!Aura.INSTANCE.sc3AntiTurn.isState()) {
         return 1.0F;
      } else {
         float x = Math.abs(
            f
         );
         float x2 = Aura.INSTANCE
            .sc3AntiTurnThreshold
            .get();
         if (x
            <= x2
            )
          {
            return 1.0F;
         } else {
            float x3 = MathHelper.clamp(
               (
                     x
                        - x2
                  )
                  / Math.max(
                     1.0F,
                     180.0F
                        - x2
                  ),
               0.0F,
               1.0F
            );
            return MathHelper.clamp(
               1.0F
                  - x3
                     * Aura.INSTANCE.sc3AntiTurnStrength.get(),
               0.1F,
               1.0F
            );
         }
      }
   }

   private float hslow(
      Box box,
      Vec3d vec
   ) {
      boolean flag = box.expand(
               (double)Aura.INSTANCE.sc3BoxExpand.get()
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
         ? Aura.INSTANCE.sc3HitboxSlowdownStrength.get()
         : 1.0F;
      float x2 = flag
         ? 1.0F
         : Aura.INSTANCE.sc3HitboxSpeedReturn.get();
      this.slow = this.slow
         + (
               x
                  - this.slow
            )
            * x2;
      this.slow = MathHelper.clamp(this.slow, Aura.INSTANCE.sc3HitboxSlowdownStrength.get(), 1.0F);
      return this.slow;
   }

   private void shk(
      float f,
      float f2
   ) {
      if (!Aura.INSTANCE.sc3ShakeYaw.isState()) {
         this.shoy = 0.0F;
      } else {
         float yaw = Aura.INSTANCE
            .sc3ShakeAmplitudeYaw
            .get();
         float yaw2 = Math.min(
            Aura.INSTANCE.sc3ShakeSpeedYaw.get() * 0.1F, 0.5F
         );
         float yaw3 = MathHelper.clamp(
            Math.max(
               Math.abs(
                     f
                  )
                  / 25.0F,
               Math.abs(
                     f2
                  )
                  / 20.0F
            ),
            0.12F,
            1.0F
         );
         this.shpy += yaw2;
         float yaw4 = (float)Math.sin(
               (double)this.shpy
            )
            * yaw;
         float yaw5 = 1.0F
            + (this.rnd.nextFloat() - 0.5F) * Aura.INSTANCE.sc3ShakeRandomYaw.get() * 0.2F;
         this.shoy = yaw4
            * yaw5;
         this.shoy = this.shoy * (1.0F - Aura.INSTANCE.sc3ShakeDampingYaw.get() * 0.5F);
         if (Aura.INSTANCE.sc3ShakeBounceYaw.get() > 0.0F
            && Math.abs(
                  yaw4
               )
               > yaw
                  * 0.7F) {
            this.shpy = this.shpy + (float)(Math.PI * (double)Aura.INSTANCE.sc3ShakeBounceYaw.get() * 0.1F);
         }

         this.shoy = MathHelper.clamp(
            this.shoy
               * yaw3,
            -yaw,
            yaw
         );
      }

      if (!Aura.INSTANCE.sc3ShakePitch.isState()) {
         this.shop = 0.0F;
      } else {
         float yaw6 = Aura.INSTANCE
            .sc3ShakeAmplitudePitch
            .get();
         float yaw7 = Math.min(
            Aura.INSTANCE.sc3ShakeSpeedPitch.get() * 0.1F, 0.5F
         );
         float yaw8 = MathHelper.clamp(
            Math.max(
               Math.abs(
                     f
                  )
                  / 30.0F,
               Math.abs(
                     f2
                  )
                  / 18.0F
            ),
            0.1F,
            1.0F
         );
         this.shpp += yaw7;
         float yaw9 = (float)Math.sin(
               (double)this.shpp
            )
            * yaw6;
         float yaw10 = 1.0F
            + (this.rnd.nextFloat() - 0.5F) * Aura.INSTANCE.sc3ShakeRandomPitch.get() * 0.2F;
         this.shop = yaw9
            * yaw10;
         this.shop = this.shop * (1.0F - Aura.INSTANCE.sc3ShakeDampingPitch.get() * 0.5F);
         if (Aura.INSTANCE.sc3ShakeBouncePitch.get() > 0.0F
            && Math.abs(
                  yaw9
               )
               > yaw6
                  * 0.7F) {
            this.shpp = this.shpp + (float)(Math.PI * (double)Aura.INSTANCE.sc3ShakeBouncePitch.get() * 0.1F);
         }

         this.shop = MathHelper.clamp(
            this.shop
               * yaw8,
            -yaw6,
            yaw6
         );
      }
   }

   private void jtr(
      float f,
      float f2
   ) {
      if (!Aura.INSTANCE.sc3Jitter.isState()) {
         this.jtoy = 0.0F;
         this.jtop = 0.0F;
      } else {
         boolean flag = Math.abs(
                  f
               )
               <= 8.0F
            && Math.abs(
                  f2
               )
               <= 6.0F;
         if (Aura.INSTANCE.sc3JitterOnlyOnFinish.isState()
            && !flag
            )
          {
            this.jtoy = 0.0F;
            this.jtop = 0.0F;
         } else if (this.rnd.nextFloat() * 100.0F <= Aura.INSTANCE.sc3JitterChance.get()) {
            float yaw = Aura.INSTANCE
               .sc3JitterStrength
               .get();
            this.jtoy = (this.rnd.nextFloat() - 0.5F)
               * 2.0F
               * yaw;
            this.jtop = Aura.INSTANCE.sc3JitterOnlyYaw.isState()
               ? 0.0F
               : (this.rnd.nextFloat() - 0.5F)
                  * yaw;
         } else {
            this.jtoy *= 0.2F;
            this.jtop *= 0.2F;
         }
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
               this.slow = 1.0F;
               this.shoy = 0.0F;
               this.shop = 0.0F;
               this.jtoy = 0.0F;
               this.jtop = 0.0F;
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
               (double)Aura.INSTANCE.sc3RaytraceDistance.get(),
               box.expand(
                  (double)Aura.INSTANCE.sc3BoxExpand.get()
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
            if (Aura.INSTANCE.sc3FreezeOnHitbox.isState()
               && flag
               )
             {
               yaw5 = this.lyaw;
               yaw6 = this.lpit;
            } else if (Aura.INSTANCE
               .sc3SpeedMode
               .is(
                  "Постоянная"
               )) {
               float yaw7 = Math.min(
                     Math.abs(
                        yaw3
                     ),
                     Aura.INSTANCE.sc3ConstantSpeedYaw.get()
                  )
                  * Math.signum(
                     yaw3
                  );
               float yaw8 = Math.min(
                     Math.abs(
                        yaw4
                     ),
                     Aura.INSTANCE.sc3ConstantSpeedPitch.get()
                  )
                  * Math.signum(
                     yaw4
                  );
               yaw5 = this.lyaw
                  + yaw7
                     * Aura.INSTANCE.sc3SmoothMultiplier.get();
               yaw6 = this.lpit
                  + yaw8
                     * Aura.INSTANCE.sc3SmoothMultiplierPitch.get();
            } else if (Aura.INSTANCE
               .sc3SpeedMode
               .is(
                  "Адаптивная"
               )) {
               float yaw9 = MathHelper.clamp(
                  (float)Math.sqrt(
                        (double)(
                           yaw3
                                 * yaw3
                              + yaw4
                                 * yaw4
                        )
                     )
                     / 45.0F,
                  0.15F,
                  1.0F
               );
               float yaw10 = yaw3
                  * yaw9
                  * Aura.INSTANCE.sc3SmoothMultiplier.get();
               float yaw11 = yaw4
                  * yaw9
                  * Aura.INSTANCE.sc3SmoothMultiplierPitch.get();
               yaw5 = this.lyaw
                  + yaw10;
               yaw6 = this.lpit
                  + yaw11;
            } else {
               if (this.back) {
                  float yaw12 = Aura.INSTANCE
                     .sc3MinAcceleration
                     .get();
                  float yaw13 = Math.abs(
                           MathHelper.wrapDegrees(
                              yaw
                                 - this.lyaw
                           )
                        )
                        > Aura.INSTANCE.sc3BigAngleThreshold.get()
                     ? Aura.INSTANCE.sc3AccelDecrementBackBigAngle.get()
                     : Aura.INSTANCE.sc3AccelDecrementBack.get();
                  if (this.acc
                     >= yaw12
                     )
                   {
                     this.acc -= yaw13;
                  }

                  if (this.acc
                     <= yaw12
                     )
                   {
                     this.back = false;
                  }
               } else {
                  this.acc = this.acc + Aura.INSTANCE.sc3AccelIncrement.get();
                  if (this.acc >= Aura.INSTANCE.sc3MaxAcceleration.get()
                     || flag
                     )
                   {
                     this.back = true;
                  }
               }

               float yaw14 = this.atrn(
                  yaw3
               );
               yaw3 *= yaw14;
               yaw4 *= 1.0F
                  - (
                        1.0F
                           - yaw14
                     )
                     * 0.5F;
               float yaw15 = Math.max(
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
                           yaw15
                              * Aura.INSTANCE.sc3SmoothMultiplier.get(),
                           0.0F
                        ),
                        1.0F
                     );
               yaw6 = this.lpit
                  + yaw4
                     * Math.min(
                        Math.max(
                           yaw15
                              * Aura.INSTANCE.sc3SmoothMultiplierPitch.get(),
                           0.0F
                        ),
                        1.0F
                     );
            }

            if (!Aura.INSTANCE.sc3FreezeOnHitbox.isState()
               || !flag
               )
             {
               if (Aura.INSTANCE.sc3ShakeYaw.isState() || Aura.INSTANCE.sc3ShakePitch.isState()) {
                  this.shk(
                     yaw3,
                     yaw4
                  );
                  yaw5 += this.shoy;
                  yaw6 += this.shop;
               }

               if (Aura.INSTANCE.sc3Jitter.isState()) {
                  this.jtr(
                     yaw3,
                     yaw4
                  );
                  yaw5 += this.jtoy;
                  yaw6 += this.jtop;
               }
            }

            float yaw16 = Aura.INSTANCE
                  .sc3SmoothReturn
                  .isState()
               ? Aura.INSTANCE.sc3ReturnSpeedYaw.get()
               : 360.0F;
            float yaw17 = Aura.INSTANCE
                  .sc3SmoothReturn
                  .isState()
               ? Aura.INSTANCE.sc3ReturnSpeedPitch.get()
               : 360.0F;
            int x2 = Aura.INSTANCE
                  .sc3SmoothReturn
                  .isState()
               ? 1
               : 0;
            RotationStorage.update(
               new Angle(
                  yaw5,
                  yaw6
               ),
               360.0F,
               360.0F,
               yaw16,
               yaw17,
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
