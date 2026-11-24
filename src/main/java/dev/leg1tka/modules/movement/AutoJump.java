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
import dev.leg1tka.modules.combat.Aura;

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;

public class AutoJump extends Module {
   public static AutoJump INSTANCE = new AutoJump();

   public AutoJump() {
      super(
         "AutoJump",
         "Прыгает автоматически при ауре",
         Category.MOVEMENT
      );
   }

   @EventLink
   public void onUpdate(
      EventUpdate val2
   ) {
      if (mc.player != null && mc.world != null) {
         Aura val = ModuleManager.aura;
         if (val
               != null
            && val.isEnable()
            )
          {
            if (val.getTarget()
                  != null
               && mc.player.isOnGround()) {
               mc.player.jump();
            }
         }
      }
   }
}
