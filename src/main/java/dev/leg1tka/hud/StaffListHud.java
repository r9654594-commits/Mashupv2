package dev.leg1tka.hud;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

public class StaffListHud extends HudElement {
   protected static final int STATUS_VANISH_COLOR = -47526;
   protected static final int STATUS_GM3_COLOR = -9146;
   protected static final int STATUS_ONLINE_COLOR = -10158216;
   private final MinecraftClient mc = MinecraftClient.getInstance();
   protected final Map<String, StatusHolder> staffDataCache = new LinkedHashMap<>();
   protected final Map<String, Float> staffAnimations = new HashMap<>();
   protected final Set<String> activeStaff = new HashSet<>();
   private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
   private final Set<String> validStaffPrefixes = new HashSet<>();
   private final UpdateHelper widthAnimation = new UpdateHelper(60.0F, 10.5F, EasingFunctions.QUAD_OUT);
   private float staffAnimatedHeight = 18.0F;
   private long lastStaffUpdate = 0L;
   private final List<String> visiblePlayers = new ArrayList<>();
   private final Set<String> animationScratch = new HashSet<>();
   private Font font10;
   private Font font12;
   private Font font14;
   private Font font16;
   private Font iconFont;

   public StaffListHud(
      Draggable val
   ) {
      super(
         val
      );
      Set var10000 = this.validStaffPrefixes;
      String[] var10001 = new String[28];
      var10001[0] = "supp";
      var10001[1] = "ꜱupp";
      int var10003 = 2;
      var10001[var10003] = "mod";
      var10003 = 3;
      var10001[var10003] = "der";
      var10003 = 4;
      var10001[var10003] = "adm";
      var10003 = 5;
      var10001[var10003] = "wne";
      var10003 = 6;
      var10001[var10003] = "мод";
      var10003 = 7;
      var10001[var10003] = "помо";
      var10003 = 8;
      var10001[var10003] = "адм";
      var10003 = 9;
      var10001[var10003] = "владе";
      var10003 = 10;
      var10001[var10003] = "отри";
      var10003 = 11;
      var10001[var10003] = "таф";
      var10003 = 12;
      var10001[var10003] = "taf";
      var10003 = 13;
      var10001[var10003] = "curat";
      var10003 = 14;
      var10001[var10003] = "курато";
      var10003 = 15;
      var10001[var10003] = "dev";
      var10003 = 16;
      var10001[var10003] = "раз";
      var10003 = 17;
      var10001[var10003] = "сапп";
      var10003 = 18;
      var10001[var10003] = "yt";
      var10003 = 19;
      var10001[var10003] = "ютуб";
      var10003 = 20;
      var10001[var10003] = "стажер";
      var10003 = 21;
      var10001[var10003] = "сотрудник";
      var10003 = 22;
      var10001[var10003] = "owner";
      var10003 = 23;
      var10001[var10003] = "curator";
      var10003 = 24;
      var10001[var10003] = "helper";
      var10003 = 25;
      var10001[var10003] = "moder";
      var10003 = 26;
      var10001[var10003] = "admin";
      var10003 = 27;
      var10001[var10003] = "хелп";
      var10000.addAll(Arrays.asList(var10001));
   }

   protected void initFonts() {
      if (this.font12 == null) {
         this.font10 = Fonts.getFont("nunito", 10);
         this.font12 = Fonts.getFont("nunito", 12);
         this.font14 = Fonts.getFont("nunito", 14);
         this.font16 = Fonts.getFont("nunito", 16);
         this.iconFont = Fonts.getFont("icon", 13);
      }
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      if (this.mc.player != null && this.mc.world != null) {
         this.initFonts();
         long time = System.currentTimeMillis();
         if (time
               - this.lastStaffUpdate
            > 500L) {
            this.updateStaffCache();
            this.lastStaffUpdate = time;
         }

         this.updateAnimations();
         this.renderDefaultStyle(
            event
         );
         super.onRender(
            event
         );
      }
   }

   private boolean matchesStaffPrefix(
      String str
   ) {
      String str2 = str.toLowerCase(
         Locale.ROOT
      );

      for (String str3 : this.validStaffPrefixes) {
         if (str2.contains(
            str3
         )) {
            return true;
         }
      }

      return false;
   }

   private List<TextHolder> parsePrefix(
      Text text2
   ) {
      if (text2
         == null) {
         return new ArrayList<>();
      } else {
         KeyHelper val = GetplayerprivilegeHelper.infoFor(
            GetplayerprivilegeHelper.parsePrivilegeFromText(
               text2
            )
         );
         if (val
               == null
            || !val.isstaff()
            || !text2.getString()
                  .trim()
                  .isEmpty()
               && this.matchesStaffPrefix(
                  text2.getString()
               )) {
            List val2 = new ArrayList<>();
            text2.visit(
               (val3, val4) -> {
                  if (val4
                        != null
                     && !val4.isEmpty()
                     )
                   {
                     this.appendPrefixSegments(
                        val2,
                        val4,
                        val3.getColor()
                              != null
                           ? val3.getColor()
                              .getRgb()
                           : 16777215
                     );
                     return Optional.empty();
                  } else {
                     return Optional.empty();
                  }
               },
               Style.EMPTY
            );
            return val2;
         } else {
            List val5 = new ArrayList<>();
            val5.add(
               new TextHolder(
                  val.text()
                     + " ",
                  val.col()
               )
            );
            return val5;
         }
      }
   }

   private void appendPrefixSegments(
      List<TextHolder> text,
      String str,
      int n
   ) {
      int i = n;
      StringBuilder val = new StringBuilder();
      int i2 = n;
      int i3 = 0;

      while (
         i3
            < str.length()
      ) {
         int i4 = str.codePointAt(
            i3
         );
         int i5 = Character.charCount(
            i4
         );
         if (i4
               == 167
            && i3
                  + i5
               < str.length()
            )
          {
            this.flushPrefixSegment(
               text,
               val,
               i2
            );
            char val2 = Character.toLowerCase(
               str.charAt(
                  i3
                     + i5
               )
            );
            Integer val3 = this.sectionColorToRgb(
               val2
            );
            i = val3
                  != null
               ? val3
               : (
                  val2
                        == 114
                     ? n
                     : i
               );
            i2 = i;
            i3 += i5
               + 1;
         } else {
            String text2 = ReplacecodepointHelper.replaceCodePoint(
               i4
            );
            if (text2
               == null) {
               if (val.length()
                     > 0
                  && i2
                     != i
                  )
                {
                  this.flushPrefixSegment(
                     text,
                     val,
                     i2
                  );
               }

               i2 = i;
               val.appendCodePoint(
                  i4
               );
               i3 += i5;
            } else {
               this.flushPrefixSegment(
                  text,
                  val,
                  i2
               );
               int i6 = Math.max(
                  1,
                  text2.length()
               );

               for (int i7 = 0;
                  i7
                     < text2.length();
                  i7++
               ) {
                  int i8 = ReplacecodepointHelper.getGradientColorForReplacement(
                     i4,
                     i7,
                     i6,
                     1.0F,
                     i
                  );
                  if (val.length()
                        > 0
                     && i2
                        != i8
                     )
                   {
                     this.flushPrefixSegment(
                        text,
                        val,
                        i2
                     );
                  }

                  i2 = i8;
                  val.append(
                     text2.charAt(
                        i7
                     )
                  );
               }

               i3 += i5;
            }
         }
      }

      this.flushPrefixSegment(
         text,
         val,
         i2
      );
   }

   private void flushPrefixSegment(
      List<TextHolder> text,
      StringBuilder val,
      int n
   ) {
      if (!val.isEmpty()
         )
       {
         String text2 = val.toString();
         TextHolder text3 = new TextHolder(
            text2,
            n
         );
         if (this.font12 != null) {
            text3.width12 = this.font12
               .getWidth(
                  text2
               );
         }

         if (this.font14 != null) {
            text3.width14 = this.font14
               .getWidth(
                  text2
               );
         }

         text.add(
            text3
         );
         val.setLength(
            0
         );
      }
   }

   private Integer sectionColorToRgb(
      char val
   ) {
      return switch (val) {
         case '0' -> 0;
         case '1' -> 170;
         case '2' -> 43520;
         case '3' -> 43690;
         case '4' -> 11141120;
         case '5' -> 11141290;
         case '6' -> 16755200;
         case '7' -> 11184810;
         case '8' -> 5592405;
         case '9' -> 5592575;
         default -> null;
         case 'a' -> 5635925;
         case 'b' -> 5636095;
         case 'c' -> 16733525;
         case 'd' -> 16733695;
         case 'e' -> 16777045;
         case 'f' -> 16777215;
      };
   }

   protected void updateStaffCache() {
      this.activeStaff.clear();
      String name = this.mc
         .player
         .getName()
         .getString();

      for (Team val : this.mc
         .world
         .getScoreboard()
         .getTeams()) {
         Collection<String> val2 = val.getPlayerList();
         if (val2.size()
            == 1) {
            String name2 = val2.iterator()
               .next();
            if (this.namePattern
                  .matcher(
                     name2
                  )
                  .matches()
               && !name2.equals(
                  name
               )) {
               PlayerListEntry val3 = this.mc
                        .getNetworkHandler()
                     != null
                  ? this.mc
                     .getNetworkHandler()
                     .getPlayerListEntry(
                        name2
                     )
                  : null;
               boolean flag = val3
                  == null;
               boolean flag2 = val3
                     != null
                  && val3.getGameMode()
                     == GameMode.SPECTATOR;
               Text text2 = val.getPrefix();
               String name3 = text2
                     != null
                  ? text2.getString()
                  : "";
               boolean flag3 = this.matchesStaffPrefix(
                  name3
               );
               KeyHelper val4 = GetplayerprivilegeHelper.getTeamPrivilege(
                  val
               );
               boolean flag4 = val4
                     != null
                  && val4.isstaff();
               boolean flag5 = AspectDLC.INSTANCE
                  .staffStorage
                  .isStaff(
                     name2
                  );
               if ((
                     flag3
                        || flag4
                        || flag
                        || flag2
                        || flag5
                  )
                  && (
                     flag3
                        || flag4
                        || flag5
                        || this.activeStaff
                           .contains(
                              name2
                           )
                        || this.staffDataCache
                           .containsKey(
                              name2
                           )
                  )) {
                  this.activeStaff
                     .add(
                        name2
                     );
                  String var32;
                  if (flag
                     )
                   {
                     var32 = "VANISH";
                  } else if (flag2
                     )
                   {
                     var32 = "GM3";
                  } else {
                     var32 = "ONLINE";
                  }

                  String name4 = var32;
                  StatusHolder val5 = this.staffDataCache
                     .get(
                        name2
                     );
                  if (val5
                     == null) {
                     val5 = new StatusHolder(
                        name4
                     );
                     this.staffDataCache
                        .put(
                           name2,
                           val5
                        );
                  }

                  val5.status = name4;
                  if (!flag4
                     || text2
                           != null
                        && !text2.getString()
                           .trim()
                           .isEmpty()
                        && flag3
                     )
                   {
                     val5.segments = this.parsePrefix(
                        text2
                     );
                  } else {
                     val5.segments = new ArrayList<>();
                     val5.segments
                        .add(
                           new TextHolder(
                              val4.text()
                                 + " ",
                              val4.col()
                           )
                        );
                  }

                  this.calculateWidths(
                     val5,
                     name2
                  );
               }
            }
         }
      }

      if (this.mc.getNetworkHandler() != null) {
         for (PlayerListEntry val6 : this.mc
            .getNetworkHandler()
            .getPlayerList()) {
            if (val6.getProfile()
                  != null
               && val6.getProfile()
                     .getName()
                  != null) {
               String name5 = val6.getProfile()
                  .getName();
               if (!name5.equals(
                     name
                  )
                  && this.namePattern
                     .matcher(
                        name5
                     )
                     .matches()
                  && !this.activeStaff
                     .contains(
                        name5
                     )) {
                  KeyHelper val7 = GetplayerprivilegeHelper.getEntryPrivilege(
                     val6
                  );
                  boolean flag6 = val7
                        != null
                     && val7.isstaff();
                  boolean flag7 = AspectDLC.INSTANCE
                     .staffStorage
                     .isStaff(
                        name5
                     );
                  boolean flag8 = val6.getGameMode()
                     == GameMode.SPECTATOR;
                  if (flag6
                     || flag7
                     || flag8
                     )
                   {
                     this.activeStaff
                        .add(
                           name5
                        );
                     String var36;
                     if (flag8
                        )
                      {
                        var36 = "GM3";
                     } else {
                        var36 = "ONLINE";
                     }

                     String name6 = var36;
                     StatusHolder val8 = this.staffDataCache
                        .get(
                           name5
                        );
                     if (val8
                        == null) {
                        val8 = new StatusHolder(
                           name6
                        );
                        this.staffDataCache
                           .put(
                              name5,
                              val8
                           );
                     }

                     val8.status = name6;
                     if (val7
                        != null) {
                        val8.segments = new ArrayList<>();
                        val8.segments
                           .add(
                              new TextHolder(
                                 val7.text()
                                    + " ",
                                 val7.col()
                              )
                           );
                     } else if (val6.getScoreboardTeam()
                        != null) {
                        val8.segments = this.parsePrefix(
                           val6.getScoreboardTeam()
                              .getPrefix()
                        );
                     } else {
                        val8.segments = new ArrayList<>();
                     }

                     this.calculateWidths(
                        val8,
                        name5
                     );
                  }
               }
            }
         }
      }

      for (String name7 : AspectDLC.INSTANCE
         .staffStorage
         .getStaffs()) {
         if (!name7.equals(
               name
            )
            && this.namePattern
               .matcher(
                  name7
               )
               .matches()
            && !this.activeStaff
               .contains(
                  name7
               )) {
            this.activeStaff
               .add(
                  name7
               );
            PlayerListEntry val9 = this.mc
                     .getNetworkHandler()
                  != null
               ? this.mc
                  .getNetworkHandler()
                  .getPlayerListEntry(
                     name7
                  )
               : null;
            String var39;
            if (val9
               == null) {
               var39 = "VANISH";
            } else if (val9.getGameMode()
               == GameMode.SPECTATOR) {
               var39 = "GM3";
            } else {
               var39 = "ONLINE";
            }

            String name8 = var39;
            StatusHolder val10 = this.staffDataCache
               .get(
                  name7
               );
            if (val10
               == null) {
               val10 = new StatusHolder(
                  name8
               );
               val10.segments = new ArrayList<>();
               this.calculateWidths(
                  val10,
                  name7
               );
               this.staffDataCache
                  .put(
                     name7,
                     val10
                  );
            } else {
               val10.status = name8;
            }
         }
      }
   }

   private void calculateWidths(
      StatusHolder val,
      String str
   ) {
      val.prefixWidth12 = 0.0F;
      val.prefixWidth14 = 0.0F;

      for (TextHolder text : val.segments) {
         val.prefixWidth12 = val.prefixWidth12
            + text.width12;
         val.prefixWidth14 = val.prefixWidth14
            + text.width14;
      }

      val.nameWidth12 = this.font12
            != null
         ? this.font12
            .getWidth(
               str
            )
         : 0.0F;
      val.nameWidth14 = this.font14
            != null
         ? this.font14
            .getWidth(
               str
                  + " >> "
            )
         : 0.0F;
   }

   protected void updateAnimations() {
      float x = 0.1F;
      this.animationScratch.clear();
      this.animationScratch.addAll(this.staffAnimations.keySet());
      this.animationScratch.addAll(this.activeStaff);

      for (String str : this.animationScratch) {
         boolean flag = this.activeStaff
            .contains(
               str
            );
         float x2 = flag
            ? 1.0F
            : 0.0F;
         float x3 = this.staffAnimations
            .getOrDefault(
               str,
               0.0F
            );
         x3 += (
               x2
                  - x3
            )
            * x;
         this.staffAnimations
            .put(
               str,
               x3
            );
      }

      Iterator<Entry<String, Float>> val = this.staffAnimations.entrySet().iterator();

      while (
         val.hasNext()
      ) {
         Entry<String, Float> val2 = val.next();
         if (val2.getValue()
               < 0.01F
            && !this.activeStaff
               .contains(
                  val2.getKey()
               )) {
            val.remove();
            this.staffDataCache
               .remove(
                  val2.getKey()
               );
         }
      }
   }

   protected List<String> getVisiblePlayers() {
      this.visiblePlayers.clear();

      for (Entry<String, Float> val : this.staffAnimations.entrySet()) {
         if (val.getValue()
            > 0.01F) {
            this.visiblePlayers
               .add(
                  val.getKey()
               );
         }
      }

      Collections.sort(this.visiblePlayers);
      return this.visiblePlayers;
   }

   protected int getStatusColor(String status) {
      if ("AM3".equals(status)) {
         return -47526;
      } else if ("ONLINE".equals(status)) {
         return -9146;
      } else {
         return -10158216;
      }
   }

   private void renderDefaultStyle(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      MatrixStack matrices = event.getContext()
         .getMatrices();
      String var10000 = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName();
      int i;
      if (!var10000.equals("Rainbow")) {
         i = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme()
            .color[0];
      } else {
         i = ColorUtil.getThemeColor();
      }

      List<String> list2 = this.getVisiblePlayers();
      float width3 = 60.0F;
      float width4 = 16.0F;
      float width5 = 12.0F;
      float width6 = 5.0F;

      for (String name : list2) {
         StatusHolder val = this.staffDataCache
            .get(
               name
            );
         if (val
            != null) {
            var10000 = val.status;
            if (var10000.equals("VANISH")) {
               var10000 = "-";
            } else {
               var10000 = val.status;
               if (var10000.equals("GM3")) {
                  var10000 = "±";
               } else {
                  var10000 = "+";
               }
            }

            String name2 = var10000;
            float width7 = this.font12
               .getWidth(
                  name2
               );
            float width8 = width6
               + val.prefixWidth12
               + val.nameWidth12
               + width7
               + width6
               + 20.0F;
            if (width8
               > width3
               )
             {
               width3 = width8;
            }
         }
      }

      this.widthAnimation
         .update(
            width3
         );
      float width9 = this.widthAnimation
         .getValue();
      float width10 = 0.0F;

      for (String name3 : list2) {
         width10 += width5
            * this.staffAnimations
               .getOrDefault(
                  name3,
                  0.0F
               );
      }

      float width11 = list2.isEmpty()
         ? width4
         : width4
            + width10
            + 2.0F;
      this.staffAnimatedHeight = this.staffAnimatedHeight
         + (
               width11
                  - this.staffAnimatedHeight
            )
            * 0.12F;
      float width13 = this.staffAnimatedHeight;
      this.drawHudBackground(
         matrices,
         width,
         width2,
         width9,
         width13,
         i
      );
      Font font = Fonts.getFont(
         "icon", 16
      );
      font.draw(
         matrices,
         "y",
         width
            + 5.0F,
         width2
            + 6.5F,
         i
      );
      Font var44 = this.font14;
      var44.draw(
         matrices,
         "Staff list",
         width
            + 5.0F
            + font.getWidth(
               "y"
            )
            + 3.0F,
         width2
            + 6.0F,
         -1
      );
      float width14 = 18.0F;
      ScissorStack.push();
      ScissorStack.setFromComponentCoordinates(
         (double)width,
         (double)width2,
         (double)width9,
         (double)width13
      );

      for (String name4 : list2) {
         float width15 = this.staffAnimations
            .getOrDefault(
               name4,
               0.0F
            );
         if (!(
            width15
               <= 0.01F
         )) {
            StatusHolder val2 = this.staffDataCache
               .get(
                  name4
               );
            if (val2
               != null) {
               int i2 = (int)(
                  255.0F
                     * width15
               );
               float width16 = -5.0F
                  * (
                     1.0F
                        - width15
                  );
               float width17 = width
                  + width6;

               for (int i3 = 0;
                  i3
                     < val2.segments
                        .size();
                  i3++
               ) {
                  TextHolder text2 = val2.segments
                     .get(
                        i3
                     );
                  int i4 = ColorUtil.setAlphaColor(
                     text2.color,
                     i2
                  );
                  this.font12
                     .draw(
                        matrices,
                        text2.text,
                        width17,
                        width2
                           + width14
                           + 2.0F
                           + width16,
                        i4
                     );
                  width17 += text2.width12;
               }

               this.font12
                  .draw(
                     matrices,
                     name4,
                     width17,
                     width2
                        + width14
                        + 2.0F
                        + width16,
                     ColorUtil.rgba(
                        255,
                        255,
                        255,
                        i2
                     )
                  );
               float width18 = val2.prefixWidth12
                  + val2.nameWidth12;
               float width19 = width
                  + width6
                  + width18
                  + 4.0F;
               var10000 = val2.status;
               if (var10000.equals("VANISH")) {
                  var10000 = "-";
               } else {
                  var10000 = val2.status;
                  if (var10000.equals("GM3")) {
                     var10000 = "±";
                  } else {
                     var10000 = "+";
                  }
               }

               String name5 = var10000;
               int i5 = ColorUtil.setAlphaColor(
                  this.getStatusColor(
                     val2.status
                  ),
                  i2
               );
               this.font12
                  .draw(
                     matrices,
                     name5,
                     width19,
                     width2
                        + width14
                        + 2.0F
                        + width16,
                     i5
                  );
               width14 += width5
                  * width15;
            }
         }
      }

      ScissorStack.pop();
      ScissorStack.unset();
      this.draggable
         .setWidth(
            width9
         );
      this.draggable
         .setHeight(
            width13
         );
   }

   private void renderWaveStyle(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      MatrixStack matrices = event.getContext()
         .getMatrices();
      int i = (int)(
         (float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F
      );
      int i2 = ColorUtil.getThemeColor(
         i
      );
      int i3 = ColorUtil.getThemeColor(
         i
            + 30
      );
      int i4 = ColorUtil.getThemeColor(
         i
            + 90
      );
      int i5 = ColorUtil.getThemeColor(
         i
            + 120
      );
      int i6 = ColorUtil.getThemeColor(
         i
            + 180
      );
      int i7 = ColorUtil.getThemeColor(
         i
            + 210
      );
      List<String> list = this.getVisiblePlayers();
      float width3 = 80.0F;
      float width4 = 18.0F;
      float width5 = 10.0F;
      float width6 = 5.0F;

      for (String name : list) {
         StatusHolder val = this.staffDataCache
            .get(
               name
            );
         if (val
            != null) {
            String var10000 = val.status;
            if (var10000.equals("VANISH")) {
               var10000 = "-";
            } else {
               var10000 = val.status;
               if (var10000.equals("GM3")) {
                  var10000 = "±";
               } else {
                  var10000 = "+";
               }
            }

            String name2 = var10000;
            float width7 = this.font12
               .getWidth(
                  name2
               );
            float width8 = width6
               + val.prefixWidth14
               + val.nameWidth14
               + width7
               + width6;
            if (width8
               > width3
               )
             {
               width3 = width8;
            }
         }
      }

      float width9 = 0.0F;

      for (String name3 : list) {
         width9 += width5
            * this.staffAnimations
               .getOrDefault(
                  name3,
                  0.0F
               );
      }

      float width10 = list.isEmpty()
         ? width4
         : width4
            + width9
            + 4.0F;
      if (list.isEmpty()
         )
       {
         Render2D.drawWaveHudHeader(
            matrices,
            width,
            width2,
            width3,
            15.0F,
            0.0F,
            10.0F,
            10.0F,
            i2,
            i3,
            i4,
            i5,
            i6,
            i7
         );
         Font var56 = this.font16;
         float width11 = width
            + (
                  width3
                     - var56.getWidth("stafflist")
               )
               / 2.0F;
         Font var51 = this.font16;
         var51.drawStringWithShadow(
            matrices,
            "stafflist",
            width11,
            width2
               + 5.0F,
            -1
         );
         this.draggable
            .setWidth(
               width3
            );
         this.draggable
            .setHeight(
               width4
            );
      } else {
         Render2D.drawWaveHudPanel(
            matrices,
            width,
            width2,
            width3,
            width10,
            ColorUtil.rgba(
               25,
               25,
               25,
               150
            ),
            15.0F,
            0.0F,
            10.0F,
            10.0F,
            i2,
            i3,
            i4,
            i5,
            i6,
            i7
         );
         Font var10002 = this.font16;
         float width12 = width
            + (
                  width3
                     - var10002.getWidth("stafflist")
               )
               / 1.9F;
         Font var46 = this.font16;
         var46.drawStringWithShadow(
            matrices,
            "stafflist",
            width12,
            width2
               + 5.0F,
            -1
         );
         float width13 = 20.0F;
         ScissorStack.push();
         ScissorStack.setFromComponentCoordinates(
            (double)width,
            (double)width2,
            (double)width3,
            (double)width10
         );

         for (String name4 : list) {
            float width15 = this.staffAnimations
               .getOrDefault(
                  name4,
                  0.0F
               );
            if (!(
               width15
                  <= 0.01F
            )) {
               StatusHolder val2 = this.staffDataCache
                  .get(
                     name4
                  );
               if (val2
                  != null) {
                  int i8 = (int)(
                     255.0F
                        * width15
                  );
                  float width16 = -5.0F
                     * (
                        1.0F
                           - width15
                     );
                  float width17 = width
                     + width6;

                  for (int i9 = 0;
                     i9
                        < val2.segments
                           .size();
                     i9++
                  ) {
                     TextHolder text2 = val2.segments
                        .get(
                           i9
                        );
                     int i10 = ColorUtil.setAlphaColor(
                        text2.color,
                        i8
                     );
                     this.font14
                        .draw(
                           matrices,
                           text2.text,
                           width17,
                           width2
                              + width13
                              + 1.5F
                              + width16,
                           i10
                        );
                     width17 += text2.width14;
                  }

                  this.font14
                     .draw(
                        matrices,
                        name4,
                        width17,
                        width2
                           + width13
                           + 1.5F
                           + width16,
                        ColorUtil.rgba(
                           255,
                           255,
                           255,
                           i8
                        )
                     );
                  float width18 = val2.prefixWidth14
                     + this.font14
                        .getWidth(
                           name4
                        );
                  String var47 = val2.status;
                  String var48;
                  if (var47.equals("VANISH")) {
                     var48 = "-";
                  } else {
                     String var49 = val2.status;
                     if (var49.equals("GM3")) {
                        var48 = "±";
                     } else {
                        var48 = "+";
                     }
                  }

                  String name5 = var48;
                  float width19 = width
                     + width6
                     + width18
                     + 4.0F;
                  this.font14
                     .draw(
                        matrices,
                        name5,
                        width19,
                        width2
                           + width13
                           + 1.5F
                           + width16,
                        ColorUtil.setAlphaColor(
                           this.getStatusColor(
                              val2.status
                           ),
                           i8
                        )
                     );
                  width13 += width5
                     * width15;
               }
            }
         }

         ScissorStack.pop();
         ScissorStack.unset();
         this.draggable
            .setWidth(
               width3
            );
         this.draggable
            .setHeight(
               width10
            );
      }
   }
}
