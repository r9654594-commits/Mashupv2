package dev.leg1tka.mixin;

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.modules.render.ShaderEsp;
import dev.leg1tka.modules.render.Scanner;
import dev.leg1tka.core.IMinecraft;
import dev.leg1tka.modules.render.NoRender;
import dev.leg1tka.core.ModuleManager;
import dev.leg1tka.render.RenderHelper;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profilers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public class WorldRendererMixin implements IMinecraft {
   @Inject(
      method = {"renderParticles"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$renderParticles(FrameGraphBuilder frameGraphBuilder, Camera camera, float tickDelta, Fog fog, CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Частицы")) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"renderWeather"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$renderWeather(FrameGraphBuilder frameGraphBuilder, Vec3d pos, float tickDelta, Fog fog, CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Погода")) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"addWeatherParticlesAndSound"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$addWeatherParticlesAndSound(Camera camera, CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Погода")) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"renderClouds"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$renderClouds(
      FrameGraphBuilder frameGraphBuilder,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      CloudRenderMode renderMode,
      Vec3d cameraPos,
      float ticks,
      int color,
      float cloudHeight,
      CallbackInfo ci
   ) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Облака")) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"renderBlockEntities"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$renderBlockEntities(
      MatrixStack matrices, Immediate mainConsumers, Immediate translucentConsumers, Camera camera, float tickDelta, CallbackInfo ci
   ) {
      if (ModuleManager.INSTANCE != null) {
         NoRender noRender = ModuleManager.noRender;
         if (noRender != null && noRender.isEnabled("Блок-сущности")) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private void render(
      ObjectAllocator allocator,
      RenderTickCounter tickCounter,
      boolean renderBlockOutline,
      Camera camera,
      GameRenderer gameRenderer,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo ci
   ) {
      Scanner scanner = ModuleManager.INSTANCE != null ? ModuleManager.scanner : null;
      boolean has3DListeners = EventInvoker.hasListeners(Event3DRender.class);
      boolean renderScanner = scanner != null && scanner.isEnable();
      if (has3DListeners || renderScanner) {
         Profilers.get().swap("AspectDLC_renderWorld");
         MatrixStack matrices = new MatrixStack();
         matrices.multiplyPositionMatrix(positionMatrix);
         if (has3DListeners) {
            new Event3DRender(matrices, positionMatrix, projectionMatrix, camera, tickCounter.getTickDelta(false)).call();
         }

         if (renderScanner) {
            scanner.renderFromMixin(positionMatrix, projectionMatrix, camera.getPos());
         }

         RenderHelper.INSTANCE.render(matrices, projectionMatrix);
      }
   }

   @Inject(
      method = {"drawEntityOutlinesFramebuffer"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$drawEntityOutlinesFramebuffer(CallbackInfo ci) {
      if (ModuleManager.INSTANCE != null) {
         ShaderEsp shaderEsp = ModuleManager.shaderEsp;
         if (shaderEsp != null && shaderEsp.isEnable()) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"drawBlockOutline"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onDrawBlockOutline(CallbackInfo ci) {
      if (ModuleManager.blockOverlay.isEnable()) {
         ci.cancel();
      }
   }
}
