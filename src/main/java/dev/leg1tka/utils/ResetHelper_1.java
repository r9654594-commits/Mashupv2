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



import dev.leg1tka.modules.combat.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ResetHelper_1 extends AuraRotation implements IMinecraft {
   private static final Path DATASET_PATH = Path.of(System.getProperty("user.home"), "Desktop", "data.json");
   private final List<DeltayawHolder> frames = new ArrayList<>();
   private LivingEntity trackedTarget;
   private LivingEntity trackedRotationTarget;
   private Vec3d currentAimPoint;
   private Vec3d targetAimPoint;
   private long lastModified = Long.MIN_VALUE;
   private long lastLoadAttempt;
   private boolean datasetReady;
   private int playbackIndex;
   private int aimPointTicks;
   private int aimPointRefreshTicks;
   private int smoothProfileTicks;
   private float smoothYawStep;
   private float smoothPitchStep;
   private float smoothYaw;
   private float smoothPitch;
   private float yawSmoothFactor = 1.0F;
   private float pitchSmoothFactor = 1.0F;
   private boolean hasRotationState;

   public void reset() {
      this.trackedTarget = null;
      this.trackedRotationTarget = null;
      this.currentAimPoint = null;
      this.targetAimPoint = null;
      this.playbackIndex = 0;
      this.aimPointTicks = 0;
      this.aimPointRefreshTicks = 0;
      this.smoothProfileTicks = 0;
      this.smoothYawStep = 0.0F;
      this.smoothPitchStep = 0.0F;
      this.smoothYaw = 0.0F;
      this.smoothPitch = 0.0F;
      this.yawSmoothFactor = 1.0F;
      this.pitchSmoothFactor = 1.0F;
      this.hasRotationState = false;
   }

   @Override
   public void updateRotations(
      LivingEntity entity
   ) {
      if (mc.player != null
         && entity
            != null) {
         if (!AuraTransition.INSTANCE.handleTransitions()) {
            boolean flag = this.shouldFocus();
            this.ensureDatasetLoaded();
            Vec3d vec = this.selectAimPoint(
               entity,
               flag
            );
            Vec2f rot = RayTraceUtil.getRotations(
               vec
            );
            if (this.datasetReady && !this.frames.isEmpty()) {
               float yaw = mc.player
                  .getYaw();
               float yaw2 = mc.player
                  .getPitch();
               this.syncRotationState(
                  entity,
                  yaw,
                  yaw2
               );
               float yaw3 = MathHelper.wrapDegrees(
                  rot.x
                     - this.smoothYaw
               );
               float yaw4 = rot.y
                  - this.smoothPitch;
               DeltayawHolder val = this.pickFrame(
                  yaw3,
                  yaw4,
                  flag
               );
               this.updateSmoothProfile(
                  val,
                  yaw3,
                  yaw4,
                  flag
               );
               float yaw5 = Math.max(
                  MouseGCD.getGCDValue(), 1.0E-4F
               );
               float yaw6 = this.buildAxisStep(
                  yaw3,
                  val,
                  true,
                  flag
               );
               float yaw7 = this.buildAxisStep(
                  yaw4,
                  val,
                  false,
                  flag
               );
               yaw6 += this.buildJitter(
                  val,
                  yaw3,
                  true,
                  yaw5
               );
               yaw7 += this.buildJitter(
                  val,
                  yaw4,
                  false,
                  yaw5
               );
               this.smoothYawStep = this.smoothAxisStep(
                  this.smoothYawStep,
                  yaw6,
                  yaw3,
                  true,
                  flag
               );
               this.smoothPitchStep = this.smoothAxisStep(
                  this.smoothPitchStep,
                  yaw7,
                  yaw4,
                  false,
                  flag
               );
               float yaw8 = this.quantizeDelta(
                  this.smoothYawStep,
                  yaw3,
                  yaw5,
                  true
               );
               float yaw9 = this.quantizeDelta(
                  this.smoothPitchStep,
                  yaw4,
                  yaw5,
                  false
               );
               this.smoothYaw = MathHelper.wrapDegrees(
                  this.smoothYaw
                     + yaw8
               );
               this.smoothPitch = MathHelper.clamp(
                  this.smoothPitch
                     + yaw9,
                  -89.0F,
                  89.0F
               );
               AuraTransition var19 = AuraTransition.INSTANCE;
               float var20 = this.smoothYaw;
               float var21 = this.smoothPitch;
               float[] floats = var19.process(
                  var20,
                  var21,
                  entity,
                  "test"
               );
               RotationStorage.update(
                  new Angle(
                     floats[0],
                     floats[1]
                  ),
                  360.0F,
                  360.0F,
                  45.0F,
                  45.0F,
                  0,
                  1,
                  Aura.clientLook.isState()
               );
            } else {
               AuraTransition var10000 = AuraTransition.INSTANCE;
               float var10001 = rot.x;
               float var10002 = MathHelper.clamp(
                  rot.y,
                  -89.0F,
                  89.0F
               );
               float[] floats2 = var10000.process(
                  var10001,
                  var10002,
                  entity,
                  "test"
               );
               RotationStorage.update(
                  new Angle(
                     floats2[0],
                     floats2[1]
                  ),
                  360.0F,
                  360.0F,
                  45.0F,
                  45.0F,
                  0,
                  1,
                  Aura.clientLook.isState()
               );
            }
         }
      }
   }

   private boolean shouldFocus() {
      float y2 = mc.player
         .getAttackCooldownProgress(1.5F);
      boolean flag = !mc.player
            .isOnGround()
         && mc.player.getVelocity().y < 0.0
         && mc.player.fallDistance > 0.0F;
      return y2
            >= 0.88F
         || flag;
   }

   private void ensureDatasetLoaded() {
      long time = System.currentTimeMillis();
      if (this.shouldReload(
         time
      )) {
         this.lastLoadAttempt = time;
         long time2 = this.readLastModified();
         if (!this.datasetReady
            || time2
               != this.lastModified) {
            this.frames.clear();
            this.datasetReady = false;
            if (!Files.exists(DATASET_PATH)) {
               this.lastModified = Long.MIN_VALUE;
            } else {
               try (Reader val = Files.newBufferedReader(
                     DATASET_PATH
                  )) {
                  for (JsonElement val2 : JsonParser.parseReader(
                        val
                     )
                     .getAsJsonArray()) {
                     if (val2.isJsonObject()
                        )
                      {
                        DeltayawHolder val3 = this.parseFrame(
                           val2.getAsJsonObject()
                        );
                        if (val3
                           != null) {
                           this.frames
                              .add(
                                 val3
                              );
                        }
                     }
                  }

                  this.datasetReady = !this.frames.isEmpty();
                  this.lastModified = time2;
                  this.reset();
               } catch (IllegalStateException | IOException var12) {
                  this.datasetReady = false;
                  this.lastModified = Long.MIN_VALUE;
                  this.reset();
               }
            }
         }
      }
   }

   private boolean shouldReload(
      long time
   ) {
      return this.datasetReady && !this.frames.isEmpty()
         ? time
               - this.lastLoadAttempt
            >= 3000L
         : time
               - this.lastLoadAttempt
            >= 1500L;
   }

   private long readLastModified() {
      try {
         return Files.exists(DATASET_PATH) ? Files.getLastModifiedTime(DATASET_PATH).toMillis() : Long.MIN_VALUE;
      } catch (IOException var2) {
         return Long.MIN_VALUE;
      }
   }

   private DeltayawHolder parseFrame(
      JsonObject val
   ) {
      float yaw = this.getFloat(
         val,
         "fromYaw"
      );
      float yaw2 = this.getFloat(
         val,
         "toYaw"
      );
      float yaw3 = this.getFloat(
         val,
         "fromPitch"
      );
      float yaw4 = this.getFloat(
         val,
         "toPitch"
      );
      float yaw5 = MathHelper.wrapDegrees(
         yaw2
            - yaw
      );
      float yaw6 = yaw4
         - yaw3;
      float yaw7 = Math.abs(
         yaw5
      );
      float yaw8 = Math.abs(
         yaw6
      );
      float yaw9 = Math.max(
         this.getFloat(
            val,
            "deltaYaw"
         ),
         yaw7
      );
      float yaw10 = Math.max(
         this.getFloat(
            val,
            "deltaPitch"
         ),
         yaw8
      );
      if (yaw9
            <= 0.0F
         && yaw10
            <= 0.0F) {
         return null;
      } else {
         DeltayawHolder val2 = new DeltayawHolder();
         val2.deltaYaw = yaw9;
         val2.deltaPitch = yaw10;
         float var10001;
         if (yaw5
            != 0.0F) {
            var10001 = yaw5;
         } else {
            var10001 = Math.signum(
                  this.getFloat(
                     val,
                     "jitterYawDir"
                  )
               )
               * yaw9;
         }

         val2.signedYaw = var10001;
         if (yaw6
            != 0.0F) {
            var10001 = yaw6;
         } else {
            var10001 = Math.signum(
                  this.getFloat(
                     val,
                     "jitterPitchDir"
                  )
               )
               * yaw10;
         }

         val2.signedPitch = var10001;
         val2.rotationSpeed = Math.max(
            this.getFloat(
               val,
               "rotationSpeed"
            ),
            0.0F
         );
         val2.jitterScore = Math.max(
            this.getFloat(
               val,
               "jitterScore"
            ),
            0.0F
         );
         val2.jitterYawSpeed = Math.max(
            this.getFloat(
               val,
               "jitterYawSpeed"
            ),
            0.0F
         );
         val2.jitterPitchSpeed = Math.max(
            this.getFloat(
               val,
               "jitterPitchSpeed"
            ),
            0.0F
         );
         val2.isJittering = this.getBoolean(
            val,
            "isJittering"
         );
         val2.attacking = this.getBoolean(
            val,
            "attacking"
         );
         val2.combatFrame = this.getBoolean(
            val,
            "isCombatFrame"
         );
         val2.instantSnap = this.getBoolean(
            val,
            "isInstantSnap"
         );
         long var19;
         if (val.has(
            "timeDeltaMs"
         )) {
            var19 = val.get(
                  "timeDeltaMs"
               )
               .getAsLong();
         } else {
            var19 = 50L;
         }

         val2.timeDeltaMs = Math.max(
            1L, var19
         );
         return val2;
      }
   }

   private DeltayawHolder pickFrame(
      float f,
      float f2,
      boolean flag
   ) {
      float yaw = Math.abs(
            f
         )
         + Math.abs(
               f2
            )
            * 0.82F;
      int i = this.frames
         .size();
      int i2 = Math.min(
         i,
         flag
            ? 78
            : 56
      );
      int i3 = this.playbackIndex
         % i;
      float yaw2 = Float.MAX_VALUE;

      for (int i4 = 0;
         i4
            < i2;
         i4++
      ) {
         int i5 = (
               this.playbackIndex
                  + i4
            )
            % i;
         DeltayawHolder val = this.frames
            .get(
               i5
            );
         float yaw3 = val.deltaYaw
            + val.deltaPitch
               * 0.82F;
         float yaw4 = Math.abs(
            yaw3
               - yaw
         );
         if (flag
            )
          {
            if (!val.isCombatLike()
               )
             {
               yaw4 += 3.0F;
            }

            if (val.instantSnap
               )
             {
               yaw4 -= 0.5F;
            }
         } else if (val.isCombatLike()
            )
          {
            yaw4++;
         }

         if (yaw
               < 10.0F
            && val.isJittering
            )
          {
            yaw4 -= Math.min(
                  val.jitterScore,
                  2.6F
               )
               * 0.2F;
         }

         yaw4 += (float)i4
            * 0.032F;
         if (yaw4
            < yaw2
            )
          {
            yaw2 = yaw4;
            i3 = i5;
         }
      }

      this.playbackIndex = (
            i3
               + 1
         )
         % i;
      return this.frames
         .get(
            i3
         );
   }

   private void updateSmoothProfile(
      DeltayawHolder val,
      float f,
      float f2,
      boolean flag
   ) {
      if (this.smoothProfileTicks > 0) {
         this.smoothProfileTicks--;
      } else {
         ThreadLocalRandom val2 = ThreadLocalRandom.current();
         float yaw = MathHelper.clamp(
            (
                  Math.abs(
                        f
                     )
                     + Math.abs(
                        f2
                     )
               )
               / 32.0F,
            0.0F,
            1.0F
         );
         float yaw2 = MathHelper.clamp(
            (float)val.timeDeltaMs
               / 120.0F,
            0.0F,
            1.0F
         );
         float yaw3 = flag
            ? 0.94F
            : 0.86F;
         float yaw4 = flag
            ? 1.12F
            : 1.04F;
         float yaw5 = flag
            ? 0.92F
            : 0.84F;
         float yaw6 = flag
            ? 1.08F
            : 1.0F;
         this.yawSmoothFactor = val2.nextFloat(
            yaw3,
            yaw4
               + yaw
                  * 0.08F
               + yaw2
                  * 0.04F
         );
         this.pitchSmoothFactor = val2.nextFloat(
            yaw5,
            yaw6
               + yaw
                  * 0.06F
               + yaw2
                  * 0.03F
         );
         if (val.isCombatLike()
            )
          {
            this.yawSmoothFactor *= 1.02F;
            this.pitchSmoothFactor *= 1.015F;
         }

         this.smoothProfileTicks = val2.nextInt(
            flag
               ? 2
               : 3,
            flag
               ? 6
               : 8
         );
      }
   }

   private float buildAxisStep(
      float f,
      DeltayawHolder val,
      boolean flag,
      boolean flag2
   ) {
      float yaw = Math.abs(
         f
      );
      if (yaw
         <= 1.0E-4F) {
         return 0.0F;
      } else {
         float yaw2 = flag
            ? val.deltaYaw
            : val.deltaPitch;
         float yaw3 = 0.3F
            + MathHelper.clamp(
               val.rotationSpeed
                  * (
                     flag
                        ? 3.4F
                        : 2.8F
                  ),
               0.0F,
               flag
                  ? 0.2F
                  : 0.16F
            );
         float yaw4 = MathHelper.clamp(
            yaw
               / (
                  flag
                     ? 105.0F
                     : 82.0F
               ),
            0.09F,
            flag
               ? 0.52F
               : 0.46F
         );
         float yaw5 = Math.max(
            yaw2
               * Math.max(
                  yaw3,
                  yaw4
               ),
            flag
               ? 0.03F
               : 0.024F
         );
         if (val.instantSnap
            )
          {
            yaw5 = Math.max(
               yaw5,
               yaw
                  * (
                     flag
                        ? 0.085F
                        : 0.065F
                  )
            );
         }

         if (val.attacking
            || val.combatFrame
            )
          {
            yaw5 *= flag
               ? 1.02F
               : 1.015F;
         }

         yaw5 *= flag
            ? 0.5F
            : 0.46F;
         float yaw6 = flag
            ? 6.0F
            : 4.0F;
         if (yaw
            < yaw6
            )
          {
            float yaw7 = 1.0F
               + (
                     yaw6
                        - yaw
                  )
                  / yaw6
                  * 0.18F;
            yaw5 *= yaw7;
         }

         float yaw8 = flag
            ? Math.max(
               0.48F,
               yaw
                  * (
                     val.instantSnap
                        ? 0.11F
                        : 0.065F
                  )
            )
            : Math.max(
               0.34F,
               yaw
                  * (
                     val.instantSnap
                        ? 0.09F
                        : 0.058F
                  )
            );
         yaw5 = Math.min(
            yaw5,
            yaw8
         );
         yaw5 = Math.min(
            yaw5,
            yaw
         );
         return Math.signum(
               f
            )
            * yaw5;
      }
   }

   private float buildJitter(
      DeltayawHolder val,
      float f,
      boolean flag,
      float f2
   ) {
      float yaw = Math.abs(
         f
      );
      if (yaw
         > (
            flag
               ? 6.5F
               : 4.8F
         )) {
         return 0.0F;
      } else {
         float yaw2 = flag
            ? val.jitterYawSpeed
            : val.jitterPitchSpeed;
         float yaw3 = f2
            * MathHelper.clamp(
               val.jitterScore
                  * 0.01F,
               0.0F,
               flag
                  ? 0.15F
                  : 0.11F
            );
         yaw3 += f2
            * MathHelper.clamp(
               yaw2
                  * (
                     flag
                        ? 1.3F
                        : 1.0F
                  ),
               0.0F,
               flag
                  ? 0.1F
                  : 0.07F
            );
         if (val.isJittering
            )
          {
            yaw3 *= 1.05F;
         }

         if (yaw3
            <= 0.0F) {
            return 0.0F;
         } else {
            float yaw4 = ThreadLocalRandom.current()
                  .nextBoolean()
               ? 1.0F
               : -1.0F;
            float yaw5 = yaw3
               * ThreadLocalRandom.current().nextFloat(0.3F, 0.95F)
               * yaw4;
            if (Math.abs(
                     yaw5
                  )
                  > yaw
               && Math.signum(
                     yaw5
                  )
                  == Math.signum(
                     f
                  )) {
               yaw5 = f;
            }

            return yaw5;
         }
      }
   }

   private void syncRotationState(
      LivingEntity entity,
      float f,
      float f2
   ) {
      if (!this.hasRotationState
         || this.trackedRotationTarget
            != entity
         )
       {
         this.trackedRotationTarget = entity;
         this.smoothYaw = f;
         this.smoothPitch = f2;
         this.smoothYawStep = 0.0F;
         this.smoothPitchStep = 0.0F;
         this.yawSmoothFactor = 1.0F;
         this.pitchSmoothFactor = 1.0F;
         this.smoothProfileTicks = 0;
         this.hasRotationState = true;
      }
   }

   private float smoothAxisStep(
      float f,
      float f2,
      float f3,
      boolean flag,
      boolean flag2
   ) {
      float yaw = Math.abs(
         f3
      );
      if (yaw
         <= 1.0E-4F) {
         return 0.0F;
      } else {
         float yaw2 = flag
            ? (
               flag2
                  ? 0.092F
                  : 0.06F
            )
            : (
               flag2
                  ? 0.082F
                  : 0.055F
            );
         float yaw3 = yaw2
            * (
               flag
                  ? this.yawSmoothFactor
                  : this.pitchSmoothFactor
            );
         float yaw4 = f
            + (
                  f2
                     - f
               )
               * MathHelper.clamp(
                  yaw3,
                  0.025F,
                  0.16F
               );
         float yaw5 = flag
            ? 0.13F
            : 0.1F;
         float yaw6 = flag
            ? (
               flag2
                  ? 0.056F
                  : 0.036F
            )
            : (
               flag2
                  ? 0.046F
                  : 0.032F
            );
         float yaw7 = flag
            ? this.yawSmoothFactor
            : this.pitchSmoothFactor;
         float yaw8 = yaw5
            + yaw
               * yaw6
               * MathHelper.clamp(
                  yaw7,
                  0.88F,
                  1.18F
               );
         float yaw9 = flag
            ? 5.5F
            : 3.8F;
         if (yaw
            < yaw9
            )
          {
            yaw8 *= 1.12F;
         }

         yaw4 = MathHelper.clamp(
            yaw4,
            -yaw8,
            yaw8
         );
         if (Math.abs(
                  f3
               )
               < Math.abs(
                  yaw4
               )
            && Math.signum(
                  f3
               )
               == Math.signum(
                  yaw4
               )) {
            yaw4 = f3;
         }

         return yaw4;
      }
   }

   private float quantizeDelta(
      float f,
      float f2,
      float f3,
      boolean flag
   ) {
      float progress = f;
      if (Math.abs(
               f2
            )
            < Math.abs(
               f
            )
         && Math.signum(
               f2
            )
            == Math.signum(
               f
            )) {
         progress = f2;
      }

      float progress2 = (float)Math.round(
            progress
               / f3
         )
         * f3;
      if (progress2
            == 0.0F
         && Math.abs(
               progress
            )
            >= f3
               * 0.2F) {
         progress2 = Math.signum(
               progress
            )
            * f3;
      }

      if (Math.abs(
               f2
            )
            < Math.abs(
               progress2
            )
         && Math.signum(
               f2
            )
            == Math.signum(
               progress2
            )) {
         progress2 = f2;
      }

      if (!flag
         )
       {
         progress2 = MathHelper.clamp(
            progress2,
            -89.0F,
            89.0F
         );
      }

      return progress2;
   }

   private Vec3d selectAimPoint(
      LivingEntity entity,
      boolean flag
   ) {
      if (this.trackedTarget
            == entity
         && this.currentAimPoint != null
         && this.targetAimPoint != null) {
         if (this.aimPointTicks++ >= this.aimPointRefreshTicks) {
            this.targetAimPoint = this.createAimPoint(
               entity,
               flag
            );
            this.aimPointTicks = 0;
            this.aimPointRefreshTicks = this.randomRefreshTicks(
               flag
            );
         }

         float x2 = flag
            ? 0.06F
            : 0.04F;
         this.currentAimPoint = new Vec3d(
            MathHelper.lerp(
               (double)x2,
               this.currentAimPoint.x,
               this.targetAimPoint.x
            ),
            MathHelper.lerp(
               (double)x2,
               this.currentAimPoint.y,
               this.targetAimPoint.y
            ),
            MathHelper.lerp(
               (double)x2,
               this.currentAimPoint.z,
               this.targetAimPoint.z
            )
         );
         return this.currentAimPoint;
      } else {
         this.trackedTarget = entity;
         this.targetAimPoint = this.createAimPoint(
            entity,
            flag
         );
         this.currentAimPoint = this.targetAimPoint;
         this.aimPointTicks = 0;
         this.aimPointRefreshTicks = this.randomRefreshTicks(
            flag
         );
         return this.currentAimPoint;
      }
   }

   private int randomRefreshTicks(
      boolean flag
   ) {
      return ThreadLocalRandom.current()
         .nextInt(
            flag
               ? 7
               : 10,
            flag
               ? 13
               : 18
         );
   }

   private Vec3d createAimPoint(
      LivingEntity entity,
      boolean flag
   ) {
      Box box = this.getPredictedBox(
         entity
      );
      ThreadLocalRandom val = ThreadLocalRandom.current();
      double x = MathHelper.lerp(
         val.nextDouble(
            0.45, 0.55
         ),
         box.minX,
         box.maxX
      );
      double x2 = MathHelper.lerp(
         val.nextDouble(
            flag
               ? 0.53
               : 0.49,
            flag
               ? 0.7
               : 0.76
         ),
         box.minY,
         box.maxY
      );
      double x3 = MathHelper.lerp(
         val.nextDouble(
            0.45, 0.55
         ),
         box.minZ,
         box.maxZ
      );
      return new Vec3d(
         x,
         x2,
         x3
      );
   }

   private float getFloat(
      JsonObject val,
      String str
   ) {
      return val.has(
            str
         )
         ? val.get(
               str
            )
            .getAsFloat()
         : 0.0F;
   }

   private boolean getBoolean(
      JsonObject val,
      String str
   ) {
      return val.has(
            str
         )
         && val.get(
               str
            )
            .getAsBoolean();
   }
}
