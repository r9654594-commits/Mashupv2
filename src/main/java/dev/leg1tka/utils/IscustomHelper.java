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

public class IscustomHelper {
   public final String moduleName;
   public final String categoryIcon;
   public final boolean enabled;
   public final String customText;
   public final ItemStack itemStack;
   public final long startTime;

   public IscustomHelper(
      String str,
      String str2,
      boolean flag,
      String str3,
      ItemStack stack,
      long time
   ) {
      this.moduleName = str;
      this.categoryIcon = str2;
      this.enabled = flag;
      this.customText = str3;
      this.itemStack = stack;
      this.startTime = time;
   }

   public boolean isCustom() {
      return this.customText != null && !this.customText.isEmpty();
   }

   public boolean hasItemIcon() {
      return this.itemStack != null && !this.itemStack.isEmpty();
   }
}
