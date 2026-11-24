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


import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public class KeyStrokesHud extends HudElement {
   private final MinecraftClient mc = MinecraftClient.getInstance();
   private final List<Long> leftClicks = new ArrayList<>();
   private boolean wasLmbPressed = false;

   public KeyStrokesHud(
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
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      Font font = Fonts.getFont(
         "nunito", 15
      );
      Font font2 = Fonts.getFont(
         "nunito", 10
      );
      float width3 = 20.0F;
      float width4 = 2.0F;
      boolean flag = this.mc
         .options
         .forwardKey
         .isPressed();
      boolean flag2 = this.mc
         .options
         .leftKey
         .isPressed();
      boolean flag3 = this.mc
         .options
         .backKey
         .isPressed();
      boolean flag4 = this.mc
         .options
         .rightKey
         .isPressed();
      boolean flag5 = this.mc
         .options
         .jumpKey
         .isPressed();
      boolean flag6 = this.mc
         .options
         .attackKey
         .isPressed();
      boolean flag7 = this.mc
         .options
         .useKey
         .isPressed();
      long time = System.currentTimeMillis();
      if (flag6
         && !this.wasLmbPressed) {
         this.leftClicks
            .add(
               time
            );
      }

      this.wasLmbPressed = flag6;
      this.leftClicks
         .removeIf(
            val -> time
                     - val
                  > 1000L
         );
      int color = this.leftClicks
         .size();
      float width5 = width
         + width3
         + width4;
      this.drawKey(
         event,
         width5,
         width2,
         width3,
         width3,
         "W",
         flag,
         font
      );
      float width6 = width2
         + width3
         + width4;
      this.drawKey(
         event,
         width,
         width6,
         width3,
         width3,
         "A",
         flag2,
         font
      );
      float width7 = width
         + width3
         + width4;
      float width8 = width2
         + width3
         + width4;
      this.drawKey(
         event,
         width7,
         width8,
         width3,
         width3,
         "S",
         flag3,
         font
      );
      float width9 = width
         + (
               width3
                  + width4
            )
            * 2.0F;
      float width10 = width2
         + width3
         + width4;
      this.drawKey(
         event,
         width9,
         width10,
         width3,
         width3,
         "D",
         flag4,
         font
      );
      float width11 = width3
            * 3.0F
         + width4
            * 2.0F;
      float width12 = 20.0F;
      float width13 = width2
         + (
               width3
                  + width4
            )
            * 2.0F;
      this.drawKey(
         event,
         width,
         width13,
         width11,
         width12,
         "Space",
         flag5,
         font
      );
      float width14 = (
            width11
               - width4
         )
         / 2.0F;
      float width15 = 20.0F;
      float width16 = width2
         + (
               width3
                  + width4
            )
            * 2.0F
         + width12
         + width4;
      float width17 = (float)(
            System.currentTimeMillis() % 2000L
         )
         / 2000.0F
         * 360.0F;
      int color2 = ColorUtil.getThemeColor(
         (int)width17
      );
      this.drawKeyWithCps(
         event,
         width,
         width16,
         width14,
         width15,
         "LMB",
         flag6,
         font,
         font2,
         color,
         color2
      );
      float width18 = width
         + width14
         + width4;
      float width19 = width2
         + (
               width3
                  + width4
            )
            * 2.0F
         + width12
         + width4;
      this.drawKey(
         event,
         width18,
         width19,
         width14,
         width15,
         "RMB",
         flag7,
         font
      );
      float width20 = width3
            * 3.0F
         + width4
            * 2.0F;
      float width21 = width3
            * 2.0F
         + width4
         + width12
         + width4
         + width15
         + width4;
      this.draggable
         .setWidth(
            width20
         );
      this.draggable
         .setHeight(
            width21
         );
      super.onRender(
         event
      );
   }

   private void drawKey(
      EventRender.Default event,
      float f,
      float f2,
      float f3,
      float f4,
      String str,
      boolean flag,
      Object val
   ) {
      int color = flag
         ? ColorUtil.rgba(
            180,
            180,
            180,
            200
         )
         : ColorUtil.rgba(
            25,
            25,
            25,
            150
         );
      int color2 = flag
         ? ColorUtil.rgba(0, 0, 0, 255)
         : ColorUtil.rgba(
            255,
            255,
            255,
            255
         );
      Render2D.drawKeyStrokeRect(
         event.getContext()
            .getMatrices(),
         f,
         f2,
         f3,
         f4,
         3.0F,
         color
      );
      Font font = Fonts.getFont(
         "nunito", 15
      );
      float width = font.getWidth(
         str
      );
      float width2 = 8.0F;
      float width3 = f
         + (
               f3
                  - width
            )
            / 2.0F;
      float width4 = f2
         + (
               f4
                  - width2
            )
            / 2.0F;
      font.draw(
         event.getContext()
            .getMatrices(),
         str,
         width3
            - 0.5F,
         width4
            + 2.0F,
         color2
      );
   }

   private void drawKeyWithCps(
      EventRender.Default event,
      float f,
      float f2,
      float f3,
      float f4,
      String str,
      boolean flag,
      Object val,
      Object val2,
      int n,
      int n2
   ) {
      int color = flag
         ? ColorUtil.rgba(
            180,
            180,
            180,
            200
         )
         : ColorUtil.rgba(
            25,
            25,
            25,
            150
         );
      int color2 = flag
         ? ColorUtil.rgba(0, 0, 0, 255)
         : ColorUtil.rgba(
            255,
            255,
            255,
            255
         );
      Render2D.drawKeyStrokeRect(
         event.getContext()
            .getMatrices(),
         f,
         f2,
         f3,
         f4,
         3.0F,
         color
      );
      Font font = Fonts.getFont(
         "nunito", 15
      );
      Font font2 = Fonts.getFont(
         "nunito", 12
      );
      float width = font.getWidth(
         str
      );
      float width2 = f
         + (
               f3
                  - width
            )
            / 2.0F;
      float width3 = 8.0F;
      float width4 = f2
         + (
               f4
                  - width3
            )
            / 2.0F;
      font.draw(
         event.getContext()
            .getMatrices(),
         str,
         width2
            - 0.5F,
         width4
            + 2.0F,
         color2
      );
      String text = "cps: "
         + n;
      float width5 = font2.getWidth(
         text
      );
      float width6 = f
         + (
               f3
                  - width5
            )
            / 2.0F;
      float width7 = width4
         + 12.0F;
      font2.draw(
         event.getContext()
            .getMatrices(),
         text,
         width6,
         width7
            - 3.0F,
         n2
      );
   }
}
