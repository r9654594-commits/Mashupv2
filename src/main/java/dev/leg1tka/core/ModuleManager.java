package dev.leg1tka.core;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;



import dev.leg1tka.modules.render.*;
import java.util.List;

public class ModuleManager extends ManagerBase<Module> implements IModules {
   public static ModuleManager INSTANCE = new ModuleManager();
   public static Chams chams = Chams.INSTANCE;

   public void initialize() {
      Module[] var10001 = new Module[118];
      var10001[0] = ahHelper;
      var10001[1] = autoEZ;
      var10001[2] = wardenHelper;
      var10001[3] = antibot;
      var10001[4] = aimBot;
      var10001[5] = airStuck;
      var10001[6] = arrows;
      var10001[7] = autoCart;
      var10001[8] = aura;
      var10001[9] = autoAccept;
      var10001[10] = grimexploit;
      var10001[11] = totemparticles;
      var10001[12] = autoArmor;
      var10001[13] = packetMace;
      var10001[14] = maceExploit;
      var10001[15] = antiAFK;
      var10001[16] = autoBuy;
      var10001[17] = autoDuel;
      var10001[18] = autoEat;
      var10001[19] = totemtracker;
      var10001[20] = autoLeave;
      var10001[21] = autoExplosion;
      var10001[22] = autoJoin;
      var10001[23] = autoPvp;
      var10001[24] = nameProtect;
      var10001[25] = autoJump;
      var10001[26] = autoSwap;
      var10001[27] = maxDamage;
      var10001[28] = autoTool;
      var10001[29] = autoTotem;
      var10001[30] = autoTrap;
      var10001[31] = blockesp;
      var10001[32] = blockOverlay;
      var10001[33] = chestStealer;
      var10001[34] = clientSounds;
      var10001[35] = clickPearl;
      var10001[36] = gappletracker;
      var10001[37] = cosmetics;
      var10001[38] = cubes;
      var10001[39] = deathCoord;
      var10001[40] = ecopen;
      var10001[41] = elytraBoost;
      var10001[42] = elytraBounce;
      var10001[43] = elytraMotion;
      var10001[44] = elytraTarget;
      var10001[45] = elytraresolver;
      var10001[46] = elytraSlow;
      var10001[47] = entityESP;
      var10001[48] = fireworkESP;
      var10001[49] = fastBreak;
      var10001[50] = fastExp;
      var10001[51] = freeCam;
      var10001[52] = fullBright;
      var10001[53] = grimNoFall;
      var10001[54] = highJump;
      var10001[55] = helpMessage;
      var10001[56] = hitBubbles;
      var10001[57] = hitMarker;
      var10001[58] = interfaceModule;
      var10001[59] = interpolateF5;
      var10001[60] = guiMove;
      var10001[61] = itemAim;
      var10001[62] = itemRelease;
      var10001[63] = itemScroller;
      var10001[64] = jumpCircle;
      var10001[65] = killEffect;
      var10001[66] = kTLeave;
      var10001[67] = leavetracker;
      var10001[68] = lineGlyphes;
      var10001[69] = lockSlot;
      var10001[70] = openWalls;
      var10001[71] = lootTracker;
      var10001[72] = noClip;
      var10001[73] = noJumpDelay;
      var10001[74] = nuker;
      var10001[75] = noPush;
      var10001[76] = noSlow;
      var10001[77] = noVelocity;
      var10001[78] = noVignette;
      var10001[79] = noWeb;
      var10001[80] = packetCriticals;
      var10001[81] = particle;
      var10001[82] = playerFakeLags;
      var10001[83] = projectile;
      var10001[84] = scoreboardHP;
      var10001[85] = hpFixer;
      var10001[86] = noRender;
      var10001[87] = rPSpoofer;
      var10001[88] = seeInvisibles;
      var10001[89] = shaderEsp;
      var10001[90] = shaderHands;
      var10001[91] = serverHelper;
      var10001[92] = shulkerPreview;
      var10001[93] = scanner;
      var10001[94] = speed;
      var10001[95] = sprint;
      var10001[96] = step;
      var10001[97] = swingAnimations;
      var10001[98] = targetESP;
      var10001[99] = talismanIndication;
      var10001[100] = targetPearl;
      var10001[101] = timer;
      var10001[102] = totemAngel;
      var10001[103] = tpsSync;
      var10001[104] = tpBack;
      var10001[105] = tpGrim;
      var10001[106] = tpLoot;
      var10001[107] = trails;
      var10001[108] = trajectories;
      var10001[109] = triggerBot;
      var10001[110] = unhook;
      var10001[111] = useTracker;
      var10001[112] = viewModel;
      var10001[113] = worldTweaks;
      var10001[114] = xCarry;
      var10001[115] = rwHelper;
      var10001[116] = ftHelper;
      var10001[117] = elytraHelper;
      this.add(var10001);
   }

   private void add(
      Module... val
   ) {
      this.getObject()
         .addAll(
            List.of(
               val
            )
         );
   }
}
