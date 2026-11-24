package dev.leg1tka.modules.movement;



import dev.leg1tka.modules.combat.Aura;
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

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventMove;
import net.minecraft.util.math.Vec3d;

public class ElytraMotion extends Module {
   public static ElytraMotion INSTANCE = new ElytraMotion();
   public NumberSetting distance = new NumberSetting("Дистанция до игрока", 3.0F, 0.0F, 6.0F, 0.1F);
   public BooleanSetting bypass = new BooleanSetting("Обход", false);

   public ElytraMotion() {
      super("ElytraMotion", "Зависает рядом с игроком на эликах", Category.MOVEMENT);
      this.addSettings(new Setting[]{
         this.distance,
         this.bypass
      });
   }

   @EventLink
   public void onMove(
      EventMove val
   ) {
      if (this.isEnable()) {
         Aura val2 = ModuleManager.aura;
         if (mc.player != null
            && mc.world != null
            && val2.getTarget()
               != null) {
            if (mc.player.isGliding()
               && mc.player
                     .distanceTo(
                        val2.getTarget()
                     )
                  < this.distance.getValue().floatValue()) {
               if (this.bypass.isState()) {
                  float y = mc.player
                     .getYaw();
                  double d = Math.toRadians(
                     (double)y
                  );
                  double d2 = 0.01;
                  double d3 = -1.0E-4;
                  double d4 = -Math.sin(
                        d
                     )
                     * d2;
                  double d5 = Math.cos(
                        d
                     )
                     * d2;
                  val.setMovePos(
                     new Vec3d(
                        d4,
                        d3,
                        d5
                     )
                  );
               } else {
                  val.setMovePos(
                     Vec3d.ZERO
                  );
               }
            }
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }
}
