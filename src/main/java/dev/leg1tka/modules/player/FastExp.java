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
import dev.leg1tka.mixin.IMinecraftClientAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FastExp extends Module {
   public static FastExp INSTANCE = new FastExp();

   public FastExp() {
      super(
         "FastExp",
         "Позволяет бросать пузырьки опыта без задержки",
         Category.PLAYER
      );
   }

   @EventLink
   public void onUpdate(
      EventUpdate val
   ) {
      if (mc.player != null) {
         ItemStack stack = mc.player
            .getMainHandStack();
         if (stack.isOf(
            Items.EXPERIENCE_BOTTLE
         )) {
            ((IMinecraftClientAccessor)mc).setItemUseCooldown(0);
         }
      }
   }
}
