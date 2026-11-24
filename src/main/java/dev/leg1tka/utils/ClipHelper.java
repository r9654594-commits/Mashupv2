package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;


import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

public final class ClipHelper {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   private ClipHelper() {
   }

   public static void clip(
      double d
   ) {
      if (mc.player != null && mc.world != null) {
         if (d
            == 0.0) {
            ChatUtil.sendMessage("Дистанция не должна быть 0");
         } else {
            int slot = findElytraSlot();
            boolean flag = slot
                  != -1
               && slot
                  != -2;
            if (flag
               )
             {
               swapElytra(
                  slot,
                  true
               );
            }

            teleport(
               d,
               flag
            );
            if (flag
               )
             {
               swapElytra(
                  slot,
                  false
               );
            }
         }
      }
   }

   public static void clipUp() {
      if (mc.player != null && mc.world != null) {
         float y = scanVerticalOffset(
            mc.player.getBlockPos(), true
         );
         if (y
            == 0.0F) {
            ChatUtil.sendMessage("Не удалось выполнить телепортацию");
         } else {
            clip(
               (double)y
            );
         }
      }
   }

   public static void clipDown() {
      if (mc.player != null && mc.world != null) {
         float y = scanVerticalOffset(
            mc.player.getBlockPos(), false
         );
         if (y
            == 0.0F) {
            ChatUtil.sendMessage("Не удалось выполнить телепортацию");
         } else {
            clip(
               (double)y
            );
         }
      }
   }

   private static void teleport(
      double d,
      boolean flag
   ) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         double dist = mc.player
            .getX();
         double dist2 = mc.player
            .getY();
         double dist3 = mc.player
            .getZ();
         if (flag
            )
          {
            for (int i = 0;
               i
                  < 2;
               i++
            ) {
               mc.getNetworkHandler()
                  .sendPacket(
                     new PositionAndOnGround(
                        dist,
                        dist2,
                        dist3,
                        false,
                        false
                     )
                  );
            }

            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
            mc.getNetworkHandler()
               .sendPacket(
                  new PositionAndOnGround(
                     dist,
                     dist2
                        + d,
                     dist3,
                     false,
                     false
                  )
               );
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
         } else {
            int i2 = Math.max(
               (int)(
                  Math.abs(
                        d
                     )
                     / 1000.0
               ),
               3
            );

            for (int i3 = 0;
               i3
                  < i2;
               i3++
            ) {
               mc.getNetworkHandler().sendPacket(new OnGroundOnly(mc.player.isOnGround(), false));
            }

            mc.getNetworkHandler()
               .sendPacket(
                  new PositionAndOnGround(
                     dist,
                     dist2
                        + d,
                     dist3,
                     false,
                     false
                  )
               );
         }

         mc.player
            .setPosition(
               dist,
               dist2
                  + d,
               dist3
            );
         mc.player.fallDistance = 0.0F;
         ChatUtil.sendMessage(
            String.format(
               "Телепорт на %.1f по вертикали",
               d
            )
         );
      }
   }

   private static float scanVerticalOffset(
      BlockPos pos,
      boolean flag
   ) {
      if (mc.world == null) {
         return 0.0F;
      } else {
         int i = flag
            ? 3
            : -1;
         int i2 = flag
            ? 320
            : -320;
         int i3 = flag
            ? 1
            : -1;

         for (int i4 = i;
            i4
               != i2;
            i4 += i3
         ) {
            BlockPos pos2 = pos.add(
               0,
               i4,
               0
            );
            if (mc.world
               .getBlockState(
                  pos2
               )
               .isAir()) {
               return (float)(
                  i4
                     + (
                        flag
                           ? 1
                           : -1
                     )
               );
            }

            if (!flag
               && mc.world
                     .getBlockState(
                        pos2
                     )
                     .getBlock()
                  == Blocks.BEDROCK) {
               ChatUtil.sendMessage("Тут нельзя телепортироваться под землю");
               return 0.0F;
            }
         }

         return 0.0F;
      }
   }

   private static void swapElytra(
      int n,
      boolean flag
   ) {
      if (mc.interactionManager != null && mc.player != null) {
         int slot = 6;
         if (flag
            )
          {
            mc.interactionManager
               .clickSlot(
                  0,
                  n,
                  0,
                  SlotActionType.PICKUP,
                  mc.player
               );
            mc.interactionManager
               .clickSlot(
                  0,
                  slot,
                  0,
                  SlotActionType.PICKUP,
                  mc.player
               );
         } else {
            mc.interactionManager
               .clickSlot(
                  0,
                  slot,
                  0,
                  SlotActionType.PICKUP,
                  mc.player
               );
            mc.interactionManager
               .clickSlot(
                  0,
                  n,
                  0,
                  SlotActionType.PICKUP,
                  mc.player
               );
         }
      }
   }

   private static int findElytraSlot() {
      if (mc.player == null) {
         return -1;
      } else {
         ItemStack stack = mc.player
            .getEquippedStack(EquipmentSlot.CHEST);
         if (stack.getItem()
            == Items.ELYTRA) {
            return -2;
         } else {
            int i = -1;

            for (int i2 = 0;
               i2
                  < 36;
               i2++
            ) {
               ItemStack stack2 = mc.player
                  .getInventory()
                  .getStack(
                     i2
                  );
               if (stack2.getItem()
                  == Items.ELYTRA) {
                  i = i2;
                  break;
               }
            }

            if (i
                  >= 0
               && i
                  < 9) {
               i += 36;
            }

            return i;
         }
      }
   }
}
