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


import java.awt.Color;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public final class ChatUtil {
   public static void sendMessage(
      Object val
   ) {
      MinecraftClient val2 = MinecraftClient.getInstance();
      if (val2.player
         == null) {
         System.out
            .println(
               "AspectDLC "
                  + val
            );
      } else {
         MutableText text = Text.literal(
            ""
         );
         String text2 = "AspectDLC";

         for (int i = 0;
            i
               < text2.length();
            i++
         ) {
            text.append(
               Text.literal(
                     String.valueOf(
                        text2.charAt(
                           i
                        )
                     )
                  )
                  .setStyle(
                     Style.EMPTY
                        .withBold(true)
                        .withColor(
                           TextColor.fromRgb(
                              ColorUtil.gradient(
                                 ColorUtil.getThemeColor(0),
                                 ColorUtil.getThemeColor(90),
                                 (float)i
                                    / (float)text2.length()
                              )
                           )
                        )
                  )
            );
         }

         text.append(
            Text.literal(" -> ")
               .setStyle(
                  Style.EMPTY
                     .withBold(false)
                     .withColor(
                        TextColor.fromRgb(
                           new Color(
                                 200,
                                 200,
                                 200
                              )
                              .getRGB()
                        )
                     )
               )
         );
         text.append(
            Text.literal(
                  String.valueOf(
                     val
                  )
               )
               .setStyle(
                  Style.EMPTY
                     .withBold(false)
                     .withColor(
                        TextColor.fromRgb(
                           new Color(
                                 200,
                                 200,
                                 200
                              )
                              .getRGB()
                        )
                     )
               )
         );
         val2.player
            .sendMessage(
               text,
               false
            );
      }
   }

   @Generated
   private ChatUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
