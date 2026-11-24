package dev.leg1tka.modules.combat;

import dev.leg1tka.modules.player.AutoEat;



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
import dev.leg1tka.modules.movement.Sprint;

import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventAttackEntity;
import dev.leg1tka.api.events.implement.EventChangeSprint;
import dev.leg1tka.api.events.implement.EventGameUpdate;
import dev.leg1tka.api.events.implement.EventKeyboardInput;
import dev.leg1tka.api.events.implement.EventLook;
import dev.leg1tka.api.events.implement.EventMoveInput;
import dev.leg1tka.api.events.implement.EventUpdate;
import dev.leg1tka.api.events.implement.EventUpdatePost;
import dev.leg1tka.mixin.ILivingEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import lombok.Generated;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class Aura extends Module {
   public static Aura INSTANCE = new Aura();
   public final ModeSetting rotationType = new ModeSetting(
      "Тип",
      "Matrix/Fast",
      "Matrix/Fast",
      "SC3",
      "ShardAC",
      "ShardAC-AI",
      "AresMine",
      "AresMineNEW",
      "NoRotate",
      "WhiteRise",
      "Matrix/Vulcan",
      "PolarAC/Old",
      "Grim/Test",
      "Assist"
   );
   public final NumberSetting assistRadius = new NumberSetting(
         "Assist Radius", 0.0F, -0.5F, 1.0F, 0.01F
      )
      .visible(
         () -> this.rotationType.is("Assist")
      );
   public final NumberSetting assistSpeedAimed = new NumberSetting(
         "Assist Speed Aimed", 0.25F, 0.0F, 5.0F, 0.01F
      )
      .visible(
         () -> this.rotationType.is("Assist")
      );
   public final NumberSetting assistSpeedGround = new NumberSetting(
         "Assist Speed Ground", 2.0F, 0.0F, 5.0F, 0.01F
      )
      .visible(
         () -> this.rotationType.is("Assist")
      );
   public final NumberSetting assistSpeedAir = new NumberSetting(
         "Assist Speed Air", 2.0F, 0.0F, 5.0F, 0.01F
      )
      .visible(
         () -> this.rotationType.is("Assist")
      );
   public final NumberSetting assistSpeedElytra = new NumberSetting(
         "Assist Speed Elytra", 2.0F, 0.0F, 10.0F, 0.01F
      )
      .visible(
         () -> this.rotationType.is("Assist")
      );
   public final BooleanSetting clientLookAssist = new BooleanSetting(
         "Client Look Assist", false
      )
      .visible(
         () -> this.rotationType.is("Assist")
      );
   private final BooleanSetting maceBypass = new BooleanSetting(
      "Бить булавой",
      false
   );
   private int maceTicks = 0;
   private final MultiBooleanSetting targets = new MultiBooleanSetting(
      "Цели",
      new BooleanSetting("Игроки", true),
      new BooleanSetting("Голые", true),
      new BooleanSetting(
         "Невидимки", true
      ),
      new BooleanSetting("Мирные", false),
      new BooleanSetting("Мобы", true)
   );
   public final NumberSetting range = new NumberSetting(
      "Дистанция атаки",
      3.0F,
      0.0F,
      6.0F,
      0.05F
   );
   public final NumberSetting aimRange = new NumberSetting(
      "Дистанция наводки",
      3.0F,
      0.0F,
      6.0F,
      0.05F
   );
   public final NumberSetting elytraAimRange = new NumberSetting(
      "Дистанция на элитрах",
      50.0F,
      10.0F,
      100.0F,
      0.05F
   );
   public final BooleanSetting smartCrit = new BooleanSetting(
      "Умные криты",
      false
   );
   public final BooleanSetting enhancedCrit = new BooleanSetting(
      "Улучшить криты",
      false
   );
   public final ModeSetting critCooldownMode = new ModeSetting(
      "Кулдаун ударов",
      "Быстрый",
      "Быстрый",
      "Адаптивный",
      "Рандомный",
      "Кастом"
   );
   public final NumberSetting customCritCooldown = new NumberSetting(
         "Мин. кулдаун",
         0.93F,
         0.85F,
         1.0F,
         0.01F
      )
      .visible(
         () -> this.critCooldownMode.is("Кастом")
      );
   public final NumberSetting customMinFall = new NumberSetting(
         "Мин. падение",
         0.05F,
         0.0F,
         0.5F,
         0.01F
      )
      .visible(
         () -> this.critCooldownMode.is("Кастом")
      );
   public final NumberSetting customHitDelay = new NumberSetting(
         "Задержка (мс)",
         0.0F,
         0.0F,
         150.0F,
         5.0F
      )
      .visible(
         () -> this.critCooldownMode.is("Кастом")
      );
   public final BooleanSetting customRandomize = new BooleanSetting(
         "Рандомизация",
         true
      )
      .visible(
         () -> this.critCooldownMode.is("Кастом")
      );
   public final ModeSetting sprintMode = new ModeSetting(
      "Сброс спринта",
      "Обычный",
      "Нет",
      "Обычный",
      "Пакетный",
      "WTap",
      "Легитный",
      "WellMine"
   );
   private final BooleanSetting raycast = new BooleanSetting(
      "Проверка на наведение",
      false
   );
   public final BooleanSetting expandHitbox = new BooleanSetting(
         "Расширять хитбокс",
         false
      )
      .visible(this.raycast::isState);
   public final NumberSetting expandHitboxValue = new NumberSetting(
         "Размер расширения",
         0.05F,
         0.0F,
         1.0F,
         0.05F
      )
      .visible(() -> this.raycast.isState() && this.expandHitbox.isState());
   private final BooleanSetting unpressShield = new BooleanSetting(
      "Отжимать щит",
      false
   );
   private final BooleanSetting breakShield = new BooleanSetting(
      "Ломать щит", true
   );
   private final BooleanSetting attackOnEating = new BooleanSetting(
      "Не бить когда ешь",
      true
   );
   public static BooleanSetting clientLook = new BooleanSetting("Наводка от первого лица", false);
   private final ModeSetting moveFix = new ModeSetting(
      "Коррекция",
      "Свободная",
      "Свободная",
      "Сфокусированная",
      "Таргетированная"
   );
   private final ModeSetting priority = new ModeSetting(
      "Приоритет",
      "Дистанция",
      "Дистанция",
      "Здоровье",
      "Угол",
      "Никакой"
   );
   public final ModeSetting sc3SpeedMode = new ModeSetting(
         "SC3: Режим скорости",
         "Ускорение",
         "Ускорение",
         "Постоянная",
         "Адаптивная"
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final BooleanSetting sc3TimingLead = new BooleanSetting(
         "SC3: Доводка", false
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3ConstantSpeedYaw = new NumberSetting(
         "SC3: Скорость Yaw",
         45.0F,
         5.0F,
         180.0F,
         5.0F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            if (var10000.is("SC3")) {
               var10000 = this.sc3SpeedMode;
               if (var10000.is("Постоянная")) {
                  return true;
               }
            }

            return false;
         }
      );
   public final NumberSetting sc3ConstantSpeedPitch = new NumberSetting(
         "SC3: Скорость Pitch",
         30.0F,
         5.0F,
         180.0F,
         5.0F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            if (var10000.is("SC3")) {
               var10000 = this.sc3SpeedMode;
               if (var10000.is("Постоянная")) {
                  return true;
               }
            }

            return false;
         }
      );
   public final BooleanSetting sc3FreezeOnHitbox = new BooleanSetting(
         "SC3: Застывать на хитбоксе",
         false
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3AccelIncrement = new NumberSetting(
         "SC3: Прирост ускорения",
         0.004F,
         0.001F,
         0.02F,
         0.001F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            if (var10000.is("SC3")) {
               var10000 = this.sc3SpeedMode;
               if (var10000.is("Ускорение")) {
                  return true;
               }
            }

            return false;
         }
      );
   public final NumberSetting sc3AccelDecrementBack = new NumberSetting(
         "SC3: Спад ускорения",
         0.01F,
         0.001F,
         0.1F,
         0.001F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            if (var10000.is("SC3")) {
               var10000 = this.sc3SpeedMode;
               if (var10000.is("Ускорение")) {
                  return true;
               }
            }

            return false;
         }
      );
   public final NumberSetting sc3AccelDecrementBackBigAngle = new NumberSetting(
         "SC3: Спад при большом угле",
         0.1F,
         0.01F,
         0.3F,
         0.01F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            if (var10000.is("SC3")) {
               var10000 = this.sc3SpeedMode;
               if (var10000.is("Ускорение")) {
                  return true;
               }
            }

            return false;
         }
      );
   public final NumberSetting sc3MaxAcceleration = new NumberSetting(
         "SC3: Макс. ускорение",
         0.18F,
         0.1F,
         0.5F,
         0.01F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            if (var10000.is("SC3")) {
               var10000 = this.sc3SpeedMode;
               if (var10000.is("Ускорение")) {
                  return true;
               }
            }

            return false;
         }
      );
   public final NumberSetting sc3MinAcceleration = new NumberSetting(
         "SC3: Мин. ускорение",
         -0.02F,
         -0.1F,
         0.0F,
         0.01F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            if (var10000.is("SC3")) {
               var10000 = this.sc3SpeedMode;
               if (var10000.is("Ускорение")) {
                  return true;
               }
            }

            return false;
         }
      );
   public final NumberSetting sc3BigAngleThreshold = new NumberSetting(
         "SC3: Порог большого угла",
         80.0F,
         30.0F,
         180.0F,
         5.0F
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3RaytraceDistance = new NumberSetting(
         "SC3: Дистанция RayTrace",
         999.0F,
         100.0F,
         2000.0F,
         10.0F
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3BoxExpand = new NumberSetting(
         "SC3: Расширение хитбокса",
         -0.2F,
         -1.0F,
         1.0F,
         0.1F
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final ModeSetting sc3PointMode = new ModeSetting(
         "SC3: Режим точки",
         "Ближайшая видимая",
         "Ближайшая видимая",
         "Грудь",
         "Голова"
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final BooleanSetting sc3Multipoint = new BooleanSetting(
         "SC3: Мультипоинт",
         true
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final ModeSetting sc3MultipointMode = new ModeSetting(
         "SC3: Режим мультипоинта",
         "Обычный",
         "Обычный",
         "Мягкий",
         "Агрессивный",
         "Органический",
         "Скелетный",
         "Гауссов"
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3Multipoint.isState();
         }
      );
   public final BooleanSetting sc3PointLock = new BooleanSetting(
         "SC3: Фиксация точки",
         true
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3PointLockTicks = new NumberSetting(
         "SC3: Время фиксации",
         3.0F,
         1.0F,
         10.0F,
         1.0F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3PointLock.isState();
         }
      );
   public final NumberSetting sc3SmoothMultiplier = new NumberSetting(
         "SC3: Множитель плавности Yaw",
         1.0F,
         0.1F,
         2.5F,
         0.1F
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3SmoothMultiplierPitch = new NumberSetting(
         "SC3: Множитель плавности Pitch",
         0.5F,
         0.1F,
         2.5F,
         0.1F
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3MaxPitchDownPercent = new NumberSetting(
         "SC3: Максимально вниз",
         100.0F,
         0.0F,
         100.0F,
         1.0F
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3MaxPitchUpPercent = new NumberSetting(
         "SC3: Максимально вверх",
         100.0F,
         0.0F,
         100.0F,
         1.0F
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3HitboxSlowdownStrength = new NumberSetting(
         "SC3: Замедление на хитбоксе",
         0.26F,
         0.05F,
         1.0F,
         0.01F
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3HitboxSpeedReturn = new NumberSetting(
         "SC3: Возврат скорости",
         0.03F,
         0.01F,
         0.5F,
         0.01F
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final BooleanSetting sc3AntiTurn = new BooleanSetting(
         "SC3: Анти резкий разворот",
         true
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3AntiTurnThreshold = new NumberSetting(
         "SC3: Порог анти разворота",
         110.0F,
         45.0F,
         180.0F,
         5.0F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3AntiTurn.isState();
         }
      );
   public final NumberSetting sc3AntiTurnStrength = new NumberSetting(
         "SC3: Сила анти разворота",
         0.45F,
         0.05F,
         0.95F,
         0.05F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3AntiTurn.isState();
         }
      );
   public final BooleanSetting sc3GcdFix = new BooleanSetting(
         "SC3: Исправление GCD",
         false
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final BooleanSetting sc3Overshoot = new BooleanSetting(
         "SC3: Микро-овершут",
         false
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3OvershootStrength = new NumberSetting(
         "SC3: Сила овершута",
         0.6F,
         0.1F,
         2.0F,
         0.05F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3Overshoot.isState();
         }
      );
   public final BooleanSetting sc3ReactionDelay = new BooleanSetting(
         "SC3: Задержка реакции",
         false
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3ReactionTimeMs = new NumberSetting(
         "SC3: Время реакции (мс)",
         80.0F,
         20.0F,
         200.0F,
         5.0F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ReactionDelay.isState();
         }
      );
   public final BooleanSetting sc3Bezier = new BooleanSetting(
         "SC3: Траектория Безье",
         false
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final BooleanSetting sc3ShakeYaw = new BooleanSetting("SC3: Shake Yaw", false)
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3ShakeSpeedYaw = new NumberSetting(
         "SC3: Скорость Shake Yaw",
         0.45F,
         0.0F,
         3.0F,
         0.05F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakeYaw.isState();
         }
      );
   public final NumberSetting sc3ShakeAmplitudeYaw = new NumberSetting(
         "SC3: Амплитуда Shake Yaw",
         1.8F,
         0.0F,
         10.0F,
         0.1F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakeYaw.isState();
         }
      );
   public final NumberSetting sc3ShakeDampingYaw = new NumberSetting(
         "SC3: Затухание Shake Yaw",
         0.3F,
         0.0F,
         1.0F,
         0.01F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakeYaw.isState();
         }
      );
   public final NumberSetting sc3ShakeBounceYaw = new NumberSetting(
         "SC3: Отскок Shake Yaw",
         0.1F,
         0.0F,
         1.0F,
         0.05F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakeYaw.isState();
         }
      );
   public final NumberSetting sc3ShakeRandomYaw = new NumberSetting(
         "SC3: Рандом Shake Yaw",
         0.8F,
         0.0F,
         1.0F,
         0.05F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakeYaw.isState();
         }
      );
   public final BooleanSetting sc3ShakePitch = new BooleanSetting(
         "SC3: Shake Pitch", false
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3ShakeSpeedPitch = new NumberSetting(
         "SC3: Скорость Shake Pitch",
         0.25F,
         0.0F,
         3.0F,
         0.05F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakePitch.isState();
         }
      );
   public final NumberSetting sc3ShakeAmplitudePitch = new NumberSetting(
         "SC3: Амплитуда Shake Pitch",
         0.8F,
         0.0F,
         6.0F,
         0.1F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakePitch.isState();
         }
      );
   public final NumberSetting sc3ShakeDampingPitch = new NumberSetting(
         "SC3: Затухание Shake Pitch",
         0.35F,
         0.0F,
         1.0F,
         0.01F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakePitch.isState();
         }
      );
   public final NumberSetting sc3ShakeBouncePitch = new NumberSetting(
         "SC3: Отскок Shake Pitch",
         0.0F,
         0.0F,
         1.0F,
         0.05F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakePitch.isState();
         }
      );
   public final NumberSetting sc3ShakeRandomPitch = new NumberSetting(
         "SC3: Рандом Shake Pitch",
         0.5F,
         0.0F,
         1.0F,
         0.05F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3ShakePitch.isState();
         }
      );
   public final BooleanSetting sc3Jitter = new BooleanSetting("SC3: Jitter", false)
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3JitterChance = new NumberSetting(
         "SC3: Шанс Jitter",
         18.0F,
         0.0F,
         100.0F,
         1.0F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3Jitter.isState();
         }
      );
   public final NumberSetting sc3JitterStrength = new NumberSetting(
         "SC3: Сила Jitter",
         0.35F,
         0.0F,
         2.0F,
         0.05F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3Jitter.isState();
         }
      );
   public final BooleanSetting sc3JitterOnlyOnFinish = new BooleanSetting(
         "SC3: Jitter только на доводке",
         true
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3Jitter.isState();
         }
      );
   public final BooleanSetting sc3JitterOnlyYaw = new BooleanSetting(
         "SC3: Jitter только Yaw",
         true
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3Jitter.isState();
         }
      );
   public final BooleanSetting sc3SmoothReturn = new BooleanSetting(
         "SC3: Плавная отводка",
         true
      )
      .visible(
         () -> this.rotationType.is("SC3")
      );
   public final NumberSetting sc3ReturnSpeedYaw = new NumberSetting(
         "SC3: Скорость отводки Yaw",
         25.0F,
         5.0F,
         180.0F,
         1.0F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3SmoothReturn.isState();
         }
      );
   public final NumberSetting sc3ReturnSpeedPitch = new NumberSetting(
         "SC3: Скорость отводки Pitch",
         20.0F,
         5.0F,
         180.0F,
         1.0F
      )
      .visible(
         () -> {
            ModeSetting var10000 = this.rotationType;
            return var10000.is("SC3") && this.sc3SmoothReturn.isState();
         }
      );
   private LivingEntity target;
   private boolean needSprintReset = false;
   private boolean sprintResetDone = false;
   private int sprintResetTicks = 0;
   private int wtapticks = 0;
   public Vec2f currentRotations = new Vec2f(0.0F, 0.0F);
   public Vec2f targetRotations = new Vec2f(0.0F, 0.0F);
   private final FinishedHelper attackTimer = new FinishedHelper();
   private final ResetHelper_12 wellMineRotation = new ResetHelper_12(this);
   private final ResetHelper_1 testRotation = new ResetHelper_1();
   private final ResetHelper_3 sc3Rotation = new ResetHelper_3();
   private final ResetHelper_9 shardACRotation = new ResetHelper_9();
   private final GrimRotation shardAIRotation = new GrimRotation(this);
   private final ResetHelper_2 oldShardRotation = new ResetHelper_2(this);
   private final ResetHelper_7 shardACTestRotation = new ResetHelper_7();
   private final ResetHelper_15 shard2Rotation = new ResetHelper_15();
   private final ResetHelper_4 aresMineRotation = new ResetHelper_4();
   private final ResetHelper_10 aresMineNewRotation = new ResetHelper_10();
   private final ResetHelper_11 whiteRiseRotation = new ResetHelper_11(this);
   private final ResetHelper_5 shardRotation = new ResetHelper_5(this);
   private final ResetHelper_8 funTimeRotation = new ResetHelper_8(this);
   private final UpdaterotationsHelper_1 lonyGriefRotation = new UpdaterotationsHelper_1(this);
   private final MatrixRotation holyWorldRotation = new MatrixRotation(this);
   private final ResetHelper_13 focusedRotation = new ResetHelper_13(this);
   private final FinishedHelper backTimer = new FinishedHelper();
   private TpsSync tpsSync;
   private long cps = 0L;
   private int ticksToAttack = 0;
   public int attackBlockTicks = 0;
   private int swapGrace = 0;
   private LivingEntity lastDataTarget = null;
   private float lastYaw = 0.0F;
   private float lastPitch = 0.0F;
   private float[] droidLerpRotation = new float[]{0.0F, 0.0F};
   private boolean spookyIsLookingAway = false;
   private long spookyLastLookAwayTime = 0L;
   private long spookyLookAwayStartTime = 0L;
   private float spookyLookAwayTargetYaw = 0.0F;
   private float spookyLookAwayTargetPitch = 0.0F;
   private long spookyLastPitchUpdateTime = 0L;
   public static float adjYaw;
   public static float adjPitch;
   public static float otvodkaPitch;
   public boolean isRotated;
   private final BooleanSetting ignoreNaked = new BooleanSetting(
      "Игнорировать голых",
      false
   );
   public static long lastPhysicalMoveTime;
   private float speedAcceleration;
   private float lastAssistYaw;
   private float lastAssistPitch;
   private int hitcnt = 0;
   private long lasthit = 0L;
   private int ph1lim = 4;
   private int ph2lim = 9;
   private final Random rnd = new Random();

   public Aura() {
      super(
         "AttackAura",
         "Автоматически наводиться и бьёт таргета",
         Category.COMBAT
      );
      this.addSettings(
         new Setting[]{
            this.rotationType,
            this.assistRadius,
            this.assistSpeedAimed,
            this.assistSpeedGround,
            this.assistSpeedAir,
            this.assistSpeedElytra,
            this.clientLookAssist,
            this.targets,
            this.range,
            this.aimRange,
            this.elytraAimRange,
            this.smartCrit,
            this.enhancedCrit,
            this.critCooldownMode,
            this.customCritCooldown,
            this.customMinFall,
            this.customHitDelay,
            this.customRandomize,
            this.maceBypass,
            this.sprintMode,
            this.attackOnEating,
            this.raycast,
            this.expandHitbox,
            this.expandHitboxValue,
            this.unpressShield,
            this.breakShield,
            clientLook,
            this.moveFix,
            this.priority,
            this.sc3SpeedMode,
            this.sc3TimingLead,
            this.sc3ConstantSpeedYaw,
            this.sc3ConstantSpeedPitch,
            this.sc3FreezeOnHitbox,
            this.sc3AccelIncrement,
            this.sc3AccelDecrementBack,
            this.sc3AccelDecrementBackBigAngle,
            this.sc3MaxAcceleration,
            this.sc3MinAcceleration,
            this.sc3BigAngleThreshold,
            this.sc3RaytraceDistance,
            this.sc3BoxExpand,
            this.sc3PointMode,
            this.sc3Multipoint,
            this.sc3MultipointMode,
            this.sc3PointLock,
            this.sc3PointLockTicks,
            this.sc3SmoothMultiplier,
            this.sc3SmoothMultiplierPitch,
            this.sc3MaxPitchDownPercent,
            this.sc3MaxPitchUpPercent,
            this.sc3HitboxSlowdownStrength,
            this.sc3HitboxSpeedReturn,
            this.sc3AntiTurn,
            this.sc3AntiTurnThreshold,
            this.sc3AntiTurnStrength,
            this.sc3GcdFix,
            this.sc3Overshoot,
            this.sc3OvershootStrength,
            this.sc3ReactionDelay,
            this.sc3ReactionTimeMs,
            this.sc3Bezier,
            this.sc3ShakeYaw,
            this.sc3ShakeSpeedYaw,
            this.sc3ShakeAmplitudeYaw,
            this.sc3ShakeDampingYaw,
            this.sc3ShakeBounceYaw,
            this.sc3ShakeRandomYaw,
            this.sc3ShakePitch,
            this.sc3ShakeSpeedPitch,
            this.sc3ShakeAmplitudePitch,
            this.sc3ShakeDampingPitch,
            this.sc3ShakeBouncePitch,
            this.sc3ShakeRandomPitch,
            this.sc3Jitter,
            this.sc3JitterChance,
            this.sc3JitterStrength,
            this.sc3JitterOnlyOnFinish,
            this.sc3JitterOnlyYaw,
            this.sc3SmoothReturn,
            this.sc3ReturnSpeedYaw,
            this.sc3ReturnSpeedPitch
         }
      );
   }

   @EventLink
   public void onLook(
      EventLook val
   ) {
      if (Math.abs(
               val.getYaw()
            )
            > 0.01
         || Math.abs(
               val.getPitch()
            )
            > 0.01) {
         lastPhysicalMoveTime = System.currentTimeMillis();
      }
   }

   @EventLink
   public void onPlayerTick(
      EventUpdate val6
   ) {
      if (mc.player != null && mc.world != null) {
         AuraTransition.INSTANCE.setMode(false, false);
         if (this.tpsSync == null && AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.moduleStorage != null) {
            this.tpsSync = ModuleManager.tpsSync;
         }

         this.lastYaw++;
         this.updateTarget();
      }
   }

   @EventLink
   public void onAttackEntity(
      EventAttackEntity val
   ) {
      if (mc.player != null && mc.world != null) {
         if (val.getPlayer()
            == mc.player) {
            if (val.getTarget() instanceof LivingEntity val2
               )
             {
               if (this.isValidTarget(
                  val2
               )) {
                  this.target = val2;
               }
            }
         }
      }
   }

   @EventLink
   public void onMoveInput(
      EventMoveInput val
   ) {
      if (this.shouldUseLegitSprintReset() && this.needSprintReset) {
         val.setForward(
            0.0F
         );
         val.setStrafe(
            0.0F
         );
         this.needSprintReset = false;
         this.sprintResetDone = true;
         this.sprintResetTicks = 0;
      }

      if (this.wtapticks > 0) {
         val.setForward(
            0.0F
         );
         val.setStrafe(
            0.0F
         );
      }

      this.applyMoveFix(
         val
      );
   }

   private void applyMoveFix(
      EventMoveInput val
   ) {
      if (mc.player != null && this.target != null) {
         ModeSetting var10000 = this.rotationType;
         if (!var10000.is("NoRotate")) {
            var10000 = this.moveFix;
            if (var10000.is("Таргетированная")) {
               if (mc.player.isGliding()) {
                  val.setForward(
                     0.0F
                  );
                  val.setStrafe(
                     0.0F
                  );
               } else if (val.getForward()
                     != 0.0F
                  || val.getStrafe()
                     != 0.0F) {
                  if (val.getForward()
                        > 0.0F
                     && val.getStrafe()
                        == 0.0F) {
                     this.applyTargetedMoveCorrection(
                        val,
                        this.target
                     );
                  } else {
                     float yaw = this.getCorrectionYaw();
                     this.applyMovementCorrection(
                        val,
                        yaw,
                        mc.player.getYaw()
                     );
                  }
               }
            } else {
               var10000 = this.moveFix;
               if (var10000.is("Сфокусированная")) {
                  float yaw2 = this.getCorrectionYaw();
                  this.applyMovementCorrection(
                     val,
                     yaw2,
                     mc.player.getYaw()
                  );
               } else {
                  var10000 = this.moveFix;
                  if (var10000.is("Свободная")) {
                     HasplayermovementHelper.fixMovementFree(
                        val
                     );
                  }
               }
            }

            return;
         }
      }
   }

   @EventLink
   public void onKeyboardInput(
      EventKeyboardInput val
   ) {
      if (mc.player != null && mc.world != null && this.target != null) {
         ModeSetting var10000 = this.rotationType;
         if (!var10000.is("NoRotate")) {
            float yaw = this.getCorrectionYaw();
            var10000 = this.moveFix;
            if (var10000.is("Сфокусированная")) {
               val.setYaw(
                  yaw,
                  mc.player.getYaw()
               );
            }

            return;
         }
      }
   }

   private float getCorrectionYaw() {
      return RotationStorage.instance != null && RotationStorage.instance.targetRotation() != null
         ? RotationStorage.instance.targetRotation().getYaw()
         : mc.player.getYaw();
   }

   private float getTargetDirectionYaw() {
      return RayTraceUtil.getRotations(this.target.getBoundingBox().getCenter()).x;
   }

   private void applyMovementCorrection(
      EventMoveInput val,
      float val2,
      float val3
   ) {
      float f = val.getForward();
      float f2 = val.getStrafe();
      if (f
            != 0.0F
         || f2
            != 0.0F) {
         double d = MathHelper.wrapDegrees(
            Math.toDegrees(
               HasplayermovementHelper.direction(
                  val3,
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
               double d2;
               if ((
                     f5
                           != 0.0F
                        || f6
                           != 0.0F
                  )
                  && (
                        d2 = Math.abs(
                           d
                              - MathHelper.wrapDegrees(
                                 Math.toDegrees(
                                    HasplayermovementHelper.direction(
                                       val2,
                                       f5,
                                       f6
                                    )
                                 )
                              )
                        )
                     )
                     < (double)x2
                  )
                {
                  x2 = (float)d2;
                  f3 = f5;
                  f4 = f6;
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

   private void applyTargetedMoveCorrection(
      EventMoveInput val,
      LivingEntity val2
   ) {
      if (mc.player != null
         && val2
            != null) {
         Vec3d pos = val2.getPos()
            .add(
               0.0,
               (double)val2.getHeight()
                  * 0.5,
               0.0
            );
         double d = pos.x
            - mc.player.getX();
         double d2;
         if (!(
            d
                     * d
                  + (
                        d2 = pos.z
                           - mc.player.getZ()
                     )
                     * d2
               < 1.0E-6
         )) {
            float f = (float)MathHelper.wrapDegrees(
               Math.toDegrees(
                     Math.atan2(
                        d2,
                        d
                     )
                  )
                  - 90.0
            );
            float f2 = MathHelper.wrapDegrees(
               mc.player.getYaw()
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
                  float f7;
                  if ((
                        f5
                              != 0.0F
                           || f6
                              != 0.0F
                     )
                     && (
                           f7 = Math.abs(
                              MathHelper.wrapDegrees(
                                 f
                                    - (float)MathHelper.wrapDegrees(
                                       Math.toDegrees(
                                          this.getDirectionAngle(
                                             f2,
                                             f5,
                                             f6
                                          )
                                       )
                                    )
                              )
                           )
                        )
                        < x2
                     )
                   {
                     x2 = f7;
                     f3 = f5;
                     f4 = f6;
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

   private double getDirectionAngle(
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
      } else if (val3
         < 0.0F) {
         val += 90.0F
            * f;
      }

      return Math.toRadians(
         (double)val
      );
   }

   @EventLink
   private void onGameUpdate(
      EventGameUpdate val6
   ) {
      if (mc.player != null && mc.world != null && this.target != null) {
         ModeSetting var10000 = this.rotationType;
         if (!var10000.is("Focused")) {
            this.rotate();
         }
      }
   }

   @EventLink
   public void onTick(
      EventUpdate val6
   ) {
      if (this.maceTicks > 0) {
         this.maceTicks--;
      }

      if (this.swapGrace > 0) {
         this.swapGrace--;
      }

      if (this.attackBlockTicks > 0) {
         this.attackBlockTicks--;
      }

      if (mc.player != null && mc.world != null) {
         ModeSetting var10000 = this.rotationType;
         if (var10000.is("Grim/Test") && this.target != null) {
            this.rotate();
         }

         if (this.ticksToAttack > 0) {
            this.ticksToAttack--;
         }

         boolean flag = ModuleManager.packetCriticals
               .isEnable()
            && mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING);
         if (!flag
            )
          {
            this.processAttack();
         }
      }
   }

   @EventLink
   public void onPost(
      EventUpdatePost val6
   ) {
      if (mc.player != null && mc.world != null) {
         boolean flag = ModuleManager.packetCriticals
               .isEnable()
            && mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING);
         if (flag
            && mc.player.fallDistance > 0.0F
            && mc.player.fallDistance < 1.0F) {
            this.processAttack();
         }
      }
   }

   private void processAttack() {
      if (this.sprintResetDone) {
         this.sprintResetTicks++;
      }

      if (this.wtapticks > 0) {
         this.wtapticks--;
         if (this.wtapticks == 0 && mc.player != null) {
            mc.player.setSprinting(true);
            if (mc.player.networkHandler != null) {
               mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_SPRINTING));
            }
         }
      }

      this.updateTarget();
      if (this.target != null) {
         if (this.shouldAttack() && this.cps <= System.currentTimeMillis()) {
            if (this.attackOnEating.isState() && (this.shouldBlockAttackWhileUsingItem() || AutoEat.shouldSuppressCombat())) {
               return;
            }

            if (this.shouldUseLegitSprintReset() && mc.player.isSprinting() && !this.sprintResetDone && !this.shouldSkipSprintResetInWater()) {
               this.needSprintReset = true;
               return;
            }

            if (this.shouldUseLegitSprintReset() && this.sprintResetDone && this.sprintResetTicks < 1) {
               return;
            }

            this.attack();
            if (this.shouldUseLegitSprintReset()) {
               this.sprintResetDone = false;
               this.sprintResetTicks = 0;
            }

            if (this.shouldUseWTapSprintReset()) {
               this.wtapticks = 2;
               if (mc.player != null) {
                  mc.player.setSprinting(false);
                  mc.options.sprintKey.setPressed(false);
                  if (mc.player.networkHandler != null) {
                     mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
                  }
               }

               Sprint.pushPause((int)DirectionHelper.random(60.0F, 100.0F));
            }
         }
      } else {
         this.needSprintReset = false;
         this.sprintResetDone = false;
         this.sprintResetTicks = 0;
         this.wtapticks = 0;
         this.cps = System.currentTimeMillis();
         this.backTimer.reset();
         adjPitch = 0.0F;
         adjYaw = 0.0F;
         this.wellMineRotation.reset();
         this.testRotation.reset();
         this.sc3Rotation.reset();
         this.shardACRotation.reset();
         this.shardAIRotation.reset();
         ModeSetting var10000 = this.rotationType;
         if (!var10000.is("OldShard")) {
            this.oldShardRotation.reset();
         }

         this.shardRotation.reset();
         this.shardACTestRotation.reset();
         this.shard2Rotation.reset();
         this.aresMineRotation.reset();
         this.aresMineNewRotation.reset();
         this.whiteRiseRotation.reset();
         this.focusedRotation.reset();
         this.funTimeRotation.reset();
         this.holyWorldRotation.reset();
         this.lastDataTarget = null;
         this.ticksToAttack = 0;
         this.swapGrace = 0;
      }
   }

   public void Rotate() {
      this.rotate();
   }

   @EventLink
   public void onChangeSprint(
      EventChangeSprint val6
   ) {
   }

   public void onWeaponSwapped() {
      this.swapGrace = 4;
   }

   private void rotate() {
      if (ModuleManager.elytraresolver != null && ModuleManager.elytraresolver.isEscaping()) {
         Vec3d vec = ModuleManager.elytraresolver
            .getEscapePos();
         double d = vec.x
            - mc.player.getX();
         double d2 = vec.y
            - (mc.player.getY() + (double)mc.player.getEyeHeight(mc.player.getPose()));
         double d3 = vec.z
            - mc.player.getZ();
         double d4 = Math.sqrt(
            d
                  * d
               + d3
                  * d3
         );
         float f = (float)(
            Math.toDegrees(
                  Math.atan2(
                     d3,
                     d
                  )
               )
               - 90.0
         );
         float f2 = (float)(
            -Math.toDegrees(
               Math.atan2(
                  d2,
                  d4
               )
            )
         );
         float f3 = ((Double)mc.options
                     .getMouseSensitivity()
                     .getValue())
                  .floatValue()
               * 0.6F
            + 0.2F;
         f3 = f3
            * f3
            * f3
            * 1.2F;
         f -= f
            % f3;
         f2 -= f2
            % f3;
         RotationStorage.update(
            new Angle(
               f,
               f2
            ),
            180.0F,
            180.0F,
            25.0F,
            20.0F,
            1,
            1,
            clientLook.isState()
         );
      } else if (this.target != null) {
         ModeSetting var10000 = this.rotationType;
         AuraRotation val;
         if (var10000.is("Matrix/Fast")) {
            val = new AuraRotation(
               
            ) {
               @Override
               public void updateRotations(
                  LivingEntity val2
               ) {
                  if (!Aura.mc.player.isGliding()) {
                     Vec3d pos = val2.getPos()
                        .add(
                           0.0,
                           (double)(
                              val2.getHeight()
                                 * 0.6F
                           ),
                           0.0
                        )
                        .subtract(Aura.mc.player.getEyePos());
                     float f4 = (float)MathHelper.wrapDegrees(
                        Math.toDegrees(
                              Math.atan2(
                                 pos.z,
                                 pos.x
                              )
                           )
                           - 90.0
                     );
                     float f5 = (float)(
                        -Math.toDegrees(
                           Math.atan2(
                              pos.y,
                              Math.hypot(
                                 pos.x,
                                 pos.z
                              )
                           )
                        )
                     );
                     RotationStorage.update(
                        new Angle(
                           f4,
                           f5
                        ),
                        360.0F,
                        360.0F,
                        360.0F,
                        360.0F,
                        1,
                        1,
                        Aura.clientLook.isState()
                     );
                  } else {
                     Vec3d vec2 = Vec3d.fromPolar(
                        val2.getLerpTargetPitch(),
                        val2.getLerpTargetYaw()
                     );
                     Vec3d vec3 = val2.getRotationVector();
                     Vec3d pos2 = val2.getPos()
                        .add(
                           0.0,
                           (double)(
                              val2.getHeight()
                                 * 0.6F
                           ),
                           0.0
                        )
                        .subtract(Aura.mc.player.getEyePos());
                     Vec3d vec4 = vec2.normalize()
                        .lerp(
                           vec3,
                           vec2.length()
                        );
                     if (Aura.mc.player.isGliding()
                        && val2.isGliding()
                        && ModuleManager.elytraTarget.isEnable()) {
                        pos2 = pos2.add(
                           vec4.normalize()
                              .multiply((double)ModuleManager.elytraTarget.predictValue.getValue().floatValue())
                        );
                     }

                     float f6 = (float)MathHelper.wrapDegrees(
                        Math.toDegrees(
                              Math.atan2(
                                 pos2.z,
                                 pos2.x
                              )
                           )
                           - 90.0
                     );
                     float f7 = (float)(
                        -Math.toDegrees(
                           Math.atan2(
                              pos2.y,
                              Math.hypot(
                                 pos2.x,
                                 pos2.z
                              )
                           )
                        )
                     );
                     RotationStorage.update(
                        new Angle(
                           f6,
                           f7
                        ),
                        360.0F,
                        360.0F,
                        360.0F,
                        360.0F,
                        1,
                        1,
                        Aura.clientLook.isState()
                     );
                  }
               }
            };
         } else {
            var10000 = this.rotationType;
            if (var10000.is("Matrix/Vulcan")) {
               val = this.wellMineRotation;
            } else {
               var10000 = this.rotationType;
               if (var10000.is("SC3")) {
                  val = this.sc3Rotation;
               } else {
                  var10000 = this.rotationType;
                  if (var10000.is("ShardAC")) {
                     val = this.shardACRotation;
                  } else {
                     var10000 = this.rotationType;
                     if (var10000.is("ShardAC-AI")) {
                        val = this.shardAIRotation;
                     } else {
                        var10000 = this.rotationType;
                        if (var10000.is("OldShard")) {
                           val = this.oldShardRotation;
                        } else {
                           var10000 = this.rotationType;
                           if (var10000.is("Shard")) {
                              val = this.shardRotation;
                           } else {
                              var10000 = this.rotationType;
                              if (var10000.is("ShardAC/Test")) {
                                 val = this.shardACTestRotation;
                              } else {
                                 var10000 = this.rotationType;
                                 if (var10000.is("AresMine")) {
                                    val = this.aresMineRotation;
                                 } else {
                                    var10000 = this.rotationType;
                                    if (var10000.is("AresMineNEW")) {
                                       val = this.aresMineNewRotation;
                                    } else {
                                       var10000 = this.rotationType;
                                       if (var10000.is("AresMine/Test")) {
                                          val = this.funTimeRotation;
                                       } else {
                                          var10000 = this.rotationType;
                                          if (var10000.is("PolarAC/Old")) {
                                             val = this.lonyGriefRotation;
                                          } else {
                                             var10000 = this.rotationType;
                                             if (var10000.is("HolyWorld")) {
                                                val = this.holyWorldRotation;
                                             } else {
                                                var10000 = this.rotationType;
                                                if (var10000.is("Grim/Test")) {
                                                   val = this.focusedRotation;
                                                } else {
                                                   var10000 = this.rotationType;
                                                   if (var10000.is("Shard 2")) {
                                                      val = this.shard2Rotation;
                                                   } else {
                                                      var10000 = this.rotationType;
                                                      if (var10000.is("WhiteRise")) {
                                                         val = this.whiteRiseRotation;
                                                      } else {
                                                         var10000 = this.rotationType;
                                                         if (var10000.is("NoRotate")) {
                                                            val = new AuraRotation(
                                                               
                                                            ) {
                                                               @Override
                                                               public void updateRotations(
                                                                  LivingEntity val3
                                                               ) {
                                                                  if (Aura.mc.player.isGliding()
                                                                     && val3.isGliding()
                                                                     )
                                                                   {
                                                                     Vec3d vec5;
                                                                     if (ModuleManager.elytraTarget != null && ModuleManager.elytraTarget.isEnable()) {
                                                                        Vec3d vec6 = val3.getBoundingBox()
                                                                           .getCenter();
                                                                        Vec3d vec7 = new Vec3d(
                                                                           val3.getX()
                                                                              - val3.prevX,
                                                                           val3.getY()
                                                                              - val3.prevY,
                                                                           val3.getZ()
                                                                              - val3.prevZ
                                                                        );
                                                                        int n = ModuleManager.elytraTarget
                                                                           .predictValue
                                                                           .getValue()
                                                                           .intValue();
                                                                        Vec3d vec8 = Vec3d.fromPolar(
                                                                           val3.getLerpTargetPitch(),
                                                                           val3.getLerpTargetYaw()
                                                                        );
                                                                        Vec3d vec9 = val3.getRotationVector();
                                                                        Vec3d vec10 = vec8.normalize()
                                                                           .lerp(
                                                                              vec9,
                                                                              0.5
                                                                           );
                                                                        Vec3d vec11 = vec10.normalize()
                                                                           .multiply(
                                                                              (double)n
                                                                                 * 0.6
                                                                           )
                                                                           .add(
                                                                              vec7.multiply(
                                                                                 (double)n
                                                                              )
                                                                           );
                                                                        vec5 = vec6.add(
                                                                           vec11
                                                                        );
                                                                        Vec3d vec12 = Aura.mc
                                                                           .player
                                                                           .getEyePos();
                                                                        double dist = vec12.distanceTo(
                                                                           vec5
                                                                        );
                                                                        double d5 = Math.min(
                                                                           1.0,
                                                                           dist
                                                                              / 10.0
                                                                        );
                                                                        vec5 = vec6.lerp(
                                                                           vec5,
                                                                           d5
                                                                        );
                                                                        vec5 = vec5.add(
                                                                           0.0,
                                                                           (double)(
                                                                              val3.getHeight()
                                                                                 * 0.3F
                                                                           ),
                                                                           0.0
                                                                        );
                                                                     } else {
                                                                        vec5 = val3.getPos()
                                                                           .add(
                                                                              0.0,
                                                                              (double)(
                                                                                 val3.getHeight()
                                                                                    * 0.6F
                                                                              ),
                                                                              0.0
                                                                           );
                                                                     }

                                                                     Vec3d vec13 = vec5.subtract(
                                                                        Aura.mc.player.getEyePos()
                                                                     );
                                                                     float f8 = (float)MathHelper.wrapDegrees(
                                                                        Math.toDegrees(
                                                                              Math.atan2(
                                                                                 vec13.z,
                                                                                 vec13.x
                                                                              )
                                                                           )
                                                                           - 90.0
                                                                     );
                                                                     float f9 = (float)(
                                                                        -Math.toDegrees(
                                                                           Math.atan2(
                                                                              vec13.y,
                                                                              Math.hypot(
                                                                                 vec13.x,
                                                                                 vec13.z
                                                                              )
                                                                           )
                                                                        )
                                                                     );
                                                                     RotationStorage.update(
                                                                        new Angle(
                                                                           f8,
                                                                           f9
                                                                        ),
                                                                        180.0F,
                                                                        180.0F,
                                                                        120.0F,
                                                                        120.0F,
                                                                        1,
                                                                        1,
                                                                        Aura.clientLook.isState()
                                                                     );
                                                                  } else {
                                                                     RotationStorage.update(
                                                                        new Angle(FreeLookStorage.getFreeYaw(), FreeLookStorage.getFreePitch()),
                                                                        DirectionHelper.random(100.0F, 170.0F),
                                                                        DirectionHelper.random(100.0F, 170.0F),
                                                                        DirectionHelper.random(100.0F, 170.0F),
                                                                        DirectionHelper.random(100.0F, 170.0F),
                                                                        1,
                                                                        6,
                                                                        false
                                                                     );
                                                                  }
                                                               }
                                                            };
                                                         } else {
                                                            var10000 = this.rotationType;
                                                            if (var10000.is("Assist")) {
                                                               val = new AuraRotation(
                                                                  
                                                               ) {
                                                                  @Override
                                                                  public void updateRotations(
                                                                     LivingEntity val4
                                                                  ) {
                                                                     Aura.this.updateAssistRotation(
                                                                        val4
                                                                     );
                                                                  }
                                                               };
                                                            } else {
                                                               final Vec2f rotations = RayTraceUtil.getRotations(
                                                                  this.getPredictedRotationPoint(this.target, this.target.getLeashPos(1.0F))
                                                               );
                                                               val = new AuraRotation(
                                                                  
                                                               ) {
                                                                  @Override
                                                                  public void updateRotations(
                                                                     LivingEntity val5
                                                                  ) {
                                                                     Aura.this.currentRotations = new Vec2f(
                                                                        Aura.mc.player.getYaw(), Aura.mc.player.getPitch()
                                                                     );
                                                                     RotationStorage.update(
                                                                        new Angle(
                                                                           rotations.x,
                                                                           rotations.y
                                                                        ),
                                                                        360.0F,
                                                                        360.0F,
                                                                        360.0F,
                                                                        360.0F,
                                                                        1,
                                                                        1,
                                                                        Aura.clientLook.isState()
                                                                     );
                                                                  }
                                                               };
                                                            }
                                                         }
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         val.updateRotations(
            this.target
         );
      }
   }

   private boolean shouldSkipSprintResetInWater() {
      return mc.player != null
         && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater())
         && Sprint.INSTANCE != null
         && Sprint.INSTANCE.shouldKeepSprintInWater();
   }

   private boolean isCritAttack() {
      if (mc.player == null) {
         return false;
      } else {
         float f = this.getRequiredFallDistance();
         if (this.smartCrit.isState()) {
            return this.enhancedCrit.isState()
               ? GetaicooldownHelper.enhancedCanCritical(
                  this.target,
                  f
               )
               : GetaicooldownHelper.canCritical(
                  this.target,
                  f
               );
         } else {
            return !mc.player.isOnGround()
               && mc.player.getVelocity().y < 0.0
               && mc.player.fallDistance
                  >= f;
         }
      }
   }

   private boolean shouldUseWTapSprintReset() {
      ModeSetting var10000 = this.sprintMode;
      if (var10000.is("WTap")) {
         return true;
      } else {
         var10000 = this.sprintMode;
         return var10000.is("WellMine") ? !this.isCritAttack() : false;
      }
   }

   private boolean shouldUsePacketSprintReset() {
      ModeSetting var10000 = this.sprintMode;
      if (!var10000.is("Обычный")) {
         var10000 = this.sprintMode;
         if (!var10000.is("Пакетный")) {
            return false;
         }
      }

      return true;
   }

   private boolean shouldUseLegitSprintReset() {
      ModeSetting var10000 = this.sprintMode;
      if (var10000.is("Легитный")) {
         return true;
      } else {
         var10000 = this.sprintMode;
         return var10000.is("WellMine") ? this.isCritAttack() : false;
      }
   }

   private float getCurrentLookYaw() {
      return FreeLookStorage.isActive() ? FreeLookStorage.getFreeYaw() : (mc.player == null ? 0.0F : mc.player.getYaw());
   }

   private float getCurrentLookPitch() {
      return FreeLookStorage.isActive() ? FreeLookStorage.getFreePitch() : (mc.player == null ? 0.0F : mc.player.getPitch());
   }

   private EntityHitResult getAttackRaycastResult() {
      if (mc.player != null && this.target != null) {
         float f;
         float f2;
         label34: {
            ModeSetting var10000 = this.rotationType;
            if (!var10000.is("Assist")) {
               var10000 = this.rotationType;
               if (!var10000.is("NoRotate")) {
                  if (RotationStorage.instance != null && RotationStorage.instance.isRotating() && RotationStorage.instance.targetRotation() != null) {
                     f2 = RotationStorage.instance
                        .targetRotation()
                        .getPitch();
                     f = RotationStorage.instance
                        .targetRotation()
                        .getYaw();
                  } else {
                     f2 = mc.player
                        .getPitch();
                     f = mc.player
                        .getYaw();
                  }
                  break label34;
               }
            }

            f2 = this.getCurrentLookPitch();
            f = this.getCurrentLookYaw();
         }

         Vec3d vec = mc.player
            .getEyePos();
         Vec3d vec2 = mc.player
            .getRotationVector(
               f2,
               f
            );
         double d = (double)this.range
            .getValue()
            .floatValue();
         Vec3d vec3 = vec.add(
            vec2.multiply(
               d
            )
         );
         Box box = this.target
            .getBoundingBox();
         if (this.expandHitbox.isState()) {
            box = box.expand(
               (double)this.expandHitboxValue.getValue().floatValue()
            );
         }

         return !box.contains(
                  vec
               )
               && !box.raycast(
                     vec,
                     vec3
                  )
                  .isPresent()
            ? null
            : new EntityHitResult(this.target);
      } else {
         return null;
      }
   }

   private Vec3d getPredictedRotationPoint(
      LivingEntity val,
      Vec3d val2
   ) {
      return mc.player != null
            && val
               != null
            && mc.player.isGliding()
            && val.isGliding()
            && ModuleManager.elytraTarget != null
            && ModuleManager.elytraTarget.isEnable()
         ? GetsmartpredictedpositionHelper.bypasselytrahacking(
            val
         )
         : val2;
   }

   private Vec3d getDataRotationPoint(
      LivingEntity val
   ) {
      if (this.isInsideOrNearHitbox(
         val
      )) {
         return this.getPredictedRotationPoint(
            val,
            this.getStableBodyPoint(
               val
            )
         );
      } else {
         Vec3d vec = RaycastUtil.getNearestPoint(
            val
         );
         if (vec
            == null) {
            vec = GetclosestpointHelper.getClosestPoint(
               val
            );
         }

         if (vec
            == null) {
            vec = this.getStableBodyPoint(
               val
            );
         }

         return this.getPredictedRotationPoint(
            val,
            vec
         );
      }
   }

   private boolean isInsideOrNearHitbox(
      LivingEntity val
   ) {
      if (mc.player != null
         && val
            != null) {
         Vec3d vec = mc.player
            .getEyePos();
         Box box = val.getBoundingBox();
         if (box.expand(
               0.12
            )
            .contains(
               vec
            )) {
            return true;
         } else {
            Vec3d vec2 = this.getStableBodyPoint(
               val
            );
            return vec.squaredDistanceTo(
                  vec2
               )
               <= 2.25;
         }
      } else {
         return false;
      }
   }

   private Vec3d getStableBodyPoint(
      LivingEntity val
   ) {
      Box box = val.getBoundingBox();
      return new Vec3d(
         box.getCenter()
            .x,
         box.minY
            + box.getLengthY()
               * 0.72,
         box.getCenter()
            .z
      );
   }

   private LivingEntity findTarget() {
      ArrayList<LivingEntity> entity2 = new ArrayList<>();

      for (Object val : mc.world
         .getEntities()) {
         LivingEntity entity3;
         if (val instanceof LivingEntity
            && this.isValidTarget(
               entity3 = (LivingEntity)val
            )) {
            entity2.add(
               entity3
            );
         }
      }

      if (!entity2.isEmpty()
         && this.isEnable()) {
         Comparator<LivingEntity> entity4 = null;
         String var6 = this.priority.getCurrent();
         int var8 = -1;
         switch (var6.hashCode()) {
            case -1397728228:
               if (var6.equals("Здоровье")) {
                  var8 = 1;
               }
               break;
            case 32616493:
               if (var6.equals("Угол")) {
                  var8 = 2;
               }
               break;
            case 1835963915:
               if (var6.equals("Дистанция")) {
                  var8 = 0;
               }
         }

         switch (var8) {
            case 0:
               entity4 = Comparator.comparingDouble(
                  val2 -> val2.getBoundingBox()
                        .getCenter()
                        .squaredDistanceTo(mc.player.getEyePos())
               );
               break;
            case 1:
               entity4 = Comparator.comparingDouble(
                  LivingEntity::getHealth
               );
               break;
            case 2:
               entity4 = Comparator.comparingDouble(
                  val3 -> {
                     Vec2f rotations = RayTraceUtil.getRotations(
                        val3.getBoundingBox()
                           .getCenter()
                     );
                     double d = (double)Math.abs(
                        MathHelper.wrapDegrees(
                           rotations.x
                              - mc.player.getYaw()
                        )
                     );
                     double d2 = (double)Math.abs(
                        MathHelper.wrapDegrees(
                           rotations.y
                              - mc.player.getPitch()
                        )
                     );
                     return d
                        + d2;
                  }
               );
         }

         Comparator<LivingEntity> entity5 = entity4;
         entity2.sort(
            (val4, val5) -> {
               if (mc.player != null && mc.player.isGliding()) {
                  if (val4.isGliding()
                     && !val5.isGliding()
                     )
                   {
                     return -1;
                  }

                  if (!val4.isGliding()
                     && val5.isGliding()
                     )
                   {
                     return 1;
                  }
               }

               return entity5
                     != null
                  ? entity5.compare(
                     val4,
                     val5
                  )
                  : 0;
            }
         );
         return entity2.isEmpty()
            ? null
            : entity2.get(
               0
            );
      } else {
         return null;
      }
   }

   private void updateTarget() {
      if (!this.isEnable()) {
         this.target = null;
         this.resetCombo();
      } else if (this.target == null || !this.isValidTarget(this.target)) {
         LivingEntity entity2 = this.target;
         this.target = this.findTarget();
         if (this.target
            != entity2
            )
          {
            this.resetCombo();
         }
      }
   }

   private boolean shouldFocusDataRotation() {
      float y2 = mc.player
         .getAttackCooldownProgress(1.5F);
      float x2 = Math.max(
         0.82F, GetaicooldownHelper.getAICooldown() - 0.08F
      );
      boolean flag = y2
         >= x2;
      boolean flag2 = !mc.player
            .isOnGround()
         && mc.player.getVelocity().y < 0.0
         && mc.player.fallDistance > 0.0F;
      return flag
         || flag2;
   }

   private void attack() {
        if (this.target == null || mc.player == null || !mc.player.canSee(this.target)) return;
      Hand hand = null;
      boolean flag = false;
      if (this.unpressShield.isState()
         && (
               hand = this.getBlockingShieldHand()
            )
            != null) {
         flag = true;
         mc.interactionManager.stopUsingItem(mc.player);

         try {
            Thread.sleep(5L);
         } catch (InterruptedException var11) {
            Thread.currentThread().interrupt();
         }
      }


      boolean flag2 = false;
      LivingEntity entity2 = this.target;
      PlayerEntity player2;
      if (entity2 instanceof PlayerEntity
         && (player2 = (PlayerEntity)entity2)
            .isBlocking()
         && this.breakShield.isState()
         && this.shieldBreak(
            player2
         )) {
         try {
            Thread.sleep(50L);
         } catch (InterruptedException var10) {
            Thread.currentThread().interrupt();
         }
      }

      boolean flag3 = mc.player
         .isSprinting();
      if (mc.player.getMainHandStack().getItem() == Items.MACE && this.maceBypass.isState()) {
         this.maceTicks = 6;
      }

      if (!MaxDamage.INSTANCE.exec(this.target)) {
         mc.interactionManager.attackEntity(mc.player, this.target);
         mc.player.swingHand(Hand.MAIN_HAND);
      }

      ModeSetting var10000 = this.sprintMode;
      if (var10000.is("Пакетный")
         && flag3
         )
       {
         mc.player.setSprinting(true);
      }

      if (ModuleManager.elytraresolver != null) {
         ModuleManager.elytraresolver.onAuraAttack();
      }

      if (flag
         && hand
            != null) {
         try {
            Thread.sleep(5L);
         } catch (InterruptedException var9) {
            Thread.currentThread().interrupt();
         }

         this.restoreShieldBlocking(
            hand
         );
      }

      var10000 = this.rotationType;
      if (var10000.is("WhiteRise")) {
         this.whiteRiseRotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("SC3")) {
         this.sc3Rotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("ShardAC")) {
         this.shardACRotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("ShardAC-AI")) {
         this.shardAIRotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("OldShard")) {
         this.oldShardRotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("Shard")) {
         this.shardRotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("ShardAC/Test")) {
         this.shardACTestRotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("Shard 2")) {
         this.shard2Rotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("Grim/Test")) {
         this.focusedRotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("AresMine")) {
         this.aresMineRotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("AresMineNEW")) {
         this.aresMineNewRotation.onAttack();
      }

      var10000 = this.rotationType;
      if (var10000.is("AresMine/Test")) {
         this.funTimeRotation.onAttack();
      }

      this.hitcnt++;
      this.lasthit = System.currentTimeMillis();
      var10000 = this.critCooldownMode;
      if (var10000.is("Адаптивный")
         && this.hitcnt
            > this.ph2lim
               + 5
               + this.rnd.nextInt(5)) {
         this.resetCombo();
      }

      long val = 467L
         + this.getExtraHitDelay();
      if (this.tpsSync != null && this.tpsSync.isEnable()) {
         val = (long)(
            (float)this.tpsSync
                  .getAdjustedCooldown(
                     val
                  )
               * 1.1F
         );
      }

      this.cps = System.currentTimeMillis()
         + val;
      this.ticksToAttack = 10;
      this.attackTimer.reset();
   }

   private boolean shieldBreak(
      PlayerEntity val
   ) {
      FindItemResult item2 = GetitemcountHelper.getAxe();
      if (!item2.found()
         )
       {
         return false;
      } else {
         int n = mc.player
            .getInventory()
            .selectedSlot;
         if (item2.slot()
            == n
            )
          {
            try {
               Thread.sleep(8L);
            } catch (InterruptedException var11) {
               Thread.currentThread().interrupt();
            }

            boolean flag = mc.player
               .isSprinting();
            if (mc.player.getMainHandStack().getItem() == Items.MACE && this.maceBypass.isState()) {
               this.maceTicks = 6;
            }

            mc.interactionManager
               .attackEntity(
                  mc.player,
                  val
               );
            ModeSetting var10000 = this.sprintMode;
            if (var10000.is("Пакетный")
               && flag
               )
             {
               mc.player.setSprinting(true);
            }

            if (ModuleManager.elytraresolver != null) {
               ModuleManager.elytraresolver.onAuraAttack();
            }

            return true;
         } else if (item2.isInHotBar()
            )
          {
            return this.attackWithSilentHotbarSlot(
               val,
               item2.slot(),
               n
            );
         } else if (mc.player.currentScreenHandler.syncId != 0) {
            return false;
         } else {
            int slot2 = this.findSilentSwapHotbarSlot(
               n
            );
            if (slot2
               == -1) {
               return false;
            } else {
               this.swapInventoryIntoHotbar(
                  item2.slot(),
                  slot2
               );
               boolean flag2 = false;

               try {
                  try {
                     Thread.sleep(10L);
                  } catch (InterruptedException var12) {
                     Thread.currentThread().interrupt();
                  }

                  flag2 = this.attackWithSilentHotbarSlot(
                     val,
                     slot2,
                     n
                  );
               } finally {
                  this.swapInventoryIntoHotbar(
                     item2.slot(),
                     slot2
                  );
               }

               return flag2;
            }
         }
      }
   }

   private boolean attackWithSilentHotbarSlot(
      PlayerEntity val,
      int val2,
      int val3
   ) {
      if (mc.player != null && mc.player.networkHandler != null && mc.interactionManager != null) {
         mc.player
            .networkHandler
            .sendPacket(
               new UpdateSelectedSlotC2SPacket(
                  val2
               )
            );

         try {
            Thread.sleep(12L);
         } catch (InterruptedException var16) {
            Thread.currentThread().interrupt();
         }

         boolean var7;
         try {
            boolean flag = mc.player
               .isSprinting();
            if (mc.player.getMainHandStack().getItem() == Items.MACE && this.maceBypass.isState()) {
               this.maceTicks = 6;
            }

            mc.interactionManager
               .attackEntity(
                  mc.player,
                  val
               );
            ModeSetting var10000 = this.sprintMode;
            if (var10000.is("Пакетный")
               && flag
               )
             {
               mc.player.setSprinting(true);
            }

            if (ModuleManager.elytraresolver != null) {
               ModuleManager.elytraresolver.onAuraAttack();
            }

            boolean flag2 = true;
            boolean flag3 = true;
            var7 = flag3;
         } finally {
            try {
               Thread.sleep(8L);
            } catch (InterruptedException var15) {
               Thread.currentThread().interrupt();
            }

            mc.player
               .networkHandler
               .sendPacket(
                  new UpdateSelectedSlotC2SPacket(
                     val3
                  )
               );
         }

         return var7;
      } else {
         return false;
      }
   }

   private void swapInventoryIntoHotbar(
      int val,
      int val2
   ) {
      if (mc.player != null && mc.interactionManager != null && mc.player.networkHandler != null) {
         int n = mc.player
            .currentScreenHandler
            .syncId;
         mc.interactionManager
            .clickSlot(
               n,
               val,
               val2,
               SlotActionType.SWAP,
               mc.player
            );
         mc.player
            .networkHandler
            .sendPacket(
               new CloseHandledScreenC2SPacket(
                  n
               )
            );
      }
   }

   private int findSilentSwapHotbarSlot(
      int val
   ) {
      for (int i = 8;
         i
            >= 0;
         i--
      ) {
         if (i
            != val
            )
          {
            return i;
         }
      }

      return val
               >= 0
            && val
               < 9
         ? val
         : -1;
   }

   private Hand getBlockingShieldHand() {
      if (mc.player != null && mc.player.isBlocking()) {
         Hand hand = mc.player
            .getActiveHand();
         if (hand
            == null) {
            return null;
         } else {
            return this.isShieldStack(
                  mc.player
                     .getStackInHand(
                        hand
                     )
               )
               ? hand
               : null;
         }
      } else {
         return null;
      }
   }

   private boolean shouldBlockAttackWhileUsingItem() {
      return mc.player != null && mc.player.isUsingItem() ? !this.unpressShield.isState() || this.getBlockingShieldHand() == null : false;
   }

   private void restoreShieldBlocking(
      Hand val
   ) {
      if (mc.player != null
         && mc.interactionManager != null
         && val
            != null) {
         if (this.isShieldStack(
               mc.player
                  .getStackInHand(
                     val
                  )
            )
            && !mc.player.isUsingItem()) {
            mc.interactionManager
               .interactItem(
                  mc.player,
                  val
               );
         }
      }
   }

   private boolean isShieldStack(
      ItemStack val
   ) {
      return !val.isEmpty()
         && val.getItem() instanceof ShieldItem;
   }

   private boolean isWeapon() {
      Item item2 = mc.player
         .getMainHandStack()
         .getItem();
      return item2
            != Items.AIR
         && (
            item2 instanceof SwordItem
               || item2 instanceof PickaxeItem
               || item2 instanceof AxeItem
               || item2 instanceof HoeItem
               || item2 instanceof ShovelItem
               || item2 instanceof MaceItem
               || item2
                  == Items.MACE
         );
   }

   private boolean isValidTarget(
      LivingEntity val
   ) {
      if (val
            != null
         && val
            != mc.player) {
         if (!val.isAlive()
            || val.getHealth()
               <= 0.0F) {
            return false;
         } else if (val instanceof ArmorStandEntity
            )
          {
            return false;
         } else if (val instanceof IronGolemEntity
            || val instanceof BatEntity
            )
          {
            return false;
         } else if (Antibot.checkBot(
            val
         )) {
            return false;
         } else {
            if (val instanceof PlayerEntity val2
               )
             {
               MultiBooleanSetting var10000 = this.targets;
               if (!var10000.is("Игроки")) {
                  return false;
               }

               if (this.isNaked(
                  val2
               )) {
                  var10000 = this.targets;
                  if (!var10000.is("Голые")) {
                     return false;
                  }
               }

               if (val2.hasStatusEffect(
                  StatusEffects.INVISIBILITY
               )) {
                  var10000 = this.targets;
                  if (!var10000.is("Невидимки")) {
                     return false;
                  }
               }

               if (AspectDLC.INSTANCE
                  .friendStorage
                  .isFriend(
                     val.getName()
                        .getString()
                  )) {
                  return false;
               }
            } else if (!(
                  val instanceof PassiveEntity
               )
               && !(
                  val instanceof CodEntity
               )) {
               if (!(
                  val instanceof HostileEntity
               )) {
                  return false;
               }

               MultiBooleanSetting var7 = this.targets;
               if (!var7.is("Мобы")) {
                  return false;
               }
            } else {
               MultiBooleanSetting var6 = this.targets;
               if (!var6.is("Мирные")) {
                  return false;
               }
            }

            Vec3d vec;
            if ((
                  vec = RaycastUtil.getNearestPoint(
                     val
                  )
               )
               == null) {
               vec = GetclosestpointHelper.getClosestPoint(
                  val
               );
            }

            return mc.player
                     .getEyePos()
                     .distanceTo(
                        vec
                     )
                  > (double)this.getMaxAimRange()
               ? false
               : mc.player
                     .canSee(
                        val
                     );
         }
      } else {
         return false;
      }
   }

   private boolean isNaked(
      PlayerEntity val
   ) {
      for (ItemStack stack : val.getArmorItems()) {
         if (!stack.isEmpty()
            )
          {
            return false;
         }
      }

      return true;
   }

   private void checkComboReset() {
      if (System.currentTimeMillis() - this.lasthit > 1500L) {
         this.resetCombo();
      }
   }

   private void resetCombo() {
      this.hitcnt = 0;
      this.ph1lim = 3
         + this.rnd.nextInt(4);
      this.ph2lim = this.ph1lim
         + 4
         + this.rnd.nextInt(5);
   }

   public float getRequiredCooldownProgress() {
      this.checkComboReset();
      ModeSetting var10000 = this.critCooldownMode;
      if (var10000.is("Быстрый")) {
         return GetaicooldownHelper.getAICooldown();
      } else {
         var10000 = this.critCooldownMode;
         if (var10000.is("Адаптивный")) {
            if (this.hitcnt <= this.ph1lim) {
               return 0.91F + this.rnd.nextFloat() * 0.02F;
            } else {
               return this.hitcnt <= this.ph2lim ? 0.94F + this.rnd.nextFloat() * 0.03F : 0.97F + this.rnd.nextFloat() * 0.03F;
            }
         } else {
            var10000 = this.critCooldownMode;
            if (var10000.is("Рандомный")) {
               return 0.92F + this.rnd.nextFloat() * 0.07F;
            } else {
               var10000 = this.critCooldownMode;
               if (var10000.is("Кастом")) {
                  float f = this.customCritCooldown
                     .getValue()
                     .floatValue();
                  if (this.customRandomize.isState()) {
                     f += (
                           this.rnd.nextFloat() - 0.5F
                        )
                        * 0.04F;
                  }

                  return MathHelper.clamp(
                     f,
                     0.85F,
                     1.0F
                  );
               } else {
                  return GetaicooldownHelper.getAICooldown();
               }
            }
         }
      }
   }

   public float getRequiredFallDistance() {
      this.checkComboReset();
      ModeSetting var10000 = this.critCooldownMode;
      if (var10000.is("Быстрый")) {
         return 0.01F;
      } else {
         var10000 = this.critCooldownMode;
         if (var10000.is("Адаптивный")) {
            if (this.hitcnt <= this.ph1lim) {
               return 0.01F + this.rnd.nextFloat() * 0.03F;
            } else {
               return this.hitcnt <= this.ph2lim ? 0.06F + this.rnd.nextFloat() * 0.06F : 0.14F + this.rnd.nextFloat() * 0.1F;
            }
         } else {
            var10000 = this.critCooldownMode;
            if (var10000.is("Рандомный")) {
               return 0.03F + this.rnd.nextFloat() * 0.18F;
            } else {
               var10000 = this.critCooldownMode;
               if (var10000.is("Кастом")) {
                  float f = this.customMinFall
                     .getValue()
                     .floatValue();
                  if (this.customRandomize.isState()) {
                     f += (
                           this.rnd.nextFloat() - 0.5F
                        )
                        * 0.04F;
                  }

                  return Math.max(
                     f,
                     0.0F
                  );
               } else {
                  return 0.01F;
               }
            }
         }
      }
   }

   public long getExtraHitDelay() {
      this.checkComboReset();
      ModeSetting var10000 = this.critCooldownMode;
      if (var10000.is("Быстрый")) {
         return 0L;
      } else {
         var10000 = this.critCooldownMode;
         if (var10000.is("Адаптивный")) {
            if (this.hitcnt <= this.ph1lim) {
               return (long)this.rnd.nextInt(10);
            } else {
               return this.hitcnt <= this.ph2lim
                  ? 10L + (long)this.rnd.nextInt(15)
                  : 20L + (long)this.rnd.nextInt(25);
            }
         } else {
            var10000 = this.critCooldownMode;
            if (var10000.is("Рандомный")) {
               return (long)this.rnd.nextInt(35);
            } else {
               var10000 = this.critCooldownMode;
               if (var10000.is("Кастом")) {
                  long val = (long)this.customHitDelay
                     .getValue()
                     .floatValue();
                  if (this.customRandomize.isState()) {
                     val += (long)this.rnd
                        .nextInt(15);
                  }

                  return Math.max(
                     val,
                     0L
                  );
               } else {
                  return 0L;
               }
            }
         }
      }
   }

   private boolean shouldAttack() {
      if (this.attackBlockTicks > 0) {
         return false;
      } else {
         boolean flag = this.maceBypass
               .isState()
            && mc.player.getMainHandStack().getItem() == Items.MACE;
         float f = this.getRequiredCooldownProgress();
         if (!(
               mc.player.getAttackCooldownProgress(1.5F)
                  < f
            )
            || flag
               && this.maceTicks <= 0) {
            if (flag
               && this.maceTicks > 0) {
               return false;
            } else {
               EntityHitResult entityHit;
               if (!this.raycast.isState()
                  || mc.player.isGliding() && this.target.isGliding()
                  || (
                           entityHit = this.getAttackRaycastResult()
                        )
                        != null
                     && entityHit.getEntity()
                        == this.target) {
                  if (mc.player.isGliding() && this.target.isGliding()) {
                     Vec3d vec = this.target
                        .getPos()
                        .add(0.0, (double)this.target.getHeight() / 2.0, 0.0);
                     int n = 0;
                     if (ModuleManager.elytraTarget != null && ModuleManager.elytraTarget.isEnable()) {
                        n = ModuleManager.elytraTarget
                           .predictValue
                           .getValue()
                           .intValue();
                     }

                     Vec3d pos = GetsmartpredictedpositionHelper.predict(
                        this.target,
                        vec,
                        n
                     );
                     if (mc.player
                           .getEyePos()
                           .distanceTo(
                              pos
                           )
                        > 5.0) {
                        return false;
                     }
                  } else {
                     double d = mc.player
                        .getEyePos()
                        .distanceTo(this.target.getBoundingBox().getCenter());
                     Vec3d vec2 = d
                           > 3.0
                        ? RaycastUtil.getNearestPoint(this.target)
                        : this.target.getBoundingBox().getCenter();
                     if (vec2
                        == null) {
                        vec2 = GetclosestpointHelper.getClosestPoint(
                           this.target
                        );
                     }

                     if (mc.player
                           .getEyePos()
                           .distanceTo(
                              vec2
                           )
                        > (double)this.range.getValue().floatValue()) {
                        return false;
                     }
                  }

                  float f2 = this.getRequiredFallDistance();
                  if (!this.enhancedCrit.isState()) {
                     return GetaicooldownHelper.canCritical(
                        this.target,
                        f2
                     );
                  } else {
                     if (this.swapGrace > 0) {
                        boolean flag2 = !mc.player
                              .isOnGround()
                           && mc.player.getVelocity().y < 0.0
                           && mc.player.fallDistance
                              >= f2;
                        if (!flag2
                           )
                         {
                           return false;
                        }
                     }

                     return GetaicooldownHelper.enhancedCanCritical(
                        this.target,
                        f2
                     );
                  }
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      }
   }

   public boolean isReadyToAttack() {
      return mc.player == null ? false : mc.player.getAttackCooldownProgress(1.5F) >= this.getRequiredCooldownProgress();
   }

   public int getWhiteRiseTicksToAttack() {
      return this.ticksToAttack;
   }

   public boolean isAboveWater() {
      BlockPos pos = BlockPos.ofFloored(
         mc.player.getPos().add(0.0, -0.4, 0.0)
      );
      return !mc.player.isSubmergedInWater()
         && mc.world
            .getBlockState(
               pos
            )
            .isOf(Blocks.WATER);
   }

   public float getAttackCooldown() {
      return MathHelper.clamp((float)((ILivingEntity)mc.player).getLastAttackedTicks() / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
   }

   public float getAttackCooldownProgressPerTick() {
      return (float)(1.0 / mc.player.getAttributeValue(EntityAttributes.ATTACK_SPEED) * 20.0);
   }

   private float getMaxAimRange() {
      return mc.player.isGliding()
         ? this.elytraAimRange.getValue().floatValue()
         : this.range.getValue().floatValue() + this.aimRange.getValue().floatValue();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.target != null) {
         this.backTimer.reset();
      }

      this.target = null;
      this.wellMineRotation.reset();
      this.testRotation.reset();
      this.sc3Rotation.reset();
      this.shardACRotation.reset();
      this.shardAIRotation.reset();
      ModeSetting var10000 = this.rotationType;
      if (var10000.is("OldShard") && this.oldShardRotation.isActive()) {
         this.oldShardRotation.returnToCamera();
      } else {
         this.oldShardRotation.reset();
      }

      this.shardRotation.reset();
      this.shardACTestRotation.reset();
      this.shard2Rotation.reset();
      this.aresMineRotation.reset();
      this.aresMineNewRotation.reset();
      this.whiteRiseRotation.reset();
      this.focusedRotation.reset();
      this.funTimeRotation.reset();
      this.holyWorldRotation.reset();
      this.lastDataTarget = null;
      this.ticksToAttack = 0;
      this.resetCombo();
      this.needSprintReset = false;
      this.sprintResetDone = false;
      this.sprintResetTicks = 0;
      this.wtapticks = 0;
      this.spookyIsLookingAway = false;
      this.spookyLastLookAwayTime = 0L;
      this.spookyLastPitchUpdateTime = 0L;
      float[] var1 = new float[2];
      var1[0] = 0.0F;
      var1[1] = 0.0F;
      this.droidLerpRotation = var1;
      AuraTransition.INSTANCE.setMode(false, false);
      AuraTransition.INSTANCE.reset();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.wellMineRotation.reset();
      this.testRotation.reset();
      this.sc3Rotation.reset();
      this.shardACRotation.reset();
      this.shardAIRotation.reset();
      this.oldShardRotation.reset();
      this.shardRotation.reset();
      this.shardACTestRotation.reset();
      this.shard2Rotation.reset();
      this.aresMineRotation.reset();
      this.aresMineNewRotation.reset();
      this.whiteRiseRotation.reset();
      this.focusedRotation.reset();
      this.funTimeRotation.reset();
      this.holyWorldRotation.reset();
      this.lastDataTarget = null;
      this.ticksToAttack = 0;
      this.resetCombo();
      if (mc.player != null) {
         this.currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
         float[] var10001 = new float[2];
         var10001[0] = mc.player.getYaw();
         var10001[1] = mc.player.getPitch();
         this.droidLerpRotation = var10001;
      }

      AuraTransition.INSTANCE.setMode(false, false);
      AuraTransition.INSTANCE.reset();
   }

   private void handleDroidRotation(
      LivingEntity val
   ) {
      if (mc.player != null) {
         if (mc.player.isGliding()) {
            Vec3d pos = val.getPos()
               .add(
                  0.0,
                  (double)(
                     val.getHeight()
                        * 0.6F
                  ),
                  0.0
               )
               .subtract(mc.player.getEyePos());
            if (val.isGliding()
               && ModuleManager.elytraTarget.isEnable()) {
               Vec3d vec = Vec3d.fromPolar(
                  val.getLerpTargetPitch(),
                  val.getLerpTargetYaw()
               );
               Vec3d vec2 = val.getRotationVector();
               Vec3d vec3 = vec.normalize()
                  .lerp(
                     vec2,
                     vec.length()
                  );
               pos = pos.add(
                  vec3.normalize()
                     .multiply((double)ModuleManager.elytraTarget.predictValue.getValue().floatValue())
               );
            }

            float f = (float)MathHelper.wrapDegrees(
               Math.toDegrees(
                     Math.atan2(
                        pos.z,
                        pos.x
                     )
                  )
                  - 90.0
            );
            float f2 = (float)(
               -Math.toDegrees(
                  Math.atan2(
                     pos.y,
                     Math.hypot(
                        pos.x,
                        pos.z
                     )
                  )
               )
            );
            RotationStorage.update(
               new Angle(
                  f,
                  f2
               ),
               360.0F,
               360.0F,
               360.0F,
               360.0F,
               1,
               1,
               clientLook.isState()
            );
         } else {
            if (this.droidLerpRotation[0] == 0.0F && this.droidLerpRotation[1] == 0.0F) {
               this.droidLerpRotation[0] = mc.player.getYaw();
               this.droidLerpRotation[1] = mc.player.getPitch();
            }

            Vec3d vec4 = new Vec3d(
               val.getX(),
               val.getY()
                  + (double)(
                     val.getHeight()
                        * 0.8F
                  ),
               val.getZ()
            );
            Vec3d vec5 = mc.player
               .getEyePos();
            Vec3d vec6 = vec4.subtract(
                  vec5
               )
               .normalize();
            float f3 = (float)Math.toDegrees(
               Math.atan2(
                  -vec6.x,
                  vec6.z
               )
            );
            float f4 = (float)MathHelper.clamp(
               -Math.toDegrees(
                  Math.atan2(
                     vec6.y,
                     Math.hypot(
                        vec6.x,
                        vec6.z
                     )
                  )
               ),
               -89.0,
               89.0
            );
            long val2 = System.currentTimeMillis();
            Random val3 = new Random();
            boolean flag = mc.player
                  .getAttackCooldownProgress(1.5F)
               >= 0.92F;
            if (!flag
               )
             {
               if (!this.spookyIsLookingAway
                  && val2
                        - this.spookyLastLookAwayTime
                     >= 2000L) {
                  this.spookyIsLookingAway = true;
                  this.spookyLookAwayStartTime = val2;
                  float f5 = 180.0F
                     + (
                        val3.nextFloat()
                              * 40.0F
                           - 20.0F
                     );
                  float var29;
                  this.spookyLookAwayTargetYaw = f3
                     + (
                        var29 = f5
                           * (float)(
                              val3.nextBoolean()
                                 ? 1
                                 : -1
                           )
                     );
                  float f6 = 45.0F
                     + (
                        val3.nextFloat()
                              * 40.0F
                           - 20.0F
                     );
                  float var32;
                  this.spookyLookAwayTargetPitch = MathHelper.clamp(
                     f4
                        + (
                           var32 = f6
                              * (float)(
                                 val3.nextBoolean()
                                    ? 1
                                    : -1
                              )
                        ),
                     -89.0F,
                     89.0F
                  );
               }

               if (this.spookyIsLookingAway) {
                  long val4 = val2
                     - this.spookyLookAwayStartTime;
                  if (val4
                     < 80L) {
                     float f7 = (float)val4
                        / 80.0F;
                     float f8 = 1.0F
                        - (float)Math.pow(
                           (double)(
                              1.0F
                                 - f7
                           ),
                           3.0
                        );
                     f3 += (
                           this.spookyLookAwayTargetYaw
                              - f3
                        )
                        * f8;
                     f4 += (
                           this.spookyLookAwayTargetPitch
                              - f4
                        )
                        * f8;
                  } else if (val4
                     < 160L) {
                     long val5 = val4
                        - 80L;
                     float f9 = (float)val5
                        / 80.0F;
                     float f10 = (float)Math.pow(
                        (double)f9,
                        3.0
                     );
                     Vec3d vec7 = new Vec3d(
                        val.getX(),
                        val.getY()
                           + (double)(
                              val.getHeight()
                                 * 0.8F
                           ),
                        val.getZ()
                     );
                     Vec3d vec8 = vec7.subtract(
                           vec5
                        )
                        .normalize();
                     float f11 = (float)Math.toDegrees(
                        Math.atan2(
                           -vec8.x,
                           vec8.z
                        )
                     );
                     float f12 = (float)MathHelper.clamp(
                        -Math.toDegrees(
                           Math.atan2(
                              vec8.y,
                              Math.hypot(
                                 vec8.x,
                                 vec8.z
                              )
                           )
                        ),
                        -89.0,
                        89.0
                     );
                     f3 = this.spookyLookAwayTargetYaw
                        + (
                              f11
                                 - this.spookyLookAwayTargetYaw
                           )
                           * f10;
                     f4 = this.spookyLookAwayTargetPitch
                        + (
                              f12
                                 - this.spookyLookAwayTargetPitch
                           )
                           * f10;
                  } else {
                     this.spookyIsLookingAway = false;
                     this.spookyLastLookAwayTime = val2;
                  }
               }
            } else if (this.spookyIsLookingAway) {
               this.spookyIsLookingAway = false;
               this.spookyLastLookAwayTime = val2
                  - 2000L
                  + 500L;
            }

            float f13 = MathHelper.wrapDegrees(
               f3
                  - this.droidLerpRotation[0]
            );
            float f14 = MathHelper.wrapDegrees(
               f4
                  - this.droidLerpRotation[1]
            );
            float y2 = this.spookyIsLookingAway
               ? 3.0F
               : 1.0F;
            float f15 = ((Double)mc.options
                        .getMouseSensitivity()
                        .getValue())
                     .floatValue()
                  * 0.6F
               + 0.2F;
            f15 = f15
               * f15
               * f15
               * 1.2F;
            float f16 = 8.5F
               * y2;
            float f17 = Math.min(
               Math.abs(
                  f13
               ),
               f16
            );
            float f18;
            if (val2
                  - this.spookyLastPitchUpdateTime
               >= 290L) {
               f18 = Math.min(
                  Math.abs(
                     f14
                  ),
                  8.0F
               );
               this.spookyLastPitchUpdateTime = val2;
            } else {
               f18 = Math.min(
                  Math.abs(
                     f14
                  ),
                  3.0F
               );
            }

            float f19 = this.droidLerpRotation[0]
               + (
                  f13
                        > 0.0F
                     ? f17
                     : -f17
               );
            float f20 = this.droidLerpRotation[1]
               + (
                  f14
                        > 0.0F
                     ? f18
                     : -f18
               );
            f20 = MathHelper.clamp(
               f20,
               -89.0F,
               89.0F
            );
            f19 -= (
                  f19
                     - this.droidLerpRotation[0]
               )
               % f15;
            f20 -= (
                  f20
                     - this.droidLerpRotation[1]
               )
               % f15;
            this.droidLerpRotation[0] = f19;
            this.droidLerpRotation[1] = f20;
            float y3 = this.spookyIsLookingAway
               ? 8.0F
               : 3.0F;
            this.targetRotations = new Vec2f(
               f19,
               f20
            );
            this.currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
            RotationStorage.update(
               new Angle(
                  f19,
                  f20
               ),
               y3
                  * 12.0F,
               y3
                  * 10.0F,
               y3
                  * 4.0F,
               y3
                  * 4.0F,
               1,
               5,
               clientLook.isState()
            );
         }
      }
   }

   private void updateAssistRotation(
      LivingEntity val
   ) {
      boolean flag;
      if (val
            != null
         && (
            (
                  flag = mc.player
                     .isGliding()
               )
               || System.currentTimeMillis() - lastPhysicalMoveTime <= 100L
         )) {
         if (System.currentTimeMillis() - lastPhysicalMoveTime < 100L) {
            this.lastAssistYaw = this.getCurrentLookYaw();
            this.lastAssistPitch = this.getCurrentLookPitch();
         }

         Vec3d vec;
         if ((
               vec = RaycastUtil.getNearestPoint(
                  val
               )
            )
            == null) {
            vec = GetclosestpointHelper.getClosestPoint(
               val
            );
         }

         if (vec
            == null) {
            vec = val.getBoundingBox()
               .getCenter();
         }

         if (flag
            && val.isGliding()
            && ModuleManager.elytraTarget != null
            && ModuleManager.elytraTarget.isEnable()) {
            vec = this.getPredictedRotationPoint(
               val,
               vec
            );
         }

         Vec3d vec2 = mc.player
            .getEyePos();
         double d = vec.x
            - vec2.x;
         double d2 = vec.y
            - vec2.y;
         double d3 = vec.z
            - vec2.z;
         double d4 = Math.sqrt(
            d
                  * d
               + d3
                  * d3
         );
         float f = (float)(
            Math.toDegrees(
                  Math.atan2(
                     d3,
                     d
                  )
               )
               - 90.0
         );
         float f2 = (float)(
            -Math.toDegrees(
               Math.atan2(
                  d2,
                  d4
               )
            )
         );
         float f3 = this.assistRadius
            .getValue()
            .floatValue();
         Box box = val.getBoundingBox();
         Vec3d vec3 = RaytraceHelper.getVectorForRotation(
            this.getCurrentLookPitch(), this.getCurrentLookYaw()
         );
         boolean flag2 = box.expand(
               (double)f3,
               (double)f3,
               (double)f3
            )
            .raycast(
               vec2,
               vec2.add(
                  vec3.multiply(
                     6.0
                  )
               )
            )
            .isPresent();
         float f4 = flag2
            ? this.assistSpeedAimed.getValue().floatValue() * 0.25F
            : (
               mc.player.isGliding()
                  ? this.assistSpeedElytra.getValue().floatValue() * 4.0F
                  : (
                     mc.player.isOnGround()
                        ? this.assistSpeedGround.getValue().floatValue() * 0.25F
                        : this.assistSpeedAir.getValue().floatValue() * 0.25F
                  )
            );
         float y2 = mc.player
                  .getAttackCooldownProgress(0.5F)
               > 0.85F
            ? 1.2F
            : 0.4F;
         f4 *= y2;
         if (!box.expand(
               (double)(
                  f3
                     + 0.1F
               ),
               (double)(
                  f3
                     + 0.1F
               ),
               (double)(
                  f3
                     + 0.1F
               )
            )
            .raycast(
               vec2,
               vec2.add(
                  vec3.multiply(
                     6.0
                  )
               )
            )
            .isPresent()) {
            this.speedAcceleration += 5.0E-4F
               * y2;
         } else if (this.speedAcceleration >= -0.01F) {
            this.speedAcceleration -= 4.0E-4F;
         }

         float x2 = Math.max(
            this.speedAcceleration, 0.0F
         );
         float f5 = MathHelper.wrapDegrees(
            f
               - this.lastAssistYaw
         );
         float f6 = f2
            - this.lastAssistPitch;
         float var31;
         float f7 = MathHelper.clamp(
            f5,
            -(
               var31 = f4
                  + x2
                  + (float)((Math.random() - 0.5) * 0.02)
            ),
            var31
         );
         float f8 = MathHelper.clamp(
            f6,
            -var31,
            var31
         );
         float yaw = this.lastAssistYaw
            + f7;
         float f9 = MathHelper.clamp(
            this.lastAssistPitch
               + f8,
            -89.9F,
            89.9F
         );
         float f10 = MouseGCD.getGCDValue();
         if (f10
            > 0.0F) {
            yaw = this.lastAssistYaw
               + (float)Math.round(
                     (
                           yaw
                              - this.lastAssistYaw
                        )
                        / f10
                  )
                  * f10;
            f9 = this.lastAssistPitch
               + (float)Math.round(
                     (
                           f9
                              - this.lastAssistPitch
                        )
                        / f10
                  )
                  * f10;
         }

         Angle val2 = new Angle(
            yaw,
            f9
         );
         RotationStorage.update(
            val2,
            360.0F,
            360.0F,
            360.0F,
            360.0F,
            0,
            1,
            false
         );
         this.targetRotations = new Vec2f(
            val2.getYaw(),
            val2.getPitch()
         );
         this.lastAssistYaw = val2.getYaw();
         this.lastAssistPitch = val2.getPitch();
      }
   }

   @Generated
   public LivingEntity getTarget() {
      return this.target;
   }

   @Generated
   public Vec2f getCurrentRotations() {
      return this.currentRotations;
   }

   @Generated
   public Vec2f getTargetRotations() {
      return this.targetRotations;
   }

   public FinishedHelper getAttackTimer() {
      return this.attackTimer;
   }
}
