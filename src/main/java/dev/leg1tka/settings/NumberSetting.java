package dev.leg1tka.settings;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public class NumberSetting extends Setting {
   private float value;
   private final float min;
   private final float max;
   private final float increment;
   private boolean active;

   public NumberSetting(
      String str,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      super(
         str
      );
      this.value = f;
      this.min = f2;
      this.max = f3;
      this.increment = f4;
   }

   public Number getValue() {
      return MathHelper.clamp(this.value, this.getMin(), this.getMax());
   }

   public void setValue(
      float f
   ) {
      this.value = MathHelper.clamp(
         f,
         this.getMin(),
         this.getMax()
      );
   }

   public float get() {
      return this.getValue().floatValue();
   }

   public NumberSetting visible(
      Supplier<Boolean> val
   ) {
      this.visible = val;
      return this;
   }

   @Generated
   public float getMin() {
      return this.min;
   }

   @Generated
   public float getMax() {
      return this.max;
   }

   @Generated
   public float getIncrement() {
      return this.increment;
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }

   @Generated
   public void setActive(
      boolean flag
   ) {
      this.active = flag;
   }
}
