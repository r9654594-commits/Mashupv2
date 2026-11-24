package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.WorldTweaks;
import dev.leg1tka.modules.render.NoRender;
import dev.leg1tka.core.ModuleManager;
import dev.leg1tka.render.ColorUtil;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.entity.Entity;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BackgroundRenderer.class})
public class BackgroundRendererMixin {
   @Inject(
      method = {"getFogModifier"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void AspectDLC$getFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<Object> cir) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Плохие эффекты")) {
            cir.setReturnValue(null);
         }
      }
   }

   @Inject(
      method = {"applyFog"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void AspectDLC$applyFog(
      Camera camera, FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta, CallbackInfoReturnable<Fog> cir
   ) {
      if (ModuleManager.INSTANCE != null) {
         WorldTweaks tweaks = ModuleManager.worldTweaks;
         if (tweaks != null && tweaks.isFogEnabled()) {
            float fogDistance = Math.max(12.0F, tweaks.getFogDistance());
            float fogEnd = Math.min(viewDistance, fogDistance);
            float fogStart = Math.max(0.0F, fogEnd * 0.05F);
            int color1 = tweaks.getFogColor();
            cir.setReturnValue(new Fog(fogStart, fogEnd, FogShape.SPHERE, ColorUtil.redf(color1), ColorUtil.greenf(color1), ColorUtil.bluef(color1), 1.0F));
         }
      }
   }
}
