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

public class BooleanComponent extends GuiComponent {
   private final BooleanSetting setting;
   private final Animation anim = new Animation(Easing.QUINTIC_OUT, 250L);
   private boolean binding;

   public BooleanComponent(
      BooleanSetting val
   ) {
      this.setting = val;
      this.anim
         .setValue(
            val.isState()
               ? 1.0F
               : 0.0F
         );
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
         float width5 = 15.0F;
         float width6 = 8.5F;
         float width7 = this.x
            + this.width
            - width5
            - 4.0F;
         float width8 = this.y
            + 4.0F;
         float width9 = this.x
            + 4.0F;
         float width10 = this.y
            + 4.0F;
         boolean flag = HoverUtil.isHovered(
            (double)n,
            (double)n2,
            (double)this.x,
            (double)this.y,
            (double)this.width,
            17.0
         );
         if (flag
            )
          {
            CursorManager.requestHand();
         }

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

         float width11 = width7
            - width9
            - 3.0F;
         drawTextScrolling(
            matrices,
            font,
            this.binding ? "Binding..." : this.setting.displayName(),
            width9,
            width10
               + 0.5F,
            width11,
            ColorUtil.rgba(
               255,
               255,
               255,
               color
            )
         );
         this.anim.run(this.setting.isState());
         float width12 = this.anim
            .getValue();
         boolean flag2 = this.parent
               != null
            && this.parent.getModule().isEnable();
         int color2 = flag2
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
         int color3 = ColorUtil.rgba(
            91,
            104,
            246,
            color
         );
         int color4 = ColorUtil.interpolateColor(
            color2,
            color3,
            width12
         );
         Render2D.drawRoundedRect(
            matrices,
            width7,
            width8,
            width5,
            width6,
            4.0F,
            color4
         );
         float width13 = 3.2F;
         float width14 = width7
            + 1.0F;
         float width15 = width7
            + width5
            - width13
               * 2.0F
            - 1.0F;
         float width16 = width14
            + (
                  width15
                     - width14
               )
               * width12;
         float width17 = width8
            + 1.0F;
         Render2D.drawRoundedRect(
            matrices,
            width16,
            width17,
            width13
               * 2.0F,
            width13
               * 2.0F,
            width13,
            ColorUtil.rgba(
               255,
               255,
               255,
               color
            )
         );
         this.height = 17.0F;
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
         17.0
      )) {
         if (n
            == 0) {
            this.setting.setState(!this.setting.isState());
         }

         if (n
               == 2
            && this.binding) {
            this.binding = false;
            return;
         }

         if (this.binding) {
            this.setting
               .setKey(
                  n
               );
            this.binding = false;
         }

         if (n
            == 2) {
            this.binding = true;
         }
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
