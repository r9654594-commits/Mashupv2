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

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventBinding;
import dev.leg1tka.api.events.implement.EventPlayerSync;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.item.Items;

public class ClickPearl extends Module {
   public static final ClickPearl INSTANCE = new ClickPearl();
   private final ModeSetting mode;
   private final BooleanSetting bypassgrim;
   private final KeybindSetting key;
   private boolean used;
   private int ticks;

   public ClickPearl() {
      super(
         "ClickPearl",
         "Click Pearl",
         Category.MISC
      );
      this.mode = new ModeSetting("Мод", "Обычный", new String[]{"Обычный", "Легитный", "WellMine"});
      this.bypassgrim = new BooleanSetting(
         "Обходить Grim",
         true
      );
      this.key = new KeybindSetting(
         "Клавиша броска",
         -98
      );
      this.addSettings(new Setting[]{
         this.mode,
         this.bypassgrim,
         this.key
      });
   }

   @EventLink
   public void onKey(
      EventBinding val
   ) {
      if (this.isEnable() && mc.player != null) {
         if (val.getKey()
            == this.key.getKey()) {
            if (this.bypassgrim.isState()) {
               QueueHelper.queue(
                  () -> {
                     if (this.mode
                        .is(
                           "Обычный"
                        )) {
                        SearchitemHelper.swapAndUseHvH(Items.ENDER_PEARL);
                     } else if (this.mode
                        .is(
                           "WellMine"
                        )) {
                        SearchitemHelper.swapAndUseWellMine(Items.ENDER_PEARL);
                     } else {
                        this.used = true;
                     }
                  }
               );
            } else if (this.mode
               .is(
                  "Обычный"
               )) {
               SearchitemHelper.swapAndUseHvH(Items.ENDER_PEARL);
            } else if (this.mode
               .is(
                  "WellMine"
               )) {
               SearchitemHelper.swapAndUseWellMine(Items.ENDER_PEARL);
            } else {
               this.used = true;
            }
         }
      }
   }

   @EventLink
   public void onPlayerTick(
      EventUpdate val2
   ) {
      if (mc.player != null) {
         if (!this.used && this.ticks > 0) {
            this.ticks--;
         }

         if (this.used || this.ticks > 0) {
            mc.player.setSprinting(false);
         }
      }
   }

   @EventLink
   public void onPlayerSync(
      EventPlayerSync val2
   ) {
      if (mc.player != null && this.used) {
         int n = SearchitemHelper.searchItem(
            Items.ENDER_PEARL, 0, 9
         );
         if (n
            != -1) {
            SearchitemHelper.swapAndUseLegit(Items.ENDER_PEARL);
         } else {
            if (this.ticks == 0) {
               this.ticks++;
               return;
            }

            SearchitemHelper.swapAndUseLegit(Items.ENDER_PEARL);
            this.ticks = 2;
         }

         this.used = false;
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.used = false;
      this.ticks = 0;
   }
}
