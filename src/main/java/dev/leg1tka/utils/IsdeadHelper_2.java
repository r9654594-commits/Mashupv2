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


import net.minecraft.util.math.Vec3d;

public class IsdeadHelper_2 {
   public Vec3d position;
   public long birthTime;
   public long fadeInTime;
   public long displayTime;
   public long fadeOutTime;
   public IsdeadHelper_2(
      Vec3d vec,
      long time,
      long time2,
      long time3,
      long time4
   ) {
      this.position = vec;
      this.birthTime = time;
      this.fadeInTime = time2;
      this.displayTime = time3;
      this.fadeOutTime = time4;
   }

   public boolean isDead() {
      return System.currentTimeMillis() - this.birthTime >= this.fadeInTime + this.displayTime + this.fadeOutTime;
   }

   public float getAlpha() {
      long time = System.currentTimeMillis()
         - this.birthTime;
      if (time
         < this.fadeInTime) {
         return this.easeOutCubic(
            (float)time
               / (float)this.fadeInTime
         );
      } else if (time
         < this.fadeInTime + this.displayTime) {
         return 1.0F;
      } else {
         long time2 = time
            - this.fadeInTime
            - this.displayTime;
         return 1.0F
            - this.easeInCubic(
               Math.min(
                  1.0F,
                  (float)time2
                     / (float)this.fadeOutTime
               )
            );
      }
   }

   public float getScaleMultiplier() {
      long time = System.currentTimeMillis()
         - this.birthTime;
      if (time
         < this.fadeInTime) {
         return 0.5F
            + 0.5F
               * this.easeOutBack(
                  (float)time
                     / (float)this.fadeInTime
               );
      } else if (time
         < this.fadeInTime + this.displayTime) {
         return 1.0F;
      } else {
         long time2 = time
            - this.fadeInTime
            - this.displayTime;
         return 1.0F
            - 0.3F
               * this.easeInCubic(
                  Math.min(
                     1.0F,
                     (float)time2
                        / (float)this.fadeOutTime
                  )
               );
      }
   }

   private float easeOutCubic(
      float f
   ) {
      return 1.0F
         - (float)Math.pow(
            1.0
               - (double)f,
            3.0
         );
   }

   private float easeInCubic(
      float f
   ) {
      return f
         * f
         * f;
   }

   private float easeOutBack(
      float f
   ) {
      float f2 = 1.70158F;
      float f3 = f2
         + 1.0F;
      return 1.0F
         + f3
            * (float)Math.pow(
               (double)f
                  - 1.0,
               3.0
            )
         + f2
            * (float)Math.pow(
               (double)f
                  - 1.0,
               2.0
            );
   }
}
