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

public class PosHelper_3 {
   public final Vec3d pos;
   public final long startTimeMs;

   public PosHelper_3(Vec3d pos, long startTimeMs) {
      this.pos = pos;
      this.startTimeMs = startTimeMs;
   }

   public Vec3d pos() {
      return this.pos;
   }

   public long startTimeMs() {
      return this.startTimeMs;
   }
}
