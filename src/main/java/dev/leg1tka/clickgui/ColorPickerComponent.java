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
import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ColorPickerComponent extends GuiComponent {
   private final IntegerSetting setting;
   private boolean opened;
   private final Animation anim = new Animation(Easing.QUINTIC_OUT, 300L);
   private boolean dragsv;
   private boolean draghue;
   private float hue;
   private float sat;
   private float val;

   public ColorPickerComponent(
      IntegerSetting val2
   ) {
      this.setting = val2;
      this.update();
   }

   private void update() {
      Color color = new Color(
         this.setting.getValue(), true
      );
      float[] floats = Color.RGBtoHSB(
         color.getRed(),
         color.getGreen(),
         color.getBlue(),
         null
      );
      this.hue = floats[0];
      this.sat = floats[1];
      this.val = floats[2];
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
         this.anim.run(this.opened);
         float width5 = 55.0F;
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

         float width6 = 8.0F;
         float width7 = this.x
            + this.width
            - width6
            - 5.0F;
         float width8 = this.y
            + 2.5F;
         float width9 = width7
            - (this.x + 4.5F)
            - 2.0F;
         drawTextScrolling(
            matrices,
            font,
            this.setting.displayName(),
            this.x + 4.5F,
            this.y + 2.5F,
            width9,
            ColorUtil.rgba(
               255,
               255,
               255,
               i
            )
         );
         if (HoverUtil.isHovered(
            (double)n,
            (double)n2,
            (double)width7,
            (double)width8,
            (double)width6,
            (double)width6
         )) {
            CursorManager.requestHand();
         }

         Render2D.drawRoundedRect(
            matrices,
            width7
               - 0.5F,
            width8
               - 0.5F,
            width6
               + 1.0F,
            width6
               + 1.0F,
            1.5F,
            ColorUtil.rgba(
               60,
               60,
               65,
               (int)(
                  150.0F
                     * width4
               )
            )
         );
         Render2D.drawRoundedRect(
            matrices,
            width7,
            width8,
            width6,
            width6,
            1.5F,
            this.setting.getValue()
         );
         if (this.anim.getValue() > 0.01F) {
            float width10 = this.y
               + 13.0F;
            float width11 = this.anim
                  .getValue()
               * width5;
            float width12 = width4
               * this.anim.getValue();
            int i2 = (int)(
               255.0F
                  * width12
            );
            ScissorStack.push();
            ScissorStack.setFromComponentCoordinates(
               (double)this.x,
               (double)width10,
               (double)this.width,
               (double)width11
            );
            Render2D.drawRoundedRect(
               matrices,
               this.x + 4.5F,
               width10
                  - 0.5F,
               this.width - 9.0F,
               width5
                  + 1.0F,
               2.5F,
               ColorUtil.rgba(
                  60,
                  60,
                  65,
                  (int)(
                     150.0F
                        * width12
                  )
               )
            );
            Render2D.drawRoundedRect(
               matrices,
               this.x + 5.0F,
               width10,
               this.width - 10.0F,
               width5,
               2.5F,
               ColorUtil.rgba(
                  20,
                  20,
                  25,
                  i2
               )
            );
            float width13 = this.x
               + 9.0F;
            float width14 = width10
               + 4.0F;
            float width15 = 40.0F;
            int i3 = ColorUtil.setAlphaColor(
               Color.HSBtoRGB(this.hue, 1.0F, 1.0F),
               i2
            );
            int i4 = ColorUtil.rgba(
               255,
               255,
               255,
               i2
            );
            int i5 = ColorUtil.rgba(
               255,
               255,
               255,
               0
            );
            int i6 = ColorUtil.rgba(
               0,
               0,
               0,
               i2
            );
            int i7 = ColorUtil.rgba(
               0, 0, 0, 0
            );
            Render2D.drawRoundedRect(
               matrices,
               width13,
               width14,
               width15,
               width15,
               2.0F,
               i3
            );
            Render2D.drawGradientRect(
               matrices,
               width13,
               width14,
               width15,
               width15,
               2.0F,
               i4,
               i5,
               i4,
               i5
            );
            Render2D.drawGradientRect(
               matrices,
               width13,
               width14,
               width15,
               width15,
               2.0F,
               i7,
               i7,
               i6,
               i6
            );
            float width16 = width13
               + this.sat
                  * width15;
            float width17 = width14
               + (1.0F - this.val)
                  * width15;
            Render2D.drawRoundedRect(
               matrices,
               width16
                  - 2.5F,
               width17
                  - 2.5F,
               5.0F,
               5.0F,
               2.5F,
               ColorUtil.rgba(
                  0,
                  0,
                  0,
                  (int)(
                     180.0F
                        * width12
                  )
               )
            );
            Render2D.drawRoundedRect(
               matrices,
               width16
                  - 1.5F,
               width17
                  - 1.5F,
               3.0F,
               3.0F,
               1.5F,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  i2
               )
            );
            float width18 = width13
               + width15
               + 6.0F;
            float width19 = width14;
            float width20 = 6.0F;

            for (float width21 = 0.0F;
               width21
                  <= width15;
               width21 += 0.5F
            ) {
               int i8 = ColorUtil.setAlphaColor(
                  Color.HSBtoRGB(
                     width21
                        / width15,
                     1.0F,
                     1.0F
                  ),
                  i2
               );
               Render2D.drawRoundedRect(
                  matrices,
                  width18,
                  width19
                     + width21,
                  width20,
                  1.0F,
                  0.0F,
                  i8
               );
            }

            float width22 = width19
               + this.hue
                  * width15;
            Render2D.drawRoundedRect(
               matrices,
               width18
                  - 1.5F,
               width22
                  - 2.5F,
               width20
                  + 3.0F,
               5.0F,
               2.0F,
               ColorUtil.rgba(
                  0,
                  0,
                  0,
                  (int)(
                     180.0F
                        * width12
                  )
               )
            );
            Render2D.drawRoundedRect(
               matrices,
               width18
                  - 0.5F,
               width22
                  - 1.5F,
               width20
                  + 1.0F,
               3.0F,
               1.0F,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  i2
               )
            );
            if (this.dragsv) {
               this.sat = Math.max(
                  0.0F,
                  Math.min(
                     1.0F,
                     (
                           (float)n
                              - width13
                        )
                        / width15
                  )
               );
               this.val = 1.0F
                  - Math.max(
                     0.0F,
                     Math.min(
                        1.0F,
                        (
                              (float)n2
                                 - width14
                           )
                           / width15
                     )
                  );
               this.sync();
            } else if (this.draghue) {
               this.hue = Math.max(
                  0.0F,
                  Math.min(
                     1.0F,
                     (
                           (float)n2
                              - width19
                        )
                        / width15
                  )
               );
               this.sync();
            }

            ScissorStack.pop();
         }

         this.height = 13.0F
            + this.anim.getValue()
               * width5;
      }
   }

   private void sync() {
      int color = Color.HSBtoRGB(
         this.hue, this.sat, this.val
      );
      this.setting
         .setValue(
            color
               | -16777216
         );
   }

   @Override
   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
      float width2 = 8.0F;
      float width3 = this.x
         + this.width
         - width2
         - 5.0F;
      float width4 = this.y
         + 2.5F;
      if (HoverUtil.isHovered(
            d,
            d2,
            (double)width3,
            (double)width4,
            (double)width2,
            (double)width2
         )
         && n
            == 1) {
         this.opened = !this.opened;
      } else {
         if (this.opened
            && n
               == 0) {
            float width5 = this.x
               + 9.0F;
            float width6 = this.y
               + 13.0F
               + 4.0F;
            float width7 = 40.0F;
            float width8 = width5
               + width7
               + 6.0F;
            if (HoverUtil.isHovered(
               d,
               d2,
               (double)width5,
               (double)width6,
               (double)width7,
               (double)width7
            )) {
               this.dragsv = true;
            } else if (HoverUtil.isHovered(
               d,
               d2,
               (double)(
                  width8
                     - 2.0F
               ),
               (double)width6,
               10.0,
               (double)width7
            )) {
               this.draghue = true;
            }
         }
      }
   }

   @Override
   public void mouseReleased(
      double d,
      double d2,
      int n
   ) {
      this.dragsv = false;
      this.draghue = false;
   }
}
