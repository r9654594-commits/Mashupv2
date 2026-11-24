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
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;

public class AutoPvp extends Module {
   public static AutoPvp INSTANCE = new AutoPvp();
   private static final Pattern SEARCH_PATTERN = Pattern.compile("Игрок\\s+(\\S+)\\s+ищет себе соперника!", 2);
   private static final long OPEN_DELAY_MS = 250L;
   private static final long RETRY_DELAY_MS = 1000L;
   private static final int SEARCH_MENU_SLOT = 20;
   private final MultiBooleanSetting donateSettings;
   private final FinishedHelper actionTimer;
   private boolean queuedJoin;
   private String queuedNickname;

   public AutoPvp() {
      super("AutoPvP", "Помощник в подборе пвп для сервера LonyGrief", Category.MISC);
      this.donateSettings = new MultiBooleanSetting("С кем идти...", new BooleanSetting[]{
         new BooleanSetting("CUSTOM", false),
         new BooleanSetting("D.HELPER", false),
         new BooleanSetting("FROSTINE", false),
         new BooleanSetting("SPRING", false),
         new BooleanSetting("AUTUMN", false),
         new BooleanSetting("GLADIATOR", false),
         new BooleanSetting("PALADIN", false),
         new BooleanSetting("LUXE", false),
         new BooleanSetting("STAFF", false),
         new BooleanSetting("SUPPORT", false),
         new BooleanSetting("ETERNITY", false),
         new BooleanSetting("OVERLORD", false),
         new BooleanSetting("D.ADMIN", false),
         new BooleanSetting("POVELITEL", false),
         new BooleanSetting("IMPERATOR", false),
         new BooleanSetting("LEGENDA", false),
         new BooleanSetting("PRAVITEL", false),
         new BooleanSetting("PHOENIX", false),
         new BooleanSetting("PLAYER", false)
      });
      this.actionTimer = new FinishedHelper();
      this.addSettings(new Setting[]{this.donateSettings});
   }

   @Override
   public void onEnable() {
      this.queuedJoin = false;
      this.queuedNickname = null;
      this.actionTimer.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.queuedJoin = false;
      this.queuedNickname = null;
      super.onDisable();
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (mc.player != null
         && mc.world != null
         && val.getType()
            == EventPacket.Type.RECEIVE) {
         if (val.getPacket() instanceof GameMessageS2CPacket val2
            )
          {
            String str = val2.content()
               .getString();
            if (str
               != null) {
               String text = Formatting.strip(
                  str
               );
               if (text
                  == null) {
                  text = str;
               }

               Matcher matcher2 = SEARCH_PATTERN.matcher(
                  text
               );
               if (matcher2.find()
                  )
                {
                  String str2 = matcher2.group(
                     1
                  );
                  if (str2
                        != null
                     && !str2.isBlank()
                     )
                   {
                     if (AspectDLC.INSTANCE == null
                        || AspectDLC.INSTANCE.friendStorage == null
                        || !AspectDLC.INSTANCE
                           .friendStorage
                           .isFriend(
                              str2
                           )) {
                        NameType val3 = this.resolveDonateRank(
                           str2
                        );
                        if (this.isAllowed(
                           val3
                        )) {
                           this.queuedJoin = true;
                           this.queuedNickname = str2;
                           this.actionTimer.setMillis(0L);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val4
   ) {
      if (this.queuedJoin && mc.player != null && mc.world != null && mc.getNetworkHandler() != null && mc.interactionManager != null) {
         if (mc.currentScreen instanceof GenericContainerScreen val
            )
          {
            String str = val.getTitle()
               .getString()
               .toLowerCase();
            if (str.contains(
                  "поиск поединка"
               )
               && this.actionTimer.finished(250L)) {
               mc.interactionManager
                  .clickSlot(
                     ((GenericContainerScreenHandler)val.getScreenHandler())
                        .syncId,
                     20,
                     0,
                     SlotActionType.PICKUP,
                     mc.player
                  );
               this.queuedJoin = false;
               this.queuedNickname = null;
               this.actionTimer.reset();
            }
         } else if (mc.currentScreen == null) {
            if (this.actionTimer.finished(1000L)) {
               mc.getNetworkHandler().sendChatCommand("pvp");
               this.actionTimer.reset();
            }
         }
      }
   }

   private NameType resolveDonateRank(
      String val
   ) {
      if (mc.getNetworkHandler() == null) {
         return null;
      } else {
         for (PlayerListEntry val2 : mc.getNetworkHandler()
            .getPlayerList()) {
            String name = val2.getDisplayName()
                  != null
               ? val2.getDisplayName()
                  .getString()
               : val2.getProfile()
                  .getName();
            String text = Formatting.strip(
               name
            );
            if (text
               == null) {
               text = name;
            }

            int n = this.indexOfIgnoreCase(
               text,
               val
            );
            if (n
               >= 0) {
               String str = text.substring(
                     0,
                     n
                  )
                  .trim();
               if (str.isEmpty()
                  )
                {
                  return null;
               }

               String str2 = this.decodeDonatePrefix(
                     str
                  )
                  .replace("+", "");
               return NameType.fromString(
                  str2
               );
            }
         }

         return null;
      }
   }

   private boolean isAllowed(
      NameType val
   ) {
      return val
            == null
         ? this.donateSettings.is("CUSTOM")
         : this.donateSettings
            .is(
               val.getName()
            );
   }

   private String safeStrip(
      String val
   ) {
      String text = Formatting.strip(
         val
      );
      return text
            == null
         ? val
         : text;
   }

   private int indexOfIgnoreCase(
      String val,
      String val2
   ) {
      return val.toLowerCase()
         .indexOf(
            val2.toLowerCase()
         );
   }

   private String decodeDonatePrefix(
      String val
   ) {
      StringBuilder val2 = new StringBuilder(
         val.length()
      );
      int n = 0;

      while (
         n
            < val.length()
      ) {
         int n2 = val.codePointAt(
            n
         );
         n += Character.charCount(
            n2
         );
         String str = ReplacecodepointHelper.replaceCodePoint(
            n2
         );
         if (str
            != null) {
            val2.append(
               str
            );
         } else {
            val2.appendCodePoint(
               n2
            );
         }
      }

      return this.convertStyledToNormal(
            val2.toString()
         )
         .trim();
   }

   private String convertStyledToNormal(
      String val
   ) {
      String str = "\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e3\ud090\ud082\ud0e3\ud090\ud09a\ud0e5\ud084\ud0a2\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e5\ud080\ud09e\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e5\ud080\ud0a6\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e5\ud080\ud0a1\ud0e3\ud090\ud0ba\ud0e3\ud091\ud09a\ud0e3\ud082\ud0b0\ud0e3\ud090\ud099\ud0e3\ud091\ud09e\ud0e3\ud090\ud09a\ud0e3\ud091\ud09a\ud0e3\ud090\ud099\ud0e3\ud090\ud084\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e3\ud090\ud089\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e5\ud080\ud0b9\ud0e3\ud090\ud09a\ud0e3\ud091\ud09f\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e3\ud090\ud08c\ud0e3\ud090\ud099\ud0e3\ud092\ud091\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e3\ud090\ud08f\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\u0098\ud0e3\ud090\ud09a\ud0e3\ud090\ud082\ud0e3\ud090\ud0ba\ud0e3\ud091\ud09a\ud0e3\ud082\ud0b1\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e5\ud080\ud0ba\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e3\ud091\ud09a\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e3\ud082\ud0a0\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e3\ud090\ud08e\ud0e3\ud090\ud09a\ud0e3\ud090\ud08f\ud0e3\ud090\ud0b1\ud0e3\ud092\ud091\ud0e3\ud091\ud09e\ud0e3\ud090\ud099\ud0e3\ud092\ud091";
      String str2 = "ABCDEFGHIJKLMNOPRSTUVWYZN";
      StringBuilder val2 = new StringBuilder();

      for (char val3 : val.toCharArray()) {
         int n = str.indexOf(
            val3
         );
         if (n
            != -1) {
            val2.append(
               str2.charAt(
                  n
               )
            );
         } else {
            val2.append(
               val3
            );
         }
      }

      return val2.toString();
   }
}
