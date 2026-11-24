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
import net.minecraft.util.math.MathHelper;

public class CustomTrailParticle_SicxOS8J extends SpriteBillboardParticle {
   private final SpriteProvider sprites;
   private final float startScale;

   public CustomTrailParticle_SicxOS8J(
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
         d4,
         d5,
         d6
      );
      this.sprites = val2;
      this.startScale = 0.14F + this.random.nextFloat() * 0.06F;
      this.maxAge = 16
         + this.random.nextInt(6);
      this.collidesWithWorld = false;
      this.velocityY = 0.01;
      this.setColor(0.82F, 0.92F, 1.0F);
      this.scale = this.startScale;
      this.setAlpha(0.9F);
      this.setSpriteForAge(
         val2
      );
   }

   public void tick() {
      if (this.age++ >= this.maxAge) {
         this.markDead();
      } else {
         this.velocityX *= 0.92;
         this.velocityY *= 0.9;
         this.velocityZ *= 0.92;
         this.move(this.velocityX, this.velocityY, this.velocityZ);
         float alpha = (float)this.age
            / (float)this.maxAge;
         this.scale = this.startScale
            * (
               1.0F
                  + alpha
                     * 2.2F
            );
         this.setAlpha(
            MathHelper.clamp(
               1.0F
                  - alpha
                     * 1.15F,
               0.0F,
               1.0F
            )
         );
         this.setSpriteForAge(this.sprites);
      }
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
   }
}
