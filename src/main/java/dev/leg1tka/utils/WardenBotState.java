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


public enum WardenBotState {
   IDLE,
   PATH,
   WAIT_GUI,
   LOOT,
   GO_HOME,
   WAIT_HOME_LOAD,
   HOME_PATH,
   WAIT_HOME_GUI,
   DUMP,
   GO_WARP,
   WAIT_WARP_LOAD;
}
