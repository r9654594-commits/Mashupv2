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
import java.awt.Color;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;

public class TpsHud extends HudElement {
   private static final int TEXT_WHITE = new Color(255, 255, 255, 255).getRGB();
   private static final int TEXT_DARK_GRAY = new Color(130, 130, 130, 255).getRGB();
   private static final float PAD_X = 2.5F;
   private static final float PAD_Y = 2.5F;
   private static final float GAP_Y = 0.5F;
   private static final float ROW_H = 10.5F;

   public TpsHud(
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
      MatrixStack matrices = event.getContext()
         .getMatrices();
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      Font font = Fonts.getFont(
         "nunito", 14
      );
      Font font2 = Fonts.getFont(
         "nunito", 12
      );
      Font font3 = Fonts.getFont(
         "iconnew", 13
      );
      if (font
            != null
         && font2
            != null) {
         int color = (int)(
            (float)(System.currentTimeMillis() % 4000L) / 4000.0F * 360.0F
         );
         int color2 = ColorUtil.getThemeColor(
            color
         );
         int color3 = ColorUtil.getThemeColor(
            color
               + 120
         );
         int color4 = getAccent();
         String text = "AspectDLC";
         String text2 = "d";
         String text3 = "1337";
         String text4 = "o";
         int color5 = mc
               != null
            ? mc.getCurrentFps()
            : 0;
         String text5 = color5
            + " Fps";
         String text6 = "p";
         int color6 = 0;
         if (mc != null && mc.player != null && mc.getNetworkHandler() != null) {
            PlayerListEntry val = mc.getNetworkHandler()
               .getPlayerListEntry(mc.player.getUuid());
            if (val
               != null) {
               color6 = val.getLatency();
            }
         }

         String text7 = color6
            + "ms";
         String text8 = "y";
         float width3 = getTps();
         String text9 = formatOneDecimal(
               width3
            )
            + " TPS";
         String text10 = "b";
         String text11 = "Singleplayer";
         if (mc != null) {
            ServerInfo val2 = mc.getCurrentServerEntry();
            if (val2
                  != null
               && val2.address
                  != null
               && !val2.address
                  .isEmpty()) {
               text11 = val2.address;
            }
         }

         String text12 = "m";
         String text13 = "•";
         float width4 = font2.getStringWidth(
            text13
         );
         float width5 = 1.5F;
         float width6 = width5
               * 2.0F
            + width4;
         float width7 = 0.0F;
         if (font3
            != null) {
            width7 += font3.getStringWidth(
                  text2
               )
               + 1.5F;
         }

         width7 += font.getStringWidth(
            text
         );
         width7 += width6;
         if (font3
            != null) {
            width7 += font3.getStringWidth(
                  text4
               )
               + 1.5F;
         }

         width7 += font2.getStringWidth(
            text3
         );
         width7 += width6;
         if (font3
            != null) {
            width7 += font3.getStringWidth(
                  text6
               )
               + 1.5F;
         }

         width7 += font2.getStringWidth(
            text5
         );
         width7 += width6;
         if (font3
            != null) {
            width7 += font3.getStringWidth(
                  text8
               )
               + 1.5F;
         }

         width7 += font2.getStringWidth(
            text7
         );
         float width8 = 0.0F;
         if (font3
            != null) {
            width8 += font3.getStringWidth(
                  text10
               )
               + 1.5F;
         }

         width8 += font2.getStringWidth(
            text9
         );
         width8 += width6;
         if (font3
            != null) {
            width8 += font3.getStringWidth(
                  text12
               )
               + 1.5F;
         }

         width8 += font2.getStringWidth(
            text11
         );
         float width9 = 5.0F
            + width7;
         float width10 = 15.5F;
         float width11 = 5.0F
            + width8;
         float width12 = 15.5F;
         float width13 = 2.5F;
         float width14 = width2
            + width10
            + width13;
         this.drawHudBackground(
            matrices,
            width,
            width2,
            width9,
            width10,
            color4
         );
         this.drawHudBackground(
            matrices,
            width,
            width14,
            width11,
            width12,
            color4
         );
         float width15 = width
            + 2.5F;
         float width16 = width2
            + 2.5F
            + 3.5F;
         float width17 = width2
            + 2.5F
            + 5.0F;
         if (font3
            != null) {
            font3.drawString(
               matrices,
               text2,
               width15,
               width17,
               color4
            );
            width15 += font3.getStringWidth(
                  text2
               )
               + 1.5F;
         }

         font.drawGradientStringHorizontal(
            matrices,
            text,
            width15,
            width16
               - 1.0F,
            color2,
            color3
         );
         width15 += font.getStringWidth(
            text
         );
         width15 += width5;
         font2.drawString(
            matrices,
            text13,
            width15,
            width16,
            TEXT_DARK_GRAY
         );
         width15 += width4
            + width5;
         if (font3
            != null) {
            font3.drawString(
               matrices,
               text4,
               width15,
               width17,
               color4
            );
            width15 += font3.getStringWidth(
                  text4
               )
               + 1.5F;
         }

         font2.drawString(
            matrices,
            text3,
            width15,
            width16,
            TEXT_WHITE
         );
         width15 += font2.getStringWidth(
            text3
         );
         width15 += width5;
         font2.drawString(
            matrices,
            text13,
            width15,
            width16,
            TEXT_DARK_GRAY
         );
         width15 += width4
            + width5;
         if (font3
            != null) {
            font3.drawString(
               matrices,
               text6,
               width15,
               width17,
               color4
            );
            width15 += font3.getStringWidth(
                  text6
               )
               + 1.5F;
         }

         font2.drawString(
            matrices,
            text5,
            width15,
            width16,
            TEXT_WHITE
         );
         width15 += font2.getStringWidth(
            text5
         );
         width15 += width5;
         font2.drawString(
            matrices,
            text13,
            width15,
            width16,
            TEXT_DARK_GRAY
         );
         width15 += width4
            + width5;
         if (font3
            != null) {
            font3.drawString(
               matrices,
               text8,
               width15,
               width17,
               color4
            );
            width15 += font3.getStringWidth(
                  text8
               )
               + 1.5F;
         }

         font2.drawString(
            matrices,
            text7,
            width15,
            width16,
            TEXT_WHITE
         );
         width15 = width
            + 2.5F;
         width16 = width14
            + 2.5F
            + 3.5F;
         width17 = width14
            + 2.5F
            + 5.0F;
         if (font3
            != null) {
            font3.drawString(
               matrices,
               text10,
               width15,
               width17,
               color4
            );
            width15 += font3.getStringWidth(
                  text10
               )
               + 1.5F;
         }

         font2.drawString(
            matrices,
            text9,
            width15,
            width16,
            TEXT_WHITE
         );
         width15 += font2.getStringWidth(
            text9
         );
         width15 += width5;
         font2.drawString(
            matrices,
            text13,
            width15,
            width16,
            TEXT_DARK_GRAY
         );
         width15 += width4
            + width5;
         if (font3
            != null) {
            font3.drawString(
               matrices,
               text12,
               width15,
               width17,
               color4
            );
            width15 += font3.getStringWidth(
                  text12
               )
               + 1.5F;
         }

         font2.drawString(
            matrices,
            text11,
            width15,
            width16
               - 0.5F,
            TEXT_WHITE
         );
         this.draggable
            .setWidth(
               Math.max(
                  width9,
                  width11
               )
            );
         this.draggable
            .setHeight(
               width10
                  + width13
                  + width12
            );
      }
   }

   private static int getAccent() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null) {
         ThemeColors color2 = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         if (color2
            == null) {
            return ColorUtil.getThemeColor();
         } else {
            if ("Rainbow"
               .equals(
                  color2.getName()
               )) {
               return ColorUtil.getThemeColor();
            } else {
               return color2.color
                        != null
                     && color2.color.length
                        > 0
                  ? color2.color[0]
                  : ColorUtil.getThemeColor();
            }
         }
      } else {
         return ColorUtil.getThemeColor();
      }
   }

   private static float getTps() {
      return AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.tpsCalc != null ? Math.max(0.0F, Math.min(20.0F, AspectDLC.INSTANCE.tpsCalc.getTPS())) : 20.0F;
   }

   private static String formatOneDecimal(
      float f
   ) {
      int n = Math.round(
         f
            * 10.0F
      );
      return n
            / 10
         + "."
         + Math.abs(
            n
               % 10
         );
   }
}
