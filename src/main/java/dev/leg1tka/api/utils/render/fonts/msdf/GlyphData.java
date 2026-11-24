package dev.leg1tka.api.utils.render.fonts.msdf;

public class GlyphData {
   private final int unicode;
   private final float advance;
   private final GlyphData.Bounds planeBounds;
   private final GlyphData.Bounds atlasBounds;

   public GlyphData(int unicode, float advance, GlyphData.Bounds planeBounds, GlyphData.Bounds atlasBounds) {
      this.unicode = unicode;
      this.advance = advance;
      this.planeBounds = planeBounds;
      this.atlasBounds = atlasBounds;
   }

   public int getUnicode() {
      return this.unicode;
   }

   public float getAdvance() {
      return this.advance;
   }

   public GlyphData.Bounds getPlaneBounds() {
      return this.planeBounds;
   }

   public GlyphData.Bounds getAtlasBounds() {
      return this.atlasBounds;
   }

   public boolean hasGeometry() {
      return this.planeBounds != null && this.atlasBounds != null;
   }

   public char getChar() {
      return (char)this.unicode;
   }

   @Override
   public String toString() {
      return "GlyphData{unicode="
         + this.unicode
         + " ('"
         + (char)this.unicode
         + "'), advance="
         + this.advance
         + ", planeBounds="
         + this.planeBounds
         + ", atlasBounds="
         + this.atlasBounds
         + "}";
   }

   public static class Bounds {
      private final float left;
      private final float bottom;
      private final float right;
      private final float top;

      public Bounds(float left, float bottom, float right, float top) {
         this.left = left;
         this.bottom = bottom;
         this.right = right;
         this.top = top;
      }

      public float getLeft() {
         return this.left;
      }

      public float getBottom() {
         return this.bottom;
      }

      public float getRight() {
         return this.right;
      }

      public float getTop() {
         return this.top;
      }

      public float getWidth() {
         return this.right - this.left;
      }

      public float getHeight() {
         return this.top - this.bottom;
      }

      @Override
      public String toString() {
         return "Bounds{left="
            + this.left
            + ", bottom="
            + this.bottom
            + ", right="
            + this.right
            + ", top="
            + this.top
            + ", width="
            + this.getWidth()
            + ", height="
            + this.getHeight()
            + "}";
      }
   }
}
