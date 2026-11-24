package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.TalismanIndication;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ItemModelManager.class})
public class ItemModelManagerMixin {
   @ModifyVariable(
      method = {"update"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private ItemStack replaceTalismanModel(ItemStack stack) {
      return TalismanIndication.INSTANCE.getRenderStack(stack);
   }
}
