package dev.leg1tka.modules.combat;


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
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.TridentItem;

public class ItemRelease extends Module {
   public static ItemRelease INSTANCE = new ItemRelease();
   private final MultiBooleanSetting items;
   private final NumberSetting tickBow;

   public ItemRelease() {
      super("ItemRelease", Category.COMBAT);
      String var3 = "Предметы";
      BooleanSetting[] var10004 = new BooleanSetting[3];
      var10004[0] = new BooleanSetting("Лук", true);
      var10004[1] = new BooleanSetting("Трезубец", false);
      int var10006 = 2;
      var10004[var10006] = new BooleanSetting("Арбалет", true);
      this.items = new MultiBooleanSetting(var3, var10004);
      this.tickBow = new NumberSetting(
            "Задержка выстрела", 2.5F, 2.0F, 5.0F, 0.05F
         )
         .visible(
            () -> this.items.is("Лук")
         );
      this.addSettings(new Setting[]{
         this.items,
         this.tickBow
      });
   }

   @EventLink
   public void onUpdate(
      EventUpdate val
   ) {
      if (mc.player != null && mc.world != null) {
         MultiBooleanSetting var10000 = this.items;
         if (var10000.is("Лук")
            && mc.player.getMainHandStack().getItem() instanceof BowItem
            && mc.player.isUsingItem()
            && (float)mc.player.getItemUseTime() >= this.tickBow.getValue().floatValue()) {
            mc.interactionManager.stopUsingItem(mc.player);
         }

         var10000 = this.items;
         if (var10000.is("Трезубец")
            && mc.player.getMainHandStack().getItem() instanceof TridentItem
            && mc.player.isUsingItem()
            && mc.player.getItemUseTime() >= 10) {
            mc.interactionManager.stopUsingItem(mc.player);
         }

         var10000 = this.items;
         if (var10000.is("Арбалет")
            && mc.player.getMainHandStack().getItem() instanceof CrossbowItem
            && mc.player.isUsingItem()
            && mc.player.getItemUseTime() >= CrossbowItem.getPullTime(mc.player.getMainHandStack(), mc.player)) {
            mc.interactionManager.stopUsingItem(mc.player);
         }
      }
   }
}
