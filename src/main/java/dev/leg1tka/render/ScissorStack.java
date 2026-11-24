package dev.leg1tka.render;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import com.google.common.collect.Lists;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL30;

public class ScissorStack {
   private static CloneHelper state = new CloneHelper();
   private static final List<CloneHelper> stateStack = Lists.newArrayList();

   public static void push() {
      stateStack.add(state.clone());
   }

   public static void pop() {
      if (!stateStack.isEmpty()) {
         state = stateStack.remove(stateStack.size() - 1);
         if (state.enabled) {
            GL30.glEnable(3089);
            GL30.glScissor(state.x, state.y, state.width, state.height);
         } else {
            GL30.glDisable(3089);
         }
      }
   }

   public static void unset() {
      GL30.glDisable(3089);
      state.enabled = false;
   }

   private static Window getWindow() {
      MinecraftClient val = MinecraftClient.getInstance();
      return val
            == null
         ? null
         : val.getWindow();
   }

   private static double getScaleFactor() {
      Window val = getWindow();
      return val
            == null
         ? 1.0
         : val.getScaleFactor();
   }

   public static void setFromComponentCoordinates(
      int n,
      int n2,
      int n3,
      int n4
   ) {
      Window val = getWindow();
      if (val
         != null) {
         double d = getScaleFactor();
         int height = (int)(
            (double)n
               * d
         );
         int height2 = (int)(
            (double)n2
               * d
         );
         int height3 = (int)(
            (double)n3
               * d
         );
         int height4 = (int)(
            (double)n4
               * d
         );
         height2 = val.getHeight()
            - height2
            - height4;
         set(
            height,
            height2,
            height3,
            height4
         );
      }
   }

   public static void setFromComponentCoordinates(
      double d,
      double d2,
      double d3,
      double d4
   ) {
      Window val = getWindow();
      if (val
         != null) {
         double d5 = getScaleFactor();
         int height = (int)(
            d
               * d5
         );
         int height2 = (int)(
            d2
               * d5
         );
         int height3 = (int)(
            d3
               * d5
         );
         int height4 = (int)(
            d4
               * d5
         );
         height2 = val.getHeight()
            - height2
            - height4;
         set(
            height,
            height2,
            height3,
            height4
         );
      }
   }

   public static void setFromComponentCoordinates(
      double d,
      double d2,
      double d3,
      double d4,
      float f
   ) {
      Window val = getWindow();
      if (val
         != null) {
         double d5 = getScaleFactor();
         float width = (
               1.0F
                  - f
            )
            / 2.0F;
         double d6 = d
            + d3
               * (double)width;
         double d7 = d2
            + d4
               * (double)width;
         double d8 = d3
            * (double)f;
         double d9 = d4
            * (double)f;
         d6 = d6
               * (double)f
            + (
                  (double)val.getScaledWidth()
                     - d8
               )
               * (double)width;
         int width2 = (int)(
            d6
               * d5
         );
         int width3 = (int)(
            d7
               * d5
         );
         int width4 = (int)(
            d8
               * d5
         );
         int width5 = (int)(
            d9
               * d5
         );
         width3 = val.getHeight()
            - width3
            - width5;
         set(
            width2,
            width3,
            width4,
            width5
         );
      }
   }

   public static void set(
      int n,
      int n2,
      int n3,
      int n4
   ) {
      Window val = getWindow();
      if (val
         != null) {
         Rectangle val2 = new Rectangle(
            0,
            0,
            val.getWidth(),
            val.getHeight()
         );
         Rectangle val3;
         if (state.enabled) {
            val3 = new Rectangle(
               state.x, state.y, state.width, state.height
            );
         } else {
            val3 = val2;
         }

         Rectangle val4 = new Rectangle(
            n
               + state.transX,
            n2
               + state.transY,
            n3,
            n4
         );
         Rectangle val5 = val3.intersection(
            val4
         );
         val5 = val5.intersection(
            val2
         );
         if (val5.width
            < 0) {
            val5.width = 0;
         }

         if (val5.height
            < 0) {
            val5.height = 0;
         }

         state.enabled = true;
         state.x = val5.x;
         state.y = val5.y;
         state.width = val5.width;
         state.height = val5.height;
         GL30.glEnable(3089);
         GL30.glScissor(
            val5.x,
            val5.y,
            val5.width,
            val5.height
         );
      }
   }

   public static void translate(
      int n,
      int n2
   ) {
      state.transX = n;
      state.transY = n2;
   }

   public static void translateFromComponentCoordinates(
      int n,
      int n2
   ) {
      Window val = getWindow();
      if (val
         != null) {
         int height = val.getScaledHeight();
         double d = getScaleFactor();
         int height2 = (int)(
            (double)n
               * d
         );
         int height3 = (int)(
            (double)n2
               * d
         );
         height3 = (int)(
               (double)height
                  * d
            )
            - height3;
         translate(
            height2,
            height3
         );
      }
   }
}
