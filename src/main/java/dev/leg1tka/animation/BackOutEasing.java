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

public abstract class BackOutEasing implements Easing {
   private float amplitude;
   private float period;

   public BackOutEasing(
      float f,
      float f2
   ) {
      this.amplitude = f;
      this.period = f2;
   }

   public BackOutEasing() {
      this(-1.0F, 0.0F);
   }

   @Generated
   public void setAmplitude(
      float f
   ) {
      this.amplitude = f;
   }

   @Generated
   public void setPeriod(
      float f
   ) {
      this.period = f;
   }

   @Generated
   public float getAmplitude() {
      return this.amplitude;
   }

   @Generated
   public float getPeriod() {
      return this.period;
   }
}
