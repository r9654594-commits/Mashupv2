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


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import javax.imageio.ImageIO;

public class GettitleHelper implements Serializable {
   private final String title;
   private final String artist;
   private final byte[] artworkPng;
   private final long position;
   private final long duration;
   private final boolean playing;

   public GettitleHelper(
      String str,
      String str2,
      byte[] val,
      long time,
      long time2,
      boolean flag
   ) {
      this.title = str;
      this.artist = str2;
      this.artworkPng = val;
      this.position = time;
      this.duration = time2;
      this.playing = flag;
   }

   public String getTitle() {
      return this.title;
   }

   public String getArtist() {
      return this.artist;
   }

   public byte[] getArtworkPng() {
      return this.artworkPng;
   }

   public long getPosition() {
      return this.position;
   }

   public long getDuration() {
      return this.duration;
   }

   public boolean isPlaying() {
      return this.playing;
   }

   public BufferedImage getArtwork() {
      try {
         return ImageIO.read(new ByteArrayInputStream(this.artworkPng));
      } catch (Exception var2) {
         return null;
      }
   }

   @Override
   public boolean equals(
      Object val
   ) {
      if (this
         == val
         )
       {
         return true;
      } else if (val
            != null
         && this.getClass()
            == val.getClass()
         )
       {
         GettitleHelper val2 = (GettitleHelper)val;
         return this.position
               == val2.position
            && this.duration
               == val2.duration
            && this.playing
               == val2.playing
            && Objects.equals(
               this.title,
               val2.title
            )
            && Objects.equals(
               this.artist,
               val2.artist
            )
            && Arrays.equals(
               this.artworkPng,
               val2.artworkPng
            );
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      Object[] var10000 = new Object[5];
      var10000[0] = this.title;
      var10000[1] = this.artist;
      var10000[2] = this.position;
      var10000[3] = this.duration;
      var10000[4] = this.playing;
      int y = Objects.hash(
         var10000
      );
      return 31
            * y
         + Arrays.hashCode(this.artworkPng);
   }

   @Override
   public String toString() {
      return "MediaInfo{title='"
         + this.title
         + "', artist='"
         + this.artist
         + "', position="
         + this.position
         + ", duration="
         + this.duration
         + ", playing="
         + this.playing
         + "}";
   }
}
