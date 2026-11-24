package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;


import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.network.packet.Packet;

public final class SendsilentpacketHelper implements IMinecraft {
   private static final List<Packet<?>> silentPackets = new ArrayList<>();

   public static void sendSilentPacket(
      Packet<?> val
   ) {
      silentPackets.add(
         val
      );
      mc.getNetworkHandler()
         .sendPacket(
            val
         );
   }

   public static void sendPacket(
      Packet<?> val
   ) {
      mc.getNetworkHandler()
         .sendPacket(
            val
         );
   }

   @Generated
   private SendsilentpacketHelper() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }

   @Generated
   public static List<Packet<?>> getSilentPackets() {
      return silentPackets;
   }
}
