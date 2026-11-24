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


import lombok.Generated;

public class FinishedHelper {
   private long millis;

   public FinishedHelper() {
      this.reset();
   }

   public boolean finished(
      float f
   ) {
      return (float)System.currentTimeMillis()
            - f
         >= (float)this.millis;
   }

   public boolean finished(
      long time
   ) {
      return System.currentTimeMillis() - this.millis
         >= time;
   }

   public boolean isReached(
      long time
   ) {
      return System.currentTimeMillis() - this.millis
         > time;
   }

   public void reset() {
      this.millis = System.currentTimeMillis();
   }

   public long getElapsedTime() {
      return System.currentTimeMillis() - this.millis;
   }

   @Generated
   public long getMillis() {
      return this.millis;
   }

   @Generated
   public void setMillis(
      long time
   ) {
      this.millis = time;
   }
}
