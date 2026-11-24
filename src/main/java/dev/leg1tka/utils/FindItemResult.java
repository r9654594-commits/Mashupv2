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
import org.jetbrains.annotations.NotNull;

public class FindItemResult implements IMinecraft {
   public final int slot;
   public final boolean found;
   public final ItemStack stack;

   private static final FindItemResult NOT_FOUND_RESULT = new FindItemResult(-1, false, ItemStack.EMPTY);

   public FindItemResult(int slot, boolean found, ItemStack stack) {
      this.slot = slot;
      this.found = found;
      this.stack = stack;
   }

   public int slot() {
      return this.slot;
   }

   public boolean found() {
      return this.found;
   }

   public ItemStack stack() {
      return this.stack;
   }

   public static FindItemResult notFound() {
      return NOT_FOUND_RESULT;
   }

   @NotNull
   public static FindItemResult inOffhand(
      ItemStack stack
   ) {
      return new FindItemResult(
         999,
         true,
         stack
      );
   }

   public boolean isHolding() {
      return mc.player == null ? false : this.isOffhand() || mc.player.getInventory().selectedSlot == this.slot;
   }

   public boolean isOffhand() {
      return this.slot == 999;
   }

   public boolean isInHotBar() {
      return this.slot >= 0 && this.slot < 9;
   }

   public void switchTo() {
      if (this.found && this.isInHotBar()) {
         GetitemcountHelper.switchTo(this.slot);
      }
   }

   public void switchToSilent() {
      if (this.found && this.isInHotBar()) {
         GetitemcountHelper.switchToSilent(this.slot);
      }
   }
}
