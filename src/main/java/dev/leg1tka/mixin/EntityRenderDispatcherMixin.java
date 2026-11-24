package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.NoRender;
import dev.leg1tka.core.ModuleManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {
   @Inject(
      method = {"renderShadow"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void AspectDLC$renderShadow(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      EntityRenderState renderState,
      float opacity,
      float tickDelta,
      WorldView world,
      float radius,
      CallbackInfo ci
   ) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Тени")) {
            ci.cancel();
         }
      }
   }
}
