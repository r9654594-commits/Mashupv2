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
import com.sun.jna.Native;

public interface IClientAction_GmJQLvIQ extends Library {
   public IClientAction_GmJQLvIQ INSTANCE = (IClientAction_GmJQLvIQ)Native.loadLibrary("discord-rpc", IClientAction_GmJQLvIQ.class);

   public void Discord_UpdateHandlers(NativeDisconnectedJoinrequestStruct var1);

   public void Discord_UpdatePresence(NativeLargeimagekeyLargeimagetextStruct var1);

   public void Discord_Respond(String var1, int var2);

   public void Discord_Register(String var1, String var2);

   public void Discord_Shutdown();

   public void Discord_UpdateConnection();

   public void Discord_RegisterSteamGame(String var1, String var2);

   public void Discord_RunCallbacks();

   public void Discord_Initialize(String var1, NativeDisconnectedJoinrequestStruct var2, boolean var3, String var4);

   public void Discord_ClearPresence();
}
