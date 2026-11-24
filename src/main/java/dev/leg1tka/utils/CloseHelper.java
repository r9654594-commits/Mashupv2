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


import java.io.FilterInputStream;
import java.io.InputStream;

public class CloseHelper extends FilterInputStream {
   public CloseHelper(InputStream var1) {
      super(var1);
   }

   @Override
   public void close() {
   }
}
