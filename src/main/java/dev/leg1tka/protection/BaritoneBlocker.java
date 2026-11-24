package dev.leg1tka.protection;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.filter.AbstractFilter;

public class BaritoneBlocker {
   public static final String[] PTN = new String[]{
      "aspectdlc",
      "AspectDLC",
      "leg1tka",
      "dev.leg1tka",
      "[Fonts] Failed to load",
      "baritone-standalone",
      "mixins.baritone",
      "Baritone-Blocker",
      "NowPlaying-Poller"
   };
   private static AbstractFilter flt;
   private static PrintStream oout;
   private static PrintStream oerr;
   private static ScheduledExecutorService blk;

   public static void addLogFilter() {
      try {
         LoggerContext text = (LoggerContext)LogManager.getContext(
            false
         );
         Configuration val = text.getConfiguration();
         flt = new AbstractFilter(Result.DENY, Result.NEUTRAL) {
            public Result filter(
               LogEvent event
            ) {
               try {
                  String text2 = event.getMessage()
                     .getFormattedMessage();
                  if (text2
                     != null) {
                     for (String text3 : BaritoneBlocker.PTN) {
                        if (text2.contains(
                           text3
                        )) {
                           return Result.DENY;
                        }
                     }
                  }

                  Throwable val2 = event.getThrown();
                  if (val2
                     != null) {
                     String text4 = val2.toString();

                     for (String text5 : BaritoneBlocker.PTN) {
                        if (text4.contains(
                           text5
                        )) {
                           return Result.DENY;
                        }
                     }
                  }
               } catch (Exception var9) {
               }

               return Result.NEUTRAL;
            }
         };
         val.addFilter(
            flt
         );
         text.updateLoggers();
      } catch (Exception var2) {
      }
   }

   public static void dropLogFilter() {
      try {
         if (flt == null) {
            return;
         }

         LoggerContext text = (LoggerContext)LogManager.getContext(
            false
         );
         Configuration val = text.getConfiguration();
         val.removeFilter(
            flt
         );
         text.updateLoggers();
         flt = null;
      } catch (Exception var2) {
      }
   }

   public static void addOutFilter() {
      try {
         oout = System.out;
         oerr = System.err;
         System.setOut(new PrintlnHelper(oout));
         System.setErr(new PrintlnHelper(oerr));
      } catch (Exception var1) {
      }
   }

   public static void dropOutFilter() {
      try {
         if (oout != null) {
            System.setOut(oout);
         }

         if (oerr != null) {
            System.setErr(oerr);
         }

         oout = null;
         oerr = null;
      } catch (Exception var1) {
      }
   }

   public static void spoofLogs(
      File val
   ) {
      try {
         Path var10000 = val.toPath();
         Path val2 = var10000.resolve(
            "logs"
         );
         if (!Files.exists(
            val2
         )) {
            return;
         }

         Path val3 = val2.resolve(
            "latest.log"
         );
         if (Files.exists(
            val3
         )) {
            try {
               stopAppenders();
               spoofFile(
                  val3
               );
            } finally {
               startAppenders();
            }
         }

         try (DirectoryStream<Path> val4 = Files.newDirectoryStream(
               val2,
               "*.log.gz"
            )) {
            for (Path val5 : val4) {
               spoofGz(
                  val5
               );
            }
         }
      } catch (Exception var13) {
      }
   }

   private static void stopAppenders() {
      try {
         LoggerContext text = (LoggerContext)LogManager.getContext(
            false
         );

         for (Appender val : text.getConfiguration()
            .getAppenders()
            .values()) {
            String name = val.getClass()
               .getSimpleName()
               .toLowerCase();
            if (!name.contains(
               "file"
            )) {
               if (!name.contains(
                  "rolling"
               )) {
                  continue;
               }
            }

            val.stop();
         }
      } catch (Exception var4) {
      }
   }

   private static void startAppenders() {
      try {
         LoggerContext text = (LoggerContext)LogManager.getContext(
            false
         );

         for (Appender val : text.getConfiguration()
            .getAppenders()
            .values()) {
            String name = val.getClass()
               .getSimpleName()
               .toLowerCase();
            if (!name.contains(
               "file"
            )) {
               if (!name.contains(
                  "rolling"
               )) {
                  continue;
               }
            }

            if (!val.isStarted()
               )
             {
               val.start();
            }
         }
      } catch (Exception var4) {
      }
   }

   private static void spoofFile(
      Path val
   ) {
      try {
         List<String> list = Files.readAllLines(
            val,
            StandardCharsets.UTF_8
         );
         List val2 = new ArrayList<>(
            list.size()
         );

         for (String str : list) {
            if (!tainted(
               str
            )) {
               val2.add(
                  str
               );
            }
         }

         Files.write(
            val,
            val2,
            StandardCharsets.UTF_8
         );
      } catch (Exception var5) {
      }
   }

   private static void spoofGz(
      Path val
   ) {
      try {
         List<String> list = new ArrayList<>();

         String name;
         try (
            GZIPInputStream val2 = new GZIPInputStream(
               Files.newInputStream(
                  val
               )
            );
            BufferedReader val3 = new BufferedReader(
               new InputStreamReader(
                  val2,
                  StandardCharsets.UTF_8
               )
            );
         ) {
            while (
               (
                     name = val3.readLine()
                  )
                  != null
            ) {
               list.add(
                  name
               );
            }
         }

         ArrayList var20 = new ArrayList(
            list.size()
         );

         for (String name2 : list) {
            if (!tainted(
               name2
            )) {
               var20.add(
                  name2
               );
            }
         }

         Path val4 = val.resolveSibling(
            val.getFileName()
               + ".tmp"
         );

         try (
            GZIPOutputStream val5 = new GZIPOutputStream(
               Files.newOutputStream(
                  val4
               )
            );
            OutputStreamWriter val6 = new OutputStreamWriter(
               val5,
               StandardCharsets.UTF_8
            );
            BufferedWriter val7 = new BufferedWriter(
               val6
            );
         ) {
            for (int i = 0;
               i
                  < var20.size();
               i++
            ) {
               val7.write(
                  (String)var20.get(
                     i
                  )
               );
               if (i
                  < var20.size() - 1) {
                  val7.newLine();
               }
            }
         }

         Files.move(
            val4,
            val,
            StandardCopyOption.REPLACE_EXISTING
         );
      } catch (Exception var19) {
      }
   }

   private static boolean tainted(
      String str
   ) {
      for (String str2 : PTN) {
         if (str.contains(
            str2
         )) {
            return true;
         }
      }

      return false;
   }

   public static void fixThreads() {
      try {
         ThreadGroup val = Thread.currentThread()
            .getThreadGroup();

         while (
            val.getParent()
               != null
         ) {
            val = val.getParent();
         }

         int i = val.activeCount();
         Thread[] val2 = new Thread[i
            + 50];
         val.enumerate(
            val2,
            true
         );
         String var10 = "AspectDLC-ClientSounds";
         String var11 = "MC-AudioPool-1";
         String var12 = "Discord-RPC";
         String var13 = "JVM-Timer-1";
         String var14 = "Discord-RPC-Updater";
         String var15 = "JVM-Timer-2";
         String var16 = "Baritone-Blocker";
         String var17 = "MC-Worker-3";
         String var18 = "NowPlaying-Poller";
         Map<String, String> val3 = Map.of(
            var10,
            var11,
            var12,
            var13,
            var14,
            var15,
            var16,
            var17,
            var18,
            "MC-Worker-4"
         );

         for (Thread val4 : val2) {
            if (val4
               != null) {
               String name = val3.get(
                  val4.getName()
               );
               if (name
                  != null) {
                  val4.setName(
                     name
                  );
               }
            }
         }
      } catch (Exception var9) {
      }
   }

   public static void startBaritoneBlocker(
      File val
   ) {
      startBlk(
         val
      );
   }

   public static void stopBaritoneBlocker(
      File val
   ) {
      stopBlk(
         val
      );
   }

   public static void deleteDirectory(
      Path val
   ) {
      delDir(
         val
      );
   }

   public static void performDeepClean() {
      CompletableFuture.runAsync(() -> {
         cleanShell();
         cleanReg();
         cleanEvt();
         cleanRecent();
         cleanTemp();
         cleanDns();
         cleanUsn();
         cleanEverything();
      });
   }

   public static void startBlk(
      File val
   ) {
      if (blk == null || blk.isShutdown()) {
         blk = Executors.newSingleThreadScheduledExecutor(
            val2 -> {
               Thread val3 = new Thread(
                  val2,
                  "MC-Worker-3"
               );
               val3.setDaemon(
                  true
               );
               return val3;
            }
         );
         blk.scheduleAtFixedRate(
            () -> {
               try {
                  File val4 = new File(
                     val,
                     "baritone"
                  );
                  if (val4.exists()
                     && val4.isDirectory()
                     )
                   {
                     delDir(
                        val4.toPath()
                     );
                  }

                  if (!val4.exists()
                     )
                   {
                     val4.createNewFile();
                     val4.setReadOnly();
                  }
               } catch (Exception var2) {
               }
            },
            0L,
            5000L,
            TimeUnit.MILLISECONDS
         );
      }
   }

   public static void stopBlk(
      File val
   ) {
      if (blk != null && !blk.isShutdown()) {
         blk.shutdownNow();
         blk = null;
      }

      try {
         File val2 = new File(
            val,
            "baritone"
         );
         if (val2.exists()
            && val2.isFile()
            )
          {
            val2.delete();
         }
      } catch (Exception var2) {
      }
   }

   public static void cleanShell() {
      try {
         String str = "Remove-Item 'HKCU:\\Software\\Microsoft\\Windows\\Shell\\Bags' -Recurse -Force -ErrorAction SilentlyContinue; Remove-Item 'HKCU:\\Software\\Microsoft\\Windows\\Shell\\BagMRU' -Recurse -Force -ErrorAction SilentlyContinue; Remove-Item 'HKCU:\\Software\\Classes\\Local Settings\\Software\\Microsoft\\Windows\\Shell\\Bags' -Recurse -Force -ErrorAction SilentlyContinue; Remove-Item 'HKCU:\\Software\\Classes\\Local Settings\\Software\\Microsoft\\Windows\\Shell\\BagMRU' -Recurse -Force -ErrorAction SilentlyContinue";
         cmd(
            "powershell.exe -NoProfile -NonInteractive -Command \""
               + str
               + "\"",
            10
         );
      } catch (Exception var1) {
      }
   }

   public static void cleanReg() {
      String[] val = new String[]{
         "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\RecentDocs",
         "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\ComDlg32\\OpenSavePidlMRU",
         "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\ComDlg32\\LastVisitedPidlMRU",
         "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Search\\RecentApps",
         "HKCU\\Software\\Classes\\Local Settings\\Software\\Microsoft\\Windows\\Shell\\MuiCache",
         "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\TypedPaths",
         "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\RunMRU"
      };

      for (String str : val) {
         cmd(
            "reg delete \""
               + str
               + "\" /f",
            5
         );
      }
   }

   public static void cleanEvt() {
      String str = "$logs = wevtutil el | Where-Object { $_ -match 'java|minecraft|fabric|javaw' }; foreach ($l in $logs) { wevtutil cl $l 2>$null }";
      cmd(
         "powershell.exe -NoProfile -NonInteractive -Command \""
            + str
            + "\"",
         10
      );
   }

   public static void cleanRecent() {
      try {
         String str = System.getProperty(
            "user.home"
         );
         String[] var10001 = new String[5];
         var10001[0] = "AppData";
         var10001[1] = "Roaming";
         int var10003 = 2;
         var10001[var10003] = "Microsoft";
         var10003 = 3;
         var10001[var10003] = "Windows";
         var10003 = 4;
         var10001[var10003] = "Recent";
         Path val = Paths.get(
            str,
            var10001
         );
         if (Files.exists(
            val
         )) {
            Files.walk(
                  val
               )
               .filter(
                  val2 -> Files.isRegularFile(
                        val2
                     )
               )
               .forEach(
                  val3 -> {
                     try {
                        Files.delete(
                           val3
                        );
                     } catch (IOException var2x) {
                     }
                  }
               );
         }

         try {
            Path val4 = Paths.get(
               "C:\\Windows\\Prefetch"
            );
            if (Files.exists(
               val4
            )) {
               Files.walk(
                     val4
                  )
                  .filter(
                     val5 -> Files.isRegularFile(
                           val5
                        )
                  )
                  .filter(
                     val6 -> {
                        String str2 = val6.toString()
                           .toLowerCase();
                        return str2.contains(
                              "java"
                           )
                           || str2.contains(
                              "minecraft"
                           );
                     }
                  )
                  .forEach(
                     val7 -> {
                        try {
                           Files.delete(
                              val7
                           );
                        } catch (IOException var2x) {
                        }
                     }
                  );
            }
         } catch (Exception var3) {
         }
      } catch (Exception var4) {
      }
   }

   public static void cleanTemp() {
      String[] var10000 = new String[3];
      var10000[0] = System.getProperty("java.io.tmpdir");
      var10000[1] = System.getenv("TEMP");
      int var10002 = 2;
      var10000[var10002] = System.getenv("TMP");
      String[] val = var10000;
      var10000 = new String[3];
      var10000[0] = "aspectdlc";
      var10000[1] = "minecraft";
      var10002 = 2;
      var10000[var10002] = "fabric";
      String[] val2 = var10000;

      for (String name : val) {
         if (name
            != null) {
            File val3 = new File(
               name
            );
            File[] val4 = val3.listFiles();
            if (val4
               != null) {
               for (File val5 : val4) {
                  String name2 = val5.getName()
                     .toLowerCase();

                  for (String name3 : val2) {
                     if (name2.contains(
                        name3
                     )) {
                        try {
                           if (val5.isFile()
                              )
                            {
                              secDel(
                                 val5
                              );
                           } else {
                              delDir(
                                 val5.toPath()
                              );
                           }
                        } catch (Exception var18) {
                        }
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   public static void cleanDns() {
      cmd(
         "ipconfig /flushdns",
         5
      );
   }

   public static void cleanUsn() {
      String str = System.getenv(
         "SystemDrive"
      );
      if (str
         == null) {
         str = "C:";
      }

      cmd(
         "fsutil usn deletejournal /D "
            + str,
         10
      );
   }

   public static void cleanEverything() {
      try {
         cmd(
            "taskkill /F /IM Everything.exe",
            3
         );
         String str = System.getenv(
            "APPDATA"
         );
         if (str
            == null) {
            return;
         }

         String[] var7 = new String[4];
         var7[0] = "Everything.db";
         var7[1] = "Everything.ini";
         int var10002 = 2;
         var7[var10002] = "Search History.csv";
         var10002 = 3;
         var7[var10002] = "Run History.csv";
         String[] var1 = var7;
         int var2 = var1.length;

         for (int var3 = 0; var3 < var2; var3++) {
            String str2 = var1[var3];
            secDel(
               new File(
                  str
                     + "\\Everything\\"
                     + str2
               )
            );
         }
      } catch (Exception var5) {
      }
   }

   public static void nukeJar(
      File val
   ) {
      try {
         File val2 = new File(
            val,
            "mods"
         );
         if (!val2.exists()
            )
          {
            return;
         }

         File[] val3 = val2.listFiles(
            (val4, val5) -> {
               String var10000 = val5.toLowerCase();
               if (var10000.endsWith(".jar")) {
                  var10000 = val5.toLowerCase();
                  if (var10000.contains("aspectdlc")) {
                     return true;
                  }
               }

               return false;
            }
         );
         if (val3
            == null) {
            return;
         }

         for (File val6 : val3) {
            secDel(
               val6
            );
         }
      } catch (Exception var7) {
      }
   }

   public static void restartExplorer() {
      cmd(
         "taskkill /F /IM explorer.exe",
         3
      );
      cmd(
         "start explorer.exe",
         3
      );
   }

   public static void deleteBaritone(
      File val
   ) {
      try {
         File val2 = new File(
            val,
            "baritone"
         );
         if (val2.exists()
            && val2.isDirectory()
            )
          {
            delDir(
               val2.toPath()
            );
         }
      } catch (Exception var2) {
      }
   }

   public static void deleteGpsFiles(
      File val
   ) {
      try {
         File val2 = new File(
            val,
            "waypoints.json"
         );
         if (val2.exists()
            )
          {
            val2.delete();
         }

         File val3 = new File(
            val,
            "gps.dat"
         );
         if (val3.exists()
            )
          {
            val3.delete();
         }
      } catch (Exception var3) {
      }
   }

   public static void secDel(
      File val
   ) {
      if (val
            != null
         && val.exists()
         && val.isFile()
         )
       {
         try {
            long time = val.length();
            Random val2 = new Random();

            for (int i = 0;
               i
                  < 3;
               i++
            ) {
               try (RandomAccessFile val3 = new RandomAccessFile(
                     val,
                     "rw"
                  )) {
                  val3.seek(
                     0L
                  );
                  byte[] val4 = new byte[4096];
                  long time2 = time;

                  while (
                     time2
                        > 0L
                  ) {
                     int i2 = (int)Math.min(
                        (long)val4.length,
                        time2
                     );
                     val2.nextBytes(
                        val4
                     );
                     val3.write(
                        val4,
                        0,
                        i2
                     );
                     time2 -= (long)i2;
                  }
               }
            }
         } catch (Exception var12) {
         }

         val.delete();
      }
   }

   public static void delDir(
      Path val
   ) {
      try {
         Files.walkFileTree(
            val,
            new SimpleFileVisitor<Path>() {
               public FileVisitResult visitFile(
                  Path val2,
                  BasicFileAttributes val3
               ) {
                  try {
                     Files.delete(
                        val2
                     );
                  } catch (IOException var4) {
                  }

                  return FileVisitResult.CONTINUE;
               }

               public FileVisitResult postVisitDirectory(
                  Path val4,
                  IOException val5
               ) {
                  try {
                     Files.delete(
                        val4
                     );
                  } catch (IOException var4) {
                  }

                  return FileVisitResult.CONTINUE;
               }
            }
         );
      } catch (Exception var2) {
      }
   }

   private static void cmd(
      String str,
      int n
   ) {
      try {
         String[] var10002 = new String[3];
         var10002[0] = "cmd.exe";
         var10002[1] = "/c";
         var10002[2] = str;
         ProcessBuilder val = new ProcessBuilder(
            var10002
         );
         val.redirectErrorStream(
            true
         );
         Process val2 = val.start();
         val2.waitFor(
            (long)n,
            TimeUnit.SECONDS
         );
      } catch (Exception var4) {
      }
   }
}
