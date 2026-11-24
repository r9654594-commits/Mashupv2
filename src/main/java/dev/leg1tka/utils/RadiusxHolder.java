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


public class RadiusxHolder {
   public final float radiusX;
   public final float radiusZ;
   public final float yOffset;
   public final float startDeg;
   public final float arcDeg;
   public final float tiltX;
   public final float tiltZ;
   public final float speedDeg;
   public final float alphaMul;
   public final int startColor;
   public final int endColor;
   public final float baseYaw;

   public RadiusxHolder(
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      int n,
      int n2
   ) {
      this.radiusX = f;
      this.radiusZ = f2;
      this.yOffset = f3;
      this.startDeg = f4;
      this.arcDeg = f5;
      this.tiltX = f6;
      this.tiltZ = f7;
      this.speedDeg = f8;
      this.alphaMul = f9;
      this.startColor = n;
      this.endColor = n2;
      this.baseYaw = f4
         * 0.35F;
   }
}
