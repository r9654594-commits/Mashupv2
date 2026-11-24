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


import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityHelper {
   public final Entity entity;
   public final Vec3d hitPos;

   public EntityHelper(Entity entity, Vec3d hitPos) {
      this.entity = entity;
      this.hitPos = hitPos;
   }

   public Entity entity() {
      return this.entity;
   }

   public Vec3d hitPos() {
      return this.hitPos;
   }
}
