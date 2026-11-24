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


public class TypeHelper {
   public final ClientOption_DDpev0RQ type;
   public final int sourceSlot;
   public final int hotbarSlot;

   public TypeHelper(ClientOption_DDpev0RQ type, int sourceSlot, int hotbarSlot) {
      this.type = type;
      this.sourceSlot = sourceSlot;
      this.hotbarSlot = hotbarSlot;
   }

   public ClientOption_DDpev0RQ type() {
      return this.type;
   }

   public int sourceSlot() {
      return this.sourceSlot;
   }

   public int hotbarSlot() {
      return this.hotbarSlot;
   }
}
