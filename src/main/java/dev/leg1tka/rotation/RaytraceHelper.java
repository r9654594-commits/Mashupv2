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


import java.util.Objects;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Vector3f;

public final class RaytraceHelper implements IMinecraft {
   public static HitResult rayTrace(
      double d,
      float f,
      float f2,
      Entity entity
   ) {
      Vec3d vec = mc.player
         .getEyePos();
      Vec3d vec2 = getVectorForRotation(
         f2,
         f
      );
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
               FluidHandling.NONE,
               entity
            )
         );
   }

   public static BlockHitResult raycast(
      Vec3d vec,
      Vec3d vec2,
      ShapeType val
   ) {
      return raycast(
         vec,
         vec2,
         val,
         mc.player
      );
   }

   public static BlockHitResult raycast(
      Vec3d vec,
      Vec3d vec2,
      ShapeType val,
      Entity entity
   ) {
      return mc.world
         .raycast(
            new RaycastContext(
               vec,
               vec2,
               val,
               FluidHandling.NONE,
               entity
            )
         );
   }

   public static boolean rayTrace(
      Vec3d vec,
      double d,
      Box box
   ) {
      Vec3d vec2 = Objects.requireNonNull(
            mc.player
         )
         .getEyePos();
      return box.contains(
            vec2
         )
         || box.raycast(
               vec2,
               vec2.add(
                  vec.multiply(
                     d
                  )
               )
            )
            .isPresent();
   }

   public static boolean isViewEntity(
      LivingEntity entity,
      float f,
      float f2,
      float f3,
      boolean flag
   ) {
      Entity entity2 = mc.getCameraEntity();
      if (entity2
            != null
         && mc.world != null) {
         double x2 = (double)(
            f3
               * f3
         );
         Vec3d vec = entity2.getEyePos();
         Vector3f vec3f = calculateViewVector(
            f,
            f2
         );
         vec3f.mul(
            f3,
            f3,
            f3
         );
         Vec3d vec2 = vec.add(
            (double)vec3f.x,
            (double)vec3f.y,
            (double)vec3f.z
         );
         Box box = entity.getBoundingBox();
         EntityHitResult val = ProjectileUtil.raycast(
            entity2,
            vec,
            vec2,
            box,
            val2 -> !val2.isSpectator()
                  && val2.isAlive()
                  && val2
                     == entity,
            x2
         );
         return val
            != null;
      } else {
         return false;
      }
   }

   public static Vector3f calculateViewVector(
      float f,
      float f2
   ) {
      float f3 = f2
         * (float) (Math.PI / 180.0);
      float f4 = -f
         * (float) (Math.PI / 180.0);
      float f5 = MathHelper.cos(
         f4
      );
      float f6 = MathHelper.sin(
         f4
      );
      float f7 = MathHelper.cos(
         f3
      );
      float f8 = MathHelper.sin(
         f3
      );
      return new Vector3f(
         f6
            * f7,
         -f8,
         f5
            * f7
      );
   }

   public static Vec3d getVectorForRotation(
      float f,
      float f2
   ) {
      float f3 = -f2
            * (float) (Math.PI / 180.0)
         - (float) Math.PI;
      float f4 = -f
         * (float) (Math.PI / 180.0);
      float f5 = MathHelper.cos(
         f3
      );
      float f6 = MathHelper.sin(
         f3
      );
      float f7 = -MathHelper.cos(
         f4
      );
      float f8 = MathHelper.sin(
         f4
      );
      return new Vec3d(
         (double)(
            f6
               * f7
         ),
         (double)f8,
         (double)(
            f5
               * f7
         )
      );
   }

   public static boolean rayTraceSingleEntity(
      float f,
      float f2,
      double d,
      Entity entity
   ) {
      Vec3d vec = mc.player
         .getEyePos();
      Vec3d vec2 = mc.player
         .getRotationVector(
            f2,
            f
         );
      Vec3d vec3 = vec.add(
         vec2.multiply(
            d
         )
      );
      Box box = entity.getBoundingBox();
      return box.contains(
            vec
         )
         || box.raycast(
               vec,
               vec3
            )
            .isPresent();
   }

   @Generated
   private RaytraceHelper() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
