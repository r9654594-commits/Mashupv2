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



import dev.leg1tka.modules.player.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ExecuteHelper_10 extends ExecuteHelper_8 {
   public ExecuteHelper_10() {
      super("item");
   }

   public ExecuteHelper_10(
      String str
   ) {
      super(
         str
      );
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      LiteralArgumentBuilder var10001 = this.literal("add");
      LiteralArgumentBuilder var10000 = (LiteralArgumentBuilder)val.then(
         var10001.then(
            this.arg(
                  "block",
                  StringArgumentType.word()
               )
               .suggests(
                  (val2, val3) -> {
                     String name = Nuker.normalizeBlockName(
                        val3.getRemaining()
                     );
                     Registries.BLOCK
                        .stream()
                        .map(Registries.BLOCK::getId)
                        .<String>map(Identifier::getPath)
                        .filter(
                           val4 -> val4.startsWith(
                                 name
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
                     String name2 = Nuker.normalizeBlockName(
                        (String)val5.getArgument(
                           "block",
                           String.class
                        )
                     );
                     if (Nuker.INSTANCE
                        .isTargetBlock(
                           name2
                        )) {
                        ChatUtil.sendMessage(
                           "§cБлок §e"
                              + name2
                              + "§c уже в списке Nuker!"
                        );
                        return 1;
                     } else if (!this.blockExists(
                        name2
                     )) {
                        ChatUtil.sendMessage(
                           "§cБлок §e"
                              + name2
                              + "§c не найден!"
                        );
                        return 1;
                     } else {
                        Nuker.INSTANCE
                           .addBlock(
                              name2
                           );
                        ChatUtil.sendMessage(
                           "§aБлок §e"
                              + name2
                              + "§a добавлен в Nuker!"
                        );
                        return 1;
                     }
                  }
               )
         )
      );
      var10001 = this.literal("remove");
      var10000 = (LiteralArgumentBuilder)var10000.then(
         var10001.then(
            this.arg(
                  "block",
                  StringArgumentType.word()
               )
               .suggests(
                  (val6, val7) -> {
                     String name3 = Nuker.normalizeBlockName(
                        val7.getRemaining()
                     );
                     Nuker.INSTANCE
                        .getTargetBlocks()
                        .stream()
                        .sorted(String::compareTo)
                        .filter(
                           val8 -> val8.startsWith(
                                 name3
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
                     String name4 = Nuker.normalizeBlockName(
                        (String)val9.getArgument(
                           "block",
                           String.class
                        )
                     );
                     if (!Nuker.INSTANCE
                        .isTargetBlock(
                           name4
                        )) {
                        ChatUtil.sendMessage(
                           "§cБлока §e"
                              + name4
                              + "§c нет в списке Nuker!"
                        );
                        return 1;
                     } else {
                        Nuker.INSTANCE
                           .removeBlock(
                              name4
                           );
                        ChatUtil.sendMessage(
                           "§aБлок §e"
                              + name4
                              + "§a удален из Nuker!"
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
               val10 -> {
                  Set val11 = Nuker.INSTANCE
                     .getTargetBlocks();
                  if (val11.isEmpty()
                     )
                   {
                     ChatUtil.sendMessage("§cСписок Nuker пуст!");
                     return 1;
                  } else {
                     Stream<String> var10000x = val11.stream().sorted();
                     String name5 = var10000x.collect(
                        Collectors.joining(
                           "§7, §e"
                        )
                     );
                     ChatUtil.sendMessage(
                        "§aБлоки Nuker §7(§e"
                           + val11.size()
                           + "§7)§a: §e"
                           + name5
                     );
                     return 1;
                  }
               }
            )
      );
      var10000.then(
         this.literal("clear")
            .executes(
               val12 -> {
                  if (Nuker.INSTANCE.getTargetBlocks().isEmpty()) {
                     ChatUtil.sendMessage("§cСписок Nuker уже пуст!");
                     return 1;
                  } else {
                     Nuker.INSTANCE.clearBlocks();
                     ChatUtil.sendMessage(
                        "§aСписок Nuker очищен!"
                     );
                     return 1;
                  }
               }
            )
      );
   }

   private boolean blockExists(
      String str
   ) {
      return Registries.BLOCK
         .stream()
         .anyMatch(
            val -> Registries.BLOCK
                  .getId(
                     val
                  )
                  .getPath()
                  .equalsIgnoreCase(
                     str
                  )
         );
   }
}
