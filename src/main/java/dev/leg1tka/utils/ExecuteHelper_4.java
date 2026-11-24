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
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;

public class ExecuteHelper_4 extends ExecuteHelper_8 {
   public ExecuteHelper_4() {
      super(
         "staff"
      );
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)val.then(
                  this.literal(
                        "add"
                     )
                     .then(
                        this.arg(
                              "player",
                              StringArgumentType.word()
                           )
                           .suggests(
                              (val2, val3) -> {
                                 for (PlayerListEntry val4 : mc.getNetworkHandler()
                                    .getPlayerList()) {
                                    String name = val4.getProfile()
                                       .getName();
                                    if (name.toLowerCase()
                                       .startsWith(
                                          val3.getRemaining()
                                             .toLowerCase()
                                       )) {
                                       val3.suggest(
                                          name
                                       );
                                    }
                                 }

                                 return val3.buildFuture();
                              }
                           )
                           .executes(
                              val5 -> {
                                 String name2 = (String)val5.getArgument(
                                    "player",
                                    String.class
                                 );
                                 if (!AspectDLC.INSTANCE
                                    .staffStorage
                                    .isStaff(
                                       name2
                                    )) {
                                    AspectDLC.INSTANCE
                                       .staffStorage
                                       .add(
                                          name2
                                       );
                                    ChatUtil.sendMessage(
                                       "Игрок "
                                          + name2
                                          + " добавлен в список стаффов!"
                                    );
                                 } else {
                                    ChatUtil.sendMessage(
                                       "Игрок "
                                          + name2
                                          + " уже в списке стаффов!"
                                    );
                                 }

                                 return 1;
                              }
                           )
                     )
               ))
               .then(
                  this.literal(
                        "remove"
                     )
                     .then(
                        this.arg(
                              "player",
                              StringArgumentType.word()
                           )
                           .suggests(
                              (val6, val7) -> {
                                 AspectDLC.INSTANCE
                                    .staffStorage
                                    .getStaffs()
                                    .stream()
                                    .sorted(String::compareTo)
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
                                 String name3 = (String)val9.getArgument(
                                    "player",
                                    String.class
                                 );
                                 if (AspectDLC.INSTANCE
                                    .staffStorage
                                    .isStaff(
                                       name3
                                    )) {
                                    AspectDLC.INSTANCE
                                       .staffStorage
                                       .remove(
                                          name3
                                       );
                                    ChatUtil.sendMessage(
                                       "Игрок "
                                          + name3
                                          + " удалён из списка стаффов!"
                                    );
                                 } else {
                                    ChatUtil.sendMessage(
                                       "Игрок "
                                          + name3
                                          + " не найден в списке стаффов!"
                                    );
                                 }

                                 return 1;
                              }
                           )
                     )
               ))
            .then(
               this.literal(
                     "list"
                  )
                  .executes(
                     val10 -> {
                        StringBuilder val11 = new StringBuilder();
                        if (AspectDLC.INSTANCE.staffStorage.getStaffs().isEmpty()) {
                           ChatUtil.sendMessage(
                              "Список стаффов пуст!"
                           );
                        } else {
                           for (int i = 0;
                              i
                                 < AspectDLC.INSTANCE.staffStorage.getStaffs().size();
                              i++
                           ) {
                              val11.append(
                                 AspectDLC.INSTANCE
                                    .staffStorage
                                    .getStaffs()
                                    .get(
                                       i
                                    )
                              );
                              if (i
                                 < AspectDLC.INSTANCE.staffStorage.getStaffs().size() - 1) {
                                 val11.append(
                                    ", "
                                 );
                              }
                           }

                           val11.append(
                              "."
                           );
                           ChatUtil.sendMessage(
                              "Стаффы: "
                                 + val11
                           );
                        }

                        return 1;
                     }
                  )
            ))
         .then(
            this.literal(
                  "clear"
               )
               .executes(
                  val12 -> {
                     if (!AspectDLC.INSTANCE.staffStorage.isEmpty()) {
                        AspectDLC.INSTANCE.staffStorage.clear();
                        ChatUtil.sendMessage(
                           "Список стаффов очищен!"
                        );
                     } else {
                        ChatUtil.sendMessage(
                           "Список стаффов пуст!"
                        );
                     }

                     return 1;
                  }
               )
         );
   }
}
