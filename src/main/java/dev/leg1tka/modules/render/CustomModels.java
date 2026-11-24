package dev.leg1tka.modules.render;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


public class CustomModels extends Module {
   public static CustomModels INSTANCE = new CustomModels();

   public CustomModels() {
      super("CustomModels", Category.RENDER);
      String var1 = "CustomModels";
   }

   public float getHeadScale() {
      return this.isEnable() ? 2.8F : 1.0F;
   }

   public float getBodyScale() {
      return this.isEnable() ? 2.0F : 1.0F;
   }

   public float getLegsScale() {
      return this.isEnable() ? 0.35F : 1.0F;
   }

   public float getArmsScale() {
      return this.isEnable() ? 1.4F : 1.0F;
   }
}
