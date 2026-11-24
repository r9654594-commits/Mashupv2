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

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventSlowWalking;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class NoSlow extends Module {
   public static NoSlow INSTANCE = new NoSlow();
   private final ModeSetting mode;
   private final BooleanSetting sprint;
   private final BooleanSetting polarhop;
   private final NumberSetting polarspeed;
   private int ticks;

   public NoSlow() {
      super("NoSlow", Category.MOVEMENT);
      String var1 = "NoSlow";
      String var3 = "Мод";
      String var7 = "Grim Old";
      String[] var10005 = new String[5];
      var10005[0] = "Grim Old";
      var10005[1] = "Grim Last";
      int var10007 = 2;
      var10005[var10007] = "Polar";
      var10007 = 3;
      var10005[var10007] = "SpookyTime";
      var10007 = 4;
      var10005[var10007] = "SkyTime";
      this.mode = new ModeSetting(var3, var7, var10005);
      this.sprint = new BooleanSetting("Спринт", true)
         .visible(
            () -> {
               ModeSetting var10000 = this.mode;
               if (!var10000.is("Polar")) {
                  var10000 = this.mode;
                  if (!var10000.is("Grim Old")) {
                     return false;
                  }
               }

               return true;
            }
         );
      this.polarhop = new BooleanSetting("Авто-прыжок", false)
         .visible(
            () -> this.mode.is("Polar")
         );
      this.polarspeed = new NumberSetting("Скорость", 0.5F, 0.2F, 1.0F, 0.05F)
         .visible(
            () -> {
               ModeSetting var10000 = this.mode;
               return var10000.is("Polar") && !this.polarhop.isState();
            }
         );
      this.addSettings(new Setting[]{
         this.mode,
         this.sprint,
         this.polarhop,
         this.polarspeed
      });
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.ticks = 0;
   }

   @EventLink
   public void onSlowDown(
      EventSlowWalking val
   ) {
      if (mc.player == null) {
         this.ticks = 0;
      } else {
         ModeSetting var10000 = this.mode;
         if (var10000.is("Polar")) {
            if (!mc.player.isUsingItem()) {
               this.ticks = 0;
            } else {
               this.ticks++;
               if (!mc.player.isTouchingWater() && !mc.player.isSubmergedInWater()) {
                  if (this.sprint.isState()) {
                     mc.player
                        .setSprinting(
                           (ModuleManager.sprint.isEnable() && Sprint.isSprinting() || mc.options.sprintKey.isPressed())
                              && mc.player.input.movementForward > 0.0F
                              && !mc.player.isGliding()
                        );
                  }

                  boolean flag = mc.player
                           .input
                           .movementForward
                        != 0.0F
                     || mc.player.input.movementSideways != 0.0F;
                  if (flag
                     )
                   {
                     if (this.polarhop.isState()) {
                        if (mc.player.isOnGround() && !mc.options.jumpKey.isPressed()) {
                           mc.player.jump();
                        }

                        val.setCancelled(
                           true
                        );
                     } else {
                        float f = MathHelper.clamp(
                           this.polarspeed.get(), 0.2F, 1.0F
                        );
                        mc.player.input.movementForward *= f;
                        mc.player.input.movementSideways *= f;
                        val.setCancelled(
                           true
                        );
                     }
                  }
               }
            }
         } else {
            var10000 = this.mode;
            if (var10000.is("SkyTime")) {
               if (!mc.player.isUsingItem()) {
                  this.ticks = 0;
               } else {
                  this.ticks++;
                  if (!mc.player.isTouchingWater() && !mc.player.isSubmergedInWater()) {
                     if (mc.player.isSprinting()) {
                        mc.player.setSprinting(false);
                        mc.options.sprintKey.setPressed(false);
                        if (mc.getNetworkHandler() != null) {
                           mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
                        }
                     }

                     boolean flag2 = mc.player
                              .input
                              .movementForward
                           != 0.0F
                        || mc.player.input.movementSideways != 0.0F;
                     if (flag2
                        )
                      {
                        val.setCancelled(
                           true
                        );
                     }
                  }
               }
            } else if (!mc.player.isUsingItem()) {
               this.ticks = 0;
            } else {
               var10000 = this.mode;
               if (var10000.is("SpookyTime")) {
                  this.ticks++;
                  if ((
                        mc.player.getMainHandStack().getUseAction() != UseAction.BLOCK && mc.player.getOffHandStack().getUseAction() != UseAction.EAT
                           || mc.player.getActiveHand() != Hand.MAIN_HAND
                     )
                     && mc.player.isUsingItem()) {
                     mc.player.setSprinting(true);
                     if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
                        mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
                        val.setCancelled(
                           true
                        );
                        return;
                     }

                     mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                     if (this.ticks >= 2) {
                        val.setCancelled(
                           true
                        );
                        this.ticks = 0;
                     }
                  }
               } else {
                  var10000 = this.mode;
                  if (var10000.is("Grim Last")
                     && mc.player.getItemUseTime() % 2 == 0) {
                     val.setCancelled(
                        true
                     );
                  }

                  var10000 = this.mode;
                  if (var10000.is("Grim Old")) {
                     Hand hand = mc.player
                        .getActiveHand();
                     boolean flag3 = IstargetprotocolbelowonenineteenHelper.isTargetProtocolBelowOneNineteen();
                     if (this.sprint.isState()) {
                        mc.player
                           .setSprinting(
                              (ModuleManager.sprint.isEnable() && Sprint.isSprinting() || mc.options.sprintKey.isPressed())
                                 && mc.player.input.movementForward > 0.0F
                                 && (
                                    !flag3
                                       || !mc.player.horizontalCollision && !mc.player.collidedSoftly
                                 )
                                 && !mc.player.isGliding()
                           );
                     }

                     Hand hand2 = hand
                           == Hand.MAIN_HAND
                        ? Hand.OFF_HAND
                        : Hand.MAIN_HAND;
                     mc.getNetworkHandler()
                        .sendPacket(
                           new PlayerInteractItemC2SPacket(
                              hand2,
                              0,
                              mc.player.getYaw(),
                              mc.player.getPitch()
                           )
                        );
                     val.setCancelled(
                        true
                     );
                  }
               }
            }
         }
      }
   }
}
