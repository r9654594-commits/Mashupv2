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


public final class PretranslateyHolder {
   public final float preTranslateY;
   public final float preTranslateZ;
   public final float anchorY;
   public final float anchorZ;
   public final float pitchRotation;
   public final float rollRotation;
   public final float openMultiplier;
   public final float scaleMultiplier;
   public final float motionSpreadBoost;
   public final float flapAmplitude;
   public final float sideOffset;
   public final float sideYOffset;
   public final float sideZOffset;
   public final float sideRoll;
   public final float sidePitch;
   public final float flapSpeed;

   public PretranslateyHolder(
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      float f12,
      float f13,
      float f14,
      float f15,
      float f16
   ) {
      this.preTranslateY = f;
      this.preTranslateZ = f2;
      this.anchorY = f3;
      this.anchorZ = f4;
      this.pitchRotation = f5;
      this.rollRotation = f6;
      this.openMultiplier = f7;
      this.scaleMultiplier = f8;
      this.motionSpreadBoost = f9;
      this.flapAmplitude = f10;
      this.sideOffset = f11;
      this.sideYOffset = f12;
      this.sideZOffset = f13;
      this.sideRoll = f14;
      this.sidePitch = f15;
      this.flapSpeed = f16;
   }
}
