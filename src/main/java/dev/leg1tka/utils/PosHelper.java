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

public class PosHelper {
   public final Vec3d pos;
   public final float seconds;
   public final ItemStack stack;

   public PosHelper(Vec3d pos, float seconds, ItemStack stack) {
      this.pos = pos;
      this.seconds = seconds;
      this.stack = stack;
   }

   public Vec3d pos() {
      return this.pos;
   }

   public float seconds() {
      return this.seconds;
   }

   public ItemStack stack() {
      return this.stack;
   }
}
