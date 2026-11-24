package dev.leg1tka.api.events.implement;

import dev.leg1tka.api.events.Event;
import lombok.Generated;

public class EventChangeSprint extends Event {
   private boolean sprinting;

   @Generated
   public boolean isSprinting() {
      return this.sprinting;
   }

   @Generated
   public void setSprinting(boolean sprinting) {
      this.sprinting = sprinting;
   }

   @Generated
   public EventChangeSprint(boolean sprinting) {
      this.sprinting = sprinting;
   }
}
