package dev.leg1tka.mixin;

import dev.leg1tka.api.utils.render.fonts.ttf.Fonts;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftClient.class})
public class FontInitMixin {
   @Inject(
      method = {"onFinishedLoading"},
      at = {@At("TAIL")}
   )
   private void onFinishedLoading(CallbackInfo ci) {
      Fonts.init();
      dev.leg1tka.api.utils.render.fonts.msdf.Fonts.init();
   }
}
