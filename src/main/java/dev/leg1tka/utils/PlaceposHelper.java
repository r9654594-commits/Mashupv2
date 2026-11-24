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

public class PlaceposHelper {
   public final BlockPos placePos;
   public final int ticksToImpact;

   public PlaceposHelper(BlockPos placePos, int ticksToImpact) {
      this.placePos = placePos;
      this.ticksToImpact = ticksToImpact;
   }

   public BlockPos placePos() {
      return this.placePos;
   }

   public int ticksToImpact() {
      return this.ticksToImpact;
   }
}
