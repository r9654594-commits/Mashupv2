package dev.leg1tka.mixin;

import dev.leg1tka.core.ModuleManager;
import dev.leg1tka.modules.misc.NameProtect;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({DrawContext.class})
public class DrawContextNameProtectMixin {
   @ModifyVariable(
      method = "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private String AspectDLC$patchStringShadow(String text) {
      return this.patch(text);
   }

   @ModifyVariable(
      method = "drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private String AspectDLC$patchString(String text) {
      return this.patch(text);
   }

   private String patch(String text) {
      if (ModuleManager.INSTANCE == null) {
         return text;
      } else {
         NameProtect nameProtect = ModuleManager.nameProtect;
         return nameProtect != null && nameProtect.isEnable() ? nameProtect.patchIncomingText(text) : text;
      }
   }
}
