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



import dev.leg1tka.modules.misc.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PushHelper {
   public static final long DURATION_MS = 2500L;
   private static final long MODULE_SOUND_STARTUP_MUTE_MS = 4000L;
   private static final long INIT_TIME_MS = System.currentTimeMillis();
   private static final List<IscustomHelper> entries = new ArrayList<>();

   public static void push(
      String str,
      String str2,
      boolean flag
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         entries.add(
            new IscustomHelper(
               str,
               str2,
               flag,
               null,
               null,
               System.currentTimeMillis()
            )
         );
         playModuleSound(
            flag
         );
      }
   }

   public static void pushCustom(
      String str,
      String str2
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         entries.add(
            new IscustomHelper(
               str,
               str2,
               false,
               str,
               null,
               System.currentTimeMillis()
            )
         );
      }
   }

   public static void pushItemNotification(
      String str,
      ItemStack stack
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         entries.add(
            new IscustomHelper(
               str,
               null,
               false,
               str,
               stack,
               System.currentTimeMillis()
            )
         );
      }
   }

   public static List<IscustomHelper> getActive() {
      long time = System.currentTimeMillis();
      Iterator<IscustomHelper> val = entries.iterator();

      while (
         val.hasNext()
      ) {
         IscustomHelper val2 = val.next();
         if (time
               - val2.startTime
            > 2500L) {
            val.remove();
         }
      }

      return entries;
   }

   private static void playModuleSound(
      boolean flag
   ) {
      if (System.currentTimeMillis() - INIT_TIME_MS >= 4000L) {
         ClientSounds val = ClientSounds.INSTANCE;
         if (val
               != null
            && val.isEnable()
            )
          {
            String str = val.stateSounds
               .getCurrent();
            if ("Celestial"
               .equals(
                  str
               )) {
               String str2 = flag
                  ? "celestial_enable"
                  : "celestial_disable";
               float x = 1.0F;
               float x2 = val.volume
                     .get()
                  / val.volume
                     .getMax();

               try {
                  MinecraftClient val2 = MinecraftClient.getInstance();
                  if (val2
                        != null
                     && val2.getSoundManager()
                        != null) {
                     SoundEvent event = SoundEvent.of(
                        Identifier.of(
                           "aspectdlc",
                           str2
                        )
                     );
                     val2.getSoundManager()
                        .play(
                           PositionedSoundInstance.master(
                              event,
                              x,
                              x2
                           )
                        );
                  }
               } catch (Exception var8) {
               }
            }
         }
      }
   }
}
