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


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventBinding;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.util.Formatting;

public class MacroStorage implements IMinecraft {
   private final List<GetnameHelper> macros = new ArrayList<>();
   private final List<String> names = new ArrayList<>();

   public MacroStorage() {
      EventInvoker.register(this);
   }

   public void add(
      GetnameHelper val
   ) {
      if (val
            != null
         && val.getName()
            != null
         && !val.getName()
            .isBlank()
         && this.getMacro(
               val.getName()
            )
            == null) {
         this.macros
            .add(
               val
            );
         this.names
            .add(
               val.getName()
            );
      }
   }

   public void remove(
      GetnameHelper val
   ) {
      if (val
         != null) {
         this.macros
            .remove(
               val
            );
         this.names
            .remove(
               val.getName()
            );
      }
   }

   public void clear() {
      if (!this.macros.isEmpty()) {
         this.macros.clear();
      }

      if (!this.names.isEmpty()) {
         this.names.clear();
      }
   }

   public boolean isEmpty() {
      return this.macros.isEmpty();
   }

   public GetnameHelper getMacro(
      String str
   ) {
      for (GetnameHelper val : this.macros) {
         if (val.getName()
            .equalsIgnoreCase(
               str
            )) {
            return val;
         }
      }

      return null;
   }

   @EventLink
   public void onKey(
      EventBinding event
   ) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null && mc.player.networkHandler != null && !this.macros.isEmpty()) {
         for (GetnameHelper val : this.macros) {
            if (val
                  != null
               && val.getBind()
                  != null
               && val.getBind()
                     .getKey()
                  == event.getKey()
               )
             {
               this.executeMacro(
                  val
               );
            }
         }
      }
   }

   private void executeMacro(
      GetnameHelper val
   ) {
      String name = val.getCommand();
      if (name
            != null
         && !name.isBlank()
         )
       {
         if (name.startsWith(
            "/"
         )) {
            mc.player
               .networkHandler
               .sendChatCommand(
                  name.substring(
                     1
                  )
               );
         } else {
            String name2 = AspectDLC.INSTANCE
               .commandStorage
               .getPrefix();
            if (name2
                  != null
               && !name2.isEmpty()
               && name.startsWith(
                  name2
               )) {
               try {
                  AspectDLC.INSTANCE
                     .commandStorage
                     .getDispatcher()
                     .execute(
                        name.substring(
                           name2.length()
                        ),
                        AspectDLC.INSTANCE.commandStorage.getSource()
                     );
               } catch (CommandSyntaxException var5) {
                  ChatUtil.sendMessage(
                     Formatting.RED
                        + "Ошибка в использовании макроса "
                        + val.getName()
                        + "!"
                  );
               }
            } else {
               mc.player
                  .networkHandler
                  .sendChatMessage(
                     name
                  );
            }
         }
      }
   }

   @Generated
   public List<GetnameHelper> getMacros() {
      return this.macros;
   }

   @Generated
   public List<String> getNames() {
      return this.names;
   }
}
