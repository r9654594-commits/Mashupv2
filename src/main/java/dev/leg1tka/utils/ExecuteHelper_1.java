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
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.leg1tka.AspectDLC;
import java.lang.reflect.Field;
import net.minecraft.command.CommandSource;
import org.lwjgl.glfw.GLFW;

public class ExecuteHelper_1 extends ExecuteHelper_8 {
   public ExecuteHelper_1() {
      super("macro");
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      LiteralArgumentBuilder var10001 = this.literal(
         "add"
      );
      RequiredArgumentBuilder var5 = this.arg("name", StringArgumentType.word());
      RequiredArgumentBuilder var9 = this.arg("bind", StringArgumentType.word())
         .suggests(
            (val2, val3) -> {
               for (Field val4 : GLFW.class
                  .getDeclaredFields()) {
                  String name2 = val4.getName();
                  if (name2.startsWith(
                     "GLFW_KEY_"
                  )) {
                     String name3 = name2.replace(
                        "GLFW_KEY_", ""
                     );
                     if (name3.startsWith(
                        val3.getRemaining()
                     )) {
                        val3.suggest(
                           name3
                        );
                     }
                  }
               }

               if ("NONE"
                  .startsWith(
                     val3.getRemaining()
                        .toUpperCase()
                  )) {
                  val3.suggest(
                     "NONE"
                  );
               }

               return val3.buildFuture();
            }
         );
      LiteralArgumentBuilder var10000 = (LiteralArgumentBuilder)val.then(
         var10001.then(
            var5.then(
               var9.then(
                  this.arg(
                        "command",
                        StringArgumentType.greedyString()
                     )
                     .executes(
                        val5 -> {
                           String name4 = (String)val5.getArgument(
                              "name",
                              String.class
                           );
                           String name5 = ((String)val5.getArgument(
                                 "bind",
                                 String.class
                              ))
                              .toUpperCase();
                           String name6 = (String)val5.getArgument(
                              "command", String.class
                           );
                           if (AspectDLC.INSTANCE
                                 .macroStorage
                                 .getMacro(
                                    name4
                                 )
                              != null) {
                              ChatUtil.sendMessage(
                                 "Макрос "
                                    + name4
                                    + " уже существует!"
                              );
                              return 1;
                           } else {
                              try {
                                 int i = "NONE"
                                       .equals(
                                          name5
                                       )
                                    ? -1
                                    : GLFW.class
                                       .getField(
                                          "GLFW_KEY_"
                                             + name5
                                       )
                                       .getInt(null);
                                 MacroStorage var6x = AspectDLC.INSTANCE.macroStorage;
                                 var6x.add(
                                    new GetnameHelper(
                                       name4,
                                       name6,
                                       new KeybindSetting(
                                          "bind",
                                          i
                                       )
                                    )
                                 );
                                 ChatUtil.sendMessage(
                                    "Макрос "
                                       + name4
                                       + " был добавлен!"
                                 );
                              } catch (Exception var5x) {
                                 ChatUtil.sendMessage(
                                    "Неверный бинд: "
                                       + name5
                                 );
                              }

                              return 1;
                           }
                        }
                     )
               )
            )
         )
      );
      var10001 = this.literal("remove");
      var10000 = (LiteralArgumentBuilder)var10000.then(
         var10001.then(
            this.arg("name", StringArgumentType.word())
               .suggests(
                  (val6, val7) -> {
                     AspectDLC.INSTANCE
                        .macroStorage
                        .getNames()
                        .stream()
                        .filter(
                           val8 -> val8.startsWith(
                                 val7.getRemaining()
                              )
                        )
                        .forEach(
                           val7::suggest
                        );
                     return val7.buildFuture();
                  }
               )
               .executes(
                  val9 -> {
                     String name7 = (String)val9.getArgument(
                        "name", String.class
                     );
                     if (AspectDLC.INSTANCE.macroStorage.isEmpty()) {
                        ChatUtil.sendMessage(
                           "Список макросов пуст!"
                        );
                        return 1;
                     } else {
                        GetnameHelper val10 = AspectDLC.INSTANCE
                           .macroStorage
                           .getMacro(
                              name7
                           );
                        if (val10
                           == null) {
                           ChatUtil.sendMessage(
                              "Макрос "
                                 + name7
                                 + " не найден!"
                           );
                           return 1;
                        } else {
                           AspectDLC.INSTANCE
                              .macroStorage
                              .remove(
                                 val10
                              );
                           ChatUtil.sendMessage(
                              "Макрос "
                                 + name7
                                 + " был удалён!"
                           );
                           return 1;
                        }
                     }
                  }
               )
         )
      );
      var10000 = (LiteralArgumentBuilder)var10000.then(
         this.literal("list")
            .executes(
               val11 -> {
                  StringBuilder val12 = new StringBuilder();
                  if (AspectDLC.INSTANCE.macroStorage.getNames().isEmpty()) {
                     ChatUtil.sendMessage("Список макросов пуст!");
                  } else {
                     for (int i2 = 0;
                        i2
                           < AspectDLC.INSTANCE.macroStorage.getNames().size();
                        i2++
                     ) {
                        val12.append(
                           AspectDLC.INSTANCE
                              .macroStorage
                              .getNames()
                              .get(
                                 i2
                              )
                        );
                        if (i2
                           < AspectDLC.INSTANCE.macroStorage.getNames().size() - 1) {
                           val12.append(
                              ", "
                           );
                        }
                     }

                     val12.append(
                        "."
                     );
                     ChatUtil.sendMessage(
                        "Макросы: "
                           + val12
                     );
                  }

                  return 1;
               }
            )
      );
      var10000.then(
         this.literal("clear")
            .executes(
               val13 -> {
                  if (!AspectDLC.INSTANCE.macroStorage.isEmpty()) {
                     AspectDLC.INSTANCE.macroStorage.clear();
                     ChatUtil.sendMessage("Все макросы были удалены!");
                  } else {
                     ChatUtil.sendMessage("Список макросов пуст!");
                  }

                  return 1;
               }
            )
      );
   }
}
