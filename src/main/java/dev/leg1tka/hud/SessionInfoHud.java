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
import net.minecraft.client.network.ServerInfo;

public class SessionInfoHud extends HudElement {
   private long sessionStartTime = System.currentTimeMillis();

   public SessionInfoHud(
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
      long time = System.currentTimeMillis();
      float width3 = 18.0F;
      String name2 = "local";
      if (mc != null) {
         ServerInfo val = mc.getCurrentServerEntry();
         if (val
               != null
            && val.address
               != null
            && !val.address
               .isEmpty()) {
            name2 = val.address;
         }
      }

      String name3 = "unknown";
      if (mc != null && mc.player != null) {
         name3 = mc.player
            .getName()
            .getString();
      } else if (mc != null && mc.getSession() != null) {
         name3 = mc.getSession()
            .getUsername();
      }

      long time2 = time
         - this.sessionStartTime;
      long time3 = time2
         / 1000L;
      long time4 = time3
         / 3600L;
      long time5 = time3
         % 3600L
         / 60L;
      long time6 = time3
         % 60L;
      String name4 = time4
         + "h "
         + time5
         + "m "
         + time6
         + "s";
      String name5 = "sessioninfo";
      String name6 = "server: "
         + name2;
      String name7 = "name: "
         + name3;
      String name8 = "playtime: "
         + name4;
      Font font = Fonts.getFont(
         "nunito", 15
      );
      float width4 = font.getWidth(
         name5
      );
      float width5 = font.getWidth(
         name6
      );
      float width6 = font.getWidth(
         name7
      );
      float width7 = font.getWidth(
         name8
      );
      float width8 = Math.max(
         width4,
         Math.max(
            width5,
            Math.max(
               width6,
               width7
            )
         )
      );
      float width9 = width8
         + 10.0F;
      int color = (int)(
         (float)(
               time
                  % 2000L
            )
            / 2000.0F
            * 360.0F
      );
      int color2 = ColorUtil.getThemeColor(
         color
      );
      Render2D.drawDefaultHudThemedPanel(
         event.getContext()
            .getMatrices(),
         width,
         width2,
         width9,
         width3
            + 25.0F,
         3.0F,
         3.5F,
         color2,
         this.getHudRectStyle()
      );
      font.drawStringWithShadow(
         event.getContext()
            .getMatrices(),
         name5,
         width
            + 3.0F,
         width2
            + 5.0F,
         -1
      );
      font.drawStringWithShadow(
         event.getContext()
            .getMatrices(),
         name6,
         width
            + 3.0F,
         width2
            + 18.0F,
         -1
      );
      font.drawStringWithShadow(
         event.getContext()
            .getMatrices(),
         name7,
         width
            + 3.0F,
         width2
            + 25.5F,
         -1
      );
      font.drawStringWithShadow(
         event.getContext()
            .getMatrices(),
         name8,
         width
            + 3.0F,
         width2
            + 33.5F,
         -1
      );
      this.draggable
         .setHeight(
            width3
               + 25.0F
         );
      this.draggable
         .setWidth(
            width9
         );
      super.onRender(
         event
      );
   }
}
