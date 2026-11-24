package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.WorldTweaks;
import dev.leg1tka.render.RenderskyHelper;
import net.minecraft.client.render.SkyRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SkyRendering.class})
public class SkyRenderingMixin {
   @Inject(
      method = {"renderSky"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderShaderSky(float red, float green, float blue, CallbackInfo ci) {
      WorldTweaks worldTweaks = WorldTweaks.INSTANCE;
      if (worldTweaks != null && worldTweaks.isShaderEnabled()) {
         RenderskyHelper.INSTANCE.renderSky();
         ci.cancel();
      }
   }

   @Inject(
      method = {"close"},
      at = {@At("HEAD")}
   )
   private void closeShaderSky(CallbackInfo ci) {
      RenderskyHelper.INSTANCE.close();
   }
}
