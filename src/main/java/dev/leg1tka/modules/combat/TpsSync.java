package dev.leg1tka.modules.combat;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import dev.leg1tka.AspectDLC;
import net.minecraft.util.math.MathHelper;

public class TpsSync extends Module {
   public static TpsSync INSTANCE = new TpsSync();

   public TpsSync() {
      super(
         "TpsSync",
         "Синхронизация с TPS сервера",
         Category.COMBAT
      );
   }

   public float getCurrentTPS() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.tpsCalc != null) {
         float f = AspectDLC.INSTANCE
            .tpsCalc
            .getTPS();
         return MathHelper.clamp(
            f,
            0.1F,
            20.0F
         );
      } else {
         return 20.0F;
      }
   }

   public long getAdjustedCooldown(
      long val
   ) {
      if (!this.isEnable()) {
         return val;
      } else {
         float f = this.getCurrentTPS();
         if (f
            >= 20.0F) {
            return val;
         } else {
            float f2 = 20.0F
               / f;
            float f3 = 1.0F
               + (
                     20.0F
                        - f
                  )
                  * 0.05F;
            long val2 = (long)(
               (float)val
                  * f2
                  * f3
            );
            return Math.min(
               val2,
               3000L
            );
         }
      }
   }

   public boolean canAttack(
      long val,
      long val2,
      long val3
   ) {
      if (!this.isEnable()) {
         return val3
            >= val
               + val2;
      } else {
         long val4 = this.getAdjustedCooldown(
            val2
         );
         return val3
            >= val
               + val4;
      }
   }
}
