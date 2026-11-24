package dev.leg1tka.render;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;

public class CustomTrailParticle_YFPL1S2p extends SpriteBillboardParticle {
   private final float width;
   private final float speed;
   private boolean spawned;

   public CustomTrailParticle_YFPL1S2p(
      ClientWorld val,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6
   ) {
      super(
         val,
         d,
         d2,
         d3,
         0.0,
         0.0,
         0.0
      );
      this.width = Math.max(
         0.2F,
         (float)d4
      );
      this.speed = Math.max(
         0.0F,
         (float)d5
      );
      this.maxAge = 4;
      this.collidesWithWorld = false;
      this.setAlpha(0.0F);
      this.scale = 0.0F;
   }

   public void tick() {
      if (this.age++ >= this.maxAge) {
         this.markDead();
      } else {
         if (!this.spawned) {
            this.spawned = true;
            this.spawnChildren();
         }
      }
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.NO_RENDER;
   }

   private void spawnChildren() {
      int i = Math.max(
         8, MathHelper.ceil(this.width * 10.0F)
      );
      double x2 = Math.max(
         0.18, (double)this.width * 0.25
      );
      double x3 = 0.06
         + (double)this.speed * 0.05;

      for (int i2 = 0;
         i2
            < i;
         i2++
      ) {
         double x4 = (
               Math.PI * 2
            )
            * (double)i2
            / (double)i;
         double x5 = this.x
            + Math.cos(
                  x4
               )
               * x2;
         double x6 = this.z
            + Math.sin(
                  x4
               )
               * x2;
         double x7 = Math.cos(
               x4
            )
            * (0.02 + (double)this.speed * 0.02);
         double x8 = Math.sin(
               x4
            )
            * (0.02 + (double)this.speed * 0.02);
         this.world
            .addParticle(
               RegisterHelper_1.WATER_SPLASH,
               x5,
               this.y,
               x6,
               x7,
               x3,
               x8
            );
         this.world
            .addParticle(
               RegisterHelper_1.WATER_SPLASH_FOAM,
               x5,
               this.y + 0.03,
               x6,
               x7
                  * 0.55,
               x3
                  * 0.35,
               x8
                  * 0.55
            );
      }

      this.world.addParticle(RegisterHelper_1.WATER_SPLASH_RING, this.x, this.y, this.z, 0.0, 0.02 + (double)this.speed * 0.01, 0.0);
      int i3 = Math.max(
         3, MathHelper.ceil(this.width * 2.0F)
      );

      for (int i4 = 0;
         i4
            < i3;
         i4++
      ) {
         double x9 = (
               this.random.nextDouble() - 0.5
            )
            * 0.04;
         double x10 = 0.05
            + this.random.nextDouble() * 0.05
            + (double)this.speed * 0.03;
         double x11 = (
               this.random.nextDouble() - 0.5
            )
            * 0.04;
         this.world
            .addParticle(
               ParticleTypes.FALLING_WATER,
               this.x,
               this.y + 0.02,
               this.z,
               x9,
               x10,
               x11
            );
      }
   }
}
