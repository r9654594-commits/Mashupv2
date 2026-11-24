package dev.leg1tka.render;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


public class Color1Helper {
   public final int color1;
   public final int color2;
   public final float waveSpeed;
   public final float waveScale;
   public final float outline;
   public final float glow;
   public final float fill;
   public final float alpha;

   public Color1Helper(int color1, int color2, float waveSpeed, float waveScale, float outline, float glow, float fill, float alpha) {
      this.color1 = color1;
      this.color2 = color2;
      this.waveSpeed = waveSpeed;
      this.waveScale = waveScale;
      this.outline = outline;
      this.glow = glow;
      this.fill = fill;
      this.alpha = alpha;
   }

   public int color1() {
      return this.color1;
   }

   public int color2() {
      return this.color2;
   }

   public float waveSpeed() {
      return this.waveSpeed;
   }

   public float waveScale() {
      return this.waveScale;
   }

   public float outline() {
      return this.outline;
   }

   public float glow() {
      return this.glow;
   }

   public float fill() {
      return this.fill;
   }

   public float alpha() {
      return this.alpha;
   }

   public static Color1Helper defaults(
      int n,
      int n2
   ) {
      return new Color1Helper(
         n,
         n2,
         1.2F,
         1.0F,
         1.1F,
         1.0F,
         0.6F,
         1.0F
      );
   }

   public static Color1Helper bright(
      int n,
      int n2
   ) {
      return new Color1Helper(
         n,
         n2,
         1.35F,
         1.0F,
         1.65F,
         2.6F,
         0.95F,
         1.0F
      );
   }

   public static Color1Helper predict(
      int n,
      int n2
   ) {
      return new Color1Helper(
         n,
         n2,
         1.55F,
         1.0F,
         2.15F,
         4.2F,
         1.0F,
         1.0F
      );
   }

   public static Color1Helper hud(
      int n,
      int n2,
      float f
   ) {
      float f2 = Math.min(
         1.0F,
         f
            * 1.18F
      );
      return new Color1Helper(
         n,
         n2,
         1.35F,
         1.0F,
         1.25F,
         1.85F,
         0.88F,
         f2
      );
   }

   public static Color1Helper hudThreads(
      int n,
      int n2,
      float f
   ) {
      return new Color1Helper(
         n,
         n2,
         1.2F,
         1.0F,
         1.05F,
         0.45F,
         0.32F,
         Math.min(
            1.0F,
            f
               * 0.92F
         )
      );
   }
}
