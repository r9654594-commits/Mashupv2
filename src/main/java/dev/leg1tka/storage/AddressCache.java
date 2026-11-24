package dev.leg1tka.storage;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.utils.*;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;

public class AddressCache {
   private static final Map<String, Address> cache = new ConcurrentHashMap<>();
   public static final ExecutorService pool = Executors.newFixedThreadPool(
      24,
      val -> {
         Thread val2 = new Thread(
            val,
            "FastPing-Worker"
         );
         val2.setDaemon(
            true
         );
         return val2;
      }
   );

   public static Optional<Address> getCached(
      String str
   ) {
      if (str
         == null) {
         return Optional.empty();
      } else {
         Address val = cache.get(
            str.toLowerCase()
         );
         return val
               != null
            ? Optional.of(
               val
            )
            : Optional.empty();
      }
   }

   public static void putCache(
      String str,
      Address val
   ) {
      if (str
            != null
         && val
            != null) {
         cache.put(
            str.toLowerCase(),
            val
         );
      }
   }

   public static Optional<Address> resolve(
      ServerAddress val
   ) {
      if (val
         == null) {
         return Optional.empty();
      } else {
         String str = val.getAddress()
            + ":"
            + val.getPort();
         Address val2 = cache.get(
            str.toLowerCase()
         );
         if (val2
            != null) {
            return Optional.of(
               val2
            );
         } else {
            Optional<Address> val3 = AllowedAddressResolver.DEFAULT.resolve(
                  val
               );
            val3.ifPresent(
               val4 -> {
                  cache.put(
                     str.toLowerCase(),
                     val4
                  );
                  cache.put(
                     val.getAddress()
                        .toLowerCase(),
                     val4
                  );
               }
            );
            return val3;
         }
      }
   }
}
