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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TpGrim extends Module {
   public static TpGrim INSTANCE = new TpGrim();
   public final NumberSetting targetX;
   public final NumberSetting targetY;
   public final NumberSetting targetZ;
   private final Map<String, Vec3d> positions;
   private Vec3d dest;
   private boolean active;
   private int waitTicks;
   private int stepCount;

   public TpGrim() {
      super("TpGrim", Category.MOVEMENT);
      String var1 = "TpGrim";
      this.targetX = new NumberSetting(
         "X",
         0.0F,
         -3.0E7F,
         3.0E7F,
         1.0F
      );
      this.targetY = new NumberSetting(
         "Y",
         64.0F,
         -64.0F,
         320.0F,
         1.0F
      );
      this.targetZ = new NumberSetting(
         "Z",
         0.0F,
         -3.0E7F,
         3.0E7F,
         1.0F
      );
      this.positions = new HashMap<>();
      this.dest = null;
      this.active = false;
      this.waitTicks = 0;
      this.stepCount = 0;
      this.addSettings(new Setting[]{
         this.targetX,
         this.targetY,
         this.targetZ
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.player != null) {
         this.start(this.targetX.getValue().doubleValue(), this.targetY.getValue().doubleValue(), this.targetZ.getValue().doubleValue());
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.active = false;
      this.dest = null;
      this.waitTicks = 0;
      this.stepCount = 0;
   }

   public void start(
      double val,
      double val2,
      double val3
   ) {
      this.dest = new Vec3d(
         val,
         val2,
         val3
      );
      this.active = true;
      this.waitTicks = 0;
      this.stepCount = 0;
      String var7 = "ТП -> %.0f %.0f %.0f";
      Object[] var10001 = new Object[3];
      var10001[0] = val;
      var10001[1] = val2;
      var10001[2] = val3;
      ChatUtil.sendMessage(String.format(var7, var10001));
   }

   public boolean startToPlayer(
      String val
   ) {
      Vec3d pos = this.positions
         .get(
            val.toLowerCase()
         );
      if (pos
         == null) {
         ChatUtil.sendMessage(
            "Игрок не найден: "
               + val
         );
         return false;
      } else {
         String var3 = "ТП к игроку %s (%.0f %.0f %.0f)";
         Object[] var10001 = new Object[4];
         var10001[0] = val;
         var10001[1] = pos.x;
         var10001[2] = pos.y;
         var10001[3] = pos.z;
         ChatUtil.sendMessage(String.format(var3, var10001));
         this.start(
            pos.x,
            pos.y,
            pos.z
         );
         return true;
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val5
   ) {
      if (mc.player != null && mc.world != null) {
         for (PlayerEntity player2 : mc.world
            .getPlayers()) {
            if (player2
               != mc.player) {
               this.positions
                  .put(
                     player2.getName()
                        .getString()
                        .toLowerCase(),
                     player2.getPos()
                  );
            }
         }

         if (this.active && this.dest != null) {
            if (this.waitTicks > 0) {
               this.waitTicks--;
               this.sendPos(mc.player.getPos());
               mc.player.setVelocity(0.0, 0.0, 0.0);
            } else {
               Vec3d vec = mc.player
                  .getPos();
               double d2 = Math.hypot(
                  this.dest.x
                     - vec.x,
                  this.dest.z
                     - vec.z
               );
               if (d2
                  < 1.5) {
                  this.active = false;
                  mc.player.setVelocity(0.0, 0.0, 0.0);
                  mc.player.fallDistance = 0.0F;
                  ChatUtil.sendMessage(
                     String.format(
                        "Прибыли! (%d шагов)", this.stepCount
                     )
                  );
                  this.setEnabled(false);
               } else {
                  Vec3d vec2 = this.dest
                     .subtract(
                        vec
                     )
                     .normalize();
                  double d3 = Math.min(
                     d2,
                     ThreadLocalRandom.current().nextDouble(7.0, 9.5)
                  );
                  double d4 = ThreadLocalRandom.current()
                     .nextDouble(-0.4, 0.4);
                  Vec3d vec3 = new Vec3d(
                        -vec2.z,
                        0.0,
                        vec2.x
                     )
                     .multiply(
                        d4
                     );
                  Vec3d vec4 = vec.add(
                        vec2.multiply(
                           d3
                        )
                     )
                     .add(
                        vec3
                     );
                  if (this.isHole(
                     mc.world,
                     vec4.x,
                     vec4.z,
                     vec.y
                  )) {
                     vec4 = this.bypassHole(
                        mc.world,
                        vec,
                        vec4
                     );
                  }

                  double d5 = this.getRealSurfaceY(
                     mc.world,
                     vec4.x,
                     vec4.z
                  );
                  boolean flag = d5
                        - vec.y
                     > 2.0;
                  double d6 = flag
                     ? this.findFullBuriedY(
                        mc.world,
                        vec4.x,
                        vec4.z
                     )
                     : this.findHalfBuriedY(
                        mc.world,
                        vec4.x,
                        vec4.z
                     );
                  Vec3d vec5 = new Vec3d(
                     vec4.x,
                     d6,
                     vec4.z
                  );
                  this.sendPos(
                     vec5
                  );
                  mc.player
                     .setPosition(
                        vec5.x,
                        vec5.y,
                        vec5.z
                     );
                  mc.player.setVelocity(0.0, 0.0, 0.0);
                  mc.player.fallDistance = 0.0F;
                  this.stepCount++;
                  if (this.stepCount % 4 == 0) {
                     this.waitTicks = ThreadLocalRandom.current()
                        .nextInt(
                           12,
                           18
                        );
                  } else {
                     this.waitTicks = ThreadLocalRandom.current()
                        .nextInt(
                           2,
                           4
                        );
                  }
               }
            }
         }
      }
   }

   private boolean isSolid(
      BlockState val
   ) {
      return !val.isAir()
         && val.getFluidState()
            .isEmpty()
         && val.blocksMovement();
   }

   private double getRealSurfaceY(
      World val,
      double val2,
      double val3
   ) {
      int n = this.MathHelperFloor(
         val2
      );
      int n2 = this.MathHelperFloor(
         val3
      );
      int n3 = 320;
      int n4 = val.getBottomY();

      for (int i = n3;
         i
            >= n4;
         i--
      ) {
         if (this.isSolid(
            val.getBlockState(
               new BlockPos(
                  n,
                  i,
                  n2
               )
            )
         )) {
            return (double)i
               + 1.0;
         }
      }

      return (double)n4
         + 1.0;
   }

   private double findHalfBuriedY(
      World val,
      double val2,
      double val3
   ) {
      return this.getRealSurfaceY(
            val,
            val2,
            val3
         )
         - 0.9;
   }

   private double findFullBuriedY(
      World val,
      double val2,
      double val3
   ) {
      int n = this.MathHelperFloor(
         val2
      );
      int n2 = this.MathHelperFloor(
         val3
      );
      double d2 = this.getRealSurfaceY(
         val,
         val2,
         val3
      );
      int n3 = (int)Math.floor(
            d2
         )
         - 1;
      int n4 = val.getBottomY();

      for (int i = n3;
         i
            >= Math.max(
               n4,
               n3
                  - 5
            );
         i--
      ) {
         BlockState state = val.getBlockState(
            new BlockPos(
               n,
               i,
               n2
            )
         );
         BlockState state2 = val.getBlockState(
            new BlockPos(
               n,
               i
                  + 1,
               n2
            )
         );
         if (this.isSolid(
               state
            )
            && this.isSolid(
               state2
            )) {
            return (double)i
               + 0.1;
         }
      }

      return d2
         - 0.9;
   }

   private boolean isHole(
      World val,
      double val2,
      double val3,
      double val4
   ) {
      return val4
            - this.getRealSurfaceY(
               val,
               val2,
               val3
            )
         > 4.0;
   }

   private Vec3d bypassHole(
      World val,
      Vec3d val2,
      Vec3d val3
   ) {
      Vec3d vec = val3.subtract(
            val2
         )
         .normalize();
      Vec3d vec2 = new Vec3d(
         -vec.z,
         0.0,
         vec.x
      );
      double[] var10000 = new double[6];
      var10000[0] = 2.0;
      var10000[1] = -2.0;
      var10000[2] = 4.0;
      var10000[3] = -4.0;
      var10000[4] = 6.0;
      var10000[5] = -6.0;
      double[] val4 = var10000;

      for (double d2 : val4) {
         Vec3d vec3 = val3.add(
            vec2.multiply(
               d2
            )
         );
         if (!this.isHole(
            val,
            vec3.x,
            vec3.z,
            val2.y
         )) {
            return vec3;
         }
      }

      return val2.add(
         this.dest
            .subtract(
               val2
            )
            .normalize()
            .multiply(3.0)
      );
   }

   private void sendPos(
      Vec3d val
   ) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         mc.getNetworkHandler()
            .sendPacket(
               new Full(
                  val.x,
                  val.y,
                  val.z,
                  mc.player.getYaw(),
                  mc.player.getPitch(),
                  true,
                  false
               )
            );
      }
   }

   private int MathHelperFloor(
      double val
   ) {
      int n = (int)val;
      return val
            < (double)n
         ? n
            - 1
         : n;
   }
}
