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

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventBinding;
import dev.leg1tka.api.events.implement.EventMoveInput;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class FtHelper extends Module {
   public static final FtHelper INSTANCE = new FtHelper();
   private static final int SPOOKY_HOTBAR_SLOT = 8;
   private static final int SPOOKY_STAGE_IDLE = 0;
   private static final int SPOOKY_STAGE_STOP = 1;
   private static final int SPOOKY_STAGE_ACTION = 2;
   private static final int SPOOKY_STAGE_RELEASE = 3;
   private static final long REBUFF_DEBUFF_TO_AURA_DELAY_MS = 500L;
   private static final long REBUFF_AURA_TO_BUFF_DELAY_MS = 100L;
   private final BooleanSetting enemyCooldowns = new BooleanSetting(
      "Кулдауны противников",
      true
   );
   private final BooleanSetting enemyCooldownsChat = new BooleanSetting(
         "Оповещение в чат",
         true
      )
      .visible(this.enemyCooldowns::isState);
   private final BooleanSetting legit = new BooleanSetting(
      "Легитный",
      false
   );
   private final BooleanSetting bypassgrim = new BooleanSetting(
      "Обходить Grim",
      true
   );
   private final ModeSetting mode;
   private final ModeSetting arbaletMode;
   private final KeybindSetting windChargeBind;
   private final KeybindSetting fireChargeBind;
   private final KeybindSetting dezorientationBind;
   private final KeybindSetting trapBind;
   private final KeybindSetting plastBind;
   private final KeybindSetting yavkaBind;
   private final KeybindSetting bojkaBind;
   private final KeybindSetting snejokBind;
   private final KeybindSetting arbaletBind;
   private final KeybindSetting arbaletReloadBind;
   private final KeybindSetting assassinPotionBind;
   private final KeybindSetting paladinPotionBind;
   private final KeybindSetting sleepingPotionBind;
   private final KeybindSetting wrathPotionBind;
   private final KeybindSetting crackerPotionBind;
   private final KeybindSetting radiationPotionBind;
   private final KeybindSetting holyWaterBind;
   private final KeybindSetting rebuffBind;
   private final ModeSetting rebuffDebuff;
   private final ModeSetting rebuffBuff;
   private int spookyStage;
   private Item spookyItem;
   private CustomPotion spookyPotionPreset;
   private ClientOption_DDpev0RQ spookySourceType;
   private int spookySourceSlot;
   private int spookyHotbarSlot;
   private int spookyOriginalSlot;
   private boolean spookySprintBlocked;
   private boolean spookyActionCompleted;
   private ClientOption_YE6zi33V rebuffStage;
   private CustomPotion rebuffBuffPreset;
   private final ResetHelper_6 rebuffTimer;
   private ClientOption_OqgoPy3e reloadStage;
   private int reloadArrowOriginalSlot;
   private int reloadCrossbowOriginalSlot;
   private int reloadCrossbowHotbarSlot;
   private final ResetHelper_6 reloadTimer;

   public FtHelper() {
      super(
         "FTHelper",
         "FTHelper",
         Category.COMBAT
      );
      String var10003 = "Режим";
      String var10004 = "Default";
      String[] var10005 = new String[2];
      var10005[0] = "Default";
      var10005[1] = "SpookyTime";
      this.mode = new ModeSetting(var10003, var10004, var10005);
      var10003 = "Режим арбалета";
      var10004 = "По бинду";
      var10005 = new String[2];
      var10005[0] = "По бинду";
      var10005[1] = "Умный";
      this.arbaletMode = new ModeSetting(var10003, var10004, var10005);
      this.windChargeBind = new KeybindSetting(
         "Заряд ветра",
         -1
      );
      this.fireChargeBind = new KeybindSetting(
         "Огненный смерч",
         -1
      );
      this.dezorientationBind = new KeybindSetting(
         "Дезориентация",
         -1
      );
      this.trapBind = new KeybindSetting(
         "Трапка",
         -1
      );
      this.plastBind = new KeybindSetting(
         "Пласт",
         -1
      );
      this.yavkaBind = new KeybindSetting(
         "Явная пыль",
         -1
      );
      this.bojkaBind = new KeybindSetting(
         "Божья аура",
         -1
      );
      this.snejokBind = new KeybindSetting(
         "Снежок",
         -1
      );
      this.arbaletBind = new KeybindSetting(
         "Арбалет",
         -1
      );
      this.arbaletReloadBind = new KeybindSetting(
            "Перезарядка",
            -1
         )
         .visible(
            () -> this.arbaletMode
                  .is(
                     "Умный"
                  )
         );
      this.assassinPotionBind = new KeybindSetting(
         "Зелье ассасина",
         -1
      );
      this.paladinPotionBind = new KeybindSetting(
         "Зелье палладина",
         -1
      );
      this.sleepingPotionBind = new KeybindSetting(
         "Снотворное",
         -1
      );
      this.wrathPotionBind = new KeybindSetting(
         "Зелье гнева",
         -1
      );
      this.crackerPotionBind = new KeybindSetting(
         "Хлопушка",
         -1
      );
      this.radiationPotionBind = new KeybindSetting(
         "Зелье радиации",
         -1
      );
      this.holyWaterBind = new KeybindSetting(
         "Святая вода",
         -1
      );
      this.rebuffBind = new KeybindSetting(
         "Разбафф",
         -1
      );
      var10003 = "Дебафф";
      var10004 = "Снотворное";
      var10005 = new String[2];
      var10005[0] = "Снотворное";
      var10005[1] = "Радиация";
      this.rebuffDebuff = new ModeSetting(var10003, var10004, var10005);
      var10003 = "Бафф";
      var10004 = "Зелье гнева";
      var10005 = new String[2];
      var10005[0] = "Зелье гнева";
      var10005[1] = "Зелье ассасина";
      this.rebuffBuff = new ModeSetting(var10003, var10004, var10005);
      this.spookyStage = 0;
      this.spookySourceType = ClientOption_DDpev0RQ.NONE;
      this.spookySourceSlot = -1;
      this.spookyHotbarSlot = -1;
      this.spookyOriginalSlot = -1;
      this.rebuffStage = ClientOption_YE6zi33V.IDLE;
      this.rebuffTimer = new ResetHelper_6();
      this.reloadStage = ClientOption_OqgoPy3e.IDLE;
      this.reloadArrowOriginalSlot = -1;
      this.reloadCrossbowOriginalSlot = -1;
      this.reloadCrossbowHotbarSlot = -1;
      this.reloadTimer = new ResetHelper_6();
      this.addSettings(new Setting[]{
         this.enemyCooldowns,
         this.enemyCooldownsChat,
         this.legit,
         this.bypassgrim,
         this.mode,
         this.arbaletMode,
         this.windChargeBind,
         this.fireChargeBind,
         this.dezorientationBind,
         this.trapBind,
         this.plastBind,
         this.yavkaBind,
         this.bojkaBind,
         this.snejokBind,
         this.arbaletBind,
         this.arbaletReloadBind,
         this.assassinPotionBind,
         this.paladinPotionBind,
         this.sleepingPotionBind,
         this.wrathPotionBind,
         this.crackerPotionBind,
         this.radiationPotionBind,
         this.holyWaterBind,
         this.rebuffBind,
         this.rebuffDebuff,
         this.rebuffBuff
      });
      EventInvoker.register(this);
   }

   @EventLink
   public void onKey(
      EventBinding val
   ) {
      if (mc.currentScreen == null) {
         Item item2 = null;
         CustomPotion val2 = null;
         if (!this.isRebuffActive()) {
            if (val.getKey()
               == this.rebuffBind.getKey()) {
               this.startRebuff();
            } else {
               if (val.getKey()
                  == this.windChargeBind.getKey()) {
                  item2 = Items.WIND_CHARGE;
               } else if (val.getKey()
                  == this.fireChargeBind.getKey()) {
                  item2 = Items.FIRE_CHARGE;
               } else if (val.getKey()
                  == this.dezorientationBind.getKey()) {
                  item2 = Items.ENDER_EYE;
               } else if (val.getKey()
                  == this.trapBind.getKey()) {
                  item2 = Items.NETHERITE_SCRAP;
               } else if (val.getKey()
                  == this.plastBind.getKey()) {
                  item2 = Items.DRIED_KELP;
               } else if (val.getKey()
                  == this.yavkaBind.getKey()) {
                  item2 = Items.SUGAR;
               } else if (val.getKey()
                  == this.bojkaBind.getKey()) {
                  item2 = Items.PHANTOM_MEMBRANE;
               } else if (val.getKey()
                  == this.snejokBind.getKey()) {
                  item2 = Items.SNOWBALL;
               } else {
                  if (this.arbaletMode
                        .is(
                           "Умный"
                        )
                     && val.getKey()
                        == this.arbaletReloadBind.getKey()) {
                     if (this.reloadStage == ClientOption_OqgoPy3e.IDLE) {
                        mc.player
                           .sendMessage(
                              Text.of(
                                 "§a[FTHelper] Начинается перезарядка..."
                              ),
                              false
                           );
                        this.reloadStage = ClientOption_OqgoPy3e.FINDING_ARROW;
                     } else {
                        mc.player
                           .sendMessage(
                              Text.of(
                                 "§c[FTHelper] Остановка перезарядки!"
                              ),
                              false
                           );
                        this.cancelReload();
                     }

                     return;
                  }

                  if (val.getKey()
                     == this.arbaletBind.getKey()) {
                     if (this.arbaletMode
                        .is(
                           "Умный"
                        )) {
                        if (this.bypassgrim.isState()) {
                           QueueHelper.executeFast(this::startSmartCrossbowUse);
                        } else {
                           this.startSmartCrossbowUse();
                        }

                        return;
                     }

                     item2 = Items.CROSSBOW;
                  } else if (val.getKey()
                     == this.assassinPotionBind.getKey()) {
                     val2 = CustomPotion.ASSASSIN;
                  } else if (val.getKey()
                     == this.paladinPotionBind.getKey()) {
                     val2 = CustomPotion.PALADIN;
                  } else if (val.getKey()
                     == this.sleepingPotionBind.getKey()) {
                     val2 = CustomPotion.SLEEPING;
                  } else if (val.getKey()
                     == this.wrathPotionBind.getKey()) {
                     val2 = CustomPotion.WRATH;
                  } else if (val.getKey()
                     == this.crackerPotionBind.getKey()) {
                     val2 = CustomPotion.CRACKER;
                  } else if (val.getKey()
                     == this.radiationPotionBind.getKey()) {
                     val2 = CustomPotion.RADIATION;
                  } else if (val.getKey()
                     == this.holyWaterBind.getKey()) {
                     val2 = CustomPotion.HOLY_WATER;
                  }
               }

               if (item2
                  != null) {
                  Item item3 = item2;
                  if (this.mode
                     .is(
                        "SpookyTime"
                     )) {
                     this.startSpookyTime(
                        item3
                     );
                  } else if (this.bypassgrim.isState()) {
                     QueueHelper.executeFast(
                        () -> UseHelper.use(
                              item3
                           )
                     );
                  } else {
                     UseHelper.use(
                        item3
                     );
                  }
               } else if (val2
                  != null) {
                  CustomPotion val3 = val2;
                  if (this.mode
                     .is(
                        "SpookyTime"
                     )) {
                     this.startSpookyTime(
                        val3
                     );
                  } else if (this.bypassgrim.isState()) {
                     QueueHelper.executeFast(
                        () -> this.usePotionPreset(
                              val3
                           )
                     );
                  } else {
                     this.usePotionPreset(
                        val3
                     );
                  }
               }
            }
         }
      }
   }

   @EventLink(
      priority = 100
   )
   public void onMoveInput(
      EventMoveInput val
   ) {
      if (this.spookyStage != 0) {
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
   public void onPacket(
      EventPacket val
   ) {
      ClearallHelper.onPacket(
         val
      );
   }

   @EventLink
   public void onUpdate(
      EventUpdate val8
   ) {
      ClearallHelper.onUpdate();
      this.updateSpookyTime();
      this.updateRebuff();
      this.updateReload();
   }

   public BooleanSetting getEnemyCooldowns() {
      return this.enemyCooldowns;
   }

   public BooleanSetting getEnemyCooldownsChat() {
      return this.enemyCooldownsChat;
   }

   private void updateSpookyTime() {
      if (this.spookyStage != 0 && mc.player != null) {
         if (this.spookyStage == 1) {
            this.applySpookyStop();
            this.spookyStage = 2;
         } else if (this.spookyStage == 2) {
            this.applySpookyStop();
            this.performSpookyAction();
            this.spookyStage = 3;
         } else {
            if (this.spookyStage == 3) {
               this.releaseSpookyControl();
               this.resetSpookyState();
            }
         }
      }
   }

   private void updateRebuff() {
      if (this.rebuffStage != ClientOption_YE6zi33V.IDLE) {
         if (this.rebuffStage == ClientOption_YE6zi33V.WAITING_DEBUFF) {
            if (!this.isActionBusy()) {
               this.rebuffStage = ClientOption_YE6zi33V.WAITING_AURA;
               this.rebuffTimer.reset();
            }
         } else if (this.rebuffStage == ClientOption_YE6zi33V.WAITING_AURA) {
            if (this.rebuffTimer.isReached(500L)) {
               if (!this.hasRebuffWeakness()) {
                  if (!this.useBoundPotion(this.rebuffBuffPreset)) {
                     this.resetRebuffState();
                  } else {
                     this.rebuffStage = ClientOption_YE6zi33V.WAITING_BUFF_ACTION;
                  }
               } else if (!this.useBoundItem(Items.PHANTOM_MEMBRANE)) {
                  this.resetRebuffState();
               } else {
                  this.rebuffStage = ClientOption_YE6zi33V.WAITING_AURA_ACTION;
               }
            }
         } else if (this.rebuffStage == ClientOption_YE6zi33V.WAITING_AURA_ACTION) {
            if (!this.isActionBusy()) {
               this.rebuffStage = ClientOption_YE6zi33V.WAITING_BUFF;
               this.rebuffTimer.reset();
            }
         } else if (this.rebuffStage == ClientOption_YE6zi33V.WAITING_BUFF) {
            if (this.rebuffTimer.isReached(100L)) {
               if (!this.useBoundPotion(this.rebuffBuffPreset)) {
                  this.resetRebuffState();
               } else {
                  this.rebuffStage = ClientOption_YE6zi33V.WAITING_BUFF_ACTION;
               }
            }
         } else {
            if (!this.isActionBusy()) {
               this.resetRebuffState();
            }
         }
      }
   }

   private void updateReload() {
      if (this.reloadStage != ClientOption_OqgoPy3e.IDLE && mc.player != null) {
         if (this.reloadStage == ClientOption_OqgoPy3e.FINDING_ARROW) {
            int n = this.findLightArrow();
            if (n
               == -1) {
               mc.player
                  .sendMessage(
                     Text.of(
                        "§c[FTHelper] Световая стрела не найдена!"
                     ),
                     false
                  );
               this.reloadStage = ClientOption_OqgoPy3e.IDLE;
               return;
            }

            this.reloadArrowOriginalSlot = n;
            this.reloadStage = ClientOption_OqgoPy3e.SWAPPING_ARROW;
         } else if (this.reloadStage == ClientOption_OqgoPy3e.SWAPPING_ARROW) {
            if (this.reloadArrowOriginalSlot != 44) {
               this.swapScreenSlotWithHotbar(this.reloadArrowOriginalSlot, 8);
            }

            this.reloadStage = ClientOption_OqgoPy3e.FINDING_CROSSBOW;
         } else if (this.reloadStage == ClientOption_OqgoPy3e.FINDING_CROSSBOW) {
            int n2 = this.findUnchargedCrossbowInventory();
            if (n2
               == -1) {
               this.reloadStage = ClientOption_OqgoPy3e.RESTORING_ARROW;
               return;
            }

            this.reloadCrossbowOriginalSlot = n2;
            this.reloadCrossbowHotbarSlot = this.legit.isState()
               ? mc.player.getInventory().selectedSlot
               : this.findFreeOrReplaceableHotbarSlotExcept(8);
            this.reloadStage = ClientOption_OqgoPy3e.SWAPPING_CROSSBOW;
         } else if (this.reloadStage == ClientOption_OqgoPy3e.SWAPPING_CROSSBOW) {
            this.applySpookyStop();
            this.spookyOriginalSlot = mc.player.getInventory().selectedSlot;
            this.swapScreenSlotWithHotbar(this.reloadCrossbowOriginalSlot, this.reloadCrossbowHotbarSlot);
            this.selectSlot(this.reloadCrossbowHotbarSlot);
            this.reloadStage = ClientOption_OqgoPy3e.START_CHARGING;
         } else if (this.reloadStage == ClientOption_OqgoPy3e.START_CHARGING) {
            mc.options.useKey.setPressed(true);
            this.sendUsePacket(Hand.MAIN_HAND);
            this.reloadTimer.reset();
            this.reloadStage = ClientOption_OqgoPy3e.CHARGING;
         } else if (this.reloadStage == ClientOption_OqgoPy3e.CHARGING) {
            ItemStack stack = mc.player
               .getInventory()
               .getStack(this.reloadCrossbowHotbarSlot);
            if (stack.getItem() instanceof CrossbowItem
               )
             {
               float f = (float)CrossbowItem.getPullTime(
                     stack,
                     mc.player
                  )
                  * 50.0F;
               if (this.reloadTimer
                  .isReached(
                     (long)f
                        + 50L
                  )) {
                  mc.options.useKey.setPressed(false);
                  mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
                  this.reloadTimer.reset();
                  this.reloadStage = ClientOption_OqgoPy3e.WAITING_AFTER_CHARGE;
               }
            } else {
               this.cancelReload();
            }
         } else if (this.reloadStage == ClientOption_OqgoPy3e.WAITING_AFTER_CHARGE) {
            if (this.reloadTimer.isReached(500L)) {
               this.reloadStage = ClientOption_OqgoPy3e.RESTORING_CROSSBOW;
            }
         } else if (this.reloadStage == ClientOption_OqgoPy3e.RESTORING_CROSSBOW) {
            this.restoreOriginalSlot();
            this.swapScreenSlotWithHotbar(this.reloadCrossbowOriginalSlot, this.reloadCrossbowHotbarSlot);
            this.releaseSpookyControl();
            this.reloadStage = ClientOption_OqgoPy3e.FINDING_CROSSBOW;
         } else if (this.reloadStage == ClientOption_OqgoPy3e.RESTORING_ARROW) {
            if (this.reloadArrowOriginalSlot != -1
               && this.reloadArrowOriginalSlot != 44) {
               this.swapScreenSlotWithHotbar(this.reloadArrowOriginalSlot, 8);
            }

            mc.player
               .sendMessage(
                  Text.of(
                     "§a[FTHelper] Все арбалеты заряжены!"
                  ),
                  false
               );
            this.resetReloadState();
         }
      }
   }

   private void cancelReload() {
      if (mc.player == null) {
         this.resetReloadState();
      } else {
         if (this.reloadStage == ClientOption_OqgoPy3e.START_CHARGING || this.reloadStage == ClientOption_OqgoPy3e.CHARGING || this.reloadStage == ClientOption_OqgoPy3e.WAITING_AFTER_CHARGE) {
            mc.options.useKey.setPressed(false);
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            this.restoreOriginalSlot();
            this.swapScreenSlotWithHotbar(this.reloadCrossbowOriginalSlot, this.reloadCrossbowHotbarSlot);
            this.releaseSpookyControl();
         }

         if (this.reloadStage != ClientOption_OqgoPy3e.IDLE
            && this.reloadStage != ClientOption_OqgoPy3e.FINDING_ARROW
            && this.reloadArrowOriginalSlot != -1
            && this.reloadArrowOriginalSlot != 44) {
            this.swapScreenSlotWithHotbar(this.reloadArrowOriginalSlot, 8);
         }

         this.resetReloadState();
      }
   }

   private void resetReloadState() {
      this.reloadStage = ClientOption_OqgoPy3e.IDLE;
      this.reloadArrowOriginalSlot = -1;
      this.reloadCrossbowOriginalSlot = -1;
      this.reloadCrossbowHotbarSlot = -1;
      this.reloadTimer.reset();
      if (mc.player != null) {
         mc.options.useKey.setPressed(false);
      }
   }

   private int findLightArrow() {
      if (mc.player != null && mc.player.currentScreenHandler != null) {
         for (int i = 9;
            i
               < 45;
            i++
         ) {
            ItemStack stack = mc.player
               .currentScreenHandler
               .getSlot(
                  i
               )
               .getStack();
            if (stack
                  != null
               && stack.isOf(
                  Items.TIPPED_ARROW
               )) {
               String name = stack.getName()
                  .getString()
                  .toLowerCase();
               if (name.contains(
                  "световая"
               )) {
                  return i;
               }
            }
         }

         ItemStack stack2 = mc.player
            .currentScreenHandler
            .getSlot(45)
            .getStack();
         return stack2
                  != null
               && stack2.isOf(
                  Items.TIPPED_ARROW
               )
               && stack2.getName()
                  .getString()
                  .toLowerCase()
                  .contains(
                     "световая"
                  )
            ? 45
            : -1;
      } else {
         return -1;
      }
   }

   private int findUnchargedCrossbowInventory() {
      if (mc.player != null && mc.player.currentScreenHandler != null) {
         for (int i = 9;
            i
               < 36;
            i++
         ) {
            ItemStack stack = mc.player
               .currentScreenHandler
               .getSlot(
                  i
               )
               .getStack();
            if (stack
                  != null
               && stack.getItem() instanceof CrossbowItem
               && !CrossbowItem.isCharged(
                  stack
               )) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private int findChargedCrossbowInventory() {
      if (mc.player != null && mc.player.currentScreenHandler != null) {
         for (int i = 9;
            i
               < 36;
            i++
         ) {
            ItemStack stack = mc.player
               .currentScreenHandler
               .getSlot(
                  i
               )
               .getStack();
            if (stack
                  != null
               && stack.getItem() instanceof CrossbowItem
               && CrossbowItem.isCharged(
                  stack
               )) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void swapScreenSlotWithHotbar(
      int val,
      int val2
   ) {
      if (mc.player != null && mc.player.currentScreenHandler != null) {
         int n = mc.player
            .currentScreenHandler
            .syncId;
         mc.interactionManager
            .clickSlot(
               n,
               val,
               val2,
               SlotActionType.SWAP,
               mc.player
            );
         mc.getNetworkHandler()
            .sendPacket(
               new CloseHandledScreenC2SPacket(
                  n
               )
            );
      }
   }

   private void startSmartCrossbowUse() {
      int n = this.findChargedCrossbowInventory();
      if (n
         != -1) {
         this.spookyItem = Items.CROSSBOW;
         this.spookyPotionPreset = null;
         this.spookySourceType = ClientOption_DDpev0RQ.INVENTORY;
         this.spookySourceSlot = n;
         this.spookyHotbarSlot = this.legit.isState() ? mc.player.getInventory().selectedSlot : this.findFreeOrReplaceableHotbarSlot();
         this.spookyOriginalSlot = mc.player.getInventory().selectedSlot;
         this.spookyActionCompleted = false;
         this.spookyStage = 1;
      }
   }

   private int findFreeOrReplaceableHotbarSlotExcept(
      int val
   ) {
      for (int i = 0;
         i
            < 9;
         i++
      ) {
         if (i
               != val
            && mc.player
               .getInventory()
               .getStack(
                  i
               )
               .isEmpty()) {
            return i;
         }
      }

      return val
            == 8
         ? 7
         : 8;
   }

   private void startRebuff() {
      if (mc.player != null && !this.isRebuffActive() && !this.isActionBusy()) {
         CustomPotion val = this.resolveRebuffDebuffPreset();
         CustomPotion val2 = this.resolveRebuffBuffPreset();
         if (this.useBoundPotion(
            val
         )) {
            this.rebuffBuffPreset = val2;
            this.rebuffTimer.reset();
            this.rebuffStage = ClientOption_YE6zi33V.WAITING_DEBUFF;
         }
      }
   }

   private boolean startSpookyTime(
      Item val
   ) {
      return this.prepareSpookyAction(
         val,
         null
      );
   }

   private boolean startSpookyTime(
      CustomPotion val
   ) {
      return this.prepareSpookyAction(
         Items.SPLASH_POTION,
         val
      );
   }

   private boolean prepareSpookyAction(
      Item val,
      CustomPotion val2
   ) {
      if (this.spookyStage == 0 && mc.player != null) {
         TypeHelper val3 = this.resolveSpookyUseSource(
            val,
            val2
         );
         if (val3
            == null) {
            return false;
         } else {
            this.spookyItem = val;
            this.spookyPotionPreset = val2;
            this.spookySourceType = val3.type();
            this.spookySourceSlot = val3.sourceSlot();
            this.spookyHotbarSlot = val3.hotbarSlot();
            this.spookyOriginalSlot = mc.player.getInventory().selectedSlot;
            this.spookyActionCompleted = false;
            this.spookyStage = 1;
            return true;
         }
      } else {
         return false;
      }
   }

   private TypeHelper resolveSpookyUseSource(
      Item val,
      CustomPotion val2
   ) {
      if (this.matchesRequestedStack(
         mc.player.getMainHandStack(),
         val,
         val2
      )) {
         int n = mc.player
            .getInventory()
            .selectedSlot;
         return new TypeHelper(
            ClientOption_DDpev0RQ.HOTBAR,
            n,
            n
         );
      } else if (this.matchesRequestedStack(
         mc.player.getOffHandStack(),
         val,
         val2
      )) {
         return new TypeHelper(
            ClientOption_DDpev0RQ.OFFHAND,
            -1,
            -1
         );
      } else {
         int slot2 = this.findMatchingSlot(
            val,
            val2,
            0,
            8
         );
         if (slot2
            != -1) {
            return new TypeHelper(
               ClientOption_DDpev0RQ.HOTBAR,
               slot2,
               slot2
            );
         } else {
            int slot3 = this.findMatchingSlot(
               val,
               val2,
               9,
               45
            );
            return slot3
                  == -1
               ? null
               : new TypeHelper(
                  ClientOption_DDpev0RQ.INVENTORY,
                  slot3,
                  this.findFreeOrReplaceableHotbarSlot()
               );
         }
      }
   }

   private void applySpookyStop() {
      mc.options.sprintKey.setPressed(false);
      mc.player.setSprinting(false);
      if (!this.spookySprintBlocked) {
         this.spookySprintBlocked = true;
         mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
         if (Sprint.INSTANCE.isEnable()) {
            mc.options.sprintKey.setPressed(false);
         }
      }
   }

   private void performSpookyAction() {
      if (this.spookyItem != null && mc.player.currentScreenHandler != null) {
         if (this.spookySourceType == ClientOption_DDpev0RQ.OFFHAND) {
            this.useHand(Hand.OFF_HAND);
            this.spookyActionCompleted = true;
         } else if (this.spookySourceType == ClientOption_DDpev0RQ.HOTBAR) {
            if (this.isSpookyHotbarStackAvailable()) {
               this.selectSlot(this.spookyHotbarSlot);
               this.useHand(Hand.MAIN_HAND);
               this.restoreOriginalSlot();
               this.spookyActionCompleted = true;
            }
         } else if (this.spookySourceType == ClientOption_DDpev0RQ.INVENTORY
            && this.spookySourceSlot >= 0
            && this.spookySourceSlot < mc.player.currentScreenHandler.slots.size()
            && this.matchesSpookyStack(mc.player.currentScreenHandler.getSlot(this.spookySourceSlot).getStack())) {
            int n = mc.player
               .currentScreenHandler
               .syncId;
            int n2 = this.legit
                  .isState()
               ? this.spookyOriginalSlot
               : this.spookyHotbarSlot;
            if (n2
                  >= 0
               && n2
                  <= 8) {
               mc.interactionManager
                  .clickSlot(
                     n,
                     this.spookySourceSlot,
                     n2,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler()
                  .sendPacket(
                     new CloseHandledScreenC2SPacket(
                        n
                     )
                  );
               this.selectSlot(
                  n2
               );
               this.useHand(Hand.MAIN_HAND);
               this.restoreOriginalSlot();
               mc.interactionManager
                  .clickSlot(
                     n,
                     this.spookySourceSlot,
                     n2,
                     SlotActionType.SWAP,
                     mc.player
                  );
               mc.getNetworkHandler()
                  .sendPacket(
                     new CloseHandledScreenC2SPacket(
                        n
                     )
                  );
               this.spookyActionCompleted = true;
            }
         }
      }
   }

   private boolean usePotionPreset(
      CustomPotion val
   ) {
      if (mc.player != null && mc.interactionManager != null && mc.world != null) {
         if (this.matchesPotionPreset(
            mc.player.getOffHandStack(),
            val
         )) {
            this.useHand(Hand.OFF_HAND);
            return true;
         } else {
            int slot2 = this.findPotionPresetSlot(
               val
            );
            if (slot2
               == -1) {
               return false;
            } else {
               int n = mc.player
                  .getInventory()
                  .selectedSlot;
               if (slot2
                  < 9) {
                  mc.player.getInventory().selectedSlot = slot2;
                  mc.getNetworkHandler()
                     .sendPacket(
                        new UpdateSelectedSlotC2SPacket(
                           slot2
                        )
                     );
                  this.useHand(Hand.MAIN_HAND);
                  mc.player.getInventory().selectedSlot = n;
                  mc.getNetworkHandler()
                     .sendPacket(
                        new UpdateSelectedSlotC2SPacket(
                           n
                        )
                     );
                  return true;
               } else {
                  int n2 = mc.player
                     .currentScreenHandler
                     .syncId;
                  mc.interactionManager
                     .clickSlot(
                        n2,
                        slot2,
                        n,
                        SlotActionType.SWAP,
                        mc.player
                     );
                  mc.getNetworkHandler()
                     .sendPacket(
                        new CloseHandledScreenC2SPacket(
                           n2
                        )
                     );
                  mc.getNetworkHandler()
                     .sendPacket(
                        new UpdateSelectedSlotC2SPacket(
                           n
                        )
                     );
                  this.useHand(Hand.MAIN_HAND);
                  mc.interactionManager
                     .clickSlot(
                        n2,
                        slot2,
                        n,
                        SlotActionType.SWAP,
                        mc.player
                     );
                  mc.getNetworkHandler()
                     .sendPacket(
                        new CloseHandledScreenC2SPacket(
                           n2
                        )
                     );
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   private boolean useItemDirect(
      Item val
   ) {
      return UseHelper.use(
         val
      );
   }

   private int findPotionPresetSlot(
      CustomPotion val
   ) {
      if (mc.player != null && mc.player.currentScreenHandler != null) {
         for (int i = mc.player
                  .currentScreenHandler
                  .slots
                  .size()
               - 1;
            i
               >= 0;
            i--
         ) {
            if (this.matchesPotionPreset(
               mc.player
                  .currentScreenHandler
                  .getSlot(
                     i
                  )
                  .getStack(),
               val
            )) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private boolean matchesSpookyStack(
      ItemStack val
   ) {
      return val
               != null
            && !val.isEmpty()
            && this.spookyItem != null
            && val.isOf(
               this.spookyItem
            )
         ? this.spookyPotionPreset == null
            || this.matchesPotionPreset(
               val,
               this.spookyPotionPreset
            )
         : false;
   }

   private boolean matchesRequestedStack(
      ItemStack val,
      Item val2,
      CustomPotion val3
   ) {
      return val
               != null
            && !val.isEmpty()
            && val.isOf(
               val2
            )
         ? val3
               == null
            || this.matchesPotionPreset(
               val,
               val3
            )
         : false;
   }

   private int findMatchingSlot(
      Item val,
      CustomPotion val2,
      int val3,
      int val4
   ) {
      if (mc.player != null && mc.player.currentScreenHandler != null) {
         int n = Math.min(
            val4,
            mc.player.currentScreenHandler.slots.size() - 1
         );

         for (int i = n;
            i
               >= val3;
            i--
         ) {
            if (this.matchesRequestedStack(
               mc.player
                  .currentScreenHandler
                  .getSlot(
                     i
                  )
                  .getStack(),
               val,
               val2
            )) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private int findFreeOrReplaceableHotbarSlot() {
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
            .isEmpty()) {
            return i;
         }
      }

      return 8;
   }

   private boolean matchesPotionPreset(
      ItemStack val,
      CustomPotion val2
   ) {
      if (val
            != null
         && !val.isEmpty()
         && val.isOf(
            Items.SPLASH_POTION
         )) {
         String name = this.normalizePotionName(
            val.getName()
               .getString()
         );
         if (!name.isEmpty()
            && name.contains(
               this.normalizePotionName(
                  val2.displayName
               )
            )) {
            return true;
         } else {
            PotionContentsComponent val3 = (PotionContentsComponent)val.get(
               DataComponentTypes.POTION_CONTENTS
            );
            if (val3
               == null) {
               return false;
            } else {
               Map<Object, Integer> val4 = new HashMap<>();

               for (StatusEffectInstance val5 : val3.getEffects()) {
                  val4.put(
                     val5.getEffectType(),
                     val5.getAmplifier()
                  );
               }

               if (val4.size()
                  != val2.effects
                     .size()) {
                  return false;
               } else {
                  for (PotionEffectEntry val6 : val2.effects) {
                     Integer val7 = val4.get(
                        val6.effect()
                     );
                     if (val7
                           == null
                        || val7
                           != val6.amplifier()
                        )
                      {
                        return false;
                     }
                  }

                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   private String normalizePotionName(
      String val
   ) {
      return val
            == null
         ? ""
         : val.toLowerCase()
            .replace(
               "[+]",
               ""
            )
            .replace(
               "[-]",
               ""
            )
            .replace(
               "[*]",
               ""
            )
            .replace(
               "[#]",
               ""
            )
            .replaceAll(
               "\\s+",
               " "
            )
            .trim();
   }

   private boolean performUseAction(
      Item val,
      boolean val2
   ) {
      return val2
         ? this.startSpookyTime(
            val
         )
         : this.useItemDirect(
            val
         );
   }

   private boolean performUseAction(
      CustomPotion val,
      boolean val2
   ) {
      return val2
         ? this.startSpookyTime(
            val
         )
         : this.usePotionPreset(
            val
         );
   }

   private boolean useBoundItem(
      Item val
   ) {
      return this.mode
            .is(
               "SpookyTime"
            )
         ? this.startSpookyTime(
            val
         )
         : UseHelper.use(
            val
         );
   }

   private boolean useBoundPotion(
      CustomPotion val
   ) {
      return this.mode
            .is(
               "SpookyTime"
            )
         ? this.startSpookyTime(
            val
         )
         : this.usePotionPreset(
            val
         );
   }

   private boolean isActionBusy() {
      return this.spookyStage != 0 || this.reloadStage != ClientOption_OqgoPy3e.IDLE || UseHelper.isBusy();
   }

   private boolean canUseItem(
      Item val
   ) {
      return UseHelper.hasItem(
         val
      );
   }

   private boolean canUsePotionPreset(
      CustomPotion val
   ) {
      return this.matchesPotionPreset(
            mc.player.getOffHandStack(),
            val
         )
         || this.findPotionPresetSlot(
               val
            )
            != -1;
   }

   private boolean hasRebuffWeakness() {
      if (mc.player == null) {
         return false;
      } else {
         StatusEffectInstance val = mc.player
            .getStatusEffect(StatusEffects.WEAKNESS);
         return val
               != null
            && val.getAmplifier()
               >= 1;
      }
   }

   private CustomPotion resolveRebuffDebuffPreset() {
      return this.rebuffDebuff
            .is(
               "Радиация"
            )
         ? CustomPotion.RADIATION
         : CustomPotion.SLEEPING;
   }

   private CustomPotion resolveRebuffBuffPreset() {
      return this.rebuffBuff
            .is(
               "Зелье ассасина"
            )
         ? CustomPotion.ASSASSIN
         : CustomPotion.WRATH;
   }

   private boolean isRebuffActive() {
      return this.rebuffStage != ClientOption_YE6zi33V.IDLE;
   }

   private void resetRebuffState() {
      this.rebuffStage = ClientOption_YE6zi33V.IDLE;
      this.rebuffBuffPreset = null;
      this.rebuffTimer.reset();
   }

   private void sendUsePacket(
      Hand val
   ) {
      mc.interactionManager
         .sendSequencedPacket(
            mc.world,
            val2 -> new PlayerInteractItemC2SPacket(
                  val,
                  val2,
                  mc.player.getYaw(),
                  mc.player.getPitch()
               )
         );
   }

   private void useHand(
      Hand val
   ) {
      if (this.legit.isState()) {
         mc.interactionManager
            .interactItem(
               mc.player,
               val
            );
      } else {
         this.sendUsePacket(
            val
         );
      }
   }

   private void releaseSpookyControl() {
      this.spookySprintBlocked = false;
      this.restoreOriginalSlot();
      mc.options.sprintKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.sprintKey.getDefaultKey().getCode()));
   }

   private boolean isSpookyHotbarStackAvailable() {
      return this.spookyHotbarSlot >= 0
         && this.spookyHotbarSlot < 9
         && this.matchesRequestedStack(mc.player.getInventory().getStack(this.spookyHotbarSlot), this.spookyItem, this.spookyPotionPreset);
   }

   private void selectSlot(
      int val
   ) {
      if (mc.player != null
         && val
            >= 0) {
         mc.player.getInventory().selectedSlot = val;
         this.syncSelectedSlot(
            val
         );
      }
   }

   private void syncSelectedSlot(
      int val
   ) {
      if (this.legit.isState()) {
         mc.interactionManager.syncSelectedSlot();
      } else {
         mc.getNetworkHandler()
            .sendPacket(
               new UpdateSelectedSlotC2SPacket(
                  val
               )
            );
      }
   }

   private void restoreOriginalSlot() {
      if (mc.player != null && this.spookyOriginalSlot >= 0) {
         this.selectSlot(this.spookyOriginalSlot);
      }
   }

   private void resetSpookyState() {
      this.spookyStage = 0;
      this.spookyItem = null;
      this.spookyPotionPreset = null;
      this.spookySourceType = ClientOption_DDpev0RQ.NONE;
      this.spookySourceSlot = -1;
      this.spookyHotbarSlot = -1;
      this.spookyOriginalSlot = -1;
      this.spookySprintBlocked = false;
      this.spookyActionCompleted = false;
   }

   @Override
   public void onDisable() {
      if (mc.player != null && this.spookyActionCompleted) {
         this.restoreOriginalSlot();
      }

      this.cancelReload();
      this.resetRebuffState();
      this.resetSpookyState();
      if (mc.player != null) {
         mc.options.sprintKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.sprintKey.getDefaultKey().getCode()));
      }

      super.onDisable();
   }
}
