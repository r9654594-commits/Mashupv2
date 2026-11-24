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
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.List;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.math.Vec3d;

public final class TpLoot extends Module {
   public static TpLoot INSTANCE = new TpLoot();
   private final NumberSetting range;
   private final NumberSetting lootDelay;
   private final ModeSetting afterLoot;
   private final NumberSetting actionDelay;
   private final FinishedHelper lootTimer;
   private final FinishedHelper actionTimer;
   private Vec3d originalPos;
   private boolean waitingAction;
   private static final List<Item> TARGET_ITEMS = List.of(
      Items.NETHERITE_SWORD,
      Items.NETHERITE_HELMET,
      Items.NETHERITE_CHESTPLATE,
      Items.NETHERITE_LEGGINGS,
      Items.NETHERITE_BOOTS,
      Items.PLAYER_HEAD,
      Items.GOLDEN_APPLE,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.END_CRYSTAL,
      Items.TOTEM_OF_UNDYING,
      Items.ELYTRA
   );

   public TpLoot() {
      super("TpLoot", Category.MISC);
      String var1 = "TPLoot";
      this.range = new NumberSetting("Дистанция", 10.0F, 3.0F, 50.0F, 1.0F);
      this.lootDelay = new NumberSetting("Задержка лута", 500.0F, 100.0F, 5000.0F, 50.0F);
      String var5 = "После лута";
      String var7 = "Возвращаться";
      this.afterLoot = new ModeSetting(var5, var7, new String[]{"Возвращаться", "Тепаться на спавн"});
      this.actionDelay = new NumberSetting("Задержка действия", 1000.0F, 200.0F, 10000.0F, 100.0F);
      this.lootTimer = new FinishedHelper();
      this.actionTimer = new FinishedHelper();
      this.originalPos = null;
      this.waitingAction = false;
      this.addSettings(new Setting[]{
         this.range,
         this.lootDelay,
         this.afterLoot,
         this.actionDelay
      });
   }

   @EventLink
   public void onTick(
      EventUpdate val2
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.waitingAction) {
            if (this.actionTimer.finished((long)this.actionDelay.getValue().floatValue())) {
               ModeSetting var10000 = this.afterLoot;
               if (var10000.is("Возвращаться") && this.originalPos != null) {
                  this.teleportTo(this.originalPos);
                  ChatUtil.sendMessage("TpLoot: возврат на исходную позицию");
               }

               var10000 = this.afterLoot;
               if (var10000.is("Тепаться на спавн")) {
                  ClientPlayNetworkHandler var7 = mc.player.networkHandler;
                  var7.sendChatCommand("spawn");
                  ChatUtil.sendMessage("TpLoot: выполнен /spawn");
               }

               this.waitingAction = false;
               this.originalPos = null;
               this.lootTimer.reset();
            }
         } else if (this.lootTimer.finished((long)this.lootDelay.getValue().floatValue())) {
            ItemEntity entity2 = this.findTargetItem();
            if (entity2
               != null) {
               this.originalPos = mc.player.getPos();
               Vec3d pos = entity2.getPos();
               this.teleportTo(
                  pos
               );
               ItemStack stack = entity2.getStack();
               ChatUtil.sendMessage(
                  "TpLoot: подобран "
                     + stack.getName()
                        .getString()
               );
               this.lootTimer.reset();
               this.waitingAction = true;
               this.actionTimer.reset();
            }
         }
      }
   }

   private ItemEntity findTargetItem() {
      double d = this.range
         .getValue()
         .doubleValue();
      ItemEntity entity2 = null;
      double d2 = Double.MAX_VALUE;

      for (Entity entity3 : mc.world
         .getEntities()) {
         if (entity3 instanceof ItemEntity
            )
          {
            ItemEntity entity4 = (ItemEntity)entity3;
            ItemStack stack = entity4.getStack();
            if (this.isTargetItem(
               stack.getItem()
            )) {
               double d3 = mc.player
                  .squaredDistanceTo(
                     entity3
                  );
               if (!(
                     d3
                        > d
                           * d
                  )
                  && d3
                     < d2
                  )
                {
                  d2 = d3;
                  entity2 = entity4;
               }
            }
         }
      }

      return entity2;
   }

   private boolean isTargetItem(
      Item val
   ) {
      return TARGET_ITEMS.contains(
         val
      );
   }

   private void teleportTo(
      Vec3d val
   ) {
      int n = (int)Math.ceil(
         mc.player
               .getPos()
               .distanceTo(
                  val
               )
            / 10.0
      );
      n = Math.max(
         n,
         3
      );

      for (int i = 0;
         i
            < n;
         i++
      ) {
         mc.player.networkHandler.sendPacket(new OnGroundOnly(mc.player.isOnGround(), mc.player.horizontalCollision));
      }

      mc.player
         .networkHandler
         .sendPacket(
            new PositionAndOnGround(
               val.x,
               val.y,
               val.z,
               false,
               mc.player.horizontalCollision
            )
         );
      mc.player
         .setPosition(
            val.x,
            val.y,
            val.z
         );
   }

   @Override
   public void onEnable() {
      this.originalPos = null;
      this.waitingAction = false;
      this.lootTimer.reset();
      this.actionTimer.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.originalPos = null;
      this.waitingAction = false;
      super.onDisable();
   }
}
