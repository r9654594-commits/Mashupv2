package dev.leg1tka.mixin;

import dev.leg1tka.AspectDLC;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Main.class})
public class MainMixin {
   @Inject(
      method = {"main"},
      at = {@At("HEAD")}
   )
   private static void onMain(String[] args, CallbackInfo ci) {
      if (AspectDLC.INSTANCE.isServer) {
         try {
            AspectDLC.INSTANCE.closeMinecraft();
         } catch (Exception var3) {
            var3.printStackTrace();
         }

         AspectDLC.INSTANCE.isServer = false;
      }
   }
}
