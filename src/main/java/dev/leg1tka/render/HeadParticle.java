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


import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class HeadParticle extends BaseCustomParticle {
   public HeadParticle(
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
         val2,
         d4,
         d5,
         d6
      );
      this.setColor(1.0F, 1.0F, 1.0F);
      this.setAlpha(0.85F);
   }
}
