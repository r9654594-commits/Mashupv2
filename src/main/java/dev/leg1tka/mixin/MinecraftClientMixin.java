package dev.leg1tka.mixin;

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.implement.EventGameUpdate;
import dev.leg1tka.api.events.implement.EventTickPost;
import dev.leg1tka.api.events.implement.EventTickPre;
import dev.leg1tka.modules.player.OpenWalls;
import dev.leg1tka.modules.render.ShaderEsp;
import dev.leg1tka.modules.misc.Unhook;
import dev.leg1tka.core.ModuleManager;
import dev.leg1tka.utils.UpdatefpsHelper;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MinecraftClient.class})
public abstract class MinecraftClientMixin {
   @Unique
   private long lastHookTime = Util.getMeasuringTimeNano();
   @Unique
   private int accumulatedCalls = 0;

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   public void tick(CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      Unhook.checkUnhookKey();
      if (EventInvoker.hasListeners(EventTickPre.class)) {
         EventTickPre event = new EventTickPre();
         EventInvoker.invoke(event);
      }

      UpdatefpsHelper.updateFPS();
   }

   @Inject(
      method = {"doItemUse"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void openWalls$doItemUse(CallbackInfo ci) {
      OpenWalls openWalls = OpenWalls.INSTANCE;
      if (openWalls != null && openWalls.isEnable() && openWalls.tryOpenThroughWall()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   public void tickEnd(CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (EventInvoker.hasListeners(EventTickPost.class)) {
         EventTickPost event = new EventTickPost();
         EventInvoker.invoke(event);
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void render(boolean tick, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (!EventInvoker.hasListeners(EventGameUpdate.class)) {
         this.lastHookTime = Util.getMeasuringTimeNano();
         this.accumulatedCalls = 0;
      } else {
         long now = Util.getMeasuringTimeNano();
         long delta = now - this.lastHookTime;
         this.accumulatedCalls += (int)(delta / 4166666L);
         this.lastHookTime = this.lastHookTime + (long)this.accumulatedCalls * 4166666L;

         for (this.accumulatedCalls = Math.min(this.accumulatedCalls, 240); this.accumulatedCalls > 0; this.accumulatedCalls--) {
            EventInvoker.invoke(new EventGameUpdate());
         }
      }
   }

   @Inject(
      method = {"hasOutline"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (ModuleManager.INSTANCE != null) {
         ShaderEsp shaderEsp = ModuleManager.shaderEsp;
         if (shaderEsp != null && shaderEsp.shouldOutline(entity)) {
            cir.setReturnValue(true);
         }
      }
   }
}
