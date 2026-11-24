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


import java.util.List;
import lombok.Generated;

public class OfHelper {
   protected final GetrgbHelper topLeftColor;
   protected final GetrgbHelper bottomLeftColor;
   protected final GetrgbHelper topRightColor;
   protected final GetrgbHelper bottomRightColor;

   protected OfHelper(
      GetrgbHelper val,
      GetrgbHelper val2,
      GetrgbHelper val3,
      GetrgbHelper val4
   ) {
      this.topLeftColor = val;
      this.bottomLeftColor = val2;
      this.topRightColor = val3;
      this.bottomRightColor = val4;
   }

   public static OfHelper of(
      GetrgbHelper val,
      GetrgbHelper val2,
      GetrgbHelper val3,
      GetrgbHelper val4
   ) {
      return new OfHelper(
         val,
         val2,
         val3,
         val4
      );
   }

   public static OfHelper of(
      List<GetrgbHelper> list
   ) {
      return new OfHelper(
         list.get(
            0
         ),
         list.get(
            1
         ),
         list.get(
            2
         ),
         list.get(
            3
         )
      );
   }

   public OfHelper rotate() {
      return this;
   }

   public OfHelper mulAlpha(
      float f
   ) {
      return new OfHelper(
         this.topLeftColor
            .mulAlpha(
               f
            ),
         this.bottomLeftColor
            .mulAlpha(
               f
            ),
         this.topRightColor
            .mulAlpha(
               f
            ),
         this.bottomRightColor
            .mulAlpha(
               f
            )
      );
   }

   @Generated
   public GetrgbHelper getTopLeftColor() {
      return this.topLeftColor;
   }

   @Generated
   public GetrgbHelper getBottomLeftColor() {
      return this.bottomLeftColor;
   }

   @Generated
   public GetrgbHelper getTopRightColor() {
      return this.topRightColor;
   }

   @Generated
   public GetrgbHelper getBottomRightColor() {
      return this.bottomRightColor;
   }
}
