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

public abstract class OvershootEasing implements Easing {
   public static final float DEFAULT_OVERSHOOT = 1.70158F;
   private float overshoot;

   public OvershootEasing() {
      this(1.70158F);
   }

   public OvershootEasing(
      float f
   ) {
      this.overshoot = f;
   }

   @Generated
   public void setOvershoot(
      float f
   ) {
      this.overshoot = f;
   }

   @Generated
   public float getOvershoot() {
      return this.overshoot;
   }
}
