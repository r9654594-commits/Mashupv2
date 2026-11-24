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


public class PotioninfoHolder {
   public DisplaynameType potionInfo;
   public double lastX;
   public double lastY;
   public double lastZ;

   public PotioninfoHolder(
      DisplaynameType val,
      double d,
      double d2,
      double d3
   ) {
      this.potionInfo = val;
      this.lastX = d;
      this.lastY = d2;
      this.lastZ = d3;
   }
}
