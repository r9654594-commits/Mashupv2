package dev.leg1tka.mixin;

import dev.leg1tka.AspectDLC;
import dev.leg1tka.hud.Draggable;
import dev.leg1tka.hud.Draggables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChatScreen.class})
public class ChatScreenMixin {
   @Unique
   private boolean AspectDLC$leftPressed;

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      for (Draggable draggable : Draggables.draggables.values()) {
         if (draggable.getModule().isEnable() && draggable.onClick(mouseX, mouseY, button)) {
            cir.setReturnValue(true);
            return;
         }
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      Window window = mc.getWindow();
      boolean leftPressed = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 0) == 1;
      if (this.AspectDLC$leftPressed && !leftPressed) {
         for (Draggable draggable : Draggables.draggables.values()) {
            draggable.onRelease(0);
         }
      }

      this.AspectDLC$leftPressed = leftPressed;

      for (Draggable draggable : Draggables.draggables.values()) {
         if (draggable.getModule().isEnable()) {
            draggable.onDraw(mouseX, mouseY, window, context.getMatrices());
         }
      }
   }

   @Inject(
      method = {"removed"},
      at = {@At("HEAD")}
   )
   private void onRemoved(CallbackInfo ci) {
      this.AspectDLC$leftPressed = false;

      for (Draggable draggable : Draggables.draggables.values()) {
         draggable.onRelease(0);
      }

      try {
         AspectDLC.INSTANCE.configStorage.saveConfig(AspectDLC.INSTANCE.configStorage.currentConfig);
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }
}
