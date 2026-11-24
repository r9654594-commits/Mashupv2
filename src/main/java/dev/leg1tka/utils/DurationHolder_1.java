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


public class DurationHolder_1 {
   public long duration;
   public long time;
   public long ticksExisted;
   public Runnable runnable;
   public boolean condition;
   public ClientOption_BSfnhH93 mode;

   public DurationHolder_1(
      long time2,
      Runnable val,
      boolean flag
   ) {
      this.mode = ClientOption_BSfnhH93.TIME;
      this.duration = time2;
      this.runnable = val;
      this.time = System.currentTimeMillis();
      this.condition = flag;
   }

   public DurationHolder_1(
      ClientOption_BSfnhH93 val,
      int n,
      Runnable val2,
      boolean flag
   ) {
      this.mode = val;
      this.runnable = val2;
      this.time = (long)n;
      this.condition = flag;
   }
}
