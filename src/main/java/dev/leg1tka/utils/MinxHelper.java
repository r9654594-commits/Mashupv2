package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;


public class MinxHelper {
   public final float minX;
   public final float minY;
   public final float maxX;
   public final float maxY;

   public MinxHelper(float minX, float minY, float maxX, float maxY) {
      this.minX = minX;
      this.minY = minY;
      this.maxX = maxX;
      this.maxY = maxY;
   }

   public float minX() {
      return this.minX;
   }

   public float minY() {
      return this.minY;
   }

   public float maxX() {
      return this.maxX;
   }

   public float maxY() {
      return this.maxY;
   }

   public float centerX() {
      return (this.minX + this.maxX) * 0.5F;
   }

   public float centerY() {
      return (this.minY + this.maxY) * 0.5F;
   }

}
