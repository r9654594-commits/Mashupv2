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
import net.minecraft.client.util.math.MatrixStack;

public class SetboundsHelper {
   private float x;
   private float y;
   private float width;
   private float height;
   public String text = "";
   private boolean isFocused;
   private final String placeholder;
   private static final float ICON_BOX_W = 17.0F;
   private static final float RADIUS = 4.0F;

   public SetboundsHelper(
      String str
   ) {
      this.placeholder = str;
   }

   public void setBounds(
      float f,
      float f2,
      float f3,
      float f4
   ) {
      this.x = f;
      this.y = f2;
      this.width = f3;
      this.height = f4;
   }

   public void render(
      MatrixStack matrices,
      int n,
      int n2,
      float f
   ) {
      if (HoverUtil.isHovered(
         (double)n,
         (double)n2,
         (double)this.x,
         (double)this.y,
         (double)this.width,
         (double)this.height
      )) {
         CursorManager.requestIBeam();
      }

      Render2D.drawBlur(
         matrices,
         this.x,
         this.y,
         this.width,
         this.height,
         4.0F,
         20.0F,
         ColorUtil.rgba(
            75,
            75,
            75,
            255
         )
      );
      Render2D.drawRoundedRect(
         matrices,
         this.x,
         this.y,
         this.width,
         this.height,
         4.0F,
         ColorUtil.setAlphaColor(ColorUtil.getColorWindowBg(), 30)
      );
      Render2D.drawRoundedRect(
         matrices,
         this.x,
         this.y,
         17.0F,
         this.height,
         4.0F,
         4.0F,
         4.0F,
         4.0F,
         ColorUtil.setAlphaColor(ColorUtil.getColorHeaderBg(), 80)
      );
      Font font = Fonts.getFont(
         "iconsminced",
         16
      );
      if (font
         != null) {
         float width2 = font.getStringWidth(
            "g"
         );
         float width3 = this.x
            + (
                  17.0F
                     - width2
               )
               / 2.0F;
         float width4 = this.y
            + this.height / 2.0F
            - 3.5F;
         GuiComponent.drawText(
            matrices,
            font,
            "g",
            width3,
            width4,
            ColorUtil.setAlphaColor(ColorUtil.getThemeColor(), 220)
         );
      }

      Font font2 = Fonts.getFont(
         "sf_regular",
         14
      );
      String text2 = this.text
               .isEmpty()
            && !this.isFocused
         ? this.placeholder
         : this.text;
      String text3 = this.isFocused
            && System.currentTimeMillis() % 1000L > 500L
         ? "|"
         : "";
      int color = this.text
               .isEmpty()
            && !this.isFocused
         ? ColorUtil.setAlphaColor(ColorUtil.getColorInactiveText(), 255)
         : ColorUtil.setAlphaColor(ColorUtil.getColorText(), 255);
      GuiComponent.drawText(
         matrices,
         font2,
         text2
            + text3,
         this.x + 17.0F + 4.0F,
         this.y + this.height / 2.0F - 3.5F,
         color
      );
   }

   public void charTyped(
      char val,
      int n
   ) {
      if (this.isFocused && this.text.length() < 15) {
         this.text = this.text
            + val;
      }
   }

   public void keyPressed(
      int n,
      int n2,
      int n3
   ) {
      if (this.isFocused) {
         if (n
               == 259
            && !this.text.isEmpty()) {
            this.text = this.text.substring(0, this.text.length() - 1);
         }

         if (n
               == 257
            || n
               == 256) {
            this.isFocused = false;
         }
      }
   }

   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
      this.isFocused = HoverUtil.isHovered(
         d,
         d2,
         (double)this.x,
         (double)this.y,
         (double)this.width,
         (double)this.height
      );
   }

   public boolean isEmpty() {
      return this.text.isEmpty();
   }

   public String getText() {
      return this.text;
   }
}
