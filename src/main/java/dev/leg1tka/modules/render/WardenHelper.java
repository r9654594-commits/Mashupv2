package dev.leg1tka.modules.render;

import dev.leg1tka.core.Module;
import dev.leg1tka.settings.BooleanSetting;
import dev.leg1tka.settings.Setting;
import dev.leg1tka.core.Category;
import dev.leg1tka.utils.WardenBotState;
import dev.leg1tka.utils.WardenChestHolder;
import dev.leg1tka.utils.WardenChestState;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventUpdate;
import dev.leg1tka.utils.ChatUtil;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

public class WardenHelper extends Module {
   public static final WardenHelper INSTANCE = new WardenHelper();
   private final BooleanSetting bot = new BooleanSetting("AutoBot", false);
   private final Map<BlockPos, WardenChestHolder> chestMap = new ConcurrentHashMap<>();
   private static final Pattern timerPattern = Pattern.compile("(?:§.|&.)*(\\d{1,2}):(\\d{2})");
   private long lastScanTime = 0L;
   private WardenBotState botState = WardenBotState.IDLE;
   private long waitTime = 0L;
   private BlockPos targetPos = null;
   private final Set<BlockPos> blacklistedChests = new HashSet<>();

   public WardenHelper() {
      super("WardenHelper", "WardenHelper", Category.RENDER);
      this.addSettings(new Setting[]{this.bot});
   }

   private void setBaritoneGoal(BlockPos goalPos) {
      if (goalPos != null && mc.player != null && mc.player.networkHandler != null) {
         mc.player.networkHandler.sendChatMessage("#goto " + goalPos.getX() + " " + goalPos.getY() + " " + goalPos.getZ());
      }
   }

   private void cancelBaritone() {
      if (mc.player != null && mc.player.networkHandler != null) {
         mc.player.networkHandler.sendChatMessage("#stop");
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.chestMap.clear();
      if (this.bot.isState()) {
         this.cancelBaritone();
      }
      this.botState = WardenBotState.IDLE;
      this.targetPos = null;
   }

   private List<BlockPos> getChests() {
      List<BlockPos> chests = new ArrayList<>();
      int chunkRadius = 4;
      ChunkPos playerChunk = mc.player.getChunkPos();

      for (int chunkX = playerChunk.x - chunkRadius; chunkX <= playerChunk.x + chunkRadius; chunkX++) {
         for (int chunkZ = playerChunk.z - chunkRadius; chunkZ <= playerChunk.z + chunkRadius; chunkZ++) {
            WorldChunk chunk = mc.world.getChunk(chunkX, chunkZ);
            if (chunk != null) {
               for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                  if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof TrappedChestBlockEntity) {
                     chests.add(blockEntity.getPos());
                  }
               }
            }
         }
      }
      return chests;
   }

   private boolean isValuableItem(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      }
      if (stack.isOf(Items.PHANTOM_MEMBRANE) || stack.isOf(Items.ELYTRA) || stack.isOf(Items.BOW)) {
         return true;
      }
      String name = stack.getName().getString().toLowerCase();
      return name.contains("снотворное")
         || name.contains("сет")
         || name.contains("чарка")
         || name.contains("молот")
         || name.contains("крушитель")
         || name.contains("титан")
         || name.contains("световая")
         || name.contains("палладина");
   }

   private boolean isFull() {
      for (int slot = 9; slot < 36; slot++) {
         if (mc.player.getInventory().getStack(slot).isEmpty()) {
            return false;
         }
      }
      return true;
   }

   private void tickBot(long currentTime) {
      if (currentTime < this.waitTime) {
         return;
      }

      switch (this.botState) {
         case IDLE -> {
            if (this.isFull()) {
               mc.player.networkHandler.sendCommand("home");
               this.waitTime = currentTime + 8000L;
               this.botState = WardenBotState.WAIT_HOME_LOAD;
               return;
            }

            double minDistSq = 99999.0;
            BlockPos closestPos = null;

            for (WardenChestHolder holder : this.chestMap.values()) {
               if (holder.state == WardenChestState.UNKNOWN || holder.state == WardenChestState.ACTIVE) {
                  int x = holder.pos.getX();
                  int z = holder.pos.getZ();
                  if (x >= 1919 && x <= 2042 && z >= 1938 && z <= 2063) {
                     double distSq = mc.player.squaredDistanceTo(holder.pos.toCenterPos());
                     if (distSq < minDistSq) {
                        minDistSq = distSq;
                        closestPos = holder.pos;
                     }
                  }
               }
            }

            if (closestPos != null) {
               this.targetPos = closestPos;
               this.setBaritoneGoal(this.targetPos);
               this.botState = WardenBotState.PATH;
               ChatUtil.sendMessage("[WardenBot] Иду к сундуку на координатах: " + this.targetPos.toShortString());
            }
         }
         case PATH -> {
            if (this.targetPos == null) {
               this.botState = WardenBotState.IDLE;
               return;
            }
            if (mc.player.squaredDistanceTo(this.targetPos.toCenterPos()) < 19.36) {
               this.cancelBaritone();
               BlockHitResult hitResult = new BlockHitResult(this.targetPos.toCenterPos(), Direction.UP, this.targetPos, false);
               mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
               this.waitTime = currentTime + 50L;
               this.botState = WardenBotState.WAIT_GUI;
               ChatUtil.sendMessage("[WardenBot] Открываю сундук...");
            }
         }
         case WAIT_GUI -> {
            if (mc.currentScreen instanceof GenericContainerScreen) {
               this.botState = WardenBotState.LOOT;
               this.waitTime = currentTime;
            } else if (currentTime > this.waitTime + 1200L) {
               this.botState = WardenBotState.IDLE;
            }
         }
         case LOOT -> {
            if (!(mc.currentScreen instanceof GenericContainerScreen containerScreen)) {
               if (currentTime > this.waitTime + 1000L) {
                  this.botState = WardenBotState.IDLE;
                  this.targetPos = null;
               }
               return;
            }

            GenericContainerScreenHandler handler = (GenericContainerScreenHandler) containerScreen.getScreenHandler();
            int containerSize = handler.getInventory().size();
            boolean itemLooted = false;

            for (int slot = 0; slot < containerSize; slot++) {
               if (this.isValuableItem(handler.getSlot(slot).getStack())) {
                  mc.interactionManager.clickSlot(handler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
                  itemLooted = true;
                  this.waitTime = currentTime + 50L;
                  break;
               }
            }

            if (!itemLooted) {
               mc.player.closeHandledScreen();
               this.targetPos = null;
               this.waitTime = currentTime + 100L;
               this.botState = WardenBotState.IDLE;
            }
         }
         case WAIT_HOME_LOAD -> {
            this.botState = WardenBotState.HOME_PATH;
         }
         case HOME_PATH -> {
            double minDistSq = 99999.0;
            BlockPos closestChest = null;

            for (BlockPos chestPos : this.getChests()) {
               if (!this.blacklistedChests.contains(chestPos)) {
                  double distSq = mc.player.squaredDistanceTo(chestPos.toCenterPos());
                  if (distSq < minDistSq) {
                     minDistSq = distSq;
                     closestChest = chestPos;
                  }
               }
            }

            if (closestChest != null) {
               this.targetPos = closestChest;
               this.setBaritoneGoal(this.targetPos);
               this.botState = WardenBotState.WAIT_HOME_GUI;
            } else {
               this.blacklistedChests.clear();
               this.botState = WardenBotState.GO_WARP;
            }
         }
         case WAIT_HOME_GUI -> {
            if (this.targetPos == null) {
               this.botState = WardenBotState.HOME_PATH;
               return;
            }
            if (mc.player.squaredDistanceTo(this.targetPos.toCenterPos()) < 19.36) {
               this.cancelBaritone();
               BlockHitResult hitResult = new BlockHitResult(this.targetPos.toCenterPos(), Direction.UP, this.targetPos, false);
               mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
               this.waitTime = currentTime + 50L;
               this.botState = WardenBotState.DUMP;
            }
         }
         case DUMP -> {
            if (!(mc.currentScreen instanceof GenericContainerScreen containerScreen)) {
               if (currentTime > this.waitTime + 1000L) {
                  this.blacklistedChests.add(this.targetPos);
                  this.botState = WardenBotState.HOME_PATH;
               }
               return;
            }

            GenericContainerScreenHandler handler = (GenericContainerScreenHandler) containerScreen.getScreenHandler();
            int containerSize = handler.getInventory().size();
            boolean dumpedItem = false;

            for (int slot = containerSize; slot < containerSize + 27; slot++) {
               if (!handler.getSlot(slot).getStack().isEmpty()) {
                  mc.interactionManager.clickSlot(handler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
                  dumpedItem = true;
                  this.waitTime = currentTime + 50L;
                  break;
               }
            }

            if (!dumpedItem) {
               boolean hasRemainingItems = false;
               for (int slot = containerSize; slot < containerSize + 27; slot++) {
                  if (!handler.getSlot(slot).getStack().isEmpty()) {
                     hasRemainingItems = true;
                     break;
                  }
               }

               mc.player.closeHandledScreen();
               if (hasRemainingItems) {
                  this.blacklistedChests.add(this.targetPos);
                  this.botState = WardenBotState.HOME_PATH;
               } else {
                  this.blacklistedChests.clear();
                  this.botState = WardenBotState.GO_WARP;
               }
               this.waitTime = currentTime + 100L;
            }
         }
         case GO_WARP -> {
            mc.player.networkHandler.sendCommand("warp warden");
            this.waitTime = currentTime + 8000L;
            this.botState = WardenBotState.WAIT_WARP_LOAD;
         }
         case WAIT_WARP_LOAD -> {
            this.botState = WardenBotState.IDLE;
         }
      }
   }

   @EventLink
   public void onUpdate(EventUpdate event) {
      if (mc.world == null || mc.player == null) {
         return;
      }

      long currentTime = System.currentTimeMillis();
      if (!this.bot.isState()) {
         if (this.botState != WardenBotState.IDLE) {
            this.botState = WardenBotState.IDLE;
            this.targetPos = null;
            this.cancelBaritone();
            ChatUtil.sendMessage("[WardenBot] Бот остановлен.");
         }
      } else {
         this.tickBot(currentTime);
      }

      if (currentTime - this.lastScanTime >= 50L) {
         this.lastScanTime = currentTime;

         for (BlockPos chestPos : this.getChests()) {
            if (!this.chestMap.containsKey(chestPos)) {
               this.chestMap.put(chestPos, new WardenChestHolder(chestPos));
            }
         }

         for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ArmorStandEntity || entity instanceof TextDisplayEntity) {
               String name = entity.hasCustomName() ? entity.getCustomName().getString() : "";
               if (entity instanceof TextDisplayEntity textDisplay && textDisplay.getText() != null) {
                  name = textDisplay.getText().getString();
               }

               Matcher matcher = timerPattern.matcher(name);
               if (matcher.find()) {
                  try {
                     int minutes = Integer.parseInt(matcher.group(1));
                     int seconds = Integer.parseInt(matcher.group(2));
                     long remainingMillis = (minutes * 60L + seconds) * 1000L;
                     long readyTime = currentTime + remainingMillis;

                     WardenChestHolder closestHolder = null;
                     double minDistSq = 25.0;

                     for (WardenChestHolder holder : this.chestMap.values()) {
                        double distSq = holder.pos.toCenterPos().squaredDistanceTo(entity.getPos());
                        if (distSq < minDistSq) {
                           minDistSq = distSq;
                           closestHolder = holder;
                        }
                     }

                     if (closestHolder != null && (closestHolder.state != WardenChestState.TIMER || Math.abs(closestHolder.readyTime - readyTime) > 2000L)) {
                        closestHolder.state = WardenChestState.TIMER;
                        closestHolder.readyTime = readyTime;
                     }
                  } catch (Exception ignored) {
                  }
               }
            }
         }
      }

      for (WardenChestHolder holder : this.chestMap.values()) {
         if (holder.state == WardenChestState.TIMER && currentTime >= holder.readyTime) {
            holder.state = WardenChestState.ACTIVE;
         }
      }
   }

   @EventLink
   public void onRender3D(Event3DRender event) {
      if (this.chestMap.isEmpty() || mc.gameRenderer == null || mc.gameRenderer.getCamera() == null) {
         return;
      }

      Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
      MatrixStack matrices = event.getMatrices();
      matrices.push();
      matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
      Matrix4f matrix = matrices.peek().getPositionMatrix();

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder fillBuffer = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (WardenChestHolder holder : this.chestMap.values()) {
         float r = 0.0F, g = 0.0F, b = 0.0F;
         if (holder.state == WardenChestState.UNKNOWN) {
            r = 1.0F; g = 1.0F; b = 0.0F;
         } else if (holder.state == WardenChestState.TIMER) {
            r = 1.0F; g = 0.2F; b = 0.2F;
         } else if (holder.state == WardenChestState.ACTIVE) {
            r = 0.2F; g = 1.0F; b = 0.2F;
         }
         this.addFilled(fillBuffer, matrix, holder.pos, r, g, b, 0.18F);
      }
      BufferRenderer.drawWithGlobalProgram(fillBuffer.end());

      RenderSystem.lineWidth(2.0F);
      BufferBuilder outlineBuffer = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

      for (WardenChestHolder holder : this.chestMap.values()) {
         float r = 0.0F, g = 0.0F, b = 0.0F;
         if (holder.state == WardenChestState.UNKNOWN) {
            r = 1.0F; g = 1.0F; b = 0.0F;
         } else if (holder.state == WardenChestState.TIMER) {
            r = 1.0F; g = 0.2F; b = 0.2F;
         } else if (holder.state == WardenChestState.ACTIVE) {
            r = 0.2F; g = 1.0F; b = 0.2F;
         }
         this.addOutlined(outlineBuffer, matrix, holder.pos, r, g, b, 1.0F);
      }
      BufferRenderer.drawWithGlobalProgram(outlineBuffer.end());

      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      matrices.pop();

      Font font = Fonts.getFont("sf_regular", 20);
      if (font != null) {
         long currentTime = System.currentTimeMillis();
         for (WardenChestHolder holder : this.chestMap.values()) {
            if (holder.state != WardenChestState.UNKNOWN) {
               String labelText = "";
               int textColor = -1;
               if (holder.state == WardenChestState.TIMER) {
                  long diff = Math.max(0L, holder.readyTime - currentTime);
                  int totalSeconds = (int) (diff / 1000L);
                  labelText = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
                  textColor = -43691;
               } else if (holder.state == WardenChestState.ACTIVE) {
                  labelText = "Активен";
                  textColor = -11141291;
               }

               Vec3d textPos = holder.pos.toCenterPos().add(0.0, 0.7, 0.0);
               this.draw3DText(matrices, labelText, textPos, font, textColor);
            }
         }
      }
   }

   private void draw3DText(MatrixStack matrices, String text, Vec3d pos, Font font, int color) {
      Camera camera = mc.gameRenderer.getCamera();
      Vec3d cameraPos = camera.getPos();
      matrices.push();
      matrices.translate(pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
      matrices.scale(-0.025F, -0.025F, 0.025F);

      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();

      float textWidth = font.getStringWidth(text);
      float paddingY = 10.0F;
      float paddingX = 4.0F;

      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      Matrix4f matrix = matrices.peek().getPositionMatrix();

      float left = -textWidth / 2.0F - paddingX;
      float right = textWidth / 2.0F + paddingX;
      float top = -paddingY - paddingX;
      float bottom = paddingX;
      float alpha = 0.5F;

      buffer.vertex(matrix, left, top, 0.0F).color(0.0F, 0.0F, 0.0F, alpha);
      buffer.vertex(matrix, left, bottom, 0.0F).color(0.0F, 0.0F, 0.0F, alpha);
      buffer.vertex(matrix, right, bottom, 0.0F).color(0.0F, 0.0F, 0.0F, alpha);
      buffer.vertex(matrix, right, top, 0.0F).color(0.0F, 0.0F, 0.0F, alpha);
      BufferRenderer.drawWithGlobalProgram(buffer.end());

      font.drawCenteredString(matrices, text, 0.0F, -paddingY, color);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      matrices.pop();
   }

   private void addFilled(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
      float minX = (float) pos.getX() + 0.0625F;
      float minY = (float) pos.getY();
      float minZ = (float) pos.getZ() + 0.0625F;
      float maxX = (float) pos.getX() + 0.9375F;
      float maxY = (float) pos.getY() + 0.875F;
      float maxZ = (float) pos.getZ() + 0.9375F;

      // Bottom
      buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
      // Top
      buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
      // North
      buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
      // South
      buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
      // West
      buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
      // East
      buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
   }

   private void addOutlined(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
      float minX = (float) pos.getX() + 0.0625F;
      float minY = (float) pos.getY();
      float minZ = (float) pos.getZ() + 0.0625F;
      float maxX = (float) pos.getX() + 0.9375F;
      float maxY = (float) pos.getY() + 0.875F;
      float maxZ = (float) pos.getZ() + 0.9375F;

      // Bottom 4 edges
      buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a); buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a); buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a); buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a); buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);

      // Top 4 edges
      buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a); buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a); buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a); buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a); buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

      // 4 Vertical edges
      buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a); buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a); buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
      buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a); buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
      buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a); buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
   }
}
