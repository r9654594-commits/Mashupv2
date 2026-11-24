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


import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class NotificationsHud extends HudElement {
   private static final float PILL_H = 16.0F;
   private static final float RADIUS = 7.0F;
   private static final float FONT_SZ = 13.0F;
   private static final int COLOR_SUCCESS = ColorUtil.rgba(70, 220, 70, 255);
   private static final int COLOR_ERROR = ColorUtil.rgba(220, 70, 70, 255);
   private static final int COLOR_WARNING = ColorUtil.rgba(255, 170, 0, 255);
   private static final int COLOR_INFO = ColorUtil.rgba(66, 165, 245, 255);
   private static final int COLOR_DEFAULT = ColorUtil.rgba(164, 66, 255, 255);
   private final UpdateHelper widthAnim = new UpdateHelper(60.0F, 10.0F, EasingFunctions.CUBIC_OUT);
   private final UpdateHelper scaleAnim = new UpdateHelper(0.0F, 8.0F, EasingFunctions.BACK_OUT);
   private String lastNotifText = null;
   private boolean lastNotifEnabled = false;
   private long lastNotifExpiry = 0L;
   private static final Pattern TIMER_RE = Pattern.compile("(\\d+)(?!.*\\d)");

   public NotificationsHud(
      Draggable val
   ) {
      super(
         val
      );
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      if (mc != null && mc.player != null && mc.world != null) {
         MatrixStack matrices = event.getContext()
            .getMatrices();
         int color = mc.getWindow()
            .getScaledWidth();
         Font font = Fonts.getFont(
            "nunito", 13
         );
         Font font2 = Fonts.getFont(
            "nunito", 12
         );
         Font font3 = Fonts.getFont(
            "iconnew", 14
         );
         if (font
               != null
            && font2
               != null) {
            int color2 = this.getThemeColor();
            this.refreshNotifCache();
            long time = System.currentTimeMillis();
            boolean flag = this.lastNotifText
                  != null
               && time
                  < this.lastNotifExpiry;
            ClientBossBar val = flag
               ? null
               : this.getPvpBossBar();
            String name;
            int color3;
            if (flag
               )
             {
               name = this.lastNotifText;
               color3 = this.lastNotifEnabled
                  ? COLOR_SUCCESS
                  : COLOR_ERROR;
            } else if (val
               != null) {
               name = val.getName()
                  .getString();
               color3 = ColorUtil.rgba(
                  200,
                  50,
                  50,
                  255
               );
            } else {
               name = "AspectDLC Client";
               color3 = color2;
            }

            float width = 6.0F;
            float width2 = 6.0F;
            float width3 = width2
               + width
               + 4.0F
               + font.getStringWidth(
                  name
               )
               + width2;
            this.widthAnim
               .update(
                  width3
               );
            this.scaleAnim.update(1.0F);
            float width4 = this.scaleAnim
               .getValue();
            float width5 = Math.min(
               1.0F,
               width4
                  * 1.5F
            );
            float width6 = this.widthAnim
               .getValue();
            float width7 = 16.0F;
            LocalTime var37 = LocalTime.now();
            String name2 = var37.format(
               DateTimeFormatter.ofPattern("HH:mm")
            );
            float width8 = font.getStringWidth(
               name2
            );
            int color4 = this.getPlayerPing();
            boolean flag2 = !mc.isInSingleplayer()
               && color4
                  < 999;
            float width9 = flag2
               ? 14.5F
               : 0.0F;
            float width10 = width8
               + 4.0F
               + width6
               + (
                  flag2
                     ? 4.0F
                        + width9
                     : 0.0F
               );
            float width11 = (float)color
               / 2.0F;
            float width12 = 5.0F
               + width7
                  / 2.0F;
            matrices.push();
            matrices.translate(
               width11,
               width12,
               0.0F
            );
            matrices.scale(
               width4,
               width4,
               1.0F
            );
            matrices.translate(
               -width11,
               -width12,
               0.0F
            );
            float width13 = width11
               - width10
                  / 2.0F;
            float width14 = width13
               + width8
               + 4.0F;
            float width15 = 5.0F;
            float width16 = width15
               + (
                     width7
                        - font.getHeight()
                  )
                  / 2.0F;
            font.drawString(
               matrices,
               name2,
               width13,
               width16,
               ColorUtil.setAlphaColor(
                  -1,
                  (int)(
                     255.0F
                        * width5
                  )
               )
            );
            int color5 = ColorUtil.rgba(
               10,
               10,
               10,
               (int)(
                  230.0F
                     * width5
               )
            );
            if (Render2D.isLiquidGlassHudActive()) {
               Render2D.drawHudLiquidGlass(
                  matrices,
                  width14,
                  width15,
                  width6,
                  width7,
                  7.0F,
                  color2,
                  false,
                  this.getHudRectStyle()
               );
            } else {
               Render2D.drawBlur(
                  matrices,
                  width14,
                  width15,
                  width6,
                  width7,
                  7.0F,
                  color5
               );
               Render2D.drawRoundedRect(
                  matrices,
                  width14,
                  width15,
                  width6,
                  width7,
                  7.0F,
                  color5
               );
            }

            if (flag
               )
             {
               this.renderNotif(
                  matrices,
                  font,
                  width14,
                  width15,
                  width6,
                  width7,
                  name,
                  color3,
                  width5
               );
            } else if (val
               != null) {
               this.renderBossBar(
                  matrices,
                  font,
                  font2,
                  width14,
                  width15,
                  width6,
                  width7,
                  val,
                  width5,
                  color2
               );
            } else {
               this.renderDefault(
                  matrices,
                  font,
                  width14,
                  width15,
                  width6,
                  width7,
                  name,
                  color3,
                  width5
               );
            }

            if (flag2
               )
             {
               this.renderPing(
                  matrices,
                  width14
                     + width6
                     + 4.0F,
                  width15,
                  width7,
                  color4,
                  width5
               );
            }

            matrices.pop();
            this.draggable
               .setWidth(
                  width10
               );
            this.draggable
               .setHeight(
                  width7
               );
            this.draggable
               .setX(
                  width13
               );
            this.draggable
               .setY(
                  width15
               );
            super.onRender(
               event
            );
         }
      }
   }

   private void renderDefault(
      MatrixStack matrices,
      Font font,
      float f,
      float f2,
      float f3,
      float f4,
      String str,
      int n,
      float f5
   ) {
      float height = 6.0F;
      float height2 = f
         + 6.0F;
      float height3 = f2
         + (
               f4
                  - height
            )
            / 2.0F;
      Render2D.drawRoundedRect(
         matrices,
         height2,
         height3,
         height,
         height,
         height
            / 2.0F,
         ColorUtil.setAlphaColor(
            n,
            (int)(
               255.0F
                  * f5
            )
         )
      );
      font.drawString(
         matrices,
         str,
         height2
            + height
            + 4.0F,
         f2
            + (
                  f4
                     - font.getHeight()
               )
               / 2.0F,
         ColorUtil.setAlphaColor(
            -1,
            (int)(
               255.0F
                  * f5
            )
         )
      );
   }

   private void renderNotif(
      MatrixStack matrices,
      Font font,
      float f,
      float f2,
      float f3,
      float f4,
      String str,
      int n,
      float f5
   ) {
      float height = 7.0F;
      float height2 = f
         + 6.0F;
      float height3 = f2
         + (
               f4
                  - height
            )
            / 2.0F;
      Render2D.drawRoundedRect(
         matrices,
         height2,
         height3,
         height,
         height,
         3.5F,
         ColorUtil.setAlphaColor(
            n,
            (int)(
               255.0F
                  * f5
            )
         )
      );
      font.drawString(
         matrices,
         str,
         height2
            + height
            + 5.0F,
         f2
            + (
                  f4
                     - font.getHeight()
               )
               / 2.0F,
         ColorUtil.setAlphaColor(
            -1,
            (int)(
               255.0F
                  * f5
            )
         )
      );
   }

   private void renderBossBar(
      MatrixStack matrices,
      Font font,
      Font font2,
      float f,
      float f2,
      float f3,
      float f4,
      ClientBossBar val,
      float f5,
      int n
   ) {
      String name = val.getName()
         .getString();
      Matcher val2 = TIMER_RE.matcher(
         name
      );
      String var10000;
      if (val2.find()
         )
       {
         var10000 = val2.group(
               1
            )
            + "s";
      } else {
         var10000 = "??s";
      }

      String name2 = var10000;
      var10000 = name.replaceAll(
         ",?\\s*до конца.*$", ""
      );
      String var23 = "Телепортация.*";
      String name3 = var10000.replaceAll(
            var23, "Телепортация"
         )
         .trim();
      float width = font.getStringWidth(
         name2
      );
      float width2 = font.getStringWidth(
         name3
      );
      float width3 = width
         + 8.0F;
      float width4 = f
         + (
               f3
                  - width3
                  - 4.0F
                  - width2
            )
            / 2.0F;
      float width5 = f2
         + (
               f4
                  - font.getHeight()
            )
            / 2.0F;
      Render2D.drawRoundedRect(
         matrices,
         width4
            - 2.0F,
         f2
            + 2.0F,
         width3,
         f4
            - 4.0F,
         3.0F,
         ColorUtil.rgba(
            200,
            50,
            50,
            (int)(
               200.0F
                  * f5
            )
         )
      );
      font.drawString(
         matrices,
         name2,
         width4
            + (
                  width3
                     - width
               )
               / 2.0F
            - 2.0F,
         width5,
         ColorUtil.setAlphaColor(
            -1,
            (int)(
               255.0F
                  * f5
            )
         )
      );
      font.drawString(
         matrices,
         name3,
         width4
            + width3
            + 4.0F,
         width5,
         ColorUtil.setAlphaColor(
            -1,
            (int)(
               255.0F
                  * f5
            )
         )
      );
   }

   private void renderPing(
      MatrixStack matrices,
      float f,
      float f2,
      float f3,
      int n,
      float f4
   ) {
      int i = n
            <= 60
         ? 4
         : (
            n
                  <= 100
               ? 3
               : (
                  n
                        <= 150
                     ? 2
                     : (
                        n
                              <= 250
                           ? 1
                           : 0
                     )
               )
         );
      float[] var10000 = new float[4];
      var10000[0] = 3.0F;
      var10000[1] = 5.0F;
      var10000[2] = 7.0F;
      var10000[3] = 9.0F;
      float[] floats = var10000;
      float x = 2.5F;
      float x2 = 1.5F;
      float x3 = f2
         + f3
         - 2.0F;

      for (int i2 = 0;
         i2
            < 4;
         i2++
      ) {
         float x4 = floats[i2];
         int i3 = i2
               < i
            ? ColorUtil.rgba(
               255,
               255,
               255,
               (int)(
                  210.0F
                     * f4
               )
            )
            : ColorUtil.rgba(
               255,
               255,
               255,
               (int)(
                  45.0F
                     * f4
               )
            );
         Render2D.drawRoundedRect(
            matrices,
            f
               + (float)i2
                  * (
                     x
                        + x2
                  ),
            x3
               - x4,
            x,
            x4,
            0.8F,
            i3
         );
      }
   }

   private void refreshNotifCache() {
      List<IscustomHelper> list = PushHelper.getActive();
      if (!list.isEmpty()
         )
       {
         IscustomHelper val = list.get(
            list.size()
               - 1
         );
         String name = val.isCustom()
            ? val.customText
            : val.moduleName;
         if (!val.isCustom()
            )
          {
            String var10000 = val.moduleName;
            String var4;
            if (val.enabled
               )
             {
               var4 = " вкл";
            } else {
               var4 = " выкл";
            }

            name = var10000
               + var4;
         }

         this.lastNotifText = name;
         this.lastNotifEnabled = val.enabled;
         this.lastNotifExpiry = val.startTime
            + 2500L
            + 500L;
      }

      if (this.lastNotifText != null && System.currentTimeMillis() >= this.lastNotifExpiry) {
         this.lastNotifText = null;
      }
   }

   private ClientBossBar getPvpBossBar() {
      if (mc.inGameHud == null) {
         return null;
      } else {
         BossBarHud val = mc.inGameHud
            .getBossBarHud();
         if (val
            == null) {
            return null;
         } else {
            try {
               Field val2 = BossBarHud.class
                  .getDeclaredField("bossBars");
               val2.setAccessible(
                  true
               );
               Map val3 = (Map<UUID, ClientBossBar>)val2.get(
                  val
               );
               if (val3
                  == null) {
                  return null;
               } else {
                  Iterator var4 = val3.values()
                     .iterator();

                  ClientBossBar val4;
                  String name;
                  do {
                     if (!var4.hasNext()) {
                        return null;
                     }

                     val4 = (ClientBossBar)var4.next();
                     name = val4.getName()
                        .getString()
                        .toLowerCase();
                     if (name.contains(
                        "pvp"
                     )) {
                        break;
                     }

                     if (name.contains(
                        "пвп"
                     )) {
                        break;
                     }

                     if (name.contains(
                        "кт"
                     )) {
                        break;
                     }

                     if (name.contains(
                        "kt"
                     )) {
                        break;
                     }

                     if (name.contains(
                        "телепортация"
                     )) {
                        break;
                     }

                  } while (
                     !name.contains(
                        "до конца"
                     )
                  );

                  return val4;
               }
            } catch (Exception var7) {
               return null;
            }
         }
      }
   }

   private int getPlayerPing() {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         PlayerListEntry val = mc.getNetworkHandler()
            .getPlayerListEntry(mc.player.getUuid());
         return val
               != null
            ? val.getLatency()
            : 0;
      } else {
         return 0;
      }
   }

   private int getThemeColor() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null) {
         ThemeColors color2 = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         if (color2
            == null) {
            return ColorUtil.getThemeColor();
         } else {
            if ("Rainbow"
               .equals(
                  color2.getName()
               )) {
               return ColorUtil.getThemeColor();
            } else {
               return color2.color
                        != null
                     && color2.color.length
                        > 0
                  ? color2.color[0]
                  : ColorUtil.getThemeColor();
            }
         }
      } else {
         return ColorUtil.getThemeColor();
      }
   }

   public static void checkArmorDurability() {
      MinecraftClient val = MinecraftClient.getInstance();
      if (val
            != null
         && val.player
            != null) {
         EquipmentSlot[] var10000 = new EquipmentSlot[4];
         var10000[0] = EquipmentSlot.FEET;
         var10000[1] = EquipmentSlot.LEGS;
         var10000[2] = EquipmentSlot.CHEST;
         var10000[3] = EquipmentSlot.HEAD;
         EquipmentSlot[] val2 = var10000;
         String[] var6 = new String[4];
         var6[0] = "Ботинки";
         var6[1] = "Поножи";
         int var10002 = 2;
         var6[var10002] = "Нагрудник";
         var10002 = 3;
         var6[var10002] = "Шлем";
         String[] val3 = var6;

         for (int i = 0;
            i
               < val2.length;
            i++
         ) {
            ItemStack stack = val.player
               .getEquippedStack(
                  val2[i]
               );
            if (!stack.isEmpty()
               && stack.isDamageable()
               )
             {
               float x = (float)(
                     stack.getMaxDamage()
                        - stack.getDamage()
                  )
                  / (float)stack.getMaxDamage();
               if (x
                     <= 0.1F
                  && x
                     > 0.0F) {
                  PushHelper.pushCustom(
                     val3[i]
                        + " — низкая прочность!",
                     "!"
                  );
                  return;
               }
            }
         }
      }
   }
}
