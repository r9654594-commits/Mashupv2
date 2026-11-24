package dev.leg1tka.storage;
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
import dev.leg1tka.utils.*;


import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

public class CommandStorage {
   private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher();
   private final List<ExecuteHelper_8> commands = new ArrayList<>();
   private String prefix = ".";

   public CommandStorage() {
      this.registry();
   }

   private void registry() {
      ExecuteHelper_8[] var10001 = new ExecuteHelper_8[11];
      var10001[0] = new ExecuteHelper_2();
      var10001[1] = new ExecuteHelper_3();
      var10001[2] = new ExecuteHelper_1();
      var10001[3] = new ExecuteHelper_9();
      var10001[4] = new ExecuteHelper();
      var10001[5] = new ExecuteHelper_10();
      int var10003 = 6;
      var10001[var10003] = new ExecuteHelper_10("nuk");
      var10001[7] = new ExecuteHelper_5();
      var10001[8] = new ExecuteHelper_11();
      var10001[9] = new ExecuteHelper_4();
      var10001[10] = new ExecuteHelper_6();
      this.addCommands(var10001);
   }

   public CommandSource getSource() {
      return new ClientCommandSource(null, MinecraftClient.getInstance());
   }

   private void addCommands(
      ExecuteHelper_8... val
   ) {
      for (ExecuteHelper_8 val2 : val) {
         val2.register(
            this.dispatcher
         );
         this.commands
            .add(
               val2
            );
      }
   }

   @Generated
   public CommandDispatcher<CommandSource> getDispatcher() {
      return this.dispatcher;
   }

   @Generated
   public List<ExecuteHelper_8> getCommands() {
      return this.commands;
   }

   @Generated
   public String getPrefix() {
      return this.prefix;
   }

   @Generated
   public void setPrefix(
      String str
   ) {
      this.prefix = str;
   }
}
