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
import java.util.List;
import java.util.Locale;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;

public class ServerHelper extends Module {
   public static ServerHelper INSTANCE = new ServerHelper();
   private final ModeSetting mode;
   private final BooleanSetting bypassgrim;
   private final KeybindSetting featherKey;
   private final KeybindSetting magmaKey;
   private final KeybindSetting cryingObsidianKey;
   private final KeybindSetting clayKey;
   private final KeybindSetting disorientationKey;
   private final KeybindSetting trapKey;
   private final KeybindSetting plastKey;
   private final KeybindSetting pilKey;
   private final KeybindSetting snegKey;
   private final KeybindSetting auraKey;
   private Item pendingItem;
   private QueryType pendingAction;

   public ServerHelper() {
      super("ServerHelper", Category.MISC);
      String var1 = "ServerHelper";
      String var3 = "Режим";
      String var15 = "Lony";
      this.mode = new ModeSetting(var3, var15, new String[]{"Lony", "Spooky"});
      this.bypassgrim = new BooleanSetting("Обходить Grim", true);
      this.featherKey = new KeybindSetting("Перышко", -1)
         .visible(
            () -> this.mode.is("Lony")
         );
      this.magmaKey = new KeybindSetting("Ливалка", -1)
         .visible(
            () -> this.mode.is("Lony")
         );
      this.cryingObsidianKey = new KeybindSetting("Трапка", -1)
         .visible(
            () -> this.mode.is("Lony")
         );
      this.clayKey = new KeybindSetting("Ливалка с платформой", -1)
         .visible(
            () -> this.mode.is("Lony")
         );
      this.disorientationKey = new KeybindSetting("Дезориентация", -1)
         .visible(
            () -> this.mode.is("Spooky")
         );
      this.trapKey = new KeybindSetting("Трапка", -1)
         .visible(
            () -> this.mode.is("Spooky")
         );
      this.plastKey = new KeybindSetting("Пласт", -1)
         .visible(
            () -> this.mode.is("Spooky")
         );
      this.pilKey = new KeybindSetting("Явная пыль", -1)
         .visible(
            () -> this.mode.is("Spooky")
         );
      this.snegKey = new KeybindSetting("Снег заморозки", -1)
         .visible(
            () -> this.mode.is("Spooky")
         );
      this.auraKey = new KeybindSetting("Божья аура", -1)
         .visible(
            () -> this.mode.is("Spooky")
         );
      this.addSettings(new Setting[]{
         this.mode,
         this.bypassgrim,
         this.featherKey,
         this.magmaKey,
         this.cryingObsidianKey,
         this.clayKey,
         this.disorientationKey,
         this.trapKey,
         this.plastKey,
         this.pilKey,
         this.snegKey,
         this.auraKey
      });
      this.setHidden(true);
   }

   public boolean isSpookyMode() {
      ModeSetting var10000 = this.mode;
      return var10000.is("Spooky");
   }

   public boolean isLonyMode() {
      ModeSetting var10000 = this.mode;
      return var10000.is("Lony");
   }

   public List<NameHelper_2> getLonyHelperBinds() {
      NameHelper_2 var10000 = new NameHelper_2("Перышко", Items.FEATHER, this.featherKey);
      NameHelper_2 var10001 = new NameHelper_2("Ливалка", Items.MAGMA_CREAM, this.magmaKey);
      NameHelper_2 var1 = new NameHelper_2("Трапка", Items.CRYING_OBSIDIAN, this.cryingObsidianKey);
      return List.of(var10000, var10001, var1, new NameHelper_2("Ливалка с платформой", Items.CLAY, this.clayKey));
   }

   public List<NameHelper_2> getSpookyHelperBinds() {
      NameHelper_2 var10000 = new NameHelper_2("Дезориентация", Items.ENDER_EYE, this.disorientationKey);
      NameHelper_2 var10001 = new NameHelper_2("Трапка", Items.NETHERITE_SCRAP, this.trapKey);
      NameHelper_2 var1 = new NameHelper_2("Пласт", Items.DRIED_KELP, this.plastKey);
      NameHelper_2 var2 = new NameHelper_2("Явная пыль", Items.SUGAR, this.pilKey);
      NameHelper_2 var3 = new NameHelper_2("Снег заморозки", Items.SNOWBALL, this.snegKey);
      return List.of(var10000, var10001, var1, var2, var3, new NameHelper_2("Божья аура", Items.PHANTOM_MEMBRANE, this.auraKey));
   }

   @Override
   public void onEnable() {
      this.pendingItem = null;
      this.pendingAction = null;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.pendingItem = null;
      this.pendingAction = null;
      super.onDisable();
   }

   @EventLink
   public void onBinding(
      EventBinding val
   ) {
      if (mc.currentScreen == null) {
         ModeSetting var10000 = this.mode;
         if (var10000.is("Lony")) {
            if (val.getKey()
               == this.featherKey.getKey()) {
               this.pendingItem = Items.FEATHER;
            } else if (val.getKey()
               == this.magmaKey.getKey()) {
               this.pendingItem = Items.MAGMA_CREAM;
            } else if (val.getKey()
               == this.cryingObsidianKey.getKey()) {
               this.pendingItem = Items.CRYING_OBSIDIAN;
            } else if (val.getKey()
               == this.clayKey.getKey()) {
               this.pendingItem = Items.CLAY_BALL;
            }
         } else {
            if (val.getKey()
               == this.disorientationKey.getKey()) {
               this.pendingAction = QueryType.DISORIENTATION;
            } else if (val.getKey()
               == this.trapKey.getKey()) {
               this.pendingAction = QueryType.TRAP;
            } else if (val.getKey()
               == this.plastKey.getKey()) {
               this.pendingAction = QueryType.PLAST;
            } else if (val.getKey()
               == this.pilKey.getKey()) {
               this.pendingAction = QueryType.DUST;
            } else if (val.getKey()
               == this.snegKey.getKey()) {
               this.pendingAction = QueryType.FREEZE_SNOW;
            } else if (val.getKey()
               == this.auraKey.getKey()) {
               this.pendingAction = QueryType.AURA;
            }
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val4
   ) {
      if (mc.player != null && mc.world != null) {
         ModeSetting var10000 = this.mode;
         if (var10000.is("Lony")) {
            if (this.pendingItem != null) {
               Item item2 = this.pendingItem;
               if (this.bypassgrim.isState()) {
                  QueueHelper.queue(
                     () -> SearchitemHelper.swapAndUseHvH(
                           item2
                        )
                  );
               } else {
                  SearchitemHelper.swapAndUseHvH(
                     item2
                  );
               }

               this.pendingItem = null;
            }
         } else if (this.pendingAction != null && mc.interactionManager != null) {
            QueryType val = this.pendingAction;
            if (this.bypassgrim.isState()) {
               QueueHelper.queue(
                  () -> this.useAction(
                        val
                     )
               );
            } else {
               this.useAction(
                  val
               );
            }

            this.pendingAction = null;
         }
      } else {
         this.pendingItem = null;
         this.pendingAction = null;
      }
   }

   private void useAction(
      QueryType val
   ) {
      if (mc.player
         .getItemCooldownManager()
         .isCoolingDown(
            new ItemStack(
               val.item
            )
         )) {
         ChatUtil.sendMessage(
            "У предмета "
               + val.cooldownName
               + " есть кд"
         );
      } else if (this.matchesAction(
         mc.player.getMainHandStack(),
         val
      )) {
         mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
         mc.player.swingHand(Hand.MAIN_HAND);
         ChatUtil.sendMessage(
            val.successText
         );
      } else if (this.matchesAction(
         mc.player.getOffHandStack(),
         val
      )) {
         mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
         mc.player.swingHand(Hand.OFF_HAND);
         ChatUtil.sendMessage(
            val.successText
         );
      } else {
         int slot2 = this.findMatchingSlot(
            val,
            0,
            8
         );
         if (slot2
            != -1) {
            int n = mc.player
               .getInventory()
               .selectedSlot;
            mc.player
               .networkHandler
               .sendPacket(
                  new UpdateSelectedSlotC2SPacket(
                     slot2
                  )
               );
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.player
               .networkHandler
               .sendPacket(
                  new UpdateSelectedSlotC2SPacket(
                     n
                  )
               );
            ChatUtil.sendMessage(
               val.successText
            );
         } else {
            int slot3 = this.findMatchingSlot(
               val,
               9,
               35
            );
            if (slot3
               != -1) {
               this.useFromInventory(
                  slot3
               );
               ChatUtil.sendMessage(
                  val.successText
               );
            } else {
               ChatUtil.sendMessage(
                  val.failText
               );
            }
         }
      }
   }

   private void useFromInventory(
      int val
   ) {
      int n = mc.player
         .getInventory()
         .selectedSlot;
      int slot2 = this.findTemporaryHotbarSlot();
      boolean flag = false;
      if (mc.player.isSprinting()) {
         mc.player.networkHandler.sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
         mc.player.setSprinting(false);
         mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
         if (!ModuleManager.sprint.isEnable()) {
            mc.options.sprintKey.setPressed(false);
         }

         flag = true;
      }

      mc.interactionManager
         .clickSlot(
            0,
            val,
            slot2,
            SlotActionType.SWAP,
            mc.player
         );
      mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
      mc.player
         .networkHandler
         .sendPacket(
            new UpdateSelectedSlotC2SPacket(
               slot2
            )
         );
      mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
      mc.player.swingHand(Hand.MAIN_HAND);
      mc.player
         .networkHandler
         .sendPacket(
            new UpdateSelectedSlotC2SPacket(
               n
            )
         );
      mc.interactionManager
         .clickSlot(
            0,
            val,
            slot2,
            SlotActionType.SWAP,
            mc.player
         );
      mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
      if (flag
         )
       {
         mc.player.networkHandler.sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
      }
   }

   private int findTemporaryHotbarSlot() {
      int n = 8;

      for (int i = 0;
         i
            < 9;
         i++
      ) {
         if (i
            != mc.player.getInventory().selectedSlot) {
            ItemStack stack = mc.player
               .getInventory()
               .getStack(
                  i
               );
            if (stack.isEmpty()
               )
             {
               return i;
            }

            if (stack.getUseAction()
               == UseAction.NONE) {
               n = i;
            }
         }
      }

      return n;
   }

   private int findMatchingSlot(
      QueryType val,
      int val2,
      int val3
   ) {
      for (int i = val2;
         i
            <= val3;
         i++
      ) {
         if (this.matchesAction(
            mc.player
               .getInventory()
               .getStack(
                  i
               ),
            val
         )) {
            return i;
         }
      }

      return -1;
   }

   private boolean matchesAction(
      ItemStack val,
      QueryType val2
   ) {
      return val
               != null
            && !val.isEmpty()
            && val.getItem()
               == val2.item
         ? val.getName()
            .getString()
            .toLowerCase(Locale.ROOT)
            .contains(
               val2.query
            )
         : false;
   }
}
