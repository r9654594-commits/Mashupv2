package dev.leg1tka.modules.misc;
import dev.leg1tka.modules.player.HpFixer;


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

import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;

public class ScoreboardHP extends Module {
   public static final ScoreboardHP INSTANCE = new ScoreboardHP();
   private static final Pattern HP_NUMBER = Pattern.compile("(\\d+(?:[.,]\\d+)?)");
   private static final float MAX_REASONABLE_HP = 1024.0F;
   public static final float UNKNOWN_HP = -1.0F;
   private final BooleanSetting gulpvp;

   public ScoreboardHP() {
      super("ScoreboardHP", Category.MISC);
      String var1 = "ScoreboardHP";
      this.gulpvp = new BooleanSetting("GulpVP", false);
      this.addSettings(new Setting[]{this.gulpvp});
   }

   private static boolean shouldHideHealth(
      PlayerEntity val
   ) {
      if (INSTANCE.isEnable() && INSTANCE.gulpvp.isState()) {
         if (mc.getCurrentServerEntry() != null) {
            String str = mc.getCurrentServerEntry()
               .address;
            if (str
               != null) {
               String var10000 = str.toLowerCase();
               if (var10000.contains("gulpvp.pw")) {
                  return true;
               }
            }
         }

         return isHealthHiddenOnServer(
            val
         );
      } else {
         return false;
      }
   }

   private static boolean isHealthHiddenOnServer(
      PlayerEntity val
   ) {
      if (mc.world == null) {
         return false;
      } else {
         try {
            Scoreboard val2 = mc.world
               .getScoreboard();
            ScoreboardObjective val3 = val2.getObjectiveForSlot(
               ScoreboardDisplaySlot.BELOW_NAME
            );
            ScoreboardObjective val4 = val2.getObjectiveForSlot(
               ScoreboardDisplaySlot.LIST
            );
            if (val3
                  == null
               && val4
                  == null) {
               Float val5 = getGulpVpSidebarHealth(
                  val
               );
               return val5
                  == null;
            } else {
               return false;
            }
         } catch (Exception var5) {
            return true;
         }
      }
   }

   public static float getHealth(
      LivingEntity val
   ) {
      if (val
         == null) {
         return 0.0F;
      } else if (HpFixer.INSTANCE.isEnable()) {
         return HpFixer.getHealth(
            val
         );
      } else if (!INSTANCE.isEnable()) {
         return val.getHealth();
      } else if (val instanceof ClientPlayerEntity
         )
       {
         return val.getHealth();
      } else if (val instanceof PlayerEntity val2
         )
       {
         if (shouldHideHealth(
            val2
         )) {
            return -1.0F;
         } else {
            if (INSTANCE.gulpvp.isState()) {
               Float val3 = getGulpVpSidebarHealth(
                  val2
               );
               if (val3
                  != null) {
                  return val3;
               }
            }

            return mc.getCurrentServerEntry() == null
               ? val.getHealth()
               : getObjectiveHealth(
                  val2
               );
         }
      } else {
         return val.getHealth();
      }
   }

   public static float getHealthWithAbsorption(
      LivingEntity val
   ) {
      if (val
         == null) {
         return 0.0F;
      } else if (HpFixer.INSTANCE.isEnable()) {
         return HpFixer.getHealth(
            val
         );
      } else if (!INSTANCE.isEnable()) {
         return Math.max(
            0.0F,
            val.getHealth()
               + val.getAbsorptionAmount()
         );
      } else if (val instanceof ClientPlayerEntity
         )
       {
         return Math.max(
            0.0F,
            val.getHealth()
               + val.getAbsorptionAmount()
         );
      } else if (val instanceof PlayerEntity val2
         )
       {
         if (shouldHideHealth(
            val2
         )) {
            return -1.0F;
         } else {
            if (INSTANCE.gulpvp.isState()
               && val instanceof PlayerEntity
               )
             {
               Float val3 = getGulpVpSidebarHealth(
                  val2
               );
               if (val3
                  != null) {
                  return Math.max(
                     0.0F,
                     val3
                  );
               }
            }

            return Math.max(
               0.0F,
               getHealth(
                     val
                  )
                  + val.getAbsorptionAmount()
            );
         }
      } else {
         return Math.max(
            0.0F,
            getHealth(
                  val
               )
               + val.getAbsorptionAmount()
         );
      }
   }

   public static boolean shouldShowUnknownInTargetHud(
      LivingEntity val
   ) {
      return val instanceof PlayerEntity val2
         ? shouldHideHealth(
            val2
         )
         : false;
   }

   private static float getObjectiveHealth(
      PlayerEntity val
   ) {
      try {
         Scoreboard val2 = val.getScoreboard();
         ScoreboardObjective val3 = val2.getObjectiveForSlot(
            ScoreboardDisplaySlot.BELOW_NAME
         );
         if (val3
            == null) {
            val3 = val2.getObjectiveForSlot(
               ScoreboardDisplaySlot.LIST
            );
         }

         if (val3
            == null) {
            return val.getHealth();
         } else {
            ReadableScoreboardScore val4 = val2.getScore(
               val,
               val3
            );
            return val4
                  == null
               ? val.getHealth()
               : (float)val4.getScore();
         }
      } catch (Exception var4) {
         return val.getHealth();
      }
   }

   private static Float getGulpVpSidebarHealth(
      PlayerEntity val
   ) {
      if (mc.world == null) {
         return null;
      } else {
         try {
            Scoreboard val2 = mc.world
               .getScoreboard();
            ScoreboardObjective val3 = val2.getObjectiveForSlot(
               ScoreboardDisplaySlot.SIDEBAR
            );
            if (val3
               == null) {
               return null;
            } else {
               NumberFormat val4 = val3.getNumberFormatOr(
                  StyledNumberFormat.RED
               );
               String[] val5 = collectNameVariants(
                  val
               );
               Float val6 = null;
               int n = -1;

               for (ScoreboardEntry val7 : val2.getScoreboardEntries(
                  val3
               )) {
                  if (!val7.hidden()
                     )
                   {
                     Team val8 = val2.getScoreHolderTeam(
                        val7.owner()
                     );
                     String text2 = stripFormatting(
                        Team.decorateName(
                              val8,
                              val7.name()
                           )
                           .getString()
                     );
                     String text3 = stripFormatting(
                        val7.owner()
                     );
                     String text4 = stripFormatting(
                        val7.formatted(
                              val4
                           )
                           .getString()
                     );
                     int n2 = getNameMatchScore(
                        text2,
                        text3,
                        val5
                     );
                     if (n2
                        >= 0) {
                        Float val9 = extractSidebarHp(
                           val7,
                           text4,
                           text2
                        );
                        if (val9
                              != null
                           && n2
                              > n
                           )
                         {
                           n = n2;
                           val6 = val9;
                        }
                     }
                  }
               }

               return val6;
            }
         } catch (Exception var15) {
            return null;
         }
      }
   }

   private static Float extractSidebarHp(
      ScoreboardEntry val,
      String val2,
      String val3
   ) {
      Float val4 = parseHpNumber(
         val2
      );
      if (val4
         != null) {
         return val4;
      } else if (isReasonableHp(
         val.value()
      )) {
         return (float)val.value();
      } else {
         Float val5 = parseHpFromLine(
            val3
         );
         return val5
               != null
            ? val5
            : null;
      }
   }

   private static String[] collectNameVariants(
      PlayerEntity val
   ) {
      String text2 = stripFormatting(
         val.getName()
            .getString()
      );
      String text3 = stripFormatting(
         val.getNameForScoreboard()
      );
      String str = val.getGameProfile()
            != null
         ? stripFormatting(
            val.getGameProfile()
               .getName()
         )
         : "";
      String str2 = "";
      if (mc.getNetworkHandler() != null) {
         PlayerListEntry val2 = mc.getNetworkHandler()
            .getPlayerListEntry(
               val.getUuid()
            );
         if (val2
            != null) {
            Text val3 = val2.getDisplayName();
            if (val3
               != null) {
               str2 = stripFormatting(
                  val3.getString()
               );
            }
         }
      }

      String name2 = protectName(
         text2
      );
      String name3 = protectName(
         text3
      );
      String name4 = protectName(
         str
      );
      String name5 = protectName(
         str2
      );
      String[] var10000 = new String[8];
      var10000[0] = text2;
      var10000[1] = name2;
      var10000[2] = text3;
      var10000[3] = name3;
      var10000[4] = str;
      var10000[5] = name4;
      var10000[6] = str2;
      var10000[7] = name5;
      return dedupeNames(var10000);
   }

   private static String[] dedupeNames(
      String... val
   ) {
      LinkedHashSet<String> set = new LinkedHashSet<>();

      for (String str : val) {
         if (str
            != null) {
            String str2 = str.trim();
            if (!str2.isEmpty()
               )
             {
               set.add(
                  str2
               );
            }
         }
      }

      return set.toArray(
         String[]::new
      );
   }

   private static String protectName(
      String val
   ) {
      if (val
            != null
         && !val.isEmpty()
         )
       {
         NameProtect val2 = ModuleManager.INSTANCE
               != null
            ? ModuleManager.nameProtect
            : null;
         return val2
                  != null
               && val2.isEnable()
            ? val2.patch(
               val
            )
            : val;
      } else {
         return "";
      }
   }

   private static int getNameMatchScore(
      String val,
      String val2,
      String[] val3
   ) {
      int n = -1;

      for (String str : val3) {
         if (str
               != null
            && !str.isEmpty()
            )
          {
            if (!val2.isEmpty()
               )
             {
               if (val2.equalsIgnoreCase(
                  str
               )) {
                  n = Math.max(
                     n,
                     100
                  );
               } else if (val2.contains(
                  str
               )) {
                  n = Math.max(
                     n,
                     80
                  );
               }
            }

            if (!val.isEmpty()
               )
             {
               if (val.equalsIgnoreCase(
                  str
               )) {
                  n = Math.max(
                     n,
                     95
                  );
               } else if (!val.endsWith(
                     str
                  )
                  && !val.endsWith(
                     " "
                        + str
                  )) {
                  int n2 = indexOfIgnoreCase(
                     val,
                     str
                  );
                  if (n2
                     >= 0) {
                     n = Math.max(
                        n,
                        70
                           + Math.min(
                              20,
                              str.length()
                           )
                     );
                  }
               } else {
                  n = Math.max(
                     n,
                     90
                  );
               }
            }
         }
      }

      return n;
   }

   private static int indexOfIgnoreCase(
      String val,
      String val2
   ) {
      if (val
            != null
         && val2
            != null
         && !val2.isEmpty()
         )
       {
         int n = val.length()
            - val2.length();

         for (int i2 = 0;
            i2
               <= n;
            i2++
         ) {
            if (val.regionMatches(
               true,
               i2,
               val2,
               0,
               val2.length()
            )) {
               return i2;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private static Float parseHpNumber(
      String val
   ) {
      if (val
            != null
         && !val.isEmpty()
         )
       {
         String var10000 = val.replace(
            "❤", ""
         );
         var10000 = var10000.replace("♥", "");
         var10000 = var10000.replace("HP", "");
         var10000 = var10000.replace("hp", "");
         var10000 = var10000.replace("хп", "");
         String str = var10000.replace(
               "Хп", ""
            )
            .trim();
         Matcher matcher2 = HP_NUMBER.matcher(
            str
         );
         if (!matcher2.find()
            )
          {
            return null;
         } else {
            try {
               float f2 = Float.parseFloat(
                  matcher2.group(
                        1
                     )
                     .replace(
                        (char)44,
                        (char)46
                     )
               );
               return isReasonableHp(
                     f2
                  )
                  ? f2
                  : null;
            } catch (NumberFormatException var4) {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static Float parseHpFromLine(
      String val
   ) {
      if (val
            != null
         && !val.isEmpty()
         )
       {
         Matcher matcher2 = HP_NUMBER.matcher(
            val
         );
         Float val2 = null;

         while (
            matcher2.find()
         ) {
            try {
               float f2 = Float.parseFloat(
                  matcher2.group(
                        1
                     )
                     .replace(
                        (char)44,
                        (char)46
                     )
               );
               if (isReasonableHp(
                  f2
               )) {
                  val2 = f2;
               }
            } catch (NumberFormatException var4) {
            }
         }

         return val2;
      } else {
         return null;
      }
   }

   private static boolean isReasonableHp(
      float val
   ) {
      return val
            >= 0.0F
         && val
            <= 1024.0F;
   }

   private static boolean isReasonableHp(
      int val
   ) {
      return val
            >= 0
         && val
            <= 1024;
   }

   private static String stripFormatting(
      String val
   ) {
      if (val
         == null) {
         return "";
      } else {
         return val.replaceAll(
               "§[0-9a-fk-orx]", ""
            )
            .replaceAll("(?i)§x(§[0-9a-f]){6}", "")
            .trim();
      }
   }
}
