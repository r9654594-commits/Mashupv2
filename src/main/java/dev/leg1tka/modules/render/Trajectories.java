package dev.leg1tka.modules.render;





import dev.leg1tka.core.Module;


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

import dev.leg1tka.api.events.implement.EventRender;

import dev.leg1tka.api.utils.render.fonts.msdf.Font;

import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;


import java.util.ArrayList;

import java.util.List;

import java.util.Optional;

import net.minecraft.block.Blocks;

import net.minecraft.client.gl.ShaderProgramKeys;

import net.minecraft.client.gui.DrawContext;

import net.minecraft.client.render.BufferBuilder;

import net.minecraft.client.render.BufferRenderer;

import net.minecraft.client.render.Camera;

import net.minecraft.client.render.Tessellator;

import net.minecraft.client.render.VertexFormats;

import net.minecraft.client.render.VertexFormat.DrawMode;

import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.enchantment.Enchantments;

import net.minecraft.entity.Entity;

import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.entity.projectile.PersistentProjectileEntity;

import net.minecraft.entity.projectile.TridentEntity;

import net.minecraft.entity.projectile.thrown.ThrownItemEntity;

import net.minecraft.item.BowItem;

import net.minecraft.item.CrossbowItem;

import net.minecraft.item.Item;

import net.minecraft.item.ItemStack;

import net.minecraft.item.Items;

import net.minecraft.item.TridentItem;

import net.minecraft.registry.tag.FluidTags;

import net.minecraft.util.Identifier;

import net.minecraft.util.hit.BlockHitResult;

import net.minecraft.util.hit.HitResult.Type;

import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.Box;

import net.minecraft.util.math.Direction;

import net.minecraft.util.math.MathHelper;

import net.minecraft.util.math.Vec3d;

import net.minecraft.world.RaycastContext;

import net.minecraft.world.RaycastContext.FluidHandling;

import net.minecraft.world.RaycastContext.ShapeType;

import org.joml.Matrix4f;

import org.joml.Quaternionf;

import org.joml.Vector3f;

import org.joml.Vector4f;



public class Trajectories extends Module {

   public static Trajectories INSTANCE = new Trajectories();

   private static final int MAX_STEPS = 440;

   private static final double SIMULATION_STEP = 0.5;

   private static final double SPLASH_RADIUS = 4.0;

   private static final Identifier GLOW_TEXTURE = ClientIdentifier.of("textures/trajectories/glow.png");

   private final NumberSetting lineWidth = new NumberSetting(

      "Ширина линии",

      2.2F,

      0.5F,

      5.0F,

      0.1F

   );

   private final List<StackHelper> flyingImpactPoints = new ArrayList<>();

   private final Matrix4f lastProjectionMatrix = new Matrix4f();

   private final Quaternionf lastCameraRotation = new Quaternionf();

   private Vec3d lastCameraPos = Vec3d.ZERO;

   private boolean hasRenderMatrices;



   public Trajectories() {

      super(

         "Trajectories",

         "Показывает траекторию предмета в руке",

         Category.RENDER

      );

      this.addSettings(new Setting[]{this.lineWidth});

   }



   @EventLink

   public void onRender3D(

      Event3DRender event

   ) {

      if (mc.player != null && mc.world != null) {

         MatrixStack matrices = event.getMatrices();

         Camera camera = event.getCamera();

         Vec3d cameraPos = camera.getPos();

         int themeColor = ColorUtil.getThemeColor();

         float tickDelta = event.getTickDelta();

         ItemStack heldStack = this.getHeldProjectileStack();

         if (!heldStack.isEmpty()

            )

          {

            VelocityHelper params = this.getParams(

               heldStack

            );

            if (params

               != null) {

               Vec3d playerEyePos = mc.player

                  .getCameraPosVec(

                     tickDelta

                  );

               Vec3d[] shotDirections = this.getShotDirections(

                  heldStack,

                  tickDelta

               );

               PointsHelper[] predictions = new PointsHelper[shotDirections.length];

               int shotIdx = 0;



               for (Vec3d dir : shotDirections) {

                  PointsHelper pred = this.predict(

                     mc.player,

                     params,

                     playerEyePos,

                     dir

                  );

                  if (pred

                        != null

                     && pred.points.length

                        >= 2) {

                     predictions[shotIdx++] = pred;

                  }

               }



               if (shotIdx

                  > 0) {

                  RenderSystem.enableBlend();

                  RenderSystem.defaultBlendFunc();

                  RenderSystem.disableCull();

                  RenderSystem.enableDepthTest();

                  RenderSystem.depthMask(false);

                  RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

                  RenderSystem.lineWidth(this.lineWidth.getValue().floatValue());

                  matrices.push();

                  matrices.translate(

                     -cameraPos.x,

                     -cameraPos.y,

                     -cameraPos.z

                  );

                  Matrix4f matrix = matrices.peek()

                     .getPositionMatrix();



                  for (int predIdx = 0;

                     predIdx

                        < shotIdx;

                     predIdx++

                  ) {

                     PointsHelper prediction = predictions[predIdx];

                     this.drawTrajectoryLine(

                        matrix,

                        prediction.points,

                        ColorUtil.setAlphaColor(

                           themeColor,

                           190

                        )

                     );

                     if (prediction.entityHit

                           != null

                        && prediction.entityHit

                           .isAlive()) {

                        this.drawEntityBox(

                           matrix,

                           prediction.entityHit,

                           ColorUtil.rgba(

                              255,

                              70,

                              70,

                              210

                           )

                        );

                     } else if (prediction.blockHit

                        != null) {

                        this.drawImpactMarker(

                           matrix,

                           prediction.hitPos,

                           prediction.blockHit

                              .getSide(),

                           ColorUtil.setAlphaColor(

                              themeColor,

                              230

                           )

                        );

                     }



                     if (heldStack.isOf(

                           Items.SPLASH_POTION

                        )

                        && prediction.hitPos

                           != null) {

                        this.drawPotionRadiusGlow(

                           matrices,

                           prediction.hitPos,

                           themeColor

                        );

                     }

                  }



                  matrices.pop();

                  RenderSystem.depthMask(true);

                  RenderSystem.enableCull();

                  RenderSystem.disableBlend();

                  RenderSystem.defaultBlendFunc();

               }

            }

         }



         this.trackFlyingEntities(

            event

         );

      }

   }



   private void trackFlyingEntities(

      Event3DRender event

   ) {

      this.flyingImpactPoints.clear();

      Box searchBox = mc.player.getBoundingBox().expand(128.0);

      List<Entity> entities = new ArrayList<>();

      entities.addAll(

         mc.world

            .getEntitiesByClass(

               PersistentProjectileEntity.class,

               searchBox,

               Entity::isAlive

            )

      );

      entities.addAll(

         mc.world

            .getEntitiesByClass(

               ThrownItemEntity.class,

               searchBox,

               Entity::isAlive

            )

      );

      MatrixStack matrices = event.getMatrices();

      Vec3d cameraPos = event.getCamera()

         .getPos();

      int themeColor = ColorUtil.getThemeColor();

      RenderSystem.enableBlend();

      RenderSystem.defaultBlendFunc();

      RenderSystem.disableCull();

      RenderSystem.enableDepthTest();

      RenderSystem.depthMask(false);

      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

      RenderSystem.lineWidth(this.lineWidth.getValue().floatValue());

      matrices.push();

      matrices.translate(

         -cameraPos.x,

         -cameraPos.y,

         -cameraPos.z

      );

      Matrix4f matrix = matrices.peek()

         .getPositionMatrix();



      for (Entity entity : entities) {

         if (!this.isStationary(

            entity

         )) {

            Vec3d velocity = entity.getVelocity();

            Vec3d pos = entity.getPos();

            int pointCount = 0;



            for (int step = 0;

               step

                  < 300;

               step++

            ) {

               Vec3d stepPos = pos;

               pos = pos.add(

                  velocity

               );

               velocity = this.calculateEntityMotion(

                  entity,

                  stepPos,

                  velocity

               );

               BlockHitResult blockHit = mc.world

                  .raycast(

                     new RaycastContext(

                        stepPos,

                        pos,

                        ShapeType.COLLIDER,

                        FluidHandling.NONE,

                        entity

                     )

                  );

               if (blockHit.getType()

                  != Type.MISS) {

                  pos = blockHit.getPos();

                  this.drawTrajectorySegment(

                     matrix,

                     stepPos,

                     pos,

                     themeColor,

                     step

                  );

                  this.drawFlyingImpactMarker(

                     matrix,

                     pos,

                     ColorUtil.setAlphaColor(

                        themeColor,

                        230

                     )

                  );

                  this.flyingImpactPoints

                     .add(

                        new StackHelper(

                           this.getEntityItem(

                              entity

                           ),

                           pos,

                           pointCount

                        )

                     );

                  break;

               }



               this.drawTrajectorySegment(

                  matrix,

                  stepPos,

                  pos,

                  themeColor,

                  step

               );

               boolean hasHit = false;



               for (Entity otherEntity : mc.world

                  .getOtherEntities(

                     entity,

                     new Box(

                           stepPos,

                           pos

                        )

                        .expand(0.3),

                     entityCandidate -> entityCandidate

                              != null

                           && entityCandidate.isAlive()

                           && entityCandidate

                              != mc.player

                           && entityCandidate

                              != entity

                  )) {

                  if (otherEntity.getBoundingBox()

                     .expand(0.3)

                     .raycast(

                        stepPos,

                        pos

                     )

                     .isPresent()) {

                     hasHit = true;

                     this.drawEntityBox(

                        matrix,

                        otherEntity,

                        ColorUtil.rgba(

                           255,

                           70,

                           70,

                           210

                        )

                     );

                     this.drawFlyingImpactMarker(

                        matrix,

                        pos,

                        ColorUtil.rgba(

                           255,

                           70,

                           70,

                           230

                        )

                     );

                     this.flyingImpactPoints

                        .add(

                           new StackHelper(

                              this.getEntityItem(

                                 entity

                              ),

                              pos,

                              pointCount

                           )

                        );

                     break;

                  }

               }



               if (hasHit

                  || pos.y

                     < (double)mc.world.getBottomY()) {

                  if (!hasHit

                     )

                   {

                     this.drawFlyingImpactMarker(

                        matrix,

                        pos,

                        ColorUtil.setAlphaColor(

                           themeColor,

                           230

                        )

                     );

                     this.flyingImpactPoints

                        .add(

                           new StackHelper(

                              this.getEntityItem(

                                 entity

                              ),

                              pos,

                              pointCount

                           )

                        );

                  }

                  break;

               }



               pointCount++;

            }

         }

      }



      matrices.pop();

      RenderSystem.depthMask(true);

      RenderSystem.enableCull();

      RenderSystem.disableBlend();

      RenderSystem.defaultBlendFunc();

      this.lastProjectionMatrix

         .set(

            event.getProjectionMatrix()

         );

      this.lastCameraPos = event.getCamera()

         .getPos();

      this.lastCameraRotation

         .set(

            event.getCamera()

               .getRotation()

         );

      this.hasRenderMatrices = true;

   }



   private boolean isStationary(

      Entity entity

   ) {

      return entity.getX()

            == entity.prevX

         && entity.getY()

            == entity.prevY

         && entity.getZ()

            == entity.prevZ;

   }



   private Vec3d calculateEntityMotion(

      Entity entity,

      Vec3d pos,

      Vec3d velocity

   ) {

      boolean inWater = mc.world

         .getBlockState(

            BlockPos.ofFloored(

               pos

            )

         )

         .getFluidState()

         .isIn(FluidTags.WATER);

      double drag;

      if (entity instanceof TridentEntity

         )

       {

         drag = 0.99;

      } else if (entity instanceof PersistentProjectileEntity

         && inWater

         )

       {

         drag = 0.6;

      } else {

         drag = inWater

            ? 0.8

            : 0.99;

      }



      return velocity.multiply(

            drag

         )

         .add(

            0.0,

            -entity.getFinalGravity(),

            0.0

         );

   }



   private ItemStack getEntityItem(

      Entity entity

   ) {

      if (entity instanceof ThrownItemEntity thrownItem

         )

       {

         return thrownItem.getStack();

      } else {

         return entity instanceof PersistentProjectileEntity projectile

            ? projectile.getItemStack()

            : ItemStack.EMPTY;

      }

   }



   private void drawTrajectorySegment(

      Matrix4f matrix,

      Vec3d pos1,

      Vec3d pos2,

      int color,

      int alpha

   ) {

      int fadeAlpha = (int)(

         MathHelper.clamp(

               (float)alpha

                  / 25.0F,

               0.0F,

               1.0F

            )

            * 190.0F

      );

      int finalColor = ColorUtil.setAlphaColor(

         color,

         fadeAlpha

      );

      int red = finalColor

            >> 16

         & 255;

      int green = finalColor

            >> 8

         & 255;

      int blue = finalColor

         & 255;

      int alphaComp = finalColor

            >> 24

         & 255;

      BufferBuilder buffer = Tessellator.getInstance()

         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

      buffer.vertex(

            matrix,

            (float)pos1.x,

            (float)pos1.y,

            (float)pos1.z

         )

         .color(

            red,

            green,

            blue,

            alphaComp

         );

      buffer.vertex(

            matrix,

            (float)pos2.x,

            (float)pos2.y,

            (float)pos2.z

         )

         .color(

            red,

            green,

            blue,

            alphaComp

         );

      BufferRenderer.drawWithGlobalProgram(

         buffer.end()

      );

   }



   private void drawFlyingImpactMarker(

      Matrix4f matrix,

      Vec3d hitPos,

      int color

   ) {

      int red = color

            >> 16

         & 255;

      int green = color

            >> 8

         & 255;

      int blue = color

         & 255;

      int alpha = color

            >> 24

         & 255;

      double radius = 0.35;

      BufferBuilder buffer = Tessellator.getInstance()

         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

      buffer.vertex(

            matrix,

            (float)(

               hitPos.x

                  - radius

            ),

            (float)hitPos.y,

            (float)(

               hitPos.z

                  - radius

            )

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            (float)(

               hitPos.x

                  + radius

            ),

            (float)hitPos.y,

            (float)(

               hitPos.z

                  + radius

            )

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            (float)(

               hitPos.x

                  - radius

            ),

            (float)hitPos.y,

            (float)(

               hitPos.z

                  + radius

            )

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            (float)(

               hitPos.x

                  + radius

            ),

            (float)hitPos.y,

            (float)(

               hitPos.z

                  - radius

            )

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

      BufferRenderer.drawWithGlobalProgram(

         buffer.end()

      );

   }



   @EventLink

   public void onRender2D(

      EventRender.Default event

   ) {

      if (this.hasRenderMatrices && !this.flyingImpactPoints.isEmpty() && mc.player != null) {

         DrawContext context = event.getContext();

         MatrixStack matrices = context.getMatrices();

         Font font = Fonts.getFont(

            "sf_regular",

            14

         );

         if (font

            != null) {

            for (StackHelper point : this.flyingImpactPoints) {

               Vec3d screenPos = this.projectToScreen(

                  point.pos

               );

               if (screenPos

                     != null

                  && !(

                     screenPos.z

                        >= 1.0

                  )) {

                  double seconds = (double)(

                        point.ticks

                           * 50

                     )

                     / 1000.0;

                  String timeStr = String.format(

                           "%.1f",

                           seconds

                        )

                        .replace(

                           (char)44,

                           (char)46

                        )

                     + " сек";

                  float textWidth = font.getWidth(

                     timeStr

                  );

                  float screenX = (float)screenPos.x;

                  float screenY = (float)screenPos.y;

                  float padding = 5.75F;

                  float boxX = screenX

                     - textWidth

                        / 2.0F;

                  float boxY = screenY

                     - padding

                        / 2.0F;

                  float textY = boxY

                     + 0.0F;

                  Render2D.drawRoundedRect(

                     matrices,

                     boxX

                        - 7.0F,

                     textY

                        - 2.0F,

                     textWidth

                        + 14.75F,

                     padding

                        + 5.0F,

                     0.0F,

                     ColorUtil.rgba(0, 0, 0, 120)

                  );

                  matrices.push();

                  matrices.translate(

                     boxX

                        - 5.0F,

                     textY

                        - 0.75F,

                     0.0F

                  );

                  matrices.scale(

                     0.5F, 0.5F, 1.0F

                  );

                  context.drawItem(

                     point.stack,

                     0,

                     0

                  );

                  matrices.scale(

                     1.0F, 1.0F, 1.0F

                  );

                  matrices.translate(

                     -(

                        boxX

                           - 5.0F

                     ),

                     -(

                        textY

                           - 0.75F

                     ),

                     0.0F

                  );

                  matrices.pop();

                  font.drawString(

                     matrices,

                     timeStr,

                     boxX

                        + 4.5F,

                     textY,

                     ColorUtil.rgba(

                        255,

                        255,

                        255,

                        255

                     )

                  );

               }

            }

         }

      }

   }



   private Vec3d projectToScreen(

      Vec3d worldPos

   ) {

      if (mc.player == null) {

         return null;

      } else {

         Vector3f cameraRelPos = new Vector3f(

            (float)(

               worldPos.x

                  - this.lastCameraPos.x

            ),

            (float)(

               worldPos.y

                  - this.lastCameraPos.y

            ),

            (float)(

               worldPos.z

                  - this.lastCameraPos.z

            )

         );

         cameraRelPos.rotate(

            new Quaternionf(this.lastCameraRotation).conjugate()

         );

         Vector4f clipCoords = new Vector4f(

            cameraRelPos.x,

            cameraRelPos.y,

            cameraRelPos.z,

            1.0F

         );

         this.lastProjectionMatrix

            .transform(

               clipCoords

            );

         if (clipCoords.w()

            <= 1.0E-5F) {

            return null;

         } else {

            float invW = 1.0F

               / clipCoords.w();

            float screenX = (

                  clipCoords.x()

                        * invW

                     + 1.0F

               )

               * (float)mc.getWindow().getScaledWidth()

               / 2.0F;

            float screenY = (

                  1.0F

                     - clipCoords.y()

                        * invW

               )

               * (float)mc.getWindow().getScaledHeight()

               / 2.0F;

            float clipZ = clipCoords.z()

               * invW;

            return !Float.isNaN(

                     screenX

                  )

                  && !Float.isNaN(

                     screenY

                  )

                  && !Float.isInfinite(

                     screenX

                  )

                  && !Float.isInfinite(

                     screenY

                  )

               ? new Vec3d(

                  (double)screenX,

                  (double)screenY,

                  (double)clipZ

               )

               : null;

         }

      }

   }



   private ItemStack getHeldProjectileStack() {

      ItemStack mainHandStack = mc.player

         .getMainHandStack();

      if (!mainHandStack.isEmpty()

         && this.getParams(

               mainHandStack

            )

            != null) {

         return mainHandStack;

      } else {

         ItemStack offHandStack = mc.player

            .getOffHandStack();

         return !offHandStack.isEmpty()

               && this.getParams(

                     offHandStack

                  )

                  != null

            ? offHandStack

            : ItemStack.EMPTY;

      }

   }



   private VelocityHelper getParams(

      ItemStack stack

   ) {

      Item item = stack.getItem();

      if (item

            == Items.ENDER_PEARL

         || item

            == Items.SNOWBALL

         || item

            == Items.EGG) {

         return new VelocityHelper(1.5, 0.03, 0.99);

      } else if (item

            == Items.SPLASH_POTION

         || item

            == Items.LINGERING_POTION) {

         return new VelocityHelper(0.5, 0.05, 0.99);

      } else if (item instanceof BowItem

         )

       {

         float power = 1.0F;

         if (mc.player.isUsingItem()

            && mc.player.getActiveItem()

               == stack

            )

          {

            float useTime = (float)mc.player

               .getItemUseTime();

            float charge = useTime

               / 20.0F;

            charge = (

                  charge

                        * charge

                     + charge

                        * 2.0F

               )

               / 3.0F;

            power = Math.min(

               charge,

               1.0F

            );

         }



         double velocity = 3.0

            * (double)power;

         return velocity

               <= 0.01

            ? null

            : new VelocityHelper(

               velocity,

               0.05,

               0.99

            );

      } else if (item instanceof CrossbowItem

         )

       {

         return !CrossbowItem.isCharged(

               stack

            )

            ? null

            : new VelocityHelper(3.15, 0.05, 0.99);

      } else {

         return item instanceof TridentItem

            ? new VelocityHelper(2.5, 0.05, 0.99)

            : null;

      }

   }



   private Vec3d[] getShotDirections(

      ItemStack stack,

      float tickDelta

   ) {

      Vec3d eyePos = mc.player

         .getRotationVec(

            tickDelta

         )

         .normalize();

      if (stack.getItem() instanceof CrossbowItem

         && SearchitemHelper.getEnchantmentLevel(

               stack,

               Enchantments.MULTISHOT

            )

            > 0) {

         float pitch = (float)(

               MathHelper.atan2(

                     eyePos.z,

                     eyePos.x

                  )

                  * (180.0 / Math.PI)

            )

            - 90.0F;

         float yaw = (float)(

            -(

               MathHelper.atan2(

                     eyePos.y,

                     (double)MathHelper.sqrt(

                        (float)(

                           eyePos.x

                                 * eyePos.x

                              + eyePos.z

                                 * eyePos.z

                        )

                     )

                  )

                  * (180.0 / Math.PI)

            )

         );

         Vec3d[] var10000 = new Vec3d[3];

         var10000[0] = this.getDirectionFromYawPitch(

            pitch

               - 10.0F,

            yaw

         );

         var10000[1] = eyePos;

         var10000[2] = this.getDirectionFromYawPitch(

            pitch

               + 10.0F,

            yaw

         );

         return var10000;

      } else {

         return new Vec3d[]{

            eyePos

         };

      }

   }



   private Vec3d getDirectionFromYawPitch(

      float pitch,

      float yaw

   ) {

      float pitchRad = pitch

         * (float) (Math.PI / 180.0);

      float yawRad = yaw

         * (float) (Math.PI / 180.0);

      float sinPitch = MathHelper.sin(

            -pitchRad

               - (float) Math.PI

         )

         * -MathHelper.cos(

            -yawRad

         );

      float sinYaw = MathHelper.sin(

         -yawRad

      );

      float cosPitch = MathHelper.cos(

            -pitchRad

               - (float) Math.PI

         )

         * -MathHelper.cos(

            -yawRad

         );

      return new Vec3d(

            (double)sinPitch,

            (double)sinYaw,

            (double)cosPitch

         )

         .normalize();

   }



   private PointsHelper predict(

      PlayerEntity eyePos,

      VelocityHelper velocityHelper,

      Vec3d startPos,

      Vec3d direction

   ) {

      Vec3d currentPos = startPos;

      Vec3d velocity = direction.normalize()

         .multiply(

            velocityHelper.velocity

         );

      Vec3d[] points = new Vec3d[441];

      int pointCount = 0;

      points[pointCount++] = startPos;

      Entity hitEntity = null;

      Vec3d hitPos = null;



      for (int step = 0;

         step

            < 440;

         step++

      ) {

         Vec3d nextPos = currentPos.add(

            velocity.multiply(

               0.5

            )

         );

         if (hitEntity

            == null) {

            EntityHelper entityHit = this.rayTraceEntities(

               currentPos,

               nextPos,

               eyePos

            );

            if (entityHit

               != null) {

               hitEntity = entityHit.entity;

               hitPos = entityHit.hitPos;

            }

         }



         BlockHitResult blockHit = mc.world

            .raycast(

               new RaycastContext(

                  currentPos,

                  nextPos,

                  ShapeType.COLLIDER,

                  FluidHandling.NONE,

                  eyePos

               )

            );

         if (blockHit.getType()

            == Type.BLOCK) {

            points[pointCount++] = blockHit.getPos();

            return new PointsHelper(

               this.copyPoints(

                  points,

                  pointCount

               ),

               blockHit,

               blockHit.getPos(),

               hitEntity,

               hitPos

            );

         }



         points[pointCount++] = nextPos;

         currentPos = nextPos;

         boolean inWater = mc.world

            .getBlockState(

               BlockPos.ofFloored(

                  nextPos

               )

            )

            .isOf(Blocks.WATER);

         double drag = Math.pow(

            inWater

               ? 0.8

               : velocityHelper.drag,

            0.5

         );

         velocity = velocity.multiply(

               drag

            )

            .subtract(

               0.0,

               velocityHelper.gravity

                  * 0.5,

               0.0

            );

         if (nextPos.y

            <= (double)mc.world.getBottomY()) {

            break;

         }

      }



      Vec3d finalHitPos = hitPos

            != null

         ? hitPos

         : points[pointCount

            - 1];

      return new PointsHelper(

         this.copyPoints(

            points,

            pointCount

         ),

         null,

         finalHitPos,

         hitEntity,

         hitPos

      );

   }



   private Vec3d[] copyPoints(

      Vec3d[] points,

      int length

   ) {

      Vec3d[] trimmed = new Vec3d[length];

      System.arraycopy(

         points,

         0,

         trimmed,

         0,

         length

      );

      return trimmed;

   }



   private EntityHelper rayTraceEntities(

      Vec3d startPos,

      Vec3d endPos,

      Entity filterEntity

   ) {

      Box boundingBox = new Box(

            startPos,

            endPos

         )

         .expand(1.0);

      Entity closestEntity = null;

      Vec3d closestHitPos = null;

      double minDistSq = Double.MAX_VALUE;



      for (Entity candidate : mc.world

         .getOtherEntities(

            filterEntity,

            boundingBox,

            entityCandidate -> entityCandidate

                     != null

                  && entityCandidate.isAlive()

                  && entityCandidate.canHit()

         )) {

         Optional<Vec3d> hitResult = candidate.getBoundingBox().expand(0.3).raycast(

               startPos,

               endPos

            );

         if (!hitResult.isEmpty()

            )

          {

            double distSq = startPos.squaredDistanceTo(

               hitResult.get()

            );

            if (distSq

               < minDistSq

               )

             {

               minDistSq = distSq;

               closestEntity = candidate;

               closestHitPos = hitResult.get();

            }

         }

      }



      return closestEntity

            == null

         ? null

         : new EntityHelper(

            closestEntity,

            closestHitPos

         );

   }



   private void drawTrajectoryLine(

      Matrix4f matrix,

      Vec3d[] points,

      int color

   ) {

      int red = color

            >> 16

         & 255;

      int green = color

            >> 8

         & 255;

      int blue = color

         & 255;

      int alpha = color

            >> 24

         & 255;

      BufferBuilder buffer = Tessellator.getInstance()

         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);



      for (int i = 0;

         i

            < points.length

               - 1;

         i++

      ) {

         Vec3d p1 = points[i];

         Vec3d p2 = points[i

            + 1];

         buffer.vertex(

               matrix,

               (float)p1.x,

               (float)p1.y,

               (float)p1.z

            )

            .color(

               red,

               green,

               blue,

               alpha

            );

         buffer.vertex(

               matrix,

               (float)p2.x,

               (float)p2.y,

               (float)p2.z

            )

            .color(

               red,

               green,

               blue,

               alpha

            );

      }



      BufferRenderer.drawWithGlobalProgram(

         buffer.end()

      );

   }



   private void drawImpactMarker(

      Matrix4f matrix,

      Vec3d hitPos,

      Direction side,

      int color

   ) {

      Vec3d normal = Vec3d.of(

            side.getVector()

         )

         .normalize();

      Vec3d tangent1 = side

               != Direction.UP

            && side

               != Direction.DOWN

         ? normal.crossProduct(

               new Vec3d(0.0, 1.0, 0.0)

            )

            .normalize()

         : new Vec3d(1.0, 0.0, 0.0);

      Vec3d tangent2 = normal.crossProduct(

            tangent1

         )

         .normalize();

      Vec3d center = hitPos.add(

         normal.multiply(

            0.004

         )

      );

      double radius = 0.35;

      int red = color

            >> 16

         & 255;

      int green = color

            >> 8

         & 255;

      int blue = color

         & 255;

      int alpha = color

            >> 24

         & 255;

      BufferBuilder buffer = Tessellator.getInstance()

         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

      int segments = 48;

      Vec3d prevPoint = null;



      for (int i = 0;

         i

            <= segments;

         i++

      ) {

         double angle = (

               Math.PI * 2

            )

            * (double)i

            / (double)segments;

         Vec3d currPoint = center.add(

               tangent1.multiply(

                  Math.cos(

                        angle

                     )

                     * radius

               )

            )

            .add(

               tangent2.multiply(

                  Math.sin(

                        angle

                     )

                     * radius

               )

            );

         if (prevPoint

            != null) {

            buffer.vertex(

                  matrix,

                  (float)prevPoint.x,

                  (float)prevPoint.y,

                  (float)prevPoint.z

               )

               .color(

                  red,

                  green,

                  blue,

                  alpha

               );

            buffer.vertex(

                  matrix,

                  (float)currPoint.x,

                  (float)currPoint.y,

                  (float)currPoint.z

               )

               .color(

                  red,

                  green,

                  blue,

                  alpha

               );

         }



         prevPoint = currPoint;

      }



      Vec3d pTop = center.add(

         tangent1.multiply(

            -radius

         )

      );

      Vec3d pBottom = center.add(

         tangent1.multiply(

            radius

         )

      );

      Vec3d pLeft = center.add(

         tangent2.multiply(

            -radius

         )

      );

      Vec3d pRight = center.add(

         tangent2.multiply(

            radius

         )

      );

      buffer.vertex(

            matrix,

            (float)pTop.x,

            (float)pTop.y,

            (float)pTop.z

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            (float)pBottom.x,

            (float)pBottom.y,

            (float)pBottom.z

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            (float)pLeft.x,

            (float)pLeft.y,

            (float)pLeft.z

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            (float)pRight.x,

            (float)pRight.y,

            (float)pRight.z

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

      BufferRenderer.drawWithGlobalProgram(

         buffer.end()

      );

   }



   private void drawEntityBox(

      Matrix4f matrix,

      Entity entity,

      int color

   ) {

      Box box = entity.getBoundingBox();

      int red = color

            >> 16

         & 255;

      int green = color

            >> 8

         & 255;

      int blue = color

         & 255;

      int alpha = color

            >> 24

         & 255;

      BufferBuilder buffer = Tessellator.getInstance()

         .begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

      this.vertexBox(

         buffer,

         matrix,

         box,

         red,

         green,

         blue,

         alpha

      );

      BufferRenderer.drawWithGlobalProgram(

         buffer.end()

      );

   }



   private void vertexBox(

      BufferBuilder buffer,

      Matrix4f matrix,

      Box box,

      int red,

      int green,

      int blue,

      int alpha

   ) {

      float minX = (float)box.minX;

      float minY = (float)box.minY;

      float minZ = (float)box.minZ;

      float maxX = (float)box.maxX;

      float maxY = (float)box.maxY;

      float maxZ = (float)box.maxZ;

      this.line(

         buffer,

         matrix,

         minX,

         minY,

         minZ,

         maxX,

         minY,

         minZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         maxX,

         minY,

         minZ,

         maxX,

         minY,

         maxZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         maxX,

         minY,

         maxZ,

         minX,

         minY,

         maxZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         minX,

         minY,

         maxZ,

         minX,

         minY,

         minZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         minX,

         maxY,

         minZ,

         maxX,

         maxY,

         minZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         maxX,

         maxY,

         minZ,

         maxX,

         maxY,

         maxZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         maxX,

         maxY,

         maxZ,

         minX,

         maxY,

         maxZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         minX,

         maxY,

         maxZ,

         minX,

         maxY,

         minZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         minX,

         minY,

         minZ,

         minX,

         maxY,

         minZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         maxX,

         minY,

         minZ,

         maxX,

         maxY,

         minZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         maxX,

         minY,

         maxZ,

         maxX,

         maxY,

         maxZ,

         red,

         green,

         blue,

         alpha

      );

      this.line(

         buffer,

         matrix,

         minX,

         minY,

         maxZ,

         minX,

         maxY,

         maxZ,

         red,

         green,

         blue,

         alpha

      );

   }



   private void line(

      BufferBuilder buffer,

      Matrix4f matrix,

      float x1,

      float y1,

      float z1,

      float x2,

      float y2,

      float z2,

      int red,

      int green,

      int blue,

      int alpha

   ) {

      buffer.vertex(

            matrix,

            x1,

            y1,

            z1

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            x2,

            y2,

            z2

         )

         .color(

            red,

            green,

            blue,

            alpha

         );

   }



   private void drawPotionRadiusGlow(

      MatrixStack matrices,

      Vec3d hitPos,

      int color

   ) {

      int alphaColor = ColorUtil.setAlphaColor(

         color,

         82

      );

      int red = alphaColor

            >> 16

         & 255;

      int green = alphaColor

            >> 8

         & 255;

      int blue = alphaColor

         & 255;

      int alpha = alphaColor

            >> 24

         & 255;

      float radius = 4.0F;

      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

      RenderSystem.setShaderTexture(0, GLOW_TEXTURE);

      RenderSystem.enableBlend();

      RenderSystem.defaultBlendFunc();

      matrices.push();

      matrices.translate(

         hitPos.x,

         hitPos.y

            + 0.012,

         hitPos.z

      );

      Matrix4f matrix = matrices.peek()

         .getPositionMatrix();

      BufferBuilder buffer = Tessellator.getInstance()

         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      buffer.vertex(

            matrix,

            -radius,

            0.0F,

            -radius

         )

         .texture(0.0F, 0.0F)

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            -radius,

            0.0F,

            radius

         )

         .texture(0.0F, 1.0F)

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            radius,

            0.0F,

            radius

         )

         .texture(1.0F, 1.0F)

         .color(

            red,

            green,

            blue,

            alpha

         );

      buffer.vertex(

            matrix,

            radius,

            0.0F,

            -radius

         )

         .texture(1.0F, 0.0F)

         .color(

            red,

            green,

            blue,

            alpha

         );

      BufferRenderer.drawWithGlobalProgram(

         buffer.end()

      );

      matrices.pop();

      RenderSystem.setShaderTexture(0, 0);

      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

   }

}

