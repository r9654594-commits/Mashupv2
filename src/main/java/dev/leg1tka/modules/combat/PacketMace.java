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

import dev.leg1tka.api.events.Event;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventPacket;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class PacketMace extends Module {
   public static PacketMace INSTANCE = new PacketMace();
   private final BooleanSetting gnd = new BooleanSetting("Только на земле", true);
   private final NumberSetting dly = new NumberSetting(
      "Задержка переключения", 50.0F, 0.0F, 200.0F, 1.0F
   );
   private boolean atk = false;
   private long ltm = 0L;

   public PacketMace() {
      super(
         "PacketMace",
         "Автоматически бьет булавой и возвращает предмет",
         Category.COMBAT
      );
      this.addSettings(new Setting[]{
         this.gnd,
         this.dly
      });
   }

   @EventLink(
      priority = 100
   )
   public void onEvent(
      Event val
   ) {
      if (mc.player != null && mc.world != null) {
         if (val instanceof EventPacket val2
            && val2.getType()
               == EventPacket.Type.SEND) {
            Packet val3 = val2.getPacket();
            if (val3 instanceof PlayerInteractEntityC2SPacket
               )
             {
               if (this.atk) {
                  return;
               }

               if (this.gnd.isState() && !mc.player.isOnGround()) {
                  return;
               }

               long val4 = System.currentTimeMillis();
               if (val4
                     - this.ltm
                  < (long)this.dly.getValue().floatValue()) {
                  return;
               }

               int n = this.find(
                  Items.MACE
               );
               if (n
                  == -1) {
                  return;
               }

               int n2 = mc.player
                  .getInventory()
                  .selectedSlot;
               if (n
                  == n2
                  )
                {
                  return;
               }

               val2.setCancelled(
                  true
               );
               this.atk = true;
               this.ltm = val4;
               mc.player.getInventory().selectedSlot = n;
               mc.player
                  .networkHandler
                  .sendPacket(
                     new UpdateSelectedSlotC2SPacket(
                        n
                     )
                  );
               mc.player
                  .networkHandler
                  .sendPacket(
                     val3
                  );
               mc.player.getInventory().selectedSlot = n2;
               mc.player
                  .networkHandler
                  .sendPacket(
                     new UpdateSelectedSlotC2SPacket(
                        n2
                     )
                  );
               this.atk = false;
            }
         }
      }
   }

   private int find(
      Item val
   ) {
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
         if (stack.getItem()
            == val
            )
          {
            return i;
         }
      }

      return -1;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.atk = false;
      this.ltm = 0L;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.atk = false;
   }
}
