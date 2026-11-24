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
import dev.leg1tka.mixin.ILivingEntity;

public class NoJumpDelay extends Module {
   public static NoJumpDelay INSTANCE = new NoJumpDelay();

   public NoJumpDelay() {
      super(
         "NoJumpDelay",
         "Убирает задержку на прыжок",
         Category.PLAYER
      );
   }

   @EventLink
   public void onEvent(
      EventUpdate val
   ) {
      if (mc.player != null && mc.world != null) {
         ((ILivingEntity)mc.player).setJumpingCooldown(0);
      }
   }
}
