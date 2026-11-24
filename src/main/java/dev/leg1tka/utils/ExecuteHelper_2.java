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

public class ExecuteHelper_2 extends ExecuteHelper_8 {
   public ExecuteHelper_2() {
      super("friend");
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)val.then(
                  this.literal("add")
                     .then(
                        this.arg("player", StringArgumentType.word())
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
                                    "player", String.class
                                 );
                                 if (!AspectDLC.INSTANCE
                                    .friendStorage
                                    .isFriend(
                                       name2
                                    )) {
                                    AspectDLC.INSTANCE
                                       .friendStorage
                                       .add(
                                          name2
                                       );
                                    ChatUtil.sendMessage(
                                       "Игрок "
                                          + name2
                                          + " добавлен в друзья!"
                                    );
                                 } else {
                                    ChatUtil.sendMessage(
                                       "Игрок "
                                          + name2
                                          + " уже в списке друзей!"
                                    );
                                 }

                                 return 1;
                              }
                           )
                     )
               ))
               .then(
                  this.literal("remove")
                     .then(
                        this.arg("player", StringArgumentType.word())
                           .suggests(
                              (val6, val7) -> {
                                 AspectDLC.INSTANCE
                                    .friendStorage
                                    .getFriends()
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
                                    "player", String.class
                                 );
                                 if (AspectDLC.INSTANCE
                                    .friendStorage
                                    .isFriend(
                                       name3
                                    )) {
                                    AspectDLC.INSTANCE
                                       .friendStorage
                                       .remove(
                                          name3
                                       );
                                    ChatUtil.sendMessage(
                                       "Игрок "
                                          + name3
                                          + " удалён из друзей!"
                                    );
                                 } else {
                                    ChatUtil.sendMessage(
                                       "Игрок "
                                          + name3
                                          + " не найден в списке друзей!"
                                    );
                                 }

                                 return 1;
                              }
                           )
                     )
               ))
            .then(
               this.literal("list")
                  .executes(
                     val10 -> {
                        if (AspectDLC.INSTANCE.friendStorage.getFriends().isEmpty()) {
                           ChatUtil.sendMessage("Список друзей пуст!");
                        } else {
                           StringBuilder val11 = new StringBuilder();

                           for (int i = 0;
                              i
                                 < AspectDLC.INSTANCE.friendStorage.getFriends().size();
                              i++
                           ) {
                              val11.append(
                                 AspectDLC.INSTANCE
                                    .friendStorage
                                    .getFriends()
                                    .get(
                                       i
                                    )
                              );
                              if (i
                                 < AspectDLC.INSTANCE.friendStorage.getFriends().size() - 1) {
                                 val11.append(
                                    ", "
                                 );
                              }
                           }

                           ChatUtil.sendMessage(
                              "Друзья: "
                                 + val11
                           );
                        }

                        return 1;
                     }
                  )
            ))
         .then(
            this.literal("clear")
               .executes(
                  val12 -> {
                     if (!AspectDLC.INSTANCE.friendStorage.isEmpty()) {
                        AspectDLC.INSTANCE.friendStorage.clear();
                        ChatUtil.sendMessage("Список друзей очищен!");
                     } else {
                        ChatUtil.sendMessage("Список друзей пуст!");
                     }

                     return 1;
                  }
               )
         );
   }
}
