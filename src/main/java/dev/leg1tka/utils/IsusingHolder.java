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

public class IsusingHolder {
   public boolean isUsing = false;
   public ItemStack lastUsedStack = null;
   public long useStartTime = 0L;
   public int lastPearlCount = 0;

   public IsusingHolder() {
   }
}
