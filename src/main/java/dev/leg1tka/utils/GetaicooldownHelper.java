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



import dev.leg1tka.modules.movement.*;
import dev.leg1tka.mixin.IEntity;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class GetaicooldownHelper implements IMinecraft {
   private static final int WATER_CRIT_INTENT_TICKS = 8;
   private static final int WATER_CRIT_CONTACT_TICKS = 10;
   private static final double WATER_CRIT_MIN_UPWARD_VELOCITY = 0.05;
   private static int lastWaterContactAge = Integer.MIN_VALUE;
   private static int lastWaterCritIntentAge = Integer.MIN_VALUE;

   public static float getAICooldown() {
      if (mc.player.getMainHandStack().getItem() == Items.AIR) {
         return 0.9F;
      } else {
         return !(mc.player.getMainHandStack().getItem() instanceof AxeItem) && !(mc.player.getMainHandStack().getItem() instanceof ShovelItem)
            ? 0.93F
            : 0.95F;
      }
   }

   public static boolean canAIFall() {
      BlockPos pos = BlockPos.ofFloored(
         mc.player.getPos().add(0.0, -0.4F, 0.0)
      );
      return mc.world
            .getBlockState(
               pos
            )
            .isOf(Blocks.WATER)
         ? true
         : getBlock(0.0, 3.0, 0.0) == Blocks.AIR && getBlock(0.0, 2.0, 0.0) == Blocks.AIR && getBlock(0.0, 1.0, 0.0) == Blocks.AIR
            || mc.player.fallDistance < (getBlock(0.0, 2.0, 0.0) != Blocks.AIR ? 0.08F : 0.6F)
            || mc.player.fallDistance > 1.2F;
   }

   public static boolean canCritical(
      LivingEntity entity
   ) {
      return canCritical(
         entity,
         0.0F
      );
   }

   public static boolean canCritical(
      LivingEntity entity,
      float f
   ) {
      updateWaterCritState();
      boolean flag = ModuleManager.packetCriticals
         .isEnable();
      boolean flag2 = mc.player
         .hasStatusEffect(StatusEffects.SLOW_FALLING);
      boolean flag3 = isInCobweb();
      boolean flag4 = ModuleManager.aura
         .smartCrit
         .isState();
      if (flag
         && flag3
         )
       {
         return true;
      } else if (flag
         && flag2
         )
       {
         return mc.player.getVelocity().y < 0.0
            && mc.player.fallDistance
               >= f;
      } else if (isTryingWaterCrit()) {
         return isWaterCritWindow();
      } else {
         boolean flag5 = !mc.player
               .isOnGround()
            && mc.player.getVelocity().y < 0.0
            && mc.player.fallDistance
               >= f;
         if (isNoJumpDelayCeilingCritIntent()) {
            return isNoJumpDelayCeilingCritWindow();
         } else if (isNoJumpDelayJumpCritIntent()) {
            return isNoJumpDelayJumpCritWindow();
         } else if (cannotPerformCrit()) {
            return true;
         } else {
            return !flag4
               ? flag5
               : mc.player.isOnGround()
                  || flag5;
         }
      }
   }

   public static boolean enhancedCanCritical(
      LivingEntity entity
   ) {
      return enhancedCanCritical(
         entity,
         0.0F
      );
   }

   public static boolean enhancedCanCritical(
      LivingEntity entity,
      float f
   ) {
      updateWaterCritState();
      boolean flag = ModuleManager.packetCriticals
         .isEnable();
      boolean flag2 = mc.player
         .hasStatusEffect(StatusEffects.SLOW_FALLING);
      boolean flag3 = isInCobweb();
      if (flag
         && flag3
         )
       {
         return true;
      } else if (flag
         && flag2
         )
       {
         return mc.player.getVelocity().y < 0.0
            && mc.player.fallDistance
               >= f;
      } else if (isTryingWaterCrit()) {
         return isWaterCritWindow();
      } else {
         boolean flag4 = !mc.player
               .isOnGround()
            && mc.player.getVelocity().y < 0.0
            && mc.player.fallDistance
               >= f;
         if (isNoJumpDelayCeilingCritIntent()) {
            return isNoJumpDelayCeilingCritWindow();
         } else if (isNoJumpDelayJumpCritIntent()) {
            return isNoJumpDelayJumpCritWindow();
         } else {
            return cannotPerformCrit()
               ? true
               : flag4;
         }
      }
   }

   public static boolean isAirStuckCritValid() {
      if (ModuleManager.INSTANCE == null) {
         return false;
      } else {
         AirStuck val = AirStuck.INSTANCE;
         return val
               != null
            && val.isEnable()
            && val.isFrozen();
      }
   }

   private static boolean isNoJumpDelayCeilingCritIntent() {
      return ModuleManager.noJumpDelay.isEnable() && mc.options != null && mc.options.jumpKey.isPressed() && hasLowCeilingForJumpCrit();
   }

   private static boolean isNoJumpDelayJumpCritIntent() {
      return ModuleManager.noJumpDelay.isEnable() && mc.options != null && mc.options.jumpKey.isPressed();
   }

   private static boolean isNoJumpDelayCeilingCritWindow() {
      return mc.player != null
         && !mc.player.isOnGround()
         && mc.player.getVelocity().y <= 0.01
         && !mc.player.isTouchingWater()
         && !mc.player.isSubmergedInWater()
         && !mc.player.isInLava()
         && !mc.player.isClimbing()
         && !mc.player.hasVehicle()
         && !mc.player.getAbilities().flying;
   }

   public static boolean isNoJumpDelayJumpCritWindow() {
      return mc.player != null
         && mc.world != null
         && ModuleManager.noJumpDelay.isEnable()
         && mc.options != null
         && mc.options.jumpKey.isPressed()
         && !mc.player.isOnGround()
         && mc.player.getVelocity().y < 0.0
         && !mc.player.isTouchingWater()
         && !mc.player.isSubmergedInWater()
         && !mc.player.isInLava()
         && !mc.player.isClimbing()
         && !mc.player.hasVehicle()
         && !mc.player.getAbilities().flying
         && !mc.player.hasStatusEffect(StatusEffects.LEVITATION)
         && !mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
         && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
         && !mc.player.isGliding()
         && !isInCobweb();
   }

   private static boolean hasLowCeilingForJumpCrit() {
      if (mc.player != null && mc.world != null) {
         Box box = mc.player
            .getBoundingBox()
            .contract(0.03);
         Box box2 = new Box(
            box.minX,
            box.maxY,
            box.minZ,
            box.maxX,
            box.maxY
               + 0.32,
            box.maxZ
         );

         for (BlockPos pos : BlockPos.iterate(
            MathHelper.floor(
               box2.minX
            ),
            MathHelper.floor(
               box2.minY
            ),
            MathHelper.floor(
               box2.minZ
            ),
            MathHelper.floor(
               box2.maxX
            ),
            MathHelper.floor(
               box2.maxY
            ),
            MathHelper.floor(
               box2.maxZ
            )
         )) {
            BlockState state = mc.world
               .getBlockState(
                  pos
               );
            if (!state.isAir()
               && !state.getCollisionShape(
                     mc.world,
                     pos
                  )
                  .isEmpty()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean canPacketCrit() {
      return isInCobweb() || mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING);
   }

   private static void updateWaterCritState() {
      if (mc.player != null && mc.world != null) {
         boolean flag = isNearWaterSurface();
         if (flag
            )
          {
            lastWaterContactAge = mc.player.age;
            if (isWaterCritIntentState()) {
               lastWaterCritIntentAge = mc.player.age;
            }
         }
      } else {
         lastWaterContactAge = -2147483648;
         lastWaterCritIntentAge = -2147483648;
      }
   }

   private static boolean isWaterCritIntentState() {
      return mc.player != null && mc.options != null
         ? mc.options.jumpKey.isPressed()
            && !mc.player.isOnGround()
            && !mc.player.isSubmergedInWater()
            && mc.player.getVelocity().y > 0.05
         : false;
   }

   private static boolean isTryingWaterCrit() {
      return mc.player != null && mc.options != null && mc.options.jumpKey.isPressed()
         ? mc.player.age - lastWaterCritIntentAge <= 8
            && mc.player.age - lastWaterContactAge <= 10
         : false;
   }

   private static boolean isWaterCritWindow() {
      return mc.player != null
         && !mc.player.isOnGround()
         && !mc.player.isTouchingWater()
         && !mc.player.isSubmergedInWater()
         && mc.player.fallDistance > 0.0F
         && mc.player.getVelocity().y < 0.0;
   }

   private static boolean isNearWaterSurface() {
      if (mc.player != null && mc.world != null) {
         BlockPos pos = BlockPos.ofFloored(
            mc.player.getPos().add(0.0, -0.4F, 0.0)
         );
         return mc.player.isTouchingWater()
            || mc.player.isSubmergedInWater()
            || mc.world
               .getBlockState(
                  pos
               )
               .isOf(Blocks.WATER);
      } else {
         return false;
      }
   }

   private static boolean cannotPerformCrit() {
      double y2 = (double)mc.player
         .getStepHeight();
      Vec3d vec = new Vec3d(
         0.0,
         y2,
         0.0
      );
      Vec3d vec2 = ((IEntity)mc.player)
         .invokeAdjustMovementForCollisions(
            vec
         );
      boolean flag = isInCobweb();
      BlockPos pos = BlockPos.ofFloored(
         mc.player.getPos().add(0.0, (double)(mc.player.getHeight() / 2.0F), 0.0)
      );
      return mc.player.isInLava()
         || mc.player.isClimbing()
         || mc.world
            .getBlockState(
               pos
            )
            .isOf(Blocks.WATER)
         || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
         || mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
         || mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
         || flag
         || mc.player.isGliding()
         || mc.player.hasVehicle()
         || mc.player.getAbilities().flying
         || mc.player.isTouchingWater()
         || vec2.y
               < (double)mc.player.getStepHeight() - 0.5
            && mc.player.isOnGround();
   }

   public static boolean isInCobweb() {
      Box box = mc.player
         .getBoundingBox();

      for (BlockPos pos : BlockPos.iterate(
         MathHelper.floor(
            box.minX
         ),
         MathHelper.floor(
            box.minY
         ),
         MathHelper.floor(
            box.minZ
         ),
         MathHelper.floor(
            box.maxX
         ),
         MathHelper.floor(
            box.maxY
         ),
         MathHelper.floor(
            box.maxZ
         )
      )) {
         if (mc.world
            .getBlockState(
               pos
            )
            .isOf(Blocks.COBWEB)) {
            return true;
         }
      }

      return false;
   }

   public static Block getBlock(
      double d,
      double d2,
      double d3
   ) {
      return mc.world
         .getBlockState(
            mc.player
               .getBlockPos()
               .add(
                  (int)d,
                  (int)d2,
                  (int)d3
               )
         )
         .getBlock();
   }

   public static boolean findFall(
      float f
   ) {
      Vec3d vec = mc.player
         .getRotationVector();
      double x2 = mc.player
         .getVelocity()
         .x;
      double x3 = mc.player
         .getVelocity()
         .y;
      double x4 = mc.player
         .getVelocity()
         .z;
      float pitch = MathHelper.cos(
         mc.player.getPitch() * (float) (Math.PI / 180.0)
      );
      pitch = (float)(
         (double)(
               pitch
                  * pitch
            )
            * Math.min(
               vec.length()
                  / 0.4,
               1.0
            )
      );
      Vec3d vec2 = new Vec3d(
            x2,
            x3,
            x4
         )
         .add(
            0.0,
            0.08
               * (
                  -1.0
                     + (double)pitch
                        * 0.75
               ),
            0.0
         );
      x3 = vec2.y
         * 0.98F;
      return x3
         < (double)f;
   }

   @Generated
   private GetaicooldownHelper() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
