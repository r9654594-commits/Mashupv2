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
import dev.leg1tka.api.events.implement.EventKeyboardInput;
import dev.leg1tka.api.events.implement.EventUpdate;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public class RotationStorage implements IMinecraft {
   public static RotationStorage instance;
   private ClientOption_AYnqSxWF currentTask = ClientOption_AYnqSxWF.IDLE;
   private float currentYawSpeed;
   private float currentPitchSpeed;
   private float currentYawReturnSpeed;
   private float currentPitchReturnSpeed;
   private int currentPriority;
   private int currentTimeout;
   private int idleTicks;
   private Angle targetRotation;

   public RotationStorage() {
      instance = this;
      EventInvoker.register(this);
   }

   public static double direction(
      float f,
      float f2,
      float f3
   ) {
      if (f2
         < 0.0F) {
         f += 180.0F;
      }

      float f4 = 1.0F;
      if (f2
         < 0.0F) {
         f4 = -0.5F;
      }

      if (f2
         > 0.0F) {
         f4 = 0.5F;
      }

      if (f3
         > 0.0F) {
         f -= 90.0F
            * f4;
      }

      if (f3
         < 0.0F) {
         f += 90.0F
            * f4;
      }

      return Math.toRadians(
         (double)f
      );
   }

   public static void fixMovement(
      EventKeyboardInput event,
      float f
   ) {
      float x = event.getMovementForward();
      float x2 = event.getMovementSideways();
      if (x
            != 0.0F
         || x2
            != 0.0F) {
         double x3 = MathHelper.wrapDegrees(
            Math.toDegrees(
               direction(
                  f,
                  x,
                  x2
               )
            )
         );
         float x4 = 0.0F;
         float x5 = 0.0F;
         float x6 = Float.MAX_VALUE;

         for (float x7 = -1.0F;
            x7
               <= 1.0F;
            x7++
         ) {
            for (float x8 = -1.0F;
               x8
                  <= 1.0F;
               x8++
            ) {
               if (x7
                     != 0.0F
                  || x8
                     != 0.0F) {
                  double x9 = MathHelper.wrapDegrees(
                     Math.toDegrees(
                        direction(
                           f,
                           x7,
                           x8
                        )
                     )
                  );
                  float x10 = Math.abs(
                     MathHelper.wrapDegrees(
                        (float)(
                           x3
                              - x9
                        )
                     )
                  );
                  if (x10
                     < x6
                     )
                   {
                     x6 = x10;
                     x4 = x7;
                     x5 = x8;
                  }
               }
            }
         }

         event.setMovementForward(
            x4
         );
         event.setMovementSideways(
            x5
         );
      }
   }

   @EventLink
   public void onInput(
      EventKeyboardInput event
   ) {
      if (this.isRotating()) {
         fixMovement(
            event,
            MathHelper.wrapDegrees(mc.gameRenderer.getCamera().getYaw())
         );
      }
   }

   private void resetRotation() {
      Angle val = new Angle(
         FreeLookStorage.getFreeYaw(), FreeLookStorage.getFreePitch()
      );
      if (this.updateRotation(
         val,
         this.currentYawReturnSpeed(),
         this.currentPitchReturnSpeed()
      )) {
         this.stopRotation();
      }
   }

   @EventLink
   public void onEventTick(
      EventUpdate event
   ) {
      if (this.currentTask().equals(ClientOption_AYnqSxWF.AIM) && this.idleTicks() > this.currentTimeout()) {
         this.currentTask(ClientOption_AYnqSxWF.RESET);
      }

      if (this.currentTask().equals(ClientOption_AYnqSxWF.RESET)) {
         this.resetRotation();
      }

      this.idleTicks++;
   }

   public static void update(
      Angle val,
      float f,
      float f2,
      float f3,
      float f4,
      int n,
      int n2,
      boolean flag
   ) {
      RotationStorage val2 = instance;
      if (mc.player != null) {
         if (val2.currentPriority()
            <= n2
            )
          {
            if (val2.currentTask()
                  .equals(ClientOption_AYnqSxWF.IDLE)
               && !flag
               )
             {
               FreeLookStorage.setFreeYaw(mc.player.getYaw());
               FreeLookStorage.setFreePitch(mc.player.getPitch());
               FreeLookStorage.setActive(true);
            }

            val2.currentYawSpeed(
               f
            );
            val2.currentPitchSpeed(
               f2
            );
            val2.currentYawReturnSpeed(
               f3
            );
            val2.currentPitchReturnSpeed(
               f4
            );
            val2.currentTimeout(
               n
            );
            val2.currentPriority(
               n2
            );
            val2.currentTask(
               ClientOption_AYnqSxWF.AIM
            );
            val2.targetRotation(
               val
            );
            val2.updateRotation(
               val,
               f,
               f2
            );
         }
      }
   }

   public static void update(
      Angle val,
      float f,
      float f2,
      int n,
      int n2
   ) {
      update(
         val,
         f,
         f,
         f2,
         f2,
         n,
         n2,
         false
      );
   }

   public static void update(
      Angle val,
      float f,
      float f2,
      float f3,
      int n,
      int n2
   ) {
      update(
         val,
         f,
         f2,
         f3,
         f3,
         n,
         n2,
         false
      );
   }

   private boolean updateRotation(
      Angle val,
      float f,
      float f2
   ) {
      if (mc.player == null) {
         return false;
      } else {
         Angle val2 = new Angle(
            mc.player
         );
         float yaw = MathHelper.wrapDegrees(
            val.getYaw()
               - val2.getYaw()
         );
         float yaw2 = val.getPitch()
            - val2.getPitch();
         float yaw3 = Math.min(
            Math.abs(
               yaw
            ),
            f
         );
         float yaw4 = Math.min(
            Math.abs(
               yaw2
            ),
            f2
         );
         float yaw5 = mc.player
            .getYaw();
         yaw5 += MouseGCD.getFixedRotation(
            MathHelper.clamp(
               yaw,
               -yaw3,
               yaw3
            )
         );
         mc.player
            .setYaw(
               yaw5
            );
         float yaw6 = mc.player
            .getPitch();
         yaw6 += MouseGCD.getFixedRotation(
            MathHelper.clamp(
               yaw2,
               -yaw4,
               yaw4
            )
         );
         yaw6 = MathHelper.clamp(
            yaw6,
            -90.0F,
            90.0F
         );
         mc.player
            .setPitch(
               yaw6
            );
         Aura val3 = Aura.INSTANCE;
         if (FreeLookStorage.isActive()
            && val3
               != null
            && val3.isEnable()
            && val3.clientLookAssist
               .isState()) {
            FreeLookStorage.setFreeYaw(mc.player.getYaw());
            FreeLookStorage.setFreePitch(mc.player.getPitch());
         }

         this.idleTicks(0);
         return new Angle(mc.player)
               .getDelta(
                  val
               )
            < 1.0F;
      }
   }

   public void stopRotation() {
      this.currentTask(ClientOption_AYnqSxWF.IDLE);
      this.currentPriority(0);
      FreeLookStorage.setActive(false);
   }

   public boolean isRotating() {
      return !this.currentTask.equals(ClientOption_AYnqSxWF.IDLE);
   }

   @Generated
   public ClientOption_AYnqSxWF currentTask() {
      return this.currentTask;
   }

   @Generated
   public float currentYawSpeed() {
      return this.currentYawSpeed;
   }

   @Generated
   public float currentPitchSpeed() {
      return this.currentPitchSpeed;
   }

   @Generated
   public float currentYawReturnSpeed() {
      return this.currentYawReturnSpeed;
   }

   @Generated
   public float currentPitchReturnSpeed() {
      return this.currentPitchReturnSpeed;
   }

   @Generated
   public int currentPriority() {
      return this.currentPriority;
   }

   @Generated
   public int currentTimeout() {
      return this.currentTimeout;
   }

   @Generated
   public int idleTicks() {
      return this.idleTicks;
   }

   @Generated
   public Angle targetRotation() {
      return this.targetRotation;
   }

   @Generated
   public RotationStorage currentTask(
      ClientOption_AYnqSxWF val
   ) {
      this.currentTask = val;
      return this;
   }

   @Generated
   public RotationStorage currentYawSpeed(
      float f
   ) {
      this.currentYawSpeed = f;
      return this;
   }

   @Generated
   public RotationStorage currentPitchSpeed(
      float f
   ) {
      this.currentPitchSpeed = f;
      return this;
   }

   @Generated
   public RotationStorage currentYawReturnSpeed(
      float f
   ) {
      this.currentYawReturnSpeed = f;
      return this;
   }

   @Generated
   public RotationStorage currentPitchReturnSpeed(
      float f
   ) {
      this.currentPitchReturnSpeed = f;
      return this;
   }

   @Generated
   public RotationStorage currentPriority(
      int n
   ) {
      this.currentPriority = n;
      return this;
   }

   @Generated
   public RotationStorage currentTimeout(
      int n
   ) {
      this.currentTimeout = n;
      return this;
   }

   @Generated
   public RotationStorage idleTicks(
      int n
   ) {
      this.idleTicks = n;
      return this;
   }

   @Generated
   public RotationStorage targetRotation(
      Angle val
   ) {
      this.targetRotation = val;
      return this;
   }
}
