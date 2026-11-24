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
import dev.leg1tka.api.events.implement.EventMove;
import dev.leg1tka.api.events.implement.EventPacket;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.Vec3d;

public class AirStuck extends Module {
   public static AirStuck INSTANCE = new AirStuck();
   private final ModeSetting mode;
   private final BooleanSetting cancelPackets;
   private final BooleanSetting swapElytra;
   private Vec3d freezePosition;
   private boolean frozen;

   public boolean isFrozen() {
      return this.frozen;
   }

   public AirStuck() {
      super("AirStuck", Category.MOVEMENT);
      String var1 = "AirStuck";
      String var3 = "Мод";
      String var6 = "Обычный";
      this.mode = new ModeSetting(var3, var6, new String[]{"Обычный", "LonyGrief"});
      this.cancelPackets = new BooleanSetting("Отменять пакеты", true);
      this.swapElytra = new BooleanSetting("Свапать элитру", true);
      this.freezePosition = Vec3d.ZERO;
      this.frozen = false;
      this.addSettings(new Setting[]{
         this.mode,
         this.cancelPackets,
         this.swapElytra
      });
   }

   @Override
   public void onEnable() {
      this.frozen = false;
      if (mc.player != null && this.swapElytra.isState()) {
         this.swapChestEquipment();
      }

      if (mc.player != null) {
         ModeSetting var10000 = this.mode;
         if (var10000.is("Обычный")) {
            this.freezePosition = mc.player.getPos();
            this.frozen = true;
         }
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.frozen = false;
      super.onDisable();
   }

   private void swapChestEquipment() {
      ItemStack stack = mc.player
         .getEquippedStack(EquipmentSlot.CHEST);
      if (stack.isOf(
         Items.ELYTRA
      )) {
         int slot2 = SearchitemHelper.findBestChestplateSlot();
         if (slot2
            != -1) {
            this.doSwap(
               slot2
            );
         }
      }
   }

   private void doSwap(
      int val
   ) {
      if (val
            >= 0
         && val
            < 9) {
         mc.interactionManager
            .clickSlot(
               0,
               6,
               val,
               SlotActionType.SWAP,
               mc.player
            );
      } else {
         mc.interactionManager
            .clickSlot(
               0,
               val,
               0,
               SlotActionType.SWAP,
               mc.player
            );
         mc.interactionManager.clickSlot(0, 6, 0, SlotActionType.SWAP, mc.player);
         mc.interactionManager
            .clickSlot(
               0,
               val,
               0,
               SlotActionType.SWAP,
               mc.player
            );
      }

      mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
   }

   @EventLink
   public void onMove(
      EventMove val
   ) {
      if (mc.player != null) {
         ModeSetting var10000 = this.mode;
         if (var10000.is("LonyGrief")
            && !this.frozen
            && mc.player.fallDistance > 0.0F
            && mc.player.getVelocity().y < 0.0) {
            this.freezePosition = mc.player.getPos();
            this.frozen = true;
         }

         if (this.frozen) {
            val.setMovePos(
               Vec3d.ZERO
            );
            mc.player.setPosition(this.freezePosition.x, this.freezePosition.y, this.freezePosition.z);
            mc.player.setVelocity(0.0, 0.0, 0.0);
         }
      }
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (this.frozen
         && val.getType()
            == EventPacket.Type.SEND) {
         if (val.getPacket() instanceof PlayerMoveC2SPacket val2
            )
          {
            if (this.cancelPackets.isState()) {
               val.cancel();
            } else {
               val.cancel();
               SendsilentpacketHelper.sendSilentPacket(
                  this.createFrozenPacket(
                     val2
                  )
               );
            }
         }
      }
   }

   private PlayerMoveC2SPacket createFrozenPacket(
      PlayerMoveC2SPacket val
   ) {
      boolean flag = val.isOnGround();
      boolean flag2 = val.horizontalCollision();
      if (val.changesPosition()
         && val.changesLook()
         )
       {
         return new Full(
            this.freezePosition.x,
            this.freezePosition.y,
            this.freezePosition.z,
            val.getYaw(
               mc.player.getYaw()
            ),
            val.getPitch(
               mc.player.getPitch()
            ),
            flag,
            flag2
         );
      } else if (val.changesPosition()
         )
       {
         return new PositionAndOnGround(
            this.freezePosition.x,
            this.freezePosition.y,
            this.freezePosition.z,
            flag,
            flag2
         );
      } else {
         return (PlayerMoveC2SPacket)(val.changesLook()
            ? new LookAndOnGround(
               val.getYaw(
                  mc.player.getYaw()
               ),
               val.getPitch(
                  mc.player.getPitch()
               ),
               flag,
               flag2
            )
            : new OnGroundOnly(
               flag,
               flag2
            ));
      }
   }
}
