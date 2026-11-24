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

public class ShouldremoveHelper {
   public final float x;
   public final float y;
   public final float z;
   public final long startTime;
   public final int maxLife;
   public final int baseColor;
   public ShouldremoveHelper(
      float f,
      float f2,
      float f3,
      int n,
      int n2
   ) {
      this.x = f;
      this.y = f2;
      this.z = f3;
      this.startTime = System.currentTimeMillis();
      this.maxLife = n;
      this.baseColor = n2;
   }

   public boolean shouldRemove() {
      return System.currentTimeMillis() - this.startTime >= (long)this.maxLife;
   }

   public float getTimeProgress() {
      return MathHelper.clamp((float)(System.currentTimeMillis() - this.startTime) / (float)this.maxLife, 0.0F, 1.0F);
   }

   public int getColor(
      float f
   ) {
      int color = (int)(
         (float)(
               this.baseColor >> 24
                  & 255
            )
            * (
               1.0F
                  - f
            )
      );
      color = Math.max(
         0,
         Math.min(
            255,
            color
         )
      );
      return color
            << 24
         | this.baseColor & 16777215;
   }
}
