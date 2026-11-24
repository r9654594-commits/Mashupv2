package dev.leg1tka.modules.misc;


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
import dev.leg1tka.api.events.implement.EventBinding;
import dev.leg1tka.api.events.implement.EventGameUpdate;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Ecopen extends Module {
   public static Ecopen INSTANCE = new Ecopen();
   private final KeybindSetting openKey = new KeybindSetting(
      "Открыть",
      -1
   );
   private final NumberSetting range = new NumberSetting(
      "Дистанция",
      6.0F,
      3.0F,
      6.0F,
      0.1F
   );
   private BlockPos targetChest = null;
   private boolean shouldRotate = false;
   private int rotationTicks = 0;
   private float currentYaw;
   private float currentPitch;

   public Ecopen() {
      super(
         "EcOpen",
         "Открывает эндер сундук по бинду",
         Category.MISC
      );
      this.addSettings(new Setting[]{
         this.openKey,
         this.range
      });
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   @EventLink
   public void onBinding(
      EventBinding val
   ) {
      if (mc.currentScreen == null && mc.player != null && mc.world != null) {
         if (val.getKey()
            == this.openKey.getKey()) {
            this.findEnderChest();
         }
      }
   }

   @EventLink
   public void onGameUpdate(
      EventGameUpdate val2
   ) {
      if (this.shouldRotate && this.targetChest != null && mc.player != null) {
         if (!mc.world.getBlockState(this.targetChest).isOf(Blocks.ENDER_CHEST)) {
            this.reset();
         } else {
            Vec3d vec = Vec3d.ofCenter(
               this.targetChest
            );
            float[] val = this.calculateRotation(
               vec
            );
            float f = MathHelper.wrapDegrees(
               val[0]
                  - this.currentYaw
            );
            float f2 = val[1]
               - this.currentPitch;
            this.currentYaw += f
               * 0.8F;
            this.currentPitch = MathHelper.clamp(
               this.currentPitch
                  + f2
                     * 0.8F,
               -90.0F,
               90.0F
            );
            RotationStorage.update(new Angle(this.currentYaw, this.currentPitch), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
            this.rotationTicks++;
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val2
   ) {
      if (this.shouldRotate && this.targetChest != null && mc.player != null) {
         if (this.rotationTicks >= 2) {
            Vec3d vec = Vec3d.ofCenter(
                  this.targetChest
               )
               .add(0.0, 0.5, 0.0);
            BlockHitResult blockHit = new BlockHitResult(
               vec,
               Direction.UP,
               this.targetChest,
               false
            );
            mc.interactionManager
               .interactBlock(
                  mc.player,
                  Hand.MAIN_HAND,
                  blockHit
               );
            mc.player.swingHand(Hand.MAIN_HAND);
            this.reset();
         }

         if (this.rotationTicks > 20) {
            this.reset();
         }
      }
   }

   private void findEnderChest() {
      BlockPos pos = mc.player
         .getBlockPos();
      int n = this.range
         .getValue()
         .intValue();
      double d = (double)(
         this.range.getValue().floatValue() * this.range.getValue().floatValue()
      );
      double d2 = Double.MAX_VALUE;
      BlockPos pos2 = null;

      for (int i = -n;
         i
            <= n;
         i++
      ) {
         for (int i2 = -n;
            i2
               <= n;
            i2++
         ) {
            for (int i3 = -n;
               i3
                  <= n;
               i3++
            ) {
               BlockPos pos3 = pos.add(
                  i,
                  i2,
                  i3
               );
               if (mc.world
                  .getBlockState(
                     pos3
                  )
                  .isOf(Blocks.ENDER_CHEST)) {
                  double d3 = mc.player
                     .getEyePos()
                     .squaredDistanceTo(
                        Vec3d.ofCenter(
                           pos3
                        )
                     );
                  if (d3
                        < d2
                     && d3
                        <= d
                     )
                   {
                     d2 = d3;
                     pos2 = pos3;
                  }
               }
            }
         }
      }

      if (pos2
         != null) {
         this.targetChest = pos2;
         this.shouldRotate = true;
         this.rotationTicks = 0;
         this.currentYaw = mc.player.getYaw();
         this.currentPitch = mc.player.getPitch();
      }
   }

   private float[] calculateRotation(
      Vec3d val
   ) {
      Vec3d vec = mc.player
         .getEyePos();
      double d = val.x
         - vec.x;
      double d2 = val.y
         - vec.y;
      double d3 = val.z
         - vec.z;
      double d4 = Math.sqrt(
         d
               * d
            + d3
               * d3
      );
      float f = (float)Math.toDegrees(
            Math.atan2(
               d3,
               d
            )
         )
         - 90.0F;
      float f2 = (float)(
         -Math.toDegrees(
            Math.atan2(
               d2,
               d4
            )
         )
      );
      float[] var10000 = new float[2];
      var10000[0] = f;
      var10000[1] = MathHelper.clamp(
         f2,
         -90.0F,
         90.0F
      );
      return var10000;
   }

   private void reset() {
      this.targetChest = null;
      this.shouldRotate = false;
      this.rotationTicks = 0;
   }
}
