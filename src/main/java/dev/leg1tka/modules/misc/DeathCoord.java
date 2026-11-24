package dev.leg1tka.modules.misc;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventUpdate;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DeathCoord extends Module {
   public static DeathCoord INSTANCE = new DeathCoord();
   private final BooleanSetting copyToClipboard;
   private BlockPos deathPos;
   private boolean isDead;

   public DeathCoord() {
      super("DeathCoord", Category.MISC);
      String var1 = "DeathCoord";
      this.copyToClipboard = new BooleanSetting("Копировать в буфер", true);
      this.deathPos = null;
      this.isDead = false;
      this.addSettings(new Setting[]{this.copyToClipboard});
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.isDead = false;
      this.deathPos = null;
   }

   @EventLink
   public void onUpdate(
      EventUpdate val
   ) {
      if (mc.player != null && mc.world != null) {
         if (mc.player.getHealth() <= 0.0F && !this.isDead) {
            this.isDead = true;
            this.deathPos = mc.player.getBlockPos();
            String str = "X: "
               + this.deathPos.getX()
               + " Y: "
               + this.deathPos.getY()
               + " Z: "
               + this.deathPos.getZ();
            String str2 = this.getDimension();
            String str3 = "§cВы умерли! §f"
               + str
               + " §7("
               + str2
               + ")";
            mc.player
               .sendMessage(
                  Text.literal(
                     str3
                  ),
                  false
               );
            if (this.copyToClipboard.isState()) {
               mc.keyboard.setClipboard(this.deathPos.getX() + " " + this.deathPos.getY() + " " + this.deathPos.getZ());
            }
         }

         if (mc.player.getHealth() > 0.0F && this.isDead) {
            this.isDead = false;
         }
      }
   }

   private String getDimension() {
      if (mc.world == null) {
         return "Unknown";
      } else {
         String str = mc.world
            .getRegistryKey()
            .getValue()
            .toString();
         if (str.contains(
            "overworld"
         )) {
            return "Overworld";
         } else {
            if (str.contains(
               "nether"
            )) {
               return "Nether";
            } else {
               if (str.contains(
                  "end"
               )) {
                  return "End";
               } else {
                  return str;
               }
            }
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.isDead = false;
      this.deathPos = null;
   }
}
