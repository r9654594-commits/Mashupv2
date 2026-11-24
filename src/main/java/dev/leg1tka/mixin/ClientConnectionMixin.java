package dev.leg1tka.mixin;

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.utils.SendsilentpacketHelper;
import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientConnection.class})
public abstract class ClientConnectionMixin {
   @Inject(
      method = {"channelRead0"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      EventPacket eventReceive = new EventPacket(packet, EventPacket.Type.RECEIVE);
      EventInvoker.invoke(eventReceive);
      if (eventReceive.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"send"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void send(Packet<?> packet, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (SendsilentpacketHelper.getSilentPackets().contains(packet)) {
         SendsilentpacketHelper.getSilentPackets().remove(packet);
      } else {
         EventPacket eventSend = new EventPacket(packet, EventPacket.Type.SEND);
         EventInvoker.invoke(eventSend);
         if (eventSend.isCancelled()) {
            ci.cancel();
         }
      }
   }
}
