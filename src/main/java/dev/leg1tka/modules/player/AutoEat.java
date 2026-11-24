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
import dev.leg1tka.modules.movement.Sprint;

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoEat extends Module {
   public static final AutoEat INSTANCE = new AutoEat();
   private static final String BARITONE_API_CLASS = "baritone.api.BaritoneAPI";
   private final NumberSetting hungerBars = new NumberSetting(
      "Плашки голода",
      6.0F,
      1.0F,
      10.0F,
      1.0F
   );
   private boolean eating;
   private boolean sprintPaused;
   private boolean swappedFromInventory;
   private int originalSlot = -1;
   private int swappedInventorySlot = -1;

   public AutoEat() {
      super(
         "AutoEat",
         "Автоматически ест при низком голоде",
         Category.PLAYER
      );
      this.addSettings(new Setting[]{this.hungerBars});
   }

   public static boolean shouldSuppressCombat() {
      return INSTANCE != null && INSTANCE.isEnable() && INSTANCE.eating;
   }

   @Override
   public void onDisable() {
      this.stopEating();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val5
   ) {
      if (mc.player == null || mc.world == null || mc.interactionManager == null) {
         this.stopEating();
      } else if (mc.currentScreen != null) {
         this.stopEating();
      } else if (!mc.player.getAbilities().creativeMode && !mc.player.isSpectator()) {
         if (!this.eating) {
            if (!this.shouldStartEating()) {
               return;
            }

            this.eating = true;
            this.originalSlot = mc.player.getInventory().selectedSlot;
         }

         this.tickEating();
      } else {
         this.stopEating();
      }
   }

   private void tickEating() {
      ClientPlayerEntity player2 = mc.player;
      if (player2
         == null) {
         this.stopEating();
      } else {
         this.pauseBaritone();
         if (!this.sprintPaused) {
            Sprint.pushPause(0L);
            this.sprintPaused = true;
         }

         mc.options.attackKey.setPressed(false);
         if (!this.needsFood()) {
            if (!player2.isUsingItem()
               )
             {
               this.stopEating();
            }
         } else if (!this.ensureFoodReady()) {
            this.stopEating();
         } else {
            Hand hand = this.getEatingHand(
               player2
            );
            if (hand
               == null) {
               this.stopEating();
            } else {
               mc.options.useKey.setPressed(true);
               if (!player2.isUsingItem()
                  || player2.getActiveHand()
                     != hand
                  )
                {
                  mc.interactionManager
                     .interactItem(
                        player2,
                        hand
                     );
               }
            }
         }
      }
   }

   private boolean shouldStartEating() {
      return this.needsFood()
         && !mc.player.isUsingItem()
         && (this.isValidFood(mc.player.getOffHandStack()) || this.findFoodSlot() != -1);
   }

   private boolean needsFood() {
      return mc.player != null
         && mc.player.getHungerManager().getFoodLevel() < 20
         && mc.player.getHungerManager().getFoodLevel() <= this.getFoodThreshold();
   }

   private int getFoodThreshold() {
      return Math.round(this.hungerBars.get()) * 2;
   }

   private boolean ensureFoodReady() {
      ClientPlayerEntity player2 = mc.player;
      if (player2
         == null) {
         return false;
      } else if (this.isValidFood(
         player2.getOffHandStack()
      )) {
         return true;
      } else if (this.isValidFood(
         player2.getMainHandStack()
      )) {
         return true;
      } else {
         int slot2 = this.findFoodSlot();
         if (slot2
            == -1) {
            return false;
         } else if (slot2
            < 9) {
            this.swappedFromInventory = false;
            this.swappedInventorySlot = -1;
            this.selectHotbarSlot(
               slot2
            );
            return this.isValidFood(
               player2.getMainHandStack()
            );
         } else {
            this.selectHotbarSlot(
               this.originalSlot == -1
                  ? player2.getInventory()
                     .selectedSlot
                  : this.originalSlot
            );
            this.swapInventorySlotWithHotbar(
               slot2,
               player2.getInventory()
                  .selectedSlot
            );
            this.swappedFromInventory = true;
            this.swappedInventorySlot = slot2;
            return this.isValidFood(
               player2.getMainHandStack()
            );
         }
      }
   }

   private Hand getEatingHand(
      ClientPlayerEntity val
   ) {
      if (val
         == null) {
         return null;
      } else if (this.isValidFood(
         val.getOffHandStack()
      )) {
         return Hand.OFF_HAND;
      } else {
         return this.isValidFood(
               val.getMainHandStack()
            )
            ? Hand.MAIN_HAND
            : null;
      }
   }

   private int findFoodSlot() {
      ClientPlayerEntity player2 = mc.player;
      if (player2
         == null) {
         return -1;
      } else {
         int n = player2.getInventory()
            .selectedSlot;
         if (this.isValidFood(
            player2.getInventory()
               .getStack(
                  n
               )
         )) {
            return n;
         } else {
            for (int i = 0;
               i
                  < 9;
               i++
            ) {
               if (i
                     != n
                  && this.isValidFood(
                     player2.getInventory()
                        .getStack(
                           i
                        )
                  )) {
                  return i;
               }
            }

            for (int i2 = 9;
               i2
                  < 36;
               i2++
            ) {
               if (this.isValidFood(
                  player2.getInventory()
                     .getStack(
                        i2
                     )
               )) {
                  return i2;
               }
            }

            return -1;
         }
      }
   }

   private boolean isValidFood(
      ItemStack val
   ) {
      if (val
            == null
         || val.isEmpty()
         )
       {
         return false;
      } else {
         return !val.isOf(
                  Items.GOLDEN_APPLE
               )
               && !val.isOf(
                  Items.ENCHANTED_GOLDEN_APPLE
               )
               && !val.isOf(
                  Items.CHORUS_FRUIT
               )
            ? val.getUseAction()
               == UseAction.EAT
            : false;
      }
   }

   private void selectHotbarSlot(
      int val
   ) {
      if (mc.player != null
         && val
            >= 0
         && val
            <= 8
         && mc.player.getInventory().selectedSlot
            != val
         )
       {
         mc.player.getInventory().selectedSlot = val;
         if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler()
               .sendPacket(
                  new UpdateSelectedSlotC2SPacket(
                     val
                  )
               );
         }
      }
   }

   private void swapInventorySlotWithHotbar(
      int val,
      int val2
   ) {
      if (mc.player != null
         && mc.interactionManager != null
         && val
            >= 9
         && val
            <= 35
         && val2
            >= 0
         && val2
            <= 8) {
         mc.interactionManager
            .clickSlot(
               0,
               val,
               val2,
               SlotActionType.SWAP,
               mc.player
            );
         if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
         }
      }
   }

   private void stopEating() {
      if (mc.options != null) {
         mc.options.useKey.setPressed(false);
      }

      if (this.sprintPaused) {
         Sprint.popPause();
         this.sprintPaused = false;
      }

      this.restoreHeldItem();
      this.eating = false;
   }

   private void restoreHeldItem() {
      if (mc.player != null && mc.interactionManager != null) {
         if (this.swappedFromInventory && this.swappedInventorySlot != -1) {
            int slot2 = this.originalSlot
                  == -1
               ? mc.player.getInventory().selectedSlot
               : this.originalSlot;
            this.selectHotbarSlot(
               slot2
            );
            this.swapInventorySlotWithHotbar(
               this.swappedInventorySlot,
               slot2
            );
         }

         if (this.originalSlot != -1) {
            this.selectHotbarSlot(this.originalSlot);
         }

         this.resetSwapState();
      } else {
         this.resetSwapState();
      }
   }

   private void resetSwapState() {
      this.swappedFromInventory = false;
      this.swappedInventorySlot = -1;
      this.originalSlot = -1;
   }

   private void pauseBaritone() {
      try {
         Object val = getPrimaryBaritone();
         if (val
            == null) {
            this.cancelVanillaBreaking();
            return;
         }

         Object val2 = invoke(
            val,
            "getPathingBehavior"
         );
         if (val2
               == null
            || !Boolean.TRUE
               .equals(
                  invoke(
                     val2,
                     "hasPath"
                  )
               )) {
            this.cancelVanillaBreaking();
            return;
         }

         Object val3 = invoke(
            val,
            "getInputOverrideHandler"
         );
         if (val3
            != null) {
            val3.getClass()
               .getMethod(
                  "clearAllKeys"
               )
               .invoke(
                  val3
               );
            Object val4 = val3.getClass()
               .getMethod(
                  "getBlockBreakHelper"
               )
               .invoke(
                  val3
               );
            if (val4
               != null) {
               val4.getClass()
                  .getMethod(
                     "stopBreakingBlock"
                  )
                  .invoke(
                     val4
                  );
            }
         }

         val2.getClass()
            .getMethod(
               "requestPause"
            )
            .invoke(
               val2
            );
         this.cancelVanillaBreaking();
      } catch (Throwable var5) {
         this.cancelVanillaBreaking();
      }
   }

   private void cancelVanillaBreaking() {
      try {
         if (mc.interactionManager != null) {
            mc.interactionManager.cancelBlockBreaking();
         }
      } catch (Throwable var2) {
      }
   }

   private static Object getPrimaryBaritone() throws ReflectiveOperationException {
      Class val = Class.forName(
         "baritone.api.BaritoneAPI"
      );
      Object val2 = val.getMethod(
            "getProvider"
         )
         .invoke(null);
      return val2
            == null
         ? null
         : val2.getClass()
            .getMethod(
               "getPrimaryBaritone"
            )
            .invoke(
               val2
            );
   }

   private static Object invoke(
      Object val,
      String val2
   ) throws ReflectiveOperationException {
      return val.getClass()
         .getMethod(
            val2
         )
         .invoke(
            val
         );
   }
}
