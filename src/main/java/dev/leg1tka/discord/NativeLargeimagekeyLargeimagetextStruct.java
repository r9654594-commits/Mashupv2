package dev.leg1tka.discord;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class NativeLargeimagekeyLargeimagetextStruct extends Structure {
   public String largeImageKey;
   public String largeImageText;
   public String smallImageText;
   public String partyPrivacy;
   public long startTimestamp;
   public int instance;
   public String partyId;
   public int partySize;
   public long endTimestamp;
   public String details;
   public String joinSecret;
   public String spectateSecret;
   public String smallImageKey;
   public String matchSecret;
   public String state;
   public int partyMax;
   public String button_url_1;
   public String button_label_1;
   public String button_url_2;
   public String button_label_2;

   public NativeLargeimagekeyLargeimagetextStruct() {
      this.setStringEncoding(
         "UTF-8"
      );
   }

   protected List<String> getFieldOrder() {
      return Arrays.asList(new String[]{"state", "details", "startTimestamp", "endTimestamp", "largeImageKey", "largeImageText", "smallImageKey", "smallImageText", "partyId", "partySize", "partyMax", "partyPrivacy", "matchSecret", "joinSecret", "spectateSecret", "button_label_1", "button_url_1", "button_label_2", "button_url_2", "instance"});
   }
}
