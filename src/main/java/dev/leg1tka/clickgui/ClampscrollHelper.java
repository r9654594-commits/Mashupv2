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
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ClampscrollHelper {
   public float x;
   public float y;
   public float width;
   public float height;
   public final Category category;
   public final List<ModuleComponent> moduleComponents = new ArrayList<>();
   private final Animation animation = new Animation(Easing.QUINTIC_OUT, 350L);
   private final Animation animationAlpha = new Animation(Easing.QUINTIC_OUT, 250L);
   private final Animation scrollbarAnim = new Animation(Easing.CUBIC_IN_OUT, 220L);
   public float scroll;
   public float maxScroll;
   private final HudEditorScreen parent;

   public ClampscrollHelper(
      Category val,
      HudEditorScreen val2
   ) {
      this.category = val;
      this.parent = val2;
      if (ModuleManager.INSTANCE != null) {
         ModuleManager.INSTANCE
            .getObject()
            .stream()
            .filter(
               val3 -> val3.getCategory()
                        == this.category
                     && !val3.isHidden()
            )
            .sorted(
               Comparator.comparing(
                  val4 -> val4.getName()
                        .toLowerCase()
               )
            )
            .forEach(
               val5 -> this.moduleComponents
                     .add(
                        new ModuleComponent(
                           val5,
                           this
                        )
                     )
            );
      }
   }

   public void clampScroll() {
      if (this.maxScroll > 0.0F) {
         this.scroll = MathHelper.clamp(this.scroll, -this.maxScroll, 0.0F);
      } else {
         this.scroll = 0.0F;
      }
   }

   public void render(
      MatrixStack matrices,
      int n,
      int n2,
      float f
   ) {
      float width2 = MathHelper.clamp(
         this.animationAlpha.getValue(), 0.0F, 1.0F
      );
      if (!(
         width2
            < 0.01F
      )) {
         int i = (int)(
            255.0F
               * width2
         );
         int i2 = ColorUtil.rgba(
            12,
            12,
            15,
            (int)(
               240.0F
                  * width2
            )
         );
         Render2D.drawRoundedRect(
            matrices,
            this.x,
            this.y,
            this.width,
            this.height,
            8.0F,
            i2
         );
         Font font = Fonts.getFont(
            "nunito",
            16
         );
         if (font
            == null) {
            font = Fonts.getFont(
               "bold",
               15
            );
         }

         if (font
            != null) {
            String name2 = this.category
               .name();
            float width3 = font.getStringWidth(
               name2
            );
            GuiComponent.drawText(
               matrices,
               font,
               name2,
               this.x
                  + (
                        this.width
                           - width3
                     )
                     / 2.0F,
               this.y + 4.5F,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  i
               )
            );
         }

         this.clampScroll();
         this.animation.run(this.scroll);
         float width4 = 2.0F;
         float width5 = 16.5F;
         ScissorStack.push();
         ScissorStack.setFromComponentCoordinates(
            (double)this.x,
            (double)(
               this.y
                  + width5
            ),
            (double)this.width,
            (double)(
               this.height
                  - width5
                  - 4.0F
            )
         );

         for (ModuleComponent val : this.moduleComponents) {
            if (!this.parent
               .searchCheck(
                  val.getModule()
                     .getName()
               )) {
               val.setX(
                  this.x + 2.5F
               );
               val.setY(
                  this.y
                     + width5
                     + width4
                     + this.animation.getValue()
               );
               val.setWidth(
                  this.width - 5.0F
               );
               float width6 = 16.5F;
               float width7 = 0.0F;
               if (val.getAnimation()
                     .getValue()
                  > 0.01F) {
                  ObjectListIterator var15 = val.getComponents()
                     .iterator();

                  while (var15.hasNext()) {
                     GuiComponent val2 = (GuiComponent)var15.next();
                     float width8 = MathHelper.clamp(
                        val2.getAlphaAnimSetting()
                           .getValue(),
                        0.0F,
                        1.0F
                     );
                     if (val2.isVisible()
                        || width8
                           > 0.0F) {
                        width7 += val2.getHeight()
                           * width8;
                     }
                  }
               }

               val.setHeight(
                  width6
                     + width7
                        * val.getAnimation()
                           .getValue()
               );
               ScissorStack.setFromComponentCoordinates(
                  (double)this.x,
                  (double)(
                     this.y
                        + width5
                  ),
                  (double)this.width,
                  (double)(
                     this.height
                        - width5
                        - 4.0F
                  )
               );
               val.render(
                  matrices,
                  n,
                  n2,
                  f
               );
               ScissorStack.setFromComponentCoordinates(
                  (double)this.x,
                  (double)(
                     this.y
                        + width5
                  ),
                  (double)this.width,
                  (double)(
                     this.height
                        - width5
                        - 4.0F
                  )
               );
               width4 += val.getHeight()
                  + 2.0F;
            }
         }

         this.maxScroll = Math.max(
            0.0F,
            width4
               - (
                  this.height
                     - width5
                     - 8.0F
               )
         );
         this.scrollbarAnim.run(this.maxScroll > 0.0F);
         ScissorStack.unset();
         ScissorStack.pop();
      }
   }

   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
      if (HoverUtil.isHovered(
         d,
         d2,
         (double)this.x,
         (double)(this.y + 16.5F),
         (double)this.width,
         (double)(this.height - 16.5F)
      )) {
         for (ModuleComponent val : this.moduleComponents) {
            if (!this.parent
               .searchCheck(
                  val.getModule()
                     .getName()
               )) {
               val.mouseClicked(
                  d,
                  d2,
                  n
               );
            }
         }
      }
   }

   public void mouseReleased(
      double d,
      double d2,
      int n
   ) {
      for (ModuleComponent val : this.moduleComponents) {
         if (!this.parent
            .searchCheck(
               val.getModule()
                  .getName()
            )) {
            val.mouseReleased(
               d,
               d2,
               n
            );
         }
      }
   }

   public void mouseScrolled(
      double d,
      double d2,
      double d3,
      double d4
   ) {
      if (HoverUtil.isHovered(
         d,
         d2,
         (double)this.x,
         (double)this.y,
         (double)this.width,
         (double)this.height
      )) {
         this.scroll += (float)(
            d4
               * 30.0
         );
         this.clampScroll();
      }
   }

   public void keyPressed(
      int n,
      int n2,
      int n3
   ) {
      for (ModuleComponent val : this.moduleComponents) {
         if (!this.parent
            .searchCheck(
               val.getModule()
                  .getName()
            )) {
            val.keyPressed(
               n,
               n2,
               n3
            );
         }
      }
   }

   public void charTyped(
      char val,
      int n
   ) {
      for (ModuleComponent val2 : this.moduleComponents) {
         if (!this.parent
            .searchCheck(
               val2.getModule()
                  .getName()
            )) {
            val2.charTyped(
               val,
               n
            );
         }
      }
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
   public float getHeight() {
      return this.height;
   }

   @Generated
   public Category getCategory() {
      return this.category;
   }

   @Generated
   public List<ModuleComponent> getModuleComponents() {
      return this.moduleComponents;
   }

   @Generated
   public Animation getAnimation() {
      return this.animation;
   }

   @Generated
   public Animation getAnimationAlpha() {
      return this.animationAlpha;
   }

   @Generated
   public Animation getScrollbarAnim() {
      return this.scrollbarAnim;
   }

   @Generated
   public float getScroll() {
      return this.scroll;
   }

   @Generated
   public float getMaxScroll() {
      return this.maxScroll;
   }

   @Generated
   public HudEditorScreen getParent() {
      return this.parent;
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

   @Generated
   public void setScroll(
      float f
   ) {
      this.scroll = f;
   }

   @Generated
   public void setMaxScroll(
      float f
   ) {
      this.maxScroll = f;
   }
}
