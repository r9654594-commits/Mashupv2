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


public class VelocityHelper {
   public final double velocity;
   public final double gravity;
   public final double drag;

   public VelocityHelper(double velocity, double gravity, double drag) {
      this.velocity = velocity;
      this.gravity = gravity;
      this.drag = drag;
   }

   public double velocity() {
      return this.velocity;
   }

   public double gravity() {
      return this.gravity;
   }

   public double drag() {
      return this.drag;
   }
}
