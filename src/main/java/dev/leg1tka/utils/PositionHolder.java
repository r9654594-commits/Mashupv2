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

public class PositionHolder {
   public final Vec3d position;
   public final float bodyYaw;
   public final float netHeadYaw;
   public final float headPitch;
   public final float limbSwing;
   public final float limbSwingAmount;
   public final boolean sneaking;
   public final float height;
   public final long startTime;
   public PositionHolder(
      Vec3d vec,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      boolean flag,
      float f6,
      long time
   ) {
      this.position = vec;
      this.bodyYaw = f;
      this.netHeadYaw = f2;
      this.headPitch = f3;
      this.limbSwing = f4;
      this.limbSwingAmount = f5;
      this.sneaking = flag;
      this.height = f6;
      this.startTime = time;
   }
}
