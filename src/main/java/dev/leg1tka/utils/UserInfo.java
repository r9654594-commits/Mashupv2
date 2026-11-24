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



public class UserInfo {
   private final String username;
   private final int uid;
   private final String role;
   private final String expireDate;

   public UserInfo(
      String str,
      int n,
      String str2,
      String str3
   ) {
      this.username = str;
      this.uid = n;
      this.role = str2;
      this.expireDate = str3;
   }

   public static UserInfo empty() {
      return new UserInfo("Unknown", 0, "Unknown", "");
   }

   public String getUsername() {
      return this.username;
   }

   public int getUid() {
      return this.uid;
   }

   public String getRole() {
      return this.role;
   }

   public String getExpireDate() {
      return this.expireDate;
   }
}
