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


import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.awt.Color;
import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ModuleComponent extends GuiComponent {
   private final Module module;
   private final ClampscrollHelper panel;
   private final Animation anim = new Animation(Easing.QUINTIC_OUT, 280L);
   private final Animation hoveranim = new Animation(Easing.QUINTIC_OUT, 200L);
   private final Animation enanim = new Animation(Easing.CUBIC_IN_OUT, 220L);
   public boolean open;
   private boolean hover;
   private boolean binding;
   private final ObjectArrayList<GuiComponent> components = new ObjectArrayList();

   public ModuleComponent(
      Module val,
      ClampscrollHelper val2
   ) {
      this.module = val;
      this.panel = val2;
      this.enanim
         .setValue(
            val.isEnable()
               ? 1.0F
               : 0.0F
         );
      this.anim.setValue(0.0F);

      for (Setting val3 : val.getSettings()) {
         GuiComponent val4 = null;
         if (val3 instanceof BooleanSetting val5
            )
          {
            val4 = new BooleanComponent(
               val5
            );
         } else if (val3 instanceof NumberSetting val6
            )
          {
            val4 = new SliderComponent(
               val6
            );
         } else if (val3 instanceof ModeSetting val7
            )
          {
            val4 = new ListSettingComponent(
               val7
            );
         } else if (val3 instanceof MultiBooleanSetting val8
            )
          {
            val4 = new ModeComponent(
               val8
            );
         } else if (val3 instanceof KeybindSetting val9
            )
          {
            val4 = new KeybindComponent(
               val9
            );
         } else if (val3 instanceof IntegerSetting val10
            )
          {
            val4 = new ColorPickerComponent(
               val10
            );
         } else if (val3 instanceof ListSetting val11
            )
          {
            val4 = new MultiBooleanComponent(
               val11
            );
         } else if (val3 instanceof StringSetting val12
            )
          {
            val4 = new TextBoxComponent(
               val12
            );
         }

         if (val4
            != null) {
            val4.setParent(
               this
            );
            this.components
               .add(
                  val4
               );
         }
      }

      if ("Interface"
         .equalsIgnoreCase(
            val.getName()
         )) {
         SettingComponent val13 = new SettingComponent(
            this
         );
         val13.setParent(
            this
         );
         this.components
            .add(
               val13
            );
      }
   }

   public boolean isHovered() {
      return this.hover;
   }

   public Animation getAnimation() {
      return this.anim;
   }

   public Animation getEnabledAnim() {
      return this.enanim;
   }

   @Override
   public void render(
      MatrixStack matrices,
      int n,
      int n2,
      float f
   ) {
      this.hover = HoverUtil.isHovered(
         (double)n,
         (double)n2,
         (double)this.x,
         (double)this.y,
         (double)this.width,
         16.5
      );
      this.hoveranim.run(this.hover);
      this.anim.run(this.open);
      this.enanim.run(this.module.isEnable());
      if (this.hover) {
         CursorManager.requestHand();
      }

      float width2 = Math.max(
         Math.min(this.panel.getAnimationAlpha().getValue(), 1.0F), 0.0F
      );
      if (!(
         width2
            < 0.01F
      )) {
         int color2 = (int)(
            255.0F
               * width2
         );
         Theme val = AspectDLC.INSTANCE
                  != null
               && AspectDLC.INSTANCE.themeStorage != null
            ? AspectDLC.INSTANCE.themeStorage.getThemes()
            : null;
         int color3 = ColorUtil.getThemeColor();
         if (val
               != null
            && val.getTheme()
               != null) {
            int[] colors = val.getTheme()
               .color;
            if (colors
                  != null
               && colors.length
                  > 0) {
               color3 = colors[0];
            }
         }

         int color4 = color3
               >> 16
            & 255;
         int color5 = color3
               >> 8
            & 255;
         int color6 = color3
            & 255;
         float[] floats = Color.RGBtoHSB(
            color4,
            color5,
            color6,
            null
         );
         float width3 = (
               floats[0]
                  - 0.045F
                  + 1.0F
            )
            % 1.0F;
         float width4 = Math.max(
            0.65F,
            Math.min(
               0.88F,
               floats[1]
                  * 0.88F
            )
         );
         float width5 = 1.0F;
         int color7 = Color.HSBtoRGB(
               width3,
               width4,
               width5
            )
            | -16777216;
         float width6 = (
               floats[0]
                  + 0.045F
            )
            % 1.0F;
         float width7 = Math.min(
            1.0F,
            floats[1]
               * 1.05F
         );
         float width8 = Math.max(
            0.88F,
            Math.min(
               1.0F,
               floats[2]
            )
         );
         int color8 = Color.HSBtoRGB(
               width6,
               width7,
               width8
            )
            | -16777216;
         int color9 = ColorUtil.setAlphaColor(
            color7,
            color2
         );
         int color10 = ColorUtil.setAlphaColor(
            color8,
            color2
         );
         int color11 = ColorUtil.rgba(
            20,
            20,
            25,
            (int)(
               240.0F
                  * width2
            )
         );
         float width9 = MathHelper.clamp(
            this.enanim.getValue(), 0.0F, 1.0F
         );
         int color12 = ColorUtil.interpolateColor(
            color11,
            color9,
            width9
         );
         int color13 = ColorUtil.interpolateColor(
            color11,
            color9,
            width9
         );
         int color14 = ColorUtil.interpolateColor(
            color11,
            color10,
            width9
         );
         int color15 = ColorUtil.interpolateColor(
            color11,
            color10,
            width9
         );
         float width10 = 16.5F
            + (this.height - 16.5F) * this.anim.getValue();
         Render2D.drawGradientRect(
            matrices,
            this.x,
            this.y,
            this.width,
            width10,
            3.5F,
            color12,
            color14,
            color13,
            color15
         );
         Font font = Fonts.getFont(
            "semibold", 15
         );
         if (font
            == null) {
            font = Fonts.getFont(
               "medium", 15
            );
         }

         if (font
            == null) {
            font = Fonts.getFont(
               "sf_regular", 15
            );
         }

         if (font
            != null) {
            if (this.binding) {
               String name = "Нажмите клавишу...";
               float width11 = font.getStringWidth(
                  name
               );
               drawText(
                  matrices,
                  font,
                  name,
                  this.x
                     + (
                           this.width
                              - width11
                        )
                        / 2.0F,
                  this.y + 4.75F,
                  ColorUtil.rgba(
                     255,
                     255,
                     255,
                     color2
                  )
               );
            } else {
               String name2 = this.module
                  .getDisplayName();
               float width12 = font.getStringWidth(
                  name2
               );
               float width13 = this.x
                  + (
                        this.width
                           - width12
                     )
                     / 2.0F;
               int color16 = ColorUtil.rgba(
                  220,
                  220,
                  230,
                  color2
               );
               int color17 = ColorUtil.rgba(
                  255,
                  255,
                  255,
                  color2
               );
               int color18 = ColorUtil.interpolateColor(
                  color16,
                  color17,
                  width9
               );
               drawText(
                  matrices,
                  font,
                  name2,
                  width13,
                  this.y + 4.75F,
                  color18
               );
               if (!this.components.isEmpty()) {
                  String name3 = this.open
                     ? "v"
                     : ">";
                  Font font2 = Fonts.getFont(
                     "semibold",
                     14
                  );
                  if (font2
                     == null) {
                     font2 = font;
                  }

                  int color19 = ColorUtil.rgba(
                     130,
                     130,
                     145,
                     (int)(
                        220.0F
                           * width2
                     )
                  );
                  int color20 = ColorUtil.rgba(
                     255,
                     255,
                     255,
                     color2
                  );
                  int color21 = ColorUtil.interpolateColor(
                     color19,
                     color20,
                     width9
                  );
                  drawText(
                     matrices,
                     font2,
                     name3,
                     this.x + this.width - 9.5F,
                     this.y + 4.75F,
                     color21
                  );
               }
            }
         }

         if (this.anim.getValue() > 0.01F) {
            float width14 = this.y
               + 16.5F;
            float width15 = this.panel
                  .getY()
               + 16.5F;
            float width16 = this.panel
                  .getY()
               + this.panel.getHeight()
               - 4.0F;
            float width17 = this.y
               + 16.5F;
            float width18 = this.y
               + width10;
            float width19 = Math.max(
               width17,
               width15
            );
            float width20 = Math.min(
               width18,
               width16
            );
            float width21 = Math.max(
               0.0F,
               width20
                  - width19
            );
            ObjectListIterator var53 = this.components.iterator();

            while (var53.hasNext()) {
               GuiComponent val2 = (GuiComponent)var53.next();
               val2.getAlphaAnim()
                  .setValue(
                     width2
                  );
               val2.getAlphaAnimSetting()
                  .run(
                     val2.isVisible()
                  );
               float width22 = MathHelper.clamp(
                  val2.getAlphaAnimSetting()
                     .getValue(),
                  0.0F,
                  1.0F
               );
               if (val2.isVisible()
                  || width22
                     > 0.0F) {
                  val2.setX(
                     this.x + 2.0F
                  );
                  val2.setY(
                     width14
                  );
                  val2.setWidth(
                     this.width - 4.0F
                  );
                  ScissorStack.push();
                  ScissorStack.setFromComponentCoordinates(
                     (double)this.x,
                     (double)width19,
                     (double)this.width,
                     (double)width21
                  );
                  val2.render(
                     matrices,
                     n,
                     n2,
                     f
                  );
                  ScissorStack.pop();
                  width14 += val2.getHeight()
                     * width22;
               }
            }
         }
      }
   }

   @Override
   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
      if (this.binding) {
         if (HoverUtil.isHovered(
               d,
               d2,
               (double)this.x,
               (double)this.y,
               (double)this.width,
               16.5
            )
            && n
               == 2) {
            this.binding = false;
         } else {
            if (n
               != 0) {
               this.module
                  .setKey(
                     CallHelper.createMouseBind(
                        n
                     )
                  );
            }

            this.binding = false;
         }
      } else {
         if (HoverUtil.isHovered(
            d,
            d2,
            (double)this.x,
            (double)this.y,
            (double)this.width,
            16.5
         )) {
            if (n
               == 0) {
               this.module.toggle();
            }

            if (n
                  == 1
               && !this.components.isEmpty()) {
               this.open = !this.open;
            }

            if (n
               == 2) {
               this.binding = !this.binding;
            }
         }

         if (this.open) {
            ObjectListIterator var6 = this.components.iterator();

            while (var6.hasNext()) {
               GuiComponent val = (GuiComponent)var6.next();
               if (val.isVisible()
                  && val.getAlphaAnimSetting()
                        .getValue()
                     > 0.5F) {
                  val.mouseClicked(
                     d,
                     d2,
                     n
                  );
               }
            }
         }
      }
   }

   @Override
   public void mouseReleased(
      double d,
      double d2,
      int n
   ) {
      if (this.open) {
         ObjectListIterator var6 = this.components.iterator();

         while (var6.hasNext()) {
            GuiComponent val = (GuiComponent)var6.next();
            val.mouseReleased(
               d,
               d2,
               n
            );
         }
      }
   }

   @Override
   public void keyPressed(
      int n,
      int n2,
      int n3
   ) {
      if (this.binding) {
         if (n
               != 256
            && n
               != 261
            && n
               != 259) {
            this.module
               .setKey(
                  n
               );
         } else {
            this.module.setKey(-1);
         }

         this.binding = false;
      } else {
         if (this.open) {
            ObjectListIterator var4 = this.components.iterator();

            while (var4.hasNext()) {
               GuiComponent val = (GuiComponent)var4.next();
               val.keyPressed(
                  n,
                  n2,
                  n3
               );
            }
         }
      }
   }

   public boolean isBinding() {
      return this.binding;
   }

   @Override
   public void charTyped(
      char val,
      int n
   ) {
      if (this.open) {
         ObjectListIterator var3 = this.components.iterator();

         while (var3.hasNext()) {
            GuiComponent val2 = (GuiComponent)var3.next();
            val2.charTyped(
               val,
               n
            );
         }
      }
   }

   @Generated
   public Module getModule() {
      return this.module;
   }

   @Generated
   public ClampscrollHelper getPanel() {
      return this.panel;
   }

   @Generated
   public Animation getAnim() {
      return this.anim;
   }

   @Generated
   public Animation getHoveranim() {
      return this.hoveranim;
   }

   @Generated
   public Animation getEnanim() {
      return this.enanim;
   }

   @Generated
   public boolean isOpen() {
      return this.open;
   }

   @Generated
   public boolean isHover() {
      return this.hover;
   }

   @Generated
   public ObjectArrayList<GuiComponent> getComponents() {
      return this.components;
   }
}
