package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.NoRender;
import dev.leg1tka.core.ModuleManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   targets = {"net/minecraft/client/gui/hud/InGameHud$HeartType"}
)
public class InGameHudHeartTypeMixin {
   @Redirect(
      method = {"fromPlayerState"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z"
      )
   )
   private static boolean hookStatus(PlayerEntity plr, RegistryEntry<StatusEffect> eff) {
      NoRender nr = ModuleManager.noRender;
      return nr == null || !nr.isEnabled("Плохие эффекты") || !eff.equals(StatusEffects.POISON) && !eff.equals(StatusEffects.WITHER)
         ? plr.hasStatusEffect(eff)
         : false;
   }
}
