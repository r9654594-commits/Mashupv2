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


import lombok.Generated;

public class ThemeColors implements IMinecraft {
   private String name;
   public int[] color;
   public int colorMain;
   public int colorVisualModules;
   public int colorText;
   public int colorInactiveText;
   public int colorHeaderBg;
   public int colorHeaderText;
   public int colorSlider;
   public int colorSliderCircle;
   public int colorSliderWindow;
   public int colorIndicator;
   public int colorInactiveIndicator;
   public int colorButton;
   public int colorInactiveButton;
   public int colorSeparator;
   public int colorField;
   public int colorInactiveField;
   public int colorTooltipText;
   public int colorWindowBg;
   public int colorIcons;
   public int colorClient;

   public ThemeColors(
      String str,
      int... val
   ) {
      this.name = str;
      this.color = val;
      int color2 = val.length
            > 0
         ? val[0]
            | -16777216
         : -5618433;
      int color3 = val.length
            > 1
         ? val[1]
            | -16777216
         : -15921904;
      this.applyDefaultsFromColors(
         color2,
         color3
      );
   }

   public void applyDefaultsFromColors(
      int n,
      int n2
   ) {
      this.colorMain = -15921904;
      this.colorVisualModules = n;
      this.colorText = -1513240;
      this.colorInactiveText = -7829351;
      this.colorHeaderBg = -16119283;
      this.colorHeaderText = n;
      this.colorSlider = n;
      this.colorSliderCircle = -1;
      this.colorSliderWindow = -14013899;
      this.colorIndicator = n;
      this.colorInactiveIndicator = -14013899;
      this.colorButton = n;
      this.colorInactiveButton = -14013899;
      this.colorSeparator = -14013899;
      this.colorField = -15066590;
      this.colorInactiveField = n2;
      this.colorTooltipText = -1;
      this.colorWindowBg = n2;
      this.colorIcons = n;
      this.colorClient = n;
   }

   public void setAllColors(
      int n,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6,
      int n7,
      int n8,
      int n9,
      int n10,
      int n11,
      int n12,
      int n13,
      int n14,
      int n15,
      int n16,
      int n17,
      int n18,
      int n19,
      int n20,
      int n21,
      int n22
   ) {
      if (this.color != null && this.color.length >= 2) {
         this.color[0] = n;
         this.color[1] = n2;
      } else {
         int[] var10001 = new int[2];
         var10001[0] = n;
         var10001[1] = n2;
         this.color = var10001;
      }

      this.colorMain = n3;
      this.colorVisualModules = n4;
      this.colorText = n5;
      this.colorInactiveText = n6;
      this.colorHeaderBg = n7;
      this.colorHeaderText = n8;
      this.colorSlider = n9;
      this.colorSliderCircle = n10;
      this.colorSliderWindow = n11;
      this.colorIndicator = n12;
      this.colorInactiveIndicator = n13;
      this.colorButton = n14;
      this.colorInactiveButton = n15;
      this.colorSeparator = n16;
      this.colorField = n17;
      this.colorInactiveField = n18;
      this.colorTooltipText = n19;
      this.colorWindowBg = n20;
      this.colorIcons = n21;
      this.colorClient = n22;
   }

   public int getColor(
      int n
   ) {
      return this.name.equals("Rainbow")
         ? ColorUtil.rainbow(
            10,
            n,
            0.6F,
            1.0F,
            1.0F
         )
         : ColorUtil.gradient(
            5,
            n,
            this.color
         );
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public int[] getColor() {
      return this.color;
   }

   @Generated
   public int getColorMain() {
      return this.colorMain;
   }

   @Generated
   public int getColorVisualModules() {
      return this.colorVisualModules;
   }

   @Generated
   public int getColorText() {
      return this.colorText;
   }

   @Generated
   public int getColorInactiveText() {
      return this.colorInactiveText;
   }

   @Generated
   public int getColorHeaderBg() {
      return this.colorHeaderBg;
   }

   @Generated
   public int getColorHeaderText() {
      return this.colorHeaderText;
   }

   @Generated
   public int getColorSlider() {
      return this.colorSlider;
   }

   @Generated
   public int getColorSliderCircle() {
      return this.colorSliderCircle;
   }

   @Generated
   public int getColorSliderWindow() {
      return this.colorSliderWindow;
   }

   @Generated
   public int getColorIndicator() {
      return this.colorIndicator;
   }

   @Generated
   public int getColorInactiveIndicator() {
      return this.colorInactiveIndicator;
   }

   @Generated
   public int getColorButton() {
      return this.colorButton;
   }

   @Generated
   public int getColorInactiveButton() {
      return this.colorInactiveButton;
   }

   @Generated
   public int getColorSeparator() {
      return this.colorSeparator;
   }

   @Generated
   public int getColorField() {
      return this.colorField;
   }

   @Generated
   public int getColorInactiveField() {
      return this.colorInactiveField;
   }

   @Generated
   public int getColorTooltipText() {
      return this.colorTooltipText;
   }

   @Generated
   public int getColorWindowBg() {
      return this.colorWindowBg;
   }

   @Generated
   public int getColorIcons() {
      return this.colorIcons;
   }

   @Generated
   public int getColorClient() {
      return this.colorClient;
   }

   @Generated
   public void setName(
      String str
   ) {
      this.name = str;
   }

   @Generated
   public void setColor(
      int[] array
   ) {
      this.color = array;
   }

   @Generated
   public void setColorMain(
      int n
   ) {
      this.colorMain = n;
   }

   @Generated
   public void setColorVisualModules(
      int n
   ) {
      this.colorVisualModules = n;
   }

   @Generated
   public void setColorText(
      int n
   ) {
      this.colorText = n;
   }

   @Generated
   public void setColorInactiveText(
      int n
   ) {
      this.colorInactiveText = n;
   }

   @Generated
   public void setColorHeaderBg(
      int n
   ) {
      this.colorHeaderBg = n;
   }

   @Generated
   public void setColorHeaderText(
      int n
   ) {
      this.colorHeaderText = n;
   }

   @Generated
   public void setColorSlider(
      int n
   ) {
      this.colorSlider = n;
   }

   @Generated
   public void setColorSliderCircle(
      int n
   ) {
      this.colorSliderCircle = n;
   }

   @Generated
   public void setColorSliderWindow(
      int n
   ) {
      this.colorSliderWindow = n;
   }

   @Generated
   public void setColorIndicator(
      int n
   ) {
      this.colorIndicator = n;
   }

   @Generated
   public void setColorInactiveIndicator(
      int n
   ) {
      this.colorInactiveIndicator = n;
   }

   @Generated
   public void setColorButton(
      int n
   ) {
      this.colorButton = n;
   }

   @Generated
   public void setColorInactiveButton(
      int n
   ) {
      this.colorInactiveButton = n;
   }

   @Generated
   public void setColorSeparator(
      int n
   ) {
      this.colorSeparator = n;
   }

   @Generated
   public void setColorField(
      int n
   ) {
      this.colorField = n;
   }

   @Generated
   public void setColorInactiveField(
      int n
   ) {
      this.colorInactiveField = n;
   }

   @Generated
   public void setColorTooltipText(
      int n
   ) {
      this.colorTooltipText = n;
   }

   @Generated
   public void setColorWindowBg(
      int n
   ) {
      this.colorWindowBg = n;
   }

   @Generated
   public void setColorIcons(
      int n
   ) {
      this.colorIcons = n;
   }

   @Generated
   public void setColorClient(
      int n
   ) {
      this.colorClient = n;
   }
}
