package dev.leg1tka.render;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import lombok.Generated;

public class ThemeStorage {
   private ObjectArrayList<Theme> themeList = new ObjectArrayList();
   private Theme themes;

   public ThemeStorage() {
      this.onInitialize();
   }

   private void onInitialize() {
      ObjectArrayList var10000 = this.themeList;
      Theme[] var10001 = new Theme[36];
      var10001[0] = Theme.Custom;
      var10001[1] = Theme.Purple;
      var10001[2] = Theme.Red;
      var10001[3] = Theme.Blue;
      var10001[4] = Theme.Green;
      var10001[5] = Theme.Pink;
      var10001[6] = Theme.Orange;
      var10001[7] = Theme.Blues;
      var10001[8] = Theme.Yellows;
      var10001[9] = Theme.Cyan;
      var10001[10] = Theme.Magenta;
      var10001[11] = Theme.Steel;
      var10001[12] = Theme.Midnight;
      var10001[13] = Theme.Mint;
      var10001[14] = Theme.Coral;
      var10001[15] = Theme.Amethyst;
      var10001[16] = Theme.Sunset;
      var10001[17] = Theme.Cherry;
      var10001[18] = Theme.Teal;
      var10001[19] = Theme.Lime;
      var10001[20] = Theme.Wine;
      var10001[21] = Theme.Crimson;
      var10001[22] = Theme.Navy;
      var10001[23] = Theme.Amber;
      var10001[24] = Theme.Violet;
      var10001[25] = Theme.Forest;
      var10001[26] = Theme.Ruby;
      var10001[27] = Theme.Sapphire;
      var10001[28] = Theme.Pearl;
      var10001[29] = Theme.Ember;
      var10001[30] = Theme.Arctic;
      var10001[31] = Theme.Neon;
      var10001[32] = Theme.Shadow;
      var10001[33] = Theme.Gold;
      var10001[34] = Theme.Silver;
      var10001[35] = Theme.Bronze;
      var10000.addAll(Arrays.asList(var10001));
      this.themes = (Theme)this.themeList.get(1);
   }

   @Generated
   public ObjectArrayList<Theme> getThemeList() {
      return this.themeList;
   }

   @Generated
   public Theme getThemes() {
      return this.themes;
   }

   @Generated
   public void setThemeList(
      ObjectArrayList<Theme> list
   ) {
      this.themeList = list;
   }

   @Generated
   public void setThemes(
      Theme val
   ) {
      this.themes = val;
   }
}
