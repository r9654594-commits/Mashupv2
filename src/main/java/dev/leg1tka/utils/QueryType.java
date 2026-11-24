package dev.leg1tka.utils;
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
import dev.leg1tka.storage.*;


import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum QueryType {
   DISORIENTATION("дезориентация", "дезориентации", Items.ENDER_EYE, "Использовал дезориентацию!", "Дезориентация не найдена!"),
   TRAP("трапка", "трапки", Items.NETHERITE_SCRAP, "Использовал трапку!", "Трапка не найдена!"),
   PLAST("пласт", "пласта", Items.DRIED_KELP, "Использовал пласт!", "Пласт не найден!"),
   DUST("явная пыль", "пыли", Items.SUGAR, "Использовал пыль!", "Пыль не найдена!"),
   FREEZE_SNOW("заморозка", "снега", Items.SNOWBALL, "Использовал снег!", "Снег не найден!"),
   AURA("божья", "ауры", Items.PHANTOM_MEMBRANE, "Использовал ауру!", "Аура не найдена!");

   public final String query;
   public final String cooldownName;
   public final Item item;
   public final String successText;
   public final String failText;

   private QueryType(String var3, String var4, Item var5, String var6, String var7) {
      this.query = var3;
      this.cooldownName = var4;
      this.item = var5;
      this.successText = var6;
      this.failText = var7;
   }
}
