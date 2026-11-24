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


import dev.leg1tka.AspectDLC;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import lombok.Generated;

public class ModeSetting extends Setting {
   private List<String> mods;
   private String current;
   private int index;

   public ModeSetting(
      String str,
      String str2,
      String... val
   ) {
      super(
         str
      );
      this.mods = Arrays.asList(
         val
      );
      this.index = this.mods
         .indexOf(
            str2
         );
      if (this.index < 0) {
         this.index = 0;
      }

      this.current = this.mods.get(this.index);
   }

   public void set(
      String str
   ) {
      int x = this.mods
         .indexOf(
            str
         );
      if (x
         >= 0) {
         this.current = str;
         this.index = x;
      }
   }

   public boolean is(
      String str
   ) {
      return this.current
         .equals(
            str
         );
   }

   public String displayMode(
      String str
   ) {
      return AspectDLC.INSTANCE.localizationStorage == null
         ? str
         : AspectDLC.INSTANCE
            .localizationStorage
            .translate(
               str
            );
   }

   public String displayCurrent() {
      return this.displayMode(this.current);
   }

   public ModeSetting visible(
      Supplier<Boolean> val
   ) {
      this.visible = val;
      return this;
   }

   @Generated
   public List<String> getMods() {
      return this.mods;
   }

   @Generated
   public String getCurrent() {
      return this.current;
   }

   @Generated
   public int getIndex() {
      return this.index;
   }

   @Generated
   public void setMods(
      List<String> list
   ) {
      this.mods = list;
   }

   @Generated
   public void setCurrent(
      String str
   ) {
      this.current = str;
   }

   @Generated
   public void setIndex(
      int n
   ) {
      this.index = n;
   }
}
