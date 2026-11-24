package dev.leg1tka.api.events.implement;

import dev.leg1tka.api.events.Event;

public class EventSlowWalking extends Event {
   private boolean cancelled;

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
