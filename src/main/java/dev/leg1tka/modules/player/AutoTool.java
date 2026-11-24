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
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.hit.BlockHitResult;

public class AutoTool extends Module {
   public static AutoTool INSTANCE = new AutoTool();
   private final BooleanSetting packet;
   private final BooleanSetting silent;
   private int previousSlot;

   public AutoTool() {
      super("AutoTool", Category.PLAYER);
      String var1 = "AutoTool";
      this.packet = new BooleanSetting("Пакетный", false);
      this.silent = new BooleanSetting("Видно только для других людей", false);
      this.previousSlot = -1;
      this.addSettings(new Setting[]{
         this.packet,
         this.silent
      });
   }

   @EventLink
   public void onEvent(
      EventUpdate val2
   ) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null && !mc.player.isCreative()) {
         if (mc.interactionManager.isBreakingBlock()) {
            if (this.previousSlot == -1) {
               this.previousSlot = mc.player.getInventory().selectedSlot;
            }

            int n = this.findOptimalTool();
            if (n
               != -1) {
               this.switchToSlot(
                  n
               );
            }
         } else if (this.previousSlot != -1) {
            this.switchToSlot(this.previousSlot);
            this.previousSlot = -1;
         }
      } else {
         this.previousSlot = -1;
      }
   }

   private void switchToSlot(
      int val
   ) {
      if (val
            >= 0
         && val
            <= 8) {
         if (mc.player.getInventory().selectedSlot
            != val
            )
          {
            if (this.silent.isState()) {
               mc.getNetworkHandler()
                  .sendPacket(
                     new UpdateSelectedSlotC2SPacket(
                        val
                     )
                  );
            } else if (this.packet.isState()) {
               mc.player.getInventory().selectedSlot = val;
               mc.getNetworkHandler()
                  .sendPacket(
                     new UpdateSelectedSlotC2SPacket(
                        val
                     )
                  );
            } else {
               mc.player.getInventory().selectedSlot = val;
            }
         }
      }
   }

   private int findOptimalTool() {
      if (mc.crosshairTarget instanceof BlockHitResult val
         )
       {
         BlockState var3 = mc.world
            .getBlockState(
               val.getBlockPos()
            );
         return this.findBestToolSlot(var3);
      } else {
         return -1;
      }
   }

   private int findBestToolSlot(
      BlockState val
   ) {
      int n = -1;
      float f = 1.0F;

      for (int i = 0;
         i
            < 9;
         i++
      ) {
         float y = mc.player
            .getInventory()
            .getStack(
               i
            )
            .getMiningSpeedMultiplier(
               val
            );
         if (y
            > f
            )
          {
            f = y;
            n = i;
         }
      }

      return n;
   }

   @Override
   public void onDisable() {
      this.previousSlot = -1;
      super.onDisable();
   }
}
