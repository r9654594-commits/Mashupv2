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


public class DurationHolder {
   public long duration;
   public long time;
   public long reflection;
   public Runnable runnable;
   public boolean condition;

   public DurationHolder(
      long time2,
      long time3,
      Runnable val
   ) {
      this.duration = time2;
      this.reflection = time3;
      this.runnable = val;
      this.time = System.currentTimeMillis();
      this.condition = true;
   }

   public DurationHolder(
      long time2,
      long time3,
      Runnable val,
      boolean flag
   ) {
      this.duration = time2;
      this.reflection = time3;
      this.runnable = val;
      this.time = System.currentTimeMillis();
      this.condition = flag;
   }

   public DurationHolder(
      long time2,
      Runnable val
   ) {
      this.duration = time2;
      this.reflection = 20L;
      this.runnable = val;
      this.time = System.currentTimeMillis();
      this.condition = true;
   }

   public DurationHolder(
      long time2,
      Runnable val,
      boolean flag
   ) {
      this.duration = time2;
      this.reflection = 20L;
      this.runnable = val;
      this.time = System.currentTimeMillis();
      this.condition = flag;
   }
}
