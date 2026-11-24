package dev.leg1tka.modules.combat;


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
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class NoVelocity extends Module {
   public static NoVelocity INSTANCE = new NoVelocity();
   private final ModeSetting mode;
   private final BooleanSetting explosions;
   private boolean needJump;
   private int hurtTicks;

   public NoVelocity() {
      super("Velocity", "Отключает отдачу от урона", Category.COMBAT);
      this.mode = new ModeSetting("Мод", "Vanilla", new String[]{"Vanilla", "Grim", "Jump Reset"});
      this.explosions = new BooleanSetting("Взрывы", true);
      this.addSettings(new Setting[]{
         this.mode,
         this.explosions
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.needJump = false;
      this.hurtTicks = 0;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.needJump = false;
      this.hurtTicks = 0;
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (mc.player != null && mc.world != null) {
         if (val.getType()
            == EventPacket.Type.RECEIVE) {
            if (val.getPacket() instanceof EntityVelocityUpdateS2CPacket val2
               )
             {
               if (val2.getEntityId()
                  != mc.player.getId()) {
                  return;
               }

               if (this.mode.is("Vanilla")) {
                  val.cancel();
               }

               if (this.mode.is("Grim")) {
                  val.cancel();
                  double d = val2.getVelocityY()
                     / 8000.0;
                  if (mc.player.isOnGround()
                     && d
                        > 0.0) {
                     mc.player.setVelocity(mc.player.getVelocity().x, 0.0, mc.player.getVelocity().z);
                  } else if (d
                     > 0.0) {
                     mc.player.setVelocity(mc.player.getVelocity().x, 0.0, mc.player.getVelocity().z);
                  }
               }

               if (this.mode.is("Jump Reset")) {
                  double d2 = val2.getVelocityY()
                     / 8000.0;
                  if (d2
                     > 0.1) {
                     this.needJump = true;
                     this.hurtTicks = 0;
                  }
               }
            }

            if (this.explosions.isState()
               && val.getPacket() instanceof ExplosionS2CPacket
               && (this.mode.is("Vanilla") || this.mode.is("Grim"))) {
               val.cancel();
            }
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val3
   ) {
      if (mc.player != null) {
         if (this.mode.is("Jump Reset") && this.needJump) {
            this.hurtTicks++;
            if (mc.player.isOnGround()) {
               mc.player.jump();
               this.needJump = false;
               this.hurtTicks = 0;
            }

            if (this.hurtTicks > 5) {
               this.needJump = false;
               this.hurtTicks = 0;
            }
         }
      }
   }
}
