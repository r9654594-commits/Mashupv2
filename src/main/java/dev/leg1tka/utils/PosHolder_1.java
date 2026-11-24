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

public class PosHolder_1 {
   public Vec3d pos;
   public double distance;
   public double score;
   public PosHolder_1(
      Vec3d vec,
      double d,
      double d2
   ) {
      this.pos = vec;
      this.distance = d;
      this.score = d2;
   }
}
