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


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.Generated;
import net.minecraft.command.CommandSource;

public abstract class ExecuteHelper_8 implements IMinecraft {
   private final String command;

   public ExecuteHelper_8(
      String str
   ) {
      this.command = str;
   }

   public abstract void execute(LiteralArgumentBuilder<CommandSource> var1);

   public void register(
      CommandDispatcher<CommandSource> val
   ) {
      LiteralArgumentBuilder val2 = LiteralArgumentBuilder.literal(
         this.command
      );
      this.execute(
         val2
      );
      val.register(
         val2
      );
   }

   protected <T> RequiredArgumentBuilder<CommandSource, T> arg(
      String str,
      ArgumentType<T> val
   ) {
      return RequiredArgumentBuilder.argument(
         str,
         val
      );
   }

   protected LiteralArgumentBuilder<CommandSource> literal(
      String str
   ) {
      return LiteralArgumentBuilder.literal(
         str
      );
   }

   @Generated
   public String getCommand() {
      return this.command;
   }
}
