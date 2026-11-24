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


import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

public class PointsHelper {
   public final Vec3d[] points;
   public final BlockHitResult blockHit;
   public final Vec3d hitPos;
   public final Entity entityHit;
   public final Vec3d entityHitPos;

   public PointsHelper(Vec3d[] points, BlockHitResult blockHit, Vec3d hitPos, Entity entityHit, Vec3d entityHitPos) {
      this.points = points;
      this.blockHit = blockHit;
      this.hitPos = hitPos;
      this.entityHit = entityHit;
      this.entityHitPos = entityHitPos;
   }

   public Vec3d[] points() {
      return this.points;
   }

   public BlockHitResult blockHit() {
      return this.blockHit;
   }

   public Vec3d hitPos() {
      return this.hitPos;
   }

   public Entity entityHit() {
      return this.entityHit;
   }

   public Vec3d entityHitPos() {
      return this.entityHitPos;
   }
}
