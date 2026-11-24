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


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GetremainingsecHelper {
   public final String playername;
   public final Item item;
   public final String name;
   public final long expire;
   public final long total;
   public final ItemStack stack;
   public boolean alerted;

   public GetremainingsecHelper(
      String str,
      Item item2,
      String str2,
      long time,
      long time2,
      ItemStack stack2
   ) {
      this.playername = str;
      this.item = item2;
      this.name = str2;
      this.expire = time;
      this.total = time2;
      this.stack = stack2;
      this.alerted = false;
   }

   public float getRemainingSec() {
      long time = this.expire
         - System.currentTimeMillis();
      return time
            <= 0L
         ? 0.0F
         : (float)time
            / 1000.0F;
   }

   public float getProgress() {
      long time = this.expire
         - System.currentTimeMillis();
      return time
            <= 0L
         ? 0.0F
         : Math.max(
            0.0F,
            Math.min(
               1.0F,
               (float)time
                  / (float)this.total
            )
         );
   }
}
