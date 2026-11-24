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

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

public class ViewModel extends Module {
   public static ViewModel INSTANCE = new ViewModel();
   public final NumberSetting mainHandX;
   public final NumberSetting mainHandY;
   public final NumberSetting mainHandZ;
   public final NumberSetting offHandX;
   public final NumberSetting offHandY;
   public final NumberSetting offHandZ;
   public final BooleanSetting onlyAura;

   public ViewModel() {
      super("ViewModel", Category.RENDER);
      String var1 = "ViewModel";
      this.mainHandX = new NumberSetting("Правая рука X", 0.0F, -2.0F, 2.0F, 0.01F);
      this.mainHandY = new NumberSetting("Правая рука Y", 0.0F, -2.0F, 2.0F, 0.01F);
      this.mainHandZ = new NumberSetting("Правая рука Z", 0.0F, -2.0F, 2.0F, 0.01F);
      this.offHandX = new NumberSetting("Левая рука X", 0.0F, -2.0F, 2.0F, 0.01F);
      this.offHandY = new NumberSetting("Левая рука Y", 0.0F, -2.0F, 2.0F, 0.01F);
      this.offHandZ = new NumberSetting("Левая рука Z", 0.0F, -2.0F, 2.0F, 0.01F);
      this.onlyAura = new BooleanSetting("Только с аурой", false);
      this.addSettings(new Setting[]{
         this.mainHandX,
         this.mainHandY,
         this.mainHandZ,
         this.offHandX,
         this.offHandY,
         this.offHandZ,
         this.onlyAura
      });
   }

   public void applyHandPosition(
      MatrixStack val,
      Arm val2
   ) {
      if (val2
         == Arm.RIGHT) {
         val.translate(
            this.mainHandX.get(), this.mainHandY.get(), this.mainHandZ.get()
         );
      } else {
         val.translate(
            this.offHandX.get(), this.offHandY.get(), this.offHandZ.get()
         );
      }
   }
}
