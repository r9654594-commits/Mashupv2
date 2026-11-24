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

public class PosHelper_2 {
   public final Vec3d pos;
   public final long spawnTime;
   public final float spinSeed;
   public final float sideYaw;

   public PosHelper_2(Vec3d pos, long spawnTime, float spinSeed, float sideYaw) {
      this.pos = pos;
      this.spawnTime = spawnTime;
      this.spinSeed = spinSeed;
      this.sideYaw = sideYaw;
   }

   public Vec3d pos() {
      return this.pos;
   }

   public long spawnTime() {
      return this.spawnTime;
   }

   public float spinSeed() {
      return this.spinSeed;
   }

   public float sideYaw() {
      return this.sideYaw;
   }
}
