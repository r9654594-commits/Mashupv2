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


public class XHelper {
   public final int x;
   public final int y;
   public final int w;
   public final int h;
   public final Runnable onClick;

   public XHelper(int x, int y, int w, int h, Runnable onClick) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.onClick = onClick;
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public int w() {
      return this.w;
   }

   public int h() {
      return this.h;
   }

   public Runnable onClick() {
      return this.onClick;
   }

   public boolean contains(
      double d,
      double d2
   ) {
      return d
            >= (double)this.x
         && d
            < (double)(this.x + this.w)
         && d2
            >= (double)this.y
         && d2
            < (double)(this.y + this.h);
   }

}
