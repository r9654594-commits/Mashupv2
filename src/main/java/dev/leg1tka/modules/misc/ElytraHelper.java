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
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;

public class ElytraHelper extends Module {
   public static final ElytraHelper INSTANCE = new ElytraHelper();
   private final ModeSetting mode;
   private final KeybindSetting swapKey;
   private final KeybindSetting fireworkKey;
   private final ModeSetting throwFireworkMode;
   private final BooleanSetting autoTakeoff;
   private boolean firework;
   private boolean swapped;
   private int air;

   public ElytraHelper() {
      super(
         "ElytraHelper", "Elytra Helper", Category.MISC
      );
      String var10003 = "Мод";
      String var10004 = "Vanilla";
      String[] var10005 = new String[3];
      var10005[0] = "Vanilla";
      var10005[1] = "Grim";
      var10005[2] = "Polar";
      this.mode = new ModeSetting(var10003, var10004, var10005);
      this.swapKey = new KeybindSetting(
         "Кнопка свапа",
         -1
      );
      this.fireworkKey = new KeybindSetting(
         "Кнопка феерверка",
         -1
      );
      var10003 = "Мод пуска феера";
      var10004 = "Обычный";
      var10005 = new String[3];
      var10005[0] = "Обычный";
      var10005[1] = "Легитный";
      var10005[2] = "WellMine";
      this.throwFireworkMode = new ModeSetting(var10003, var10004, var10005);
      this.autoTakeoff = new BooleanSetting("Автовзлёт", true);
      this.addSettings(new Setting[]{
         this.mode,
         this.swapKey,
         this.fireworkKey,
         this.throwFireworkMode,
         this.autoTakeoff
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.air = 0;
      this.swapped = false;
      this.firework = false;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.air = 0;
      this.swapped = false;
      this.firework = false;
   }

   @EventLink
   public void onKey(
      EventBinding val
   ) {
      if (this.isEnable() && mc.player != null) {
         if (val.getKey()
            == this.swapKey.getKey()) {
            this.swapped = true;
         }

         if (val.getKey()
               == this.fireworkKey.getKey()
            && mc.player.isGliding()) {
            this.firework = true;
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      if (this.isEnable() && mc.player != null) {
         if (this.swapped) {
            this.swapped = false;
            this.swap(this.mode, mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA);
         }

         if (this.autoTakeoff.isState()) {
            ItemStack stack = mc.player
               .getEquippedStack(EquipmentSlot.CHEST);
            if (stack.getItem()
                  != Items.ELYTRA
               || mc.player.isInLava()
               || mc.player.isTouchingWater()
               || mc.player.hasVehicle()
               || mc.player.isSpectator()) {
               this.air = 0;
            } else if (mc.player.isOnGround()) {
               this.air = 0;
               if (!mc.options.jumpKey.isPressed() && !mc.player.isGliding()) {
                  mc.player.jump();
               }
            } else if (!mc.player.isGliding()) {
               this.air++;
               if (this.air >= 1) {
                  SendsilentpacketHelper.sendSilentPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
                  mc.player.startGliding();
                  this.air = 0;
               }
            } else {
               this.air = 0;
            }
         }

         if (this.firework) {
            String var4 = this.throwFireworkMode.getCurrent();
            int var3 = -1;
            switch (var4.hashCode()) {
               case -356678047:
                  if (var4.equals("WellMine")) {
                     var3 = 2;
                  }
                  break;
               case 828965756:
                  if (var4.equals("Обычный")) {
                     var3 = 0;
                  }
                  break;
               case 1843870376:
                  if (var4.equals("Легитный")) {
                     var3 = 1;
                  }
            }

            switch (var3) {
               case 0:
                  SearchitemHelper.swapAndUseHvH(Items.FIREWORK_ROCKET);
                  break;
               case 1:
                  SearchitemHelper.swapAndUseLegit(Items.FIREWORK_ROCKET);
                  break;
               case 2:
                  SearchitemHelper.swapAndUseWellMine(Items.FIREWORK_ROCKET);
            }

            this.firework = false;
         }
      }
   }

   public void swap(
      ModeSetting val,
      boolean val2
   ) {
      String var3 = val.getCurrent();
      int var4 = -1;
      switch (var3.hashCode()) {
         case 2228079:
            if (var3.equals("Grim")) {
               var4 = 1;
            }
            break;
         case 77295390:
            if (var3.equals("Polar")) {
               var4 = 2;
            }
            break;
         case 1897755483:
            if (var3.equals("Vanilla")) {
               var4 = 0;
            }
      }

      switch (var4) {
         case 0:
            this.vanillaSwap(
               val2
            );
            break;
         case 1:
            this.grimSwap(
               val2
            );
            break;
         case 2:
            this.polarSwap(
               val2
            );
      }
   }

   private void vanillaSwap(
      boolean val
   ) {
      int n = val
         ? SearchitemHelper.findBestChestplateSlot()
         : SearchitemHelper.findBestElytraSlot();
      if (n
         == -1) {
         String str = "Элитра"
            + Formatting.GRAY
            + " не найдена в инвентаре";
         if (val
            )
          {
            str = "Нагрудник"
               + Formatting.GRAY
               + " не найден в инвентаре";
         }

         ChatUtil.sendMessage(
            str
         );
      } else {
         if (n
               >= 0
            && n
               <= 8) {
            mc.interactionManager
               .clickSlot(
                  0,
                  6,
                  n,
                  SlotActionType.SWAP,
                  mc.player
               );
         } else if (n
               >= 8
            && n
               <= 45) {
            mc.interactionManager
               .clickSlot(
                  0,
                  n,
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
                  n,
                  8,
                  SlotActionType.SWAP,
                  mc.player
               );
         }
      }
   }

   public void swapGrim(
      boolean val
   ) {
      this.grimSwap(
         val
      );
   }

   private void grimSwap(
      boolean val
   ) {
      int n = val
         ? SearchitemHelper.findBestChestplateSlot()
         : SearchitemHelper.findBestElytraSlot();
      if (n
         == -1) {
         String str = "Элитра"
            + Formatting.GRAY
            + " не найдена в инвентаре";
         if (val
            )
          {
            str = "Нагрудник"
               + Formatting.GRAY
               + " не найден в инвентаре";
         }

         ChatUtil.sendMessage(
            str
         );
      } else {
         if (n
               >= 0
            && n
               <= 8) {
            SearchitemHelper.swapWithBypassGrim(
               () -> mc.interactionManager
                     .clickSlot(
                        0,
                        6,
                        n,
                        SlotActionType.SWAP,
                        mc.player
                     )
            );
         } else if (n
               >= 8
            && n
               <= 45) {
            SearchitemHelper.swapWithBypassGrim(
               () -> {
                  mc.interactionManager
                     .clickSlot(
                        0,
                        n,
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
                        n,
                        8,
                        SlotActionType.SWAP,
                        mc.player
                     );
               }
            );
         }
      }
   }

   private void polarSwap(
      boolean val
   ) {
      int n = val
         ? SearchitemHelper.findBestChestplateSlot()
         : SearchitemHelper.findBestElytraSlot();
      if (n
         == -1) {
         String str = "Элитра"
            + Formatting.GRAY
            + " не найдена в инвентаре";
         if (val
            )
          {
            str = "Нагрудник"
               + Formatting.GRAY
               + " не найден в инвентаре";
         }

         ChatUtil.sendMessage(
            str
         );
      } else {
         if (n
               >= 0
            && n
               <= 8) {
            SearchitemHelper.swapWithBypassPolar(
               () -> mc.interactionManager
                     .clickSlot(
                        0,
                        6,
                        n,
                        SlotActionType.SWAP,
                        mc.player
                     )
            );
         } else if (n
               >= 8
            && n
               <= 45) {
            SearchitemHelper.swapWithBypassPolar(
               () -> {
                  mc.interactionManager
                     .clickSlot(
                        0,
                        n,
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
                        n,
                        8,
                        SlotActionType.SWAP,
                        mc.player
                     );
               }
            );
         }
      }
   }
}
