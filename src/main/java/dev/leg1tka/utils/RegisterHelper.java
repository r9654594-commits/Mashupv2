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

public final class RegisterHelper {
   public static final SimpleParticleType REVIVE = FabricParticleTypes.simple(true);
   public static final SimpleParticleType REVIVE_SPARK = FabricParticleTypes.simple(true);

   private RegisterHelper() {
   }

   public static void register() {
      Registry.register(
         Registries.PARTICLE_TYPE, Identifier.of("elysium", "revive"), REVIVE
      );
      Registry.register(
         Registries.PARTICLE_TYPE, Identifier.of("elysium", "revive_spark"), REVIVE_SPARK
      );
   }
}
