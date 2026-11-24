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



import dev.leg1tka.modules.render.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ExecuteHelper extends ExecuteHelper_8 {
   public ExecuteHelper() {
      super("blockesp");
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      LiteralArgumentBuilder var10001 = this.literal("add");
      LiteralArgumentBuilder var10000 = (LiteralArgumentBuilder)val.then(
         var10001.then(
            this.arg("block", StringArgumentType.word())
               .suggests(
                  (val2, val3) -> {
                     String str = val3.getRemaining()
                        .toLowerCase();
                     Registries.BLOCK
                        .stream()
                        .map(Registries.BLOCK::getId)
                        .<String>map(Identifier::getPath)
                        .filter(
                           val4 -> val4.startsWith(
                                 str
                              )
                        )
                        .limit(20L)
                        .forEach(
                           val3::suggest
                        );
                     return val3.buildFuture();
                  }
               )
               .executes(
                  val5 -> {
                     String str2 = (String)val5.getArgument(
                        "block", String.class
                     );
                     if (Blockesp.INSTANCE
                        .isTracking(
                           str2
                        )) {
                        ChatUtil.sendMessage(
                           "§cБлок §e"
                              + str2
                              + "§c уже отслеживается!"
                        );
                        return 1;
                     } else {
                        boolean flag = Registries.BLOCK
                           .stream()
                           .anyMatch(
                              val6 -> {
                                 String str3 = Registries.BLOCK
                                    .getId(
                                       val6
                                    )
                                    .getPath();
                                 return str3.equalsIgnoreCase(
                                    str2
                                 );
                              }
                           );
                        if (!flag
                           )
                         {
                           ChatUtil.sendMessage(
                              "§cБлок §e"
                                 + str2
                                 + "§c не найден!"
                           );
                           return 1;
                        } else {
                           Blockesp.INSTANCE
                              .addBlock(
                                 str2
                              );
                           ChatUtil.sendMessage(
                              "§aБлок §e"
                                 + str2
                                 + "§a добавлен в отслеживание!"
                           );
                           return 1;
                        }
                     }
                  }
               )
         )
      );
      var10001 = this.literal("remove");
      var10000 = (LiteralArgumentBuilder)var10000.then(
         var10001.then(
            this.arg("block", StringArgumentType.word())
               .suggests(
                  (val7, val8) -> {
                     Blockesp.INSTANCE
                        .getTrackedBlocks()
                        .stream()
                        .sorted(String::compareTo)
                        .filter(
                           val9 -> val9.startsWith(
                                 val8.getRemaining()
                                    .toLowerCase()
                              )
                        )
                        .forEach(
                           val8::suggest
                        );
                     return val8.buildFuture();
                  }
               )
               .executes(
                  val10 -> {
                     String str4 = (String)val10.getArgument(
                        "block", String.class
                     );
                     if (!Blockesp.INSTANCE
                        .isTracking(
                           str4
                        )) {
                        ChatUtil.sendMessage(
                           "§cБлок §e"
                              + str4
                              + "§c не отслеживается!"
                        );
                        return 1;
                     } else {
                        Blockesp.INSTANCE
                           .removeBlock(
                              str4
                           );
                        ChatUtil.sendMessage(
                           "§aБлок §e"
                              + str4
                              + "§a удалён из отслеживания!"
                        );
                        return 1;
                     }
                  }
               )
         )
      );
      var10000 = (LiteralArgumentBuilder)var10000.then(
         this.literal("list")
            .executes(
               val11 -> {
                  Set val12 = Blockesp.INSTANCE
                     .getTrackedBlocks();
                  if (val12.isEmpty()
                     )
                   {
                     ChatUtil.sendMessage("§cСписок отслеживаемых блоков пуст!");
                     return 1;
                  } else {
                     Stream var10000x = val12.stream()
                        .sorted();
                     String str5 = (String)var10000x.collect(
                        Collectors.joining("§7, §e")
                     );
                     ChatUtil.sendMessage(
                        "§aОтслеживаемые блоки §7(§e"
                           + val12.size()
                           + "§7)§a: §e"
                           + str5
                     );
                     return 1;
                  }
               }
            )
      );
      var10000.then(
         this.literal("clear")
            .executes(
               val13 -> {
                  if (Blockesp.INSTANCE.getTrackedBlocks().isEmpty()) {
                     ChatUtil.sendMessage("§cСписок отслеживаемых блоков уже пуст!");
                     return 1;
                  } else {
                     Blockesp.INSTANCE.clearBlocks();
                     ChatUtil.sendMessage("§aСписок отслеживаемых блоков очищен!");
                     return 1;
                  }
               }
            )
      );
   }
}
