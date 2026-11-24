package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.SeeInvisibles;
import dev.leg1tka.core.IMinecraft;
import dev.leg1tka.utils.IClientAction_PPRmC3gr;
import dev.leg1tka.core.ModuleManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
   implements IMinecraft {
   @Inject(
      method = {"updateRenderState"},
      at = {@At("TAIL")}
   )
   private void AspectDLC$updateSeeInvisiblesState(T entity, S state, float tickDelta, CallbackInfo ci) {
      boolean shouldRenderInvisible = this.AspectDLC$shouldRenderInvisible(entity);
      ((IClientAction_PPRmC3gr)state).AspectDLC$setSeeInvisiblesTarget(shouldRenderInvisible);
      if (shouldRenderInvisible) {
         state.invisible = true;
         state.invisibleToPlayer = false;
      }
   }

   @ModifyConstant(
      method = {"render"},
      constant = {@Constant(
         intValue = 654311423
      )}
   )
   private int AspectDLC$changeInvisibleAlpha(int original, S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      return ((IClientAction_PPRmC3gr)state).AspectDLC$isSeeInvisiblesTarget() ? SeeInvisibles.INVISIBLE_COLOR : original;
   }

   @Unique
   private boolean AspectDLC$shouldRenderInvisible(T entity) {
      if (entity instanceof PlayerEntity player && ModuleManager.INSTANCE != null) {
         SeeInvisibles seeInvisibles = ModuleManager.seeInvisibles;
         return seeInvisibles != null && seeInvisibles.shouldRenderInvisible(player);
      }

      return false;
   }

   @Unique
   private PlayerEntity AspectDLC$resolvePlayer(S state) {
      if (state instanceof PlayerEntityRenderState playerState && mc.world != null) {
         return mc.world.getEntityById(playerState.id) instanceof PlayerEntity player ? player : null;
      }

      return null;
   }
}
