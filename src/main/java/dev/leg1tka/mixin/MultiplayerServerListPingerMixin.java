package dev.leg1tka.mixin;

import dev.leg1tka.storage.AddressCache;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MultiplayerServerListPinger.class})
public abstract class MultiplayerServerListPingerMixin {
   @Shadow
   public abstract void add(ServerInfo var1, Runnable var2, Runnable var3);

   @Inject(
      method = {"add"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAddHead(ServerInfo info, Runnable saver, Runnable cb, CallbackInfo ci) {
      if (!Thread.currentThread().getName().startsWith("FastPing-Worker")) {
         ci.cancel();
         AddressCache.pool.execute(() -> {
            try {
               this.add(info, saver, cb);
            } catch (Exception var5) {
            }
         });
      }
   }
}
