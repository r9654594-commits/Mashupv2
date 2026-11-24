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


import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class TrackvelocityHelper {
   private final Deque<Float> velocities = new ArrayDeque<>(4);

   public void trackVelocity(
      ClientPlayerEntity player
   ) {
      if (player
         != null) {
         this.velocities
            .addLast(
               (float)Math.abs(
                  player.getVelocity()
                     .y
               )
            );
         if (this.velocities.size() > 4) {
            this.velocities.removeFirst();
         }
      }
   }

   public void trySpawnOnWaterEntry(
      ClientPlayerEntity player
   ) {
      if (player
         != null) {
         World val = player.getWorld();
         if (val
               != null
            && val.isClient
            )
          {
            double x = this.findWaterSurfaceY(
               player
            );
            float width = this.velocities
                  .isEmpty()
               ? 0.0F
               : Collections.max(this.velocities);
            spawnEmitter(
               val,
               player.getX(),
               x,
               player.getZ(),
               player.getWidth()
                  * 2.0F,
               width
            );
         }
      }
   }

   private double findWaterSurfaceY(
      ClientPlayerEntity player
   ) {
      World val = player.getWorld();
      BlockPos pos = BlockPos.ofFloored(
         player.getX(),
         player.getY(),
         player.getZ()
      );

      for (int i = 0;
         i
            < 5;
         i++
      ) {
         BlockPos pos2 = pos.up(
            i
         );
         FluidState val2 = val.getFluidState(
            pos2
         );
         if (val2.isIn(
            FluidTags.WATER
         )) {
            return (double)(
                  (float)pos2.getY()
                     + val2.getHeight(
                        val,
                        pos2
                     )
               )
               - 0.01;
         }
      }

      return player.getY();
   }

   public static void spawnEmitter(
      World val,
      double d,
      double d2,
      double d3,
      float f,
      float f2
   ) {
      if (val
            != null
         && val.isClient
         )
       {
         val.addParticle(
            RegisterHelper_1.WATER_SPLASH_EMITTER,
            d,
            d2,
            d3,
            (double)f,
            (double)f2,
            0.0
         );
      }
   }
}
