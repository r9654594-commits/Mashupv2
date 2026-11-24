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


import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class GetpointHelper implements IMinecraft {
   private static Vec3d position = Vec3d.ZERO;
   private static Vec3d velocity = Vec3d.ZERO;

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
      if (velocity.equals(Vec3d.ZERO)) {
         velocity = new Vec3d(randomBest(-0.02, 0.02), randomBest(-0.02, 0.02), randomBest(-0.02, 0.02));
      }

      if (position.equals(Vec3d.ZERO)) {
         position = new Vec3d(
            0.0,
            x7
               * 0.5,
            0.0
         );
      }

      position = position.add(velocity);
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
      if (position.x
         >= x9
         )
       {
         velocity = new Vec3d(
            -randomBest(
               (double)x2,
               (double)x3
            ),
            velocity.getY(),
            velocity.getZ()
         );
      } else if (position.x
         <= -x9
         )
       {
         velocity = new Vec3d(
            randomBest(
               (double)x2,
               (double)x3
            ),
            velocity.getY(),
            velocity.getZ()
         );
      }

      if (position.y
         >= x7
            * 0.75) {
         velocity = new Vec3d(
            velocity.getX(),
            -randomBest(
               (double)x4,
               (double)x5
            ),
            velocity.getZ()
         );
      } else if (position.y
         <= x7
            * 0.3) {
         velocity = new Vec3d(
            velocity.getX(),
            randomBest(
               (double)x4,
               (double)x5
            ),
            velocity.getZ()
         );
      }

      if (position.z
         >= x10
         )
       {
         velocity = new Vec3d(
            velocity.getX(),
            velocity.getY(),
            -randomBest(
               (double)x2,
               (double)x3
            )
         );
      } else if (position.z
         <= -x10
         )
       {
         velocity = new Vec3d(
            velocity.getX(),
            velocity.getY(),
            randomBest(
               (double)x2,
               (double)x3
            )
         );
      }

      position = position.add(randomBest(-0.05, 0.05), 0.0, randomBest(-0.05, 0.05));
      Vec3d vec = entity.getPos()
         .add(position);
      MinecraftClient val = MinecraftClient.getInstance();
      if (val.player
            != null
         && val.world
            != null) {
         Vec3d vec2 = val.player
            .getEyePos();
         if (!isPointVisible(
            vec2,
            vec,
            entity
         )) {
            Vec3d vec3 = getNearestVisiblePoint(
               entity,
               vec2,
               x6,
               x7,
               d
            );
            if (vec3
               != null) {
               position = vec3.subtract(
                  entity.getPos()
               );
               return vec3;
            }
         }
      }

      return vec;
   }

   private static boolean isPointVisible(
      Vec3d vec,
      Vec3d vec2,
      Entity entity
   ) {
      MinecraftClient val = MinecraftClient.getInstance();
      if (val.world
         == null) {
         return true;
      } else {
         BlockHitResult val2 = val.world
            .raycast(
               new RaycastContext(
                  vec,
                  vec2,
                  ShapeType.COLLIDER,
                  FluidHandling.NONE,
                  val.player
               )
            );
         return val2.getType()
               == Type.MISS
            || entity.getBoundingBox()
               .contains(
                  val2.getPos()
               )
            || vec.squaredDistanceTo(
                  val2.getPos()
               )
               >= vec.squaredDistanceTo(
                     vec2
                  )
                  - 0.01;
      }
   }

   private static Vec3d getNearestVisiblePoint(
      Entity entity,
      Vec3d vec,
      double d,
      double d2,
      double d3
   ) {
      float x = (float)(
            d
               / 2.0
         )
         * 0.8F;

      for (float x2 = -x;
         x2
            <= x;
         x2 += 0.1F
      ) {
         for (float x3 = -x;
            x3
               <= x;
            x3 += 0.1F
         ) {
            for (float x4 = (float)(
                  d2
                     * 0.9
               );
               (double)x4
                  >= d2
                     * 0.3;
               x4 -= 0.1F
            ) {
               Vec3d vec2 = new Vec3d(
                  entity.getX()
                     + (double)x2,
                  entity.getY()
                     + (double)x4,
                  entity.getZ()
                     + (double)x3
               );
               if (isPointVisible(
                  vec,
                  vec2,
                  entity
               )) {
                  return vec2;
               }
            }
         }
      }

      return null;
   }

   private static double randomBest(
      double d,
      double d2
   ) {
      return ThreadLocalRandom.current().nextDouble()
            * (
               d2
                  - d
            )
         + d;
   }
}
