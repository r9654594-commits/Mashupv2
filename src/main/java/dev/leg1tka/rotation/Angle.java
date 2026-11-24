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


import lombok.Generated;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Angle implements IMinecraft {
   private float yaw;
   private float pitch;

   public Angle(
      Entity entity
   ) {
      this.yaw = entity.getYaw();
      this.pitch = entity.getPitch();
   }

   public float getDelta(
      Angle val
   ) {
      float yaw2 = MathHelper.wrapDegrees(
         val.getYaw()
            - this.yaw
      );
      float yaw3 = val.getPitch()
         - this.pitch;
      return (float)Math.hypot(
         (double)Math.abs(
            yaw2
         ),
         (double)Math.abs(
            yaw3
         )
      );
   }

   public double getDeltaDouble(
      Angle val
   ) {
      double delta = (double)MathHelper.wrapDegrees(
         val.getYaw()
            - this.yaw
      );
      double delta2 = (double)MathHelper.wrapDegrees(
         val.getPitch()
            - this.pitch
      );
      return Math.hypot(
         delta,
         delta2
      );
   }

   public static Vector2f camera() {
      return new Vector2f(cameraYaw(), cameraPitch());
   }

   public static float cameraYaw() {
      return MathHelper.wrapDegrees(
         mc.gameRenderer.getCamera().getYaw()
            + (float)(mc.gameRenderer.getCamera().isThirdPerson() ? 180 : 0)
      );
   }

   public static float cameraPitch() {
      return (float)(mc.gameRenderer.getCamera().isThirdPerson() ? -1 : 1)
         * mc.gameRenderer.getCamera().getPitch();
   }

   public static Angle from(
      PlayerEntity player,
      Entity entity
   ) {
      Vec3d vec = player.getCameraPosVec(
         0.0F
      );
      Vec3d vec2 = entity.getPos()
         .add(
            0.0,
            (double)entity.getHeight()
               * 0.5,
            0.0
         );
      double x2 = vec2.x
         - vec.x;
      double x3 = vec2.y
         - vec.y;
      double x4 = vec2.z
         - vec.z;
      double x5 = Math.sqrt(
         x2
               * x2
            + x4
               * x4
      );
      float height = (float)Math.toDegrees(
            Math.atan2(
               x4,
               x2
            )
         )
         - 90.0F;
      float height2 = (float)(
         -Math.toDegrees(
            Math.atan2(
               x3,
               x5
            )
         )
      );
      return new Angle(
         height,
         height2
      );
   }

   public final Vec3d toVector() {
      float yaw2 = this.pitch
         * (float) (Math.PI / 180.0);
      float yaw3 = -this.yaw
         * (float) (Math.PI / 180.0);
      float yaw4 = MathHelper.cos(
         yaw3
      );
      float yaw5 = MathHelper.sin(
         yaw3
      );
      float yaw6 = MathHelper.cos(
         yaw2
      );
      float yaw7 = MathHelper.sin(
         yaw2
      );
      return new Vec3d(
         (double)(
            yaw5
               * yaw6
         ),
         (double)(
            -yaw7
         ),
         (double)(
            yaw4
               * yaw6
         )
      );
   }

   public final Vec3d getVector() {
      return this.toVector();
   }

   @Generated
   public float getYaw() {
      return this.yaw;
   }

   @Generated
   public float getPitch() {
      return this.pitch;
   }

   @Generated
   public void setYaw(
      float f
   ) {
      this.yaw = f;
   }

   @Generated
   public void setPitch(
      float f
   ) {
      this.pitch = f;
   }

   @Generated
   @Override
   public boolean equals(
      Object val
   ) {
      if (val
         == this) {
         return true;
      } else if (!(
         val instanceof Angle val2
      )) {
         return false;
      } else if (!val2.canEqual(
         this
      )) {
         return false;
      } else {
         return Float.compare(
                  this.getYaw(),
                  val2.getYaw()
               )
               != 0
            ? false
            : Float.compare(
                  this.getPitch(),
                  val2.getPitch()
               )
               == 0;
      }
   }

   @Generated
   protected boolean canEqual(
      Object val
   ) {
      return val instanceof Angle;
   }

   @Generated
   @Override
   public int hashCode() {
      int y = 59;
      int y2 = 1;
      y2 = y2
            * 59
         + Float.floatToIntBits(this.getYaw());
      return y2
            * 59
         + Float.floatToIntBits(this.getPitch());
   }

   @Generated
   @Override
   public String toString() {
      return "Rotation(yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ")";
   }

   @Generated
   public Angle() {
   }

   @Generated
   public Angle(
      float f,
      float f2
   ) {
      this.yaw = f;
      this.pitch = f2;
   }
}
