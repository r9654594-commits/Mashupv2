package dev.leg1tka.mixin;

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.implement.EventChangeSprint;
import dev.leg1tka.api.events.implement.EventOnTravelPost;
import dev.leg1tka.modules.render.SwingAnimations;
import dev.leg1tka.core.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin {
   @Inject(
      method = {"setSprinting"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSetSprinting(boolean sprinting, CallbackInfo ci) {
      if ((Object)(Object)(Object)(Object)(Object)(Object) (Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)this instanceof ClientPlayerEntity && sprinting) {
         try {
            EventChangeSprint event = new EventChangeSprint(true);
            EventInvoker.invoke(event);
            if (!event.isSprinting()) {
               ci.cancel();
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }
   }

   @Inject(
      method = {"getHandSwingDuration"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
      if ((Object)this == MinecraftClient.getInstance().player) {
         if (ModuleManager.INSTANCE != null) {
            SwingAnimations tweaks = ModuleManager.swingAnimations;
            if (tweaks != null && tweaks.isEnable() && tweaks.smoothEnabled.isState()) {
               cir.setReturnValue((int)tweaks.slowAnimationSpeed.get());
            }
         }
      }
   }

   @Inject(
      method = {"calcGlidingVelocity"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void onsetvelocity(Vec3d oldVelocity, CallbackInfoReturnable<Vec3d> cir) throws Exception {
      if (EventInvoker.hasListeners(EventOnTravelPost.class)) {
         EventOnTravelPost event = new EventOnTravelPost(oldVelocity.multiply(0.99F, 0.98F, 0.99F));
         EventInvoker.invoke(event);
         cir.setReturnValue(event.getOldVelocity());
      }
   }
}
