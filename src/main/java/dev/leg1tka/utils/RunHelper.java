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



public class RunHelper extends Thread {
   public final DiscordManager this$0;

   public RunHelper(DiscordManager var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      this.setName("Discord-RPC");

      try {
         while (this.this$0.running) {
            IClientAction_GmJQLvIQ.INSTANCE.Discord_RunCallbacks();
            Thread.sleep(15000L);
         }
      } catch (Exception var2) {
         this.this$0.stopRPC();
      }

      super.run();
   }
}
