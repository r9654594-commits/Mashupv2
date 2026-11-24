package dev.leg1tka.api.events.implement;

import dev.leg1tka.api.events.Event;
import lombok.Generated;
import net.minecraft.util.math.Vec3d;

public class EventOnTravelPost extends Event {
   private Vec3d oldVelocity;

   @Generated
   public EventOnTravelPost(Vec3d oldVelocity) {
      this.oldVelocity = oldVelocity;
   }

   @Generated
   public Vec3d getOldVelocity() {
      return this.oldVelocity;
   }

   @Generated
   public void setOldVelocity(Vec3d oldVelocity) {
      this.oldVelocity = oldVelocity;
   }
}
