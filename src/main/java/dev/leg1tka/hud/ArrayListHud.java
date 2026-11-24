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
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;

public class ArrayListHud extends HudElement {
   private static final float LINE_HEIGHT = 9.5F;
   private static final float FLOW_SPEED = 1000.0F;
   private static final Comparator<LowernameHelper> MODULE_WIDTH_COMPARATOR = Comparator.<LowernameHelper>comparingDouble(
         val2 -> (double)val2.width
      )
      .reversed();
   private final List<LowernameHelper> visibleModules = new ArrayList<>();

   public ArrayListHud(
      Draggable val
   ) {
      super(
         val
      );
   }

   private Font font() {
      return Fonts.getFont("nunito", 14);
   }

   private void drawFlowingText(
      MatrixStack matrices,
      Font font,
      String str,
      float f,
      float f2,
      int n,
      float f3
   ) {
      int color = ColorUtil.setAlphaColor(
         n,
         (int)(
            255.0F
               * f3
         )
      );
      font.draw(
         matrices,
         str,
         f,
         f2,
         color
      );
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      MatrixStack matrices = event.getContext()
         .getMatrices();
      Font font2 = this.font();
      List<Module> list = (List<Module>)ModuleManager.INSTANCE
         .getObject();
      this.visibleModules.clear();

      for (Module val : list) {
         val.getArrayAnimka()
            .update(
               val.isEnable()
                  ? 1.0F
                  : 0.0F
            );
         float width2 = val.getArrayAnimka()
            .getValue();
         if (!(
            width2
               <= 0.03F
         )) {
            String name = val.getDisplayName();
            this.visibleModules
               .add(
                  new LowernameHelper(
                     name.toLowerCase(),
                     font2.getWidth(
                        name
                     ),
                     width2
                  )
               );
         }
      }

      this.visibleModules.sort(MODULE_WIDTH_COMPARATOR);
      long time = System.currentTimeMillis();
      float width3 = this.draggable
         .getX();
      float width4 = this.draggable
         .getY();
      float width5 = 0.0F;
      boolean flag = width3
         <= (float)mc.getWindow().getScaledWidth() * 0.5F;

      for (LowernameHelper val2 : this.visibleModules) {
         width5 = Math.max(
            width5,
            val2.width
         );
      }

      float width6 = 0.0F;

      for (LowernameHelper val3 : this.visibleModules) {
         width6 += 9.5F
            * val3.anim;
      }

      float width7 = 0.0F;
      if (Render2D.isLiquidGlassHudActive()
         && width6
            > 0.5F) {
         float width8 = flag
            ? 10.0F
            : 6.0F;
         float width9 = 3.0F;
         float width10 = flag
            ? width3
               - width8
            : width3
               - 2.0F;
         float width11 = width5
            + width8
            + 6.0F;
         Render2D.drawHudLiquidGlass(
            matrices,
            width10,
            width4
               - width9,
            width11,
            width6
               + width9
                  * 2.0F,
            3.5F,
            ColorUtil.getThemeColor(),
            true,
            this.getHudRectStyle()
         );
      }

      for (int i = 0;
         i
            < this.visibleModules.size();
         i++
      ) {
         LowernameHelper val4 = this.visibleModules
            .get(
               i
            );
         float width12 = val4.anim;
         float width13 = 9.5F
            * width12;
         int i2 = (int)(
               (float)time
                  * 1000.0F
                  / 10.0F
            )
            + i
               * 42;
         int i3 = ColorUtil.getThemeColor(
            i2
         );
         int i4 = ColorUtil.getThemeColor(
            i2
               + 90
         );
         int i5 = (int)(
            (float)(
                  flag
                     ? 140
                     : 170
               )
               * width12
         );
         int i6 = ColorUtil.setAlphaColor(
            i3,
            i5
         );
         int i7 = ColorUtil.setAlphaColor(
            i4,
            i5
         );
         float width14 = val4.width;
         float width15;
         if (flag
            )
          {
            width15 = width3
               - 3.0F;
         } else {
            width15 = width3
               + (
                  width5
                     - width14
               )
               - 3.0F;
         }

         float width16 = width4
            + width7
            + (
                  1.0F
                     - width12
               )
               * 7.0F;
         float width17 = flag
            ? width15
               - 0.6F
            : width15
               - 1.5F;
         float width18 = flag
            ? width14
               - 4.0F
            : width14;
         Render2D.drawShadow(
            matrices,
            width17,
            width16,
            width18,
            6.0F,
            5.0F,
            11.0F,
            i7,
            i7,
            i6,
            i6
         );
         float width19 = flag
            ? width15
               - 0.8F
            : width15
               - 2.0F;
         this.drawFlowingText(
            matrices,
            font2,
            val4.lowerName,
            width19,
            width16
               + 1.5F,
            i3,
            width12
         );
         width7 += width13;
      }

      if (width7
         > 0.5F) {
         float width20 = flag
            ? width3
               - 6.5F
            : width3
               + width5
               - 7.0F;
         float width21 = 2.5F;
         int i8 = ColorUtil.setAlphaColor(
            ColorUtil.getThemeColor(0), 220
         );
         int i9 = ColorUtil.setAlphaColor(
            ColorUtil.getThemeColor(180),
            220
         );
         Render2D.drawGradientRect(
            matrices,
            width20,
            width4,
            width21,
            width7
               - 2.0F,
            0.0F,
            i8,
            i9
         );
      }

      this.draggable
         .setWidth(
            width5
               + 4.0F
         );
      this.draggable
         .setHeight(
            width7
         );
      super.onRender(
         event
      );
   }
}
