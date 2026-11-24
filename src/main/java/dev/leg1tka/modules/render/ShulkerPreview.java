package dev.leg1tka.modules.render;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class ShulkerPreview extends Module {
   public static ShulkerPreview INSTANCE = new ShulkerPreview();
   private static final float RECT_RADIUS = 5.0F;
   private static final int SLOT_SIZE = 18;
   private static final int PADDING = 7;
   private static final int ROWS = 3;
   private static final int COLS = 9;
   private static final int TITLE_HEIGHT = 14;
   private static final int SLOT_BG_COLOR = -7631989;
   private Field guiLeftField;
   private Field guiTopField;
   private static ShulkerPreview instance;

   public ShulkerPreview() {
      super("ShulkerPreview", Category.RENDER);
      String var1 = "ShulkerPreview";
      this.initReflection();
      instance = this;
   }

   public static ShulkerPreview getInstance() {
      return instance;
   }

   private void initReflection() {
      try {
         for (Field val : HandledScreen.class
            .getDeclaredFields()) {
            if (val.getType()
               == int.class) {
               val.setAccessible(
                  true
               );
               String name = val.getName();
               if (!name.equals(
                  "x"
               )) {
                  if (!name.equals(
                     "field_2776"
                  )) {
                     if (!name.contains(
                        "Left"
                     )) {
                        if (!name.contains(
                           "guiLeft"
                        )) {
                           if (!name.equals(
                              "y"
                           )) {
                              if (!name.equals(
                                 "field_2800"
                              )) {
                                 if (!name.contains(
                                    "Top"
                                 )) {
                                    if (!name.contains(
                                       "guiTop"
                                    )) {
                                       continue;
                                    }
                                 }
                              }
                           }

                           this.guiTopField = val;
                           continue;
                        }
                     }
                  }
               }

               this.guiLeftField = val;
            }
         }
      } catch (Exception var6) {
      }
   }

   private int getGuiLeft(
      HandledScreen<?> val
   ) {
      try {
         if (this.guiLeftField != null) {
            return this.guiLeftField
               .getInt(
                  val
               );
         }
      } catch (Exception var3) {
      }

      return (mc.getWindow().getScaledWidth() - 176)
         / 2;
   }

   private int getGuiTop(
      HandledScreen<?> val
   ) {
      try {
         if (this.guiTopField != null) {
            return this.guiTopField
               .getInt(
                  val
               );
         }
      } catch (Exception var3) {
      }

      return (mc.getWindow().getScaledHeight() - 166)
         / 2;
   }

   public void renderFromMixin(
      DrawContext val,
      int val2,
      int val3
   ) {
      if (this.isEnable()) {
         if (mc != null && mc.player != null && mc.currentScreen != null) {
            if (mc.currentScreen instanceof HandledScreen<?> val4
               )
             {
               long val5 = mc.getWindow()
                  .getHandle();
               boolean flag = GLFW.glfwGetKey(
                     val5,
                     341
                  )
                  == 1;
               if (flag
                  )
                {
                  Slot val6 = this.getHoveredSlot(
                     val4
                  );
                  if (val6
                     != null) {
                     ItemStack stack = val6.getStack();
                     if (this.isShulkerBox(
                        stack
                     )) {
                        ContainerComponent val7 = (ContainerComponent)stack.get(
                           DataComponentTypes.CONTAINER
                        );
                        if (val7
                           != null) {
                           this.renderShulkerPreview(
                              val,
                              stack,
                              val7,
                              (float)val2,
                              (float)val3
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private Slot getHoveredSlot(
      HandledScreen<?> val
   ) {
      try {
         ScreenHandler hand = val.getScreenHandler();
         if (hand
               == null
            || hand.slots
               == null) {
            return null;
         }

         double d = mc.mouse
               .getX()
            * (double)mc.getWindow().getScaledWidth()
            / (double)mc.getWindow().getWidth();
         double d2 = mc.mouse
               .getY()
            * (double)mc.getWindow().getScaledHeight()
            / (double)mc.getWindow().getHeight();
         int n = this.getGuiLeft(
            val
         );
         int n2 = this.getGuiTop(
            val
         );

         for (Slot val2 : hand.slots) {
            int n3 = n
               + val2.x;
            int n4 = n2
               + val2.y;
            if (d
                  >= (double)n3
               && d
                  < (double)(
                     n3
                        + 16
                  )
               && d2
                  >= (double)n4
               && d2
                  < (double)(
                     n4
                        + 16
                  )) {
               return val2;
            }
         }
      } catch (Exception var13) {
      }

      return null;
   }

   private boolean isShulkerBox(
      ItemStack val
   ) {
      return val
               != null
            && !val.isEmpty()
         ? val.getItem()
               == Items.SHULKER_BOX
            || val.getItem()
               == Items.WHITE_SHULKER_BOX
            || val.getItem()
               == Items.ORANGE_SHULKER_BOX
            || val.getItem()
               == Items.MAGENTA_SHULKER_BOX
            || val.getItem()
               == Items.LIGHT_BLUE_SHULKER_BOX
            || val.getItem()
               == Items.YELLOW_SHULKER_BOX
            || val.getItem()
               == Items.LIME_SHULKER_BOX
            || val.getItem()
               == Items.PINK_SHULKER_BOX
            || val.getItem()
               == Items.GRAY_SHULKER_BOX
            || val.getItem()
               == Items.LIGHT_GRAY_SHULKER_BOX
            || val.getItem()
               == Items.CYAN_SHULKER_BOX
            || val.getItem()
               == Items.PURPLE_SHULKER_BOX
            || val.getItem()
               == Items.BLUE_SHULKER_BOX
            || val.getItem()
               == Items.BROWN_SHULKER_BOX
            || val.getItem()
               == Items.GREEN_SHULKER_BOX
            || val.getItem()
               == Items.RED_SHULKER_BOX
            || val.getItem()
               == Items.BLACK_SHULKER_BOX
         : false;
   }

   private int getShulkerColor(
      ItemStack val
   ) {
      if (val.getItem()
         == Items.SHULKER_BOX) {
         return -6394435;
      } else if (val.getItem()
         == Items.WHITE_SHULKER_BOX) {
         return -1;
      } else if (val.getItem()
         == Items.ORANGE_SHULKER_BOX) {
         return -425955;
      } else if (val.getItem()
         == Items.MAGENTA_SHULKER_BOX) {
         return -3715395;
      } else if (val.getItem()
         == Items.LIGHT_BLUE_SHULKER_BOX) {
         return -12930086;
      } else if (val.getItem()
         == Items.YELLOW_SHULKER_BOX) {
         return -75715;
      } else if (val.getItem()
         == Items.LIME_SHULKER_BOX) {
         return -8337633;
      } else if (val.getItem()
         == Items.PINK_SHULKER_BOX) {
         return -816214;
      } else if (val.getItem()
         == Items.GRAY_SHULKER_BOX) {
         return -12103854;
      } else if (val.getItem()
         == Items.LIGHT_GRAY_SHULKER_BOX) {
         return -6447721;
      } else if (val.getItem()
         == Items.CYAN_SHULKER_BOX) {
         return -15295332;
      } else if (val.getItem()
         == Items.PURPLE_SHULKER_BOX) {
         return -7785800;
      } else if (val.getItem()
         == Items.BLUE_SHULKER_BOX) {
         return -12827478;
      } else if (val.getItem()
         == Items.BROWN_SHULKER_BOX) {
         return -8170446;
      } else if (val.getItem()
         == Items.GREEN_SHULKER_BOX) {
         return -10585066;
      } else if (val.getItem()
         == Items.RED_SHULKER_BOX) {
         return -5231066;
      } else {
         return val.getItem()
               == Items.BLACK_SHULKER_BOX
            ? -14869215
            : -6394435;
      }
   }

   private void renderShulkerPreview(
      DrawContext val,
      ItemStack val2,
      ContainerComponent val3,
      float val4,
      float val5
   ) {
      MatrixStack matrices = val.getMatrices();
      int n = val.getScaledWindowWidth();
      int n2 = val.getScaledWindowHeight();
      float f = 162.0F;
      float f2 = 54.0F;
      float f3 = f
         + 14.0F;
      float f4 = f2
         + 14.0F
         + 14.0F;
      float f5 = val4
         + 12.0F;
      float f6 = val5
         - 12.0F;
      if (f5
            + f3
         > (float)n
         )
       {
         f5 = val4
            - f3
            - 4.0F;
      }

      if (f6
            + f4
         > (float)n2
         )
       {
         f6 = (float)n2
            - f4
            - 4.0F;
      }

      if (f6
         < 4.0F) {
         f6 = 4.0F;
      }

      if (f5
         < 4.0F) {
         f5 = 4.0F;
      }

      int color = this.getShulkerColor(
         val2
      );
      int color2 = ColorUtil.applyAlpha(
         color,
         0.85F
      );
      int color3 = this.darkenColor(
         color,
         0.6F
      );
      int color4 = this.lightenColor(
         color,
         1.3F
      );
      matrices.push();
      GL11.glClear(256);
      RenderSystem.disableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      matrices.translate(
         0.0F, 0.0F, 500.0F
      );
      Render2D.drawBlur(
         matrices,
         f5
            - 2.0F,
         f6
            - 2.0F,
         f3
            + 4.0F,
         f4
            + 4.0F,
         7.0F,
         8.0F,
         -1
      );
      val.fill(
         (int)f5,
         (int)f6,
         (int)(
            f5
               + f3
         ),
         (int)(
            f6
               + f4
         ),
         color2
      );
      val.fill(
         (int)f5,
         (int)f6,
         (int)(
            f5
               + f3
         ),
         (int)(
            f6
               + 2.0F
         ),
         color4
      );
      val.fill(
         (int)f5,
         (int)(
            f6
               + f4
               - 2.0F
         ),
         (int)(
            f5
               + f3
         ),
         (int)(
            f6
               + f4
         ),
         color3
      );
      val.fill(
         (int)f5,
         (int)f6,
         (int)(
            f5
               + 2.0F
         ),
         (int)(
            f6
               + f4
         ),
         color4
      );
      val.fill(
         (int)(
            f5
               + f3
               - 2.0F
         ),
         (int)f6,
         (int)(
            f5
               + f3
         ),
         (int)(
            f6
               + f4
         ),
         color3
      );
      Font font = Fonts.getFont(
         "sf_regular", 12
      );
      if (font
         != null) {
         String name = val2.getName()
            .getString();
         float f7 = f5
            + 7.0F;
         float f8 = f6
            + 7.0F
            - 1.0F;
         int color5 = this.isColorDark(
               color
            )
            ? -1
            : -15066598;
         font.drawString(
            matrices,
            name,
            f7,
            f8,
            color5
         );
      }

      float f9 = f5
         + 7.0F;
      float f10 = f6
         + 7.0F
         + 14.0F
         - 2.0F;
      int color6 = this.darkenColor(
         color,
         0.5F
      );
      val.fill(
         (int)(
            f9
               - 1.0F
         ),
         (int)(
            f10
               - 1.0F
         ),
         (int)(
            f9
               + f
               + 1.0F
         ),
         (int)(
            f10
               + f2
               + 1.0F
         ),
         color6
      );
      List<ItemStack> stack = new ArrayList<>();
      val3.stream()
         .forEach(
            stack::add
         );

      for (int i = 0;
         i
            < 27;
         i++
      ) {
         int n3 = i
            / 9;
         int n4 = i
            % 9;
         int n5 = (int)(
            f9
               + (float)(
                  n4
                     * 18
               )
         );
         int n6 = (int)(
            f10
               + (float)(
                  n3
                     * 18
               )
         );
         val.fill(
            n5,
            n6,
            n5
               + 18
               - 2,
            n6
               + 18
               - 2,
            -7631989
         );
         val.fill(
            n5,
            n6,
            n5
               + 18
               - 2,
            n6
               + 1,
            -11184811
         );
         val.fill(
            n5,
            n6,
            n5
               + 1,
            n6
               + 18
               - 2,
            -11184811
         );
         val.fill(
            n5,
            n6
               + 18
               - 3,
            n5
               + 18
               - 2,
            n6
               + 18
               - 2,
            -1
         );
         val.fill(
            n5
               + 18
               - 3,
            n6,
            n5
               + 18
               - 2,
            n6
               + 18
               - 2,
            -1
         );
         if (i
            < stack.size()
            )
          {
            ItemStack stack2 = stack.get(
               i
            );
            if (!stack2.isEmpty()
               )
             {
               val.drawItem(
                  stack2,
                  n5,
                  n6
               );
               val.drawStackOverlay(
                  mc.textRenderer,
                  stack2,
                  n5,
                  n6
               );
            }
         }
      }

      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
      matrices.pop();
   }

   private int darkenColor(
      int val,
      float val2
   ) {
      int n = val
            >> 24
         & 255;
      int n2 = (int)(
         (float)(
               val
                     >> 16
                  & 255
            )
            * val2
      );
      int n3 = (int)(
         (float)(
               val
                     >> 8
                  & 255
            )
            * val2
      );
      int n4 = (int)(
         (float)(
               val
                  & 255
            )
            * val2
      );
      return n
            << 24
         | Math.min(
               255,
               n2
            )
            << 16
         | Math.min(
               255,
               n3
            )
            << 8
         | Math.min(
            255,
            n4
         );
   }

   private int lightenColor(
      int val,
      float val2
   ) {
      int n = val
            >> 24
         & 255;
      int n2 = (int)Math.min(
         255.0F,
         (float)(
               val
                     >> 16
                  & 255
            )
            * val2
      );
      int n3 = (int)Math.min(
         255.0F,
         (float)(
               val
                     >> 8
                  & 255
            )
            * val2
      );
      int n4 = (int)Math.min(
         255.0F,
         (float)(
               val
                  & 255
            )
            * val2
      );
      return n
            << 24
         | n2
            << 16
         | n3
            << 8
         | n4;
   }

   private boolean isColorDark(
      int val
   ) {
      int n = val
            >> 16
         & 255;
      int n2 = val
            >> 8
         & 255;
      int n3 = val
         & 255;
      double d = (
            0.299
                  * (double)n
               + 0.587
                  * (double)n2
               + 0.114
                  * (double)n3
         )
         / 255.0;
      return d
         < 0.5;
   }
}
