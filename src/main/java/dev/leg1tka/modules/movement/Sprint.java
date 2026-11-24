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
import net.minecraft.entity.effect.StatusEffects;

public class Sprint extends Module {
   public static Sprint INSTANCE = new Sprint();
   private static long pauseEndTime = 0L;

   public Sprint() {
      super("Sprint", Category.MOVEMENT);
      String var1 = "Sprint";
   }

   @EventLink
   public void onEvent(
      EventUpdate val2
   ) {
      if (mc.player != null) {
         if (System.currentTimeMillis() >= pauseEndTime) {
            mc.options.sprintKey.setPressed(false);
            boolean flag = FreeCam.INSTANCE
                  != null
               && FreeCam.INSTANCE.isEnable();
            mc.player
               .setSprinting(
                  !flag
                     && (!mc.player.isTouchingWater() || mc.player.isSubmergedInWater())
                     && !mc.player.isGliding()
                     && ((float)mc.player.getHungerManager().getFoodLevel() > 6.0F || mc.player.getAbilities().allowFlying)
                     && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                     && !mc.player.isUsingItem()
                     && (!mc.player.hasVehicle() || mc.player.getVehicle().canSprintAsVehicle() && mc.player.getVehicle().isLogicalSideForUpdatingMovement())
                     && (!mc.player.shouldSlowDown() || mc.player.isSubmergedInWater())
                     && mc.player.input.hasForwardMovement()
                     && !mc.player.horizontalCollision
                     && !mc.player.collidedSoftly
               );
         }
      }
   }

   @Override
   public void onDisable() {
      if (mc.player != null) {
         mc.player.setSprinting(false);
         mc.options.sprintKey.setPressed(false);
      }

      super.onDisable();
   }

   public static void pushPause(
      long val
   ) {
      pauseEndTime = System.currentTimeMillis()
         + val;
   }

   public static void pushPause(
      int val
   ) {
      pauseEndTime = System.currentTimeMillis()
         + (long)val;
   }

   public static void popPause() {
      pauseEndTime = 0L;
   }

   public static boolean isSprinting() {
      return mc.player != null && mc.player.isSprinting();
   }

   public boolean shouldKeepSprintInWater() {
      return false;
   }
}
