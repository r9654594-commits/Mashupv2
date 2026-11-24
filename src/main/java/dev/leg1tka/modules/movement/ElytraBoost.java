package dev.leg1tka.modules.movement;



import dev.leg1tka.modules.combat.Aura;
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

import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class ElytraBoost extends Module {
   private static final String[] RANGE_LABELS = new String[]{"0 - 5", "5 - 10", "10 - 15", "15 - 20", "20 - 25", "25 - 30", "30 - 35", "35 - 40", "40 - 45"};
   private static final long DEBUG_MESSAGE_INTERVAL_MS = 800L;
   public static ElytraBoost INSTANCE = new ElytraBoost();
   private final NumberSetting[] yawSpeeds;
   private final NumberSetting[] pitchSpeeds;
   private final ModeSetting mode;
   private final BooleanSetting debug;
   private long lastDebugMessageAt;

   public ElytraBoost() {
      super("ElytraBoost", Category.MOVEMENT);
      String var3 = "Superfirework";
      this.yawSpeeds = new NumberSetting[9];
      this.pitchSpeeds = new NumberSetting[9];
      String var5 = "Сервер";
      String var7 = "Custom";
      String[] var10005 = new String[6];
      var10005[0] = "Custom";
      var10005[1] = "LonyGrief";
      int var10007 = 2;
      var10005[var10007] = "LonyHvh";
      var10007 = 3;
      var10005[var10007] = "BravoHVH";
      var10007 = 4;
      var10005[var10007] = "ReallyWorld";
      var10007 = 5;
      var10005[var10007] = "SlimeWorld";
      this.mode = new ModeSetting(var5, var7, var10005);
      this.debug = new BooleanSetting("Дебаг", false)
         .visible(this::isCustomMode);

      for (int i = 0;
         i
            < this.yawSpeeds.length;
         i++
      ) {
         this.yawSpeeds[i] = new NumberSetting(
               "yaw "
                  + RANGE_LABELS[i],
               1.5F,
               1.5F,
               2.5F,
               0.01F
            )
            .visible(this::isCustomMode);
      }

      for (int i2 = 0;
         i2
            < this.pitchSpeeds.length;
         i2++
      ) {
         this.pitchSpeeds[i2] = new NumberSetting(
               "pitch "
                  + RANGE_LABELS[i2],
               1.5F,
               1.5F,
               2.5F,
               0.01F
            )
            .visible(this::isCustomMode);
      }

      this.addSettings(new Setting[]{
         this.mode,
         this.debug
      });
      this.addSettings(this.yawSpeeds);
      this.addSettings(this.pitchSpeeds);
   }

   public boolean isCustomMode() {
      ModeSetting var10000 = this.mode;
      return var10000.is("Custom");
   }

   public Vec2f getBoostV2() {
      float y2 = mc.player
            != null
         ? mc.player.getYaw()
         : 0.0F;
      float y3 = mc.player
            != null
         ? mc.player.getPitch()
         : 0.0F;
      Aura val = Aura.INSTANCE;
      if (val
            != null
         && val.isEnable()
         && val.getTarget()
            != null) {
         Vec2f rotations = val.getTargetRotations();
         if (rotations
            != null) {
            y2 = rotations.x;
            y3 = rotations.y;
         }
      }

      float f = this.convertValToRange(
         MathHelper.wrapDegrees(
            y2
         )
      );
      float f2 = this.convertValToRange(
         Math.abs(
            y3
         )
      );
      int n = this.getRangeIndex(
         f,
         this.yawSpeeds.length
      );
      int n2 = this.getRangeIndex(
         f2,
         this.pitchSpeeds.length
      );
      float yaw2 = this.yawSpeeds[n]
         .getValue()
         .floatValue();
      float pitch2 = this.pitchSpeeds[n2]
         .getValue()
         .floatValue();
      if (pitch2
         > yaw2
         )
       {
         yaw2 = pitch2;
      }

      this.logDebug(
         n,
         yaw2,
         n2,
         pitch2
      );
      return new Vec2f(
         yaw2,
         pitch2
      );
   }

   private void logDebug(
      int val,
      float val2,
      int val3,
      float val4
   ) {
      if (this.debug.isState()) {
         long val5 = System.currentTimeMillis();
         if (val5
               - this.lastDebugMessageAt
            >= 800L) {
            this.lastDebugMessageAt = val5;
            ChatUtil.sendMessage(
               "yaw "
                  + RANGE_LABELS[val]
                  + ": "
                  + val2
                  + " | pitch "
                  + RANGE_LABELS[val3]
                  + ": "
                  + val4
            );
         }
      }
   }

   private int getRangeIndex(
      float val,
      int val2
   ) {
      return Math.min(
         (int)(
            val
               / 5.0F
         ),
         val2
            - 1
      );
   }

   private float convertValToRange(
      float val
   ) {
      float f = Math.abs(
         val
      );
      if (f
         > 90.0F) {
         f = 180.0F
            - f;
      }

      if (f
         > 45.0F) {
         f = 90.0F
            - f;
      }

      return f;
   }

   @Generated
   public NumberSetting[] getYawSpeeds() {
      return this.yawSpeeds;
   }

   @Generated
   public NumberSetting[] getPitchSpeeds() {
      return this.pitchSpeeds;
   }

   @Generated
   public ModeSetting getMode() {
      return this.mode;
   }

   @Generated
   public BooleanSetting getDebug() {
      return this.debug;
   }

   @Generated
   public long getLastDebugMessageAt() {
      return this.lastDebugMessageAt;
   }

   @Generated
   public void setLastDebugMessageAt(
      long val
   ) {
      this.lastDebugMessageAt = val;
   }
}
