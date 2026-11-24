package dev.leg1tka.hud;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;



import dev.leg1tka.modules.combat.*;
import dev.leg1tka.modules.misc.*;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class TargetHud extends HudElement {
   private final UpdateHelper alphaAnimation = new UpdateHelper(0.0F, 9.0F, EasingFunctions.QUAD_OUT);
   private final UpdateHelper unusualRevealAnimation = new UpdateHelper(0.0F, 4.6F, EasingFunctions.QUAD_OUT);
   private final UpdateHelper hpAnimation = new UpdateHelper(1.0F, 9.2F, EasingFunctions.QUAD_OUT);
   private final UpdateHelper hpTrailAnimation = new UpdateHelper(1.0F, 7.4F, EasingFunctions.QUAD_OUT);
   private final UpdateHelper hpValueAnimation = new UpdateHelper(20.0F, 7.0F, EasingFunctions.QUAD_OUT);
   private final UpdateHelper ABValueAnimation = new UpdateHelper(0.0F, 7.0F, EasingFunctions.QUAD_OUT);
   private final UpdateHelper goldenHpAnimation = new UpdateHelper(0.0F, 9.2F, EasingFunctions.QUAD_OUT);
   private final UpdateHelper goldenHpTrailAnimation = new UpdateHelper(0.0F, 7.4F, EasingFunctions.QUAD_OUT);
   private final UpdateHelper goldenAlphaAnimation = new UpdateHelper(0.0F, 9.0F, EasingFunctions.QUAD_OUT);
   private final List<XHolder_3> headParticles = new ObjectArrayList();
   private LivingEntity lastTarget;
   private float maxAbsorption = 20.0F;
   private boolean headParticlesEnabled = true;
   private boolean healthBarStyleEnabled = false;
   private boolean nursultanStyleEnabled = false;
   private boolean outlineEnabled = false;
   private float nursultanAnimatedHealth = 0.0F;
   private float nursultanTrailHealth = 0.0F;
   private long lastParticleUpdateNs = System.nanoTime();
   private LivingEntity particleTarget;
   private int lastTargetHurtTime = 0;
   private int cachedBarThemeColor = ColorUtil.rgba(
      124,
      91,
      242,
      255
   );
   private int cachedBarThemeColor2 = ColorUtil.rgba(
      93,
      67,
      175,
      255
   );
   private Framebuffer burnBuffer;
   private int burnBufferWidth = -1;
   private int burnBufferHeight = -1;
   private final ItemStack[] displayItems = new ItemStack[6];
   private final ItemStack[] armorScratch = new ItemStack[4];
   private boolean isHpUnknown = false;
   private final UpdateHelper scaleAnimation = new UpdateHelper(0.0F, 12.0F, EasingFunctions.BACK_OUT);
   private final UpdateHelper hideScaleAnimation = new UpdateHelper(1.0F, 8.0F, EasingFunctions.BACK_IN);
   private boolean wasVisible = false;

   private static float targetHudAlphaMul() {
      return 1.0F;
   }

   public TargetHud(
      Draggable val
   ) {
      super(
         val
      );
   }

   private Font issue(
      int n
   ) {
      return Fonts.getFont(
         "nunito",
         n
      );
   }

   public boolean isHeadParticlesEnabled() {
      return this.headParticlesEnabled;
   }

   public void setHeadParticlesEnabled(
      boolean flag
   ) {
      this.headParticlesEnabled = flag;
      if (!flag
         )
       {
         this.headParticles.clear();
      }
   }

   public boolean isHealthBarStyleEnabled() {
      return this.healthBarStyleEnabled;
   }

   public void setHealthBarStyleEnabled(
      boolean flag
   ) {
      this.healthBarStyleEnabled = flag;
   }

   public boolean isNursultanStyleEnabled() {
      return this.nursultanStyleEnabled;
   }

   public void setNursultanStyleEnabled(
      boolean flag
   ) {
      this.nursultanStyleEnabled = flag;
   }

   public boolean isOutlineEnabled() {
      return this.outlineEnabled;
   }

   public void setOutlineEnabled(
      boolean flag
   ) {
      this.outlineEnabled = flag;
   }

   private int interpolateColor(
      int n,
      int n2,
      float f
   ) {
      f = MathHelper.clamp(
         f,
         0.0F,
         1.0F
      );
      int color = n
            >> 16
         & 255;
      int color2 = n
            >> 8
         & 255;
      int color3 = n
         & 255;
      int color4 = n
            >> 24
         & 255;
      int color5 = n2
            >> 16
         & 255;
      int color6 = n2
            >> 8
         & 255;
      int color7 = n2
         & 255;
      int color8 = n2
            >> 24
         & 255;
      int color9 = (int)(
         (float)color
            + (float)(
                  color5
                     - color
               )
               * f
      );
      int color10 = (int)(
         (float)color2
            + (float)(
                  color6
                     - color2
               )
               * f
      );
      int color11 = (int)(
         (float)color3
            + (float)(
                  color7
                     - color3
               )
               * f
      );
      int color12 = (int)(
         (float)color4
            + (float)(
                  color8
                     - color4
               )
               * f
      );
      return color12
            << 24
         | color9
            << 16
         | color10
            << 8
         | color11;
   }

   private void ensureBurnBuffer() {
      if (mc != null && mc.getWindow() != null) {
         int width = mc.getWindow()
            .getFramebufferWidth();
         int width2 = mc.getWindow()
            .getFramebufferHeight();
         if (this.burnBuffer == null
            || this.burnBufferWidth
               != width
            || this.burnBufferHeight
               != width2
            )
          {
            if (this.burnBuffer != null) {
               this.burnBuffer.delete();
            }

            this.burnBuffer = new SimpleFramebuffer(
               width,
               width2,
               true
            );
            this.burnBufferWidth = width;
            this.burnBufferHeight = width2;
         }
      }
   }

   private int collectDisplayItems(
      LivingEntity entity
   ) {
      int i = 0;

      for (ItemStack stack : entity.getArmorItems()) {
         if (i
            < this.armorScratch.length) {
            this.armorScratch[i++] = stack;
         }
      }

      int i2 = 0;

      for (int i3 = i
            - 1;
         i3
            >= 0;
         i3--
      ) {
         this.displayItems[i2++] = this.armorScratch[i3];
         this.armorScratch[i3] = ItemStack.EMPTY;
      }

      ItemStack stack2 = entity.getMainHandStack();
      if (!stack2.isEmpty()
         )
       {
         this.displayItems[i2++] = stack2;
      }

      ItemStack stack3 = entity.getOffHandStack();
      if (!stack3.isEmpty()
         )
       {
         this.displayItems[i2++] = stack3;
      }

      return i2;
   }

   private boolean beginBurnPassIfNeeded(
      boolean flag
   ) {
      if (!flag
         )
       {
         return false;
      } else {
         this.ensureBurnBuffer();
         if (this.burnBuffer == null) {
            return false;
         } else {
            this.burnBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            this.burnBuffer.clear();
            this.burnBuffer.beginWrite(false);
            return true;
         }
      }
   }

   private void applyHudTransform(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6
   ) {
      float x = f
         + f3
            * 0.5F;
      float x2 = f2
         + f4
            * 0.5F;
      matrices.translate(
         x,
         x2,
         0.0F
      );
      matrices.scale(
         f5,
         f6,
         1.0F
      );
      matrices.translate(
         -x,
         -x2,
         0.0F
      );
   }

   private void updateAndRenderHeadParticles(
      MatrixStack matrices,
      LivingEntity entity,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      if (entity
            != null
         && !(
            f4
               <= 0.02F
         )) {
         long time = System.nanoTime();
         float alpha = MathHelper.clamp(
            (float)(
                  time
                     - this.lastParticleUpdateNs
               )
               / 1.0E9F
               * 60.0F,
            0.2F,
            3.0F
         );
         this.lastParticleUpdateNs = time;
         if (this.particleTarget
            != entity
            )
          {
            this.headParticles.clear();
            this.particleTarget = entity;
            this.lastTargetHurtTime = Math.max(
               0,
               entity.hurtTime
            );
         }

         ThreadLocalRandom val = ThreadLocalRandom.current();
         float alpha2 = f
            + f3
               * 0.5F;
         float alpha3 = f2
            + f3
               * 0.5F;
         int i = Math.max(
            0,
            entity.hurtTime
         );
         boolean flag = i
               > 0
            && (
               i
                     > this.lastTargetHurtTime
                  || i
                        % 3
                     == 0
            );
         this.lastTargetHurtTime = i;
         if (flag
            )
          {
            int i2 = 1
               + val.nextInt(
                  2
               );

            for (int i3 = 0;
               i3
                     < i2
                  && this.headParticles.size() < 14;
               i3++
            ) {
               float alpha4 = (float)(
                  val.nextDouble()
                     * Math.PI
                     * 2.0
               );
               float alpha5 = val.nextFloat()
                  * f3
                  * 0.24F;
               float alpha6 = (float)(
                  val.nextDouble()
                     * Math.PI
                     * 2.0
               );
               float alpha7 = 0.58F
                  + val.nextFloat()
                     * 0.9F;
               XHolder_3 val2 = new XHolder_3();
               val2.x = alpha2
                  + MathHelper.cos(
                        alpha4
                     )
                     * alpha5;
               val2.y = alpha3
                  + MathHelper.sin(
                        alpha4
                     )
                     * alpha5;
               val2.vx = MathHelper.cos(
                        alpha6
                     )
                     * alpha7
                  + (
                        val2.x
                           - alpha2
                     )
                     * 0.025F;
               val2.vy = MathHelper.sin(
                        alpha6
                     )
                     * alpha7
                  + (
                        val2.y
                           - alpha3
                     )
                     * 0.025F;
               val2.size = 3.8F
                  + val.nextFloat()
                     * 1.4F;
               val2.age = 0.0F;
               val2.maxAge = 74.0F
                  + val.nextFloat()
                     * 42.0F;
               this.headParticles
                  .add(
                     val2
                  );
            }
         }

         for (int i4 = this.headParticles
                  .size()
               - 1;
            i4
               >= 0;
            i4--
         ) {
            XHolder_3 val3 = this.headParticles
               .get(
                  i4
               );
            val3.age += alpha;
            if (val3.age
               >= val3.maxAge
               )
             {
               this.headParticles
                  .remove(
                     i4
                  );
            } else {
               val3.x = val3.x
                  + val3.vx
                     * alpha;
               val3.y = val3.y
                  + val3.vy
                     * alpha;
               val3.vx = val3.vx
                  * (float)Math.pow(
                     0.975F,
                     (double)alpha
                  );
               val3.vy = val3.vy
                  * (float)Math.pow(
                     0.975F,
                     (double)alpha
                  );
               val3.vy += 0.0012F
                  * alpha;
               float alpha8 = 1.0F
                  - val3.age
                     / val3.maxAge;
               float alpha9 = alpha8
                  * alpha8
                  * (
                     3.0F
                        - 2.0F
                           * alpha8
                  );
               float alpha10 = f4
                  * alpha9;
               if (!(
                  alpha10
                     <= 0.02F
               )) {
                  float alpha11 = val3.x
                     - val3.size
                        * 0.5F;
                  float alpha12 = val3.y
                     - val3.size
                        * 0.5F;
                  int i5 = ColorUtil.applyAlpha(
                     n,
                     alpha10
                        * 0.58F
                  );
                  Render2D.drawRoundedRect(
                     matrices,
                     alpha11,
                     alpha12,
                     val3.size,
                     val3.size,
                     val3.size
                        * 0.45F,
                     i5
                  );
               }
            }
         }
      } else {
         this.headParticles.clear();
         this.particleTarget = entity;
         this.lastTargetHurtTime = 0;
      }
   }

   private void drawTargetHudItem(
      EventRender.Default event,
      MatrixStack matrices,
      ItemStack stack,
      float f,
      float f2,
      float f3
   ) {
      if (!stack.isEmpty()
         )
       {
         matrices.push();
         float x = 16.0F
            * f3;
         matrices.translate(
            f,
            f2,
            0.0F
         );
         matrices.scale(
            f3,
            f3,
            1.0F
         );
         event.getContext()
            .drawItem(
               stack,
               0,
               0
            );
         matrices.pop();
      }
   }

   private void renderEnemyCooldowns(
      EventRender.Default event,
      MatrixStack matrices,
      LivingEntity entity,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n
   ) {
      if (event
            != null
         && matrices
            != null
         && entity
            != null
         && !(
            f5
               <= 0.01F
         )) {
         if (FtHelper.INSTANCE != null && FtHelper.INSTANCE.isEnable() && FtHelper.INSTANCE.getEnemyCooldowns().isState()) {
            if (entity instanceof PlayerEntity player
               )
             {
               List<GetremainingsecHelper> list = ClearallHelper.getCooldowns(
                  player
               );
               if (!list.isEmpty()
                  )
                {
                  float width = f2
                     + f4
                     + 2.5F;
                  float width2 = 10.0F;
                  float width3 = 11.0F;
                  float width4 = 12.5F;
                  float width5 = 8.0F;
                  float width6 = f;
                  Font font = Fonts.getFont(
                     "semibold", 9
                  );
                  if (font
                     == null) {
                     font = Fonts.getFont(
                        "sf_regular", 9
                     );
                  }

                  for (var val : list) {
                     this.drawHudBackground(
                        matrices,
                        width6,
                        width,
                        width2,
                        width3,
                        n
                     );
                     float width7 = width6
                        + (
                              width2
                                 - width5
                           )
                           * 0.5F;
                     float width8 = width
                        + (
                              width3
                                 - width5
                           )
                           * 0.5F;
                     this.drawTargetHudItem(
                        event,
                        matrices,
                        val.stack,
                        width7,
                        width8,
                        0.5F
                           * f5
                     );
                     float width9 = val.getRemainingSec();
                     Locale var28 = Locale.US;
                     String text = String.format(
                        var28,
                        "%.1f",
                        width9
                     );
                     if (font
                        != null) {
                        float width10 = font.getStringWidth(
                           text
                        );
                        int i = width9
                              <= 3.0F
                           ? ColorUtil.rgba(
                              255,
                              90,
                              90,
                              (int)(
                                 255.0F
                                    * f5
                              )
                           )
                           : ColorUtil.rgba(
                              230,
                              230,
                              240,
                              (int)(
                                 255.0F
                                    * f5
                              )
                           );
                        GuiComponent.drawText(
                           matrices,
                           font,
                           text,
                           width6
                              + (
                                    width2
                                       - width10
                                 )
                                 * 0.5F,
                           width
                              + width3
                              + 1.5F,
                           i
                        );
                     }

                     width6 += width4;
                  }
               }
            }
         }
      }
   }

   private String getHpDisplayText(
      LivingEntity entity,
      float f
   ) {
      if (ScoreboardHP.shouldShowUnknownInTargetHud(
         entity
      )) {
         this.isHpUnknown = true;
         return "???";
      } else {
         this.isHpUnknown = false;
         return String.format(
            "%.1f",
            f
         );
      }
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      if (this.nursultanStyleEnabled) {
         this.NursultanStyle(
            event
         );
      } else {
         this.DefaultStyle(
            event
         );
      }

      super.onRender(
         event
      );
   }

   public void DefaultStyle(
      EventRender.Default event
   ) {
      if (mc.player == null) {
         this.headParticles.clear();
         this.lastTargetHurtTime = 0;
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      } else {
         Aura val = ModuleManager.aura;
         boolean flag = mc.currentScreen instanceof ChatScreen;
         LivingEntity target = val
               != null
            ? val.getTarget()
            : null;
         boolean flag2 = flag
            || target
               != null;
         this.alphaAnimation
            .setSpeed(
               flag2
                  ? 9.0F
                  : 5.0F
            );
         this.alphaAnimation
            .update(
               flag2
                  ? 1.0F
                  : 0.0F
            );
         float width = MathHelper.clamp(
            this.alphaAnimation.getValue(), 0.0F, 1.0F
         );
         if (flag2
            )
          {
            this.lastTarget = (LivingEntity)(flag
               ? mc.player
               : target);
         }

         LivingEntity target2 = (LivingEntity)(flag2
            ? (
               flag
                  ? mc.player
                  : target
            )
            : this.lastTarget);
         if (target2
               != null
            && !(
               width
                  <= 0.01F
            )) {
            String var10000 = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName();
            int i;
            if (!var10000.equals("Rainbow")) {
               i = AspectDLC.INSTANCE
                  .themeStorage
                  .getThemes()
                  .getTheme()
                  .color[0];
            } else {
               i = ColorUtil.getThemeColor();
            }

            int i2 = ColorUtil.darken(
               i,
               0.4F
            );
            if (flag2
               )
             {
               this.cachedBarThemeColor = i;
               this.cachedBarThemeColor2 = i2;
            }

            int i3 = flag2
               ? i
               : this.cachedBarThemeColor;
            int i4 = flag2
               ? i2
               : this.cachedBarThemeColor2;
            float width2 = target2.getAbsorptionAmount();
            boolean flag3 = width2
               > 0.0F;
            this.goldenAlphaAnimation
               .setSpeed(
                  flag3
                     ? 9.0F
                     : 5.0F
               );
            this.goldenAlphaAnimation
               .update(
                  flag3
                     ? 1.0F
                     : 0.0F
               );
            float width3 = MathHelper.clamp(
               this.goldenAlphaAnimation.getValue(), 0.0F, 1.0F
            );
            float width4 = Math.max(
               1.0F,
               target2.getMaxHealth()
            );
            float width5 = Math.max(
               1.0F, this.maxAbsorption
            );
            float width6 = ScoreboardHP.getHealth(
               target2
            );
            boolean flag4 = width6
               == -1.0F;
            float width7 = flag4
               ? 0.0F
               : width6;
            this.hpValueAnimation
               .update(
                  width7
               );
            this.ABValueAnimation
               .update(
                  flag2
                     ? width2
                     : 0.0F
               );
            float width8 = MathHelper.clamp(
               this.hpValueAnimation.getValue(),
               0.0F,
               width4
            );
            float width9 = MathHelper.clamp(
               this.ABValueAnimation.getValue(),
               0.0F,
               width5
            );
            float width10 = flag4
               ? 0.0F
               : MathHelper.clamp(
                  width6
                     / width4,
                  0.0F,
                  1.0F
               );
            this.hpAnimation
               .update(
                  width10
               );
            float width11 = MathHelper.clamp(
               this.hpAnimation.getValue(), 0.0F, 1.0F
            );
            if (width11
               < this.hpTrailAnimation.getValue()) {
               this.hpTrailAnimation
                  .update(
                     width11
                  );
            }

            float width12 = MathHelper.clamp(
               this.hpTrailAnimation.getValue(), 0.0F, 1.0F
            );
            boolean flag5 = !flag2;
            if (flag5
               )
             {
               width12 = width11;
            }

            float width13 = this.draggable
               .getX();
            float width14 = this.draggable
               .getY();
            float width15 = 32.0F;
            float width16 = 18.0F;
            float width17 = 3.0F;
            float width18 = (
                     width15
                        - width16
                  )
                  * 0.5F
               - 3.5F;
            float width19 = 5.0F;
            float width20 = 5.0F;
            float width21 = 5.9F;
            float width22 = width14
               + width15
               - 7.5F;
            String name = target2.getName()
               .getString();
            if (flag4
               )
             {
               var10000 = "HP: ???";
            } else {
               var10000 = "HP: "
                  + String.format(
                     "%.1f",
                     width8
                  );
            }

            String name2 = var10000;
            Font font = this.issue(
               13
            );
            Font font2 = this.issue(
               12
            );
            float width23 = width13
               + width17
               + width16
               + width19;
            float width24 = font.getStringWidth(
               name
            );
            float width25 = font2.getStringWidth(
               name2
            );
            if (flag3
               && width3
                  > 0.01F) {
               var10000 = " (+"
                  + String.format(
                     "%.0f",
                     width9
                  )
                  + ")";
            } else {
               var10000 = "";
            }

            String name3 = var10000;
            float width26 = width25
               + font2.getStringWidth(
                  name3
               );
            float width27 = Math.max(
               width24,
               width26
            );
            float width28 = Math.max(
               90.0F,
               width17
                  + width16
                  + width19
                  + width27
                  + width20
            );
            float width29 = width13
               + width17;
            float width30 = width14
               + width18;
            int i5 = (int)(
               255.0F
                  * width
            );
            MatrixStack matrices = event.getContext()
               .getMatrices();
            matrices.push();
            int i6 = 0;

            for (ItemStack stack : target2.getArmorItems()) {
               if (i6
                  < this.armorScratch.length) {
                  this.armorScratch[i6++] = stack;
               }
            }

            ItemStack stack2 = target2.getMainHandStack();
            ItemStack stack3 = target2.getOffHandStack();
            float width31 = 10.0F;
            float width32 = 0.5F
               * width;
            float width33 = 11.0F;
            float width34 = width14
               - width33
               - 2.5F;
            int i7 = 0;

            for (int i8 = i6
                  - 1;
               i8
                  >= 0;
               i8--
            ) {
               if (this.armorScratch[i8]
                     != null
                  && !this.armorScratch[i8]
                     .isEmpty()) {
                  i7++;
               }
            }

            int i9 = 0;
            if (!stack2.isEmpty()
               )
             {
               i9++;
            }

            if (!stack3.isEmpty()
               )
             {
               i9++;
            }

            if (i7
               > 0) {
               float width35 = (float)i7
                  * width31;
               this.drawHudBackground(
                  matrices,
                  width13,
                  width34,
                  width35,
                  width33,
                  i3
               );
               float width36 = 16.0F
                  * width32;
               int i10 = i6
                  - 1;

               for (int i11 = 0;
                  i10
                     >= 0;
                  i10--
               ) {
                  ItemStack stack4 = this.armorScratch[i10];
                  if (stack4
                        != null
                     && !stack4.isEmpty()
                     )
                   {
                     float width37 = width13
                        + (float)i11
                           * width31
                        + (
                              width31
                                 - width36
                           )
                           * 0.5F;
                     float width38 = width34
                        + (
                              width33
                                 - width36
                           )
                           * 0.5F;
                     this.drawTargetHudItem(
                        event,
                        matrices,
                        stack4,
                        width37,
                        width38,
                        width32
                     );
                     i11++;
                  }
               }
            }

            if (i9
               > 0) {
               float width39 = width13
                  + (float)i7
                     * width31
                  + (
                     i7
                           > 0
                        ? 2.5F
                        : 0.0F
                  );
               float width40 = (float)i9
                  * width31;
               this.drawHudBackground(
                  matrices,
                  width39,
                  width34,
                  width40,
                  width33,
                  i3
               );
               float width41 = 16.0F
                  * width32;
               int i12 = 0;
               if (!stack2.isEmpty()
                  )
                {
                  float width42 = width39
                     + (float)i12
                        * width31
                     + (
                           width31
                              - width41
                        )
                        * 0.5F;
                  float width43 = width34
                     + (
                           width33
                              - width41
                        )
                        * 0.5F;
                  this.drawTargetHudItem(
                     event,
                     matrices,
                     stack2,
                     width42,
                     width43,
                     width32
                  );
                  i12++;
               }

               if (!stack3.isEmpty()
                  )
                {
                  float width44 = width39
                     + (float)i12
                        * width31
                     + (
                           width31
                              - width41
                        )
                        * 0.5F;
                  float width45 = width34
                     + (
                           width33
                              - width41
                        )
                        * 0.5F;
                  this.drawTargetHudItem(
                     event,
                     matrices,
                     stack3,
                     width44,
                     width45,
                     width32
                  );
               }
            }

            for (int i13 = 0;
               i13
                  < i6;
               i13++
            ) {
               this.armorScratch[i13] = ItemStack.EMPTY;
            }

            i6 = (int)(
               255.0F * targetHudAlphaMul()
            );
            if (i6
               < 0) {
               i6 = 0;
            }

            if (i6
               > 255) {
               i6 = 255;
            }

            if (i6
               > 0) {
               this.drawHudBlurOrRect(
                  matrices,
                  width13,
                  width14,
                  width28,
                  width15,
                  4.0F,
                  ColorUtil.rgba(
                     7,
                     7,
                     9,
                     i6
                  )
               );
            }

            if (this.headParticlesEnabled) {
               this.updateAndRenderHeadParticles(
                  matrices,
                  target2,
                  width29,
                  width30,
                  width16,
                  width,
                  i3
               );
            } else {
               this.headParticles.clear();
            }

            if (target2 instanceof PlayerEntity player2
               )
             {
               Render2D.drawPlayerHead(
                  matrices,
                  player2.getUuid(),
                  width29,
                  width30,
                  width16,
                  2.0F,
                  width,
                  0.0F
               );
            } else {
               Render2D.drawTargetHudDefaultPlaceholder(
                  matrices,
                  width29,
                  width30,
                  width
               );
               this.issue(14)
                  .drawCenteredString(
                     matrices,
                     "?",
                     width29
                        + width16
                           / 2.0F,
                     width30
                        + 3.0F,
                     ColorUtil.rgba(
                        220,
                        220,
                        220,
                        i5
                     )
                  );
            }

            float width46 = Math.max(
               8.0F,
               width28
                  - width17
                  - width16
                  - width19
                  - width20
            );
            font.drawStringWithFade(
               matrices,
               name,
               width23,
               width14
                  + 5.5F,
               width46,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  i5
               )
            );
            float width47 = width14
               + 14.5F;
            if (flag4
               )
             {
               font2.drawString(
                  matrices,
                  "HP: ",
                  width23,
                  width47,
                  ColorUtil.rgba(
                     232,
                     232,
                     232,
                     i5
                  )
               );
               String var109 = "???";
               font2.drawString(
                  matrices,
                  var109,
                  width23
                     + font2.getStringWidth(
                        "HP: "
                     ),
                  width47,
                  ColorUtil.rgba(
                     255,
                     200,
                     100,
                     i5
                  )
               );
            } else {
               font2.drawString(
                  matrices,
                  "HP: ",
                  width23,
                  width47,
                  ColorUtil.rgba(
                     232,
                     232,
                     232,
                     i5
                  )
               );
               width31 = width23
                  + font2.getStringWidth(
                     "HP: "
                  );
               font2.drawString(
                  matrices,
                  String.format(
                     "%.1f",
                     width8
                  ),
                  width31,
                  width47,
                  ColorUtil.rgba(
                     232,
                     232,
                     232,
                     i5
                  )
               );
               if (width3
                     > 0.01F
                  && !name3.isEmpty()
                  )
                {
                  width32 = width31
                     + font2.getStringWidth(
                        String.format(
                           "%.1f",
                           width8
                        )
                     );
                  int i14 = (int)(
                     255.0F
                        * width3
                        * width
                  );
                  font2.drawGradientStringHorizontal(
                     matrices,
                     name3,
                     width32,
                     width47,
                     ColorUtil.rgba(
                        236,
                        183,
                        39,
                        i14
                     ),
                     ColorUtil.rgba(
                        147,
                        108,
                        16,
                        i14
                     )
                  );
               }
            }

            int i15 = ColorUtil.rgba(
               30,
               30,
               38,
               (int)(
                  200.0F
                     * width
               )
            );
            Render2D.drawRoundedRect(
               matrices,
               width13
                  + 2.0F,
               width22,
               width28
                  - 4.0F,
               width21,
               2.0F,
               i15
            );
            if (!flag4
               )
             {
               if (!flag5
                  && width12
                     > width11
                        + 0.01F) {
                  width32 = (
                        width28
                           - 4.0F
                     )
                     * width12;
                  if (width32
                     > 1.0F) {
                     int i16 = ColorUtil.applyAlpha(
                        ColorUtil.darken(
                           i4,
                           0.6F
                        ),
                        width
                           * 0.55F
                     );
                     Render2D.drawRoundedRect(
                        matrices,
                        width13
                           + 2.0F,
                        width22,
                        width32,
                        width21,
                        2.0F,
                        i16
                     );
                  }
               }

               width32 = (
                     width28
                        - 4.0F
                  )
                  * width11;
               if (width32
                  > 1.0F) {
                  int i17;
                  int i18;
                  if (this.healthBarStyleEnabled) {
                     i7 = ColorUtil.rgba(
                        220,
                        50,
                        50,
                        255
                     );
                     i9 = ColorUtil.rgba(
                        220,
                        200,
                        50,
                        255
                     );
                     int i19 = ColorUtil.rgba(
                        80,
                        220,
                        80,
                        255
                     );
                     if (width11
                        < 0.5F) {
                        float width48 = width11
                           / 0.5F;
                        i18 = this.interpolateColor(
                           i7,
                           i9,
                           width48
                        );
                     } else {
                        float width49 = (
                              width11
                                 - 0.5F
                           )
                           / 0.5F;
                        i18 = this.interpolateColor(
                           i9,
                           i19,
                           width49
                        );
                     }

                     i17 = ColorUtil.darken(
                        i18,
                        0.4F
                     );
                  } else {
                     i17 = i4;
                     i18 = i3;
                  }

                  Render2D.drawGradientRect(
                     matrices,
                     width13
                        + 2.0F,
                     width22,
                     width32,
                     width21,
                     2.0F,
                     ColorUtil.applyAlpha(
                        i17,
                        width
                     ),
                     ColorUtil.applyAlpha(
                        i18,
                        width
                     ),
                     true
                  );
               }

               if (width3
                  > 0.01F) {
                  width33 = MathHelper.clamp(
                     width2
                        / width5,
                     0.0F,
                     1.0F
                  );
                  this.goldenHpAnimation
                     .update(
                        width33
                     );
                  width34 = (
                        width28
                           - 4.0F
                     )
                     * MathHelper.clamp(this.goldenHpAnimation.getValue(), 0.0F, 1.0F);
                  if (width34
                     > 1.0F) {
                     Render2D.drawGradientRect(
                        matrices,
                        width13
                           + 2.0F,
                        width22,
                        width34,
                        width21,
                        2.0F,
                        ColorUtil.applyAlpha(
                           ColorUtil.rgba(
                              147,
                              108,
                              16,
                              255
                           ),
                           width3
                              * width
                        ),
                        ColorUtil.applyAlpha(
                           ColorUtil.rgba(
                              236,
                              183,
                              39,
                              255
                           ),
                           width3
                              * width
                        ),
                        true
                     );
                  }
               }
            } else {
               Render2D.drawRoundedRect(
                  matrices,
                  width13
                     + 2.0F,
                  width22,
                  (
                        width28
                           - 4.0F
                     )
                     * 0.5F,
                  width21,
                  2.0F,
                  ColorUtil.applyAlpha(
                     ColorUtil.rgba(
                        80,
                        80,
                        80,
                        255
                     ),
                     width
                        * 0.5F
                  )
               );
            }

            this.renderEnemyCooldowns(
               event,
               matrices,
               target2,
               width13,
               width14,
               width28,
               width15,
               width,
               i3
            );
            matrices.pop();
            this.draggable
               .setWidth(
                  width28
               );
            this.draggable
               .setHeight(
                  width15
               );
         } else {
            this.headParticles.clear();
            this.lastTargetHurtTime = 0;
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
            this.goldenAlphaAnimation.setValue(0.0F);
            this.ABValueAnimation.setValue(0.0F);
            this.goldenHpAnimation.setValue(0.0F);
            this.goldenHpTrailAnimation.setValue(0.0F);
         }
      }
   }

   public void NursultanStyle(
      EventRender.Default event
   ) {
      if (mc.player == null) {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      } else {
         Aura val = ModuleManager.aura;
         boolean flag = mc.currentScreen instanceof ChatScreen;
         LivingEntity target = val
               != null
            ? val.getTarget()
            : null;
         boolean flag2 = flag
            || target
               != null;
         this.alphaAnimation
            .setSpeed(
               flag2
                  ? 9.0F
                  : 5.0F
            );
         this.alphaAnimation
            .update(
               flag2
                  ? 1.0F
                  : 0.0F
            );
         float width = MathHelper.clamp(
            this.alphaAnimation.getValue(), 0.0F, 1.0F
         );
         if (flag2
            )
          {
            this.lastTarget = (LivingEntity)(flag
               ? mc.player
               : target);
         }

         LivingEntity target2 = (LivingEntity)(flag2
            ? (
               flag
                  ? mc.player
                  : target
            )
            : this.lastTarget);
         if (target2
               != null
            && !(
               width
                  <= 0.01F
            )) {
            String var10000 = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName();
            int i;
            if (!var10000.equals("Rainbow")) {
               i = AspectDLC.INSTANCE
                  .themeStorage
                  .getThemes()
                  .getTheme()
                  .color[0];
            } else {
               i = ColorUtil.getThemeColor();
            }

            int i2 = ColorUtil.darken(
               i,
               0.5F
            );
            float width2 = this.draggable
               .getX();
            float width3 = this.draggable
               .getY();
            float width4 = 112.0F;
            float width5 = 44.0F;
            float width6 = ScoreboardHP.getHealth(
               target2
            );
            boolean flag3 = width6
               == -1.0F;
            float width7 = Math.max(
               1.0F,
               target2.getMaxHealth()
            );
            float width8 = target2.getAbsorptionAmount();
            boolean flag4 = width8
               > 0.0F;
            this.goldenAlphaAnimation
               .setSpeed(
                  flag4
                     ? 9.0F
                     : 5.0F
               );
            this.goldenAlphaAnimation
               .update(
                  flag4
                     ? 1.0F
                     : 0.0F
               );
            float width9 = MathHelper.clamp(
               this.goldenAlphaAnimation.getValue(), 0.0F, 1.0F
            );
            float width10 = 68.0F;
            float width11 = 4.0F;
            float width12 = flag3
               ? 2.0F
               : MathHelper.clamp(
                  width6
                     / width7
                     * width10,
                  2.0F,
                  width10
               );
            this.nursultanAnimatedHealth = this.nursultanAnimatedHealth
               + (
                     width12
                        - this.nursultanAnimatedHealth
                  )
                  * 0.1F;
            this.nursultanAnimatedHealth = MathHelper.clamp(
               this.nursultanAnimatedHealth,
               2.0F,
               width10
            );
            if (this.nursultanAnimatedHealth < this.nursultanTrailHealth) {
               this.nursultanTrailHealth = this.nursultanTrailHealth + (this.nursultanAnimatedHealth - this.nursultanTrailHealth) * 0.05F;
            }

            this.nursultanTrailHealth = MathHelper.clamp(
               this.nursultanTrailHealth,
               2.0F,
               width10
            );
            float width13 = Math.max(
               1.0F, this.maxAbsorption
            );
            float width14 = MathHelper.clamp(
               width8
                  / width13,
               0.0F,
               1.0F
            );
            this.goldenHpAnimation
               .update(
                  width14
               );
            float width15 = MathHelper.clamp(
                  this.goldenHpAnimation.getValue(), 0.0F, 1.0F
               )
               * width10;
            int i3 = (int)(
               255.0F
                  * width
            );
            MatrixStack matrices = event.getContext()
               .getMatrices();
            matrices.push();
            float width16 = width2
               + width4
                  / 2.0F;
            float width17 = width3
               + width5
                  / 2.0F;
            matrices.translate(
               width16,
               width17,
               0.0F
            );
            matrices.scale(
               width,
               width,
               1.0F
            );
            matrices.translate(
               -width16,
               -width17,
               0.0F
            );
            if (this.isBlurEnabled()) {
               BLUR.render(
                  matrices,
                  width2,
                  width3,
                  width4,
                  width5,
                  4.0F,
                  8.0F
               );
            }

            float width18 = width
               * targetHudAlphaMul();
            if (width18
               > 0.001F) {
               Render2D.drawDefaultHudPanel(
                  matrices,
                  width2,
                  width3,
                  width4,
                  width5,
                  4.0F,
                  4.5F,
                  ColorUtil.applyAlpha(
                     ColorUtil.rgba(
                        50,
                        50,
                        50,
                        255
                     ),
                     width18
                  ),
                  ColorUtil.applyAlpha(
                     ColorUtil.darken(
                        i,
                        0.15F
                     ),
                     width18
                  ),
                  ColorUtil.applyAlpha(
                     ColorUtil.darken(
                        i,
                        0.05F
                     ),
                     width18
                  ),
                  true,
                  this.getHudRectStyle()
               );
            }

            float width19 = width2
               + 6.0F;
            float width20 = width3
               + (
                     width5
                        - 22.0F
                  )
                  / 2.0F;
            if (target2 instanceof PlayerEntity
               )
             {
               Render2D.drawPlayerHead(
                  matrices,
                  target2.getUuid(),
                  width19,
                  width20,
                  22.0F,
                  2.0F,
                  width,
                  0.0F
               );
            } else {
               Render2D.drawTargetHudDefaultPlaceholder(
                  matrices,
                  width19,
                  width20,
                  width
               );
               this.issue(20)
                  .drawCenteredString(
                     matrices,
                     "?",
                     width19
                        + 11.0F,
                     width20
                        + 5.0F,
                     ColorUtil.rgba(
                        220,
                        220,
                        220,
                        i3
                     )
                  );
            }

            String name = target2.getName()
               .getString();
            float width21 = 50.0F;
            if (this.issue(13)
                  .getWidth(
                     name
                  )
               > width21
               )
             {
               while (
                  this.issue(13)
                           .getWidth(
                              name
                                 + "..."
                           )
                        > width21
                     && name.length()
                        > 0
               ) {
                  name = name.substring(
                     0,
                     name.length()
                        - 1
                  );
               }

               name = name
                  + "...";
            }

            this.issue(13)
               .drawString(
                  matrices,
                  name,
                  width2
                     + 35.0F,
                  width3
                     + 7.0F,
                  ColorUtil.rgba(
                     255,
                     255,
                     255,
                     i3
                  )
               );
            if (flag3
               )
             {
               var10000 = "???";
            } else {
               var10000 = Math.round(
                     width6
                  )
                  + ".0";
            }

            String name2 = var10000;
            Font font = this.issue(
               12
            );
            float width22 = font.getStringWidth(
               name2
            );
            font.drawString(
               matrices,
               name2,
               width2
                  + width4
                  - 7.0F
                  - width22,
               width3
                  + 7.0F,
               ColorUtil.rgba(
                  255,
                  255,
                  255,
                  i3
               )
            );
            if (width9
                  > 0.01F
               && !flag3
               )
             {
               String name3 = "+"
                  + Math.round(
                     width8
                  );
               float width23 = width2
                  + width4
                  - 7.0F
                  - width22
                  - font.getStringWidth(
                     name3
                  )
                  - 2.0F;
               int i4 = (int)(
                  255.0F
                     * width9
                     * width
               );
               font.drawGradientStringHorizontal(
                  matrices,
                  name3,
                  width23,
                  width3
                     + 7.0F,
                  ColorUtil.rgba(
                     236,
                     183,
                     39,
                     i4
                  ),
                  ColorUtil.rgba(
                     147,
                     108,
                     16,
                     i4
                  )
               );
            }

            float width24 = width3
               + 18.0F;
            Font font2 = Fonts.getFont(
               "iconnew", 11
            );
            if (font2
               != null) {
               font2.drawGradientStringHorizontal(
                  matrices,
                  "m",
                  width2
                     + width4
                     - 9.0F,
                  width24,
                  ColorUtil.applyAlpha(
                     i,
                     (float)i3
                  ),
                  ColorUtil.applyAlpha(
                     i2,
                     (float)i3
                  )
               );
            }

            float width25 = 8.0F;
            float width26 = 2.0F;
            float width27 = 0.5F
               * width;
            float width28 = width24
               - 1.0F;
            int i5 = 0;

            for (ItemStack stack : target2.getArmorItems()) {
               if (i5
                  < this.armorScratch.length) {
                  this.armorScratch[i5++] = stack;
               }
            }

            for (int i6 = 0;
               i6
                  < 4;
               i6++
            ) {
               ItemStack stack2 = i5
                     > 0
                  ? this.armorScratch[3
                     - i6]
                  : ItemStack.EMPTY;
               if (stack2
                  == null) {
                  stack2 = ItemStack.EMPTY;
               }

               float width29 = width2
                  + 35.0F
                  + (float)i6
                     * (
                        width25
                           + width26
                     );
               if (!stack2.isEmpty()
                  )
                {
                  this.drawTargetHudItem(
                     event,
                     matrices,
                     stack2,
                     width29,
                     width28,
                     width27
                  );
               }

               if (i5
                  > 0) {
                  this.armorScratch[3
                     - i6] = ItemStack.EMPTY;
               }
            }

            float width30 = width3
               + width5
               - 8.0F;
            Render2D.drawRoundedRect(
               matrices,
               width2
                  + 34.0F,
               width30,
               width10,
               width11,
               1.5F,
               ColorUtil.rgba(
                  38,
                  34,
                  50,
                  (int)(
                     235.0F
                        * width
                  )
               )
            );
            if (this.nursultanTrailHealth > this.nursultanAnimatedHealth + 1.0F) {
               Render2D.drawGradientRect(
                  matrices,
                  width2
                     + 34.0F,
                  width30,
                  this.nursultanTrailHealth,
                  width11,
                  1.5F,
                  ColorUtil.applyAlpha(
                     ColorUtil.darken(
                        i2,
                        0.5F
                     ),
                     width
                        * 0.6F
                  ),
                  ColorUtil.applyAlpha(
                     ColorUtil.darken(
                        i,
                        0.5F
                     ),
                     width
                        * 0.6F
                  ),
                  true
               );
            }

            if (this.nursultanAnimatedHealth > 1.0F) {
               int i7;
               int i8;
               if (this.healthBarStyleEnabled) {
                  float width31 = flag3
                     ? 0.0F
                     : MathHelper.clamp(
                        this.nursultanAnimatedHealth
                           / width10,
                        0.0F,
                        1.0F
                     );
                  int i9 = ColorUtil.rgba(
                     220,
                     50,
                     50,
                     255
                  );
                  int i10 = ColorUtil.rgba(
                     220,
                     200,
                     50,
                     255
                  );
                  int i11 = ColorUtil.rgba(
                     80,
                     220,
                     80,
                     255
                  );
                  if (width31
                     < 0.5F) {
                     float width32 = width31
                        / 0.5F;
                     i8 = this.interpolateColor(
                        i9,
                        i10,
                        width32
                     );
                  } else {
                     float width33 = (
                           width31
                              - 0.5F
                        )
                        / 0.5F;
                     i8 = this.interpolateColor(
                        i10,
                        i11,
                        width33
                     );
                  }

                  i7 = ColorUtil.darken(
                     i8,
                     0.4F
                  );
               } else {
                  i7 = i2;
                  i8 = i;
               }

               Render2D.drawGradientRect(
                  matrices,
                  width2
                     + 34.0F,
                  width30,
                  this.nursultanAnimatedHealth,
                  width11,
                  1.5F,
                  ColorUtil.applyAlpha(
                     i7,
                     width
                  ),
                  ColorUtil.applyAlpha(
                     i8,
                     width
                  ),
                  true
               );
            }

            if (width9
                  > 0.01F
               && width15
                  > 1.0F) {
               float width34 = width9
                  * width;
               Render2D.drawGradientRect(
                  matrices,
                  width2
                     + 34.0F,
                  width30,
                  width15,
                  width11,
                  1.5F,
                  ColorUtil.applyAlpha(
                     ColorUtil.rgba(
                        147,
                        108,
                        16,
                        255
                     ),
                     width34
                  ),
                  ColorUtil.applyAlpha(
                     ColorUtil.rgba(
                        236,
                        183,
                        39,
                        255
                     ),
                     width34
                  ),
                  true
               );
            }

            this.renderEnemyCooldowns(
               event,
               matrices,
               target2,
               width2,
               width3,
               width4,
               width5,
               width,
               i
            );
            matrices.pop();
            this.draggable
               .setWidth(
                  width4
               );
            this.draggable
               .setHeight(
                  width5
               );
         } else {
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
         }
      }
   }

   public void WaveStyle(
      EventRender.Default event
   ) {
      if (mc.player == null) {
         this.draggable.setWidth(0.0F);
         this.draggable.setHeight(0.0F);
      } else {
         Aura val = ModuleManager.aura;
         boolean flag = mc.currentScreen instanceof ChatScreen;
         LivingEntity target = val
               != null
            ? val.getTarget()
            : null;
         boolean flag2 = flag
            || target
               != null;
         this.alphaAnimation
            .update(
               flag2
                  ? 1.0F
                  : 0.0F
            );
         if (flag2
            && !this.wasVisible) {
            this.scaleAnimation.setValue(0.0F);
            this.hideScaleAnimation.setValue(1.0F);
            this.wasVisible = true;
         } else if (!flag2
            && this.wasVisible) {
            this.hideScaleAnimation.setValue(1.0F);
            this.wasVisible = false;
         }

         if (flag2
            )
          {
            this.scaleAnimation.update(1.0F);
            this.lastTarget = (LivingEntity)(flag
               ? mc.player
               : target);
         } else {
            this.hideScaleAnimation.update(0.0F);
         }

         float yaw;
         if (flag2
            )
          {
            yaw = MathHelper.clamp(
               this.scaleAnimation.getValue(), 0.0F, 1.0F
            );
         } else {
            yaw = MathHelper.clamp(
               this.hideScaleAnimation.getValue(), 0.0F, 1.0F
            );
         }

         LivingEntity target2 = (LivingEntity)(flag2
            ? (
               flag
                  ? mc.player
                  : target
            )
            : this.lastTarget);
         if (target2
               != null
            && !(
               yaw
                  <= 0.01F
            )) {
            float yaw2 = this.draggable
               .getX();
            float yaw3 = this.draggable
               .getY();
            float yaw4 = 3.0F;
            float yaw5 = 110.0F;
            float yaw6 = 46.0F;
            float yaw7 = ScoreboardHP.getHealth(
               target2
            );
            boolean flag3 = yaw7
               == -1.0F;
            float yaw8 = Math.max(
               1.0F,
               target2.getMaxHealth()
            );
            float yaw9 = flag3
               ? 0.0F
               : yaw7;
            this.hpValueAnimation
               .update(
                  yaw9
               );
            float yaw10 = MathHelper.clamp(
               this.hpValueAnimation.getValue(),
               0.0F,
               yaw8
            );
            float yaw11 = flag3
               ? 0.0F
               : MathHelper.clamp(
                  yaw7
                     / yaw8,
                  0.0F,
                  1.0F
               );
            this.hpAnimation
               .update(
                  yaw11
               );
            float yaw12 = MathHelper.clamp(
               this.hpAnimation.getValue(), 0.0F, 1.0F
            );
            if (yaw12
               > this.hpTrailAnimation.getValue()) {
               this.hpTrailAnimation
                  .setValue(
                     yaw12
                  );
            } else {
               this.hpTrailAnimation
                  .update(
                     yaw12
                  );
            }

            MatrixStack matrices = event.getContext()
               .getMatrices();
            matrices.push();
            this.applyHudTransform(
               matrices,
               yaw2,
               yaw3,
               yaw5,
               yaw6,
               yaw,
               yaw
            );
            float yaw13 = yaw;
            int i = (int)(
               255.0F
                  * yaw
            );
            float yaw14 = yaw6
               - yaw4
                  * 2.0F
               - 4.0F;
            Render2D.drawTargetHudWaveFrame(
               matrices,
               yaw2,
               yaw3,
               yaw5,
               yaw6,
               yaw4,
               yaw14,
               yaw
            );
            matrices.pop();
            int i2 = (int)(
               yaw2
                  + yaw4
                  + 3.0F
            );
            int i3 = (int)(
               yaw3
                  + yaw4
                  + 3.0F
            );
            int i4 = (int)(
               yaw2
                  + yaw4
                  + 3.0F
                  + yaw14
                  - 2.0F
            );
            int i5 = (int)(
               yaw3
                  + yaw4
                  + 3.0F
                  + yaw14
                  - 2.0F
            );
            int i6 = (int)(
               15.0F
                  * yaw
            );
            float yaw15 = yaw2
               + yaw4
               + 3.0F
               + (
                     yaw14
                        - 2.0F
                  )
                  / 2.0F;
            float yaw16 = yaw3
               + yaw4
               + 3.0F
               + (
                     yaw14
                        - 2.0F
                  )
                  / 3.0F;
            float yaw17 = target2.bodyYaw;
            float yaw18 = target2.getPitch();
            double dist = Math.toRadians(
               (double)(
                  yaw17
                     + 180.0F
               )
            );
            float yaw19 = yaw15
               + (float)(
                  Math.sin(
                        dist
                     )
                     * 50.0
               );
            float yaw20 = yaw16
               - yaw18;
            if (yaw
               > 0.5F) {
               InventoryScreen.drawEntity(
                  event.getContext(),
                  i2,
                  i3,
                  i4,
                  i5,
                  i6,
                  0.0F,
                  yaw19,
                  yaw20,
                  target2
               );
            }

            matrices.push();
            this.applyHudTransform(
               matrices,
               yaw2,
               yaw3,
               yaw5,
               yaw6,
               yaw,
               yaw
            );
            String name = target2.getName()
               .getString();
            float yaw21 = yaw2
               + yaw4
               + yaw14
               + 6.0F;
            float yaw22 = Math.max(
               8.0F,
               yaw2
                  + yaw5
                  - yaw4
                  - 4.0F
                  - yaw21
            );
            this.issue(14)
               .drawStringWithFade(
                  matrices,
                  name,
                  yaw21,
                  yaw3
                     + yaw4
                     + 5.0F,
                  yaw22,
                  ColorUtil.rgba(
                     255,
                     255,
                     255,
                     i
                  )
               );
            if (flag3
               )
             {
               this.issue(14)
                  .draw(
                     matrices,
                     "HP: Неизвестно | Dist: "
                        + (int)target2.distanceTo(
                           mc.player
                        ),
                     yaw21,
                     yaw3
                        + yaw4
                        + 20.0F,
                     ColorUtil.rgba(
                        255,
                        200,
                        100,
                        i
                     )
                  );
            } else {
               Font var10000 = this.issue(14);
               var10000.draw(
                  matrices,
                  "HP: "
                     + String.format(
                        "%.1f",
                        yaw10
                     )
                     + " | Dist: "
                     + (int)target2.distanceTo(
                        mc.player
                     ),
                  yaw21,
                  yaw3
                     + yaw4
                     + 20.0F,
                  ColorUtil.rgba(
                     255,
                     255,
                     255,
                     i
                  )
               );
            }

            float yaw23 = yaw21;
            float yaw24 = yaw3
               + yaw4
               + 15.0F;
            float yaw25 = 5.0F;
            float yaw26 = 0.5F;
            int i7 = 10;
            float yaw27 = yaw8
               / (float)i7;
            float yaw28 = flag3
               ? 0.0F
               : yaw7;
            int i8;
            if (flag3
               )
             {
               i8 = ColorUtil.rgba(
                  128,
                  128,
                  128,
                  i
               );
            } else if (yaw7
               <= yaw8
                  * 0.25F) {
               i8 = ColorUtil.rgba(
                  255,
                  50,
                  50,
                  i
               );
            } else if (yaw7
               <= yaw8
                  * 0.5F) {
               i8 = ColorUtil.rgba(
                  255,
                  220,
                  0,
                  i
               );
            } else {
               i8 = ColorUtil.rgba(
                  0,
                  255,
                  0,
                  i
               );
            }

            int i9 = ColorUtil.applyAlpha(
               i8,
               yaw
                  * 0.5F
            );

            for (int i10 = 0;
               i10
                  < i7;
               i10++
            ) {
               float yaw29 = yaw23
                  + (float)i10
                     * (
                        yaw25
                           + yaw26
                     );
               Render2D.drawTargetHudHeartBase(
                  matrices,
                  yaw29,
                  yaw24
                     - 3.0F,
                  yaw13
               );
               if (yaw28
                     > 0.0F
                  && !flag3
                  )
                {
                  float yaw30 = MathHelper.clamp(
                     yaw28
                        / yaw27,
                     0.0F,
                     1.0F
                  );
                  float yaw31 = yaw25
                     * yaw30;
                  if (yaw31
                     > 0.0F) {
                     Render2D.drawTargetHudHeartFill(
                        matrices,
                        yaw29,
                        yaw24
                           - 3.0F,
                        yaw31,
                        i8,
                        i9
                     );
                  }

                  yaw28 -= yaw27;
               }
            }

            float yaw32 = yaw21
               - 1.0F;
            float yaw33 = yaw3
               + yaw4
               + 28.0F;
            float yaw34 = 10.0F;
            String var70 = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName();
            int i11 = !var70.equals(
                  "Rainbow"
               )
               ? AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().color[0]
               : ColorUtil.getThemeColor();
            int i12 = ColorUtil.applyAlpha(
               ColorUtil.rgba(
                  50,
                  50,
                  50,
                  255
               ),
               yaw13
            );
            int i13 = ColorUtil.applyAlpha(
               ColorUtil.darken(
                  i11,
                  0.15F
               ),
               yaw13
            );
            int i14 = ColorUtil.applyAlpha(
               ColorUtil.darken(
                  i11,
                  0.05F
               ),
               yaw13
            );
            int i15 = this.collectDisplayItems(
               target2
            );
            if (i15
               > 0) {
               float yaw35 = yaw32
                  - 0.85F;
               float yaw36 = yaw33
                  - 0.85F;
               float yaw37 = (float)(
                        i15
                           - 1
                     )
                     * yaw34
                  + 9.8F;
               float var61 = 9.8F;
            }

            float yaw38 = 0.5F;

            for (int i16 = 0;
               i16
                  < i15;
               i16++
            ) {
               ItemStack stack = this.displayItems[i16];
               if (!stack.isEmpty()
                  )
                {
                  float yaw39 = yaw32
                     + (float)i16
                        * yaw34;
                  this.drawTargetHudItem(
                     event,
                     matrices,
                     stack,
                     yaw39,
                     yaw33,
                     yaw38
                  );
               }

               this.displayItems[i16] = ItemStack.EMPTY;
            }

            this.renderEnemyCooldowns(
               event,
               matrices,
               target2,
               yaw2,
               yaw3,
               yaw5,
               yaw6,
               yaw13,
               i11
            );
            matrices.pop();
            this.draggable
               .setWidth(
                  yaw5
               );
            this.draggable
               .setHeight(
                  yaw6
               );
         } else {
            this.draggable.setWidth(0.0F);
            this.draggable.setHeight(0.0F);
         }
      }
   }
}
