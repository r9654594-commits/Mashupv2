package dev.leg1tka.animation;
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
import dev.leg1tka.utils.*;


import lombok.Generated;

public class Animation {
   private long duration;
   private float value;
   private Easing easing;
   private long startTime;
   private float startValue;
   private float targetValue;
   private boolean done;
   private boolean direction;

   public Animation(
      long time,
      float f,
      Easing val
   ) {
      this.duration = time;
      this.easing = val;
      this.value = f;
      this.startValue = f;
      this.targetValue = f;
      this.done = true;
   }

   public Animation(
      Easing val,
      long time
   ) {
      this(
         time,
         0.0F,
         val
      );
   }

   public void run(
      boolean flag
   ) {
      this.run(
         flag
            ? 1.0F
            : 0.0F
      );
   }

   public float run(
      float f
   ) {
      long time = System.currentTimeMillis();
      if (f
         != this.targetValue) {
         this.startValue = this.value;
         this.targetValue = f;
         this.startTime = time;
         this.done = false;
      }

      long time2 = time
         - this.startTime;
      if (time2
         >= this.duration) {
         this.value = this.targetValue;
         this.done = true;
         return this.value;
      } else {
         float y = (float)time2
            / (float)this.duration;
         float y2 = this.easing
            .ease(
               y,
               0.0F,
               1.0F,
               1.0F
            );
         this.value = this.startValue
            + (this.targetValue - this.startValue)
               * y2;
         return this.value;
      }
   }

   public void setValue(
      float f
   ) {
      this.value = f;
      this.startValue = f;
      this.targetValue = f;
      this.done = true;
   }

   public void reset(
      float f
   ) {
      this.value = f;
      this.startValue = f;
      this.targetValue = f;
      this.done = true;
   }

   public void reset() {
      this.reset(0.0F);
   }

   public void animateTo(
      float f
   ) {
      if (f
         != this.targetValue) {
         this.startValue = this.value;
         this.targetValue = f;
         this.startTime = System.currentTimeMillis();
         this.done = false;
      }
   }

   public float run() {
      return this.run(this.targetValue);
   }

   @Generated
   public long getDuration() {
      return this.duration;
   }

   @Generated
   public float getValue() {
      return this.value;
   }

   @Generated
   public Easing getEasing() {
      return this.easing;
   }

   @Generated
   public long getStartTime() {
      return this.startTime;
   }

   @Generated
   public float getStartValue() {
      return this.startValue;
   }

   @Generated
   public float getTargetValue() {
      return this.targetValue;
   }

   @Generated
   public boolean isDone() {
      return this.done;
   }

   @Generated
   public boolean isDirection() {
      return this.direction;
   }

   @Generated
   public void setDuration(
      long time
   ) {
      this.duration = time;
   }

   @Generated
   public void setEasing(
      Easing val
   ) {
      this.easing = val;
   }

   @Generated
   public void setStartTime(
      long time
   ) {
      this.startTime = time;
   }

   @Generated
   public void setStartValue(
      float f
   ) {
      this.startValue = f;
   }

   @Generated
   public void setTargetValue(
      float f
   ) {
      this.targetValue = f;
   }

   @Generated
   public void setDone(
      boolean flag
   ) {
      this.done = flag;
   }

   @Generated
   public void setDirection(
      boolean flag
   ) {
      this.direction = flag;
   }

   @Generated
   @Override
   public boolean equals(
      Object val
   ) {
      if (val
         == this) {
         return true;
      } else if (!(
         val instanceof Animation val2
      )) {
         return false;
      } else if (!val2.canEqual(
         this
      )) {
         return false;
      } else if (this.getDuration()
         != val2.getDuration()
         )
       {
         return false;
      } else if (Float.compare(
            this.getValue(),
            val2.getValue()
         )
         != 0) {
         return false;
      } else if (this.getStartTime()
         != val2.getStartTime()
         )
       {
         return false;
      } else if (Float.compare(
            this.getStartValue(),
            val2.getStartValue()
         )
         != 0) {
         return false;
      } else if (Float.compare(
            this.getTargetValue(),
            val2.getTargetValue()
         )
         != 0) {
         return false;
      } else if (this.isDone()
         != val2.isDone()
         )
       {
         return false;
      } else if (this.isDirection()
         != val2.isDirection()
         )
       {
         return false;
      } else {
         Object val3 = this.getEasing();
         Object val4 = val2.getEasing();
         return val3
               == null
            ? val4
               == null
            : val3.equals(
               val4
            );
      }
   }

   @Generated
   protected boolean canEqual(
      Object val
   ) {
      return val instanceof Animation;
   }

   @Generated
   @Override
   public int hashCode() {
      int n = 59;
      int n2 = 1;
      long time = this.getDuration();
      n2 = n2
            * 59
         + (int)(
            time
                  >>> 32
               ^ time
         );
      n2 = n2
            * 59
         + Float.floatToIntBits(this.getValue());
      long time2 = this.getStartTime();
      n2 = n2
            * 59
         + (int)(
            time2
                  >>> 32
               ^ time2
         );
      n2 = n2
            * 59
         + Float.floatToIntBits(this.getStartValue());
      n2 = n2
            * 59
         + Float.floatToIntBits(this.getTargetValue());
      n2 = n2
            * 59
         + (
            this.isDone()
               ? 79
               : 97
         );
      n2 = n2
            * 59
         + (
            this.isDirection()
               ? 79
               : 97
         );
      Object val = this.getEasing();
      return n2
            * 59
         + (
            val
                  == null
               ? 43
               : val.hashCode()
         );
   }

   @Generated
   @Override
   public String toString() {
      return "GuiAnimation(duration="
         + this.getDuration()
         + ", value="
         + this.getValue()
         + ", easing="
         + this.getEasing()
         + ", startTime="
         + this.getStartTime()
         + ", startValue="
         + this.getStartValue()
         + ", targetValue="
         + this.getTargetValue()
         + ", done="
         + this.isDone()
         + ", direction="
         + this.isDirection()
         + ")";
   }
}
