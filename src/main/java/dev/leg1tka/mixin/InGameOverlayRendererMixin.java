package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.NoRender;
import dev.leg1tka.core.ModuleManager;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameOverlayRenderer.class})
public class InGameOverlayRendererMixin {
   @Inject(
      method = {"renderFireOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void AspectDLC$renderFireOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Огонь")) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"renderInWallOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void AspectDLC$renderInWallOverlay(Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Оверлей в блоке")) {
            ci.cancel();
         }
      }
   }
}
