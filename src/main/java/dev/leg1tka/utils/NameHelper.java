package dev.leg1tka.utils;
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
import dev.leg1tka.storage.*;


import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;

public final class NameHelper {
   private final String name;
   private final String address;
   private final ClientConnection connection;
   private final ClientPlayNetworkHandler handler;
   private final ClientWorld world;
   private final ClientPlayerEntity player;
   private final ClientPlayerInteractionManager interactionManager;
   private final Session session;
   private final ServerInfo serverInfo;

   public NameHelper(
      String str,
      String str2,
      ClientConnection val,
      ClientPlayNetworkHandler hand,
      ClientWorld val2,
      ClientPlayerEntity player2,
      ClientPlayerInteractionManager player3,
      Session val3,
      ServerInfo val4
   ) {
      this.name = str;
      this.address = str2;
      this.connection = val;
      this.handler = hand;
      this.world = val2;
      this.player = player2;
      this.interactionManager = player3;
      this.session = val3;
      this.serverInfo = val4;
   }

   public String name() {
      return this.name;
   }

   public String address() {
      return this.address;
   }

   public ClientConnection connection() {
      return this.connection;
   }

   public ClientPlayNetworkHandler handler() {
      return this.handler;
   }

   public ClientWorld world() {
      return this.world;
   }

   public ClientPlayerEntity player() {
      return this.player;
   }

   public ClientPlayerInteractionManager interactionManager() {
      return this.interactionManager;
   }

   public Session session() {
      return this.session;
   }

   public ServerInfo serverInfo() {
      return this.serverInfo;
   }
}
