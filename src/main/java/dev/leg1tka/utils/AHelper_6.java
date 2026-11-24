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



public abstract class AHelper_6 extends ClientUtil_MZI6A8SE {
   public int a = 0;
   public int b = 0;

   public abstract void a();

   public final int a(short[] var1, int var2) {
      this.a();
      short var3 = var1[var2];
      int var4 = (this.a >>> 11) * var3;
      byte var5;
      if ((this.b ^ -2147483648)
         < (var4 ^ -2147483648)) {
         this.a = var4;
         var1[var2] = (short)(
            var3
               + (
                  2048 - var3
                     >>> 5
               )
         );
         var5 = 0;
      } else {
         this.a -= var4;
         this.b -= var4;
         var1[var2] = (short)(var3 - (var3 >>> 5));
         var5 = 1;
      }

      return var5;
   }

   public final int a(short[] var1) {
      int var2 = 1;

      while ((var2 = var2 << 1 | this.a(var1, var2)) < var1.length) {
      }

      return var2 - var1.length;
   }

   public final int b(short[] var1) {
      int var2 = 1;
      int var3 = 0;
      int var4 = 0;

      do {
         int var5 = this.a(var1, var2);
         var2 = var2 << 1 | var5;
         var4 |= var5 << var3++;
      } while (var2 < var1.length);

      return var4;
   }
}
