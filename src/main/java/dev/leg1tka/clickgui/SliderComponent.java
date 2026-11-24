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
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SliderComponent extends GuiComponent {
   private final NumberSetting setting;
   private boolean drag;
   private final Animation anim = new Animation(Easing.QUINTIC_OUT, 120L);

   public SliderComponent(
      NumberSetting val
   ) {
      this.setting = val;
   }

   private int getPrecision() {
      float x = this.setting
         .getIncrement();
      if (x
         <= 0.0F) {
         return 2;
      } else {
         String str = BigDecimal.valueOf(
               (double)x
            )
            .stripTrailingZeros()
            .toPlainString();
         int x2 = str.indexOf(
            46
         );
         return x2
               < 0
            ? 0
            : Math.max(
               0,
               Math.min(
                  str.length()
                     - x2
                     - 1,
                  4
               )
            );
      }
   }

   private double round(
      double d
   ) {
      double x = (double)this.setting
         .getIncrement();
      if (x
         <= 0.0) {
         return d;
      } else {
         double x2 = (double)Math.round(
               d
                  / x
            )
            * x;
         int x3 = Math.max(
            this.getPrecision(), 4
         );
         return new BigDecimal(
               Double.toString(
                  x2
               )
            )
            .setScale(
               x3,
               RoundingMode.HALF_UP
            )
            .doubleValue();
      }
   }

   private String formatNumber(
      double d
   ) {
      if (!Double.isNaN(
            d
         )
         && !Double.isInfinite(
            d
         )) {
         int n = this.getPrecision();
         return n
               == 0
            ? String.valueOf(
               Math.round(
                  d
               )
            )
            : BigDecimal.valueOf(
                  d
               )
               .setScale(
                  n,
                  RoundingMode.HALF_UP
               )
               .stripTrailingZeros()
               .toPlainString();
      } else {
         return "0";
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
         int color = (int)(
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

         String name = this.formatNumber(
            (double)this.setting.get()
         );
         float width5 = font2
               == null
            ? 13.0F
            : font2.getStringWidth(
               name
            );
         float width6 = width5
            + 8.0F;
         float width7 = 10.5F;
         float width8 = this.x
            + this.width
            - width6
            - 4.0F;
         float width9 = this.y
            + 2.0F;
         int color2 = ColorUtil.rgba(
            91,
            104,
            246,
            color
         );
         Render2D.drawRoundedRect(
            matrices,
            width8,
            width9,
            width6,
            width7,
            2.5F,
            color2
         );
         if (font2
            != null) {
            drawText(
               matrices,
               font2,
               name,
               width8
                  + 4.0F,
               width9
                  + 1.8F,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  color
               )
            );
         }

         float width10 = width8
            - (this.x + 4.0F)
            - 3.0F;
         drawTextScrolling(
            matrices,
            font,
            this.setting.displayName(),
            this.x + 4.0F,
            this.y + 2.0F,
            width10,
            ColorUtil.rgba(
               255,
               255,
               255,
               color
            )
         );
         float width11 = this.x
            + 4.0F;
         float width12 = this.width
            - 8.0F;
         float width13 = this.y
            + 14.5F;
         float width14 = 3.2F;
         boolean flag2 = HoverUtil.isHovered(
            (double)n,
            (double)n2,
            (double)this.x,
            (double)this.y,
            (double)this.width,
            21.0
         );
         if (flag2
            || this.drag) {
            CursorManager.requestHand();
         }

         if (this.drag) {
            double x2 = (double)(
               this.setting.getMax() - this.setting.getMin()
            );
            double x3 = (double)this.setting
                  .getMin()
               + (double)MathHelper.clamp(
                     (
                           (float)n
                              - width11
                        )
                        / width12,
                     0.0F,
                     1.0F
                  )
                  * x2;
            this.setting
               .setValue(
                  (float)this.round(
                     x3
                  )
               );
         }

         this.anim
            .run(
               width12
                  * (this.setting.get() - this.setting.getMin())
                  / (this.setting.getMax() - this.setting.getMin())
            );
         float width15 = MathHelper.clamp(
            this.anim.getValue(),
            0.0F,
            width12
         );
         int color3 = flag
            ? ColorUtil.rgba(
               0,
               0,
               0,
               (int)(
                  60.0F
                     * width4
               )
            )
            : ColorUtil.rgba(
               255,
               255,
               255,
               (int)(
                  25.0F
                     * width4
               )
            );
         int color4 = ColorUtil.rgba(
            91,
            104,
            246,
            color
         );
         Render2D.drawRoundedRect(
            matrices,
            width11,
            width13,
            width12,
            width14,
            1.5F,
            color3
         );
         if (width15
            > 0.0F) {
            Render2D.drawRoundedRect(
               matrices,
               width11,
               width13,
               width15,
               width14,
               1.5F,
               color4
            );
         }

         float width16 = 3.5F;
         float width17 = width11
            + width15;
         float width18 = width13
            + width14
               / 2.0F;
         Render2D.drawRoundedRect(
            matrices,
            width17
               - width16,
            width18
               - width16,
            width16
               * 2.0F,
            width16
               * 2.0F,
            width16,
            ColorUtil.rgba(
               255,
               255,
               255,
               color
            )
         );
         this.height = 21.0F;
      }
   }

   @Override
   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
      if (HoverUtil.isHovered(
            d,
            d2,
            (double)this.x,
            (double)this.y,
            (double)this.width,
            21.0
         )
         && n
            == 0) {
         this.drag = true;
      }
   }

   @Override
   public void mouseReleased(
      double d,
      double d2,
      int n
   ) {
      this.drag = false;
   }
}
