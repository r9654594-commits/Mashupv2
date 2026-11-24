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

public class IdHolder {
   public final String id;
   public final String displayName;
   public final ItemStack icon;
   public final ClientOption_L6ONUCj5 base;
   public final long start;
   public final long duration;

   private IdHolder(
      String str,
      String str2,
      ItemStack stack,
      ClientOption_L6ONUCj5 val,
      long time,
      long time2
   ) {
      this.id = str;
      this.displayName = str2;
      this.icon = stack;
      this.base = val;
      this.start = time;
      this.duration = time2;
   }

   public static IdHolder fromTicks(
      String str,
      String str2,
      ItemStack stack,
      long time,
      long time2
   ) {
      return new IdHolder(
         str,
         str2,
         stack,
         ClientOption_L6ONUCj5.TICKS,
         time,
         time2
      );
   }

   public static IdHolder fromReal(
      String str,
      String str2,
      ItemStack stack,
      long time,
      long time2
   ) {
      return new IdHolder(
         str,
         str2,
         stack,
         ClientOption_L6ONUCj5.REAL,
         time,
         time2
      );
   }

   public boolean isFinished(
      long time
   ) {
      return switch (this.base) {
         case TICKS -> time
         >= this.start + this.duration;
         case REAL -> System.nanoTime() >= this.start + this.duration;
      };
   }

   public float getRemainingRatio(
      long time
   ) {
      long time2 = switch (this.base) {
         case TICKS -> Math.max(
         0L,
         this.start
            + this.duration
            - time
      );
         case REAL -> Math.max(0L, this.start + this.duration - System.nanoTime());
      };
      return (float)time2
         / (float)this.duration;
   }

   public int getRemainingSeconds(
      long time
   ) {
      long time2 = switch (this.base) {
         case TICKS -> Math.max(
         0L,
         this.start
            + this.duration
            - time
      );
         case REAL -> Math.max(0L, this.start + this.duration - System.nanoTime());
      };
      return this.base == ClientOption_L6ONUCj5.TICKS
         ? (int)(
            time2
               / 20L
         )
         : (int)(
            time2
               / 1000000000L
         );
   }
}
