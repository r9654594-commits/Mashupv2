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



public class XHolder_2 {
   public final float x;
   public final float y;
   public final float z;
   public final int r;
   public final int g;
   public final int b;
   public final int a;
   public XHolder_2(
      float f,
      float f2,
      float f3,
      int n
   ) {
      this.x = f;
      this.y = f2;
      this.z = f3;
      this.a = n
            >> 24
         & 255;
      this.r = n
            >> 16
         & 255;
      this.g = n
            >> 8
         & 255;
      this.b = n
         & 255;
   }
}
