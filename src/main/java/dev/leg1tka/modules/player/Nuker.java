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

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Nuker extends Module {
   public static Nuker INSTANCE = new Nuker();
   private final NumberSetting radius;
   private final BooleanSetting breakAll;
   private final BooleanSetting swing;
   private final Set<String> targetBlocks;
   private BlockPos currentTargetBlock;

   public Nuker() {
      super("Nuker", Category.PLAYER);
      this.radius = new NumberSetting("Дистанция", 3.0F, 1.0F, 5.0F, 1.0F);
      this.breakAll = new BooleanSetting("Ломать все блоки", false);
      this.swing = new BooleanSetting("Анимация руки", true);
      this.targetBlocks = new HashSet<>();
      this.addSettings(new Setting[]{
         this.radius,
         this.breakAll,
         this.swing
      });
   }

   @EventLink
   public void onUpdate(
      EventUpdate val2
   ) {
      if (mc.player == null || mc.world == null || mc.interactionManager == null) {
         this.resetBreaking();
      } else if (!this.breakAll.isState() && this.targetBlocks.isEmpty()) {
         this.resetBreaking();
      } else {
         if (!this.isCurrentTargetValid()) {
            this.currentTargetBlock = this.findNewTarget();
         }

         if (this.currentTargetBlock != null) {
            this.breakCurrentTarget();
         }
      }
   }

   private boolean isCurrentTargetValid() {
      return this.currentTargetBlock != null && this.isInRange(this.currentTargetBlock) && this.shouldBreak(this.currentTargetBlock);
   }

   private BlockPos findNewTarget() {
      int n = Math.round(
         this.radius.get()
      );
      BlockPos pos = mc.player
         .getBlockPos();
      return BlockPos.stream(
            pos.add(
               -n,
               0,
               -n
            ),
            pos.add(
               n,
               n,
               n
            )
         )
         .<BlockPos>map(BlockPos::toImmutable)
         .filter(this::isInRange)
         .filter(this::shouldBreak)
         .min(
            Comparator.comparingDouble(
               val -> mc.player
                     .squaredDistanceTo(
                        Vec3d.ofCenter(
                           val
                        )
                     )
            )
         )
         .orElse(null);
   }

   private boolean isInRange(
      BlockPos val
   ) {
      double d = (double)this.radius
         .get();
      return mc.player
            .squaredDistanceTo(
               Vec3d.ofCenter(
                  val
               )
            )
         <= d
            * d;
   }

   private boolean shouldBreak(
      BlockPos val
   ) {
      BlockState state = mc.world
         .getBlockState(
            val
         );
      if (state
            == null
         || state.isAir()
         || state.getHardness(
               mc.world,
               val
            )
            < 0.0F) {
         return false;
      } else if (this.breakAll.isState()) {
         return true;
      } else {
         String str = Registries.BLOCK
            .getId(
               state.getBlock()
            )
            .getPath()
            .toLowerCase();
         return this.targetBlocks
            .contains(
               str
            );
      }
   }

   private void breakCurrentTarget() {
      if (this.currentTargetBlock != null && mc.player != null && mc.interactionManager != null) {
         mc.interactionManager.attackBlock(this.currentTargetBlock, Direction.UP);
         mc.interactionManager.updateBlockBreakingProgress(this.currentTargetBlock, Direction.UP);
         if (this.swing.isState()) {
            mc.player.swingHand(Hand.MAIN_HAND);
         }

         if (mc.world.getBlockState(this.currentTargetBlock).isAir()) {
            this.resetBreaking();
         }
      }
   }

   private void resetBreaking() {
      this.currentTargetBlock = null;
      if (mc.interactionManager != null) {
         mc.interactionManager.cancelBlockBreaking();
      }
   }

   public void addBlock(
      String val
   ) {
      this.targetBlocks
         .add(
            normalizeBlockName(
               val
            )
         );
   }

   public void removeBlock(
      String val
   ) {
      this.targetBlocks
         .remove(
            normalizeBlockName(
               val
            )
         );
   }

   public void clearBlocks() {
      this.targetBlocks.clear();
      this.resetBreaking();
   }

   public boolean isTargetBlock(
      String val
   ) {
      return this.targetBlocks
         .contains(
            normalizeBlockName(
               val
            )
         );
   }

   public Set<String> getTargetBlocks() {
      return new HashSet<>(this.targetBlocks);
   }

   public static String normalizeBlockName(
      String val
   ) {
      if (val
         == null) {
         return "";
      } else {
         String str = val.toLowerCase()
            .trim();
         int n = str.indexOf(
            58
         );
         return n
               >= 0
            ? str.substring(
               n
                  + 1
            )
            : str;
      }
   }

   @Override
   public void onDisable() {
      this.resetBreaking();
      super.onDisable();
   }
}
