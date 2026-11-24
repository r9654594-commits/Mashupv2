package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;

public final class GetitemcountHelper implements IMinecraft {
   private static int cachedSlot = -1;

   public static int getItemCount(
      Item item
   ) {
      if (mc.player == null) {
         return 0;
      } else {
         int i = 0;

         for (int i2 = 0;
            i2
               < mc.player.getInventory().size();
            i2++
         ) {
            ItemStack stack = mc.player
               .getInventory()
               .getStack(
                  i2
               );
            if (stack.isOf(
               item
            )) {
               i += stack.getCount();
            }
         }

         return i;
      }
   }

   public static FindItemResult getAxe() {
      return findBest(
         val -> val.getItem() instanceof AxeItem,
         false
      );
   }

   public static FindItemResult getAxeHotBar() {
      return findBest(
         val -> val.getItem() instanceof AxeItem,
         true
      );
   }

   public static FindItemResult getPickAxe() {
      return findBest(
         val -> val.getItem() instanceof PickaxeItem,
         false
      );
   }

   public static FindItemResult getPickAxeHotbar() {
      return getPickAxeHotBar();
   }

   public static FindItemResult getPickAxeHotBar() {
      return findBest(
         val -> val.getItem() instanceof PickaxeItem,
         true
      );
   }

   public static FindItemResult getSword() {
      return findBest(
         val -> val.getItem() instanceof SwordItem,
         false
      );
   }

   public static FindItemResult getSwordHotBar() {
      return findBest(
         val -> val.getItem() instanceof SwordItem,
         true
      );
   }

   public static FindItemResult getSkull() {
      return findInHotBar(
         val -> val.isOf(
                  Items.SKELETON_SKULL
               )
               || val.isOf(
                  Items.WITHER_SKELETON_SKULL
               )
               || val.isOf(
                  Items.CREEPER_HEAD
               )
               || val.isOf(
                  Items.PLAYER_HEAD
               )
               || val.isOf(
                  Items.ZOMBIE_HEAD
               )
      );
   }

   public static int getElytra() {
      if (mc.player == null) {
         return -1;
      } else {
         for (ItemStack stack : mc.player
            .getInventory()
            .armor) {
            if (stack.isOf(
                  Items.ELYTRA
               )
               && stack.getDamage()
                  < stack.getMaxDamage()
                     - 1) {
               return -2;
            }
         }

         for (int i = 0;
            i
               < 36;
            i++
         ) {
            ItemStack stack2 = mc.player
               .getInventory()
               .getStack(
                  i
               );
            if (stack2.isOf(
                  Items.ELYTRA
               )
               && stack2.getDamage()
                  < stack2.getMaxDamage()
                     - 1) {
               return i
                     < 9
                  ? i
                     + 36
                  : i;
            }
         }

         return -1;
      }
   }

   public static FindItemResult findInHotBar(
      IClientAction_EfYLFl4o val
   ) {
      if (mc.player != null) {
         if (val.isValid(
            mc.player.getOffHandStack()
         )) {
            return FindItemResult.inOffhand(mc.player.getOffHandStack());
         }

         for (int i = 0;
            i
               < 9;
            i++
         ) {
            ItemStack stack = mc.player
               .getInventory()
               .getStack(
                  i
               );
            if (val.isValid(
               stack
            )) {
               return new FindItemResult(
                  i,
                  true,
                  stack
               );
            }
         }
      }

      return FindItemResult.notFound();
   }

   public static FindItemResult findItemInHotBar(
      List<Item> item
   ) {
      return findInHotBar(
         val -> item.contains(
               val.getItem()
            )
      );
   }

   public static FindItemResult findItemInHotBar(
      Item... item
   ) {
      return findItemInHotBar(
         Arrays.asList(
            item
         )
      );
   }

   public static FindItemResult findInInventory(
      IClientAction_EfYLFl4o val
   ) {
      if (mc.player != null) {
         for (int i = 35;
            i
               >= 0;
            i--
         ) {
            ItemStack stack = mc.player
               .getInventory()
               .getStack(
                  i
               );
            if (val.isValid(
               stack
            )) {
               return new FindItemResult(
                  i,
                  true,
                  stack
               );
            }
         }
      }

      return FindItemResult.notFound();
   }

   public static FindItemResult findItemInInventory(
      List<Item> item
   ) {
      return findInInventory(
         val -> item.contains(
               val.getItem()
            )
      );
   }

   public static FindItemResult findItemInInventory(
      Item... item
   ) {
      return findItemInInventory(
         Arrays.asList(
            item
         )
      );
   }

   public static FindItemResult findBlockInHotBar(
      @NotNull List<Block> list
   ) {
      return findItemInHotBar(
         list.stream()
            .<Item>map(Block::asItem)
            .toList()
      );
   }

   public static FindItemResult findBlockInHotBar(
      Block... val
   ) {
      return findItemInHotBar(
         Arrays.stream(
               val
            )
            .<Item>map(Block::asItem)
            .toList()
      );
   }

   public static FindItemResult findBlockInInventory(
      @NotNull List<Block> list
   ) {
      return findItemInInventory(
         list.stream()
            .<Item>map(Block::asItem)
            .toList()
      );
   }

   public static FindItemResult findBlockInInventory(
      Block... val
   ) {
      return findItemInInventory(
         Arrays.stream(
               val
            )
            .<Item>map(Block::asItem)
            .toList()
      );
   }

   public static void saveSlot() {
      if (mc.player != null) {
         cachedSlot = mc.player.getInventory().selectedSlot;
      }
   }

   public static void returnSlot() {
      if (cachedSlot != -1) {
         switchTo(cachedSlot);
      }

      cachedSlot = -1;
   }

   public static void saveAndSwitchTo(
      int n
   ) {
      saveSlot();
      switchTo(
         n
      );
   }

   public static void switchTo(
      int n
   ) {
      if (mc.player != null
         && mc.getNetworkHandler() != null
         && n
            >= 0
         && n
            <= 8) {
         if (mc.player.getInventory().selectedSlot
            != n
            )
          {
            mc.player.getInventory().selectedSlot = n;
            mc.getNetworkHandler()
               .sendPacket(
                  new UpdateSelectedSlotC2SPacket(
                     n
                  )
               );
         }
      }
   }

   public static void switchToSilent(
      int n
   ) {
      if (mc.player != null
         && mc.getNetworkHandler() != null
         && n
            >= 0
         && n
            <= 8) {
         mc.getNetworkHandler()
            .sendPacket(
               new UpdateSelectedSlotC2SPacket(
                  n
               )
            );
      }
   }

   public static FindItemResult getAntiWeaknessItem() {
      if (mc.player == null) {
         return FindItemResult.notFound();
      } else {
         Item item = mc.player
            .getMainHandStack()
            .getItem();
         return !(
                  item instanceof SwordItem
               )
               && !(
                  item instanceof PickaxeItem
               )
               && !(
                  item instanceof AxeItem
               )
               && !(
                  item instanceof ShovelItem
               )
            ? findInHotBar(
               val -> val.getItem() instanceof SwordItem
                     || val.getItem() instanceof PickaxeItem
                     || val.getItem() instanceof AxeItem
                     || val.getItem() instanceof ShovelItem
            )
            : new FindItemResult(mc.player.getInventory().selectedSlot, true, mc.player.getMainHandStack());
      }
   }

   public static float getHitDamage(
      @NotNull ItemStack stack,
      PlayerEntity player2
   ) {
      if (mc.player != null && mc.world != null) {
         float y = getBaseAttackDamage(
            stack
         );
         if (mc.player.fallDistance > 0.0F) {
            y += y
               / 2.0F;
         }

         if (mc.player.hasStatusEffect(StatusEffects.STRENGTH)) {
            int y2 = Objects.requireNonNull(
                     mc.player.getStatusEffect(StatusEffects.STRENGTH)
                  )
                  .getAmplifier()
               + 1;
            y += 3.0F
               * (float)y2;
         }

         return DamageUtil.getDamageLeft(
            player2,
            y,
            mc.world.getDamageSources().generic(),
            (float)player2.getArmor(),
            (float)player2.getAttributeValue(
               EntityAttributes.ARMOR_TOUGHNESS
            )
         );
      } else {
         return 0.0F;
      }
   }

   public static FindItemResult findBedInHotBar() {
      return findInHotBar(
         val -> val.getItem() instanceof BedItem
      );
   }

   public static FindItemResult findBed() {
      return findInInventory(
         val -> val.getItem() instanceof BedItem
      );
   }

   public static Item getItem(
      String str
   ) {
      if (str
         == null) {
         return Items.AIR;
      } else {
         String str2 = str.toLowerCase();

         for (Block val : Registries.BLOCK) {
            if (val.getTranslationKey()
               .replace("block.minecraft.", "")
               .equals(
                  str2
               )) {
               return Item.fromBlock(
                  val
               );
            }
         }

         for (Item item2 : Registries.ITEM) {
            if (item2.getTranslationKey()
               .replace("item.minecraft.", "")
               .equals(
                  str2
               )) {
               return item2;
            }
         }

         return Items.DIRT;
      }
   }

   public static int getBedsCount() {
      if (mc.player == null) {
         return 0;
      } else {
         int i = 0;

         for (int i2 = 0;
            i2
               < mc.player.getInventory().size();
            i2++
         ) {
            ItemStack stack = mc.player
               .getInventory()
               .getStack(
                  i2
               );
            if (stack.getItem() instanceof BedItem
               )
             {
               i += stack.getCount();
            }
         }

         return i;
      }
   }

   private static FindItemResult findBest(
      IClientAction_EfYLFl4o val,
      boolean flag
   ) {
      if (mc.player == null) {
         return FindItemResult.notFound();
      } else {
         int i = -1;
         float y = 0.0F;
         int i2 = flag
            ? 8
            : 35;

         for (int i3 = 0;
            i3
               <= i2;
            i3++
         ) {
            ItemStack stack = mc.player
               .getInventory()
               .getStack(
                  i3
               );
            if (val.isValid(
               stack
            )) {
               float y2 = getBaseAttackDamage(
                  stack
               );
               if (y2
                  > y
                  )
                {
                  y = y2;
                  i = i3;
               }
            }
         }

         return i
               == -1
            ? FindItemResult.notFound()
            : new FindItemResult(
               i,
               true,
               mc.player
                  .getInventory()
                  .getStack(
                     i
                  )
            );
      }
   }

   private static float getBaseAttackDamage(
      ItemStack stack
   ) {
      AttributeModifiersComponent val = (AttributeModifiersComponent)stack.getOrDefault(
         DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT
      );
      double y = 1.0;

      for (Entry val2 : val.modifiers()) {
         if (val2.attribute()
            .equals(EntityAttributes.ATTACK_DAMAGE)) {
            y += val2.modifier()
               .value();
         }
      }

      return (float)y;
   }

   public static boolean isHolding(
      Item item
   ) {
      return mc.player != null
         && (
            mc.player
                  .getMainHandStack()
                  .isOf(
                     item
                  )
               || mc.player
                  .getOffHandStack()
                  .isOf(
                     item
                  )
         );
   }

   public static Hand getHand(
      Item item
   ) {
      if (mc.player == null) {
         return null;
      } else if (mc.player
         .getOffHandStack()
         .isOf(
            item
         )) {
         return Hand.OFF_HAND;
      } else {
         return mc.player
               .getMainHandStack()
               .isOf(
                  item
               )
            ? Hand.MAIN_HAND
            : null;
      }
   }

   @Generated
   private GetitemcountHelper() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
