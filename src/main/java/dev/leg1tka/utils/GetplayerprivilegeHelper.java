package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;


import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class GetplayerprivilegeHelper {
   private static final Map<String, String> pmap = new LinkedHashMap<>();
   private static final Map<String, Integer> pcols = new LinkedHashMap<>();
   private static final Set<String> pkeys = Collections.unmodifiableSet(pmap.keySet());
   private static final Set<String> sroles = new HashSet<>();

   public static KeyHelper getPlayerPrivilege(
      PlayerEntity player
   ) {
      if (player
         == null) {
         return null;
      } else {
         MinecraftClient val = MinecraftClient.getInstance();
         if (val.getNetworkHandler()
            != null) {
            PlayerListEntry val2 = val.getNetworkHandler()
               .getPlayerListEntry(
                  player.getUuid()
               );
            if (val2
                  != null
               && val2.getDisplayName()
                  != null) {
               String name = parsePrivilegeFromText(
                  val2.getDisplayName()
               );
               if (name
                  != null) {
                  return infoFor(
                     name
                  );
               }
            }
         }

         Text text = player.getDisplayName();
         if (text
            != null) {
            String name2 = parsePrivilegeFromText(
               text
            );
            if (name2
               != null) {
               return infoFor(
                  name2
               );
            }
         }

         Team val3 = player.getScoreboardTeam();
         if (val3
            != null) {
            String name3 = parsePrivilegeFromTeam(
               val3
            );
            if (name3
               != null) {
               return infoFor(
                  name3
               );
            }
         }

         return null;
      }
   }

   public static KeyHelper getEntryPrivilege(
      PlayerListEntry val
   ) {
      if (val
         == null) {
         return null;
      } else {
         if (val.getDisplayName()
            != null) {
            String name = parsePrivilegeFromText(
               val.getDisplayName()
            );
            if (name
               != null) {
               return infoFor(
                  name
               );
            }
         }

         if (val.getScoreboardTeam()
            != null) {
            String name2 = parsePrivilegeFromTeam(
               val.getScoreboardTeam()
            );
            if (name2
               != null) {
               return infoFor(
                  name2
               );
            }
         }

         return null;
      }
   }

   public static KeyHelper getTeamPrivilege(
      Team val
   ) {
      if (val
         == null) {
         return null;
      } else {
         String str = parsePrivilegeFromTeam(
            val
         );
         return str
               != null
            ? infoFor(
               str
            )
            : null;
      }
   }

   public static String parsePrivilegeFromText(
      Text text
   ) {
      if (text
         == null) {
         return null;
      } else {
         Style style = text.getStyle();
         if (style
               != null
            && style.getFont()
               != null) {
            String text2 = style.getFont()
               .toString();
            String text3 = parsePrivilegeFromFontId(
               text2
            );
            if (text3
               != null) {
               return text3;
            }
         }

         String text4 = text.getString();
         if (text4
               != null
            && !text4.isEmpty()
            )
          {
            String text5 = text4.toLowerCase(
               Locale.ROOT
            );

            for (String text6 : pkeys) {
               if (text5.contains(
                  text6
               )) {
                  return text6;
               }
            }
         }

         for (Text text7 : text.getSiblings()) {
            String text8 = parsePrivilegeFromText(
               text7
            );
            if (text8
               != null) {
               return text8;
            }
         }

         return null;
      }
   }

   public static String parsePrivilegeFromFontId(
      String str
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         String str2 = str.toLowerCase(
            Locale.ROOT
         );
         String[] var10000 = new String[8];
         var10000[0] = "groups/";
         var10000[1] = "ranks/";
         int var10002 = 2;
         var10000[var10002] = "prefix/";
         var10002 = 3;
         var10000[var10002] = "staff/";
         var10002 = 4;
         var10000[var10002] = "roles/";
         var10002 = 5;
         var10000[var10002] = "badges/";
         var10002 = 6;
         var10000[var10002] = "tab/";
         var10002 = 7;
         var10000[var10002] = "icons/";
         String[] val = var10000;

         for (String str3 : val) {
            int i = str2.indexOf(
               str3
            );
            if (i
               >= 0) {
               String str4 = str2.substring(
                  i
                     + str3.length()
               );
               int i2 = str4.length();

               for (int i3 = 0;
                  i3
                     < str4.length();
                  i3++
               ) {
                  char val2 = str4.charAt(
                     i3
                  );
                  if (val2
                        < 97
                     || val2
                        > 122) {
                     i2 = i3;
                     break;
                  }
               }

               if (i2
                  > 0) {
                  String str5 = str4.substring(
                     0,
                     i2
                  );
                  if (pkeys.contains(
                     str5
                  )) {
                     return str5;
                  }
               }
            }
         }

         for (String str6 : pkeys) {
            if (str2.contains(
                  "/"
                     + str6
               )
               || str2.endsWith(
                  str6
               )) {
               return str6;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static String parsePrivilegeFromTeam(
      Team val
   ) {
      if (val
         == null) {
         return null;
      } else {
         if (val.getPrefix()
            != null) {
            String name = parsePrivilegeFromText(
               val.getPrefix()
            );
            if (name
               != null) {
               return name;
            }
         }

         if (val.getDisplayName()
            != null) {
            String name2 = parsePrivilegeFromText(
               val.getDisplayName()
            );
            if (name2
               != null) {
               return name2;
            }
         }

         return null;
      }
   }

   public static boolean isStaffRole(
      String str
   ) {
      return str
            != null
         && sroles.contains(
            str.toLowerCase(
               Locale.ROOT
            )
         );
   }

   public static KeyHelper infoFor(
      String str
   ) {
      if (str
         == null) {
         return null;
      } else {
         String str2 = str.toLowerCase(
            Locale.ROOT
         );
         String str3 = pmap.getOrDefault(
            str2,
            str
         );
         int i = pcols.getOrDefault(
            str2,
            -1
         );
         boolean flag = sroles.contains(
            str2
         );
         return new KeyHelper(
            str2,
            str3,
            i,
            flag
         );
      }
   }

   static {
      pmap.put("hydra", "Гидра");
      pmap.put("cerberus", "Цербер");
      pmap.put("triton", "Тритон");
      pmap.put("phoenix", "Феникс");
      pmap.put("pandar", "Пандар");
      pmap.put("heat", "Жара");
      pmap.put("cold", "Холод");
      pmap.put("kronos", "Кронос");
      pmap.put("summer", "Лето");
      pmap.put("winter", "Зима");
      pmap.put("phobos", "Фобос");
      pmap.put("ares", "Арес");
      pmap.put("aristocrat", "Аристократ");
      pmap.put("youtuber", "Ютубер");
      pmap.put("helper", "Хелпер");
      pmap.put("shelper", "Ст. Хелпер");
      pmap.put("moder", "Модер");
      pmap.put("smoder", "Ст. Модер");
      pmap.put("admin", "Админ");
      pmap.put("curator", "Куратор");
      pmap.put("owner", "Владелец");
      pmap.put("dev", "Разработчик");
      pcols.put("hydra", -14291019);
      pcols.put("cerberus", -14407514);
      pcols.put("triton", -14412111);
      pcols.put("phoenix", -13636240);
      pcols.put("pandar", -13779220);
      pcols.put("heat", -13635945);
      pcols.put("cold", -14123877);
      pcols.put("kronos", -14268497);
      pcols.put("summer", -13634800);
      pcols.put("winter", -13968170);
      pcols.put("phobos", -13675849);
      pcols.put("ares", -14123193);
      pcols.put("aristocrat", -14038609);
      pcols.put("youtuber", -13634684);
      pcols.put("helper", -14675989);
      pcols.put("shelper", -13631376);
      pcols.put("moder", -14155920);
      pcols.put("smoder", -16777017);
      pcols.put("admin", -13636944);
      pcols.put("curator", -43691);
      pcols.put("owner", -52429);
      pcols.put("dev", -11141121);
      sroles.add("youtuber");
      sroles.add("helper");
      sroles.add("shelper");
      sroles.add("moder");
      sroles.add("smoder");
      sroles.add("admin");
      sroles.add("curator");
      sroles.add("owner");
      sroles.add("dev");
   }
}
