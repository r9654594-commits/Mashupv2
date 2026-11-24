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

import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class AhHelper extends Module {
   public static final AhHelper INSTANCE = new AhHelper();
   private static final Pattern PRICE_PATTERN = Pattern.compile("(?:\\$|Цена|цена|Стоимость|стоимость|Купить|купить|Price|price:?)\\s*([\\d\\s,.]+)");
   private final IntegerSetting cheapSlotColor = new IntegerSetting(
      "Цвет дешевого",
      ColorUtil.rgba(
         0,
         255,
         140,
         235
      )
   );

   public AhHelper() {
      super(
         "AHHelper",
         "Помощник в поиске дешевых предметов на аукционе",
         Category.MISC
      );
   }

   public void renderFromMixin(
      DrawContext val,
      HandledScreen<?> val2,
      int val3,
      int val4
   ) {
      if (this.isEnable()
         && mc.player != null
         && val2
            != null
         && val
            != null) {
         Slot val5 = this.findBestSlot(
            val2
         );
         if (val5
            != null) {
            int n = val3
               + val5.x;
            int n2 = val4
               + val5.y;
            int color = this.cheapSlotColor
               .getValue();
            int color2 = ColorUtil.applyAlpha(
               color,
               0.3F
            );
            int color3 = ColorUtil.applyAlpha(
               color,
               0.16F
            );
            val.fill(
               n
                  - 2,
               n2
                  - 2,
               n
                  + 18,
               n2
                  + 18,
               color3
            );
            val.fill(
               n,
               n2,
               n
                  + 16,
               n2
                  + 16,
               color2
            );
            val.fill(
               n
                  - 2,
               n2
                  - 2,
               n
                  + 18,
               n2
                  - 1,
               color
            );
            val.fill(
               n
                  - 2,
               n2
                  + 17,
               n
                  + 18,
               n2
                  + 18,
               color
            );
            val.fill(
               n
                  - 2,
               n2
                  - 1,
               n
                  - 1,
               n2
                  + 17,
               color
            );
            val.fill(
               n
                  + 17,
               n2
                  - 1,
               n
                  + 18,
               n2
                  + 17,
               color
            );
         }
      }
   }

   private Slot findBestSlot(
      HandledScreen<?> val
   ) {
      if (val.getScreenHandler()
            != null
         && val.getScreenHandler()
               .slots
            != null) {
         PriceperitemHelper item2 = null;

         for (Slot val2 : val.getScreenHandler()
            .slots) {
            if (this.isAuctionSlot(
               val2
            )) {
               ItemStack stack2 = val2.getStack();
               if (!stack2.isEmpty()
                  )
                {
                  OptionalLong val3 = this.parsePrice(
                     stack2
                  );
                  if (!val3.isEmpty()
                     )
                   {
                     PriceperitemHelper item3 = new PriceperitemHelper(
                        val2,
                        stack2,
                        val3.getAsLong()
                     );
                     if (item3.price
                           > 0L
                        && (
                           item2
                                 == null
                              || this.isBetterDeal(
                                 item3,
                                 item2
                              )
                        )) {
                        item2 = item3;
                     }
                  }
               }
            }
         }

         return item2
               == null
            ? null
            : item2.slot;
      } else {
         return null;
      }
   }

   private boolean isBetterDeal(
      PriceperitemHelper val,
      PriceperitemHelper val2
   ) {
      int n = Double.compare(
         val.pricePerItem(),
         val2.pricePerItem()
      );
      if (n
         != 0) {
         return n
            < 0;
      } else {
         int n2 = Long.compare(
            val.price,
            val2.price
         );
         return n2
               != 0
            ? n2
               < 0
            : val.stack
                  .getCount()
               > val2.stack
                  .getCount();
      }
   }

   private boolean isAuctionSlot(
      Slot val
   ) {
      return val
            != null
         && val.inventory
            != null
         && !(
            val.inventory instanceof PlayerInventory
         )
         && val.isEnabled()
         && val.id
            >= 0;
   }

   private OptionalLong parsePrice(
      ItemStack val
   ) {
      LoreComponent val2 = (LoreComponent)val.get(
         DataComponentTypes.LORE
      );
      if (val2
         == null) {
         return OptionalLong.empty();
      } else {
         long val3 = Long.MAX_VALUE;

         for (Text val4 : val2.lines()) {
            String str = val4.getString();
            if (str
                  != null
               && (
                  str.contains(
                        "$"
                     )
                     || str.contains(
                        "Цена"
                     )
                     || str.contains(
                        "цена"
                     )
                     || str.contains(
                        "Стоимость"
                     )
                     || str.contains(
                        "монет"
                     )
                     || str.contains(
                        "Price"
                     )
                     || str.contains(
                        "price"
                     )
               )) {
               Matcher matcher2 = PRICE_PATTERN.matcher(
                  str
               );

               while (
                  matcher2.find()
               ) {
                  String str2 = matcher2.group(
                        1
                     )
                     .replaceAll("[^\\d]", "");
                  if (!str2.isEmpty()
                     )
                   {
                     try {
                        long val5 = Long.parseLong(
                           str2
                        );
                        if (val5
                           > 0L) {
                           val3 = Math.min(
                              val3,
                              val5
                           );
                        }
                     } catch (Exception var13) {
                     }
                  }
               }

               if (val3
                  == Long.MAX_VALUE) {
                  String str3 = str.replaceAll(
                     "[^\\d]", ""
                  );
                  if (!str3.isEmpty()
                     )
                   {
                     try {
                        long val6 = Long.parseLong(
                           str3
                        );
                        if (val6
                           > 0L) {
                           val3 = Math.min(
                              val3,
                              val6
                           );
                        }
                     } catch (Exception var12) {
                     }
                  }
               }
            }
         }

         return val3
               == Long.MAX_VALUE
            ? OptionalLong.empty()
            : OptionalLong.of(
               val3
            );
      }
   }
}
