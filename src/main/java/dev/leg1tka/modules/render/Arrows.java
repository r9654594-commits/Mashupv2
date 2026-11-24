package dev.leg1tka.modules.render;


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
import dev.leg1tka.api.events.implement.EventRender;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class Arrows extends Module {
   public static Arrows INSTANCE = new Arrows();
   private static final Identifier FIRST_ARROW_TEXTURE = ClientIdentifier.of("textures/arrows/arrow.png");
   private static final Identifier SECOND_ARROW_TEXTURE = ClientIdentifier.of("textures/arrows/arr.png");
   private static final Identifier MAMA_ARROW_TEXTURE = ClientIdentifier.of("textures/arrows/arrowsnurik.png");
   private final ModeSetting type;
   private final NumberSetting radius;
   private final NumberSetting size;
   private final NumberSetting glowRadius;
   private final BooleanSetting onlyArmored;
   private final Map<UUID, AlphaHolder> states;
   private final Set<UUID> seenPlayers;

   public Arrows() {
      super("Arrows", Category.RENDER);
      String var1 = "Arrows";
      String var3 = "Вид";
      String var8 = "Первый";
      String[] var10005 = new String[3];
      var10005[0] = "Первый";
      var10005[1] = "Второй";
      int var10007 = 2;
      var10005[var10007] = "Третий";
      this.type = new ModeSetting(var3, var8, var10005);
      this.radius = new NumberSetting(
         "Радиус", 58.0F, 30.0F, 120.0F, 1.0F
      );
      this.size = new NumberSetting(
         "Размер", 13.0F, 8.0F, 28.0F, 0.5F
      );
      this.glowRadius = new NumberSetting(
         "Свечение", 7.5F, 0.0F, 20.0F, 0.5F
      );
      this.onlyArmored = new BooleanSetting(
         "Только в броне", false
      );
      this.states = new HashMap<>();
      this.seenPlayers = new HashSet<>();
      this.addSettings(new Setting[]{
         this.type,
         this.radius,
         this.size,
         this.glowRadius,
         this.onlyArmored
      });
   }

   @EventLink
   public void onRender(
      EventRender.Default val
   ) {
      if (mc.player != null && mc.world != null && !mc.options.hudHidden) {
         if (mc.options.getPerspective() != Perspective.FIRST_PERSON) {
            this.fadeAllStates();
         } else {
            float f = val.getPartialTicks();
            float f2 = (float)mc.getWindow()
                  .getScaledWidth()
               * 0.5F;
            float f3 = (float)mc.getWindow()
                  .getScaledHeight()
               * 0.5F;
            float f4 = this.size
               .get();
            float f5 = f3
               - this.radius.get();
            float yaw = this.getReferenceYaw(
               f
            );
            Vec3d pos = this.getReferencePos(
               f
            );
            this.seenPlayers.clear();

            for (AbstractClientPlayerEntity player2 : mc.world
               .getPlayers()) {
               if (player2
                     != mc.player
                  && player2.isAlive()
                  && !player2.isSpectator()
                  && !this.isGhostPlayer(
                     player2
                  )) {
                  UUID uuid = player2.getUuid();
                  AlphaHolder val2 = this.states
                     .computeIfAbsent(
                        uuid,
                        val3 -> new AlphaHolder()
                     );
                  this.seenPlayers
                     .add(
                        uuid
                     );
                  if (this.onlyArmored.isState()
                     && !this.hasAnyArmor(
                        player2
                     )) {
                     val2.alpha = this.approach(
                        val2.alpha,
                        0.0F,
                        0.1F
                     );
                  } else {
                     int color = this.getPlayerColor(
                        player2
                     );
                     float yaw2 = this.getRelativeYaw(
                        player2,
                        f,
                        yaw,
                        pos
                     );
                     val2.rotation = this.interpolateAngle(
                        val2.rotation,
                        yaw2,
                        0.18F
                     );
                     val2.alpha = this.approach(
                        val2.alpha,
                        1.0F,
                        0.12F
                     );
                     float f6 = MathHelper.clamp(
                        val2.alpha,
                        0.0F,
                        1.0F
                     );
                     if (!(
                        f6
                           <= 0.01F
                     )) {
                        int color2 = ColorUtil.applyAlpha(
                           color,
                           f6
                        );
                        int color3 = ColorUtil.applyAlpha(
                           ColorUtil.darken(
                              color,
                              0.55F
                           ),
                           f6
                              * 0.65F
                        );
                        this.renderArrow(
                           val.getContext()
                              .getMatrices(),
                           f2,
                           f3,
                           f5,
                           f4,
                           val2.rotation,
                           color2,
                           color3
                        );
                     }
                  }
               }
            }

            this.states
               .entrySet()
               .removeIf(
                  val4 -> {
                     if (this.seenPlayers
                        .contains(
                           val4.getKey()
                        )) {
                        return false;
                     } else {
                        AlphaHolder val5 = val4.getValue();
                        val5.alpha = this.approach(
                           val5.alpha,
                           0.0F,
                           0.1F
                        );
                        return val5.alpha
                           <= 0.02F;
                     }
                  }
               );
         }
      } else {
         this.states.clear();
      }
   }

   private void renderArrow(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      float val6,
      int val7,
      int val8
   ) {
      Identifier id;
      if (this.type.getIndex() == 0) {
         id = FIRST_ARROW_TEXTURE;
      } else if (this.type.getIndex() == 1) {
         id = SECOND_ARROW_TEXTURE;
      } else {
         id = MAMA_ARROW_TEXTURE;
      }

      val.push();
      val.translate(
         val2,
         val3,
         0.0F
      );
      val.multiply(
         RotationAxis.POSITIVE_Z
            .rotationDegrees(
               val6
            )
      );
      val.translate(
         -val2,
         -val3,
         0.0F
      );
      float f = val2
         - val5
            * 0.5F;
      Render2D.drawImage(
         val,
         id,
         f,
         val4
            + val5
               * 0.08F,
         val5,
         val5,
         val8
      );
      Render2D.drawImage(
         val,
         id,
         f,
         val4,
         val5,
         val5,
         val7
      );
      val.pop();
   }

   private void fadeAllStates() {
      this.states
         .entrySet()
         .removeIf(
            val -> {
               AlphaHolder val2 = val.getValue();
               val2.alpha = this.approach(
                  val2.alpha,
                  0.0F,
                  0.1F
               );
               return val2.alpha
                  <= 0.02F;
            }
         );
   }

   private float approach(
      float val,
      float val2,
      float val3
   ) {
      val3 = MathHelper.clamp(
         val3,
         0.0F,
         1.0F
      );
      return MathHelper.lerp(
         val3,
         val,
         val2
      );
   }

   private int getPlayerColor(
      AbstractClientPlayerEntity val
   ) {
      String name = val.getName()
         .getString();
      boolean flag = AspectDLC.INSTANCE
               .friendStorage
            != null
         && AspectDLC.INSTANCE
            .friendStorage
            .isFriend(
               name
            );
      return flag
         ? ColorUtil.rgb(
            85,
            255,
            85
         )
         : ColorUtil.getThemeColor();
   }

   private float getRelativeYaw(
      Entity val,
      float val2,
      float val3,
      Vec3d val4
   ) {
      Vec3d vec = DirectionHelper.interpolate(
         val,
         val2
      );
      double d = vec.x
         - val4.x;
      double d2 = vec.z
         - val4.z;
      float f = (float)(
         -Math.toDegrees(
            Math.atan2(
               d,
               d2
            )
         )
      );
      return MathHelper.wrapDegrees(
         f
            - val3
      );
   }

   private float getReferenceYaw(
      float val
   ) {
      return FreeLookStorage.isActive()
         ? FreeLookStorage.getFreeYaw()
         : MathHelper.lerp(
            val,
            mc.player.prevYaw,
            mc.player.getYaw()
         );
   }

   private Vec3d getReferencePos(
      float val
   ) {
      return FreeLookStorage.isActive() && mc.gameRenderer != null && mc.gameRenderer.getCamera() != null
         ? mc.gameRenderer.getCamera().getPos()
         : DirectionHelper.interpolate(
            mc.player,
            val
         );
   }

   private float interpolateAngle(
      float val,
      float val2,
      float val3
   ) {
      float f = MathHelper.wrapDegrees(
         val2
            - val
      );
      return val
         + f
            * val3;
   }

   private boolean isGhostPlayer(
      AbstractClientPlayerEntity val
   ) {
      if (val.getCustomName()
         != null) {
         String name = val.getCustomName()
            .getString();
         if (name
            != null) {
            if (name.startsWith(
               "Ghost_"
            )) {
               return true;
            }
         }
      }

      return "OtherClientPlayerEntity"
            .equals(
               val.getClass()
                  .getSimpleName()
            )
         && val.getPitch()
            == -30.0F;
   }

   private boolean hasAnyArmor(
      AbstractClientPlayerEntity val
   ) {
      for (ItemStack stack : val.getArmorItems()) {
         if (!stack.isEmpty()
            )
          {
            return true;
         }
      }

      return false;
   }
}
