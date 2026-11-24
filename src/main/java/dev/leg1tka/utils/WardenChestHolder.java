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

public class WardenChestHolder {
   public final BlockPos pos;
   public WardenChestState state = WardenChestState.UNKNOWN;
   public long readyTime = 0L;

   public WardenChestHolder(
      BlockPos pos2
   ) {
      this.pos = pos2;
   }
}
