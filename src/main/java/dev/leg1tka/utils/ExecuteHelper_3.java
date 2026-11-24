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


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.leg1tka.AspectDLC;
import java.io.File;
import java.util.Arrays;
import net.minecraft.command.CommandSource;

public class ExecuteHelper_3 extends ExecuteHelper_8 {
   public ExecuteHelper_3() {
      super("config");
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      LiteralArgumentBuilder var10001 = this.literal("save");
      LiteralArgumentBuilder var10000 = (LiteralArgumentBuilder)val.then(
         var10001.then(
            this.arg("config", StringArgumentType.word())
               .suggests(
                  (val2, val3) -> {
                     if (AspectDLC.INSTANCE.configsDir.exists() && AspectDLC.INSTANCE.configsDir.isDirectory()) {
                        File[] val4 = AspectDLC.INSTANCE
                           .configsDir
                           .listFiles(
                              (val5, val6) -> {
                                 String var10000x = val6.toLowerCase();
                                 return var10000x.endsWith(".aspect");
                              }
                           );
                        if (val4
                           != null) {
                           Arrays.stream(
                                 val4
                              )
                              .map(File::getName)
                              .map(
                                 val7 -> {
                                    return val7.replace(
                                       ".aspect", ""
                                    );
                                 }
                              )
                              .forEach(
                                 val3::suggest
                              );
                        }
                     }

                     return val3.buildFuture();
                  }
               )
               .executes(
                  val8 -> {
                     String name = (String)val8.getArgument(
                        "config", String.class
                     );

                     try {
                        AspectDLC.INSTANCE
                           .configStorage
                           .saveConfig(
                              name
                           );
                        ChatUtil.sendMessage(
                           "Конфиг "
                              + name
                              + " успешно сохранён!"
                        );
                     } catch (Exception var3x) {
                        ChatUtil.sendMessage(
                           "Ошибка при сохранении конфига "
                              + name
                              + "!"
                        );
                        var3x.printStackTrace();
                     }

                     return 1;
                  }
               )
         )
      );
      var10001 = this.literal("load");
      var10000 = (LiteralArgumentBuilder)var10000.then(
         var10001.then(
            this.arg("config", StringArgumentType.word())
               .suggests(
                  (val9, val10) -> {
                     if (AspectDLC.INSTANCE.configsDir.exists() && AspectDLC.INSTANCE.configsDir.isDirectory()) {
                        File[] val11 = AspectDLC.INSTANCE
                           .configsDir
                           .listFiles(
                              (val12, val13) -> {
                                 String var10000x = val13.toLowerCase();
                                 return var10000x.endsWith(".aspect");
                              }
                           );
                        if (val11
                           != null) {
                           Arrays.stream(
                                 val11
                              )
                              .map(File::getName)
                              .map(
                                 val14 -> {
                                    return val14.replace(
                                       ".aspect", ""
                                    );
                                 }
                              )
                              .forEach(
                                 val10::suggest
                              );
                        }
                     }

                     return val10.buildFuture();
                  }
               )
               .executes(
                  val15 -> {
                     String name2 = (String)val15.getArgument(
                        "config", String.class
                     );

                     try {
                        AspectDLC.INSTANCE
                           .configStorage
                           .loadConfig(
                              name2
                           );
                        ChatUtil.sendMessage(
                           "Конфиг "
                              + name2
                              + " успешно загружен!"
                        );
                     } catch (Exception var3x) {
                        ChatUtil.sendMessage(
                           "Ошибка при загрузке конфига "
                              + name2
                              + "!"
                        );
                        var3x.printStackTrace();
                     }

                     return 1;
                  }
               )
         )
      );
      var10000 = (LiteralArgumentBuilder)var10000.then(
         this.literal("list")
            .executes(
               val16 -> {
                  File[] val17 = AspectDLC.INSTANCE
                     .configsDir
                     .listFiles(
                        (val18, val19) -> {
                           String var10000xx = val19.toLowerCase();
                           return var10000xx.endsWith(".aspect");
                        }
                     );
                  if (val17
                        != null
                     && val17.length
                        != 0) {
                     StringBuilder val20 = new StringBuilder();

                     for (int i = 0;
                        i
                           < val17.length;
                        i++
                     ) {
                        String var5x = val17[i]
                           .getName();
                        String name3 = var5x.replace(
                           ".aspect", ""
                        );
                        val20.append(
                           name3
                        );
                        if (i
                           < val17.length
                              - 1) {
                           val20.append(
                              ", "
                           );
                        }
                     }

                     ChatUtil.sendMessage(
                        "Конфиги: "
                           + val20
                     );
                  } else {
                     ChatUtil.sendMessage("Список конфигов пуст!");
                  }

                  return 1;
               }
            )
      );
      var10000.then(
         this.literal("dir")
            .executes(
               val21 -> {
                  try {
                     File var10002x = AspectDLC.INSTANCE.globalsDir;
                     File val22 = new File(
                        var10002x, "configs"
                     );
                     if (!val22.exists()
                        )
                      {
                        val22.mkdirs();
                     }

                     new ProcessBuilder(new String[]{"explorer.exe", val22.getAbsolutePath()}).start();
                     ChatUtil.sendMessage("Папка с конфигами открыта!");
                  } catch (Exception var2x) {
                     ChatUtil.sendMessage("Ошибка при открытии папки с конфигами!");
                     var2x.printStackTrace();
                  }

                  return 1;
               }
            )
      );
   }
}
