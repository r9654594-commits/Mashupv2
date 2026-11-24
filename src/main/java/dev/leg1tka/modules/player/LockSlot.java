package dev.leg1tka.modules.player;


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
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.mixin.SlotAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class LockSlot extends Module {
   public static LockSlot INSTANCE = new LockSlot();
   private final MultiBooleanSetting slots;

   public LockSlot() {
      super(
         "LockSlot",
         "Блокирует выброс предметов из выбранных слотов",
         Category.PLAYER
      );
      this.slots = new MultiBooleanSetting("Слоты", new BooleanSetting[]{
         new BooleanSetting("1", false),
         new BooleanSetting("2", false),
         new BooleanSetting(
         "3", false
      ),
         new BooleanSetting(
         "4", false
      ),
         new BooleanSetting(
         "5", false
      ),
         new BooleanSetting(
         "6", false
      ),
         new BooleanSetting(
         "7", false
      ),
         new BooleanSetting(
         "8", false
      ),
         new BooleanSetting(
         "9", false
      )
      });
      this.addSettings(new Setting[]{this.slots});
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (mc.player != null
         && val.getType()
            == EventPacket.Type.SEND) {
         if (!(mc.currentScreen instanceof HandledScreen)) {
            if (val.getPacket() instanceof PlayerActionC2SPacket val2
               )
             {
               if (val2.getAction()
                     == Action.DROP_ITEM
                  || val2.getAction()
                     == Action.DROP_ALL_ITEMS) {
                  if (this.isCurrentSlotLockedForDrop()) {
                     val.cancel();
                     this.sendLockedMessage(mc.player.getInventory().selectedSlot);
                  }
               }
            } else {
               if (val.getPacket() instanceof ClickSlotC2SPacket val3
                  && val3.getActionType()
                     == SlotActionType.THROW) {
                  int slot2 = this.getHotbarSlotFromClick(
                     val3.getSlot()
                  );
                  if (slot2
                        >= 0
                     && this.isHotbarSlotLocked(
                        slot2
                     )) {
                     val.cancel();
                     this.sendLockedMessage(
                        slot2
                     );
                  }
               }
            }
         }
      }
   }

   public boolean isCurrentSlotLockedForDrop() {
      if (!this.isEnable() || mc.player == null || mc.player.getMainHandStack().isEmpty()) {
         return false;
      } else {
         return mc.currentScreen instanceof HandledScreen ? false : this.isHotbarSlotLocked(mc.player.getInventory().selectedSlot);
      }
   }

   private boolean isHotbarSlotLocked(
      int val
   ) {
      return val
               >= 0
            && val
               < this.slots.getSettings().size()
         ? this.slots
            .getSettings()
            .get(
               val
            )
            .isState()
         : false;
   }

   private int getHotbarSlotFromClick(
      int val
   ) {
      if (mc.player != null
         && val
            >= 0
         && val
            < mc.player.currentScreenHandler.slots.size()) {
         Slot val2 = mc.player
            .currentScreenHandler
            .getSlot(
               val
            );
         SlotAccessor val3 = (SlotAccessor)val2;
         int n = val3.AspectDLC$getIndex();
         return val3.AspectDLC$getInventory()
                  == mc.player.getInventory()
               && n
                  >= 0
               && n
                  <= 8
            ? n
            : -1;
      } else {
         return -1;
      }
   }

   private void sendLockedMessage(
      int val
   ) {
      ChatUtil.sendMessage(
         "Выброс предмета из слота "
            + (
               val
                  + 1
            )
            + " заблокирован"
      );
   }
}
