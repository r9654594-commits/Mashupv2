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


public class DeltayawHolder {
   public float deltaYaw;
   public float deltaPitch;
   public float signedYaw;
   public float signedPitch;
   public float rotationSpeed;
   public float jitterScore;
   public float jitterYawSpeed;
   public float jitterPitchSpeed;
   public long timeDeltaMs;
   public boolean isJittering;
   public boolean attacking;
   public boolean combatFrame;
   public boolean instantSnap;

   public DeltayawHolder() {
   }

   public boolean isCombatLike() {
      return this.attacking || this.combatFrame || this.instantSnap;
   }
}
