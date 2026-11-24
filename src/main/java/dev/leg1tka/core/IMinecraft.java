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


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public interface IMinecraft {
   public MinecraftClient mc = MinecraftClient.getInstance();
   public Window mw = mc.getWindow();
}
