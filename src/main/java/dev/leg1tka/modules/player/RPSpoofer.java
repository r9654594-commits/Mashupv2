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
import java.util.UUID;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

public class RPSpoofer extends Module {
   public static RPSpoofer INSTANCE = new RPSpoofer();

   public RPSpoofer() {
      super(
         "RPSpoofer",
         "Убирает ресурс-пак сервера",
         Category.PLAYER
      );
   }

   @EventLink
   public void onReceivePacket(
      EventPacket val
   ) {
      if (val.getPacket() instanceof ResourcePackSendS2CPacket val2
         && (this.isEnable() || GetconnectionsHelper.shouldBypassResourcePacks())) {
         UUID uuid = val2.id();
         mc.getNetworkHandler()
            .sendPacket(
               new ResourcePackStatusC2SPacket(
                  uuid,
                  Status.ACCEPTED
               )
            );
         mc.getNetworkHandler()
            .sendPacket(
               new ResourcePackStatusC2SPacket(
                  uuid,
                  Status.SUCCESSFULLY_LOADED
               )
            );
         val.setCancelled(
            true
         );
      }
   }
}
