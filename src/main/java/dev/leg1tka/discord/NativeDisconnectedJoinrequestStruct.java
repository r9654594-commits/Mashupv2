package dev.leg1tka.discord;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class NativeDisconnectedJoinrequestStruct extends Structure {
   public IClientAction_DynyaooE disconnected;
   public IClientAction_Wu2s8fhS joinRequest;
   public IClientAction_RCeGZEVl spectateGame;
   public IClientAction_FvBRX2pb ready;
   public IClientAction_BBV7RNmy errored;
   public IClientAction_VT005wLb joinGame;

   protected List<String> getFieldOrder() {
      return Arrays.asList(new String[]{"ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest"});
   }
}
