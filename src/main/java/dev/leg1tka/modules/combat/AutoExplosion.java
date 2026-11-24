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

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventBinding;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventUpdate;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public final class AutoExplosion extends Module {
   public static AutoExplosion INSTANCE = new AutoExplosion();
   private final ModeSetting modeBaxa;
   private final KeybindSetting bind;
   private final BooleanSetting explosionOnRightClick;
   private final BooleanSetting keepCrystal;
   private static final double INTERACT_RANGE = 4.5;
   private BlockPos targetPos;
   private int targetSlot;
   private int oldSlot;
   private boolean needSync;
   private Box crystalArea;
   private boolean blocked;
   private boolean internalInteract;

   public AutoExplosion() {
      super("AutoCrystal", "Автоматически взрывает кристалл", Category.COMBAT);
      this.modeBaxa = new ModeSetting("Режим взрыва", "Авто", new String[]{"Авто", "По бинду"});
      this.bind = new KeybindSetting("Бинд", -1).visible(() -> this.modeBaxa.is("По бинду"));
      this.explosionOnRightClick = new BooleanSetting("Взрыв по ПКМ", true);
      this.keepCrystal = new BooleanSetting("Оставлять кристалл", false);
      this.targetSlot = -1;
      this.oldSlot = -1;
      this.addSettings(new Setting[]{
         this.modeBaxa,
         this.bind,
         this.explosionOnRightClick,
         this.keepCrystal
      });
   }

   @EventLink
   public void onBinding(
      EventBinding event
   ) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (this.modeBaxa
            .is(
               "По бинду"
            )) {
            boolean matchesBind = this.bind
                     .getKey()
                  == -1
               ? event.getKey()
                  == CallHelper.createMouseBind(2)
               : event.getKey()
                  == this.bind.getKey();
            if (matchesBind
               )
             {
               this.placeObsidianByCrosshair();
            }
         }
      }
   }

   @EventLink
   public void onPacket(
      EventPacket event
   ) {
      if (mc.player != null && mc.world != null) {
         if (event.getType()
            == EventPacket.Type.SEND) {
            if (!this.internalInteract) {
               if (event.getPacket() instanceof PlayerInteractBlockC2SPacket packet
                  )
                {
                  BlockHitResult hitResult = packet.getBlockHitResult();
                  BlockPos pos = hitResult.getBlockPos();
                  BlockPos crystalPos = pos.offset(
                     hitResult.getSide()
                  );
                  if (this.isHoldingObsidian()
                     && this.isInRange(
                        crystalPos
                     )
                     && !mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(Items.END_CRYSTAL))) {
                     int crystalSlot = this.findCrystalSlot();
                     if (crystalSlot
                        != -1) {
                        this.targetPos = crystalPos;
                        this.targetSlot = crystalSlot;
                        this.blocked = true;
                     }
                  }

                  if (this.explosionOnRightClick.isState()
                     && this.shouldPlaceByRightClick(
                        pos
                     )
                     && this.placeCrystalFromOffhand(
                        hitResult,
                        pos
                     )) {
                     event.cancel();
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onTick(
      EventUpdate event
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.needSync) {
            this.needSync = false;
            this.restoreSelectedSlot();
         }

         if (this.targetPos != null) {
            if (mc.world.getBlockState(this.targetPos).isAir()) {
               this.targetPos = null;
            } else if (this.blocked) {
               this.blocked = false;
            } else {
               this.tryPlaceCrystalFast(this.targetPos);
            }
         }

         this.processCrystalArea();
      } else {
         this.reset();
      }
   }

   private void tryPlaceCrystalFast(
      BlockPos pos
   ) {
      if (this.targetSlot >= 0
         && this.targetSlot <= 8
         && this.canPlaceCrystal(
            pos
         )) {
         this.rotateTo(
            Vec3d.ofCenter(
               pos
            )
         );
         this.oldSlot = mc.player.getInventory().selectedSlot;
         mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.targetSlot));
         mc.player.getInventory().selectedSlot = this.targetSlot;
         Vec3d centerPos = Vec3d.ofCenter(
               pos
            )
            .add(0.0, 0.5, 0.0);
         BlockHitResult hitResult = new BlockHitResult(
            centerPos,
            Direction.UP,
            pos,
            false
         );
         this.sendInteract(
            Hand.MAIN_HAND,
            hitResult
         );
         mc.player.swingHand(Hand.MAIN_HAND);
         this.needSync = true;
         this.crystalArea = this.boxFromBlock(
               pos.up()
            )
            .expand(0.1);
         this.targetPos = null;
      }
   }

   private void processCrystalArea() {
      if (this.crystalArea != null) {
         for (Entity entity : mc.world
            .getOtherEntities(null, this.crystalArea)) {
            if (entity instanceof EndCrystalEntity crystal
               && crystal.isAlive()
               )
             {
               if (!crystal.getBoundingBox()
                  .contains(mc.player.getEyePos())) {
                  this.rotateTo(
                     crystal.getBoundingBox()
                        .getCenter()
                  );
               }

               this.attackCrystal(
                  crystal
               );
               this.crystalArea = null;
               if (!this.keepCrystal.isState()) {
                  this.restoreSelectedSlot();
               }

               return;
            }
         }
      }
   }

   private boolean shouldPlaceByRightClick(
      BlockPos pos
   ) {
      if (mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(Items.END_CRYSTAL))) {
         return false;
      } else if (this.isHoldingBlockForPlace()) {
         return false;
      } else {
         Block block = mc.world
            .getBlockState(
               pos
            )
            .getBlock();
         return block
                  != Blocks.OBSIDIAN
               && block
                  != Blocks.BEDROCK
            ? false
            : mc.world
               .getBlockState(
                  pos.up()
               )
               .isAir();
      }
   }

   private boolean placeCrystalFromOffhand(
      BlockHitResult hitResult,
      BlockPos crystalPos
   ) {
      int screenSlot = this.findScreenSlot(
         Items.END_CRYSTAL
      );
      if (screenSlot
            == -1
         && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
         return false;
      } else {
         boolean swapped = false;
         if (mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
            this.swapSlotToOffhand(
               screenSlot
            );
            swapped = true;
         }

         this.sendInteract(
            Hand.OFF_HAND,
            hitResult
         );
         mc.player.swingHand(Hand.OFF_HAND);
         this.crystalArea = this.boxFromBlock(
               crystalPos.up()
            )
            .expand(0.1);
         if (swapped
            )
          {
            this.swapSlotToOffhand(
               screenSlot
            );
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
         }

         return true;
      }
   }

   private void placeObsidianByCrosshair() {
      int obsidianSlot = this.findScreenSlot(
         Items.OBSIDIAN
      );
      int crystalSlot = this.findCrystalSlot();
      if (obsidianSlot
            != -1
         && crystalSlot
            != -1) {
         if (mc.crosshairTarget instanceof BlockHitResult hitResult
            )
          {
            if (hitResult.getType()
               == Type.BLOCK) {
               if (!mc.world
                  .getBlockState(
                     hitResult.getBlockPos()
                  )
                  .isAir()) {
                  BlockPos placePos = hitResult.getBlockPos()
                     .offset(
                        hitResult.getSide()
                     );
                  this.targetPos = placePos;
                  this.targetSlot = crystalSlot;
                  this.blocked = true;
                  this.swapSlotToOffhand(
                     obsidianSlot
                  );
                  this.sendInteract(
                     Hand.OFF_HAND,
                     hitResult
                  );
                  mc.player.swingHand(Hand.OFF_HAND);
                  this.swapSlotToOffhand(
                     obsidianSlot
                  );
                  mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
               }
            }
         }
      }
   }

   private void attackCrystal(
      EndCrystalEntity crystal
   ) {
        if (mc.player == null || crystal == null || !mc.player.canSee(crystal)) return;
      mc.getNetworkHandler()
         .sendPacket(
            PlayerInteractEntityC2SPacket.attack(
               crystal,
               false
            )
         );
      mc.player.swingHand(Hand.MAIN_HAND);
   }

   private void sendInteract(
      Hand hand,
      BlockHitResult hitResult
   ) {
      this.internalInteract = true;

      try {
         mc.getNetworkHandler()
            .sendPacket(
               new PlayerInteractBlockC2SPacket(
                  hand,
                  hitResult,
                  0
               )
            );
      } finally {
         this.internalInteract = false;
      }
   }

   private void rotateTo(
      Vec3d target
   ) {
      Vec2f rotations = RayTraceUtil.getRotations(
         target
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
         1,
         2,
         false
      );
   }

   private boolean canPlaceCrystal(
      BlockPos pos
   ) {
      BlockPos upPos = pos.up();
      BlockPos up2Pos = pos.up(
         2
      );
      if (!mc.world
         .getBlockState(
            upPos
         )
         .isAir()) {
         return false;
      } else if (!mc.world
         .getBlockState(
            up2Pos
         )
         .isAir()) {
         return false;
      } else {
         Box box = new Box(
            (double)upPos.getX(),
            (double)upPos.getY(),
            (double)upPos.getZ(),
            (double)upPos.getX()
               + 1.0,
            (double)upPos.getY()
               + 2.0,
            (double)upPos.getZ()
               + 1.0
         );

         for (Entity entity : mc.world
            .getOtherEntities(
               null,
               box
            )) {
            if (!(
               entity instanceof EndCrystalEntity
            )) {
               return false;
            }
         }

         return true;
      }
   }

   private int findCrystalSlot() {
      for (int slot = 0;
         slot
            < 9;
         slot++
      ) {
         if (mc.player
               .getInventory()
               .getStack(
                  slot
               )
               .getItem()
            == Items.END_CRYSTAL) {
            return slot;
         }
      }

      return -1;
   }

   private int findScreenSlot(
      Item item
   ) {
      for (int slot = 9;
         slot
            < 45;
         slot++
      ) {
         ItemStack stack = mc.player
            .playerScreenHandler
            .getSlot(
               slot
            )
            .getStack();
         if (stack.getItem()
            == item
            )
          {
            return slot;
         }
      }

      return -1;
   }

   private void swapSlotToOffhand(
      int slot
   ) {
      if (slot
            >= 36
         && slot
            <= 44) {
         mc.interactionManager
            .clickSlot(
               0,
               45,
               slot
                  - 36,
               SlotActionType.SWAP,
               mc.player
            );
      } else {
         mc.interactionManager
            .clickSlot(
               0,
               slot,
               0,
               SlotActionType.SWAP,
               mc.player
            );
         mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.SWAP, mc.player);
         mc.interactionManager
            .clickSlot(
               0,
               slot,
               0,
               SlotActionType.SWAP,
               mc.player
            );
      }
   }

   private void restoreSelectedSlot() {
      if (this.oldSlot != -1) {
         mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.oldSlot));
         mc.player.getInventory().selectedSlot = this.oldSlot;
         this.oldSlot = -1;
      }
   }

   private Box boxFromBlock(
      BlockPos pos
   ) {
      return new Box(
         (double)pos.getX(),
         (double)pos.getY(),
         (double)pos.getZ(),
         (double)pos.getX()
            + 1.0,
         (double)pos.getY()
            + 1.0,
         (double)pos.getZ()
            + 1.0
      );
   }

   private boolean isHoldingObsidian() {
      return mc.player.getMainHandStack().getItem() == Items.OBSIDIAN || mc.player.getOffHandStack().getItem() == Items.OBSIDIAN;
   }

   private boolean isHoldingBlockForPlace() {
      Item mainItem = mc.player
         .getMainHandStack()
         .getItem();
      Item offItem = mc.player
         .getOffHandStack()
         .getItem();
      return mainItem instanceof BlockItem
            && mainItem
               != Items.PLAYER_HEAD
         || offItem instanceof BlockItem
            && offItem
               != Items.PLAYER_HEAD;
   }

   private boolean isInRange(
      BlockPos pos
   ) {
      return mc.player
            .getEyePos()
            .distanceTo(
               Vec3d.ofCenter(
                  pos
               )
            )
         <= 4.5;
   }

   private void reset() {
      if (this.oldSlot != -1 && mc.player != null && mc.getNetworkHandler() != null) {
         this.restoreSelectedSlot();
      }

      this.targetPos = null;
      this.targetSlot = -1;
      this.needSync = false;
      this.crystalArea = null;
      this.blocked = false;
      this.internalInteract = false;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.reset();
   }

   @Generated
   public ModeSetting getModeBaxa() {
      return this.modeBaxa;
   }

   @Generated
   public KeybindSetting getBind() {
      return this.bind;
   }

   @Generated
   public BooleanSetting getExplosionOnRightClick() {
      return this.explosionOnRightClick;
   }

   @Generated
   public BooleanSetting getKeepCrystal() {
      return this.keepCrystal;
   }

   @Generated
   public BlockPos getTargetPos() {
      return this.targetPos;
   }

   @Generated
   public int getTargetSlot() {
      return this.targetSlot;
   }

   @Generated
   public int getOldSlot() {
      return this.oldSlot;
   }

   @Generated
   public boolean isNeedSync() {
      return this.needSync;
   }

   @Generated
   public Box getCrystalArea() {
      return this.crystalArea;
   }

   @Generated
   public boolean isBlocked() {
      return this.blocked;
   }

   @Generated
   public boolean isInternalInteract() {
      return this.internalInteract;
   }

   @Generated
   public void setTargetPos(
      BlockPos targetPos
   ) {
      this.targetPos = targetPos;
   }

   @Generated
   public void setTargetSlot(
      int targetSlot
   ) {
      this.targetSlot = targetSlot;
   }

   @Generated
   public void setOldSlot(
      int oldSlot
   ) {
      this.oldSlot = oldSlot;
   }

   @Generated
   public void setNeedSync(
      boolean needSync
   ) {
      this.needSync = needSync;
   }

   @Generated
   public void setCrystalArea(
      Box crystalArea
   ) {
      this.crystalArea = crystalArea;
   }

   @Generated
   public void setBlocked(
      boolean blocked
   ) {
      this.blocked = blocked;
   }

   @Generated
   public void setInternalInteract(
      boolean internalInteract
   ) {
      this.internalInteract = internalInteract;
   }
}
