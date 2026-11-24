package dev.leg1tka.api.events.implement;

import dev.leg1tka.api.events.Event;
import lombok.Generated;
import net.minecraft.network.packet.Packet;

public class EventPacket extends Event {
   private final Packet<?> packet;
   private final EventPacket.Type type;

   @Generated
   public EventPacket(Packet<?> packet, EventPacket.Type type) {
      this.packet = packet;
      this.type = type;
   }

   @Generated
   public Packet<?> getPacket() {
      return this.packet;
   }

   @Generated
   public EventPacket.Type getType() {
      return this.type;
   }

   public static enum Type {
      SEND,
      RECEIVE;
   }
}
