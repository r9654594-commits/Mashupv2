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


import lombok.Generated;
import net.minecraft.network.packet.Packet;

public class GetsendtimeHelper {
   public final Packet<?> packet;
   public final long sendTime;

   public GetsendtimeHelper(
      Packet<?> val,
      long time
   ) {
      this.packet = val;
      this.sendTime = time;
   }

   @Generated
   public Packet<?> getPacket() {
      return this.packet;
   }

   @Generated
   public long getSendTime() {
      return this.sendTime;
   }
}
