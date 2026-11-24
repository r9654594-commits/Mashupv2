package dev.leg1tka.modules.render;


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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright extends Module {
   public static FullBright INSTANCE = new FullBright();

   public FullBright() {
      super(
         "FullBright",
         "Всегда светло",
         Category.RENDER
      );
   }

   @EventLink
   public void onUpdate(
      EventUpdate val
   ) {
      if (mc.player != null && mc.world != null) {
         mc.player
            .addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 777, 1));
      }
   }

   @Override
   public void onDisable() {
      if (mc.player != null && mc.world != null) {
         mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
         super.onDisable();
      }
   }
}
