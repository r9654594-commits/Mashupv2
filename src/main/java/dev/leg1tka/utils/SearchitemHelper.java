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



import dev.leg1tka.modules.movement.*;
import com.google.common.collect.Lists;
import com.mojang.text2speech.Narrator;
import dev.leg1tka.AspectDLC;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.collection.DefaultedList;

public class SearchitemHelper implements IMinecraft {
   public static final SearchitemHelper instance = new SearchitemHelper();

   public static int searchItem(
      Item item
   ) {
      for (int i = 0;
         i
            < mc.player.getInventory().getChangeCount();
         i++
      ) {
         if (mc.player
            .getInventory()
            .getStack(
               i
            )
            .getItem()
            .equals(
               item
            )) {
            return i;
         }
      }

      return -1;
   }

   public static int searchItem(
      Item item,
      int n,
      int n2
   ) {
      for (int i = n;
         i
            < n2;
         i++
      ) {
         if (mc.player
            .getInventory()
            .getStack(
               i
            )
            .getItem()
            .equals(
               item
            )) {
            return i;
         }
      }

      return -1;
   }

   public static int searchItem(
      List<Item> item
   ) {
      for (int i = 0;
         i
            < mc.player.getInventory().getChangeCount();
         i++
      ) {
         for (Item item2 : item) {
            if (mc.player
               .getInventory()
               .getStack(
                  i
               )
               .getItem()
               .equals(
                  item2
               )) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int searchItemHotbar(
      Item item
   ) {
      for (int i = 0;
         i
            < 9;
         i++
      ) {
         if (mc.player
            .getInventory()
            .getStack(
               i
            )
            .getItem()
            .equals(
               item
            )) {
            return i;
         }
      }

      return -1;
   }

   public static int searchItemHotbar(
      List<Item> item
   ) {
      for (int i = 0;
         i
            < 9;
         i++
      ) {
         for (Item item2 : item) {
            if (mc.player
               .getInventory()
               .getStack(
                  i
               )
               .getItem()
               .equals(
                  item2
               )) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int searchItemStack(
      Predicate<ItemStack> stack
   ) {
      for (int i = 0;
         i
            < 45;
         i++
      ) {
         ItemStack stack2 = mc.player
            .getInventory()
            .getStack(
               i
            );
         if (!stack2.isEmpty()
            && stack.test(
               stack2
            )) {
            return i;
         }
      }

      return -1;
   }

   public static int searchHotbarStack(
      Predicate<ItemStack> stack
   ) {
      for (int i = 0;
         i
            < 9;
         i++
      ) {
         ItemStack stack2 = mc.player
            .getInventory()
            .getStack(
               i
            );
         if (!stack2.isEmpty()
            && stack.test(
               stack2
            )) {
            return i;
         }
      }

      return -1;
   }

   public static void swapWithBypassGrim(
      Runnable val
   ) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         QueueHelper.queue(
            () -> {
               try {
                  val.run();
                  SendsilentpacketHelper.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
               } catch (Exception var2) {
                  var2.printStackTrace();
               }
            }
         );
      }
   }

   public static void swapWithBypassPolar(
      Runnable val
   ) {
      swapWithBypassPolar(
         val,
         100L
      );
   }

   public static void swapWithBypassPolar(
      Runnable val,
      long time
   ) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         try {
            OngameupdateHelper.addTask(
               new DurationHolder(
                  time,
                  () -> {
                     if (mc.player.isUsingItem()) {
                        mc.interactionManager.stopUsingItem(mc.player);
                     }

                     val.run();
                     SendsilentpacketHelper.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
                  }
               )
            );
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }
   }

   public static void swapAndUseHvH(
      Item item
   ) {
      if (!mc.player
         .getItemCooldownManager()
         .isCoolingDown(
            new ItemStack(
               item
            )
         )) {
         int i = searchItem(
            item,
            9,
            45
         );
         int i2 = searchItem(
            item,
            0,
            9
         );
         int i3 = mc.player
            .getInventory()
            .selectedSlot;
         boolean flag = mc.player
            .isUsingItem();
         Hand hand = mc.player
            .getActiveHand();
         if (mc.player.getMainHandStack().getItem()
            == item
            )
          {
            if (!flag
               )
             {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
         } else if (mc.player.getOffHandStack().getItem()
            == item
            )
          {
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
         } else if (flag
            && hand
               == Hand.MAIN_HAND) {
            if (i2
               != -1) {
               mc.interactionManager
                  .clickSlot(
                     0,
                     36
                        + i2,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
               mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
               mc.interactionManager
                  .clickSlot(
                     0,
                     36
                        + i2,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
            } else if (i
               != -1) {
               mc.interactionManager
                  .clickSlot(
                     0,
                     i,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
               mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
               mc.interactionManager
                  .clickSlot(
                     0,
                     i,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
            }
         } else if (i2
            != -1) {
            mc.getNetworkHandler()
               .sendPacket(
                  new UpdateSelectedSlotC2SPacket(
                     i2
                  )
               );
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.getNetworkHandler()
               .sendPacket(
                  new UpdateSelectedSlotC2SPacket(
                     i3
                  )
               );
         } else {
            if (i
               != -1) {
               int i4 = -1;

               for (int i5 = 0;
                  i5
                     < 8;
                  i5++
               ) {
                  ItemStack stack = mc.player
                     .getInventory()
                     .getStack(
                        i5
                     );
                  if (stack.isEmpty()
                     )
                   {
                     i4 = i5;
                  }

                  if (stack.getUseAction()
                     == UseAction.NONE) {
                     i4 = i5;
                  }
               }

               int i6 = i4
                     == -1
                  ? 8
                  : i4;
               QueueHelper.queue(
                  () -> {
                     mc.interactionManager
                        .clickSlot(
                           0,
                           i,
                           i6,
                           SlotActionType.SWAP,
                           mc.player
                        );
                     mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                     mc.getNetworkHandler()
                        .sendPacket(
                           new UpdateSelectedSlotC2SPacket(
                              i6
                           )
                        );
                     mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                     mc.getNetworkHandler()
                        .sendPacket(
                           new UpdateSelectedSlotC2SPacket(
                              i3
                           )
                        );
                     mc.interactionManager
                        .clickSlot(
                           0,
                           i,
                           i6,
                           SlotActionType.SWAP,
                           mc.player
                        );
                     mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                  }
               );
            }
         }
      }
   }

   public static void swapAndUseLegit(
      Item item
   ) {
      if (!mc.player
         .getItemCooldownManager()
         .isCoolingDown(
            new ItemStack(
               item
            )
         )) {
         int i = searchItem(
            item,
            9,
            45
         );
         int i2 = searchItem(
            item,
            0,
            9
         );
         int i3 = mc.player
            .getInventory()
            .selectedSlot;
         boolean flag = mc.player
            .isUsingItem();
         Hand hand = mc.player
            .getActiveHand();
         if (mc.player.getMainHandStack().getItem()
            == item
            )
          {
            if (!flag
               )
             {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
         } else if (mc.player.getOffHandStack().getItem()
            == item
            )
          {
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
         } else if (flag
            && hand
               == Hand.MAIN_HAND) {
            if (i2
               != -1) {
               mc.interactionManager
                  .clickSlot(
                     0,
                     36
                        + i2,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
               mc.interactionManager
                  .sendSequencedPacket(
                     mc.world,
                     val -> new PlayerInteractItemC2SPacket(
                           Hand.OFF_HAND,
                           val,
                           mc.player.getYaw(),
                           mc.player.getPitch()
                        )
                  );
               mc.interactionManager
                  .clickSlot(
                     0,
                     36
                        + i2,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
            } else if (i
               != -1) {
               mc.interactionManager
                  .clickSlot(
                     0,
                     i,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
               mc.interactionManager
                  .sendSequencedPacket(
                     mc.world,
                     val2 -> new PlayerInteractItemC2SPacket(
                           Hand.OFF_HAND,
                           val2,
                           mc.player.getYaw(),
                           mc.player.getPitch()
                        )
                  );
               mc.interactionManager
                  .clickSlot(
                     0,
                     i,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
            }
         } else if (i2
            != -1) {
            mc.player.getInventory().selectedSlot = i2;
            mc.interactionManager.syncSelectedSlot();
            mc.interactionManager
               .sendSequencedPacket(
                  mc.world,
                  val3 -> new PlayerInteractItemC2SPacket(
                        Hand.MAIN_HAND,
                        val3,
                        mc.player.getYaw(),
                        mc.player.getPitch()
                     )
               );
            mc.player.getInventory().selectedSlot = i3;
            mc.interactionManager.syncSelectedSlot();
         } else {
            if (i
               != -1) {
               int i4 = -1;

               for (int i5 = 0;
                  i5
                     < 8;
                  i5++
               ) {
                  ItemStack stack = mc.player
                     .getInventory()
                     .getStack(
                        i5
                     );
                  if (stack.isEmpty()
                     )
                   {
                     i4 = i5;
                  }

                  if (stack.getUseAction()
                     == UseAction.NONE) {
                     i4 = i5;
                  }
               }

               int i6 = i4
                     == -1
                  ? 8
                  : i4;
               mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
               mc.interactionManager
                  .clickSlot(
                     0,
                     i,
                     i6,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
               mc.player.getInventory().selectedSlot = i6;
               mc.interactionManager.syncSelectedSlot();
               mc.interactionManager
                  .sendSequencedPacket(
                     mc.world,
                     val4 -> new PlayerInteractItemC2SPacket(
                           Hand.MAIN_HAND,
                           val4,
                           mc.player.getYaw(),
                           mc.player.getPitch()
                        )
                  );
               mc.player.getInventory().selectedSlot = i3;
               mc.interactionManager.syncSelectedSlot();
               mc.interactionManager
                  .clickSlot(
                     0,
                     i,
                     i6,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
               mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
            }
         }
      }
   }

   public static void swapAndUseWellMine(
      Item item
   ) {
      if (!mc.player
         .getItemCooldownManager()
         .isCoolingDown(
            new ItemStack(
               item
            )
         )) {
         int i = searchItem(
            item,
            9,
            45
         );
         int i2 = searchItem(
            item,
            0,
            9
         );
         int i3 = mc.player
            .getInventory()
            .selectedSlot;
         boolean flag = mc.player
            .isUsingItem();
         Hand hand = mc.player
            .getActiveHand();
         if (mc.player.getMainHandStack().getItem()
            == item
            )
          {
            if (!flag
               )
             {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
         } else if (mc.player.getOffHandStack().getItem()
            == item
            )
          {
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
         } else if (flag
            && hand
               == Hand.MAIN_HAND) {
            if (i2
               != -1) {
               mc.interactionManager
                  .clickSlot(
                     0,
                     36
                        + i2,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
               OngameupdateHelper.addTimeTask(
                  new DurationHolder_1(
                     ClientOption_BSfnhH93.TICKS,
                     1,
                     () -> {
                        mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
                        mc.player.swingHand(Hand.OFF_HAND);
                        OngameupdateHelper.addTimeTask(
                           new DurationHolder_1(
                              ClientOption_BSfnhH93.TICKS,
                              5,
                              () -> {
                                 mc.interactionManager
                                    .clickSlot(
                                       0,
                                       36
                                          + i2,
                                       40,
                                       SlotActionType.SWAP,
                                       mc.player
                                    );
                                 mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                              },
                              true
                           )
                        );
                     },
                     true
                  )
               );
            } else if (i
               != -1) {
               mc.interactionManager
                  .clickSlot(
                     0,
                     i,
                     40,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
               OngameupdateHelper.addTimeTask(
                  new DurationHolder_1(
                     ClientOption_BSfnhH93.TICKS,
                     1,
                     () -> {
                        mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
                        mc.player.swingHand(Hand.OFF_HAND);
                        OngameupdateHelper.addTimeTask(
                           new DurationHolder_1(
                              ClientOption_BSfnhH93.TICKS,
                              5,
                              () -> {
                                 mc.interactionManager
                                    .clickSlot(
                                       0,
                                       i,
                                       40,
                                       SlotActionType.SWAP,
                                       mc.player
                                    );
                                 mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                              },
                              true
                           )
                        );
                     },
                     true
                  )
               );
            }
         } else if (i2
            != -1) {
            mc.player.getInventory().selectedSlot = i2;
            mc.interactionManager.syncSelectedSlot();
            OngameupdateHelper.addTimeTask(
               new DurationHolder_1(
                  ClientOption_BSfnhH93.TICKS,
                  1,
                  () -> {
                     mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                     mc.player.swingHand(Hand.MAIN_HAND);
                     OngameupdateHelper.addTimeTask(
                        new DurationHolder_1(
                           ClientOption_BSfnhH93.TICKS,
                           5,
                           () -> {
                              mc.player.getInventory().selectedSlot = i3;
                              mc.interactionManager.syncSelectedSlot();
                           },
                           true
                        )
                     );
                  },
                  true
               )
            );
         } else {
            if (i
               != -1) {
               int i4 = -1;

               for (int i5 = 0;
                  i5
                     < 8;
                  i5++
               ) {
                  ItemStack stack = mc.player
                     .getInventory()
                     .getStack(
                        i5
                     );
                  if (stack.isEmpty()
                     )
                   {
                     i4 = i5;
                  }

                  if (stack.getUseAction()
                     == UseAction.NONE) {
                     i4 = i5;
                  }
               }

               int i6 = i4
                     == -1
                  ? 8
                  : i4;
               mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
               if (AspectDLC.INSTANCE.serverStorage.isServerSprinting()) {
                  mc.player.setSprinting(false);
                  mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
                  if (!Sprint.INSTANCE.isEnable()) {
                     mc.options.sprintKey.setPressed(false);
                  }
               }

               mc.interactionManager
                  .clickSlot(
                     0,
                     i,
                     i6,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
               mc.player.getInventory().selectedSlot = i6;
               mc.interactionManager.syncSelectedSlot();
               OngameupdateHelper.addTimeTask(
                  new DurationHolder_1(
                     ClientOption_BSfnhH93.TICKS,
                     1,
                     () -> {
                        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        OngameupdateHelper.addTimeTask(
                           new DurationHolder_1(
                              ClientOption_BSfnhH93.TICKS,
                              5,
                              () -> {
                                 mc.player.getInventory().selectedSlot = i3;
                                 mc.interactionManager.syncSelectedSlot();
                                 mc.interactionManager
                                    .clickSlot(
                                       0,
                                       i,
                                       i6,
                                       SlotActionType.SWAP,
                                       mc.player
                                    );
                                 mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                                 mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
                              },
                              true
                           )
                        );
                     },
                     true
                  )
               );
            }
         }
      }
   }

   public static void clickSlotNoSync(
      int n,
      int n2,
      int n3,
      SlotActionType val,
      PlayerEntity player2
   ) {
      ScreenHandler hand = player2.currentScreenHandler;
      if (n
         != hand.syncId
         )
       {
         Narrator.LOGGER
            .warn(
               "Ignoring click in mismatching container. Click in {}, player has {}.",
               n,
               hand.syncId
            );
      } else {
         DefaultedList val2 = hand.slots;
         int i = val2.size();
         List val3 = Lists.newArrayListWithCapacity(
            i
         );

         for (Slot val4 : (DefaultedList<Slot>)val2) {
            val3.add(
               val4.getStack()
                  .copy()
            );
         }

         hand.onSlotClick(
            n2,
            n3,
            val,
            player2
         );
         Int2ObjectMap val5 = new Int2ObjectOpenHashMap();

         for (int i2 = 0;
            i2
               < i;
            i2++
         ) {
            ItemStack stack = (ItemStack)val3.get(
               i2
            );
            ItemStack stack2 = ((Slot)val2.get(
                  i2
               ))
               .getStack();
            if (!ItemStack.areEqual(
               stack,
               stack2
            )) {
               val5.put(
                  i2,
                  stack2.copy()
               );
            }
         }

         SendsilentpacketHelper.sendSilentPacket(
            new ClickSlotC2SPacket(
               n,
               hand.getRevision(),
               n2,
               n3,
               val,
               hand.getCursorStack()
                  .copy(),
               val5
            )
         );
      }
   }

   public static int findBestElytraSlot() {
      int i = -1;
      double x = -1.0;
      RegistryEntry val = (RegistryEntry<Enchantment>)((Registry)MinecraftClient.getInstance()
            .world
            .getRegistryManager()
            .getOptional(RegistryKeys.ENCHANTMENT)
            .get())
         .getEntry(Enchantments.PROTECTION.getValue())
         .orElseThrow();
      RegistryEntry val2 = (RegistryEntry<Enchantment>)((Registry)MinecraftClient.getInstance()
            .world
            .getRegistryManager()
            .getOptional(RegistryKeys.ENCHANTMENT)
            .get())
         .getEntry(Enchantments.UNBREAKING.getValue())
         .orElseThrow();
      RegistryEntry val3 = (RegistryEntry<Enchantment>)((Registry)MinecraftClient.getInstance()
            .world
            .getRegistryManager()
            .getOptional(RegistryKeys.ENCHANTMENT)
            .get())
         .getEntry(Enchantments.MENDING.getValue())
         .orElseThrow();

      for (int i2 = 0;
         i2
            < 36;
         i2++
      ) {
         ItemStack stack = mc.player
            .getInventory()
            .getStack(
               i2
            );
         if (stack.isOf(
            Items.ELYTRA
         )) {
            int i3 = EnchantmentHelper.getLevel(
               val,
               stack
            );
            int i4 = EnchantmentHelper.getLevel(
               val2,
               stack
            );
            int i5 = EnchantmentHelper.getLevel(
               val3,
               stack
            );
            int i6 = stack.getMaxDamage();
            int i7 = stack.getDamage();
            double x2 = (double)(
                  i6
                     - i7
               )
               / (double)i6;
            double x3 = (double)(
                  i3
                        * 100
                     + i4
                        * 10
                     + (
                        i5
                              > 0
                           ? 1
                           : 0
                     )
               )
               + x2
                  * 10.0;
            if (x3
               > x
               )
             {
               x = x3;
               i = i2;
            }
         }
      }

      return i;
   }

   public static int findBestChestplateSlot() {
      int i = -1;
      double x = -1.0;
      Reference val = (Reference<Enchantment>)((Registry)mc.world
            .getRegistryManager()
            .getOptional(RegistryKeys.ENCHANTMENT)
            .get())
         .getEntry(Enchantments.PROTECTION.getValue())
         .orElseThrow();
      Reference val2 = (Reference<Enchantment>)((Registry)mc.world
            .getRegistryManager()
            .getOptional(RegistryKeys.ENCHANTMENT)
            .get())
         .getEntry(Enchantments.UNBREAKING.getValue())
         .orElseThrow();
      Reference val3 = (Reference<Enchantment>)((Registry)mc.world
            .getRegistryManager()
            .getOptional(RegistryKeys.ENCHANTMENT)
            .get())
         .getEntry(Enchantments.MENDING.getValue())
         .orElseThrow();

      for (int i2 = 0;
         i2
            < 36;
         i2++
      ) {
         ItemStack stack = mc.player
            .getInventory()
            .getStack(
               i2
            );
         Item var9 = stack.getItem();
         if (var9 instanceof ArmorItem) {
            ArmorItem item = (ArmorItem)var9;
            int i3 = EnchantmentHelper.getLevel(
               val,
               stack
            );
            int i4 = EnchantmentHelper.getLevel(
               val2,
               stack
            );
            int i5 = EnchantmentHelper.getLevel(
               val3,
               stack
            );
            int i6 = getChestplatePriority(
               item
            );
            int i7 = stack.getMaxDamage();
            int i8 = stack.getDamage();
            double x2 = (double)(
                  i7
                     - i8
               )
               / (double)i7;
            double x3 = (double)(
                  i6
                        * 10000
                     + i3
                        * 100
                     + i4
                        * 10
                     + (
                        i5
                              > 0
                           ? 1
                           : 0
                     )
               )
               + x2
                  * 10.0;
            if (x3
               > x
               )
             {
               x = x3;
               i = i2;
            }
         }
      }

      return i;
   }

   private static int getArmorPriority(
      Item item
   ) {
      if (item
            == Items.NETHERITE_HELMET
         || item
            == Items.NETHERITE_CHESTPLATE
         || item
            == Items.NETHERITE_LEGGINGS
         || item
            == Items.NETHERITE_BOOTS) {
         return 6;
      } else if (item
            == Items.DIAMOND_HELMET
         || item
            == Items.DIAMOND_CHESTPLATE
         || item
            == Items.DIAMOND_LEGGINGS
         || item
            == Items.DIAMOND_BOOTS) {
         return 5;
      } else if (item
            == Items.IRON_HELMET
         || item
            == Items.IRON_CHESTPLATE
         || item
            == Items.IRON_LEGGINGS
         || item
            == Items.IRON_BOOTS) {
         return 4;
      } else if (item
            == Items.CHAINMAIL_HELMET
         || item
            == Items.CHAINMAIL_CHESTPLATE
         || item
            == Items.CHAINMAIL_LEGGINGS
         || item
            == Items.CHAINMAIL_BOOTS) {
         return 3;
      } else if (item
            == Items.GOLDEN_HELMET
         || item
            == Items.GOLDEN_CHESTPLATE
         || item
            == Items.GOLDEN_LEGGINGS
         || item
            == Items.GOLDEN_BOOTS) {
         return 2;
      } else {
         return item
                  != Items.LEATHER_HELMET
               && item
                  != Items.LEATHER_CHESTPLATE
               && item
                  != Items.LEATHER_LEGGINGS
               && item
                  != Items.LEATHER_BOOTS
            ? 0
            : 1;
      }
   }

   private static int getChestplatePriority(
      Item item
   ) {
      if (item
         == Items.NETHERITE_CHESTPLATE) {
         return 6;
      } else if (item
         == Items.DIAMOND_CHESTPLATE) {
         return 5;
      } else if (item
         == Items.IRON_CHESTPLATE) {
         return 4;
      } else if (item
         == Items.CHAINMAIL_CHESTPLATE) {
         return 3;
      } else if (item
         == Items.GOLDEN_CHESTPLATE) {
         return 2;
      } else {
         return item
               == Items.LEATHER_CHESTPLATE
            ? 1
            : 0;
      }
   }

   public static int getBestArmorSlot(
      EquipmentSlot val
   ) {
      int i = -1;
      double y = -1.0;

      for (int i2 = 0;
         i2
            < 36;
         i2++
      ) {
         ItemStack stack = mc.player
            .getInventory()
            .getStack(
               i2
            );
         if (stack.getItem() instanceof ArmorItem
            && getNeededArmorSlot(
                  stack
               )
               == val
            )
          {
            double y2 = getArmorScore(
               stack
            );
            if (y2
               > y
               )
             {
               y = y2;
               i = i2;
            }
         }
      }

      return i;
   }

   private static EquipmentSlot getNeededArmorSlot(
      ItemStack stack
   ) {
      Item item = stack.getItem();
      if (item instanceof ArmorItem
         )
       {
         if (item
               == Items.NETHERITE_HELMET
            || item
               == Items.DIAMOND_HELMET
            || item
               == Items.IRON_HELMET
            || item
               == Items.GOLDEN_HELMET
            || item
               == Items.CHAINMAIL_HELMET
            || item
               == Items.LEATHER_HELMET) {
            return EquipmentSlot.HEAD;
         }

         if (item
               == Items.NETHERITE_CHESTPLATE
            || item
               == Items.DIAMOND_CHESTPLATE
            || item
               == Items.IRON_CHESTPLATE
            || item
               == Items.GOLDEN_CHESTPLATE
            || item
               == Items.CHAINMAIL_CHESTPLATE
            || item
               == Items.LEATHER_CHESTPLATE) {
            return EquipmentSlot.CHEST;
         }

         if (item
               == Items.NETHERITE_LEGGINGS
            || item
               == Items.DIAMOND_LEGGINGS
            || item
               == Items.IRON_LEGGINGS
            || item
               == Items.GOLDEN_LEGGINGS
            || item
               == Items.CHAINMAIL_LEGGINGS
            || item
               == Items.LEATHER_LEGGINGS) {
            return EquipmentSlot.LEGS;
         }

         if (item
               == Items.NETHERITE_BOOTS
            || item
               == Items.DIAMOND_BOOTS
            || item
               == Items.IRON_BOOTS
            || item
               == Items.GOLDEN_BOOTS
            || item
               == Items.CHAINMAIL_BOOTS
            || item
               == Items.LEATHER_BOOTS) {
            return EquipmentSlot.FEET;
         }
      }

      return null;
   }

   public static double getArmorScore(
      ItemStack stack
   ) {
      double x = 0.0;
      x += (double)getArmorPriority(
         stack.getItem()
      );
      Reference val = (Reference<Enchantment>)((Registry)mc.world
            .getRegistryManager()
            .getOptional(RegistryKeys.ENCHANTMENT)
            .get())
         .getEntry(Enchantments.PROTECTION.getValue())
         .orElseThrow();
      Reference val2 = (Reference<Enchantment>)((Registry)mc.world
            .getRegistryManager()
            .getOptional(RegistryKeys.ENCHANTMENT)
            .get())
         .getEntry(Enchantments.UNBREAKING.getValue())
         .orElseThrow();
      Reference val3 = (Reference<Enchantment>)((Registry)mc.world
            .getRegistryManager()
            .getOptional(RegistryKeys.ENCHANTMENT)
            .get())
         .getEntry(Enchantments.MENDING.getValue())
         .orElseThrow();
      x += (double)EnchantmentHelper.getLevel(
            val,
            stack
         )
         * 0.75;
      x += (double)EnchantmentHelper.getLevel(
            val2,
            stack
         )
         * 0.3;
      x += (double)EnchantmentHelper.getLevel(
            val3,
            stack
         )
         * 0.1;
      double x2 = 1.0;
      if (stack.isDamageable()
         )
       {
         int x3 = stack.getMaxDamage();
         int x4 = x3
            - stack.getDamage();
         x2 = Math.max(
            0.05,
            (double)x4
               / (double)x3
         );
      }

      return x
         * x2;
   }

   public static int getItemSlot(
      Item item
   ) {
      for (int i = 0;
         i
            < 36;
         i++
      ) {
         if (mc.player
               .getInventory()
               .getStack(
                  i
               )
               .getItem()
            == item
            )
          {
            return i;
         }
      }

      return -1;
   }

   public static int find(
      Item item,
      int n,
      int n2
   ) {
      for (int i = n;
         i
            <= n2;
         i++
      ) {
         if (mc.player
               .getInventory()
               .getStack(
                  i
               )
               .getItem()
            == item
            )
          {
            return i;
         }
      }

      return -1;
   }

   public static int getEnchantmentLevel(
      ItemStack stack,
      RegistryKey<Enchantment> val
   ) {
      Optional<Registry<Enchantment>> val2 = mc.world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT);
      if (val2.isEmpty()
         )
       {
         return 0;
      } else {
         Reference<Enchantment> val3 = val2.get()
            .getEntry(
               val.getValue()
            )
            .orElse(null);
         return val3
               != null
            ? EnchantmentHelper.getLevel(
               val3,
               stack
            )
            : 0;
      }
   }

   @Generated
   public static SearchitemHelper getInstance() {
      return instance;
   }
}
