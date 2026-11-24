package dev.leg1tka.modules.misc;
import dev.leg1tka.modules.movement.Sprint;


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

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventBinding;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;

public final class RwHelper extends Module {
   public static final RwHelper INSTANCE = new RwHelper();
   private final BooleanSetting bypassgrim = new BooleanSetting("Обходить Grim", true);
   private final KeybindSetting afbind = new KeybindSetting(
      "Анти-Полёт", -1
   );
   private final KeybindSetting tpbind = new KeybindSetting(
      "Ловушка", -1
   );
   private int stage = 0;
   private Item sitem;
   private ClientOption_GGRRGytk stype = ClientOption_GGRRGytk.NONE;
   private int sslot = -1;
   private int oslot = -1;
   private boolean sblock;
   private boolean done;

   public RwHelper() {
      super(
         "RWHelper",
         "Помощник для ReallyWorld",
         Category.MISC
      );
      this.addSettings(new Setting[]{
         this.bypassgrim,
         this.afbind,
         this.tpbind
      });
      EventInvoker.register(this);
   }

   @EventLink
   public void onKey(
      EventBinding val
   ) {
      if (mc.currentScreen == null) {
         if (val.getKey()
            == this.afbind.getKey()) {
            if (this.bypassgrim.isState()) {
               QueueHelper.queue(this::runaf);
            } else {
               this.runaf();
            }
         } else if (val.getKey()
            == this.tpbind.getKey()) {
            if (this.bypassgrim.isState()) {
               QueueHelper.queue(() -> this.startspk(Items.HEART_OF_THE_SEA));
            } else {
               this.startspk(Items.HEART_OF_THE_SEA);
            }
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      this.updatespk();
   }

   private void runaf() {
      if (mc.player != null && mc.world != null) {
         Slot val = null;

         for (int i = 9;
            i
               <= 44;
            i++
         ) {
            if (mc.player
               .currentScreenHandler
               .getSlot(
                  i
               )
               .getStack()
               .isOf(Items.FIREWORK_STAR)) {
               val = mc.player
                  .currentScreenHandler
                  .getSlot(
                     i
                  );
               break;
            }
         }

         if (mc.player.getOffHandStack().isOf(Items.FIREWORK_STAR)) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.PRESS_SHIFT_KEY));
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.RELEASE_SHIFT_KEY));
         } else if (val
            != null) {
            boolean flag = false;
            if (mc.player.isSprinting()) {
               mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
               mc.player.setSprinting(false);
               mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
               if (!Sprint.INSTANCE.isEnable()) {
                  mc.options.sprintKey.setPressed(false);
               }

               flag = true;
            }

            int n = mc.player
               .currentScreenHandler
               .syncId;
            mc.interactionManager
               .clickSlot(
                  n,
                  val.id,
                  40,
                  SlotActionType.SWAP,
                  mc.player
               );
            mc.getNetworkHandler()
               .sendPacket(
                  new CloseHandledScreenC2SPacket(
                     n
                  )
               );
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.PRESS_SHIFT_KEY));
            mc.interactionManager
               .clickSlot(
                  n,
                  val.id,
                  40,
                  SlotActionType.SWAP,
                  mc.player
               );
            mc.getNetworkHandler()
               .sendPacket(
                  new CloseHandledScreenC2SPacket(
                     n
                  )
               );
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.RELEASE_SHIFT_KEY));
            if (flag
               )
             {
               mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
            }
         }
      }
   }

   private boolean startspk(
      Item val
   ) {
      if (this.stage == 0 && mc.player != null) {
         TypeHelper_1 val2 = this.resolvesrc(
            val
         );
         if (val2
            == null) {
            return false;
         } else {
            this.sitem = val;
            this.stype = val2.type();
            this.sslot = val2.slot();
            this.oslot = mc.player.getInventory().selectedSlot;
            this.done = false;
            this.stage = 1;
            return true;
         }
      } else {
         return false;
      }
   }

   private TypeHelper_1 resolvesrc(
      Item val
   ) {
      if (mc.player
         .getMainHandStack()
         .isOf(
            val
         )) {
         int n = mc.player
            .getInventory()
            .selectedSlot;
         return new TypeHelper_1(
            ClientOption_GGRRGytk.HOTBAR,
            n
               + 36
         );
      } else if (mc.player
         .getOffHandStack()
         .isOf(
            val
         )) {
         return new TypeHelper_1(ClientOption_GGRRGytk.OFFHAND, 45);
      } else {
         for (int i = 36;
            i
               <= 44;
            i++
         ) {
            if (mc.player
               .currentScreenHandler
               .getSlot(
                  i
               )
               .getStack()
               .isOf(
                  val
               )) {
               return new TypeHelper_1(
                  ClientOption_GGRRGytk.HOTBAR,
                  i
               );
            }
         }

         for (int i2 = 9;
            i2
               <= 35;
            i2++
         ) {
            if (mc.player
               .currentScreenHandler
               .getSlot(
                  i2
               )
               .getStack()
               .isOf(
                  val
               )) {
               return new TypeHelper_1(
                  ClientOption_GGRRGytk.INVENTORY,
                  i2
               );
            }
         }

         return null;
      }
   }

   private void updatespk() {
      if (this.stage != 0 && mc.player != null) {
         if (this.stage == 1) {
            this.stopmove();
            this.stage = 2;
         } else if (this.stage == 2) {
            this.stopmove();
            this.runspk();
            this.stage = 3;
         } else {
            if (this.stage == 3) {
               this.freemove();
               this.resetspk();
            }
         }
      }
   }

   private void stopmove() {
      mc.options.sprintKey.setPressed(false);
      mc.player.setSprinting(false);
      if (!this.sblock) {
         this.sblock = true;
         mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
         if (Sprint.INSTANCE.isEnable()) {
            mc.options.sprintKey.setPressed(false);
         }
      }
   }

   private void runspk() {
      if (this.sitem != null && mc.player.currentScreenHandler != null) {
         if (this.stype == ClientOption_GGRRGytk.OFFHAND) {
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
            this.done = true;
         } else if (this.stype == ClientOption_GGRRGytk.HOTBAR) {
            int slot2 = this.sslot
               - 36;
            mc.player.getInventory().selectedSlot = slot2;
            mc.interactionManager.syncSelectedSlot();
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.getInventory().selectedSlot = this.oslot;
            mc.interactionManager.syncSelectedSlot();
            this.done = true;
         } else {
            if (this.stype == ClientOption_GGRRGytk.INVENTORY) {
               int n = mc.player
                  .currentScreenHandler
                  .syncId;
               mc.interactionManager
                  .clickSlot(
                     n,
                     this.sslot,
                     this.oslot,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler()
                  .sendPacket(
                     new CloseHandledScreenC2SPacket(
                        n
                     )
                  );
               mc.player.getInventory().selectedSlot = this.oslot;
               mc.interactionManager.syncSelectedSlot();
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               mc.interactionManager
                  .clickSlot(
                     n,
                     this.sslot,
                     this.oslot,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler()
                  .sendPacket(
                     new CloseHandledScreenC2SPacket(
                        n
                     )
                  );
               this.done = true;
            }
         }
      }
   }

   private void freemove() {
      this.sblock = false;
      mc.player.getInventory().selectedSlot = this.oslot;
      mc.interactionManager.syncSelectedSlot();
      mc.options.sprintKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.sprintKey.getDefaultKey().getCode()));
   }

   private void resetspk() {
      this.stage = 0;
      this.sitem = null;
      this.stype = ClientOption_GGRRGytk.NONE;
      this.sslot = -1;
      this.oslot = -1;
      this.sblock = false;
      this.done = false;
   }

   @Override
   public void onDisable() {
      if (mc.player != null && this.done) {
         mc.player.getInventory().selectedSlot = this.oslot;
         mc.interactionManager.syncSelectedSlot();
      }

      this.resetspk();
      if (mc.player != null) {
         mc.options.sprintKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.sprintKey.getDefaultKey().getCode()));
      }

      super.onDisable();
   }
}
