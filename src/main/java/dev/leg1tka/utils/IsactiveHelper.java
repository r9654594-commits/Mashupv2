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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public final class IsactiveHelper {
   private static volatile boolean active = false;
   private static final List<StatusEffectInstance> EFFECTS = new CopyOnWriteArrayList<>();

   private IsactiveHelper() {
   }

   public static boolean isActive() {
      return active;
   }

   public static void setActive(
      boolean flag
   ) {
      active = flag;
      if (!flag
         )
       {
         EFFECTS.clear();
      }
   }

   public static List<StatusEffectInstance> getEffects() {
      return Collections.unmodifiableList(EFFECTS);
   }

   public static void addEffect(
      RegistryEntry<StatusEffect> effect,
      int n,
      int n2,
      boolean flag
   ) {
      if (effect
         != null) {
         removeEffect(
            effect
         );
         int x = flag
            ? -1
            : Math.max(
               1,
               n2
            );
         EFFECTS.add(
            new StatusEffectInstance(
               effect,
               x,
               Math.max(
                  0,
                  n
               )
            )
         );
      }
   }

   public static void removeEffect(
      RegistryEntry<StatusEffect> effect
   ) {
      if (effect
         != null) {
         EFFECTS.removeIf(
            val -> val.getEffectType()
                     .value()
                  == effect.value()
         );
      }
   }

   public static void clearEffects() {
      EFFECTS.clear();
   }

   public static void tick() {
      if (active) {
         List val = new ArrayList<>(
            EFFECTS.size()
         );
         boolean flag = false;

         for (StatusEffectInstance effect : EFFECTS) {
            if (effect.isInfinite()
               )
             {
               val.add(
                  effect
               );
            } else {
               int i = effect.getDuration()
                  - 1;
               if (i
                  <= 0) {
                  flag = true;
               } else {
                  val.add(
                     new StatusEffectInstance(
                        effect.getEffectType(),
                        i,
                        effect.getAmplifier()
                     )
                  );
                  flag = true;
               }
            }
         }

         if (flag
            )
          {
            EFFECTS.clear();
            EFFECTS.addAll(
               val
            );
         }
      }
   }
}
