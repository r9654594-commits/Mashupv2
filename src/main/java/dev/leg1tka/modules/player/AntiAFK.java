package dev.leg1tka.modules.player;


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
import dev.leg1tka.api.events.implement.EventUpdate;

public class AntiAFK extends Module {
   public static AntiAFK INSTANCE = new AntiAFK();
   private long lastActionTime = 0L;
   private long walkStartTime = 0L;
   private long jumpStartTime = 0L;
   private boolean isWalking = false;
   private boolean isJumping = false;
   private static final long ACTION_INTERVAL = 5000L;
   private static final long WALK_DURATION = 2000L;
   private static final long JUMP_DURATION = 100L;

   public AntiAFK() {
      super(
         "AntiAFK",
         "Автоматически нажимает пробел и идет вперед чтобы не кикнуло за АФК",
         Category.PLAYER
      );
   }

   @Override
   public void onEnable() {
      this.lastActionTime = System.currentTimeMillis();
      this.walkStartTime = 0L;
      this.jumpStartTime = 0L;
      this.isWalking = false;
      this.isJumping = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (mc.player != null && mc.options != null) {
         mc.options.forwardKey.setPressed(false);
         mc.options.jumpKey.setPressed(false);
      }

      this.isWalking = false;
      this.isJumping = false;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val2
   ) {
      if (mc.player != null && mc.world != null && mc.options != null) {
         long val = System.currentTimeMillis();
         if (this.isJumping
            && val
                  - this.jumpStartTime
               >= 100L) {
            mc.options.jumpKey.setPressed(false);
            this.isJumping = false;
         }

         if (this.isWalking) {
            if (val
                  - this.walkStartTime
               >= 2000L) {
               mc.options.forwardKey.setPressed(false);
               this.isWalking = false;
               this.lastActionTime = val;
            }
         } else if (!this.isJumping
            && val
                  - this.lastActionTime
               >= 5000L) {
            mc.options.jumpKey.setPressed(true);
            this.isJumping = true;
            this.jumpStartTime = val;
            mc.options.forwardKey.setPressed(true);
            this.isWalking = true;
            this.walkStartTime = val;
         }
      }
   }
}
