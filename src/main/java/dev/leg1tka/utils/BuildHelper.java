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


public class BuildHelper {
   private final NativeDisconnectedJoinrequestStruct handlers = new NativeDisconnectedJoinrequestStruct();

   public NativeDisconnectedJoinrequestStruct build() {
      return this.handlers;
   }

   public BuildHelper disconnected(
      IClientAction_DynyaooE val
   ) {
      this.handlers.disconnected = val;
      return this;
   }

   public BuildHelper errored(
      IClientAction_BBV7RNmy val
   ) {
      this.handlers.errored = val;
      return this;
   }

   public BuildHelper ready(
      IClientAction_FvBRX2pb val
   ) {
      this.handlers.ready = val;
      return this;
   }

   public BuildHelper joinRequest(
      IClientAction_Wu2s8fhS val
   ) {
      this.handlers.joinRequest = val;
      return this;
   }

   public BuildHelper joinGame(
      IClientAction_VT005wLb val
   ) {
      this.handlers.joinGame = val;
      return this;
   }

   public BuildHelper spectateGame(
      IClientAction_RCeGZEVl val
   ) {
      this.handlers.spectateGame = val;
      return this;
   }
}
