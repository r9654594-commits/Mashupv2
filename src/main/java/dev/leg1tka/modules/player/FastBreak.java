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
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FastBreak extends Module {
   public static FastBreak INSTANCE = new FastBreak();
   private final NumberSetting speed = new NumberSetting("Ускорение", 0.5F, 0.3F, 1.0F, 0.1F);

   public FastBreak() {
      super("FastBreak", "Ускоряет ломание блоков", Category.PLAYER);
      this.addSettings(new Setting[]{this.speed});
   }

   @EventLink
   public void onUpdate(
      EventUpdate val8
   ) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (mc.crosshairTarget instanceof BlockHitResult val
            )
          {
            if (mc.options.attackKey.isPressed()) {
               accelerateClientBreak(
                  mc.interactionManager,
                  mc.player,
                  mc.world,
                  val.getBlockPos(),
                  val.getSide(),
                  this.speed.get(),
                  true
               );
            }
         }
      }
   }

   public float getSpeed() {
      return this.speed.get();
   }

   public static int getExtraTicks(
      float val
   ) {
      return Math.max(
         1,
         Math.round(
            Math.max(
                  0.3F,
                  val
               )
               / 0.35F
         )
      );
   }

   public static boolean accelerateClientBreak(
      ClientPlayerInteractionManager val,
      ClientPlayerEntity val2,
      ClientWorld val3,
      BlockPos val4,
      Direction val5,
      float val6,
      boolean val7
   ) {
      if (val
            != null
         && val2
            != null
         && val3
            != null
         && val4
            != null) {
         BlockState state = val3.getBlockState(
            val4
         );
         if (state
               != null
            && !state.isAir()
            )
          {
            Direction direction = val5
                  == null
               ? Direction.UP
               : val5;
            int n = getExtraTicks(
               val6
            );

            for (int i = 0;
               i
                  < n;
               i++
            ) {
               val.updateBlockBreakingProgress(
                  val4,
                  direction
               );
            }

            if (val7
               )
             {
               val2.swingHand(
                  Hand.MAIN_HAND
               );
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean packetBreak(
      ClientPlayNetworkHandler val,
      ClientPlayerEntity val2,
      BlockPos val3,
      Direction val4,
      boolean val5
   ) {
      if (val
            != null
         && val2
            != null
         && val3
            != null) {
         Direction direction = val4
               == null
            ? Direction.UP
            : val4;
         val.sendPacket(
            new PlayerActionC2SPacket(
               Action.START_DESTROY_BLOCK,
               val3,
               direction
            )
         );
         val.sendPacket(
            new PlayerActionC2SPacket(
               Action.STOP_DESTROY_BLOCK,
               val3,
               direction
            )
         );
         if (val5
            )
          {
            val.sendPacket(
               new HandSwingC2SPacket(Hand.MAIN_HAND)
            );
         }

         return true;
      } else {
         return false;
      }
   }
}
