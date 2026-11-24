package dev.leg1tka.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.implement.EventOnMovePost;
import dev.leg1tka.modules.render.ShaderEsp;
import dev.leg1tka.modules.render.SeeInvisibles;
import dev.leg1tka.core.IMinecraft;
import dev.leg1tka.core.ModuleManager;
import dev.leg1tka.modules.misc.NoPush;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public abstract class EntityMixin implements IMinecraft {
   @ModifyExpressionValue(
      method = {"move"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;isControlledByPlayer()Z"
      )}
   )
   private boolean fixFallDistanceCalculation(boolean original) {
      return (Object)this == mc.player ? false : original;
   }

   @Inject(
      method = {"pushAwayFrom"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pushAwayFrom(CallbackInfo ci) {
      if ((Object)this == mc.player && ModuleManager.INSTANCE != null) {
         NoPush noPush = ModuleManager.noPush;
         if (noPush != null && noPush.isEnable() && noPush.getCollisionList().is("Игроки")) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"isPushedByFluids"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void isPushedByFluids(CallbackInfoReturnable<Boolean> ci) {
      if ((Object)this == mc.player && ModuleManager.INSTANCE != null) {
         NoPush noPush = ModuleManager.noPush;
         if (noPush != null && noPush.isEnable() && noPush.getCollisionList().is("Вода")) {
            ci.setReturnValue(false);
         }
      }
   }

   @Inject(
      method = {"getTeamColorValue"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
      if (ModuleManager.INSTANCE != null) {
         ShaderEsp shaderEsp = ModuleManager.shaderEsp;
         if (shaderEsp != null && shaderEsp.shouldOutline((Entity)(Object)this)) {
            cir.setReturnValue(shaderEsp.getOutlineColor());
         }
      }
   }

   @Inject(
      method = {"isInvisibleTo"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$allowSeeInvisibles(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
      if ((Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)(Object)this instanceof PlayerEntity target && ModuleManager.INSTANCE != null) {
         SeeInvisibles seeInvisibles = ModuleManager.seeInvisibles;
         if (seeInvisibles != null && seeInvisibles.shouldRenderInvisible(target)) {
            cir.setReturnValue(false);
         }

         return;
      }
   }

   @Inject(
      method = {"updateVelocity"},
      at = {@At("TAIL")}
   )
   private void AspectDLC$onUpdateVelocity(float speed, Vec3d movementInput, CallbackInfo ci) {
      Entity me = (Entity)(Object)this;
      if (MinecraftClient.getInstance().player != null
         && me.getId() == MinecraftClient.getInstance().player.getId()
         && EventInvoker.hasListeners(EventOnMovePost.class)) {
         EventOnMovePost event = new EventOnMovePost(speed, movementInput);
         event.call();
      }
   }
}
