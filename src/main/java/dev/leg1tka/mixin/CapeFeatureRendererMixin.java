package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.Chams;
import dev.leg1tka.core.IMinecraft;
import dev.leg1tka.core.ModuleManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CapeFeatureRenderer.class})
public class CapeFeatureRendererMixin implements IMinecraft {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$hideCape(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      PlayerEntityRenderState playerState,
      float limbAngle,
      float limbDistance,
      CallbackInfo ci
   ) {
      if (ModuleManager.INSTANCE != null && mc.world != null) {
         Chams chams = ModuleManager.chams;
         if (chams != null && chams.isEnable()) {
            if (mc.world.getEntityById(playerState.id) instanceof PlayerEntity player && chams.shouldHideItemsAndCape(player)) {
               ci.cancel();
            }
         }
      }
   }
}
