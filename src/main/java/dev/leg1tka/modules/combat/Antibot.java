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
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Antibot extends Module {
   public static Antibot INSTANCE = new Antibot();
   public static final List<Entity> isBot = new ArrayList<>();

   public Antibot() {
      super(
         "AntiBot",
         "Определяет ботов на сервере",
         Category.COMBAT
      );
   }

   @EventLink
   public void onUpdate(
      EventUpdate val2
   ) {
      this.newMatrix();
   }

   public void newMatrix() {
      if (mc.world != null) {
         for (PlayerEntity player2 : mc.world
            .getPlayers()) {
            if (mc.player
                  != player2
               && ((ItemStack)player2.getInventory()
                        .armor
                        .get(0))
                     .getItem()
                  != Items.AIR
               && ((ItemStack)player2.getInventory()
                        .armor
                        .get(1))
                     .getItem()
                  != Items.AIR
               && ((ItemStack)player2.getInventory()
                        .armor
                        .get(2))
                     .getItem()
                  != Items.AIR
               && ((ItemStack)player2.getInventory()
                        .armor
                        .get(3))
                     .getItem()
                  != Items.AIR
               && ((ItemStack)player2.getInventory()
                     .armor
                     .get(0))
                  .isEnchantable()
               && ((ItemStack)player2.getInventory()
                     .armor
                     .get(1))
                  .isEnchantable()
               && ((ItemStack)player2.getInventory()
                     .armor
                     .get(2))
                  .isEnchantable()
               && ((ItemStack)player2.getInventory()
                     .armor
                     .get(3))
                  .isEnchantable()
               && player2.getOffHandStack()
                     .getItem()
                  == Items.AIR
               && (
                  ((ItemStack)player2.getInventory()
                              .armor
                              .get(0))
                           .getItem()
                        == Items.LEATHER_BOOTS
                     || ((ItemStack)player2.getInventory()
                              .armor
                              .get(1))
                           .getItem()
                        == Items.LEATHER_LEGGINGS
                     || ((ItemStack)player2.getInventory()
                              .armor
                              .get(2))
                           .getItem()
                        == Items.LEATHER_CHESTPLATE
                     || ((ItemStack)player2.getInventory()
                              .armor
                              .get(3))
                           .getItem()
                        == Items.LEATHER_HELMET
                     || ((ItemStack)player2.getInventory()
                              .armor
                              .get(0))
                           .getItem()
                        == Items.IRON_BOOTS
                     || ((ItemStack)player2.getInventory()
                              .armor
                              .get(1))
                           .getItem()
                        == Items.IRON_LEGGINGS
                     || ((ItemStack)player2.getInventory()
                              .armor
                              .get(2))
                           .getItem()
                        == Items.IRON_CHESTPLATE
                     || ((ItemStack)player2.getInventory()
                              .armor
                              .get(3))
                           .getItem()
                        == Items.IRON_HELMET
               )
               && player2.getMainHandStack()
                     .getItem()
                  != Items.AIR
               && !((ItemStack)player2.getInventory()
                     .armor
                     .get(0))
                  .isDamaged()
               && !((ItemStack)player2.getInventory()
                     .armor
                     .get(1))
                  .isDamaged()
               && !((ItemStack)player2.getInventory()
                     .armor
                     .get(2))
                  .isDamaged()
               && !((ItemStack)player2.getInventory()
                     .armor
                     .get(3))
                  .isDamaged()
               && player2.getHungerManager()
                     .getFoodLevel()
                  == 20) {
               if (!isBot.contains(
                  player2
               )) {
                  isBot.add(
                     player2
                  );
               }

               return;
            }

            isBot.remove(
               player2
            );
         }
      }
   }

   public static boolean checkBot(
      LivingEntity val
   ) {
      return val instanceof PlayerEntity
         && isBot.contains(
            val
         );
   }

   @Override
   public void onDisable() {
      super.onDisable();
      isBot.clear();
   }
}
