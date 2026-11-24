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


import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class GetclosestpointHelper implements IMinecraft {
   public static Vec3d getClosestPoint(
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

   @Generated
   private GetclosestpointHelper() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
