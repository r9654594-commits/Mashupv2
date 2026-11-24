package dev.leg1tka.protection;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventInvoker;

public class AspectInitializer implements IMinecraft {
   public void onInitialize() {
      EventInvoker.register(this);
      this.initStorages();
   }

   public void initStorages() {
      AspectDLC.INSTANCE.moduleStorage = new ModuleStorage();
      AspectDLC.INSTANCE.themeStorage = new ThemeStorage();
      AspectDLC.INSTANCE.tpsCalc = new TpsCalc();
      EventInvoker.register(AspectDLC.INSTANCE.tpsCalc);
      AspectDLC.INSTANCE.localizationStorage = new LocalizationStorage();
      AspectDLC.INSTANCE.freeLookStorage = new FreeLookStorage();
      AspectDLC.INSTANCE.rotationStorage = new RotationStorage();
      AspectDLC.INSTANCE.serverStorage = new ServerStorage();
      AspectDLC.INSTANCE.friendStorage = new FriendStorage();
      AspectDLC.INSTANCE.macroStorage = new MacroStorage();
      AspectDLC.INSTANCE.staffStorage = new StaffStorage();
      AspectDLC.INSTANCE.waypointStorage = new WaypointStorage();
      AspectDLC.INSTANCE.commandStorage = new CommandStorage();
      AspectDLC.INSTANCE.configStorage = new ConfigStorage();
   }
}
