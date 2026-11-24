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

import dev.leg1tka.AspectDLC;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.lwjgl.glfw.GLFW;

public class Unhook extends Module {
   public static Unhook INSTANCE = new Unhook();
   private final KeybindSetting unhookKey;
   private final BooleanSetting allowBaritone;
   private final BooleanSetting clearGPS;
   private final BooleanSetting clearLogs;
   private final BooleanSetting clearBaritone;
   private final BooleanSetting clearRecent;
   private final BooleanSetting blockBaritone;
   private final BooleanSetting deepClean;
   private static boolean isUnhooked = false;
   private static final Map<Module, Boolean> savedStates = new HashMap<>();
   private static boolean clickGuiWasEnabled = false;
   private static long lastKeyPressTime = 0L;
   private static final long KEY_PRESS_COOLDOWN = 200L;

   public Unhook() {
      super("Unhook", Category.MISC);
      String var1 = "Unhook";
      this.unhookKey = new KeybindSetting(
         "Unhook Key",
         -1
      );
      this.allowBaritone = new BooleanSetting("Allow Baritone Commands", true);
      this.clearGPS = new BooleanSetting("Очистить GPS", true);
      this.clearLogs = new BooleanSetting("Очистить логи", true);
      this.clearBaritone = new BooleanSetting(
         "Удалить Baritone папку", true
      );
      this.clearRecent = new BooleanSetting(
         "Очистить Recent файлы", true
      );
      this.blockBaritone = new BooleanSetting(
         "Блокировать Baritone папку", true
      );
      this.deepClean = new BooleanSetting("Глубокая очистка следов", true);
      this.addSettings(new Setting[]{
         this.unhookKey,
         this.allowBaritone,
         this.clearGPS,
         this.clearLogs,
         this.clearBaritone,
         this.clearRecent,
         this.blockBaritone,
         this.deepClean
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.blockBaritone.isState()) {
         BaritoneBlocker.startBaritoneBlocker(mc.runDirectory);
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      BaritoneBlocker.stopBaritoneBlocker(mc.runDirectory);
      if (isUnhooked) {
         restoreClient();
      }
   }

   public static boolean isBaritoneAllowed() {
      return INSTANCE != null && INSTANCE.allowBaritone.isState();
   }

   public static void checkUnhookKey() {
      if (INSTANCE != null && INSTANCE.isEnable()) {
         int n = INSTANCE.unhookKey
            .getKey();
         if (n
            != -1) {
            long val = System.currentTimeMillis();
            if (val
                  - lastKeyPressTime
               >= 200L) {
               if (isKeyPressed(
                  n
               )) {
                  lastKeyPressTime = val;
                  toggleUnhook();
               }
            }
         }
      }
   }

   private static void toggleUnhook() {
      if (isUnhooked) {
         restoreClient();
      } else {
         hideClient();
      }
   }

   private static void hideClient() {
      if (!isUnhooked) {
         isUnhooked = true;
         savedStates.clear();
         if (INSTANCE.clearGPS.isState()) {
            clearGPSWaypoints();
         }

         if (INSTANCE.clearLogs.isState()) {
            clearClientLogs();
         }

         if (INSTANCE.clearBaritone.isState()) {
            deleteBaritoneFolder();
         }

         if (INSTANCE.clearRecent.isState()) {
            clearWindowsRecent();
         }

         if (INSTANCE.deepClean.isState()) {
            BaritoneBlocker.performDeepClean();
         }

         ObjectListIterator var0 = ModuleManager.INSTANCE.getObject().iterator();

         while (var0.hasNext()) {
            Module val = (Module)var0.next();
            if (val
               != INSTANCE) {
               savedStates.put(
                  val,
                  val.isEnable()
               );
               if (val.isEnable()
                  )
                {
                  val.setEnabled(
                     false
                  );
               }
            }
         }

         if (mc != null && mc.currentScreen != null) {
            String str = mc.currentScreen
               .getClass()
               .getSimpleName();
            clickGuiWasEnabled = str.contains(
               "ClickGui"
            );
            if (clickGuiWasEnabled) {
               mc.setScreen(null);
            }
         }

         clearChat();
      }
   }

   private static void restoreClient() {
      if (isUnhooked) {
         isUnhooked = false;

         for (Entry<Module, Boolean> val : savedStates.entrySet()) {
            Module val2 = val.getKey();
            boolean flag = val.getValue();
            if (flag
               && !val2.isEnable()
               )
             {
               val2.setEnabled(
                  true
               );
            }
         }

         savedStates.clear();
      }
   }

   public static boolean isUnhooked() {
      return isUnhooked;
   }

   private static void clearChat() {
      try {
         if (mc != null && mc.inGameHud != null && mc.inGameHud.getChatHud() != null) {
            mc.inGameHud.getChatHud().clear(false);
         }
      } catch (Exception var1) {
      }
   }

   private static void clearClientLogs() {
      try {
         File var10002 = mc.runDirectory;
         File val = new File(
            var10002, "logs"
         );
         if (val.exists()
            && val.isDirectory()
            )
          {
            BaritoneBlocker.deleteDirectory(
               val.toPath()
            );
            val.mkdirs();
         }

         File val2 = new File(
            System.getProperty("java.io.tmpdir")
         );
         if (val2.exists()
            )
          {
            File[] val3 = val2.listFiles(
               (val4, val5) -> {
                  if (!val5.toLowerCase()
                     .contains("minecraft")) {
                     String var10000 = val5.toLowerCase();
                     if (!var10000.contains(
                        "fabric"
                     )) {
                        var10000 = val5.toLowerCase();
                        if (!var10000.contains(
                           "java"
                        )) {
                           return false;
                        }
                     }
                  }

                  return true;
               }
            );
            if (val3
               != null) {
               for (File val6 : val3) {
                  try {
                     if (val6.isFile()
                        )
                      {
                        val6.delete();
                     }
                  } catch (Exception var8) {
                  }
               }
            }
         }

         System.gc();
      } catch (Exception var9) {
      }
   }

   private static void clearGPSWaypoints() {
      try {
         if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.commandStorage != null) {
            String str = AspectDLC.INSTANCE
               .commandStorage
               .getPrefix();
            mc.player
               .networkHandler
               .sendChatMessage(
                  str
                     + "gps remove"
               );
         }

         File var10002 = mc.runDirectory;
         File val = new File(
            var10002, "waypoints.json"
         );
         if (val.exists()
            )
          {
            val.delete();
         }

         var10002 = mc.runDirectory;
         File val2 = new File(
            var10002, "gps.dat"
         );
         if (val2.exists()
            )
          {
            val2.delete();
         }
      } catch (Exception var2) {
      }
   }

   private static void deleteBaritoneFolder() {
      try {
         File var10002 = mc.runDirectory;
         File val = new File(
            var10002, "baritone"
         );
         if (val.exists()
            && val.isDirectory()
            )
          {
            BaritoneBlocker.deleteDirectory(
               val.toPath()
            );
         }
      } catch (Exception var1) {
      }
   }

   private static void clearWindowsRecent() {
      try {
         String str = System.getProperty(
               "os.name"
            )
            .toLowerCase();
         if (str.contains(
            "win"
         )) {
            String str2 = System.getProperty(
               "user.home"
            );
            String[] var8 = new String[5];
            var8[0] = "AppData";
            var8[1] = "Roaming";
            int var10003 = 2;
            var8[var10003] = "Microsoft";
            var10003 = 3;
            var8[var10003] = "Windows";
            var10003 = 4;
            var8[var10003] = "Recent";
            Path val = Paths.get(
               str2,
               var8
            );
            if (Files.exists(
               val
            )) {
               Files.walk(
                     val
                  )
                  .filter(
                     val2 -> Files.isRegularFile(
                           val2
                        )
                  )
                  .forEach(
                     val3 -> {
                        try {
                           Files.delete(
                              val3
                           );
                        } catch (IOException var2x) {
                        }
                     }
                  );
            }

            try {
               Path val4 = Paths.get(
                  "C:\\Windows\\Prefetch"
               );
               if (Files.exists(
                  val4
               )) {
                  Files.walk(
                        val4
                     )
                     .filter(
                        val5 -> Files.isRegularFile(
                              val5
                           )
                     )
                     .filter(
                        val6 -> {
                           String var10000x = val6.toString()
                              .toLowerCase();
                           return var10000x.contains(
                                 "java"
                              )
                              || val6.toString()
                                 .toLowerCase()
                                 .contains("minecraft");
                        }
                     )
                     .forEach(
                        val7 -> {
                           try {
                              Files.delete(
                                 val7
                              );
                           } catch (IOException var2x) {
                           }
                        }
                     );
               }
            } catch (Exception var4) {
            }
         }
      } catch (Exception var5) {
      }
   }

   private static void deleteDirectory(
      File val
   ) {
      BaritoneBlocker.deleteDirectory(
         val.toPath()
      );
   }

   private static boolean isKeyPressed(
      int val
   ) {
      if (mc != null && mc.getWindow() != null) {
         try {
            if (val
               >= 1000) {
               int n = val
                  - 1000;
               return GLFW.glfwGetMouseButton(
                     mc.getWindow().getHandle(),
                     n
                  )
                  == 1;
            } else {
               return GLFW.glfwGetKey(
                     mc.getWindow().getHandle(),
                     val
                  )
                  == 1;
            }
         } catch (Exception var2) {
            return false;
         }
      } else {
         return false;
      }
   }
}
