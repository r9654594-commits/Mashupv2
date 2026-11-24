package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.WorldTweaks;
import dev.leg1tka.core.ModuleManager;
import net.minecraft.client.world.ClientWorld.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Properties.class})
public class ClientWorldPropertiesMixin {
   @Shadow
   private long timeOfDay;

   @Inject(
      method = {"setTimeOfDay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$setTimeOfDay(long timeOfDay, CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null) {
         WorldTweaks tweaks = ModuleManager.worldTweaks;
         if (tweaks != null && tweaks.isTimeEnabled()) {
            this.timeOfDay = tweaks.getForcedTime();
            ci.cancel();
         }
      }
   }
}
