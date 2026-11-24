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


import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetsmallimageHelper {
   private final NativeLargeimagekeyLargeimagetextStruct richPresence = new NativeLargeimagekeyLargeimagetextStruct();

   public SetsmallimageHelper setSmallImage(
      String str
   ) {
      return this.setSmallImage(
         str,
         ""
      );
   }

   public SetsmallimageHelper setDetails(
      String str
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         this.richPresence.details = str.substring(
            0,
            Math.min(
               str.length(),
               128
            )
         );
      }

      return this;
   }

   public SetsmallimageHelper setLargeImage(
      String str,
      String str2
   ) {
      this.richPresence.largeImageKey = str;
      this.richPresence.largeImageText = str2;
      return this;
   }

   public SetsmallimageHelper setState(
      String str
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         this.richPresence.state = str.substring(
            0,
            Math.min(
               str.length(),
               128
            )
         );
      }

      return this;
   }

   public SetsmallimageHelper setInstance(
      boolean flag
   ) {
      if ((this.richPresence.button_label_1 == null || !this.richPresence.button_label_1.isEmpty())
         && (this.richPresence.button_label_2 == null || !this.richPresence.button_label_2.isEmpty())) {
         this.richPresence.instance = flag
            ? 1
            : 0;
      }

      return this;
   }

   public SetsmallimageHelper setButtons(
      WebLink val
   ) {
      return this.setButtons(
         Collections.singletonList(
            val
         )
      );
   }

   public SetsmallimageHelper setSmallImage(
      String str,
      String str2
   ) {
      this.richPresence.smallImageKey = str;
      this.richPresence.smallImageText = str2;
      return this;
   }

   public SetsmallimageHelper setParty(
      String str,
      int n,
      int n2
   ) {
      if ((this.richPresence.button_label_1 == null || !this.richPresence.button_label_1.isEmpty())
         && (this.richPresence.button_label_2 == null || !this.richPresence.button_label_2.isEmpty())) {
         this.richPresence.partyId = str;
         this.richPresence.partySize = n;
         this.richPresence.partyMax = n2;
      }

      return this;
   }

   public SetsmallimageHelper setButtons(
      List<WebLink> list
   ) {
      if (list
            != null
         && !list.isEmpty()
         )
       {
         int count = Math.min(
            list.size(),
            2
         );
         this.richPresence.button_label_1 = list.get(
               0
            )
            .getLabel();
         this.richPresence.button_url_1 = list.get(
               0
            )
            .getUrl();
         if (count
            == 2) {
            this.richPresence.button_label_2 = list.get(
                  1
               )
               .getLabel();
            this.richPresence.button_url_2 = list.get(
                  1
               )
               .getUrl();
         }
      }

      return this;
   }

   public SetsmallimageHelper setStartTimestamp(
      OffsetDateTime val
   ) {
      this.richPresence.startTimestamp = val.toEpochSecond();
      return this;
   }

   public SetsmallimageHelper setSecrets(
      String str,
      String str2,
      String str3
   ) {
      if ((this.richPresence.button_label_1 == null || !this.richPresence.button_label_1.isEmpty())
         && (this.richPresence.button_label_2 == null || !this.richPresence.button_label_2.isEmpty())) {
         this.richPresence.matchSecret = str;
         this.richPresence.joinSecret = str2;
         this.richPresence.spectateSecret = str3;
      }

      return this;
   }

   public void setButtons(
      WebLink val,
      WebLink val2
   ) {
      WebLink[] var10001 = new WebLink[2];
      var10001[0] = val;
      var10001[1] = val2;
      this.setButtons(Arrays.asList(var10001));
   }

   public void setStartTimestamp(
      long time
   ) {
      this.richPresence.startTimestamp = time;
   }

   public SetsmallimageHelper setSecrets(
      String str,
      String str2
   ) {
      if ((this.richPresence.button_label_1 == null || !this.richPresence.button_label_1.isEmpty())
         && (this.richPresence.button_label_2 == null || !this.richPresence.button_label_2.isEmpty())) {
         this.richPresence.joinSecret = str;
         this.richPresence.spectateSecret = str2;
      }

      return this;
   }

   public SetsmallimageHelper setEndTimestamp(
      long time
   ) {
      this.richPresence.endTimestamp = time;
      return this;
   }

   public SetsmallimageHelper setEndTimestamp(
      OffsetDateTime val
   ) {
      this.richPresence.endTimestamp = val.toEpochSecond();
      return this;
   }

   public SetsmallimageHelper setLargeImage(
      String str
   ) {
      return this.setLargeImage(
         str,
         ""
      );
   }

   public NativeLargeimagekeyLargeimagetextStruct build() {
      return this.richPresence;
   }
}
