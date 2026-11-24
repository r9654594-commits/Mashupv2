package dev.leg1tka.modules.combat;


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
import dev.leg1tka.api.events.implement.EventAttackEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;

public class PacketCriticals extends Module {
   public static PacketCriticals INSTANCE = new PacketCriticals();

   public PacketCriticals() {
      super("PacketCriticals", Category.COMBAT);
   }

   @EventLink
   public void onAttack(
      EventAttackEntity val
   ) {
      if (mc.player != null && mc.world != null) {
         boolean flag = GetaicooldownHelper.isInCobweb();
         double d = mc.player
            .getX();
         double d2 = mc.player
            .getY();
         double d3 = mc.player
            .getZ();
         if (flag
            )
          {
            mc.player
               .networkHandler
               .sendPacket(
                  new PositionAndOnGround(
                     d,
                     d2
                        + 0.003,
                     d3,
                     false,
                     false
                  )
               );
            mc.player
               .networkHandler
               .sendPacket(
                  new PositionAndOnGround(
                     d,
                     d2,
                     d3,
                     false,
                     false
                  )
               );
         }
      }
   }
}
