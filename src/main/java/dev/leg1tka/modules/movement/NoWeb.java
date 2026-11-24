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
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class NoWeb extends Module {
   public static NoWeb INSTANCE = new NoWeb();
   public ModeSetting web;

   public NoWeb() {
      super("NoWeb", "Убирает замедление от паутины", Category.MOVEMENT);
      this.web = new ModeSetting("Мод", "Коллизия", new String[]{"Коллизия", "Обычный", "Grim"});
      this.addSettings(new Setting[]{this.web});
   }

   @EventLink
   public void onUpdate(
      EventUpdate val
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.web.is("Коллизия")) {
            BlockPos pos = mc.player
               .getBlockPos();

            for (int i = -1;
               i
                  <= 1;
               i++
            ) {
               for (int i2 = 0;
                  i2
                     <= 2;
                  i2++
               ) {
                  for (int i3 = -1;
                     i3
                        <= 1;
                     i3++
                  ) {
                     BlockPos pos2 = pos.add(
                        i,
                        i2,
                        i3
                     );
                     if (mc.world
                           .getBlockState(
                              pos2
                           )
                           .getBlock()
                        == Blocks.COBWEB) {
                        mc.player
                           .networkHandler
                           .sendPacket(
                              new PlayerActionC2SPacket(
                                 Action.STOP_DESTROY_BLOCK,
                                 pos2,
                                 Direction.UP
                              )
                           );
                     }
                  }
               }
            }
         }

         if (this.web.is("Обычный") && (!mc.player.isSneaking() || !mc.player.isOnGround())) {
            boolean flag = false;
            boolean flag2 = false;

            for (double d = -0.295;
               d
                  <= 0.295;
               d += 0.05
            ) {
               for (double d2 = -0.295;
                  d2
                     <= 0.295;
                  d2 += 0.05
               ) {
                  for (double d3 = (double)mc.player
                        .getStandingEyeHeight();
                     d3
                        >= 0.0;
                     d3 -= 0.1
                  ) {
                     BlockPos pos3 = BlockPos.ofFloored(
                        mc.player.getX()
                           + d,
                        mc.player.getY()
                           + d3,
                        mc.player.getZ()
                           + d2
                     );
                     if (mc.world
                           .getBlockState(
                              pos3
                           )
                           .getBlock()
                        == Blocks.COBWEB) {
                        flag = true;
                        break;
                     }
                  }
               }
            }

            if (!flag
               )
             {
               for (double var22 = -0.295; var22 <= 0.295; var22 += 0.05) {
                  for (double d4 = -0.295;
                     d4
                        <= 0.295;
                     d4 += 0.05
                  ) {
                     BlockPos pos4 = BlockPos.ofFloored(
                        mc.player.getX() + var22,
                        mc.player.getY(),
                        mc.player.getZ()
                           + d4
                     );
                     if (mc.world
                           .getBlockState(
                              pos4
                           )
                           .getBlock()
                        == Blocks.COBWEB) {
                        flag2 = true;
                        break;
                     }
                  }
               }
            }

            BlockPos pos5 = BlockPos.ofFloored(
               mc.player.getX(), mc.player.getY() + (double)mc.player.getStandingEyeHeight() + 0.2F, mc.player.getZ()
            );
            if (!flag
               && !flag2
               && mc.world
                     .getBlockState(
                        pos5
                     )
                     .getBlock()
                  == Blocks.COBWEB) {
               flag = true;
            }

            if (flag
               || flag2
               )
             {
               if (mc.options.jumpKey.isPressed()) {
                  mc.player.setVelocity(0.0, 0.8, 0.0);
               } else if (mc.options.sneakKey.isPressed()) {
                  mc.player.setVelocity(0.0, -0.8, 0.0);
               } else {
                  mc.player.setVelocity(0.0, 0.0, 0.0);
               }

               SetmotionHelper.setMotion(0.21);
            }
         }

         if (this.web.is("Grim") && mc.player != null) {
            boolean flag3 = false;
            Box box = mc.player
               .getBoundingBox();

            for (BlockPos pos6 : BlockPos.iterate(
               MathHelper.floor(
                  box.minX
               ),
               MathHelper.floor(
                  box.minY
               ),
               MathHelper.floor(
                  box.minZ
               ),
               MathHelper.floor(
                  box.maxX
               ),
               MathHelper.floor(
                  box.maxY
               ),
               MathHelper.floor(
                  box.maxZ
               )
            )) {
               if (mc.world
                  .getBlockState(
                     pos6
                  )
                  .isOf(Blocks.COBWEB)) {
                  Box box2 = new Box(
                     pos6
                  );
                  if (box.intersects(
                     box2
                  )) {
                     flag3 = true;
                  }
               }
            }

            if (flag3
               )
             {
               float y2 = mc.player
                  .getYaw();
               double d5 = 0.0;
               double d6 = 0.0;
               if (mc.player.input.playerInput.forward()) {
                  d5++;
               }

               if (mc.player.input.playerInput.backward()) {
                  d5--;
               }

               if (mc.player.input.playerInput.left()) {
                  d6++;
               }

               if (mc.player.input.playerInput.right()) {
                  d6--;
               }

               if (d5
                     != 0.0
                  || d6
                     != 0.0) {
                  if (d5
                     != 0.0) {
                     if (d6
                        > 0.0) {
                        y2 += (float)(
                           d5
                                 > 0.0
                              ? -45
                              : 45
                        );
                     } else if (d6
                        < 0.0) {
                        y2 += (float)(
                           d5
                                 > 0.0
                              ? 45
                              : -45
                        );
                     }

                     d6 = 0.0;
                     if (d5
                        > 0.0) {
                        d5 = 1.0;
                     } else {
                        d5 = -1.0;
                     }
                  }

                  double d7 = Math.toDegrees(
                        Math.atan2(
                           d6,
                           d5
                        )
                     )
                     + (double)y2;
                  y2 = (float)(
                     (
                           d7
                                 % 360.0
                              + 360.0
                        )
                        % 360.0
                  );
               }

               double d8 = mc.player
                  .getVelocity()
                  .y;
               if (!mc.options.jumpKey.isPressed()) {
                  if (mc.options.sneakKey.isPressed()) {
                     d8 = -3.6;
                  } else {
                     d8 = 0.0;
                  }
               } else {
                  d8 = d5
                           == 0.0
                        && d6
                           == 0.0
                     ? 1.4
                     : 1.2;
               }

               float y3 = mc.player
                     .getYaw()
                  * (float) (Math.PI / 180.0);
               float f = (float)d5
                  * 0.633F;
               float f2 = (float)d6
                  * 0.633F;
               if (f
                     == 0.0F
                  && f2
                     == 0.0F) {
                  mc.player
                     .setVelocity(
                        0.0,
                        d8,
                        0.0
                     );
               } else {
                  mc.player
                     .setVelocity(
                        (double)(
                              -MathHelper.sin(
                                    y3
                                 )
                                 * f
                           )
                           + (double)(
                              MathHelper.cos(
                                    y3
                                 )
                                 * f2
                           ),
                        d8,
                        (double)(
                              MathHelper.cos(
                                    y3
                                 )
                                 * f
                           )
                           + (double)(
                              MathHelper.sin(
                                    y3
                                 )
                                 * f2
                           )
                     );
               }
            }
         }
      }
   }
}
