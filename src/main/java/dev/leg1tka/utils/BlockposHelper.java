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


import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockposHelper {
   public final BlockPos blockPos;
   public final Vec3d pos;
   public final long endTime;

   public BlockposHelper(BlockPos blockPos, Vec3d pos, long endTime) {
      this.blockPos = blockPos;
      this.pos = pos;
      this.endTime = endTime;
   }

   public BlockPos blockPos() {
      return this.blockPos;
   }

   public Vec3d pos() {
      return this.pos;
   }

   public long endTime() {
      return this.endTime;
   }
}
