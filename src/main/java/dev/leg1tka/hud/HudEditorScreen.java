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


import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class HudEditorScreen extends Screen implements IMinecraft {
   private final List<ClampscrollHelper> panels;
   private final SetboundsHelper searchField;
   private final ApplycolorsHelper themeManager;
   private final Animation openanim;
   private boolean closing;

   public HudEditorScreen() {
      super(net.minecraft.text.Text.of("HudEditorScreen"));
      this.panels = new ArrayList<>();
      this.openanim = new Animation(Easing.QUINTIC_OUT, 250L);
      this.searchField = new SetboundsHelper("Search...");

      for (Category val : Category.values()) {
         if (!val.isThemesPanel()
            )
          {
            this.panels
               .add(
                  new ClampscrollHelper(
                     val,
                     this
                  )
               );
         }
      }

      this.themeManager = new ApplycolorsHelper(this);
      this.openanim.reset(0.0F);
      this.closing = false;
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
      if (mc != null && mc.getWindow() != null) {
         this.openanim.run(this.closing ? 0.0F : 1.0F);
         float width = this.openanim
            .getValue();
         if (this.closing
            && width
               < 0.02F) {
            mc.setScreen(null);
         } else {
            float width2 = MathHelper.clamp(
               width,
               0.0F,
               1.0F
            );
            float width3 = 0.94F
               + 0.06F
                  * width;
            CursorManager.reset();
            CursorManager.resetIBeam();
            CursorManager.resetClick();
            int i = mc.getWindow()
               .getScaledWidth();
            int i2 = mc.getWindow()
               .getScaledHeight();
            Render2D.drawRoundedRect(
               context.getMatrices(),
               0.0F,
               0.0F,
               (float)i,
               (float)i2,
               0.0F,
               ColorUtil.rgba(
                  0,
                  0,
                  0,
                  (int)(
                     120.0F
                        * width2
                  )
               )
            );
            float width4 = 101.0F;
            float width5 = 7.5F;
            float width6 = 275.0F;
            float width7 = (float)this.panels
                     .size()
                  * (
                     width4
                        + width5
                  )
               - width5;
            float width8 = (
                  (float)i
                     - width7
               )
               / 2.0F;
            float width9 = (
                     (float)i2
                        - width6
                  )
                  / 2.0F
               - 10.0F;
            context.getMatrices()
               .push();
            context.getMatrices()
               .translate(
                  (float)i
                     / 2.0F,
                  (float)i2
                     / 2.0F,
                  0.0F
               );
            context.getMatrices()
               .scale(
                  width3,
                  width3,
                  1.0F
               );
            context.getMatrices()
               .translate(
                  (float)(
                        -i
                     )
                     / 2.0F,
                  (float)(
                        -i2
                     )
                     / 2.0F,
                  0.0F
               );

            for (int i3 = 0;
               i3
                  < this.panels.size();
               i3++
            ) {
               ClampscrollHelper val = this.panels
                  .get(
                     i3
                  );
               val.getAnimationAlpha()
                  .setValue(
                     width2
                  );
               val.setX(
                  width8
                     + (float)i3
                        * (
                           width4
                              + width5
                        )
               );
               val.setY(
                  width9
               );
               val.setWidth(
                  width4
               );
               val.setHeight(
                  width6
               );
               val.render(
                  context.getMatrices(),
                  n,
                  n2,
                  f
               );
            }

            float width10 = 90.0F;
            float width11 = 18.0F;
            float width12 = (float)i
                  / 2.0F
               - width10
                  / 2.0F;
            float width13 = width9
               + width6
               + 10.0F;
            this.searchField
               .setBounds(
                  width12,
                  width13,
                  width10,
                  width11
               );
            this.searchField
               .render(
                  context.getMatrices(),
                  n,
                  n2,
                  f
               );
            this.themeManager.setX(20.0F);
            this.themeManager
               .setY(
                  width9
               );
            this.themeManager
               .render(
                  context.getMatrices(),
                  n,
                  n2,
                  f
               );
            Font font = Fonts.getFont(
               "bold", 14
            );
            if (font
               == null) {
               font = Fonts.getFont(
                  "semibold", 14
               );
            }

            if (font
               == null) {
               font = Fonts.getFont(
                  "sf_regular", 14
               );
            }

            for (ClampscrollHelper val2 : this.panels) {
               boolean flag = HoverUtil.isHovered(
                  (double)n,
                  (double)n2,
                  (double)val2.getX(),
                  (double)val2.getY(),
                  (double)val2.getWidth(),
                  (double)val2.getHeight()
               );

               for (ModuleComponent val3 : val2.getModuleComponents()) {
                  if (val3.isHovered()
                     && flag
                     && this.searchField.isEmpty()) {
                     String text = val3.getModule()
                        .getDisplayDescription();
                     if (text
                           != null
                        && !text.isEmpty()
                        )
                      {
                        if (!"NULLABLE"
                           .equalsIgnoreCase(
                              text
                           )) {
                           float width14 = val3.getX();
                           float width15 = val2.getY()
                              - 14.0F;
                           GuiComponent.drawText(
                              context.getMatrices(),
                              font,
                              text,
                              width14,
                              width15,
                              ColorUtil.rgba(
                                 255,
                                 255,
                                 255,
                                 (int)(
                                    240.0F
                                       * width2
                                 )
                              )
                           );
                        }
                     }
                  }
               }
            }

            context.getMatrices()
               .pop();
            long time = mc.getWindow()
               .getHandle();
            if (CursorManager.shouldBeHand()) {
               GLFW.glfwSetCursor(
                  time,
                  GLFW.glfwCreateStandardCursor(221188)
               );
            } else if (CursorManager.shouldIBeam()) {
               GLFW.glfwSetCursor(
                  time,
                  GLFW.glfwCreateStandardCursor(221186)
               );
            } else if (CursorManager.shouldClick()) {
               GLFW.glfwSetCursor(
                  time,
                  GLFW.glfwCreateStandardCursor(221188)
               );
            } else {
               GLFW.glfwSetCursor(
                  time,
                  GLFW.glfwCreateStandardCursor(221185)
               );
            }
         }
      }
   }

   public boolean searchCheck(
      String str
   ) {
      return !this.searchField.isEmpty()
         && !str.replaceAll(
               " ", ""
            )
            .toLowerCase()
            .contains(
               this.searchField
                  .text
                  .replaceAll(" ", "")
                  .toLowerCase()
            );
   }

   public boolean mouseClicked(
      double d,
      double d2,
      int n
   ) {
      if (this.closing) {
         return false;
      } else {
         this.themeManager
            .mouseClicked(
               d,
               d2,
               n
            );
         this.searchField
            .mouseClicked(
               d,
               d2,
               n
            );
         if (this.searchField.isEmpty()) {
            for (ClampscrollHelper val : this.panels) {
               if (HoverUtil.isHovered(
                  d,
                  d2,
                  (double)val.getX(),
                  (double)val.getY(),
                  (double)val.getWidth(),
                  (double)val.getHeight()
               )) {
                  val.mouseClicked(
                     d,
                     d2,
                     n
                  );
               }
            }
         } else {
            for (ClampscrollHelper val2 : this.panels) {
               val2.mouseClicked(
                  d,
                  d2,
                  n
               );
            }
         }

         return super.mouseClicked(
            d,
            d2,
            n
         );
      }
   }

   public boolean mouseReleased(
      double d,
      double d2,
      int n
   ) {
      if (this.closing) {
         return false;
      } else {
         this.themeManager
            .mouseReleased(
               d,
               d2,
               n
            );

         for (ClampscrollHelper val : this.panels) {
            val.mouseReleased(
               d,
               d2,
               n
            );
         }

         return super.mouseReleased(
            d,
            d2,
            n
         );
      }
   }

   public boolean mouseScrolled(
      double d,
      double d2,
      double d3,
      double d4
   ) {
      if (this.closing) {
         return false;
      } else {
         this.themeManager
            .mouseScrolled(
               d,
               d2,
               d3,
               d4
            );

         for (ClampscrollHelper val : this.panels) {
            val.mouseScrolled(
               d,
               d2,
               d3,
               d4
            );
         }

         return super.mouseScrolled(
            d,
            d2,
            d3,
            d4
         );
      }
   }

   private boolean isBinding() {
      for (ClampscrollHelper val : this.panels) {
         for (ModuleComponent val2 : val.getModuleComponents()) {
            if (val2.isBinding()
               )
             {
               return true;
            }

            ObjectListIterator var5 = val2.getComponents()
               .iterator();

            while (var5.hasNext()) {
               GuiComponent val3 = (GuiComponent)var5.next();
               if (val3 instanceof KeybindComponent val4
                  && val4.isBinding()
                  )
                {
                  return true;
               }

               if (val3 instanceof BooleanComponent val5
                  && val5.isBinding()
                  )
                {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean keyPressed(
      int n,
      int n2,
      int n3
   ) {
      if (this.isBinding()) {
         for (ClampscrollHelper val : this.panels) {
            val.keyPressed(
               n,
               n2,
               n3
            );
         }

         return true;
      } else if (n
         == 256) {
         if (!this.closing) {
            this.closing = true;
            long time = mc.getWindow()
               .getHandle();
            GLFW.glfwSetCursor(
               time,
               GLFW.glfwCreateStandardCursor(221185)
            );
         }

         return true;
      } else if (this.closing) {
         return false;
      } else {
         this.themeManager
            .keyPressed(
               n
            );
         this.searchField
            .keyPressed(
               n,
               n2,
               n3
            );

         for (ClampscrollHelper val2 : this.panels) {
            val2.keyPressed(
               n,
               n2,
               n3
            );
         }

         return super.keyPressed(
            n,
            n2,
            n3
         );
      }
   }

   public boolean charTyped(
      char val,
      int n
   ) {
      if (this.closing) {
         return false;
      } else {
         this.themeManager
            .charTyped(
               val
            );
         this.searchField
            .charTyped(
               val,
               n
            );

         for (ClampscrollHelper val2 : this.panels) {
            val2.charTyped(
               val,
               n
            );
         }

         return super.charTyped(
            val,
            n
         );
      }
   }

   public boolean shouldPause() {
      return false;
   }

   @Generated
   public List<ClampscrollHelper> getPanels() {
      return this.panels;
   }

   @Generated
   public SetboundsHelper getSearchField() {
      return this.searchField;
   }

   @Generated
   public ApplycolorsHelper getThemeManager() {
      return this.themeManager;
   }

   @Generated
   public Animation getOpenanim() {
      return this.openanim;
   }

   @Generated
   public boolean isClosing() {
      return this.closing;
   }
}
