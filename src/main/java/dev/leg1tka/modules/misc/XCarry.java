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
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class XCarry extends Module {
   public static XCarry INSTANCE = new XCarry();
   public BooleanSetting autoDisable = new BooleanSetting("Авто выкл", true);
   private boolean wasInInventory = false;

   public XCarry() {
      super(
         "XCarry",
         "Дополнительные слоты",
         Category.MISC
      );
      this.addSettings(new Setting[]{this.autoDisable});
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (mc.player != null && mc.world != null) {
         if (val.getPacket() instanceof CloseHandledScreenC2SPacket
            && mc.currentScreen instanceof InventoryScreen) {
            val.cancel();
            this.wasInInventory = true;
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val2
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.wasInInventory && mc.currentScreen == null) {
            if (this.autoDisable.isState()) {
               this.toggle();
            }

            this.wasInInventory = false;
         }
      }
   }
}
