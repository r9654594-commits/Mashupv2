package dev.leg1tka.core;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventInvoker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Generated;

public abstract class Module implements IMinecraft {
   private String name;
   private String description;
   private int key;
   private Category category;
   private boolean isOpen;
   private boolean enable;
   private boolean hidden = false;
   private final List<Setting> settings = new ArrayList<>();
   private final UpdateHelper animka = new UpdateHelper(60.0F, 11.0F, EasingFunctions.LINEAR);
   private final UpdateHelper arrayAnimka = new UpdateHelper(0.0F, 11.0F, EasingFunctions.LINEAR);

   public Module(
      String str,
      String str2,
      Category val
   ) {
      this.name = str;
      this.description = str2;
      this.category = val;
      this.key = -1;
   }

   public Module(
      String str,
      Category val
   ) {
      this.name = str;
      this.description = "NULLABLE";
      this.category = val;
      this.key = -1;
   }

   public void onEnable() {
      this.enable = true;
      EventInvoker.register(this);
      this.animka.update(1.0F);
      PushHelper.push(this.name, this.category.getIcons(), true);
   }

   public void onDisable() {
      this.enable = false;
      EventInvoker.unregister(this);
      this.animka.update(0.0F);
      PushHelper.push(this.name, this.category.getIcons(), false);
   }

   public void toggle() {
      this.enable = !this.enable;
      if (this.enable) {
         this.onEnable();
      } else {
         this.onDisable();
      }
   }

   public void setEnabled(
      boolean flag
   ) {
      boolean flag2 = this.enable;
      this.enable = flag;

      try {
         if (flag
            )
          {
            this.onEnable();
         } else if (flag2
            )
          {
            this.onDisable();
         }
      } catch (Exception var4) {
         this.enable = false;
         this.onDisable();
      }
   }

   public void addSettings(
      Setting... val
   ) {
      if (val
            != null
         && val.length
            != 0) {
         Arrays.stream(
               val
            )
            .filter(Objects::nonNull)
            .forEach(this.settings::add);
      }
   }

   public String getDisplayName() {
      return AspectDLC.INSTANCE.localizationStorage == null ? this.name : AspectDLC.INSTANCE.localizationStorage.translate(this.name);
   }

   public String getDisplayDescription() {
      return AspectDLC.INSTANCE.localizationStorage == null ? this.description : AspectDLC.INSTANCE.localizationStorage.translate(this.description);
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getDescription() {
      return this.description;
   }

   @Generated
   public int getKey() {
      return this.key;
   }

   @Generated
   public Category getCategory() {
      return this.category;
   }

   @Generated
   public boolean isOpen() {
      return this.isOpen;
   }

   @Generated
   public boolean isEnable() {
      return this.enable;
   }

   @Generated
   public boolean isHidden() {
      return this.hidden;
   }

   @Generated
   public List<Setting> getSettings() {
      return this.settings;
   }

   @Generated
   public UpdateHelper getAnimka() {
      return this.animka;
   }

   @Generated
   public UpdateHelper getArrayAnimka() {
      return this.arrayAnimka;
   }

   @Generated
   public void setName(
      String str
   ) {
      this.name = str;
   }

   @Generated
   public void setDescription(
      String str
   ) {
      this.description = str;
   }

   @Generated
   public void setKey(
      int n
   ) {
      this.key = n;
   }

   @Generated
   public void setCategory(
      Category val
   ) {
      this.category = val;
   }

   @Generated
   public void setOpen(
      boolean flag
   ) {
      this.isOpen = flag;
   }

   @Generated
   public void setEnable(
      boolean flag
   ) {
      this.enable = flag;
   }

   @Generated
   public void setHidden(
      boolean flag
   ) {
      this.hidden = flag;
   }
}
