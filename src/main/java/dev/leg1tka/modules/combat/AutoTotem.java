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
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class AutoTotem extends Module {
   public static AutoTotem INSTANCE = new AutoTotem();
   private final NumberSetting health = new NumberSetting("Здоровье", 6.0F, 1.0F, 20.0F, 0.5F);
   private final BooleanSetting ignoreAbsorption = new BooleanSetting(
      "Игнорировать золотые сердца", true
   );
   private final BooleanSetting dontSwapWhileEating = new BooleanSetting(
      "Не свапать когда кушаешь", true
   );
   private final NumberSetting elytraHealth = new NumberSetting(
      "Здоровье на элитрах", 10.0F, 1.0F, 20.0F, 0.5F
   );
   private final NumberSetting fallDistance = new NumberSetting(
      "Высота падения", 10.0F, 3.0F, 50.0F, 1.0F
   );
   private final NumberSetting healthWithBall = new NumberSetting("Здоровье с шаром", 10.0F, 0.0F, 20.0F, 0.5F);
   private final BooleanSetting returnTotem = new BooleanSetting("Возвращать тотем", true);
   private final NumberSetting swapBackDelay = new NumberSetting(
         "Задержка свапа обратно (мс)", 500.0F, 0.0F, 2000.0F, 50.0F
      )
      .visible(this.returnTotem::isState);
   private final BooleanSetting saveEnchanted = new BooleanSetting("Сохранять зачар", true);
   private final BooleanSetting bypassgrim = new BooleanSetting("Обходить Grim", true);
   private final MultiBooleanSetting triggers;
   private final NumberSetting obsidianRadius;
   private final NumberSetting maceRadius;
   private int cooldownTicks;
   private ItemStack previousStack;
   private long swapBackTimestamp;
   private int savedTotemSlot;
   private boolean totemTakenByUs;

   public AutoTotem() {
      super(
         "AutoTotem",
         "Автоматически берёт тотем в опасности",
         Category.COMBAT
      );
      this.triggers = new MultiBooleanSetting("Доп триггеры", new BooleanSetting[]{
         new BooleanSetting("Кристаллы (Умный детект)", true),
         new BooleanSetting("Обсидиан в руке", true),
         new BooleanSetting(
         "Булава в руке", true
      ),
         new BooleanSetting(
         "Элитры", true
      ),
         new BooleanSetting(
         "Падения", true
      )
      });
      this.obsidianRadius = new NumberSetting(
            "Радиус обсидиана", 6.0F, 1.0F, 12.0F, 0.5F
         )
         .visible(() -> this.triggers.is("Обсидиан в руке"));
      this.maceRadius = new NumberSetting("Радиус булавы", 100.0F, 10.0F, 150.0F, 5.0F)
         .visible(() -> this.triggers.is("Булава в руке"));
      this.previousStack = ItemStack.EMPTY;
      this.swapBackTimestamp = -1L;
      this.savedTotemSlot = -1;
      this.totemTakenByUs = false;
      this.addSettings(new Setting[]{
         this.health,
         this.ignoreAbsorption,
         this.dontSwapWhileEating,
         this.elytraHealth,
         this.healthWithBall,
         this.fallDistance,
         this.saveEnchanted,
         this.bypassgrim,
         this.returnTotem,
         this.swapBackDelay,
         this.triggers,
         this.obsidianRadius,
         this.maceRadius
      });
   }

   @EventLink
   public void onUpdate(
      EventUpdate val4
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
         }

         boolean flag = this.shouldUseTotem();
         if (flag
            )
          {
            this.swapBackTimestamp = -1L;
            if (!this.shouldSkipSwapBecauseEating()) {
               if (!this.hasTotemInOffhand()) {
                  int slot2 = this.findTotemSlot();
                  if (slot2
                     != -1) {
                     this.rememberPreviousStack();
                     this.swapToOffhand(
                        slot2
                     );
                     this.cooldownTicks = 3;
                  }
               } else if (this.saveEnchanted.isState()) {
                  ItemStack stack = mc.player
                     .getOffHandStack();
                  if (!stack.isEmpty()
                     && stack.isOf(
                        Items.TOTEM_OF_UNDYING
                     )
                     && stack.hasEnchantments()
                     )
                   {
                     int slot3 = this.findNormalTotemSlot();
                     if (slot3
                        != -1) {
                        this.rememberPreviousStack();
                        this.swapToOffhand(
                           slot3
                        );
                        this.cooldownTicks = 3;
                     }
                  }
               }
            }
         } else if (!this.totemTakenByUs) {
            this.swapBackTimestamp = -1L;
         } else if (this.returnTotem.isState()) {
            if (this.swapBackTimestamp == -1L) {
               this.swapBackTimestamp = System.currentTimeMillis();
            }

            long val = System.currentTimeMillis()
               - this.swapBackTimestamp;
            long val2 = (long)this.swapBackDelay
               .getValue()
               .floatValue();
            if (val
                  >= val2
               && this.cooldownTicks <= 0) {
               this.performReturn();
            }
         }
      }
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (val.getType()
            == EventPacket.Type.RECEIVE
         && val.getPacket() instanceof EntityStatusS2CPacket val2
         && mc.world != null
         && mc.player != null
         && val2.getStatus()
            == 35
         && val2.getEntity(
               mc.world
            )
            == mc.player) {
         this.swapBackTimestamp = -1L;
         this.cooldownTicks = 5;
         this.totemTakenByUs = false;
         this.previousStack = ItemStack.EMPTY;
      }
   }

   private void rememberPreviousStack() {
      if (!this.totemTakenByUs) {
         ItemStack stack = mc.player
            .getOffHandStack();
         if (stack.isEmpty()
            )
          {
            this.previousStack = ItemStack.EMPTY;
         } else {
            this.previousStack = stack.copy();
         }

         this.totemTakenByUs = true;
      }
   }

   private boolean hasTotemInOffhand() {
      ItemStack stack = mc.player
         .getOffHandStack();
      return !stack.isEmpty()
         && stack.isOf(
            Items.TOTEM_OF_UNDYING
         );
   }

   private int findTotemSlot() {
      int n = -1;
      int n2 = -1;

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
         if (stack.isOf(
            Items.TOTEM_OF_UNDYING
         )) {
            if (stack.hasEnchantments()
               )
             {
               if (n2
                  == -1) {
                  n2 = i;
               }
            } else if (n
               == -1) {
               n = i;
            }
         }
      }

      if (this.saveEnchanted.isState()) {
         return n
               != -1
            ? n
            : n2;
      } else {
         return n2
               != -1
            ? n2
            : n;
      }
   }

   private int findNormalTotemSlot() {
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
         if (stack.isOf(
               Items.TOTEM_OF_UNDYING
            )
            && !stack.hasEnchantments()
            )
          {
            return i;
         }
      }

      return -1;
   }

   private void swapToOffhand(
      int val
   ) {
      if (val
         != -1) {
         this.savedTotemSlot = val;
         if (this.bypassgrim.isState()) {
            QueueHelper.executeFast(
               () -> this.executeSwap(
                     val
                  )
            );
         } else {
            this.executeSwap(
               val
            );
         }
      }
   }

   private void performReturn() {
      if (this.hasTotemInOffhand() && this.totemTakenByUs) {
         int n = -1;
         if (!this.previousStack.isEmpty()) {
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
               if (ItemStack.areItemsEqual(
                     stack,
                     this.previousStack
                  )
                  && ItemStack.areEqual(
                     stack,
                     this.previousStack
                  )) {
                  n = i;
                  break;
               }
            }
         }

         if (n
            == -1) {
            n = this.savedTotemSlot
                  != -1
               ? this.savedTotemSlot
               : 9;
         }

         int n2 = n;
         if (this.bypassgrim.isState()) {
            QueueHelper.executeFast(
               () -> this.executeReturn(
                     n2
                  )
            );
         } else {
            this.executeReturn(
               n2
            );
         }

         this.cooldownTicks = 3;
         this.totemTakenByUs = false;
         this.previousStack = ItemStack.EMPTY;
         this.swapBackTimestamp = -1L;
      } else {
         this.totemTakenByUs = false;
      }
   }

   private void executeSwap(
      int val
   ) {
      this.doSwap(
         val
      );
      mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
   }

   private void executeReturn(
      int val
   ) {
      this.doSwap(
         val
      );
      mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
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

   private boolean shouldUseTotem() {
      if (mc.player != null && !mc.player.isInCreativeMode() && !mc.player.isSpectator()) {
         float f = this.getCurrentHealthValue();
         if (f
            <= this.health.getValue().floatValue()) {
            return true;
         } else if (this.triggers.is("Элитры")
            && mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)
            && mc.player.isGliding()
            && f
               <= this.elytraHealth.getValue().floatValue()) {
            return true;
         } else if (this.triggers.is("Падения")
            && mc.player.fallDistance >= this.fallDistance.getValue().floatValue()
            && !mc.player.isGliding()) {
            return true;
         } else if (this.triggers.is("Кристаллы (Умный детект)")
            && this.isCrystalDangerous(
               f
            )) {
            return true;
         } else {
            return this.triggers.is("Обсидиан в руке")
                  && this.isPlayerHolding(Items.OBSIDIAN, this.obsidianRadius.getValue().floatValue())
               ? true
               : this.triggers.is("Булава в руке")
                  && this.isPlayerHolding(Items.MACE, this.maceRadius.getValue().floatValue());
         }
      } else {
         return false;
      }
   }

   private boolean isPlayerHolding(
      Item val,
      float val2
   ) {
      double d = (double)(
         val2
            * val2
      );

      for (PlayerEntity player2 : mc.world
         .getPlayers()) {
         if (player2
               != mc.player
            && mc.player
                  .squaredDistanceTo(
                     player2
                  )
               <= d
            && (
               player2.getMainHandStack()
                     .isOf(
                        val
                     )
                  || player2.getOffHandStack()
                     .isOf(
                        val
                     )
            )) {
            return true;
         }
      }

      return false;
   }

   private float getCurrentHealthValue() {
      if (mc.player == null) {
         return 0.0F;
      } else {
         return this.ignoreAbsorption.isState() ? mc.player.getHealth() : mc.player.getHealth() + mc.player.getAbsorptionAmount();
      }
   }

   private boolean isCrystalDangerous(
      float val
   ) {
      if (mc.player != null && mc.world != null) {
         List val2 = new ArrayList<>();

         for (Entity entity2 : mc.world
            .getEntities()) {
            if (entity2 instanceof EndCrystalEntity
               )
             {
               EndCrystalEntity crystal = (EndCrystalEntity)entity2;
               if (!crystal.isRemoved()
                  && mc.player
                        .squaredDistanceTo(
                           crystal
                        )
                     <= 144.0) {
                  val2.add(
                     crystal
                  );
               }
            }
         }

         if (val2.isEmpty()
            )
          {
            return false;
         } else {
            float x = this.getMaxPotentialDamage(
               val2,
               true
            );
            if (x
               <= 0.0F) {
               return false;
            } else {
               float f = this.isHoldingEnchantedHead()
                  ? this.healthWithBall.getValue().floatValue()
                  : this.health.getValue().floatValue();
               return val
                     - x
                  <= f;
            }
         }
      } else {
         return false;
      }
   }

   private boolean isHoldingEnchantedHead() {
      if (mc.player == null) {
         return false;
      } else {
         ItemStack stack = mc.player
            .getOffHandStack();
         return stack.getItem()
               == Items.PLAYER_HEAD
            && stack.hasEnchantments();
      }
   }

   private float getMaxPotentialDamage(
      List<EndCrystalEntity> val,
      boolean val2
   ) {
      float f = 0.0F;

      for (EndCrystalEntity crystal : val) {
         float y = this.calculateCrystalDamage(
            crystal,
            val2
         );
         if (y
            > f
            )
          {
            f = y;
         }
      }

      return f;
   }

   private float calculateCrystalDamage(
      EndCrystalEntity val,
      boolean val2
   ) {
      if (mc.player != null
         && mc.world != null
         && val
            != null
         && !val.isRemoved()
         )
       {
         Vec3d pos = val.getPos();
         Vec3d vec = mc.player
            .getEyePos();
         if (val2
            && this.isLineBlocked(
               vec,
               pos
            )) {
            return 0.0F;
         } else {
            float x = this.calculateRawExplosionDamage(
               pos,
               mc.player.getPos(),
               6.0F
            );
            float f = this.getProtectionFactor();
            float f2 = x
               * (
                  1.0F
                     - f
               );
            return Math.max(
               0.0F,
               f2
            );
         }
      } else {
         return 0.0F;
      }
   }

   private float getProtectionFactor() {
      if (mc.player == null) {
         return 0.0F;
      } else {
         float y = (float)mc.player
            .getAttributeValue(EntityAttributes.ARMOR);
         float f = Math.min(
            0.5F,
            y
               * 0.02F
         );
         float f2 = 0.0F;
         StatusEffectInstance val = mc.player
            .getStatusEffect(StatusEffects.RESISTANCE);
         if (val
            != null) {
            f2 = (float)(
                  val.getAmplifier()
                     + 1
               )
               * 0.2F;
         }

         return Math.min(
            0.8F,
            f
               + f2
         );
      }
   }

   private float calculateRawExplosionDamage(
      Vec3d val,
      Vec3d val2,
      float val3
   ) {
      double dist = val.distanceTo(
         val2
      );
      double d = 1.0
         - dist
            / (
               (double)val3
                  * 2.0
            );
      if (d
         <= 0.0) {
         return 0.0F;
      } else {
         double d2 = (
                  d
                        * d
                     + d
               )
               / 2.0
               * 7.0
               * (double)val3
               * 2.0
            + 1.0;
         return (float)d2;
      }
   }

   private boolean isLineBlocked(
      Vec3d val,
      Vec3d val2
   ) {
      BlockHitResult blockHit = mc.world
         .raycast(
            new RaycastContext(
               val,
               val2,
               ShapeType.OUTLINE,
               FluidHandling.NONE,
               mc.player
            )
         );
      return blockHit.getType()
         == Type.BLOCK;
   }

   private boolean shouldSkipSwapBecauseEating() {
      if (this.dontSwapWhileEating.isState() && mc.player != null && mc.player.isUsingItem()) {
         UseAction val = mc.player
            .getActiveItem()
            .getUseAction();
         return val
               == UseAction.EAT
            || val
               == UseAction.DRINK;
      } else {
         return false;
      }
   }

   @Override
   public void onDisable() {
      this.cooldownTicks = 0;
      this.savedTotemSlot = -1;
      this.previousStack = ItemStack.EMPTY;
      this.totemTakenByUs = false;
      this.swapBackTimestamp = -1L;
      super.onDisable();
   }
}
