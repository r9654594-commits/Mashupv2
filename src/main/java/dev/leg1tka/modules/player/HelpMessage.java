package dev.leg1tka.modules.player;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventBinding;

public class HelpMessage extends Module {
   public static HelpMessage INSTANCE = new HelpMessage();
   private final KeybindSetting bind = new KeybindSetting(
      "Бинд",
      -1
   );

   public HelpMessage() {
      super(
         "HelpMessage",
         "Отправляет координаты в глобальный чат",
         Category.PLAYER
      );
      this.addSettings(new Setting[]{this.bind});
   }

   @EventLink
   public void onBinding(
      EventBinding val
   ) {
      if (mc.player != null && mc.getNetworkHandler() != null && mc.currentScreen == null) {
         if (val.getKey()
            == this.bind.getKey()) {
            int n = mc.player
               .getBlockX();
            int n2 = mc.player
               .getBlockY();
            int n3 = mc.player
               .getBlockZ();
            mc.getNetworkHandler()
               .sendChatMessage(
                  "! "
                     + n
                     + " "
                     + n2
                     + " "
                     + n3
               );
         }
      }
   }
}
