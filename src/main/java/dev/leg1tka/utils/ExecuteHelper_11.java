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
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import net.minecraft.command.CommandSource;
import org.lwjgl.glfw.GLFW;

public class ExecuteHelper_11 extends ExecuteHelper_8 {
   public ExecuteHelper_11() {
      super("bind");
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      val.then(
         this.literal("add")
            .then(
               this.arg("module", StringArgumentType.word())
                  .suggests(
                     (val2, val3) -> {
                        String name = val3.getRemaining()
                           .toLowerCase();
                        ModuleManager.INSTANCE
                           .getObject()
                           .stream()
                           .map(Module::getName)
                           .filter(
                              val4 -> val4.toLowerCase()
                                    .startsWith(
                                       name
                                    )
                           )
                           .forEach(
                              val3::suggest
                           );
                        return val3.buildFuture();
                     }
                  )
                  .then(
                     this.arg("key", StringArgumentType.word())
                        .suggests(
                           (val5, val6) -> {
                              String name2 = val6.getRemaining()
                                 .toUpperCase();

                              for (Field val7 : GLFW.class
                                 .getDeclaredFields()) {
                                 String name3 = val7.getName();
                                 if (name3.startsWith(
                                    "GLFW_KEY_"
                                 )) {
                                    String name4 = name3.replace(
                                       "GLFW_KEY_", ""
                                    );
                                    if (name4.startsWith(
                                       name2
                                    )) {
                                       val6.suggest(
                                          name4
                                       );
                                    }
                                 }
                              }

                              if ("NONE"
                                 .startsWith(
                                    name2
                                 )) {
                                 val6.suggest(
                                    "NONE"
                                 );
                              }

                              return val6.buildFuture();
                           }
                        )
                        .executes(
                           val8 -> {
                              String name5 = (String)val8.getArgument(
                                 "module", String.class
                              );
                              Optional<Module> val9 = this.findModuleByName(
                                 name5
                              );
                              if (val9.isEmpty()
                                 )
                               {
                                 ChatUtil.sendMessage(
                                    "Модуль "
                                       + name5
                                       + " не найден"
                                 );
                                 return 1;
                              } else {
                                 Module val10 = val9.get();
                                 String name6 = ((String)val8.getArgument(
                                       "key", String.class
                                    ))
                                    .toUpperCase();
                                 int i = this.getKeyCode(
                                    name6
                                 );
                                 if (i
                                    == -1) {
                                    ChatUtil.sendMessage(
                                       "Клавиша "
                                          + name6
                                          + " не найдена"
                                    );
                                 } else {
                                    val10.setKey(
                                       i
                                    );
                                    ChatUtil.sendMessage(
                                       "Модуль "
                                          + val10.getName()
                                          + " привязан к клавише "
                                          + name6
                                    );
                                 }

                                 return 1;
                              }
                           }
                        )
                  )
            )
      );
      val.then(
         this.literal("remove")
            .then(
               this.arg("module", StringArgumentType.word())
                  .executes(
                     val11 -> {
                        String name7 = (String)val11.getArgument(
                           "module", String.class
                        );
                        Optional<Module> val12 = this.findModuleByName(
                           name7
                        );
                        if (val12.isEmpty()
                           )
                         {
                           ChatUtil.sendMessage(
                              "Модуль "
                                 + name7
                                 + " не найден"
                           );
                           return 1;
                        } else {
                           Module val13 = val12.get();
                           val13.setKey(
                              -1
                           );
                           ChatUtil.sendMessage(
                              "Привязка клавиши для модуля "
                                 + val13.getName()
                                 + " удалена"
                           );
                           return 1;
                        }
                     }
                  )
            )
      );
      val.then(
         this.literal("clear")
            .executes(
               val14 -> {
                  ModuleManager.INSTANCE
                     .getObject()
                     .forEach(
                        val15 -> val15.setKey(
                              -1
                           )
                     );
                  ChatUtil.sendMessage("Все привязки клавиш удалены");
                  return 1;
               }
            )
      );
      val.then(
         this.literal("list")
            .executes(
               val16 -> {
                  List<Module> list2 = ModuleManager.INSTANCE
                     .getObject()
                     .stream()
                     .filter(
                        val17 -> val17.getKey()
                              != -1
                     )
                     .toList();
                  if (list2.isEmpty()
                     )
                   {
                     ChatUtil.sendMessage("Нет привязанных модулей");
                  } else {
                     ChatUtil.sendMessage("Список привязанных модулей:");

                     for (Module val18 : list2) {
                        String name8 = CallHelper.getKeyName(
                           val18.getKey()
                        );
                        ChatUtil.sendMessage(
                           "§7• §f"
                              + val18.getName()
                              + " §7-> §f"
                              + name8
                        );
                     }
                  }

                  return 1;
               }
            )
      );
   }

   private Optional<Module> findModuleByName(
      String str
   ) {
      return ModuleManager.INSTANCE
         .getObject()
         .stream()
         .filter(
            val -> val.getName()
                  .equalsIgnoreCase(
                     str
                  )
         )
         .findFirst();
   }

   private int getKeyCode(
      String str
   ) {
      if ("NONE"
         .equalsIgnoreCase(
            str
         )) {
         return -1;
      } else {
         try {
            return GLFW.class
               .getField(
                  "GLFW_KEY_"
                     + str
               )
               .getInt(null);
         } catch (IllegalAccessException | NoSuchFieldException var3) {
            return -1;
         }
      }
   }
}
