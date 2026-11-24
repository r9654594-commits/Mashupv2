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


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;

public class ExecuteHelper_7 extends ExecuteHelper_8 {
   public ExecuteHelper_7() {
      super("tp");
   }

   @Override
   public void execute(
      LiteralArgumentBuilder<CommandSource> val
   ) {
      val.then(
         this.arg("Y", IntegerArgumentType.integer())
            .executes(
               val2 -> {
                  int x = (Integer)val2.getArgument(
                     "Y", Integer.class
                  );
                  mc.player
                     .setPosition(
                        mc.player.getX(),
                        mc.player.getY()
                           + (double)x,
                        mc.player.getZ()
                     );
                  return 1;
               }
            )
      );
      val.then(
         this.literal("up")
            .executes(
               val3 -> {
                  this.clipToSafeBlock(true);
                  return 1;
               }
            )
      );
      val.then(
         this.literal("down")
            .executes(
               val4 -> {
                  this.clipToSafeBlock(false);
                  return 1;
               }
            )
      );
   }

   private void clipToSafeBlock(
      boolean flag
   ) {
      if (mc.player != null && mc.world != null) {
         int i = mc.player
            .getBlockY();
         int i2 = mc.world
            .getBottomY();
         int i3 = mc.world
               .getTopYInclusive()
            - 2;
         int i4 = flag
            ? 1
            : -1;
         int i5 = flag
            ? i
               + 1
            : i
               - 1;
         int i6 = flag
            ? i3
            : i2;

         for (int i7 = i5;
            flag
               ? i7
                  <= i6
               : i7
                  >= i6;
            i7 += i4
         ) {
            if (this.isSafeStandPosition(
               i7
            )) {
               VoxelShape val = mc.world
                  .getBlockState(
                     new BlockPos(
                        mc.player.getBlockX(),
                        i7
                           - 1,
                        mc.player.getBlockZ()
                     )
                  )
                  .getCollisionShape(
                     mc.world,
                     new BlockPos(
                        mc.player.getBlockX(),
                        i7
                           - 1,
                        mc.player.getBlockZ()
                     )
                  );
               double x = val.isEmpty()
                  ? 0.0
                  : val.getMax(
                     Axis.Y
                  );
               mc.player
                  .setPosition(
                     mc.player.getX(),
                     (double)i7
                        + x,
                     mc.player.getZ()
                  );
               return;
            }
         }
      }
   }

   private boolean isSafeStandPosition(
      int n
   ) {
      BlockPos pos = new BlockPos(
         mc.player.getBlockX(),
         n
            - 1,
         mc.player.getBlockZ()
      );
      BlockPos pos2 = pos.up();
      BlockPos pos3 = pos2.up();
      BlockState state = mc.world
         .getBlockState(
            pos
         );
      return state.getCollisionShape(
               mc.world,
               pos
            )
            .isEmpty()
         ? false
         : mc.world
               .getBlockState(
                  pos2
               )
               .getCollisionShape(
                  mc.world,
                  pos2
               )
               .isEmpty()
            && mc.world
               .getBlockState(
                  pos3
               )
               .getCollisionShape(
                  mc.world,
                  pos3
               )
               .isEmpty();
   }
}
