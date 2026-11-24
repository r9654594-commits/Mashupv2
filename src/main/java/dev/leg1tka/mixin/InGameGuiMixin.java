package dev.leg1tka.mixin;

import dev.leg1tka.api.events.EventInvoker;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.utils.ScoreboardLine;
import dev.leg1tka.render.KawaseBlur;
import dev.leg1tka.core.IMinecraft;
import dev.leg1tka.core.ModuleManager;
import dev.leg1tka.modules.misc.NameProtect;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameHud.class})
public class InGameGuiMixin implements IMinecraft {
   private static final int DOMAIN_COLOR = 15557921;
   @Shadow
   @Final
   private MinecraftClient client;

   @Inject(
      method = {"renderVignetteOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo ci) {
      if (ModuleManager.noVignette.isEnable()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      KawaseBlur.getInstance().beginFrame();
      if (EventInvoker.hasListeners(EventRender.Default.class)) {
         new EventRender.Default(context, tickCounter.getTickDelta(true)).call();
      }
   }

   @Shadow
   private PlayerEntity getCameraPlayer() {
      return null;
   }

   @Inject(
      method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
      at = {@At("HEAD")},
      cancellable = true
   )
   private void AspectDLC$renderPatchedScoreboard(DrawContext drawContext, ScoreboardObjective objective, CallbackInfo ci) {
      if (this.AspectDLC$shouldPatchScoreboard()) {
         Scoreboard scoreboard = objective.getScoreboard();
         NumberFormat numberFormat = objective.getNumberFormatOr(StyledNumberFormat.RED);
         List<ScoreboardLine> lines = scoreboard.getScoreboardEntries(objective)
            .stream()
            .filter(entry -> !entry.hidden())
            .sorted(Comparator.comparing(ScoreboardEntry::value).reversed().thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER))
            .limit(15L)
            .map(entry -> {
               Team team = scoreboard.getScoreHolderTeam(entry.owner());
               Text name = this.AspectDLC$patchText(Team.decorateName(team, entry.name()));
               Text score = entry.formatted(numberFormat);
               int scoreWidth = this.client.textRenderer.getWidth(score);
               return new ScoreboardLine(name, score, scoreWidth);
            })
            .toList();
         Text title = this.AspectDLC$patchText(objective.getDisplayName());
         int titleWidth = this.client.textRenderer.getWidth(title);
         int maxWidth = titleWidth;
         int separatorWidth = this.client.textRenderer.getWidth(": ");

         for (ScoreboardLine line : lines) {
            maxWidth = Math.max(maxWidth, this.client.textRenderer.getWidth(line.name) + (line.scoreWidth > 0 ? separatorWidth + line.scoreWidth : 0));
         }

         int lineCount = lines.size();
         int totalHeight = lineCount * 9;
         int bottom = drawContext.getScaledWindowHeight() / 2 + totalHeight / 3;
         int left = drawContext.getScaledWindowWidth() - maxWidth - 3;
         int right = drawContext.getScaledWindowWidth() - 1;
         int bodyColor = this.client.options.getTextBackgroundColor(0.3F);
         int headerColor = this.client.options.getTextBackgroundColor(0.4F);
         int top = bottom - lineCount * 9;
         drawContext.fill(left - 2, top - 10, right, top - 1, headerColor);
         drawContext.fill(left - 2, top - 1, right, bottom, bodyColor);
         drawContext.drawText(this.client.textRenderer, title, left + maxWidth / 2 - titleWidth / 2, top - 9, -1, false);

         for (int index = 0; index < lineCount; index++) {
            ScoreboardLine line = lines.get(index);
            int y = bottom - (lineCount - index) * 9;
            drawContext.drawText(this.client.textRenderer, line.name, left, y, -1, false);
            drawContext.drawText(this.client.textRenderer, line.score, right - line.scoreWidth, y, -1, false);
         }

         ci.cancel();
      }
   }

   private boolean AspectDLC$shouldPatchScoreboard() {
      return ModuleManager.INSTANCE != null && ModuleManager.nameProtect != null && ModuleManager.nameProtect.isEnable();
   }

   private Text AspectDLC$patchText(Text text) {
      NameProtect nameProtect = ModuleManager.nameProtect;
      Text patched = nameProtect.patchText(text);
      String patchedString = patched.getString();
      if (nameProtect.shouldHideGrief()) {
         if (patchedString.contains("Анархия-")) {
            patchedString = patchedString.replaceAll("Анархия-\\d+", "AspectDLC.fun");
         }

         if (patchedString.contains("ГРИФ #")) {
            patchedString = patchedString.replaceAll("ГРИФ #\\d+", "AspectDLC.fun");
         }
      }

      return (Text)(patchedString.equals(patched.getString())
         ? patched
         : Text.literal(patchedString).setStyle(patched.getStyle().withColor(TextColor.fromRgb(15557921))));
   }
}
