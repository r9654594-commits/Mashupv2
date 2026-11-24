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


import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class MultiBooleanComponent extends GuiComponent {
   private final ListSetting setting;
   private final Map<Theme, Animation> checkanims = new HashMap<>();

   public MultiBooleanComponent(
      ListSetting val
   ) {
      this.setting = val;

      for (Theme val2 : val.getThemes()) {
         this.checkanims
            .put(
               val2,
               new Animation(Easing.QUINTIC_OUT, 350L)
            );
      }
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
         float width5 = this.x
            + 2.0F;
         float width6 = width5;
         float width7 = this.y
            + 4.0F;
         float width8 = this.width
            - 4.0F;

         for (Theme val : this.setting
            .getThemes()) {
            if (width6
                  + 10.0F
               > width5
                  + width8
               )
             {
               width6 = width5;
               width7 += 12.0F;
            }

            Animation val2 = this.checkanims
               .computeIfAbsent(
                  val,
                  val3 -> new Animation(
                        Easing.QUINTIC_OUT, 350L
                     )
               );
            val2.run(
               this.setting.getValue()
                  == val
            );
            if (HoverUtil.isHovered(
               (double)n,
               (double)n2,
               (double)(
                  width6
                     - 1.0F
               ),
               (double)(
                  width7
                     - 1.0F
               ),
               10.0,
               10.0
            )) {
               CursorManager.requestHand();
            }

            if (val2.getValue()
               > 0.01F) {
               float width9 = val2.getValue()
                  * width4;
               Render2D.drawRoundedRect(
                  matrices,
                  width6
                     - 1.5F,
                  width7
                     - 1.5F,
                  11.0F,
                  11.0F,
                  5.5F,
                  ColorUtil.rgba(
                     255,
                     255,
                     255,
                     (int)(
                        255.0F
                           * width9
                     )
                  )
               );
            }

            this.drawTheme(
               matrices,
               val,
               width6,
               width7,
               width4,
               val2.getValue()
            );
            width6 += 12.0F;
         }

         this.height = width7
            - this.y
            + 16.0F;
      }
   }

   private void drawTheme(
      MatrixStack matrices,
      Theme val,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      ThemeColors color2 = val.getTheme();
      boolean flag = "Rainbow"
         .equalsIgnoreCase(
            color2.getName()
         );
      int i = (int)(
         255.0F
            * f3
      );
      if (flag
         )
       {
         int[] colors = new int[4];

         for (int i2 = 0;
            i2
               < 4;
            i2++
         ) {
            colors[i2] = Color.HSBtoRGB(
               (float)i2
                  / 4.0F,
               0.8F,
               1.0F
            );
         }

         Render2D.drawGradientRect(
            matrices,
            f,
            f2,
            8.0F,
            8.0F,
            4.0F,
            ColorUtil.setAlphaColor(
               colors[0],
               i
            ),
            ColorUtil.setAlphaColor(
               colors[1],
               i
            ),
            ColorUtil.setAlphaColor(
               colors[2],
               i
            ),
            ColorUtil.setAlphaColor(
               colors[3],
               i
            )
         );
      } else {
         int i3 = color2.color.length
               > 0
            ? color2.color[0]
            : ColorUtil.getThemeColor();
         int i4 = color2.color.length
               > 1
            ? color2.color[1]
            : i3;
         int i5 = ColorUtil.setAlphaColor(
            i3,
            i
         );
         int i6 = ColorUtil.setAlphaColor(
            i4,
            i
         );
         Render2D.drawGradientRect(
            matrices,
            f,
            f2,
            8.0F,
            8.0F,
            4.0F,
            i5,
            i6,
            i5,
            i6
         );
      }

      if (f4
         > 0.01F) {
         float alpha = 3.0F
            * f4;
         float alpha2 = f
            + (
                  8.0F
                     - alpha
               )
               / 2.0F;
         float alpha3 = f2
            + (
                  8.0F
                     - alpha
               )
               / 2.0F;
         Render2D.drawRoundedRect(
            matrices,
            alpha2,
            alpha3,
            alpha,
            alpha,
            alpha
               / 2.0F,
            ColorUtil.rgba(
               255,
               255,
               255,
               (int)(
                  255.0F
                     * f3
                     * f4
               )
            )
         );
      }
   }

   @Override
   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
      if (n
         == 0) {
         float width2 = this.x
            + 2.0F;
         float width3 = width2;
         float width4 = this.y
            + 4.0F;
         float width5 = this.width
            - 4.0F;

         for (Theme val : this.setting
            .getThemes()) {
            if (width3
                  + 10.0F
               > width2
                  + width5
               )
             {
               width3 = width2;
               width4 += 12.0F;
            }

            if (HoverUtil.isHovered(
               d,
               d2,
               (double)(
                  width3
                     - 1.0F
               ),
               (double)(
                  width4
                     - 1.0F
               ),
               10.0,
               10.0
            )) {
               this.setting
                  .setValue(
                     val
                  );
               return;
            }

            width3 += 12.0F;
         }
      }
   }
}
