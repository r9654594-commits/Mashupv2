package dev.leg1tka.modules.misc;


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
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class UseTracker extends Module {
   public static UseTracker INSTANCE = new UseTracker();
   private final BooleanSetting trackGoldenApple;
   private final BooleanSetting trackEnchantedApple;
   private final BooleanSetting trackEnderPearl;
   private final BooleanSetting trackPotion;
   private final Map<String, IsusingHolder> playerStates;
   private final Map<String, Long> notificationCooldowns;
   private static final long NOTIFICATION_COOLDOWN_MS = 1000L;

   public UseTracker() {
      super("UseTracker", Category.MISC);
      String var1 = "UseTracker";
      this.trackGoldenApple = new BooleanSetting("Золотое яблоко", true);
      this.trackEnchantedApple = new BooleanSetting("Зачарованное яблоко", true);
      this.trackEnderPearl = new BooleanSetting("Эндер перл", true);
      this.trackPotion = new BooleanSetting("Зелья", true);
      this.playerStates = new HashMap<>();
      this.notificationCooldowns = new HashMap<>();
      this.addSettings(new Setting[]{
         this.trackGoldenApple,
         this.trackEnchantedApple,
         this.trackEnderPearl,
         this.trackPotion
      });
   }

   @Override
   public void onDisable() {
      this.playerStates.clear();
      this.notificationCooldowns.clear();
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val6
   ) {
      if (mc.player != null && mc.world != null) {
         long val = System.currentTimeMillis();

         for (PlayerEntity player2 : mc.world
            .getPlayers()) {
            if (player2
                  != null
               && player2
                  != mc.player
               && player2.isAlive()
               && !(
                  mc.player
                        .squaredDistanceTo(
                           player2
                        )
                     > 2500.0
               )) {
               String name = player2.getName()
                  .getString();
               IsusingHolder val2 = this.playerStates
                  .computeIfAbsent(
                     name,
                     val3 -> new IsusingHolder()
                  );
               if (player2.isUsingItem()
                  )
                {
                  ItemStack stack = player2.getActiveItem();
                  Hand hand = player2.getActiveHand();
                  if (!stack.isEmpty()
                     )
                   {
                     Item item2 = stack.getItem();
                     if (!val2.isUsing
                        || !ItemStack.areEqual(
                           val2.lastUsedStack,
                           stack
                        )) {
                        val2.isUsing = true;
                        val2.lastUsedStack = stack.copy();
                        val2.useStartTime = val;
                     }
                  }
               } else if (val2.isUsing
                  && val2.lastUsedStack
                     != null) {
                  long val4 = val
                     - val2.useStartTime;
                  if (val4
                     > 100L) {
                     this.checkItemUsage(
                        name,
                        val2.lastUsedStack,
                        val
                     );
                  }

                  val2.isUsing = false;
                  val2.lastUsedStack = null;
               }

               if (this.trackEnderPearl.isState()) {
                  ItemStack stack2 = player2.getMainHandStack();
                  ItemStack stack3 = player2.getOffHandStack();
                  if (!stack2.isEmpty()
                     && stack2.getItem()
                        == Items.ENDER_PEARL) {
                     int count = stack2.getCount();
                     if (val2.lastPearlCount
                        > count
                        )
                      {
                        this.sendNotification(
                           name,
                           "Эндер перл",
                           new ItemStack(Items.ENDER_PEARL),
                           val
                        );
                     }

                     val2.lastPearlCount = count;
                  } else if (!stack3.isEmpty()
                     && stack3.getItem()
                        == Items.ENDER_PEARL) {
                     int count2 = stack3.getCount();
                     if (val2.lastPearlCount
                        > count2
                        )
                      {
                        this.sendNotification(
                           name,
                           "Эндер перл",
                           new ItemStack(Items.ENDER_PEARL),
                           val
                        );
                     }

                     val2.lastPearlCount = count2;
                  } else {
                     val2.lastPearlCount = 0;
                  }
               }
            }
         }

         this.notificationCooldowns
            .entrySet()
            .removeIf(
               val5 -> val
                        - val5.getValue()
                     > 2000L
            );
      } else {
         this.playerStates.clear();
         this.notificationCooldowns.clear();
      }
   }

   private void checkItemUsage(
      String val,
      ItemStack val2,
      long val3
   ) {
      if (val2
            != null
         && !val2.isEmpty()
         )
       {
         Item item2 = val2.getItem();
         String str = null;
         ItemStack stack = null;
         if (this.trackGoldenApple.isState()
            && item2
               == Items.GOLDEN_APPLE) {
            str = "Золотое яблоко";
            stack = new ItemStack(
               Items.GOLDEN_APPLE
            );
         } else if (this.trackEnchantedApple.isState()
            && item2
               == Items.ENCHANTED_GOLDEN_APPLE) {
            str = "Зачарованное яблоко";
            stack = new ItemStack(
               Items.ENCHANTED_GOLDEN_APPLE
            );
         } else if (this.trackPotion.isState()
            && (
               item2
                     == Items.POTION
                  || item2
                     == Items.SPLASH_POTION
                  || item2
                     == Items.LINGERING_POTION
            )) {
            str = "Зелье";
            stack = val2.copy();
         }

         if (str
               != null
            && stack
               != null) {
            this.sendNotification(
               val,
               str,
               stack,
               val3
            );
         }
      }
   }

   private void sendNotification(
      String val,
      String val2,
      ItemStack val3,
      long val4
   ) {
      String str = val
         + ":"
         + val2;
      Long val5 = this.notificationCooldowns
         .get(
            str
         );
      if (val5
            == null
         || val4
               - val5
            >= 1000L) {
         String str2 = val
            + " использовал "
            + val2;
         PushHelper.pushItemNotification(
            str2,
            val3
         );
         this.notificationCooldowns
            .put(
               str,
               val4
            );
      }
   }
}
