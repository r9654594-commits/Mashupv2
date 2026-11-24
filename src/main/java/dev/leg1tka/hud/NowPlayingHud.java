package dev.leg1tka.hud;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class NowPlayingHud extends HudElement {
   private static final long POLL_INTERVAL_MS = 500L;
   private static final float HUD_HEIGHT = 16.0F;
   private static final float ICON_PLATE_W = 16.0F;
   private static final float ICON_PLATE_GAP = 2.5F;
   private static final float DIVIDER_W = 0.8F;
   private static final float PAD = 4.0F;
   private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
      val -> {
         Thread val2 = new Thread(
            val,
            "NowPlaying-Poller"
         );
         val2.setDaemon(
            true
         );
         return val2;
      }
   );
   private final AtomicReference<String> artist = new AtomicReference<>("No media");
   private final AtomicReference<String> title = new AtomicReference<>("Nothing is playing");
   private final AtomicReference<Long> positionSec = new AtomicReference<>(0L);
   private final AtomicReference<Long> durationSec = new AtomicReference<>(0L);
   private final AtomicReference<byte[]> pendingArtwork = new AtomicReference<>(null);
   private final AtomicBoolean artworkDirty = new AtomicBoolean(false);
   private Identifier artworkTexture = null;
   private byte[] lastArtworkPng = null;
   private float animatedProgress = 0.0F;

   public NowPlayingHud(
      Draggable val
   ) {
      super(
         val
      );
      this.scheduler.scheduleAtFixedRate(this::pollMedia, 0L, 500L, TimeUnit.MILLISECONDS);
   }

   private void pollMedia() {
      try {
         List<IClientAction_QB5XzjiV> list = IClientAction_EDlHLyjx.INSTANCE
            .getMediaSessions();
         IClientAction_QB5XzjiV val = list.stream()
            .filter(s -> this.hasMedia(s))
            .filter(
               val2 -> val2.getMedia()
                     .isPlaying()
            )
            .findFirst()
            .orElseGet(
               () -> list.stream()
                     .filter(s -> this.hasMedia(s))
                     .findFirst()
                     .orElse(null)
            );
         if (val
            == null) {
            this.clearMedia();
            return;
         }

         GettitleHelper val3 = val.getMedia();
         this.artist
            .set(
               this.truncate(
                  val3.getArtist(),
                  24
               )
            );
         this.title
            .set(
               this.truncate(
                  val3.getTitle(),
                  32
               )
            );
         this.positionSec
            .set(
               Math.max(
                  0L,
                  val3.getPosition()
               )
            );
         this.durationSec
            .set(
               Math.max(
                  0L,
                  val3.getDuration()
               )
            );
         byte[] val4 = val3.getArtworkPng();
         if (!Arrays.equals(
            val4,
            this.lastArtworkPng
         )) {
            this.pendingArtwork
               .set(
                  val4
               );
            this.artworkDirty.set(true);
         }
      } catch (Throwable var5) {
         this.clearMedia();
      }
   }

   private boolean hasMedia(
      IClientAction_QB5XzjiV val
   ) {
      if (val
            != null
         && val.getMedia()
            != null) {
         GettitleHelper val2 = val.getMedia();
         return val2.getArtist()
               != null
            && !val2.getArtist()
               .isBlank()
            && val2.getTitle()
               != null
            && !val2.getTitle()
               .isBlank();
      } else {
         return false;
      }
   }

   private void clearMedia() {
      this.artist.set("No media");
      this.title.set("Nothing is playing");
      this.positionSec.set(0L);
      this.durationSec.set(0L);
      this.pendingArtwork.set(null);
      this.artworkDirty.set(true);
   }

   private void updateArtworkTexture() {
      if (this.artworkDirty.compareAndSet(true, false)) {
         byte[] val = this.pendingArtwork
            .get();
         this.lastArtworkPng = val;
         if (this.artworkTexture != null && mc != null) {
            mc.getTextureManager().destroyTexture(this.artworkTexture);
            this.artworkTexture = null;
         }

         if (val
               != null
            && val.length
               >= 8) {
            try {
               NativeImage val2 = NativeImage.read(
                  new ByteArrayInputStream(
                     val
                  )
               );
               NativeImageBackedTexture text = new NativeImageBackedTexture(
                  val2
               );
               this.artworkTexture = Identifier.of(
                  "aspectdlc", "nowplaying_art_" + System.currentTimeMillis()
               );
               if (mc != null) {
                  mc.getTextureManager()
                     .registerTexture(
                        this.artworkTexture,
                        text
                     );
               }
            } catch (Throwable var4) {
               this.artworkTexture = null;
            }
         }
      }
   }

   private String truncate(
      String str,
      int n
   ) {
      if (str
            != null
         && !str.isBlank()
         )
       {
         String str2 = str.trim();
         return str2.length()
               > n
            ? str2.substring(
                  0,
                  n
                     - 1
               )
               + "…"
            : str2;
      } else {
         return "";
      }
   }

   private String formatTime(
      long time
   ) {
      long time2 = time
         / 60L;
      long time3 = time
         % 60L;
      return time2
         + ":"
         + String.format(
            "%02d",
            time3
         );
   }

   private int getThemeColor() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null) {
         ThemeColors color2 = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         if (color2
            == null) {
            return ColorUtil.getThemeColor();
         } else if ("Rainbow"
            .equals(
               color2.getName()
            )) {
            return ColorUtil.getThemeColor();
         } else {
            return color2.color
                     != null
                  && color2.color.length
                     > 0
               ? color2.color[0]
               : ColorUtil.getThemeColor();
         }
      } else {
         return ColorUtil.getThemeColor();
      }
   }

   @Override
   public void onRender(
      EventRender.Default event
   ) {
      this.updateArtworkTexture();
      MatrixStack matrices = event.getContext()
         .getMatrices();
      float width = this.draggable
         .getX();
      float width2 = this.draggable
         .getY();
      Font font = Fonts.getFont(
         "nunito", 13
      );
      Font font2 = Fonts.getFont(
         "nunito", 14
      );
      Font font3 = Fonts.getFont(
         "iconnew", 14
      );
      if (font
            != null
         && font2
            != null) {
         int color = this.getThemeColor();
         int color2 = -1;
         int color3 = ColorUtil.rgba(
            180,
            180,
            180,
            255
         );
         String text = this.artist
            .get();
         String text2 = this.title
            .get();
         long time = this.positionSec
            .get();
         long time2 = this.durationSec
            .get();
         String text3 = this.formatTime(
            time
         );
         float width3 = time2
               > 0L
            ? (float)time
               / (float)time2
            : 0.0F;
         this.animatedProgress = this.animatedProgress
            + (
                  width3
                     - this.animatedProgress
               )
               * 0.08F;
         float width4 = font2.getStringWidth(
            text
         );
         float width5 = font.getStringWidth(
            " "
               + text2
         );
         float width6 = font.getStringWidth(
            text3
         );
         float width7 = 4.0F
            + width4
            + width5
            + 4.0F
            + 0.8F
            + 4.0F
            + width6
            + 4.0F;
         float width8 = width
            + 16.0F
            + 2.5F;
         this.drawHudBackground(
            matrices,
            width,
            width2,
            16.0F,
            16.0F,
            color
         );
         if (this.artworkTexture != null) {
            float width9 = 1.5F;
            float width10 = 16.0F
               - width9
                  * 2.0F;
            Render2D.drawImage(
               matrices,
               this.artworkTexture,
               width
                  + width9,
               width2
                  + width9,
               width10,
               width10,
               -1
            );
         } else if (font3
            != null) {
            String text4 = "m";
            float width11 = font3.getStringWidth(
               text4
            );
            font3.drawGradientStringHorizontal(
               matrices,
               text4,
               width
                  + (
                        16.0F
                           - width11
                     )
                     * 0.5F,
               width2
                  + 6.4F,
               ColorUtil.getThemeColor(0),
               ColorUtil.getThemeColor(120)
            );
         }

         this.drawHudBackground(
            matrices,
            width8,
            width2,
            width7,
            16.0F,
            color
         );
         if (time2
            > 0L) {
            float width12 = 1.5F;
            float width13 = width2
               + 16.0F
               - width12;
            float width14 = width7
               * this.animatedProgress;
            Render2D.drawRoundedRect(
               matrices,
               width8,
               width13,
               width7,
               width12,
               0.75F,
               ColorUtil.rgba(
                  40,
                  40,
                  40,
                  120
               )
            );
            if (width14
               > 1.0F) {
               Render2D.drawGradientRect(
                  matrices,
                  width8,
                  width13,
                  width14,
                  width12,
                  0.75F,
                  color,
                  ColorUtil.getThemeColor(120),
                  true
               );
            }
         }

         float width15 = width8
            + 4.0F
            + width4
            + width5
            + 4.0F;
         Render2D.drawRoundedRect(
            matrices,
            width15,
            width2
               + 3.0F,
            0.8F,
            10.0F,
            0.4F,
            ColorUtil.rgba(
               80,
               80,
               80,
               180
            )
         );
         float width16 = width2
            + 6.5F;
         float width17 = width8
            + 4.0F;
         font2.drawString(
            matrices,
            text,
            width17,
            width16,
            color2
         );
         width17 += width4;
         font.drawString(
            matrices,
            " "
               + text2,
            width17,
            width16
               + 0.5F,
            color3
         );
         font.drawString(
            matrices,
            text3,
            width15
               + 0.8F
               + 4.0F,
            width16
               + 0.5F,
            color
         );
         this.draggable
            .setWidth(
               18.5F
                  + width7
            );
         this.draggable.setHeight(16.0F);
         super.onRender(
            event
         );
      }
   }

   public void shutdown() {
      this.scheduler.shutdownNow();
      if (this.artworkTexture != null && mc != null) {
         mc.getTextureManager().destroyTexture(this.artworkTexture);
         this.artworkTexture = null;
      }
   }
}
