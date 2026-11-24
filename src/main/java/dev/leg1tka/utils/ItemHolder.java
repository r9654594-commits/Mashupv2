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

public class ItemHolder {
   public final Item item;
   public final int count;
   public final boolean isheal;
   public ItemHolder(
      Item item2,
      int n,
      boolean flag
   ) {
      this.item = item2;
      this.count = n;
      this.isheal = flag;
   }
}
