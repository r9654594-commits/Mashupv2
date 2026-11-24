package dev.leg1tka.storage;
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
import dev.leg1tka.utils.*;


import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import lombok.Generated;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class WaypointStorage implements IMinecraft {
   private static final Identifier ARROW_TEXTURE = ClientIdentifier.of("textures/arrows/gps.png");
   private final UpdateHelper alphaAnimation = new UpdateHelper(0.0F, 8.5F, EasingFunctions.CUBIC_OUT);
   private float animatedYaw;
   private GetxHelper activeWaypoint = null;

   public WaypointStorage() {
      EventInvoker.register(this);
   }

   public void set(
      GetxHelper val
   ) {
      this.activeWaypoint = val;
   }

   public void remove(
      GetxHelper val
   ) {
      if (this.activeWaypoint != null
         && this.activeWaypoint
            .equals(
               val
            )) {
         this.activeWaypoint = null;
      }
   }

   public void clear() {
      this.activeWaypoint = null;
   }

   public boolean isEmpty() {
      return this.activeWaypoint == null;
   }

   @EventLink
   public void onRender2D(
      EventRender.Default event
   ) {
      if (mc.player != null && mc.world != null) {
         this.alphaAnimation.update(this.activeWaypoint == null ? 0.0F : 1.0F);
         float yaw = MathHelper.clamp(
            this.alphaAnimation.getValue(), 0.0F, 1.0F
         );
         if (this.activeWaypoint != null
            && !(
               yaw
                  <= 0.02F
            )) {
            float yaw2 = (float)mc.getWindow()
                  .getScaledWidth()
               * 0.5F;
            float yaw3 = (float)mc.getWindow()
                  .getScaledHeight()
               * 0.25F;
            float yaw4 = 40.0F;
            double x = this.activeWaypoint
                  .getX()
               - mc.player.getX();
            double x2 = this.activeWaypoint
                  .getZ()
               - mc.player.getZ();
            int color = (int)DirectionHelper.round(
               MathHelper.sqrt(
                  (float)(
                     x
                           * x
                        + x2
                           * x2
                  )
               )
            );
            float yaw5 = (float)(
                  -Math.toDegrees(
                     Math.atan2(
                        x,
                        x2
                     )
                  )
               )
               - mc.gameRenderer.getCamera().getYaw();
            this.animatedYaw = this.interpolateAngle(
               this.animatedYaw,
               yaw5,
               0.18F
            );
            int color2 = ColorUtil.applyAlpha(
               ColorUtil.getThemeColor(),
               yaw
            );
            Font font = Fonts.getFont(
               "sf_regular", 12
            );
            if (font
               != null) {
               String text = color
                  + "m.";
               font.draw(
                  event.getContext()
                     .getMatrices(),
                  text,
                  yaw2
                     - font.getWidth(
                           text
                        )
                        * 0.5F
                     + 1.5F,
                  yaw3
                     + 7.5F,
                  ColorUtil.applyAlpha(
                     -1,
                     yaw
                  )
               );
            }

            event.getContext()
               .getMatrices()
               .push();
            event.getContext()
               .getMatrices()
               .translate(
                  yaw2,
                  yaw3,
                  0.0F
               );
            event.getContext()
               .getMatrices()
               .multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.animatedYaw));
            event.getContext()
               .getMatrices()
               .translate(
                  -yaw2,
                  -yaw3,
                  0.0F
               );
            float yaw6 = yaw2
               - yaw4
                  * 0.5F;
            float yaw7 = yaw3
               - yaw4
                  * 0.5F;
            Render2D.drawImage(
               event.getContext()
                  .getMatrices(),
               ARROW_TEXTURE,
               yaw6,
               yaw7,
               yaw4,
               yaw4,
               color2
            );
            event.getContext()
               .getMatrices()
               .pop();
         }
      }
   }

   private float interpolateAngle(
      float f,
      float f2,
      float f3
   ) {
      float f4 = MathHelper.wrapDegrees(
         f2
            - f
      );
      return f
         + f4
            * f3;
   }

   @Generated
   public GetxHelper getActiveWaypoint() {
      return this.activeWaypoint;
   }
}
