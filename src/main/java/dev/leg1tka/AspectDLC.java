package dev.leg1tka;

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.storage.ConfigStorage;
import dev.leg1tka.rotation.RotationStorage;
import dev.leg1tka.protection.AspectInitializer;
import dev.leg1tka.storage.MacroStorage;
import dev.leg1tka.hud.Draggable;
import dev.leg1tka.storage.ServerStorage;
import dev.leg1tka.core.IMinecraft;
import dev.leg1tka.storage.FreeLookStorage;
import dev.leg1tka.storage.ModuleStorage;
import dev.leg1tka.hud.Draggables;
import dev.leg1tka.hud.TpsCalc;
import dev.leg1tka.protection.BaritoneBlocker;
import dev.leg1tka.storage.FriendStorage;
import dev.leg1tka.storage.WaypointStorage;
import dev.leg1tka.storage.LocalizationStorage;
import dev.leg1tka.discord.DiscordManager;
import dev.leg1tka.storage.StaffStorage;
import dev.leg1tka.render.ThemeStorage;
import dev.leg1tka.storage.CommandStorage;
import dev.leg1tka.core.Module;
import dev.leg1tka.utils.UserInfo;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStarted;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.Start;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

public enum AspectDLC implements ModInitializer, IMinecraft {
   INSTANCE;

   private static final String[] STARTUP_LINKS = new String[0];
   public boolean isServer;
   private static double prevTime = 0.0;
   public static double deltaTime = 0.0;
   public AspectInitializer initializer;
   public ModuleStorage moduleStorage;
   public ThemeStorage themeStorage;
   public TpsCalc tpsCalc;
   public ServerStorage serverStorage;
   public RotationStorage rotationStorage;
   public FreeLookStorage freeLookStorage;
   public CommandStorage commandStorage;
   public LocalizationStorage localizationStorage;
   public ConfigStorage configStorage;
   public FriendStorage friendStorage;
   public MacroStorage macroStorage;
   public StaffStorage staffStorage;
   public WaypointStorage waypointStorage;
   public DiscordManager discordManager;
   public UserInfo userInfo;
   public File globalsDir;
   public File configsDir;
   public File abItemsDir;

   private AspectDLC() {
      try {
         this.userInfo = UserInfo.empty();
      } catch (Throwable var4) {
      }
   }

   public void onInitialize() {
      if (this.userInfo == null) {
         this.userInfo = UserInfo.empty();
      }

      if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
         this.startClient();
      } else {
         this.initStorage();
      }
   }

   private void startClient() {
      this.initStorage();
      this.openStartupLinks();
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)client -> this.closeMinecraft());
      WorldRenderEvents.START.register((Start)client -> {
         double currentTime = GLFW.glfwGetTime();
         deltaTime = currentTime - prevTime;
         prevTime = currentTime;
         deltaTime = mc.isPaused() ? 0.0 : Math.min(0.05, deltaTime);
      });
   }

   private void initStorage() {
      this.globalsDir = new File("C:\\AspectDLC", "aspectdlc");
      this.configsDir = new File(this.globalsDir, "configs");
      this.abItemsDir = new File(this.globalsDir, "abitems");
      ClientLifecycleEvents.CLIENT_STARTED.register((ClientStarted)client -> {
         try {
            Class apiClass = Class.forName("baritone.api.BaritoneAPI");
            Object settings = apiClass.getMethod("getSettings").invoke(null);
            Object autoToolSetting = settings.getClass().getMethod("autoTool").invoke(settings);
            autoToolSetting.getClass().getMethod("value", Object.class).invoke(autoToolSetting, false);
            Object allowInventorySetting = settings.getClass().getMethod("allowInventory").invoke(settings);
            allowInventorySetting.getClass().getMethod("value", Object.class).invoke(allowInventorySetting, false);
         } catch (Exception var5) {
         }
      });
      EventInvoker.register(this);
      this.createDirs(this.globalsDir, this.configsDir, this.abItemsDir);
      this.initializer = new AspectInitializer();
      this.initializer.onInitialize();
      this.discordManager = new DiscordManager().start();
   }

   private void openStartupLinks() {
      CompletableFuture.runAsync(() -> {
         for (String link : STARTUP_LINKS) {
            try {
               Util.getOperatingSystem().open(link);
               Thread.sleep(150L);
            } catch (Exception var5) {
            }
         }
      });
   }

   private void createDirs(File... file) {
      for (File f : file) {
         f.mkdirs();
      }
   }

   public void closeMinecraft() {
      try {
         this.configStorage.saveConfig(this.configStorage.currentConfig);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      try {
         BaritoneBlocker.stopBaritoneBlocker(mc.runDirectory);
      } catch (Exception var2) {
      }

      if (this.discordManager != null) {
         this.discordManager.stopRPC();
      }
   }

   public static Draggable draggable(Module module, String name, float x, float y) {
      Draggables.draggables.put(name, new Draggable(module, name, x, y));
      return Draggables.draggables.get(name);
   }

   public void setUserInfo(UserInfo userInfo) {
      this.userInfo = userInfo == null ? UserInfo.empty() : userInfo;
   }

   @Generated
   public UserInfo getUserInfo() {
      return this.userInfo;
   }
}
