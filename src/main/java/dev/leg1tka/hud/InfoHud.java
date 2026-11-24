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
import java.util.Arrays;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;

public class InfoHud extends HudElement {
   private static final String ICON_GLYPH = "d";
   private static final float ICON_PLATE_SIZE = 16.0F;
   private static final int ICON_SIZE = 14;
   private boolean showFps = true;
   private boolean showMs = true;
   private boolean showServer = true;
   private boolean showTps = true;
   private boolean showTpsRow = true;
   private boolean showBps = false;
   private boolean showXyz = false;
   private boolean showServerRow = true;
   private static String cachedUsername = null;
   private static int cachedUid = -1;
   private static boolean hasLoadedFromFile = false;
   private int SKULL_ICON_SIZE;

   public InfoHud(
      Draggable val
   ) {
      super(
         val
      );
   }

   public boolean isShowFps() {
      return this.showFps;
   }

   public void setShowFps(
      boolean flag
   ) {
      this.showFps = flag;
   }

   public boolean isShowMs() {
      return this.showMs;
   }

   public void setShowMs(
      boolean flag
   ) {
      this.showMs = flag;
   }

   public boolean isShowServer() {
      return this.showServer;
   }

   public void setShowServer(
      boolean flag
   ) {
      this.showServer = flag;
   }

   public boolean isShowTps() {
      return this.showTps;
   }

   public void setShowTps(
      boolean flag
   ) {
      this.showTps = flag;
   }

   public boolean isShowTpsRow() {
      return this.showTpsRow;
   }

   public void setShowTpsRow(
      boolean flag
   ) {
      this.showTpsRow = flag;
   }

   public boolean isShowBps() {
      return this.showBps;
   }

   public void setShowBps(
      boolean flag
   ) {
      this.showBps = flag;
   }

   public boolean isShowXyz() {
      return this.showXyz;
   }

   public void setShowXyz(
      boolean flag
   ) {
      this.showXyz = flag;
   }

   public boolean isShowServerRow() {
      return this.showServerRow;
   }

   public void setShowServerRow(
      boolean flag
   ) {
      this.showServerRow = flag;
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
      MatrixStack matrices = event.getContext()
         .getMatrices();
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      Font font = Fonts.getFont(
         "nunito",
         15
      );
      Font font2 = Fonts.getFont(
         "iconnew",
         14
      );
      Font font3 = Fonts.getFont(
         "iconnew",
         15
      );
      Font font4 = Fonts.getFont(
         "icon",
         14
      );
      Font font5 = Fonts.getFont(
         "icon",
         14
      );
      Font font6 = font2
            != null
         ? font2
         : font4;
      Font font7 = Fonts.getFont(
         "nunito",
         13
      );
      float width3 = 16.0F;
      float width4 = 16.0F;
      float width5 = 2.5F;
      String name = "AspectDLC";
      int color2 = (int)(
         (float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F
      );
      float width6 = font.getStringWidth(
         name
      );
      float width7 = width
         + width4
         + width5;
      float width8 = width7
         + 4.0F;
      float width9 = width2
         + 6.2F;
      String var91 = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName();
      int color3;
      if (!var91.equals("Rainbow")) {
         color3 = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme()
            .color[0];
      } else {
         color3 = ColorUtil.getThemeColor();
      }

      HudRectStyle style = this.getHudRectStyle();
      float width10 = 3.0F;
      int color4 = new Color(
            255,
            255,
            255,
            255
         )
         .getRGB();
      float width11 = width2
         + 6.8F;
      float width12 = width6
         + 8.0F;
      this.drawHudBackground(
         matrices,
         width,
         width2,
         width4,
         width3,
         color3
      );
      if (font5
         != null) {
         float width13 = font5.getWidth(
            "d"
         );
         float width14 = width
            + (
                  width4
                     - width13
               )
               * 0.5F;
         float width15 = width2
            + 6.4F;
         font5.drawGradientStringHorizontal(
            matrices,
            "d",
            width14,
            width15,
            ColorUtil.getThemeColor(
               color2
            ),
            ColorUtil.getThemeColor(
               color2
                  + 120
            )
         );
      }

      this.drawHudBackground(
         matrices,
         width7,
         width2,
         width12,
         width3,
         color3
      );
      int color5 = ColorUtil.applyAlpha(
         color3,
         0.32F
      );
      Render2D.drawShadow(
         matrices,
         width8
            - 1.2F,
         width9
            - 1.35F,
         width6
            + 2.0F,
         7.2F,
         3.0F,
         5.0F,
         color5
      );
      font.drawGradientStringHorizontal(
         matrices,
         name,
         width8,
         width9,
         ColorUtil.getThemeColor(
            color2
         ),
         ColorUtil.getThemeColor(
            color2
               + 120
         )
      );
      float width16 = width7
         + width12
         + 2.5F;
      float width17 = 15.85F;
      String name2 = "e";
      float width18 = width2
         + 7.45F;
      String name3 = "p";
      String name4 = "y";
      float width19 = width2
         + 7.25F;
      int color6 = mc
            != null
         ? mc.getCurrentFps()
         : 0;
      String name5 = String.valueOf(
         color6
      );
      String name6 = "fps";
      String name7 = name5
         + name6;
      int color7 = 0;
      if (mc != null && mc.player != null && mc.getNetworkHandler() != null) {
         PlayerListEntry val = mc.getNetworkHandler()
            .getPlayerListEntry(mc.player.getUuid());
         if (val
            != null) {
            color7 = val.getLatency();
         }
      }

      String name8 = String.valueOf(
         color7
      );
      String name9 = "ms";
      String name10 = name8
         + name9;
      float width20 = width10;
      if (this.showFps) {
         width20 = width10
            + (
               font6
                     != null
                  ? font6.getStringWidth(
                        name3
                     )
                     + 2.0F
                  : 0.0F
            );
         width20 += font7.getStringWidth(
               name7
            )
            + 2.0F;
      }

      if (this.showMs) {
         width20 += font6
               != null
            ? font6.getStringWidth(
                  name4
               )
               + 2.0F
            : 0.0F;
         width20 += font7.getStringWidth(
               name10
            )
            + 2.0F;
      }

      if (this.showTps) {
         String name11 = "b";
         float width21 = this.getServerTps();
         String name12 = this.formatOneDecimal(
            width21
         );
         String name13 = "tps";
         String name14 = name12
            + name13;
         width20 += font6
               != null
            ? font6.getStringWidth(
                  name11
               )
               + 2.0F
            : 0.0F;
         width20 += font7.getStringWidth(
               name14
            )
            + 2.0F;
      }

      width20 += width10;
      float width22 = width20
         - 1.05F;
      this.drawHudBackground(
         matrices,
         width16,
         width2,
         width22,
         width17,
         color3
      );
      float width23 = width16
         + width10
         + 1.5F;
      if (this.showFps) {
         if (font6
            != null) {
            font6.drawGradientStringHorizontal(
               matrices,
               name3,
               width23,
               width19,
               color3,
               color3
            );
            width23 += font6.getStringWidth(
                  name3
               )
               + 2.0F;
         }

         font7.drawString(
            matrices,
            name5,
            width23,
            width11,
            color4
         );
         font7.drawString(
            matrices,
            name6,
            width23
               + font7.getStringWidth(
                  name5
               )
               - 1.0F,
            width11,
            color3
         );
         width23 += font7.getStringWidth(
               name7
            )
            + 2.0F;
      }

      if (this.showMs) {
         if (font6
            != null) {
            font6.drawGradientStringHorizontal(
               matrices,
               name4,
               width23,
               width19,
               color3,
               color3
            );
            width23 += font6.getStringWidth(
                  name4
               )
               + 2.0F;
         }

         font7.drawString(
            matrices,
            name8,
            width23,
            width11,
            color4
         );
         font7.drawString(
            matrices,
            name9,
            (double)(
                  width23
                     + font7.getStringWidth(
                        name8
                     )
               )
               - 0.5,
            (double)width11,
            color3
         );
         width23 += font7.getStringWidth(
               name10
            )
            + 2.0F;
      }

      if (this.showTps) {
         String name15 = "b";
         float width24 = this.getServerTps();
         String name16 = this.formatOneDecimal(
            width24
         );
         String name17 = "tps";
         String name18 = name16
            + name17;
         if (font6
            != null) {
            font6.drawGradientStringHorizontal(
               matrices,
               name15,
               width23,
               width19,
               color3,
               color3
            );
            width23 += font6.getStringWidth(
                  name15
               )
               + 2.0F;
         }

         font7.drawString(
            matrices,
            name16,
            width23,
            width11,
            color4
         );
         font7.drawString(
            matrices,
            name17,
            width23
               + font7.getStringWidth(
                  name16
               )
               - 1.0F,
            width11,
            color3
         );
      }

      float width25 = width2
         + width3
         + 2.5F;
      float width26 = 15.85F;
      float width27 = width25
         + 6.8F;
      float width28 = width25
         + 7.25F;
      Font font8 = Fonts.getFont(
         "icon",
         16
      );
      float width29 = 0.0F;
      if (this.showServer) {
         String name19 = "Singleplayer";
         if (mc != null) {
            ServerInfo val2 = mc.getCurrentServerEntry();
            if (val2
                  != null
               && val2.address
                  != null
               && !val2.address
                  .isEmpty()) {
               name19 = this.formatServerNameForDisplay(
                  val2.address
               );
            }
         }

         String name20 = "m";
         Font font9 = Fonts.getFont(
            "iconnew",
            15
         );
         float width30 = width10;
         if (font9
            != null) {
            width30 = width10
               + font9.getStringWidth(
                  name20
               )
               + 3.0F;
         }

         width30 += font7.getStringWidth(
               name19
            )
            + width10;
         if (width30
            < 40.0F) {
            width30 = 40.0F;
         }

         this.drawHudBackground(
            matrices,
            width,
            width25,
            width30,
            width26,
            color3
         );
         float width31 = width
            + width10
            + 1.5F;
         if (font9
            != null) {
            font9.drawGradientStringHorizontal(
               matrices,
               name20,
               width31
                  - 1.0F,
               width28
                  - 0.3F,
               color3,
               color3
            );
            width31 += font9.getStringWidth(
                  name20
               )
               + 3.0F;
         }

         this.drawServerNameWithThemeParts(
            matrices,
            name19,
            width31,
            width27,
            color3,
            color4
         );
         width29 += width30;
      }

      float width32 = width4
         + width5
         + width12
         + 2.0F
         + width22;
      boolean flag = this.showServerRow;
      float width33 = flag
         ? Math.max(
            width32,
            width29
         )
         : width32;
      float width34 = flag
         ? width3
            + 2.5F
            + width26
         : width3;
      this.draggable
         .setWidth(
            width33
         );
      this.draggable
         .setHeight(
            width34
         );
      this.renderBottomLeftInfo(
         matrices,
         color3,
         font7,
         font8
      );
   }

   private void renderBottomLeftInfo(
      MatrixStack matrices,
      int n,
      Font font,
      Font font2
   ) {
      if (mc != null && mc.getWindow() != null) {
         int color = mc.getWindow()
            .getScaledWidth();
         int color2 = mc.getWindow()
            .getScaledHeight();
         int color3 = new Color(
               255,
               255,
               255,
               255
            )
            .getRGB();
         float width = 4.0F;
         float width2 = 4.0F;
         float width3 = 4.0F;
         float width4 = 15.85F;
         float width5 = 2.5F;
         float width6 = 3.0F;
         int color4 = mc.player
               != null
            ? (int)Math.floor(mc.player.getX())
            : 0;
         int color5 = mc.player
               != null
            ? (int)Math.floor(mc.player.getY())
            : 0;
         int color6 = mc.player
               != null
            ? (int)Math.floor(mc.player.getZ())
            : 0;
         String str = color4
            + " "
            + color5
            + " "
            + color6;
         String str2 = "x";
         float width7 = width6;
         if (font2
            != null) {
            width7 = width6
               + font2.getWidth(
                  str2
               )
               + 3.0F;
         }

         width7 += font.getStringWidth(
               str
            )
            + width6;
         if (width7
            < 40.0F) {
            width7 = 40.0F;
         }

         float width8 = (float)color2
            - width3
            - width4;
         this.drawHudBackground(
            matrices,
            width,
            width8,
            width7,
            width4,
            n
         );
         float width9 = width
            + width6
            + 1.5F;
         float width10 = width8
            + 6.8F;
         float width11 = width8
            + 7.25F;
         if (font2
            != null) {
            font2.draw(
               matrices,
               str2,
               width9,
               width11
                  - 0.5F,
               n
            );
            width9 += font2.getWidth(
                  str2
               )
               + 3.0F;
         }

         font.drawString(
            matrices,
            str,
            width9,
            width10,
            color3
         );
         double x2 = (double)DirectionHelper.calculateBPS();
         String str3 = this.formatTwoDecimals(
            x2
         );
         String str4 = "bps";
         String str5 = str3
            + str4;
         String str6 = "c";
         float width12 = width6;
         if (font2
            != null) {
            width12 = width6
               + font2.getWidth(
                  str6
               )
               + 3.0F;
         }

         width12 += font.getStringWidth(
               str5
            )
            + width6;
         if (width12
            < 40.0F) {
            width12 = 40.0F;
         }

         float width13 = width8
            - width5
            - width4;
         this.drawHudBackground(
            matrices,
            width,
            width13,
            width12,
            width4,
            n
         );
         float width14 = width
            + width6
            + 1.5F;
         float width15 = width13
            + 6.8F;
         float width16 = width13
            + 7.25F;
         if (font2
            != null) {
            font2.draw(
               matrices,
               str6,
               width14,
               width16
                  - 0.5F,
               n
            );
            width14 += font2.getWidth(
                  str6
               )
               + 3.0F;
         }

         font.drawString(
            matrices,
            str3,
            width14,
            width15,
            color3
         );
         font.drawString(
            matrices,
            str4,
            width14
               + font.getStringWidth(
                  str3
               )
               - 1.0F,
            width15,
            n
         );
      }
   }

   public void WaveStyle(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      MatrixStack matrices = event.getContext()
         .getMatrices();
      Font font = Fonts.getFont(
         "wave",
         30
      );
      String text = "AspectDLC";
      int color = ColorUtil.getThemeColor(
         90
      );
      int color2 = ColorUtil.getThemeColor(
         180
      );
      int color3 = ColorUtil.getThemeColor(
         270
      );
      int color4 = ColorUtil.getColor(
         360
      );
      float width3 = 95.0F
         + font.getStringWidth(
            "ful"
         );
      Render2D.drawShadow(
         matrices,
         width,
         width2,
         width3,
         12.0F,
         10.0F,
         15.0F,
         color4,
         color2,
         color,
         color3
      );
      font.drawGradientStringHorizontal(
         matrices,
         text,
         width,
         width2,
         color,
         color2
      );
      this.draggable
         .setWidth(
            Math.max(
               width3,
               font.getStringWidth(
                  text
               )
            )
         );
      this.draggable.setHeight(12.0F);
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

   private void drawServerNameWithThemeParts(
      MatrixStack matrices,
      String str,
      float f,
      float f2,
      int n,
      int n2
   ) {
      Font font = Fonts.getFont(
         "nunito", 13
      );
      String[] val = str.split(
         "\\."
      );
      if (val.length
         < 2) {
         font.drawString(
            matrices,
            str,
            f,
            f2,
            n2
         );
      } else {
         String name = String.join(
            ".",
            Arrays.copyOf(
               val,
               val.length
                  - 1
            )
         );
         String name2 = "."
            + val[val.length
               - 1];
         font.drawString(
            matrices,
            name,
            f,
            f2,
            n2
         );
         float width = f
            + font.getStringWidth(
               name
            )
            - 2.0F;
         font.drawString(
            matrices,
            name2,
            width,
            f2,
            n
         );
      }
   }

   private String formatServerNameForDisplay(
      String str
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         String name = str;
         int x = str.indexOf(
            58
         );
         if (x
            > 0) {
            name = str.substring(
               0,
               x
            );
         }

         String[] val = name.split(
            "\\."
         );
         return val.length
               >= 3
            ? String.join(
               ".",
               Arrays.copyOfRange(
                  val,
                  1,
                  val.length
               )
            )
            : name;
      } else {
         return "";
      }
   }

   private float getServerTps() {
      return AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.tpsCalc != null ? Math.max(0.0F, Math.min(20.0F, AspectDLC.INSTANCE.tpsCalc.getTPS())) : 20.0F;
   }

   private String formatOneDecimal(
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
