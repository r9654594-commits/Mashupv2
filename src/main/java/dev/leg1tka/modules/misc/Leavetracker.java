package dev.leg1tka.modules.misc;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class Leavetracker extends Module {
   public static Leavetracker INSTANCE = new Leavetracker();
   private final Map<UUID, NameHelper_1> trackedPlayers = new HashMap<>();
   private ClientWorld lastWorld;
   private boolean initialized;

   public Leavetracker() {
      super("LeaveTracker", "Пишет координаты ливнутых игроков из прогрузки", Category.MISC);
   }

   @Override
   public void onDisable() {
      this.trackedPlayers.clear();
      this.initialized = false;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val5
   ) {
      if (mc.player != null && mc.world != null) {
         if (mc.world != this.lastWorld) {
            this.lastWorld = mc.world;
            this.trackedPlayers.clear();
            this.initialized = false;
         }

         if (!this.initialized) {
            this.snapshotPlayers();
            this.initialized = true;
         } else {
            Set val = new HashSet<>();

            for (PlayerEntity player2 : mc.world
               .getPlayers()) {
               if (player2
                     != mc.player
                  && player2.isAlive()
                  )
                {
                  UUID uuid = player2.getUuid();
                  val.add(
                     uuid
                  );
                  this.trackedPlayers
                     .put(
                        uuid,
                        new NameHelper_1(
                           player2.getName()
                              .getString(),
                           player2.getBlockPos()
                        )
                     );
               }
            }

            Iterator<Entry<UUID, NameHelper_1>> val2 = this.trackedPlayers
               .entrySet()
               .iterator();

            while (
               val2.hasNext()
            ) {
               Entry<UUID, NameHelper_1> val3 = val2.next();
               if (!val.contains(
                  val3.getKey()
               )) {
                  NameHelper_1 val4 = val3.getValue();
                  double d = mc.player
                     .squaredDistanceTo(
                        (double)val4.pos
                           .getX(),
                        (double)val4.pos
                           .getY(),
                        (double)val4.pos
                           .getZ()
                     );
                  if (d
                     < 65536.0) {
                     ChatUtil.sendMessage(
                        Formatting.GRAY
                           + val4.name
                           + Formatting.WHITE
                           + " ливнул на "
                           + Formatting.GRAY
                           + val4.pos
                              .getX()
                           + " "
                           + val4.pos
                              .getY()
                           + " "
                           + val4.pos
                              .getZ()
                     );
                  }

                  val2.remove();
               }
            }
         }
      }
   }

   private void snapshotPlayers() {
      this.trackedPlayers.clear();

      for (PlayerEntity player2 : mc.world
         .getPlayers()) {
         if (player2
               != mc.player
            && player2.isAlive()
            )
          {
            this.trackedPlayers
               .put(
                  player2.getUuid(),
                  new NameHelper_1(
                     player2.getName()
                        .getString(),
                     player2.getBlockPos()
                  )
               );
         }
      }
   }
}
