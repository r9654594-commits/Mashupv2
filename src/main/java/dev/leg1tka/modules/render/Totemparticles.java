package dev.leg1tka.modules.render;

import dev.leg1tka.core.Module;
import dev.leg1tka.core.Category;
import dev.leg1tka.utils.SpawnHelper;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventPopTotem;

public class Totemparticles extends Module {
   public static final Totemparticles INSTANCE = new Totemparticles();

   public Totemparticles() {
      super("TotemParticles", "Custom totem particles", Category.RENDER);
   }

   @EventLink
   public void onPopTotem(EventPopTotem event) {
      if (event != null && event.getPlayer() != null) {
         SpawnHelper.spawn(event.getPlayer());
      }
   }
}
