package dev.leg1tka.modules.combat;
import dev.leg1tka.modules.movement.Sprint;


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
import dev.leg1tka.api.events.implement.EventMoveInput;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.PlayerInput;

public class AutoSwap extends Module {
   public static AutoSwap INSTANCE = new AutoSwap();
   private final ModeSetting firstItem;
   private final ModeSetting secondItem;
   private final KeybindSetting swapKey;
   private final BooleanSetting bypassgrim;
   private final BooleanSetting talismanSwap;
   private int bypassTicks;
   private boolean sprintPaused;
   private int swapCooldown;
   private int targetSlot;
   private boolean needSwap;

   public AutoSwap() {
      super("AutoSwap", Category.COMBAT);
      String var1 = "AutoSwap";
      String var3 = "Первый предмет";
      String var9 = "Руна";
      String[] var10005 = new String[5];
      var10005[0] = "Руна";
      var10005[1] = "Тотем";
      int var10007 = 2;
      var10005[var10007] = "Шар";
      var10007 = 3;
      var10005[var10007] = "Гэпл";
      var10007 = 4;
      var10005[var10007] = "Щит";
      this.firstItem = new ModeSetting(var3, var9, var10005);
      String var5 = "Второй предмет";
      String var11 = "Тотем";
      var10005 = new String[5];
      var10005[0] = "Руна";
      var10005[1] = "Тотем";
      var10007 = 2;
      var10005[var10007] = "Шар";
      var10007 = 3;
      var10005[var10007] = "Гэпл";
      var10007 = 4;
      var10005[var10007] = "Щит";
      this.secondItem = new ModeSetting(var5, var11, var10005);
      this.swapKey = new KeybindSetting(
         "Бинд свапа",
         -98
      );
      this.bypassgrim = new BooleanSetting("Обход Grim", true);
      this.talismanSwap = new BooleanSetting(
         "Свапать талисманы", false
      );
      this.targetSlot = -1;
      this.needSwap = false;
      this.addSettings(new Setting[]{
         this.firstItem,
         this.secondItem,
         this.swapKey,
         this.talismanSwap,
         this.bypassgrim
      });
   }

   @Override
   public void onEnable() {
      this.needSwap = false;
      this.targetSlot = -1;
      this.bypassTicks = 0;
      this.swapCooldown = 0;
      super.onEnable();
   }

   @EventLink
   public void onBinding(
      EventBinding val
   ) {
      if (mc.currentScreen == null) {
         if (mc.player != null && mc.world != null) {
            if (val.getKey()
                  == this.swapKey.getKey()
               && this.swapCooldown == 0) {
               this.needSwap = true;
            }
         }
      }
   }

   @EventLink
   public void onInput(
      EventMoveInput val
   ) {
      if (this.bypassgrim.isState() && this.bypassTicks > 0) {
         if (mc.player == null) {
            return;
         }

         mc.player.setSprinting(false);
         val.setForward(
            0.0F
         );
         val.setStrafe(
            0.0F
         );
         val.setJump(
            false
         );
         val.setSneak(
            false
         );
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.swapCooldown > 0) {
            this.swapCooldown--;
         }

         if (this.bypassgrim.isState() && this.bypassTicks > 0) {
            mc.player.setSprinting(false);
            this.bypassTicks--;
            if (this.bypassTicks == 1) {
               this.performSwap();
            }

            if (this.bypassTicks == 0) {
               this.restoreSprint();
            }
         } else {
            if (this.needSwap && this.targetSlot == -1) {
               this.needSwap = false;
               Item item2 = mc.player
                  .getOffHandStack()
                  .getItem();
               Item item3 = this.getItem(
                  this.firstItem.getCurrent()
               );
               Item item4 = this.getItem(
                  this.secondItem.getCurrent()
               );
               int slot2 = this.findItemSlot(
                  item3
               );
               int slot3 = this.findItemSlot(
                  item4
               );
               if (slot2
                     == -1
                  && slot3
                     == -1) {
                  return;
               }

               int n;
               if (item2
                     == item3
                  && slot3
                     != -1) {
                  n = slot3;
               } else if (slot2
                  != -1) {
                  n = slot2;
               } else {
                  n = slot3;
               }

               if (n
                  == -1) {
                  return;
               }

               this.targetSlot = n;
               if (this.bypassgrim.isState()) {
                  QueueHelper.executeFast(this::performSwap);
                  this.swapCooldown = 2;
               } else {
                  this.performSwap();
                  this.swapCooldown = 2;
               }
            }
         }
      }
   }

   private void performSwap() {
      if (this.targetSlot != -1) {
         this.doSwap(this.targetSlot);
         mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
         this.targetSlot = -1;
      }
   }

   private void doSwap(
      int val
   ) {
      mc.interactionManager
         .clickSlot(
            0,
            val,
            40,
            SlotActionType.SWAP,
            mc.player
         );
   }

   private int findItemSlot(
      Item val
   ) {
      if (!this.talismanSwap.isState()) {
         for (int i = 9;
            i
               < 45;
            i++
         ) {
            ItemStack stack = mc.player
               .playerScreenHandler
               .getSlot(
                  i
               )
               .getStack();
            if (stack.getItem()
               == val
               )
             {
               return i;
            }
         }

         return -1;
      } else {
         int n = -1;
         boolean flag = false;

         for (int i2 = 9;
            i2
               < 45;
            i2++
         ) {
            ItemStack stack2 = mc.player
               .playerScreenHandler
               .getSlot(
                  i2
               )
               .getStack();
            if (stack2.getItem()
               == val
               )
             {
               if (val
                  == Items.PLAYER_HEAD) {
                  AttributeModifiersComponent val2 = (AttributeModifiersComponent)stack2.get(
                     DataComponentTypes.ATTRIBUTE_MODIFIERS
                  );
                  if (val2
                        == null
                     || val2.modifiers()
                        .isEmpty()) {
                     continue;
                  }
               }

               boolean flag2 = stack2.hasEnchantments();
               if (n
                     == -1
                  || flag2
                     && !flag
                  )
                {
                  n = i2;
                  flag = flag2;
               }
            }
         }

         return n;
      }
   }

   private Item getItem(
      String val
   ) {
      int var3 = -1;
      switch (val.hashCode()) {
         case 1056824:
            if (val.equals(
               "Шар"
            )) {
               var3 = 2;
            }
            break;
         case 1058035:
            if (val.equals(
               "Щит"
            )) {
               var3 = 5;
            }
            break;
         case 32141790:
            if (val.equals(
               "Гепл"
            )) {
               var3 = 3;
            }
            break;
         case 32164854:
            if (val.equals(
               "Гэпл"
            )) {
               var3 = 4;
            }
            break;
         case 32542454:
            if (val.equals(
               "Руна"
            )) {
               var3 = 0;
            }
            break;
         case 1010520205:
            if (val.equals(
               "Тотем"
            )) {
               var3 = 1;
            }
      }
      return switch (var3) {
         case 0 -> Items.FIREWORK_STAR;
         case 1 -> Items.TOTEM_OF_UNDYING;
         case 2 -> Items.PLAYER_HEAD;
         case 3 -> Items.GOLDEN_APPLE;
         case 4 -> Items.GOLDEN_APPLE;
         case 5 -> Items.SHIELD;
         default -> Items.AIR;
      };
   }

   private void disableSprint() {
      if (!this.sprintPaused) {
         this.sprintPaused = true;
         Sprint.pushPause(1000);
         mc.player.setSprinting(false);
         mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
         if (AspectDLC.INSTANCE.serverStorage.isServerSprinting()) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
         }
      }
   }

   private void restoreSprint() {
      if (this.sprintPaused) {
         this.sprintPaused = false;
         Sprint.popPause();
         mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
      }
   }

   @Override
   public void onDisable() {
      this.bypassTicks = 0;
      this.swapCooldown = 0;
      this.needSwap = false;
      this.targetSlot = -1;
      this.restoreSprint();
      super.onDisable();
   }
}
