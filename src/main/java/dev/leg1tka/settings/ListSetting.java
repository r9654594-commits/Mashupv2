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
import java.util.List;
import java.util.function.Supplier;

public class ListSetting extends Setting {
   public ListSetting(
      String str
   ) {
      super(
         str
      );
   }

   public List<Theme> getThemes() {
      return (List<Theme>)(AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null
         ? AspectDLC.INSTANCE.themeStorage.getThemeList()
         : List.of());
   }

   public Theme getValue() {
      return AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null ? AspectDLC.INSTANCE.themeStorage.getThemes() : null;
   }

   public void setValue(
      Theme val
   ) {
      if (AspectDLC.INSTANCE != null
         && AspectDLC.INSTANCE.themeStorage != null
         && val
            != null) {
         AspectDLC.INSTANCE
            .themeStorage
            .setThemes(
               val
            );
         if (val.getTheme()
                  .color
               != null
            && val.getTheme()
                  .color.length
               > 0) {
            int color2 = val.getTheme()
               .color[0];
            int color3 = val.getTheme()
                     .color.length
                  > 1
               ? val.getTheme()
                  .color[1]
               : -15921904;
            val.getTheme()
               .applyDefaultsFromColors(
                  color2,
                  color3
               );
         }

         try {
            if (AspectDLC.INSTANCE.configStorage != null) {
               AspectDLC.INSTANCE.configStorage.saveGlobals();
            }
         } catch (Exception var4) {
         }
      }
   }

   public ListSetting visible(
      Supplier<Boolean> val
   ) {
      this.visible = val;
      return this;
   }
}
