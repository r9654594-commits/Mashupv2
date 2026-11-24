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

import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

public class AutoLeave extends Module {
   public static final AutoLeave INSTANCE = new AutoLeave();
   private static final Set<String> STAFF_PREFIXES = new HashSet<>(
      Arrays.asList("supp", "mod", "der", "adm", "wne", "curat", "dev", "yt", "мод", "помо", "адм", "владе", "курато", "сапп", "ютуб", "стажер", "сотрудник")
   );
   private final NumberSetting leaveDistance;
   private final MultiBooleanSetting leaveIfSeen;
   private final ModeSetting leaveType;
   private final BooleanSetting stopBaritone;
   private final BooleanSetting leaveDisable;
   private int cooldownTicks;

   public AutoLeave() {
      super("AutoLeave", Category.MISC);
      String var1 = "AutoLeave";
      this.leaveDistance = new NumberSetting(
         "Дистанция срабатывания", 5.0F, 3.0F, 50.0F, 1.0F
      );
      String var4 = "Выходить если замечен";
      this.leaveIfSeen = new MultiBooleanSetting(var4, new BooleanSetting[]{
         new BooleanSetting("Игрок", true),
         new BooleanSetting("Модератор", false)
      });
      String var6 = "Тип выхода";
      String var10 = "В мейн меню";
      String[] var10005 = new String[4];
      var10005[0] = "В мейн меню";
      var10005[1] = "/hub";
      int var10007 = 2;
      var10005[var10007] = "/home";
      var10007 = 3;
      var10005[var10007] = "/spawn";
      this.leaveType = new ModeSetting(var6, var10, var10005);
      this.stopBaritone = new BooleanSetting("Выключать баритон", false);
      this.leaveDisable = new BooleanSetting(
         "Выключать после выхода", true
      );
      this.addSettings(new Setting[]{
         this.leaveDistance,
         this.leaveIfSeen,
         this.leaveType,
         this.stopBaritone,
         this.leaveDisable
      });
   }

   @Override
   public void onEnable() {
      this.cooldownTicks = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.cooldownTicks = 0;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
         } else {
            float f = this.leaveDistance
               .get();

            for (PlayerEntity player2 : mc.world
               .getPlayers()) {
               if (player2
                     != null
                  && player2
                     != mc.player
                  && mc.player
                        .distanceTo(
                           player2
                        )
                     <= f
                  && this.shouldLeaveFor(
                     player2
                  )) {
                  this.triggerLeave();
                  break;
               }
            }
         }
      }
   }

   private boolean shouldLeaveFor(
      PlayerEntity val
   ) {
      if (this.isModerator(
         val
      )) {
         MultiBooleanSetting var2 = this.leaveIfSeen;
         return var2.is("Модератор");
      } else {
         MultiBooleanSetting var10000 = this.leaveIfSeen;
         return var10000.is("Игрок");
      }
   }

   private boolean isModerator(
      PlayerEntity val
   ) {
      if (val
         == null) {
         return false;
      } else {
         String name = val.getName()
            .getString();
         if (AspectDLC.INSTANCE != null
            && AspectDLC.INSTANCE.staffStorage != null
            && AspectDLC.INSTANCE
               .staffStorage
               .isStaff(
                  name
               )) {
            return true;
         } else {
            Team val2 = val.getScoreboardTeam();
            if (val2
               == null) {
               return false;
            } else {
               String str = val2.getPrefix()
                  .getString()
                  .toLowerCase(Locale.ROOT);

               for (String str2 : STAFF_PREFIXES) {
                  if (str.contains(
                     str2
                  )) {
                     return true;
                  }
               }

               return false;
            }
         }
      }
   }

   private void triggerLeave() {
      this.tryStopBaritone();
      String var1 = this.leaveType.getCurrent();
      int var2 = -1;
      switch (var1.hashCode()) {
         case 1503846:
            if (var1.equals("/hub")) {
               var2 = 1;
            }
            break;
         case 46613902:
            if (var1.equals("/home")) {
               var2 = 2;
            }
            break;
         case 193977407:
            if (var1.equals("В мейн меню")) {
               var2 = 0;
            }
            break;
         case 1455208620:
            if (var1.equals("/spawn")) {
               var2 = 3;
            }
      }

      switch (var2) {
         case 0:
            this.disconnectLeave();
            break;
         case 1:
            this.commandLeave("hub");
            break;
         case 2:
            this.commandLeave("home home");
            break;
         case 3:
            this.commandLeave("spawn");
      }
   }

   private void tryStopBaritone() {
      if (this.stopBaritone.isState() && mc.getNetworkHandler() != null) {
         ClientPlayNetworkHandler var10000 = mc.getNetworkHandler();
         var10000.sendChatMessage("#stop");
      }
   }

   private void disconnectLeave() {
      if (mc.getNetworkHandler() == null) {
         ChatUtil.sendMessage("Модуль не работает в одиночном мире");
      } else {
         ClientConnection var10000 = mc.getNetworkHandler().getConnection();
         var10000.disconnect(
            Text.literal("AutoLeave")
         );
         if (this.leaveDisable.isState()) {
            this.toggle();
         }
      }
   }

   private void commandLeave(
      String val
   ) {
      if (mc.getNetworkHandler() == null) {
         ChatUtil.sendMessage("AutoLeave нельзя использовать в одиночной игре!");
      } else {
         mc.getNetworkHandler()
            .sendChatCommand(
               val
            );
         this.cooldownTicks = this.leaveDisable.isState()
            ? 10
            : 30;
         if (this.leaveDisable.isState()) {
            this.toggle();
         }
      }
   }
}
