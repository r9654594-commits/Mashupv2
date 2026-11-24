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
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class AutoCart extends Module {
   public static AutoCart INSTANCE = new AutoCart();
   private static final int MAX_PREDICTION_TICKS = 80;
   private static final long CART_PLACE_DELAY_MS = 100L;
   private static final double RANGE = 5.0;
   private static final double RANGE_SQ = 25.0;
   private final Set<Integer> knownArrows;
   private final Set<Integer> processedArrows;
   private long nextCartPlaceAttemptMs;

   public AutoCart() {
      super("AutoCart", Category.MISC);
      String var1 = "AutoCart";
      this.knownArrows = new HashSet<>();
      this.processedArrows = new HashSet<>();
   }

   @Override
   public void onEnable() {
      this.knownArrows.clear();
      this.processedArrows.clear();
      this.nextCartPlaceAttemptMs = 0L;
      this.cacheExistingArrows();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.knownArrows.clear();
      this.processedArrows.clear();
      this.nextCartPlaceAttemptMs = 0L;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val5
   ) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         Set val = new HashSet<>();

         for (Entity entity2 : mc.world
            .getEntities()) {
            if (entity2 instanceof PersistentProjectileEntity
               )
             {
               PersistentProjectileEntity entity3 = (PersistentProjectileEntity)entity2;
               if (!(
                     entity3 instanceof TridentEntity
                  )
                  && entity3.getOwner()
                     == mc.player) {
                  int n = entity3.getId();
                  val.add(
                     n
                  );
                  this.knownArrows
                     .add(
                        n
                     );
                  if (!this.processedArrows
                        .contains(
                           n
                        )
                     && this.hasRequiredItemsInHotbar()
                     && this.shouldProcessArrow(
                        entity3
                     )) {
                     PlaceposHelper val2 = this.predictLanding(
                        entity3
                     );
                     if (val2
                        != null) {
                        if (mc.player
                              .squaredDistanceTo(
                                 Vec3d.ofCenter(
                                    val2.placePos()
                                 )
                              )
                           > 25.0) {
                           this.processedArrows
                              .add(
                                 n
                              );
                        } else if (this.placeCartAt(
                              val2.placePos()
                           )
                           || entity3.isOnGround()
                           )
                         {
                           this.processedArrows
                              .add(
                                 n
                              );
                        }
                     }
                  }
               }
            }
         }

         this.knownArrows
            .retainAll(
               val
            );
         this.processedArrows
            .retainAll(
               val
            );
      }
   }

   private void cacheExistingArrows() {
      if (mc.player != null && mc.world != null) {
         for (Entity entity2 : mc.world
            .getEntities()) {
            if (entity2 instanceof PersistentProjectileEntity
               )
             {
               PersistentProjectileEntity entity3 = (PersistentProjectileEntity)entity2;
               if (!(
                     entity3 instanceof TridentEntity
                  )
                  && entity3.getOwner()
                     == mc.player) {
                  this.knownArrows
                     .add(
                        entity3.getId()
                     );
               }
            }
         }
      }
   }

   private boolean hasRequiredItemsInHotbar() {
      return this.searchItemHotbar(Items.RAIL) != -1
         && this.searchItemHotbar(Items.TNT_MINECART) != -1;
   }

   private boolean shouldProcessArrow(
      PersistentProjectileEntity val
   ) {
      return !val.isOnGround()
         && val.getVelocity()
               .lengthSquared()
            > 1.0E-4;
   }

   private PlaceposHelper predictLanding(
      PersistentProjectileEntity val
   ) {
      if (mc.world == null) {
         return null;
      } else {
         Vec3d pos = val.getPos();
         Vec3d velocity = val.getVelocity();

         for (int i = 1;
            i
               <= 80;
            i++
         ) {
            Vec3d vec = pos;
            pos = pos.add(
               velocity
            );
            velocity = this.updateMotion(
               val,
               vec,
               velocity
            );
            BlockHitResult blockHit = this.raycast(
               vec,
               pos,
               val
            );
            if (blockHit.getType()
               != Type.MISS) {
               BlockPos pos2 = this.resolvePlacePos(
                  blockHit
               );
               if (pos2
                  != null) {
                  return new PlaceposHelper(
                     pos2,
                     i
                  );
               }

               return null;
            }

            if (pos.y
               < (double)(mc.world.getBottomY() - 2)) {
               break;
            }
         }

         return null;
      }
   }

   private Vec3d updateMotion(
      PersistentProjectileEntity val,
      Vec3d val2,
      Vec3d val3
   ) {
      if (mc.world == null) {
         return val3;
      } else {
         FluidState val4 = mc.world
            .getBlockState(
               BlockPos.ofFloored(
                  val2
               )
            )
            .getFluidState();
         boolean flag = val4.isIn(
            FluidTags.WATER
         );
         double d = flag
            ? 0.6
            : 0.99;
         return val3.multiply(
               d
            )
            .add(
               0.0,
               -val.getFinalGravity(),
               0.0
            );
      }
   }

   private BlockHitResult raycast(
      Vec3d val,
      Vec3d val2,
      Entity val3
   ) {
      return mc.world
         .raycast(
            new RaycastContext(
               val,
               val2,
               ShapeType.COLLIDER,
               FluidHandling.NONE,
               val3
            )
         );
   }

   private BlockPos resolvePlacePos(
      BlockHitResult val
   ) {
      if (mc.world == null) {
         return null;
      } else {
         BlockPos pos = val.getBlockPos();
         if (this.isRail(
            pos
         )) {
            return pos;
         } else {
            BlockPos pos2 = val.getSide()
                  == Direction.UP
               ? pos.up()
               : pos;
            if (this.canPlaceRailAt(
               pos2
            )) {
               return pos2;
            } else {
               BlockPos pos3 = pos.up();
               if (this.canPlaceRailAt(
                  pos3
               )) {
                  return pos3;
               } else {
                  BlockPos pos4 = pos.down();
                  return this.canPlaceRailAt(
                        pos4
                     )
                     ? pos4
                     : null;
               }
            }
         }
      }
   }

   private boolean placeCartAt(
      BlockPos val
   ) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         int n = this.searchItemHotbar(
            Items.RAIL
         );
         int n2 = this.searchItemHotbar(
            Items.TNT_MINECART
         );
         if (n
               != -1
            && n2
               != -1) {
            int slot2 = this.getSelectedSlot();

            boolean var6;
            try {
               if (this.isRail(
                     val
                  )
                  || this.placeRail(
                     val,
                     n
                  )) {
                  if (this.hasTntMinecart(
                     val
                  )) {
                     return true;
                  }

                  if (System.currentTimeMillis() >= this.nextCartPlaceAttemptMs) {
                     if (this.placeTntMinecart(
                        val,
                        n2
                     )) {
                        this.nextCartPlaceAttemptMs = System.currentTimeMillis() + 100L;
                        this.restockMinecartSlot(
                           n2
                        );
                     }

                     return this.hasTntMinecart(
                        val
                     );
                  }

                  return false;
               }

               var6 = false;
            } finally {
               this.setSelectedSlot(
                  slot2
               );
            }

            return var6;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean isRail(
      BlockPos val
   ) {
      return mc.world != null
         && mc.world
            .getBlockState(
               val
            )
            .isIn(BlockTags.RAILS);
   }

   private boolean canPlaceRailAt(
      BlockPos val
   ) {
      if (mc.world == null) {
         return false;
      } else if (this.isRail(
         val
      )) {
         return true;
      } else {
         BlockState state = mc.world
            .getBlockState(
               val
            );
         if (!this.isRailReplaceable(
            state
         )) {
            return false;
         } else {
            BlockPos pos = val.down();
            return mc.world
               .getBlockState(
                  pos
               )
               .isSideSolidFullSquare(
                  mc.world,
                  pos,
                  Direction.UP
               );
         }
      }
   }

   private boolean isRailReplaceable(
      BlockState val
   ) {
      if (val.isReplaceable()
         )
       {
         return true;
      } else if (val.isIn(
         BlockTags.SNOW
      )) {
         return true;
      } else {
         return val.isIn(
               BlockTags.SWORD_EFFICIENT
            )
            ? true
            : val.isOf(
                  Blocks.SHORT_GRASS
               )
               || val.isOf(
                  Blocks.TALL_GRASS
               )
               || val.isOf(
                  Blocks.FERN
               )
               || val.isOf(
                  Blocks.LARGE_FERN
               )
               || val.isOf(
                  Blocks.DEAD_BUSH
               )
               || val.isOf(
                  Blocks.VINE
               )
               || val.isOf(
                  Blocks.SNOW
               );
      }
   }

   private boolean placeRail(
      BlockPos val,
      int val2
   ) {
      if (mc.player != null && mc.interactionManager != null) {
         this.setSelectedSlot(
            val2
         );
         BlockPos pos = val.down();
         BlockHitResult blockHit = new BlockHitResult(
            new Vec3d(
               (double)val.getX()
                  + 0.5,
               (double)val.getY(),
               (double)val.getZ()
                  + 0.5
            ),
            Direction.UP,
            pos,
            false
         );
         ActionResult val3 = mc.interactionManager
            .interactBlock(
               mc.player,
               Hand.MAIN_HAND,
               blockHit
            );
         if (val3.isAccepted()
            )
          {
            mc.player.swingHand(Hand.MAIN_HAND);
            return true;
         } else {
            return this.isRail(
               val
            );
         }
      } else {
         return false;
      }
   }

   private boolean placeTntMinecart(
      BlockPos val,
      int val2
   ) {
      if (mc.player != null && mc.interactionManager != null) {
         this.setSelectedSlot(
            val2
         );
         BlockHitResult blockHit = new BlockHitResult(
            new Vec3d(
               (double)val.getX()
                  + 0.5,
               (double)val.getY()
                  + 0.15,
               (double)val.getZ()
                  + 0.5
            ),
            Direction.UP,
            val,
            false
         );
         ActionResult val3 = mc.interactionManager
            .interactBlock(
               mc.player,
               Hand.MAIN_HAND,
               blockHit
            );
         if (val3.isAccepted()
            )
          {
            mc.player.swingHand(Hand.MAIN_HAND);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void restockMinecartSlot(
      int val
   ) {
      if (mc.player != null && mc.interactionManager != null) {
         if (val
               >= 0
            && val
               <= 8) {
            if (!mc.player
               .getInventory()
               .getStack(
                  val
               )
               .isOf(Items.TNT_MINECART)) {
               int n = this.searchItem(
                  Items.TNT_MINECART,
                  9,
                  36
               );
               if (n
                  != -1) {
                  mc.interactionManager
                     .clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        n,
                        val,
                        SlotActionType.SWAP,
                        mc.player
                     );
               }
            }
         }
      }
   }

   private boolean hasTntMinecart(
      BlockPos val
   ) {
      if (mc.world == null) {
         return false;
      } else {
         Box box = new Box(
               val
            )
            .expand(0.2, 1.0, 0.2);

         for (Entity entity2 : mc.world
            .getOtherEntities(
               null,
               box
            )) {
            if (entity2 instanceof TntMinecartEntity
               )
             {
               return true;
            }
         }

         return false;
      }
   }

   private int searchItemHotbar(
      Item val
   ) {
      return this.searchItem(
         val,
         0,
         9
      );
   }

   private int getSelectedSlot() {
      return mc.player.getInventory().selectedSlot;
   }

   private void setSelectedSlot(
      int val
   ) {
      if (mc.player != null
         && val
            >= 0
         && val
            <= 8) {
         mc.player.getInventory().selectedSlot = val;
         if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler()
               .sendPacket(
                  new UpdateSelectedSlotC2SPacket(
                     val
                  )
               );
         }
      }
   }

   private int searchItem(
      Item val,
      int val2,
      int val3
   ) {
      if (mc.player == null) {
         return -1;
      } else {
         for (int i = val2;
            i
               < val3;
            i++
         ) {
            if (mc.player
               .getInventory()
               .getStack(
                  i
               )
               .isOf(
                  val
               )) {
               return i;
            }
         }

         return -1;
      }
   }
}
