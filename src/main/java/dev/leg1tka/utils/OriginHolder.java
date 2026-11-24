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


import java.util.List;
import net.minecraft.util.math.Vec3d;

public class OriginHolder {
   public final Vec3d origin;
   public final long startTime;
   public final float baseRotation;
   public final List<DirectionHolder> particles;
   public final List<RadiusxHolder> orbitLines;

   public OriginHolder(
      Vec3d vec,
      long time,
      float f,
      List<DirectionHolder> direction,
      List<RadiusxHolder> list
   ) {
      this.origin = vec;
      this.startTime = time;
      this.baseRotation = f;
      this.particles = direction;
      this.orbitLines = list;
   }
}
