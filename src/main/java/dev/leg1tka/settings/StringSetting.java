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

public class StringSetting extends Setting {
   private String text;
   private final int maxLength;

   public StringSetting(
      String str,
      String str2
   ) {
      this(
         str,
         str2,
         32
      );
   }

   public StringSetting(
      String str,
      String str2,
      int n
   ) {
      super(
         str
      );
      this.maxLength = Math.max(
         1,
         n
      );
      this.setText(
         str2
      );
   }

   public void setText(
      String str
   ) {
      if (str
         == null) {
         this.text = "";
      } else {
         StringBuilder val = new StringBuilder();

         for (int i = 0;
            i
                  < str.length()
               && val.length()
                  < this.maxLength;
            i++
         ) {
            char val2 = str.charAt(
               i
            );
            if (!Character.isISOControl(
               val2
            )) {
               val.append(
                  val2
               );
            }
         }

         this.text = val.toString();
      }
   }

   public String get() {
      return this.text;
   }

   public StringSetting visible(
      Supplier<Boolean> val
   ) {
      this.visible = val;
      return this;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public int getMaxLength() {
      return this.maxLength;
   }
}
