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


import lombok.Generated;

public final class TostringHelper {
   private final String targetType;
   private final String distanceBucket;
   private final String movementState;
   private final String critState;
   private final String healthState;

   @Override
   public String toString() {
      return this.targetType + "_" + this.distanceBucket + "_" + this.movementState + "_" + this.critState + "_" + this.healthState;
   }

   @Generated
   public TostringHelper(
      String str,
      String str2,
      String str3,
      String str4,
      String str5
   ) {
      this.targetType = str;
      this.distanceBucket = str2;
      this.movementState = str3;
      this.critState = str4;
      this.healthState = str5;
   }

   @Generated
   public String getTargetType() {
      return this.targetType;
   }

   @Generated
   public String getDistanceBucket() {
      return this.distanceBucket;
   }

   @Generated
   public String getMovementState() {
      return this.movementState;
   }

   @Generated
   public String getCritState() {
      return this.critState;
   }

   @Generated
   public String getHealthState() {
      return this.healthState;
   }

   @Generated
   @Override
   public boolean equals(
      Object val
   ) {
      if (val
         == this) {
         return true;
      } else if (!(
         val instanceof TostringHelper val2
      )) {
         return false;
      } else {
         Object val3 = this.getTargetType();
         Object val4 = val2.getTargetType();
         if (val3
               == null
            ? val4
               == null
            : val3.equals(
               val4
            )) {
            Object val5 = this.getDistanceBucket();
            Object val6 = val2.getDistanceBucket();
            if (val5
                  == null
               ? val6
                  == null
               : val5.equals(
                  val6
               )) {
               Object val7 = this.getMovementState();
               Object val8 = val2.getMovementState();
               if (val7
                     == null
                  ? val8
                     == null
                  : val7.equals(
                     val8
                  )) {
                  Object val9 = this.getCritState();
                  Object val10 = val2.getCritState();
                  if (val9
                        == null
                     ? val10
                        == null
                     : val9.equals(
                        val10
                     )) {
                     Object val11 = this.getHealthState();
                     Object val12 = val2.getHealthState();
                     return val11
                           == null
                        ? val12
                           == null
                        : val11.equals(
                           val12
                        );
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   @Override
   public int hashCode() {
      int y = 59;
      int y2 = 1;
      Object val = this.getTargetType();
      y2 = y2
            * 59
         + (
            val
                  == null
               ? 43
               : val.hashCode()
         );
      Object val2 = this.getDistanceBucket();
      y2 = y2
            * 59
         + (
            val2
                  == null
               ? 43
               : val2.hashCode()
         );
      Object val3 = this.getMovementState();
      y2 = y2
            * 59
         + (
            val3
                  == null
               ? 43
               : val3.hashCode()
         );
      Object val4 = this.getCritState();
      y2 = y2
            * 59
         + (
            val4
                  == null
               ? 43
               : val4.hashCode()
         );
      Object val5 = this.getHealthState();
      return y2
            * 59
         + (
            val5
                  == null
               ? 43
               : val5.hashCode()
         );
   }
}
