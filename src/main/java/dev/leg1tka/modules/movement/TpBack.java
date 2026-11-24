package dev.leg1tka.modules.movement;


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
import java.util.concurrent.ThreadLocalRandom;

public class TpBack extends Module {
   public static TpBack INSTANCE = new TpBack();
   private static final long HOME_DELAY_MS = 100L;
   private static final long COMMAND_DELAY_MS = 200L;
   private boolean isDead;
   private long deathTimestamp;
   public BooleanSetting randomName;
   public StringSetting homeName;
   public BooleanSetting sendAfterCommand;
   public StringSetting afterCommand;

   public TpBack() {
      super("TpBack", Category.MOVEMENT);
      String var1 = "TpBack";
      this.isDead = false;
      this.deathTimestamp = 0L;
      this.randomName = new BooleanSetting(
         "Рандом название", false
      );
      String var4 = "Название дома";
      this.homeName = new StringSetting(
            var4,
            "home",
            32
         )
         .visible(() -> !this.randomName.isState());
      this.sendAfterCommand = new BooleanSetting("Команда после /home", false);
      String var7 = "Что писать после /home";
      this.afterCommand = new StringSetting(
            var7,
            "rtp far",
            64
         )
         .visible(() -> this.sendAfterCommand.isState());
      this.addSettings(new Setting[]{
         this.randomName,
         this.homeName,
         this.sendAfterCommand,
         this.afterCommand
      });
   }

   @Override
   public void onDisable() {
      this.isDead = false;
      this.deathTimestamp = 0L;
      super.onDisable();
   }

   @EventLink
   public void onEvent(
      EventUpdate val4
   ) {
      if (mc.player != null && mc.world != null && mc.player.networkHandler != null) {
         boolean flag = mc.player
                  .getHealth()
               <= 0.0F
            && mc.player.deathTime > 0;
         if (flag
            && !this.isDead) {
            this.isDead = true;
            this.deathTimestamp = System.currentTimeMillis();
            String name = this.getHomeName();
            this.sendCommand(
               "sethome "
                  + name
            );
            mc.player.requestRespawn();
            ChatUtil.sendMessage(
               "TpBack: сохранён дом \""
                  + name
                  + "\""
            );
            this.scheduleHomeChain(
               name
            );
         }

         if (!flag
            && System.currentTimeMillis() - this.deathTimestamp > 1000L) {
            this.isDead = false;
         }
      }
   }

   private void scheduleHomeChain(
      String val
   ) {
      Thread val2 = new Thread(
         () -> {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var3) {
               return;
            }

            mc.execute(
               () -> {
                  if (mc.player != null) {
                     this.sendCommand(
                        "home "
                           + val
                     );
                     ChatUtil.sendMessage(
                        "TpBack: выполнен /home "
                           + val
                     );
                     if (this.sendAfterCommand.isState()) {
                        Thread val3 = new Thread(
                           () -> {
                              try {
                                 Thread.sleep(200L);
                              } catch (InterruptedException var2x) {
                                 return;
                              }

                              mc.execute(
                                 () -> {
                                    if (mc.player != null) {
                                       String str = this.normalizeCommand(
                                          this.afterCommand.get()
                                       );
                                       if (!str.isEmpty()
                                          )
                                        {
                                          this.sendCommand(
                                             str
                                          );
                                          ChatUtil.sendMessage(
                                             "TpBack: отправлена команда \""
                                                + str
                                                + "\""
                                          );
                                       }
                                    }
                                 }
                              );
                           }
                        );
                        val3.setDaemon(
                           true
                        );
                        val3.start();
                     }
                  }
               }
            );
         }
      );
      val2.setDaemon(
         true
      );
      val2.start();
   }

   private String getHomeName() {
      if (this.randomName.isState()) {
         return this.generateRandomName();
      } else {
         String name = this.homeName
            .get()
            .trim();
         String var2;
         if (name.isEmpty()
            )
          {
            var2 = "home";
         } else {
            var2 = name;
         }

         return var2;
      }
   }

   private String generateRandomName() {
      ThreadLocalRandom val = ThreadLocalRandom.current();
      int n = val.nextInt(
         2, 7
      );
      char[] val2 = new char[n];

      for (int i = 0;
         i
            < n;
         i++
      ) {
         val2[i] = (char)(
            97
               + val.nextInt(
                  26
               )
         );
      }

      return new String(
         val2
      );
   }

   private String normalizeCommand(
      String val
   ) {
      if (val
         == null) {
         return "";
      } else {
         String str = val.trim();

         while (
            str.startsWith(
               "/"
            )
         ) {
            str = str.substring(
                  1
               )
               .trim();
         }

         return str;
      }
   }

   private void sendCommand(
      String val
   ) {
      if (mc.player != null && mc.player.networkHandler != null) {
         mc.player
            .networkHandler
            .sendChatCommand(
               val
            );
      }
   }
}
