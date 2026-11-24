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

public class IsdeadHelper_3 {
   public final Vec3d position;
   public final Vec3d moveDir;
   public final float bodyYaw;
   public final float netHeadYaw;
   public final float headPitch;
   public final float limbSwing;
   public final float limbSwingAmount;
   public final boolean sneaking;
   public final long birthTime;
   private static final long DURATION_MS = 2000L;
   public IsdeadHelper_3(
      Vec3d vec,
      Vec3d vec2,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      boolean flag,
      long time
   ) {
      this.position = vec;
      this.moveDir = vec2;
      this.bodyYaw = f;
      this.netHeadYaw = f2;
      this.headPitch = f3;
      this.limbSwing = f4;
      this.limbSwingAmount = f5;
      this.sneaking = flag;
      this.birthTime = time;
   }

   public boolean isDead() {
      return System.currentTimeMillis() - this.birthTime >= 2000L;
   }

   public float getProgress() {
      return Math.min(1.0F, (float)(System.currentTimeMillis() - this.birthTime) / 2000.0F);
   }

   public float getAlpha() {
      float alpha = this.getProgress();
      return alpha
            < 0.08F
         ? alpha
            / 0.08F
         : 1.0F
            - (
                  alpha
                     - 0.08F
               )
               / 0.92F;
   }
}
