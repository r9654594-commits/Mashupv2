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


import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventPacket;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

public class TpsCalc {
   private float TPS = 20.0F;
   private float adjustTicks = 0.0F;
   private long timestamp;
   private long lastPacketTime;
   private static final int SAMPLE_SIZE = 20;
   private final float[] tpsSamples = new float[20];
   private int sampleIndex = 0;

   @EventLink
   public void onPacket(
      EventPacket event
   ) {
      if (event.getType()
            == EventPacket.Type.RECEIVE
         && event.getPacket() instanceof WorldTimeUpdateS2CPacket
         )
       {
         this.updateTPS();
      }
   }

   public float getTPS() {
      if (this.lastPacketTime == 0L) {
         return this.TPS;
      } else {
         MinecraftClient val = MinecraftClient.getInstance();
         return val
                  != null
               && val.getNetworkHandler()
                  != null
               && System.currentTimeMillis() - this.lastPacketTime <= 3500L
            ? this.TPS
            : 20.0F;
      }
   }

   private void updateTPS() {
      long time = System.nanoTime();
      this.lastPacketTime = System.currentTimeMillis();
      if (this.timestamp == 0L) {
         this.timestamp = time;
      } else {
         long time2 = time
            - this.timestamp;
         this.timestamp = time;
         if (time2
            > 0L) {
            float x = 20.0F;
            float x2 = x
               * (
                  1.0E9F
                     / (float)time2
               );
            float x3 = MathHelper.clamp(
               x2,
               0.0F,
               x
            );
            this.tpsSamples[this.sampleIndex % 20] = x3;
            this.sampleIndex++;
            int i = Math.min(
               this.sampleIndex, 20
            );
            float x4 = 0.0F;

            for (int i2 = 0;
               i2
                  < i;
               i2++
            ) {
               float x5 = this.tpsSamples[i2];
               x4 += x5;
            }

            this.TPS = (float)this.round(
               (double)(
                  x4
                     / (float)i
               )
            );
            this.adjustTicks = this.TPS
               - x;
         }
      }
   }

   public double round(
      double d
   ) {
      return (double)Math.round(
            d
               * 10.0
         )
         / 10.0;
   }

   @Generated
   public float getAdjustTicks() {
      return this.adjustTicks;
   }

   @Generated
   public long getTimestamp() {
      return this.timestamp;
   }

   @Generated
   public long getLastPacketTime() {
      return this.lastPacketTime;
   }

   @Generated
   public float[] getTpsSamples() {
      return this.tpsSamples;
   }

   @Generated
   public int getSampleIndex() {
      return this.sampleIndex;
   }
}
