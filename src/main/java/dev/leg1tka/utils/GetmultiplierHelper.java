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


import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class GetmultiplierHelper implements IMinecraft {
   public float getMultiplier(
      LivingEntity entity
   ) {
      return this.getDistanceToEdge(
               entity
            )
            <= 0.9
         ? 0.93F
         : 1.0F;
   }

   private double getDistanceToEdge(
      LivingEntity entity
   ) {
      Vec3d vec = mc.player
         .getEyePos();
      double dist = MathHelper.clamp(
         vec.x,
         entity.getBoundingBox()
            .minX,
         entity.getBoundingBox()
            .maxX
      );
      double dist2 = MathHelper.clamp(
         vec.z,
         entity.getBoundingBox()
            .minZ,
         entity.getBoundingBox()
            .maxZ
      );
      return Math.hypot(
         vec.x
            - dist,
         vec.z
            - dist2
      );
   }
}
