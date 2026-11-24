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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public class PotionsHud extends HudElement {
   private final Map<StatusEffect, UpdateHelper> animations = new LinkedHashMap<>();
   private final Map<StatusEffect, EntryHolder> snapshots = new HashMap<>();
   private final Map<StatusEffect, Integer> maxDurations = new HashMap<>();
   private final Set<StatusEffect> renderOrderSeen = new HashSet<>();
   private final UpdateHelper widthAnimation = new UpdateHelper(70.0F, 10.5F, EasingFunctions.QUAD_OUT);
   private boolean potionStyleCompact = false;

   public PotionsHud(
      Draggable val
   ) {
      super(
         val
      );
   }

   public boolean isPotionStyleCompact() {
      return this.potionStyleCompact;
   }

   public void setPotionStyleCompact(
      boolean flag
   ) {
      this.potionStyleCompact = flag;
   }

   private Font issue(
      int n
   ) {
      return Fonts.getFont(
         "nunito",
         n
      );
   }

   private Font icon(
      int n
   ) {
      return Fonts.getFont(
         "icon",
         n
      );
   }

   private UpdateHelper getAnimation(
      StatusEffect effect
   ) {
      return this.animations
         .computeIfAbsent(
            effect,
            val -> new UpdateHelper(
                  0.0F, 10.5F, EasingFunctions.QUAD_OUT
               )
         );
   }

   private static String getLevelSuffix(
      int n
   ) {
      return String.valueOf(
         Math.max(
            1,
            n
         )
      );
   }

   private static String formatDuration(
      StatusEffectInstance effect
   ) {
      return formatDuration(
         effect.getDuration(),
         effect.isInfinite()
      );
   }

   private static String formatDuration(
      int n,
      boolean flag
   ) {
      if (flag
         )
       {
         return "inf";
      } else {
         int x = Math.max(
            0,
            n
               / 20
         );
         int x2 = x
            / 60;
         int x3 = x
            % 60;
         return x2
            + ":"
            + (
               x3
                     < 10
                  ? "0"
                     + x3
                  : String.valueOf(
                     x3
                  )
            );
      }
   }

   private void updateSnapshot(
      StatusEffectInstance effect
   ) {
      StatusEffect effect2 = (StatusEffect)effect.getEffectType()
         .value();
      EntryHolder val = this.snapshots
         .computeIfAbsent(
            effect2,
            val2 -> new EntryHolder()
         );
      val.entry = effect.getEffectType();
      val.baseName = I18n.translate(
         effect.getTranslationKey(),
         new Object[0]
      );
      val.amplifier = effect.getAmplifier()
         + 1;
      val.duration = effect.getDuration();
      val.infinite = effect.isInfinite();
   }

   private List<StatusEffect> buildRenderOrder(
      Collection<StatusEffectInstance> effect,
      Set<StatusEffect> effect2
   ) {
      List val = new ArrayList<>();
      this.renderOrderSeen.clear();

      for (StatusEffectInstance effect3 : effect) {
         StatusEffect effect4 = (StatusEffect)effect3.getEffectType()
            .value();
         if (this.renderOrderSeen
            .add(
               effect4
            )) {
            val.add(
               effect4
            );
         }
      }

      for (StatusEffect effect5 : this.animations
         .keySet()) {
         if (!effect2.contains(
            effect5
         )) {
            val.add(
               effect5
            );
         }
      }

      return val;
   }

   private void drawEffectIcon(
      EventRender.Default event,
      RegistryEntry<StatusEffect> effect,
      float f,
      float f2,
      int n,
      int n2
   ) {
      Sprite val = mc.getStatusEffectSpriteManager()
         .getSprite(
            effect
         );
      int color = ColorUtil.rgba(
         255,
         255,
         255,
         n2
      );
      Render2D.drawSprite(
         event.getContext()
            .getMatrices(),
         val,
         f,
         f2,
         (float)n,
         color
      );
   }

   private void drawTextWithShadow(
      EventRender.Default event,
      Font font,
      String str,
      float f,
      float f2,
      int n
   ) {
      int color = ColorUtil.rgba(
         20,
         20,
         20,
         145
      );
      font.draw(
         event.getContext()
            .getMatrices(),
         str,
         f
            + 0.8F,
         f2
            + 0.8F,
         color
      );
      font.draw(
         event.getContext()
            .getMatrices(),
         str,
         f,
         f2,
         n
      );
   }

   private String toRoman(
      int n
   ) {
      String[] var10000 = new String[4];
      var10000[0] = "";
      var10000[1] = "M";
      int var10002 = 2;
      var10000[var10002] = "MM";
      var10002 = 3;
      var10000[var10002] = "MMM";
      String[] val = var10000;
      var10000 = new String[10];
      var10000[0] = "";
      var10000[1] = "C";
      var10002 = 2;
      var10000[var10002] = "CC";
      var10002 = 3;
      var10000[var10002] = "CCC";
      var10002 = 4;
      var10000[var10002] = "CD";
      var10000[5] = "D";
      var10002 = 6;
      var10000[var10002] = "DC";
      var10002 = 7;
      var10000[var10002] = "DCC";
      var10002 = 8;
      var10000[var10002] = "DCCC";
      var10002 = 9;
      var10000[var10002] = "CM";
      String[] val2 = var10000;
      var10000 = new String[10];
      var10000[0] = "";
      var10000[1] = "X";
      var10002 = 2;
      var10000[var10002] = "XX";
      var10002 = 3;
      var10000[var10002] = "XXX";
      var10002 = 4;
      var10000[var10002] = "XL";
      var10000[5] = "L";
      var10002 = 6;
      var10000[var10002] = "LX";
      var10002 = 7;
      var10000[var10002] = "LXX";
      var10002 = 8;
      var10000[var10002] = "LXXX";
      var10002 = 9;
      var10000[var10002] = "XC";
      String[] val3 = var10000;
      var10000 = new String[10];
      var10000[0] = "";
      var10000[1] = "I";
      var10002 = 2;
      var10000[var10002] = "II";
      var10002 = 3;
      var10000[var10002] = "III";
      var10002 = 4;
      var10000[var10002] = "IV";
      var10000[5] = "V";
      var10002 = 6;
      var10000[var10002] = "VI";
      var10002 = 7;
      var10000[var10002] = "VII";
      var10002 = 8;
      var10000[var10002] = "VIII";
      var10002 = 9;
      var10000[var10002] = "IX";
      String[] val4 = var10000;
      return n
               > 0
            && n
               < 4000
         ? val[n
               / 1000]
            + val2[n
               % 1000
               / 100]
            + val3[n
               % 100
               / 10]
            + val4[n
               % 10]
         : String.valueOf(
            n
         );
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      if (this.potionStyleCompact) {
         this.CompactStyle(
            event
         );
      } else {
         this.DefaultStyle(
            event
         );
      }

      super.onRender(
         event
      );
   }

   public void DefaultStyle(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
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

      float width3 = 70.0F;
      float width4 = 16.0F;
      int i2 = 0;
      Collection<StatusEffectInstance> effect = (Collection<StatusEffectInstance>)(IsactiveHelper.isActive()
         ? IsactiveHelper.getEffects()
         : (mc != null && mc.player != null ? mc.player.getStatusEffects() : List.of()));
      Set val = new HashSet<>();

      for (StatusEffectInstance effect2 : effect) {
         StatusEffect effect3 = (StatusEffect)effect2.getEffectType()
            .value();
         val.add(
            effect3
         );
         this.getAnimation(
               effect3
            )
            .update(1.0F);
         this.updateSnapshot(
            effect2
         );
         int i3 = effect2.getDuration();
         Integer val2 = this.maxDurations
            .get(
               effect3
            );
         if (val2
               == null
            || i3
               > val2
            )
          {
            this.maxDurations
               .put(
                  effect3,
                  i3
               );
         }
      }

      for (Entry<StatusEffect, UpdateHelper> val3 : this.animations
         .entrySet()) {
         if (!val.contains(
            val3.getKey()
         )) {
            val3.getValue()
               .update(0.0F);
         }
      }

      List<StatusEffect> effect4 = this.buildRenderOrder(
         effect,
         val
      );

      for (StatusEffect effect5 : effect4) {
         UpdateHelper val4 = this.getAnimation(
            effect5
         );
         float width5 = val4.getValue();
         EntryHolder val5 = this.snapshots
            .get(
               effect5
            );
         if (width5
               > 0.01F
            && val5
               != null) {
            i2++;
            String name = val5.baseName
                  != null
               ? val5.baseName
               : I18n.translate(
                  effect5.getTranslationKey(),
                  new Object[0]
               );
            String name2 = formatDuration(
               val5.duration,
               val5.infinite
            );
            float width6 = this.issue(
                  12
               )
               .getWidth(
                  name
               );
            if (val5.amplifier
               > 1) {
               width6 += this.issue(
                     11
                  )
                  .getWidth(
                     " "
                        + this.toRoman(
                           val5.amplifier
                        )
                  );
            }

            float width7 = this.issue(
                     10
                  )
                  .getWidth(
                     name2
                  )
               + 10.0F;
            float width8 = width6
               + width7
               + 20.0F;
            if (width8
               > width3
               )
             {
               width3 = width8;
            }

            width4 += 12.0F
               * width5;
         }
      }

      if (i2
         > 0) {
         width4 += 2.0F;
      }

      this.widthAnimation
         .update(
            width3
         );
      float width9 = this.widthAnimation
         .getValue();
      float width10 = width4;
      this.drawHudBackground(
         event.getContext()
            .getMatrices(),
         width,
         width2,
         width9,
         width4,
         i
      );
      this.icon(16)
         .draw(
            event.getContext()
               .getMatrices(),
            "p",
            width
               + 5.0F,
            width2
               + 6.5F,
            i
         );
      Font var46 = this.issue(14);
      MatrixStack var47 = event.getContext()
         .getMatrices();
      var46.draw(
         var47,
         "Potions list",
         width
            + 5.0F
            + this.icon(16)
               .getWidth(
                  "d"
               )
            + 3.0F,
         width2
            + 6.0F,
         -1
      );
      float width11 = 18.0F;

      for (StatusEffect effect6 : effect4) {
         UpdateHelper val6 = this.getAnimation(
            effect6
         );
         float width12 = val6.getValue();
         EntryHolder val7 = this.snapshots
            .get(
               effect6
            );
         if (width12
               > 0.01F
            && val7
               != null) {
            ScissorStack.push();
            ScissorStack.setFromComponentCoordinates(
               (double)width,
               (double)width2,
               (double)width9,
               (double)width10
            );
            int i4 = (int)(
               255.0F
                  * width12
            );
            int i5 = ColorUtil.rgba(
               255,
               255,
               255,
               i4
            );
            int i6 = ColorUtil.setAlphaColor(
               i,
               i4
            );
            String name3 = val7.baseName
                  != null
               ? val7.baseName
               : I18n.translate(
                  effect6.getTranslationKey(),
                  new Object[0]
               );
            String name4 = "";
            if (val7.amplifier
               > 1) {
               name4 = " "
                  + this.toRoman(
                     val7.amplifier
                  );
            }

            String name5 = formatDuration(
               val7.duration,
               val7.infinite
            );
            float width13 = width
               + 5.5F;
            float width14 = width2
               + 2.0F
               + width11;
            this.issue(12)
               .draw(
                  event.getContext()
                     .getMatrices(),
                  name3,
                  width13,
                  width14,
                  i5
               );
            if (!name4.isEmpty()
               )
             {
               float width15 = this.issue(
                     12
                  )
                  .getWidth(
                     name3
                  );
               this.issue(11)
                  .draw(
                     event.getContext()
                        .getMatrices(),
                     name4,
                     width13
                        + width15,
                     width14
                        + 0.5F,
                     i6
                  );
            }

            float width16 = width
               + width9
               - this.issue(10)
                  .getWidth(
                     name5
                  )
               - 7.0F;
            this.issue(10)
               .draw(
                  event.getContext()
                     .getMatrices(),
                  name5,
                  width16,
                  width14
                     + 1.0F,
                  i6
               );
            width11 += 12.0F
               * width12;
            ScissorStack.pop();
            ScissorStack.unset();
         }
      }

      this.animations
         .entrySet()
         .removeIf(
            val8 -> !val.contains(
                     val8.getKey()
                  )
                  && val8.getValue()
                        .getValue()
                     <= 0.01F
         );
      this.snapshots
         .keySet()
         .removeIf(
            val9 -> !this.animations
                  .containsKey(
                     val9
                  )
         );
      this.maxDurations
         .keySet()
         .removeIf(
            val10 -> !this.animations
                  .containsKey(
                     val10
                  )
         );
      this.draggable
         .setWidth(
            width9
         );
      this.draggable
         .setHeight(
            width10
         );
   }

   public void CompactStyle(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
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

      float width3 = 70.0F;
      float width4 = 16.0F;
      int i2 = 0;
      Collection<StatusEffectInstance> effect = (Collection<StatusEffectInstance>)(IsactiveHelper.isActive()
         ? IsactiveHelper.getEffects()
         : (mc != null && mc.player != null ? mc.player.getStatusEffects() : List.of()));
      Set val = new HashSet<>();

      for (StatusEffectInstance effect2 : effect) {
         StatusEffect effect3 = (StatusEffect)effect2.getEffectType()
            .value();
         val.add(
            effect3
         );
         this.getAnimation(
               effect3
            )
            .update(1.0F);
         this.updateSnapshot(
            effect2
         );
         int i3 = effect2.getDuration();
         Integer val2 = this.maxDurations
            .get(
               effect3
            );
         if (val2
               == null
            || i3
               > val2
            )
          {
            this.maxDurations
               .put(
                  effect3,
                  i3
               );
         }
      }

      for (Entry<StatusEffect, UpdateHelper> val3 : this.animations
         .entrySet()) {
         if (!val.contains(
            val3.getKey()
         )) {
            val3.getValue()
               .update(0.0F);
         }
      }

      List<StatusEffect> effect4 = this.buildRenderOrder(
         effect,
         val
      );

      for (StatusEffect effect5 : effect4) {
         UpdateHelper val4 = this.getAnimation(
            effect5
         );
         float width5 = val4.getValue();
         EntryHolder val5 = this.snapshots
            .get(
               effect5
            );
         if (width5
               > 0.01F
            && val5
               != null) {
            i2++;
            String name = val5.baseName
                  != null
               ? val5.baseName
               : I18n.translate(
                  effect5.getTranslationKey(),
                  new Object[0]
               );
            String name2 = getLevelSuffix(
               val5.amplifier
            );
            String name3 = formatDuration(
               val5.duration,
               val5.infinite
            );
            float width6 = 16.0F;
            float width7 = this.issue(
                  12
               )
               .getWidth(
                  name
               );
            if (!name2.isEmpty()
               )
             {
               width7 += this.issue(
                     10
                  )
                  .getWidth(
                     " "
                        + name2
                  );
            }

            float width8 = this.issue(
                  10
               )
               .getWidth(
                  name3
               );
            float width9 = width6
               + width7
               + width8
               + 20.0F;
            if (width9
               > width3
               )
             {
               width3 = width9;
            }

            width4 += 20.0F
               * width5;
         }
      }

      if (i2
         > 0) {
         width4 += 2.0F;
      }

      this.widthAnimation
         .update(
            width3
         );
      float width10 = this.widthAnimation
         .getValue();
      float width11 = width4;
      this.drawHudBackground(
         event.getContext()
            .getMatrices(),
         width,
         width2,
         width10,
         width4,
         i
      );
      Font var53 = this.issue(14);
      MatrixStack var54 = event.getContext()
         .getMatrices();
      var53.draw(
         var54,
         "Potions list",
         width
            + 5.0F,
         width2
            + 6.0F,
         -1
      );
      this.icon(13)
         .draw(
            event.getContext()
               .getMatrices(),
            "d",
            width
               + width10
               - 12.5F,
            width2
               + 7.5F,
            i
         );
      float width12 = 18.0F;

      for (StatusEffect effect6 : effect4) {
         UpdateHelper val6 = this.getAnimation(
            effect6
         );
         float width13 = val6.getValue();
         EntryHolder val7 = this.snapshots
            .get(
               effect6
            );
         if (width13
               > 0.01F
            && val7
               != null) {
            ScissorStack.push();
            ScissorStack.setFromComponentCoordinates(
               (double)width,
               (double)width2,
               (double)width10,
               (double)width11
            );
            int i4 = (int)(
               255.0F
                  * width13
            );
            int i5 = ColorUtil.rgba(
               255,
               255,
               255,
               i4
            );
            int i6 = ColorUtil.rgba(
               55,
               55,
               55,
               i4
            );
            int i7 = ColorUtil.rgba(
               35,
               35,
               35,
               i4
            );
            Render2D.drawHudSurface(
               event.getContext()
                  .getMatrices(),
               width
                  + 3.0F,
               width2
                  + width12
                  - 0.9F,
               width10
                  - 6.0F,
               18.0F,
               2.2F,
               ColorUtil.rgba(
                  18,
                  22,
                  30,
                  Math.max(
                     28,
                     i4
                        - 20
                  )
               ),
               i
            );
            float width14 = 16.0F;
            float width15 = width
               + 5.0F;
            float width16 = width2
               + width12
               + 1.0F;
            if (val7.entry
               != null) {
               this.drawEffectIcon(
                  event,
                  val7.entry,
                  width15,
                  width16,
                  (int)width14,
                  i4
               );
            }

            String name4 = val7.baseName
                  != null
               ? val7.baseName
               : I18n.translate(
                  effect6.getTranslationKey(),
                  new Object[0]
               );
            String name5 = getLevelSuffix(
               val7.amplifier
            );
            float width17 = width15
               + width14
               + 4.0F;
            float width18 = width2
               + width12
               + 5.0F;
            this.issue(12)
               .draw(
                  event.getContext()
                     .getMatrices(),
                  name4,
                  width17,
                  width18,
                  i5
               );
            if (!name5.isEmpty()
               )
             {
               float width19 = this.issue(
                     12
                  )
                  .getWidth(
                     name4
                  );
               int i8 = ColorUtil.setAlphaColor(
                  i,
                  i4
               );
               this.issue(10)
                  .draw(
                     event.getContext()
                        .getMatrices(),
                     " "
                        + name5,
                     width17
                        + width19,
                     width18
                        + 1.0F,
                     i8
                  );
            }

            String name6 = formatDuration(
               val7.duration,
               val7.infinite
            );
            float width20 = width
               + width10
               - this.issue(10)
                  .getWidth(
                     name6
                  )
               - 7.0F;
            this.issue(10)
               .draw(
                  event.getContext()
                     .getMatrices(),
                  name6,
                  width20,
                  width18
                     + 1.0F,
                  ColorUtil.setAlphaColor(
                     i,
                     i4
                  )
               );
            width12 += 20.0F
               * width13;
            ScissorStack.pop();
            ScissorStack.unset();
         }
      }

      this.animations
         .entrySet()
         .removeIf(
            val8 -> !val.contains(
                     val8.getKey()
                  )
                  && val8.getValue()
                        .getValue()
                     <= 0.01F
         );
      this.snapshots
         .keySet()
         .removeIf(
            val9 -> !this.animations
                  .containsKey(
                     val9
                  )
         );
      this.maxDurations
         .keySet()
         .removeIf(
            val10 -> !this.animations
                  .containsKey(
                     val10
                  )
         );
      this.draggable
         .setWidth(
            width10
         );
      this.draggable
         .setHeight(
            width11
         );
   }

   public void WaveStyle(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
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
      Collection<StatusEffectInstance> effect = (Collection<StatusEffectInstance>)(IsactiveHelper.isActive()
         ? IsactiveHelper.getEffects()
         : (mc != null && mc.player != null ? mc.player.getStatusEffects() : List.of()));
      Set val = new HashSet<>();

      for (StatusEffectInstance effect2 : effect) {
         StatusEffect effect3 = (StatusEffect)effect2.getEffectType()
            .value();
         val.add(
            effect3
         );
         this.getAnimation(
               effect3
            )
            .update(1.0F);
      }

      for (Entry<StatusEffect, UpdateHelper> val2 : this.animations
         .entrySet()) {
         if (!val.contains(
            val2.getKey()
         )) {
            val2.getValue()
               .update(0.0F);
         }
      }

      float width3 = 84.0F;
      float width4 = 18.0F;
      int i8 = 0;

      for (StatusEffectInstance effect4 : effect) {
         UpdateHelper val3 = this.getAnimation(
            (StatusEffect)effect4.getEffectType()
               .value()
         );
         float width5 = val3.getValue();
         if (!(
            width5
               <= 0.01F
         )) {
            i8++;
            String text = I18n.translate(
               effect4.getTranslationKey(),
               new Object[0]
            );
            String text2 = getLevelSuffix(
               effect4.getAmplifier()
                  + 1
            );
            String text3 = text
               + (
                  text2.isEmpty()
                     ? ""
                     : " > "
                        + text2
               );
            width3 = Math.max(
               width3,
               this.issue(16)
                     .getWidth(
                        text3
                     )
                  + 38.0F
            );
            width3 = Math.max(
               width3,
               this.issue(15)
                     .getWidth(
                        formatDuration(
                           effect4
                        )
                     )
                  + 38.0F
            );
            width4 += 18.0F
               * width5;
         }
      }

      if (i8
         == 0) {
         float width6 = 18.0F;
         Render2D.drawWaveHudHeader(
            event.getContext()
               .getMatrices(),
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
         String text4 = "potions";
         float width7 = width
            + (
                  width3
                     - this.issue(16)
                        .getWidth(
                           text4
                        )
               )
               / 2.0F;
         this.drawTextWithShadow(
            event,
            this.issue(16),
            text4,
            width7,
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
               width6
            );
      } else {
         Render2D.drawWaveHudPanel(
            event.getContext()
               .getMatrices(),
            width,
            width2,
            width3,
            width4,
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
         String text5 = "potions";
         float width8 = width
            + (
                  width3
                     - this.issue(16)
                        .getWidth(
                           text5
                        )
               )
               / 1.9F;
         this.drawTextWithShadow(
            event,
            this.issue(16),
            text5,
            width8,
            width2
               + 5.0F,
            -1
         );
         float width9 = 20.0F;

         for (StatusEffectInstance effect5 : effect) {
            UpdateHelper val4 = this.getAnimation(
               (StatusEffect)effect5.getEffectType()
                  .value()
            );
            float width10 = val4.getValue();
            if (!(
               width10
                  <= 0.01F
            )) {
               ScissorStack.push();
               ScissorStack.setFromComponentCoordinates(
                  (double)width,
                  (double)width2,
                  (double)width3,
                  (double)width4
               );
               int i9 = (int)(
                  255.0F
                     * width10
               );
               int i10 = ColorUtil.rgba(
                  255,
                  255,
                  255,
                  i9
               );
               int i11 = ColorUtil.rgba(
                  20,
                  185,
                  45,
                  i9
               );
               float width11 = width
                  + 5.0F;
               float width12 = width2
                  + width9;
               this.drawEffectIcon(
                  event,
                  effect5.getEffectType(),
                  width11,
                  width12,
                  11,
                  i9
               );
               String text6 = I18n.translate(
                     effect5.getTranslationKey(),
                     new Object[0]
                  )
                  .toLowerCase();
               String text7 = getLevelSuffix(
                  effect5.getAmplifier()
                     + 1
               );
               float width13 = width11
                  + 14.0F;
               this.issue(15)
                  .draw(
                     event.getContext()
                        .getMatrices(),
                     text6
                        + " >",
                     width13,
                     width2
                        + width9
                        - 1.0F,
                     i10
                  );
               if (!text7.isEmpty()
                  )
                {
                  float width14 = this.issue(
                        14
                     )
                     .getWidth(
                        text6
                           + " >"
                     );
                  this.issue(14)
                     .draw(
                        event.getContext()
                           .getMatrices(),
                        " "
                           + text7,
                        (double)(
                           width13
                              + width14
                              + 2.0F
                        ),
                        (double)(
                              width2
                                 + width9
                           )
                           - 0.5,
                        i11
                     );
               }

               this.issue(14)
                  .draw(
                     event.getContext()
                        .getMatrices(),
                     formatDuration(
                        effect5
                     ),
                     (double)width13,
                     (double)(
                           width2
                              + width9
                        )
                        + 7.5,
                     i10
                  );
               width9 += 18.0F
                  * width10;
               ScissorStack.pop();
               ScissorStack.unset();
            }
         }

         this.draggable
            .setWidth(
               width3
            );
         this.draggable
            .setHeight(
               width4
            );
      }
   }
}
