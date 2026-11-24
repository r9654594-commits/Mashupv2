package dev.leg1tka.modules.combat;


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

import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventBinding;
import dev.leg1tka.api.events.implement.EventMoveInput;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.Comparator;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class TargetPearl extends Module {
   private static final double MAX_TRACK_DISTANCE = 256.0;
   private static final double MIN_LANDING_DISTANCE = 11.0;
   private static final long LOCAL_THROW_COOLDOWN_MS = 2500L;
   private static final float DIRECT_MIN_PITCH = -25.0F;
   private static final float DIRECT_MAX_PITCH = 35.0F;
   private static final float PITCH_STEP = 0.25F;
   public static final TargetPearl INSTANCE = new TargetPearl();
   private final ModeSetting mode;
   private final KeybindSetting bind;
   private final BooleanSetting bypassgrim;
   private final BooleanSetting onlyTarget;
   private final BooleanSetting ignoreFriends;
   private final FinishedHelper timer;
   private EnderPearlEntity targetPearl;
   private int lastHandledPearlId;
   private long nextThrowAt;
   private boolean isThrowing;
   private Vec2f serverRotation;

   public TargetPearl() {
      super("TargetPearl", Category.COMBAT);
      String var1 = "TargetPearl";
      String var3 = "Тип";
      String var8 = "Автоматический";
      this.mode = new ModeSetting(var3, var8, new String[]{"По бинду", "Автоматический"});
      this.bind = new KeybindSetting("Бинд", -1)
         .visible(
            () -> this.mode.is("По бинду")
         );
      this.bypassgrim = new BooleanSetting("Обходить Grim", true);
      this.onlyTarget = new BooleanSetting("Только за противником", false);
      this.ignoreFriends = new BooleanSetting("Игнорировать друзей", true);
      this.timer = new FinishedHelper();
      this.lastHandledPearlId = -1;
      this.addSettings(new Setting[]{
         this.mode,
         this.bind,
         this.bypassgrim,
         this.onlyTarget,
         this.ignoreFriends
      });
   }

   @EventLink
   public void onBinding(
      EventBinding val
   ) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         ModeSetting var10000 = this.mode;
         if (var10000.is("По бинду")
            && val.getKey()
               == this.bind.getKey()) {
            if (this.canThrowNow()) {
               this.aimAndThrowPearl();
            }
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val9
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.lastHandledPearlId != -1
            && (
               !(
                     mc.world.getEntityById(this.lastHandledPearlId) instanceof EnderPearlEntity val
                  )
                  || !val.isAlive()
            )) {
            this.lastHandledPearlId = -1;
         }

         ModeSetting var10000 = this.mode;
         if (var10000.is("Автоматический") && this.canThrowNow()) {
            this.aimAndThrowPearl();
         }
      } else {
         this.resetThrowState();
      }
   }

   @EventLink
   public void onMoveInput(
      EventMoveInput val
   ) {
      if (this.isEnable() && this.isThrowing && this.serverRotation != null) {
         float f = val.getForward();
         float f2 = val.getStrafe();
         if (f
               != 0.0F
            || f2
               != 0.0F) {
            double d = MathHelper.wrapDegrees(
               Math.toDegrees(
                  direction(
                     this.serverRotation.x,
                     f,
                     f2
                  )
               )
            );
            float f3 = 0.0F;
            float f4 = 0.0F;
            float x2 = Float.MAX_VALUE;

            for (float f5 = -1.0F;
               f5
                  <= 1.0F;
               f5++
            ) {
               for (float f6 = -1.0F;
                  f6
                     <= 1.0F;
                  f6++
               ) {
                  if (f5
                        != 0.0F
                     || f6
                        != 0.0F) {
                     double d2 = MathHelper.wrapDegrees(
                        Math.toDegrees(
                           direction(
                              this.serverRotation.x,
                              f5,
                              f6
                           )
                        )
                     );
                     float f7 = Math.abs(
                        MathHelper.wrapDegrees(
                           (float)(
                              d
                                 - d2
                           )
                        )
                     );
                     if (f7
                        < x2
                        )
                      {
                        x2 = f7;
                        f3 = f5;
                        f4 = f6;
                     }
                  }
               }
            }

            val.setForward(
               f3
            );
            val.setStrafe(
               f4
            );
         }
      }
   }

   @Override
   public void onDisable() {
      this.resetThrowState();
      this.lastHandledPearlId = -1;
      this.nextThrowAt = 0L;
      this.timer.reset();
      super.onDisable();
   }

   private boolean canThrowNow() {
      return System.currentTimeMillis() < this.nextThrowAt
         ? false
         : !mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(Items.ENDER_PEARL)) && this.timer.finished(1000L);
   }

   private void aimAndThrowPearl() {
      Vec3d pos = this.getTargetPearlLandingPosition();
      if (pos
         == null) {
         this.resetThrowState();
      } else {
         float[] val = this.calculateYawPitch(
            pos
         );
         if (val
               != null
            && !Float.isNaN(
               val[0]
            )
            && !Float.isNaN(
               val[1]
            )) {
            Vec3d vec = this.checkTrajectory(
               val[0],
               val[1]
            );
            double d = Math.max(
               3.0,
               mc.player
                     .getPos()
                     .distanceTo(
                        pos
                     )
                  * 0.12
            );
            if (vec
                  == null
               || pos.distanceTo(
                     vec
                  )
                  > d
               )
             {
               this.resetThrowState();
            } else if (!this.hasPearl()) {
               this.resetThrowState();
            } else {
               float y2 = mc.player
                  .getYaw();
               float y3 = mc.player
                  .getPitch();
               this.isThrowing = true;
               this.serverRotation = new Vec2f(
                  val[0],
                  val[1]
               );

               try {
                  mc.player
                     .setYaw(
                        val[0]
                     );
                  mc.player
                     .setPitch(
                        val[1]
                     );
                  mc.player
                     .networkHandler
                     .sendPacket(
                        new LookAndOnGround(
                           val[0],
                           val[1],
                           mc.player.isOnGround(),
                           mc.player.horizontalCollision
                        )
                     );
                  SearchitemHelper.swapAndUseHvH(Items.ENDER_PEARL);
                  this.timer.reset();
                  this.nextThrowAt = System.currentTimeMillis() + 2500L;
                  if (this.targetPearl != null) {
                     this.lastHandledPearlId = this.targetPearl.getId();
                  }
               } finally {
                  mc.player
                     .setYaw(
                        y2
                     );
                  mc.player
                     .setPitch(
                        y3
                     );
                  this.resetThrowState();
               }
            }
         } else {
            this.resetThrowState();
         }
      }
   }

   private Vec3d getTargetPearlLandingPosition() {
      this.targetPearl = this.getTargetPearl();
      if (this.targetPearl != null && this.targetPearl.isAlive()) {
         Vec3d vec = this.predictPearlLanding(
            this.targetPearl
         );
         return vec
                  != null
               && this.isWithinRange(
                  vec
               )
            ? vec
            : null;
      } else {
         return null;
      }
   }

   private EnderPearlEntity getTargetPearl() {
      Box box = mc.player
         .getBoundingBox()
         .expand(256.0);
      LivingEntity entity2 = ModuleManager.INSTANCE
            != null
         ? ModuleManager.aura.getTarget()
         : null;
      return mc.world
         .getOtherEntities(
            mc.player,
            box,
            val -> {
               if (val instanceof EnderPearlEntity val2
                  && val2.isAlive()
                  && val2.getOwner()
                     != mc.player
                  && val2.getId()
                     != this.lastHandledPearlId
                  && !this.isIgnoredFriend(
                     val2.getOwner()
                  )
                  && (
                     !this.onlyTarget.isState()
                        || entity2
                              != null
                           && val2.getOwner()
                              == entity2
                  )) {
                  return true;
               }

               return false;
            }
         )
         .stream()
         .map(
            val3 -> (EnderPearlEntity)val3
         )
         .filter(
            val4 -> this.getHorizontalDistanceTo(
                     val4
                  )
                  <= 256.0
         )
         .min(Comparator.comparingDouble(this::getHorizontalDistanceTo))
         .orElse(null);
   }

   private boolean isIgnoredFriend(
      Entity val
   ) {
      return this.ignoreFriends.isState()
            && val instanceof PlayerEntity val2
         ? AspectDLC.INSTANCE != null
            && AspectDLC.INSTANCE.friendStorage != null
            && AspectDLC.INSTANCE
               .friendStorage
               .isFriend(
                  val2.getName()
                     .getString()
               )
         : false;
   }

   private double getHorizontalDistanceTo(
      EnderPearlEntity val
   ) {
      Vec3d vec = mc.player
         .getPos();
      Vec3d pos = val.getPos();
      double d = pos.x
         - vec.x;
      double d2 = pos.z
         - vec.z;
      return Math.sqrt(
         d
               * d
            + d2
               * d2
      );
   }

   private Vec3d predictPearlLanding(
      EnderPearlEntity val
   ) {
      Vec3d pos = val.getPos();
      Vec3d velocity = val.getVelocity();
      Vec3d vec = pos;

      for (int i = 0;
         i
            < 200;
         i++
      ) {
         vec = pos;
         pos = pos.add(
            velocity
         );
         if (this.hitsBlock(
               vec,
               pos
            )
            || pos.y
               <= (double)mc.world.getBottomY()) {
            return new Vec3d(
               (double)MathHelper.floor(
                     vec.x
                  )
                  + 0.5,
               (double)MathHelper.floor(
                  vec.y
               ),
               (double)MathHelper.floor(
                     vec.z
                  )
                  + 0.5
            );
         }

         velocity = this.updatePearlMotion(
            velocity,
            pos
         );
      }

      return new Vec3d(
         (double)MathHelper.floor(
               vec.x
            )
            + 0.5,
         (double)MathHelper.floor(
            vec.y
         ),
         (double)MathHelper.floor(
               vec.z
            )
            + 0.5
      );
   }

   private Vec3d updatePearlMotion(
      Vec3d val,
      Vec3d val2
   ) {
      BlockPos pos = BlockPos.ofFloored(
         val2
      );
      return mc.world
            .getBlockState(
               pos
            )
            .isOf(Blocks.WATER)
         ? val.multiply(
               0.8
            )
            .add(0.0, -0.03, 0.0)
         : val.multiply(
               0.99
            )
            .add(0.0, -0.03, 0.0);
   }

   private boolean isWithinRange(
      Vec3d val
   ) {
      double d = mc.player
         .getPos()
         .distanceTo(
            val
         );
      return d
            >= 11.0
         && d
            <= 256.0;
   }

   private float[] calculateYawPitch(
      Vec3d val
   ) {
      Vec3d vec = mc.player
         .getPos();
      double d = val.x
         - vec.x;
      double d2 = val.y
         - mc.player.getEyeY();
      double d3 = val.z
         - vec.z;
      float f = (float)Math.toDegrees(
            Math.atan2(
               d3,
               d
            )
         )
         - 90.0F;
      double d4 = Math.sqrt(
         d
               * d
            + d3
               * d3
      );
      double d5 = Math.max(
         1.5,
         mc.player
               .getPos()
               .distanceTo(
                  val
               )
            * 0.08
      );
      PitchHolder val2 = this.findBestCandidate(
         val,
         f,
         -25.0F,
         35.0F,
         d5,
         true
      );
      if (val2
         != null) {
         float[] var19 = new float[2];
         var19[0] = f;
         var19[1] = MathHelper.clamp(
            val2.pitch,
            -90.0F,
            90.0F
         );
         return var19;
      } else {
         PitchHolder val3 = this.findBestCandidate(
            val,
            f,
            -85.0F,
            85.0F,
            d5,
            false
         );
         if (val3
            == null) {
            double d6 = -Math.toDegrees(
                  Math.atan2(
                     d2,
                     d4
                  )
               )
               + 5.0;
            float[] var18 = new float[2];
            var18[0] = f;
            var18[1] = MathHelper.clamp(
               (float)d6,
               -90.0F,
               90.0F
            );
            return var18;
         } else {
            float[] var10000 = new float[2];
            var10000[0] = f;
            var10000[1] = MathHelper.clamp(
               val3.pitch,
               -90.0F,
               90.0F
            );
            return var10000;
         }
      }
   }

   private PitchHolder findBestCandidate(
      Vec3d val,
      float val2,
      float val3,
      float val4,
      double val5,
      boolean val6
   ) {
      Vec3d vec = mc.player
         .getPos();
      double d = 1.5;
      PitchHolder val7 = null;

      for (float f = val3;
         f
            <= val4;
         f += 0.25F
      ) {
         float f2 = (float)Math.toRadians(
            (double)f
         );
         double d2 = (double)(
               -MathHelper.sin(
                     (float)Math.toRadians(
                        (double)val2
                     )
                  )
                  * MathHelper.cos(
                     f2
                  )
            )
            * d;
         double d3 = (double)(
               -MathHelper.sin(
                  f2
               )
            )
            * d;
         double d4 = (double)(
               MathHelper.cos(
                     (float)Math.toRadians(
                        (double)val2
                     )
                  )
                  * MathHelper.cos(
                     f2
                  )
            )
            * d;
         Vec3d vec2 = new Vec3d(
            vec.x,
            mc.player.getEyeY(),
            vec.z
         );
         Vec3d vec3 = new Vec3d(
            d2,
            d3,
            d4
         );
         int n = 0;

         for (int i = 0;
            i
               < 200;
            i++
         ) {
            Vec3d vec4 = vec2;
            vec2 = vec2.add(
               vec3
            );
            vec3 = this.updatePearlMotion(
               vec3,
               vec2
            );
            n++;
            if (this.hitsEntity(
               vec4,
               vec2
            )) {
               break;
            }

            if (this.hitsBlock(
                  vec4,
                  vec2
               )
               || !(
                  vec2.y
                     > (double)mc.world.getBottomY()
               )) {
               double dist = vec2.distanceTo(
                  val
               );
               PitchHolder val8 = new PitchHolder(
                  f,
                  dist,
                  n,
                  vec2
               );
               if (this.isBetterCandidate(
                  val8,
                  val7,
                  val5,
                  val6
               )) {
                  val7 = val8;
               }
               break;
            }
         }
      }

      return val7
               != null
            && !(
               val7.distanceToTarget
                  > val5
            )
         ? val7
         : null;
   }

   private boolean isBetterCandidate(
      PitchHolder val,
      PitchHolder val2,
      double val3,
      boolean val4
   ) {
      if (val2
         == null) {
         return true;
      } else {
         boolean flag = val.distanceToTarget
            <= val3;
         boolean flag2 = val2.distanceToTarget
            <= val3;
         if (flag
            != flag2
            )
          {
            return flag;
         } else {
            if (val4
               && flag
               && flag2
               )
             {
               float f = Math.abs(
                  val.pitch
               );
               float f2 = Math.abs(
                  val2.pitch
               );
               if (Math.abs(
                     f
                        - f2
                  )
                  > 0.01F) {
                  return f
                     < f2;
               }

               if (val.ticks
                  != val2.ticks
                  )
                {
                  return val.ticks
                     < val2.ticks;
               }
            }

            if (Math.abs(
                  val.distanceToTarget
                     - val2.distanceToTarget
               )
               > 0.01) {
               return val.distanceToTarget
                  < val2.distanceToTarget;
            } else if (val.ticks
               != val2.ticks
               )
             {
               return val.ticks
                  < val2.ticks;
            } else {
               return !val4
                  ? Math.abs(
                        val.pitch
                     )
                     < Math.abs(
                        val2.pitch
                     )
                  : false;
            }
         }
      }
   }

   private Vec3d checkTrajectory(
      float val,
      float val2
   ) {
      float f = (float)Math.toRadians(
         (double)val
      );
      float f2 = (float)Math.toRadians(
         (double)val2
      );
      double d = 1.5;
      double d2 = mc.player
            .getX()
         - (double)(
            MathHelper.cos(
                  f
               )
               * 0.16F
         );
      double d3 = mc.player
            .getY()
         + (double)mc.player.getEyeHeight(mc.player.getPose())
         - 0.1;
      double d4 = mc.player
            .getZ()
         - (double)(
            MathHelper.sin(
                  f
               )
               * 0.16F
         );
      double d5 = (double)(
            -MathHelper.sin(
                  f
               )
               * MathHelper.cos(
                  f2
               )
         )
         * d;
      double d6 = (double)(
            -MathHelper.sin(
               f2
            )
         )
         * d;
      double d7 = (double)(
            MathHelper.cos(
                  f
               )
               * MathHelper.cos(
                  f2
               )
         )
         * d;
      Vec3d vec = new Vec3d(
         d2,
         d3,
         d4
      );
      Vec3d vec2 = new Vec3d(
         d5,
         d6,
         d7
      );

      for (int i = 0;
         i
            <= 200;
         i++
      ) {
         Vec3d vec3 = vec;
         vec = vec.add(
            vec2
         );
         vec2 = this.updatePearlMotion(
            vec2,
            vec
         );
         if (this.hitsEntity(
            vec3,
            vec
         )) {
            return null;
         }

         if (this.hitsBlock(
               vec3,
               vec
            )
            || vec.y
               <= (double)mc.world.getBottomY()) {
            return new Vec3d(
               (double)MathHelper.floor(
                     vec.x
                  )
                  + 0.5,
               (double)MathHelper.floor(
                  vec.y
               ),
               (double)MathHelper.floor(
                     vec.z
                  )
                  + 0.5
            );
         }
      }

      return null;
   }

   private boolean hitsBlock(
      Vec3d val,
      Vec3d val2
   ) {
      return mc.world
            .raycast(
               new RaycastContext(
                  val,
                  val2,
                  ShapeType.COLLIDER,
                  FluidHandling.NONE,
                  mc.player
               )
            )
            .getType()
         == Type.BLOCK;
   }

   private boolean hitsEntity(
      Vec3d val,
      Vec3d val2
   ) {
      Box box = new Box(
            val,
            val2
         )
         .expand(0.3);

      for (Entity entity2 : mc.world
         .getOtherEntities(
            mc.player,
            box,
            val3 -> {
               if (!val3.isAlive()
                  || val3.isSpectator()
                  || val3.noClip
                  )
                {
                  return false;
               } else {
                  return val3
                        == this.targetPearl
                     ? false
                     : !(
                        val3 instanceof EnderPearlEntity
                     );
               }
            }
         )) {
         if (entity2.getBoundingBox()
            .expand(0.25)
            .raycast(
               val,
               val2
            )
            .isPresent()) {
            return true;
         }
      }

      return false;
   }

   private boolean hasPearl() {
      return mc.player.getMainHandStack().isOf(Items.ENDER_PEARL)
         || mc.player.getOffHandStack().isOf(Items.ENDER_PEARL)
         || SearchitemHelper.find(Items.ENDER_PEARL, 0, 8)
            != -1
         || SearchitemHelper.find(
               Items.ENDER_PEARL,
               9,
               45
            )
            != -1;
   }

   private void resetThrowState() {
      this.isThrowing = false;
      this.targetPearl = null;
      this.serverRotation = null;
   }

   private static double direction(
      float val,
      float val2,
      float val3
   ) {
      if (val2
         < 0.0F) {
         val += 180.0F;
      }

      float f = 1.0F;
      if (val2
         < 0.0F) {
         f = -0.5F;
      } else if (val2
         > 0.0F) {
         f = 0.5F;
      }

      if (val3
         > 0.0F) {
         val -= 90.0F
            * f;
      }

      if (val3
         < 0.0F) {
         val += 90.0F
            * f;
      }

      return Math.toRadians(
         (double)val
      );
   }
}
