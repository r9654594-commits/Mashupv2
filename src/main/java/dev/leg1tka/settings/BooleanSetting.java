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

public class BooleanSetting extends Setting {
   private boolean state;
   private int key = -1;

   public BooleanSetting(
      String str,
      boolean flag
   ) {
      super(
         str
      );
      this.state = flag;
   }

   public static BooleanSetting of(
      String str,
      boolean flag
   ) {
      return new BooleanSetting(
         str,
         flag
      );
   }

   public BooleanSetting visible(
      Supplier<Boolean> val
   ) {
      this.visible = val;
      return this;
   }

   @Generated
   public boolean isState() {
      return this.state;
   }

   @Generated
   public int getKey() {
      return this.key;
   }

   @Generated
   public void setState(
      boolean flag
   ) {
      this.state = flag;
   }

   @Generated
   public void setKey(
      int n
   ) {
      this.key = n;
   }
}
