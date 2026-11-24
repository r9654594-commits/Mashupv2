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
import dev.leg1tka.api.events.implement.EventAttackEntity;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.player.PlayerEntity;

public class AutoEZ extends Module {
   public static final AutoEZ INSTANCE = new AutoEZ();
   private final ModeSetting mode;
   private final StringSetting msg;
   private final BooleanSetting mention;
   private final NumberSetting killDelay;
   private final Map<PlayerEntity, Long> map;

   public AutoEZ() {
      super("AutoEZ", "Пишет сообщение при убийстве игрока", Category.MISC);
      this.mode = new ModeSetting("Режим", "Глобальный чат", new String[]{"Глобальный чат", "Личные сообщения"});
      this.msg = new StringSetting(
         "Что писать",
         "ez by leg1tka",
         100
      );
      this.mention = new BooleanSetting("Упоминать ник", true)
         .visible(() -> this.mode.is("Глобальный чат"));
      this.killDelay = new NumberSetting("Таймаут килла (сек)", 2.0F, 0.5F, 10.0F, 0.5F);
      this.map = new ConcurrentHashMap<>();
      this.addSettings(new Setting[]{
         this.mode,
         this.msg,
         this.mention,
         this.killDelay
      });
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.map.clear();
   }

   @EventLink
   public void onAttack(
      EventAttackEntity val
   ) {
      if (mc.player != null && mc.world != null) {
         if (val.getTarget() instanceof PlayerEntity val2
            && val2
               != mc.player) {
            this.map
               .put(
                  val2,
                  System.currentTimeMillis()
               );
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val5
   ) {
      if (mc.player != null && mc.world != null) {
         long val = System.currentTimeMillis();
         Iterator<Entry<PlayerEntity, Long>> val2 = this.map
            .entrySet()
            .iterator();

         while (
            val2.hasNext()
         ) {
            Entry<PlayerEntity, Long> val3 = val2.next();
            PlayerEntity player2 = val3.getKey();
            Long val4 = val3.getValue();
            if (val
                  - val4
               > (long)(this.killDelay.getValue().floatValue() * 1000.0F)) {
               val2.remove();
            } else if (!player2.isAlive()
               || player2.getHealth()
                  <= 0.0F
               || player2.getRemovalReason()
                  == RemovalReason.KILLED) {
               this.sendEzMessage(
                  player2.getName()
                     .getString()
               );
               val2.remove();
            }
         }
      } else {
         this.map.clear();
      }
   }

   private void sendEzMessage(
      String val
   ) {
      String str = this.msg
         .get();
      if (this.mode.is("Личные сообщения")) {
         if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler()
               .sendChatCommand(
                  "msg "
                     + val
                     + " "
                     + str
               );
         }
      } else if (mc.getNetworkHandler() != null) {
         if (this.mention.isState()) {
            mc.getNetworkHandler()
               .sendChatMessage(
                  "!"
                     + val
                     + " "
                     + str
               );
         } else {
            mc.getNetworkHandler()
               .sendChatMessage(
                  "!"
                     + str
               );
         }
      }
   }
}
