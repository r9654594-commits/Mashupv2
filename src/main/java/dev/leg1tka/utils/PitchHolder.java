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


import net.minecraft.util.math.Vec3d;

public final class PitchHolder {
   public final float pitch;
   public final double distanceToTarget;
   public final int ticks;
   public final Vec3d landingPos;

   public PitchHolder(
      float f,
      double d,
      int n,
      Vec3d vec
   ) {
      this.pitch = f;
      this.distanceToTarget = d;
      this.ticks = n;
      this.landingPos = vec;
   }
}
