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


import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class UseHelper implements IMinecraft {
   public static final UseHelper INSTANCE = new UseHelper();
   private static final int maxticks = 5;
   private ClientOption_DPvyq3Og st = ClientOption_DPvyq3Og.IDLE;
   private Item curitem;
   private int origslot = -1;
   private int tgthotbarslot = -1;
   private int tgtinvslot = -1;
   private int ticks = 0;

   private UseHelper() {
      EventInvoker.register(this);
   }

   public static boolean use(
      Item item
   ) {
      return INSTANCE.start(
         item
      );
   }

   public static boolean hasItem(
      Item item
   ) {
      return findSlot(
            item
         )
         != -1;
   }

   public static boolean isBusy() {
      return INSTANCE.st != ClientOption_DPvyq3Og.IDLE;
   }

   public static int findSlot(
      Item item
   ) {
      return INSTANCE.search(
         item,
         0,
         35
      );
   }

   private boolean start(
      Item item
   ) {
      if (mc.player != null && this.st == ClientOption_DPvyq3Og.IDLE) {
         if (mc.player
            .getMainHandStack()
            .isOf(
               item
            )) {
            this.sendUse();
            return true;
         } else if (mc.player
            .getOffHandStack()
            .isOf(
               item
            )) {
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
            return true;
         } else {
            int slot = this.search(
               item,
               0,
               8
            );
            if (slot
               != -1) {
               this.origslot = mc.player.getInventory().selectedSlot;
               mc.player.getInventory().selectedSlot = slot;
               mc.getNetworkHandler()
                  .sendPacket(
                     new UpdateSelectedSlotC2SPacket(
                        slot
                     )
                  );
               this.sendUse();
               mc.player.getInventory().selectedSlot = this.origslot;
               mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.origslot));
               return true;
            } else {
               int slot2 = this.search(
                  item,
                  9,
                  35
               );
               if (slot2
                  == -1) {
                  return false;
               } else {
                  this.curitem = item;
                  this.origslot = mc.player.getInventory().selectedSlot;
                  this.tgtinvslot = slot2;
                  this.ticks = 0;
                  this.tgthotbarslot = this.findFreeSlot();
                  mc.interactionManager
                     .clickSlot(
                        0,
                        slot2,
                        this.tgthotbarslot,
                        SlotActionType.SWAP,
                        mc.player
                     );
                  mc.player.getInventory().selectedSlot = this.tgthotbarslot;
                  mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.tgthotbarslot));
                  this.st = ClientOption_DPvyq3Og.SWAPTO;
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate event
   ) {
      if (this.st != ClientOption_DPvyq3Og.IDLE && mc.player != null) {
         this.ticks++;
         if (this.ticks > 5) {
            this.cleanup();
         } else {
            if (this.st == ClientOption_DPvyq3Og.SWAPTO) {
               if (this.isSwappedTo()) {
                  this.ticks = 0;
                  this.st = ClientOption_DPvyq3Og.USEANDBACK;
               }
            } else if (this.st == ClientOption_DPvyq3Og.USEANDBACK) {
               this.sendUse();
               mc.player.getInventory().selectedSlot = this.origslot;
               mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.origslot));
               mc.interactionManager.clickSlot(0, this.tgtinvslot, this.tgthotbarslot, SlotActionType.SWAP, mc.player);
               this.ticks = 0;
               this.st = ClientOption_DPvyq3Og.SWAPBACK;
            } else if (this.st == ClientOption_DPvyq3Og.SWAPBACK && this.isSwappedBack()) {
               this.reset();
            }
         }
      }
   }

   private boolean isSwappedTo() {
      return this.tgthotbarslot >= 0
         && this.curitem != null
         && mc.player.getInventory().selectedSlot == this.tgthotbarslot
         && mc.player.getMainHandStack().isOf(this.curitem);
   }

   private boolean isSwappedBack() {
      return this.tgthotbarslot >= 0
         && this.tgtinvslot >= 0
         && this.curitem != null
         && mc.player.getInventory().selectedSlot == this.origslot
         && !mc.player.getMainHandStack().isOf(this.curitem);
   }

   private void sendUse() {
      mc.interactionManager
         .sendSequencedPacket(
            mc.world,
            val -> new PlayerInteractItemC2SPacket(
                  Hand.MAIN_HAND,
                  val,
                  mc.player.getYaw(),
                  mc.player.getPitch()
               )
         );
   }

   private int search(
      Item item,
      int n,
      int n2
   ) {
      if (mc.player == null) {
         return -1;
      } else {
         for (int i = n2;
            i
               >= n;
            i--
         ) {
            if (i
                  < mc.player.getInventory().size()
               && mc.player
                  .getInventory()
                  .getStack(
                     i
                  )
                  .isOf(
                     item
                  )) {
               return i;
            }
         }

         return -1;
      }
   }

   private int findFreeSlot() {
      for (int i = 0;
         i
            < 9;
         i++
      ) {
         if (mc.player
            .getInventory()
            .getStack(
               i
            )
            .isEmpty()) {
            return i;
         }
      }

      return 8;
   }

   private void cleanup() {
      if (mc.player == null) {
         this.reset();
      } else {
         if (this.st == ClientOption_DPvyq3Og.SWAPBACK
            && this.tgtinvslot >= 0
            && this.tgthotbarslot >= 0
            && this.curitem != null
            && mc.player.getInventory().getStack(this.tgthotbarslot).isOf(this.curitem)) {
            mc.player.getInventory().selectedSlot = this.origslot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.origslot));
            mc.interactionManager.clickSlot(0, this.tgtinvslot, this.tgthotbarslot, SlotActionType.SWAP, mc.player);
         } else if (this.origslot >= 0) {
            mc.player.getInventory().selectedSlot = this.origslot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.origslot));
         }

         this.reset();
      }
   }

   private void reset() {
      this.st = ClientOption_DPvyq3Og.IDLE;
      this.curitem = null;
      this.origslot = -1;
      this.tgtinvslot = -1;
      this.tgthotbarslot = -1;
      this.ticks = 0;
   }
}
