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
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class SeeInvisibles extends Module {
   public static final float INVISIBLE_ALPHA = 0.7F;
   public static final int INVISIBLE_COLOR = Math.round(178.5F) << 24 | 16777215;
   public static SeeInvisibles INSTANCE = new SeeInvisibles();

   public SeeInvisibles() {
      super("SeeInvisibles", Category.RENDER);
      String var1 = "SeeInvisibles";
   }

   @EventLink
   public void onUpdate(
      EventUpdate val2
   ) {
      if (mc.player != null && mc.world != null) {
         for (PlayerEntity player2 : mc.world
            .getPlayers()) {
            if (this.shouldRenderInvisible(
               player2
            )) {
               player2.setInvisible(
                  false
               );
            }
         }
      }
   }

   public boolean shouldRenderInvisible(
      PlayerEntity val
   ) {
      return this.isEnable()
         && mc.player != null
         && val
            != null
         && val
            != mc.player
         && (
            val.isInvisible()
               || val.hasStatusEffect(
                  StatusEffects.INVISIBILITY
               )
         );
   }
}
