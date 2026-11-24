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


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Generated;

public final class ResetHelper_14 {
   public static void reset(
      String str
   ) throws IOException {
      Path val = Paths.get(
         str
      );
      if (Files.exists(
         val
      )) {
         new File(
               str
            )
            .delete();
      }

      Files.createFile(
         val
      );
   }

   public static boolean exists(
      String str
   ) {
      return Files.exists(
         Paths.get(
            str
         )
      );
   }

   @Generated
   private ResetHelper_14() {
      throw new UnsupportedOperationException(
         "This is a utility class and cannot be instantiated"
      );
   }
}
