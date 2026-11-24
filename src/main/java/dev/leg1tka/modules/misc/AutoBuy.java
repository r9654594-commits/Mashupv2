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


public class AutoBuy extends Module {
   public static AutoBuy INSTANCE = new AutoBuy();
   public KeybindSetting openKey;

   public AutoBuy() {
      super("AutoBuy", Category.MISC);
      this.openKey = new KeybindSetting(
         "Бинд гуи",
         -1
      );
      this.addSettings(new Setting[]{this.openKey});
      this.setHidden(true);
   }
}
