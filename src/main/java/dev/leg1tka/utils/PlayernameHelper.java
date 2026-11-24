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


public class PlayernameHelper {
   public final String playerName;
   public final int percent;
   public final double distance;

   public PlayernameHelper(String playerName, int percent, double distance) {
      this.playerName = playerName;
      this.percent = percent;
      this.distance = distance;
   }

   public String playerName() {
      return this.playerName;
   }

   public int percent() {
      return this.percent;
   }

   public double distance() {
      return this.distance;
   }
}
