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


import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventMoveInput;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.LinkedList;
import java.util.Queue;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class QueueHelper implements IMinecraft {
   public static final QueueHelper INSTANCE = new QueueHelper();
   private ClientOption_JJ71zVwg stage = ClientOption_JJ71zVwg.IDLE;
   private final Queue<Runnable> tasks = new LinkedList<>();
   private final Queue<Runnable> fasttasks = new LinkedList<>();

   private QueueHelper() {
      EventInvoker.register(this);
   }

   public static void queue(
      Runnable val
   ) {
      INSTANCE.tasks
         .add(
            val
         );
   }

   public static void executeFast(
      Runnable val
   ) {
      INSTANCE.fasttasks
         .add(
            val
         );
      if (INSTANCE.stage == ClientOption_JJ71zVwg.IDLE) {
         INSTANCE.stage = ClientOption_JJ71zVwg.FAST_STOP;
      }
   }

   public static boolean isBusy() {
      return INSTANCE.stage != ClientOption_JJ71zVwg.IDLE || !INSTANCE.tasks.isEmpty() || !INSTANCE.fasttasks.isEmpty();
   }

   @EventLink(
      priority = 100
   )
   public void onMoveInput(
      EventMoveInput event
   ) {
      if (this.stage == ClientOption_JJ71zVwg.STOP || this.stage == ClientOption_JJ71zVwg.FAST_STOP || this.stage == ClientOption_JJ71zVwg.SEND) {
         event.setForward(
            0.0F
         );
         event.setStrafe(
            0.0F
         );
         event.setJump(
            false
         );
         event.setSneak(
            false
         );
      }
   }

   @EventLink(
      priority = 100
   )
   public void onUpdate(
      EventUpdate event
   ) {
      if (mc.player == null) {
         this.stage = ClientOption_JJ71zVwg.IDLE;
         this.tasks.clear();
         this.fasttasks.clear();
      } else {
         KeyBinding[] var10000 = new KeyBinding[6];
         var10000[0] = mc.options.forwardKey;
         var10000[1] = mc.options.backKey;
         var10000[2] = mc.options.leftKey;
         var10000[3] = mc.options.rightKey;
         var10000[4] = mc.options.jumpKey;
         var10000[5] = mc.options.sprintKey;
         KeyBinding[] val = var10000;
         if (this.stage == ClientOption_JJ71zVwg.FAST_STOP) {
            this.disableKeys(
               val
            );
            this.stopSprint();

            while (!this.fasttasks.isEmpty()) {
               Runnable val2 = this.fasttasks
                  .poll();
               if (val2
                  != null) {
                  val2.run();
               }
            }

            this.stage = ClientOption_JJ71zVwg.RELEASE;
         } else if (this.stage == ClientOption_JJ71zVwg.STOP) {
            this.disableKeys(
               val
            );
            this.stopSprint();

            while (!this.tasks.isEmpty()) {
               Runnable val3 = this.tasks
                  .poll();
               if (val3
                  != null) {
                  val3.run();
               }
            }

            this.stage = ClientOption_JJ71zVwg.RELEASE;
         } else if (this.stage == ClientOption_JJ71zVwg.SEND) {
            this.disableKeys(
               val
            );

            while (!this.tasks.isEmpty()) {
               Runnable val4 = this.tasks
                  .poll();
               if (val4
                  != null) {
                  val4.run();
               }
            }

            this.stage = ClientOption_JJ71zVwg.RELEASE;
         } else if (this.stage == ClientOption_JJ71zVwg.RELEASE) {
            this.enableKeys(
               val
            );
            this.restoreSprintKey();
            this.stage = ClientOption_JJ71zVwg.IDLE;
         } else {
            if (!this.fasttasks.isEmpty()) {
               this.stage = ClientOption_JJ71zVwg.FAST_STOP;
               this.disableKeys(
                  val
               );
            } else if (!this.tasks.isEmpty()) {
               this.stage = ClientOption_JJ71zVwg.STOP;
               this.disableKeys(
                  val
               );
            }
         }
      }
   }

   private void disableKeys(
      KeyBinding[] val
   ) {
      for (KeyBinding val2 : val) {
         val2.setPressed(
            false
         );
      }
   }

   private void enableKeys(
      KeyBinding[] val
   ) {
      for (KeyBinding val2 : val) {
         val2.setPressed(
            InputUtil.isKeyPressed(
               mc.getWindow().getHandle(),
               val2.getDefaultKey()
                  .getCode()
            )
         );
      }
   }

   private void restoreSprintKey() {
      mc.options.sprintKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.sprintKey.getDefaultKey().getCode()));
   }

   private void stopSprint() {
      mc.options.sprintKey.setPressed(false);
      if (mc.player != null && AspectDLC.INSTANCE.serverStorage.isServerSprinting()) {
         mc.player.setSprinting(false);
         mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
      }
   }
}
