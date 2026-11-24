package dev.leg1tka.discord;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import lombok.Generated;

public class DiscordManager implements IMinecraft {
   private RunHelper discordDaemonThread;
   public boolean running;
   public static NativeLargeimagekeyLargeimagetextStruct discordRichPresence = new NativeLargeimagekeyLargeimagetextStruct();
   public static IClientAction_GmJQLvIQ discordRPC = IClientAction_GmJQLvIQ.INSTANCE;

   private void cppInit() {
      this.discordDaemonThread = new RunHelper(this);
      this.running = true;
   }

   public void init() {
      this.cppInit();
      NativeDisconnectedJoinrequestStruct val = new BuildHelper()
         .build();
      IClientAction_GmJQLvIQ.INSTANCE
         .Discord_Initialize(
            ApplicationidHelper.applicationId(),
            val,
            true,
            ""
         );
      discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
      this.applyPresence();
      discordRPC.Discord_UpdatePresence(discordRichPresence);
      new Thread(() -> {
         while (this.running) {
            try {
               this.applyPresence();
               IClientAction_GmJQLvIQ.INSTANCE.Discord_UpdatePresence(discordRichPresence);
               Thread.sleep(2000L);
            } catch (InterruptedException var2) {
               Thread.currentThread().interrupt();
               return;
            }
         }
      }, "Discord-RPC-Updater").start();
      this.discordDaemonThread.start();
   }

   private void applyPresence() {
      String text = "Singleplayer";
      if (mc != null && mc.getCurrentServerEntry() != null) {
         text = mc.getCurrentServerEntry()
            .address;
      }

      discordRichPresence.details = "Играет на: "
         + text;
      discordRichPresence.state = "User | Build: Recode";
      discordRichPresence.largeImageKey = "logo";
      discordRichPresence.largeImageText = "AspectDLC";
      discordRichPresence.button_label_2 = "Телеграмм";
      discordRichPresence.button_url_2 = "https://t.me/aspectbeta";
   }

   public DiscordManager start() {
      this.init();
      return this;
   }

   public void stopRPC() {
      this.running = false;
      IClientAction_GmJQLvIQ.INSTANCE.Discord_Shutdown();
      if (this.discordDaemonThread != null) {
         this.discordDaemonThread.interrupt();
      }
   }

   @Generated
   public RunHelper getDiscordDaemonThread() {
      return this.discordDaemonThread;
   }

   @Generated
   public boolean isRunning() {
      return this.running;
   }
}
