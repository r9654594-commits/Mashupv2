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

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventRender;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InterfaceModule extends Module {
   public static InterfaceModule INSTANCE = new InterfaceModule();
   public final MultiBooleanSetting hudModules;
   public final ListSetting theme;
   private final TpsHud waterMark;
   private final KeyBindsHud keyBinds;
   private final HelperBindsHud helperBinds;
   private final PotionsHud potions;
   private final CooldownsHud cooldowns;
   private final WatermarkHud notifications;
   private final TargetHud targetHud;
   private final StaffListHud staffList;
   private final NowPlayingHud nowPlaying;
   private final ArmorStatusHud armorStatus;
   private final ItemCounterHud itemCounter;
   private final ArrayListHud arrayListHud;

   public InterfaceModule() {
      super("Interface", "Интерфейс клиента", Category.RENDER);
      this.hudModules = new MultiBooleanSetting("Элементы", new BooleanSetting[]{
         new BooleanSetting("Ватермарка", true),
         new BooleanSetting("Горячие клавиши", true),
         new BooleanSetting("Серверные бинды", true),
         new BooleanSetting("Зелья", true),
         new BooleanSetting("Стафф", true),
         new BooleanSetting("Уведомления", true),
         new BooleanSetting("Таргет худ", true),
         new BooleanSetting("Музыка", true),
         new BooleanSetting("Статус брони", true),
         new BooleanSetting("Счётчик предметов", true),
         new BooleanSetting("ArrayList", true),
         new BooleanSetting("Кулдауны", true)
      });
      this.theme = new ListSetting("Тема");
      this.addSettings(new Setting[]{
         this.hudModules,
         this.theme
      });
      this.waterMark = new TpsHud(AspectDLC.draggable(this, "WaterMark", 10.0F, 10.0F));
      this.keyBinds = new KeyBindsHud(AspectDLC.draggable(this, "KeyBinds", 30.0F, 30.0F));
      this.helperBinds = new HelperBindsHud(AspectDLC.draggable(this, "HelperBinds", 90.0F, 30.0F));
      this.potions = new PotionsHud(AspectDLC.draggable(this, "Potions", 30.0F, 60.0F));
      this.cooldowns = new CooldownsHud(AspectDLC.draggable(this, "Cooldowns", 30.0F, 80.0F));
      this.staffList = new StaffListHud(AspectDLC.draggable(this, "StaffList", 60.0F, 100.0F));
      this.notifications = new WatermarkHud(AspectDLC.draggable(this, "Notifications", 0.0F, 0.0F));
      this.targetHud = new TargetHud(AspectDLC.draggable(this, "TargetHud", 30.0F, 90.0F));
      this.nowPlaying = new NowPlayingHud(AspectDLC.draggable(this, "NowPlaying", 10.0F, 120.0F));
      this.armorStatus = new ArmorStatusHud(AspectDLC.draggable(this, "ArmorStatus", 0.0F, 0.0F));
      this.itemCounter = new ItemCounterHud(AspectDLC.draggable(this, "ItemCounter", 10.0F, 200.0F));
      this.arrayListHud = new ArrayListHud(AspectDLC.draggable(this, "ArrayList", 10.0F, 30.0F));
   }

   public static boolean isDropdownStyle() {
      return false;
   }

   private void renderHudElement(
      HudElement val,
      EventRender.Default val2
   ) {
      val.draggable
         .beginRenderTilt(
            val2.getContext()
               .getMatrices()
         );

      try {
         val.onRender(
            val2
         );
      } finally {
         val.draggable
            .endRenderTilt(
               val2.getContext()
                  .getMatrices()
            );
      }
   }

   public boolean isHudModuleVisible(
      String val
   ) {
      try {
         return this.hudModules
            .is(
               val
            );
      } catch (Exception var3) {
         return false;
      }
   }

   public void setHudModuleVisible(
      String val,
      boolean val2
   ) {
      try {
         this.hudModules
            .set(
               val,
               val2
            );
      } catch (Exception var4) {
      }
   }

   public List<String> getHudModuleNames() {
      List val = new ArrayList<>();

      for (BooleanSetting val2 : this.hudModules
         .getSettings()) {
         val.add(
            val2.name()
         );
      }

      return val;
   }

   public Map<String, HudElement> getConfigurableHudElements() {
      Map val = new LinkedHashMap<>();
      val.put(
         "waterMark", this.waterMark
      );
      val.put(
         "keyBinds", this.keyBinds
      );
      val.put(
         "helperBinds", this.helperBinds
      );
      val.put(
         "potions", this.potions
      );
      val.put(
         "cooldowns", this.cooldowns
      );
      val.put(
         "notifications", this.notifications
      );
      val.put(
         "targetHud", this.targetHud
      );
      val.put(
         "staffList", this.staffList
      );
      val.put(
         "nowPlaying", this.nowPlaying
      );
      val.put(
         "armorStatus", this.armorStatus
      );
      val.put(
         "itemCounter", this.itemCounter
      );
      val.put(
         "arrayListHud", this.arrayListHud
      );
      return val;
   }

   @EventLink(
      priority = -200
   )
   public void onEvent(
      EventRender.Default val
   ) {
      if (mc == null || !(mc.currentScreen instanceof HudEditorScreen)) {
         boolean flag = this.hudModules
            .is("Ватермарка");
         boolean flag2 = this.hudModules
            .is("Горячие клавиши");
         boolean flag3 = this.hudModules
            .is("Серверные бинды");
         boolean flag4 = this.hudModules
            .is("Зелья");
         boolean flag5 = false;

         try {
            flag5 = this.hudModules
               .is("Кулдауны");
         } catch (Exception var17) {
         }

         boolean flag6 = this.hudModules
            .is("Стафф");
         boolean flag7 = this.hudModules
            .is("Уведомления");
         boolean flag8 = this.hudModules
            .is("Таргет худ");
         boolean flag9 = this.hudModules
            .is("Музыка");
         boolean flag10 = this.hudModules
            .is("Статус брони");
         boolean flag11 = this.hudModules
            .is("Счётчик предметов");
         boolean flag12 = this.hudModules
            .is("ArrayList");
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         UpdateHelper_1.update();

         try {
            if (flag
               )
             {
               this.renderHudElement(
                  this.waterMark,
                  val
               );
            }

            if (flag2
               )
             {
               this.renderHudElement(
                  this.keyBinds,
                  val
               );
            }

            if (flag3
               )
             {
               this.renderHudElement(
                  this.helperBinds,
                  val
               );
            }

            if (flag5
               )
             {
               this.renderHudElement(
                  this.cooldowns,
                  val
               );
            }

            if (flag4
               )
             {
               this.renderHudElement(
                  this.potions,
                  val
               );
            }

            if (flag6
               )
             {
               this.renderHudElement(
                  this.staffList,
                  val
               );
            }

            if (flag7
               )
             {
               this.renderHudElement(
                  this.notifications,
                  val
               );
            }

            if (flag8
               )
             {
               this.renderHudElement(
                  this.targetHud,
                  val
               );
            }

            if (flag9
               )
             {
               this.renderHudElement(
                  this.nowPlaying,
                  val
               );
            }

            if (flag10
               )
             {
               this.renderHudElement(
                  this.armorStatus,
                  val
               );
            }

            if (flag11
               )
             {
               this.renderHudElement(
                  this.itemCounter,
                  val
               );
            }

            if (flag12
               )
             {
               this.renderHudElement(
                  this.arrayListHud,
                  val
               );
            }
         } finally {
            Render2D.resetGuiRenderState();
         }
      }
   }
}
