package dev.leg1tka.api.utils.render.fonts.msdf;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public final class MsdfGlyph {
   private final int code;
   private final float minU;
   private final float maxU;
   private final float minV;
   private final float maxV;
   private final float advance;
   private final float topPosition;
   private final float width;
   private final float height;

   public MsdfGlyph(
      int unicode,
      float advance,
      float planeLeft,
      float planeTop,
      float planeRight,
      float planeBottom,
      float atlasLeft,
      float atlasTop,
      float atlasRight,
      float atlasBottom,
      float atlasWidth,
      float atlasHeight
   ) {
      this.code = unicode;
      this.advance = advance;
      if (atlasLeft == 0.0F && atlasRight == 0.0F && atlasTop == 0.0F && atlasBottom == 0.0F) {
         this.minU = 0.0F;
         this.maxU = 0.0F;
         this.minV = 0.0F;
         this.maxV = 0.0F;
      } else {
         this.minU = atlasLeft / atlasWidth;
         this.maxU = atlasRight / atlasWidth;
         this.minV = 1.0F - atlasTop / atlasHeight;
         this.maxV = 1.0F - atlasBottom / atlasHeight;
      }

      if (planeLeft == 0.0F && planeRight == 0.0F && planeTop == 0.0F && planeBottom == 0.0F) {
         this.width = 0.0F;
         this.height = 0.0F;
         this.topPosition = 0.0F;
      } else {
         this.width = planeRight - planeLeft;
         this.height = planeTop - planeBottom;
         this.topPosition = planeTop;
      }
   }

   public float apply(Matrix4f matrix, VertexConsumer consumer, float size, float x, float y, float z, int red, int green, int blue, int alpha) {
      y -= this.topPosition * size;
      y--;
      float w = this.width * size;
      float h = this.height * size;
      consumer.vertex(matrix, x, y, z).color(red, green, blue, alpha).texture(this.minU, this.minV);
      consumer.vertex(matrix, x, y + h, z).color(red, green, blue, alpha).texture(this.minU, this.maxV);
      consumer.vertex(matrix, x + w, y + h, z).color(red, green, blue, alpha).texture(this.maxU, this.maxV);
      consumer.vertex(matrix, x + w, y, z).color(red, green, blue, alpha).texture(this.maxU, this.minV);
      return this.advance * size;
   }

   public float getWidth(float size) {
      return this.advance * size;
   }

   public int getCharCode() {
      return this.code;
   }
}
