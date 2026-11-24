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


import dev.leg1tka.api.events.implement.EventBinding;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Generated;
import org.lwjgl.glfw.GLFW;

public final class CallHelper implements IMinecraft {
   public static final int MOUSE_BUTTON_OFFSET = 1000;

   public static void call(
      int n,
      int n2
   ) {
      if (n
         > -1) {
         if (n2
            == 1) {
            if (n
               == 344) {
               PlaysoundHelper.playSound("opengui.wav", 0.6, 1.0F);
               mc.setScreen(new HudEditorScreen());
            }

            if (n
               == ModuleManager.autoBuy.openKey.getKey()) {
               mc.setScreen(new ClickGuiScreen());
            }

            new EventBinding(
                  n,
                  EventBinding.BindType.KEYBOARD
               )
               .call();
            ObjectArrayList val = ModuleManager.INSTANCE
               .getObject();
            int i = 0;

            for (int i2 = val.size();
               i
                  < i2;
               i++
            ) {
               Module val2 = (Module)val.get(
                  i
               );
               if (val2.getKey()
                  == n
                  )
                {
                  val2.toggle();
               }
            }
         }
      }
   }

   public static String getKeyName(
      int n
   ) {
      if (n
         == -1) {
         return "None";
      } else if (n
         >= 1000) {
         return getBindName(
            n
         );
      } else {
         String name = GLFW.glfwGetKeyName(
            n,
            0
         );
         return name
               != null
            ? name.toUpperCase()
            : getBindName(
               n
            );
      }
   }

   public static void callMouse(
      int n,
      int n2
   ) {
      if (mc.currentScreen == null) {
         if (n
            >= 0) {
            if (n2
               == 1) {
               int i = 1000
                  + n;
               new EventBinding(
                     i,
                     EventBinding.BindType.MOUSE
                  )
                  .call();
               ObjectArrayList val = ModuleManager.INSTANCE
                  .getObject();
               int i2 = 0;

               for (int i3 = val.size();
                  i2
                     < i3;
                  i2++
               ) {
                  Module val2 = (Module)val.get(
                     i2
                  );
                  if (val2.getKey()
                     == i
                     )
                   {
                     val2.toggle();
                  }
               }
            }
         }
      }
   }

   public static boolean isBindHeld(
      int n
   ) {
      if (n
         == -1) {
         return false;
      } else {
         long time = mc.getWindow()
            .getHandle();
         if (n
            >= 1000) {
            int y = n
               - 1000;
            return GLFW.glfwGetMouseButton(
                  time,
                  y
               )
               == 1;
         } else {
            return GLFW.glfwGetKey(
                  time,
                  n
               )
               == 1;
         }
      }
   }

   public static boolean isBindPressed(
      int n
   ) {
      return isBindHeld(
         n
      );
   }

   public static String getBindName(
      int n2
   ) {
      if (n2
         == -1) {
         return "n/a";
      } else if (n2
         >= 1000) {
         int y = n2
            - 1000;

         return switch (y) {
            case 0 -> "ЛКМ";
            case 1 -> "ПКМ";
            case 2 -> "СКМ";
            case 3 -> "MOUSE4";
            case 4 -> "MOUSE5";
            default -> "MOUSE"
            + (
               y
                  + 1
            );
         };
      } else if (n2
            >= 65
         && n2
            <= 90) {
         return String.valueOf(
            (char)(
               65
                  + (
                     n2
                        - 65
                  )
            )
         );
      } else if (n2
            >= 48
         && n2
            <= 57) {
         return String.valueOf(
            (char)(
               48
                  + (
                     n2
                        - 48
                  )
            )
         );
      } else {
         String name = switch (n2) {
            case 39 -> "'";
            case 44 -> ",";
            case 45 -> "-";
            case 46 -> ".";
            case 47 -> "/";
            case 59 -> ";";
            case 61 -> "=";
            case 91 -> "[";
            case 92 -> "\\";
            case 93 -> "]";
            case 96 -> "`";
            default -> null;
         };
         if (name
            != null) {
            return name;
         } else {
            return switch (n2) {
               case 32 -> "SPACE";
               case 256 -> "ESC";
               case 257 -> "ENTER";
               case 258 -> "TAB";
               case 259 -> "BACKSPACE";
               case 260 -> "INSERT";
               case 261 -> "DELETE";
               case 262 -> "RIGHT";
               case 263 -> "LEFT";
               case 264 -> "DOWN";
               case 265 -> "UP";
               case 266 -> "PAGEUP";
               case 267 -> "PAGEDOWN";
               case 268 -> "HOME";
               case 269 -> "END";
               case 280 -> "CAPS";
               case 290 -> "F1";
               case 291 -> "F2";
               case 292 -> "F3";
               case 293 -> "F4";
               case 294 -> "F5";
               case 295 -> "F6";
               case 296 -> "F7";
               case 297 -> "F8";
               case 298 -> "F9";
               case 299 -> "F10";
               case 300 -> "F11";
               case 301 -> "F12";
               case 320 -> "NUM0";
               case 321 -> "NUM1";
               case 322 -> "NUM2";
               case 323 -> "NUM3";
               case 324 -> "NUM4";
               case 325 -> "NUM5";
               case 326 -> "NUM6";
               case 327 -> "NUM7";
               case 328 -> "NUM8";
               case 329 -> "NUM9";
               case 330 -> "NUM.";
               case 331 -> "NUM/";
               case 332 -> "NUM*";
               case 333 -> "NUM-";
               case 334 -> "NUM+";
               case 335 -> "NUMENTER";
               case 340 -> "LSHIFT";
               case 341 -> "LCTRL";
               case 342 -> "LALT";
               case 344 -> "RSHIFT";
               case 345 -> "RCTRL";
               case 346 -> "RALT";
               default -> "KEY"
               + n2;
            };
         }
      }
   }

   public static boolean isMouseButton(
      int n
   ) {
      return n
         >= 1000;
   }

   public static int getMouseButtonFromKey(
      int n
   ) {
      return isMouseButton(
            n
         )
         ? n
            - 1000
         : -1;
   }

   public static int createMouseBind(
      int n
   ) {
      return 1000
         + n;
   }

   @Generated
   private CallHelper() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
