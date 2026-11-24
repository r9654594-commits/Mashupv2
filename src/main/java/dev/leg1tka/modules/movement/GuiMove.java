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
import dev.leg1tka.api.events.implement.EventCloseInv;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class GuiMove extends Module {
   public static GuiMove INSTANCE = new GuiMove();
   public ModeSetting mode;
   public ModeSetting grimVersion;
   public int tick;
   private final List<ClickSlotC2SPacket> pnd;
   private CloseHandledScreenC2SPacket cpkt;
   private boolean spause;
   private boolean wclose;
   private int dflush;
   private boolean flsh;
   private static final int SPK_TICKS = 5;
   private boolean spkstop;
   private boolean spkflsh;
   private boolean spkcls;
   private CloseHandledScreenC2SPacket spkpkt;
   private int spkstck;
   private static final int LG_IDLE = 0;
   private static final int LG_LOCK = 1;
   private static final int LG_W2 = 2;
   private static final int LG_W3 = 3;
   private static final int LG_SEND = 4;
   private static final int LG_W5 = 5;
   private static final int LG_CLOSE = 6;
   private static final int LG_W7 = 7;
   private static final int LG_UNLOCK = 8;
   private int lgstg;
   private boolean lgcls;
   private CloseHandledScreenC2SPacket lgpkt;

   public GuiMove() {
      super("GuiMove", Category.MOVEMENT);
      String var3 = "Обход";
      String var6 = "Обычный";
      String[] var10005 = new String[4];
      var10005[0] = "Обычный";
      var10005[1] = "Grim";
      int var10007 = 2;
      var10005[var10007] = "SpookyTime";
      var10007 = 3;
      var10005[var10007] = "Legit";
      this.mode = new ModeSetting(var3, var6, var10005);
      String var5 = "Версия свапа";
      String var8 = "Test";
      var10005 = new String[2];
      var10005[0] = "Test";
      var10005[1] = "New";
      this.grimVersion = new ModeSetting(var5, var8, var10005)
         .visible(
            () -> this.mode.is("Grim")
         );
      this.tick = 0;
      this.pnd = new ArrayList<>();
      this.cpkt = null;
      this.spause = false;
      this.wclose = false;
      this.dflush = -1;
      this.flsh = false;
      this.spkstop = false;
      this.spkflsh = false;
      this.spkcls = false;
      this.spkpkt = null;
      this.spkstck = 0;
      this.lgstg = 0;
      this.lgcls = false;
      this.lgpkt = null;
      this.addSettings(new Setting[]{
         this.mode,
         this.grimVersion
      });
   }

   @EventLink
   public void onUpdate(
      EventUpdate val5
   ) {
      if (mc.player != null) {
         KeyBinding[] var10000 = new KeyBinding[6];
         var10000[0] = mc.options.forwardKey;
         var10000[1] = mc.options.backKey;
         var10000[2] = mc.options.leftKey;
         var10000[3] = mc.options.rightKey;
         var10000[4] = mc.options.jumpKey;
         var10000[5] = mc.options.sprintKey;
         KeyBinding[] val = var10000;
         ModeSetting var3 = this.mode;
         if (var3.is("SpookyTime")) {
            this.handleSpookyTick(
               val
            );
         } else {
            ModeSetting var4 = this.mode;
            if (var4.is("Legit")) {
               this.handleLegitTick(
                  val
               );
            } else {
               ModeSetting var5 = this.mode;
               if (var5.is("Grim")) {
                  ModeSetting var6 = this.grimVersion;
                  if (var6.is("Test")
                     && this.wclose
                     && !SetmotionHelper.isMoving()) {
                     this.flushQueuedPackets(true);
                     this.wclose = false;
                     this.tick = 3;
                  }
               }

               ModeSetting var7 = this.mode;
               if (var7.is("Grim")) {
                  ModeSetting var8 = this.grimVersion;
                  if (var8.is("New")
                     && this.dflush >= 0) {
                     if (this.dflush == 0) {
                        this.flushQueuedPackets(true);
                        this.dflush = -1;
                        this.tick = 1;
                     } else {
                        this.dflush--;
                     }
                  }
               }

               if (this.tick == 0 && !this.pnd.isEmpty() && mc.currentScreen == null && !this.wclose) {
                  this.sendPendingPackets();
               }

               if (this.tick != 0) {
                  this.disableKeys(
                     val
                  );
                  this.tick--;
                  if (this.tick == 0 && this.spause) {
                     this.spause = false;
                     Sprint.popPause();
                  }
               } else if (!(mc.currentScreen instanceof ChatScreen) && !(mc.currentScreen instanceof SignEditScreen)) {
                  ModeSetting var9 = this.mode;
                  if (!var9.is("Grim")
                     || !(mc.currentScreen instanceof HandledScreen)
                     || mc.currentScreen instanceof InventoryScreen) {
                     if (this.wclose) {
                        this.disableKeys(
                           val
                        );
                     } else {
                        this.enableKeys(
                           val
                        );
                     }
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (val.getType()
            == EventPacket.Type.SEND
         && !this.flsh) {
         Object val2 = val.getPacket();
         ModeSetting var10000 = this.mode;
         if (var10000.is("SpookyTime")) {
            this.handleSpookyPacket(
               val,
               val2
            );
         } else {
            var10000 = this.mode;
            if (var10000.is("Legit")) {
               this.handleLegitPacket(
                  val,
                  val2
               );
            } else {
               var10000 = this.mode;
               if (var10000.is("Grim")
                  && SetmotionHelper.isMoving()
                  && mc.currentScreen instanceof InventoryScreen) {
                  if (val2 instanceof ClickSlotC2SPacket val3
                     )
                   {
                     this.pnd
                        .add(
                           val3
                        );
                     val.cancel();
                  } else {
                     if (val2 instanceof CloseHandledScreenC2SPacket val4
                        )
                      {
                        this.cpkt = val4;
                        var10000 = this.grimVersion;
                        if (var10000.is("New")) {
                           this.dflush = 1;
                           this.wclose = false;
                        } else {
                           this.wclose = true;
                        }

                        this.pauseSprint();
                        val.cancel();
                     }
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onCloseInv(
      EventCloseInv val
   ) {
      ModeSetting var10000 = this.mode;
      if (var10000.is("Grim")) {
         var10000 = this.grimVersion;
         if (var10000.is("New")
            && SetmotionHelper.isMoving()
            && mc.currentScreen instanceof InventoryScreen) {
            this.cpkt = new CloseHandledScreenC2SPacket(
               val.windowId
            );
            this.dflush = 1;
            this.pauseSprint();
            this.tick = 1;
            val.cancel();
            return;
         }
      }

      var10000 = this.mode;
      if (var10000.is("Grim") && !this.wclose
         )
       {
         this.pauseSprint();
         this.tick = 1;
      }
   }

   private void handleSpookyPacket(
      EventPacket val,
      Object val2
   ) {
      if (mc.currentScreen instanceof HandledScreen) {
         if (val2 instanceof ClickSlotC2SPacket val3
            )
          {
            if (!this.spkflsh && this.isMoving()) {
               val.cancel();
               this.pnd
                  .add(
                     val3
                  );
            }
         } else if (val2 instanceof CloseHandledScreenC2SPacket val4
            && !this.pnd.isEmpty()
            && this.isMoving()) {
            val.cancel();
            this.spkcls = true;
            this.spkpkt = val4;
         }
      }
   }

   private void handleSpookyTick(
      KeyBinding[] val
   ) {
      boolean flag = this.isInvOpen();
      if (this.spkstop || this.spkflsh) {
         this.disableKeys(
            val
         );
      } else if (flag
         || mc.currentScreen == null) {
         this.enableKeys(
            val
         );
      }

      boolean flag2 = !this.pnd
            .isEmpty()
         && (
            !flag
               || !this.isMoving()
               || this.spkcls
         );
      if (this.spkstop) {
         if (this.spkstck > 0) {
            this.spkstck--;
            return;
         }

         this.spkstop = false;
         this.spkflsh = true;
      } else if (flag2
         )
       {
         this.spkstop = true;
         this.spkstck = 5;
         this.disableKeys(
            val
         );
         return;
      }

      if (this.spkflsh) {
         this.flsh = true;

         while (!this.pnd.isEmpty()) {
            mc.getNetworkHandler().sendPacket((Packet)this.pnd.remove(0));
         }

         if (this.spkcls && this.spkpkt != null) {
            mc.getNetworkHandler().sendPacket(this.spkpkt);
         }

         this.flsh = false;
         this.resetSpk();
         this.enableKeys(
            val
         );
      }
   }

   private void handleLegitPacket(
      EventPacket val,
      Object val2
   ) {
      if (val2 instanceof ClickSlotC2SPacket val3
         )
       {
         if (!(mc.currentScreen instanceof HandledScreen)) {
            return;
         }

         if (!this.flsh && this.isMoving()) {
            val.cancel();
            this.pnd
               .add(
                  val3
               );
         }
      } else if (val2 instanceof CloseHandledScreenC2SPacket val4
         && !this.flsh
         && !this.pnd.isEmpty()) {
         val.cancel();
         this.lgcls = true;
         this.lgpkt = val4;
      }
   }

   private void handleLegitTick(
      KeyBinding[] val
   ) {
      boolean flag = this.isInvOpen();
      switch (this.lgstg) {
         case 1:
            this.disableKeys(
               val
            );
            this.lgstg = 2;
            return;
         case 2:
            this.lgstg = 3;
            return;
         case 3:
            this.lgstg = 4;
            return;
         case 4:
            this.flsh = true;

            while (!this.pnd.isEmpty()) {
               mc.getNetworkHandler().sendPacket((Packet)this.pnd.remove(0));
            }

            this.flsh = false;
            this.lgstg = 5;
            return;
         case 5:
            this.lgstg = 6;
            return;
         case 6:
            if (this.lgcls && this.lgpkt != null) {
               this.flsh = true;
               mc.getNetworkHandler().sendPacket(this.lgpkt);
               this.flsh = false;
            }

            this.lgstg = 7;
            return;
         case 7:
            this.lgstg = 8;
            return;
         case 8:
            this.enableKeys(
               val
            );
            this.resetLg();
            return;
         default:
            if (this.pnd.isEmpty() && !this.lgcls) {
               if (flag
                  || mc.currentScreen == null) {
                  this.enableKeys(
                     val
                  );
               }
            } else {
               this.lgstg = 1;
               this.disableKeys(
                  val
               );
            }
      }
   }

   private boolean isMoving() {
      return InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.forwardKey.getDefaultKey().getCode())
         || InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.backKey.getDefaultKey().getCode())
         || InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.leftKey.getDefaultKey().getCode())
         || InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.rightKey.getDefaultKey().getCode());
   }

   private boolean isInvOpen() {
      return mc.currentScreen instanceof HandledScreen;
   }

   private void disableKeys(
      KeyBinding[] val
   ) {
      for (KeyBinding val2 : val) {
         val2.setPressed(
            false
         );
      }
   }

   private void enableKeys(
      KeyBinding[] val
   ) {
      for (KeyBinding val2 : val) {
         val2.setPressed(
            InputUtil.isKeyPressed(
               mc.getWindow().getHandle(),
               val2.getDefaultKey()
                  .getCode()
            )
         );
      }
   }

   private void resetSpk() {
      this.pnd.clear();
      this.spkstop = false;
      this.spkflsh = false;
      this.spkcls = false;
      this.spkpkt = null;
      this.spkstck = 0;
   }

   private void resetLg() {
      this.pnd.clear();
      this.lgstg = 0;
      this.lgcls = false;
      this.lgpkt = null;
   }

   private void pauseSprint() {
      if (!this.spause) {
         Sprint.pushPause(0);
         this.spause = true;
      }
   }

   private void sendPendingPackets() {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         this.flsh = true;

         try {
            for (ClickSlotC2SPacket val : this.pnd) {
               mc.getNetworkHandler()
                  .sendPacket(
                     val
                  );
            }
         } finally {
            this.flsh = false;
         }

         this.pnd.clear();
      } else {
         this.pnd.clear();
      }
   }

   private void flushQueuedPackets(
      boolean val
   ) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         this.sendPendingPackets();
         if (val
            && this.cpkt != null) {
            this.flsh = true;

            try {
               mc.getNetworkHandler().sendPacket(this.cpkt);
            } finally {
               this.flsh = false;
            }

            this.cpkt = null;
         }
      } else {
         this.pnd.clear();
         this.cpkt = null;
      }
   }

   public static void stopTick(
      int val
   ) {
      GuiMove val2 = ModuleManager.guiMove;
      if (val2
            != null
         && val2.isEnable()
         )
       {
         val2.tick = Math.max(
            val2.tick,
            val
         );
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.flushQueuedPackets(true);
      if (this.spause) {
         this.spause = false;
         Sprint.popPause();
      }

      this.wclose = false;
      this.dflush = -1;
      this.flsh = false;
      this.tick = 0;
      this.resetSpk();
      this.resetLg();
   }
}
