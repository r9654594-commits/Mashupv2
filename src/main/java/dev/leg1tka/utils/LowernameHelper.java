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


public class LowernameHelper {
   public final String lowerName;
   public final float width;
   public final float anim;

   public LowernameHelper(String lowerName, float width, float anim) {
      this.lowerName = lowerName;
      this.width = width;
      this.anim = anim;
   }

   public String lowerName() {
      return this.lowerName;
   }

   public float width() {
      return this.width;
   }

   public float anim() {
      return this.anim;
   }
}
