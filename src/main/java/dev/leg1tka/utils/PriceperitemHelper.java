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


import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class PriceperitemHelper {
   public final Slot slot;
   public final ItemStack stack;
   public final long price;
   public PriceperitemHelper(
      Slot val,
      ItemStack stack2,
      long time
   ) {
      this.slot = val;
      this.stack = stack2;
      this.price = time;
   }

   public double pricePerItem() {
      return (double)this.price / (double)Math.max(1, this.stack.getCount());
   }
}
