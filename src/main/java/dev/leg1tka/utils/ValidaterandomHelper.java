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


import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ValidaterandomHelper extends Random {
   private final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
   private Random random = null;
   private volatile boolean seedSet;
   private volatile boolean seedUpdated;
   private volatile long seed;

   private void validateRandom() {
      if (this.random == null) {
         this.random = new Random(this.seed);
         this.seedUpdated = false;
      } else if (this.seedUpdated) {
         this.random.setSeed(this.seed);
         this.seedUpdated = false;
      }
   }

   public static long mix(
      long time,
      long time2
   ) {
      time *= time
            * 6364136223846793005L
         + 1442695040888963407L;
      return time
         + time2;
   }

   @Override
   public void setSeed(
      long time
   ) {
      this.seed = time;
      this.seedSet = true;
      this.seedUpdated = true;
   }

   @Override
   public void nextBytes(
      byte[] val
   ) {
      if (this.seedSet) {
         this.validateRandom();
         this.random
            .nextBytes(
               val
            );
      } else {
         this.threadLocalRandom
            .nextBytes(
               val
            );
      }
   }

   @Override
   public int nextInt() {
      if (this.seedSet) {
         this.validateRandom();
         return this.random.nextInt();
      } else {
         return this.threadLocalRandom.nextInt();
      }
   }

   @Override
   public int nextInt(
      int n
   ) {
      if (this.seedSet) {
         this.validateRandom();
         return this.random
            .nextInt(
               n
            );
      } else {
         return this.threadLocalRandom
            .nextInt(
               n
            );
      }
   }

   @Override
   public long nextLong() {
      if (this.seedSet) {
         this.validateRandom();
         return this.random.nextLong();
      } else {
         return this.threadLocalRandom.nextLong();
      }
   }

   @Override
   public boolean nextBoolean() {
      if (this.seedSet) {
         this.validateRandom();
         return this.random.nextBoolean();
      } else {
         return this.threadLocalRandom.nextBoolean();
      }
   }

   @Override
   public float nextFloat() {
      if (this.seedSet) {
         this.validateRandom();
         return this.random.nextFloat();
      } else {
         return this.threadLocalRandom.nextFloat();
      }
   }

   @Override
   public double nextDouble() {
      if (this.seedSet) {
         this.validateRandom();
         return this.random.nextDouble();
      } else {
         return this.threadLocalRandom.nextDouble();
      }
   }

   @Override
   public double nextGaussian() {
      if (this.seedSet) {
         this.validateRandom();
         return this.random.nextGaussian();
      } else {
         return this.threadLocalRandom.nextGaussian();
      }
   }
}
