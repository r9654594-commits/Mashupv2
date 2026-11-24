package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.EntityESP;
import dev.leg1tka.core.ModuleManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public abstract class EntityRendererMixin<S extends EntityRenderState> {
   @Inject(
      method = {"renderLabelIfPresent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$renderLabelIfPresent(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null) {
         EntityESP esp = ModuleManager.entityESP;
         if (esp != null) {
            if (esp.shouldHideVanillaTags()) {
               ci.cancel();
            }
         }
      }
   }
}
