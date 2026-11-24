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


public class XHolder_4 {
   public double x;
   public double y;
   public double z;
   public float vx;
   public float vy;
   public float vz;
   public float rotX;
   public float rotY;
   public float rotZ;
   public float rotSpeedX;
   public float rotSpeedY;
   public float rotSpeedZ;
   public float wobblePhase;
   public float wobbleOffset;
   public float renderAlpha;
   public int life;
   public int maxLife;
   public XHolder_4(
      double d,
      double d2,
      double d3,
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
      float f10,
      float f11
   ) {
      this.x = d;
      this.y = d2;
      this.z = d3;
      this.vx = f;
      this.vy = f2;
      this.vz = f3;
      this.rotX = f4;
      this.rotY = f5;
      this.rotZ = f6;
      this.rotSpeedX = f7;
      this.rotSpeedY = f8;
      this.rotSpeedZ = f9;
      this.life = this.maxLife = n;
      this.wobblePhase = f10;
      this.wobbleOffset = f11;
   }
}
