package dev.leg1tka.modules.misc;


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

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TrapScanner extends Module {
   public static TrapScanner INSTANCE = new TrapScanner();
   private static final long DELAYED_SCAN_MS = 250L;
   private static final int SEARCH_RADIUS = 4;
   private static final int SEARCH_HEIGHT = 4;
   private static final int MAX_TIMERS = 100;
   private static final float TIMER_SECONDS = 19.5F;
   private static final float MAX_DISTANCE = 96.0F;
   private static final double TIMER_Y_OFFSET = 0.6;
   private static final ItemStack LAYER_ICON = new ItemStack(Items.DRIED_KELP);
   private final Matrix4f lastProjectionMatrix = new Matrix4f();
   private final Quaternionf lastCameraRotation = new Quaternionf();
   private Vec3d lastCameraPos = Vec3d.ZERO;
   private boolean hasProjection;
   private final List<BlockposHelper> timers = new ArrayList<>();
   private final List<CenterHelper> pendingScans = new ArrayList<>();

   public TrapScanner() {
      super(
         "LayerCooldown",
         "Показывает таймер возле поставленного пласта",
         Category.MISC
      );
   }

   @Override
   public void onDisable() {
      this.timers.clear();
      this.pendingScans.clear();
      this.hasProjection = false;
      super.onDisable();
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (val.getType()
            == EventPacket.Type.RECEIVE
         && mc.world != null
         && mc.player != null) {
         if (val.getPacket() instanceof PlaySoundS2CPacket val2
            )
          {
            String str = this.getSoundPath(
               val2
            );
            if (str
               != null) {
               Vec3d vec = new Vec3d(
                  val2.getX(),
                  val2.getY(),
                  val2.getZ()
               );
               BlockPos pos2 = BlockPos.ofFloored(
                  vec
               );
               if ("block.piston.extend"
                  .equals(
                     str
                  )) {
                  this.addTimer(
                     pos2,
                     vec
                  );
               } else {
                  if (this.isDelayedTrapSound(
                     str
                  )) {
                     this.pendingScans
                        .add(
                           new CenterHelper(
                              pos2,
                              System.currentTimeMillis() + 250L
                           )
                        );
                  }
               }
            }
         }
      }
   }

   @EventLink(
      priority = 100
   )
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.world != null && mc.player != null) {
         this.hasProjection = true;
         this.lastProjectionMatrix
            .set(
               val.getProjectionMatrix()
            );
         this.lastCameraRotation
            .set(
               val.getCamera()
                  .getRotation()
            );
         this.lastCameraPos = val.getCamera()
            .getPos();
         this.processPendingScans();
      }
   }

   @EventLink(
      priority = 100
   )
   public void onRender2D(
      EventRender.Default val
   ) {
      if (this.hasProjection && mc.world != null && mc.player != null) {
         long val2 = System.currentTimeMillis();
         this.timers
            .removeIf(
               val3 -> val3.endTime
                     <= val2
            );

         while (this.timers.size() > 100) {
            this.timers.remove(0);
         }

         if (!this.timers.isEmpty()) {
            MatrixStack matrices = val.getContext()
               .getMatrices();
            Font font = Fonts.getFont(
               "sf_regular",
               13
            );
            if (font
               != null) {
               float f = 9216.0F;

               for (int i = 0;
                  i
                     < this.timers.size();
                  i++
               ) {
                  BlockposHelper pos2 = this.timers
                     .get(
                        i
                     );
                  if (!(
                     mc.player
                           .squaredDistanceTo(
                              pos2.pos
                           )
                        > (double)f
                  )) {
                     Vec3d vec = this.worldToScreen(
                        pos2.pos
                     );
                     if (vec
                        != null) {
                        float x2 = Math.max(
                           0.0F,
                           (float)(
                                 pos2.endTime
                                    - val2
                              )
                              / 1000.0F
                        );
                        this.drawTimer(
                           val.getContext(),
                           matrices,
                           font,
                           (float)vec.x,
                           (float)vec.y,
                           x2
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private void processPendingScans() {
      if (!this.pendingScans.isEmpty() && mc.world != null) {
         long val = System.currentTimeMillis();
         Iterator<CenterHelper> val2 = this.pendingScans.iterator();

         while (
            val2.hasNext()
         ) {
            CenterHelper val3 = val2.next();
            if (val3.runAt
               <= val
               )
             {
               BlockPos pos2 = this.findLayerLikeBlock(
                  val3.center
               );
               Vec3d vec = pos2
                     == null
                  ? Vec3d.ofCenter(
                     val3.center
                  )
                  : new Vec3d(
                     (double)pos2.getX()
                        + 0.5,
                     (double)pos2.getY()
                        + 0.65,
                     (double)pos2.getZ()
                        + 0.5
                  );
               this.addTimer(
                  pos2
                        == null
                     ? val3.center
                     : pos2,
                  vec
               );
               val2.remove();
            }
         }
      }
   }

   private BlockPos findLayerLikeBlock(
      BlockPos val
   ) {
      BlockPos pos2 = null;
      double d = Double.MAX_VALUE;

      for (int i = -4;
         i
            <= 4;
         i++
      ) {
         for (int i2 = -4;
            i2
               <= 4;
            i2++
         ) {
            for (int i3 = -4;
               i3
                  <= 4;
               i3++
            ) {
               BlockPos pos3 = val.add(
                  i,
                  i2,
                  i3
               );
               BlockState state = mc.world
                  .getBlockState(
                     pos3
                  );
               if (this.isLayerLikeBlock(
                  state
               )) {
                  double dist = pos3.getSquaredDistance(
                     val
                  );
                  if (dist
                     < d
                     )
                   {
                     d = dist;
                     pos2 = pos3;
                  }
               }
            }
         }
      }

      return pos2;
   }

   private boolean isLayerLikeBlock(
      BlockState val
   ) {
      if (val
            != null
         && !val.isAir()
         )
       {
         Block val2 = val.getBlock();
         return val2
               == Blocks.PISTON
            || val2
               == Blocks.STICKY_PISTON
            || val2
               == Blocks.MOVING_PISTON
            || val2
               == Blocks.DRIED_KELP_BLOCK
            || val2
               == Blocks.ANVIL
            || val2
               == Blocks.CHIPPED_ANVIL
            || val2
               == Blocks.DAMAGED_ANVIL;
      } else {
         return false;
      }
   }

   private void addTimer(
      BlockPos val,
      Vec3d val2
   ) {
      long val3 = System.currentTimeMillis()
         + 19500L;

      for (int i = 0;
         i
            < this.timers.size();
         i++
      ) {
         BlockposHelper pos2 = this.timers
            .get(
               i
            );
         if (pos2.blockPos
               .getSquaredDistance(
                  val
               )
            <= 2.25) {
            this.timers
               .set(
                  i,
                  new BlockposHelper(
                     val,
                     val2.add(
                        0.0, 0.6, 0.0
                     ),
                     val3
                  )
               );
            return;
         }
      }

      this.timers
         .add(
            new BlockposHelper(
               val,
               val2.add(
                  0.0, 0.6, 0.0
               ),
               val3
            )
         );
   }

   private boolean isDelayedTrapSound(
      String val
   ) {
      return "block.anvil.place"
            .equals(
               val
            )
         || "entity.zombie_horse.death"
            .equals(
               val
            )
         || "entity.ender_dragon.growl"
            .equals(
               val
            );
   }

   private String getSoundPath(
      PlaySoundS2CPacket val
   ) {
      try {
         return Registries.SOUND_EVENT
            .getId(
               (SoundEvent)val.getSound()
                  .value()
            )
            .getPath();
      } catch (Exception var3) {
         return null;
      }
   }

   private void drawTimer(
      DrawContext val,
      MatrixStack val2,
      Font val3,
      float val4,
      float val5,
      float val6
   ) {
      String text = this.formatOneDecimal(
            val6
         )
         + "с";
      float width = val3.getStringWidth(
         text
      );
      float f = 10.0F;
      float f2 = 0.62F;
      float f3 = 3.0F;
      float f4 = f
         + f3
         + width
         + 8.0F;
      float f5 = 12.5F;
      float f6 = val4
         - f4
            * 0.5F;
      float f7 = val5
         - f5
            * 0.5F;
      int color = ColorUtil.getThemeColor();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      Render2D.drawDefaultHudThemedPanel(
         val2,
         f6,
         f7,
         f4,
         f5,
         2.0F,
         3.0F,
         color
      );
      this.drawItemIcon(
         val,
         val2,
         f6
            + 4.0F,
         f7
            + 1.25F,
         f2
      );
      val3.drawString(
         val2,
         text,
         f6
            + 4.0F
            + f
            + f3,
         f7
            + 4.55F,
         -1
      );
      RenderSystem.disableBlend();
   }

   private String formatOneDecimal(
      float val
   ) {
      int n = Math.round(
         val
            * 10.0F
      );
      return n
            / 10
         + "."
         + Math.abs(
            n
               % 10
         );
   }

   private void drawItemIcon(
      DrawContext val,
      MatrixStack val2,
      float val3,
      float val4,
      float val5
   ) {
      if (val
         != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         val2.push();
         val2.translate(
            val3,
            val4,
            0.0F
         );
         val2.scale(
            val5,
            val5,
            1.0F
         );
         val.drawItem(
            LAYER_ICON, 0, 0
         );
         val2.pop();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
      }
   }

   private Vec3d worldToScreen(
      Vec3d val
   ) {
      if (mc != null && mc.getWindow() != null) {
         Vector3f vec3f = new Vector3f(
            (float)(
               val.x
                  - this.lastCameraPos.x
            ),
            (float)(
               val.y
                  - this.lastCameraPos.y
            ),
            (float)(
               val.z
                  - this.lastCameraPos.z
            )
         );
         Quaternionf quaternion = new Quaternionf(
               this.lastCameraRotation
            )
            .conjugate();
         vec3f.rotate(
            quaternion
         );
         Vector4f vec4f = new Vector4f(
            vec3f.x,
            vec3f.y,
            vec3f.z,
            1.0F
         );
         this.lastProjectionMatrix
            .transform(
               vec4f
            );
         float f = vec4f.w;
         if (f
            <= 1.0E-5F) {
            return null;
         } else {
            float x2 = vec4f.x
               / f;
            float y2 = vec4f.y
               / f;
            float f2 = vec4f.z
               / f;
            float f3 = (
                  x2
                        * 0.5F
                     + 0.5F
               )
               * (float)mc.getWindow().getScaledWidth();
            float f4 = (
                  1.0F
                     - (
                        y2
                              * 0.5F
                           + 0.5F
                     )
               )
               * (float)mc.getWindow().getScaledHeight();
            if (!Float.isNaN(
                  f3
               )
               && !Float.isNaN(
                  f4
               )
               && !Float.isInfinite(
                  f3
               )
               && !Float.isInfinite(
                  f4
               )) {
               return !(
                        f3
                           < -400.0F
                     )
                     && !(
                        f4
                           < -400.0F
                     )
                     && !(
                        f3
                           > (float)(mc.getWindow().getScaledWidth() + 400)
                     )
                     && !(
                        f4
                           > (float)(mc.getWindow().getScaledHeight() + 400)
                     )
                  ? new Vec3d(
                     (double)f3,
                     (double)f4,
                     (double)f2
                  )
                  : null;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }
}
