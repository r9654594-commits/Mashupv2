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



import dev.leg1tka.modules.render.*;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;

public class WatermarkHud extends HudElement {
   private static final float STATE_ICON_GAP = 4.0F;
   private static final int STATE_ICON_SIZE = 14;
   private static final String GLYPH_STATE_OFF = "j";
   private static final String GLYPH_STATE_ON = "k";
   private static final int COLOR_STATE_OFF = ColorUtil.rgba(220, 70, 70, 255);
   private static final int COLOR_STATE_ON = ColorUtil.rgba(80, 220, 80, 255);
   private static final float V2_RADIUS = 4.0F;
   private static final float V2_PAD_X = 4.0F;
   private static final float V2_ICON_GAP = 3.0F;
   private static final float V2_HEIGHT = 16.0F;
   private static final int V2_TEXT_SIZE = 13;
   private static final int V2_ICON_SIZE = 14;
   private static final int V2_BG_R = 7;
   private static final int V2_BG_G = 7;
   private static final int V2_BG_B = 9;
   private static final int V2_TEXT_WHITE = ColorUtil.rgba(255, 255, 255, 255);
   private static final float V2_TEXT_BASE_Y = 6.3F;
   private static final float V2_ICON_BASE_Y = 5.8F;
   private final Map<IscustomHelper, UpdateHelper> appearAnimations = new HashMap<>();
   private final Map<IscustomHelper, Float> currentYPositions = new HashMap<>();
   private final Set<IscustomHelper> activeEntriesScratch = new HashSet<>();
   private long lastRenderTime = System.currentTimeMillis();
   private float previewAlpha = 0.0F;
   private float currentMaxTextWidth = 0.0F;

   public WatermarkHud(
      Draggable val
   ) {
      super(
         val
      );
   }

   private Font issue(
      int n
   ) {
      return Fonts.getFont(
         "nunito",
         n
      );
   }

   private Font icons(
      int n
   ) {
      return Fonts.getFont(
         "icon",
         n
      );
   }

   private Font icon1(
      int n
   ) {
      Font font = Fonts.getFont(
         "icon1",
         n
      );
      return font
            != null
         ? font
         : Fonts.getFont(
            "icon",
            n
         );
   }

   private String getEntryText(
      IscustomHelper val
   ) {
      return val.isCustom()
         ? val.customText
         : val.moduleName;
   }

   private String getStateGlyph(
      boolean flag
   ) {
      return flag
         ? "k"
         : "j";
   }

   private int getStateColor(
      boolean flag
   ) {
      return flag
         ? COLOR_STATE_ON
         : COLOR_STATE_OFF;
   }

   private float getStateIconWidth() {
      Font font = this.icon1(
         14
      );
      return font
            == null
         ? 0.0F
         : Math.max(
            font.getWidth(
               "k"
            ),
            font.getWidth(
               "j"
            )
         );
   }

   private String getWaveBodyText(
      IscustomHelper val
   ) {
      return val.isCustom()
         ? val.customText
         : val.moduleName;
   }

   private InterfaceModule iface() {
      return InterfaceModule.INSTANCE;
   }

   private float alphaPercent() {
      return 0.57F;
   }

   private static int clamp255(
      int n
   ) {
      return n
            < 0
         ? 0
         : (
            n
                  > 255
               ? 255
               : n
         );
   }

   private void drawV2Panel(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5
   ) {
      if (this.isBlurEnabled()) {
         BLUR.render(
            matrices,
            f,
            f2,
            f3,
            f4,
            4.0F,
            8.0F
         );
      }

      int color = clamp255(
         (int)(
            255.0F
               * this.alphaPercent()
               * f5
         )
      );
      if (color
         > 0) {
         Render2D.drawRoundedRect(
            matrices,
            f,
            f2,
            f3,
            f4,
            4.0F,
            ColorUtil.rgba(
               7,
               7,
               9,
               color
            )
         );
      }
   }

   private float entryWidth(
      String str,
      boolean flag
   ) {
      float width = this.issue(
            13
         )
         .getWidth(
            str
         );
      float width2 = flag
         ? 14.0F
         : this.getStateIconWidth();
      return 4.0F
         + width2
         + 3.0F
         + width
         + 4.0F;
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      this.DefaultStyle(
         event
      );
      super.onRender(
         event
      );
   }

   private void DefaultStyle(
      EventRender.Default event
   ) {
      if (mc != null) {
         long time = System.currentTimeMillis();
         float width = (float)(
               time
                  - this.lastRenderTime
            )
            / 1000.0F;
         this.lastRenderTime = time;
         List<IscustomHelper> list = PushHelper.getActive();
         boolean flag = mc.currentScreen instanceof ChatScreen;
         boolean flag2 = !list.isEmpty()
            || flag;
         float width2 = flag
            ? 0.7F
            : 0.0F;
         float width3 = 8.0F;
         this.previewAlpha = this.previewAlpha
            + (
                  width2
                     - this.previewAlpha
               )
               * Math.min(
                  1.0F,
                  width3
                     * width
               );
         if (!flag2
            && this.previewAlpha < 0.01F) {
            this.appearAnimations.clear();
            this.currentYPositions.clear();
            this.previewAlpha = 0.0F;
         } else {
            float width4 = this.draggable
               .getX();
            float width5 = this.draggable
               .getY();
            float width6 = 1.0F;
            int i;
            if (!AspectDLC.INSTANCE
               .themeStorage
               .getThemes()
               .getTheme()
               .getName()
               .equals(
                  "Rainbow"
               )) {
               i = AspectDLC.INSTANCE
                  .themeStorage
                  .getThemes()
                  .getTheme()
                  .color[0];
            } else {
               i = ColorUtil.getThemeColor();
            }

            long time2 = System.currentTimeMillis();
            float width7 = 16.0F;
            float width8 = 3.0F;
            float width9 = 12.0F;
            String name = "Кликни на меня для открытия настроек!";
            float width10 = this.entryWidth(
               name,
               false
            );
            float width11 = width10;

            for (IscustomHelper val : list) {
               UpdateHelper val2 = this.appearAnimations
                  .computeIfAbsent(
                     val,
                     val3 -> new UpdateHelper(
                           0.0F, 12.0F, EasingFunctions.QUAD_OUT
                        )
                  );
               long time3 = time2
                  - val.startTime;
               val2.update(
                  1.0F
               );
               float width12 = val2.getValue();
               float width13 = width12;
               if (time3
                  > 2300L) {
                  width13 = (
                        1.0F
                           - (float)(
                                 time3
                                    - 2300L
                              )
                              / 200.0F
                     )
                     * width12;
               }

               if (!(
                  width13
                     <= 0.01F
               )) {
                  String name2 = this.getEntryText(
                     val
                  );
                  String name3 = val.isCustom()
                     ? name2
                     : "\""
                        + name2
                        + "\" был "
                        + (
                           val.enabled
                              ? "включен"
                              : "выключен"
                        );
                  float width14 = this.entryWidth(
                     name3,
                     val.hasItemIcon()
                  );
                  if (width14
                     > width11
                     )
                   {
                     width11 = width14;
                  }
               }
            }

            float width15 = 10.0F;
            this.currentMaxTextWidth = this.currentMaxTextWidth
               + (
                     width11
                        - this.currentMaxTextWidth
                  )
                  * Math.min(
                     1.0F,
                     width15
                        * width
                  );
            if (Math.abs(
                  width11
                     - this.currentMaxTextWidth
               )
               < 0.1F) {
               this.currentMaxTextWidth = width11;
            }

            float width16 = this.currentMaxTextWidth;
            float width17 = width4
               + width16
                  * 0.5F;
            MatrixStack matrices = event.getContext()
               .getMatrices();
            matrices.push();
            matrices.translate(
               width4,
               width5,
               0.0F
            );
            matrices.scale(
               width6,
               width6,
               1.0F
            );
            matrices.translate(
               -width4,
               -width5,
               0.0F
            );
            float width18 = width5;
            if (this.previewAlpha > 0.01F) {
               float width19 = this.previewAlpha;
               float width20 = 0.86F
                  + 0.14F
                     * width19;
               float width21 = this.entryWidth(
                  name,
                  false
               );
               float width22 = width17
                  - width21
                     * 0.5F;
               MatrixStack matrices2 = event.getContext()
                  .getMatrices();
               matrices2.push();
               matrices2.translate(
                  width17,
                  width5
                     + width7
                        * 0.5F,
                  0.0F
               );
               matrices2.scale(
                  width20,
                  width20,
                  1.0F
               );
               matrices2.translate(
                  -width17,
                  -(
                     width5
                        + width7
                           * 0.5F
                  ),
                  0.0F
               );
               this.drawV2Panel(
                  matrices2,
                  width22,
                  width5,
                  width21,
                  width7,
                  width19
               );
               int i2 = ColorUtil.setAlphaColor(
                  V2_TEXT_WHITE,
                  clamp255(
                     (int)(
                        255.0F
                           * width19
                     )
                  )
               );
               int i3 = ColorUtil.setAlphaColor(
                  i,
                  clamp255(
                     (int)(
                        255.0F
                           * width19
                     )
                  )
               );
               Font font = this.icon1(
                  14
               );
               if (font
                  != null) {
                  float width23 = font.getWidth(
                     "k"
                  );
                  float width24 = width22
                     + 4.0F
                     + (
                           this.getStateIconWidth()
                              - width23
                        )
                        * 0.5F;
                  font.drawStringWithShadow(
                     matrices2,
                     "k",
                     width24,
                     width5
                        + 5.8F,
                     i3
                  );
               }

               float width25 = width22
                  + 4.0F
                  + this.getStateIconWidth()
                  + 3.0F;
               this.issue(13)
                  .drawStringWithShadow(
                     matrices2,
                     name,
                     width25,
                     width5
                        + 6.3F,
                     i2
                  );
               matrices2.pop();
               width18 = width5
                  + width7
                  + width8;
            }

            for (IscustomHelper val4 : list) {
               UpdateHelper val5 = this.appearAnimations
                  .computeIfAbsent(
                     val4,
                     val6 -> new UpdateHelper(
                           0.0F, 12.0F, EasingFunctions.QUAD_OUT
                        )
                  );
               long time4 = time2
                  - val4.startTime;
               val5.update(
                  1.0F
               );
               float width26 = val5.getValue();
               float width27 = width26;
               if (time4
                  > 2300L) {
                  width27 = (
                        1.0F
                           - (float)(
                                 time4
                                    - 2300L
                              )
                              / 200.0F
                     )
                     * width26;
               }

               if (width27
                  <= 0.0F) {
                  width18 += width7
                     + width8;
               } else {
                  Float val7 = this.currentYPositions
                     .get(
                        val4
                     );
                  if (val7
                     == null) {
                     val7 = width18;
                  }

                  float width28 = width18
                     - val7;
                  if (Math.abs(
                        width28
                     )
                     > 0.01F) {
                     val7 = val7
                        + width28
                           * Math.min(
                              1.0F,
                              width9
                                 * width
                           );
                  } else {
                     val7 = width18;
                  }

                  this.currentYPositions
                     .put(
                        val4,
                        val7
                     );
                  String name4 = this.getEntryText(
                     val4
                  );
                  String name5 = val4.isCustom()
                     ? name4
                     : "\""
                        + name4
                        + "\" был "
                        + (
                           val4.enabled
                              ? "включен"
                              : "выключен"
                        );
                  float width29 = 6.0F
                     * (
                        1.0F
                           - width26
                     );
                  float width30 = val7
                     + width29;
                  float width31 = 0.86F
                     + 0.14F
                        * width27;
                  float width32 = this.entryWidth(
                     name5,
                     val4.hasItemIcon()
                  );
                  float width33 = width17
                     - width32
                        * 0.5F;
                  MatrixStack matrices3 = event.getContext()
                     .getMatrices();
                  matrices3.push();
                  matrices3.translate(
                     width17,
                     width30
                        + width7
                           * 0.5F,
                     0.0F
                  );
                  matrices3.scale(
                     width31,
                     width31,
                     1.0F
                  );
                  matrices3.translate(
                     -width17,
                     -(
                        width30
                           + width7
                              * 0.5F
                     ),
                     0.0F
                  );
                  this.drawV2Panel(
                     matrices3,
                     width33,
                     width30,
                     width32,
                     width7,
                     width27
                  );
                  int i4 = ColorUtil.setAlphaColor(
                     V2_TEXT_WHITE,
                     clamp255(
                        (int)(
                           255.0F
                              * width27
                        )
                     )
                  );
                  if (val4.hasItemIcon()
                     )
                   {
                     matrices3.push();
                     float width34 = 14.0F;
                     float width35 = width33
                        + 4.0F
                        + (this.getStateIconWidth() - 16.0F) * 0.5F;
                     float width36 = width30
                        + (
                              width7
                                 - 16.0F
                           )
                           * 0.5F;
                     matrices3.translate(
                        width35,
                        width36,
                        0.0F
                     );
                     matrices3.scale(
                        width34
                           / 16.0F,
                        width34
                           / 16.0F,
                        1.0F
                     );
                     event.getContext()
                        .drawItem(
                           val4.itemStack,
                           0,
                           0
                        );
                     matrices3.pop();
                  } else {
                     Font font2 = this.icon1(
                        14
                     );
                     if (font2
                           != null
                        && !val4.isCustom()
                        )
                      {
                        int i5 = ColorUtil.setAlphaColor(
                           this.getStateColor(
                              val4.enabled
                           ),
                           clamp255(
                              (int)(
                                 255.0F
                                    * width27
                              )
                           )
                        );
                        String name6 = this.getStateGlyph(
                           val4.enabled
                        );
                        float width37 = font2.getWidth(
                           name6
                        );
                        float width38 = width33
                           + 4.0F
                           + (
                                 this.getStateIconWidth()
                                    - width37
                              )
                              * 0.5F;
                        font2.drawStringWithShadow(
                           matrices3,
                           name6,
                           width38,
                           width30
                              + 5.8F,
                           i5
                        );
                     }
                  }

                  float width39 = width33
                     + 4.0F
                     + this.getStateIconWidth()
                     + 3.0F;
                  this.issue(13)
                     .drawStringWithShadow(
                        matrices3,
                        name5,
                        width39,
                        width30
                           + 6.3F,
                        i4
                     );
                  matrices3.pop();
                  width18 += width7
                     + width8;
               }
            }

            this.activeEntriesScratch.clear();
            this.activeEntriesScratch
               .addAll(
                  list
               );
            this.appearAnimations
               .keySet()
               .removeIf(
                  val8 -> !this.activeEntriesScratch
                        .contains(
                           val8
                        )
               );
            this.currentYPositions
               .keySet()
               .removeIf(
                  val9 -> !this.activeEntriesScratch
                        .contains(
                           val9
                        )
               );
            matrices.pop();
            this.draggable
               .setWidth(
                  width16
                     * width6
               );
            this.draggable
               .setHeight(
                  Math.max(
                        1.0F,
                        width18
                           - width5
                     )
                     * width6
               );
         }
      }
   }

   private void WaveStyle(
      EventRender.Default event
   ) {
      if (mc != null) {
         long time = System.currentTimeMillis();
         float width = (float)(
               time
                  - this.lastRenderTime
            )
            / 1000.0F;
         this.lastRenderTime = time;
         List<IscustomHelper> list = PushHelper.getActive();
         if (list.isEmpty()
            )
          {
            this.appearAnimations.clear();
            this.currentYPositions.clear();
         } else {
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
            long time2 = System.currentTimeMillis();
            float width2 = 4.0F;
            float width3 = 14.0F;
            float width4 = (float)mc.getWindow()
               .getScaledWidth();
            float width5 = (float)mc.getWindow()
               .getScaledHeight();
            float width6 = 5.0F;
            float width7 = 5.0F;
            float width8 = 0.0F;
            float width9 = 120.0F;

            for (IscustomHelper val : list) {
               String text = "Notify";
               String text2 = this.getWaveBodyText(
                  val
               );
               String text3 = val.isCustom()
                  ? "j"
                  : (
                     val.categoryIcon
                              != null
                           && !val.categoryIcon
                              .isEmpty()
                        ? val.categoryIcon
                        : "?"
                  );
               float width10 = val.isCustom()
                  ? this.icon1(14)
                     .getWidth(
                        text3
                     )
                  : this.icons(14)
                     .getWidth(
                        text3
                     );
               float width11 = this.issue(
                     15
                  )
                  .getWidth(
                     text
                  );
               float width12 = this.issue(
                     13
                  )
                  .getWidth(
                     text2
                  );
               float width13 = val.isCustom()
                  ? 0.0F
                  : this.getStateIconWidth() + 4.0F;
               float width14 = Math.max(
                  120.0F,
                  Math.max(
                     width11
                        + width10
                        + 18.0F,
                     width12
                        + width13
                        + 14.0F
                  )
               );
               width9 = Math.max(
                  width9,
                  width14
               );
            }

            for (IscustomHelper val2 : list) {
               UpdateHelper val3 = this.appearAnimations
                  .computeIfAbsent(
                     val2,
                     val4 -> new UpdateHelper(
                           0.0F, 12.0F, EasingFunctions.QUAD_OUT
                        )
                  );
               val3.update(
                  1.0F
               );
               float width15 = val3.getValue();
               long time3 = time2
                  - val2.startTime;
               float width16 = width15;
               if (time3
                  > 2300L) {
                  width16 = (
                        1.0F
                           - (float)(
                                 time3
                                    - 2300L
                              )
                              / 200.0F
                     )
                     * width15;
               }

               if (!(
                  width16
                     <= 0.01F
               )) {
                  String text4 = "Notify";
                  String text5 = this.getWaveBodyText(
                     val2
                  );
                  String text6 = val2.isCustom()
                     ? "j"
                     : (
                        val2.categoryIcon
                                 != null
                              && !val2.categoryIcon
                                 .isEmpty()
                           ? val2.categoryIcon
                           : "?"
                     );
                  float width17 = val2.isCustom()
                     ? this.icon1(14)
                        .getWidth(
                           text6
                        )
                     : this.icons(14)
                        .getWidth(
                           text6
                        );
                  float width18 = this.issue(
                        15
                     )
                     .getWidth(
                        text4
                     );
                  float width19 = this.issue(
                        13
                     )
                     .getWidth(
                        text5
                     );
                  float width20 = val2.isCustom()
                     ? 0.0F
                     : this.getStateIconWidth() + 4.0F;
                  float width21 = Math.max(
                     120.0F,
                     Math.max(
                        width18
                           + width17
                           + 18.0F,
                        width19
                           + width20
                           + 14.0F
                     )
                  );
                  float width22 = 24.0F;
                  float width23 = width4
                     - width21
                     - width6;
                  float width24 = width5
                     - width7
                     - width22
                     - width8;
                  Float val5 = this.currentYPositions
                     .get(
                        val2
                     );
                  if (val5
                     == null) {
                     val5 = width24;
                  }

                  val5 = val5
                     + (
                           width24
                              - val5
                        )
                        * Math.min(
                           1.0F,
                           width3
                              * width
                        );
                  this.currentYPositions
                     .put(
                        val2,
                        val5
                     );
                  float width25 = val5;
                  float width26 = 0.86F
                     + 0.14F
                        * width16;
                  int i8 = ColorUtil.rgba(
                     25,
                     25,
                     25,
                     (int)(
                        150.0F
                           * width16
                     )
                  );
                  int i9 = ColorUtil.setAlphaColor(
                     -1,
                     (int)(
                        255.0F
                           * width16
                     )
                  );
                  int i10 = val2.isCustom()
                     ? ColorUtil.setAlphaColor(
                        ColorUtil.rgba(
                           235,
                           0,
                           0,
                           255
                        ),
                        (int)(
                           255.0F
                              * width16
                        )
                     )
                     : ColorUtil.setAlphaColor(
                        AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getColor(0),
                        (int)(
                           255.0F
                              * width16
                        )
                     );
                  float width27 = width23
                     + width21
                        * 0.5F;
                  float width28 = width25
                     + width22
                        * 0.5F;
                  MatrixStack matrices = event.getContext()
                     .getMatrices();
                  matrices.push();
                  matrices.translate(
                     width27,
                     width28,
                     0.0F
                  );
                  matrices.scale(
                     width26,
                     width26,
                     1.0F
                  );
                  matrices.translate(
                     -width27,
                     -width28,
                     0.0F
                  );
                  Render2D.drawWaveHudPanel(
                     matrices,
                     width23,
                     width25,
                     width21,
                     width22
                        - 1.5F,
                     i8,
                     3.5F,
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
                  if (val2.isCustom()
                     )
                   {
                     this.icon1(28)
                        .draw(
                           matrices,
                           text6,
                           width23
                              + 3.0F,
                           width25
                              + 8.0F,
                           i10
                        );
                  } else {
                     this.icons(28)
                        .draw(
                           matrices,
                           text6,
                           width23
                              + 3.0F,
                           width25
                              + 8.0F,
                           i10
                        );
                  }

                  this.issue(15)
                     .draw(
                        matrices,
                        text4,
                        width23
                           + 19.0F,
                        width25
                           + 6.5F,
                        i9
                     );
                  float width29 = width23
                     + 19.0F;
                  this.issue(13)
                     .draw(
                        matrices,
                        text5,
                        width29,
                        width25
                           + 15.0F,
                        i9
                     );
                  if (!val2.isCustom()
                     )
                   {
                     Font font = this.icon1(
                        14
                     );
                     if (font
                        != null) {
                        float width30 = width29
                           + width19
                           + 4.0F;
                        int i11 = ColorUtil.setAlphaColor(
                           this.getStateColor(
                              val2.enabled
                           ),
                           (int)(
                              255.0F
                                 * width16
                           )
                        );
                        font.draw(
                           matrices,
                           this.getStateGlyph(
                              val2.enabled
                           ),
                           width30,
                           width25
                              + 15.2F,
                           i11
                        );
                     }
                  }

                  matrices.pop();
                  width8 += (
                        width22
                           + width2
                     )
                     * width15;
               }
            }

            this.appearAnimations
               .keySet()
               .removeIf(
                  val6 -> !list.contains(
                        val6
                     )
               );
            this.currentYPositions
               .keySet()
               .removeIf(
                  val7 -> !list.contains(
                        val7
                     )
               );
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
         }
      }
   }
}
