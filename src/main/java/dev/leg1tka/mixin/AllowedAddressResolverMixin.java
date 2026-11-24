package dev.leg1tka.mixin;

import dev.leg1tka.storage.AddressCache;
import java.util.Optional;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AllowedAddressResolver.class})
public class AllowedAddressResolverMixin {
   @Inject(
      method = {"resolve"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onResolveHead(ServerAddress saddr, CallbackInfoReturnable<Optional<Address>> cir) {
      if (saddr != null) {
         String key = saddr.getAddress() + ":" + saddr.getPort();
         Optional cached = AddressCache.getCached(key);
         if (cached.isEmpty()) {
            cached = AddressCache.getCached(saddr.getAddress());
         }

         if (cached.isPresent()) {
            cir.setReturnValue(cached);
         }
      }
   }

   @Inject(
      method = {"resolve"},
      at = {@At("RETURN")}
   )
   private void onResolveReturn(ServerAddress saddr, CallbackInfoReturnable<Optional<Address>> cir) {
      if (saddr != null && cir.getReturnValue() != null && ((Optional)cir.getReturnValue()).isPresent()) {
         String key = saddr.getAddress() + ":" + saddr.getPort();
         AddressCache.putCache(key, (Address)((Optional)cir.getReturnValue()).get());
         AddressCache.putCache(saddr.getAddress(), (Address)((Optional)cir.getReturnValue()).get());
      }
   }
}
