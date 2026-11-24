package dev.leg1tka.rotation;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext.ShapeType;

public final class RaycastUtil implements IMinecraft {
   private static Vec3d rotationPoint = Vec3d.ZERO;
   private static Vec3d rotationMotion = Vec3d.ZERO;

   public static Vec3d getRotationPoint() {
      return rotationPoint;
   }

   public static Vec3d getNearestPoint(
      Entity entity
   ) {
      Box box = entity.getBoundingBox();
      double dist = 0.1;
      Vec3d vec = null;
      double dist2 = Double.MAX_VALUE;

      for (double dist3 = box.minX;
         dist3
            <= box.maxX;
         dist3 += dist
      ) {
         for (double dist4 = box.minY;
            dist4
               <= box.maxY;
            dist4 += dist
         ) {
            for (double dist5 = box.minZ;
               dist5
                  <= box.maxZ;
               dist5 += dist
            ) {
               Vec3d vec2 = new Vec3d(
                  dist3,
                  dist4,
                  dist5
               );
               double dist6 = mc.player
                  .getEyePos()
                  .distanceTo(
                     vec2
                  );
               if (dist6
                  < dist2
                  )
                {
                  dist2 = dist6;
                  vec = vec2;
               }
            }
         }
      }

      return vec;
   }

   public static Vec3d getPoint(
      Entity entity
   ) {
      Box box = entity.getBoundingBox();
      double x = box.maxX
         - box.minX;
      double x2 = box.maxY
         - box.minY;
      double x3 = box.maxZ
         - box.minZ;
      double x4 = box.minX
         + x
            / 2.0;
      double x5 = box.minY
         + x2
            * 0.7;
      double x6 = box.minZ
         + x3
            / 2.0;
      double x7 = (double)System.currentTimeMillis()
         / 50.0;
      int x8 = entity.getId();
      double x9 = Math.sin(
            x7
               + (double)x8
         )
         * x
         * 0.45;
      double x10 = Math.cos(
            x7
                  * 0.8
               + (double)x8
         )
         * x2
         * 0.1;
      double x11 = Math.cos(
            x7
                  * 1.2
               + (double)x8
         )
         * x3
         * 0.45;
      return new Vec3d(
         x4
            + x9,
         x5
            + x10,
         x6
            + x11
      );
   }

   public static Vec3d getPoint2(
      Entity entity
   ) {
      Box box = entity.getBoundingBox();
      double x = box.maxX
         - box.minX;
      double x2 = box.maxY
         - box.minY;
      double x3 = box.maxZ
         - box.minZ;
      double x4 = box.minX
         + x
            / 2.0;
      double x5 = box.minY
         + x2
            * 0.65;
      double x6 = box.minZ
         + x3
            / 2.0;
      double x7 = (double)System.currentTimeMillis()
         / 65.0;
      int x8 = entity.getId();
      double x9 = Math.sin(
            x7
               + (double)x8
         )
         * x
         * 0.7;
      double x10 = Math.cos(
            x7
                  * 0.8
               + (double)x8
         )
         * x2
         * 0.4;
      double x11 = Math.cos(
            x7
                  * 1.2
               + (double)x8
         )
         * x3
         * 0.7;
      return new Vec3d(
         x4
            + x9,
         x5
            + x10,
         x6
            + x11
      );
   }

   public static Vec3d getNearestVisiblePoint(
      Entity entity,
      Vec3d vec,
      double d
   ) {
      if (vec
            != null
         && mc.player != null
         && mc.world != null) {
         if (isPointVisible(
            entity,
            vec,
            d
         )) {
            return vec;
         } else {
            Box box = entity.getBoundingBox();
            double dist = 0.12;
            Vec3d vec2 = null;
            double dist2 = Double.MAX_VALUE;

            for (double dist3 = box.minX;
               dist3
                  <= box.maxX;
               dist3 += dist
            ) {
               for (double dist4 = box.minY;
                  dist4
                     <= box.maxY;
                  dist4 += dist
               ) {
                  for (double dist5 = box.minZ;
                     dist5
                        <= box.maxZ;
                     dist5 += dist
                  ) {
                     Vec3d vec3 = new Vec3d(
                        dist3,
                        dist4,
                        dist5
                     );
                     if (isPointVisible(
                        entity,
                        vec3,
                        d
                     )) {
                        double dist6 = vec3.squaredDistanceTo(
                           vec
                        );
                        if (dist6
                           < dist2
                           )
                         {
                           dist2 = dist6;
                           vec2 = vec3;
                        }
                     }
                  }
               }
            }

            return vec2
                  != null
               ? vec2
               : vec;
         }
      } else {
         return vec;
      }
   }

   private static boolean isPointVisible(
      Entity entity,
      Vec3d vec,
      double d
   ) {
      Vec3d vec2 = mc.player
         .getEyePos();
      double dist = vec2.distanceTo(
         vec
      );
      if (dist
         > d
         )
       {
         return false;
      } else {
         Vec3d vec3 = vec.subtract(
               vec2
            )
            .normalize();
         if (!RaytraceHelper.rayTrace(
            vec3,
            dist
               + 0.2,
            entity.getBoundingBox()
         )) {
            return false;
         } else {
            BlockHitResult val = RaytraceHelper.raycast(
               vec2,
               vec,
               ShapeType.COLLIDER,
               mc.player
            );
            return val.getType()
                  == Type.MISS
               || vec2.squaredDistanceTo(
                     val.getPos()
                  )
                  >= vec2.squaredDistanceTo(
                        vec
                     )
                     - 1.0E-4;
         }
      }
   }

   public static Vec3d getMultipoint(
      Entity entity,
      double d
   ) {
      float x2 = 0.005F;
      float x3 = 0.015F;
      float x4 = 0.0015F;
      float x5 = 0.015F;
      double x6 = entity.getBoundingBox()
         .getLengthX();
      double x7 = entity.getBoundingBox()
         .getLengthY();
      double x8 = entity.getBoundingBox()
         .getLengthZ();
      if (rotationMotion.equals(Vec3d.ZERO)) {
         rotationMotion = new Vec3d(DirectionHelper.randomBest(-0.02F, 0.02F), DirectionHelper.randomBest(-0.02F, 0.02F), DirectionHelper.randomBest(-0.02F, 0.02F));
      }

      if (rotationPoint.equals(Vec3d.ZERO)) {
         rotationPoint = new Vec3d(
            0.0,
            x7
               * 0.5,
            0.0
         );
      }

      rotationPoint = rotationPoint.add(rotationMotion);
      double x9 = (
            x6
               - 0.1
         )
         / 2.0;
      double x10 = (
            x8
               - 0.1
         )
         / 2.0;
      if (rotationPoint.x
         >= x9
         )
       {
         rotationMotion = new Vec3d(
            -DirectionHelper.randomBest(
               (double)x2,
               (double)x3
            ),
            rotationMotion.getY(),
            rotationMotion.getZ()
         );
      } else if (rotationPoint.x
         <= -x9
         )
       {
         rotationMotion = new Vec3d(
            DirectionHelper.randomBest(
               (double)x2,
               (double)x3
            ),
            rotationMotion.getY(),
            rotationMotion.getZ()
         );
      }

      if (rotationPoint.y
         >= x7
            * 0.75) {
         rotationMotion = new Vec3d(
            rotationMotion.getX(),
            -DirectionHelper.randomBest(
               (double)x4,
               (double)x5
            ),
            rotationMotion.getZ()
         );
      } else if (rotationPoint.y
         <= x7
            * 0.3) {
         rotationMotion = new Vec3d(
            rotationMotion.getX(),
            DirectionHelper.randomBest(
               (double)x4,
               (double)x5
            ),
            rotationMotion.getZ()
         );
      }

      if (rotationPoint.z
         >= x10
         )
       {
         rotationMotion = new Vec3d(
            rotationMotion.getX(),
            rotationMotion.getY(),
            -DirectionHelper.randomBest(
               (double)x2,
               (double)x3
            )
         );
      } else if (rotationPoint.z
         <= -x10
         )
       {
         rotationMotion = new Vec3d(
            rotationMotion.getX(),
            rotationMotion.getY(),
            DirectionHelper.randomBest(
               (double)x2,
               (double)x3
            )
         );
      }

      rotationPoint.add(DirectionHelper.randomBest(-0.05F, 0.05F), 0.0, DirectionHelper.randomBest(-0.05F, 0.05F));
      if (!RaytraceHelper.rayTrace(
         mc.player.getRotationVector(),
         d,
         entity.getBoundingBox()
      )) {
         float x11 = (float)(
               x6
                  / 2.0
            )
            * 0.8F;

         for (float x12 = -x11;
            x12
               <= x11;
            x12 += 0.1F
         ) {
            for (float x13 = -x11;
               x13
                  <= x11;
               x13 += 0.1F
            ) {
               for (float x14 = (float)(
                     x7
                        * 0.9
                  );
                  (double)x14
                     >= x7
                        * 0.3;
                  x14 -= 0.1F
               ) {
                  Vec3d vec = new Vec3d(
                     entity.getX()
                        + (double)x12,
                     entity.getY()
                        + (double)x14,
                     entity.getZ()
                        + (double)x13
                  );
                  Angle val = RayTraceUtil.fromVec3d(
                     vec
                  );
                  if (RaytraceHelper.rayTrace(
                     val.toVector(),
                     d,
                     entity.getBoundingBox()
                  )) {
                     rotationPoint = new Vec3d(
                        (double)x12,
                        (double)x14,
                        (double)x13
                     );
                     return entity.getPos()
                        .add(rotationPoint);
                  }
               }
            }
         }
      }

      return entity.getPos()
         .add(rotationPoint);
   }

   @Generated
   private RaycastUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
