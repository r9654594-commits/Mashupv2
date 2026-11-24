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


import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class InitializeHelper {
   private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
   private static final AtomicInteger VIOLATION_COUNT = new AtomicInteger(0);
   private static final Random RNG = new Random(System.nanoTime());
   private static volatile boolean _dbg = false;
   private static volatile boolean _vm = false;
   private static volatile boolean _dec = false;

   private InitializeHelper() {
   }

   public static void initialize() {
      if (INITIALIZED.compareAndSet(false, true)) {
         AlwaystrueHelper.execute(() -> {
            if (AlwaystrueHelper.alwaysTrue()) {
               performChecks();
            }
         });
      }
   }

   private static void performChecks() {
      Thread val = new Thread(
         InitializeHelper::checkDebugger, "Debugger-Check"
      );
      Thread val2 = new Thread(
         InitializeHelper::checkVM, "VM-Check"
      );
      Thread val3 = new Thread(
         InitializeHelper::checkDecompiler, "Decompiler-Check"
      );
      Thread val4 = new Thread(
         InitializeHelper::checkMemoryDumper, "MemoryDumper-Check"
      );
      val.setDaemon(
         true
      );
      val2.setDaemon(
         true
      );
      val3.setDaemon(
         true
      );
      val4.setDaemon(
         true
      );
      val.start();
      val2.start();
      val3.start();
      val4.start();
   }

   private static void checkDebugger() {
      try {
         RuntimeMXBean val = ManagementFactory.getRuntimeMXBean();

         for (String name : val.getInputArguments()) {
            if (name.contains(
                  "-agentlib:jdwp"
               )
               || name.contains(
                  "-Xrunjdwp"
               )
               || name.contains(
                  "-Xdebug"
               )
               || name.contains(
                  "-Xnoagent"
               )) {
               _dbg = true;
               onViolation("Debugger detected via JVM flags");
               return;
            }
         }

         ThreadGroup val2 = Thread.currentThread()
            .getThreadGroup();

         while (
            val2.getParent()
               != null
         ) {
            val2 = val2.getParent();
         }

         Thread[] val3 = new Thread[val2.activeCount()
            * 2];
         int i = val2.enumerate(
            val3
         );

         for (int i2 = 0;
            i2
               < i;
            i2++
         ) {
            if (val3[i2]
               != null) {
               String name2 = val3[i2]
                  .getName()
                  .toLowerCase();
               if (name2.contains(
                     "jdwp"
                  )
                  || name2.contains(
                     "debug"
                  )
                  || name2.contains(
                     "attach"
                  )
                  || name2.contains(
                     "instrument"
                  )) {
                  _dbg = true;
                  onViolation(
                     "Debugger thread detected: "
                        + name2
                  );
                  return;
               }
            }
         }

         long time = System.nanoTime();
         AlwaystrueHelper.dummyComputation();
         long time2 = System.nanoTime()
            - time;
         if (time2
            > 10000000L) {
            _dbg = true;
            onViolation("Timing anomaly detected (possible debugger)");
         }
      } catch (Exception var9) {
         onViolation("Exception during debugger check");
      }
   }

   private static void checkVM() {
      try {
         String[] val = new String[]{"C:\\windows\\system32\\drivers\\vmmouse.sys", "C:\\windows\\system32\\drivers\\vmhgfs.sys", "/usr/bin/vmware-toolbox-cmd", "/usr/bin/VBoxControl", "C:\\Program Files\\VMware\\", "C:\\Program Files\\Oracle\\VirtualBox\\"};

         for (String name2 : val) {
            if (new File(
                  name2
               )
               .exists()) {
               _vm = true;
               onViolation(
                  "VM artifact detected: "
                     + name2
               );
               return;
            }
         }

         int i = Runtime.getRuntime()
            .availableProcessors();
         if (i
            < 2) {
            _vm = true;
            onViolation(
               "Suspicious CPU count: "
                  + i
            );
         }

         String name3 = System.getProperty(
               "java.vendor", ""
            )
            .toLowerCase();
         String name4 = System.getProperty(
               "java.vm.name", ""
            )
            .toLowerCase();
         if (name3.contains(
               "android"
            )
            || name4.contains(
               "dalvik"
            )) {
            _vm = true;
            onViolation("Android/Dalvik VM detected");
         }
      } catch (Exception var5) {
         onViolation("Exception during VM check");
      }
   }

   private static void checkDecompiler() {
      try {
         StackTraceElement[] val = Thread.currentThread()
            .getStackTrace();

         for (StackTraceElement val2 : val) {
            String name = val2.getClassName()
               .toLowerCase();
            if (name.contains(
                  "jd.gui"
               )
               || name.contains(
                  "fernflower"
               )
               || name.contains(
                  "procyon"
               )
               || name.contains(
                  "cfr"
               )
               || name.contains(
                  "jadx"
               )
               || name.contains(
                  "jdec"
               )
               || name.contains(
                  "luyten"
               )) {
               _dec = true;
               onViolation(
                  "Decompiler detected: "
                     + name
               );
               return;
            }
         }

         String name2 = System.getProperty(
            "java.class.path", ""
         );
         if (name2.contains(
               "jd-gui"
            )
            || name2.contains(
               "fernflower"
            )
            || name2.contains(
               "procyon"
            )
            || name2.contains(
               "cfr"
            )) {
            _dec = true;
            onViolation("Decompiler in classpath");
         }

         try {
            Class.forName("org.benf.cfr.reader.Main");
            _dec = true;
            onViolation("CFR decompiler loaded");
         } catch (ClassNotFoundException var6) {
         }
      } catch (Exception var7) {
         onViolation("Exception during decompiler check");
      }
   }

   private static void checkMemoryDumper() {
      try {
         String str = System.getProperty(
            "user.dir", ""
         );
         File val = new File(
            str
         );
         if (val.exists()
            && val.isDirectory()
            )
          {
            File[] val2 = val.listFiles(
               (val3, val4) -> val4.endsWith(
                        ".hprof"
                     )
                     || val4.endsWith(
                        ".dump"
                     )
                     || val4.contains(
                        "heapdump"
                     )
            );
            if (val2
                  != null
               && val2.length
                  > 0) {
               onViolation("Heap dump file detected");
            }
         }
      } catch (Exception var3) {
      }
   }

   private static void onViolation(
      String str
   ) {
      int count = VIOLATION_COUNT.incrementAndGet();
      if (AlwaystrueHelper.alwaysTrue()) {
         if (count
            >= 3) {
            corruptExecution();
         } else {
            subtleCorruption();
         }
      }
   }

   private static void corruptExecution() {
      if (RNG.nextBoolean()) {
         throw new ClientSecurityException_Z2j3d96x();
      } else {
         if (RNG.nextBoolean()) {
            while (AlwaystrueHelper.alwaysTrue()) {
               Thread.yield();
            }
         }

         System.exit(0);
      }
   }

   private static void subtleCorruption() {
      try {
         Thread.sleep(
            (long)(
               RNG.nextInt(1000)
                  + 500
            )
         );
      } catch (InterruptedException var1) {
      }

      AlwaystrueHelper.OPAQUE_STATE = AlwaystrueHelper.OPAQUE_STATE + RNG.nextInt(1000);
   }

   public static void startMonitoring() {
      Thread val = new Thread(
         () -> {
            while (AlwaystrueHelper.alwaysTrue()) {
               try {
                  Thread.sleep(
                     (long)(
                        5000
                           + RNG.nextInt(5000)
                     )
                  );
                  if (_dbg || _vm || _dec) {
                     onViolation("Continuous monitoring alert");
                  }

                  checkDebugger();
                  checkVM();
                  checkDecompiler();
               } catch (Exception var1) {
               }
            }
         },
         "ٸ"
      );
      val.setDaemon(
         true
      );
      val.setPriority(
         1
      );
      val.start();
   }

   public static boolean isSafe() {
      return !_dbg && !_vm && !_dec;
   }

   public static void executeIfSafe(
      Runnable val
   ) {
      if (AlwaystrueHelper.alwaysTrue()) {
         if (isSafe()) {
            val.run();
         } else {
            onViolation("Attempted execution in unsafe environment");
         }
      }
   }
}
