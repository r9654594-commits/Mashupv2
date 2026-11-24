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

public class ResetHelper_6 {
   public long lastMS = System.currentTimeMillis();

   public void reset() {
      this.lastMS = System.currentTimeMillis();
   }

   public boolean isReached(
      long time
   ) {
      return System.currentTimeMillis() - this.lastMS
         > time;
   }

   public void setLastMS(
      long time
   ) {
      this.lastMS = System.currentTimeMillis()
         + time;
   }

   public void setTime(
      long time
   ) {
      this.lastMS = time;
   }

   public long getTime() {
      return System.currentTimeMillis() - this.lastMS;
   }

   public boolean isRunning() {
      return System.currentTimeMillis() - this.lastMS <= 0L;
   }

   public boolean hasTimeElapsed() {
      return this.lastMS < System.currentTimeMillis();
   }

   public boolean hasTimeElapsed2(
      long time
   ) {
      return System.currentTimeMillis() - this.lastMS
         >= time;
   }

   @Generated
   public long getLastMS() {
      return this.lastMS;
   }
}
