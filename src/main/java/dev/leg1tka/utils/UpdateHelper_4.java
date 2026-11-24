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



import dev.leg1tka.modules.render.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class UpdateHelper_4 implements IMinecraft {
   public double x;
   public double y;
   public double z;
   public double worldX;
   public double worldY;
   public double worldZ;
   public long time;
   public LivingEntity entity;
   public boolean fading;
   public long fadeStartTime;
   public float vx;
   public float vy;
   public float vz;
   public float rotX;
   public float rotY;
   public float rotZ;
   public float rotSpeedX;
   public float rotSpeedY;
   public float rotSpeedZ;

   public UpdateHelper_4(
      LivingEntity entity2,
      double d,
      double d2,
      double d3
   ) {
      this.entity = entity2;
      this.x = d;
      this.y = d2;
      this.z = d3;
      this.time = System.currentTimeMillis();
      this.rotX = (float)(Math.random() * 360.0);
      this.rotY = (float)(Math.random() * 360.0);
      this.rotZ = (float)(Math.random() * 360.0);
      this.rotSpeedX = 1.4F + (float)Math.random() * 3.4F;
      this.rotSpeedY = 1.4F + (float)Math.random() * 3.4F;
      this.rotSpeedZ = 1.4F + (float)Math.random() * 3.4F;
      this.vx = (float)((Math.random() - 0.5) * 0.0022);
      this.vy = 0.031F + (float)Math.random() * 0.02F;
      this.vz = (float)((Math.random() - 0.5) * 0.0022);
   }

   public void update(
      float f,
      long time2,
      LivingEntity entity2
   ) {
      float height = f
         * 60.0F;
      this.rotX = this.rotX
         + this.rotSpeedX
            * height;
      this.rotY = this.rotY
         + this.rotSpeedY
            * height;
      this.rotZ = this.rotZ
         + this.rotSpeedZ
            * height;
      if (!this.fading) {
         this.x = this.x
            + (double)(
               this.vx
                  * height
            );
         this.y = this.y
            + (double)(
               this.vy
                  * height
            );
         this.z = this.z
            + (double)(
               this.vz
                  * height
            );
         this.vx *= 0.992F;
         this.vz *= 0.992F;
         this.vy *= 0.989F;
         if (this.entity != null) {
            double x2 = Math.max(
               2.2, (double)this.entity.getHeight() * 1.85
            );
            if (this.y
               >= x2
               )
             {
               this.y = x2;
               this.beginFade(
                  time2
               );
               return;
            }
         }

         boolean flag = entity2
               == null
            || this.entity == null
            || !this.entity.isAlive()
            || this.entity
               != entity2;
         if (flag
            || time2
                  - this.time
               >= 560L) {
            this.beginFade(
               time2
            );
         }
      }
   }

   public boolean shouldRemove(
      long time
   ) {
      return this.fading
         && time
               - this.fadeStartTime
            >= 320L;
   }

   public int getRenderColor(
      int n,
      int n2,
      float f,
      long time
   ) {
      float alpha = this.getAlpha(
         time
      );
      if (alpha
         <= 0.001F) {
         return 0;
      } else {
         int color = ColorUtil.replAlpha(
            n,
            (int)(
               alpha
                  * 255.0F
            )
         );
         int color2 = ColorUtil.replAlpha(
            n2,
            (int)(
               alpha
                  * 255.0F
            )
         );
         return TargetESP.INSTANCE
            .overCol(
               color,
               color2,
               f
            );
      }
   }

   public boolean appendCubeFaces(
      BufferBuilder buffer,
      MatrixStack matrices,
      Vec3d vec,
      float f,
      int n
   ) {
      float x2 = (float)(
            n
                  >> 24
               & 255
         )
         / 255.0F;
      if (x2
         <= 0.001F) {
         return false;
      } else {
         Vec3d vec2 = this.getRenderPos(
            f
         );
         if (vec2
            == null) {
            return false;
         } else {
            float x3 = this.fading
               ? MathHelper.lerp(MathHelper.clamp((float)(System.currentTimeMillis() - this.fadeStartTime) / 320.0F, 0.0F, 1.0F), 1.0F, 0.45F)
               : 1.0F;
            float x4 = 0.12F
               * x3;
            matrices.push();
            matrices.translate(
               vec2.x
                  - vec.x,
               vec2.y
                  - vec.y,
               vec2.z
                  - vec.z
            );
            matrices.multiply(
               RotationAxis.POSITIVE_X.rotationDegrees(this.rotX)
            );
            matrices.multiply(
               RotationAxis.POSITIVE_Y.rotationDegrees(this.rotY)
            );
            matrices.multiply(
               RotationAxis.POSITIVE_Z.rotationDegrees(this.rotZ)
            );
            matrices.scale(
               x4,
               x4,
               x4
            );
            Matrix4f matrix = matrices.peek()
               .getPositionMatrix();
            this.appendFaces(
               buffer,
               matrix,
               n
            );
            matrices.pop();
            return true;
         }
      }
   }

   public boolean appendCubeLines(
      BufferBuilder buffer,
      MatrixStack matrices,
      Vec3d vec,
      float f,
      int n
   ) {
      float alpha = (float)(
            n
                  >> 24
               & 255
         )
         / 255.0F;
      if (alpha
         <= 0.001F) {
         return false;
      } else {
         Vec3d vec2 = this.getRenderPos(
            f
         );
         if (vec2
            == null) {
            return false;
         } else {
            float alpha2 = this.fading
               ? MathHelper.lerp(MathHelper.clamp((float)(System.currentTimeMillis() - this.fadeStartTime) / 320.0F, 0.0F, 1.0F), 1.0F, 0.45F)
               : 1.0F;
            float alpha3 = 0.12F
               * alpha2;
            matrices.push();
            matrices.translate(
               vec2.x
                  - vec.x,
               vec2.y
                  - vec.y,
               vec2.z
                  - vec.z
            );
            matrices.multiply(
               RotationAxis.POSITIVE_X.rotationDegrees(this.rotX)
            );
            matrices.multiply(
               RotationAxis.POSITIVE_Y.rotationDegrees(this.rotY)
            );
            matrices.multiply(
               RotationAxis.POSITIVE_Z.rotationDegrees(this.rotZ)
            );
            matrices.scale(
               alpha3,
               alpha3,
               alpha3
            );
            Matrix4f matrix = matrices.peek()
               .getPositionMatrix();
            this.appendEdges(
               buffer,
               matrix,
               ColorUtil.replAlpha(
                  n,
                  Math.max(
                     1,
                     (int)(
                        (float)(
                              n
                                    >> 24
                                 & 255
                           )
                           * 0.7F
                     )
                  )
               )
            );
            matrices.pop();
            return true;
         }
      }
   }

   public boolean appendBloom(
      BufferBuilder buffer,
      MatrixStack matrices,
      Vec3d vec,
      float f,
      float f2,
      float f3,
      int n,
      long time
   ) {
      float alpha = this.getAlpha(
         time
      );
      if (alpha
         <= 0.001F) {
         return false;
      } else {
         Vec3d vec2 = this.getRenderPos(
            f3
         );
         if (vec2
            == null) {
            return false;
         } else {
            float alpha2 = this.fading
               ? MathHelper.lerp(
                  MathHelper.clamp(
                     (float)(
                           time
                              - this.fadeStartTime
                        )
                        / 320.0F,
                     0.0F,
                     1.0F
                  ),
                  1.0F,
                  0.55F
               )
               : 1.0F;
            float alpha3 = 0.95F
               * alpha2;
            int color2 = (int)(
               alpha
                  * 0.15F
                  * 255.0F
            );
            if (color2
               <= 0) {
               return false;
            } else {
               int color3 = n
                     >> 16
                  & 255;
               int color4 = n
                     >> 8
                  & 255;
               int color5 = n
                  & 255;
               matrices.push();
               matrices.translate(
                  vec2.x
                     - vec.x,
                  vec2.y
                     - vec.y,
                  vec2.z
                     - vec.z
               );
               matrices.multiply(
                  RotationAxis.POSITIVE_Y
                     .rotationDegrees(
                        -f
                     )
               );
               matrices.multiply(
                  RotationAxis.POSITIVE_X
                     .rotationDegrees(
                        f2
                     )
               );
               matrices.scale(
                  alpha3,
                  alpha3,
                  alpha3
               );
               Matrix4f matrix = matrices.peek()
                  .getPositionMatrix();
               buffer.vertex(
                     matrix,
                     -0.5F,
                     0.5F,
                     0.0F
                  )
                  .texture(0.0F, 1.0F)
                  .color(
                     color3,
                     color4,
                     color5,
                     color2
                  );
               buffer.vertex(
                     matrix,
                     0.5F,
                     0.5F,
                     0.0F
                  )
                  .texture(1.0F, 1.0F)
                  .color(
                     color3,
                     color4,
                     color5,
                     color2
                  );
               buffer.vertex(
                     matrix,
                     0.5F,
                     -0.5F,
                     0.0F
                  )
                  .texture(1.0F, 0.0F)
                  .color(
                     color3,
                     color4,
                     color5,
                     color2
                  );
               buffer.vertex(
                     matrix,
                     -0.5F,
                     -0.5F,
                     0.0F
                  )
                  .texture(0.0F, 0.0F)
                  .color(
                     color3,
                     color4,
                     color5,
                     color2
                  );
               matrices.pop();
               return true;
            }
         }
      }
   }

   private void beginFade(
      long time
   ) {
      if (!this.fading) {
         Vec3d vec = this.getRenderPos(
            1.0F
         );
         if (vec
            != null) {
            this.worldX = vec.x;
            this.worldY = vec.y;
            this.worldZ = vec.z;
         }

         this.fadeStartTime = time;
         this.fading = true;
         this.entity = null;
      }
   }

   private float getAlpha(
      long time2
   ) {
      if (!this.fading) {
         float alpha = MathHelper.clamp(
            (float)(
                  time2
                     - this.time
               )
               / 140.0F,
            0.0F,
            1.0F
         );
         float alpha2 = 1.0F
            - MathHelper.clamp(
               (float)(
                     time2
                        - this.time
                        - 440L
                  )
                  / 120.0F,
               0.0F,
               0.35F
            );
         return alpha
            * alpha2;
      } else {
         return 1.0F
            - MathHelper.clamp(
               (float)(
                     time2
                        - this.fadeStartTime
                  )
                  / 320.0F,
               0.0F,
               1.0F
            );
      }
   }

   private Vec3d getRenderPos(
      float f
   ) {
      return !this.fading && this.entity != null
         ? new Vec3d(
            MathHelper.lerp(
                  (double)f,
                  this.entity.lastRenderX,
                  this.entity.getX()
               )
               + this.x,
            MathHelper.lerp(
                  (double)f,
                  this.entity.lastRenderY,
                  this.entity.getY()
               )
               + this.y,
            MathHelper.lerp(
                  (double)f,
                  this.entity.lastRenderZ,
                  this.entity.getZ()
               )
               + this.z
         )
         : new Vec3d(this.worldX, this.worldY, this.worldZ);
   }

   private void appendFaces(
      BufferBuilder buffer,
      Matrix4f matrix,
      int n
   ) {
      float alpha = -0.5F;
      float alpha2 = 0.5F;
      int color = ColorUtil.replAlpha(
         n,
         Math.max(
            1,
            (int)(
               (float)(
                     n
                           >> 24
                        & 255
                  )
                  * 0.16F
            )
         )
      );
      this.addFace(
         buffer,
         matrix,
         alpha,
         alpha,
         alpha,
         alpha2,
         alpha2,
         alpha2,
         color
      );
   }

   private void appendEdges(
      BufferBuilder buffer,
      Matrix4f matrix,
      int n
   ) {
      for (byte[] val : TargetESP.CUBE_EDGES) {
         buffer.vertex(
               matrix,
               (float)val[0]
                  * 0.5F,
               (float)val[1]
                  * 0.5F,
               (float)val[2]
                  * 0.5F
            )
            .color(
               n
            );
         buffer.vertex(
               matrix,
               (float)val[3]
                  * 0.5F,
               (float)val[4]
                  * 0.5F,
               (float)val[5]
                  * 0.5F
            )
            .color(
               n
            );
      }
   }

   private void addFace(
      BufferBuilder buffer,
      Matrix4f matrix,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n
   ) {
      buffer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f2,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f2,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f5,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f5,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f5,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f5,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f5,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f5,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f2,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f2,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f5,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f5,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f2,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f5,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f,
            f5,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f2,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f5,
            f3
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f5,
            f6
         )
         .color(
            n
         );
      buffer.vertex(
            matrix,
            f4,
            f2,
            f6
         )
         .color(
            n
         );
   }
}
