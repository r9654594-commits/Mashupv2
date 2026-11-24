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

public class KTLeave extends Module {
   public static KTLeave INSTANCE = new KTLeave();
   private boolean hasGM;
   private double lastX;
   private double lastY;
   private double lastZ;
   private KeybindSetting bind = new KeybindSetting(
      "Кнопка лива",
      -1
   );

   public KTLeave() {
      super(
         "KTLeave",
         "Позволяет ливнуть с пвп прямо в кт",
         Category.PLAYER
      );
      this.addSettings(new Setting[]{this.bind});
   }

   @EventLink
   public void onKey(
      EventBinding val
   ) {
      if (mc.player != null) {
         if (val.getKey()
            == this.bind.getKey()) {
            this.hasGM = !this.hasGM;
            if (this.hasGM) {
               this.lastX = mc.player.getX();
               this.lastY = mc.player.getY();
               this.lastZ = mc.player.getZ();
               mc.player.setPosition(mc.player.getX() + 10.0, mc.player.getY() + 10.0, mc.player.getZ() + 10.0);
            } else {
               mc.player.setPosition(this.lastX, this.lastY, this.lastZ);
            }
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.hasGM = false;
   }
}
