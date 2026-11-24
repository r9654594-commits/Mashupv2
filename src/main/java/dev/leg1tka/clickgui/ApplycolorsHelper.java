package dev.leg1tka.clickgui;
import dev.leg1tka.animation.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ApplycolorsHelper implements IMinecraft {
   private float x;
   private float y;
   private float width = 110.0F;
   private float height = 250.0F;
   private boolean open;
   private final Animation openAnim = new Animation(Easing.QUINTIC_OUT, 400L);
   private final Animation scrollAnim = new Animation(Easing.CUBIC_IN_OUT, 200L);
   private final Animation pickerAnim = new Animation(Easing.QUINTIC_OUT, 250L);
   private final Animation presetScrollAnim = new Animation(Easing.CUBIC_IN_OUT, 180L);
   private float scroll;
   private float maxScroll;
   private float presetScroll;
   private float presetMaxScroll;
   private final HudEditorScreen parent;
   private static final File THEME_DIR = new File("aspectdlc");
   private static final File THEME_FILE = new File(THEME_DIR, "themes.json");
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   public static final String[] COLOR_NAMES = new String[]{
      "Основной",
      "Визуальные модули",
      "Текст",
      "Неактивный текст",
      "Цвет заголовков",
      "Текст заголовков",
      "Слайдер",
      "Круг слайдера",
      "Окно слайдера",
      "Индикатор",
      "Неактивный индикатор",
      "Кнопка",
      "Неактивная кнопка",
      "Разделитель",
      "Поле",
      "Неактивное поле",
      "Текст в подсказках",
      "Фон окон",
      "Цвет иконок",
      "Цвет клиента"
   };
   public static final int COLOR_COUNT = COLOR_NAMES.length;
   private static final int[][] PRESETS = new int[][]{
      {-5618433, -15921904},
      {-12276993, -16119272},
      {-12255318, -16115696},
      {-48060, -15071224},
      {-26300, -15069176},
      {-12255250, -16246248},
      {-47958, -15071216},
      {-7846657, -15726054},
      {-8892, -15067128},
      {-12255420, -16246264},
      {-12285697, -16250342}
   };
   private final List<NameHolder> savedThemes = new ArrayList<>();
   private int[] currentColors;
   private int editingSlot = -1;
   private final float[] hsv = new float[3];
   private boolean draggingSV;
   private boolean draggingH;
   private float pickerPopupX;
   private float pickerPopupY;
   private String themeName = "";
   private boolean nameFieldFocused = false;
   private static final float HEADER_H = 20.0F;
   private static final float ROW_H = 16.0F;
   private static final float PRESET_D = 9.0F;
   private static final float PRESET_GAP = 1.5F;
   private static final float PRESET_ROW_H = 13.0F;
   private static final float FIELD_H = 16.0F;
   private static final float BTN_H = 16.0F;
   private static final float FIXED_BOTTOM_H = 58.0F;
   private static final float SV_SIZE = 60.0F;
   private static final float HUE_W = 6.0F;
   private static final float PICKER_PAD = 4.0F;
   private static final float PICKER_W = 70.0F;
   private static final float PICKER_H_TOT = 60.0F;

   public ApplycolorsHelper(
      HudEditorScreen val
   ) {
      this.parent = val;
      this.currentColors = buildDefaultColors(
         -5618433,
         -15921904
      );
      this.load();
   }

   static int[] buildDefaultColors(
      int n,
      int n2
   ) {
      int[] colors = new int[COLOR_COUNT];
      colors[0] = n2;
      colors[1] = n;
      colors[2] = -1513240;
      colors[3] = -7829351;
      colors[4] = -16119283;
      colors[5] = n;
      colors[6] = n;
      colors[7] = -1;
      colors[8] = -14013899;
      colors[9] = n;
      colors[10] = -14013899;
      colors[11] = n;
      colors[12] = -14013899;
      colors[13] = -14013899;
      colors[14] = -15066590;
      colors[15] = n2;
      colors[16] = -1;
      colors[17] = n2;
      colors[18] = n;
      colors[19] = n;
      return colors;
   }

   private void applyColors() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null) {
         Theme val = AspectDLC.INSTANCE
            .themeStorage
            .getThemes();
         if (val
               != null
            && val
               == Theme.Custom) {
            ThemeColors color = val.getTheme();
            int[] colors = this.currentColors;
            color.setAllColors(
               colors[1],
               colors[0],
               colors[0],
               colors[1],
               colors[2],
               colors[3],
               colors[4],
               colors[5],
               colors[6],
               colors[7],
               colors[8],
               colors[9],
               colors[10],
               colors[11],
               colors[12],
               colors[13],
               colors[14],
               colors[15],
               colors[16],
               colors[17],
               colors[18],
               colors[19]
            );
         }
      }
   }

   public void save() {
      try {
         if (!THEME_DIR.exists()) {
            THEME_DIR.mkdirs();
         }

         JsonObject val = new JsonObject();
         JsonArray val2 = new JsonArray();

         for (int i : this.currentColors) {
            val2.add(
               i
            );
         }

         val.add(
            "colors",
            val2
         );
         JsonArray val3 = new JsonArray();

         for (NameHolder val4 : this.savedThemes) {
            JsonObject val5 = new JsonObject();
            val5.addProperty(
               "name",
               val4.name
            );
            JsonArray val6 = new JsonArray();

            for (int i2 : val4.colors) {
               val6.add(
                  i2
               );
            }

            val5.add(
               "colors",
               val6
            );
            val3.add(
               val5
            );
         }

         val.add(
            "savedThemes",
            val3
         );
         Files.writeString(
            THEME_FILE.toPath(),
            GSON.toJson(
               val
            )
         );
      } catch (IOException var12) {
         var12.printStackTrace();
      }
   }

   public void load() {
      if (THEME_FILE.exists()) {
         try {
            JsonObject val = JsonParser.parseString(
                  Files.readString(THEME_FILE.toPath())
               )
               .getAsJsonObject();
            if (val.has(
               "colors"
            )) {
               JsonArray val2 = val.getAsJsonArray(
                  "colors"
               );

               for (int i = 0;
                  i
                     < Math.min(
                        val2.size(),
                        COLOR_COUNT
                     );
                  i++
               ) {
                  this.currentColors[i] = val2.get(
                        i
                     )
                     .getAsInt();
               }
            }

            if (val.has(
               "savedThemes"
            )) {
               this.savedThemes.clear();
               for (JsonElement val3 : val.getAsJsonArray(
                  "savedThemes"
               )) {
                  JsonObject val4 = val3.getAsJsonObject();
                  String name2 = val4.get(
                        "name"
                     )
                     .getAsString();
                  JsonArray val5 = val4.getAsJsonArray(
                     "colors"
                  );
                  int[] colors2 = new int[COLOR_COUNT];

                  for (int i2 = 0;
                     i2
                        < Math.min(
                           val5.size(),
                           COLOR_COUNT
                        );
                     i2++
                  ) {
                     colors2[i2] = val5.get(
                           i2
                        )
                        .getAsInt();
                  }

                  this.savedThemes
                     .add(
                        new NameHolder(
                           name2,
                           colors2
                        )
                     );
               }
            }
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }
   }

   private float scrollAreaH() {
      return this.height - 20.0F - 58.0F;
   }

   private float scrollContentH() {
      return (float)COLOR_COUNT * 16.0F + 2.0F;
   }

   private void clampScroll() {
      this.maxScroll = Math.max(0.0F, this.scrollContentH() - this.scrollAreaH());
      if (this.maxScroll > 0.0F) {
         this.scroll = MathHelper.clamp(this.scroll, -this.maxScroll, 0.0F);
      } else {
         this.scroll = 0.0F;
      }
   }

   private int allPresetsCount() {
      return PRESETS.length + this.savedThemes.size();
   }

   private void clampPresetScroll() {
      float width2 = this.width
         - 8.0F;
      float width3 = (float)this.allPresetsCount()
            * 9.0F
         + (float)(this.allPresetsCount() - 1) * 1.5F;
      this.presetMaxScroll = Math.max(
         0.0F,
         width3
            - width2
      );
      if (this.presetMaxScroll > 0.0F) {
         this.presetScroll = MathHelper.clamp(this.presetScroll, -this.presetMaxScroll, 0.0F);
      } else {
         this.presetScroll = 0.0F;
      }
   }

   public void render(
      MatrixStack matrices,
      int n,
      int n2,
      float f
   ) {
      this.openAnim.run(this.open);
      if (!(this.openAnim.getValue() < 0.01F)) {
         float width2 = this.openAnim
            .getValue();
         float width3 = this.x
            - this.width
               * (
                  1.0F
                     - width2
               );
         int i = (int)(
            255.0F
               * width2
         );
         Render2D.drawBlur(
            matrices,
            width3,
            this.y,
            this.width,
            this.height,
            8.0F,
            20.0F,
            ColorUtil.rgba(
               75,
               75,
               75,
               i
            )
         );
         Render2D.drawRoundedRect(
            matrices,
            width3,
            this.y,
            this.width,
            this.height,
            8.0F,
            ColorUtil.setAlphaColor(
               ColorUtil.getColorWindowBg(),
               (int)(
                  30.0F
                     * width2
               )
            )
         );
         Render2D.drawRoundedRect(
            matrices,
            width3,
            this.y,
            this.width,
            20.0F,
            8.0F,
            8.0F,
            0.0F,
            0.0F,
            ColorUtil.rgba(
               0,
               0,
               0,
               (int)(
                  80.0F
                     * width2
               )
            )
         );
         Font font = Fonts.getFont(
            "sf_regular", 17
         );
         String name = "Theme Editor";
         if (font
            != null) {
            float width4 = font.getStringWidth(
               name
            );
            GuiComponent.drawText(
               matrices,
               font,
               name,
               width3
                  + this.width / 2.0F
                  - width4
                     / 2.0F,
               this.y + 6.0F,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  i
               )
            );
         }

         this.clampScroll();
         this.clampPresetScroll();
         this.scrollAnim.run(this.scroll);
         this.presetScrollAnim.run(this.presetScroll);
         float width5 = this.y
            + 20.0F;
         float width6 = this.scrollAreaH();
         ScissorStack.push();
         ScissorStack.setFromComponentCoordinates(
            (double)width3,
            (double)width5,
            (double)this.width,
            (double)width6
         );
         float width7 = this.scrollAnim
            .getValue();
         float width8 = width5
            + 2.0F
            + width7;

         for (int i2 = 0;
            i2
               < COLOR_COUNT;
            i2++
         ) {
            this.renderRow(
               matrices,
               i2,
               width3
                  + 3.5F,
               width8,
               this.width - 7.0F,
               n,
               n2,
               width2
            );
            width8 += 16.0F;
         }

         ScissorStack.pop();
         float width9 = this.y
            + this.height
            - 58.0F;
         Render2D.drawRoundedRect(
            matrices,
            width3
               + 4.0F,
            width9,
            this.width - 8.0F,
            0.5F,
            0.0F,
            ColorUtil.rgba(
               255,
               255,
               255,
               (int)(
                  25.0F
                     * width2
               )
            )
         );
         width9 += 3.0F;
         float width10 = width3
            + 4.0F;
         float width11 = this.width
            - 8.0F;
         float width12 = this.presetScrollAnim
            .getValue();
         ScissorStack.push();
         ScissorStack.setFromComponentCoordinates(
            (double)width10,
            (double)width9,
            (double)width11,
            13.0
         );
         float width13 = width10
            + width12;
         float width14 = width9
            + 2.0F;

         for (int[] colors2 : PRESETS) {
            boolean flag = HoverUtil.isHovered(
               (double)n,
               (double)n2,
               (double)width13,
               (double)width14,
               9.0,
               9.0
            );
            if (flag
               )
             {
               CursorManager.requestHand();
            }

            Render2D.drawRoundedRect(
               matrices,
               width13,
               width14,
               9.0F,
               9.0F,
               4.0F,
               ColorUtil.setAlphaColor(
                  colors2[0],
                  i
               )
            );
            width13 += 10.5F;
         }

         for (NameHolder val : this.savedThemes) {
            boolean flag2 = HoverUtil.isHovered(
               (double)n,
               (double)n2,
               (double)width13,
               (double)width14,
               9.0,
               9.0
            );
            if (flag2
               )
             {
               CursorManager.requestHand();
            }

            Render2D.drawRoundedRect(
               matrices,
               width13,
               width14,
               9.0F,
               9.0F,
               4.0F,
               ColorUtil.setAlphaColor(
                  val.colors[1],
                  i
               )
            );
            width13 += 10.5F;
         }

         ScissorStack.pop();
         width9 += 15.0F;
         Render2D.drawRoundedRect(
            matrices,
            width3
               + 4.0F,
            width9,
            this.width - 8.0F,
            0.5F,
            0.0F,
            ColorUtil.rgba(
               255,
               255,
               255,
               (int)(
                  25.0F
                     * width2
               )
            )
         );
         width9 += 3.0F;
         float width15 = width3
            + 4.0F;
         float width16 = this.width
            - 8.0F;
         int i3 = ColorUtil.setAlphaColor(
            this.currentColors[1],
            (int)(
               55.0F
                  * width2
            )
         );
         int i4 = ColorUtil.setAlphaColor(
            this.currentColors[1],
            (int)(
               90.0F
                  * width2
            )
         );
         Render2D.drawRoundedRect(
            matrices,
            width15,
            width9,
            width16,
            16.0F,
            3.0F,
            i3
         );
         Font font2 = Fonts.getFont(
            "sf_regular", 14
         );
         String var47;
         if (this.themeName.isEmpty()) {
            var47 = "Название";
         } else {
            var47 = this.themeName;
         }

         String name2 = var47;
         int i5 = this.themeName
               .isEmpty()
            ? ColorUtil.rgba(
               200,
               200,
               200,
               (int)(
                  160.0F
                     * width2
               )
            )
            : ColorUtil.rgba(
               255,
               255,
               255,
               (int)(
                  255.0F
                     * width2
               )
            );
         GuiComponent.drawText(
            matrices,
            font2,
            name2,
            width15
               + 4.0F,
            width9
               + 4.0F,
            i5
         );
         width9 += 18.0F;
         boolean flag3 = HoverUtil.isHovered(
            (double)n,
            (double)n2,
            (double)width15,
            (double)width9,
            (double)width16,
            16.0
         );
         if (flag3
            )
          {
            CursorManager.requestHand();
         }

         Render2D.drawRoundedRect(
            matrices,
            width15,
            width9,
            width16,
            16.0F,
            3.0F,
            flag3
               ? i4
               : i3
         );
         String name3 = "Создать тему";
         if (font2
            != null) {
            float width17 = font2.getStringWidth(
               name3
            );
            GuiComponent.drawText(
               matrices,
               font2,
               name3,
               width15
                  + width16
                     / 2.0F
                  - width17
                     / 2.0F,
               width9
                  + 4.0F,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  (int)(
                     255.0F
                        * width2
                  )
               )
            );
         }

         this.pickerAnim.run(this.editingSlot >= 0);
         if (this.pickerAnim.getValue() > 0.01F) {
            this.renderPickerPopup(
               matrices,
               this.pickerPopupX,
               this.pickerPopupY,
               n,
               n2,
               this.pickerAnim.getValue()
                  * width2
            );
         }
      }
   }

   private void renderRow(
      MatrixStack matrices,
      int n,
      float f,
      float f2,
      float f3,
      int n2,
      int n3,
      float f4
   ) {
      boolean flag = HoverUtil.isHovered(
         (double)n2,
         (double)n3,
         (double)f,
         (double)f2,
         (double)f3,
         16.0
      );
      boolean flag2 = this.editingSlot
         == n;
      if (flag
         || flag2
         )
       {
         CursorManager.requestHand();
      }

      if (flag2
         )
       {
         Render2D.drawRoundedRect(
            matrices,
            f,
            f2,
            f3,
            16.0F,
            2.0F,
            ColorUtil.rgba(
               60,
               60,
               80,
               (int)(
                  60.0F
                     * f4
               )
            )
         );
      }

      Font font = Fonts.getFont(
         "sf_regular", 14
      );
      GuiComponent.drawText(
         matrices,
         font,
         COLOR_NAMES[n],
         f
            + 3.0F,
         f2
            + 4.0F,
         ColorUtil.rgba(
            210,
            210,
            210,
            (int)(
               255.0F
                  * f4
            )
         )
      );
      float alpha = 8.0F;
      Render2D.drawRoundedRect(
         matrices,
         f
            + f3
            - alpha
            - 3.0F,
         f2
            + 8.0F
            - alpha
               / 2.0F,
         alpha,
         alpha,
         alpha
               / 2.0F
            - 1.0F,
         ColorUtil.setAlphaColor(
            this.currentColors[n],
            (int)(
               255.0F
                  * f4
            )
         )
      );
   }

   private void renderPickerPopup(
      MatrixStack matrices,
      float f,
      float f2,
      int n,
      int n2,
      float f3
   ) {
      int i = (int)(
         255.0F
            * f3
      );
      float alpha = 4.0F;
      Render2D.drawBlur(
         matrices,
         f
            - alpha,
         f2
            - alpha,
         70.0F
            + alpha
               * 2.0F,
         60.0F
            + alpha
               * 2.0F,
         5.0F,
         20.0F,
         ColorUtil.rgba(
            75,
            75,
            75,
            (int)(
               255.0F
                  * f3
            )
         )
      );
      Render2D.drawRoundedRect(
         matrices,
         f
            - alpha,
         f2
            - alpha,
         70.0F
            + alpha
               * 2.0F,
         60.0F
            + alpha
               * 2.0F,
         5.0F,
         ColorUtil.rgba(
            14,
            14,
            16,
            (int)(
               130.0F
                  * f3
            )
         )
      );
      int i2 = ColorUtil.setAlphaColor(
         Color.HSBtoRGB(this.hsv[0], 1.0F, 1.0F),
         i
      );
      int i3 = ColorUtil.rgba(
         255,
         255,
         255,
         i
      );
      int i4 = ColorUtil.rgba(
         255,
         255,
         255,
         0
      );
      int i5 = ColorUtil.rgba(
         0,
         0,
         0,
         i
      );
      int i6 = ColorUtil.rgba(
         0, 0, 0, 0
      );
      Render2D.drawRoundedRect(
         matrices,
         f,
         f2,
         60.0F,
         60.0F,
         2.0F,
         i2
      );
      Render2D.drawGradientRect(
         matrices,
         f,
         f2,
         60.0F,
         60.0F,
         2.0F,
         i3,
         i4,
         i3,
         i4
      );
      Render2D.drawGradientRect(
         matrices,
         f,
         f2,
         60.0F,
         60.0F,
         2.0F,
         i6,
         i6,
         i5,
         i5
      );
      float alpha2 = f
         + this.hsv[1] * 60.0F;
      float alpha3 = f2
         + (1.0F - this.hsv[2]) * 60.0F;
      Render2D.drawRoundedRect(
         matrices,
         alpha2
            - 3.0F,
         alpha3
            - 3.0F,
         6.0F,
         6.0F,
         3.0F,
         ColorUtil.rgba(
            0,
            0,
            0,
            (int)(
               180.0F
                  * f3
            )
         )
      );
      Render2D.drawRoundedRect(
         matrices,
         alpha2
            - 2.0F,
         alpha3
            - 2.0F,
         4.0F,
         4.0F,
         2.0F,
         ColorUtil.rgba(
            255,
            255,
            255,
            i
         )
      );
      float alpha4 = f
         + 60.0F
         + 4.0F;

      for (float alpha5 = 0.0F;
         alpha5
            <= 60.0F;
         alpha5 += 0.5F
      ) {
         Render2D.drawRoundedRect(
            matrices,
            alpha4,
            f2
               + alpha5,
            6.0F,
            1.0F,
            0.0F,
            ColorUtil.setAlphaColor(
               Color.HSBtoRGB(
                  alpha5
                     / 60.0F,
                  1.0F,
                  1.0F
               ),
               i
            )
         );
      }

      float alpha6 = f2
         + this.hsv[0] * 60.0F;
      Render2D.drawRoundedRect(
         matrices,
         alpha4
            - 1.5F,
         alpha6
            - 2.5F,
         9.0F,
         5.0F,
         2.0F,
         ColorUtil.rgba(
            0,
            0,
            0,
            (int)(
               180.0F
                  * f3
            )
         )
      );
      Render2D.drawRoundedRect(
         matrices,
         alpha4
            - 0.5F,
         alpha6
            - 1.5F,
         7.0F,
         3.0F,
         1.0F,
         ColorUtil.rgba(
            255,
            255,
            255,
            i
         )
      );
      if (this.draggingSV) {
         this.hsv[1] = Math.max(
            0.0F,
            Math.min(
               1.0F,
               (
                     (float)n
                        - f
                  )
                  / 60.0F
            )
         );
         this.hsv[2] = 1.0F
            - Math.max(
               0.0F,
               Math.min(
                  1.0F,
                  (
                        (float)n2
                           - f2
                     )
                     / 60.0F
               )
            );
         this.applyHSV();
      } else if (this.draggingH) {
         this.hsv[0] = Math.max(
            0.0F,
            Math.min(
               1.0F,
               (
                     (float)n2
                        - f2
                  )
                  / 60.0F
            )
         );
         this.applyHSV();
      }
   }

   private void applyHSV() {
      if (this.editingSlot >= 0) {
         this.currentColors[this.editingSlot] = Color.HSBtoRGB(
               this.hsv[0], this.hsv[1], this.hsv[2]
            )
            | -16777216;
         if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null) {
            AspectDLC.INSTANCE.themeStorage.setThemes(Theme.Custom);
         }

         this.applyColors();
      }
   }

   private void loadHSV(
      int n
   ) {
      Color color = new Color(
         this.currentColors[n],
         true
      );
      Color.RGBtoHSB(
         color.getRed(),
         color.getGreen(),
         color.getBlue(),
         this.hsv
      );
   }

   public void mouseClicked(
      double d,
      double d2,
      int n
   ) {
      if (this.open && !(this.openAnim.getValue() < 0.9F)) {
         float width2 = this.openAnim
            .getValue();
         float width3 = this.x
            - this.width
               * (
                  1.0F
                     - width2
               );
         if (this.editingSlot >= 0 && this.pickerAnim.getValue() > 0.5F) {
            float width4 = this.pickerPopupX;
            float width5 = this.pickerPopupY;
            if (HoverUtil.isHovered(
                  d,
                  d2,
                  (double)width4,
                  (double)width5,
                  60.0,
                  60.0
               )
               && n
                  == 0) {
               this.draggingSV = true;
               return;
            }

            if (HoverUtil.isHovered(
                  d,
                  d2,
                  (double)(
                     width4
                        + 60.0F
                        + 4.0F
                  ),
                  (double)width5,
                  10.0,
                  60.0
               )
               && n
                  == 0) {
               this.draggingH = true;
               return;
            }

            if (!HoverUtil.isHovered(
               d,
               d2,
               (double)(
                  width4
                     - 4.0F
               ),
               (double)(
                  width5
                     - 4.0F
               ),
               78.0,
               68.0
            )) {
               this.editingSlot = -1;
            }
         }

         float width6 = this.y
            + 20.0F;
         float width7 = this.scrollAreaH();
         if (HoverUtil.isHovered(
            d,
            d2,
            (double)width3,
            (double)width6,
            (double)this.width,
            (double)width7
         )) {
            float width8 = this.scrollAnim
               .getValue();
            float width9 = width6
               + 2.0F
               + width8;

            for (int i = 0;
               i
                  < COLOR_COUNT;
               i++
            ) {
               float width10 = width3
                  + 3.5F;
               float width11 = this.width
                  - 7.0F;
               if (HoverUtil.isHovered(
                     d,
                     d2,
                     (double)width10,
                     (double)width9,
                     (double)width11,
                     16.0
                  )
                  && n
                     == 0) {
                  this.editingSlot = this.editingSlot
                        == i
                     ? -1
                     : i;
                  if (this.editingSlot >= 0) {
                     this.loadHSV(
                        i
                     );
                     this.pickerPopupX = width3
                        + this.width
                        + 4.0F;
                     this.pickerPopupY = width9
                        + 8.0F
                        - 30.0F;
                  }

                  return;
               }

               width9 += 16.0F;
            }
         }

         float width12 = this.y
            + this.height
            - 58.0F
            + 1.0F
            + 2.0F;
         float width13 = width3
            + 4.0F;
         float width14 = this.width
            - 8.0F;
         float width15 = this.presetScrollAnim
            .getValue();
         float width16 = width13
            + width15;
         float width17 = width12
            + 2.0F;

         for (int[] colors2 : PRESETS) {
            if (HoverUtil.isHovered(
                  d,
                  d2,
                  (double)width16,
                  (double)width17,
                  9.0,
                  9.0
               )
               && width16
                  >= width13
               && width16
                     + 9.0F
                  <= width13
                     + width14
               && n
                  == 0) {
               this.currentColors = buildDefaultColors(
                  colors2[0],
                  colors2[1]
               );
               if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null) {
                  AspectDLC.INSTANCE.themeStorage.setThemes(Theme.Custom);
               }

               this.applyColors();
               this.save();
               return;
            }

            width16 += 10.5F;
         }

         for (NameHolder val : this.savedThemes) {
            if (HoverUtil.isHovered(
                  d,
                  d2,
                  (double)width16,
                  (double)width17,
                  9.0,
                  9.0
               )
               && width16
                  >= width13
               && width16
                     + 9.0F
                  <= width13
                     + width14
               && n
                  == 0) {
               this.currentColors = (int[])val.colors
                  .clone();
               if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null) {
                  AspectDLC.INSTANCE.themeStorage.setThemes(Theme.Custom);
               }

               this.applyColors();
               this.save();
               return;
            }

            width16 += 10.5F;
         }

         width12 += 18.0F;
         float width18 = width3
            + 4.0F;
         float width19 = this.width
            - 8.0F;
         this.nameFieldFocused = HoverUtil.isHovered(
               d,
               d2,
               (double)width18,
               (double)width12,
               (double)width19,
               16.0
            )
            && n
               == 0;
         width12 += 18.0F;
         if (HoverUtil.isHovered(
               d,
               d2,
               (double)width18,
               (double)width12,
               (double)width19,
               16.0
            )
            && n
               == 0) {
            String name = this.themeName
                  .trim()
                  .isEmpty()
               ? "Theme " + (this.savedThemes.size() + 1)
               : this.themeName.trim();
            this.savedThemes
               .add(
                  new NameHolder(
                     name,
                     (int[])this.currentColors.clone()
                  )
               );
            this.save();
            this.themeName = "";
            this.nameFieldFocused = false;
         }
      }
   }

   public void mouseReleased(
      double d,
      double d2,
      int n
   ) {
      if (this.draggingSV || this.draggingH) {
         this.save();
      }

      this.draggingSV = false;
      this.draggingH = false;
   }

   public void mouseScrolled(
      double d,
      double d2,
      double d3,
      double d4
   ) {
      if (this.open) {
         float width2 = this.openAnim
            .getValue();
         float width3 = this.x
            - this.width
               * (
                  1.0F
                     - width2
               );
         float width4 = this.y
            + this.height
            - 58.0F
            + 1.0F
            + 2.0F;
         if (HoverUtil.isHovered(
            d,
            d2,
            (double)(
               width3
                  + 4.0F
            ),
            (double)width4,
            (double)(this.width - 8.0F),
            13.0
         )) {
            this.presetScroll += (float)(
               d4
                  * 12.0
            );
            this.clampPresetScroll();
         } else {
            if (HoverUtil.isHovered(
               d,
               d2,
               (double)width3,
               (double)(this.y + 20.0F),
               (double)this.width,
               (double)this.scrollAreaH()
            )) {
               this.scroll += (float)(
                  d4
                     * 20.0
               );
               this.clampScroll();
            }
         }
      }
   }

   public void charTyped(
      char val
   ) {
      if (this.nameFieldFocused) {
         if (val
               >= 32
            && this.themeName.length() < 24) {
            this.themeName = this.themeName
               + val;
         }
      }
   }

   public void keyPressed(
      int n
   ) {
      if (this.nameFieldFocused) {
         if (n
               == 259
            && !this.themeName.isEmpty()) {
            this.themeName = this.themeName.substring(0, this.themeName.length() - 1);
         }

         if (n
            == 256) {
            this.nameFieldFocused = false;
         }
      }
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public boolean isOpen() {
      return this.open;
   }

   @Generated
   public Animation getOpenAnim() {
      return this.openAnim;
   }

   @Generated
   public Animation getScrollAnim() {
      return this.scrollAnim;
   }

   @Generated
   public Animation getPickerAnim() {
      return this.pickerAnim;
   }

   @Generated
   public Animation getPresetScrollAnim() {
      return this.presetScrollAnim;
   }

   @Generated
   public float getScroll() {
      return this.scroll;
   }

   @Generated
   public float getMaxScroll() {
      return this.maxScroll;
   }

   @Generated
   public float getPresetScroll() {
      return this.presetScroll;
   }

   @Generated
   public float getPresetMaxScroll() {
      return this.presetMaxScroll;
   }

   @Generated
   public HudEditorScreen getParent() {
      return this.parent;
   }

   @Generated
   public List<NameHolder> getSavedThemes() {
      return this.savedThemes;
   }

   @Generated
   public int[] getCurrentColors() {
      return this.currentColors;
   }

   @Generated
   public int getEditingSlot() {
      return this.editingSlot;
   }

   @Generated
   public float[] getHsv() {
      return this.hsv;
   }

   @Generated
   public boolean isDraggingSV() {
      return this.draggingSV;
   }

   @Generated
   public boolean isDraggingH() {
      return this.draggingH;
   }

   @Generated
   public float getPickerPopupX() {
      return this.pickerPopupX;
   }

   @Generated
   public float getPickerPopupY() {
      return this.pickerPopupY;
   }

   @Generated
   public String getThemeName() {
      return this.themeName;
   }

   @Generated
   public boolean isNameFieldFocused() {
      return this.nameFieldFocused;
   }

   @Generated
   public void setX(
      float f
   ) {
      this.x = f;
   }

   @Generated
   public void setY(
      float f
   ) {
      this.y = f;
   }

   @Generated
   public void setWidth(
      float f
   ) {
      this.width = f;
   }

   @Generated
   public void setHeight(
      float f
   ) {
      this.height = f;
   }

   @Generated
   public void setOpen(
      boolean flag
   ) {
      this.open = flag;
   }

   @Generated
   public void setScroll(
      float f
   ) {
      this.scroll = f;
   }

   @Generated
   public void setMaxScroll(
      float f
   ) {
      this.maxScroll = f;
   }

   @Generated
   public void setPresetScroll(
      float f
   ) {
      this.presetScroll = f;
   }

   @Generated
   public void setPresetMaxScroll(
      float f
   ) {
      this.presetMaxScroll = f;
   }

   @Generated
   public void setCurrentColors(
      int[] array
   ) {
      this.currentColors = array;
   }

   @Generated
   public void setEditingSlot(
      int n
   ) {
      this.editingSlot = n;
   }

   @Generated
   public void setDraggingSV(
      boolean flag
   ) {
      this.draggingSV = flag;
   }

   @Generated
   public void setDraggingH(
      boolean flag
   ) {
      this.draggingH = flag;
   }

   @Generated
   public void setPickerPopupX(
      float f
   ) {
      this.pickerPopupX = f;
   }

   @Generated
   public void setPickerPopupY(
      float f
   ) {
      this.pickerPopupY = f;
   }

   @Generated
   public void setThemeName(
      String str
   ) {
      this.themeName = str;
   }

   @Generated
   public void setNameFieldFocused(
      boolean flag
   ) {
      this.nameFieldFocused = flag;
   }
}
