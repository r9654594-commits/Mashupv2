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
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;

public class HighJump extends Module {
   public static HighJump INSTANCE = new HighJump();
   private final ModeSetting mode;
   private final NumberSetting slimeMultiplier;
   private boolean wasInBoat;
   private double lastVelY;
   private int cooldown;

   public HighJump() {
      super("HighJump", Category.MOVEMENT);
      String var3 = "Режим";
      String var5 = "Shulker";
      String[] var10005 = new String[3];
      var10005[0] = "Shulker";
      var10005[1] = "Slime";
      int var10007 = 2;
      var10005[var10007] = "Boat";
      this.mode = new ModeSetting(var3, var5, var10005);
      this.slimeMultiplier = new NumberSetting("Множитель", 2.0F, 1.1F, 5.0F, 0.1F);
      this.addSettings(new Setting[]{
         this.mode,
         this.slimeMultiplier
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.wasInBoat = false;
      this.lastVelY = 0.0;
      this.cooldown = 0;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.wasInBoat = false;
      this.lastVelY = 0.0;
      this.cooldown = 0;
   }

   @EventLink
   public void onUpdate(
      EventUpdate val
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.cooldown > 0) {
            this.cooldown--;
         }

         ModeSetting var10000 = this.mode;
         if (var10000.is("Shulker")) {
            this.handleShulker();
         }

         var10000 = this.mode;
         if (var10000.is("Slime")) {
            this.handleSlime();
         }

         var10000 = this.mode;
         if (var10000.is("Boat")) {
            this.handleBoat();
         }
      }
   }

   private void handleShulker() {
      if (mc.currentScreen instanceof ShulkerBoxScreen) {
         BlockPos pos = mc.player
            .getBlockPos();
         BlockPos[] var10000 = new BlockPos[6];
         var10000[0] = pos.down();
         var10000[1] = pos;
         var10000[2] = pos.north();
         var10000[3] = pos.south();
         var10000[4] = pos.east();
         var10000[5] = pos.west();
         BlockPos[] pos2 = var10000;
         boolean flag = false;

         for (BlockPos pos3 : pos2) {
            BlockState state = mc.world
               .getBlockState(
                  pos3
               );
            if (state.getBlock() instanceof ShulkerBoxBlock
               )
             {
               flag = true;
               break;
            }
         }

         if (flag
            )
          {
            mc.player.setVelocity(mc.player.getVelocity().x, 2.0, mc.player.getVelocity().z);
            mc.player.closeHandledScreen();
         }
      }
   }

   private void handleSlime() {
      double d = mc.player
         .getVelocity()
         .y;
      BlockPos pos = mc.player
         .getBlockPos()
         .down();
      BlockPos pos2 = mc.player
         .getBlockPos()
         .down(2);
      boolean flag = mc.world
            .getBlockState(
               pos
            )
            .isOf(Blocks.SLIME_BLOCK)
         || mc.world
            .getBlockState(
               pos2
            )
            .isOf(Blocks.SLIME_BLOCK);
      if (this.lastVelY < -0.1
         && d
            > 0.1
         && flag
         && this.cooldown == 0) {
         double d2 = d
            * (double)this.slimeMultiplier.get();
         mc.player
            .setVelocity(
               mc.player.getVelocity().x,
               d2,
               mc.player.getVelocity().z
            );
         this.cooldown = 5;
      }

      this.lastVelY = d;
   }

   private void handleBoat() {
      boolean flag = mc.player
         .getVehicle() instanceof BoatEntity;
      if (this.wasInBoat
         && !flag
         && this.cooldown == 0) {
         mc.player.setVelocity(mc.player.getVelocity().x, 1.5, mc.player.getVelocity().z);
         this.cooldown = 20;
      }

      this.wasInBoat = flag;
   }
}
