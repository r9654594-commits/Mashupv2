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


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GetmediasessionsHelper_1 implements IClientAction_EDlHLyjx {
   private static volatile boolean loadFailed;

   @Override
   public native List<IClientAction_QB5XzjiV> getMediaSessions();

   public static boolean isLoadFailed() {
      return loadFailed;
   }

   static {
      try {
         Path val = Files.createTempDirectory(
            "mediaplayerinfo-"
         );
         Path val2 = val.resolve(
            "MediaPlayerInfo.dll"
         );

         try (InputStream val3 = GetmediasessionsHelper_1.class
               .getResourceAsStream("/mediaplayerinfo/natives/win/MediaPlayerInfo.dll")) {
            if (val3
               == null) {
               throw new IOException("Resource not found: /mediaplayerinfo/natives/win/MediaPlayerInfo.dll");
            }

            Files.write(
               val2,
               val3.readAllBytes()
            );
         }

         System.load(
            val2.toAbsolutePath()
               .toString()
         );

         try {
            Files.deleteIfExists(
               val2
            );
            Files.deleteIfExists(
               val
            );
         } catch (IOException var6) {
            val2.toFile()
               .deleteOnExit();
            val.toFile()
               .deleteOnExit();
         }
      } catch (Throwable var8) {
         loadFailed = true;
      }
   }
}
