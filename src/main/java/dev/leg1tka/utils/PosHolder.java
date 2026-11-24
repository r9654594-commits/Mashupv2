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

public class PosHolder {
   public final Vec3d pos;
   public final long timestamp;
   public final float ageSec;
   public PosHolder(
      Vec3d vec,
      long time,
      float f
   ) {
      this.pos = vec;
      this.timestamp = time;
      this.ageSec = f;
   }
}
