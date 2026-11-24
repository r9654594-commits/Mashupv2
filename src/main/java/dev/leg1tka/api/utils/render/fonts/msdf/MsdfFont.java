package dev.leg1tka.api.utils.render.fonts.msdf;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.leg1tka.render.ClientAssets;
import dev.leg1tka.core.IMinecraft;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public final class MsdfFont implements IMinecraft {
   private final String name;
   private final AbstractTexture texture;
   private final float atlasw;
   private final float atlash;
   private final float range;
   private final float lineh;
   private final float asc;
   private final float desc;
   private final HashMap<Integer, MsdfGlyph> glyphs;
   private boolean filtered = false;

   private MsdfFont(
      String name, AbstractTexture texture, float atlasw, float atlash, float range, float lineh, float asc, float desc, HashMap<Integer, MsdfGlyph> glyphs
   ) {
      this.name = name;
      this.texture = texture;
      this.atlasw = atlasw;
      this.atlash = atlash;
      this.range = range;
      this.lineh = lineh;
      this.asc = asc;
      this.desc = desc;
      this.glyphs = glyphs;
   }

   public void setFiltered() {
      if (!this.filtered) {
         this.texture.setFilter(true, false);
         this.filtered = true;
      }
   }

   public int getTextureId() {
      return this.texture.getGlId();
   }

   public float getAtlasWidth() {
      return this.atlasw;
   }

   public float getAtlasHeight() {
      return this.atlash;
   }

   public float getRange() {
      return this.range;
   }

   public float getLineHeight() {
      return this.lineh;
   }

   public float getBaselineHeight() {
      return this.lineh + this.desc;
   }

   public String getName() {
      return this.name;
   }

   public void applyGlyphs(
      Matrix4f matrix, VertexConsumer consumer, float size, String text, float thickness, float x, float y, float z, int r, int g, int b, int a
   ) {
      text = replaceSymbols(text);

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c == 167 && i + 1 < text.length()) {
            i++;
         } else {
            MsdfGlyph glyph = this.glyphs.get(Integer.valueOf(c));
            if (glyph != null) {
               x += glyph.apply(matrix, consumer, size, x, y, z, r, g, b, a) + thickness;
            }
         }
      }
   }

   public float getWidth(String text, float size) {
      text = replaceSymbols(text);
      float w = 0.0F;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c == 167 && i + 1 < text.length()) {
            i++;
         } else {
            MsdfGlyph glyph = this.glyphs.get(Integer.valueOf(c));
            if (glyph != null) {
               w += glyph.getWidth(size);
            }
         }
      }

      return w;
   }

   private static String replaceSymbols(String text) {
      return text == null
         ? ""
         : text.replace("ᴀ", "A")
            .replace("ʙ", "B")
            .replace("ᴄ", "C")
            .replace("ᴅ", "D")
            .replace("ᴇ", "E")
            .replace("ғ", "F")
            .replace("ɢ", "G")
            .replace("ʜ", "H")
            .replace("ɪ", "I")
            .replace("ᴊ", "J")
            .replace("ᴋ", "K")
            .replace("ʟ", "L")
            .replace("ᴍ", "M")
            .replace("ɴ", "N")
            .replace("ᴏ", "O")
            .replace("ᴘ", "P")
            .replace("ǫ", "Q")
            .replace("ʀ", "R")
            .replace("ꜱ", "S")
            .replace("ᴛ", "T")
            .replace("ᴜ", "U")
            .replace("ᴠ", "V")
            .replace("ᴡ", "W")
            .replace("ʏ", "Y")
            .replace("ᴢ", "Z")
            .replace("ꜰ", "F");
   }

   private static String readResource(Identifier id) {
      try {
         InputStream is = mc.getResourceManager().open(id);
         BufferedReader rd = new BufferedReader(new InputStreamReader(is));
         String res = rd.lines().collect(Collectors.joining("\n"));
         rd.close();
         is.close();
         return res;
      } catch (Exception var4) {
         throw new RuntimeException("Failed to read resource: " + id, var4);
      }
   }

   public static MsdfFont.Builder builder() {
      return new MsdfFont.Builder();
   }

   public static class Builder {
      private String name = "?";
      private Identifier dataid;
      private Identifier atlasid;

      public MsdfFont.Builder name(String name) {
         this.name = name;
         return this;
      }

      public MsdfFont.Builder data(String name) {
         Identifier id1 = Identifier.of(ClientAssets.assets(), "fonts/msdf/" + name + "/font.json");
         Identifier id2 = Identifier.of(ClientAssets.assets(), "fonts/msdf/" + name + ".json");
         this.dataid = IMinecraft.mc.getResourceManager().getResource(id1).isPresent() ? id1 : id2;
         return this;
      }

      public MsdfFont.Builder atlas(String name) {
         Identifier id1 = Identifier.of(ClientAssets.assets(), "fonts/msdf/" + name + "/font.png");
         Identifier id2 = Identifier.of(ClientAssets.assets(), "fonts/msdf/" + name + ".png");
         this.atlasid = IMinecraft.mc.getResourceManager().getResource(id1).isPresent() ? id1 : id2;
         return this;
      }

      public MsdfFont build() {
         String json = MsdfFont.readResource(this.dataid);
         JsonObject root = JsonParser.parseString(json).getAsJsonObject();
         JsonObject atlasobj = root.getAsJsonObject("atlas");
         float atlasw = atlasobj.get("width").getAsFloat();
         float atlash = atlasobj.get("height").getAsFloat();
         float range = atlasobj.get("distanceRange").getAsFloat();
         JsonObject metricsobj = root.getAsJsonObject("metrics");
         float lineh = metricsobj.get("lineHeight").getAsFloat();
         float asc = metricsobj.get("ascender").getAsFloat();
         float desc = metricsobj.get("descender").getAsFloat();
         HashMap glyphs = new HashMap<>();

         for (JsonElement el : root.getAsJsonArray("glyphs")) {
            JsonObject obj = el.getAsJsonObject();
            int unicode = obj.get("unicode").getAsInt();
            float adv = obj.get("advance").getAsFloat();
            float pl = 0.0F;
            float pt = 0.0F;
            float pr = 0.0F;
            float pb = 0.0F;
            if (obj.has("planeBounds") && !obj.get("planeBounds").isJsonNull()) {
               JsonObject plane = obj.getAsJsonObject("planeBounds");
               pl = plane.get("left").getAsFloat();
               pt = plane.get("top").getAsFloat();
               pr = plane.get("right").getAsFloat();
               pb = plane.get("bottom").getAsFloat();
            }

            float al = 0.0F;
            float at = 0.0F;
            float ar = 0.0F;
            float ab = 0.0F;
            if (obj.has("atlasBounds") && !obj.get("atlasBounds").isJsonNull()) {
               JsonObject atlas = obj.getAsJsonObject("atlasBounds");
               al = atlas.get("left").getAsFloat();
               at = atlas.get("top").getAsFloat();
               ar = atlas.get("right").getAsFloat();
               ab = atlas.get("bottom").getAsFloat();
            }

            MsdfGlyph glyph = new MsdfGlyph(unicode, adv, pl, pt, pr, pb, al, at, ar, ab, atlasw, atlash);
            glyphs.put(unicode, glyph);
         }

         AbstractTexture tex = IMinecraft.mc.getTextureManager().getTexture(this.atlasid);
         return new MsdfFont(this.name, tex, atlasw, atlash, range, lineh, asc, desc, glyphs);
      }
   }
}
