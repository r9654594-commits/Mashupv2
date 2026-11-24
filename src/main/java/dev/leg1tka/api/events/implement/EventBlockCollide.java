package dev.leg1tka.api.events.implement;

import dev.leg1tka.api.events.Event;
import lombok.Generated;
import net.minecraft.util.math.BlockPos;

public class EventBlockCollide extends Event {
   private final BlockPos pos;

   public EventBlockCollide(BlockPos pos) {
      this.pos = pos;
   }

   @Generated
   public BlockPos getPos() {
      return this.pos;
   }
}
