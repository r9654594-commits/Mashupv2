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
import dev.leg1tka.mixin.ChatScreenAccessor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class NameProtect extends Module {
   public static final NameProtect INSTANCE = new NameProtect();
   private final BooleanSetting friends = new BooleanSetting(
      "Скрывать друзей",
      true
   );
   private final BooleanSetting grief = new BooleanSetting(
      "Скрывать гриф",
      false
   );
   private final StringSetting nickname = new StringSetting(
      "Никнейм",
      "AspectDLC",
      32
   );
   private final BooleanSetting fakeDonat = new BooleanSetting(
      "Фейк донат",
      false
   );
   private final ModeSetting donatRank;
   private static final int PATCH_CACHE_LIMIT = 512;
   private final Map<String, String> patchCache;
   private static final Map<String, String> DONATE_RANK_UNICODE = new LinkedHashMap<>();

   private NameProtect() {
      super(
         "NameProtect",
         "Скрывает никнеймы",
         Category.MISC
      );
      this.donatRank = new ModeSetting("Донат ранг", "Нет", new String[]{"Нет", "PLAYER", "HERO", "TITAN", "AVENGER", "OVERLORD", "MAGISTER", "IMPERATOR", "DRAGON", "BULL", "TIGER", "VAMPIRE", "BUNNY", "COBRA", "HYDRA", "RABBIT", "GOD", "WINTER", "SAKURA", "SUMMER", "HALLOWEEN", "YT", "TIKTOK", "TIKTOK+", "MEDIA", "YOUTUBE", "PRINCE", "PHOENIX", "GUARDIAN", "KRATOS", "PHANTOM", "CUSTOM", "D.HELPER", "HELPER", "ML.MODER", "MODER", "MODER+", "ST.MODER", "GL.MODER", "ML.ADMIN", "ADMIN", "CURATOR", "SPECTATOR", "DEVELOPER", "Hydra", "Cerberus", "Triton", "Phoenix", "Pandar", "Heat", "Cold", "Kronos", "Summer", "Winter", "Phobos", "Ares", "Aristocrat"}).visible(this.fakeDonat::isState);
      this.patchCache = new LinkedHashMap<String, String>(512, 0.75F, true) {
         @Override
         protected boolean removeEldestEntry(
            Entry<String, String> val
         ) {
            return this.size() > 512;
         }
      };
      this.addSettings(new Setting[]{
         this.friends,
         this.grief,
         this.nickname,
         this.fakeDonat,
         this.donatRank
      });
   }

   public String getFakeDonatePrefix() {
      return this.fakeDonat.isState()
            && !this.donatRank
               .is(
                  "Нет"
               )
         ? DONATE_RANK_UNICODE.getOrDefault(this.donatRank.getCurrent(), null)
         : null;
   }

   public String patch(
      String val
   ) {
      if (val
         == null) {
         return null;
      } else if (!this.shouldPatch()) {
         return val;
      } else {
         String str = this.getPatchCacheKey(
            val
         );
         String str2 = this.patchCache
            .get(
               str
            );
         if (str2
            != null) {
            return str2;
         } else {
            String name = this.getReplacementName();
            String var4 = this.replaceIgnoreCase(
               val,
               mc.getSession().getUsername(),
               name
            );
            if (this.friends.isState() && AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.friendStorage != null) {
               for (String str3 : AspectDLC.INSTANCE
                  .friendStorage
                  .getFriends()) {
                  var4 = this.replaceIgnoreCase(
                     var4,
                     str3,
                     name
                  );
               }
            }

            var4 = this.patchGrief(var4);
            this.patchCache
               .put(
                  str,
                  var4
               );
            return var4;
         }
      }
   }

   public String patchIncomingText(
      String val
   ) {
      return this.patch(
         val
      );
   }

   public Text patchText(
      Text val
   ) {
      if (val
         == null) {
         return null;
      } else if (!this.shouldPatch()) {
         return val;
      } else {
         String name = this.getReplacementName();
         Text var2 = ReplaceHelper.replace(
            val,
            mc.getSession().getUsername(),
            name
         );
         if (this.friends.isState() && AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.friendStorage != null) {
            for (String str : AspectDLC.INSTANCE
               .friendStorage
               .getFriends()) {
               var2 = ReplaceHelper.replace(
                  var2,
                  str,
                  name
               );
            }
         }

         return var2;
      }
   }

   public String getReplacementName() {
      String name = this.nickname
         .get();
      return name
               != null
            && !name.isBlank()
         ? name
         : "AspectDLC";
   }

   public boolean shouldHideGrief() {
      return this.grief.isState();
   }

   private String replaceIgnoreCase(
      String val,
      String val2,
      String val3
   ) {
      if (val
            != null
         && val2
            != null
         && !val2.isEmpty()
         )
       {
         int n = this.indexOfIgnoreCase(
            val,
            val2,
            0
         );
         if (n
            < 0) {
            return val;
         } else {
            StringBuilder val4 = new StringBuilder(
               val.length()
                  + val3.length()
            );
            int n2 = 0;

            for (int i = n;
               i
                  >= 0;
               i = this.indexOfIgnoreCase(
                  val,
                  val2,
                  n2
               )
            ) {
               val4.append(
                     val,
                     n2,
                     i
                  )
                  .append(
                     val3
                  );
               n2 = i
                  + val2.length();
            }

            val4.append(
               val,
               n2,
               val.length()
            );
            return val4.toString();
         }
      } else {
         return val;
      }
   }

   private int indexOfIgnoreCase(
      String val,
      String val2,
      int val3
   ) {
      int n = val.length()
         - val2.length();

      for (int i = Math.max(
            0,
            val3
         );
         i
            <= n;
         i++
      ) {
         if (val.regionMatches(
            true,
            i,
            val2,
            0,
            val2.length()
         )) {
            return i;
         }
      }

      return -1;
   }

   private String patchGrief(
      String val
   ) {
      if (val
            != null
         && this.grief.isState()) {
         String str = val.replaceAll(
            "Анархия-\\d+",
            "AspectDLC.fun"
         );
         return str.replaceAll(
            "ГРИФ #\\d+",
            "AspectDLC.fun"
         );
      } else {
         return val;
      }
   }

   private String getPatchCacheKey(
      String val
   ) {
      String str = mc
               != null
            && mc.getSession() != null
         ? mc.getSession().getUsername()
         : "";
      int n = 0;
      if (this.friends.isState() && AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.friendStorage != null) {
         List val2 = AspectDLC.INSTANCE
            .friendStorage
            .getFriends();
         n = val2.hashCode();
      }

      return str
         + this.getReplacementName()
         + this.friends.isState()
         + this.grief.isState()
         + n
         + val;
   }

   private boolean shouldPatch() {
      return this.isEnable() && mc != null && mc.player != null && mc.world != null && !this.isFriendRemoveInputActive();
   }

   private boolean isFriendRemoveInputActive() {
      if (!(
         mc.currentScreen instanceof ChatScreen val
      )) {
         return false;
      } else {
         TextFieldWidget val2 = ((ChatScreenAccessor)val)
            .AspectDLC$getChatField();
         if (val2
            == null) {
            return false;
         } else {
            String text2 = val2.getText();
            if (text2
               == null) {
               return false;
            } else {
               String str = text2.trim()
                  .toLowerCase();
               String str2 = AspectDLC.INSTANCE
                        != null
                     && AspectDLC.INSTANCE.commandStorage != null
                  ? AspectDLC.INSTANCE.commandStorage.getPrefix().toLowerCase()
                  : ".";
               return str.startsWith(
                  str2
                     + "friend remove"
               );
            }
         }
      }
   }

   static {
      DONATE_RANK_UNICODE.put("PLAYER", "ꔀ");
      DONATE_RANK_UNICODE.put("HERO", "ꔄ");
      DONATE_RANK_UNICODE.put("TITAN", "ꔈ");
      DONATE_RANK_UNICODE.put("AVENGER", "ꔒ");
      DONATE_RANK_UNICODE.put("OVERLORD", "ꔖ");
      DONATE_RANK_UNICODE.put("MAGISTER", "ꔠ");
      DONATE_RANK_UNICODE.put("IMPERATOR", "ꔤ");
      DONATE_RANK_UNICODE.put("DRAGON", "ꔨ");
      DONATE_RANK_UNICODE.put("D.HELPER", "ꕠ");
      DONATE_RANK_UNICODE.put("BULL", "ꔲ");
      DONATE_RANK_UNICODE.put("TIGER", "ꔶ");
      DONATE_RANK_UNICODE.put("VAMPIRE", "ꕄ");
      DONATE_RANK_UNICODE.put("BUNNY", "ꕖ");
      DONATE_RANK_UNICODE.put("COBRA", "ꕈ");
      DONATE_RANK_UNICODE.put("HYDRA", "ꕀ");
      DONATE_RANK_UNICODE.put("RABBIT", "ꕒ");
      DONATE_RANK_UNICODE.put("GOD", "ꕁ");
      DONATE_RANK_UNICODE.put("HELPER", "ꔉ");
      DONATE_RANK_UNICODE.put("ML.MODER", "ꔓ");
      DONATE_RANK_UNICODE.put("MODER", "ꔗ");
      DONATE_RANK_UNICODE.put("MODER+", "ꔡ");
      DONATE_RANK_UNICODE.put("ST.MODER", "ꔥ");
      DONATE_RANK_UNICODE.put("GL.MODER", "ꔩ");
      DONATE_RANK_UNICODE.put("ML.ADMIN", "ꔳ");
      DONATE_RANK_UNICODE.put("ADMIN", "ꔷ");
      DONATE_RANK_UNICODE.put("MEDIA", "ꔁ");
      DONATE_RANK_UNICODE.put("YT", "ꔅ");
      DONATE_RANK_UNICODE.put("HELPER", "ꔉ");
      DONATE_RANK_UNICODE.put("HERO", "ဲ");
      DONATE_RANK_UNICODE.put("TITAN", "ဪ");
      DONATE_RANK_UNICODE.put("PRINCE", "ဤ");
      DONATE_RANK_UNICODE.put("PHOENIX", "ဦ");
      DONATE_RANK_UNICODE.put("OVERLORD", "ာ");
      DONATE_RANK_UNICODE.put("GUARDIAN", "ါ");
      DONATE_RANK_UNICODE.put("KRATOS", "င");
      DONATE_RANK_UNICODE.put("PHANTOM", "ဢ");
      DONATE_RANK_UNICODE.put("CUSTOM", "ဴ");
      DONATE_RANK_UNICODE.put("WINTER", "ာ");
      DONATE_RANK_UNICODE.put("SAKURA", "်");
      DONATE_RANK_UNICODE.put("SUMMER", "ဥ");
      DONATE_RANK_UNICODE.put("HALLOWEEN", "ဣ");
      DONATE_RANK_UNICODE.put("TIKTOK", "ဵ");
      DONATE_RANK_UNICODE.put("TIKTOK+", "ံ");
      DONATE_RANK_UNICODE.put("MEDIA", "ဩ");
      DONATE_RANK_UNICODE.put("YOUTUBE", "အ");
      DONATE_RANK_UNICODE.put("CURATOR", "ဧ");
      DONATE_RANK_UNICODE.put("SPECTATOR", "ဿ");
      DONATE_RANK_UNICODE.put("DEVELOPER", "သ");
      DONATE_RANK_UNICODE.put("Hydra", "hydra");
      DONATE_RANK_UNICODE.put("Cerberus", "cerberus");
      DONATE_RANK_UNICODE.put("Triton", "triton");
      DONATE_RANK_UNICODE.put("Phoenix", "phoenix");
      DONATE_RANK_UNICODE.put("Pandar", "pandar");
      DONATE_RANK_UNICODE.put("Heat", "heat");
      DONATE_RANK_UNICODE.put("Cold", "cold");
      DONATE_RANK_UNICODE.put("Kronos", "kronos");
      DONATE_RANK_UNICODE.put("Summer", "summer");
      DONATE_RANK_UNICODE.put("Winter", "winter");
      DONATE_RANK_UNICODE.put("Phobos", "phobos");
      DONATE_RANK_UNICODE.put("Ares", "ares");
      DONATE_RANK_UNICODE.put("Aristocrat", "aristocrat");
   }
}
