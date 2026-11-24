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


import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ArmorStatusHud extends HudElement {
   public ArmorStatusHud(
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
         ItemStack stack = mc.player
            .getInventory()
            .getArmorStack(3);
         ItemStack stack2 = mc.player
            .getInventory()
            .getArmorStack(2);
         ItemStack stack3 = mc.player
            .getInventory()
            .getArmorStack(1);
         ItemStack stack4 = mc.player
            .getInventory()
            .getArmorStack(0);
         ItemStack stack5 = mc.player
            .getOffHandStack();
         float width3 = 18.0F;
         float width4 = 2.0F;
         float width5 = 2.0F;
         float width6 = 1.0F;
         int i;
         if (!AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme()
            .getName()
            .equals(
               "Rainbow"
            )) {
            i = AspectDLC.INSTANCE
               .themeStorage
               .getThemes()
               .getTheme()
               .color[0];
         } else {
            i = ColorUtil.getThemeColor();
         }

         ItemStack[] var10000 = new ItemStack[5];
         var10000[0] = stack;
         var10000[1] = stack2;
         var10000[2] = stack3;
         var10000[3] = stack4;
         var10000[4] = stack5;
         ItemStack[] stack6 = var10000;
         float width7 = width;
         float width8 = width2;

         for (ItemStack stack7 : stack6) {
            if (!stack7.isEmpty()
               )
             {
               Render2D.drawRoundedRect(
                  matrices,
                  width7,
                  width8,
                  width3,
                  width3,
                  2.0F,
                  ColorUtil.rgba(
                     30,
                     30,
                     30,
                     180
                  )
               );
               int i2 = ColorUtil.applyAlpha(
                  i,
                  0.5F
               );
               Render2D.drawRoundedRectOutline(
                  matrices,
                  width7,
                  width8,
                  width3,
                  width3,
                  2.0F,
                  1.0F,
                  i2,
                  i2,
                  i2,
                  i2
               );
               matrices.push();
               matrices.translate(
                  width7
                     + 1.0F,
                  width8
                     + 1.0F,
                  0.0F
               );
               context.drawItem(
                  stack7,
                  0,
                  0
               );
               matrices.pop();
               if (stack7.isDamageable()
                  )
                {
                  int i3 = stack7.getMaxDamage();
                  int i4 = stack7.getDamage();
                  float width9 = 1.0F
                     - (float)i4
                        / (float)i3;
                  width9 = MathHelper.clamp(
                     width9,
                     0.0F,
                     1.0F
                  );
                  float width10 = width8
                     + width3
                     + width6;
                  Render2D.drawRoundedRect(
                     matrices,
                     width7,
                     width10,
                     width3,
                     width5,
                     1.0F,
                     ColorUtil.rgba(
                        40,
                        40,
                        40,
                        200
                     )
                  );
                  int i5;
                  if (width9
                     > 0.5F) {
                     i5 = ColorUtil.rgba(
                        85,
                        255,
                        85,
                        255
                     );
                  } else if (width9
                     > 0.25F) {
                     i5 = ColorUtil.rgba(
                        255,
                        255,
                        85,
                        255
                     );
                  } else {
                     i5 = ColorUtil.rgba(
                        255,
                        85,
                        85,
                        255
                     );
                  }

                  if (width9
                     > 0.01F) {
                     Render2D.drawRoundedRect(
                        matrices,
                        width7,
                        width10,
                        width3
                           * width9,
                        width5,
                        1.0F,
                        i5
                     );
                  }
               }

               width7 += width3
                  + width4;
            }
         }

         float width11 = (
                  width3
                     + width4
               )
               * 5.0F
            - width4;
         float width12 = width3
            + width6
            + width5;
         this.draggable
            .setWidth(
               width11
            );
         this.draggable
            .setHeight(
               width12
            );
         super.onRender(
            event
         );
      }
   }
}
