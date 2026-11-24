package dev.leg1tka.modules.render;

import dev.leg1tka.core.Module;
import dev.leg1tka.settings.BooleanSetting;
import dev.leg1tka.settings.NumberSetting;
import dev.leg1tka.settings.Setting;
import dev.leg1tka.core.Category;
import dev.leg1tka.render.Render2D;
import dev.leg1tka.render.ColorUtil;
import dev.leg1tka.render.ThemeColors;
import dev.leg1tka.utils.TrackedLootSource;
import dev.leg1tka.utils.LootSourceType;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LootTracker extends Module {
   public static final LootTracker INSTANCE = new LootTracker();
   private static final float TAG_BOX_HEIGHT = 12.5F;
   private static final float TAG_PADDING = 5.0F;
   private static final float TAG_HUD_RADIUS = 1.1F;
   private static final int TAG_HUD_ALPHA = 204;

   private final BooleanSetting showSpawners = new BooleanSetting("Спавнеры", true);
   private final BooleanSetting showMinecarts = new BooleanSetting("Вагонетки", true);
   private final NumberSetting maxDistance = new NumberSetting("Макс. дистанция", 64.0F, 16.0F, 128.0F, 1.0F);

   private final Matrix4f lastProjectionMatrix = new Matrix4f();
   private final Quaternionf lastCameraRotation = new Quaternionf();
   private Vec3d lastCameraPos = Vec3d.ZERO;
   private float lastTickDelta;
   private boolean hasProjection;
   private final List<TrackedLootSource> cachedSources = new ArrayList<>();
   private long lastCacheUpdate = 0L;
   private static final long CACHE_UPDATE_INTERVAL = 500L;

   public LootTracker() {
      super("LootTracker", "Показывает залутанные спавнеры и вагонетки", Category.RENDER);
      this.addSettings(new Setting[]{this.showSpawners, this.showMinecarts, this.maxDistance});
   }

   @Override
   public void onDisable() {
      this.hasProjection = false;
      this.cachedSources.clear();
      super.onDisable();
   }

   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
      if (mc.player != null && mc.world != null) {
         this.hasProjection = true;
         this.lastProjectionMatrix.set(event.getProjectionMatrix());
         this.lastCameraPos = event.getCamera().getPos();
         this.lastCameraRotation.set(event.getCamera().getRotation());
         this.lastTickDelta = event.getTickDelta();

         long now = System.currentTimeMillis();
         if (now - this.lastCacheUpdate > CACHE_UPDATE_INTERVAL) {
            this.updateCache();
            this.lastCacheUpdate = now;
         }
      }
   }

   @EventLink(priority = 100)
   public void onRender2D(EventRender.Default event) {
      if (this.hasProjection && mc.world != null && mc.player != null) {
         MatrixStack matrices = event.getContext().getMatrices();
         Font font = Fonts.getFont("sf_regular", 14);
         if (font != null) {
            float maxDistSq = this.maxDistance.getValue().floatValue() * this.maxDistance.getValue().floatValue();
            for (TrackedLootSource source : this.cachedSources) {
               double distSq = mc.player.squaredDistanceTo(source.pos.getX(), source.pos.getY(), source.pos.getZ());
               if (distSq <= maxDistSq) {
                  Vec3d screenPos = this.worldToScreen(new Vec3d(
                     source.pos.getX() + 0.5,
                     source.pos.getY() + 1.5,
                     source.pos.getZ() + 0.5
                  ));
                  if (screenPos != null) {
                     this.drawLootTag(matrices, font, (float) screenPos.x, (float) screenPos.y, source);
                  }
               }
            }
         }
      }
   }

   private void updateCache() {
      this.cachedSources.clear();
      if (this.showSpawners.isState()) {
         int viewDistance = (Integer) mc.options.getViewDistance().getValue();
         ChunkPos playerChunk = mc.player.getChunkPos();

         for (int dx = -viewDistance; dx <= viewDistance; dx++) {
            for (int dz = -viewDistance; dz <= viewDistance; dz++) {
               WorldChunk chunk = mc.world.getChunk(playerChunk.x + dx, playerChunk.z + dz);
               if (chunk != null) {
                  for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                     if (blockEntity instanceof MobSpawnerBlockEntity spawner) {
                        if (this.hasSingleChestNearby(spawner.getPos())) {
                           int delay = this.getSpawnerDelay(spawner);
                           boolean isLooted = (delay > 0 && delay != 20) || this.isAreaExplored(spawner.getPos());
                           this.cachedSources.add(new TrackedLootSource(spawner.getPos(), LootSourceType.SPAWNER, isLooted));
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.showMinecarts.isState()) {
         for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ChestMinecartEntity minecart) {
               boolean isLooted = this.isAreaExplored(minecart.getBlockPos());
               this.cachedSources.add(new TrackedLootSource(minecart.getBlockPos(), LootSourceType.MINECART, isLooted));
            }
         }
      }
   }

   private void drawLootTag(MatrixStack matrices, Font font, float x, float y, TrackedLootSource source) {
      String typeText = source.type == LootSourceType.SPAWNER ? "Спавнер" : "Вагонетка";
      String statusText = source.isLooted ? " [Залутано]" : " [Не залутано]";
      float typeWidth = font.getStringWidth(typeText);
      float statusWidth = font.getStringWidth(statusText);
      float totalTextWidth = typeWidth + statusWidth;
      float boxWidth = totalTextWidth + 10.0F;
      float boxHeight = TAG_BOX_HEIGHT;
      float boxX = x - boxWidth / 2.0F;
      float boxY = y - boxHeight / 2.0F;

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      this.drawDefaultTagPanel(matrices, boxX, boxY, boxWidth, boxHeight);

      float textX = boxX + TAG_PADDING;
      float textY = boxY + boxHeight / 2.0F - font.getHeight() * 0.1F;
      font.drawString(matrices, typeText, textX, textY, -1);
      textX += typeWidth;

      int statusColor = source.isLooted ? -43691 : -11141291;
      font.drawString(matrices, statusText, textX, textY, statusColor);
      RenderSystem.disableBlend();
   }

   private void drawDefaultTagPanel(MatrixStack matrices, float x, float y, float width, float height) {
      int themeColor = this.getStableThemeColor();
      Render2D.drawDefaultHudPanel(
         matrices,
         x,
         y,
         width,
         height,
         TAG_HUD_RADIUS,
         TAG_HUD_RADIUS,
         ColorUtil.rgba(50, 50, 50, TAG_HUD_ALPHA),
         ColorUtil.setAlphaColor(ColorUtil.darken(themeColor, 0.15F), TAG_HUD_ALPHA),
         ColorUtil.setAlphaColor(ColorUtil.darken(themeColor, 0.05F), TAG_HUD_ALPHA)
      );
   }

   private int getStableThemeColor() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null) {
         ThemeColors theme = AspectDLC.INSTANCE.themeStorage.getThemes().getTheme();
         return (theme != null && theme.color != null && theme.color.length != 0)
            ? theme.color[0]
            : ColorUtil.getThemeColor(0);
      } else {
         return ColorUtil.getThemeColor(0);
      }
   }

   private boolean isAreaExplored(BlockPos pos) {
      int radius = 20;
      int airCount = 0;
      int totalCount = 0;

      for (int dx = -radius; dx <= radius; dx += 4) {
         for (int dy = -radius; dy <= radius; dy += 4) {
            for (int dz = -radius; dz <= radius; dz += 4) {
               BlockPos checkPos = pos.add(dx, dy, dz);
               BlockState state = mc.world.getBlockState(checkPos);
               totalCount++;
               if (state.isOf(Blocks.AIR) || state.isOf(Blocks.CAVE_AIR)) {
                  airCount++;
               }
            }
         }
      }

      return (double) airCount > (double) totalCount * 0.3;
   }

   private int getSpawnerDelay(MobSpawnerBlockEntity spawner) {
      try {
         NbtCompound nbt = spawner.createNbtWithIdentifyingData(mc.world.getRegistryManager());
         return nbt.contains("Delay") ? nbt.getShort("Delay") : 20;
      } catch (Exception e) {
         return 20;
      }
   }

   private boolean hasSingleChestNearby(BlockPos pos) {
      int radius = 3;
      for (int dx = -radius; dx <= radius; dx++) {
         for (int dy = -radius; dy <= radius; dy++) {
            for (int dz = -radius; dz <= radius; dz++) {
               BlockPos checkPos = pos.add(dx, dy, dz);
               BlockState state = mc.world.getBlockState(checkPos);
               if (state.getBlock() instanceof ChestBlock && state.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   private Vec3d worldToScreen(Vec3d pos) {
      Vector3f relativePos = new Vector3f(
         (float) (pos.x - this.lastCameraPos.x),
         (float) (pos.y - this.lastCameraPos.y),
         (float) (pos.z - this.lastCameraPos.z)
      );
      Quaternionf cameraRotation = new Quaternionf(this.lastCameraRotation).conjugate();
      relativePos.rotate(cameraRotation);

      Vector4f clipCoords = new Vector4f(relativePos.x, relativePos.y, relativePos.z, 1.0F);
      this.lastProjectionMatrix.transform(clipCoords);
      float w = clipCoords.w;
      if (w <= 1.0E-5F) {
         return null;
      }

      float ndcX = clipCoords.x / w;
      float ndcY = clipCoords.y / w;
      float screenX = (ndcX * 0.5F + 0.5F) * (float) mc.getWindow().getScaledWidth();
      float screenY = (1.0F - (ndcY * 0.5F + 0.5F)) * (float) mc.getWindow().getScaledHeight();

      if (Float.isNaN(screenX) || Float.isNaN(screenY)) {
         return null;
      }
      if (Float.isInfinite(screenX) || Float.isInfinite(screenY)) {
         return null;
      }
      return new Vec3d(screenX, screenY, 0.0);
   }
}
