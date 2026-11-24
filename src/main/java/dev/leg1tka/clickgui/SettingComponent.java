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

public class SettingComponent extends GuiComponent {
   private final ModuleComponent owner;
   private final Animation hanim = new Animation(Easing.QUINTIC_OUT, 300L);

   public SettingComponent(
      ModuleComponent val
   ) {
      this.owner = val;
   }

   @Override
   public void render(
      MatrixStack matrices,
      int n,
      int n2,
      float f
   ) {
      float width2 = this.owner
         .getPanel()
         .getAnimationAlpha()
         .getValue();
      if (!(
         width2
            < 0.01F
      )) {
         float width3 = this.x
            + 2.0F;
         float width4 = this.y
            + 1.0F;
         float width5 = this.width
            - 4.0F;
         float width6 = 14.0F;
         boolean flag = HoverUtil.isHovered(
            (double)n,
            (double)n2,
            (double)width3,
            (double)width4,
            (double)width5,
            (double)width6
         );
         this.hanim
            .run(
               flag
            );
         if (flag
            )
          {
            CursorManager.requestHand();
         }

         int color = (int)(
            255.0F
               * width2
         );
         int color2 = ColorUtil.rgba(
            255,
            255,
            255,
            (int)(
               30.0F
                  * width2
            )
         );
         Render2D.drawRoundedRect(
            matrices,
            width3,
            width4,
            width5,
            width6,
            3.0F,
            color2
         );
         Font font = Fonts.getFont(
            "semibold",
            13
         );
         if (font
            == null) {
            font = Fonts.getFont(
               "medium",
               13
            );
         }

         GuiComponent.drawText(
            matrices,
            font,
            "Открыть менеджер тем",
            width3
               + 4.0F,
            width4
               + 4.0F,
            ColorUtil.rgba(
               255,
               255,
               255,
               color
            )
         );
         this.height = 16.0F;
      }
   }

   @Override
   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
      float width2 = this.x
         + 2.0F;
      float width3 = this.y
         + 1.0F;
      float width4 = this.width
         - 4.0F;
      float width5 = 14.0F;
      if (HoverUtil.isHovered(
            d,
            d2,
            (double)width2,
            (double)width3,
            (double)width4,
            (double)width5
         )
         && n
            == 0) {
         ApplycolorsHelper color = this.owner
            .getPanel()
            .getParent()
            .getThemeManager();
         color.setOpen(
            !color.isOpen()
         );
      }
   }

   @Override
   public boolean isVisible() {
      return true;
   }

   @Override
   public float getHeight() {
      return 16.0F;
   }
}
