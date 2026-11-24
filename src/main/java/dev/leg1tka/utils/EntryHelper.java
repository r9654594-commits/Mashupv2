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


import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public class EntryHelper {
   public final RegistryEntry<StatusEffect> entry;
   public final int amp;
   public final int durTicks;
   public final boolean infinite;

   public EntryHelper(RegistryEntry<StatusEffect> entry, int amp, int durTicks, boolean infinite) {
      this.entry = entry;
      this.amp = amp;
      this.durTicks = durTicks;
      this.infinite = infinite;
   }

   public RegistryEntry<StatusEffect> entry() {
      return this.entry;
   }

   public int amp() {
      return this.amp;
   }

   public int durTicks() {
      return this.durTicks;
   }

   public boolean infinite() {
      return this.infinite;
   }

   public String label() {
      String name = ((StatusEffect)this.entry
            .value())
         .getName()
         .getString();
      String name2 = this.amp
            == 0
         ? ""
         : " " + roman(this.amp + 1);
      String name3 = this.infinite
         ? "inf"
         : this.durTicks
               / 20
               / 60
            + ":"
            + String.format(
               "%02d",
               this.durTicks
                  / 20
                  % 60
            );
      return name
         + name2
         + "  "
         + name3;
   }

   private static String roman(
      int n
   ) {
      return switch (n) {
         case 1 -> "I";
         case 2 -> "II";
         case 3 -> "III";
         case 4 -> "IV";
         case 5 -> "V";
         default -> String.valueOf(
         n
      );
      };
   }

}
