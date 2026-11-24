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



import dev.leg1tka.modules.misc.*;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class HelperBindsHud extends HudElement {
   private final UpdateHelper widthAnimation = new UpdateHelper(80.0F, 10.5F, EasingFunctions.QUAD_OUT);

   public HelperBindsHud(
      Draggable val
   ) {
      super(
         val
      );
   }

   private Font issue(
      int n
   ) {
      return Fonts.getFont(
         "nunito",
         n
      );
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      List val = this.getVisibleBinds();
      if (val.isEmpty()
         )
       {
         this.widthAnimation.update(0.0F);
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      } else {
         this.DefaultStyle(
            event,
            val
         );
         super.onRender(
            event
         );
      }
   }

   private List<NameHelper_2> getVisibleBinds() {
      ServerHelper val = ServerHelper.INSTANCE;
      List val2 = new ArrayList<>();
      if (val
         == null) {
         return val2;
      } else {
         for (NameHelper_2 val3 : val.isLonyMode()
            ? val.getLonyHelperBinds()
            : val.getSpookyHelperBinds()) {
            if (val3.bind()
                  .getKey()
               != -1) {
               val2.add(
                  val3
               );
            }
         }

         return val2;
      }
   }

   private void DefaultStyle(
      EventRender.Default event,
      List<NameHelper_2> list
   ) {
      MatrixStack matrices = event.getContext()
         .getMatrices();
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      int color = this.getThemeColor();
      int color2 = 13;
      Font font = this.issue(
         color2
      );
      float width3 = 19.0F;
      float width4 = 9.8F;
      float width5 = 0.61F;
      float width6 = 2.8F;
      float width7 = 5.0F;
      float width8 = 6.0F;
      float width9 = this.getCompactWidth(
         list,
         font,
         width4,
         width6,
         width7,
         width8,
         60.0F
      );
      this.widthAnimation
         .update(
            width9
         );
      float width10 = this.widthAnimation
         .getValue();
      this.drawDefaultPanel(
         matrices,
         width,
         width2,
         width10,
         width3,
         color
      );
      if (list.isEmpty()
         )
       {
         this.issue(12)
            .draw(
               matrices,
               "Helper",
               width
                  + 5.0F,
               width2
                  + 6.0F,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  230
               )
            );
         this.draggable
            .setWidth(
               width10
            );
         this.draggable
            .setHeight(
               width3
            );
      } else {
         this.drawCompactBinds(
            event.getContext(),
            list,
            font,
            width,
            width2,
            width3,
            width4,
            width5,
            width6,
            width7,
            width8,
            8.2F
         );
         this.draggable
            .setWidth(
               width10
            );
         this.draggable
            .setHeight(
               width3
            );
      }
   }

   private void WaveStyle(
      EventRender.Default event,
      List<NameHelper_2> list
   ) {
      MatrixStack matrices = event.getContext()
         .getMatrices();
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      int color = (int)(
         (float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F
      );
      int color2 = ColorUtil.getThemeColor(
         color
      );
      int color3 = ColorUtil.getThemeColor(
         color
            + 30
      );
      int color4 = ColorUtil.getThemeColor(
         color
            + 90
      );
      int color5 = ColorUtil.getThemeColor(
         color
            + 120
      );
      int color6 = ColorUtil.getThemeColor(
         color
            + 180
      );
      int color7 = ColorUtil.getThemeColor(
         color
            + 210
      );
      Font font = this.issue(
         14
      );
      float width3 = 22.0F;
      float width4 = 11.0F;
      float width5 = 0.69F;
      float width6 = 3.5F;
      float width7 = 6.0F;
      float width8 = 7.0F;
      float width9 = this.getCompactWidth(
         list,
         font,
         width4,
         width6,
         width7,
         width8,
         72.0F
      );
      this.widthAnimation
         .update(
            width9
         );
      float width10 = this.widthAnimation
         .getValue();
      if (list.isEmpty()
         )
       {
         Render2D.drawWaveHudHeader(
            matrices,
            width,
            width2,
            width10,
            15.0F,
            0.0F,
            10.0F,
            10.0F,
            color2,
            color3,
            color4,
            color5,
            color6,
            color7
         );
         String name = "helper";
         float width11 = width
            + (
                  width10
                     - this.issue(15)
                        .getWidth(
                           name
                        )
               )
               / 2.0F;
         this.issue(15)
            .drawStringWithShadow(
               matrices,
               name,
               width11,
               width2
                  + 5.0F,
               -1
            );
         this.draggable
            .setWidth(
               width10
            );
         this.draggable.setHeight(18.0F);
      } else {
         Render2D.drawWaveHudPanel(
            matrices,
            width,
            width2,
            width10,
            width3,
            ColorUtil.rgba(
               25,
               25,
               25,
               150
            ),
            3.5F,
            0.0F,
            10.0F,
            10.0F,
            color2,
            color3,
            color4,
            color5,
            color6,
            color7
         );
         this.drawCompactBinds(
            event.getContext(),
            list,
            font,
            width,
            width2,
            width3,
            width4,
            width5,
            width6,
            width7,
            width8,
            9.5F
         );
         this.draggable
            .setWidth(
               width10
            );
         this.draggable
            .setHeight(
               width3
            );
      }
   }

   private float getCompactWidth(
      List<NameHelper_2> list,
      Font font,
      float f,
      float f2,
      float f3,
      float f4,
      float f5
   ) {
      if (list.isEmpty()
         )
       {
         return f5;
      } else {
         float width = f4
            * 2.0F;

         for (int i = 0;
            i
               < list.size();
            i++
         ) {
            String name = CallHelper.getBindName(
               list.get(
                     i
                  )
                  .bind()
                  .getKey()
            );
            width += f
               + f2
               + font.getWidth(
                  name
               );
            if (i
               < list.size()
                  - 1) {
               width += f3;
            }
         }

         return width;
      }
   }

   private void drawCompactBinds(
      DrawContext context,
      List<NameHelper_2> list,
      Font font,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9
   ) {
      MatrixStack matrices = context.getMatrices();
      float width = f
         + f8;
      float width2 = f2
         + (
               f3
                  - f4
            )
            * 0.5F;
      float width3 = f2
         + f9;

      for (int i = 0;
         i
            < list.size();
         i++
      ) {
         NameHelper_2 val = list.get(
            i
         );
         String name = CallHelper.getBindName(
            val.bind()
               .getKey()
         );
         this.drawItemIcon(
            context,
            new ItemStack(
               val.item()
            ),
            width,
            width2,
            f5
         );
         font.draw(
            matrices,
            name,
            width
               + f4
               + f6,
            width3,
            ColorUtil.rgba(
               255,
               255,
               255,
               240
            )
         );
         width += f4
            + f6
            + font.getWidth(
               name
            );
         if (i
            < list.size()
               - 1) {
            width += f7;
         }
      }
   }

   private void drawItemIcon(
      DrawContext context,
      ItemStack stack,
      float f,
      float f2,
      float f3
   ) {
      MatrixStack matrices = context.getMatrices();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      matrices.push();
      matrices.translate(
         f,
         f2,
         0.0F
      );
      matrices.scale(
         f3,
         f3,
         1.0F
      );
      context.drawItem(
         stack,
         0,
         0
      );
      matrices.pop();
      RenderSystem.depthMask(false);
      RenderSystem.disableDepthTest();
   }

   private void drawDefaultPanel(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      this.drawHudBackground(
         matrices,
         f,
         f2,
         f3,
         f4,
         n
      );
   }

   private int getThemeColor() {
      return !AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
         ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0]
         : ColorUtil.getThemeColor();
   }
}
