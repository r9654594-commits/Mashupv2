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


import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class RegisterHelper_1 {
   public static final SimpleParticleType WATER_SPLASH_EMITTER = FabricParticleTypes.simple(true);
   public static final SimpleParticleType WATER_SPLASH = FabricParticleTypes.simple(true);
   public static final SimpleParticleType WATER_SPLASH_FOAM = FabricParticleTypes.simple(true);
   public static final SimpleParticleType WATER_SPLASH_RING = FabricParticleTypes.simple(true);

   private RegisterHelper_1() {
   }

   public static void register() {
      Registry var10000 = Registries.PARTICLE_TYPE;
      String var3 = "elysium";
      Registry.register(var10000, Identifier.of(var3, "water_splash_emitter"), WATER_SPLASH_EMITTER);
      var10000 = Registries.PARTICLE_TYPE;
      String var5 = "elysium";
      Registry.register(var10000, Identifier.of(var5, "water_splash"), WATER_SPLASH);
      var10000 = Registries.PARTICLE_TYPE;
      String var7 = "elysium";
      Registry.register(var10000, Identifier.of(var7, "water_splash_foam"), WATER_SPLASH_FOAM);
      var10000 = Registries.PARTICLE_TYPE;
      String var9 = "elysium";
      Registry.register(var10000, Identifier.of(var9, "water_splash_ring"), WATER_SPLASH_RING);
   }
}
