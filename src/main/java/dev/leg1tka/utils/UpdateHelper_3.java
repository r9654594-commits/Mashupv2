package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;


import net.minecraft.client.input.Input;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class UpdateHelper_3 extends Input {
   public boolean forceSafeWalk = false;
   public float movementForward;
   public float movementSideways;
   public PlayerInput playerInput;
   public static final double MAX_WALKING_SPEED = 0.121;

   public UpdateHelper_3(
      PlayerInput val
   ) {
      this.playerInput = val;
   }

   public void update() {
      if (this.playerInput.forward() != this.playerInput.backward()) {
         this.movementForward = this.playerInput.forward() ? 1.0F : -1.0F;
      } else {
         this.movementForward = 0.0F;
      }

      if (this.playerInput.left() == this.playerInput.right()) {
         this.movementSideways = 0.0F;
      } else {
         this.movementSideways = this.playerInput.left() ? 1.0F : -1.0F;
      }

      if (this.playerInput.sneak()) {
         this.movementSideways *= 0.3F;
         this.movementForward *= 0.3F;
      }
   }

   public String toString() {
      return "SimulatedPlayerInput(forwards={"
         + this.playerInput.forward()
         + "}, backwards={"
         + this.playerInput.backward()
         + "}, left={"
         + this.playerInput.left()
         + "}, right={"
         + this.playerInput.right()
         + "}, jumping={"
         + this.playerInput.jump()
         + "}, sprinting="
         + this.playerInput.sprint()
         + ", slowDown="
         + this.playerInput.sneak()
         + ")";
   }

   public static UpdateHelper_3 fromClientPlayer(
      PlayerInput val
   ) {
      return new UpdateHelper_3(
         val
      );
   }

   public static UpdateHelper_3 guessInput(
      PlayerEntity player
   ) {
      Vec3d vec = player.getPos()
         .subtract(
            new Vec3d(
               player.prevX,
               player.prevY,
               player.prevZ
            )
         );
      double x = vec.horizontalLengthSquared();
      PlayerInput val = new PlayerInput(
         false,
         false,
         false,
         false,
         !player.isOnGround(),
         player.isSneaking(),
         x
            >= 0.014641
      );
      if (x
         > 0.0025000000000000005) {
         double x2 = HasplayermovementHelper.getDegreesRelativeToView(
            vec,
            player.getYaw()
         );
         double x3 = MathHelper.wrapDegrees(
            x2
         );
         val = HasplayermovementHelper.getDirectionalInputForDegrees(
            val,
            x3
         );
      }

      return new UpdateHelper_3(
         val
      );
   }
}
