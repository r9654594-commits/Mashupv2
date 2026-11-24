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
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;

public class GrimNoFall extends Module {
   public static GrimNoFall INSTANCE = new GrimNoFall();

   public GrimNoFall() {
      super("GrimNoFall", Category.MOVEMENT);
   }

   @EventLink
   public void onUpdate(
      EventUpdate val
   ) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         if (!mc.player.isOnGround() && mc.player.fallDistance > 1.0F) {
            mc.getNetworkHandler()
               .sendPacket(
                  new Full(
                     mc.player.getX(),
                     mc.player.getY() + 1.0E-9,
                     mc.player.getZ(),
                     mc.player.getYaw(),
                     mc.player.getPitch(),
                     true,
                     false
                  )
               );
            mc.player.onLanding();
         }
      }
   }
}
