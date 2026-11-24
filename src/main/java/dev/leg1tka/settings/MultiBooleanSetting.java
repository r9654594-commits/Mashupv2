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


import java.util.List;
import java.util.function.Supplier;
import lombok.Generated;

public class MultiBooleanSetting extends Setting {
   public List<BooleanSetting> settings;

   public MultiBooleanSetting(
      String str,
      BooleanSetting... val
   ) {
      super(
         str
      );
      this.settings = List.of(
         val
      );
   }

   public MultiBooleanSetting of(
      String str,
      BooleanSetting... val
   ) {
      return new MultiBooleanSetting(
         str,
         val
      );
   }

   public boolean is(
      String str
   ) {
      return this.requireSetting(
            str
         )
         .isState();
   }

   public void set(
      String str,
      boolean flag
   ) {
      this.requireSetting(
            str
         )
         .setState(
            flag
         );
   }

   public MultiBooleanSetting visible(
      Supplier<Boolean> val
   ) {
      this.visible = val;
      return this;
   }

   private BooleanSetting requireSetting(
      String str
   ) {
      for (BooleanSetting val : this.settings) {
         if (val.name()
            .equalsIgnoreCase(
               str
            )) {
            return val;
         }
      }

      throw new NullPointerException(
         "Unknown list setting entry: "
            + str
      );
   }

   @Generated
   public List<BooleanSetting> getSettings() {
      return this.settings;
   }

   @Generated
   public void setSettings(
      List<BooleanSetting> list
   ) {
      this.settings = list;
   }
}
