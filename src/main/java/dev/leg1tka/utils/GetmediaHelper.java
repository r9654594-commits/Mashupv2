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


public class GetmediaHelper implements IClientAction_QB5XzjiV {
   private final GettitleHelper media;
   private final String owner;
   private final int index;
   public static int cycle = -1;

   public GetmediaHelper(
      GettitleHelper val,
      String str,
      int n
   ) {
      this.media = val;
      this.owner = str;
      this.index = n;
   }

   @Override
   public GettitleHelper getMedia() {
      return this.media;
   }

   @Override
   public String getOwner() {
      return this.owner;
   }

   @Override
   public native void play();

   @Override
   public native void pause();

   @Override
   public native void playPause();

   @Override
   public native void stop();

   @Override
   public native void next();

   @Override
   public native void previous();

   @Override
   public native void swapCycle();

   @Override
   public native int getCycleType();

   public static int getCycle() {
      return cycle;
   }

   public static void setCycle(
      int n
   ) {
      cycle = n;
   }
}
