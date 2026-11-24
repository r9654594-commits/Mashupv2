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


import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;

public class SimulatelocalplayerHelper implements IMinecraft {
   public final PlayerEntity player;
   public final UpdateHelper_3 input;
   public Vec3d pos;
   public Vec3d velocity;
   public Box boundingBox;
   public float yaw;
   public float pitch;
   public boolean sprinting;
   public float fallDistance;
   public int jumpingCooldown;
   public boolean isJumping;
   public boolean isFallFlying;
   public boolean onGround;
   public boolean horizontalCollision;
   public boolean verticalCollision;
   public boolean touchingWater;
   public boolean isSwimming;
   public boolean submergedInWater;
   private final Object2DoubleMap<TagKey<Fluid>> fluidHeight;
   private final HashSet<TagKey<Fluid>> submergedFluidTag;
   private int simulatedTicks = 0;
   private boolean clipLedged = false;
   private static final double STEP_HEIGHT = 0.5;

   public SimulatelocalplayerHelper(
      PlayerEntity player2,
      UpdateHelper_3 val,
      Vec3d vec,
      Vec3d vec2,
      Box box,
      float f,
      float f2,
      boolean flag,
      float f3,
      int n,
      boolean flag2,
      boolean flag3,
      boolean flag4,
      boolean flag5,
      boolean flag6,
      boolean flag7,
      boolean flag8,
      boolean flag9,
      Object2DoubleMap<TagKey<Fluid>> map,
      HashSet<TagKey<Fluid>> set
   ) {
      this.player = player2;
      this.input = val;
      this.pos = vec;
      this.velocity = vec2;
      this.boundingBox = box;
      this.yaw = f;
      this.pitch = f2;
      this.sprinting = flag;
      this.fallDistance = f3;
      this.jumpingCooldown = n;
      this.isJumping = flag2;
      this.isFallFlying = flag3;
      this.onGround = flag4;
      this.horizontalCollision = flag5;
      this.verticalCollision = flag6;
      this.touchingWater = flag7;
      this.isSwimming = flag8;
      this.submergedInWater = flag9;
      this.fluidHeight = map;
      this.submergedFluidTag = set;
   }

   public static SimulatelocalplayerHelper simulateLocalPlayer(
      int n
   ) {
      SimulatelocalplayerHelper val = fromClientPlayer(
         UpdateHelper_3.fromClientPlayer(mc.player.input.playerInput)
      );

      for (int i = 0;
         i
            < n;
         i++
      ) {
         val.tick();
      }

      return val;
   }

   public static SimulatelocalplayerHelper simulateOtherPlayer(
      PlayerEntity player,
      int n
   ) {
      SimulatelocalplayerHelper val = fromOtherPlayer(
         player,
         UpdateHelper_3.guessInput(
            player
         )
      );

      for (int i = 0;
         i
            < n;
         i++
      ) {
         val.tick();
      }

      return val;
   }

   public static SimulatelocalplayerHelper fromClientPlayer(
      UpdateHelper_3 val
   ) {
      ClientPlayerEntity player2 = mc.player;
      return new SimulatelocalplayerHelper(
         player2,
         val,
         player2.getPos(),
         player2.getVelocity(),
         player2.getBoundingBox(),
         player2.getYaw(),
         player2.getPitch(),
         player2.isSprinting(),
         player2.fallDistance,
         player2.jumpingCooldown,
         player2.jumping,
         player2.isGliding(),
         player2.isOnGround(),
         player2.horizontalCollision,
         player2.verticalCollision,
         player2.isTouchingWater(),
         player2.isSwimming(),
         player2.isSubmergedInWater(),
         new Object2DoubleArrayMap(
            player2.fluidHeight
         ),
         new HashSet<>(
            player2.submergedFluidTag
         )
      );
   }

   public static SimulatelocalplayerHelper fromOtherPlayer(
      PlayerEntity player,
      UpdateHelper_3 val
   ) {
      return new SimulatelocalplayerHelper(
         player,
         val,
         player.getPos(),
         player.getPos()
            .subtract(
               new Vec3d(
                  player.prevX,
                  player.prevY,
                  player.prevZ
               )
            ),
         player.getBoundingBox(),
         player.getYaw(),
         player.getPitch(),
         player.isSprinting(),
         player.fallDistance,
         player.jumpingCooldown,
         player.jumping,
         player.isGliding(),
         player.isOnGround(),
         player.horizontalCollision,
         player.verticalCollision,
         player.isTouchingWater(),
         player.isSwimming(),
         player.isSubmergedInWater(),
         new Object2DoubleArrayMap(
            player.fluidHeight
         ),
         new HashSet<>(
            player.submergedFluidTag
         )
      );
   }

   public Vec3d pos() {
      return this.player.getPos();
   }

   public void tick() {
      this.simulatedTicks++;
      this.clipLedged = false;
      if (!(this.pos.y <= -70.0)) {
         this.input.update();
         this.checkWaterState();
         this.updateSubmergedInWaterState();
         this.updateSwimming();
         if (this.jumpingCooldown > 0) {
            this.jumpingCooldown--;
         }

         this.isJumping = this.input.playerInput.jump();
         double x2 = this.velocity
            .x;
         double x3 = this.velocity
            .y;
         double x4 = this.velocity
            .z;
         if (Math.abs(this.velocity.x) < 0.003) {
            x2 = 0.0;
         }

         if (Math.abs(this.velocity.y) < 0.003) {
            x3 = 0.0;
         }

         if (Math.abs(this.velocity.z) < 0.003) {
            x4 = 0.0;
         }

         if (this.onGround) {
            this.isFallFlying = false;
         }

         this.velocity = new Vec3d(
            x2,
            x3,
            x4
         );
         if (this.isJumping) {
            double x5 = this.isInLava()
               ? this.getFluidHeight(FluidTags.LAVA)
               : this.getFluidHeight(FluidTags.WATER);
            boolean flag = this.isTouchingWater()
               && x5
                  > 0.0;
            double x6 = this.getSwimHeight();
            if (!flag
               || this.onGround
                  && !(
                     x5
                        > x6
                  )) {
               if (!this.isInLava()
                  || this.onGround
                     && !(
                        x5
                           > x6
                     )) {
                  if ((
                        this.onGround
                           || flag
                              && x5
                                 <= x6
                     )
                     && this.jumpingCooldown == 0) {
                     this.jump();
                     this.jumpingCooldown = 10;
                  }
               } else {
                  this.swimUpward(FluidTags.LAVA);
               }
            } else {
               this.swimUpward(FluidTags.WATER);
            }
         }

         float height = this.input
               .movementSideways
            * 0.98F;
         float height2 = this.input
               .movementForward
            * 0.98F;
         float height3 = 0.0F;
         if (this.hasStatusEffect(StatusEffects.SLOW_FALLING) || this.hasStatusEffect(StatusEffects.LEVITATION)) {
            this.onLanding();
         }

         this.travel(
            new Vec3d(
               (double)height,
               (double)height3,
               (double)height2
            )
         );
      }
   }

   private void travel(
      Vec3d vec
   ) {
      if (this.isSwimming && !this.player.hasVehicle()) {
         double dist = this.getRotationVector()
            .y;
         double dist2 = dist
               < -0.2
            ? 0.085
            : 0.06;
         BlockPos pos2 = new BlockPos(
            MathHelper.floor(this.pos.x),
            MathHelper.floor(this.pos.y + 1.0 - 0.1),
            MathHelper.floor(this.pos.z)
         );
         if (dist
               <= 0.0
            || this.input.playerInput.jump()
            || !this.player
               .getWorld()
               .getBlockState(
                  pos2
               )
               .getFluidState()
               .isEmpty()) {
            this.velocity = this.velocity
               .add(
                  0.0,
                  (
                        dist
                           - this.velocity.y
                     )
                     * dist2,
                  0.0
               );
         }
      }

      double dist3 = this.velocity
         .y;
      double dist4 = 0.08;
      boolean flag = this.velocity
            .y
         <= 0.0;
      if (this.velocity.y <= 0.0 && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
         dist4 = 0.01;
         this.onLanding();
      }

      if (this.isTouchingWater() && this.player.shouldSwimInFluids()) {
         double dist5 = this.pos
            .y;
         float yaw2 = this.isSprinting()
            ? 0.9F
            : 0.8F;
         float yaw3 = 0.02F;
         float yaw4 = (float)this.getAttributeValue(
            EntityAttributes.WATER_MOVEMENT_EFFICIENCY
         );
         if (!this.onGround) {
            yaw4 *= 0.5F;
         }

         if (yaw4
            > 0.0F) {
            yaw2 += (
                  0.54600006F
                     - yaw2
               )
               * yaw4
               / 3.0F;
            yaw3 += (
                  this.getMovementSpeed()
                     - yaw3
               )
               * yaw4
               / 3.0F;
         }

         if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
            yaw2 = 0.96F;
         }

         this.updateVelocity(
            yaw3,
            vec
         );
         this.move(this.velocity);
         Vec3d vec2 = this.velocity;
         if (this.horizontalCollision && this.isClimbing()) {
            vec2 = new Vec3d(
               vec2.x,
               0.2,
               vec2.z
            );
         }

         this.velocity = vec2.multiply(
            (double)yaw2,
            0.8,
            (double)yaw2
         );
         Vec3d vec3 = this.player
            .applyFluidMovingSpeed(
               dist4,
               flag,
               this.velocity
            );
         this.velocity = vec3;
         if (this.horizontalCollision
            && this.doesNotCollide(
               vec3.x,
               vec3.y
                  + 0.6
                  - this.pos.y
                  + dist5,
               vec3.z
            )) {
            this.velocity = new Vec3d(
               vec3.x,
               0.3,
               vec3.z
            );
         }
      } else if (this.isInLava() && this.player.shouldSwimInFluids()) {
         double dist6 = this.pos
            .y;
         this.updateVelocity(
            0.02F,
            vec
         );
         this.move(this.velocity);
         if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
            this.velocity = this.velocity.multiply(0.5, 0.8, 0.5);
            this.velocity = this.player
               .applyFluidMovingSpeed(
                  dist4,
                  flag,
                  this.velocity
               );
         } else {
            this.velocity = this.velocity.multiply(0.5);
         }

         if (!this.player.hasNoGravity()) {
            this.velocity = this.velocity
               .add(
                  0.0,
                  -dist4
                     / 4.0,
                  0.0
               );
         }

         if (this.horizontalCollision
            && this.doesNotCollide(
               this.velocity.x,
               this.velocity.y
                  + 0.6
                  - this.pos.y
                  + dist6,
               this.velocity.z
            )) {
            this.velocity = new Vec3d(this.velocity.x, 0.3, this.velocity.z);
         }
      } else if (this.isFallFlying) {
         Vec3d vec4 = this.velocity;
         if (vec4.y
            > -0.5) {
            this.fallDistance = 1.0F;
         }

         Vec3d vec5 = this.getRotationVector(
            this.pitch + (this.pitch - this.player.prevPitch), this.yaw + (this.yaw - this.player.prevYaw)
         );
         float yaw5 = this.pitch
            * (float) (Math.PI / 180.0);
         double dist7 = Math.sqrt(
            vec5.x
                  * vec5.x
               + vec5.z
                  * vec5.z
         );
         double dist8 = this.velocity
            .horizontalLength();
         double dist9 = vec5.length();
         float yaw6 = MathHelper.cos(
            yaw5
         );
         yaw6 = (float)(
            (double)yaw6
               * (double)yaw6
               * Math.min(
                  1.0,
                  dist9
                     / 0.4
               )
         );
         vec4 = this.velocity
            .add(
               0.0,
               dist4
                  * (
                     -1.0
                        + (double)yaw6
                           * 0.75
                  ),
               0.0
            );
         if (vec4.y
               < 0.0
            && dist7
               > 0.0) {
            double dist10 = vec4.y
               * -0.1
               * (double)yaw6;
            vec4 = vec4.add(
               vec5.x
                  * dist10
                  / dist7,
               dist10,
               vec5.z
                  * dist10
                  / dist7
            );
         }

         if (yaw5
               < 0.0F
            && dist7
               > 0.0) {
            double dist11 = dist8
               * (double)(
                  -MathHelper.sin(
                     yaw5
                  )
               )
               * 0.04;
            vec4 = vec4.add(
               -vec5.x
                  * dist11
                  / dist7,
               dist11
                  * 3.2,
               -vec5.z
                  * dist11
                  / dist7
            );
         }

         if (dist7
            > 0.0) {
            vec4 = vec4.add(
               (
                     vec5.x
                           / dist7
                           * dist8
                        - vec4.x
                  )
                  * 0.1,
               0.0,
               (
                     vec5.z
                           / dist7
                           * dist8
                        - vec4.z
                  )
                  * 0.1
            );
         }

         this.velocity = vec4.multiply(
            0.99, 0.98, 0.99
         );
         this.move(this.velocity);
      } else {
         BlockPos pos3 = this.getVelocityAffectingPos();
         float yaw7 = this.player
            .getWorld()
            .getBlockState(
               pos3
            )
            .getBlock()
            .getSlipperiness();
         float yaw8 = this.onGround
            ? yaw7
               * 0.91F
            : 0.91F;
         Vec3d vec6 = this.applyMovementInput(
            vec,
            yaw7
         );
         double dist12 = vec6.y;
         if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
            StatusEffectInstance effect = this.getStatusEffect(
               StatusEffects.LEVITATION
            );
            if (effect
               != null) {
               dist12 += (
                     0.05
                           * (double)(
                              effect.getAmplifier()
                                 + 1
                           )
                        - vec6.y
                  )
                  * 0.2;
            }
         } else if (this.player.getWorld().isClient()
            && !this.player
               .getWorld()
               .isChunkLoaded(
                  pos3
               )) {
            dist12 = this.pos
                     .y
                  > (double)this.player.getWorld().getBottomY()
               ? -0.1
               : 0.0;
         } else if (!this.player.hasNoGravity()) {
            dist12 -= dist4;
         }

         if (this.player.hasNoDrag()) {
            this.velocity = new Vec3d(
               vec6.x,
               dist12,
               vec6.z
            );
         } else {
            this.velocity = new Vec3d(
               vec6.x
                  * (double)yaw8,
               dist12
                  * 0.98F,
               vec6.z
                  * (double)yaw8
            );
         }
      }

      if (this.player.getAbilities().flying && !this.player.hasVehicle()) {
         this.velocity = new Vec3d(
            this.velocity.x,
            dist3
               * 0.6,
            this.velocity.z
         );
         this.onLanding();
      }
   }

   private Vec3d applyMovementInput(
      Vec3d vec,
      float f
   ) {
      this.updateVelocity(
         this.getMovementSpeed(
            f
         ),
         vec
      );
      this.velocity = this.applyClimbingSpeed(this.velocity);
      this.move(this.velocity);
      Vec3d vec2 = this.velocity;
      BlockPos pos2 = this.posToBlockPos(
         this.pos
      );
      BlockState state = this.getState(
         pos2
      );
      if ((this.horizontalCollision || this.isJumping)
         && (
            this.isClimbing()
               || state
                     != null
                  && state.isOf(
                     Blocks.POWDER_SNOW
                  )
                  && PowderSnowBlock.canWalkOnPowderSnow(this.player)
         )) {
         vec2 = new Vec3d(
            vec2.x,
            0.2,
            vec2.z
         );
      }

      return vec2;
   }

   private void updateVelocity(
      float f,
      Vec3d vec
   ) {
      Vec3d vec2 = Entity.movementInputToVelocity(
         vec,
         f,
         this.yaw
      );
      this.velocity = this.velocity
         .add(
            vec2
         );
   }

   private float getMovementSpeed(
      float f
   ) {
      return this.onGround
         ? this.getMovementSpeed()
            * (
               0.21600002F
                  / (
                     f
                        * f
                        * f
                  )
            )
         : this.getAirStrafingSpeed();
   }

   private float getAirStrafingSpeed() {
      float y = 0.02F;
      return this.input.playerInput.sprint()
         ? y
            + 0.006F
         : y;
   }

   private float getMovementSpeed() {
      return 0.1F;
   }

   private void move(
      Vec3d vec
   ) {
      Vec3d vec2 = this.adjustMovementForSneaking(
         vec
      );
      Vec3d vec3 = this.adjustMovementForCollisions(
         vec2
      );
      if (vec3.lengthSquared()
         > 1.0E-7) {
         this.pos = this.pos
            .add(
               vec3
            );
         this.boundingBox = this.player.dimensions.getBoxAt(this.pos);
      }

      boolean flag = !MathHelper.approximatelyEquals(
         vec.x,
         vec3.x
      );
      boolean flag2 = !MathHelper.approximatelyEquals(
         vec.z,
         vec3.z
      );
      this.horizontalCollision = flag
         || flag2;
      this.verticalCollision = vec.y
         != vec3.y;
      this.onGround = this.verticalCollision
         && vec.y
            < 0.0;
      if (!this.isTouchingWater()) {
         this.checkWaterState();
      }

      if (this.onGround) {
         this.onLanding();
      } else if (vec.y
         < 0.0) {
         this.fallDistance = this.fallDistance
            - (float)vec.y;
      }

      Vec3d vec4 = this.velocity;
      if (this.horizontalCollision || this.verticalCollision) {
         this.velocity = new Vec3d(
            flag
               ? 0.0
               : vec4.x,
            this.onGround
               ? 0.0
               : vec4.y,
            flag2
               ? 0.0
               : vec4.z
         );
      }
   }

   private Vec3d adjustMovementForCollisions(
      Vec3d vec
   ) {
      Box box = new Box(
            -0.3, 0.0, -0.3, 0.3, 1.8, 0.3
         )
         .offset(this.pos);
      List val = Collections.emptyList();
      Vec3d vec2;
      if (vec.lengthSquared()
         == 0.0) {
         vec2 = vec;
      } else {
         vec2 = Entity.adjustMovementForCollisions(
            this.player,
            vec,
            box,
            this.player.getWorld(),
            val
         );
      }

      boolean flag = vec.x
         != vec2.x;
      boolean flag2 = vec.y
         != vec2.y;
      boolean flag3 = vec.z
         != vec2.z;
      boolean flag4 = this.onGround
         || flag2
            && vec.y
               < 0.0;
      if (this.player.getStepHeight() > 0.0F
         && flag4
         && (
            flag
               || flag3
         )) {
         Vec3d vec3 = Entity.adjustMovementForCollisions(
            this.player,
            new Vec3d(
               vec.x,
               (double)this.player.getStepHeight(),
               vec.z
            ),
            box,
            this.player.getWorld(),
            val
         );
         Vec3d vec4 = Entity.adjustMovementForCollisions(
            this.player,
            new Vec3d(0.0, (double)this.player.getStepHeight(), 0.0),
            box.stretch(
               vec.x,
               0.0,
               vec.z
            ),
            this.player.getWorld(),
            val
         );
         Vec3d vec5 = Entity.adjustMovementForCollisions(
               this.player,
               new Vec3d(
                  vec.x,
                  0.0,
                  vec.z
               ),
               box.offset(
                  vec4
               ),
               this.player.getWorld(),
               val
            )
            .add(
               vec4
            );
         if (vec4.y
               < (double)this.player.getStepHeight()
            && vec5.horizontalLengthSquared()
               > vec3.horizontalLengthSquared()
            )
          {
            vec3 = vec5;
         }

         if (vec3.horizontalLengthSquared()
            > vec2.horizontalLengthSquared()
            )
          {
            return vec3.add(
               Entity.adjustMovementForCollisions(
                  this.player,
                  new Vec3d(
                     0.0,
                     -vec3.y
                        + vec.y,
                     0.0
                  ),
                  box.offset(
                     vec3
                  ),
                  this.player.getWorld(),
                  val
               )
            );
         }
      }

      return vec2;
   }

   private void onLanding() {
      this.fallDistance = 0.0F;
   }

   public void jump() {
      this.velocity = this.velocity.add(0.0, (double)this.getJumpVelocity() - this.velocity.y, 0.0);
      if (this.isSprinting()) {
         float yaw2 = (float)Math.toRadians(
            (double)this.yaw
         );
         this.velocity = this.velocity
            .add(
               (double)(
                     -MathHelper.sin(
                        yaw2
                     )
                  )
                  * 0.2,
               0.0,
               (double)MathHelper.cos(
                     yaw2
                  )
                  * 0.2
            );
      }
   }

   private Vec3d applyClimbingSpeed(
      Vec3d vec
   ) {
      if (!this.isClimbing()) {
         return vec;
      } else {
         this.onLanding();
         double x2 = MathHelper.clamp(
            vec.x,
            -0.15F,
            0.15F
         );
         double x3 = MathHelper.clamp(
            vec.z,
            -0.15F,
            0.15F
         );
         double x4 = Math.max(
            vec.y,
            -0.15F
         );
         if (x4
               < 0.0
            && !this.getState(this.posToBlockPos(this.pos)).isOf(Blocks.SCAFFOLDING)
            && this.player.isHoldingOntoLadder()) {
            x4 = 0.0;
         }

         return new Vec3d(
            x2,
            x4,
            x3
         );
      }
   }

   public boolean isClimbing() {
      BlockPos pos2 = this.posToBlockPos(
         this.pos
      );
      BlockState state = this.getState(
         pos2
      );
      return state.isIn(
            BlockTags.CLIMBABLE
         )
         ? true
         : state.getBlock() instanceof TrapdoorBlock
            && this.canEnterTrapdoor(
               pos2,
               state
            );
   }

   private boolean canEnterTrapdoor(
      BlockPos pos,
      BlockState state
   ) {
      if (!(Boolean)state.get(
         TrapdoorBlock.OPEN
      )) {
         return false;
      } else {
         BlockState state2 = this.player
            .getWorld()
            .getBlockState(
               pos.down()
            );
         return state2.isOf(
               Blocks.LADDER
            )
            && ((Direction)state2.get(
                  LadderBlock.FACING
               ))
               .equals(
                  state.get(
                     TrapdoorBlock.FACING
                  )
               );
      }
   }

   private Vec3d adjustMovementForSneaking(
      Vec3d vec
   ) {
      if (vec.y
            <= 0.0
         && this.method_30263()) {
         double x2 = vec.x;
         double x3 = vec.z;

         double x4;
         for (x4 = 0.05;
            x2
                  != 0.0
               && this.player
                  .getWorld()
                  .isSpaceEmpty(
                     this.player,
                     this.boundingBox
                        .offset(
                           x2,
                           -0.5,
                           0.0
                        )
                  );
            x2 += x2
                  > 0.0
               ? -x4
               : x4
         ) {
            if (x2
                  < x4
               && x2
                  >= -x4
               )
             {
               x2 = 0.0;
               break;
            }
         }

         while (
            x3
                  != 0.0
               && this.player
                  .getWorld()
                  .isSpaceEmpty(
                     this.player,
                     this.boundingBox
                        .offset(
                           0.0,
                           -0.5,
                           x3
                        )
                  )
         ) {
            if (x3
                  < x4
               && x3
                  >= -x4
               )
             {
               x3 = 0.0;
               break;
            }

            x3 += x3
                  > 0.0
               ? -x4
               : x4;
         }

         while (
            x2
                  != 0.0
               && x3
                  != 0.0
               && this.player
                  .getWorld()
                  .isSpaceEmpty(
                     this.player,
                     this.boundingBox
                        .offset(
                           x2,
                           -0.5,
                           x3
                        )
                  )
         ) {
            x2 = x2
                     < x4
                  && x2
                     >= -x4
               ? 0.0
               : (
                  x2
                        > 0.0
                     ? x2
                        - x4
                     : x2
                        + x4
               );
            if (x3
                  < x4
               && x3
                  >= -x4
               )
             {
               x3 = 0.0;
               break;
            }

            x3 += x3
                  > 0.0
               ? -x4
               : x4;
         }

         if (vec.x
               != x2
            || vec.z
               != x3
            )
          {
            this.clipLedged = true;
         }

         if (this.shouldClipAtLedge()) {
            vec = new Vec3d(
               x2,
               vec.y,
               x3
            );
         }
      }

      return vec;
   }

   protected boolean shouldClipAtLedge() {
      return this.input.playerInput.sneak() || this.input.forceSafeWalk;
   }

   private boolean method_30263() {
      return this.onGround
         || (double)this.fallDistance < 0.5
            && !this.player.getWorld().isSpaceEmpty(this.player, this.boundingBox.offset(0.0, (double)this.fallDistance - 0.5, 0.0));
   }

   private boolean isSprinting() {
      return this.sprinting;
   }

   private float getJumpVelocity() {
      return 0.42F * this.getJumpVelocityMultiplier() + this.getJumpBoostVelocityModifier();
   }

   private float getJumpBoostVelocityModifier() {
      if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
         StatusEffectInstance effect = this.getStatusEffect(
            StatusEffects.JUMP_BOOST
         );
         return 0.1F
            * (float)(
               effect.getAmplifier()
                  + 1
            );
      } else {
         return 0.0F;
      }
   }

   private float getJumpVelocityMultiplier() {
      float y = 0.0F;
      Block val = this.getState(
            this.posToBlockPos(this.pos)
         )
         .getBlock();
      if (val
         != null) {
         y = val.getJumpVelocityMultiplier();
      }

      float y2 = 0.0F;
      Block val2 = this.getState(
            this.getVelocityAffectingPos()
         )
         .getBlock();
      if (val2
         != null) {
         y2 = val2.getJumpVelocityMultiplier();
      }

      return y
            == 1.0F
         ? y2
         : y;
   }

   private boolean doesNotCollide(
      double d,
      double d2,
      double d3
   ) {
      return this.doesNotCollide(
         this.boundingBox
            .offset(
               d,
               d2,
               d3
            )
      );
   }

   private boolean doesNotCollide(
      Box box
   ) {
      return this.player
            .getWorld()
            .isSpaceEmpty(
               this.player,
               box
            )
         && !this.player
            .getWorld()
            .containsFluid(
               box
            );
   }

   private void swimUpward(
      TagKey<Fluid> val
   ) {
      this.velocity = this.velocity.add(0.0, 0.04F, 0.0);
   }

   private BlockPos getVelocityAffectingPos() {
      return BlockPos.ofFloored(this.pos.x, this.boundingBox.minY - 0.5000001, this.pos.z);
   }

   private double getSwimHeight() {
      return (double)this.player.getStandingEyeHeight() < 0.4 ? 0.0 : 0.4;
   }

   private boolean isTouchingWater() {
      return this.touchingWater;
   }

   public boolean isInLava() {
      return this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0;
   }

   private void checkWaterState() {
      if (this.player.getVehicle() instanceof BoatEntity) {
         BoatEntity entity = (BoatEntity)this.player
            .getVehicle();
         if (!entity.isSubmergedInWater()
            )
          {
            this.touchingWater = false;
            return;
         }
      }

      if (this.updateMovementInFluid(FluidTags.WATER, 0.014)) {
         this.onLanding();
         this.touchingWater = true;
      } else {
         this.touchingWater = false;
      }
   }

   private void updateSwimming() {
      if (this.isSwimming) {
         this.isSwimming = this.isSprinting() && this.isTouchingWater() && !this.player.hasVehicle();
      } else {
         this.isSwimming = this.isSprinting()
            && this.isSubmergedInWater()
            && !this.player.hasVehicle()
            && this.player.getWorld().getFluidState(this.posToBlockPos(this.pos)).isIn(FluidTags.WATER);
      }
   }

   private void updateSubmergedInWaterState() {
      this.submergedInWater = this.submergedFluidTag.contains(FluidTags.WATER);
      this.submergedFluidTag.clear();
      double x2 = this.getEyeY()
         - 0.11111111F;
      if (this.player.getVehicle() instanceof BoatEntity val
         && !val.isSubmergedInWater()
         && val.getBoundingBox()
               .maxY
            >= x2
         && val.getBoundingBox()
               .minY
            <= x2
         )
       {
         return;
      }

      BlockPos pos2 = BlockPos.ofFloored(
         this.pos.x,
         x2,
         this.pos.z
      );
      FluidState val2 = this.player
         .getWorld()
         .getFluidState(
            pos2
         );
      double x3 = (double)(
         (float)pos2.getY()
            + val2.getHeight(
               this.player.getWorld(),
               pos2
            )
      );
      if (x3
         > x2
         )
       {
         this.submergedFluidTag
            .addAll(
               val2.streamTags()
                  .toList()
            );
      }
   }

   private double getEyeY() {
      return this.pos.y + (double)this.player.getStandingEyeHeight();
   }

   public boolean isSubmergedInWater() {
      return this.submergedInWater && this.isTouchingWater();
   }

   private double getFluidHeight(
      TagKey<Fluid> val
   ) {
      return this.fluidHeight
         .getDouble(
            val
         );
   }

   private boolean updateMovementInFluid(
      TagKey<Fluid> val,
      double d
   ) {
      if (this.isRegionUnloaded()) {
         return false;
      } else {
         Box box = this.boundingBox
            .contract(0.001);
         int i = MathHelper.floor(
            box.minX
         );
         int i2 = MathHelper.ceil(
            box.maxX
         );
         int i3 = MathHelper.floor(
            box.minY
         );
         int i4 = MathHelper.ceil(
            box.maxY
         );
         int i5 = MathHelper.floor(
            box.minZ
         );
         int i6 = MathHelper.ceil(
            box.maxZ
         );
         double x2 = 0.0;
         boolean flag = true;
         boolean flag2 = false;
         Vec3d vec = Vec3d.ZERO;
         int i7 = 0;
         Mutable val2 = new Mutable();

         for (int i8 = i;
            i8
               < i2;
            i8++
         ) {
            for (int i9 = i3;
               i9
                  < i4;
               i9++
            ) {
               for (int i10 = i5;
                  i10
                     < i6;
                  i10++
               ) {
                  val2.set(
                     i8,
                     i9,
                     i10
                  );
                  FluidState val3 = this.player
                     .getWorld()
                     .getFluidState(
                        val2
                     );
                  if (val3.isIn(
                     val
                  )) {
                     double x3 = (double)(
                        (float)i9
                           + val3.getHeight(
                              this.player.getWorld(),
                              val2
                           )
                     );
                     if (x3
                        >= box.minY
                        )
                      {
                        flag2 = true;
                        x2 = Math.max(
                           x3
                              - box.minY,
                           x2
                        );
                        if (flag
                           )
                         {
                           Vec3d vec2 = val3.getVelocity(
                              this.player.getWorld(),
                              val2
                           );
                           if (x2
                              < 0.4) {
                              vec2 = vec2.multiply(
                                 x2
                              );
                           }

                           vec = vec.add(
                              vec2
                           );
                           i7++;
                        }
                     }
                  }
               }
            }
         }

         if (vec.length()
            > 0.0) {
            if (i7
               > 0) {
               vec = vec.multiply(
                  1.0
                     / (double)i7
               );
            }

            vec = vec.multiply(
               d
            );
            if (Math.abs(this.velocity.x) < 0.003
               && Math.abs(this.velocity.z) < 0.003
               && vec.length()
                  < 0.0045) {
               vec = vec.normalize()
                  .multiply(0.0045);
            }

            this.velocity = this.velocity
               .add(
                  vec
               );
         }

         this.fluidHeight
            .put(
               val,
               x2
            );
         return flag2;
      }
   }

   private boolean isRegionUnloaded() {
      Box box = this.boundingBox
         .expand(1.0);
      int x = MathHelper.floor(
         box.minX
      );
      int x2 = MathHelper.ceil(
         box.maxX
      );
      int x3 = MathHelper.floor(
         box.minZ
      );
      int x4 = MathHelper.ceil(
         box.maxZ
      );
      return !this.player
         .getWorld()
         .isRegionLoaded(
            x,
            x3,
            x2,
            x4
         );
   }

   private Vec3d getRotationVector() {
      return this.getRotationVector(this.pitch, this.yaw);
   }

   private Vec3d getRotationVector(
      float f,
      float f2
   ) {
      float f3 = (float)(
         (double)f
            * Math.PI
            / 180.0
      );
      float f4 = (float)(
         (double)(
               -f2
            )
            * Math.PI
            / 180.0
      );
      float f5 = MathHelper.cos(
         f4
      );
      float f6 = MathHelper.sin(
         f4
      );
      float f7 = MathHelper.cos(
         f3
      );
      float f8 = MathHelper.sin(
         f3
      );
      return new Vec3d(
         (double)(
            f6
               * f7
         ),
         (double)(
            -f8
         ),
         (double)(
            f5
               * f7
         )
      );
   }

   public boolean hasStatusEffect(
      RegistryEntry<StatusEffect> effect
   ) {
      StatusEffectInstance effect2 = this.player
         .getStatusEffect(
            effect
         );
      return effect2
            != null
         && effect2.getDuration()
            >= this.simulatedTicks;
   }

   private StatusEffectInstance getStatusEffect(
      RegistryEntry<StatusEffect> effect
   ) {
      StatusEffectInstance effect2 = this.player
         .getStatusEffect(
            effect
         );
      return effect2
               != null
            && effect2.getDuration()
               >= this.simulatedTicks
         ? effect2
         : null;
   }

   public double getAttributeValue(
      RegistryEntry<EntityAttribute> entity
   ) {
      return this.player
         .getAttributes()
         .getValue(
            entity
         );
   }

   public SimulatelocalplayerHelper clone() {
      return new SimulatelocalplayerHelper(
         this.player,
         this.input,
         this.pos,
         this.velocity,
         this.boundingBox,
         this.yaw,
         this.pitch,
         this.sprinting,
         this.fallDistance,
         this.jumpingCooldown,
         this.isJumping,
         this.isFallFlying,
         this.onGround,
         this.horizontalCollision,
         this.verticalCollision,
         this.touchingWater,
         this.isSwimming,
         this.submergedInWater,
         new Object2DoubleArrayMap(this.fluidHeight),
         new HashSet<>(this.submergedFluidTag)
      );
   }

   public BlockPos posToBlockPos(
      Vec3d vec
   ) {
      return new BlockPos(
         MathHelper.floor(
            vec.x
         ),
         MathHelper.floor(
            vec.y
         ),
         MathHelper.floor(
            vec.z
         )
      );
   }

   public BlockState getState(
      BlockPos pos
   ) {
      return this.player
         .getWorld()
         .getBlockState(
            pos
         );
   }
}
