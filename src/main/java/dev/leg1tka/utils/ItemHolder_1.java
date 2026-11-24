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


import net.minecraft.item.Item;

public class ItemHolder_1 {
   public final Item item;
   public final boolean isheal;
   public final long time;
   public ItemHolder_1(
      Item item2,
      boolean flag,
      long time2
   ) {
      this.item = item2;
      this.isheal = flag;
      this.time = time2;
   }
}
