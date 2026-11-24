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

public class DirectionHolder {
   public final Vec3d direction;
   public final float spread;
   public final float swirlAmount;
   public final float rotationScale;
   public final float timeScale;
   public final float progressOffset;
   public final int color;

   public DirectionHolder(
      Vec3d vec,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n
   ) {
      this.direction = vec;
      this.spread = f;
      this.swirlAmount = f2;
      this.rotationScale = f3;
      this.timeScale = f4;
      this.progressOffset = f5;
      this.color = n;
   }
}
