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


import com.sun.jna.Library;
import com.sun.jna.Platform;

public interface IClientAction_OHWhEvU1 extends Library {
   public IClientAction_OHWhEvU1 INSTANCE = !Platform.isWindows() && !Platform.isLinux() ? null : EncryptHelper.loadNativeLib();
}
