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


public class HoverUtil {
   public static boolean isHovering(
      float f,
      float f2,
      float f3,
      float f4,
      int n,
      int n2
   ) {
      return (float)n
            >= f
         && (float)n2
            >= f2
         && (float)n
            < f
               + f3
         && (float)n2
            < f2
               + f4;
   }

   public static boolean isInRegion(
      int n,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6
   ) {
      return n
            >= n3
         && n
            <= n3
               + n5
         && n2
            >= n4
         && n2
            <= n4
               + n6;
   }

   public static boolean isInRegion(
      double d,
      double d2,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      return d
            >= (double)f
         && d
            <= (double)(
               f
                  + f3
            )
         && d2
            >= (double)f2
         && d2
            <= (double)(
               f2
                  + f4
            );
   }

   public static boolean isHovering(
      float f,
      float f2,
      float f3,
      float f4,
      double d,
      double d2
   ) {
      return d
            >= (double)f
         && d2
            >= (double)f2
         && d
            < (double)(
               f
                  + f3
            )
         && d2
            < (double)(
               f2
                  + f4
            );
   }

   public static boolean isInRegion(
      double d,
      double d2,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      return d
            >= (double)n
         && d
            <= (double)(
               n
                  + n3
            )
         && d2
            >= (double)n2
         && d2
            <= (double)(
               n2
                  + n4
            );
   }

   public static boolean isHovered(
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6
   ) {
      return d
            > d3
         && d
            < d3
               + d5
         && d2
            > d4
         && d2
            < d4
               + d6;
   }
}
