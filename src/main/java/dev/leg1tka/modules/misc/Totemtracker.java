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
import dev.leg1tka.api.events.implement.EventPacket;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class Totemtracker extends Module {
   public static Totemtracker INSTANCE = new Totemtracker();
   private final BooleanSetting chatMessage;
   private final BooleanSetting notification;
   private final BooleanSetting checkEnchant;

   public Totemtracker() {
      super("Totemtracker", Category.MISC);
      String var1 = "TotemTracker";
      this.chatMessage = new BooleanSetting("Чат сообщение", true);
      this.notification = new BooleanSetting("Уведомление", true);
      this.checkEnchant = new BooleanSetting("Проверка зачарования", true);
      this.addSettings(new Setting[]{
         this.chatMessage,
         this.notification,
         this.checkEnchant
      });
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (mc.world != null && mc.player != null) {
         if (val.getType()
            == EventPacket.Type.RECEIVE) {
            if (val.getPacket() instanceof EntityStatusS2CPacket val2
               )
             {
               if (val2.getStatus()
                  == 35) {
                  mc.execute(
                     () -> {
                        if (mc.world != null) {
                           if (val2.getEntity(
                              mc.world
                           ) instanceof AbstractClientPlayerEntity val3
                              )
                            {
                              if (val3
                                 != mc.player) {
                                 String name = val3.getName()
                                    .getString();
                                 boolean flag = this.checkEnchant
                                       .isState()
                                    && this.hasEnchantedTotem(
                                       val3
                                    );
                                 String var9;
                                 if (flag
                                    )
                                  {
                                    var9 = "да";
                                 } else {
                                    var9 = "нет";
                                 }

                                 String str = var9;
                                 if (this.chatMessage.isState()) {
                                    StringBuilder val4 = new StringBuilder();
                                    StringBuilder var11 = val4.append(
                                       name
                                    );
                                    var11.append(" §7потерял тотем!");
                                    if (this.checkEnchant.isState()) {
                                       StringBuilder var12 = val4.append(
                                          " §7Зачарован: "
                                       );
                                       String var15;
                                       if (flag
                                          )
                                        {
                                          var15 = "§aда";
                                       } else {
                                          var15 = "§cнет";
                                       }

                                       var12.append(var15);
                                    }

                                    ChatUtil.sendMessage(
                                       val4.toString()
                                    );
                                 }

                                 if (this.notification.isState()) {
                                    String str2;
                                    if (this.checkEnchant.isState()) {
                                       str2 = name
                                          + " потерял тотем | Зачарован: "
                                          + str;
                                    } else {
                                       str2 = name
                                          + " потерял тотем";
                                    }

                                    PushHelper.pushItemNotification(
                                       str2,
                                       new ItemStack(Items.TOTEM_OF_UNDYING)
                                    );
                                 }
                              }
                           }
                        }
                     }
                  );
               }
            }
         }
      }
   }

   private boolean hasEnchantedTotem(
      AbstractClientPlayerEntity val
   ) {
      if (this.isEnchantedTotem(
         val.getMainHandStack()
      )) {
         return true;
      } else if (this.isEnchantedTotem(
         val.getOffHandStack()
      )) {
         return true;
      } else {
         for (ItemStack stack : val.getArmorItems()) {
            if (this.isEnchantedTotem(
               stack
            )) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isEnchantedTotem(
      ItemStack val
   ) {
      if (!val.isEmpty()
         && val.getItem()
            == Items.TOTEM_OF_UNDYING) {
         ItemEnchantmentsComponent item2 = (ItemEnchantmentsComponent)val.get(
            DataComponentTypes.ENCHANTMENTS
         );
         return item2
               != null
            && !item2.isEmpty();
      } else {
         return false;
      }
   }
}
