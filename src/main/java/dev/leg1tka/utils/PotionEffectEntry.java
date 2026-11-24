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

public class PotionEffectEntry {
   public final RegistryEntry<StatusEffect> effect;
   public final int amplifier;

   public PotionEffectEntry(RegistryEntry<StatusEffect> effect, int amplifier) {
      this.effect = effect;
      this.amplifier = amplifier;
   }

   public RegistryEntry<StatusEffect> effect() {
      return this.effect;
   }

   public int amplifier() {
      return this.amplifier;
   }
}
