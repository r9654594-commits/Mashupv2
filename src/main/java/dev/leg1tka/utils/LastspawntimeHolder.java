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


import java.util.ArrayList;
import java.util.List;

public class LastspawntimeHolder {
   public long lastSpawnTime;
   public boolean isFriend = false;
   public boolean ownerResolved = false;
   public final List<PosHolder> points = new ArrayList<>();

   public LastspawntimeHolder() {
   }
}
