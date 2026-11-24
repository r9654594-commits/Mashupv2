package dev.leg1tka.modules.render;


import dev.leg1tka.core.Module;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;
import dev.leg1tka.utils.*;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

public class Blockesp extends Module {
   public static Blockesp INSTANCE = new Blockesp();
   private static final float BOX_LINE_WIDTH = 2.0F;
   private static final float FILL_ALPHA = 0.18F;
   private static final float GREEN_R = 0.1F;
   private static final float GREEN_G = 1.0F;
   private static final float GREEN_B = 0.15F;
   private static final long SCAN_INTERVAL_MS = 50L;
   private static final int MAX_CHUNKS_PER_PASS = 2;
   private final NumberSetting distance;
   private final Set<String> trackedBlocks;
   private final Map<BlockPos, String> foundBlocks;
   private final Set<ChunkPos> scannedChunks;
   private ChunkPos lastPlayerChunk;
   private int lastScanRadius;
   private long lastScanTime;

   public Blockesp() {
      super("Blockesp", Category.RENDER);
      String var1 = "BlockESP";
      this.distance = new NumberSetting(
         "Дистанция", 60.0F, 10.0F, 120.0F, 1.0F
      );
      this.trackedBlocks = ConcurrentHashMap.newKeySet();
      this.foundBlocks = new ConcurrentHashMap<>();
      this.scannedChunks = ConcurrentHashMap.newKeySet();
      this.lastScanRadius = -1;
      this.addSettings(new Setting[]{this.distance});
   }

   @Override
   public void onEnable() {
      this.resetScanState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetScanState();
      super.onDisable();
   }

   @EventLink(
      priority = 100
   )
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.world != null && mc.player != null && !this.trackedBlocks.isEmpty()) {
         int n = this.getDistance();
         ChunkPos chunkPos = new ChunkPos(
            mc.player.getBlockPos()
         );
         if (n
            != this.lastScanRadius) {
            this.resetScanState();
            this.lastScanRadius = n;
         }

         if (this.lastPlayerChunk == null
            || !this.lastPlayerChunk
               .equals(
                  chunkPos
               )) {
            this.scannedChunks.clear();
            this.lastPlayerChunk = chunkPos;
         }

         long val2 = System.currentTimeMillis();
         if (val2
               - this.lastScanTime
            >= 50L) {
            this.scanNearbyBlocks(
               n
            );
            this.lastScanTime = val2;
         }

         this.cleanupInvalidAndDistantBlocks(
            mc.player.getPos(),
            n
         );
         this.renderFoundBlocks(
            val.getMatrices()
         );
      }
   }

   private void scanNearbyBlocks(
      int val
   ) {
      if (mc.world != null && mc.player != null) {
         BlockPos pos = mc.player
            .getBlockPos();
         int n = pos.getX()
            >> 4;
         int n2 = pos.getZ()
            >> 4;
         int n3 = (
               val
                  >> 4
            )
            + 2;
         List<ChunkPos> chunkPos = new ArrayList<>();

         for (int i = -n3;
            i
               <= n3;
            i++
         ) {
            for (int i2 = -n3;
               i2
                  <= n3;
               i2++
            ) {
               ChunkPos chunkPos2 = new ChunkPos(
                  n
                     + i,
                  n2
                     + i2
               );
               if (!this.scannedChunks
                  .contains(
                     chunkPos2
                  )) {
                  chunkPos.add(
                     chunkPos2
                  );
               }
            }
         }

         chunkPos.sort(
            (val2, val3) -> {
               long val4 = this.chunkDistanceSq(
                  val2,
                  n,
                  n2
               );
               long val5 = this.chunkDistanceSq(
                  val3,
                  n,
                  n2
               );
               return Long.compare(
                  val4,
                  val5
               );
            }
         );
         int n4 = 0;

         for (ChunkPos chunkPos3 : chunkPos) {
            if (n4
               >= 2) {
               break;
            }

            WorldChunk chunk2 = mc.world
               .getChunk(
                  chunkPos3.x,
                  chunkPos3.z
               );
            if (chunk2
               != null) {
               this.scanChunk(
                  chunk2,
                  pos,
                  val
               );
               this.scannedChunks
                  .add(
                     chunkPos3
                  );
               n4++;
            }
         }
      }
   }

   private void scanChunk(
      WorldChunk val,
      BlockPos val2,
      int val3
   ) {
      int n = val.getPos()
         .getStartX();
      int n2 = val.getPos()
         .getStartZ();
      int n3 = n
         + 15;
      int n4 = n2
         + 15;
      int n5 = Math.max(
         mc.world.getBottomY(),
         val2.getY()
            - val3
      );
      int n6 = Math.min(
         mc.world.getTopYInclusive(),
         val2.getY()
            + val3
      );
      int n7 = val3
         * val3;
      Mutable val4 = new Mutable();

      for (int i = n;
         i
            <= n3;
         i++
      ) {
         for (int i2 = n2;
            i2
               <= n4;
            i2++
         ) {
            for (int i3 = n5;
               i3
                  <= n6;
               i3++
            ) {
               val4.set(
                  i,
                  i3,
                  i2
               );
               if (!(
                  val4.getSquaredDistance(
                        val2
                     )
                     > (double)n7
               )) {
                  BlockState state = val.getBlockState(
                     val4
                  );
                  if (!state.isAir()
                     )
                   {
                     String str = Registries.BLOCK
                        .getId(
                           state.getBlock()
                        )
                        .getPath()
                        .toLowerCase();
                     if (this.trackedBlocks
                        .contains(
                           str
                        )) {
                        this.foundBlocks
                           .put(
                              val4.toImmutable(),
                              str
                           );
                     }
                  }
               }
            }
         }
      }
   }

   private void cleanupInvalidAndDistantBlocks(
      Vec3d val,
      int val2
   ) {
      if (mc.world == null) {
         this.foundBlocks.clear();
      } else {
         int n = val2
            * val2;
         this.foundBlocks
            .entrySet()
            .removeIf(
               val3 -> {
                  BlockPos pos = val3.getKey();
                  BlockState state = mc.world
                     .getBlockState(
                        pos
                     );
                  if (state.isAir()
                     )
                   {
                     return true;
                  } else {
                     String str = Registries.BLOCK
                        .getId(
                           state.getBlock()
                        )
                        .getPath()
                        .toLowerCase();
                     return !this.trackedBlocks
                           .contains(
                              str
                           )
                        ? true
                        : pos.getSquaredDistance(
                              val
                           )
                           > (double)n;
                  }
               }
            );
      }
   }

   private void renderFoundBlocks(
      MatrixStack val
   ) {
      if (!this.foundBlocks.isEmpty()) {
         Vec3d vec = mc.gameRenderer
            .getCamera()
            .getPos();
         val.push();
         val.translate(
            -vec.x,
            -vec.y,
            -vec.z
         );
         Matrix4f matrix = val.peek()
            .getPositionMatrix();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder buffer = tessellator.begin(
            DrawMode.QUADS, VertexFormats.POSITION_COLOR
         );

         for (BlockPos pos : this.foundBlocks
            .keySet()) {
            this.addFilledBox(
               buffer,
               matrix,
               pos,
               0.1F,
               1.0F,
               0.15F,
               0.18F
            );
         }

         BufferRenderer.drawWithGlobalProgram(
            buffer.end()
         );
         RenderSystem.lineWidth(2.0F);
         BufferBuilder buffer2 = tessellator.begin(
            DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR
         );

         for (BlockPos pos2 : this.foundBlocks
            .keySet()) {
            this.addOutlinedBox(
               buffer2,
               matrix,
               pos2,
               0.1F,
               1.0F,
               0.15F,
               1.0F
            );
         }

         BufferRenderer.drawWithGlobalProgram(
            buffer2.end()
         );
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         val.pop();
      }
   }

   private void addFilledBox(
      BufferBuilder val,
      Matrix4f val2,
      BlockPos val3,
      float val4,
      float val5,
      float val6,
      float val7
   ) {
      float x2 = (float)val3.getX();
      float y2 = (float)val3.getY();
      float f = (float)val3.getZ();
      float f2 = x2
         + 1.0F;
      float f3 = y2
         + 1.0F;
      float f4 = f
         + 1.0F;
      val.vertex(
            val2,
            x2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
   }

   private void addOutlinedBox(
      BufferBuilder val,
      Matrix4f val2,
      BlockPos val3,
      float val4,
      float val5,
      float val6,
      float val7
   ) {
      float x2 = (float)val3.getX();
      float y2 = (float)val3.getY();
      float f = (float)val3.getZ();
      float f2 = x2
         + 1.0F;
      float f3 = y2
         + 1.0F;
      float f4 = f
         + 1.0F;
      val.vertex(
            val2,
            x2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            f2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            y2,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      val.vertex(
            val2,
            x2,
            f3,
            f4
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
   }

   public void addBlock(
      String val
   ) {
      this.trackedBlocks
         .add(
            val.toLowerCase()
         );
      this.scannedChunks.clear();
      this.foundBlocks.clear();
   }

   public void removeBlock(
      String val
   ) {
      this.trackedBlocks
         .remove(
            val.toLowerCase()
         );
      this.foundBlocks
         .entrySet()
         .removeIf(
            val2 -> val2.getValue()
                  .equalsIgnoreCase(
                     val
                  )
         );
   }

   public void clearBlocks() {
      this.trackedBlocks.clear();
      this.resetScanState();
   }

   public Set<String> getTrackedBlocks() {
      return new HashSet<>(this.trackedBlocks);
   }

   public boolean isTracking(
      String val
   ) {
      return this.trackedBlocks
         .contains(
            val.toLowerCase()
         );
   }

   private int getDistance() {
      return Math.round(this.distance.get());
   }

   private long chunkDistanceSq(
      ChunkPos val,
      int val2,
      int val3
   ) {
      long val4 = (long)(
         val.x
            - val2
      );
      long val5 = (long)(
         val.z
            - val3
      );
      return val4
            * val4
         + val5
            * val5;
   }

   private void resetScanState() {
      this.foundBlocks.clear();
      this.scannedChunks.clear();
      this.lastPlayerChunk = null;
      this.lastScanTime = 0L;
      this.lastScanRadius = -1;
   }
}
