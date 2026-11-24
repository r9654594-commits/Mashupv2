package dev.leg1tka.modules.movement;


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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import lombok.Generated;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class PlayerFakeLags extends Module {
   public static PlayerFakeLags INSTANCE = new PlayerFakeLags();
   public final ModeSetting mode;
   public final NumberSetting delay;
   public final NumberSetting maxPackets;
   public final BooleanSetting onlyMovement;
   public final ObjectArrayList<GetsendtimeHelper> delayedPackets;
   public final FinishedHelper timer;
   public boolean releasing;

   public PlayerFakeLags() {
      super("PlayerFakeLags", "Фейковые лаги / Поднятие пинга", Category.MOVEMENT);
      this.mode = new ModeSetting("Режим", "Ping Spoof", new String[]{"Ping Spoof", "Pulse", "Blink"});
      this.delay = new NumberSetting("Задержка (MS)", 250.0F, 50.0F, 1500.0F, 25.0F);
      this.maxPackets = new NumberSetting("Лимит пачки", 12.0F, 5.0F, 25.0F, 1.0F);
      this.onlyMovement = new BooleanSetting("Только движение", false);
      this.delayedPackets = new ObjectArrayList();
      this.timer = new FinishedHelper();
      this.releasing = false;
      this.addSettings(new Setting[]{
         this.mode,
         this.delay,
         this.maxPackets,
         this.onlyMovement
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.delayedPackets.clear();
      this.timer.reset();
      this.releasing = false;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.releasePackets();
   }

   @EventLink
   void onEvent(
      EventUpdate val3
   ) {
      if (mc.player != null) {
         long val = System.currentTimeMillis();
         if (this.mode.is("Ping Spoof")) {
            while (
               !this.delayedPackets.isEmpty()
                  && (
                     val
                           >= ((GetsendtimeHelper)this.delayedPackets.get(0)).sendTime
                        || this.delayedPackets.size() >= this.maxPackets.getValue().intValue()
                  )
            ) {
               this.sendSinglePacket(((GetsendtimeHelper)this.delayedPackets.remove(0)).packet);
            }
         } else if (this.mode.is("Pulse")
            && (this.timer.finished(this.delay.getValue().longValue()) || this.delayedPackets.size() >= this.maxPackets.getValue().intValue())) {
            this.releasePackets();
            this.timer.reset();
         }
      }
   }

   @EventLink
   void onEvent(
      EventPacket val
   ) {
      if (mc.player != null && !this.releasing) {
         if (val.getType()
            == EventPacket.Type.SEND) {
            Packet val2 = val.getPacket();
            if (this.isSystemPacket(
               val2
            )) {
               return;
            }

            if (this.onlyMovement.isState()
               && !(
                  val2 instanceof PlayerMoveC2SPacket
               )
               && !(
                  val2 instanceof PlayerInteractEntityC2SPacket
               )
               && !(
                  val2 instanceof PlayerActionC2SPacket
               )) {
               return;
            }

            val.cancel();
            this.delayedPackets
               .add(
                  new GetsendtimeHelper(
                     val2,
                     System.currentTimeMillis() + this.delay.getValue().longValue()
                  )
               );
            if (this.delayedPackets.size() >= this.maxPackets.getValue().intValue() * 2) {
               this.releasePackets();
            }
         } else if (val.getType()
               == EventPacket.Type.RECEIVE
            && val.getPacket() instanceof PlayerPositionLookS2CPacket
            )
          {
            this.releasePackets();
            this.timer.reset();
         }
      }
   }

   private boolean isSystemPacket(
      Packet<?> val
   ) {
      return val instanceof KeepAliveC2SPacket
         || val instanceof CommonPongC2SPacket
         || val instanceof TeleportConfirmC2SPacket
         || val instanceof CloseHandledScreenC2SPacket
         || val instanceof ClickSlotC2SPacket
         || val instanceof ClientStatusC2SPacket;
   }

   private void sendSinglePacket(
      Packet<?> val
   ) {
      this.releasing = true;
      mc.player
         .networkHandler
         .sendPacket(
            val
         );
      this.releasing = false;
   }

   private void releasePackets() {
      if (!this.delayedPackets.isEmpty()) {
         this.releasing = true;
         ObjectListIterator var1 = this.delayedPackets.iterator();

         while (var1.hasNext()) {
            GetsendtimeHelper val = (GetsendtimeHelper)var1.next();
            mc.player
               .networkHandler
               .sendPacket(
                  val.packet
               );
         }

         this.delayedPackets.clear();
         this.releasing = false;
      }
   }

   @Generated
   public ModeSetting getMode() {
      return this.mode;
   }

   @Generated
   public NumberSetting getDelay() {
      return this.delay;
   }

   @Generated
   public NumberSetting getMaxPackets() {
      return this.maxPackets;
   }

   @Generated
   public BooleanSetting getOnlyMovement() {
      return this.onlyMovement;
   }

   @Generated
   public ObjectArrayList<GetsendtimeHelper> getDelayedPackets() {
      return this.delayedPackets;
   }

   @Generated
   public FinishedHelper getTimer() {
      return this.timer;
   }

   @Generated
   public boolean isReleasing() {
      return this.releasing;
   }

   @Generated
   public void setReleasing(
      boolean val
   ) {
      this.releasing = val;
   }
}
