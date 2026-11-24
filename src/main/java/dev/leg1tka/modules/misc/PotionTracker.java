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

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Box;

public class PotionTracker extends Module {
   public static PotionTracker INSTANCE = new PotionTracker();
   private static final double TRACK_RADIUS = 50.0;
   private static final double SPLASH_RADIUS = 4.0;
   private static final double SPLASH_HEIGHT = 2.0;
   private static final int MAX_MESSAGES = 4;
   private static final int GRAY = new Color(200, 200, 200).getRGB();
   private static final int PLAYER = new Color(235, 235, 235).getRGB();
   private final Map<Integer, PotioninfoHolder> trackedPotions = new HashMap<>();
   private ClientWorld lastWorld;

   public PotionTracker() {
      super("PotionTracker", "Показывает попадание выкинутых зелий по игрокам", Category.MISC);
   }

   @Override
   public void onDisable() {
      this.trackedPotions.clear();
      this.lastWorld = null;
      super.onDisable();
   }

   @EventLink
   public void onUpdate(
      EventUpdate val6
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.lastWorld != mc.world) {
            this.trackedPotions.clear();
            this.lastWorld = mc.world;
         }

         Set val = new HashSet<>();
         double d = 2500.0;
         Box box = mc.player
            .getBoundingBox()
            .expand(50.0);

         for (PotionEntity entity2 : mc.world
            .getEntitiesByClass(
               PotionEntity.class,
               box,
               Entity::isAlive
            )) {
            if (!(
               mc.player
                     .squaredDistanceTo(
                        entity2
                     )
                  > d
            )) {
               DisplaynameType val2 = this.getPotionInfo(
                  entity2
               );
               if (val2
                  != null) {
                  int n = entity2.getId();
                  val.add(
                     n
                  );
                  PotioninfoHolder val3 = this.trackedPotions
                     .get(
                        n
                     );
                  if (val3
                     == null) {
                     this.trackedPotions
                        .put(
                           n,
                           new PotioninfoHolder(
                              val2,
                              entity2.getX(),
                              entity2.getY(),
                              entity2.getZ()
                           )
                        );
                  } else {
                     val3.lastX = entity2.getX();
                     val3.lastY = entity2.getY();
                     val3.lastZ = entity2.getZ();
                     val3.potionInfo = val2;
                  }
               }
            }
         }

         Set<Integer> set = new HashSet<>(this.trackedPotions.keySet());
         set.removeAll(
            val
         );

         for (int i : set) {
            PotioninfoHolder val4 = this.trackedPotions
               .remove(
                  i
               );
            if (val4
               != null) {
               this.printSplash(
                  val4
               );
            }
         }
      } else {
         this.trackedPotions.clear();
         this.lastWorld = null;
      }
   }

   private void printSplash(
      PotioninfoHolder val
   ) {
      Box box = new Box(
         val.lastX
            - 4.0,
         val.lastY
            - 2.0,
         val.lastZ
            - 4.0,
         val.lastX
            + 4.0,
         val.lastY
            + 2.0,
         val.lastZ
            + 4.0
      );
      List<PlayernameHelper> list = new ArrayList<>();

      for (PlayerEntity player2 : mc.world
         .getPlayers()) {
         if (player2
               != null
            && player2.isAlive()
            && box.contains(
               player2.getPos()
            )) {
            double d = player2.getX()
               - val.lastX;
            double d2 = player2.getZ()
               - val.lastZ;
            double d3 = Math.sqrt(
               d
                     * d
                  + d2
                     * d2
            );
            if (!(
               d3
                  > 4.0
            )) {
               double d4 = Math.max(
                  0.0,
                  1.0
                     - d3
                        / 4.0
               );
               int n = Math.max(
                  1,
                  Math.min(
                     100,
                     (int)Math.round(
                        d4
                           * 100.0
                     )
                  )
               );
               list.add(
                  new PlayernameHelper(
                     player2.getName()
                        .getString(),
                     n,
                     d3
                  )
               );
            }
         }
      }

      list.sort(
         Comparator.comparingDouble(PlayernameHelper::distance)
      );

      for (int i = 0;
         i
            < Math.min(
               4,
               list.size()
            );
         i++
      ) {
         PlayernameHelper val2 = list.get(
            i
         );
         this.sendPotionMessage(
            val2.playerName(),
            val.potionInfo,
            val2.percent()
         );
      }
   }

   private DisplaynameType getPotionInfo(
      PotionEntity val
   ) {
      PotionContentsComponent val2 = (PotionContentsComponent)val.getStack()
         .get(DataComponentTypes.POTION_CONTENTS);
      DisplaynameType val3 = this.getPotionInfo(
         val2
      );
      return val3
            != null
         ? val3
         : this.getPotionInfo(
            val.getStack()
               .getName()
               .getString()
         );
   }

   private DisplaynameType getPotionInfo(
      PotionContentsComponent val
   ) {
      if (val
            != null
         && val.hasEffects()
         )
       {
         boolean flag = this.hasEffect(
            val,
            StatusEffects.REGENERATION,
            1
         );
         boolean flag2 = this.hasEffect(
            val,
            StatusEffects.STRENGTH,
            4
         );
         boolean flag3 = this.hasEffect(
            val,
            StatusEffects.HEALTH_BOOST,
            2
         );
         boolean flag4 = this.hasEffect(
            val,
            StatusEffects.STRENGTH,
            3
         );
         boolean flag5 = this.hasEffect(
            val,
            StatusEffects.SPEED,
            2
         );
         if (flag
            )
          {
            return DisplaynameType.HOLY_WATER;
         } else if (flag2
            )
          {
            return DisplaynameType.WRATH;
         } else if (flag3
            )
          {
            return DisplaynameType.PALADIN;
         } else if (flag4
            && flag5
            )
          {
            return DisplaynameType.ASSASSIN;
         } else {
            return flag4
               ? DisplaynameType.ASSASSIN
               : null;
         }
      } else {
         return null;
      }
   }

   private boolean hasEffect(
      PotionContentsComponent val,
      RegistryEntry<StatusEffect> val2,
      int val3
   ) {
      for (StatusEffectInstance val4 : val.getEffects()) {
         if (val4.getEffectType()
               .equals(
                  val2
               )
            && val4.getAmplifier()
               == val3
            )
          {
            return true;
         }
      }

      return false;
   }

   private DisplaynameType getPotionInfo(
      String val
   ) {
      String str = this.normalize(
         val
      );

      for (DisplaynameType val2 : DisplaynameType.values()) {
         if (str.contains(
            this.normalize(
               val2.plainName()
            )
         )) {
            return val2;
         }
      }

      return null;
   }

   private String normalize(
      String val
   ) {
      return val.replaceAll(
            "§.", ""
         )
         .replace("[", "")
         .replace("]", "")
         .replace("✦", "")
         .toLowerCase(Locale.ROOT)
         .trim();
   }

   private void sendPotionMessage(
      String val,
      DisplaynameType val2,
      int val3
   ) {
      if (mc.player != null) {
         MutableText val4 = Text.literal(
            ""
         );
         val4.append(
            this.gradientText(
               "AspectDLC",
               ColorUtil.getThemeColor(0),
               ColorUtil.getThemeColor(90),
               true
            )
         );
         val4.append(
            Text.literal(" ⇒ ").setStyle(this.grayStyle())
         );
         val4.append(
            Text.literal(
                  val
               )
               .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(PLAYER)))
         );
         val4.append(
            Text.literal(" получил ").setStyle(this.grayStyle())
         );
         val4.append(
            this.gradientText(
               val2.displayName,
               val2.startColor,
               val2.endColor,
               true
            )
         );
         val4.append(
            Text.literal(
                  " "
                     + val3
                     + "%"
               )
               .setStyle(this.grayStyle())
         );
         mc.player
            .sendMessage(
               val4,
               false
            );
      }
   }

   private MutableText gradientText(
      String val,
      int val2,
      int val3,
      boolean val4
   ) {
      MutableText val5 = Text.literal(
         ""
      );

      for (int i = 0;
         i
            < val.length();
         i++
      ) {
         float f = val.length()
               <= 1
            ? 0.0F
            : (float)i
               / (float)(
                  val.length()
                     - 1
               );
         int color = ColorUtil.gradient(
            val2,
            val3,
            f
         );
         val5.append(
            Text.literal(
                  String.valueOf(
                     val.charAt(
                        i
                     )
                  )
               )
               .setStyle(
                  Style.EMPTY
                     .withBold(
                        val4
                     )
                     .withColor(
                        TextColor.fromRgb(
                           color
                        )
                     )
               )
         );
      }

      return val5;
   }

   private Style grayStyle() {
      return Style.EMPTY.withColor(TextColor.fromRgb(GRAY));
   }
}
