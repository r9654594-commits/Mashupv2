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


public class SwingAnimations extends Module {
   public static SwingAnimations INSTANCE = new SwingAnimations();
   public boolean swimmingAnimation = true;
   public boolean climbAndCrawl = true;
   public boolean mb3DCompat = false;
   public final BooleanSetting hmiEnable = new BooleanSetting("Мод на красивые руки", false);
   public final ModeSetting hmiAnimationType;
   public final NumberSetting hmiSmoothness;
   public final BooleanSetting hmiPouchMode;
   public final BooleanSetting swingEnabled;
   public final ModeSetting swingType;
   public final NumberSetting swingStrength;
   public final NumberSetting corner;
   public final NumberSetting slant;
   public final BooleanSetting smoothEnabled;
   public final NumberSetting slowAnimationSpeed;
   public final BooleanSetting auraTargetOnly;
   public final BooleanSetting swapHands;
   public final BooleanSetting pouchMode;
   public final BooleanSetting eatAnim;

   public SwingAnimations() {
      super(
         "SwingAnimations",
         "Кастомная анимация аттаки",
         Category.RENDER
      );
      String var10003 = "Вид анимации";
      String var10004 = "Классик";
      String[] var10005 = new String[2];
      var10005[0] = "Классик";
      var10005[1] = "Шарп";
      this.hmiAnimationType = new ModeSetting(var10003, var10004, var10005).visible(this.hmiEnable::isState);
      this.hmiSmoothness = new NumberSetting("Плавность анимации", 1.0F, 0.35F, 2.5F, 0.05F)
         .visible(this.hmiEnable::isState);
      this.hmiPouchMode = new BooleanSetting("каст режим", false).visible(this.hmiEnable::isState);
      this.swingEnabled = new BooleanSetting("Анимация свинга", true)
         .visible(() -> !this.hmiEnable.isState());
      var10003 = "Тип свинга";
      var10004 = "Smooth";
      var10005 = new String[13];
      var10005[0] = "Smooth";
      var10005[1] = "Static";
      var10005[2] = "Down";
      var10005[3] = "DropDown";
      var10005[4] = "Poke";
      var10005[5] = "SelfBack";
      var10005[6] = "Feast";
      var10005[7] = "ToBack";
      var10005[8] = "Block";
      var10005[9] = "Akrien";
      var10005[10] = "Break";
      var10005[11] = "Pander";
      var10005[12] = "Slant";
      this.swingType = new ModeSetting(var10003, var10004, var10005).visible(() -> !this.hmiEnable.isState() && this.swingEnabled.isState());
      this.swingStrength = new NumberSetting("Сила анимации", 1.0F, 0.1F, 3.0F, 0.01F)
         .visible(
            () -> !this.hmiEnable.isState() && this.swingEnabled.isState() && !this.swingType.is("Pander")
         );
      this.corner = new NumberSetting("Угол DropDown", 12.0F, 1.0F, 360.0F, 1.0F)
         .visible(
            () -> !this.hmiEnable.isState() && this.swingEnabled.isState() && this.swingType.is("DropDown")
         );
      this.slant = new NumberSetting("Наклон DropDown", 12.0F, 1.0F, 360.0F, 1.0F)
         .visible(
            () -> !this.hmiEnable.isState() && this.swingEnabled.isState() && this.swingType.is("DropDown")
         );
      this.smoothEnabled = new BooleanSetting("Плавная анимация", false)
         .visible(() -> !this.hmiEnable.isState());
      this.slowAnimationSpeed = new NumberSetting("Скорость анимации", 12.0F, 1.0F, 50.0F, 1.0F)
         .visible(() -> !this.hmiEnable.isState() && this.smoothEnabled.isState());
      this.auraTargetOnly = new BooleanSetting("Только при Aura", false)
         .visible(() -> !this.hmiEnable.isState());
      this.swapHands = new BooleanSetting("Свап рук", false).visible(() -> !this.hmiEnable.isState());
      this.pouchMode = new BooleanSetting("каст режим", false)
         .visible(() -> !this.hmiEnable.isState() && this.swingEnabled.isState());
      this.eatAnim = new BooleanSetting("Анимация еды", false)
         .visible(() -> !this.hmiEnable.isState());
      this.addSettings(new Setting[]{
         this.hmiEnable,
         this.hmiAnimationType,
         this.hmiSmoothness,
         this.hmiPouchMode,
         this.swingEnabled,
         this.swingType,
         this.swingStrength,
         this.corner,
         this.slant,
         this.smoothEnabled,
         this.slowAnimationSpeed,
         this.auraTargetOnly,
         this.swapHands,
         this.pouchMode,
         this.eatAnim
      });
   }
}
