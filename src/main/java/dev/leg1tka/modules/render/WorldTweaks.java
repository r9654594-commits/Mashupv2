package dev.leg1tka.modules.render;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import dev.leg1tka.AspectDLC;

public class WorldTweaks extends Module {
   public static WorldTweaks INSTANCE = new WorldTweaks();
   private final MultiBooleanSetting worldSettings;
   private final NumberSetting timeSetting;
   private final NumberSetting fogDistanceSetting;
   private final ModeSetting shaderMode;
   private final NumberSetting shaderOpacity;
   private final NumberSetting auroraSpeed;
   private final NumberSetting auroraIntensity;
   private final NumberSetting auroraScale;

   public WorldTweaks() {
      super("CustomWorld", "Настройки мира", Category.RENDER);
      this.worldSettings = new MultiBooleanSetting("Настройки мира", new BooleanSetting[]{
         new BooleanSetting("Время", true),
         new BooleanSetting("Фог", true),
         new BooleanSetting("Шейдер", false),
         new BooleanSetting("Северное сияние", false)
      });
      this.timeSetting = new NumberSetting("Время", 12.0F, 0.0F, 24.0F, 1.0F)
         .visible(() -> this.worldSettings.is("Время"));
      this.fogDistanceSetting = new NumberSetting("Дистанция фога", 100.0F, 20.0F, 200.0F, 1.0F)
         .visible(() -> this.worldSettings.is("Фог"));
      this.shaderMode = new ModeSetting("Тип шейдера", "Nebula", new String[]{
         "Nebula", "Caustic", "Space", "Summer", "Plasma", "Balatro"
      }).visible(() -> this.worldSettings.is("Шейдер"));
      this.shaderOpacity = new NumberSetting("Прозрачность шейдера", 70.0F, 0.0F, 100.0F, 5.0F)
         .visible(() -> this.worldSettings.is("Шейдер"));
      this.auroraSpeed = new NumberSetting("Скорость сияния", 0.8F, 0.1F, 3.0F, 0.1F)
         .visible(() -> this.worldSettings.is("Северное сияние"));
      this.auroraIntensity = new NumberSetting("Яркость сияния", 1.0F, 0.1F, 2.0F, 0.1F)
         .visible(() -> this.worldSettings.is("Северное сияние"));
      this.auroraScale = new NumberSetting("Масштаб сияния", 1.5F, 0.5F, 3.0F, 0.1F)
         .visible(() -> this.worldSettings.is("Северное сияние"));
      this.addSettings(new Setting[]{
         this.worldSettings,
         this.timeSetting,
         this.fogDistanceSetting,
         this.shaderMode,
         this.shaderOpacity,
         this.auroraSpeed,
         this.auroraIntensity,
         this.auroraScale
      });
   }

   public boolean isTimeEnabled() {
      return this.isEnable() && this.worldSettings.is("Время");
   }

   public boolean isFogEnabled() {
      return this.isEnable() && this.worldSettings.is("Фог");
   }

   public long getForcedTime() {
      return (long)this.timeSetting.get() * 1000L;
   }

   public float getFogDistance() {
      return this.fogDistanceSetting.get();
   }

   public int getFogColor() {
      return !AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtil.getThemeColor();
   }

   public boolean isShaderEnabled() {
      return this.isEnable() && this.worldSettings.is("Шейдер");
   }

   public boolean isCausticShader() {
      return this.shaderMode.is("Caustic");
   }

   public String getShaderMode() {
      return this.shaderMode.getCurrent();
   }

   public float getShaderOpacity() {
      return this.shaderOpacity.get() / 100.0F;
   }

   public int getShaderColor() {
      return !AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtil.getThemeColor();
   }

   public boolean isAuroraEnabled() {
      return this.isEnable() && this.worldSettings.is("Северное сияние");
   }

   public float getAuroraSpeed() {
      return this.auroraSpeed.get();
   }

   public float getAuroraIntensity() {
      return this.auroraIntensity.get();
   }

   public float getAuroraScale() {
      return this.auroraScale.get();
   }

   public int getAuroraColor1() {
      return !AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtil.getThemeColor(0);
   }

   public int getAuroraColor2() {
      return !AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtil.getThemeColor(120);
   }
}
