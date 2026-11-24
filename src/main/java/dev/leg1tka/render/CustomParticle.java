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
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CustomParticle extends SpriteBillboardParticle {
   private final SpriteProvider sprites;
   private final int targetEntityId;
   private final float startScale;

   public CustomParticle(
      ClientWorld val,
      double d,
      double d2,
      double d3,
      SpriteProvider val2,
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
      this.sprites = val2;
      this.targetEntityId = MathHelper.floor(
         d4
      );
      this.startScale = 0.16F + this.random.nextFloat() * 0.08F;
      this.maxAge = 18
         + this.random.nextInt(8);
      this.collidesWithWorld = false;
      this.setColor(1.0F, 1.0F, 1.0F);
      this.setAlpha(0.0F);
      this.scale = this.startScale;
      this.velocityX = d5
            * 0.08
         + (this.random.nextDouble() - 0.5) * 0.02;
      this.velocityY = d6
            * 0.08
         + (this.random.nextDouble() - 0.5) * 0.02
         + 0.02;
      this.velocityZ = (this.random.nextDouble() - 0.5) * 0.02;
      this.setSpriteForAge(
         val2
      );
   }

   public void tick() {
      if (this.age++ >= this.maxAge) {
         this.markDead();
      } else {
         Entity entity = this.world
            .getEntityById(this.targetEntityId);
         if (entity
            != null) {
            double x2 = entity.getX();
            double x3 = entity.getY()
               + (double)entity.getHeight()
                  * 0.5;
            double x4 = entity.getZ();
            double x5 = x2
               - this.x;
            double x6 = x3
               - this.y;
            double x7 = x4
               - this.z;
            double x8 = Math.max(
               0.001,
               Math.sqrt(
                  x5
                        * x5
                     + x6
                        * x6
                     + x7
                        * x7
               )
            );
            double x9 = this.age
                  > 6
               ? 0.075
               : 0.025;
            this.velocityX += x5
               / x8
               * x9;
            this.velocityY += x6
               / x8
               * x9;
            this.velocityZ += x7
               / x8
               * x9;
         }

         this.velocityX *= 0.94;
         this.velocityY *= 0.94;
         this.velocityZ *= 0.94;
         this.move(this.velocityX, this.velocityY, this.velocityZ);
         this.setSpriteForAge(this.sprites);
         float height = (float)this.age
            / (float)this.maxAge;
         this.setAlpha(
            MathHelper.clamp(
               1.0F
                  - height
                     * 1.25F,
               0.0F,
               1.0F
            )
         );
         this.scale = this.startScale
            * (
               1.0F
                  - height
                     * 0.45F
            );
      }
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
   }
}
