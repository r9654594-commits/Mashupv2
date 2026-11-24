package dev.leg1tka.storage;
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
import dev.leg1tka.utils.*;


import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventLook;
import dev.leg1tka.api.events.implement.EventRotation;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public class FreeLookStorage implements IMinecraft {
   private static boolean active;
   private static float freeYaw;
   private static float freePitch;

   public FreeLookStorage() {
      EventInvoker.register(this);
   }

   public static boolean isActive() {
      return active;
   }

   @EventLink
   public void onLook(
      EventLook event
   ) {
      if (active) {
         this.rotateTowards(
            event.getYaw(),
            event.getPitch()
         );
         event.cancel();
      }
   }

   @EventLink
   public void onRotation(
      EventRotation event
   ) {
      if (active) {
         event.setYaw(
            freeYaw
         );
         event.setPitch(
            freePitch
         );
      } else {
         freeYaw = event.getYaw();
         freePitch = event.getPitch();
      }
   }

   private void rotateTowards(
      double d,
      double d2
   ) {
      freePitch = MathHelper.clamp(
         (float)(
            (double)freePitch
               + d2
                  * 0.15
         ),
         -90.0F,
         90.0F
      );
      freeYaw = (float)(
         (double)freeYaw
            + d
               * 0.15
      );
   }

   @Generated
   public static void setActive(
      boolean flag
   ) {
      active = flag;
   }

   @Generated
   public static float getFreeYaw() {
      return freeYaw;
   }

   @Generated
   public static float getFreePitch() {
      return freePitch;
   }

   @Generated
   public static void setFreeYaw(
      float f
   ) {
      freeYaw = f;
   }

   @Generated
   public static void setFreePitch(
      float f
   ) {
      freePitch = f;
   }
}
