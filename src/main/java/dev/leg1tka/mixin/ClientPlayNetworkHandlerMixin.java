package dev.leg1tka.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.modules.misc.Unhook;
import dev.leg1tka.clickgui.GetconnectionsHelper;
import dev.leg1tka.utils.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public abstract class ClientPlayNetworkHandlerMixin {
   @Shadow
   private ClientWorld world;

   @Inject(
      method = {"sendChatMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void sendChatMessage(@NotNull String message, CallbackInfo ci) {
      if (message.startsWith("+baritone ")) {
         if (Unhook.isUnhooked() && !Unhook.isBaritoneAllowed()) {
            ci.cancel();
         } else {
            String baritoneCommand = "#" + message.substring(10);
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && mc.player.networkHandler != null) {
               mc.player.networkHandler.sendChatMessage(baritoneCommand);
            }

            ci.cancel();
         }
      } else if (message.startsWith("#")) {
         ci.cancel();
      } else if (Unhook.isUnhooked() && message.startsWith(".")) {
         ci.cancel();
      } else if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.commandStorage != null) {
         if (message.startsWith(AspectDLC.INSTANCE.commandStorage.getPrefix())) {
            if (Unhook.isUnhooked()) {
               ci.cancel();
            } else {
               try {
                  AspectDLC.INSTANCE
                     .commandStorage
                     .getDispatcher()
                     .execute(message.substring(AspectDLC.INSTANCE.commandStorage.getPrefix().length()), AspectDLC.INSTANCE.commandStorage.getSource());
               } catch (CommandSyntaxException var5) {
                  ChatUtil.sendMessage(Formatting.RED + "Ошибка в использовании!");
               }

               ci.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"onEntityVelocityUpdate"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
      EventPacket event = new EventPacket(packet, EventPacket.Type.RECEIVE);
      event.call();
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"onExplosion"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
      EventPacket event = new EventPacket(packet, EventPacket.Type.RECEIVE);
      event.call();
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"onEntityPositionSync"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onEntityPositionSync(EntityPositionSyncS2CPacket packet, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (this.world == null || mc.player == null || mc.world == null) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"onGameMessage"},
      at = {@At("HEAD")}
   )
   private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
   }

   @Inject(
      method = {"onGameJoin"},
      at = {@At("HEAD")}
   )
   private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
      GetconnectionsHelper.finishBotConnectStage();
   }
}
