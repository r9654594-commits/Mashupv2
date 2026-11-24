package dev.leg1tka.modules.movement;


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
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public class Step extends Module {
   public static Step INSTANCE = new Step();
   public ModeSetting mode;
   public NumberSetting height;
   public BooleanSetting reverse;
   public NumberSetting reverseHeight;
   private int timer;

   public Step() {
      super(
         "Step",
         "Моментально взбирается на блок",
         Category.MOVEMENT
      );
      this.mode = new ModeSetting("Режим", "Vanilla", new String[]{"Vanilla", "NCP", "Motion"});
      this.height = new NumberSetting("Высота", 1.0F, 1.0F, 10.0F, 0.5F);
      this.reverse = new BooleanSetting("Reverse", false);
      this.reverseHeight = new NumberSetting("Высота Reverse", 1.0F, 1.0F, 10.0F, 0.5F);
      this.timer = 0;
      this.addSettings(new Setting[]{
         this.mode,
         this.height,
         this.reverse,
         this.reverseHeight
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.timer = 0;
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.reverse.isState()
            && mc.player.isOnGround()
            && !mc.options.jumpKey.isPressed()
            && !mc.player.isSneaking()
            && !this.isBlockAbove()) {
            float height2 = this.reverseHeight
               .get();
            if (this.canFall(
               height2
            )) {
               Vec3d vec = mc.player
                  .getVelocity();
               mc.player
                  .setVelocity(
                     vec.x,
                     (double)(
                        -height2
                     ),
                     vec.z
                  );
            }
         }

         if (mc.player.horizontalCollision && mc.player.isOnGround() && !mc.options.jumpKey.isPressed()) {
            float height3 = this.getStepHeight();
            if (height3
                  > 0.6F
               && height3
                  <= this.height.get()) {
               if (this.mode.is("Vanilla")) {
                  this.handleVanillaStep(
                     height3
                  );
               }

               if (this.mode.is("NCP")) {
                  this.handleNCPStep(
                     height3
                  );
               }

               if (this.mode.is("Motion")) {
                  this.handleMotionStep(
                     height3
                  );
               }
            }
         } else {
            this.timer = 0;
         }
      }
   }

   private void handleVanillaStep(
      float val
   ) {
      mc.player
         .setPosition(
            mc.player.getX(),
            mc.player.getY()
               + (double)val,
            mc.player.getZ()
         );
   }

   private void handleNCPStep(
      float val
   ) {
      double[] val2 = null;
      double d = mc.player
         .getY();
      if (val
         <= 1.0F) {
         double[] var10000 = new double[2];
         var10000[0] = 0.42;
         var10000[1] = 0.753;
         val2 = var10000;
      } else if (val
         <= 1.5F) {
         double[] var10 = new double[6];
         var10[0] = 0.42;
         var10[1] = 0.75;
         var10[2] = 1.0;
         var10[3] = 1.16;
         var10[4] = 1.23;
         var10[5] = 1.2;
         val2 = var10;
      } else if (val
         <= 2.0F) {
         double[] var11 = new double[8];
         var11[0] = 0.42;
         var11[1] = 0.78;
         var11[2] = 0.63;
         var11[3] = 0.51;
         var11[4] = 0.9;
         var11[5] = 1.21;
         var11[6] = 1.45;
         var11[7] = 1.43;
         val2 = var11;
      } else if (val
         <= 2.5F) {
         double[] var12 = new double[10];
         var12[0] = 0.425;
         var12[1] = 0.821;
         var12[2] = 0.699;
         var12[3] = 0.599;
         var12[4] = 1.022;
         var12[5] = 1.372;
         var12[6] = 1.652;
         var12[7] = 1.869;
         var12[8] = 2.019;
         var12[9] = 1.907;
         val2 = var12;
      } else if (val
         <= 3.0F) {
         double[] var13 = new double[12];
         var13[0] = 0.42;
         var13[1] = 0.78;
         var13[2] = 0.63;
         var13[3] = 0.51;
         var13[4] = 0.9;
         var13[5] = 1.21;
         var13[6] = 1.45;
         var13[7] = 1.43;
         var13[8] = 1.78;
         var13[9] = 2.1;
         var13[10] = 2.4;
         var13[11] = 2.7;
         val2 = var13;
      }

      if (val2
         != null) {
         for (double d2 : val2) {
            mc.player
               .setPosition(
                  mc.player.getX(),
                  d
                     + d2,
                  mc.player.getZ()
               );
         }
      }
   }

   private void handleMotionStep(
      float val
   ) {
      Vec3d vec = mc.player
         .getVelocity();
      double d = 0.42;
      if (val
         <= 1.0F) {
         d = 0.42;
      } else if (val
         <= 1.5F) {
         d = 0.52;
      } else if (val
         <= 2.0F) {
         d = 0.62;
      } else if (val
         <= 2.5F) {
         d = 0.72;
      } else if (val
         <= 3.0F) {
         d = 0.82;
      }

      mc.player
         .setVelocity(
            vec.x,
            d,
            vec.z
         );
   }

   private float getStepHeight() {
      Box box = mc.player
         .getBoundingBox();
      float f = 0.0F;
      double d = 0.3;
      double d2 = Math.toRadians(
         (double)mc.player.getYaw()
      );
      double d3 = -Math.sin(
            d2
         )
         * d;
      double d4 = Math.cos(
            d2
         )
         * d;

      for (double d5 = 0.6;
         d5
            <= (double)this.height.get() + 0.6;
         d5 += 0.1
      ) {
         Box box2 = box.offset(
            d3,
            d5,
            d4
         );

         for (BlockPos pos : BlockPos.iterate(
            (int)Math.floor(
               box2.minX
            ),
            (int)Math.floor(
               box2.minY
            ),
            (int)Math.floor(
               box2.minZ
            ),
            (int)Math.floor(
               box2.maxX
            ),
            (int)Math.floor(
               box2.maxY
            ),
            (int)Math.floor(
               box2.maxZ
            )
         )) {
            BlockState state = mc.world
               .getBlockState(
                  pos
               );
            if (!state.isAir()
               )
             {
               VoxelShape val = state.getCollisionShape(
                  mc.world,
                  pos
               );
               if (!val.isEmpty()
                  )
                {
                  for (Box box3 : val.getBoundingBoxes()) {
                     Box box4 = box3.offset(
                        pos
                     );
                     float f2 = (float)(
                        box4.maxY
                           - mc.player.getY()
                     );
                     if (f2
                           > 0.6F
                        && f2
                           <= this.height.get()) {
                        f = Math.max(
                           f,
                           f2
                        );
                     }
                  }
               }
            }
         }
      }

      return f;
   }

   private boolean isBlockAbove() {
      Box box = mc.player
         .getBoundingBox()
         .offset(0.0, 1.0, 0.0);

      for (BlockPos pos : BlockPos.iterate(
         (int)Math.floor(
            box.minX
         ),
         (int)Math.floor(
            box.minY
         ),
         (int)Math.floor(
            box.minZ
         ),
         (int)Math.floor(
            box.maxX
         ),
         (int)Math.floor(
            box.maxY
         ),
         (int)Math.floor(
            box.maxZ
         )
      )) {
         if (!mc.world
            .getBlockState(
               pos
            )
            .isAir()) {
            return true;
         }
      }

      return false;
   }

   private boolean canFall(
      float val
   ) {
      Box box = mc.player
         .getBoundingBox();

      for (double d = 0.1;
         d
            <= (double)val;
         d += 0.1
      ) {
         Box box2 = box.offset(
            0.0,
            -d,
            0.0
         );

         for (BlockPos pos : BlockPos.iterate(
            (int)Math.floor(
               box2.minX
            ),
            (int)Math.floor(
               box2.minY
            ),
            (int)Math.floor(
               box2.minZ
            ),
            (int)Math.floor(
               box2.maxX
            ),
            (int)Math.floor(
               box2.maxY
            ),
            (int)Math.floor(
               box2.maxZ
            )
         )) {
            if (!mc.world
               .getBlockState(
                  pos
               )
               .isAir()) {
               return false;
            }
         }
      }

      return true;
   }
}
