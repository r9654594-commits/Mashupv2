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



public final class AHelper_2 extends AHelper_6 {
   public final byte[] a = AHelper_5.a(65531);
   public int c = this.a.length;

   @Override
   public final void a() {
      if ((super.a & -16777216) == 0) {
         try {
            super.b = super.b << 8
               | this.a[this.c++] & 255;
            super.a = super.a << 8;
         } catch (ArrayIndexOutOfBoundsException var1) {
            throw new ClientUtil_Nwswno45();
         }
      }
   }
}
