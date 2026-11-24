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

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventRender;

public class ShaderHands extends Module {
   public static ShaderHands INSTANCE = new ShaderHands();
   private static final ShaderHandsRenderer RENDERER = ShaderHandsRenderer.getInstance();
   public final ModeSetting mode;
   public final NumberSetting waveSpeed;
   public final NumberSetting waveScale;
   public final NumberSetting outline;
   public final NumberSetting glow;
   public final NumberSetting fill;
   public final NumberSetting alpha;

   public ShaderHands() {
      super("ShaderHands", "Красивый Шейдер на руки и предметы", Category.RENDER);
      this.mode = new ModeSetting("Режим", "Свечение", new String[]{"Свечение", "Красивый"});
      this.waveSpeed = new NumberSetting("Скорость волн", 1.2F, 0.1F, 5.0F, 0.1F)
         .visible(() -> this.mode.is("Красивый"));
      this.waveScale = new NumberSetting("Частота волн", 1.0F, 1.0F, 3.0F, 0.1F)
         .visible(() -> this.mode.is("Красивый"));
      this.outline = new NumberSetting("Ширина обводки", 1.2F, 0.1F, 5.0F, 0.1F);
      this.glow = new NumberSetting("Сила свечения", 1.0F, 0.0F, 5.0F, 0.1F);
      this.fill = new NumberSetting("Заливка", 0.6F, 0.0F, 1.0F, 0.01F);
      this.alpha = new NumberSetting("Прозрачность", 1.0F, 0.0F, 1.0F, 0.05F);
      this.addSettings(new Setting[]{
         this.mode,
         this.waveSpeed,
         this.waveScale,
         this.outline,
         this.glow,
         this.fill,
         this.alpha
      });
   }

   @Override
   public void onDisable() {
      RENDERER.invalidateState();
      super.onDisable();
   }

   @EventLink(
      priority = 0
   )
   public void onRender2D(
      EventRender.Default val
   ) {
      if (this.isEnable()) {
         RENDERER.renderOverlayIfPending();
      }
   }
}
