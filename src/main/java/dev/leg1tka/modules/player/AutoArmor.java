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
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
   public static AutoArmor INSTANCE = new AutoArmor();
   private final NumberSetting dly = new NumberSetting("Задержка", 25.0F, 1.0F, 1000.0F, 1.0F);
   private long ltm = 0L;

   public AutoArmor() {
      super(
         "AutoArmor",
         "Автоматически одевает броню",
         Category.PLAYER
      );
      this.addSettings(new Setting[]{this.dly});
   }

   @EventLink
   public void onEvent(
      EventUpdate val3
   ) {
      if (mc.player != null && mc.world != null) {
         if (!this.isMoving()) {
            long val = System.currentTimeMillis();
            if (!(
               (float)(
                     val
                        - this.ltm
                  )
                  < this.dly.get()
            )) {
               for (int i = 0;
                  i
                     < 4;
                  i++
               ) {
                  ItemStack stack = mc.player
                     .getInventory()
                     .getArmorStack(
                        i
                     );
                  ItemStack stack2 = null;
                  int n = -1;
                  float f = this.getScore(
                     stack
                  );

                  for (int i2 = 0;
                     i2
                        < 36;
                     i2++
                  ) {
                     ItemStack stack3 = mc.player
                        .getInventory()
                        .getStack(
                           i2
                        );
                     if (!stack3.isEmpty()
                        )
                      {
                        Item var12 = stack3.getItem();
                        if (var12 instanceof ArmorItem) {
                           ArmorItem item2 = (ArmorItem)var12;
                           if (this.getSlotIdx(
                                 item2
                              )
                              == i
                              )
                            {
                              float f2 = this.getScore(
                                 stack3
                              );
                              if ((
                                    stack.isEmpty()
                                       || f2
                                          > f
                                 )
                                 && (
                                    stack2
                                          == null
                                       || f2
                                          > this.getScore(
                                             stack2
                                          )
                                 )) {
                                 stack2 = stack3;
                                 n = i2;
                              }
                           }
                        }
                     }
                  }

                  if (n
                     != -1) {
                     int n2 = n
                           < 9
                        ? n
                           + 36
                        : n;
                     if (!stack.isEmpty()
                        )
                      {
                        int n3 = 8
                           - i;
                        mc.interactionManager
                           .clickSlot(
                              0,
                              n3,
                              0,
                              SlotActionType.QUICK_MOVE,
                              mc.player
                           );
                        this.ltm = val;
                        return;
                     }

                     mc.interactionManager
                        .clickSlot(
                           0,
                           n2,
                           0,
                           SlotActionType.QUICK_MOVE,
                           mc.player
                        );
                     this.ltm = val;
                     return;
                  }
               }
            }
         }
      }
   }

   private float getScore(
      ItemStack val
   ) {
      if (!val.isEmpty()
         && val.getItem() instanceof ArmorItem val2
         )
       {
         float var5 = this.getMatPriority(
            val2
         );
         int var4 = SearchitemHelper.getEnchantmentLevel(
            val,
            Enchantments.PROTECTION
         );
         return var5 + (float)var4 * 5.0F;
      } else {
         return 0.0F;
      }
   }

   private float getMatPriority(
      ArmorItem val
   ) {
      String str = val.toString()
         .toLowerCase();
      if (str.contains(
         "netherite"
      )) {
         return 50.0F;
      } else if (str.contains(
         "diamond"
      )) {
         return 40.0F;
      } else if (str.contains(
         "iron"
      )) {
         return 30.0F;
      } else if (str.contains(
            "golden"
         )
         || str.contains(
            "gold"
         )) {
         return 20.0F;
      } else if (str.contains(
         "chainmail"
      )) {
         return 20.0F;
      } else {
         return str.contains(
               "leather"
            )
            ? 10.0F
            : 0.0F;
      }
   }

   private boolean isMoving() {
      return mc.player.input.movementForward != 0.0F || mc.player.input.movementSideways != 0.0F;
   }

   private int getSlotIdx(
      ArmorItem val
   ) {
      String str = val.toString()
         .toLowerCase();
      if (str.contains(
            "helmet"
         )
         || str.contains(
            "skull"
         )) {
         return 3;
      } else if (str.contains(
            "chestplate"
         )
         || str.contains(
            "tunic"
         )) {
         return 2;
      } else if (str.contains(
            "leggings"
         )
         || str.contains(
            "pants"
         )) {
         return 1;
      } else {
         return !str.contains(
                  "boots"
               )
               && !str.contains(
                  "shoes"
               )
            ? 0
            : 0;
      }
   }
}
