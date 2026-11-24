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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class ChestStealer extends Module {
   public static ChestStealer INSTANCE = new ChestStealer();
   private final NumberSetting stealDelay;
   private final BooleanSetting randomize;
   private long lastStealTime;

   public ChestStealer() {
      super("ChestStealer", Category.PLAYER);
      String var1 = "ChestStealer";
      this.stealDelay = new NumberSetting("Задержка", 100.0F, 0.0F, 1000.0F, 1.0F);
      this.randomize = new BooleanSetting("Рандомизация", false);
      this.lastStealTime = 0L;
      this.addSettings(new Setting[]{
         this.stealDelay,
         this.randomize
      });
   }

   @EventLink
   private void onUpdate(
      EventUpdate val4
   ) {
      if (mc.player != null && mc.interactionManager != null) {
         ScreenHandler hand = mc.player
            .currentScreenHandler;
         if (hand
               != null
            && hand
               != mc.player.playerScreenHandler) {
            if (hand instanceof GenericContainerScreenHandler
               || hand instanceof HopperScreenHandler
               )
             {
               long val = System.currentTimeMillis();
               long val2 = (long)this.stealDelay
                  .get();
               if (val
                     - this.lastStealTime
                  >= val2
                  )
                {
                  List<Slot> list = (List<Slot>)hand.slots;
                  this.findValidItem(
                        list,
                        hand
                     )
                     .ifPresent(
                        val3 -> {
                           if (mc.player.currentScreenHandler
                              == hand
                              )
                            {
                              mc.interactionManager
                                 .clickSlot(
                                    hand.syncId,
                                    ((Slot)val3).id,
                                    0,
                                    SlotActionType.QUICK_MOVE,
                                    mc.player
                                 );
                              this.lastStealTime = val;
                           }
                        }
                     );
               }
            }
         }
      }
   }

   private Optional<Slot> findValidItem(
      List<Slot> val,
      ScreenHandler val2
   ) {
      int slot2 = this.getContainerSlotCount(
         val2
      );
      if (slot2
            > 0
         && slot2
            <= val.size()
         )
       {
         List<Slot> list = val.subList(
            0,
            slot2
         );
         List<Slot> list2 = new ArrayList<>();

         for (Slot val3 : list) {
            if (val3.hasStack()
               && !val3.getStack()
                  .isEmpty()
               && !mc.player
                  .getItemCooldownManager()
                  .isCoolingDown(
                     val3.getStack()
                  )) {
               list2.add(
                  val3
               );
            }
         }

         if (list2.isEmpty()
            )
          {
            return Optional.empty();
         } else if (this.randomize.isState()) {
            int n = ThreadLocalRandom.current()
               .nextInt(
                  list2.size()
               );
            return Optional.of(
               list2.get(
                  n
               )
            );
         } else {
            return Optional.of(
               list2.get(
                  0
               )
            );
         }
      } else {
         return Optional.empty();
      }
   }

   private int getContainerSlotCount(
      ScreenHandler val
   ) {
      if (val instanceof GenericContainerScreenHandler val2
         )
       {
         Inventory val3 = val2.getInventory();
         return val3.size();
      } else {
         return val instanceof HopperScreenHandler
            ? 5
            : 0;
      }
   }

   @Override
   public void onDisable() {
      this.lastStealTime = 0L;
      super.onDisable();
   }
}
