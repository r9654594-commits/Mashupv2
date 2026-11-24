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


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class Draggable implements IMinecraft {
   @Expose
   @SerializedName("x")
   private float xPos;
   @Expose
   @SerializedName("y")
   private float yPos;
   public float initialXVal;
   public float initialYVal;
   private float startX;
   private float startY;
   private boolean dragging;
   private float width;
   private float height;
   @Expose
   @SerializedName("name")
   private String name;
   private final Module module;
   private float targetXPos;
   private float targetYPos;
   private static final float CENTER_LINE_WIDTH = 1.0F;
   private static final float SNAP_THRESHOLD = 10.0F;
   private float lineAlpha = 0.0F;
   private long lastUpdateTime;
   private boolean snapToCenter;
   private boolean snapToCenterx;
   private boolean snapToCenter2x;
   private boolean snapToCenter3x;
   private boolean snapToCenter4x;
   private boolean snapToCenter5x;
   private boolean snapToCenter2;
   private boolean snapToCenter3;
   private boolean snapToCenter4;
   private boolean snapToCenter5;
   private static final float LERP_SPEED = 0.19F;
   private static final float MAX_TILT_DEGREES = 25.0F;
   private static final float TILT_FROM_MOUSE_DELTA = 4.0F;
   private static final float DRAG_TILT_LERP = 0.14F;
   private static final float RELEASE_TILT_LERP = 0.1F;
   private static final float TILT_DELTA_SMOOTHING = 0.18F;
   private static final float TILT_TARGET_SMOOTHING = 0.22F;
   private static final float TILT_DEADZONE = 0.18F;
   private static final float DRAG_SCALE_MULTIPLIER = 1.01F;
   private static final float DRAG_SCALE_LERP = 0.1F;
   private static final float RELEASE_SCALE_LERP = 0.02F;
   private float dragTiltDegrees;
   private float targetTiltDegrees;
   private float smoothedMouseDeltaX;
   private float lastDragMouseX;
   private boolean hasLastDragMouseX;
   private boolean tiltMatrixPushed;
   private float dragScale = 1.0F;
   private float targetScale = 1.0F;

   public Draggable(
      Module val,
      String str,
      float f,
      float f2
   ) {
      this.module = val;
      this.name = str;
      this.xPos = f;
      this.yPos = f2;
      this.initialXVal = f;
      this.initialYVal = f2;
   }

   public float getX() {
      return this.xPos;
   }

   public void setX(
      float f
   ) {
      this.xPos = f;
   }

   public float getY() {
      return this.yPos;
   }

   public void setY(
      float f
   ) {
      this.yPos = f;
   }

   private GetxHelper_1 getMouse(
      int n,
      int n2
   ) {
      MinecraftClient val = MinecraftClient.getInstance();
      Window val2 = val
            == null
         ? null
         : val.getWindow();
      double x = val2
            == null
         ? 1.0
         : val2.getScaleFactor();
      return new GetxHelper_1(
         (int)(
            (double)n
               * x
               / 2.0
         ),
         (int)(
            (double)n2
               * x
               / 2.0
         )
      );
   }

   public final void onDraw(
      int n,
      int n2,
      Window val,
      MatrixStack matrices
   ) {
      GetxHelper_1 val2 = this.getMouse(
         n,
         n2
      );
      n = val2.getX();
      n2 = val2.getY();
      float width2 = (float)val.getScaledWidth()
         / 2.0F;
      float width3 = (float)val.getScaledHeight()
         / 2.0F;
      float width4 = (float)val.getScaledWidth()
         / 4.0F;
      float width5 = (float)val.getScaledHeight()
         / 4.0F;
      float width6 = (float)val.getScaledWidth()
         / 8.0F;
      float width7 = (float)val.getScaledHeight()
         / 8.0F;
      float width8 = (float)val.getScaledWidth()
         / 1.15F;
      float width9 = (float)val.getScaledHeight()
         / 1.15F;
      float width10 = (float)val.getScaledWidth()
         / 1.35F;
      float width11 = (float)val.getScaledHeight()
         / 1.35F;
      this.snapToCenter = this.snapToCenterx = this.snapToCenter2x = this.snapToCenter3x = this.snapToCenter4x = this.snapToCenter5x = this.snapToCenter2 = this.snapToCenter3 = this.snapToCenter4 = this.snapToCenter5 = false;
      if (this.dragging) {
         this.targetScale = 1.01F;
         if (this.hasLastDragMouseX) {
            float width12 = (float)n
               - this.lastDragMouseX;
            if (Math.abs(
                  width12
               )
               < 0.18F) {
               width12 = 0.0F;
            }

            this.smoothedMouseDeltaX = DirectionHelper.lerp(
               this.smoothedMouseDeltaX,
               width12,
               0.18F
            );
            float width13 = Math.max(
               -25.0F, Math.min(25.0F, this.smoothedMouseDeltaX * 4.0F)
            );
            this.targetTiltDegrees = DirectionHelper.lerp(
               this.targetTiltDegrees,
               width13,
               0.22F
            );
         }

         this.lastDragMouseX = (float)n;
         this.hasLastDragMouseX = true;
         this.targetXPos = (float)n
            - this.startX;
         this.targetYPos = (float)n2
            - this.startY;
         boolean flag = false;
         if (Math.abs(
               this.targetXPos
                  + this.width / 2.0F
                  - width2
            )
            < 10.0F) {
            this.targetXPos = width2
               - this.width / 2.0F;
            this.snapToCenterx = true;
            flag = true;
         }

         if (Math.abs(
               this.targetYPos
                  + this.height / 2.0F
                  - width3
            )
            < 10.0F) {
            this.targetYPos = width3
               - this.height / 2.0F;
            this.snapToCenter = true;
            flag = true;
         }

         if (Math.abs(
               this.targetXPos
                  + this.width / 2.0F
                  - width4
            )
            < 10.0F) {
            this.targetXPos = width4
               - this.width / 2.0F;
            this.snapToCenter2x = true;
            flag = true;
         }

         if (Math.abs(
               this.targetYPos
                  + this.height / 2.0F
                  - width5
            )
            < 10.0F) {
            this.targetYPos = width5
               - this.height / 2.0F;
            this.snapToCenter2 = true;
            flag = true;
         }

         if (Math.abs(
               this.targetXPos
                  + this.width / 2.0F
                  - width6
            )
            < 10.0F) {
            this.targetXPos = width6
               - this.width / 2.0F;
            this.snapToCenter3x = true;
            flag = true;
         }

         if (Math.abs(
               this.targetYPos
                  + this.height / 2.0F
                  - width7
            )
            < 10.0F) {
            this.targetYPos = width7
               - this.height / 2.0F;
            this.snapToCenter3 = true;
            flag = true;
         }

         if (Math.abs(
               this.targetXPos
                  + this.width / 2.0F
                  - width8
            )
            < 10.0F) {
            this.targetXPos = width8
               - this.width / 2.0F;
            this.snapToCenter4x = true;
            flag = true;
         }

         if (Math.abs(
               this.targetYPos
                  + this.height / 2.0F
                  - width9
            )
            < 10.0F) {
            this.targetYPos = width9
               - this.height / 2.0F;
            this.snapToCenter4 = true;
            flag = true;
         }

         if (Math.abs(
               this.targetXPos
                  + this.width / 2.0F
                  - width10
            )
            < 10.0F) {
            this.targetXPos = width10
               - this.width / 2.0F;
            this.snapToCenter5x = true;
            flag = true;
         }

         if (Math.abs(
               this.targetYPos
                  + this.height / 2.0F
                  - width11
            )
            < 10.0F) {
            this.targetYPos = width11
               - this.height / 2.0F;
            this.snapToCenter5 = true;
            flag = true;
         }

         if (this.targetXPos + this.width
            > (float)val.getScaledWidth()
            )
          {
            this.targetXPos = (float)val.getScaledWidth()
               - this.width;
         }

         if (this.targetYPos + this.height
            > (float)val.getScaledHeight()
            )
          {
            this.targetYPos = (float)val.getScaledHeight()
               - this.height;
         }

         if (this.targetXPos < 0.0F) {
            this.targetXPos = 0.0F;
         }

         if (this.targetYPos < 0.0F) {
            this.targetYPos = 0.0F;
         }

         this.xPos = DirectionHelper.lerp(this.xPos, this.targetXPos, 0.19F);
         this.yPos = DirectionHelper.lerp(this.yPos, this.targetYPos, 0.19F);
         this.updateLineAlpha(
            flag
         );
      } else {
         this.targetScale = 1.0F;
         this.targetTiltDegrees = 0.0F;
         this.smoothedMouseDeltaX = DirectionHelper.lerp(this.smoothedMouseDeltaX, 0.0F, 0.18F);
         this.hasLastDragMouseX = false;
         this.updateLineAlpha(false);
      }

      this.updateTilt();
      this.drawCenterLines(
         matrices,
         val
      );
   }

   private void updateTilt() {
      float f = this.dragging
         ? 0.14F
         : 0.1F;
      this.dragTiltDegrees = DirectionHelper.lerp(
         this.dragTiltDegrees,
         this.targetTiltDegrees,
         f
      );
      if (!this.dragging && Math.abs(this.dragTiltDegrees) < 0.02F) {
         this.dragTiltDegrees = 0.0F;
      }

      float f2 = this.dragging
         ? 0.1F
         : 0.02F;
      this.dragScale = DirectionHelper.lerp(
         this.dragScale,
         this.targetScale,
         f2
      );
      if (!this.dragging && Math.abs(this.dragScale - 1.0F) < 0.002F) {
         this.dragScale = 1.0F;
      }
   }

   public void beginRenderTilt(
      MatrixStack matrices
   ) {
      this.tiltMatrixPushed = false;
   }

   public void endRenderTilt(
      MatrixStack matrices
   ) {
      this.tiltMatrixPushed = false;
   }

   private void updateLineAlpha(
      boolean flag
   ) {
      long time = System.currentTimeMillis();
      float alpha = (float)(
            time
               - this.lastUpdateTime
         )
         / 1000.0F;
      this.lastUpdateTime = time;
      float alpha2 = 2.0F;
      float alpha3 = 2.0F;
      if (flag
         )
       {
         this.lineAlpha += alpha
            * alpha2;
         if (this.lineAlpha > 1.0F) {
            this.lineAlpha = 1.0F;
         }
      } else {
         this.lineAlpha -= alpha
            * alpha3;
         if (this.lineAlpha < 0.0F) {
            this.lineAlpha = 0.0F;
         }
      }
   }

   private void drawCenterLines(
      MatrixStack matrices,
      Window val
   ) {
      if (this.lineAlpha > 0.0F) {
         float width = (float)val.getScaledWidth()
            / 2.0F;
         float width2 = (float)val.getScaledHeight()
            / 2.0F;
         float width3 = (float)val.getScaledWidth()
            / 4.0F;
         float width4 = (float)val.getScaledHeight()
            / 4.0F;
         float width5 = (float)val.getScaledWidth()
            / 8.0F;
         float width6 = (float)val.getScaledHeight()
            / 8.0F;
         float width7 = (float)val.getScaledWidth()
            / 1.15F;
         float width8 = (float)val.getScaledHeight()
            / 1.15F;
         float width9 = (float)val.getScaledWidth()
            / 1.35F;
         float width10 = (float)val.getScaledHeight()
            / 1.35F;
         int alpha = (int)(
                  this.lineAlpha * 255.0F
               )
               << 24
            | 16777215;
         if (this.snapToCenterx) {
            Render2D.drawRoundedRect(
               matrices,
               width
                  - 0.33333334F,
               0.0F,
               1.0F,
               (float)val.getScaledHeight(),
               1.0F,
               alpha
            );
         }

         if (this.snapToCenter) {
            Render2D.drawRoundedRect(
               matrices,
               0.0F,
               width2
                  - 0.33333334F,
               (float)val.getScaledWidth(),
               1.0F,
               1.0F,
               alpha
            );
         }

         if (this.snapToCenter2x) {
            Render2D.drawRoundedRect(
               matrices,
               width3
                  - 0.33333334F,
               0.0F,
               1.0F,
               (float)val.getScaledHeight(),
               1.0F,
               alpha
            );
         }

         if (this.snapToCenter2) {
            Render2D.drawRoundedRect(
               matrices,
               0.0F,
               width4
                  - 0.33333334F,
               (float)val.getScaledWidth(),
               1.0F,
               1.0F,
               alpha
            );
         }

         if (this.snapToCenter3x) {
            Render2D.drawRoundedRect(
               matrices,
               width5
                  - 0.33333334F,
               0.0F,
               1.0F,
               (float)val.getScaledHeight(),
               1.0F,
               alpha
            );
         }

         if (this.snapToCenter3) {
            Render2D.drawRoundedRect(
               matrices,
               0.0F,
               width6
                  - 0.33333334F,
               (float)val.getScaledWidth(),
               1.0F,
               1.0F,
               alpha
            );
         }

         if (this.snapToCenter4x) {
            Render2D.drawRoundedRect(
               matrices,
               width7
                  - 0.33333334F,
               0.0F,
               1.0F,
               (float)val.getScaledHeight(),
               1.0F,
               alpha
            );
         }

         if (this.snapToCenter4) {
            Render2D.drawRoundedRect(
               matrices,
               0.0F,
               width8
                  - 0.33333334F,
               (float)val.getScaledWidth(),
               1.0F,
               1.0F,
               alpha
            );
         }

         if (this.snapToCenter5x) {
            Render2D.drawRoundedRect(
               matrices,
               width9
                  - 0.33333334F,
               0.0F,
               1.0F,
               (float)val.getScaledHeight(),
               1.0F,
               alpha
            );
         }

         if (this.snapToCenter5) {
            Render2D.drawRoundedRect(
               matrices,
               0.0F,
               width10
                  - 0.33333334F,
               (float)val.getScaledWidth(),
               1.0F,
               1.0F,
               alpha
            );
         }
      }
   }

   public final boolean onClick(
      double d,
      double d2,
      int n
   ) {
      if (n
            == 0
         && HoverUtil.isInRegion(
            d,
            d2,
            this.xPos,
            this.yPos,
            this.width,
            this.height
         )) {
         this.dragging = true;
         this.targetScale = 1.01F;
         this.startX = (float)(
            (int)(
               d
                  - (double)this.xPos
            )
         );
         this.startY = (float)(
            (int)(
               d2
                  - (double)this.yPos
            )
         );
         this.smoothedMouseDeltaX = 0.0F;
         this.hasLastDragMouseX = false;
         this.lastUpdateTime = System.currentTimeMillis();
         return true;
      } else {
         return false;
      }
   }

   public final void onRelease(
      int n
   ) {
      if (n
         == 0) {
         this.dragging = false;
         this.targetScale = 1.0F;
         this.targetTiltDegrees = 0.0F;
         this.smoothedMouseDeltaX = 0.0F;
         this.hasLastDragMouseX = false;
      }
   }

   @Generated
   public void setWidth(
      float f
   ) {
      this.width = f;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public void setHeight(
      float f
   ) {
      this.height = f;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public Module getModule() {
      return this.module;
   }
}
