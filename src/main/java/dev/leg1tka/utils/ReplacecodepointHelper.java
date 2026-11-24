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


import java.util.HashMap;
import java.util.Map;

public class ReplacecodepointHelper {
   private static final Map<Integer, String> REPLACEMENTS = new HashMap<>();
   private static final Map<Integer, Integer> RANK_COLORS = new HashMap<>();
   private static final int[] RANKS = new int[]{
      42240,
      42244,
      42248,
      42258,
      42262,
      42272,
      42276,
      42280,
      42336,
      42290,
      42294,
      42308,
      42326,
      42312,
      42304,
      42322,
      42249,
      42259,
      42263,
      42273,
      42277,
      42281,
      42291,
      42295,
      42241,
      42245,
      42313,
      4144,
      4138,
      4132,
      4134,
      4140,
      4139,
      4151,
      4130,
      4148,
      4141,
      4152,
      4133,
      4131,
      4149,
      4150,
      4137,
      4129,
      4145,
      4143,
      4146,
      4135,
      4153,
      4126
   };

   public static String replaceCodePoint(
      int n
   ) {
      return REPLACEMENTS.get(
         n
      );
   }

   public static int getGradientColorForReplacement(
      int n,
      int n2,
      int n3,
      float f,
      int n4
   ) {
      if (isRank(
         n
      )) {
         Integer val = RANK_COLORS.get(
            n
         );
         if (val
            == null) {
            return withOpacity(
               n4,
               f
            );
         } else {
            int color = ColorUtil.darken(
               val,
               0.8F
            );
            float y = n3
                  <= 1
               ? 1.0F
               : (float)n2
                  / (float)(
                     n3
                        - 1
                  );
            int color2 = ColorUtil.interpolateColor(
               color,
               val,
               y
            );
            return withOpacity(
               color2,
               f
            );
         }
      } else {
         return withOpacity(
            n4,
            f
         );
      }
   }

   private static boolean isRank(
      int n
   ) {
      for (int i : RANKS) {
         if (i
            == n
            )
          {
            return true;
         }
      }

      return false;
   }

   private static int withOpacity(
      int n,
      float f
   ) {
      int color = Math.max(
         0,
         Math.min(
            255,
            (int)(
               f
                  * 255.0F
            )
         )
      );
      return ColorUtil.setAlphaColor(
         n,
         color
      );
   }

   static {
      REPLACEMENTS.put(9889, "");
      REPLACEMENTS.put(9733, "");
      REPLACEMENTS.put(42240, "PLAYER");
      REPLACEMENTS.put(42244, "HERO");
      REPLACEMENTS.put(42248, "TITAN");
      REPLACEMENTS.put(42258, "AVENGER");
      REPLACEMENTS.put(42262, "OVERLORD");
      REPLACEMENTS.put(42272, "MAGISTER");
      REPLACEMENTS.put(42276, "IMPERATOR");
      REPLACEMENTS.put(42280, "DRAGON");
      REPLACEMENTS.put(42336, "D.HELPER");
      REPLACEMENTS.put(42290, "BULL");
      REPLACEMENTS.put(42294, "TIGER");
      REPLACEMENTS.put(42308, "VAMPIRE");
      REPLACEMENTS.put(42326, "BUNNY");
      REPLACEMENTS.put(42312, "COBRA");
      REPLACEMENTS.put(42304, "HYDRA");
      REPLACEMENTS.put(42322, "RABBIT");
      REPLACEMENTS.put(42249, "HELPER");
      REPLACEMENTS.put(42259, "ML.MODER");
      REPLACEMENTS.put(42263, "MODER");
      REPLACEMENTS.put(42273, "MODER+");
      REPLACEMENTS.put(42277, "ST.MODER");
      REPLACEMENTS.put(42281, "GL.MODER");
      REPLACEMENTS.put(42291, "ML.ADMIN");
      REPLACEMENTS.put(42295, "ADMIN");
      REPLACEMENTS.put(42241, "MEDIA");
      REPLACEMENTS.put(42245, "YT");
      REPLACEMENTS.put(42305, "GOD");
      REPLACEMENTS.put(4144, "HERO");
      REPLACEMENTS.put(4138, "TITAN");
      REPLACEMENTS.put(4132, "PRINCE");
      REPLACEMENTS.put(4134, "PHOENIX");
      REPLACEMENTS.put(4140, "OVERLORD");
      REPLACEMENTS.put(4139, "GUARDIAN");
      REPLACEMENTS.put(4151, "KRATOS");
      REPLACEMENTS.put(4130, "PHANTOM");
      REPLACEMENTS.put(4148, "CUSTOM");
      REPLACEMENTS.put(4141, "WINTER");
      REPLACEMENTS.put(4152, "SAKURA");
      REPLACEMENTS.put(4133, "SUMMER");
      REPLACEMENTS.put(4131, "HALLOWEEN");
      REPLACEMENTS.put(4149, "TIKTOK");
      REPLACEMENTS.put(4150, "TIKTOK+");
      REPLACEMENTS.put(4137, "MEDIA");
      REPLACEMENTS.put(4129, "YOUTUBE");
      REPLACEMENTS.put(4145, "HELPER");
      REPLACEMENTS.put(4143, "ML.ADMIN");
      REPLACEMENTS.put(4146, "MODER");
      REPLACEMENTS.put(4135, "CURATOR");
      REPLACEMENTS.put(4153, "SPECTATOR");
      REPLACEMENTS.put(4126, "DEVELOPER");
      REPLACEMENTS.put(7424, "A");
      REPLACEMENTS.put(665, "B");
      REPLACEMENTS.put(7428, "C");
      REPLACEMENTS.put(7429, "D");
      REPLACEMENTS.put(7431, "E");
      REPLACEMENTS.put(42800, "F");
      REPLACEMENTS.put(610, "G");
      REPLACEMENTS.put(668, "H");
      REPLACEMENTS.put(618, "I");
      REPLACEMENTS.put(7434, "J");
      REPLACEMENTS.put(7435, "K");
      REPLACEMENTS.put(671, "L");
      REPLACEMENTS.put(7437, "M");
      REPLACEMENTS.put(628, "N");
      REPLACEMENTS.put(7439, "O");
      REPLACEMENTS.put(7448, "P");
      REPLACEMENTS.put(491, "Q");
      REPLACEMENTS.put(640, "R");
      REPLACEMENTS.put(7451, "T");
      REPLACEMENTS.put(7452, "U");
      REPLACEMENTS.put(42801, "S");
      REPLACEMENTS.put(7456, "V");
      REPLACEMENTS.put(7457, "W");
      REPLACEMENTS.put(7521, "X");
      REPLACEMENTS.put(655, "Y");
      REPLACEMENTS.put(7458, "Z");
      RANK_COLORS.put(42240, ColorUtil.rgb(141, 143, 141));
      RANK_COLORS.put(42244, ColorUtil.rgb(100, 113, 251));
      RANK_COLORS.put(42248, ColorUtil.rgb(245, 220, 29));
      RANK_COLORS.put(42258, ColorUtil.rgb(79, 201, 83));
      RANK_COLORS.put(42262, ColorUtil.rgb(85, 255, 255));
      RANK_COLORS.put(42272, ColorUtil.rgb(224, 138, 52));
      RANK_COLORS.put(42276, ColorUtil.rgb(202, 60, 60));
      RANK_COLORS.put(42280, ColorUtil.rgb(245, 51, 238));
      RANK_COLORS.put(42336, ColorUtil.rgb(214, 200, 42));
      RANK_COLORS.put(42290, ColorUtil.rgb(121, 81, 202));
      RANK_COLORS.put(42294, ColorUtil.rgb(202, 130, 60));
      RANK_COLORS.put(42308, ColorUtil.rgb(202, 60, 60));
      RANK_COLORS.put(42326, ColorUtil.rgb(68, 65, 66));
      RANK_COLORS.put(42312, ColorUtil.rgb(127, 214, 86));
      RANK_COLORS.put(42304, ColorUtil.rgb(92, 120, 7));
      RANK_COLORS.put(42322, ColorUtil.rgb(230, 232, 230));
      RANK_COLORS.put(42249, ColorUtil.rgb(214, 200, 42));
      RANK_COLORS.put(42259, ColorUtil.rgb(100, 113, 251));
      RANK_COLORS.put(42263, ColorUtil.rgb(100, 113, 251));
      RANK_COLORS.put(42273, ColorUtil.rgb(121, 81, 202));
      RANK_COLORS.put(42277, ColorUtil.rgb(100, 113, 251));
      RANK_COLORS.put(42281, ColorUtil.rgb(121, 81, 202));
      RANK_COLORS.put(42291, ColorUtil.rgb(64, 151, 214));
      RANK_COLORS.put(42295, ColorUtil.rgb(202, 60, 60));
      RANK_COLORS.put(42241, ColorUtil.rgb(121, 81, 202));
      RANK_COLORS.put(42245, ColorUtil.rgb(255, 255, 255));
      RANK_COLORS.put(42305, ColorUtil.rgb(245, 198, 29));
      RANK_COLORS.put(42313, ColorUtil.rgb(202, 130, 60));
      RANK_COLORS.put(4144, ColorUtil.rgb(13, 176, 209));
      RANK_COLORS.put(4138, ColorUtil.rgb(21, 232, 24));
      RANK_COLORS.put(4132, ColorUtil.rgb(232, 169, 21));
      RANK_COLORS.put(4134, ColorUtil.rgb(237, 215, 19));
      RANK_COLORS.put(4140, ColorUtil.rgb(64, 163, 152));
      RANK_COLORS.put(4139, ColorUtil.rgb(86, 196, 99));
      RANK_COLORS.put(4151, ColorUtil.rgb(147, 46, 230));
      RANK_COLORS.put(4130, ColorUtil.rgb(230, 46, 46));
      RANK_COLORS.put(4148, ColorUtil.rgb(16, 35, 179));
      RANK_COLORS.put(4141, ColorUtil.rgb(55, 154, 184));
      RANK_COLORS.put(4152, ColorUtil.rgb(184, 39, 159));
      RANK_COLORS.put(4133, ColorUtil.rgb(255, 182, 56));
      RANK_COLORS.put(4131, ColorUtil.rgb(232, 60, 30));
      RANK_COLORS.put(4149, ColorUtil.rgb(0, 0, 0));
      RANK_COLORS.put(4150, ColorUtil.rgb(0, 0, 0));
      RANK_COLORS.put(4137, ColorUtil.rgb(37, 232, 30));
      RANK_COLORS.put(4129, ColorUtil.rgb(232, 30, 30));
      RANK_COLORS.put(4145, ColorUtil.rgb(30, 134, 232));
      RANK_COLORS.put(4143, ColorUtil.rgb(89, 167, 227));
      RANK_COLORS.put(4146, ColorUtil.rgb(62, 137, 194));
      RANK_COLORS.put(4135, ColorUtil.rgb(56, 235, 74));
      RANK_COLORS.put(4153, ColorUtil.rgb(173, 184, 174));
      RANK_COLORS.put(4126, ColorUtil.rgb(255, 0, 25));
   }
}
