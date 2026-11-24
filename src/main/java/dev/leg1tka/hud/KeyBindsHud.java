package dev.leg1tka.hud;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.util.math.MatrixStack;

public class KeyBindsHud extends HudElement {
   private final Map<Module, UpdateHelper> animations = new HashMap<>();
   private final UpdateHelper widthAnimation = new UpdateHelper(60.0F, 10.5F, EasingFunctions.QUAD_OUT);
   private static final Map<Character, Character> RU_TO_EN = new HashMap<>();

   public KeyBindsHud(
      Draggable val
   ) {
      super(
         val
      );
   }

   private Font issue(
      int n
   ) {
      return Fonts.getFont(
         "nunito",
         n
      );
   }

   private Font icon(
      int n
   ) {
      return Fonts.getFont(
         "icon1",
         n
      );
   }

   private Font icons(
      int n
   ) {
      return Fonts.getFont(
         "icon",
         n
      );
   }

   private UpdateHelper getAnimation(
      Module val
   ) {
      return this.animations
         .computeIfAbsent(
            val,
            val2 -> new UpdateHelper(
                  0.0F, 10.5F, EasingFunctions.QUAD_OUT
               )
         );
   }

   private String toEnglish(
      String str
   ) {
      StringBuilder val = new StringBuilder();

      for (char val2 : str.toCharArray()) {
         val.append(
            RU_TO_EN.getOrDefault(
               val2,
               val2
            )
         );
      }

      return val.toString();
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      if (mc != null && mc.player != null) {
         this.DefaultStyle(
            event
         );
         super.onRender(
            event
         );
      }
   }

   public void DefaultStyle(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      String var10000 = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName();
      int i;
      if (!var10000.equals("Rainbow")) {
         i = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme()
            .color[0];
      } else {
         i = ColorUtil.getThemeColor();
      }

      float width3 = 64.0F;
      float width4 = 16.0F;
      int i2 = 0;
      ObjectListIterator var8 = ModuleManager.INSTANCE.getObject().iterator();

      while (var8.hasNext()) {
         Module val = (Module)var8.next();
         if (val.getKey()
               != -1
            && val.getKey()
               != 0) {
            UpdateHelper val2 = this.getAnimation(
               val
            );
            val2.update(
               val.isEnable()
                  ? 1.0F
                  : 0.0F
            );
         }
      }

      var8 = ModuleManager.INSTANCE.getObject().iterator();

      while (var8.hasNext()) {
         Module val3 = (Module)var8.next();
         if (val3.getKey()
               != -1
            && val3.getKey()
               != 0) {
            UpdateHelper val4 = this.getAnimation(
               val3
            );
            float width5 = val4.getValue();
            if (width5
               > 0.01F) {
               i2++;
               String name = this.toEnglish(
                  CallHelper.getKeyName(
                     val3.getKey()
                  )
               );
               float width6 = this.issue(
                     10
                  )
                  .getWidth(
                     name
                  );
               float width7 = this.issue(
                        12
                     )
                     .getWidth(
                        val3.getDisplayName()
                     )
                  + width6
                  + 20.0F;
               if (width7
                  > width3
                  )
                {
                  width3 = width7;
               }

               width4 += 12.0F
                  * width5;
            }
         }
      }

      if (i2
         > 0) {
         width4 += 2.0F;
      }

      this.widthAnimation
         .update(
            width3
         );
      float width8 = this.widthAnimation
            .getValue()
         + 6.0F;
      float width9 = width4;
      this.drawHudBackground(
         event.getContext()
            .getMatrices(),
         width,
         width2,
         width8,
         width4,
         i
      );
      this.icon(16)
         .draw(
            event.getContext()
               .getMatrices(),
            "f",
            width
               + 5.0F,
            width2
               + 6.5F,
            i
         );
      Font var30 = this.issue(14);
      MatrixStack var31 = event.getContext()
         .getMatrices();
      var30.draw(
         var31,
         "Keybinds",
         width
            + 5.0F
            + this.icon(16)
               .getWidth(
                  "f"
               )
            + 3.0F,
         width2
            + 6.0F,
         -1
      );
      float width10 = 18.0F;
      ObjectListIterator var26 = ModuleManager.INSTANCE.getObject().iterator();

      while (var26.hasNext()) {
         Module val5 = (Module)var26.next();
         if (val5.getKey()
               != -1
            && val5.getKey()
               != 0) {
            UpdateHelper val6 = this.getAnimation(
               val5
            );
            float width11 = val6.getValue();
            if (width11
               > 0.01F) {
               ScissorStack.push();
               ScissorStack.setFromComponentCoordinates(
                  (double)width,
                  (double)width2,
                  (double)width8,
                  (double)width9
               );
               String name2 = this.toEnglish(
                  CallHelper.getKeyName(
                     val5.getKey()
                  )
               );
               int i3 = (int)(
                  255.0F
                     * width11
               );
               int i4 = ColorUtil.rgba(
                  255,
                  255,
                  255,
                  i3
               );
               int i5 = ColorUtil.setAlphaColor(
                  i,
                  i3
               );
               this.issue(12)
                  .draw(
                     event.getContext()
                        .getMatrices(),
                     val5.getDisplayName(),
                     width
                        + 5.5F,
                     width2
                        + 2.0F
                        + width10,
                     i4
                  );
               float width12 = width
                  + width8
                  - this.issue(10)
                     .getWidth(
                        name2
                     )
                  - 7.0F;
               this.issue(10)
                  .draw(
                     event.getContext()
                        .getMatrices(),
                     name2,
                     width12,
                     width2
                        + width10
                        + 2.8F,
                     i5
                  );
               width10 += 12.0F
                  * width11;
               ScissorStack.pop();
               ScissorStack.unset();
            }
         }
      }

      this.draggable
         .setWidth(
            width8
         );
      this.draggable
         .setHeight(
            width9
         );
   }

   public void WaveStyle(
      EventRender.Default event
   ) {
      MatrixStack matrices = event.getContext()
         .getMatrices();
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      int i = (int)(
         (float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F
      );
      int i2 = ColorUtil.getThemeColor(
         i
      );
      int i3 = ColorUtil.getThemeColor(
         i
            + 30
      );
      int i4 = ColorUtil.getThemeColor(
         i
            + 90
      );
      int i5 = ColorUtil.getThemeColor(
         i
            + 120
      );
      int i6 = ColorUtil.getThemeColor(
         i
            + 180
      );
      int i7 = ColorUtil.getThemeColor(
         i
            + 210
      );
      List<Module> list = new ArrayList<>();
      ObjectListIterator var13 = ModuleManager.INSTANCE.getObject().iterator();

      while (var13.hasNext()) {
         Module val = (Module)var13.next();
         if (val.getKey()
               > 0
            && val.getKey()
               != 0) {
            val.getAnimka()
               .update(
                  val.isEnable()
                     ? 1.0F
                     : 0.0F
               );
            if (val.getAnimka()
                  .getValue()
               > 0.01F) {
               list.add(
                  val
               );
            }
         } else {
            val.getAnimka()
               .update(0.0F);
         }
      }

      float width3 = 84.0F;
      float width4 = 18.0F;
      int i8 = 0;

      for (Module val2 : list) {
         float width5 = val2.getAnimka()
            .getValue();
         if (!(
            width5
               <= 0.01F
         )) {
            i8++;
            String name = val2.getDisplayName()
                  .toLowerCase()
               + " >> toggle";
            width3 = Math.max(
               width3,
               this.issue(14)
                     .getWidth(
                        name
                     )
                  + 7.0F
            );
            width4 += 12.0F
               * width5;
         }
      }

      this.widthAnimation
         .update(
            width3
         );
      float width6 = this.widthAnimation
         .getValue();
      if (i8
         == 0) {
         float width7 = 18.0F;
         Render2D.drawWaveHudHeader(
            matrices,
            width,
            width2,
            width6,
            15.0F,
            0.0F,
            10.0F,
            10.0F,
            i2,
            i3,
            i4,
            i5,
            i6,
            i7
         );
         String name2 = "keybinds";
         float width8 = width
            + (
                  width6
                     - this.issue(15)
                        .getWidth(
                           name2
                        )
               )
               / 2.0F;
         this.issue(15)
            .drawStringWithShadow(
               event.getContext()
                  .getMatrices(),
               name2,
               width8,
               width2
                  + 5.0F,
               -1
            );
         this.draggable
            .setWidth(
               width6
            );
         this.draggable
            .setHeight(
               width7
            );
      } else {
         Render2D.drawWaveHudPanel(
            matrices,
            width,
            width2,
            width6,
            width4,
            ColorUtil.rgba(
               25,
               25,
               25,
               150
            ),
            15.0F,
            0.0F,
            10.0F,
            10.0F,
            i2,
            i3,
            i4,
            i5,
            i6,
            i7
         );
         String name3 = "keybinds";
         float width9 = width
            + (
                  width6
                     - this.issue(15)
                        .getWidth(
                           name3
                        )
               )
               / 2.0F;
         this.issue(15)
            .drawStringWithShadow(
               event.getContext()
                  .getMatrices(),
               name3,
               width9,
               width2
                  + 5.0F,
               -1
            );
         float width10 = 18.0F;

         for (Module val3 : list) {
            float width11 = val3.getAnimka()
               .getValue();
            if (!(
               width11
                  <= 0.01F
            )) {
               ScissorStack.push();
               ScissorStack.setFromComponentCoordinates(
                  (double)width,
                  (double)width2,
                  (double)width6,
                  (double)width4
               );
               int i9 = (int)(
                  255.0F
                     * width11
               );
               int i10 = ColorUtil.rgba(
                  255,
                  255,
                  255,
                  i9
               );
               int i11 = ColorUtil.getThemeColor();
               Render2D.drawRoundedRect(
                  event.getContext()
                     .getMatrices(),
                  width
                     + 5.0F,
                  width2
                     + width10
                     + 2.0F,
                  2.5F,
                  8.0F,
                  0.5F,
                  i11
               );
               String var37 = val3.getDisplayName()
                  .toLowerCase();
               String var39;
               if (val3.isEnable()
                  )
                {
                  var39 = "on";
               } else {
                  var39 = "off";
               }

               String name4 = var37
                  + " >> "
                  + var39;
               float width12 = width
                  + 11.0F;
               this.issue(14)
                  .draw(
                     matrices,
                     name4,
                     width12,
                     width2
                        + width10
                        + 2.0F,
                     i10
                  );
               width10 += 12.0F
                  * width11;
               ScissorStack.unset();
               ScissorStack.pop();
            }
         }

         this.draggable
            .setWidth(
               width6
            );
         this.draggable
            .setHeight(
               width4
            );
      }
   }

   static {
      String str = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
      String str2 = "qwertyuiop[]asdfghjkl;'zxcvbnm,.QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,.";
      int i = Math.min(
         str.length(),
         str2.length()
      );

      for (int i2 = 0;
         i2
            < i;
         i2++
      ) {
         RU_TO_EN.put(
            str.charAt(
               i2
            ),
            str2.charAt(
               i2
            )
         );
      }
   }
}
