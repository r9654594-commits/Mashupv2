package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;


import net.minecraft.util.math.MathHelper;

public class UpdateHelper {
   private float currentValue;
   private float targetValue;
   private float fromValue;
   private float speed;
   private EasingFunction easing;
   private long startTime;
   private double duration;
   private boolean isRunning;

   public UpdateHelper(
      float f,
      float f2,
      EasingFunction val
   ) {
      this.currentValue = f;
      this.targetValue = f;
      this.fromValue = f;
      this.speed = f2;
      this.easing = val
            != null
         ? val
         : EasingFunctions.LINEAR;
      this.startTime = 0L;
      this.duration = 0.0;
      this.isRunning = false;
   }

   public UpdateHelper(
      float f,
      float f2
   ) {
      this(
         f,
         f2,
         EasingFunctions.LINEAR
      );
   }

   public void update(
      float f
   ) {
      if (this.targetValue
            != f
         || !this.isRunning) {
         this.targetValue = f;
         this.fromValue = this.currentValue;
         this.startTime = System.nanoTime();
         this.duration = 1.0 / (double)this.speed * 2.0;
         this.isRunning = true;
      }

      if (this.isDone()) {
         this.currentValue = this.targetValue;
         this.isRunning = false;
      } else {
         double y = this.calculatePart();
         float y2 = (float)this.easing
            .ease(
               y
            );
         this.currentValue = MathHelper.lerp(
            y2,
            this.fromValue,
            this.targetValue
         );
      }
   }

   private double calculatePart() {
      if (!this.isRunning) {
         return 1.0;
      } else {
         long time = System.nanoTime();
         double y = (double)(
               time
                  - this.startTime
            )
            / 1.0E9;
         return MathHelper.clamp(
            y
               / this.duration,
            0.0,
            1.0
         );
      }
   }

   public float getValue() {
      return this.currentValue;
   }

   public void setValue(
      float f
   ) {
      this.currentValue = f;
      this.targetValue = f;
      this.fromValue = f;
      this.isRunning = false;
   }

   public float getTarget() {
      return this.targetValue;
   }

   public void setSpeed(
      float f
   ) {
      this.speed = f;
      this.duration = 1.0
         / (double)f;
   }

   public void setEasing(
      EasingFunction val
   ) {
      this.easing = val
            != null
         ? val
         : EasingFunctions.LINEAR;
   }

   public boolean isDone() {
      return this.calculatePart() >= 1.0;
   }

   public boolean isAlive() {
      return !this.isDone();
   }
}
