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



import dev.leg1tka.modules.render.*;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public final class CreateparticleHelper_5 implements ParticleFactory<SimpleParticleType> {
   private final FabricSpriteProvider sprites;

   public CreateparticleHelper_5(
      FabricSpriteProvider val
   ) {
      this.sprites = val;
   }

   public Particle createParticle(
      SimpleParticleType val,
      ClientWorld val2,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6
   ) {
      return new BaseCustomParticle(
         val2,
         d,
         d2,
         d3,
         this.sprites,
         d4,
         d5,
         d6
      );
   }
}
