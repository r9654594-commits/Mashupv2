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
import net.minecraft.world.World;

public final class SpawnHelper {
   private SpawnHelper() {
   }

   public static void spawn(
      LivingEntity entity
   ) {
      if (entity
         != null) {
         World val = entity.getWorld();
         if (val
               != null
            && val.isClient
            )
          {
            double x = entity.getX();
            double x2 = entity.getY()
               + (double)entity.getHeight()
                  * 0.5;
            double x3 = entity.getZ();
            float width = Math.max(
               0.4F,
               entity.getWidth()
            );
            int i = entity.getId();

            for (int i2 = 0;
               i2
                  < 17;
               i2++
            ) {
               double x4 = (
                     Math.random() - 0.5
                  )
                  * (double)entity.getWidth();
               double x5 = Math.random()
                  * (double)entity.getHeight();
               double x6 = (
                     Math.random() - 0.5
                  )
                  * (double)entity.getWidth();
               val.addParticle(
                  RegisterHelper.REVIVE,
                  x
                     + x4,
                  x2
                     + x5,
                  x3
                     + x6,
                  (double)width,
                  (double)i,
                  0.0
               );
            }

            for (int i3 = 0;
               i3
                  < 99;
               i3++
            ) {
               double x7 = 0.32;
               double x8 = (
                     Math.random() - 0.5
                  )
                  * x7;
               double x9 = Math.random()
                  * 0.22;
               double x10 = (
                     Math.random() - 0.5
                  )
                  * x7;
               val.addParticle(
                  RegisterHelper.REVIVE_SPARK,
                  x,
                  x2,
                  x3,
                  (double)i,
                  x8,
                  x9
               );
            }
         }
      }
   }
}
