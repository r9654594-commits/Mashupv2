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

public class KeybindComponent extends GuiComponent {
   private final KeybindSetting setting;
   private boolean binding;

   public KeybindComponent(
      KeybindSetting val
   ) {
      this.setting = val;
   }

   private String getKeyText() {
      if (this.binding) {
         return "...";
      } else {
         return this.setting.getKey() == -1
            ? "None"
            : CallHelper.getBindName(this.setting.getKey());
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

         String name = this.getKeyText();
         float width5 = font2
               == null
            ? 18.0F
            : font2.getStringWidth(
               name
            );
         float width6 = Math.max(
            16.0F,
            width5
               + 8.0F
         );
         float width7 = 10.5F;
         float width8 = this.x
            + this.width
            - width6
            - 4.0F;
         float width9 = this.y
            + 2.0F;
         if (HoverUtil.isHovered(
            (double)n,
            (double)n2,
            (double)width8,
            (double)width9,
            (double)width6,
            (double)width7
         )) {
            CursorManager.requestHand();
         }

         float width10 = width8
            - (this.x + 4.0F)
            - 2.0F;
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
         boolean flag2 = this.setting
               .getKey()
            != -1;
         int color2;
         if (this.binding) {
            color2 = ColorUtil.rgba(
               91,
               104,
               246,
               (int)(
                  180.0F
                     * width4
               )
            );
         } else if (flag2
            )
          {
            color2 = ColorUtil.rgba(
               91,
               104,
               246,
               color
            );
         } else {
            color2 = flag
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
         }

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
                  + (
                        width6
                           - width5
                     )
                     / 2.0F,
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

         this.height = 17.0F;
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

      String text = this.getKeyText();
      float width2 = font
            == null
         ? 18.0F
         : font.getStringWidth(
            text
         );
      float width3 = Math.max(
         16.0F,
         width2
            + 8.0F
      );
      float width4 = 10.5F;
      float width5 = this.x
         + this.width
         - width3
         - 4.0F;
      float width6 = this.y
         + 2.0F;
      if (this.binding) {
         if (n
            != 0) {
            this.setting
               .setKey(
                  CallHelper.createMouseBind(
                     n
                  )
               );
         }

         this.binding = false;
      } else if (HoverUtil.isHovered(
            d,
            d2,
            (double)width5,
            (double)width6,
            (double)width3,
            (double)width4
         )
         && n
            == 0) {
         this.binding = true;
      }
   }

   @Override
   public void keyPressed(
      int n,
      int n2,
      int n3
   ) {
      if (this.binding) {
         if (n
               != 256
            && n
               != 261
            && n
               != 259) {
            this.setting
               .setKey(
                  n
               );
         } else {
            this.setting.setKey(-1);
         }

         this.binding = false;
      }
   }

   public boolean isBinding() {
      return this.binding;
   }
}
