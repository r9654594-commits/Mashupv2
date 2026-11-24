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

public class TrackedLootSource {
   public final BlockPos pos;
   public final LootSourceType type;
   public final boolean isLooted;

   public TrackedLootSource(BlockPos pos, LootSourceType type, boolean isLooted) {
      this.pos = pos;
      this.type = type;
      this.isLooted = isLooted;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public LootSourceType type() {
      return this.type;
   }

   public boolean isLooted() {
      return this.isLooted;
   }
}
