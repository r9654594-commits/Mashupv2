package dev.leg1tka.modules.player;


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

import dev.leg1tka.mixin.IMinecraftClientAccessor;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SmithingTableBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class OpenWalls extends Module {
   public static OpenWalls INSTANCE = new OpenWalls();
   private final NumberSetting reach;

   public OpenWalls() {
      super("OpenWalls", Category.PLAYER);
      this.reach = new NumberSetting(
         "Дистанция", 4.5F, 3.0F, 6.0F, 0.1F
      );
      this.addSettings(new Setting[]{this.reach});
   }

   public boolean tryOpenThroughWall() {
      if (!this.isEnable() || mc.player == null || mc.world == null || mc.interactionManager == null) {
         return false;
      } else if (mc.currentScreen == null && !mc.player.isSpectator()) {
         HitResult hitResult = mc.crosshairTarget;
         if (hitResult instanceof BlockHitResult val
            && hitResult.getType()
               == Type.BLOCK) {
            BlockPos pos = val.getBlockPos();
            if (this.isOpenable(
               mc.world
                  .getBlockState(
                     pos
                  ),
               pos
            )) {
               return false;
            }
         }

         BlockHitResult blockHit = this.findThroughWallContainer();
         if (blockHit
            == null) {
            return false;
         } else {
            ActionResult val2 = mc.interactionManager
               .interactBlock(
                  mc.player,
                  Hand.MAIN_HAND,
                  blockHit
               );
            if (val2.isAccepted()
               )
             {
               mc.player.swingHand(Hand.MAIN_HAND);
               if (mc instanceof IMinecraftClientAccessor val3
                  )
                {
                  val3.setItemUseCooldown(
                     4
                  );
               }

               return true;
            } else {
               val2 = mc.interactionManager
                  .interactBlock(
                     mc.player,
                     Hand.OFF_HAND,
                     blockHit
                  );
               if (val2.isAccepted()
                  )
                {
                  mc.player.swingHand(Hand.OFF_HAND);
                  if (mc instanceof IMinecraftClientAccessor val4
                     )
                   {
                     val4.setItemUseCooldown(
                        4
                     );
                  }

                  return true;
               } else {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   private BlockHitResult findThroughWallContainer() {
      double d = (double)this.reach
         .get();
      Vec3d vec = mc.player
         .getCameraPosVec(1.0F);
      Vec3d vec2 = mc.player
         .getRotationVec(1.0F);
      BlockPos pos = null;
      double d2 = 0.05;

      for (double d3 = 0.0;
         d3
            <= d;
         d3 += d2
      ) {
         Vec3d vec3 = vec.add(
            vec2.multiply(
               d3
            )
         );
         BlockPos pos2 = BlockPos.ofFloored(
            vec3
         );
         if (!pos2.equals(
            pos
         )) {
            pos = pos2;
            if (mc.world
               .isChunkLoaded(
                  pos2
               )) {
               BlockState state = mc.world
                  .getBlockState(
                     pos2
                  );
               if (!state.isAir()
                  && this.isOpenable(
                     state,
                     pos2
                  )) {
                  Direction direction = this.closestFace(
                     pos2,
                     vec
                  );
                  Vec3d vec4 = this.faceHitVec(
                     pos2,
                     direction
                  );
                  return new BlockHitResult(
                     vec4,
                     direction,
                     pos2,
                     false
                  );
               }
            }
         }
      }

      return null;
   }

   private Direction closestFace(
      BlockPos val,
      Vec3d val2
   ) {
      double d = (double)val.getX()
         + 0.5;
      double d2 = (double)val.getY()
         + 0.5;
      double d3 = (double)val.getZ()
         + 0.5;
      double d4 = val2.x
         - d;
      double d5 = val2.y
         - d2;
      double d6 = val2.z
         - d3;
      double d7 = Math.abs(
         d4
      );
      double d8 = Math.abs(
         d5
      );
      double d9 = Math.abs(
         d6
      );
      if (d7
            > d8
         && d7
            > d9
         )
       {
         return d4
               > 0.0
            ? Direction.EAST
            : Direction.WEST;
      } else if (d8
            > d7
         && d8
            > d9
         )
       {
         return d5
               > 0.0
            ? Direction.UP
            : Direction.DOWN;
      } else {
         return d6
               > 0.0
            ? Direction.SOUTH
            : Direction.NORTH;
      }
   }

   private Vec3d faceHitVec(
      BlockPos val,
      Direction val2
   ) {
      double d = (double)val.getX()
         + 0.5
         + (double)val2.getOffsetX()
            * 0.5;
      double d2 = (double)val.getY()
         + 0.5
         + (double)val2.getOffsetY()
            * 0.5;
      double d3 = (double)val.getZ()
         + 0.5
         + (double)val2.getOffsetZ()
            * 0.5;
      d -= (double)val2.getOffsetX()
         * 0.01;
      d2 -= (double)val2.getOffsetY()
         * 0.01;
      d3 -= (double)val2.getOffsetZ()
         * 0.01;
      return new Vec3d(
         d,
         d2,
         d3
      );
   }

   private boolean isOpenable(
      BlockState val,
      BlockPos val2
   ) {
      if (val
            != null
         && !val.isAir()
         )
       {
         Block val3 = val.getBlock();
         if (!(
               val3 instanceof ChestBlock
            )
            && !(
               val3 instanceof AbstractChestBlock
            )
            && !(
               val3 instanceof EnderChestBlock
            )
            && !(
               val3 instanceof BarrelBlock
            )
            && !(
               val3 instanceof ShulkerBoxBlock
            )
            && !(
               val3 instanceof HopperBlock
            )
            && !(
               val3 instanceof AbstractFurnaceBlock
            )
            && !(
               val3 instanceof BrewingStandBlock
            )
            && !(
               val3 instanceof DispenserBlock
            )
            && !(
               val3 instanceof DropperBlock
            )
            && !(
               val3 instanceof CraftingTableBlock
            )
            && !(
               val3 instanceof AnvilBlock
            )
            && !(
               val3 instanceof EnchantingTableBlock
            )
            && !(
               val3 instanceof BeaconBlock
            )
            && !(
               val3 instanceof GrindstoneBlock
            )
            && !(
               val3 instanceof CartographyTableBlock
            )
            && !(
               val3 instanceof LoomBlock
            )
            && !(
               val3 instanceof StonecutterBlock
            )
            && !(
               val3 instanceof SmithingTableBlock
            )
            && !(
               val3 instanceof LecternBlock
            )
            && !(
               val3 instanceof DecoratedPotBlock
            )) {
            String str = Registries.BLOCK
               .getId(
                  val3
               )
               .getPath();
            if (!str.contains(
               "crafter"
            )) {
               if (!str.contains(
                  "chest"
               )) {
                  if (!str.contains(
                     "barrel"
                  )) {
                     if (!str.contains(
                        "shulker"
                     )) {
                        if (!str.contains(
                           "hopper"
                        )) {
                           BlockEntity entity2 = mc.world
                              .getBlockEntity(
                                 val2
                              );
                           return entity2 instanceof LockableContainerBlockEntity
                              || entity2 instanceof Inventory;
                        }
                     }
                  }
               }
            }

            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public float getReach() {
      return this.reach.get();
   }
}
