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

public class SpeedHud extends HudElement {
   public SpeedHud(
      Draggable val
   ) {
      super(
         val
      );
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      this.DefaultStyle(
         event
      );
      super.onRender(
         event
      );
   }

   public void DefaultStyle(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      Font font = Fonts.getFont(
         "nunito", 13
      );
      Font font2 = Fonts.getFont(
         "icon",
         16
      );
      String var23 = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName();
      int color2;
      if (!var23.equals("Rainbow")) {
         color2 = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme()
            .color[0];
      } else {
         color2 = ColorUtil.getThemeColor();
      }

      HudRectStyle style = this.getHudRectStyle();
      int color3 = (int)Math.floor(
         mc.player.getX()
      );
      int color4 = (int)Math.floor(
         mc.player.getY()
      );
      int color5 = (int)Math.floor(
         mc.player.getZ()
      );
      float width3 = 24.0F;
      double x2 = (double)DirectionHelper.calculateBPS();
      String name = "Bps: "
         + this.formatTwoDecimals(
            x2
         );
      String name2 = "Xyz: "
         + color3
         + " "
         + color4
         + " "
         + color5;
      float width4 = Math.max(
         82.0F,
         Math.max(
            12.0F
               + font2.getWidth(
                  "c"
               )
               + 3.0F
               + font.getWidth(
                  name
               ),
            12.0F
               + font2.getWidth(
                  "x"
               )
               + 3.0F
               + font.getWidth(
                  name2
               )
         )
      );
      this.drawHudBackground(
         event.getContext()
            .getMatrices(),
         width,
         width2,
         width4,
         width3,
         color2
      );
      float width5 = width2
         + 6.0F;
      float width6 = width2
         + 13.5F;
      int color6 = (int)(
         (float)(System.currentTimeMillis() % 2400L) / 2400.0F * 360.0F
      );
      int color7 = ColorUtil.getThemeColor(
         color6
      );
      int color8 = ColorUtil.getThemeColor(
         color6
            + 140
      );
      font2.draw(
         event.getContext()
            .getMatrices(),
         "c",
         width
            + 3.25F,
         width5,
         color7
      );
      font2.draw(
         event.getContext()
            .getMatrices(),
         "x",
         width
            + 3.25F,
         width6,
         color8
      );
      font.drawGradientStringHorizontal(
         event.getContext()
            .getMatrices(),
         name,
         width
            + 13.5F,
         width5,
         color7,
         color8
      );
      font.drawGradientStringHorizontal(
         event.getContext()
            .getMatrices(),
         name2,
         width
            + 13.5F,
         width6,
         color8,
         color7
      );
      this.draggable
         .setHeight(
            width3
         );
      this.draggable
         .setWidth(
            width4
         );
   }

   public void WaveStyle(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      float width3 = (float)(
            System.currentTimeMillis() % 2000L
         )
         / 2000.0F
         * 360.0F;
      int color = ColorUtil.getThemeColor(
         (int)width3
      );
      int color2 = ColorUtil.getThemeColor(
         (int)(
            width3
               + 30.0F
         )
      );
      int color3 = ColorUtil.getThemeColor(
         (int)(
            width3
               + 90.0F
         )
      );
      int color4 = ColorUtil.getThemeColor(
         (int)(
            width3
               + 120.0F
         )
      );
      int color5 = ColorUtil.getThemeColor(
         (int)(
            width3
               + 180.0F
         )
      );
      int color6 = ColorUtil.getThemeColor(
         (int)(
            width3
               + 210.0F
         )
      );
      String text = "info";
      String text2 = "x: "
         + (int)mc.player.getPos().getX();
      String text3 = "y: "
         + (int)mc.player.getPos().getY();
      String text4 = "z: "
         + (int)mc.player.getPos().getZ();
      String text5 = "bps: "
         + (int)DirectionHelper.calculateBPS();
      Font font = Fonts.getFont(
         "nunito",
         15
      );
      float width4 = font.getWidth(
         text2
      );
      float width5 = font.getWidth(
         text3
      );
      float width6 = font.getWidth(
         text4
      );
      float width7 = font.getWidth(
         text5
      );
      float width8 = font.getWidth(
         text
      );
      float width9 = Math.max(
         Math.max(
            width4,
            Math.max(
               width5,
               width6
            )
         ),
         width7
      );
      float width10 = 9.0F;
      float width11 = width9
         + width10;
      float width12 = 48.0F;
      width11 = Math.max(
         width11,
         35.0F
      );
      float width13 = width
         + width11
            / 2.0F;
      Render2D.drawWaveHudPanel(
         event.getContext()
            .getMatrices(),
         width,
         width2,
         width11,
         width12,
         ColorUtil.rgba(
            25,
            25,
            25,
            150
         ),
         3.5F,
         0.0F,
         10.0F,
         10.0F,
         color,
         color2,
         color3,
         color4,
         color5,
         color6
      );
      float width14 = 5.0F;
      Render2D.drawWaveHudHeader(
         event.getContext()
            .getMatrices(),
         width
            + width14,
         width2
            + 12.0F,
         width11
            - width14
               * 2.0F,
         2.5F,
         0.0F,
         10.0F,
         10.0F,
         color,
         color2,
         color3,
         color4,
         color5,
         color6
      );
      font.drawStringWithShadow(
         event.getContext()
            .getMatrices(),
         text,
         width13
            - width8
               / 2.0F,
         width2
            + 5.0F,
         -1
      );
      font.drawStringWithShadow(
         event.getContext()
            .getMatrices(),
         text2,
         width
            + 4.5F,
         width2
            + 17.0F,
         -1
      );
      font.drawStringWithShadow(
         event.getContext()
            .getMatrices(),
         text3,
         width
            + 4.5F,
         width2
            + 24.0F,
         -1
      );
      font.drawStringWithShadow(
         event.getContext()
            .getMatrices(),
         text4,
         width
            + 4.5F,
         width2
            + 31.0F,
         -1
      );
      font.drawStringWithShadow(
         event.getContext()
            .getMatrices(),
         text5,
         width
            + 4.5F,
         width2
            + 38.0F,
         -1
      );
      this.draggable
         .setWidth(
            width11
         );
      this.draggable
         .setHeight(
            width12
         );
   }

   private String formatTwoDecimals(
      double d
   ) {
      int n = (int)Math.round(
         d
            * 100.0
      );
      int n2 = Math.abs(
         n
            % 100
      );
      return n
            / 100
         + "."
         + (
            n2
                  < 10
               ? "0"
               : ""
         )
         + n2;
   }
}
