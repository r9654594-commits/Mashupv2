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


public final class XHolder {
   public final float x;
   public final float y;
   public final float alphaMultiplier;

   public XHolder(
      float f,
      float f2,
      float f3
   ) {
      this.x = f;
      this.y = f2;
      this.alphaMultiplier = f3;
   }
}
