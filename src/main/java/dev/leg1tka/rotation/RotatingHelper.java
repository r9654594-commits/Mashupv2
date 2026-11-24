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



import dev.leg1tka.modules.combat.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class RotatingHelper extends AuraRotation implements IMinecraft {
   public Vec2f rotating(
      Vec2f vec2,
      LivingEntity entity
   ) {
      Vec3d vec = this.calcPointed(
         entity
      );
      float x2 = (float)MathHelper.wrapDegrees(
         Math.toDegrees(
               Math.atan2(
                  vec.z,
                  vec.x
               )
            )
            - 90.0
      );
      float x3 = (float)MathHelper.wrapDegrees(
         Math.toDegrees(
            -Math.atan2(
               vec.y,
               Math.hypot(
                  vec.x,
                  vec.z
               )
            )
         )
      );
      float x4 = MathHelper.wrapDegrees(
         x2
            - vec2.x
      );
      float x5 = MathHelper.wrapDegrees(
         x3
            - vec2.y
      );
      if (Math.abs(
            x4
         )
         > 180.0F) {
         x4 -= Math.signum(
               x4
            )
            * 360.0F;
      }

      float x6 = MathHelper.clamp(
         x4,
         -180.0F,
         180.0F
      );
      float x7 = MathHelper.clamp(
         x5,
         -90.0F,
         90.0F
      );
      float x8 = vec2.x
         + x6;
      float x9 = vec2.y
         + x7;
      float x10 = MouseGCD.getFixedRotation(
         x8
      );
      float x11 = MouseGCD.getFixedRotation(
         x9
      );
      return new Vec2f(
         x10,
         x11
      );
   }

   private Vec3d calcPointed(
      LivingEntity entity
   ) {
      if (entity
         == null) {
         return Vec3d.ZERO;
      } else {
         Vec3d vec = entity.getBoundingBox()
            .getCenter();
         ElytraTarget val = ModuleManager.elytraTarget;
         if (val
               != null
            && val.isEnable()
            && val.predictate
               .isState()) {
            vec = GetsmartpredictedpositionHelper.getPredicted(
               entity,
               val.predictValue
                  .getValue()
                  .doubleValue(),
               val.smartElytraChase
                     .isState()
                  ? val.chaseLateral
                     .getValue()
                     .doubleValue()
                  : 0.0,
               val.smartElytraChase
                     .isState()
                  ? val.chaseForward
                     .getValue()
                     .doubleValue()
                  : 0.0
            );
         } else {
            vec = AuraRotation.getPredictedPoint(
               entity,
               entity.getBoundingBox()
                  .getCenter()
            );
         }

         return new Vec3d(
            vec.getX()
               - mc.player.getX(),
            vec.getY()
               - mc.player.getEyeY(),
            vec.getZ()
               - mc.player.getZ()
         );
      }
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
   }
}
