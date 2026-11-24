package dev.leg1tka.storage;
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
import dev.leg1tka.utils.*;


import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class StaffStorage {
   private final List<String> staffs = new ArrayList<>();

   public void add(
      String str
   ) {
      if (!str.isEmpty()
         )
       {
         this.staffs
            .add(
               str
            );
      }
   }

   public void remove(
      String str
   ) {
      this.staffs
         .remove(
            str
         );
   }

   public void clear() {
      this.staffs.clear();
   }

   public boolean isStaff(
      String str
   ) {
      return this.staffs
         .contains(
            str
         );
   }

   public boolean isEmpty() {
      return this.staffs.isEmpty();
   }

   @Generated
   public List<String> getStaffs() {
      return this.staffs;
   }
}
