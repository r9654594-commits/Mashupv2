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
import dev.leg1tka.api.events.implement.EventRotation;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.util.math.MathHelper;

public class InterpolateF5 extends Module {
   public static InterpolateF5 INSTANCE = new InterpolateF5();
   private static final float SWITCH_ANIM_SPEED = 0.26F;
   private static final float DISTANCE_SPEED = 0.13F;
   private static final float ROTATION_SMOOTH = 0.28F;
   private static final float CAMERA_DISTANCE = 4.1F;
   private static final float SNEAK_OFFSET = 0.5F;
   private static final float JUMP_MULTIPLIER = 2.0F;
   private static final float ANIM_SPEED = 0.13F;
   private float currentDistance;
   private float prevDistance;
   private float currentYaw;
   private float prevYaw;
   private float currentPitch;
   private float prevPitch;
   private float heightOffset;
   private float prevHeightOffset;
   private boolean switchAnimating;
   private boolean wasThirdPerson;
   private boolean needsInit = true;

   public InterpolateF5() {
      super(
         "Krutaya Camera",
         "Плавная камера от ф5",
         Category.RENDER
      );
   }

   @EventLink
   public void onUpdate(
      EventUpdate val2
   ) {
      if (mc.player != null && mc.world != null) {
         boolean flag = !mc.options
            .getPerspective()
            .isFirstPerson();
         if (flag
            && !this.wasThirdPerson) {
            this.initCamera(true);
         }

         if (!flag
            && this.wasThirdPerson) {
            this.needsInit = true;
            this.switchAnimating = false;
         }

         this.wasThirdPerson = flag;
         if (flag
            )
          {
            this.updateCamera();
         }
      }
   }

   @EventLink(
      priority = 100
   )
   public void onRotation(
      EventRotation val
   ) {
      if (mc.player != null && mc.world != null) {
         if (!mc.options.getPerspective().isFirstPerson()) {
            val.setYaw(
               this.getInterpolatedYaw(
                  val.getPartialTicks()
               )
            );
            val.setPitch(
               this.getInterpolatedPitch(
                  val.getPartialTicks()
               )
            );
         }
      }
   }

   private void initCamera(
      boolean val
   ) {
      if (mc.player != null) {
         this.currentYaw = this.prevYaw = this.getReferenceYaw();
         this.currentPitch = this.prevPitch = this.getReferencePitch();
         this.currentDistance = this.prevDistance = val
            ? 0.0F
            : 4.1F;
         this.heightOffset = this.prevHeightOffset = 0.0F;
         this.switchAnimating = val;
         this.needsInit = false;
      }
   }

   private void updateCamera() {
      if (mc.player != null) {
         if (this.needsInit) {
            this.initCamera(true);
         } else {
            this.prevYaw = this.currentYaw;
            this.prevPitch = this.currentPitch;
            this.prevDistance = this.currentDistance;
            this.prevHeightOffset = this.heightOffset;
            float f = 0.28F;
            this.currentYaw = this.currentYaw
               + MathHelper.wrapDegrees(this.getReferenceYaw() - this.currentYaw)
                  * f;
            this.currentPitch = MathHelper.clamp(
               this.currentPitch
                  + (this.getReferencePitch() - this.currentPitch)
                     * f,
               -90.0F,
               90.0F
            );
            float f2 = this.switchAnimating
               ? 0.26F
               : 0.13F;
            this.currentDistance = this.currentDistance
               + (4.1F - this.currentDistance)
                  * f2;
            if (this.switchAnimating && Math.abs(4.1F - this.currentDistance) <= 0.02F) {
               this.currentDistance = 4.1F;
               this.switchAnimating = false;
            }

            float f3 = 0.0F;
            if (mc.player.isSneaking()) {
               f3 = -0.5F;
            }

            if (!mc.player.isOnGround()) {
               f3 += (float)(
                  -mc.player.getVelocity().y * 2.0
               );
            }

            this.heightOffset = this.heightOffset
               + (
                     f3
                        - this.heightOffset
                  )
                  * 0.13F;
         }
      }
   }

   public float getInterpolatedYaw(
      float val
   ) {
      return mc.player == null
         ? 0.0F
         : this.prevYaw
            + (this.currentYaw - this.prevYaw)
               * val;
   }

   public float getInterpolatedPitch(
      float val
   ) {
      return mc.player == null
         ? 0.0F
         : MathHelper.clamp(
            this.prevPitch
               + (this.currentPitch - this.prevPitch)
                  * val,
            -90.0F,
            90.0F
         );
   }

   public float getInterpolatedDistance(
      float val
   ) {
      return this.prevDistance
         + (this.currentDistance - this.prevDistance)
            * val;
   }

   public float getInterpolatedHeightOffset(
      float val
   ) {
      return this.prevHeightOffset
         + (this.heightOffset - this.prevHeightOffset)
            * val;
   }

   private float getReferenceYaw() {
      if (FreeLookStorage.isActive()) {
         return FreeLookStorage.getFreeYaw();
      } else {
         return mc.player != null ? mc.player.getYaw() : 0.0F;
      }
   }

   private float getReferencePitch() {
      if (FreeLookStorage.isActive()) {
         return FreeLookStorage.getFreePitch();
      } else {
         return mc.player != null ? mc.player.getPitch() : 0.0F;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.needsInit = true;
      this.wasThirdPerson = false;
      if (mc.player != null && !mc.options.getPerspective().isFirstPerson()) {
         this.initCamera(true);
         this.wasThirdPerson = true;
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.needsInit = true;
      this.heightOffset = 0.0F;
      this.prevHeightOffset = 0.0F;
   }
}
