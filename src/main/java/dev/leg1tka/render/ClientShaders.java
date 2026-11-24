package dev.leg1tka.render;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import lombok.Generated;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class ClientShaders implements IMinecraft {
   public static final ShaderProgramKey roundedRect = register("rect", "rounded_rect", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey roundedRectOutline = register("rect", "rounded_rect_outline", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey ringArc = register("ring_arc", "ring_arc", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey roundedTexture = register("texture", "texture_rect", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey liquidGlass = register("liquidglass", "liquid", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey kawaseDown = register("kawase_down", "kawase_down", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey kawaseUp = register("kawase_up", "kawase_up", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey gradientRect = register("gradient_rect", "gradient", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shadowRect = register("shadow_rect", "shadow", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey shadow6Rect = register("shadow6", "shadow", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey shadowOuter = register("shadow_outer", "shadow", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey fontsMsdf = register("fonts", "fonts", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey face = register("face", "face", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey gradient6Rect = register("gradient6", "gradient", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey sonar = register("sonar", "sonar", VertexFormats.POSITION_COLOR);
   public static final ShaderProgramKey scanEffect = register("sonar", "scan_effect", VertexFormats.POSITION_TEXTURE);
   public static final ShaderProgramKey blockOverlay = register("blockoverlay", "block_overlay", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey chamsFill = register("chams", "chams_fill", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsMaskDiff = register("hands", "hands_mask_diff", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsOverlay = register("hands", "hands_overlay", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsGlow = register("hands", "hands_glow", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsKawaseDown = register("hands", "hands_kawase_down", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderHandsKawaseUp = register("hands", "hands_kawase_up", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderEspGlow = register("shaderesp", "glow", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey shaderEspFill = register("shaderesp", "fill", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey auroraSky = register("aurora", "aurora_sky", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey skyNebula = register("sky_nebula", "data", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey skyCaustic = register("sky_caustic", "data", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey skySpace = register("sky_space", "data", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey skySummer = register("sky_summer", "data", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey skyPlasma = register("sky_plasma", "data", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey skyBalatro = register("sky_balatro", "data", VertexFormats.POSITION_TEXTURE_COLOR);
   public static final ShaderProgramKey blur = register("blur", "blur", VertexFormats.POSITION_COLOR);

   private static ShaderProgramKey register(
      String str,
      String str2,
      VertexFormat val
   ) {
      return new ShaderProgramKey(
         Identifier.of(
            ClientAssets.assets(),
            "core/"
               + str
               + "/"
               + str2
         ),
         val,
         Defines.EMPTY
      );
   }

   @Generated
   private ClientShaders() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
