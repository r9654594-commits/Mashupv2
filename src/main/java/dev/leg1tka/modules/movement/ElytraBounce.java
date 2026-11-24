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
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;

public class ElytraBounce extends Module {
   public static final ElytraBounce INSTANCE = new ElytraBounce();
   private final ModeSetting mode;
   private final NumberSetting delay;
   private final BooleanSetting onlySpace;
   private boolean serverEquippedElytra;
   private int cooldown;
   private int lastElytraSlot;

   public ElytraBounce() {
      super(
         "ElytraBounce",
         "Пакетный баунс на элитрах",
         Category.MOVEMENT
      );
      this.mode = new ModeSetting("Режим свапа", "Hotbar (1.20+)", new String[]{"Hotbar (1.20+)", "Universal (Grim)", "Universal Fast", "Universal Exploit"});
      this.delay = new NumberSetting("Задержка (тики)", 5.0F, 1.0F, 20.0F, 1.0F);
      this.onlySpace = new BooleanSetting("Только пробел", false);
      this.serverEquippedElytra = false;
      this.cooldown = 0;
      this.lastElytraSlot = -1;
      this.addSettings(new Setting[]{
         this.mode,
         this.delay,
         this.onlySpace
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.serverEquippedElytra = false;
      this.cooldown = 0;
      this.lastElytraSlot = -1;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.serverEquippedElytra
         && this.lastElytraSlot != -1
         && mc.player != null) {
         this.sendUnequipPacket(this.lastElytraSlot);
         mc.player.stopGliding();
         this.serverEquippedElytra = false;
      }

      this.cooldown = 0;
      this.lastElytraSlot = -1;
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (this.isEnable() && this.serverEquippedElytra && mc.player != null) {
         if (val.getType()
            == EventPacket.Type.RECEIVE) {
            if (val.getPacket() instanceof ScreenHandlerSlotUpdateS2CPacket val2
               )
             {
               if (val2.getSlot()
                     == 6
                  || val2.getStack()
                        .getItem()
                     == Items.ELYTRA) {
                  val.cancel();
               }
            } else if (val.getPacket() instanceof InventoryS2CPacket
               )
             {
               val.cancel();
            } else if (val.getPacket() instanceof PlaySoundS2CPacket val3
               )
             {
               try {
                  String str = Registries.SOUND_EVENT
                     .getId(
                        (SoundEvent)val3.getSound()
                           .value()
                     )
                     .getPath();
                  if (str
                        != null
                     && (
                        str.contains(
                              "armor.equip"
                           )
                           || str.contains(
                              "elytra"
                           )
                     )) {
                     val.cancel();
                  }
               } catch (Exception var5) {
               }
            }
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val4
   ) {
      if (mc.player != null && mc.world != null) {
         int n = SearchitemHelper.searchItemHotbar(
            Items.ELYTRA
         );
         if (n
               == -1
            && (
               this.mode.is("Universal (Grim)")
                  || this.mode.is("Universal Fast")
                  || this.mode.is("Universal Exploit")
            )) {
            n = SearchitemHelper.searchItem(
               Items.ELYTRA,
               9,
               36
            );
         }

         if (n
            == -1) {
            if (this.serverEquippedElytra && this.lastElytraSlot != -1) {
               this.sendUnequipPacket(this.lastElytraSlot);
               mc.player.stopGliding();
               this.serverEquippedElytra = false;
            }
         } else {
            this.lastElytraSlot = n;
            if (!mc.player.isOnGround() && !mc.player.isTouchingWater() && !mc.player.isClimbing() && !mc.player.getAbilities().flying) {
               if (!this.onlySpace.isState() || mc.options.jumpKey.isPressed()) {
                  if (this.cooldown > 0) {
                     this.cooldown--;
                  } else {
                     if (!this.serverEquippedElytra) {
                        this.sendEquipAndFlyPackets(
                           n
                        );
                        this.serverEquippedElytra = true;
                        this.cooldown = (int)this.delay.get();
                     }
                  }
               }
            } else {
               if (this.serverEquippedElytra) {
                  this.sendUnequipPacket(
                     n
                  );
                  mc.player.stopGliding();
                  this.serverEquippedElytra = false;
               }
            }
         }
      } else {
         this.serverEquippedElytra = false;
         this.cooldown = 0;
      }
   }

   private void sendEquipAndFlyPackets(
      int val
   ) {
      if (this.mode.is("Hotbar (1.20+)")) {
         int n = mc.player
            .getInventory()
            .selectedSlot;
         mc.player.getInventory().selectedSlot = val;
         mc.interactionManager.syncSelectedSlot();
         mc.interactionManager
            .sendSequencedPacket(
               mc.world,
               val2 -> new PlayerInteractItemC2SPacket(
                     Hand.MAIN_HAND,
                     val2,
                     mc.player.getYaw(),
                     mc.player.getPitch()
                  )
            );
         SendsilentpacketHelper.sendSilentPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
         mc.player.getInventory().selectedSlot = n;
         mc.interactionManager.syncSelectedSlot();
      } else if (this.mode.is("Universal (Grim)")) {
         if (val
               >= 0
            && val
               <= 8) {
            SearchitemHelper.swapWithBypassGrim(
               () -> mc.interactionManager
                     .clickSlot(
                        0,
                        6,
                        val,
                        SlotActionType.SWAP,
                        mc.player
                     )
            );
         } else if (val
               >= 9
            && val
               <= 45) {
            SearchitemHelper.swapWithBypassGrim(
               () -> {
                  mc.interactionManager
                     .clickSlot(
                        0,
                        val,
                        8,
                        SlotActionType.SWAP,
                        mc.player
                     );
                  mc.interactionManager
                     .clickSlot(
                        0,
                        6,
                        8,
                        SlotActionType.SWAP,
                        mc.player
                     );
                  mc.interactionManager
                     .clickSlot(
                        0,
                        val,
                        8,
                        SlotActionType.SWAP,
                        mc.player
                     );
               }
            );
         }

         SendsilentpacketHelper.sendSilentPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
      } else {
         if (this.mode.is("Universal Fast")) {
            QueueHelper.queue(
               () -> {
                  try {
                     if (val
                           >= 0
                        && val
                           <= 8) {
                        mc.interactionManager
                           .clickSlot(
                              0,
                              6,
                              val,
                              SlotActionType.SWAP,
                              mc.player
                           );
                     } else if (val
                           >= 9
                        && val
                           <= 45) {
                        mc.interactionManager
                           .clickSlot(
                              0,
                              val,
                              8,
                              SlotActionType.SWAP,
                              mc.player
                           );
                        mc.interactionManager
                           .clickSlot(
                              0,
                              6,
                              8,
                              SlotActionType.SWAP,
                              mc.player
                           );
                        mc.interactionManager
                           .clickSlot(
                              0,
                              val,
                              8,
                              SlotActionType.SWAP,
                              mc.player
                           );
                     }

                     SendsilentpacketHelper.sendSilentPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
                     SendsilentpacketHelper.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
                     mc.player.startGliding();
                  } catch (Exception var2x) {
                  }
               }
            );
            return;
         }

         if (this.mode.is("Universal Exploit")) {
            QueueHelper.queue(
               () -> {
                  try {
                     SendsilentpacketHelper.sendSilentPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
                     if (val
                           >= 0
                        && val
                           <= 8) {
                        mc.interactionManager
                           .clickSlot(
                              0,
                              6,
                              val,
                              SlotActionType.SWAP,
                              mc.player
                           );
                     } else if (val
                           >= 9
                        && val
                           <= 45) {
                        mc.interactionManager
                           .clickSlot(
                              0,
                              val,
                              8,
                              SlotActionType.SWAP,
                              mc.player
                           );
                        mc.interactionManager
                           .clickSlot(
                              0,
                              6,
                              8,
                              SlotActionType.SWAP,
                              mc.player
                           );
                        mc.interactionManager
                           .clickSlot(
                              0,
                              val,
                              8,
                              SlotActionType.SWAP,
                              mc.player
                           );
                     }

                     SendsilentpacketHelper.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
                     mc.player.startGliding();
                  } catch (Exception var2x) {
                  }
               }
            );
            return;
         }
      }

      mc.player.startGliding();
   }

   private void sendUnequipPacket(
      int val
   ) {
      if (this.mode.is("Hotbar (1.20+)")) {
         int n = mc.player
            .getInventory()
            .selectedSlot;
         mc.player.getInventory().selectedSlot = val;
         mc.interactionManager.syncSelectedSlot();
         mc.interactionManager
            .sendSequencedPacket(
               mc.world,
               val2 -> new PlayerInteractItemC2SPacket(
                     Hand.MAIN_HAND,
                     val2,
                     mc.player.getYaw(),
                     mc.player.getPitch()
                  )
            );
         mc.player.getInventory().selectedSlot = n;
         mc.interactionManager.syncSelectedSlot();
      } else if (this.mode.is("Universal (Grim)")) {
         if (val
               >= 0
            && val
               <= 8) {
            SearchitemHelper.swapWithBypassGrim(
               () -> mc.interactionManager
                     .clickSlot(
                        0,
                        6,
                        val,
                        SlotActionType.SWAP,
                        mc.player
                     )
            );
         } else if (val
               >= 9
            && val
               <= 45) {
            SearchitemHelper.swapWithBypassGrim(
               () -> {
                  mc.interactionManager
                     .clickSlot(
                        0,
                        val,
                        8,
                        SlotActionType.SWAP,
                        mc.player
                     );
                  mc.interactionManager
                     .clickSlot(
                        0,
                        6,
                        8,
                        SlotActionType.SWAP,
                        mc.player
                     );
                  mc.interactionManager
                     .clickSlot(
                        0,
                        val,
                        8,
                        SlotActionType.SWAP,
                        mc.player
                     );
               }
            );
         }
      } else if (this.mode.is("Universal Fast")) {
         QueueHelper.queue(
            () -> {
               try {
                  if (val
                        >= 0
                     && val
                        <= 8) {
                     mc.interactionManager
                        .clickSlot(
                           0,
                           6,
                           val,
                           SlotActionType.SWAP,
                           mc.player
                        );
                  } else if (val
                        >= 9
                     && val
                        <= 45) {
                     mc.interactionManager
                        .clickSlot(
                           0,
                           val,
                           8,
                           SlotActionType.SWAP,
                           mc.player
                        );
                     mc.interactionManager
                        .clickSlot(
                           0,
                           6,
                           8,
                           SlotActionType.SWAP,
                           mc.player
                        );
                     mc.interactionManager
                        .clickSlot(
                           0,
                           val,
                           8,
                           SlotActionType.SWAP,
                           mc.player
                        );
                  }

                  SendsilentpacketHelper.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
               } catch (Exception var2x) {
               }
            }
         );
      } else if (this.mode.is("Universal Exploit")) {
         QueueHelper.queue(
            () -> {
               try {
                  if (val
                        >= 0
                     && val
                        <= 8) {
                     mc.interactionManager
                        .clickSlot(
                           0,
                           6,
                           val,
                           SlotActionType.SWAP,
                           mc.player
                        );
                  } else if (val
                        >= 9
                     && val
                        <= 45) {
                     mc.interactionManager
                        .clickSlot(
                           0,
                           val,
                           8,
                           SlotActionType.SWAP,
                           mc.player
                        );
                     mc.interactionManager
                        .clickSlot(
                           0,
                           6,
                           8,
                           SlotActionType.SWAP,
                           mc.player
                        );
                     mc.interactionManager
                        .clickSlot(
                           0,
                           val,
                           8,
                           SlotActionType.SWAP,
                           mc.player
                        );
                  }

                  SendsilentpacketHelper.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
               } catch (Exception var2x) {
               }
            }
         );
      }
   }
}
