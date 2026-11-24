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


import java.util.List;
import net.minecraft.entity.effect.StatusEffects;

public enum CustomPotion {
   ASSASSIN(
      List.of(
         new PotionEffectEntry(StatusEffects.STRENGTH, 3),
         new PotionEffectEntry(StatusEffects.SPEED, 2),
         new PotionEffectEntry(StatusEffects.HASTE, 0),
         new PotionEffectEntry(StatusEffects.INSTANT_DAMAGE, 1)
      ),
      "Зелье ассасина"
   ),
   PALADIN(
      List.of(
         new PotionEffectEntry(StatusEffects.RESISTANCE, 0),
         new PotionEffectEntry(StatusEffects.FIRE_RESISTANCE, 0),
         new PotionEffectEntry(StatusEffects.HEALTH_BOOST, 2),
         new PotionEffectEntry(StatusEffects.INVISIBILITY, 2)
      ),
      "Зелье палладина"
   ),
   SLEEPING(
      List.of(
         new PotionEffectEntry(StatusEffects.WEAKNESS, 1),
         new PotionEffectEntry(StatusEffects.MINING_FATIGUE, 1),
         new PotionEffectEntry(StatusEffects.WITHER, 1),
         new PotionEffectEntry(StatusEffects.BLINDNESS, 0)
      ),
      "Снотворное"
   ),
   WRATH(List.of(new PotionEffectEntry(StatusEffects.STRENGTH, 4), new PotionEffectEntry(StatusEffects.SLOWNESS, 3)), "Зелье гнева"),
   CRACKER(
      List.of(
         new PotionEffectEntry(StatusEffects.SLOWNESS, 8),
         new PotionEffectEntry(StatusEffects.SPEED, 4),
         new PotionEffectEntry(StatusEffects.BLINDNESS, 9),
         new PotionEffectEntry(StatusEffects.GLOWING, 0)
      ),
      "Хлопушка"
   ),
   RADIATION(
      List.of(
         new PotionEffectEntry(StatusEffects.POISON, 1),
         new PotionEffectEntry(StatusEffects.WITHER, 1),
         new PotionEffectEntry(StatusEffects.SLOWNESS, 2),
         new PotionEffectEntry(StatusEffects.HUNGER, 4),
         new PotionEffectEntry(StatusEffects.GLOWING, 0)
      ),
      "Зелье радиации"
   ),
   HOLY_WATER(
      List.of(new PotionEffectEntry(StatusEffects.REGENERATION, 1), new PotionEffectEntry(StatusEffects.INVISIBILITY, 1), new PotionEffectEntry(StatusEffects.INSTANT_HEALTH, 1)), "Святая вода"
   );

   public final List<PotionEffectEntry> effects;
   public final String displayName;

   private CustomPotion(List<PotionEffectEntry> var3, String var4) {
      this.effects = var3;
      this.displayName = var4;
   }
}
