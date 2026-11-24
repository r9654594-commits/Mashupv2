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


import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ItemCounterHud extends HudElement {
   private static final float ICON_BLOCK_WIDTH = 20.0F;
   private static final float ICON_BLOCK_HEIGHT = 18.0F;
   private static final float ROW_GAP = 2.0F;
   private static final float BLOCK_GAP = 2.0F;
   private static final float CORNER_RADIUS = 3.0F;
   private static final float TEXT_PADDING_H = 5.0F;

   public ItemCounterHud(
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
      if (mc != null && mc.player != null) {
         DrawContext context = event.getContext();
         MatrixStack matrices = context.getMatrices();
         float width = this.draggable
            .getX();
         float width2 = this.draggable
            .getY();
         int color2 = this.countItem(
            Items.FIREWORK_ROCKET.getDefaultStack().getItem()
         );
         int color3 = this.countItem(
            Items.TOTEM_OF_UNDYING.getDefaultStack().getItem()
         );
         String name = "x"
            + color2;
         String name2 = "x"
            + color3;
         Font font = Fonts.getFont(
            "nunito",
            14
         );
         float width3 = 10.0F;
         if (font
            != null) {
            float width4 = font.getWidth(
               name
            );
            float width5 = font.getWidth(
               name2
            );
            width3 = Math.max(
                  width4,
                  width5
               )
               + 10.0F;
         }

         if (width3
            < 28.0F) {
            width3 = 28.0F;
         }

         int color4;
         float width6;
         float width7;
         label22: {
            width6 = 22.0F
               + width3;
            width7 = 18.0F;
            if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null) {
               String var22 = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName();
               if (!var22.equals("Rainbow")) {
                  color4 = AspectDLC.INSTANCE
                     .themeStorage
                     .getThemes()
                     .getTheme()
                     .color[0];
                  break label22;
               }
            }

            color4 = ColorUtil.getThemeColor();
         }

         HudRectStyle style = this.getHudRectStyle();
         this.drawRow(
            context,
            matrices,
            width,
            width2,
            width6,
            width7,
            width3,
            color4,
            style,
            Items.FIREWORK_ROCKET.getDefaultStack(),
            name,
            font
         );
         float width8 = width2
            + width7
            + 2.0F;
         this.drawRow(
            context,
            matrices,
            width,
            width8,
            width6,
            width7,
            width3,
            color4,
            style,
            Items.TOTEM_OF_UNDYING.getDefaultStack(),
            name2,
            font
         );
         float width9 = width7
               * 2.0F
            + 2.0F;
         this.draggable
            .setWidth(
               width6
            );
         this.draggable
            .setHeight(
               width9
            );
         super.onRender(
            event
         );
      }
   }

   private void drawRow(
      DrawContext context,
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n,
      HudRectStyle style,
      ItemStack stack,
      String str,
      Font font
   ) {
      this.drawHudBackground(
         matrices,
         f,
         f2,
         20.0F,
         f4,
         n
      );
      float width = 16.0F;
      float width2 = (
            20.0F
               - width
         )
         * 0.5F;
      float width3 = (
            f4
               - width
         )
         * 0.5F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      matrices.push();
      matrices.translate(
         f
            + width2,
         f2
            + width3,
         0.0F
      );
      context.drawItem(
         stack,
         0,
         0
      );
      matrices.pop();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      float width4 = f
         + 20.0F
         + 2.0F;
      this.drawHudBackground(
         matrices,
         width4,
         f2,
         f5,
         f4,
         n
      );
      if (font
         != null) {
         float width5 = font.getWidth(
            str
         );
         float width6 = font.getHeight();
         float width7 = width4
            + (
                  f5
                     - width5
               )
               * 0.5F;
         float width8 = f2
            + (
                  f4
                     - width6
               )
               * 1.5F;
         font.draw(
            matrices,
            str,
            width7,
            width8,
            ColorUtil.rgba(
               255,
               255,
               255,
               255
            )
         );
      }
   }

   private int countItem(
      Item item
   ) {
      if (mc.player == null) {
         return 0;
      } else {
         int i = 0;

         for (int i2 = 0;
            i2
               < mc.player.getInventory().size();
            i2++
         ) {
            ItemStack stack = mc.player
               .getInventory()
               .getStack(
                  i2
               );
            if (!stack.isEmpty()
               && stack.getItem()
                  == item
               )
             {
               i += stack.getCount();
            }
         }

         return i;
      }
   }
}
