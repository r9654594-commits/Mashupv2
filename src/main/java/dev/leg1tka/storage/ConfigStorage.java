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



import dev.leg1tka.modules.render.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.leg1tka.AspectDLC;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigStorage {
   public String currentConfig = "default";
   private final String extension = ".aspect";

   public ConfigStorage() {
      this.loadAll();
      Runtime.getRuntime().addShutdownHook(new Thread(this::saveAll));
   }

   private void loadAll() {
      try {
         this.loadGlobals();
         this.loadConfig(this.currentConfig);
      } catch (Exception var2) {
         var2.printStackTrace(System.err);
      }
   }

   private void saveAll() {
      try {
         this.saveGlobals();
         this.saveConfig(this.currentConfig);
      } catch (Exception var2) {
         var2.printStackTrace(System.err);
      }
   }

   public void saveConfig(
      String name2
   ) throws Exception {
      File val = new File(
         AspectDLC.INSTANCE.configsDir,
         name2
            + ".aspect"
      );
      JsonObject val2 = new JsonObject();
      val2.add(
         "config",
         new JsonPrimitive(
            name2
         )
      );
      val2.add(
         "theme", new JsonPrimitive(AspectDLC.INSTANCE.themeStorage.getThemes().name())
      );
      val2.add(
         "language", new JsonPrimitive(AspectDLC.INSTANCE.localizationStorage.getLanguage().name())
      );
      val2.add(
         "modules", this.serializeModules()
      );
      val2.add(
         "draggables", this.serializeDraggables()
      );
      val2.add(
         "hud", this.serializeHudState()
      );

      try (Writer val3 = new OutputStreamWriter(
            new FileOutputStream(
               val,
               false
            ),
            StandardCharsets.UTF_8
         )) {
         val3.write(
            new GsonBuilder()
               .setPrettyPrinting()
               .create()
               .toJson(
                  val2
               )
         );
      }

      this.currentConfig = name2;
   }

   public void loadConfig(
      String name2
   ) throws Exception {
      if (ResetHelper_14.exists(
         AspectDLC.INSTANCE.configsDir
            + "/"
            + name2
            + ".aspect"
      )) {
         JsonObject val;
         try (
            InputStream val2 = Files.newInputStream(
               Paths.get(
                  AspectDLC.INSTANCE.configsDir
                     + "/"
                     + name2
                     + ".aspect"
               )
            );
            Reader val3 = new InputStreamReader(
               val2,
               StandardCharsets.UTF_8
            );
         ) {
            val = JsonParser.parseReader(
                  val3
               )
               .getAsJsonObject();
         }

         if (val.has(
            "theme"
         )) {
            String name3 = val.get(
                  "theme"
               )
               .getAsString();

            for (Theme val4 : Theme.values()) {
               if (val4.name()
                  .equals(
                     name3
                  )) {
                  AspectDLC.INSTANCE
                     .themeStorage
                     .setThemes(
                        val4
                     );
                  break;
               }
            }
         }

         if (val.has(
            "language"
         )) {
            try {
               AspectDLC.INSTANCE
                  .localizationStorage
                  .setLanguage(
                     DisplaynameType_1.valueOf(
                        val.get(
                              "language"
                           )
                           .getAsString()
                     )
                  );
            } catch (Exception var10) {
            }
         }

         if (val.has(
            "draggables"
         )) {
            this.deserializeDraggables(
               val.get(
                     "draggables"
                  )
                  .getAsJsonObject()
            );
         }

         if (val.has(
            "modules"
         )) {
            this.deserializeModules(
               val.get(
                     "modules"
                  )
                  .getAsJsonObject()
            );
         }

         if (val.has(
            "hud"
         )) {
            this.deserializeHudState(
               val.get(
                     "hud"
                  )
                  .getAsJsonObject()
            );
         }

         this.currentConfig = name2;
      }
   }

   public void saveGlobals() throws Exception {
      File val = new File(
         AspectDLC.INSTANCE.globalsDir, "globals.aspect"
      );
      JsonObject val2 = new JsonObject();
      val2.add(
         "config", new JsonPrimitive(this.currentConfig)
      );
      val2.add(
         "theme", new JsonPrimitive(AspectDLC.INSTANCE.themeStorage.getThemes().name())
      );
      val2.add(
         "language", new JsonPrimitive(AspectDLC.INSTANCE.localizationStorage.getLanguage().name())
      );
      val2.add(
         "draggables", this.serializeDraggables()
      );
      val2.add(
         "hud", this.serializeHudState()
      );
      JsonArray val3 = new JsonArray();
      AspectDLC.INSTANCE
         .friendStorage
         .getFriends()
         .forEach(
            val3::add
         );
      val2.add(
         "friends",
         val3
      );
      JsonArray val4 = new JsonArray();
      AspectDLC.INSTANCE
         .staffStorage
         .getStaffs()
         .forEach(
            val4::add
         );
      val2.add(
         "staffs",
         val4
      );
      JsonArray val5 = new JsonArray();
      AspectDLC.INSTANCE
         .macroStorage
         .getMacros()
         .forEach(
            val6 -> {
               JsonObject val7 = new JsonObject();
               val7.addProperty(
                  "name",
                  val6.getName()
               );
               val7.addProperty(
                  "command",
                  val6.getCommand()
               );
               val7.addProperty(
                  "key",
                  val6.getBind()
                     .getKey()
               );
               val5.add(
                  val7
               );
            }
         );
      val2.add(
         "macros",
         val5
      );

      try (Writer val8 = new OutputStreamWriter(
            new FileOutputStream(
               val,
               false
            ),
            StandardCharsets.UTF_8
         )) {
         val8.write(
            new GsonBuilder()
               .setPrettyPrinting()
               .create()
               .toJson(
                  val2
               )
         );
      }
   }

   public void loadGlobals() throws Exception {
      if (ResetHelper_14.exists(AspectDLC.INSTANCE.globalsDir + "/globals.aspect")) {
         JsonObject val;
         try (
            InputStream val2 = Files.newInputStream(
               Paths.get(AspectDLC.INSTANCE.globalsDir + "/globals.aspect")
            );
            Reader val3 = new InputStreamReader(
               val2,
               StandardCharsets.UTF_8
            );
         ) {
            val = JsonParser.parseReader(
                  val3
               )
               .getAsJsonObject();
         }

         if (val.has(
            "config"
         )) {
            this.currentConfig = val.get(
                  "config"
               )
               .getAsString();
         }

         if (val.has(
            "theme"
         )) {
            String name2 = val.get(
                  "theme"
               )
               .getAsString();

            for (Theme val4 : Theme.values()) {
               if (val4.name()
                  .equals(
                     name2
                  )) {
                  AspectDLC.INSTANCE
                     .themeStorage
                     .setThemes(
                        val4
                     );
                  break;
               }
            }
         }

         if (val.has(
            "language"
         )) {
            try {
               AspectDLC.INSTANCE
                  .localizationStorage
                  .setLanguage(
                     DisplaynameType_1.valueOf(
                        val.get(
                              "language"
                           )
                           .getAsString()
                     )
                  );
            } catch (Exception var10) {
            }
         }

         if (val.has(
            "draggables"
         )) {
            this.deserializeDraggables(
               val.get(
                     "draggables"
                  )
                  .getAsJsonObject()
            );
         }

         if (val.has(
            "hud"
         )) {
            this.deserializeHudState(
               val.get(
                     "hud"
                  )
                  .getAsJsonObject()
            );
         }

         if (val.has(
            "friends"
         )) {
            for (JsonElement val5 : val.get(
                  "friends"
               )
               .getAsJsonArray()) {
               if (!AspectDLC.INSTANCE
                  .friendStorage
                  .isFriend(
                     val5.getAsString()
                  )) {
                  AspectDLC.INSTANCE
                     .friendStorage
                     .add(
                        val5.getAsString()
                     );
               }
            }
         }

         if (val.has(
            "staffs"
         )) {
            for (JsonElement val6 : val.get(
                  "staffs"
               )
               .getAsJsonArray()) {
               if (!AspectDLC.INSTANCE
                  .staffStorage
                  .isStaff(
                     val6.getAsString()
                  )) {
                  AspectDLC.INSTANCE
                     .staffStorage
                     .add(
                        val6.getAsString()
                     );
               }
            }
         }

         if (val.has(
            "macros"
         )) {
            for (JsonElement val7 : val.get(
                  "macros"
               )
               .getAsJsonArray()) {
               try {
                  String name3;
                  String name4;
                  int i;
                  if (val7.isJsonObject()
                     )
                   {
                     JsonObject val8 = val7.getAsJsonObject();
                     name3 = val8.has(
                           "name"
                        )
                        ? val8.get(
                              "name"
                           )
                           .getAsString()
                        : "";
                     name4 = val8.has(
                           "command"
                        )
                        ? val8.get(
                              "command"
                           )
                           .getAsString()
                        : "";
                     i = val8.has(
                           "key"
                        )
                        ? val8.get(
                              "key"
                           )
                           .getAsInt()
                        : -1;
                  } else {
                     String[] val9 = val7.getAsString()
                        .split(":", 3);
                     if (val9.length
                        < 3) {
                        continue;
                     }

                     name3 = val9[0];
                     name4 = val9[1];
                     i = Integer.parseInt(
                        val9[2]
                     );
                  }

                  if (!name3.isBlank()
                     && AspectDLC.INSTANCE
                           .macroStorage
                           .getMacro(
                              name3
                           )
                        == null) {
                     AspectDLC.INSTANCE
                        .macroStorage
                        .add(
                           new GetnameHelper(
                              name3,
                              name4,
                              new KeybindSetting(
                                 "bind",
                                 i
                              )
                           )
                        );
                  }
               } catch (Exception var13) {
               }
            }
         }
      }
   }

   private JsonObject serializeModules() {
      JsonObject val = new JsonObject();
      ObjectListIterator var2 = ModuleManager.INSTANCE.getObject().iterator();

      while (var2.hasNext()) {
         Module val2 = (Module)var2.next();

         try {
            JsonObject val3 = new JsonObject();
            val3.add(
               "toggled",
               new JsonPrimitive(
                  val2.isEnable()
               )
            );
            val3.add(
               "bind",
               new JsonPrimitive(
                  val2.getKey()
               )
            );
            JsonObject val4 = new JsonObject();

            for (Setting val5 : val2.getSettings()) {
               try {
                  if (val5 instanceof BooleanSetting val6
                     )
                   {
                     val4.add(
                        val5.name(),
                        new JsonPrimitive(
                           val6.isState()
                        )
                     );
                  } else if (val5 instanceof NumberSetting val7
                     )
                   {
                     val4.add(
                        val5.name(),
                        new JsonPrimitive(
                           val7.getValue()
                              .floatValue()
                        )
                     );
                  } else if (val5 instanceof ModeSetting val8
                     )
                   {
                     val4.add(
                        val5.name(),
                        new JsonPrimitive(
                           val8.getCurrent()
                        )
                     );
                  } else if (val5 instanceof StringSetting val9
                     )
                   {
                     val4.add(
                        val5.name(),
                        new JsonPrimitive(
                           val9.get()
                        )
                     );
                  } else if (val5 instanceof KeybindSetting val10
                     )
                   {
                     val4.add(
                        val5.name(),
                        new JsonPrimitive(
                           val10.getKey()
                        )
                     );
                  } else if (val5 instanceof ListSetting val11
                     )
                   {
                     if (val11.getValue()
                        != null) {
                        val4.add(
                           val5.name(),
                           new JsonPrimitive(
                              val11.getValue()
                                 .name()
                           )
                        );
                     }
                  } else if (val5 instanceof MultiBooleanSetting val12
                     )
                   {
                     JsonObject val13 = new JsonObject();

                     for (BooleanSetting val14 : val12.getSettings()) {
                        val13.add(
                           val14.name(),
                           new JsonPrimitive(
                              val14.isState()
                           )
                        );
                     }

                     val4.add(
                        val12.name(),
                        val13
                     );
                  }
               } catch (Exception var18) {
               }
            }

            val3.add(
               "settings",
               val4
            );
            val.add(
               val2.getName(),
               val3
            );
         } catch (Exception var19) {
         }
      }

      return val;
   }

   private void deserializeModules(
      JsonObject val
   ) {
      Map<Module, Boolean> val2 = new LinkedHashMap<>();
      ObjectListIterator var3 = ModuleManager.INSTANCE.getObject().iterator();

      while (var3.hasNext()) {
         Module val3 = (Module)var3.next();

         try {
            JsonObject val4 = val.has(
                  val3.getName()
               )
               ? val.get(
                     val3.getName()
                  )
                  .getAsJsonObject()
               : null;
            boolean flag = val4
                  != null
               && val4.has(
                  "toggled"
               )
               && val4.get(
                     "toggled"
                  )
                  .getAsBoolean();
            val2.put(
               val3,
               flag
            );
            if (val3.isEnable()
               )
             {
               val3.setEnabled(
                  false
               );
            }
         } catch (Exception var25) {
            val2.put(
               val3,
               false
            );
         }
      }

      var3 = ModuleManager.INSTANCE.getObject().iterator();

      while (var3.hasNext()) {
         Module val5 = (Module)var3.next();

         try {
            if (val.has(
               val5.getName()
            )) {
               JsonObject val6 = val.get(
                     val5.getName()
                  )
                  .getAsJsonObject();
               if (val6.has(
                  "bind"
               )) {
                  val5.setKey(
                     val6.get(
                           "bind"
                        )
                        .getAsInt()
                  );
               }

               if (val6.has(
                  "settings"
               )) {
                  JsonObject val7 = val6.get(
                        "settings"
                     )
                     .getAsJsonObject();

                  for (Setting val8 : val5.getSettings()) {
                     try {
                        if (val7.has(
                           val8.name()
                        )) {
                           JsonElement val9 = val7.get(
                              val8.name()
                           );
                           if (val8 instanceof BooleanSetting val10
                              )
                            {
                              val10.setState(
                                 val9.getAsBoolean()
                              );
                           } else if (val8 instanceof NumberSetting val11
                              )
                            {
                              val11.setValue(
                                 val9.getAsFloat()
                              );
                           } else if (val8 instanceof ModeSetting val12
                              )
                            {
                              val12.set(
                                 val9.getAsString()
                              );
                           } else if (val8 instanceof StringSetting val13
                              )
                            {
                              val13.setText(
                                 val9.getAsString()
                              );
                           } else if (val8 instanceof KeybindSetting val14
                              )
                            {
                              val14.setKey(
                                 val9.getAsInt()
                              );
                           } else if (val8 instanceof ListSetting val15
                              )
                            {
                              String name2 = val9.getAsString();

                              for (Theme val16 : Theme.values()) {
                                 if (val16.name()
                                    .equalsIgnoreCase(
                                       name2
                                    )) {
                                    val15.setValue(
                                       val16
                                    );
                                    break;
                                 }
                              }
                           } else if (val8 instanceof MultiBooleanSetting val17
                              )
                            {
                              JsonObject val18 = val9.getAsJsonObject();

                              for (BooleanSetting val19 : val17.getSettings()) {
                                 if (val18.has(
                                    val19.name()
                                 )) {
                                    val19.setState(
                                       val18.get(
                                             val19.name()
                                          )
                                          .getAsBoolean()
                                    );
                                 }
                              }
                           }
                        }
                     } catch (Exception var23) {
                     }
                  }
               }
            }
         } catch (Exception var24) {
         }
      }

      for (Entry<Module, Boolean> val20 : val2.entrySet()) {
         try {
            val20.getKey()
               .setEnabled(
                  val20.getValue()
               );
         } catch (Exception var22) {
         }
      }
   }

   private JsonObject serializeHudState() {
      JsonObject val = new JsonObject();
      InterfaceModule val2 = ModuleManager.interfaceModule;
      if (val2
         == null) {
         return val;
      } else {
         for (Entry<String, HudElement> val3 : val2.getConfigurableHudElements()
            .entrySet()) {
            HudElement val4 = val3.getValue();
            if (val4
               != null) {
               JsonObject val5 = new JsonObject();
               val5.add(
                  "hudRectStyle",
                  new JsonPrimitive(
                     val4.getHudRectStyle()
                        .name()
                  )
               );
               if (val4 instanceof InfoHud val6
                  )
                {
                  val5.add(
                     "showFps",
                     new JsonPrimitive(
                        val6.isShowFps()
                     )
                  );
                  val5.add(
                     "showMs",
                     new JsonPrimitive(
                        val6.isShowMs()
                     )
                  );
                  val5.add(
                     "showServer",
                     new JsonPrimitive(
                        val6.isShowServer()
                     )
                  );
                  val5.add(
                     "showTps",
                     new JsonPrimitive(
                        val6.isShowTps()
                     )
                  );
               } else if (val4 instanceof TargetHud val7
                  )
                {
                  val5.add(
                     "headParticlesEnabled",
                     new JsonPrimitive(
                        val7.isHeadParticlesEnabled()
                     )
                  );
               }

               val.add(
                  val3.getKey(),
                  val5
               );
            }
         }

         return val;
      }
   }

   private void deserializeHudState(
      JsonObject val
   ) {
      InterfaceModule val2 = ModuleManager.interfaceModule;
      if (val2
         != null) {
         for (Entry<String, HudElement> val3 : val2.getConfigurableHudElements()
            .entrySet()) {
            if (val.has(
               val3.getKey()
            )) {
               try {
                  JsonObject val4 = val.get(
                        val3.getKey()
                     )
                     .getAsJsonObject();
                  HudElement val5 = val3.getValue();
                  if (val4.has(
                     "hudRectStyle"
                  )) {
                     try {
                        val5.setHudRectStyle(
                           HudRectStyle.valueOf(
                              val4.get(
                                    "hudRectStyle"
                                 )
                                 .getAsString()
                           )
                        );
                     } catch (IllegalArgumentException var9) {
                     }
                  } else if (val4.has(
                     "unusualRectType"
                  )) {
                     val5.setHudRectStyle(
                        HudRectStyle.fromLegacyUnusual(
                           val4.get(
                                 "unusualRectType"
                              )
                              .getAsBoolean()
                        )
                     );
                  }

                  if (val5 instanceof InfoHud val6
                     )
                   {
                     if (val4.has(
                        "showFps"
                     )) {
                        val6.setShowFps(
                           val4.get(
                                 "showFps"
                              )
                              .getAsBoolean()
                        );
                     }

                     if (val4.has(
                        "showMs"
                     )) {
                        val6.setShowMs(
                           val4.get(
                                 "showMs"
                              )
                              .getAsBoolean()
                        );
                     }

                     if (val4.has(
                        "showServer"
                     )) {
                        val6.setShowServer(
                           val4.get(
                                 "showServer"
                              )
                              .getAsBoolean()
                        );
                     }

                     if (val4.has(
                        "showTps"
                     )) {
                        val6.setShowTps(
                           val4.get(
                                 "showTps"
                              )
                              .getAsBoolean()
                        );
                     }
                  } else if (val5 instanceof TargetHud val7
                     && val4.has(
                        "headParticlesEnabled"
                     )) {
                     val7.setHeadParticlesEnabled(
                        val4.get(
                              "headParticlesEnabled"
                           )
                           .getAsBoolean()
                     );
                  }
               } catch (Exception var10) {
               }
            }
         }
      }
   }

   private JsonObject serializeDraggables() {
      JsonObject val = new JsonObject();

      for (Draggable val2 : Draggables.draggables
         .values()) {
         JsonObject val3 = new JsonObject();
         val3.add(
            "x",
            new JsonPrimitive(
               val2.getX()
            )
         );
         val3.add(
            "y",
            new JsonPrimitive(
               val2.getY()
            )
         );
         val.add(
            val2.getName(),
            val3
         );
      }

      return val;
   }

   private void deserializeDraggables(
      JsonObject val
   ) {
      for (String str : val.keySet()) {
         Draggable val2 = Draggables.draggables
            .get(
               str
            );
         if (val2
            != null) {
            JsonObject val3 = val.get(
                  str
               )
               .getAsJsonObject();
            if (val3.has(
               "x"
            )) {
               val2.setX(
                  val3.get(
                        "x"
                     )
                     .getAsFloat()
               );
            }

            if (val3.has(
               "y"
            )) {
               val2.setY(
                  val3.get(
                        "y"
                     )
                     .getAsFloat()
               );
            }
         }
      }
   }
}
