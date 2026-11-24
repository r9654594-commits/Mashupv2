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

public class NameHelper_1 {
   public final String name;
   public final BlockPos pos;

   public NameHelper_1(String name, BlockPos pos) {
      this.name = name;
      this.pos = pos;
   }

   public String name() {
      return this.name;
   }

   public BlockPos pos() {
      return this.pos;
   }
}
