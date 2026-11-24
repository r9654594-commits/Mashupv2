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
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;

public class ExecuteHelper_9 extends ExecuteHelper_8 {
   public ExecuteHelper_9() {
      super(
         "bot"
      );
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)val.then(
                              this.literal(
                                    "connect"
                                 )
                                 .then(
                                    this.arg(
                                          "name",
                                          StringArgumentType.string()
                                       )
                                       .then(
                                          this.arg(
                                                "ip",
                                                StringArgumentType.string()
                                             )
                                             .executes(
                                                val2 -> {
                                                   MinecraftClient val3 = MinecraftClient.getInstance();
                                                   if (val3.player
                                                      == null) {
                                                      return 0;
                                                   } else {
                                                      String name2 = StringArgumentType.getString(
                                                         val2,
                                                         "name"
                                                      );
                                                      String name3 = StringArgumentType.getString(
                                                         val2,
                                                         "ip"
                                                      );
                                                      GetconnectionsHelper.connect(
                                                         name2,
                                                         name3
                                                      );
                                                      ChatUtil.sendMessage(
                                                         "§7[Bot] §fПодключение выполнено: "
                                                            + name2
                                                            + " -> "
                                                            + name3
                                                      );
                                                      return 1;
                                                   }
                                                }
                                             )
                                       )
                                 )
                           ))
                           .then(
                              this.literal(
                                    "remove"
                                 )
                                 .then(
                                    this.arg(
                                          "name",
                                          StringArgumentType.string()
                                       )
                                       .suggests(
                                          (val4, val5) -> {
                                             GetconnectionsHelper.getSessionNames(false)
                                                .forEach(
                                                   val5::suggest
                                                );
                                             return val5.buildFuture();
                                          }
                                       )
                                       .executes(
                                          val6 -> {
                                             MinecraftClient val7 = MinecraftClient.getInstance();
                                             if (val7.player
                                                == null) {
                                                return 0;
                                             } else {
                                                String name4 = StringArgumentType.getString(
                                                   val6,
                                                   "name"
                                                );
                                                if (GetconnectionsHelper.remove(
                                                   name4
                                                )) {
                                                   ChatUtil.sendMessage(
                                                      "§7[Bot] §fСессия отключена и удалена: "
                                                         + name4
                                                   );
                                                } else {
                                                   ChatUtil.sendMessage(
                                                      "§7[Bot] §fСессия не найдена: "
                                                         + name4
                                                   );
                                                }

                                                return 1;
                                             }
                                          }
                                       )
                                 )
                           ))
                        .then(
                           this.literal(
                                 "control"
                              )
                              .then(
                                 this.arg(
                                       "name",
                                       StringArgumentType.string()
                                    )
                                    .suggests(
                                       (val8, val9) -> {
                                          GetconnectionsHelper.getSessionNames(false)
                                             .forEach(
                                                val9::suggest
                                             );
                                          return val9.buildFuture();
                                       }
                                    )
                                    .executes(
                                       val10 -> {
                                          MinecraftClient val11 = MinecraftClient.getInstance();
                                          if (val11.player
                                             == null) {
                                             return 0;
                                          } else {
                                             String name5 = StringArgumentType.getString(
                                                val10,
                                                "name"
                                             );
                                             if (name5.equalsIgnoreCase(
                                                GetconnectionsHelper.getCurrentSessionName()
                                             )) {
                                                ChatUtil.sendMessage(
                                                   "§7[Bot] §fТы уже управляешь этой сессией: "
                                                      + name5
                                                );
                                                return 1;
                                             } else {
                                                if (GetconnectionsHelper.control(
                                                   name5
                                                )) {
                                                   ChatUtil.sendMessage(
                                                      "§7[Bot] §fПереключаю на сессию: "
                                                         + name5
                                                   );
                                                } else {
                                                   ChatUtil.sendMessage(
                                                      "§7[Bot] §fСессия не найдена: "
                                                         + name5
                                                   );
                                                }

                                                return 1;
                                             }
                                          }
                                       }
                                    )
                              )
                        ))
                     .then(
                        this.literal(
                              "say"
                           )
                           .then(
                              this.arg(
                                    "name",
                                    StringArgumentType.string()
                                 )
                                 .suggests(
                                    (val12, val13) -> {
                                       GetconnectionsHelper.getSessionNames(false)
                                          .forEach(
                                             val13::suggest
                                          );
                                       return val13.buildFuture();
                                    }
                                 )
                                 .then(
                                    this.arg(
                                          "message",
                                          StringArgumentType.greedyString()
                                       )
                                       .executes(
                                          val14 -> {
                                             MinecraftClient val15 = MinecraftClient.getInstance();
                                             if (val15.player
                                                == null) {
                                                return 0;
                                             } else {
                                                String name6 = StringArgumentType.getString(
                                                   val14,
                                                   "name"
                                                );
                                                String name7 = StringArgumentType.getString(
                                                   val14,
                                                   "message"
                                                );
                                                if (GetconnectionsHelper.say(
                                                   name6,
                                                   name7
                                                )) {
                                                   ChatUtil.sendMessage(
                                                      "§7[Bot] §fСообщение отправлено от сессии "
                                                         + name6
                                                   );
                                                } else {
                                                   ChatUtil.sendMessage(
                                                      "§7[Bot] §fСессия не найдена: "
                                                         + name6
                                                   );
                                                }

                                                return 1;
                                             }
                                          }
                                       )
                                 )
                           )
                     ))
                  .then(
                     this.literal(
                           "sayall"
                        )
                        .then(
                           this.arg(
                                 "message",
                                 StringArgumentType.greedyString()
                              )
                              .executes(
                                 val16 -> {
                                    MinecraftClient val17 = MinecraftClient.getInstance();
                                    if (val17.player
                                       == null) {
                                       return 0;
                                    } else {
                                       String name8 = StringArgumentType.getString(
                                          val16,
                                          "message"
                                       );
                                       GetconnectionsHelper.sayAll(
                                          name8
                                       );
                                       ChatUtil.sendMessage(
                                          "§7[Bot] §fСообщение отправлено от всех ботов."
                                       );
                                       return 1;
                                    }
                                 }
                              )
                        )
                  ))
               .then(
                  ((LiteralArgumentBuilder)this.literal(
                           "return"
                        )
                        .executes(
                           val18 -> {
                              MinecraftClient val19 = MinecraftClient.getInstance();
                              if (val19.player
                                 == null) {
                                 return 0;
                              } else {
                                 if (GetconnectionsHelper.restore()) {
                                    ChatUtil.sendMessage(
                                       "§7[Bot] §fВозвращаю предыдущую сессию"
                                    );
                                 } else {
                                    ChatUtil.sendMessage(
                                       "§7[Bot] §fНет сохранённой сессии для возврата"
                                    );
                                 }

                                 return 1;
                              }
                           }
                        ))
                     .then(
                        this.arg(
                              "name",
                              StringArgumentType.string()
                           )
                           .suggests(
                              (val20, val21) -> {
                                 GetconnectionsHelper.getSessionNames(true)
                                    .forEach(
                                       val21::suggest
                                    );
                                 return val21.buildFuture();
                              }
                           )
                           .executes(
                              val22 -> {
                                 MinecraftClient val23 = MinecraftClient.getInstance();
                                 if (val23.player
                                    == null) {
                                    return 0;
                                 } else {
                                    String name9 = StringArgumentType.getString(
                                       val22,
                                       "name"
                                    );
                                    if (name9.equalsIgnoreCase(
                                       GetconnectionsHelper.getCurrentSessionName()
                                    )) {
                                       ChatUtil.sendMessage(
                                          "§7[Bot] §fТы уже управляешь этой сессией: "
                                             + name9
                                       );
                                       return 1;
                                    } else {
                                       if (GetconnectionsHelper.restore(
                                          name9
                                       )) {
                                          ChatUtil.sendMessage(
                                             "§7[Bot] §fПереключаю на сессию: "
                                                + name9
                                          );
                                       } else {
                                          ChatUtil.sendMessage(
                                             "§7[Bot] §fСессия не найдена: "
                                                + name9
                                          );
                                       }

                                       return 1;
                                    }
                                 }
                              }
                           )
                     )
               ))
            .then(
               this.literal(
                     "ignore"
                  )
                  .executes(
                     val24 -> {
                        MinecraftClient val25 = MinecraftClient.getInstance();
                        if (val25.player
                           == null) {
                           return 0;
                        } else {
                           boolean flag = GetconnectionsHelper.toggleIgnoreBotMessages();
                           ChatUtil.sendMessage(
                              "§7[Bot] §fИгнор сообщений ботов: "
                                 + (
                                    flag
                                       ? "§aвключен"
                                       : "§cвыключен"
                                 )
                           );
                           return 1;
                        }
                     }
                  )
            ))
         .then(
            this.literal(
                  "list"
               )
               .executes(
                  val26 -> {
                     MinecraftClient val27 = MinecraftClient.getInstance();
                     if (val27.player
                        == null) {
                        return 0;
                     } else {
                        List<NameHelper> list2 = GetconnectionsHelper.getConnections();
                        ChatUtil.sendMessage("§7[Bot] §fТекущая сессия: " + GetconnectionsHelper.getCurrentSessionName());
                        if (list2.isEmpty()
                           )
                         {
                           ChatUtil.sendMessage(
                              "§7[Bot] §fСписок сохранённых сессий пуст"
                           );
                        } else {
                           ChatUtil.sendMessage(
                              "§7[Bot] §fСохранённые сессии:"
                           );

                           for (NameHelper val28 : list2) {
                              ChatUtil.sendMessage(
                                 "§7- §f"
                                    + val28.name()
                                    + " (§7"
                                    + val28.address()
                                    + "§f)"
                              );
                           }
                        }

                        return 1;
                     }
                  }
               )
         );
   }
}
