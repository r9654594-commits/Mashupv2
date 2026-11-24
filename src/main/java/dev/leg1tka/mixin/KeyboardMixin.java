package dev.leg1tka.mixin;

import dev.leg1tka.api.events.implement.EventChunkReload;
import dev.leg1tka.modules.misc.Unhook;
import dev.leg1tka.core.IMinecraft;
import dev.leg1tka.utils.CallHelper;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Keyboard.class})
public class KeyboardMixin implements IMinecraft {
   @Inject(
      method = {"onKey"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onKey(long window, int key, int scan, int act, int mod, CallbackInfo ci) {
      if (!Unhook.isUnhooked()) {
         if (mc.currentScreen == null) {
            CallHelper.call(key, act);
            if (mc.currentScreen != null) {
               ci.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"processF3"},
      at = {@At("RETURN")}
   )
   private void processF3(int key, CallbackInfoReturnable<Boolean> cir) {
      if (key == 65 && (Boolean)cir.getReturnValue()) {
         new EventChunkReload().call();
      }
   }
}
