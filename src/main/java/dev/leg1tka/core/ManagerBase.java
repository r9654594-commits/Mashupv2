package dev.leg1tka.core;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Generated;

public class ManagerBase<T> {
   private final ObjectArrayList<T> object = new ObjectArrayList();

   @Generated
   public ObjectArrayList<T> getObject() {
      return this.object;
   }
}
