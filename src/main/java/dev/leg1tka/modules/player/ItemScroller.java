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


public class ItemScroller extends Module {
   public static ItemScroller INSTANCE = new ItemScroller();
   public final NumberSetting delay = new NumberSetting("Задержка", 50.0F, 0.0F, 200.0F, 1.0F);
   private long lastQuickMoveAt;

   public ItemScroller() {
      super("ItemScroller", "Убирает задержку перемещения предметов", Category.PLAYER);
      this.addSettings(new Setting[]{this.delay});
   }

   public boolean canQuickMove() {
      long val = System.currentTimeMillis();
      if (val
            - this.lastQuickMoveAt
         < (long)this.delay.get()) {
         return false;
      } else {
         this.lastQuickMoveAt = val;
         return true;
      }
   }

   public void resetTimer() {
      this.lastQuickMoveAt = 0L;
   }

   @Override
   public void onDisable() {
      this.resetTimer();
      super.onDisable();
   }
}
