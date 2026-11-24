package dev.leg1tka.api.utils.render.fonts.msdf;

import java.util.HashMap;
import net.minecraft.client.util.math.MatrixStack;

public class Fonts {
   private static final HashMap<String, MsdfFont> loaded = new HashMap<>();
   private static final HashMap<String, Font[]> cache = new HashMap<>();
   private static boolean init = false;

   public static void init() {
      init = false;
      loaded.clear();
      cache.clear();
      init = true;
      load("sf_regular");
      load("semibold");
      load("bold");
      load("roundbold");
      load("medium");
      load("regular");
      load("wave");
      load("icon");
      load("icon1");
      load("iconnew");
      load("suisse");
      load("iconsminced");
      load("divine");
      load("energy");
      load("nunito");
   }

   private static void load(String name) {
      try {
         MsdfFont font = MsdfFont.builder().name(name).atlas(name).data(name).build();
         loaded.put(name, font);
         Font[] arr = new Font[100];

         for (int i = 8; i < 100; i++) {
            arr[i] = new Font(font, (float)i);
         }

         cache.put(name, arr);
      } catch (Exception var4) {
         System.err.println("[Fonts] Failed to load " + name + ": " + var4.getMessage());
      }
   }

   public static Font getFont(String name, int size) {
      if (!init) {
         init();
      }

      String clean = name.replace(".ttf", "");
      if (clean.equals("nunito")) {
         size += 2;
      }

      if (size < 8) {
         size = 8;
      }

      if (size >= 100) {
         size = 99;
      }

      Font[] arr = cache.get(clean);
      if (arr != null && arr[size] != null) {
         return arr[size];
      } else {
         if (!loaded.containsKey(clean)) {
            load(clean);
         }

         arr = cache.get(clean);
         return arr != null && arr[size] != null ? arr[size] : null;
      }
   }

   public static void drawStringWithFade(Font font, String text, float x, float y, float max, int color) {
      if (font != null) {
         MatrixStack stack = new MatrixStack();
         font.drawStringWithFade(stack, text, x, y, max, color);
      }
   }
}
