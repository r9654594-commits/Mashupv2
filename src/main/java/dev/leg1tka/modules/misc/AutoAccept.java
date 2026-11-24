package dev.leg1tka.modules.misc;


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

import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventPacket;
import java.util.Locale;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AutoAccept extends Module {
   public static AutoAccept INSTANCE = new AutoAccept();
   private final BooleanSetting onlyFriend = new BooleanSetting(
      "Только друзья",
      false
   );

   public AutoAccept() {
      super(
         "AutoAccept",
         "Автоматически принимает телепорт",
         Category.MISC
      );
      this.addSettings(new Setting[]{this.onlyFriend});
   }

   @EventLink
   public void onEvent(
      EventPacket val
   ) {
      if (mc.player != null && mc.world != null) {
         if (val.getType()
            == EventPacket.Type.RECEIVE) {
            if (val.getPacket() instanceof GameMessageS2CPacket val2
               )
             {
               String str = val2.content()
                  .getString()
                  .toLowerCase(Locale.ROOT);
               if (str.contains(
                     "телепортироваться"
                  )
                  || str.contains(
                     "has requested teleport"
                  )
                  || str.contains(
                     "просит к вам телепортироваться"
                  )) {
                  if (this.onlyFriend.isState()) {
                     boolean flag = false;
                     if (AspectDLC.INSTANCE.friendStorage != null) {
                        for (String str2 : AspectDLC.INSTANCE
                           .friendStorage
                           .getFriends()) {
                           if (str.contains(
                              str2.toLowerCase(
                                 Locale.ROOT
                              )
                           )) {
                              flag = true;
                              break;
                           }
                        }
                     }

                     if (!flag
                        )
                      {
                        return;
                     }
                  }

                  mc.player
                     .networkHandler
                     .sendChatCommand(
                        "tpaccept"
                     );
               }
            }
         }
      }
   }
}
