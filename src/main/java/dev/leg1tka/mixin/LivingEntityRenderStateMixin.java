package dev.leg1tka.mixin;

import dev.leg1tka.utils.IClientAction_PPRmC3gr;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({LivingEntityRenderState.class})
public class LivingEntityRenderStateMixin implements IClientAction_PPRmC3gr {
   @Unique
   private boolean AspectDLC$seeInvisiblesTarget;

   @Override
   public boolean AspectDLC$isSeeInvisiblesTarget() {
      return this.AspectDLC$seeInvisiblesTarget;
   }

   @Override
   public void AspectDLC$setSeeInvisiblesTarget(boolean value) {
      this.AspectDLC$seeInvisiblesTarget = value;
   }
}
