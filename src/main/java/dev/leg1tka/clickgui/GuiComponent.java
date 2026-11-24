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
import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;

public abstract class GuiComponent {
   public ModuleComponent parent;
   public float x;
   public float y;
   public float width;
   public float height;
   private final Animation alphaAnim = new Animation(Easing.BACK_OUT, 550L);
   private final Animation alphaAnimSetting = new Animation(Easing.CUBIC_OUT, 280L);
   private final Animation alphaAnimBack = new Animation(Easing.CUBIC_OUT, 280L);

   private static float alignY(
      Font font,
      float f
   ) {
      return f
         + 3.5F
         - 0.126F
            * font.getSize();
   }

   public static void drawText(
      MatrixStack matrices,
      Font font,
      String str,
      float f,
      float f2,
      int n
   ) {
      if (font
         != null) {
         font.draw(
            matrices,
            str,
            f,
            alignY(
               font,
               f2
            ),
            n
         );
      }
   }

   public static void drawTextScrolling(
      MatrixStack matrices,
      Font font,
      String str,
      float f,
      float f2,
      float f3,
      int n
   ) {
      if (font
            != null
         && str
            != null
         && !str.isEmpty()
         )
       {
         float width = alignY(
            font,
            f2
         );
         float width2 = font.getStringWidth(
            str
         );
         if (width2
            <= f3
               + 0.5F) {
            font.draw(
               matrices,
               str,
               f,
               width,
               n
            );
         } else {
            double x = (double)System.currentTimeMillis()
               / 1000.0;
            double x2 = 5.5;
            double x3 = x
               % x2
               / x2;
            double x4 = x3
                  < 0.5
               ? x3
                  * 2.0
               : 2.0
                  - x3
                     * 2.0;
            float width3 = (float)(
               x4
                  * x4
                  * (
                     3.0
                        - 2.0
                           * x4
                  )
            );
            float width4 = Math.max(
               0.0F,
               width2
                  - f3
                  - 6.0F
            );
            float width5 = width3
               * width4;
            ScissorStack.push();
            ScissorStack.setFromComponentCoordinates(
               (double)f,
               (double)(
                  f2
                     - 5.0F
               ),
               (double)f3,
               20.0
            );
            font.draw(
               matrices,
               str,
               f
                  - width5,
               width,
               n
            );
            ScissorStack.pop();
         }
      }
   }

   public boolean isVisible() {
      return true;
   }

   public void setBounds(
      float f,
      float f2,
      float f3
   ) {
      this.x = f;
      this.y = f2;
      this.width = f3;
   }

   public float getHeight() {
      return this.height;
   }

   public void render(
      MatrixStack matrices,
      int n,
      int n2,
      float f
   ) {
   }

   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
   }

   public void mouseReleased(
      double d,
      double d2,
      int n
   ) {
   }

   public void keyPressed(
      int n,
      int n2,
      int n3
   ) {
   }

   public void charTyped(
      char val,
      int n
   ) {
   }

   @Generated
   public ModuleComponent getParent() {
      return this.parent;
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public Animation getAlphaAnim() {
      return this.alphaAnim;
   }

   @Generated
   public Animation getAlphaAnimSetting() {
      return this.alphaAnimSetting;
   }

   @Generated
   public Animation getAlphaAnimBack() {
      return this.alphaAnimBack;
   }

   @Generated
   public void setParent(
      ModuleComponent val
   ) {
      this.parent = val;
   }

   @Generated
   public void setX(
      float f
   ) {
      this.x = f;
   }

   @Generated
   public void setY(
      float f
   ) {
      this.y = f;
   }

   @Generated
   public void setWidth(
      float f
   ) {
      this.width = f;
   }

   @Generated
   public void setHeight(
      float f
   ) {
      this.height = f;
   }
}
