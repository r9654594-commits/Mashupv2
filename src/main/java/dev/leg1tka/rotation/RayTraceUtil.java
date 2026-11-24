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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.jetbrains.annotations.NotNull;

public final class RayTraceUtil implements IMinecraft {
   public static HitResult rayTrace(
      double d,
      float f,
      float f2
   ) {
      Vec3d vec = mc.player
         .getCameraPosVec(1.0F);
      Vec3d vec2 = getRotationVector(
         f2,
         f
      );
      Vec3d vec3 = vec.add(
         vec2.x
            * d,
         vec2.y
            * d,
         vec2.z
            * d
      );
      return mc.world
         .raycast(
            new RaycastContext(
               vec,
               vec3,
               ShapeType.OUTLINE,
               FluidHandling.NONE,
               mc.player
            )
         );
   }

   public static Vec3d getBestVector(
      Entity entity
   ) {
      Vec3d vec = mc.player
         .getEyePos();
      Box box = entity.getBoundingBox();
      double dist = 0.1;
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
               double dist6 = vec.distanceTo(
                  vec3
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

      return vec2;
   }

   public static Angle fromVec3d(
      Vec3d vec
   ) {
      return new Angle(
         (float)MathHelper.wrapDegrees(
            Math.toDegrees(
                  Math.atan2(
                     vec.z,
                     vec.x
                  )
               )
               - 90.0
         ),
         (float)MathHelper.wrapDegrees(
            Math.toDegrees(
               -Math.atan2(
                  vec.y,
                  Math.hypot(
                     vec.x,
                     vec.z
                  )
               )
            )
         )
      );
   }

   @NotNull
   public static Vec3d getRotationVector(
      float f,
      float f2
   ) {
      return new Vec3d(
         (double)(
            MathHelper.sin(
                  -f2
                     * (float) (Math.PI / 180.0)
               )
               * MathHelper.cos(
                  f
                     * (float) (Math.PI / 180.0)
               )
         ),
         (double)(
            -MathHelper.sin(
               f
                  * (float) (Math.PI / 180.0)
            )
         ),
         (double)(
            MathHelper.cos(
                  -f2
                     * (float) (Math.PI / 180.0)
               )
               * MathHelper.cos(
                  f
                     * (float) (Math.PI / 180.0)
               )
         )
      );
   }

   public static Vec2f getRotations(
      Entity entity
   ) {
      return getRotations(
         entity.getX(),
         entity.getY(),
         entity.getZ()
      );
   }

   public static Vec2f getRotations(
      Vec3d vec
   ) {
      return getRotations(
         vec.x,
         vec.y,
         vec.z
      );
   }

   public static Vec2f getRotations(
      double d,
      double d2,
      double d3
   ) {
      double x = d
         - mc.player.getX();
      double x2 = d2
         - mc.player.getEyeY();
      double x3 = d3
         - mc.player.getZ();
      double x4 = (double)MathHelper.sqrt(
         (float)(
            x
                  * x
               + x3
                  * x3
         )
      );
      float x5 = (float)(
         MathHelper.atan2(
                  x3,
                  x
               )
               * (180.0 / Math.PI)
            - 90.0
      );
      float x6 = (float)(
         -MathHelper.atan2(
               x2,
               x4
            )
            * (180.0 / Math.PI)
      );
      return new Vec2f(
         x5,
         x6
      );
   }

   public static float[] getRotations(
      Direction direction
   ) {
      float[] var10000;
      switch (direction) {
         case DOWN:
            var10000 = new float[2];
            var10000[0] = mc.player.getYaw();
            var10000[1] = 90.0F;
            break;
         case UP:
            var10000 = new float[2];
            var10000[0] = mc.player.getYaw();
            var10000[1] = -90.0F;
            break;
         case NORTH:
            var10000 = new float[2];
            var10000[0] = 180.0F;
            var10000[1] = mc.player.getPitch();
            break;
         case SOUTH:
            var10000 = new float[2];
            var10000[0] = 0.0F;
            var10000[1] = mc.player.getPitch();
            break;
         case WEST:
            var10000 = new float[2];
            var10000[0] = 90.0F;
            var10000[1] = mc.player.getPitch();
            break;
         case EAST:
            var10000 = new float[2];
            var10000[0] = -90.0F;
            var10000[1] = mc.player.getPitch();
            break;
         default:
            throw new MatchException(null, null);
      }

      return var10000;
   }

   public static float[] correctRotation(
      float[] floats
   ) {
      floats[0] -= floats[0]
         % MouseGCD.getGCDValue();
      floats[1] -= floats[1]
         % MouseGCD.getGCDValue();
      float[] var10000 = new float[2];
      var10000[0] = floats[0];
      var10000[1] = floats[1];
      return var10000;
   }

   public static float getFixRotate(
      float f
   ) {
      return getDeltaMouse(
            f
         )
         * MouseGCD.getGCDValue();
   }

   public static float getDeltaMouse(
      float f
   ) {
      return (float)Math.round(
         f
            / MouseGCD.getGCDValue()
      );
   }

   @Generated
   private RayTraceUtil() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
