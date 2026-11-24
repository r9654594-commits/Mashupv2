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

public class CreateHelper implements IMinecraft {
   private long startTime = System.currentTimeMillis();
   private long millis;

   public CreateHelper() {
      this.reset();
   }

   public static CreateHelper create() {
      return new CreateHelper();
   }

   public boolean finished(
      long time
   ) {
      return System.currentTimeMillis()
            - time
         >= this.millis;
   }

   public void reset() {
      this.millis = System.currentTimeMillis();
   }

   public long getElapsedTime() {
      return System.currentTimeMillis() - this.millis;
   }

   public double deltaTime() {
      return mc.getCurrentFps() > 0 ? 1.0 / (double)mc.getCurrentFps() : 1.0;
   }

   public boolean every(
      long time
   ) {
      boolean flag = this.getMillis(
            System.nanoTime() - this.millis
         )
         >= time;
      if (flag
         )
       {
         this.reset();
      }

      return flag;
   }

   public boolean passed(
      long time
   ) {
      return System.currentTimeMillis() - this.startTime
         > time;
   }

   public long getMillis(
      long time
   ) {
      return time
         / 1000000L;
   }

   public long getTime() {
      return System.currentTimeMillis() - this.startTime;
   }

   public void setTime(
      long time
   ) {
      this.startTime = time;
   }

   @Generated
   public long getStartTime() {
      return this.startTime;
   }

   @Generated
   public long getMillis() {
      return this.millis;
   }

   @Generated
   public void setStartTime(
      long time
   ) {
      this.startTime = time;
   }

   @Generated
   public void setMillis(
      long time
   ) {
      this.millis = time;
   }
}
