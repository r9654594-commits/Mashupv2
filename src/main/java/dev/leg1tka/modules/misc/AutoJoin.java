package dev.leg1tka.modules.misc;


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
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public final class AutoJoin extends Module {
   public static AutoJoin INSTANCE = new AutoJoin();
   private static final long CLICK_DELAY_MS = 30L;
   private static final int NEXT_PAGE_SLOT = 44;
   private static final int MAX_PAGE_SWITCHES = 5;
   private final NumberSetting grief = new NumberSetting("Гриф", 5.0F, 1.0F, 64.0F, 1.0F);
   private final FinishedHelper clickTimer = new FinishedHelper();
   private final FinishedHelper compassTimer = new FinishedHelper();
   private boolean joining;
   private int pageSwitches;
   private int targetGrief;

   public AutoJoin() {
      super(
         "AutoJoin",
         "Автоматически заходит на выбранный гриф",
         Category.MISC
      );
      this.addSettings(new Setting[]{this.grief});
   }

   public void startJoinTo(
      int val
   ) {
      this.grief
         .setValue(
            (float)val
         );
      if (!this.isEnable()) {
         this.toggle();
      } else {
         this.startJoin();
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.startJoin();
   }

   @Override
   public void onDisable() {
      this.joining = false;
      this.pageSwitches = 0;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      if (this.joining && mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (!(mc.currentScreen instanceof GenericContainerScreen)) {
            this.openServerSelector(false);
         } else {
            this.handleServerMenu();
         }
      }
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (this.joining
         && mc.player != null
         && mc.world != null
         && val.getType()
            == EventPacket.Type.RECEIVE) {
         if (val.getPacket() instanceof GameJoinS2CPacket
            )
          {
            ChatUtil.sendMessage("Вход на гриф #" + this.targetGrief + ": успешно");
            this.joining = false;
            this.pageSwitches = 0;
         } else if (val.getPacket() instanceof GameMessageS2CPacket val2
            )
          {
            String str = val2.content()
               .getString();
            if (str.contains(
               "Подождите несколько секунд перед повторным подключением"
            )) {
               val.cancel();
            } else if (str.contains(
               "К сожалению сервер переполнен"
            )) {
               val.cancel();
               ChatUtil.sendMessage("Вход на гриф #" + this.targetGrief + ": неудачно");
            } else {
               this.openServerSelector(false);
            }
         }
      }
   }

   private void startJoin() {
      this.joining = true;
      this.pageSwitches = 0;
      this.targetGrief = Math.round(this.grief.get());
      this.clickTimer.reset();
      this.compassTimer.reset();
      if (mc.player != null && mc.world != null) {
         this.openServerSelector(true);
      }
   }

   private void openServerSelector(
      boolean val
   ) {
      if (val
         || this.compassTimer.finished(30L)) {
         if (mc.player != null && mc.interactionManager != null && mc.getNetworkHandler() != null) {
            int n = mc.player
               .getInventory()
               .selectedSlot;
            int slot2 = this.findCompassSlot();
            if (slot2
               != -1) {
               this.pageSwitches = 0;
               mc.player.getInventory().selectedSlot = slot2;
               mc.getNetworkHandler()
                  .sendPacket(
                     new UpdateSelectedSlotC2SPacket(
                        slot2
                     )
                  );
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               mc.player.getInventory().selectedSlot = n;
               mc.getNetworkHandler()
                  .sendPacket(
                     new UpdateSelectedSlotC2SPacket(
                        n
                     )
                  );
               this.compassTimer.reset();
            }
         }
      }
   }

   private int findCompassSlot() {
      if (mc.player == null) {
         return -1;
      } else {
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
                  .getItem()
               == Items.COMPASS) {
               return i;
            }
         }

         return -1;
      }
   }

   private void handleServerMenu() {
      if (mc.currentScreen instanceof GenericContainerScreen val
         )
       {
         if (this.clickTimer.finished(30L)) {
            String str = val.getTitle()
               .getString();
            ScreenHandler hand = val.getScreenHandler();
            if (str.contains(
               "Выбор сервера"
            )) {
               this.clickSlot(
                  hand,
                  21
               );
               this.pageSwitches = 0;
               this.clickTimer.reset();
            } else if (!this.clickTargetGriefIfVisible(
               hand
            )) {
               if (this.targetGrief > 36
                  && this.pageSwitches < 5) {
                  Slot val2 = this.getSlot(
                     hand,
                     44
                  );
                  if (val2
                        != null
                     && val2.hasStack()
                     )
                   {
                     this.clickSlot(
                        hand,
                        44
                     );
                     this.pageSwitches++;
                     this.clickTimer.reset();
                  }
               }
            }
         }
      }
   }

   private boolean clickTargetGriefIfVisible(
      ScreenHandler val
   ) {
      String str = "ГРИФ #"
         + this.targetGrief
         + " (1.16.5+)";
      String str2 = "ГРИФ #"
         + this.targetGrief;

      for (int i = 0;
         i
            < val.slots
               .size();
         i++
      ) {
         Slot val2 = val.getSlot(
            i
         );
         if (val2
               != null
            && val2.hasStack()
            )
          {
            String str3 = val2.getStack()
               .getName()
               .getString();
            if (str3.equalsIgnoreCase(
                  str
               )
               || str3.toUpperCase()
                  .contains(
                     str2
                  )) {
               this.clickSlot(
                  val,
                  i
               );
               this.pageSwitches = 0;
               this.clickTimer.reset();
               return true;
            }
         }
      }

      return false;
   }

   private void clickSlot(
      ScreenHandler val,
      int val2
   ) {
      if (mc.player != null && mc.interactionManager != null) {
         if (val2
               >= 0
            && val2
               < val.slots
                  .size()) {
            mc.interactionManager
               .clickSlot(
                  val.syncId,
                  val2,
                  0,
                  SlotActionType.PICKUP,
                  mc.player
               );
         }
      }
   }

   private Slot getSlot(
      ScreenHandler val,
      int val2
   ) {
      return val2
               >= 0
            && val2
               < val.slots
                  .size()
         ? val.getSlot(
            val2
         )
         : null;
   }
}
