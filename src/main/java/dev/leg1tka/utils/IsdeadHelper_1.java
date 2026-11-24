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


import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;

public class IsdeadHelper_1 {
   public Vec3d position;
   public Vec3d velocity;
   public int color;
   public float size;
   public long lifeTime;
   public long birthTime;
   public float alpha = 1.0F;
   public float smoothFactor;
   public long lastUpdateNs;
   public double gravity;
   public IsdeadHelper_1(
      Vec3d vec,
      Vec3d vec2,
      int n,
      float f,
      long time,
      float f2,
      double d
   ) {
      this.position = vec;
      this.velocity = vec2;
      this.color = n;
      this.size = f;
      this.lifeTime = time;
      this.birthTime = System.currentTimeMillis();
      this.lastUpdateNs = System.nanoTime();
      this.smoothFactor = f2;
      this.gravity = d;
   }

   public boolean isDead() {
      return System.currentTimeMillis() - this.birthTime >= this.lifeTime;
   }

   public void update(
      MinecraftClient val
   ) {
      long time = System.nanoTime();
      double x2 = (double)(
            time
               - this.lastUpdateNs
         )
         / 1.0E9;
      this.lastUpdateNs = time;
      float alpha2 = Math.min(
         1.0F, (float)(System.currentTimeMillis() - this.birthTime) / (float)this.lifeTime
      );
      double x3 = Math.pow(
         1.0
            - (double)alpha2,
         (double)this.smoothFactor
      );
      double x4 = this.velocity
         .x;
      double x5 = this.velocity
         .y;
      double x6 = this.velocity
         .z;
      double x7 = this.position
         .x;
      double x8 = this.position
         .y;
      double x9 = this.position
         .z;
      x7 += x4
         * x3
         * x2
         * 60.0;
      if (!checkCollision(
         x7,
         this.position.y,
         this.position.z,
         this.size,
         val
      )) {
         x4 = -x4
            * 0.8;
         x7 = this.position
            .x;
      }

      x8 += x5
         * x3
         * x2
         * 60.0;
      if (!checkCollision(
         x7,
         x8,
         this.position.z,
         this.size,
         val
      )) {
         x5 = -x5
            * 1.5;
         x8 = this.position
            .y;
      }

      x9 += x6
         * x3
         * x2
         * 60.0;
      if (!checkCollision(
         x7,
         x8,
         x9,
         this.size,
         val
      )) {
         x6 = -x6
            * 0.8;
         x9 = this.position
            .z;
      }

      this.position = new Vec3d(
         x7,
         x8,
         x9
      );
      this.velocity = new Vec3d(
         x4
            * 0.9999,
         x5
               * 0.9999
            - this.gravity,
         x6
            * 0.9999
      );
      this.alpha = 1.0F
         - alpha2;
   }

   public static boolean checkCollision(
      double d,
      double d2,
      double d3,
      float f,
      MinecraftClient val
   ) {
      if (val.world
         == null) {
         return false;
      } else {
         double d4 = (double)f
            * 0.5;
         int i = MathHelper.floor(
            d
               - d4
         );
         int i2 = MathHelper.floor(
            d
               + d4
         );
         int i3 = MathHelper.floor(
            d2
               - d4
         );
         int i4 = MathHelper.floor(
            d2
               + d4
         );
         int i5 = MathHelper.floor(
            d3
               - d4
         );
         int i6 = MathHelper.floor(
            d3
               + d4
         );
         Mutable val2 = new Mutable();

         for (int i7 = i;
            i7
               <= i2;
            i7++
         ) {
            for (int i8 = i3;
               i8
                  <= i4;
               i8++
            ) {
               for (int i9 = i5;
                  i9
                     <= i6;
                  i9++
               ) {
                  val2.set(
                     i7,
                     i8,
                     i9
                  );
                  BlockState state = val.world
                     .getBlockState(
                        val2
                     );
                  if (!state.isAir()
                     && state.isSolidBlock(
                        val.world,
                        val2
                     )) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }
}
