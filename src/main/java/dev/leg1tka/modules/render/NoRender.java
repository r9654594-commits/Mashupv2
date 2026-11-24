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


public class NoRender extends Module {
   public static NoRender INSTANCE = new NoRender();
   private final MultiBooleanSetting elm;

   public NoRender() {
      super("NoRender", Category.RENDER);
      String var1 = "NoRender";
      String var2 = "Элементы";
      BooleanSetting[] var10004 = new BooleanSetting[10];
      var10004[0] = new BooleanSetting("Огонь", false);
      var10004[1] = new BooleanSetting("Плохие эффекты", false);
      int var10006 = 2;
      var10004[var10006] = new BooleanSetting(
         "Оверлей в блоке", false
      );
      var10006 = 3;
      var10004[var10006] = new BooleanSetting(
         "Частицы", false
      );
      var10006 = 4;
      var10004[var10006] = new BooleanSetting(
         "Погода", false
      );
      var10006 = 5;
      var10004[var10006] = new BooleanSetting(
         "Облака", false
      );
      var10006 = 6;
      var10004[var10006] = new BooleanSetting(
         "Блок-сущности", false
      );
      var10006 = 7;
      var10004[var10006] = new BooleanSetting(
         "Тени", false
      );
      var10006 = 8;
      var10004[var10006] = new BooleanSetting(
         "Анимацию тотема", false
      );
      var10006 = 9;
      var10004[var10006] = new BooleanSetting(
         "Тряска", false
      );
      this.elm = new MultiBooleanSetting(var2, var10004);
      this.addSettings(new Setting[]{this.elm});
   }

   public boolean isEnabled(
      String val
   ) {
      return this.isEnable()
         && this.elm
            .is(
               val
            );
   }

   public boolean isTotemAnimationDisabled() {
      return this.isEnabled("Анимацию тотема");
   }
}
