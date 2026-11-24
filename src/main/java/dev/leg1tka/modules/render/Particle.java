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
import dev.leg1tka.api.events.implement.EventAttackEntity;
import dev.leg1tka.api.events.implement.EventPacket;
import dev.leg1tka.api.events.implement.EventUpdate;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;

public class Particle extends Module {
   public static Particle INSTANCE = new Particle();
   private static final Identifier STAR_TEXTURE = ClientIdentifier.of("textures/particle/star.png");
   private static final Identifier HEART_TEXTURE = ClientIdentifier.of("textures/particle/heart.png");
   private static final Identifier DOLLAR_TEXTURE = ClientIdentifier.of("textures/particle/dollar.png");
   private static final Identifier BLOOM_TEXTURE = ClientIdentifier.of("textures/particle/bloom.png");
   private static final Identifier SPARKLE_TEXTURE = ClientIdentifier.of("textures/particle/sparkle.png");
   private static final Identifier ASPECTDLC_TEXTURE = ClientIdentifier.of("textures/particle/aspectdlc.png");
   private final ModeSetting type;
   private final MultiBooleanSetting reason;
   private final NumberSetting count;
   private final BooleanSetting glow;
   private final ArrayList<IsdeadHelper_1> particles;
   private final Random rnd;

   public Particle() {
      super(
         "Particles",
         "Красивые партиклы при разных действиях",
         Category.RENDER
      );
      String var10003 = "Тип частиц";
      this.type = new ModeSetting(var10003, "Звездочки", new String[]{"Звездочки", "Члены", "Доллары", "Блум", "Сияние"});
      var10003 = "Добавлять при";
      this.reason = new MultiBooleanSetting(var10003, new BooleanSetting[]{
         new BooleanSetting(
         "Бездействии",
         false
      ),
         new BooleanSetting(
         "Беге",
         false
      ),
         new BooleanSetting(
         "Ударе",
         true
      ),
         new BooleanSetting(
         "Падении перла",
         false
      ),
         new BooleanSetting(
         "Падении трезубца",
         false
      ),
         new BooleanSetting(
         "Сносе тотема",
         true
      )
      });
      this.count = new NumberSetting(
         "Количество",
         10.0F,
         2.0F,
         40.0F,
         1.0F
      );
      this.glow = new BooleanSetting(
         "Свечение",
         true
      );
      this.particles = new ArrayList<>();
      this.rnd = new Random();
      this.addSettings(new Setting[]{
         this.type,
         this.reason,
         this.count,
         this.glow
      });
   }

   @Override
   public void onDisable() {
      this.particles.clear();
      super.onDisable();
   }

   private Identifier getTexture() {
      return switch (this.type.getIndex()) {
         case 1 -> HEART_TEXTURE;
         case 2 -> DOLLAR_TEXTURE;
         case 3 -> BLOOM_TEXTURE;
         case 4 -> SPARKLE_TEXTURE;
         default -> STAR_TEXTURE;
      };
   }

   private boolean isPositionInBlock(
      Vec3d val
   ) {
      if (mc.world != null && mc.player != null) {
         BlockPos pos = BlockPos.ofFloored(
            val
         );
         if (mc.world
            .getBlockState(
               pos
            )
            .isSolidBlock(
               mc.world,
               pos
            )) {
            return true;
         } else {
            RaycastContext val2 = new RaycastContext(
               new Vec3d(mc.player.getX(), mc.player.getY() + (double)mc.player.getStandingEyeHeight(), mc.player.getZ()),
               val,
               ShapeType.COLLIDER,
               FluidHandling.NONE,
               mc.player
            );
            BlockHitResult blockHit = mc.world
               .raycast(
                  val2
               );
            return blockHit.getType()
               == Type.BLOCK;
         }
      } else {
         return true;
      }
   }

   private float random(
      float val,
      float val2
   ) {
      return val
         + this.rnd.nextFloat()
            * (
               val2
                  - val
            );
   }

   private boolean isMoving() {
      return mc.player != null && (mc.player.input.movementForward != 0.0F || mc.player.input.movementSideways != 0.0F);
   }

   @EventLink
   public void onAttack(
      EventAttackEntity val
   ) {
      if (mc.player != null && mc.world != null) {
         if (this.reason
            .is(
               "Ударе"
            )) {
            Entity entity2 = val.getTarget();
            if (entity2
               != null) {
               for (int i = 0;
                  i
                     < 35;
                  i++
               ) {
                  double d = entity2.getX()
                     + (double)this.random(-0.4F, 0.4F);
                  double d2 = entity2.getY()
                     + (double)this.random(
                        -0.4F,
                        entity2.getHeight()
                           + 0.4F
                     );
                  double d3 = entity2.getZ()
                     + (double)this.random(-0.4F, 0.4F);
                  if (!this.isPositionInBlock(
                     new Vec3d(
                        d,
                        d2,
                        d3
                     )
                  )) {
                     float f = this.random(
                           -0.8F, 0.8F
                        )
                        * 2.0F;
                     float f2 = this.random(
                        -0.25F, 1.4F
                     );
                     float f3 = this.random(
                           -0.8F, 0.8F
                        )
                        * 2.0F;
                     Vec3d vec = new Vec3d(
                        (double)(
                           f
                              * 0.075F
                        ),
                        (double)(
                           f2
                              * 0.075F
                        ),
                        (double)(
                           f3
                              * 0.075F
                        )
                     );
                     long val2 = (long)this.random(
                        1000.0F, 1200.0F
                     );
                     this.addParticle(
                        d,
                        d2,
                        d3,
                        vec,
                        ColorUtil.getThemeColor(),
                        0.3F,
                        val2,
                        0.5F,
                        7.0E-4F
                     );
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onPacket(
      EventPacket val
   ) {
      if (mc.world != null && mc.player != null) {
         if (this.reason
            .is(
               "Сносе тотема"
            )) {
            if (val.getPacket() instanceof EntityStatusS2CPacket val2
               && val2.getStatus()
                  == 35) {
               Entity entity2 = val2.getEntity(
                  mc.world
               );
               if (entity2
                  != null) {
                  double d = entity2.getX();
                  double d2 = entity2.getY()
                     + (double)entity2.getHeight()
                        / 2.0;
                  double d3 = entity2.getZ();

                  for (int i = 0;
                     i
                        < 50;
                     i++
                  ) {
                     double d4 = this.rnd
                           .nextDouble()
                        * 2.0
                        * Math.PI;
                     double d5 = this.rnd
                           .nextDouble()
                        * Math.PI;
                     double d6 = (
                           this.rnd.nextDouble() * 0.5 + 0.5
                        )
                        * 0.1;
                     double d7 = Math.sin(
                           d5
                        )
                        * Math.cos(
                           d4
                        )
                        * d6;
                     double d8 = Math.sin(
                           d5
                        )
                        * Math.sin(
                           d4
                        )
                        * d6;
                     double d9 = Math.cos(
                           d5
                        )
                        * d6;
                     double d10 = d
                        + (double)this.random(-0.3F, 0.3F);
                     double d11 = d2
                        + (double)this.random(-0.3F, 0.3F);
                     double d12 = d3
                        + (double)this.random(-0.3F, 0.3F);
                     if (!this.isPositionInBlock(
                        new Vec3d(
                           d10,
                           d11,
                           d12
                        )
                     )) {
                        int n = this.rnd
                                 .nextDouble()
                              < 0.7
                           ? -16711936
                           : -256;
                        long val3 = (long)this.random(
                           1500.0F, 2000.0F
                        );
                        this.addParticle(
                           d10,
                           d11,
                           d12,
                           new Vec3d(
                              d7,
                              d8,
                              d9
                           ),
                           n,
                           0.3F,
                           val3,
                           2.0F,
                           5.0E-5F
                        );
                     }
                  }
               }
            }
         }
      }
   }

   @EventLink
   public void onUpdate(
      EventUpdate val10
   ) {
      if (mc.player != null && mc.world != null) {
         int count2 = (int)this.count
            .get();
         if (this.reason
            .is(
               "Бездействии"
            )) {
            Vec3d vec = new Vec3d(
               mc.player.getX(), mc.player.getY() + (double)mc.player.getHeight() / 2.0, mc.player.getZ()
            );

            for (int i = 0;
               i
                  < count2;
               i++
            ) {
               double d = (double)this.random(
                  7.0F, 35.0F
               );
               double d2 = Math.toRadians(
                  (double)this.random(0.0F, 360.0F)
               );
               double d3 = (double)this.random(
                  -7.0F, 25.0F
               );
               double d4 = vec.x
                  + Math.cos(
                        d2
                     )
                     * d;
               double d5 = vec.y
                  + d3;
               double d6 = vec.z
                  + Math.sin(
                        d2
                     )
                     * d;
               Vec3d vec2 = new Vec3d(
                  d4,
                  d5,
                  d6
               );
               if (!this.isPositionInBlock(
                  vec2
               )) {
                  long val = (long)this.random(
                     1500.0F, 2000.0F
                  );
                  double d7 = this.rnd
                           .nextDouble()
                        < 0.8
                     ? (double)this.random(0.015F, 0.03F)
                     : 0.125;
                  double d8 = Math.toRadians(
                     (double)this.random(0.0F, 360.0F)
                  );
                  Vec3d vec3 = new Vec3d(
                     Math.cos(
                           d8
                        )
                        * d7,
                     (double)this.random(
                        (float)(
                           -d7
                              * 0.1F
                        ),
                        (float)(
                           d7
                              * 0.1F
                        )
                     ),
                     Math.sin(
                           d8
                        )
                        * d7
                  );
                  this.addParticle(
                     d4,
                     d5,
                     d6,
                     vec3,
                     ColorUtil.getThemeColor(),
                     0.3F,
                     val,
                     3.0F,
                     5.0E-5F
                  );
               }
            }
         }

         if (this.reason
               .is(
                  "Беге"
               )
            && this.isMoving()) {
            Vec3d vec4 = mc.player
               .getVelocity();
            double d9 = Math.sqrt(
               vec4.x
                     * vec4.x
                  + vec4.z
                     * vec4.z
            );
            Vec3d vec5;
            if (d9
               < 0.01) {
               vec5 = mc.player
                  .getRotationVector()
                  .multiply(-1.0);
            } else if (mc.player.isGliding()) {
               vec5 = vec4.normalize()
                  .multiply(-1.0);
            } else {
               vec5 = new Vec3d(
                  -vec4.x
                     / d9,
                  0.0,
                  -vec4.z
                     / d9
               );
            }

            double d10 = (
                  mc.player.isGliding() ? 1.2 : 0.5
               )
               + (
                  d9
                        > 0.1
                     ? d9
                        * 1.5
                     : 0.0
               );
            double d11 = (double)this.random(
               -0.35F, 0.35F
            );
            double d12 = (double)this.random(
               -0.35F, 0.35F
            );
            double d13 = mc.player
                  .getX()
               + vec5.x
                  * d10
               + d11;
            double d14 = mc.player
                  .isGliding()
               ? mc.player.getY()
                  + (double)mc.player.getHeight() / 2.0
                  + vec5.y
                     * d10
                  + (double)this.random(-0.35F, 0.35F)
               : mc.player.getY() + (double)this.random(0.2F, mc.player.getHeight() + 0.1F);
            double d15 = mc.player
                  .getZ()
               + vec5.z
                  * d10
               + d12;
            if (!this.isPositionInBlock(
               new Vec3d(
                  d13,
                  d14,
                  d15
               )
            )) {
               double d16 = 0.075;
               Vec3d vec6 = vec5.multiply(
                     d16
                  )
                  .add((double)this.random(-0.01F, 0.01F), (double)this.random(-0.05F, 0.01F), (double)this.random(-0.01F, 0.01F))
                  .multiply(0.1);
               long val2 = (long)this.random(
                  1500.0F, 2000.0F
               );
               this.addParticle(
                  d13,
                  d14,
                  d15,
                  vec6,
                  ColorUtil.getThemeColor(),
                  0.3F,
                  val2,
                  3.0F,
                  5.0E-5F
               );
            }
         }

         boolean flag = this.reason
            .is(
               "Падении перла"
            );
         boolean flag2 = this.reason
            .is(
               "Падении трезубца"
            );
         if (flag
            || flag2
            )
          {
            Box box = mc.player
               .getBoundingBox()
               .expand(100.0);

            for (Entity entity2 : mc.world
               .getOtherEntities(
                  null,
                  box,
                  val3 -> true
               )) {
               if (flag
                  && entity2 instanceof EnderPearlEntity val4
                  && !val4.isOnGround()
                  )
                {
                  this.createProjectileParticles(
                     val4.getPos(),
                     1
                  );
               }

               if (flag2
                  && entity2 instanceof TridentEntity val5
                  && val5.getVelocity()
                        .lengthSquared()
                     > 0.01) {
                  this.createProjectileParticles(
                     val5.getPos(),
                     1
                  );
               }
            }
         }
      }
   }

   private void createProjectileParticles(
      Vec3d val,
      int val2
   ) {
      int color2 = ColorUtil.getThemeColor();

      for (int i = 0;
         (double)i
            < (double)val2
               * 2.5;
         i++
      ) {
         double d = (double)this.random(
            0.1F, 0.35F
         );
         Vec3d vec = new Vec3d(
            val.x,
            val.y
               + d,
            val.z
         );
         if (!this.isPositionInBlock(
            vec
         )) {
            float f = this.random(
               0.015F, 0.0375F
            );
            float f2 = this.random(
               0.05F, 0.075F
            );
            double d2 = (double)this.random(
               f,
               f2
            );
            double d3 = d2
               * 0.4;
            double d4 = Math.toRadians(
               (double)this.random(0.0F, 360.0F)
            );
            Vec3d vec2 = new Vec3d(
               Math.cos(
                     d4
                  )
                  * d2,
               (double)this.random(
                  (float)(
                     -d3
                  ),
                  (float)d3
               ),
               Math.sin(
                     d4
                  )
                  * d2
            );
            long val3 = (long)this.random(
               2400.0F, 2800.0F
            );
            this.addParticle(
               vec.x,
               vec.y,
               vec.z,
               vec2,
               color2,
               0.25F,
               val3,
               2.0F,
               5.0E-5F
            );
         }
      }
   }

   private void addParticle(
      double val,
      double val2,
      double val3,
      Vec3d val4,
      int val5,
      float val6,
      long val7,
      float val8,
      double val9
   ) {
      if (IsdeadHelper_1.checkCollision(
         val,
         val2,
         val3,
         val6,
         mc
      )) {
         synchronized (this.particles) {
            this.particles
               .add(
                  new IsdeadHelper_1(
                     new Vec3d(
                        val,
                        val2,
                        val3
                     ),
                     val4,
                     val5,
                     val6,
                     val7,
                     val8,
                     val9
                  )
               );
         }
      }
   }

   @EventLink
   public void onRender3D(
      Event3DRender val
   ) {
      if (mc.player != null && mc.world != null) {
         synchronized (this.particles) {
            this.particles.removeIf(p -> p.isDead());
         }

         if (!this.particles.isEmpty()) {
            MatrixStack matrices = val.getMatrices();
            Vec3d vec = mc.gameRenderer
               .getCamera()
               .getPos();
            Identifier id = this.getTexture();
            RenderSystem.enableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            if (this.glow.isState()) {
               RenderSystem.blendFunc(770, 1);
            } else {
               RenderSystem.defaultBlendFunc();
            }

            RenderSystem.setShaderTexture(
               0,
               id
            );
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            ArrayList<IsdeadHelper_1> list;
            synchronized (this.particles) {
               list = new ArrayList<>(
                  this.particles
               );
            }

            for (IsdeadHelper_1 val2 : list) {
               val2.update(
                  mc
               );
               double d = val2.position
                     .x
                  - vec.x;
               double d2 = val2.position
                     .y
                  - vec.y;
               double d3 = val2.position
                     .z
                  - vec.z;
               matrices.push();
               matrices.translate(
                  (float)d,
                  (float)d2,
                  (float)d3
               );
               matrices.multiply(
                  RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw())
               );
               matrices.multiply(
                  RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch())
               );
               Matrix4f matrix = matrices.peek()
                  .getPositionMatrix();
               float f = val2.size
                  / 2.0F;
               int n = (int)(
                  val2.alpha
                     * 255.0F
               );
               int color2 = val2.color
                     >> 16
                  & 255;
               int color3 = val2.color
                     >> 8
                  & 255;
               int color4 = val2.color
                  & 255;
               BufferBuilder buffer = Tessellator.getInstance()
                  .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               buffer.vertex(
                     matrix,
                     -f,
                     -f,
                     0.0F
                  )
                  .texture(0.0F, 1.0F)
                  .color(
                     color2,
                     color3,
                     color4,
                     n
                  );
               buffer.vertex(
                     matrix,
                     -f,
                     f,
                     0.0F
                  )
                  .texture(0.0F, 0.0F)
                  .color(
                     color2,
                     color3,
                     color4,
                     n
                  );
               buffer.vertex(
                     matrix,
                     f,
                     f,
                     0.0F
                  )
                  .texture(1.0F, 0.0F)
                  .color(
                     color2,
                     color3,
                     color4,
                     n
                  );
               buffer.vertex(
                     matrix,
                     f,
                     -f,
                     0.0F
                  )
                  .texture(1.0F, 1.0F)
                  .color(
                     color2,
                     color3,
                     color4,
                     n
                  );
               BufferRenderer.drawWithGlobalProgram(
                  buffer.end()
               );
               matrices.pop();
            }

            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
         }
      }
   }
}
