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


public class TypeHelper_1 {
   public final ClientOption_GGRRGytk type;
   public final int slot;

   public TypeHelper_1(ClientOption_GGRRGytk type, int slot) {
      this.type = type;
      this.slot = slot;
   }

   public ClientOption_GGRRGytk type() {
      return this.type;
   }

   public int slot() {
      return this.slot;
   }
}
