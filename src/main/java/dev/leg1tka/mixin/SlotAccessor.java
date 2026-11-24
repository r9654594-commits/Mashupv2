package dev.leg1tka.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Slot.class})
public interface SlotAccessor {
   @Accessor("inventory")
   Inventory AspectDLC$getInventory();

   @Accessor("index")
   int AspectDLC$getIndex();
}
