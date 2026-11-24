package dev.leg1tka.settings;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import dev.leg1tka.AspectDLC;
import java.awt.Color;
import java.util.function.Supplier;
import lombok.Generated;

public abstract class Setting implements IMinecraft {
   private final String name;
   public Supplier<Boolean> visible = () -> true;
   public Color color = Color.WHITE;

   public Setting(
      String str
   ) {
      this.name = str;
   }

   public Boolean visible() {
      return this.visible.get();
   }

   public String displayName() {
      return AspectDLC.INSTANCE.localizationStorage == null ? this.name : AspectDLC.INSTANCE.localizationStorage.translate(this.name);
   }

   @Generated
   public String name() {
      return this.name;
   }

   @Generated
   public Color color() {
      return this.color;
   }
}
