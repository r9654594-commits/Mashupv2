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


import java.io.InputStream;

public final class ReadHelper extends CloseHelper {
   public long a = 0L;

   public ReadHelper(InputStream var1) {
      super(var1);
   }

   @Override
   public final int read() throws java.io.IOException {
      int var1;
      if ((var1 = this.in.read()) != -1 && this.a >= 0L) {
         this.a++;
      }

      return var1;
   }

   @Override
   public final int read(byte[] var1, int var2, int var3) throws java.io.IOException {
      int var4;
      if ((var4 = this.in.read(var1, var2, var3)) > 0 && this.a >= 0L) {
         this.a += (long)var4;
      }

      return var4;
   }
}
