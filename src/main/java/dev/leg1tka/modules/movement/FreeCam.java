package dev.leg1tka.modules.movement;


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

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventMove;
import dev.leg1tka.api.events.implement.EventMoveInput;
import dev.leg1tka.api.events.implement.EventPacket;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class FreeCam extends Module {
   public static FreeCam INSTANCE = new FreeCam();
   public Vec3d pos;

   public FreeCam() {
      super(
         "FreeCum",
         "Обзор местности за фейк игрока",
         Category.MOVEMENT
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.player != null) {
         this.pos = mc.player.getPos();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.player != null && this.pos != null) {
         mc.player.setPosition(this.pos);
      }
   }

   @EventLink
   public void onEvent(
      EventPacket val
   ) {
      Packet val2 = val.getPacket();
      if (val2 instanceof PlayerMoveC2SPacket
         )
       {
         val.cancel();
      } else if (val2 instanceof PlayerRespawnS2CPacket
         || val2 instanceof GameJoinS2CPacket
         )
       {
         this.toggle();
      }
   }

   @EventLink
   public void onEvent(
      Event3DRender val
   ) {
      if (this.pos != null && mc.player != null) {
         float y2 = mc.player
               .getWidth()
            / 2.0F;
         float y3 = mc.player
            .getHeight();
         Box box = new Box(
            this.pos.x
               - (double)y2,
            this.pos.y,
            this.pos.z
               - (double)y2,
            this.pos.x
               + (double)y2,
            this.pos.y
               + (double)y3,
            this.pos.z
               + (double)y2
         );
         this.drawHitbox(
            val.getMatrices(),
            box,
            val.getCamera()
               .getPos()
         );
      }
   }

   private void drawHitbox(
      MatrixStack val,
      Box val2,
      Vec3d val3
   ) {
      double d = val2.minX
         - val3.x;
      double d2 = val2.minY
         - val3.y;
      double d3 = val2.minZ
         - val3.z;
      double d4 = val2.maxX
         - val3.x;
      double d5 = val2.maxY
         - val3.y;
      double d6 = val2.maxZ
         - val3.z;
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      Tessellator tessellator = Tessellator.getInstance();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(1.5F);
      BufferBuilder buffer = tessellator.begin(
         DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR
      );
      float f = 1.0F;
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 1.0F;
      buffer.vertex(
            matrix,
            (float)d,
            (float)d2,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d2,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d2,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d2,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d2,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d2,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d2,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d2,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d5,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d5,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d5,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d5,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d5,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d5,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d5,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d5,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d2,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d5,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d2,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d5,
            (float)d3
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d2,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d4,
            (float)d5,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d2,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      buffer.vertex(
            matrix,
            (float)d,
            (float)d5,
            (float)d6
         )
         .color(
            f,
            f2,
            f3,
            f4
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   @EventLink
   public void onEvent(
      EventMove val
   ) {
      if (mc.player != null) {
         mc.player.noClip = true;
         double d = 1.0;
         double d2 = (double)mc.player
            .input
            .movementForward;
         double d3 = (double)mc.player
            .input
            .movementSideways;
         double d4 = Math.toRadians(
            (double)mc.player.getYaw()
         );
         double d5 = 0.0;
         double d6 = 0.0;
         if (d2
               != 0.0
            || d3
               != 0.0) {
            double d7 = d4
               + Math.atan2(
                  -d3,
                  d2
               );
            d5 = -Math.sin(
                  d7
               )
               * d;
            d6 = Math.cos(
                  d7
               )
               * d;
         }

         double d8 = 0.0;
         if (mc.options.jumpKey.isPressed()) {
            d8 = d;
         } else if (mc.options.sneakKey.isPressed()) {
            d8 = -d;
         }

         val.setMovePos(
            new Vec3d(
               d5,
               d8,
               d6
            )
         );
      }
   }

   @EventLink
   public void onEvent(
      EventMoveInput val
   ) {
      if (mc.player != null) {
         if (mc.player.getPose() == EntityPose.CROUCHING || mc.player.getPose() == EntityPose.SWIMMING) {
            val.setStrafe(
               val.getStrafe()
                  * 5.0F
            );
         }
      }
   }
}
