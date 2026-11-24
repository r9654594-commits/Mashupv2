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


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineEvent.Type;
import lombok.Generated;

public final class PlaysoundHelper {
   private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(
      val -> {
         Thread val2 = new Thread(
            val,
            "AspectDLC-ClientSounds"
         );
         val2.setDaemon(
            true
         );
         return val2;
      }
   );

   public static void playSound(
      String str,
      double d,
      float f
   ) {
      EXECUTOR.execute(
         () -> playInternal(
               str,
               d,
               f
            )
      );
   }

   private static void playInternal(
      String str,
      double d,
      float f
   ) {
      String str2 = "/assets/"
         + ClientAssets.assets()
         + "/sounds/"
         + str;

      try {
         try (InputStream val = PlaysoundHelper.class
               .getResourceAsStream(
                  str2
               )) {
            if (val
               != null) {
               try (
                  BufferedInputStream val2 = new BufferedInputStream(
                     val
                  );
                  AudioInputStream val3 = AudioSystem.getAudioInputStream(
                     val2
                  );
                  AudioInputStream val4 = resampleStream(
                     val3,
                     f
                  );
               ) {
                  Clip val5 = AudioSystem.getClip();
                  val5.addLineListener(
                     val6 -> {
                        if (val6.getType()
                           == Type.STOP) {
                           val5.close();
                        }
                     }
                  );
                  val5.open(
                     val4
                  );
                  setVolume(
                     val5,
                     d
                  );
                  val5.start();
                  return;
               }
            }
         }
      } catch (IOException | LineUnavailableException | UnsupportedAudioFileException var19) {
      }
   }

   private static AudioInputStream resampleStream(
      AudioInputStream val,
      float x
   ) throws IOException {
      AudioFormat val2 = val.getFormat();
      byte[] val3 = val.readAllBytes();
      float x2 = val2.getSampleRate()
         * Math.max(
            0.5F,
            Math.min(
               2.0F,
               x
            )
         );
      AudioFormat val4 = new AudioFormat(
         x2,
         val2.getSampleSizeInBits(),
         val2.getChannels(),
         true,
         val2.isBigEndian()
      );
      return new AudioInputStream(
         new ByteArrayInputStream(
            val3
         ),
         val4,
         (long)(
            val3.length
               / val4.getFrameSize()
         )
      );
   }

   private static void setVolume(
      Clip val,
      double d
   ) {
      if (val.isControlSupported(
         javax.sound.sampled.FloatControl.Type.MASTER_GAIN
      )) {
         double x = Math.max(
            0.0,
            Math.min(
               1.0,
               d
            )
         );
         FloatControl val2 = (FloatControl)val.getControl(
            javax.sound.sampled.FloatControl.Type.MASTER_GAIN
         );
         float x2 = (float)(
            Math.log10(
                  x
                        <= 0.0
                     ? 1.0E-4
                     : x
               )
               * 20.0
         );
         val2.setValue(
            x2
         );
      }
   }

   @Generated
   private PlaysoundHelper() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
