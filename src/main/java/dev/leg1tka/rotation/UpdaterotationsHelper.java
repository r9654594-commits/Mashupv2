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
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class UpdaterotationsHelper extends AuraRotation implements IMinecraft {
   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (!AuraTransition.INSTANCE.handleTransitions()) {
         Vec3d vec = mc.player
            .getCameraPosVec(1.0F);
         Vec3d vec2 = mc.player
            .getRotationVec(1.0F);
         Vec3d vec3 = vec.add(
            vec2.multiply(
               999.0
            )
         );
         Box box = this.getPredictedBox(
            entity
         );
         double x2 = entity.isGliding()
            ? -0.5
            : 0.1F;
         double x3 = entity.isGliding()
            ? -0.5
            : 0.1F;
         box = new Box(
            box.minX
               + box.getLengthX()
                  * x2
                  / 2.0,
            box.minY,
            box.minZ
               + box.getLengthZ()
                  * x2
                  / 2.0,
            box.maxX
               - box.getLengthX()
                  * x2
                  / 2.0,
            box.maxY
               - box.getLengthY()
                  * x3,
            box.maxZ
               - box.getLengthZ()
                  * x2
                  / 2.0
         );
         Optional val = box.raycast(
            vec,
            vec3
         );
         boolean flag = box.contains(
            vec
         );
         if (val.isPresent()
            || flag
            )
          {
            Aura.adjYaw = MathHelper.clamp(Aura.adjYaw - ThreadLocalRandom.current().nextFloat(0.005F, 0.02F), 0.0F, 1.0F);
            Aura.adjPitch = MathHelper.clamp(Aura.adjPitch - ThreadLocalRandom.current().nextFloat(0.005F, 0.02F), 0.0F, 1.0F);
         } else if (mc.player.isGliding()) {
            Aura.adjYaw = MathHelper.clamp(Aura.adjYaw + ThreadLocalRandom.current().nextFloat(5.0E-4F, 0.005F), 0.0F, 1.0F);
            Aura.adjPitch = MathHelper.clamp(Aura.adjPitch + ThreadLocalRandom.current().nextFloat(9.0E-4F, 0.009F), 0.0F, 1.0F);
         } else if (entity.isInSwimmingPose()
            )
          {
            Aura.adjYaw = MathHelper.clamp(Aura.adjYaw + ThreadLocalRandom.current().nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
            Aura.adjPitch = MathHelper.clamp(Aura.adjPitch + ThreadLocalRandom.current().nextFloat(9.0E-5F, 9.0E-4F), 0.0F, 1.0F);
         } else {
            Aura.adjYaw = MathHelper.clamp(Aura.adjYaw + ThreadLocalRandom.current().nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
            Aura.adjPitch = MathHelper.clamp(Aura.adjPitch + ThreadLocalRandom.current().nextFloat(9.0E-4F, 0.009F), 0.0F, 1.0F);
         }

         Vec2f rot = RayTraceUtil.getRotations(
            getPredictedPoint(
               entity,
               entity.getLeashPos(
                  1.0F
               )
            )
         );
         float yaw = mc.player
            .getYaw();
         float yaw2 = mc.player
            .getPitch();
         float yaw3 = MathHelper.wrapDegrees(
            rot.x
               - yaw
         );
         float yaw4 = MathHelper.wrapDegrees(
            rot.y
               - yaw2
         );
         float yaw5 = yaw
            + yaw3
               * Aura.adjYaw;
         float yaw6 = yaw2
            + yaw4
               * Aura.adjPitch;
         AuraTransition var10000 = AuraTransition.INSTANCE;
         float[] floats = var10000.process(
            yaw5,
            yaw6,
            entity,
            "legit"
         );
         RotationStorage.update(
            new Angle(
               floats[0],
               floats[1]
            ),
            360.0F,
            360.0F,
            40.0F,
            35.0F,
            1,
            1,
            Aura.clientLook.isState()
         );
      }
   }
}
