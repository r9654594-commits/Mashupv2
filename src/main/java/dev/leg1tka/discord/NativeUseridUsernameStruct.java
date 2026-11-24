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

public class NativeUseridUsernameStruct extends Structure {
   public String userId;
   public String username;
   @Deprecated
   public String discriminator;
   public String avatar;

   protected List<String> getFieldOrder() {
      return Arrays.asList(new String[]{"userId", "username", "discriminator", "avatar"});
   }
}
