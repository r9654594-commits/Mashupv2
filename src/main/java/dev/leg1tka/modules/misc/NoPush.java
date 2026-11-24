package dev.leg1tka.modules.misc;


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

import lombok.Generated;

public class NoPush extends Module {
   public static NoPush INSTANCE = new NoPush();
   private MultiBooleanSetting collisionList;

   public NoPush() {
      super(
         "NoPush",
         "Отключает коллизию",
         Category.MISC
      );
      this.collisionList = new MultiBooleanSetting("Коллизия", new BooleanSetting[]{
         new BooleanSetting("Блоки", true),
         new BooleanSetting("Вода", false),
         new BooleanSetting(
         "Удочик", true
      ),
         new BooleanSetting(
         "Игроки", true
      )
      });
      this.addSettings(new Setting[]{this.collisionList});
   }

   @Generated
   public MultiBooleanSetting getCollisionList() {
      return this.collisionList;
   }

   @Generated
   public void setCollisionList(
      MultiBooleanSetting val
   ) {
      this.collisionList = val;
   }
}
