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
import net.minecraft.client.util.math.MatrixStack;

public class StatsHud extends HudElement {
   private static final float RADIUS = 4.0F;
   private static final float PAD_X = 5.0F;
   private static final float HEIGHT = 15.0F;
   private static final float GAP = 3.0F;
   private static final float ICON_GAP = 2.0F;
   private static final int TEXT_WHITE = new Color(255, 255, 255, 255).getRGB();
   private static final int TEXT_GRAY = new Color(160, 160, 160, 255).getRGB();

   public StatsHud(
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
         "nunito",
         14
      );
      Font font2 = Fonts.getFont(
         "nunito",
         12
      );
      Font font3 = Fonts.getFont(
         "iconnew",
         14
      );
      if (font
            != null
         && font2
            != null) {
         int color = (int)(
            (float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F
         );
         int color2 = getAccent();
         int color3 = ColorUtil.rgba(
            8,
            8,
            10,
            200
         );
         String text = "AspectDLC";
         String text2 = "p";
         String text3 = "y";
         String text4 = "b";
         int color4 = mc
               != null
            ? mc.getCurrentFps()
            : 0;
         int color5 = 0;
         if (mc != null && mc.player != null && mc.getNetworkHandler() != null) {
            PlayerListEntry val = mc.getNetworkHandler()
               .getPlayerListEntry(mc.player.getUuid());
            if (val
               != null) {
               color5 = val.getLatency();
            }
         }

         float width3 = getTps();
         String text5 = color4
            + "fps";
         String text6 = color5
            + "ms";
         String text7 = formatOneDecimal(
               width3
            )
            + "tps";
         float width4 = font.getStringWidth(
            text
         );
         float width5 = 5.0F;
         if (font3
            != null) {
            width5 += font3.getStringWidth(
                  text2
               )
               + 2.0F;
         }

         width5 += font2.getStringWidth(
               text5
            )
            + 3.0F;
         if (font3
            != null) {
            width5 += font3.getStringWidth(
                  text3
               )
               + 2.0F;
         }

         width5 += font2.getStringWidth(
               text6
            )
            + 3.0F;
         if (font3
            != null) {
            width5 += font3.getStringWidth(
                  text4
               )
               + 2.0F;
         }

         width5 += font2.getStringWidth(
            text7
         );
         width5 += 5.0F;
         float width6 = 5.0F
            + width4
            + 5.0F;
         float width7 = width6
            + 3.0F
            + width5;
         Render2D.drawRoundedRect(
            matrices,
            width,
            width2,
            width7,
            15.0F,
            4.0F,
            color3
         );
         int color6 = ColorUtil.setAlphaColor(
            color2,
            180
         );
         Render2D.drawRoundedRectOutline(
            matrices,
            width,
            width2,
            width7,
            15.0F,
            4.0F,
            4.0F,
            4.0F,
            4.0F,
            0.6F,
            color6
         );
         float width8 = width
            + width6
            + 1.5F;
         Render2D.drawRoundedRect(
            matrices,
            width8,
            width2
               + 3.0F,
            0.5F,
            9.0F,
            0.0F,
            ColorUtil.rgba(
               60,
               60,
               65,
               180
            )
         );
         float width9 = width2
            + 4.0F
            - 0.5F;
         float width10 = width
            + 5.0F;
         font.drawGradientStringHorizontal(
            matrices,
            text,
            width10,
            width9,
            ColorUtil.getThemeColor(
               color
            ),
            ColorUtil.getThemeColor(
               color
                  + 120
            )
         );
         float width11 = width2
            + 4.0F
            + 0.7F;
         float width12 = width
            + width6
            + 3.0F
            + 5.0F;
         width12 = this.drawStatItem(
            matrices,
            font3,
            font2,
            width12,
            width9,
            width11,
            text2,
            String.valueOf(
               color4
            ),
            "fps",
            color2,
            color
         );
         width12 += 3.0F;
         width12 = this.drawStatItem(
            matrices,
            font3,
            font2,
            width12,
            width9,
            width11,
            text3,
            String.valueOf(
               color5
            ),
            "ms",
            color2,
            color
         );
         width12 += 3.0F;
         int color7 = getTpsColor(
            width3,
            color2
         );
         width12 = this.drawStatItem(
            matrices,
            font3,
            font2,
            width12,
            width9,
            width11,
            text4,
            formatOneDecimal(
               width3
            ),
            "tps",
            color7,
            color
         );
         this.draggable
            .setWidth(
               width7
            );
         this.draggable.setHeight(15.0F);
      }
   }

   private float drawStatItem(
      MatrixStack matrices,
      Font font,
      Font font2,
      float f,
      float f2,
      float f3,
      String str,
      String str2,
      String str3,
      int n,
      int n2
   ) {
      float width = f;
      if (font
         != null) {
         font.drawGradientStringHorizontal(
            matrices,
            str,
            f,
            f3,
            ColorUtil.getThemeColor(
               n2
            ),
            ColorUtil.getThemeColor(
               n2
                  + 120
            )
         );
         width = f
            + font.getStringWidth(
               str
            )
            + 2.0F;
      }

      font2.drawString(
         matrices,
         str2,
         width,
         f2,
         TEXT_WHITE
      );
      width += font2.getStringWidth(
         str2
      );
      font2.drawString(
         matrices,
         str3,
         width
            - 0.5F,
         f2,
         n
      );
      return width
         + font2.getStringWidth(
            str3
         );
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
         } else if ("Rainbow"
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
      } else {
         return ColorUtil.getThemeColor();
      }
   }

   private static float getTps() {
      return AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.tpsCalc != null ? Math.max(0.0F, Math.min(20.0F, AspectDLC.INSTANCE.tpsCalc.getTPS())) : 20.0F;
   }

   private static int getTpsColor(
      float f,
      int n
   ) {
      if (f
         >= 19.0F) {
         return new Color(
               80,
               220,
               100,
               255
            )
            .getRGB();
      } else {
         return f
               >= 15.0F
            ? new Color(
                  240,
                  200,
                  60,
                  255
               )
               .getRGB()
            : new Color(
                  230,
                  70,
                  70,
                  255
               )
               .getRGB();
      }
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
