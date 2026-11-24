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


public class CloneHelper implements Cloneable {
   public boolean enabled;
   public int transX;
   public int transY;
   public int x;
   public int y;
   public int width;
   public int height;

   public CloneHelper() {
   }

   public CloneHelper clone() {
      try {
         return (CloneHelper)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError(var2);
      }
   }
}
