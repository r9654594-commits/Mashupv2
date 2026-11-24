package dev.leg1tka.mixin;

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.implement.EventAttackEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerInteractionManager.class})
public abstract class ClientPlayerInteractionManagerMixin {
   @Inject(
      method = {"attackEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
      try {
         if (player != null && target != null) {
            EventAttackEntity event = new EventAttackEntity(player, target);
            EventInvoker.invoke(event);
            if (event.isCancelled()) {
               ci.cancel();
            }
         }
      } catch (Exception var5) {
      }
   }
}
