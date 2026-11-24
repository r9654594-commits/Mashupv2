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

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class MaxDamage extends Module {
   public static MaxDamage INSTANCE = new MaxDamage();
   public final ModeSetting mode;
   public int revertSlot;
   public boolean wasSprint;
   public boolean legitRevert;
   private Entity target;

   public MaxDamage() {
      super("MaxDamage", Category.COMBAT);
      String var1 = "MaxDamage";
      String var2 = "Режим";
      String var3 = "Пакетный";
      this.mode = new ModeSetting(var2, var3, new String[]{"Пакетный", "Легитный"});
      this.revertSlot = -1;
      this.wasSprint = false;
      this.legitRevert = false;
      this.addSettings(new Setting[]{this.mode});
      EventInvoker.register(this);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.doRevert();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val5
   ) {
      if (this.revertSlot != -1) {
         this.doRevert();
      }
   }

   private void doRevert() {
      if (this.revertSlot != -1) {
         if (this.legitRevert) {
            int slot2 = this.revertSlot;
            Entity entity2 = this.target;
            this.revertSlot = -1;
            this.target = null;
            if (entity2
                  != null
               && mc.player != null && mc.player.canSee(entity2)) {
               mc.interactionManager
                  .attackEntity(
                     mc.player,
                     entity2
                  );
               mc.player.swingHand(Hand.MAIN_HAND);
            }

            this.swap(
               slot2
            );
            this.close();
         } else {
            this.swap(this.revertSlot);
            this.close();
            this.revertSlot = -1;
         }
      }
   }

   public boolean exec(
      Entity val
   ) {
      if (this.isEnable() && mc.player != null && mc.interactionManager != null && val != null && mc.player.canSee(val)) {
         if (this.revertSlot != -1) {
            return false;
         } else {
            int slot2 = this.findBestSlot();
            if (slot2
               == -1) {
               return false;
            } else {
               ModeSetting var10000 = this.mode;
               if (var10000.is("Пакетный")) {
                  this.ep(
                     val,
                     slot2
                  );
               } else {
                  this.el(
                     val,
                     slot2
                  );
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private void ep(
      Entity val,
      int val2
   ) {
      QueueHelper.executeFast(
         () -> {
            if (mc.player == null || val == null || !mc.player.canSee(val)) return;
            this.swap(
               val2
            );
            mc.interactionManager
               .attackEntity(
                  mc.player,
                  val
               );
            mc.player.swingHand(Hand.MAIN_HAND);
            this.revertSlot = val2;
            this.legitRevert = false;
         }
      );
   }

   private void el(
      Entity val,
      int val2
   ) {
      this.target = val;
      QueueHelper.executeFast(
         () -> {
            this.swap(
               val2
            );
            this.revertSlot = val2;
            this.legitRevert = true;
         }
      );
   }

   private int findBestSlot() {
      if (mc.player == null) {
         return -1;
      } else {
         double d = this.getDmg(
            mc.player.getOffHandStack()
         );
         double d2 = d;
         int n = -1;

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
            if (!stack.isEmpty()
               && (
                  stack.getItem()
                        == Items.PLAYER_HEAD
                     || stack.getItem()
                        == Items.TOTEM_OF_UNDYING
               )) {
               double d3 = this.getDmg(
                  stack
               );
               if (d3
                  > d2
                  )
                {
                  d2 = d3;
                  n = i;
               }
            }
         }

         return n;
      }
   }

   private double getDmg(
      ItemStack val
   ) {
      if (val
            != null
         && !val.isEmpty()
         )
       {
         AttributeModifiersComponent val2 = (AttributeModifiersComponent)val.get(
            DataComponentTypes.ATTRIBUTE_MODIFIERS
         );
         if (val2
            == null) {
            return 0.0;
         } else {
            double d = 0.0;

            for (Entry val3 : val2.modifiers()) {
               RegistryEntry val4 = val3.attribute();
               if (val4
                     != null
                  && val4.matches(
                     EntityAttributes.ATTACK_DAMAGE
                  )) {
                  d += val3.modifier()
                     .value();
               }
            }

            return d;
         }
      } else {
         return 0.0;
      }
   }

   private void swap(
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

   private void close() {
      mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
   }
}
