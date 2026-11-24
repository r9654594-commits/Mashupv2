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


import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class StackHelper {
   public final ItemStack stack;
   public final Vec3d pos;
   public final int ticks;

   public StackHelper(ItemStack stack, Vec3d pos, int ticks) {
      this.stack = stack;
      this.pos = pos;
      this.ticks = ticks;
   }

   public ItemStack stack() {
      return this.stack;
   }

   public Vec3d pos() {
      return this.pos;
   }

   public int ticks() {
      return this.ticks;
   }
}
