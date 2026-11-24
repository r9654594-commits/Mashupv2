package dev.leg1tka.settings;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import java.util.function.Supplier;
import lombok.Generated;

public class KeybindSetting extends Setting {
   private int key;

   public KeybindSetting(
      String str,
      int n
   ) {
      super(
         str
      );
      this.key = n;
   }

   public KeybindSetting visible(
      Supplier<Boolean> val
   ) {
      this.visible = val;
      return this;
   }

   @Generated
   public int getKey() {
      return this.key;
   }

   @Generated
   public void setKey(
      int n
   ) {
      this.key = n;
   }
}
