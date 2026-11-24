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


import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.EventGameUpdate;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class OngameupdateHelper implements IMinecraft {
   private final KeyBinding[] keybindsList;
   private final List<DurationHolder> tasks;
   private final List<DurationHolder_1> timeTasks;
   private DurationHolder lastTask;
   public static final OngameupdateHelper INSTANCE = new OngameupdateHelper();

   private OngameupdateHelper() {
      KeyBinding[] var10001 = new KeyBinding[7];
      var10001[0] = mc.options.forwardKey;
      var10001[1] = mc.options.leftKey;
      var10001[2] = mc.options.rightKey;
      var10001[3] = mc.options.backKey;
      var10001[4] = mc.options.jumpKey;
      var10001[5] = mc.options.sprintKey;
      var10001[6] = mc.options.sneakKey;
      this.keybindsList = var10001;
      this.tasks = new ArrayList<>();
      this.timeTasks = new ArrayList<>();
      this.lastTask = null;
      EventInvoker.register(this);
   }

   @EventLink
   public void onGameUpdate(
      EventGameUpdate event
   ) {
      this.updateSlowTasks();
      this.updateTimeTasks(false);
   }

   @EventLink
   public void onUpdate(
      EventUpdate event
   ) {
      this.updateTimeTasks(true);
   }

   public static void addTask(
      DurationHolder val
   ) {
      for (var val2 : INSTANCE.tasks) {
         if (val2.runnable
               != null
            && val.runnable
               != null
            && val2.runnable
               .equals(
                  val.runnable
               )) {
            return;
         }
      }

      INSTANCE.lastTask = val;
      INSTANCE.tasks
         .add(
            val
         );
   }

   public static void addTimeTask(
      DurationHolder_1 val
   ) {
      INSTANCE.timeTasks
         .add(
            val
         );
   }

   public void updateSlowTasks() {
      long time2 = System.currentTimeMillis();
      if (this.lastTask != null) {
         if (time2
               - this.lastTask.time
            < this.lastTask.duration) {
            KeyBinding[] var10000 = new KeyBinding[6];
            var10000[0] = mc.options.forwardKey;
            var10000[1] = mc.options.leftKey;
            var10000[2] = mc.options.rightKey;
            var10000[3] = mc.options.backKey;
            var10000[4] = mc.options.jumpKey;
            var10000[5] = mc.options.sprintKey;
            KeyBinding[] var3 = var10000;
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; var5++) {
               KeyBinding val = var3[var5];
               val.setPressed(
                  false
               );
            }
         } else {
            for (KeyBinding val2 : this.keybindsList) {
               if (mc.currentScreen == null) {
                  val2.setPressed(
                     InputUtil.isKeyPressed(
                        mc.getWindow().getHandle(),
                        val2.getDefaultKey()
                           .getCode()
                     )
                  );
               }
            }
         }
      }

      for (var val3 : new ArrayList<>(
         this.tasks
      )) {
         if (time2
                  - val3.time
               > val3.duration
                  - val3.reflection
            && val3.runnable
               != null) {
            val3.runnable
               .run();
            val3.runnable = null;
         }
      }

      Iterator<DurationHolder> val4 = this.tasks
         .iterator();

      while (
         val4.hasNext()
      ) {
         DurationHolder val5 = val4.next();
         if (time2
                  - val5.time
               > val5.duration
            || !val5.condition
            )
          {
            val4.remove();
         }
      }
   }

   public void updateTimeTasks(
      boolean flag
   ) {
      long time2 = System.currentTimeMillis();

      for (var val : new ArrayList<>(
         this.timeTasks
      )) {
         if (flag
            )
          {
            val.ticksExisted++;
         }

         if (val.runnable
               != null
            && (
               val.mode
                     == ClientOption_BSfnhH93.TICKS
                  ? val.ticksExisted
                     >= val.time
                  : time2
                        - val.time
                     > val.duration
            )) {
            val.runnable
               .run();
            val.runnable = null;
         }
      }

      Iterator<DurationHolder_1> val2 = this.timeTasks
         .iterator();

      while (
         val2.hasNext()
      ) {
         DurationHolder_1 val3 = val2.next();
         if ((
               val3.mode
                     == ClientOption_BSfnhH93.TICKS
                  ? val3.ticksExisted
                     >= val3.time
                  : time2
                        - val3.time
                     > val3.duration
            )
            || !val3.condition
            )
          {
            val2.remove();
         }
      }
   }

   public static boolean slowTasksIsEmpty() {
      return INSTANCE.tasks.isEmpty();
   }

   public static boolean timeTasksIsEmpty() {
      return INSTANCE.timeTasks.isEmpty();
   }
}
