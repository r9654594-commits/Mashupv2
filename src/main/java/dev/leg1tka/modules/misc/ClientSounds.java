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


public class ClientSounds extends Module {
   public static ClientSounds INSTANCE = new ClientSounds();
   public final ModeSetting stateSounds;
   public final NumberSetting volume;

   public ClientSounds() {
      super("ClientSounds", Category.MISC);
      String var1 = "ClientSounds";
      String var3 = "Режим";
      String var5 = "Celestial";
      this.stateSounds = new ModeSetting(var3, var5, new String[]{"Celestial"});
      this.volume = new NumberSetting(
         "Громкость", 50.0F, 1.0F, 100.0F, 0.5F
      );
      this.addSettings(new Setting[]{
         this.stateSounds,
         this.volume
      });
   }
}
