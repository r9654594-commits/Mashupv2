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
import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.events.implement.EventUpdate;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;

public class CooldownsHud extends HudElement {
   private final UpdateHelper widthAnimation = new UpdateHelper(70.0F, 10.5F, EasingFunctions.QUAD_OUT);
   private final Map<String, IdHolder> activeCooldowns = new ConcurrentHashMap<>();
   private final Map<String, UpdateHelper> animations = new LinkedHashMap<>();
   private boolean compactStyle = false;

   public void setCompactStyle(
      boolean flag
   ) {
      this.compactStyle = flag;
   }

   public CooldownsHud(
      Draggable val
   ) {
      super(
         val
      );
      EventInvoker.register(this);
   }

   private Font issue(
      int n
   ) {
      return Fonts.getFont(
         "nunito",
         n
      );
   }

   private Font icon(
      int n
   ) {
      return Fonts.getFont(
         "icon",
         n
      );
   }

   private Font iconsMinced(
      int n
   ) {
      return Fonts.getFont(
         "iconsminced",
         n
      );
   }

   private UpdateHelper getAnimation(
      String str
   ) {
      return this.animations
         .computeIfAbsent(
            str,
            val -> new UpdateHelper(
                  0.0F, 10.5F, EasingFunctions.QUAD_OUT
               )
         );
   }

   private String formatDuration(
      int n
   ) {
      int x = Math.max(
         0,
         n
      );
      int x2 = x
         / 60;
      int x3 = x
         % 60;
      return x2
         + ":"
         + (
            x3
                  < 10
               ? "0"
                  + x3
               : String.valueOf(
                  x3
               )
         );
   }

   @EventLink
   public void onPacket(
      EventPacket event
   ) {
      if (event.getType()
         == EventPacket.Type.RECEIVE) {
         MinecraftClient val = MinecraftClient.getInstance();
         if (val
               != null
            && val.world
               != null) {
            if (event.getPacket() instanceof CooldownUpdateS2CPacket val2
               )
             {
               Item item = (Item)Registries.ITEM
                  .get(
                     val2.cooldownGroup()
                  );
               if (item
                     == Items.BOW
                  || item
                     == Items.CROSSBOW
                  || item
                     == Items.FIREWORK_ROCKET) {
                  return;
               }

               int y = val2.cooldown();
               String name = "vanilla:"
                  + item.getTranslationKey();
               if (y
                  == 0) {
                  this.activeCooldowns
                     .remove(
                        name
                     );
               } else {
                  long time = val.world
                     .getTime();
                  this.activeCooldowns
                     .put(
                        name,
                        IdHolder.fromTicks(
                           name,
                           item.getName()
                              .getString(),
                           item.getDefaultStack(),
                           time,
                           (long)y
                        )
                     );
               }
            } else if (event.getPacket() instanceof PlayerRespawnS2CPacket
               )
             {
               this.activeCooldowns.clear();
            }
         }
      }
   }

   @EventLink
   public void onUpdateCustom(
      EventUpdate event
   ) {
      MinecraftClient val = MinecraftClient.getInstance();
      if (val
            != null
         && val.player
            != null) {
         ItemStack stack = val.player
            .getActiveItem();
         if (stack
               != null
            && !stack.isEmpty()
            && val.player
                  .getItemUseTime()
               >= stack.getMaxUseTime(
                  val.player
               )) {
            PotionContentsComponent val2 = (PotionContentsComponent)stack.get(
               DataComponentTypes.POTION_CONTENTS
            );
            if (val2
                  != null
               && (
                  val2.getColor()
                        == 33461
                     || val2.getColor()
                        == -515037
               )) {
               ItemStack stack2 = Items.POTION
                  .getDefaultStack();
               stack2.set(
                  DataComponentTypes.POTION_CONTENTS,
                  new PotionContentsComponent(
                     Optional.of(Potions.SWIFTNESS),
                     Optional.of(
                        val2.getColor()
                     ),
                     List.of(),
                     Optional.empty()
                  )
               );
               long time = System.nanoTime();
               String str = "custom:Исцел";
               this.activeCooldowns
                  .put(
                     str,
                     IdHolder.fromReal(
                        str,
                        "Исцел",
                        stack2,
                        time,
                        10000000000L
                     )
                  );
            }
         }
      }
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      int i;
      if (!AspectDLC.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
         i = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme()
            .color[0];
      } else {
         i = ColorUtil.getThemeColor();
      }

      float width3 = 70.0F;
      float width4 = 5.0F
         + this.iconsMinced(17)
            .getWidth("e")
         + 3.0F
         + this.issue(14)
            .getWidth("Cooldowns")
         + 5.0F;
      if (width4
         > width3
         )
       {
         width3 = width4;
      }

      float width5 = 16.0F;
      int i2 = 0;
      MinecraftClient val = MinecraftClient.getInstance();
      long time = val
               != null
            && val.world
               != null
         ? val.world
            .getTime()
         : 0L;
      this.activeCooldowns
         .entrySet()
         .removeIf(
            val2 -> val2.getValue()
                  .isFinished(
                     time
                  )
         );
      List<Entry<String, IdHolder>> val3 = new ArrayList<>(
         this.activeCooldowns.entrySet()
      );
      val3.sort(
         (val4, val5) -> Float.compare(
               val4.getValue()
                  .getRemainingRatio(
                     time
                  ),
               val5.getValue()
                  .getRemainingRatio(
                     time
                  )
            )
      );
      Map<String, IdHolder> val6 = new LinkedHashMap<>();

      for (Entry<String, IdHolder> val7 : val3) {
         val6.put(
            val7.getKey(),
            val7.getValue()
         );
      }

      this.activeCooldowns.clear();
      this.activeCooldowns
         .putAll(
            val6
         );
      List<String> list = new ArrayList<>(
         this.activeCooldowns.keySet()
      );

      for (String name : list) {
         this.getAnimation(
               name
            )
            .update(1.0F);
      }

      for (Entry<String, UpdateHelper> val8 : this.animations.entrySet()) {
         if (!list.contains(
            val8.getKey()
         )) {
            val8.getValue()
               .update(0.0F);
         }
      }

      for (String name2 : this.animations
         .keySet()) {
         UpdateHelper val9 = this.getAnimation(
            name2
         );
         float width6 = val9.getValue();
         IdHolder val10 = this.activeCooldowns
            .get(
               name2
            );
         if (width6
               > 0.01F
            && val10
               != null) {
            i2++;
            String name3 = val10.displayName;
            if (name3.equalsIgnoreCase(
                  "Зачарованное золотое яблоко"
               )
               || val10.icon
                     .getItem()
                  == Items.ENCHANTED_GOLDEN_APPLE) {
               name3 = "Чарка";
            }

            String name4 = this.formatDuration(
               val10.getRemainingSeconds(
                  time
               )
            );
            float width7 = this.compactStyle
               ? 13.0F
               : 16.0F;
            float width8 = this.compactStyle
               ? 11.0F
               : 12.0F;
            float width9 = this.compactStyle
               ? 10.0F
               : 11.0F;
            float width10 = this.issue(
                  (int)width8
               )
               .getWidth(
                  name3
               );
            float width11 = this.issue(
                     (int)width9
                  )
                  .getWidth(
                     name4
                  )
               + (float)(
                  this.compactStyle
                     ? 6
                     : 10
               );
            float width12 = width10
               + width11
               + (float)(
                  this.compactStyle
                     ? 16
                     : 24
               );
            if (width12
               > width3
               )
             {
               width3 = width12;
            }

            width5 += width7
               * width6;
         }
      }

      if (i2
         > 0) {
         width5 += 2.0F;
      }

      this.widthAnimation
         .update(
            width3
         );
      float width13 = this.widthAnimation
         .getValue();
      float width14 = width5;
      this.drawHudBackground(
         event.getContext()
            .getMatrices(),
         width,
         width2,
         width13,
         width5,
         i
      );
      this.iconsMinced(17)
         .draw(
            event.getContext()
               .getMatrices(),
            "e",
            width
               + 5.0F,
            width2
               + 6.5F,
            i
         );
      this.issue(14)
         .draw(
            event.getContext()
               .getMatrices(),
            "Cooldowns",
            width
               + 5.0F
               + this.iconsMinced(17)
                  .getWidth("e")
               + 3.0F,
            width2
               + 6.0F,
            -1
         );
      float width15 = 18.0F;

      for (String name5 : this.animations
         .keySet()) {
         UpdateHelper val11 = this.getAnimation(
            name5
         );
         float width16 = val11.getValue();
         IdHolder val12 = this.activeCooldowns
            .get(
               name5
            );
         if (width16
               > 0.01F
            && val12
               != null) {
            ScissorStack.push();
            ScissorStack.setFromComponentCoordinates(
               (double)width,
               (double)width2,
               (double)width13,
               (double)width14
            );
            int i3 = (int)(
               255.0F
                  * width16
            );
            int i4 = ColorUtil.rgba(
               255,
               255,
               255,
               i3
            );
            int i5 = ColorUtil.setAlphaColor(
               i,
               i3
            );
            String name6 = val12.displayName;
            if (name6.equalsIgnoreCase(
                  "Зачарованное золотое яблоко"
               )
               || val12.icon
                     .getItem()
                  == Items.ENCHANTED_GOLDEN_APPLE) {
               name6 = "Чарка";
            }

            String name7 = this.formatDuration(
               val12.getRemainingSeconds(
                  time
               )
            );
            float width17 = this.compactStyle
               ? 13.0F
               : 16.0F;
            float width18 = this.compactStyle
               ? 11.0F
               : 12.0F;
            float width19 = this.compactStyle
               ? 10.0F
               : 11.0F;
            float width20 = this.compactStyle
               ? 7.0F
               : 9.0F;
            if (!val12.icon
               .isEmpty()) {
               event.getContext()
                  .getMatrices()
                  .push();
               event.getContext()
                  .getMatrices()
                  .translate(
                     width
                        + (this.compactStyle ? 3.5F : 5.5F),
                     width2
                        + 2.0F
                        + width15
                        - (this.compactStyle ? 0.5F : 1.0F),
                     0.0F
                  );
               event.getContext()
                  .getMatrices()
                  .scale(
                     width20
                        / 16.0F,
                     width20
                        / 16.0F,
                     1.0F
                  );
               event.getContext()
                  .drawItem(
                     val12.icon,
                     0,
                     0
                  );
               event.getContext()
                  .getMatrices()
                  .pop();
            }

            float width21 = width
               + (this.compactStyle ? 3.5F : 5.5F)
               + width20
               + (this.compactStyle ? 2.0F : 3.0F);
            float width22 = width2
               + width15
               + (this.compactStyle ? 2.5F : 3.5F);
            this.issue(
                  (int)width18
               )
               .draw(
                  event.getContext()
                     .getMatrices(),
                  name6,
                  width21,
                  width22,
                  i4
               );
            float width23 = width
               + width13
               - this.issue(
                     (int)width19
                  )
                  .getWidth(
                     name7
                  )
               - (float)(
                  this.compactStyle
                     ? 4
                     : 7
               );
            this.issue(
                  (int)width19
               )
               .draw(
                  event.getContext()
                     .getMatrices(),
                  name7,
                  width23,
                  width22
                     + (this.compactStyle ? 0.5F : 1.0F),
                  i5
               );
            float width24 = val12.getRemainingRatio(
               time
            );
            float width25 = (
                  width13
                     - 10.0F
               )
               * width24;
            float width26 = width2
               + 2.0F
               + width15
               + width17
               - 3.5F;
            Render2D.drawRoundedRect(
               event.getContext()
                  .getMatrices(),
               width
                  + 5.0F,
               width26,
               width13
                  - 10.0F,
               1.5F,
               0.5F,
               ColorUtil.rgba(
                  0,
                  0,
                  0,
                  i3
                     / 3
               )
            );
            Render2D.drawRoundedRect(
               event.getContext()
                  .getMatrices(),
               width
                  + 5.0F,
               width26,
               width25,
               1.5F,
               0.5F,
               i5
            );
            width15 += width17
               * width16;
            ScissorStack.pop();
            ScissorStack.unset();
         }
      }

      this.animations
         .entrySet()
         .removeIf(
            val13 -> !list.contains(
                     val13.getKey()
                  )
                  && val13.getValue()
                        .getValue()
                     <= 0.01F
         );
      this.draggable
         .setWidth(
            width13
         );
      this.draggable
         .setHeight(
            width14
         );
      super.onRender(
         event
      );
   }
}
