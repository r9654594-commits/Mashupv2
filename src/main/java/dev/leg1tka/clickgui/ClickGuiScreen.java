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


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen implements IMinecraft {
   private final float WIDTH;
   private final float HEIGHT;

   public ClickGuiScreen() {
      super(net.minecraft.text.Text.of("ClickGuiScreen"));
      this.WIDTH = 170.0F;
      this.HEIGHT = 240.0F;
   }

   public void renderBackground(
      DrawContext context,
      int n,
      int n2,
      float f
   ) {
   }

   public void render(
      DrawContext context,
      int n,
      int n2,
      float f
   ) {
      Window val = MinecraftClient.getInstance()
         .getWindow();
      if (val
         != null) {
         float width = (float)val.getScaledWidth()
               / 2.0F
            - 170.0F;
         float width2 = (float)val.getScaledHeight()
               / 2.0F
            - 240.0F;
         Render2D.drawGradientRect(
            context.getMatrices(),
            width,
            width2,
            170.0F,
            240.0F,
            5.0F,
            ColorUtil.getThemeColor(),
            ColorUtil.darken(ColorUtil.getThemeColor(), 0.5F),
            true
         );
         super.render(
            context,
            n,
            n2,
            f
         );
      }
   }
}
