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
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class NoClip extends Module {
   public static NoClip INSTANCE = new NoClip();

   public NoClip() {
      super("NoClip", Category.PLAYER);
   }

   @EventLink
   public void onUpdate(
      EventUpdate val
   ) {
      if (mc.player != null) {
         if (mc.player.age % 35 == 0) {
            ClientPlayNetworkHandler var10000 = mc.player.networkHandler;
            var10000.sendChatMessage("/gmsp");
         } else if (mc.player.age % 35
            == 2) {
            ClientPlayNetworkHandler var2 = mc.player.networkHandler;
            var2.sendChatMessage("/gms");
         }
      }
   }
}
