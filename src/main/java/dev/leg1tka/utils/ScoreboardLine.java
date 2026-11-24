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


import net.minecraft.text.Text;

public class ScoreboardLine {
   public final Text name;
   public final Text score;
   public final int scoreWidth;

   public ScoreboardLine(
      Text text,
      Text text2,
      int n
   ) {
      this.name = text;
      this.score = text2;
      this.scoreWidth = n;
   }

   public Text name() {
      return this.name;
   }

   public Text score() {
      return this.score;
   }

   public int scoreWidth() {
      return this.scoreWidth;
   }
}
