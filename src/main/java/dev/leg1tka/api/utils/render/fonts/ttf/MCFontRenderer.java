package dev.leg1tka.api.utils.render.fonts.ttf;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.awt.Font;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public class MCFontRenderer extends CFont {
   private final int[] colorCode = new int[32];
   protected CFont.CharData[] boldChars = new CFont.CharData[1104];
   protected CFont.CharData[] italicChars = new CFont.CharData[1104];
   protected CFont.CharData[] boldItalicChars = new CFont.CharData[1104];
   protected int texBold;
   protected int texItalic;
   protected int texItalicBold;

   public MCFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
      super(font, antiAlias, fractionalMetrics);
      this.setupBoldItalicIDs();

      for (int index = 0; index < 32; index++) {
         int noClue = (index >> 3 & 1) * 85;
         int red = (index >> 2 & 1) * 170 + noClue;
         int green = (index >> 1 & 1) * 170 + noClue;
         int blue = (index & 1) * 170 + noClue;
         if (index == 6) {
            red += 85;
         }

         if (index >= 16) {
            red /= 4;
            green /= 4;
            blue /= 4;
         }

         this.colorCode[index] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
      }
   }

   public float drawStringWithShadow(String text, double x, double y, int color) {
      float shadowWidth = this.drawString(text, x + 0.5, y + 0.5, color, true);
      return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
   }

   public float drawGradientString(String text, float x, float y, int topColor, int bottomColor) {
      if (text == null) {
         return 0.0F;
      } else {
         x--;
         if ((topColor & -67108864) == 0) {
            topColor |= -16777216;
         }

         if ((bottomColor & -67108864) == 0) {
            bottomColor |= -16777216;
         }

         float topAlpha = (float)(topColor >> 24 & 0xFF) / 255.0F;
         float topRed = (float)(topColor >> 16 & 0xFF) / 255.0F;
         float topGreen = (float)(topColor >> 8 & 0xFF) / 255.0F;
         float topBlue = (float)(topColor & 0xFF) / 255.0F;
         float botAlpha = (float)(bottomColor >> 24 & 0xFF) / 255.0F;
         float botRed = (float)(bottomColor >> 16 & 0xFF) / 255.0F;
         float botGreen = (float)(bottomColor >> 8 & 0xFF) / 255.0F;
         float botBlue = (float)(bottomColor & 0xFF) / 255.0F;
         double posX = (double)x * 2.0;
         double posY = ((double)y - 3.0) * 2.0;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         Matrix4f matrix = new Matrix4f();
         matrix.scale(0.5F, 0.5F, 0.5F);
         CFont.CharData[] currentData = this.charData;
         int size = text.length();

         for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if (character < currentData.length && currentData[character] != null) {
               RenderSystem.setShaderTexture(0, this.glTextureId);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
               Tessellator tessellator = Tessellator.getInstance();
               BufferBuilder buffer = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               CFont.CharData cd = currentData[character];
               float charXPos = (float)cd.storedX;
               float charYPos = (float)cd.storedY;
               float width = (float)cd.width;
               float height = (float)cd.height;
               float u0 = charXPos / 512.0F;
               float v0 = charYPos / 512.0F;
               float u1 = (charXPos + width) / 512.0F;
               float v1 = (charYPos + height) / 512.0F;
               buffer.vertex(matrix, (float)posX, (float)posY, 0.0F).texture(u0, v0).color(topRed, topGreen, topBlue, topAlpha);
               buffer.vertex(matrix, (float)posX, (float)posY + height, 0.0F).texture(u0, v1).color(botRed, botGreen, botBlue, botAlpha);
               buffer.vertex(matrix, (float)posX + width, (float)posY + height, 0.0F)
                  .texture(u1, v1)
                  .color(botRed, botGreen, botBlue, botAlpha);
               buffer.vertex(matrix, (float)posX + width, (float)posY, 0.0F).texture(u1, v0).color(topRed, topGreen, topBlue, topAlpha);
               BufferRenderer.drawWithGlobalProgram(buffer.end());
               posX += (double)(cd.width - 8 + this.charOffset);
            } else if (character == ' ' || character == 160) {
               posX += 8.0;
            }
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         return (float)posX / 2.0F;
      }
   }

   public float drawGradientStringHorizontal(String text, float x, float y, int leftColor, int rightColor) {
      if (text == null) {
         return 0.0F;
      } else {
         x--;
         if ((leftColor & -67108864) == 0) {
            leftColor |= -16777216;
         }

         if ((rightColor & -67108864) == 0) {
            rightColor |= -16777216;
         }

         double posX = (double)x * 2.0;
         double posY = ((double)y - 3.0) * 2.0;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         Matrix4f matrix = new Matrix4f();
         matrix.scale(0.5F, 0.5F, 0.5F);
         CFont.CharData[] currentData = this.charData;
         int size = text.length();
         float totalWidth = (float)this.getStringWidth(text) * 2.0F;
         float currentWidth = 0.0F;

         for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if (character < currentData.length && currentData[character] != null) {
               RenderSystem.setShaderTexture(0, this.glTextureId);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
               Tessellator tessellator = Tessellator.getInstance();
               BufferBuilder buffer = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               CFont.CharData cd = currentData[character];
               float charXPos = (float)cd.storedX;
               float charYPos = (float)cd.storedY;
               float width = (float)cd.width;
               float height = (float)cd.height;
               float charWidth = (float)(cd.width - 8 + this.charOffset);
               float u0 = charXPos / 512.0F;
               float v0 = charYPos / 512.0F;
               float u1 = (charXPos + width) / 512.0F;
               float v1 = (charYPos + height) / 512.0F;
               float firstMix = totalWidth <= 0.0F ? 0.0F : currentWidth / totalWidth;
               float lastMix = totalWidth <= 0.0F ? 1.0F : (currentWidth + charWidth) / totalWidth;
               int firstColor = this.colorMix(leftColor, rightColor, firstMix);
               int lastColor = this.colorMix(leftColor, rightColor, lastMix);
               float firstAlpha = (float)(firstColor >> 24 & 0xFF) / 255.0F;
               float firstRed = (float)(firstColor >> 16 & 0xFF) / 255.0F;
               float firstGreen = (float)(firstColor >> 8 & 0xFF) / 255.0F;
               float firstBlue = (float)(firstColor & 0xFF) / 255.0F;
               float lastAlpha = (float)(lastColor >> 24 & 0xFF) / 255.0F;
               float lastRed = (float)(lastColor >> 16 & 0xFF) / 255.0F;
               float lastGreen = (float)(lastColor >> 8 & 0xFF) / 255.0F;
               float lastBlue = (float)(lastColor & 0xFF) / 255.0F;
               buffer.vertex(matrix, (float)posX, (float)posY, 0.0F).texture(u0, v0).color(firstRed, firstGreen, firstBlue, firstAlpha);
               buffer.vertex(matrix, (float)posX, (float)posY + height, 0.0F)
                  .texture(u0, v1)
                  .color(firstRed, firstGreen, firstBlue, firstAlpha);
               buffer.vertex(matrix, (float)posX + width, (float)posY + height, 0.0F)
                  .texture(u1, v1)
                  .color(lastRed, lastGreen, lastBlue, lastAlpha);
               buffer.vertex(matrix, (float)posX + width, (float)posY, 0.0F).texture(u1, v0).color(lastRed, lastGreen, lastBlue, lastAlpha);
               BufferRenderer.drawWithGlobalProgram(buffer.end());
               posX += (double)charWidth;
               currentWidth += charWidth;
            } else if (character == ' ' || character == 160) {
               posX += 8.0;
               currentWidth += 8.0F;
            }
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         return (float)posX / 2.0F;
      }
   }

   private int colorMix(int startColor, int endColor, float mix) {
      float startAlpha = (float)(startColor >> 24 & 0xFF) / 255.0F;
      float startRed = (float)(startColor >> 16 & 0xFF) / 255.0F;
      float startGreen = (float)(startColor >> 8 & 0xFF) / 255.0F;
      float startBlue = (float)(startColor & 0xFF) / 255.0F;
      float endAlpha = (float)(endColor >> 24 & 0xFF) / 255.0F;
      float endRed = (float)(endColor >> 16 & 0xFF) / 255.0F;
      float endGreen = (float)(endColor >> 8 & 0xFF) / 255.0F;
      float endBlue = (float)(endColor & 0xFF) / 255.0F;
      int mixAlpha = (int)(((1.0F - mix) * startAlpha + mix * endAlpha) * 255.0F);
      int mixRed = (int)(((1.0F - mix) * startRed + mix * endRed) * 255.0F);
      int mixGreen = (int)(((1.0F - mix) * startGreen + mix * endGreen) * 255.0F);
      int mixBlue = (int)(((1.0F - mix) * startBlue + mix * endBlue) * 255.0F);
      return mixAlpha << 24 | mixRed << 16 | mixGreen << 8 | mixBlue;
   }

   public float drawString(String text, float x, float y, int color) {
      return this.drawString(text, (double)x, (double)y, color, false);
   }

   public float drawCenteredString(String text, float x, float y, int color) {
      return this.drawString(text, x - (float)this.getStringWidth(text) / 2.0F, y, color);
   }

   public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
      return this.drawStringWithShadow(text, (double)(x - (float)this.getStringWidth(text) / 2.0F), (double)y, color);
   }

   public float drawString(String text, double x, double y, int color, boolean shadow) {
      x--;
      if (text == null) {
         return 0.0F;
      } else {
         if (color == 553648127) {
            color = 16777215;
         }

         if ((color & -67108864) == 0) {
            color |= -16777216;
         }

         if (shadow) {
            color = (color & 16579836) >> 2 | color & new Color(20, 20, 20, 200).getRGB();
         }

         CFont.CharData[] currentData = this.charData;
         float alpha = (float)(color >> 24 & 0xFF) / 255.0F;
         boolean bold = false;
         boolean italic = false;
         boolean strikethrough = false;
         boolean underline = false;
         x *= 2.0;
         y = (y - 3.0) * 2.0;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor((float)(color >> 16 & 0xFF) / 255.0F, (float)(color >> 8 & 0xFF) / 255.0F, (float)(color & 0xFF) / 255.0F, alpha);
         Matrix4f matrix = new Matrix4f();
         matrix.scale(0.5F, 0.5F, 0.5F);
         int size = text.length();
         int currentTexture = this.glTextureId;
         RenderSystem.setShaderTexture(0, currentTexture);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);

         for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if (String.valueOf(character).equals("§") && i < size - 1) {
               int colorIndex = 21;

               try {
                  colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
               } catch (Exception var21) {
                  var21.printStackTrace();
               }

               if (colorIndex >= 16) {
                  if (colorIndex == 17) {
                     bold = true;
                     if (italic) {
                        currentTexture = this.texItalicBold;
                        currentData = this.boldItalicChars;
                     } else {
                        currentTexture = this.texBold;
                        currentData = this.boldChars;
                     }
                  } else if (colorIndex == 18) {
                     strikethrough = true;
                  } else if (colorIndex == 19) {
                     underline = true;
                  } else if (colorIndex == 20) {
                     italic = true;
                     if (bold) {
                        currentTexture = this.texItalicBold;
                        currentData = this.boldItalicChars;
                     } else {
                        currentTexture = this.texItalic;
                        currentData = this.italicChars;
                     }
                  } else if (colorIndex == 21) {
                     bold = false;
                     italic = false;
                     underline = false;
                     strikethrough = false;
                     RenderSystem.setShaderColor(
                        (float)(color >> 16 & 0xFF) / 255.0F, (float)(color >> 8 & 0xFF) / 255.0F, (float)(color & 0xFF) / 255.0F, alpha
                     );
                     currentTexture = this.glTextureId;
                     currentData = this.charData;
                  }
               } else {
                  bold = false;
                  italic = false;
                  underline = false;
                  strikethrough = false;
                  currentTexture = this.glTextureId;
                  currentData = this.charData;
                  if (colorIndex < 0 || colorIndex > 15) {
                     colorIndex = 15;
                  }

                  if (shadow) {
                     colorIndex += 16;
                  }

                  int colorcode = this.colorCode[colorIndex];
                  RenderSystem.setShaderColor(
                     (float)(colorcode >> 16 & 0xFF) / 255.0F, (float)(colorcode >> 8 & 0xFF) / 255.0F, (float)(colorcode & 0xFF) / 255.0F, alpha
                  );
               }

               i++;
            } else if (character < currentData.length && currentData[character] != null) {
               RenderSystem.setShaderTexture(0, currentTexture);
               Tessellator tessellator = Tessellator.getInstance();
               BufferBuilder buffer = tessellator.begin(DrawMode.TRIANGLES, VertexFormats.POSITION_TEXTURE);
               this.drawChar(currentData, character, (float)x, (float)y, matrix, buffer);
               BufferRenderer.drawWithGlobalProgram(buffer.end());
               if (strikethrough) {
                  this.drawLine(
                     x,
                     y + (double)((float)currentData[character].height / 2.0F),
                     x + (double)currentData[character].width - 8.0,
                     y + (double)((float)currentData[character].height / 2.0F),
                     1.0F,
                     matrix
                  );
               }

               if (underline) {
                  this.drawLine(
                     x,
                     y + (double)currentData[character].height - 2.0,
                     x + (double)currentData[character].width - 8.0,
                     y + (double)currentData[character].height - 2.0,
                     1.0F,
                     matrix
                  );
               }

               x += (double)(currentData[character].width - 8 + this.charOffset);
            }
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         return (float)x / 2.0F;
      }
   }

   @Override
   public int getStringWidth(String text) {
      int width = 0;
      CFont.CharData[] currentData = this.charData;
      boolean bold = false;
      boolean italic = false;
      int size = text.length();

      for (int i = 0; i < size; i++) {
         char character = text.charAt(i);
         if (String.valueOf(character).equals("§") && i < size - 1) {
            int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
            if (colorIndex < 16) {
               bold = false;
               italic = false;
            } else if (colorIndex == 17) {
               bold = true;
               currentData = italic ? this.boldItalicChars : this.boldChars;
            } else if (colorIndex == 20) {
               italic = true;
               currentData = bold ? this.boldItalicChars : this.italicChars;
            } else if (colorIndex == 21) {
               bold = false;
               italic = false;
               currentData = this.charData;
            }

            i++;
         } else if (character < currentData.length && currentData[character] != null) {
            width += currentData[character].width - 8 + this.charOffset;
         }
      }

      return width / 2;
   }

   @Override
   public void setFont(Font font) {
      super.setFont(font);
      this.setupBoldItalicIDs();
   }

   @Override
   public void setAntiAlias(boolean antiAlias) {
      super.setAntiAlias(antiAlias);
      this.setupBoldItalicIDs();
   }

   @Override
   public void setFractionalMetrics(boolean fractionalMetrics) {
      super.setFractionalMetrics(fractionalMetrics);
      this.setupBoldItalicIDs();
   }

   private void setupBoldItalicIDs() {
      CFont boldFont = new CFont(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics);
      this.texBold = boldFont.getGlTextureId();
      this.boldChars = boldFont.charData;
      CFont italicFont = new CFont(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics);
      this.texItalic = italicFont.getGlTextureId();
      this.italicChars = italicFont.charData;
      CFont boldItalicFont = new CFont(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics);
      this.texItalicBold = boldItalicFont.getGlTextureId();
      this.boldItalicChars = boldItalicFont.charData;
   }

   private void drawLine(double x, double y, double x1, double y1, float width, Matrix4f matrix) {
      RenderSystem.setShader(ShaderProgramKeys.POSITION);
      RenderSystem.lineWidth(width);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.begin(DrawMode.LINES, VertexFormats.POSITION);
      buffer.vertex(matrix, (float)x, (float)y, 0.0F);
      buffer.vertex(matrix, (float)x1, (float)y1, 0.0F);
      BufferRenderer.drawWithGlobalProgram(buffer.end());
   }

   public void drawStringWithOutline(String text, double x, double y, int color) {
      this.drawString(text, x - 0.5, y, Color.BLACK.getRGB(), false);
      this.drawString(text, x + 0.5, y, Color.BLACK.getRGB(), false);
      this.drawString(text, x, y - 0.5, Color.BLACK.getRGB(), false);
      this.drawString(text, x, y + 0.5, Color.BLACK.getRGB(), false);
      this.drawString(text, x, y, color, false);
   }

   public void drawCenteredStringWithOutline(String text, float x, float y, int color) {
      this.drawCenteredString(text, x - 0.5F, y, Color.BLACK.getRGB());
      this.drawCenteredString(text, x + 0.5F, y, Color.BLACK.getRGB());
      this.drawCenteredString(text, x, y - 0.5F, Color.BLACK.getRGB());
      this.drawCenteredString(text, x, y + 0.5F, Color.BLACK.getRGB());
      this.drawCenteredString(text, x, y, color);
   }
}
