package dev.leg1tka.modules.combat;


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

import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventBinding;
import dev.leg1tka.api.events.implement.EventGameUpdate;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class AutoTrap extends Module {
   public static AutoTrap INSTANCE = new AutoTrap();
   private final ModeSetting mode;
   private final NumberSetting distance;
   private final KeybindSetting bind;
   private final BooleanSetting fromInventory;
   private final BooleanSetting rotation;
   private final MultiBooleanSetting targets;
   private final BooleanSetting reverseRotate;
   private PlayerEntity target;
   private int oldSlot;
   private int inventorySlot;
   private boolean placing;
   private boolean use;
   private final List<BlockPos> blocksToPlace;
   private int placeIndex;
   private BlockPos currentBlock;
   private boolean waitingForRotation;
   private int rotationTicks;
   private float restoreYaw;
   private float restorePitch;

   public AutoTrap() {
      super(
         "AutoTrap",
         "Автоматически ставит ловушку вокруг игрока",
         Category.COMBAT
      );
      String var10003 = "Мод";
      this.mode = new ModeSetting(var10003, "Obsidian", new String[]{"Obsidian", "CobWeb"});
      this.distance = new NumberSetting("Дистанция", 3.0F, 1.0F, 5.0F, 0.1F);
      this.bind = new KeybindSetting(
         "Бинд", -1
      );
      this.fromInventory = new BooleanSetting("Из инвентаря", false);
      this.rotation = new BooleanSetting("Ротация", true);
      var10003 = "Таргеты";
      this.targets = new MultiBooleanSetting(var10003, new BooleanSetting[]{
         new BooleanSetting("Игроки", true),
         new BooleanSetting("Невидимые", true),
         new BooleanSetting(
         "Себя", false
      )
      });
      this.reverseRotate = new BooleanSetting("Реверс ротейт", true).visible(this.rotation::isState);
      this.oldSlot = -1;
      this.inventorySlot = -1;
      this.placing = false;
      this.use = false;
      this.blocksToPlace = new ArrayList<>();
      this.placeIndex = 0;
      this.currentBlock = null;
      this.waitingForRotation = false;
      this.rotationTicks = 0;
      this.addSettings(new Setting[]{
         this.mode,
         this.distance,
         this.bind,
         this.fromInventory,
         this.rotation,
         this.reverseRotate,
         this.targets
      });
   }

   @EventLink
   public void onBinding(
      EventBinding val
   ) {
      if (mc.currentScreen == null) {
         if (val.getKey()
            == this.bind.getKey()) {
            this.use = true;
         }
      }
   }

   @EventLink
   public void onGameUpdate(
      EventGameUpdate val3
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.placing && this.currentBlock != null && this.rotation.isState()) {
            this.rotateToBlock(this.currentBlock);
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.use && !this.placing) {
            this.target = this.findTarget();
            if (this.target != null) {
               this.startPlacing();
            }

            this.use = false;
         }

         if (this.placing) {
            this.processPlacing();
         }
      } else {
         this.use = false;
      }
   }

   private void rotateToBlock(
      BlockPos val
   ) {
      Direction direction = this.getPlaceSide(
         val
      );
      if (direction
         != null) {
         BlockPos pos = val.offset(
            direction
         );
         Direction direction2 = direction.getOpposite();
         Vec3d vec = this.getHitVec(
            pos,
            direction2
         );
         Vec2f rotations = RayTraceUtil.getRotations(
            vec
         );
         RotationStorage.update(
            new Angle(
               rotations.x,
               rotations.y
            ),
            360.0F,
            360.0F,
            360.0F,
            360.0F,
            5,
            1,
            false
         );
      }
   }

   private Vec3d getHitVec(
      BlockPos val,
      Direction val2
   ) {
      Vec3d vec = Vec3d.ofCenter(
         val
      );
      return vec.add(
         (double)val2.getOffsetX()
            * 0.5,
         (double)val2.getOffsetY()
            * 0.5,
         (double)val2.getOffsetZ()
            * 0.5
      );
   }

   private boolean isRotatedToBlock(
      BlockPos val
   ) {
      if (!this.rotation.isState()) {
         return true;
      } else {
         Direction direction = this.getPlaceSide(
            val
         );
         if (direction
            == null) {
            return false;
         } else {
            BlockPos pos = val.offset(
               direction
            );
            Direction direction2 = direction.getOpposite();
            Vec3d vec = this.getHitVec(
               pos,
               direction2
            );
            Vec2f rotations = RayTraceUtil.getRotations(
               vec
            );
            float f = Math.abs(
               MathHelper.wrapDegrees(
                  rotations.x
                     - mc.player.getYaw()
               )
            );
            float f2 = Math.abs(
               MathHelper.wrapDegrees(
                  rotations.y
                     - mc.player.getPitch()
               )
            );
            return f
                  < 5.0F
               && f2
                  < 5.0F;
         }
      }
   }

   private void startPlacing() {
      this.blocksToPlace.clear();
      this.placeIndex = 0;
      this.waitingForRotation = false;
      this.rotationTicks = 0;
      BlockPos pos = this.target
         .getBlockPos();
      if (this.mode.is("Obsidian")) {
         this.blocksToPlace
            .add(
               pos.add(
                  1, 0, 0
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  -1, 0, 0
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  0, 0, 1
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  0, 0, -1
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  1, 1, 0
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  -1, 1, 0
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  0, 1, 1
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  0, 1, -1
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  0, 2, 0
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  1, 2, 0
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  -1,
                  2,
                  0
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  0, 2, 1
               )
            );
         this.blocksToPlace
            .add(
               pos.add(
                  0,
                  2,
                  -1
               )
            );
      } else {
         this.blocksToPlace
            .add(
               pos
            );
         this.blocksToPlace
            .add(
               pos.up()
            );
      }

      if (this.fromInventory.isState()) {
         this.oldSlot = mc.player.getInventory().selectedSlot;
         int slot2 = this.findItemSlot();
         if (slot2
            == -1) {
            this.placing = false;
            return;
         }

         if (slot2
            < 9) {
            mc.player.getInventory().selectedSlot = slot2;
            this.inventorySlot = -1;
         } else {
            this.inventorySlot = slot2;
            mc.interactionManager
               .clickSlot(
                  mc.player.currentScreenHandler.syncId,
                  slot2,
                  this.oldSlot,
                  SlotActionType.SWAP,
                  mc.player
               );
         }
      }

      this.placing = true;
   }

   private void processPlacing() {
      if (this.target == null
         || this.target.isAlive() && !Antibot.checkBot(this.target) && !(mc.player.distanceTo(this.target) > this.distance.getValue().floatValue())) {
         if (this.placeIndex >= this.blocksToPlace.size()) {
            this.finishPlacing();
         } else {
            BlockPos pos = this.blocksToPlace
               .get(this.placeIndex);
            this.currentBlock = pos;
            if (!mc.world
               .getBlockState(
                  pos
               )
               .isReplaceable()) {
               this.placeIndex++;
               this.waitingForRotation = false;
               this.rotationTicks = 0;
            } else {
               Direction direction = this.getPlaceSide(
                  pos
               );
               if (direction
                  == null) {
                  this.placeIndex++;
                  this.waitingForRotation = false;
                  this.rotationTicks = 0;
               } else {
                  if (this.rotation.isState()) {
                     if (!this.waitingForRotation) {
                        this.rotateToBlock(
                           pos
                        );
                        this.waitingForRotation = true;
                        this.rotationTicks = 0;
                        return;
                     }

                     this.rotationTicks++;
                     if (!this.isRotatedToBlock(
                           pos
                        )
                        || this.rotationTicks < 2) {
                        this.rotateToBlock(
                           pos
                        );
                        return;
                     }
                  }

                  this.placeBlock(
                     pos
                  );
                  this.placeIndex++;
                  this.waitingForRotation = false;
                  this.rotationTicks = 0;
               }
            }
         }
      } else {
         this.finishPlacing();
      }
   }

   private void finishPlacing() {
      if (this.fromInventory.isState()) {
         if (this.inventorySlot != -1) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, this.inventorySlot, this.oldSlot, SlotActionType.SWAP, mc.player);
            this.inventorySlot = -1;
         } else if (this.oldSlot != -1) {
            mc.player.getInventory().selectedSlot = this.oldSlot;
         }

         this.oldSlot = -1;
      }

      this.placing = false;
      this.target = null;
      this.currentBlock = null;
      this.blocksToPlace.clear();
      this.placeIndex = 0;
      this.waitingForRotation = false;
      this.rotationTicks = 0;
   }

   private Direction getPlaceSide(
      BlockPos val
   ) {
      Direction[] var10000 = new Direction[6];
      var10000[0] = Direction.DOWN;
      var10000[1] = Direction.UP;
      var10000[2] = Direction.NORTH;
      var10000[3] = Direction.SOUTH;
      var10000[4] = Direction.WEST;
      var10000[5] = Direction.EAST;
      Direction[] direction = var10000;

      for (Direction direction2 : direction) {
         BlockPos pos = val.offset(
            direction2
         );
         BlockState state = mc.world
            .getBlockState(
               pos
            );
         if (!state.isReplaceable()
            && !state.isLiquid()
            && state.isSolidBlock(
               mc.world,
               pos
            )) {
            return direction2;
         }
      }

      for (Direction direction3 : direction) {
         BlockPos pos2 = val.offset(
            direction3
         );
         BlockState state2 = mc.world
            .getBlockState(
               pos2
            );
         if (!state2.isReplaceable()
            && !state2.isLiquid()
            )
          {
            return direction3;
         }
      }

      return null;
   }

   private void placeBlock(
      BlockPos val
   ) {
      Direction direction = this.getPlaceSide(
         val
      );
      if (direction
         != null) {
         BlockPos pos = val.offset(
            direction
         );
         Direction direction2 = direction.getOpposite();
         Vec3d vec = this.getHitVec(
            pos,
            direction2
         );
         BlockHitResult blockHit = new BlockHitResult(
            vec,
            direction2,
            pos,
            false
         );
         mc.interactionManager
            .interactBlock(
               mc.player,
               Hand.MAIN_HAND,
               blockHit
            );
         mc.player.swingHand(Hand.MAIN_HAND);
      }
   }

   private int findItemSlot() {
      Item item2 = this.mode
            .is("Obsidian")
         ? Items.OBSIDIAN
         : Items.COBWEB;

      for (int i = 0;
         i
            < 36;
         i++
      ) {
         if (mc.player
               .getInventory()
               .getStack(
                  i
               )
               .getItem()
            == item2
            )
          {
            return i;
         }
      }

      return -1;
   }

   private PlayerEntity findTarget() {
      if (this.targets.is("Себя")) {
         return mc.player;
      } else {
         List<PlayerEntity> player2 = new ArrayList<>();

         for (Entity entity2 : mc.world
            .getEntities()) {
            if (entity2 instanceof PlayerEntity
               )
             {
               PlayerEntity player3 = (PlayerEntity)entity2;
               if (player3
                     != mc.player
                  && player3.isAlive()
                  && !Antibot.checkBot(
                     player3
                  )
                  && this.targets.is("Игроки")
                  && (
                     !player3.hasStatusEffect(
                           StatusEffects.INVISIBILITY
                        )
                        || this.targets.is("Невидимые")
                  )
                  && !AspectDLC.INSTANCE
                     .friendStorage
                     .isFriend(
                        player3.getName()
                           .getString()
                     )
                  && !(
                     mc.player
                           .distanceTo(
                              player3
                           )
                        > this.distance.getValue().floatValue()
                  )) {
                  player2.add(
                     player3
                  );
               }
            }
         }

         if (player2.isEmpty()
            )
          {
            return null;
         } else {
            player2.sort(
               Comparator.comparingDouble(
                  val -> (double)mc.player
                        .distanceTo(
                           val
                        )
               )
            );
            return player2.get(
               0
            );
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.placing) {
         this.finishPlacing();
      }

      this.target = null;
      this.placing = false;
      this.use = false;
      this.currentBlock = null;
      this.blocksToPlace.clear();
      this.placeIndex = 0;
      this.oldSlot = -1;
      this.inventorySlot = -1;
      this.waitingForRotation = false;
      this.rotationTicks = 0;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.placing = false;
      this.use = false;
      this.currentBlock = null;
      this.blocksToPlace.clear();
      this.placeIndex = 0;
      this.oldSlot = -1;
      this.inventorySlot = -1;
      this.waitingForRotation = false;
      this.rotationTicks = 0;
   }
}
