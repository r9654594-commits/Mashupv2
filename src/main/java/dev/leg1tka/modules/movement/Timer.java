package dev.leg1tka.modules.movement;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventGameUpdate;
import dev.leg1tka.api.events.implement.EventMoveInput;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventTickPre;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;

public class Timer extends Module {
   public static Timer INSTANCE = new Timer();
   private static final float CHARGE_TIMER = 0.05F;
   private static final float BOOST_START = 1.0F;
   private final NumberSetting boostSpeed = new NumberSetting(
      "Скорость буста", 1.5F, 1.2F, 2.2F, 0.05F
   );
   private final NumberSetting chargeTime = new NumberSetting(
      "Время накопления", 1.7F, 0.4F, 4.0F, 0.05F
   );
   private final NumberSetting boostTime = new NumberSetting(
      "Время буста", 3.6F, 1.0F, 8.0F, 0.1F
   );
   private final NumberSetting rampTime = new NumberSetting(
      "Время разгона", 0.7F, 0.1F, 2.0F, 0.05F
   );
   private final NumberSetting hopDelay = new NumberSetting("Задержка hop", 0.32F, 0.0F, 1.5F, 0.02F);
   private final NumberSetting jumps = new NumberSetting("Прыжки", 8.0F, 1.0F, 20.0F, 1.0F);
   private final NumberSetting hopGroundTicks = new NumberSetting(
      "Тики земли hop", 3.0F, 1.0F, 8.0F, 1.0F
   );
   private final NumberSetting setbackCd = new NumberSetting(
      "Кулдаун setback", 1.2F, 0.2F, 4.0F, 0.1F
   );
   private ClientOption_UleLwErY phase = ClientOption_UleLwErY.CHARGING;
   private boolean cycleActive;
   private boolean airborne;
   private int completedJumps;
   private int groundTicks;
   private long phaseStartedAt;
   private long setbackCooldownUntil;
   private final Queue<CommonPongC2SPacket> delayedTransactions = new ConcurrentLinkedQueue<>();

   public Timer() {
      super(
         "Timer",
         "Замедляется, накапливает инерцию и плавно разгоняет (настройки в GUI)",
         Category.MOVEMENT
      );
      this.addSettings(new Setting[]{
         this.boostSpeed,
         this.chargeTime,
         this.boostTime,
         this.rampTime,
         this.hopDelay,
         this.jumps,
         this.hopGroundTicks,
         this.setbackCd
      });
   }

   private static long secToNanos(
      float val
   ) {
      float f = MathHelper.clamp(
         val,
         0.0F,
         30.0F
      );
      return (long)(
         f
            * 1.0E9F
      );
   }

   private int jumpsLimit() {
      return MathHelper.clamp(Math.round(this.jumps.get()), 1, 20);
   }

   private int hopGroundNeed() {
      return MathHelper.clamp(Math.round(this.hopGroundTicks.get()), 1, 8);
   }

   @Override
   public void onEnable() {
      this.stopCycle();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.stopCycle();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val4
   ) {
      if (mc.player != null && this.cycleActive && this.phase == ClientOption_UleLwErY.BOOSTING) {
         if (mc.player.verticalCollision) {
            if (this.airborne) {
               this.airborne = false;
               this.completedJumps++;
            }
         } else {
            this.airborne = true;
            if (this.completedJumps >= this.jumpsLimit() && mc.player.getVelocity().y <= 0.0) {
               this.beginCharging();
            }
         }
      }
   }

   @EventLink
   public void onGameUpdate(
      EventGameUpdate val4
   ) {
      if (mc.player != null && SetmotionHelper.isMoving()) {
         long val = System.nanoTime();
         if (!this.cycleActive) {
            this.cycleActive = true;
            this.beginCharging();
         } else {
            long val2 = secToNanos(
               this.chargeTime.get()
            );
            long val3 = secToNanos(
               this.boostTime.get()
            );
            if (this.phase == ClientOption_UleLwErY.CHARGING) {
               if (val
                  < this.setbackCooldownUntil) {
                  SettimerHelper.setTimer(0.05F);
                  return;
               }

               if (val
                     - this.phaseStartedAt
                  >= val2
                  )
                {
                  this.beginBoosting();
               } else {
                  SettimerHelper.setTimer(0.05F);
               }
            } else if (this.phase == ClientOption_UleLwErY.BOOSTING) {
               if (val
                     - this.phaseStartedAt
                  >= val3
                  )
                {
                  this.beginCharging();
               } else {
                  this.applyBoostRamp(
                     val
                  );
               }
            }
         }
      } else {
         this.stopCycle();
      }
   }

   private void applyBoostRamp(
      long val
   ) {
      long val2 = val
         - this.phaseStartedAt;
      float f = MathHelper.clamp(
         this.boostSpeed.get(), 1.2F, 2.2F
      );
      long val3 = Math.max(
         1L, secToNanos(this.rampTime.get())
      );
      float f2 = MathHelper.clamp(
         (float)val2
            / (float)val3,
         0.0F,
         1.0F
      );
      f2 = 1.0F
         - (
               1.0F
                  - f2
            )
            * (
               1.0F
                  - f2
            );
      float f3 = 1.0F
         + (
               f
                  - 1.0F
            )
            * f2;
      SettimerHelper.setTimer(
         f3
      );
   }

   @EventLink
   public void onTickPre(
      EventTickPre val4
   ) {
      if (mc.player == null || mc.world == null) {
         this.stopCycle();
      }
   }

   @EventLink
   public void onMoveInput(
      EventMoveInput val
   ) {
      if (mc.player != null
         && this.phase == ClientOption_UleLwErY.BOOSTING
         && this.isMovingInput(
            val
         )) {
         long val2 = System.nanoTime()
            - this.phaseStartedAt;
         long val3 = secToNanos(
            this.hopDelay.get()
         );
         if (val2
            < val3
            )
          {
            this.groundTicks = 0;
            if (mc.options != null) {
               mc.options.sprintKey.setPressed(true);
            }
         } else {
            this.groundTicks = mc.player.verticalCollision ? this.groundTicks + 1 : 0;
            if (mc.options != null) {
               mc.options.sprintKey.setPressed(true);
            }

            if (this.groundTicks >= this.hopGroundNeed()) {
               val.setJump(
                  true
               );
            }
         }
      } else {
         this.groundTicks = 0;
      }
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (val.getType()
            == EventPacket.Type.SEND
         && this.cycleActive
         && val.getPacket() instanceof CommonPongC2SPacket val2
         )
       {
         this.delayedTransactions
            .add(
               val2
            );
         val.cancel();
      } else {
         if (val.getType()
               == EventPacket.Type.RECEIVE
            && this.cycleActive
            && val.getPacket() instanceof PlayerPositionLookS2CPacket
            )
          {
            this.setbackCooldownUntil = System.nanoTime() + secToNanos(this.setbackCd.get());
            this.beginCharging();
         }
      }
   }

   private boolean isMovingInput(
      EventMoveInput val
   ) {
      return val.getForward()
            != 0.0F
         || val.getStrafe()
            != 0.0F;
   }

   private void beginCharging() {
      this.flushTransactions();
      this.phase = ClientOption_UleLwErY.CHARGING;
      this.airborne = false;
      this.completedJumps = 0;
      this.groundTicks = 0;
      this.phaseStartedAt = System.nanoTime();
      SettimerHelper.setTimer(0.05F);
   }

   private void beginBoosting() {
      this.phase = ClientOption_UleLwErY.BOOSTING;
      this.airborne = false;
      this.completedJumps = 0;
      this.groundTicks = 0;
      this.phaseStartedAt = System.nanoTime();
      SettimerHelper.setTimer(1.0F);
   }

   private void stopCycle() {
      this.flushTransactions();
      this.cycleActive = false;
      this.phase = ClientOption_UleLwErY.CHARGING;
      this.airborne = false;
      this.completedJumps = 0;
      this.groundTicks = 0;
      this.phaseStartedAt = 0L;
      this.setbackCooldownUntil = 0L;
      SettimerHelper.resetTimer();
   }

   private void flushTransactions() {
      if (mc != null && mc.getNetworkHandler() != null && mc.player != null) {
         CommonPongC2SPacket val;
         while (
            (
                  val = this.delayedTransactions
                     .poll()
               )
               != null
         ) {
            SendsilentpacketHelper.sendSilentPacket(
               val
            );
         }
      } else {
         this.delayedTransactions.clear();
      }
   }
}
