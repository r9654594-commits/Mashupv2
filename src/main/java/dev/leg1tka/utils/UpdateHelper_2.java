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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class UpdateHelper_2 {
   private final List<Vec3i> points;
   private final UpdateHelper alpha;
   private Vec3i lastDirection;
   private int currentStepTicks;
   private int lastStepSet;
   private int stepsLeft;
   private boolean removing;
   public LineGlyphes this$0;
   public UpdateHelper_2(LineGlyphes var1, Vec3i var2, int var3) {
      this.this$0 = var1;
      this.points = new ArrayList<>();
      this.alpha = new UpdateHelper(0.0F, 8.0F, EasingFunctions.QUAD_OUT);
      this.points.add(var2);
      this.lastDirection = var1.randomAxisDirection();
      this.stepsLeft = var3;
      this.alpha.setValue(0.0F);
   }

   public void update() {
      this.alpha.update(this.removing ? 0.0F : 1.0F);
      if (!this.removing) {
         if (this.stepsLeft <= 0) {
            this.setWantToRemove();
         } else if (this.currentStepTicks > 0) {
            this.currentStepTicks = this.currentStepTicks
               - (this.this$0.slowSpeed.isState() ? 1 : 2);
            if (this.currentStepTicks < 0) {
               this.currentStepTicks = 0;
            }
         } else {
            Vec3i val = this.points
               .get(this.points.size() - 1);
            boolean flag = false;

            for (int i = 0;
               i
                  < 8;
               i++
            ) {
               Vec3i val2 = this.this$0
                  .nextOrthogonalDirection(this.lastDirection);
               int i2 = this.this$0
                  .randInt(1, 4);
               Vec3i val3 = new Vec3i(
                  val.getX()
                     + val2.getX()
                        * i2,
                  val.getY()
                     + val2.getY()
                        * i2,
                  val.getZ()
                     + val2.getZ()
                        * i2
               );
               if (this.this$0
                  .isSpawnPosFree(
                     val3
                  )) {
                  this.lastDirection = val2;
                  this.lastStepSet = i2;
                  this.currentStepTicks = i2;
                  this.points
                     .add(
                        val3
                     );
                  this.stepsLeft--;
                  flag = true;
                  break;
               }
            }

            if (!flag
               )
             {
               this.setWantToRemove();
            }
         }
      }
   }

   public List<Vec3d> getSmoothedPositions(
      float f
   ) {
      List val = new ArrayList<>(
         this.points.size()
      );
      float x = this.this$0
         .moveAdvanceFromTicks(
            this.lastStepSet,
            this.currentStepTicks,
            f
         );

      for (int i = 0;
         i
            < this.points.size();
         i++
      ) {
         Vec3i val2 = this.points
            .get(
               i
            );
         double x2 = (double)val2.getX();
         double x3 = (double)val2.getY();
         double x4 = (double)val2.getZ();
         if (this.points.size() >= 2
            && i
               == this.points.size() - 1) {
            Vec3i val3 = this.points
               .get(this.points.size() - 2);
            x2 = LineGlyphes.lerp(
               (double)val3.getX(),
               x2,
               (double)x
            );
            x3 = LineGlyphes.lerp(
               (double)val3.getY(),
               x3,
               (double)x
            );
            x4 = LineGlyphes.lerp(
               (double)val3.getZ(),
               x4,
               (double)x
            );
         }

         val.add(
            new Vec3d(
               x2,
               x3,
               x4
            )
         );
      }

      return val;
   }

   public int getPointCount() {
      return this.points.size();
   }

   public float getAlpha() {
      return MathHelper.clamp(this.alpha.getValue(), 0.0F, 1.0F);
   }

   public boolean isRemoving() {
      return this.removing;
   }

   public void setWantToRemove() {
      this.removing = true;
   }

   public boolean isDead() {
      return this.removing && this.getAlpha() <= 0.03F;
   }
}
