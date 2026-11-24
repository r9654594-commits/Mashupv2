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


import java.io.PrintStream;

public class PrintlnHelper extends PrintStream {
   public PrintlnHelper(
      PrintStream val
   ) {
      super(
         val,
         true
      );
   }

   @Override
   public void println(
      String str
   ) {
      if (str
         != null) {
         for (String str2 : BaritoneBlocker.PTN) {
            if (str.contains(
               str2
            )) {
               return;
            }
         }
      }

      super.println(
         str
      );
   }

   @Override
   public void print(
      String str
   ) {
      if (str
         != null) {
         for (String str2 : BaritoneBlocker.PTN) {
            if (str.contains(
               str2
            )) {
               return;
            }
         }
      }

      super.print(
         str
      );
   }
}
