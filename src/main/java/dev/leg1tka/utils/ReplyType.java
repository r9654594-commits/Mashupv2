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



public enum ReplyType {
   NO(0),
   IGNORE(2),
   YES(1);

   public final int reply;

   private ReplyType(int var3) {
      this.reply = var3;
   }

   private static ReplyType[] getReplies() {
      ReplyType[] var10000 = new ReplyType[3];
      var10000[0] = NO;
      var10000[1] = YES;
      var10000[2] = IGNORE;
      return var10000;
   }
}
