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
import net.minecraft.util.math.MathHelper;

public class TextBoxComponent extends GuiComponent {
   private final StringSetting setting;
   private boolean editing;

   public TextBoxComponent(
      StringSetting val
   ) {
      this.setting = val;
      this.height = 17.0F;
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

         float width5 = 56.0F;
         float width6 = this.x
            + this.width
            - width5
            - 4.5F;
         if (HoverUtil.isHovered(
            (double)n,
            (double)n2,
            (double)width6,
            (double)(this.y + 3.0F),
            (double)width5,
            11.0
         )) {
            CursorManager.requestIBeam();
         }

         drawText(
            matrices,
            font,
            this.setting.displayName(),
            this.x + 4.5F,
            this.y + 3.0F,
            ColorUtil.rgba(
               255,
               255,
               255,
               color
            )
         );
         int color2 = this.editing
            ? ColorUtil.rgba(
               40,
               40,
               48,
               color
            )
            : ColorUtil.rgba(
               20,
               20,
               25,
               color
            );
         Render2D.drawRoundedRect(
            matrices,
            width6,
            this.y + 3.0F,
            width5,
            11.0F,
            2.5F,
            color2
         );
         if (font2
            != null) {
            String name = this.setting
               .get();
            String var21;
            if (name.isEmpty()
               )
             {
               var21 = "...";
            } else {
               var21 = name;
            }

            String name2 = var21;
            if (this.editing && System.currentTimeMillis() / 500L % 2L == 0L) {
               name2 = name2
                  + "|";
            }

            int color3 = name.isEmpty()
               ? ColorUtil.rgba(
                  140,
                  140,
                  150,
                  color
               )
               : ColorUtil.rgba(
                  255,
                  255,
                  255,
                  color
               );
            ScissorStack.push();
            ScissorStack.setFromComponentCoordinates(
               (double)(
                  width6
                     + 3.0F
               ),
               (double)(this.y + 3.0F),
               (double)(
                  width5
                     - 6.0F
               ),
               11.0
            );
            drawText(
               matrices,
               font2,
               name2,
               width6
                  + 3.0F,
               this.y + 3.0F,
               color3
            );
            ScissorStack.pop();
         }
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
         this.editing = HoverUtil.isHovered(
            d,
            d2,
            (double)this.x,
            (double)this.y,
            (double)this.width,
            (double)this.height
         );
      }
   }

   @Override
   public void keyPressed(
      int n,
      int n2,
      int n3
   ) {
      if (this.editing) {
         if (n
               == 256
            || n
               == 257
            || n
               == 335) {
            this.editing = false;
         } else if (n
               == 259
            && !this.setting.get().isEmpty()) {
            this.setting.setText(this.setting.get().substring(0, this.setting.get().length() - 1));
         }
      }
   }

   @Override
   public void charTyped(
      char val,
      int n
   ) {
      if (this.editing
         && !Character.isISOControl(
            val
         )) {
         this.setting
            .setText(
               this.setting.get()
                  + val
            );
      }
   }
}
