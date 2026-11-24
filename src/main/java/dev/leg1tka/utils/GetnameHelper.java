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


import lombok.Generated;

public class GetnameHelper {
   private String name;
   private String command;
   private KeybindSetting bind;

   @Generated
   public GetnameHelper(
      String str,
      String str2,
      KeybindSetting val
   ) {
      this.name = str;
      this.command = str2;
      this.bind = val;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getCommand() {
      return this.command;
   }

   @Generated
   public KeybindSetting getBind() {
      return this.bind;
   }
}
