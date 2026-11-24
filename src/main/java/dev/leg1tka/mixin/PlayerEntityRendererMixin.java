package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.CustomModels;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerEntityRenderer.class})
public abstract class PlayerEntityRendererMixin {
   @Inject(
      method = {"scale"},
      at = {@At("TAIL")}
   )
   private void AspectDLC$applyCustomModelScale(PlayerEntityRenderState state, MatrixStack matrices, CallbackInfo ci) {
      if (CustomModels.INSTANCE.isEnable()) {
         float headScale = CustomModels.INSTANCE.getHeadScale();
         float bodyScale = CustomModels.INSTANCE.getBodyScale();
         float legsScale = CustomModels.INSTANCE.getLegsScale();
         float avgScale = (headScale + bodyScale + legsScale) / 3.0F;
         matrices.scale(avgScale * 0.8F, avgScale * 0.8F, avgScale * 0.8F);
      }
   }
}
