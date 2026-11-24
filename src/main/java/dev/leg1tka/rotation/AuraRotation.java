package dev.leg1tka.rotation;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public abstract class AuraRotation implements IMinecraft {
   public Vec2f rotate = Vec2f.ZERO;

   public abstract void updateRotations(LivingEntity var1);

   public static Vec2f correctRotation(
      float y,
      float f
   ) {
      if ((
            y
                  != -90.0F
               || f
                  != 90.0F
         )
         && y
            != -180.0F) {
         float yaw = MouseGCD.getGCD();
         y -= y
            % yaw;
         f -= f
            % yaw;
         return new Vec2f(
            y,
            f
         );
      } else {
         return new Vec2f(mc.player.getYaw(), mc.player.getPitch());
      }
   }

   protected boolean shouldUseElytraPredict(
      LivingEntity entity
   ) {
      return mc.player != null
            && entity
               != null
            && mc.player.isGliding()
            && entity.isGliding()
            && ModuleManager.elytraTarget != null
            && ModuleManager.elytraTarget.isEnable()
         ? GetsmartpredictedpositionHelper.shouldOvertake(
            entity
         )
         : false;
   }

   protected int getElytraPredictTicks() {
      return ModuleManager.elytraTarget == null ? 0 : Math.max(0, ModuleManager.elytraTarget.predictValue.getValue().intValue());
   }

   public static Vec3d getPredictedPoint(
      LivingEntity entity,
      Vec3d vec
   ) {
      return mc.player != null
            && entity
               != null
            && mc.player.isGliding()
            && entity.isGliding()
            && ModuleManager.elytraTarget != null
            && ModuleManager.elytraTarget.isEnable()
            && GetsmartpredictedpositionHelper.shouldOvertake(
               entity
            )
         ? GetsmartpredictedpositionHelper.getElytraAimPoint(
            entity,
            vec
         )
         : vec;
   }

   protected Box getPredictedBox(
      LivingEntity entity
   ) {
      Box box = entity.getBoundingBox();
      if (!this.shouldUseElytraPredict(
         entity
      )) {
         return box;
      } else {
         Vec3d vec = box.getCenter();
         Vec3d vec2 = getPredictedPoint(
            entity,
            vec
         );
         return box.offset(
            vec2.subtract(
               vec
            )
         );
      }
   }
}
