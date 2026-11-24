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
import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventGameUpdate;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class GrimRotation extends AuraRotation implements IMinecraft {
   private final Aura aura;
   private LivingEntity tgt;
   private float lyaw;
   private float lpit;
   private float acc;
   private boolean back;
   private boolean hit;
   private float slow = 1.0F;
   private Vec3d lpt;
   private LivingEntity ltgt;
   private int ltck;
   private float ovsy;
   private float ovsp;
   private final Random rnd = new Random();
   private boolean init;
   private boolean act;
   private boolean ret;
   private long rettim;

   public GrimRotation() {
      this(null);
   }

   public GrimRotation(
      Aura val
   ) {
      this.aura = val;
   }

   public void reset() {
      EventInvoker.unregister(this);
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
      this.ovsy = 0.0F;
      this.ovsp = 0.0F;
      this.init = mc.player != null;
      this.act = false;
      this.ret = false;
      this.rettim = 0L;
      if (mc.player != null) {
         this.lyaw = mc.player.getYaw();
         this.lpit = mc.player.getPitch();
      }

      AuraTransition.INSTANCE.reset();
   }

   public boolean isActive() {
      return this.act;
   }

   public void onAttack() {
      this.hit = true;
      this.back = true;
      this.acc = -0.04F;
   }

   public void returnToCamera() {
      if (mc.player != null && this.init && FreeLookStorage.isActive() && !this.ret) {
         EventInvoker.unregister(this);
         EventInvoker.register(this);
         this.rettim = System.currentTimeMillis();
         this.tgt = null;
         this.ret = true;
      }
   }

   @EventLink
   public void onGameUpdate(
      EventGameUpdate event
   ) {
      if (this.ret) {
         if (this.aura != null && this.aura.isEnable() && this.aura.getTarget() != null) {
            this.ret = false;
            this.rettim = 0L;
            EventInvoker.unregister(this);
         } else {
            long time = System.currentTimeMillis();
            if (time
                     - this.rettim
                  < 2000L
               && mc.player != null) {
               this.updateRotations(null);
            } else {
               if (RotationStorage.instance != null) {
                  RotationStorage.instance.stopRotation();
               }

               this.reset();
            }
         }
      }
   }

   private float clamp(
      float f
   ) {
      return MathHelper.clamp(
         f,
         -89.0F,
         89.0F
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
               -0.3
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
         ? 0.05F
         : 1.0F;
      float x2 = flag
         ? 1.0F
         : 0.01F;
      this.slow = this.slow
         + (
               x
                  - this.slow
            )
            * x2;
      this.slow = MathHelper.clamp(this.slow, 0.05F, 1.0F);
      return this.slow;
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null) {
         if (!AuraTransition.INSTANCE.handleTransitions()) {
            if (entity
               == null) {
               if (this.ret) {
                  float yaw = FreeLookStorage.isActive()
                     ? FreeLookStorage.getFreeYaw()
                     : mc.player.getYaw();
                  float yaw2 = FreeLookStorage.isActive()
                     ? FreeLookStorage.getFreePitch()
                     : mc.player.getPitch();
                  float yaw3 = MathHelper.wrapDegrees(
                     yaw
                        - this.lyaw
                  );
                  float yaw4 = yaw2
                     - this.lpit;
                  if (Math.abs(
                           yaw3
                        )
                        < 0.5F
                     && Math.abs(
                           yaw4
                        )
                        < 0.5F) {
                     if (RotationStorage.instance != null) {
                        RotationStorage.instance.stopRotation();
                     }

                     this.reset();
                  } else {
                     float yaw5 = 0.2F;
                     float yaw6 = this.lyaw
                        + yaw3
                           * yaw5;
                     float yaw7 = this.lpit
                        + yaw4
                           * yaw5;
                     yaw7 = this.clamp(
                        yaw7
                     );
                     RotationStorage.update(
                        new Angle(
                           yaw6,
                           yaw7
                        ),
                        360.0F,
                        360.0F,
                        30.0F,
                        9.0F,
                        0,
                        1,
                        Aura.clientLook.isState()
                     );
                     this.lyaw = yaw6;
                     this.lpit = yaw7;
                     this.rotate = new Vec2f(this.lyaw, this.lpit);
                     Aura.INSTANCE.currentRotations = new Vec2f(this.lyaw, this.lpit);
                     Aura.INSTANCE.targetRotations = new Vec2f(
                        yaw6,
                        yaw7
                     );
                  }
               }
            } else {
               if (this.tgt
                  != entity
                  )
                {
                  this.tgt = entity;
                  this.init = true;
                  this.act = true;
                  this.ret = false;
                  this.rettim = 0L;
                  this.lyaw = mc.player.getYaw();
                  this.lpit = mc.player.getPitch();
                  this.acc = 0.0F;
                  this.back = false;
                  this.hit = false;
                  this.slow = 1.0F;
                  this.lpt = null;
                  this.ltgt = null;
                  this.ltck = 0;
                  this.ovsy = 0.0F;
                  this.ovsp = 0.0F;
               }

               Vec3d vec = this.pt(
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

               Vec3d vec2 = mc.player
                  .getEyePos();
               Vec3d vec3 = vec.subtract(
                  vec2
               );
               float yaw8 = (float)MathHelper.wrapDegrees(
                  Math.toDegrees(
                        Math.atan2(
                           vec3.z,
                           vec3.x
                        )
                     )
                     - 90.0
               );
               float yaw9 = (float)(
                  -Math.toDegrees(
                     Math.atan2(
                        vec3.y,
                        vec3.horizontalLength()
                     )
                  )
               );
               yaw9 = this.clamp(
                  yaw9
               );
               Box box = entity.getBoundingBox();
               boolean flag = RaytraceHelper.rayTrace(
                  mc.player.getRotationVector(this.lpit, this.lyaw),
                  1950.0,
                  box.expand(
                     -0.3
                  )
               );
               float yaw10 = MathHelper.wrapDegrees(
                  yaw8
                     - this.lyaw
               );
               float yaw11 = yaw9
                  - this.lpit;
               float yaw12 = this.lyaw;
               float yaw13 = this.lpit;
               float yaw14 = mc.player
                  .getAttackCooldownProgress(0.5F);
               boolean flag2 = yaw14
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
                  float yaw15 = Math.max(
                        this.acc, 0.05F
                     )
                     * this.hslow(
                        box,
                        vec2
                     )
                     * 0.4F;
                  yaw12 = this.lyaw
                     + yaw10
                        * Math.min(
                           yaw15
                              * 1.8F,
                           1.0F
                        );
                  yaw13 = this.lpit
                     + yaw11
                        * Math.min(
                           yaw15
                              * 0.4F,
                           1.0F
                        );
               } else if (flag
                  )
                {
                  float yaw16 = Math.max(
                        this.acc, 0.06F
                     )
                     * this.hslow(
                        box,
                        vec2
                     )
                     * 0.35F;
                  yaw12 = this.lyaw
                     + yaw10
                        * Math.min(
                           yaw16
                              * 1.8F,
                           1.0F
                        );
                  yaw13 = this.lpit
                     + yaw11
                        * Math.min(
                           yaw16
                              * 0.4F,
                           1.0F
                        );
               } else {
                  if (this.back) {
                     float yaw17 = -0.1F;
                     float yaw18 = Math.abs(
                              yaw10
                           )
                           > 30.0F
                        ? 0.012F
                        : 0.022F;
                     if (this.acc
                        >= yaw17
                        )
                      {
                        this.acc -= yaw18;
                     }

                     if (this.acc
                        <= yaw17
                        )
                      {
                        this.back = false;
                     }
                  } else {
                     this.acc += 0.0035F;
                     if (this.acc >= 0.24F
                        || flag
                        )
                      {
                        this.back = true;
                     }
                  }

                  float yaw19 = Math.max(
                        this.acc, 0.0F
                     )
                     * this.hslow(
                        box,
                        vec2
                     );
                  yaw12 = this.lyaw
                     + yaw10
                        * Math.min(
                           Math.max(
                              yaw19
                                 * 1.8F,
                              0.0F
                           ),
                           1.0F
                        );
                  yaw13 = this.lpit
                     + yaw11
                        * Math.min(
                           Math.max(
                              yaw19
                                 * 0.4F,
                              0.0F
                           ),
                           1.0F
                        );
               }

               if (Math.abs(
                        yaw10
                     )
                     > 18.0F
                  && Math.abs(this.acc) > 0.08F) {
                  this.ovsy = Math.signum(
                        yaw10
                     )
                     * Math.min(
                        Math.abs(
                              yaw10
                           )
                           * 0.06F,
                        1.2F
                     )
                     * 0.9F;
                  this.ovsp = Math.signum(
                        yaw11
                     )
                     * Math.min(
                        Math.abs(
                              yaw11
                           )
                           * 0.04F,
                        0.8F
                     )
                     * 0.9F;
               } else {
                  this.ovsy *= 0.65F;
                  this.ovsp *= 0.65F;
               }

               yaw12 += this.ovsy;
               yaw13 += this.ovsp;
               yaw13 = this.clamp(
                  yaw13
               );
               float yaw20 = 30.0F;
               float yaw21 = 9.0F;
               int x2 = 1;
               RotationStorage.update(
                  new Angle(
                     yaw12,
                     yaw13
                  ),
                  360.0F,
                  360.0F,
                  yaw20,
                  yaw21,
                  x2,
                  1,
                  Aura.clientLook.isState()
               );
               this.lyaw = yaw12;
               this.lpit = yaw13;
               this.rotate = new Vec2f(this.lyaw, this.lpit);
               Aura.INSTANCE.currentRotations = new Vec2f(this.lyaw, this.lpit);
               Aura.INSTANCE.targetRotations = new Vec2f(
                  yaw12,
                  yaw13
               );
            }
         }
      }
   }
}
