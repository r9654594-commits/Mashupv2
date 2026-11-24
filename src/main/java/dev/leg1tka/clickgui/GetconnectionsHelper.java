package dev.leg1tka.clickgui;
import dev.leg1tka.animation.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import dev.leg1tka.mixin.IMinecraftClientAccessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.util.ReferenceCountUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class GetconnectionsHelper {
   private static final List<NameHelper> connections = new CopyOnWriteArrayList<>();
   private static volatile boolean ignoreBotMessages;
   private static volatile boolean bypassResourcePacksDuringBotConnect;

   public static List<NameHelper> getConnections() {
      pruneDeadConnections();
      return new ArrayList<>(connections);
   }

   public static boolean shouldBypassResourcePacks() {
      return bypassResourcePacksDuringBotConnect;
   }

   public static void finishBotConnectStage() {
      bypassResourcePacksDuringBotConnect = false;
   }

   public static String getCurrentSessionName() {
      MinecraftClient val = MinecraftClient.getInstance();
      return val.getSession()
            == null
         ? ""
         : val.getSession()
            .getUsername();
   }

   public static List<String> getSessionNames(
      boolean flag
   ) {
      pruneDeadConnections();
      Set val = new LinkedHashSet<>();
      if (flag
         )
       {
         String name2 = getCurrentSessionName();
         if (!name2.isBlank()
            )
          {
            val.add(
               name2
            );
         }
      }

      for (NameHelper val2 : connections) {
         if (val2.name()
               != null
            && !val2.name()
               .isBlank()) {
            val.add(
               val2.name()
            );
         }
      }

      return new ArrayList<>(
         val
      );
   }

   public static boolean toggleIgnoreBotMessages() {
      ignoreBotMessages = !ignoreBotMessages;
      return ignoreBotMessages;
   }

   public static boolean isIgnoreBotMessages() {
      return ignoreBotMessages;
   }

   public static void connect(
      String str,
      String str2
   ) {
      MinecraftClient val = MinecraftClient.getInstance();
      if (val.getSession()
            != null
         && str
            != null
         && !str.isBlank()
         && str2
            != null
         && !str2.isBlank()
         )
       {
         Session val2 = val.getSession();
         ServerInfo val3 = val.getCurrentServerEntry();
         pruneDeadConnections();
         disconnectSessionsByName(
            str,
            Text.literal("Replaced")
         );
         NameHelper val4 = freezeCurrentSession();
         ((IMinecraftClientAccessor)val)
            .setSession(
               createSessionWithName(
                  val.getSession(),
                  str
               )
            );
         bypassResourcePacksDuringBotConnect = true;
         val.execute(
            () -> {
               try {
                  ConnectScreen.connect(
                     new MultiplayerScreen(new TitleScreen()),
                     val,
                     ServerAddress.parse(
                        str2
                     ),
                     new ServerInfo(
                        str2,
                        str2,
                        ServerType.OTHER
                     ),
                     false,
                     null
                  );
               } catch (Exception var6) {
                  bypassResourcePacksDuringBotConnect = false;
                  restoreAfterConnectFailure(
                     val,
                     val4,
                     val2,
                     val3
                  );
               }
            }
         );
      }
   }

   public static void pulseBots(
      boolean flag
   ) {
      for (NameHelper val : connections) {
         if (isConnectionUsable(
            val
         )) {
            if (flag
               )
             {
               val.handler()
                  .sendPacket(
                     new PlayerInteractItemC2SPacket(
                        Hand.MAIN_HAND,
                        0,
                        val.player()
                           .getYaw(),
                        val.player()
                           .getPitch()
                     )
                  );
            } else {
               val.handler()
                  .sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }
         }
      }
   }

   public static void sayAll(
      String str
   ) {
      for (NameHelper val : connections) {
         if (isConnectionUsable(
            val
         )) {
            if (str.startsWith(
               "/"
            )) {
               val.handler()
                  .sendChatCommand(
                     str.substring(
                        1
                     )
                  );
            } else {
               val.handler()
                  .sendChatMessage(
                     str
                  );
            }
         }
      }
   }

   public static boolean control(
      String str
   ) {
      if (str
            != null
         && !str.isBlank()
         )
       {
         pruneDeadConnections();
         MinecraftClient val = MinecraftClient.getInstance();
         return val.player
                  != null
               && val.world
                  != null
               && str.equalsIgnoreCase(
                  getCurrentSessionName()
               )
            ? true
            : connections.stream()
               .filter(
                  val2 -> matchesName(
                        val2.name(),
                        str
                     )
               )
               .findFirst()
               .map(
                  val3 -> {
                     if (!isConnectionUsable(
                        val3
                     )) {
                        connections.remove(
                           val3
                        );
                        return false;
                     } else {
                        NameHelper val4 = freezeCurrentSession();
                        if (!activateSession(
                           val3
                        )) {
                           if (val4
                                 != null
                              && activateSession(
                                 val4
                              )) {
                              connections.remove(
                                 val4
                              );
                           }

                           return false;
                        } else {
                           connections.remove(
                              val3
                           );
                           return true;
                        }
                     }
                  }
               )
               .orElse(false);
      } else {
         return false;
      }
   }

   public static boolean say(
      String str,
      String str2
   ) {
      pruneDeadConnections();
      return connections.stream()
         .filter(
            val -> matchesName(
                  val.name(),
                  str
               )
         )
         .findFirst()
         .map(
            val2 -> {
               if (!isConnectionUsable(
                  val2
               )) {
                  connections.remove(
                     val2
                  );
                  return false;
               } else {
                  if (str2.startsWith(
                     "/"
                  )) {
                     val2.handler()
                        .sendChatCommand(
                           str2.substring(
                              1
                           )
                        );
                  } else {
                     val2.handler()
                        .sendChatMessage(
                           str2
                        );
                  }

                  return true;
               }
            }
         )
         .orElse(false);
   }

   public static boolean remove(
      String str
   ) {
      if (str
            != null
         && !str.isBlank()
         )
       {
         return disconnectSessionsByName(
               str,
               Text.literal("Removed")
            )
            > 0;
      } else {
         return false;
      }
   }

   public static boolean restore() {
      return restore(null);
   }

   public static boolean restore(
      String str
   ) {
      pruneDeadConnections();
      String name2 = str
               != null
            && !str.isBlank()
         ? str
         : (connections.isEmpty() ? "" : connections.get(connections.size() - 1).name());
      return !name2.isBlank()
         && control(
            name2
         );
   }

   private static NameHelper freezeCurrentSession() {
      MinecraftClient val = MinecraftClient.getInstance();
      if (val.getNetworkHandler()
            != null
         && val.world
            != null
         && val.player
            != null) {
         ClientPlayNetworkHandler hand = val.getNetworkHandler();
         makeNettyBot(
            hand,
            val.getSession()
               .getUsername(),
            val.player
         );
         NameHelper val2 = new NameHelper(
            val.getSession()
               .getUsername(),
            val.getCurrentServerEntry()
                  != null
               ? val.getCurrentServerEntry()
                  .address
               : "",
            hand.getConnection(),
            hand,
            val.world,
            val.player,
            val.interactionManager,
            val.getSession(),
            val.getCurrentServerEntry()
         );
         replaceConnection(
            val2
         );
         clearActiveSession(
            val
         );
         return val2;
      } else {
         return null;
      }
   }

   private static boolean activateSession(
      NameHelper val
   ) {
      if (!isConnectionUsable(
         val
      )) {
         return false;
      } else {
         MinecraftClient val2 = MinecraftClient.getInstance();
         IMinecraftClientAccessor val3 = (IMinecraftClientAccessor)val2;
         Channel val4 = getChannel(
            val.connection()
         );
         if (val4
            != null) {
            ChannelPipeline var10000 = val4.pipeline();
            if (var10000.get("bot_filter") != null) {
               var10000 = val4.pipeline();
               var10000.remove("bot_filter");
            }
         }

         try {
            setMinecraftClientField(
               val2,
               ClientPlayNetworkHandler.class,
               val.handler()
            );
            val3.setSession(
               val.session()
                     != null
                  ? val.session()
                  : createSessionWithName(
                     val2.getSession(),
                     val.name()
                  )
            );
            setMinecraftClientField(
               val2,
               ServerInfo.class,
               val.serverInfo()
                     != null
                  ? val.serverInfo()
                  : createServerInfo(
                     val.name(),
                     val.address()
                  )
            );
            val3.setItemUseCooldown(
               0
            );
            val2.world = val.world();
            val2.player = val.player();
            val2.cameraEntity = val.player();
            val2.interactionManager = val.interactionManager();
            if (val2.worldRenderer
               != null) {
               val2.worldRenderer
                  .setWorld(
                     val.world()
                  );
            }

            val.handler()
               .sendPacket(
                  new Full(
                     val.player()
                        .getX(),
                     val.player()
                        .getY(),
                     val.player()
                        .getZ(),
                     val.player()
                        .getYaw(),
                     val.player()
                        .getPitch(),
                     val.player()
                        .isOnGround(),
                     val.player()
                        .horizontalCollision
                  )
               );
            val2.setScreen(
               null
            );
            return true;
         } catch (Exception var5) {
            return false;
         }
      }
   }

   private static void clearActiveSession(
      MinecraftClient val
   ) {
      IMinecraftClientAccessor val2 = (IMinecraftClientAccessor)val;
      setMinecraftClientField(
         val,
         ClientPlayNetworkHandler.class,
         null
      );
      val2.setItemUseCooldown(
         0
      );
      val.world = null;
      val.player = null;
      val.cameraEntity = null;
      val.interactionManager = null;
      if (val.worldRenderer
         != null) {
         val.worldRenderer
            .setWorld(null);
      }
   }

   private static void replaceConnection(
      NameHelper val
   ) {
      String var10000 = val.name();
      disconnectSessionsByName(var10000, Text.literal("Replaced"));
      connections.add(
         val
      );
   }

   private static void makeNettyBot(
      ClientPlayNetworkHandler hand,
      String str,
      ClientPlayerEntity player
   ) {
      Channel val = getChannel(
         hand.getConnection()
      );
      if (val
         != null) {
         ChannelPipeline var10000 = val.pipeline();
         if (var10000.get("bot_filter") != null) {
            var10000 = val.pipeline();
            var10000.remove("bot_filter");
         }

         var10000 = val.pipeline();
         if (var10000.get("packet_handler") != null) {
            var10000 = val.pipeline();
            String var10 = "packet_handler";
            var10000.addBefore(
               var10,
               "bot_filter",
               new ChannelDuplexHandler() {
                  public void channelRead(
                     ChannelHandlerContext hand2,
                     Object val2
                  ) throws Exception {
                     if (val2 instanceof KeepAliveS2CPacket val3
                        )
                      {
                        hand.getConnection()
                           .send(
                              new KeepAliveC2SPacket(
                                 val3.getId()
                              )
                           );
                        if (player
                           != null) {
                           hand.sendPacket(
                              new OnGroundOnly(
                                 player.isOnGround(),
                                 player.horizontalCollision
                              )
                           );
                        }

                        ReferenceCountUtil.release(
                           val2
                        );
                     } else if (val2 instanceof CommonPingS2CPacket val4
                        )
                      {
                        hand.getConnection()
                           .send(
                              new CommonPongC2SPacket(
                                 val4.getParameter()
                              )
                           );
                        ReferenceCountUtil.release(
                           val2
                        );
                     } else if (val2 instanceof ResourcePackSendS2CPacket val5
                        )
                      {
                        hand.sendPacket(
                           new ResourcePackStatusC2SPacket(
                              val5.id(),
                              Status.ACCEPTED
                           )
                        );
                        hand.sendPacket(
                           new ResourcePackStatusC2SPacket(
                              val5.id(),
                              Status.SUCCESSFULLY_LOADED
                           )
                        );
                        ReferenceCountUtil.release(
                           val2
                        );
                     } else if (val2 instanceof PlayerPositionLookS2CPacket val6
                        )
                      {
                        GetconnectionsHelper.applyFrozenPositionLook(
                           player,
                           val6
                        );
                        hand.sendPacket(
                           new TeleportConfirmC2SPacket(
                              val6.teleportId()
                           )
                        );
                        if (player
                           != null) {
                           hand.sendPacket(
                              new Full(
                                 player.getX(),
                                 player.getY(),
                                 player.getZ(),
                                 player.getYaw(),
                                 player.getPitch(),
                                 player.isOnGround(),
                                 player.horizontalCollision
                              )
                           );
                        }

                        ReferenceCountUtil.release(
                           val2
                        );
                     } else if (val2 instanceof EntityPositionSyncS2CPacket entity
                        )
                      {
                        GetconnectionsHelper.applyFrozenEntityPositionSync(
                           player,
                           entity
                        );
                        ReferenceCountUtil.release(
                           val2
                        );
                     } else if (val2 instanceof HealthUpdateS2CPacket val7
                        )
                      {
                        if (player
                           != null) {
                           player.setHealth(
                              val7.getHealth()
                           );
                        }

                        ReferenceCountUtil.release(
                           val2
                        );
                     } else if (val2 instanceof DisconnectS2CPacket
                        )
                      {
                        GetconnectionsHelper.connections
                           .removeIf(
                              val8 -> GetconnectionsHelper.matchesName(
                                    val8.name(),
                                    str
                                 )
                           );
                        hand2.close();
                        ReferenceCountUtil.release(
                           val2
                        );
                     } else {
                        String name2 = val2.getClass()
                           .getSimpleName();
                        if (!name2.contains(
                           "Sound"
                        )) {
                           if (!name2.contains(
                              "Particle"
                           )) {
                              if (!name2.contains(
                                    "Screen"
                                 )
                                 && (
                                    !GetconnectionsHelper.ignoreBotMessages
                                       || !GetconnectionsHelper.isBotMessagePacket(
                                          name2
                                       )
                                 )) {
                                 if (!name2.contains(
                                    "Explosion"
                                 )) {
                                    if (!name2.contains(
                                       "BossBar"
                                    )) {
                                       if (!name2.contains(
                                          "Scoreboard"
                                       )) {
                                          if (!name2.contains(
                                             "OverlayMessage"
                                          )) {
                                             super.channelRead(
                                                hand2,
                                                val2
                                             );
                                             return;
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }

                        ReferenceCountUtil.release(
                           val2
                        );
                     }
                  }

                  public void channelInactive(
                     ChannelHandlerContext hand3
                  ) throws Exception {
                     GetconnectionsHelper.connections
                        .removeIf(
                           val9 -> GetconnectionsHelper.matchesName(
                                 val9.name(),
                                 str
                              )
                        );
                     super.channelInactive(
                        hand3
                     );
                  }

                  public void exceptionCaught(
                     ChannelHandlerContext hand4,
                     Throwable val10
                  ) throws Exception {
                     GetconnectionsHelper.connections
                        .removeIf(
                           val11 -> GetconnectionsHelper.matchesName(
                                 val11.name(),
                                 str
                              )
                        );
                     hand4.close();
                  }
               }
            );
         }
      }
   }

   private static boolean isBotMessagePacket(
      String str
   ) {
      if (!str.contains(
         "Chat"
      )) {
         if (!str.contains(
            "Message"
         )) {
            if (!str.contains(
               "Title"
            )) {
               if (!str.contains(
                  "Overlay"
               )) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private static Channel getChannel(
      ClientConnection val
   ) {
      try {
         for (Field val2 : ClientConnection.class
            .getDeclaredFields()) {
            if (Channel.class
               .isAssignableFrom(
                  val2.getType()
               )) {
               val2.setAccessible(
                  true
               );
               return (Channel)val2.get(
                  val
               );
            }
         }
      } catch (Exception var5) {
      }

      return null;
   }

   private static void applyFrozenPositionLook(
      ClientPlayerEntity player,
      PlayerPositionLookS2CPacket val
   ) {
      if (player
            != null
         && val
            != null) {
         double x2 = readPacketDouble(
            val,
            "x",
            player.getX()
         );
         double x3 = readPacketDouble(
            val,
            "y",
            player.getY()
         );
         double x4 = readPacketDouble(
            val,
            "z",
            player.getZ()
         );
         float yaw2 = (float)readPacketDouble(
            val,
            "yaw",
            (double)player.getYaw()
         );
         float yaw3 = (float)readPacketDouble(
            val,
            "pitch",
            (double)player.getPitch()
         );
         Object val2 = readPacketComponent(
            val,
            "change"
         );
         if (val2
            == null) {
            val2 = readPacketComponent(
               val,
               "flags"
            );
         }

         if (hasRelativeFlag(
            val2,
            "X"
         )) {
            x2 += player.getX();
         }

         if (hasRelativeFlag(
            val2,
            "Y"
         )) {
            x3 += player.getY();
         }

         if (hasRelativeFlag(
            val2,
            "Z"
         )) {
            x4 += player.getZ();
         }

         if (hasRelativeFlag(
            val2,
            "Y_ROT"
         )) {
            yaw2 += player.getYaw();
         }

         if (hasRelativeFlag(
            val2,
            "X_ROT"
         )) {
            yaw3 += player.getPitch();
         }

         yaw3 = MathHelper.clamp(
            yaw3,
            -90.0F,
            90.0F
         );
         player.refreshPositionAndAngles(
            x2,
            x3,
            x4,
            yaw2,
            yaw3
         );
         player.setYaw(
            yaw2
         );
         player.setPitch(
            yaw3
         );
      }
   }

   private static void applyFrozenEntityPositionSync(
      ClientPlayerEntity player,
      EntityPositionSyncS2CPacket entity
   ) {
      if (player
            != null
         && entity
            != null
         && entity.id()
            == player.getId()
         && entity.values()
            != null) {
         Vec3d vec = entity.values()
            .position();
         if (vec
            != null) {
            float yaw2 = entity.values()
               .yaw();
            float yaw3 = MathHelper.clamp(
               entity.values()
                  .pitch(),
               -90.0F,
               90.0F
            );
            player.refreshPositionAndAngles(
               vec.x,
               vec.y,
               vec.z,
               yaw2,
               yaw3
            );
            player.setYaw(
               yaw2
            );
            player.setPitch(
               yaw3
            );
            if (entity.values()
                  .deltaMovement()
               != null) {
               player.setVelocity(
                  entity.values()
                     .deltaMovement()
               );
            }

            player.setOnGround(
               entity.onGround()
            );
         }
      }
   }

   private static double readPacketDouble(
      Object val,
      String str,
      double d
   ) {
      return readPacketComponent(
            val,
            str
         ) instanceof Number val2
         ? val2.doubleValue()
         : d;
   }

   private static Object readPacketComponent(
      Object val,
      String str
   ) {
      if (val
            != null
         && str
            != null
         && !str.isBlank()
         )
       {
         try {
            Method val2 = val.getClass()
               .getMethod(
                  str
               );
            val2.setAccessible(
               true
            );
            return val2.invoke(
               val
            );
         } catch (Exception var10) {
            try {
               Method val3 = val.getClass()
                  .getMethod(
                     "get"
                        + Character.toUpperCase(
                           str.charAt(
                              0
                           )
                        )
                        + str.substring(
                           1
                        )
                  );
               val3.setAccessible(
                  true
               );
               return val3.invoke(
                  val
               );
            } catch (Exception var9) {
               try {
                  RecordComponent[] val4 = val.getClass()
                     .getRecordComponents();
                  if (val4
                     != null) {
                     for (RecordComponent val5 : val4) {
                        if (str.equals(
                           val5.getName()
                        )) {
                           return val5.getAccessor()
                              .invoke(
                                 val
                              );
                        }
                     }
                  }
               } catch (Exception var8) {
               }

               try {
                  for (Field val6 : val.getClass()
                     .getDeclaredFields()) {
                     if (str.equalsIgnoreCase(
                        val6.getName()
                     )) {
                        val6.setAccessible(
                           true
                        );
                        return val6.get(
                           val
                        );
                     }
                  }
               } catch (Exception var7) {
               }

               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static boolean hasRelativeFlag(
      Object val,
      String str
   ) {
      if (val instanceof Iterable<?> val2
         && str
            != null) {
         for (Object val3 : val2) {
            if (val3 instanceof Enum<?> val4
               && str.equals(
                  val4.name()
               )) {
               return true;
            }
         }

         return false;
      }

      return false;
   }

   private static Session createSessionWithName(
      Session val,
      String str
   ) {
      try {
         Class[] var10001 = new Class[6];
         var10001[0] = String.class;
         var10001[1] = UUID.class;
         var10001[2] = String.class;
         var10001[3] = Optional.class;
         var10001[4] = Optional.class;
         var10001[5] = AccountType.class;
         Constructor val2 = Session.class
            .getDeclaredConstructor(var10001);
         val2.setAccessible(
            true
         );
         Object[] var4 = new Object[6];
         var4[0] = str;
         var4[1] = UUID.randomUUID();
         var4[2] = val
               == null
            ? ""
            : val.getAccessToken();
         var4[3] = Optional.empty();
         var4[4] = Optional.empty();
         var4[5] = AccountType.MOJANG;
         return (Session)val2.newInstance(
            var4
         );
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   private static void setMinecraftClientField(
      MinecraftClient val,
      Class<?> val2,
      Object val3
   ) {
      try {
         for (Field val4 : MinecraftClient.class
            .getDeclaredFields()) {
            if (val4.getType()
               == val2
               )
             {
               val4.setAccessible(
                  true
               );
               val4.set(
                  val,
                  val3
               );
               return;
            }
         }
      } catch (Exception var7) {
      }
   }

   private static ServerInfo createServerInfo(
      String str,
      String str2
   ) {
      String str3 = str2
            == null
         ? ""
         : str2;
      String str4 = str
               != null
            && !str.isBlank()
         ? str
         : str3;
      return new ServerInfo(
         str4,
         str3,
         ServerType.OTHER
      );
   }

   private static void restoreAfterConnectFailure(
      MinecraftClient val,
      NameHelper val2,
      Session val3,
      ServerInfo val4
   ) {
      try {
         bypassResourcePacksDuringBotConnect = false;
         if (val2
               != null
            && activateSession(
               val2
            )) {
            connections.remove(
               val2
            );
            return;
         }

         IMinecraftClientAccessor val5 = (IMinecraftClientAccessor)val;
         val5.setSession(
            val3
         );
         setMinecraftClientField(
            val,
            ServerInfo.class,
            val4
         );
      } catch (Exception var5) {
      }
   }

   private static int disconnectSessionsByName(
      String str,
      Text text
   ) {
      if (str
            != null
         && !str.isBlank()
         )
       {
         int i = 0;

         for (NameHelper val : new ArrayList<>(
            connections
         )) {
            if (matchesName(
               val.name(),
               str
            )) {
               connections.remove(
                  val
               );
               i++;

               try {
                  if (val.connection()
                     != null) {
                     val.connection()
                        .disconnect(
                           text
                        );
                  }
               } catch (Exception var6) {
               }
            }
         }

         return i;
      } else {
         return 0;
      }
   }

   private static void pruneDeadConnections() {
      connections.removeIf(
         val -> !isConnectionUsable(
               val
            )
      );
   }

   private static boolean isConnectionUsable(
      NameHelper val
   ) {
      if (val
            == null
         || val.name()
            == null
         || val.name()
            .isBlank()) {
         return false;
      } else if (val.connection()
            != null
         && val.handler()
            != null
         && val.world()
            != null
         && val.player()
            != null) {
         if (val.player()
               .networkHandler
            != val.handler()
            )
          {
            return false;
         } else {
            Channel val2 = getChannel(
               val.connection()
            );
            return val2
                  == null
               || val2.isOpen();
         }
      } else {
         return false;
      }
   }

   private static boolean matchesName(
      String str,
      String str2
   ) {
      return str
            != null
         && str2
            != null
         && str.equalsIgnoreCase(
            str2
         );
   }
}
