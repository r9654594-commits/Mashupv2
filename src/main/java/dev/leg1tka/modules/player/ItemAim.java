package dev.leg1tka.modules.player;


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
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ItemAim extends Module {
   public static ItemAim INSTANCE = new ItemAim();
   public MultiBooleanSetting element;

   public ItemAim() {
      super(
         "AutoPilot",
         "Автоматически наводиться на предмет",
         Category.PLAYER
      );
      this.element = new MultiBooleanSetting("Лутать", new BooleanSetting[]{
         new BooleanSetting(
         "Шары",
         true
      ),
         new BooleanSetting(
         "Элитры",
         true
      )
      });
      this.addSettings(new Setting[]{this.element});
   }

   @EventLink
   public void onEvent(
      EventUpdate val2
   ) {
      if (mc.player != null && mc.world != null) {
         ItemEntity entity2 = this.findTargetItem();
         if (entity2
            != null) {
            Vec2f rotations = this.getItemRotations(
               entity2
            );
            RotationStorage.update(
               new Angle(
                  rotations.x,
                  rotations.y
               ),
               360.0F,
               360.0F,
               360.0F,
               360.0F,
               0,
               1,
               false
            );
         }
      }
   }

   private ItemEntity findTargetItem() {
      ItemEntity entity2 = null;
      double d = Double.MAX_VALUE;

      for (Entity entity3 : mc.world
         .getEntities()) {
         if (entity3 instanceof ItemEntity
            )
          {
            ItemEntity entity4 = (ItemEntity)entity3;
            if (this.isWantedItem(
               entity4
            )) {
               double d2 = mc.player
                  .squaredDistanceTo(
                     entity4
                  );
               if (d2
                  < d
                  )
                {
                  d = d2;
                  entity2 = entity4;
               }
            }
         }
      }

      return entity2;
   }

   private boolean isWantedItem(
      ItemEntity val
   ) {
      return this.element
               .is(
                  "Шары"
               )
            && val.getStack()
               .isOf(Items.PLAYER_HEAD)
         || this.element
               .is(
                  "Элитры"
               )
            && val.getStack()
               .isOf(Items.ELYTRA);
   }

   private Vec2f getItemRotations(
      ItemEntity val
   ) {
      Vec3d vec = val.getBoundingBox()
         .getCenter();
      return RayTraceUtil.getRotations(
         vec
      );
   }
}
