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
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.Vec3d;

public class AutoDuel extends Module {
   public static AutoDuel INSTANCE = new AutoDuel();
   public ModeSetting mode;
   private static final Pattern NAME_PATTERN = Pattern.compile("^\\w{3,16}$");
   private final List<String> sent;
   private final FinishedHelper duelT;
   private final FinishedHelper clrT;
   private final FinishedHelper pickT;
   private final FinishedHelper setT;
   private Vec3d lastPos;
   private boolean inDuel;

   public AutoDuel() {
      super(
         "AutoDuel",
         "Автоматически кидает дуель",
         Category.MISC
      );
      this.mode = new ModeSetting("Режим", "Шары", new String[]{"Щит", "Шипы", "Лук", "Тотемы", "Нодебафф", "Шары", "Классик", "Читер", "Незер"});
      this.sent = new ArrayList<>();
      this.duelT = new FinishedHelper();
      this.clrT = new FinishedHelper();
      this.pickT = new FinishedHelper();
      this.setT = new FinishedHelper();
      this.addSettings(new Setting[]{this.mode});
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.sent.clear();
      this.inDuel = false;
      if (mc.player != null) {
         this.lastPos = mc.player.getPos();
      }

      this.duelT.reset();
      this.clrT.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.sent.clear();
      this.inDuel = false;
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      if (mc.player != null && mc.world != null && !this.inDuel) {
         if (this.lastPos != null && mc.player.getPos().distanceTo(this.lastPos) > 500.0) {
            this.toggle();
         } else {
            this.lastPos = mc.player.getPos();
            if (this.clrT.getElapsedTime() >= 30000L) {
               this.sent.clear();
               this.clrT.reset();
            }

            if (this.duelT.getElapsedTime() >= 1000L) {
               this.sendDuel();
               this.duelT.reset();
            }

            this.handleGui();
         }
      }
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (mc.player != null && mc.world != null) {
         if (val.getType()
               == EventPacket.Type.RECEIVE
            && val.getPacket() instanceof GameMessageS2CPacket val2
            )
          {
            String str = val2.content()
               .getString()
               .toLowerCase();
            if (str.contains(
                     "начало"
                  )
                  && str.contains(
                     "через"
                  )
                  && str.contains(
                     "секунд"
                  )
               || str.contains(
                  "поединок начался"
               )
               || str.contains(
                  "во время поединка"
               )) {
               this.inDuel = true;
               this.toggle();
            }
         }
      }
   }

   private void sendDuel() {
      for (String str : this.getPlayers()) {
         if (!this.sent
               .contains(
                  str
               )
            && !str.equals(
               mc.player.getName().getString()
            )) {
            mc.getNetworkHandler()
               .sendChatCommand(
                  "duel "
                     + str
               );
            this.sent
               .add(
                  str
               );
            break;
         }
      }
   }

   private void handleGui() {
      if (mc.currentScreen instanceof GenericContainerScreen val
         )
       {
         int var4 = ((GenericContainerScreenHandler)val.getScreenHandler())
            .syncId;
         String str = val.getTitle()
            .getString();
         if (str.contains(
               "Выбор набора"
            )
            && this.pickT.getElapsedTime() >= 150L) {
            mc.interactionManager.clickSlot(var4, this.getModeSlot(), 0, SlotActionType.QUICK_MOVE, mc.player);
            this.pickT.reset();
         } else if (str.contains(
               "Настройка поединка"
            )
            && this.setT.getElapsedTime() >= 150L) {
            mc.interactionManager.clickSlot(var4, 0, 0, SlotActionType.QUICK_MOVE, mc.player);
            this.setT.reset();
         }
      }
   }

   private int getModeSlot() {
      if (this.mode.is("Щит")) {
         return 0;
      } else if (this.mode.is("Шипы")) {
         return 1;
      } else if (this.mode.is("Лук")) {
         return 2;
      } else if (this.mode.is("Тотемы")) {
         return 3;
      } else if (this.mode.is("Нодебафф")) {
         return 4;
      } else if (this.mode.is("Шары")) {
         return 5;
      } else if (this.mode.is("Классик")) {
         return 6;
      } else if (this.mode.is("Читер")) {
         return 7;
      } else {
         return this.mode.is("Незер")
            ? 8
            : 5;
      }
   }

   private List<String> getPlayers() {
      List val = new ArrayList<>();
      if (mc.getNetworkHandler() == null) {
         return val;
      } else {
         for (PlayerListEntry val2 : mc.getNetworkHandler()
            .getPlayerList()) {
            String str = val2.getProfile()
               .getName();
            if (NAME_PATTERN.matcher(
                  str
               )
               .matches()) {
               val.add(
                  str
               );
            }
         }

         return val;
      }
   }
}
