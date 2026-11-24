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


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.leg1tka.AspectDLC;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandSource;

public class ExecuteHelper_5 extends ExecuteHelper_8 {
   public ExecuteHelper_5() {
      super("kpk");
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      LiteralArgumentBuilder var10000 = (LiteralArgumentBuilder)val.then(
         this.arg(
               "X",
               IntegerArgumentType.integer()
            )
            .then(
               this.arg("Z", IntegerArgumentType.integer())
                  .executes(
                     val2 -> {
                        int x = (Integer)val2.getArgument(
                           "X", Integer.class
                        );
                        int x2 = (Integer)val2.getArgument(
                           "Z", Integer.class
                        );
                        GetxHelper val3 = new GetxHelper(
                           (double)x,
                           (double)x2
                        );
                        AspectDLC.INSTANCE
                           .waypointStorage
                           .set(
                              val3
                           );
                        String var4 = "Метка поставлена: ";
                        Object[] var10001 = new Object[2];
                        var10001[0] = x;
                        var10001[1] = x2;
                        ChatUtil.sendMessage(I18n.translate(var4, var10001));
                        return 1;
                     }
                  )
            )
      );
      var10000.then(
         this.literal("remove")
            .executes(
               val4 -> {
                  if (!AspectDLC.INSTANCE.waypointStorage.isEmpty()) {
                     AspectDLC.INSTANCE.waypointStorage.clear();
                     ChatUtil.sendMessage(I18n.translate("Метка удалена!", new Object[0]));
                  } else {
                     ChatUtil.sendMessage(I18n.translate("Метки не было", new Object[0]));
                  }

                  return 1;
               }
            )
      );
   }
}
