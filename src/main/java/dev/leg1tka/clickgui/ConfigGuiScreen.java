package dev.leg1tka.clickgui;
import dev.leg1tka.animation.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;



import dev.leg1tka.modules.render.*;
import dev.leg1tka.api.events.implement.EventRender;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ConfigGuiScreen extends Screen implements IMinecraft {
   public static boolean opened = false;
   private static final Identifier BG = Identifier.of("aspectdlc", "textures/preview/background.png");
   private static final int PANEL_W = 220;
   private static final int PAD = 8;
   private boolean AspectDLC$leftPressed;
   private int panelScroll = 0;
   private final List<XHelper> clickRegions = new ArrayList<>();
   private final List<EntryHelper> presets = List.of(
      new EntryHelper(StatusEffects.SPEED, 1, secs(60), false),
      new EntryHelper(StatusEffects.STRENGTH, 1, secs(180), false),
      new EntryHelper(StatusEffects.FIRE_RESISTANCE, 0, secs(420), false),
      new EntryHelper(StatusEffects.REGENERATION, 1, secs(30), false),
      new EntryHelper(StatusEffects.HASTE, 1, secs(120), false),
      new EntryHelper(StatusEffects.JUMP_BOOST, 0, secs(180), false),
      new EntryHelper(StatusEffects.NIGHT_VISION, 0, 0, true),
      new EntryHelper(StatusEffects.SLOWNESS, 0, secs(45), false)
   );

   public ConfigGuiScreen() {
      super(Text.literal("HUD Preview"));
   }

   private static int secs(
      int n
   ) {
      return n
         * 20;
   }

   protected void init() {
      super.init();
      IsactiveHelper.setActive(true);
      if (InterfaceModule.INSTANCE != null && !InterfaceModule.INSTANCE.isEnable()) {
         InterfaceModule.INSTANCE.setEnabled(true);
      }
   }

   public void removed() {
      super.removed();

      for (Draggable val : Draggables.draggables
         .values()) {
         val.onRelease(
            0
         );
      }

      IsactiveHelper.setActive(false);
   }

   public boolean shouldPause() {
      return false;
   }

   public void tick() {
      super.tick();
      IsactiveHelper.tick();
   }

   public void render(
      DrawContext context,
      int n,
      int n2,
      float f
   ) {
      MatrixStack matrices = context.getMatrices();
      int i = this.width;
      int i2 = this.height;
      int i3 = i
         - 220;
      Render2D.drawImage(
         matrices,
         BG,
         0.0F,
         0.0F,
         (float)i3,
         (float)i2,
         -1
      );
      Render2D.drawRoundedRect(
         matrices,
         0.0F,
         0.0F,
         (float)i3,
         (float)i2,
         0.0F,
         1073741824
      );
      EventRender.Default val = new EventRender.Default(
         context,
         f
      );
      if (InterfaceModule.INSTANCE != null) {
         try {
            InterfaceModule.INSTANCE
               .onEvent(
                  val
               );
         } catch (Throwable var14) {
         }
      }

      boolean flag = GLFW.glfwGetMouseButton(
            MinecraftClient.getInstance().getWindow().getHandle(), 0
         )
         == 1;
      if (this.AspectDLC$leftPressed
         && !flag
         )
       {
         for (Draggable val2 : Draggables.draggables
            .values()) {
            val2.onRelease(
               0
            );
         }
      }

      this.AspectDLC$leftPressed = flag;
      Window val3 = MinecraftClient.getInstance()
         .getWindow();

      for (Draggable val4 : Draggables.draggables
         .values()) {
         if (val4.getModule()
            .isEnable()) {
            val4.onDraw(
               n,
               n2,
               val3,
               matrices
            );
         }
      }

      this.clickRegions.clear();
      this.renderPanel(
         context,
         i
            - 220,
         0,
         220,
         i2,
         n,
         n2
      );
   }

   private void renderPanel(
      DrawContext context,
      int n,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6
   ) {
      context.fill(
         n,
         n2,
         n
            + n3,
         n2
            + n4,
         -535818224
      );
      context.fill(
         n,
         n2,
         n
            + 1,
         n2
            + n4,
         -14013910
      );
      int i = n2
         + 8
         - this.panelScroll;
      int i2 = n
         + 8;
      int i3 = n3
         - 16;
      context.drawText(
         this.textRenderer,
         Text.literal("HUD PREVIEW").formatted(Formatting.WHITE),
         i2,
         i,
         -1,
         false
      );
      i += 12;
      context.drawText(
         this.textRenderer,
         Text.literal("Перетаскивай виджеты мышкой").formatted(Formatting.GRAY),
         i2,
         i,
         -5592406,
         false
      );
      i += 14;
      i = this.section(
         context,
         i2,
         i,
         i3,
         "Виджеты"
      );
      if (InterfaceModule.INSTANCE != null) {
         for (String name : InterfaceModule.INSTANCE
            .getHudModuleNames()) {
            boolean flag = InterfaceModule.INSTANCE
               .isHudModuleVisible(
                  name
               );
            i = this.checkboxRow(
               context,
               i2,
               i,
               i3,
               name,
               flag,
               n5,
               n6,
               () -> InterfaceModule.INSTANCE
                     .setHudModuleVisible(
                        name,
                        !flag
                     )
            );
         }
      }

      i += 6;
      i = this.section(
         context,
         i2,
         i,
         i3,
         "Модули (с биндом)"
      );
      ObjectListIterator var35 = ModuleManager.INSTANCE.getObject().iterator();

      while (var35.hasNext()) {
         Module val = (Module)var35.next();
         if (val.getKey()
               != -1
            && val.getKey()
               != 0) {
            boolean flag2 = val.isEnable();
            String name2 = val.getDisplayName();
            i = this.checkboxRow(
               context,
               i2,
               i,
               i3,
               name2,
               flag2,
               n5,
               n6,
               () -> val.setEnabled(
                     !flag2
                  )
            );
         }
      }

      i += 6;
      i = this.section(
         context,
         i2,
         i,
         i3,
         "Стиль Potions"
      );
      PotionsHud val2 = this.findPotions();
      if (val2
         != null) {
         boolean flag3 = val2.isPotionStyleCompact();
         int i4 = (
               i3
                  - 4
            )
            / 2;
         i = this.buttonRow(
            context,
            i2,
            i,
            i4,
            "Обычный",
            !flag3,
            n5,
            n6,
            () -> val2.setPotionStyleCompact(
                  false
               )
         );
         this.buttonRow(
            context,
            i2
               + i4
               + 4,
            i
               - 14,
            i4,
            "Компакт",
            flag3,
            n5,
            n6,
            () -> val2.setPotionStyleCompact(
                  true
               )
         );
      }

      i += 6;
      i = this.section(
         context,
         i2,
         i,
         i3,
         "Добавить зелье"
      );

      for (EntryHelper val3 : this.presets) {
         i = this.buttonRow(
            context,
            i2,
            i,
            i3,
            "+ "
               + val3.label(),
            false,
            n5,
            n6,
            () -> IsactiveHelper.addEffect(
                  val3.entry,
                  val3.amp,
                  val3.durTicks,
                  val3.infinite
               )
         );
      }

      i += 6;
      i = this.section(
         context,
         i2,
         i,
         i3,
         "Активные эффекты"
      );
      List<StatusEffectInstance> effect = IsactiveHelper.getEffects();
      if (effect.isEmpty()
         )
       {
         context.drawText(
            this.textRenderer,
            Text.literal("(пусто)").formatted(Formatting.DARK_GRAY),
            i2,
            i,
            -11184811,
            false
         );
         i += 12;
      } else {
         for (StatusEffectInstance effect2 : effect) {
            String name3 = ((StatusEffect)effect2.getEffectType()
                  .value())
               .getName()
               .getString();
            String name4 = effect2.getAmplifier()
                  == 0
               ? ""
               : " "
                  + romanShort(
                     effect2.getAmplifier()
                        + 1
                  );
            String name5 = effect2.isInfinite()
               ? "inf"
               : effect2.getDuration()
                     / 20
                     / 60
                  + ":"
                  + String.format(
                     "%02d",
                     effect2.getDuration()
                        / 20
                        % 60
                  );
            String name6 = "× "
               + name3
               + name4
               + "  "
               + name5;
            RegistryEntry val4 = effect2.getEffectType();
            i = this.buttonRow(
               context,
               i2,
               i,
               i3,
               name6,
               false,
               n5,
               n6,
               () -> IsactiveHelper.removeEffect(
                     val4
                  )
            );
         }
      }

      i += 8;
      i = this.buttonRow(
         context,
         i2,
         i,
         i3,
         "Сбросить позиции",
         false,
         n5,
         n6,
         this::resetPositions
      );
      i += 4;
      i = this.buttonRow(
         context,
         i2,
         i,
         i3,
         "Закрыть превью",
         false,
         n5,
         n6,
         this::close
      );
   }

   private static String romanShort(
      int n
   ) {
      return switch (n) {
         case 1 -> "I";
         case 2 -> "II";
         case 3 -> "III";
         case 4 -> "IV";
         case 5 -> "V";
         default -> String.valueOf(
         n
      );
      };
   }

   private int section(
      DrawContext context,
      int n,
      int n2,
      int n3,
      String str
   ) {
      context.fill(
         n,
         n2
            + 4,
         n
            + n3,
         n2
            + 5,
         -14013910
      );
      context.drawText(
         this.textRenderer,
         Text.literal(
               str
            )
            .formatted(Formatting.GRAY),
         n,
         n2
            - 2,
         -4210753,
         false
      );
      return n2
         + 12;
   }

   private int checkboxRow(
      DrawContext context,
      int n,
      int n2,
      int n3,
      String str,
      boolean flag,
      int n4,
      int n5,
      Runnable val
   ) {
      int x = 12;
      boolean flag2 = n4
            >= n
         && n4
            < n
               + n3
         && n5
            >= n2
         && n5
            < n2
               + x;
      if (flag2
         )
       {
         context.fill(
            n,
            n2,
            n
               + n3,
            n2
               + x,
            1090519039
         );
      }

      int x2 = flag
         ? -5261569
         : -11184811;
      context.fill(
         n
            + 1,
         n2
            + 2,
         n
            + 9,
         n2
            + 10,
         -16777216
      );
      context.fill(
         n
            + 2,
         n2
            + 3,
         n
            + 8,
         n2
            + 9,
         x2
      );
      if (flag
         )
       {
         context.fill(
            n
               + 3,
            n2
               + 5,
            n
               + 7,
            n2
               + 6,
            -16777216
         );
         context.fill(
            n
               + 4,
            n2
               + 6,
            n
               + 6,
            n2
               + 7,
            -16777216
         );
      }

      context.drawText(
         this.textRenderer,
         Text.literal(
            str
         ),
         n
            + 13,
         n2
            + 2,
         -2039584,
         false
      );
      this.clickRegions
         .add(
            new XHelper(
               n,
               n2,
               n3,
               x,
               val
            )
         );
      return n2
         + x
         + 1;
   }

   private int buttonRow(
      DrawContext context,
      int n,
      int n2,
      int n3,
      String str,
      boolean flag,
      int n4,
      int n5,
      Runnable val
   ) {
      int x = 12;
      boolean flag2 = n4
            >= n
         && n4
            < n
               + n3
         && n5
            >= n2
         && n5
            < n2
               + x;
      int x2 = flag
         ? -5261569
         : (
            flag2
               ? -13290187
               : -14803426
         );
      int x3 = flag
         ? -16777216
         : -2039584;
      context.fill(
         n,
         n2,
         n
            + n3,
         n2
            + x,
         x2
      );
      context.drawText(
         this.textRenderer,
         Text.literal(
            str
         ),
         n
            + 4,
         n2
            + 2,
         x3,
         false
      );
      this.clickRegions
         .add(
            new XHelper(
               n,
               n2,
               n3,
               x,
               val
            )
         );
      return n2
         + x
         + 2;
   }

   private PotionsHud findPotions() {
      if (InterfaceModule.INSTANCE == null) {
         return null;
      } else {
         Map<String, HudElement> val = InterfaceModule.INSTANCE
            .getConfigurableHudElements();
         HudElement val2 = val.get(
            "potions"
         );
         return val2 instanceof PotionsHud
            ? (PotionsHud)val2
            : null;
      }
   }

   private void resetPositions() {
      for (Draggable val : Draggables.draggables
         .values()) {
         val.setX(
            val.initialXVal
         );
         val.setY(
            val.initialYVal
         );
      }
   }

   public boolean mouseClicked(
      double d,
      double d2,
      int n
   ) {
      if (d
         >= (double)(this.width - 220)) {
         if (n
            == 0) {
            for (XHelper val : this.clickRegions) {
               if (val.contains(
                  d,
                  d2
               )) {
                  val.onClick
                     .run();
                  return true;
               }
            }
         }

         return super.mouseClicked(
            d,
            d2,
            n
         );
      } else {
         if (n
            == 0) {
            for (Draggable val2 : Draggables.draggables
               .values()) {
               if (val2.getModule()
                     .isEnable()
                  && val2.onClick(
                     d,
                     d2,
                     n
                  )) {
                  return true;
               }
            }
         }

         return super.mouseClicked(
            d,
            d2,
            n
         );
      }
   }

   public boolean mouseReleased(
      double d,
      double d2,
      int n
   ) {
      for (Draggable val : Draggables.draggables
         .values()) {
         val.onRelease(
            n
         );
      }

      return super.mouseReleased(
         d,
         d2,
         n
      );
   }

   public boolean mouseScrolled(
      double d,
      double d2,
      double d3,
      double d4
   ) {
      if (d
         >= (double)(this.width - 220)) {
         this.panelScroll = Math.max(
            0,
            this.panelScroll
               - (int)(
                  d4
                     * 14.0
               )
         );
         return true;
      } else {
         return super.mouseScrolled(
            d,
            d2,
            d3,
            d4
         );
      }
   }

   public boolean keyPressed(
      int n,
      int n2,
      int n3
   ) {
      if (n
         == 256) {
         this.close();
         return true;
      } else {
         return super.keyPressed(
            n,
            n2,
            n3
         );
      }
   }
}
