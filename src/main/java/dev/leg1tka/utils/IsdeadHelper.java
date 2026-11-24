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

public class IsdeadHelper {
   public final Vec3d origin;
   public final long birthTime;
   private static final long DURATION_MS = 2200L;
   public IsdeadHelper(
      Vec3d vec,
      long time
   ) {
      this.origin = vec;
      this.birthTime = time;
   }

   public boolean isDead() {
      return System.currentTimeMillis() - this.birthTime >= 2200L;
   }

   public float getProgress() {
      return Math.min(1.0F, (float)(System.currentTimeMillis() - this.birthTime) / 2200.0F);
   }

   public float getAlpha() {
      float alpha = this.getProgress();
      if (alpha
         < 0.06F) {
         return alpha
            / 0.06F;
      } else {
         return alpha
               < 0.8F
            ? 1.0F
            : 1.0F
               - (
                     alpha
                        - 0.8F
                  )
                  / 0.2F;
      }
   }
}
