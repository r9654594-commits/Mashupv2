package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.NoRender;
import dev.leg1tka.core.ModuleManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
   @Inject(
      method = {"tiltViewWhenHurt"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$noHurtCameraShake(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Тряска")) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"showFloatingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$hideTotemAnimation(ItemStack stack, CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null && stack != null && stack.isOf(Items.TOTEM_OF_UNDYING)) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isTotemAnimationDisabled()) {
            ci.cancel();
         }
      }
   }
}
