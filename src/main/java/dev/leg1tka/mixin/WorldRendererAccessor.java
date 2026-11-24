package dev.leg1tka.mixin;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({WorldRenderer.class})
public interface WorldRendererAccessor {
   @Accessor("entityOutlineFramebuffer")
   Framebuffer AspectDLC$getEntityOutlineFramebufferRaw();
}
