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


import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class ShouldblockhitcancelHelper implements IMinecraft {
   private static boolean shouldBlockHitCancel(
      Vec3d vec,
      Vec3d vec2,
      BlockHitResult val
   ) {
      if (val
            == null
         || val.getType()
            != Type.BLOCK) {
         return false;
      } else if (mc.world == null) {
         return false;
      } else {
         BlockState state = mc.world
            .getBlockState(
               val.getBlockPos()
            );
         if (state
            == null) {
            return false;
         } else if (!state.getCollisionShape(
               mc.world,
               val.getBlockPos()
            )
            .isEmpty()) {
            Vec3d vec3 = val.getPos();
            return vec3.squaredDistanceTo(
                  vec
               )
               < vec2.squaredDistanceTo(
                  vec
               );
         } else {
            return false;
         }
      }
   }

   public static BlockHitResult raycast(
      Angle val,
      double d,
      boolean flag,
      float f
   ) {
      return raycast(
         d,
         flag,
         mc.player
            .getCameraPosVec(
               f
            ),
         val.getVector(),
         mc.cameraEntity
      );
   }

   public static BlockHitResult raycast(
      double d,
      boolean flag,
      Vec3d vec,
      Vec3d vec2,
      Entity entity
   ) {
      Vec3d vec3 = vec.add(
         vec2.x
            * d,
         vec2.y
            * d,
         vec2.z
            * d
      );
      return mc.world
         .raycast(
            new RaycastContext(
               vec,
               vec3,
               ShapeType.OUTLINE,
               flag
                  ? FluidHandling.ANY
                  : FluidHandling.NONE,
               entity
            )
         );
   }

   public static EntityHitResult raytraceEntity(
      double d,
      Angle val,
      boolean flag
   ) {
      return raytraceEntity(
         d,
         val,
         flag,
         val2 -> true,
         0.0F
      );
   }

   public static EntityHitResult raytraceEntity(
      double d,
      Angle val,
      boolean flag,
      float f
   ) {
      return raytraceEntity(
         d,
         val,
         flag,
         val2 -> true,
         f
      );
   }

   public static EntityHitResult raytraceEntity(
      double d,
      Angle val,
      boolean flag,
      Predicate<Entity> entity,
      float f
   ) {
      Entity entity2 = mc.getCameraEntity();
      if (entity2
         == null) {
         return null;
      } else {
         Vec3d vec = entity2.getEyePos();
         Vec3d vec2 = val.getVector();
         Vec3d vec3 = vec.add(
            vec2.x
               * d,
            vec2.y
               * d,
            vec2.z
               * d
         );
         Box box = entity2.getBoundingBox()
            .stretch(
               vec2.multiply(
                  d
               )
            )
            .expand(1.0, 1.0, 1.0);
         EntityHitResult val2 = ProjectileUtil.raycast(
            entity2,
            vec,
            vec3,
            box,
            val3 -> val3.canHit()
                  && val3.isAlive()
                  && !val3.isSpectator()
                  && entity.test(
                     val3
                  ),
            d
               * d
         );
         if (val2
            == null) {
            return null;
         } else if (flag
            )
          {
            return val2;
         } else {
            Vec3d vec4 = val2.getEntity()
               .getBoundingBox()
               .raycast(
                  vec,
                  vec3
               )
               .orElse(
                  val2.getEntity()
                     .getPos()
               );
            BlockHitResult val4 = mc.world
                  != null
               ? mc.world
                  .raycast(
                     new RaycastContext(
                        vec,
                        vec4,
                        ShapeType.COLLIDER,
                        FluidHandling.ANY,
                        mc.player
                     )
                  )
               : null;
            return shouldBlockHitCancel(
                  vec,
                  vec4,
                  val4
               )
               ? null
               : val2;
         }
      }
   }

   @Generated
   private ShouldblockhitcancelHelper() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
