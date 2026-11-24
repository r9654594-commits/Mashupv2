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
import dev.leg1tka.api.events.implement.EventUpdate;
import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;

public class HudElement implements IMinecraft {
   protected static final BlurRenderer BLUR = new BlurRenderer();
   protected static final float BLUR_STRENGTH = 8.0F;
   public final Draggable draggable;
   private HudRectStyle hudRectStyle = HudRectStyle.SHADER;
   private boolean blurEnabled = false;
   private boolean useV2Style = false;

   public boolean isUseV2Style() {
      return this.useV2Style;
   }

   public void setUseV2Style(
      boolean flag
   ) {
      this.useV2Style = flag;
   }

   public HudRectStyle getHudRectStyle() {
      return this.hudRectStyle;
   }

   public void setHudRectStyle(
      HudRectStyle style
   ) {
      this.hudRectStyle = style
            != null
         ? style
         : HudRectStyle.SHADER;
   }

   public boolean isHudRectThreads() {
      return this.hudRectStyle == HudRectStyle.THREADS;
   }

   public boolean isBlurEnabled() {
      return this.blurEnabled;
   }

   public void setBlurEnabled(
      boolean flag
   ) {
      this.blurEnabled = flag;
   }

   public void drawHudBackground(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      int color = n
            >> 16
         & 255;
      int color2 = n
            >> 8
         & 255;
      int color3 = n
         & 255;
      int color4 = (int)(
         12.0F
            + (float)color
               * 0.08F
      );
      int color5 = (int)(
         12.0F
            + (float)color2
               * 0.08F
      );
      int color6 = (int)(
         14.0F
            + (float)color3
               * 0.08F
      );
      int color7 = ColorUtil.rgba(
         Math.min(
            color4,
            255
         ),
         Math.min(
            color5,
            255
         ),
         Math.min(
            color6,
            255
         ),
         210
      );
      Render2D.drawRoundedRect(
         matrices,
         f,
         f2,
         f3,
         f4,
         3.0F,
         color7
      );
   }

   public void drawHudBlurOrRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n
   ) {
      Render2D.drawRoundedRect(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         n
      );
   }

   public void drawHudBlurOrRect(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      int n
   ) {
      Render2D.drawRoundedRect(
         matrices,
         f,
         f2,
         f3,
         f4,
         f5,
         f6,
         f7,
         f8,
         n
      );
   }

   public void onUpdate(
      EventUpdate event
   ) {
   }

   public void onRender(
      EventRender.Default event
   ) {
   }

   @Generated
   public HudElement(
      Draggable val
   ) {
      this.draggable = val;
   }
}
