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


import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Identifier;

public class CustomItemModel {
   public final Identifier prettyModel;
   public final Identifier simpleModel;
   public final List<ModelModifierEntry> attributes;

   public CustomItemModel(Identifier prettyModel, Identifier simpleModel, List<ModelModifierEntry> attributes) {
      this.prettyModel = prettyModel;
      this.simpleModel = simpleModel;
      this.attributes = attributes;
   }

   public Identifier prettyModel() {
      return this.prettyModel;
   }

   public Identifier simpleModel() {
      return this.simpleModel;
   }

   public List<ModelModifierEntry> attributes() {
      return this.attributes;
   }

   public Identifier getModel(
      boolean flag
   ) {
      return flag
         ? this.simpleModel
         : this.prettyModel;
   }

   public boolean matches(
      List<ModelModifierEntry> list
   ) {
      if (this.attributes.size()
         != list.size()
         )
       {
         return false;
      } else {
         List val = new ArrayList<>(
            list
         );

         for (ModelModifierEntry val2 : this.attributes) {
            int i = val.indexOf(
               val2
            );
            if (i
               < 0) {
               return false;
            }

            val.remove(
               i
            );
         }

         return val.isEmpty();
      }
   }

}
