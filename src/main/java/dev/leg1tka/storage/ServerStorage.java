package dev.leg1tka.storage;
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
import dev.leg1tka.utils.*;


import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventPopTotem;
import dev.leg1tka.api.events.implement.EventTickPre;
import java.lang.reflect.InvocationTargetException;
import lombok.Generated;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class ServerStorage implements IMinecraft {
   private int serverSlot;
   private float serverYaw;
   private float serverPitch;
   private float fallDistance;
   private double serverX;
   private double serverY;
   private double serverZ;
   private boolean serverOnGround;
   private boolean serverSprinting;
   private boolean serverSneaking;
   private boolean serverHorizontalCollision;

   public ServerStorage() {
      EventInvoker.register(this);
   }

   @EventLink
   public void onTick(
      EventTickPre event
   ) {
      if (mc.player != null && mc.world != null) {
         double dist = mc.player
               .prevY
            - mc.player.getY();
         if (mc.player.isOnGround()) {
            this.fallDistance = 0.0F;
         } else if (dist
            > 0.0) {
            this.fallDistance += (float)dist;
         }
      }
   }

   @EventLink
   public void onPacketSend(
      EventPacket event
   ) {
      if (mc.player != null && mc.world != null) {
         if (event.getPacket() instanceof PlayerMoveC2SPacket val
            )
          {
            if (val.changesPosition()
               )
             {
               this.serverX = val.getX(
                  mc.player.getX()
               );
               this.serverY = val.getY(
                  mc.player.getY()
               );
               this.serverZ = val.getZ(
                  mc.player.getZ()
               );
            }

            if (val.changesLook()
               )
             {
               this.serverYaw = val.getYaw(
                  mc.player.getYaw()
               );
               this.serverPitch = val.getPitch(
                  mc.player.getPitch()
               );
            }

            this.serverOnGround = val.isOnGround();
            this.serverHorizontalCollision = val.horizontalCollision();
         }

         if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket val2
            )
          {
            this.serverSlot = val2.getSelectedSlot();
         }

         if (event.getPacket() instanceof ClientCommandC2SPacket val3
            )
          {
            switch (val3.getMode()) {
               case START_SPRINTING:
                  if (this.serverSprinting) {
                     event.cancel();
                  }

                  this.serverSprinting = true;
                  break;
               case STOP_SPRINTING:
                  if (!this.serverSprinting) {
                     event.cancel();
                  }

                  this.serverSprinting = false;
                  break;
               case PRESS_SHIFT_KEY:
                  this.serverSneaking = true;
                  break;
               case RELEASE_SHIFT_KEY:
                  this.serverSneaking = false;
            }
         }
      }
   }

   @EventLink
   public void onPacketReceive(
      EventPacket event
   ) throws InvocationTargetException, IllegalAccessException, InstantiationException {
      if (mc.player != null && mc.world != null) {
         if (event.getPacket() instanceof EntityStatusS2CPacket val
            && val.getStatus()
               == 35) {
            if (!(
               val.getEntity(
                  mc.world
               ) instanceof PlayerEntity player2
            )) {
               return;
            }

            EventInvoker.invoke(
               new EventPopTotem(
                  player2
               )
            );
         }
      }
   }

   @Generated
   public int getServerSlot() {
      return this.serverSlot;
   }

   @Generated
   public float getServerYaw() {
      return this.serverYaw;
   }

   @Generated
   public float getServerPitch() {
      return this.serverPitch;
   }

   @Generated
   public float getFallDistance() {
      return this.fallDistance;
   }

   @Generated
   public double getServerX() {
      return this.serverX;
   }

   @Generated
   public double getServerY() {
      return this.serverY;
   }

   @Generated
   public double getServerZ() {
      return this.serverZ;
   }

   @Generated
   public boolean isServerOnGround() {
      return this.serverOnGround;
   }

   @Generated
   public boolean isServerSprinting() {
      return this.serverSprinting;
   }

   @Generated
   public boolean isServerSneaking() {
      return this.serverSneaking;
   }

   @Generated
   public boolean isServerHorizontalCollision() {
      return this.serverHorizontalCollision;
   }
}
