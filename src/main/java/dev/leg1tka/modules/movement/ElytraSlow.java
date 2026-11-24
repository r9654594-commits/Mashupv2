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
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class ElytraSlow extends Module {
   public static ElytraSlow INSTANCE = new ElytraSlow();
   public final NumberSetting triggerDistance;
   public final NumberSetting speedOffset;
   public final NumberSetting smoothness;

   public ElytraSlow() {
      super("ElytraSlow", Category.MOVEMENT);
      String var1 = "ElytraSlow";
      this.triggerDistance = new NumberSetting("Радиус сближения", 3.0F, 1.0F, 10.0F, 0.5F);
      this.speedOffset = new NumberSetting("Запас скорости", 1.5F, 0.0F, 5.0F, 0.1F);
      this.smoothness = new NumberSetting("Плавность", 0.3F, 0.05F, 1.0F, 0.05F);
      this.addSettings(new Setting[]{
         this.triggerDistance,
         this.speedOffset,
         this.smoothness
      });
   }

   @EventLink
   public void onUpdate(
      EventUpdate val2
   ) {
      if (mc.player != null && mc.player.isGliding()) {
         Aura val = ModuleManager.aura;
         if (val
               != null
            && val.isEnable()
            )
          {
            LivingEntity entity2 = val.getTarget();
            if (entity2
                  != null
               && entity2.isGliding()
               )
             {
               double d = mc.player
                  .getEyePos()
                  .distanceTo(
                     entity2.getBoundingBox()
                        .getCenter()
                  );
               if (d
                  <= this.triggerDistance.getValue().doubleValue()) {
                  Vec3d vec = mc.player
                     .getVelocity();
                  Vec3d velocity = entity2.getVelocity();
                  double d2 = vec.length();
                  double d3 = velocity.length();
                  double d4 = this.speedOffset
                        .getValue()
                        .doubleValue()
                     / 20.0;
                  double d5 = d3
                     + d4;
                  if (d2
                     > d5
                     )
                   {
                     double d6 = this.smoothness
                        .getValue()
                        .doubleValue();
                     double d7 = d2
                        - (
                              d2
                                 - d5
                           )
                           * d6;
                     Vec3d vec2 = vec.multiply(
                        d7
                           / d2
                     );
                     mc.player
                        .setVelocity(
                           vec2
                        );
                  }
               }
            }
         }
      }
   }
}
