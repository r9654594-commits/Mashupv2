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


import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.registry.entry.RegistryEntry;

public class ModelModifierEntry {
   public final RegistryEntry<EntityAttribute> attribute;
   public final Operation operation;
   public final double value;

   public ModelModifierEntry(RegistryEntry<EntityAttribute> attribute, Operation operation, double value) {
      this.attribute = attribute;
      this.operation = operation;
      this.value = value;
   }

   public RegistryEntry<EntityAttribute> attribute() {
      return this.attribute;
   }

   public Operation operation() {
      return this.operation;
   }

   public double value() {
      return this.value;
   }

   @Override
   public boolean equals(
      Object val
   ) {
      if (this
         == val
         )
       {
         return true;
      } else {
         return !(
               val instanceof ModelModifierEntry val2
            )
            ? false
            : this.attribute
                  .equals(
                     val2.attribute
                  )
               && this.operation
                  == val2.operation
               && Math.abs(
                     this.value
                        - val2.value
                  )
                  < 1.0E-6;
      }
   }

   @Override
   public int hashCode() {
      long time = Math.round(
         this.value * 1000000.0
      );
      int n = this.attribute
         .hashCode();
      n = 31
            * n
         + this.operation.hashCode();
      return 31
            * n
         + Long.hashCode(
            time
         );
   }

}
