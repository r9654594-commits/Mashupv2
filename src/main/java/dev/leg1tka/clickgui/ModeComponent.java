package dev.leg1tka.clickgui;
import dev.leg1tka.animation.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ModeComponent extends GuiComponent {
   private final MultiBooleanSetting setting;
   private final List<Animation> anims = new ArrayList<>();

   public ModeComponent(
      MultiBooleanSetting val
   ) {
      this.setting = val;

      for (int i = 0;
         i
            < val.getSettings()
               .size();
         i++
      ) {
         this.anims.add(new Animation(Easing.QUINTIC_OUT, 250L));
      }
   }

   private float optWidth(
      Font font,
      String str
   ) {
      return (
            font
                  == null
               ? (float)str.length()
                  * 5.5F
               : font.getStringWidth(
                  str
               )
         )
         + 9.0F;
   }

   private float calcHeight(
      Font font
   ) {
      float width2 = this.width
         - 8.0F;
      float width3 = 0.0F;
      float width4 = 16.0F;
      boolean flag = true;

      for (BooleanSetting val : this.setting
         .getSettings()) {
         if (val.visible()
            )
          {
            float width5 = this.optWidth(
               font,
               val.displayName()
            );
            if (!flag
               && width3
                     + width5
                  > width2
               )
             {
               width4 += 16.0F;
               width3 = width5
                  + 3.0F;
            } else {
               width3 += width5
                  + 3.0F;
               flag = false;
            }
         }
      }

      return width4
         + 13.0F;
   }

   @Override
   public boolean isVisible() {
      return this.setting.visible();
   }

   @Override
   public void render(
      MatrixStack matrices,
      int n,
      int n2,
      float f
   ) {
      float width2 = this.parent
               != null
            && this.parent.getPanel() != null
         ? this.parent.getPanel().getAnimationAlpha().getValue()
         : this.getAlphaAnim().getValue();
      float width3 = this.getAlphaAnimSetting()
         .getValue();
      float width4 = MathHelper.clamp(
         width2
            * width3,
         0.0F,
         1.0F
      );
      if (!(
         width4
            < 0.01F
      )) {
         int i = (int)(
            255.0F
               * width4
         );
         boolean flag = this.parent
               != null
            && this.parent.getModule().isEnable();
         Font font = Fonts.getFont(
            "bold", 14
         );
         if (font
            == null) {
            font = Fonts.getFont(
               "semibold", 14
            );
         }

         if (font
            == null) {
            font = Fonts.getFont(
               "sf_regular", 14
            );
         }

         Font font2 = Fonts.getFont(
            "bold", 13
         );
         if (font2
            == null) {
            font2 = font;
         }

         float width5 = this.calcHeight(
            font2
         );
         this.height = width5
            + 2.0F;
         long time = this.setting
            .getSettings()
            .stream()
            .filter(
               val -> val.visible()
                     && val.isState()
            )
            .count();
         long time2 = this.setting
            .getSettings()
            .stream()
            .filter(Setting::visible)
            .count();
         String name = time
            + "/"
            + time2;
         float width6 = font2
               == null
            ? 15.0F
            : font2.getStringWidth(
               name
            );
         float width7 = this.width
            - 8.0F
            - width6
            - 4.0F;
         drawTextScrolling(
            matrices,
            font,
            this.setting.displayName(),
            this.x + 4.0F,
            this.y + 2.0F,
            width7,
            ColorUtil.rgba(
               255,
               255,
               255,
               i
            )
         );
         if (font2
            != null) {
            drawText(
               matrices,
               font2,
               name,
               this.x
                  + this.width
                  - width6
                  - 4.0F,
               this.y + 2.0F,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  (int)(
                     180.0F
                        * width4
                  )
               )
            );
         }

         float width8 = this.x
            + 4.0F;
         float width9 = this.y
            + 15.0F;
         boolean flag2 = true;
         int i2 = 0;

         for (BooleanSetting val2 : this.setting
            .getSettings()) {
            if (!val2.visible()
               )
             {
               i2++;
            } else {
               float width10 = this.optWidth(
                  font2,
                  val2.displayName()
               );
               if (!flag2
                  && width8
                        + width10
                     > this.x + this.width - 4.0F) {
                  width8 = this.x
                     + 4.0F;
                  width9 += 16.0F;
               }

               flag2 = false;
               if (i2
                  < this.anims.size()) {
                  Animation val3 = this.anims
                     .get(
                        i2
                     );
                  val3.run(
                     val2.isState()
                  );
                  float width11 = val3.getValue();
                  boolean flag3 = HoverUtil.isHovered(
                     (double)n,
                     (double)n2,
                     (double)width8,
                     (double)width9,
                     (double)width10,
                     13.0
                  );
                  if (flag3
                     )
                   {
                     CursorManager.requestHand();
                  }

                  int i3 = flag
                     ? ColorUtil.rgba(
                        0,
                        0,
                        0,
                        (int)(
                           50.0F
                              * width4
                        )
                     )
                     : ColorUtil.rgba(
                        255,
                        255,
                        255,
                        (int)(
                           20.0F
                              * width4
                        )
                     );
                  int i4 = ColorUtil.rgba(
                     91,
                     104,
                     246,
                     i
                  );
                  int i5 = ColorUtil.interpolateColor(
                     i3,
                     i4,
                     width11
                  );
                  Render2D.drawRoundedRect(
                     matrices,
                     width8,
                     width9,
                     width10,
                     13.0F,
                     3.0F,
                     i5
                  );
                  if (font2
                     != null) {
                     float width12 = font2.getStringWidth(
                        val2.displayName()
                     );
                     drawText(
                        matrices,
                        font2,
                        val2.displayName(),
                        width8
                           + (
                                 width10
                                    - width12
                              )
                              / 2.0F,
                        width9
                           + 2.8F,
                        ColorUtil.rgba(
                           255,
                           255,
                           255,
                           i
                        )
                     );
                  }
               }

               width8 += width10
                  + 3.0F;
               i2++;
            }
         }
      }
   }

   @Override
   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
      Font font = Fonts.getFont(
         "bold", 13
      );
      if (font
         == null) {
         font = Fonts.getFont(
            "semibold", 13
         );
      }

      if (font
         == null) {
         font = Fonts.getFont(
            "sf_regular", 13
         );
      }

      float width2 = this.x
         + 4.0F;
      float width3 = this.y
         + 15.0F;
      boolean flag = true;

      for (BooleanSetting val : this.setting
         .getSettings()) {
         if (val.visible()
            )
          {
            float width4 = this.optWidth(
               font,
               val.displayName()
            );
            if (!flag
               && width2
                     + width4
                  > this.x + this.width - 4.0F) {
               width2 = this.x
                  + 4.0F;
               width3 += 16.0F;
            }

            flag = false;
            if (HoverUtil.isHovered(
                  d,
                  d2,
                  (double)width2,
                  (double)width3,
                  (double)width4,
                  13.0
               )
               && n
                  == 0) {
               val.setState(
                  !val.isState()
               );
               return;
            }

            width2 += width4
               + 3.0F;
         }
      }
   }
}
