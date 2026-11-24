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


import java.util.regex.Pattern;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.PlainTextContent.Literal;
import net.minecraft.util.Formatting;

public class ReplaceHelper {
   public static Text replace(
      Text text,
      String str,
      String str2
   ) {
      if (text
            != null
         && str
            != null
         && str2
            != null) {
         MutableText text2 = Text.empty()
            .setStyle(
               text.getStyle()
            );
         appendReplaced(
            text2,
            text,
            str,
            str2
         );
         return text2;
      } else {
         return text;
      }
   }

   private static void appendReplaced(
      MutableText text,
      Text text2,
      String str,
      String str2
   ) {
      TextContent text3 = text2.getContent();
      Style style = text2.getStyle();
      if (text3 instanceof Literal val
         )
       {
         Pattern val2 = Pattern.compile(
            Pattern.quote(
               str
            ),
            2
         );
         String text4 = val2.matcher(
               val.string()
            )
            .replaceAll(
               str2
            );
         text.append(
            Text.literal(
                  text4
               )
               .setStyle(
                  style
               )
         );
      }

      for (Text text5 : text2.getSiblings()) {
         appendReplaced(
            text,
            text5,
            str,
            str2
         );
      }
   }

   public static String replaceSymbols(
      String str
   ) {
      return str.replaceAll(
            "\ua517", Formatting.BLUE + "MODER"
         )
         .replaceAll("\ua525", Formatting.BLUE + "ST.MODER")
         .replaceAll("\ua521", Formatting.LIGHT_PURPLE + "MODER+")
         .replaceAll("\ua500", Formatting.GRAY + "PLAYER")
         .replaceAll("\ua509", Formatting.YELLOW + "HELPER")
         .replaceAll("◆", "@")
         .replaceAll("┃", "|")
         .replaceAll("\ua546", Formatting.YELLOW + "PEGAS")
         .replaceAll("\ua538", Formatting.YELLOW + "GOD")
         .replaceAll("\ua533", Formatting.AQUA + "Ml.admin")
         .replaceAll("\ua505", Formatting.RED + "Y" + Formatting.WHITE + "T")
         .replaceAll("\ua502", Formatting.BLUE + "D.MODER")
         .replaceAll("\ua560", Formatting.YELLOW + "D.HELPER")
         .replaceAll("\ua544", Formatting.RED + "VAMPIRE")
         .replaceAll("\ua516", Formatting.AQUA + "OVERLORD")
         .replaceAll("\ua548", Formatting.GREEN + "COBRA")
         .replaceAll("\ua528", Formatting.LIGHT_PURPLE + "DRAGON")
         .replaceAll("\ua524", Formatting.RED + "IMPERATOR")
         .replaceAll("\ua520", Formatting.GOLD + "MAGISTER")
         .replaceAll("\ua504", Formatting.BLUE + "HERO")
         .replaceAll("\ua512", Formatting.GREEN + "AVENGER")
         .replaceAll("\ua552", Formatting.WHITE + "RABBIT")
         .replaceAll("\ua508", Formatting.YELLOW + "TITAN")
         .replaceAll("\ua540", Formatting.DARK_GREEN + "HYDRA")
         .replaceAll("\ua536", Formatting.GOLD + "TIGER")
         .replaceAll("\ua532", Formatting.DARK_PURPLE + "BULL")
         .replaceAll("\ua556", Formatting.BLACK + "BUNNY")
         .replaceAll("\ua557\ua558", Formatting.YELLOW + "SPONSOR")
         .replaceAll("🔥", "@")
         .replaceAll("ᴀ", "A")
         .replaceAll("ʙ", "B")
         .replaceAll("ᴄ", "C")
         .replaceAll("ᴅ", "D")
         .replaceAll("ᴇ", "E")
         .replaceAll("ғ", "F")
         .replaceAll("ɢ", "G")
         .replaceAll("ʜ", "H")
         .replaceAll("ɪ", "I")
         .replaceAll("ᴊ", "J")
         .replaceAll("ᴋ", "K")
         .replaceAll("ʟ", "L")
         .replaceAll("ᴍ", "M")
         .replaceAll("ɴ", "N")
         .replaceAll("ꜱ", "S")
         .replaceAll("s", "S")
         .replaceAll("ᴏ", "O")
         .replaceAll("ᴘ", "P")
         .replaceAll("\u01eb", "Q")
         .replaceAll("ʀ", "R")
         .replaceAll("ᴛ", "T")
         .replaceAll("ᴜ", "U")
         .replaceAll("ᴠ", "V")
         .replaceAll("ᴡ", "W")
         .replaceAll("ꜰ", "F")
         .replaceAll("x", "X")
         .replaceAll("ʏ", "Y")
         .replaceAll("ᴢ", "Z");
   }

   public static Text replaceSymbols(
      Text text
   ) {
      if (text.getString()
         .contains("\ua517")) {
         text = replace(
            text,
            "\ua517",
            Formatting.BLUE + "MODER"
         );
      }

      if (text.getString()
         .contains("\ua525")) {
         text = replace(
            text,
            "\ua525",
            Formatting.BLUE + "ST.MODER"
         );
      }

      if (text.getString()
         .contains("\ua521")) {
         text = replace(
            text,
            "\ua521",
            Formatting.LIGHT_PURPLE + "MODER+"
         );
      }

      if (text.getString()
         .contains("\ua500")) {
         text = replace(
            text,
            "\ua500",
            Formatting.GRAY + "PLAYER"
         );
      }

      if (text.getString()
         .contains("\ua509")) {
         text = replace(
            text,
            "\ua509",
            Formatting.YELLOW + "HELPER"
         );
      }

      if (text.getString()
         .contains("◆")) {
         text = replace(
            text,
            "◆",
            "@"
         );
      }

      if (text.getString()
         .contains("┃")) {
         text = replace(
            text,
            "┃",
            "|"
         );
      }

      if (text.getString()
         .contains("\ua533")) {
         text = replace(
            text,
            "\ua533",
            Formatting.AQUA + "Ml.admin"
         );
      }

      if (text.getString()
         .contains("\ua505")) {
         text = replace(
            text,
            "\ua505",
            Formatting.RED + "Y" + Formatting.WHITE + "T"
         );
      }

      if (text.getString()
         .contains("\ua502")) {
         text = replace(
            text,
            "\ua502",
            Formatting.BLUE + "D.MODER"
         );
      }

      if (text.getString()
         .contains("\ua560")) {
         text = replace(
            text,
            "\ua560",
            Formatting.YELLOW + "D.HELPER"
         );
      }

      if (text.getString()
         .contains("\ua544")) {
         text = replace(
            text,
            "\ua544",
            Formatting.RED + "DRACULA"
         );
      }

      if (text.getString()
         .contains("\ua516")) {
         text = replace(
            text,
            "\ua516",
            Formatting.AQUA + "OVERLORD"
         );
      }

      if (text.getString()
         .contains("\ua548")) {
         text = replace(
            text,
            "\ua548",
            Formatting.GREEN + "COBRA"
         );
      }

      if (text.getString()
         .contains("\ua528")) {
         text = replace(
            text,
            "\ua528",
            Formatting.LIGHT_PURPLE + "DRAGON"
         );
      }

      if (text.getString()
         .contains("\ua524")) {
         text = replace(
            text,
            "\ua524",
            Formatting.RED + "IMPERATOR"
         );
      }

      if (text.getString()
         .contains("\ua520")) {
         text = replace(
            text,
            "\ua520",
            Formatting.GOLD + "MAGISTER"
         );
      }

      if (text.getString()
         .contains("\ua504")) {
         text = replace(
            text,
            "\ua504",
            Formatting.BLUE + "HERO"
         );
      }

      if (text.getString()
         .contains("\ua512")) {
         text = replace(
            text,
            "\ua512",
            Formatting.GREEN + "AVENGER"
         );
      }

      if (text.getString()
         .contains("\ua552")) {
         text = replace(
            text,
            "\ua552",
            Formatting.WHITE + "RABBIT"
         );
      }

      if (text.getString()
         .contains("\ua508")) {
         text = replace(
            text,
            "\ua508",
            Formatting.YELLOW + "TITAN"
         );
      }

      if (text.getString()
         .contains("\ua540")) {
         text = replace(
            text,
            "\ua540",
            Formatting.DARK_GREEN + "HYDRA"
         );
      }

      if (text.getString()
         .contains("\ua536")) {
         text = replace(
            text,
            "\ua536",
            Formatting.GOLD + "TIGER"
         );
      }

      if (text.getString()
         .contains("\ua532")) {
         text = replace(
            text,
            "\ua532",
            Formatting.DARK_PURPLE + "BULL"
         );
      }

      if (text.getString()
         .contains("\ua556")) {
         text = replace(
            text,
            "\ua556",
            Formatting.BLACK + "BUNNY"
         );
      }

      if (text.getString()
         .contains("\ua557\ua558")) {
         text = replace(
            text,
            "\ua557\ua558",
            Formatting.YELLOW + "SPONSOR"
         );
      }

      if (text.getString()
         .contains("🔥")) {
         text = replace(
            text,
            "🔥",
            "@"
         );
      }

      if (text.getString()
         .contains("ᴀ")) {
         text = replace(
            text,
            "ᴀ",
            "A"
         );
      }

      if (text.getString()
         .contains("ʙ")) {
         text = replace(
            text,
            "ʙ",
            "B"
         );
      }

      if (text.getString()
         .contains("ᴄ")) {
         text = replace(
            text,
            "ᴄ",
            "C"
         );
      }

      if (text.getString()
         .contains("ᴅ")) {
         text = replace(
            text,
            "ᴅ",
            "D"
         );
      }

      if (text.getString()
         .contains("ᴇ")) {
         text = replace(
            text,
            "ᴇ",
            "E"
         );
      }

      if (text.getString()
         .contains("ғ")) {
         text = replace(
            text,
            "ғ",
            "F"
         );
      }

      if (text.getString()
         .contains("ɢ")) {
         text = replace(
            text,
            "ɢ",
            "G"
         );
      }

      if (text.getString()
         .contains("ʜ")) {
         text = replace(
            text,
            "ʜ",
            "H"
         );
      }

      if (text.getString()
         .contains("ɪ")) {
         text = replace(
            text,
            "ɪ",
            "I"
         );
      }

      if (text.getString()
         .contains("ᴊ")) {
         text = replace(
            text,
            "ᴊ",
            "J"
         );
      }

      if (text.getString()
         .contains("ᴋ")) {
         text = replace(
            text,
            "ᴋ",
            "K"
         );
      }

      if (text.getString()
         .contains("ʟ")) {
         text = replace(
            text,
            "ʟ",
            "L"
         );
      }

      if (text.getString()
         .contains("ᴍ")) {
         text = replace(
            text,
            "ᴍ",
            "M"
         );
      }

      if (text.getString()
         .contains("ɴ")) {
         text = replace(
            text,
            "ɴ",
            "N"
         );
      }

      if (text.getString()
         .contains("ꜱ")) {
         text = replace(
            text,
            "ꜱ",
            "S"
         );
      }

      if (text.getString()
         .contains("s")) {
         text = replace(
            text,
            "s",
            "S"
         );
      }

      if (text.getString()
         .contains("ᴏ")) {
         text = replace(
            text,
            "ᴏ",
            "O"
         );
      }

      if (text.getString()
         .contains("ᴘ")) {
         text = replace(
            text,
            "ᴘ",
            "P"
         );
      }

      if (text.getString()
         .contains("\u01eb")) {
         text = replace(
            text,
            "\u01eb",
            "Q"
         );
      }

      if (text.getString()
         .contains("ʀ")) {
         text = replace(
            text,
            "ʀ",
            "R"
         );
      }

      if (text.getString()
         .contains("ᴛ")) {
         text = replace(
            text,
            "ᴛ",
            "T"
         );
      }

      if (text.getString()
         .contains("ᴜ")) {
         text = replace(
            text,
            "ᴜ",
            "U"
         );
      }

      if (text.getString()
         .contains("ᴠ")) {
         text = replace(
            text,
            "ᴠ",
            "V"
         );
      }

      if (text.getString()
         .contains("ᴡ")) {
         text = replace(
            text,
            "ᴡ",
            "W"
         );
      }

      if (text.getString()
         .contains("ꜰ")) {
         text = replace(
            text,
            "ꜰ",
            "F"
         );
      }

      if (text.getString()
         .contains("x")) {
         text = replace(
            text,
            "x",
            "X"
         );
      }

      if (text.getString()
         .contains("ʏ")) {
         text = replace(
            text,
            "ʏ",
            "Y"
         );
      }

      if (text.getString()
         .contains("ᴢ")) {
         text = replace(
            text,
            "ᴢ",
            "Z"
         );
      }

      return text;
   }
}
