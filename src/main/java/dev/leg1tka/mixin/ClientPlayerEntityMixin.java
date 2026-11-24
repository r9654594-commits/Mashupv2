package dev.leg1tka.mixin;

import com.mojang.authlib.GameProfile;
import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.implement.EventCloseInv;
import dev.leg1tka.api.events.implement.EventMove;
import dev.leg1tka.api.events.implement.EventOnTravelPost;
import dev.leg1tka.api.events.implement.EventPlayerSync;
import dev.leg1tka.api.events.implement.EventPlayerSyncEnd;
import dev.leg1tka.api.events.implement.EventPostMotion;
import dev.leg1tka.api.events.implement.EventSlowWalking;
import dev.leg1tka.api.events.implement.EventSprint;
import dev.leg1tka.api.events.implement.EventUpdate;
import dev.leg1tka.api.events.implement.EventUpdatePost;
import dev.leg1tka.rotation.RotationStorage;
import dev.leg1tka.utils.IstargetprotocolbelowonenineteenHelper;
import dev.leg1tka.core.IMinecraft;
import dev.leg1tka.core.ModuleManager;
import dev.leg1tka.modules.combat.Aura;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientPlayerEntity.class})
public abstract class ClientPlayerEntityMixin extends PlayerEntity implements IMinecraft {
   @Shadow
   @Final
   public ClientPlayNetworkHandler networkHandler;

   public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
      super(world, pos, yaw, gameProfile);
   }

   @Shadow
   public abstract void closeScreen();

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "HEAD",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V"
      )}
   )
   private void onTick(CallbackInfo ci) {
      if (EventInvoker.hasListeners(EventUpdate.class)) {
         new EventUpdate().call();
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "TAIL",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V"
      )}
   )
   private void onTickPost(CallbackInfo ci) {
      if (EventInvoker.hasListeners(EventUpdatePost.class)) {
         new EventUpdatePost().call();
      }

      if (EventInvoker.hasListeners(EventPostMotion.class)) {
         new EventPostMotion().call();
      }

      if (this.shouldSyncRotation()) {
         this.headYaw = this.getYaw();
         this.prevHeadYaw = this.getYaw();
         this.bodyYaw = this.getYaw();
         this.prevBodyYaw = this.getYaw();
      }
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("HEAD")}
   )
   private void sendMovementPackets(CallbackInfo ci) {
      if (EventInvoker.hasListeners(EventPlayerSync.class)) {
         new EventPlayerSync().call();
      }
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("RETURN")}
   )
   private void sendMovementPacketsEnd(CallbackInfo ci) {
      if (EventInvoker.hasListeners(EventPlayerSyncEnd.class)) {
         new EventPlayerSyncEnd().call();
      }
   }

   @Unique
   private boolean shouldSyncRotation() {
      return ModuleManager.aura.isEnable() && Aura.clientLook.isState() && RotationStorage.instance.isRotating();
   }

   @Redirect(
      method = {"tickMovement"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z",
         ordinal = 1
      ),
      require = 0
   )
   private boolean onSprintKeyPressed(KeyBinding instance) {
      if (!IstargetprotocolbelowonenineteenHelper.isTargetProtocolBelowOneNineteen() || !this.horizontalCollision && !this.collidedSoftly) {
         EventSprint event = new EventSprint();
         event.call();
         return event.isCancelled() ? false : instance.isPressed();
      } else {
         return false;
      }
   }

   @Redirect(
      method = {"tickMovement"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
      ),
      require = 0
   )
   private boolean onSlowDownRedirect(ClientPlayerEntity player) {
      if (player.isUsingItem()) {
         EventSlowWalking event = new EventSlowWalking();
         event.call();
         return player.isUsingItem() && player.getVehicle() == null && !event.isCancelled();
      } else {
         return player.isUsingItem() && player.getVehicle() == null;
      }
   }

   @Inject(
      method = {"pushOutOfBlocks"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pushOutOfBlocks(double x, double z, CallbackInfo ci) {
      if (ModuleManager.noPush.isEnable() && ModuleManager.noPush.getCollisionList().is("Блоки")) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"move"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMoveHook(MovementType movementType, Vec3d movement, @NotNull CallbackInfo ci) {
      EventMove event = new EventMove(movement);
      event.call();
      if (event.isCancelled() || !event.getMovePos().equals(movement)) {
         if (event.isCancelled()) {
            ci.cancel();
         } else {
            double d = this.getX();
            double e = this.getZ();
            super.move(movementType, event.getMovePos());
            float f = (float)Math.sqrt(Math.pow(this.getX() - d, 2.0) + Math.pow(this.getZ() - e, 2.0));
            this.updateLimbs(f);
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"closeHandledScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onCloseHandledScreen(CallbackInfo ci) {
      int syncId = this.currentScreenHandler.syncId;
      EventCloseInv event = new EventCloseInv(syncId);
      event.call();
      if (!event.isCancelled()) {
         this.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(syncId));
      }

      this.closeScreen();
      ci.cancel();
   }

   @Inject(
      method = {"dropSelectedItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
      if (ModuleManager.lockSlot != null && ModuleManager.lockSlot.isCurrentSlotLockedForDrop()) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void onTickElytraVelocity(CallbackInfo ci) {
      if (this.isGliding()) {
         if (EventInvoker.hasListeners(EventOnTravelPost.class)) {
            Vec3d currentVelocity = this.getVelocity();
            EventOnTravelPost event = new EventOnTravelPost(currentVelocity.multiply(0.99F, 0.98F, 0.99F));
            event.call();
            if (!event.getOldVelocity().equals(currentVelocity.multiply(0.99F, 0.98F, 0.99F))) {
               this.setVelocity(event.getOldVelocity());
            }
         }
      }
   }
}
