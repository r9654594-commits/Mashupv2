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

public class NameHelper_2 {
   public final String name;
   public final Item item;
   public final KeybindSetting bind;

   public NameHelper_2(String name, Item item, KeybindSetting bind) {
      this.name = name;
      this.item = item;
      this.bind = bind;
   }

   public String name() {
      return this.name;
   }

   public Item item() {
      return this.item;
   }

   public KeybindSetting bind() {
      return this.bind;
   }
}
