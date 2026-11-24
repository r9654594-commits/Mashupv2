package dev.leg1tka.modules.render;

import dev.leg1tka.modules.misc.ScoreboardHP;
import dev.leg1tka.modules.misc.NameProtect;


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

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.leg1tka.AspectDLC;
import dev.leg1tka.api.events.EventLink;
import dev.leg1tka.api.events.implement.Event3DRender;
import dev.leg1tka.api.events.implement.EventRender;
import dev.leg1tka.api.utils.render.fonts.msdf.Font;
import dev.leg1tka.api.utils.render.fonts.msdf.Fonts;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterAnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class EntityESP extends Module {
   public static EntityESP INSTANCE = new EntityESP();
   private static final float TAG_FROM_ENTITY_GAP = 0.0F;
   private static final int TAG_FONT_SIZE = 13;
   private static final int TAG_TEXT_COLOR = -1;
   private static final int TAG_HEALTH_COLOR = -43691;
   private static final int TAG_FRIEND_COLOR = -11141291;
   private static final float TAG_HUD_RADIUS = 1.1F;
   private static final int TAG_HUD_ALPHA = 204;
   private static final int TAG_BG_COLOR = 1140850688;
   private static final float ARMOR_CELL_SIZE = 10.0F;
   private static final float ARMOR_ITEM_SCALE = 0.55F;
   private static final float ARMOR_CELL_GAP = 1.5F;
   private static final float DURABILITY_BAR_HEIGHT = 2.5F;
   private static final float DURABILITY_BAR_GAP = 1.0F;
   private static final float HP_BAR_HEIGHT = 4.2F;
   private static final float HP_BAR_GAP = 1.15F;
   private static final float HP_BAR_RADIUS = 1.05F;
   private static final float BOX_LINE_WIDTH = 1.5F;
   private static final float FILL_ALPHA = 0.23F;
   private static final float EPSILON = 0.001F;
   private static final long DONATE_CACHE_TTL_MS = 1000L;
   private static final long DONATE_CACHE_CLEANUP_MS = 2000L;
   private static final int MAX_ITEM_TAGS_PER_FRAME = 48;
   private final MultiBooleanSetting elements = new MultiBooleanSetting(
      "Элементы",
      new BooleanSetting("Теги", true),
      new BooleanSetting("Броня", true),
      new BooleanSetting("Зачары", false),
      new BooleanSetting("Полоска HP", false),
      new BooleanSetting(
         "Вторая рука",
         false
      ),
      new BooleanSetting(
         "Показ бафов",
         false
      )
   );
   private final BooleanSetting show3DBox = new BooleanSetting("Боксы", true);
   private final BooleanSetting boxFilled = new BooleanSetting(
      "Заполнить бокс",
      true
   );
   private final BooleanSetting itemStackList = new BooleanSetting(
      "В список", true
   );
   private final ModeSetting boxFillMode = new ModeSetting(
      "Мод заливки",
      "Обычный",
      "Обычный",
      "Волны",
      "Нитки"
   );
   private final NumberSetting waveSpeed = new NumberSetting(
         "Скорость волн",
         1.2F,
         0.1F,
         5.0F,
         0.1F
      )
      .visible(
         () -> this.boxFillMode.is("Волны")
      );
   private final NumberSetting waveScale = new NumberSetting(
         "Размер волн",
         1.0F,
         1.0F,
         3.0F,
         0.1F
      )
      .visible(
         () -> this.boxFillMode.is("Волны")
      );
   private final NumberSetting lineSpeed = new NumberSetting(
         "Скорость линий",
         1.4F,
         0.1F,
         5.0F,
         0.1F
      )
      .visible(() -> this.boxFillMode.getIndex() == 2);
   private final NumberSetting lineJitter = new NumberSetting(
         "Прыжки линий",
         0.55F,
         0.0F,
         1.5F,
         0.01F
      )
      .visible(() -> this.boxFillMode.getIndex() == 2);
   private final NumberSetting outline = new NumberSetting(
         "Обводка", 1.1F, 0.1F, 5.0F, 0.1F
      )
      .visible(this::isPostBoxMode);
   private final NumberSetting glow = new NumberSetting(
         "Свечение", 1.0F, 0.0F, 5.0F, 0.1F
      )
      .visible(this::isPostBoxMode);
   private final NumberSetting fill = new NumberSetting(
         "Сила заливки",
         0.6F,
         0.0F,
         1.0F,
         0.01F
      )
      .visible(this::isPostBoxMode);
   private final NumberSetting alpha = new NumberSetting(
         "Прозрачность",
         1.0F,
         0.0F,
         4.0F,
         0.01F
      )
      .visible(this::isPostBoxMode);
   private final BooleanSetting hurtTint = new BooleanSetting(
      "Краснеть при ударе",
      true
   );
   private final Matrix4f lastProjectionMatrix = new Matrix4f();
   private final Quaternionf lastCameraRotation = new Quaternionf();
   private final Quaternionf lastInverseCameraRotation = new Quaternionf();
   private Vec3d lastCameraPos = Vec3d.ZERO;
   private float lastTickDelta;
   private int lastScaledWidth;
   private int lastScaledHeight;
   private boolean hasProjection;
   private Framebuffer maskBuffer;
   private final List<Framebuffer> bloomBuffers = new ArrayList<>();
   private final Map<UUID, SegmentsHolder> donateCache = new HashMap<>();
   private final Map<Integer, Float> entityHurtTintProgress = new HashMap<>();
   private long nextDonateCacheCleanupAt;
   private int maskWidth = -1;
   private int maskHeight = -1;
   private boolean hasShaderMask;
   private final Vector3f projectionScratch = new Vector3f();
   private final Vector4f clipScratch = new Vector4f();
   private final XHolder_1 projectedPoint = new XHolder_1();
   private int frameThemeColor = -1;
   private final BooleanSetting targetPlayers = new BooleanSetting(
      "Игроки", true
   );
   private final BooleanSetting targetMobs = new BooleanSetting("Мобы", true);
   private final BooleanSetting targetAnimals = new BooleanSetting(
      "Животные", true
   );
   private final BooleanSetting targetItems = new BooleanSetting(
      "Предметы", true
   );
   private final MultiBooleanSetting targets = new MultiBooleanSetting(
      "Отображать",
      this.targetPlayers,
      this.targetMobs,
      this.targetAnimals,
      this.targetItems
   );
   private static final Map<String, String> EFFECT_RU = new HashMap<>();

   public EntityESP() {
      super(
         "EntityESP",
         "Показывает игроков через стену",
         Category.RENDER
      );
      this.addSettings(new Setting[]{this.targets, this.elements});
      this.addSettings(new Setting[]{this.show3DBox, this.boxFilled, this.itemStackList, this.hurtTint});
   }

   @Override
   public void onDisable() {
      this.hasProjection = false;
      this.hasShaderMask = false;
      this.donateCache.clear();
      this.entityHurtTintProgress.clear();
      this.nextDonateCacheCleanupAt = 0L;
      if (this.maskBuffer != null) {
         this.maskBuffer.delete();
         this.maskBuffer = null;
      }

      for (Framebuffer val : this.bloomBuffers) {
         val.delete();
      }

      this.bloomBuffers.clear();
      super.onDisable();
   }

   @EventLink(
      priority = 100
   )
   public void onRender3D(
      Event3DRender val
   ) {
      this.hasProjection = true;
      this.lastProjectionMatrix
         .set(
            val.getProjectionMatrix()
         );
      this.lastCameraPos = val.getCamera()
         .getPos();
      this.lastCameraRotation
         .set(
            val.getCamera()
               .getRotation()
         );
      this.lastInverseCameraRotation.set(this.lastCameraRotation).conjugate();
      this.lastTickDelta = val.getTickDelta();
      this.lastScaledWidth = mc.getWindow().getScaledWidth();
      this.lastScaledHeight = mc.getWindow().getScaledHeight();
      this.frameThemeColor = this.getStableThemeColor();
      this.hasShaderMask = false;
      if (this.show3DBox.isState() && mc.world != null && mc.player != null) {
         MatrixStack matrices = val.getMatrices();
         float f2 = val.getTickDelta();
         boolean flag = this.isPostBoxMode();
         boolean flag2 = this.isThreadMode();
         if (flag
            )
          {
            this.ensureMaskBuffer();
            if (this.maskBuffer != null) {
               this.maskBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               this.maskBuffer.clear();
               this.copyMainDepthToMask();
               this.maskBuffer.beginWrite(false);
               RenderSystem.disableBlend();
               RenderSystem.enableDepthTest();
               RenderSystem.depthMask(false);
               RenderSystem.disableCull();
               RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            }
         }

         for (Entity entity2 : mc.world
            .getEntities()) {
            if (this.shouldProcess3DEntity(
               entity2
            )) {
               if (flag
                  && this.maskBuffer != null) {
                  this.drawPlayerMaskBox(
                     matrices,
                     entity2,
                     f2
                  );
                  this.hasShaderMask = true;
               } else {
                  this.render3DBox(
                     matrices,
                     entity2,
                     f2
                  );
               }
            }
         }

         if (flag
            && this.maskBuffer != null) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            mc.getFramebuffer().beginWrite(true);
            if (this.show3DBox.isState()) {
               this.renderShaderBoxesWorldPass();
            }
         }

         if (flag2
            )
          {
            for (Entity entity3 : mc.world
               .getEntities()) {
               if (this.shouldProcess3DEntity(
                  entity3
               )) {
                  this.renderThreadWeb(
                     matrices,
                     entity3,
                     f2
                  );
               }
            }
         }
      }
   }

   @EventLink(
      priority = 100
   )
   public void onRender2D(
      EventRender.Default val
   ) {
      if (this.hasProjection && mc.world != null && mc.player != null) {
         this.frameThemeColor = this.getStableThemeColor();
         boolean flag = !this.elements
               .getSettings()
               .isEmpty()
            && this.elements.getSettings().get(0).isState();
         boolean flag2 = this.elements
                  .getSettings()
                  .size()
               > 1
            && this.elements.getSettings().get(1).isState();
         boolean flag3 = this.elements
                  .getSettings()
                  .size()
               > 2
            && this.elements.getSettings().get(2).isState();
         boolean flag4 = this.elements
                  .getSettings()
                  .size()
               > 4
            && this.elements.getSettings().get(4).isState();
         boolean flag5 = this.elements
                  .getSettings()
                  .size()
               > 5
            && this.elements.getSettings().get(5).isState();
         if (flag
            || flag2
            || flag4
            || flag5
            )
          {
            Font var30;
            if (flag
               )
             {
               var30 = Fonts.getFont("sf_regular", 13);
            } else {
               var30 = null;
            }

            Font font = var30;
            if (font
               != null) {
               var30 = font;
            } else {
               var30 = Fonts.getFont("sf_regular", 13);
            }

            Font font2 = var30;
            int n = 0;

            for (Entity entity2 : mc.world
               .getEntities()) {
               if (entity2 instanceof PlayerEntity
                  )
                {
                  PlayerEntity player2 = (PlayerEntity)entity2;
                  if (this.shouldProcess2DPlayer(
                     player2
                  )) {
                     Box box = this.getInterpolatedBox(
                        player2,
                        this.lastTickDelta
                     );
                     MinxHelper val2 = this.projectBox(
                        box
                     );
                     if (val2
                        != null) {
                        if (flag5
                           )
                         {
                           this.drawBuffs(
                              val,
                              player2,
                              val2
                           );
                        }

                        if (flag
                           && font
                              != null) {
                           this.drawTag(
                              val,
                              player2,
                              val2,
                              font,
                              flag3
                           );
                        }

                        if (flag2
                           )
                         {
                           this.drawArmor(
                              val,
                              player2,
                              val2,
                              flag
                           );
                        }

                        if (flag4
                           )
                         {
                           this.drawSecondHand(
                              val,
                              player2,
                              val2,
                              font2
                           );
                        }
                     }
                  }
               } else if (flag
                  && font
                     != null) {
                  if (entity2 instanceof ItemEntity
                     )
                   {
                     ItemEntity entity3 = (ItemEntity)entity2;
                     if (this.shouldProcessItem2D(
                           entity3
                        )
                        && n
                           >= 48) {
                     }
                  } else if (entity2 instanceof LivingEntity
                     )
                   {
                     LivingEntity entity4 = (LivingEntity)entity2;
                     if (this.shouldProcessLiving2D(
                        entity4
                     )) {
                        Box box2 = this.getInterpolatedBox(
                           entity4,
                           this.lastTickDelta
                        );
                        MinxHelper val3 = this.projectBox(
                           box2
                        );
                        if (val3
                           != null) {
                           this.drawLivingTag(
                              val,
                              entity4,
                              val3,
                              font
                           );
                        }
                     }
                  }
               }
            }

            if (flag
               && font
                  != null) {
               List<ItemEntity> entity5 = new ArrayList<>();

               for (Entity entity6 : mc.world
                  .getEntities()) {
                  if (entity6 instanceof ItemEntity
                     )
                   {
                     ItemEntity entity7 = (ItemEntity)entity6;
                     if (this.shouldProcessItem2D(
                        entity7
                     )) {
                        entity5.add(
                           entity7
                        );
                     }
                  }
               }

               for (ItemsHolder item2 : this.clusterItems(
                  entity5
               )) {
                  if (n
                     >= 48) {
                     break;
                  }

                  float y2 = (float)mc.player
                     .getPos()
                     .distanceTo(
                        item2.centerPos
                     );
                  boolean flag6 = this.itemStackList
                        .isState()
                     && y2
                        > 12.0F;
                  if (flag6
                     && item2.items
                           .size()
                        > 1) {
                     this.drawStackedItems(
                        val,
                        item2,
                        font
                     );
                     n += item2.items
                        .size();
                  } else {
                     for (ItemEntity entity8 : item2.items) {
                        if (n
                           >= 48) {
                           break;
                        }

                        if (this.projectEntityAnchor(
                           entity8,
                           (double)entity8.getHeight()
                              + 0.25,
                           this.projectedPoint
                        )) {
                           this.drawDroppedItemTag(
                              val,
                              entity8,
                              this.projectedPoint.x,
                              this.projectedPoint.y,
                              font
                           );
                           n++;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void drawSecondHand(
      EventRender.Default val,
      PlayerEntity val2,
      MinxHelper val3,
      Font val4
   ) {
      MatrixStack matrices = val.getContext()
         .getMatrices();
      ItemStack stack = val2.getMainHandStack();
      ItemStack stack2 = val2.getOffHandStack();
      boolean flag = !stack.isEmpty();
      boolean flag2 = !stack2.isEmpty();
      if (flag
         || flag2
         )
       {
         float f2 = 3.0F;
         float f3 = 10.0F;
         float f4 = 1.5F;
         boolean flag3 = AspectDLC.INSTANCE
                  .friendStorage
               != null
            && AspectDLC.INSTANCE
               .friendStorage
               .isFriend(
                  val2.getName()
                     .getString()
               );
         float x2 = val3.maxY()
            + 2.0F;
         if (flag
            )
          {
            int count = stack.getCount();
            String str = count
                  > 1
               ? stack.getName()
                     .getString()
                  + " ×"
                  + count
               : stack.getName()
                  .getString();
            float width = val4.getStringWidth(
               str
            );
            float f5 = width
               + f2
                  * 2.0F;
            float x3 = val3.centerX()
               - f5
                  * 0.5F;
            if (flag3
               )
             {
               this.drawFriendTagPanel(
                  matrices,
                  x3,
                  x2,
                  f5,
                  f3
               );
            } else {
               this.drawDefaultTagPanel(
                  matrices,
                  x3,
                  x2,
                  f5,
                  f3
               );
            }

            val4.drawString(
               matrices,
               str,
               x3
                  + f2,
               x2
                  + 2.8F,
               this.getMinecraftItemNameColor(
                  stack
               )
            );
         }

         if (flag2
            )
          {
            float f6 = x2
               + (
                  flag
                     ? f3
                        + f4
                     : 0.0F
               );
            int count2 = stack2.getCount();
            String str2 = count2
                  > 1
               ? stack2.getName()
                     .getString()
                  + " ×"
                  + count2
               : stack2.getName()
                  .getString();
            float width2 = val4.getStringWidth(
               str2
            );
            float f7 = width2
               + f2
                  * 2.0F;
            float x4 = val3.centerX()
               - f7
                  * 0.5F;
            if (flag3
               )
             {
               this.drawFriendTagPanel(
                  matrices,
                  x4,
                  f6,
                  f7,
                  f3
               );
            } else {
               this.drawDefaultTagPanel(
                  matrices,
                  x4,
                  f6,
                  f7,
                  f3
               );
            }

            val4.drawString(
               matrices,
               str2,
               x4
                  + f2,
               f6
                  + 2.8F,
               this.getMinecraftItemNameColor(
                  stack2
               )
            );
         }
      }
   }

   private void drawBuffs(
      EventRender.Default val,
      PlayerEntity val2,
      MinxHelper val3
   ) {
      MatrixStack matrices = val.getContext()
         .getMatrices();
      Collection<StatusEffectInstance> val4 = val2.getStatusEffects();
      if (!val4.isEmpty()
         )
       {
         Font font = Fonts.getFont(
            "sf_regular", 10
         );
         if (font
            != null) {
            float f2 = 5.0F;
            float f3 = 3.8F;
            float f4 = 10.0F;
            float f5 = 1.5F;
            float f6 = 3.0F;
            float x2 = val3.maxX()
               + f6;
            float f7 = 0.0F;
            int n = 0;

            for (StatusEffectInstance val5 : val4) {
               if (n
                  >= 10) {
                  break;
               }

               f7 += f4
                  + f5;
               n++;
            }

            if (f7
               > 0.0F) {
               f7 -= f5;
            }

            float y2 = val3.centerY()
               - f7
                  * 0.5F;
            int n2 = 0;

            for (StatusEffectInstance val6 : val4) {
               if (n2
                  >= 10) {
                  break;
               }

               String name = this.getEffectShortName(
                  val6
               );
               if (val6.getAmplifier()
                  > 0) {
                  name = name
                     + " "
                     + this.toRoman(
                        val6.getAmplifier()
                           + 1
                     );
               }

               String text2 = this.getDurationText(
                  val6.getDuration()
               );
               String str = name
                  + " "
                  + text2;
               float width = font.getStringWidth(
                  str
               );
               float f8 = width
                  + f2
                     * 2.0F;
               float f9 = y2
                  + (float)n2
                     * (
                        f4
                           + f5
                     );
               int color3 = this.getEffectColor(
                  val6
               );
               int color4 = ColorUtil.rgba(
                  (int)(
                     ColorUtil.redf(
                           color3
                        )
                        * 255.0F
                        * 0.18F
                  ),
                  (int)(
                     ColorUtil.greenf(
                           color3
                        )
                        * 255.0F
                        * 0.18F
                  ),
                  (int)(
                     ColorUtil.bluef(
                           color3
                        )
                        * 255.0F
                        * 0.18F
                  ),
                  180
               );
               float f10 = 2.5F;
               Render2D.drawRoundedRect(
                  matrices,
                  x2,
                  f9,
                  f10,
                  f4,
                  1.0F,
                  ColorUtil.setAlphaColor(
                     color3,
                     210
                  )
               );
               Render2D.drawRoundedRect(
                  matrices,
                  x2
                     + f10,
                  f9,
                  f8,
                  f4,
                  1.0F,
                  color4
               );
               float f11 = x2
                  + f10
                  + f2;
               float f12 = f9
                  + f3;
               font.drawString(
                  matrices,
                  str,
                  f11,
                  f12,
                  ColorUtil.rgba(
                     255,
                     255,
                     255,
                     230
                  )
               );
               n2++;
            }
         }
      }
   }

   private String toRoman(
      int val
   ) {
      return switch (val) {
         case 1 -> "I";
         case 2 -> {
            yield "II";
         }
         case 3 -> {
            yield "III";
         }
         case 4 -> {
            yield "IV";
         }
         case 5 -> "V";
         default -> String.valueOf(
         val
      );
      };
   }

   private int getEffectColor(
      StatusEffectInstance val
   ) {
      StatusEffectCategory val2 = ((StatusEffect)val.getEffectType()
            .value())
         .getCategory();

      return switch (val2) {
         case BENEFICIAL -> ColorUtil.rgba(
         80,
         220,
         100,
         255
      );
         case HARMFUL -> ColorUtil.rgba(
         220,
         60,
         60,
         255
      );
         case NEUTRAL -> ColorUtil.rgba(
         160,
         160,
         200,
         255
      );
         default -> throw new MatchException(null, null);
      };
   }

   private String getEffectShortName(
      StatusEffectInstance val
   ) {
      String str = val.getTranslationKey();
      String str2 = str.substring(
            str.lastIndexOf(
                  "."
               )
               + 1
         )
         .toLowerCase();
      String str3 = EFFECT_RU.get(
         str2
      );
      if (str3
         != null) {
         return str3;
      } else {
         str2 = str2.replace(
            "_",
            " "
         );
         return str2.substring(
                  0, 1
               )
               .toUpperCase()
            + str2.substring(
               1
            );
      }
   }

   private String getDurationText(
      int val
   ) {
      int n = val
         / 20;
      int n2 = n
         / 60;
      n %= 60;
      String var5 = "%d:%02d";
      Object[] var10001 = new Object[2];
      var10001[0] = n2;
      var10001[1] = n;
      return String.format(var5, var10001);
   }

   private void drawTag(
      EventRender.Default val,
      PlayerEntity val2,
      MinxHelper val3,
      Font val4,
      boolean val18
   ) {
      MatrixStack matrices = val.getContext()
         .getMatrices();
      List<TextHelper> list = this.getDonateSegmentsFromTab(
         val2
      );
      String name = this.getProtectedName(
         val2.getNameForScoreboard()
      );
      float f2 = ScoreboardHP.getHealthWithAbsorption(
         val2
      );
      String str = Math.round(
            f2
         )
         + " HP";
      float f3 = 2.0F;
      boolean flag = AspectDLC.INSTANCE
               .friendStorage
            != null
         && AspectDLC.INSTANCE
            .friendStorage
            .isFriend(
               val2.getName()
                  .getString()
            );
      String var41;
      if (flag
         )
       {
         var41 = " [F]";
      } else {
         var41 = "";
      }

      String str2 = var41;
      float f4 = 0.0F;

      for (TextHelper val5 : list) {
         f4 += val4.getStringWidth(
            val5.text()
         );
      }

      float f5 = f4
         + val4.getStringWidth(
            name
         )
         + f3
         + val4.getStringWidth(
            str
         )
         + val4.getStringWidth(
            str2
         );
      float f6 = 14.0F;
      float x2 = val3.centerX()
         - f5
            * 0.5F;
      float y2 = this.getTagTopY(
         val3,
         f6
      );
      float f7 = 2.0F;
      float f8 = -0.4F;
      float f9 = 10.0F;
      float f10 = 3.0F;
      float f11 = f4
         + val4.getStringWidth(
            name
         )
         + val4.getStringWidth(
            str2
         );
      float f12 = x2
         - f7;
      float f13 = y2
         + f8;
      float f14 = f11
         + f7
            * 2.0F;
      float f15 = f12
         + f14
         + f10;
      float width = val4.getStringWidth(
            str
         )
         + f7
            * 2.0F;
      if (flag
         )
       {
         this.drawFriendTagPanel(
            matrices,
            f12,
            f13,
            f14,
            f9
         );
      } else {
         this.drawDefaultTagPanel(
            matrices,
            f12,
            f13,
            f14,
            f9
         );
      }

      if (flag
         )
       {
         this.drawFriendTagPanel(
            matrices,
            f15,
            f13,
            width,
            f9
         );
      } else {
         this.drawDefaultTagPanel(
            matrices,
            f15,
            f13,
            width,
            f9
         );
      }

      float f16 = x2;

      for (TextHelper val6 : list) {
         val4.drawString(
            matrices,
            val6.text(),
            f16,
            y2
               + 3.0F,
            val6.color()
         );
         f16 += val4.getStringWidth(
            val6.text()
         );
      }

      val4.drawString(
         matrices,
         name,
         f16,
         y2
            + 3.0F,
         -1
      );
      f16 += val4.getStringWidth(
         name
      );
      if (flag
         )
       {
         val4.drawString(
            matrices,
            str2,
            f16,
            y2
               + 3.0F,
            -11141291
         );
         f16 += val4.getStringWidth(
            str2
         );
      }

      float f17 = f2
         / Math.max(
            1.0F,
            val2.getMaxHealth()
         );
      int n;
      if (f17
         > 0.75F) {
         n = ColorUtil.rgba(
            85,
            255,
            85,
            255
         );
      } else if (f17
         >= 0.35F) {
         n = ColorUtil.rgba(
            255,
            170,
            0,
            255
         );
      } else {
         n = ColorUtil.rgba(
            255,
            85,
            85,
            255
         );
      }

      val4.drawString(
         matrices,
         str,
         f15
            + f7,
         y2
            + 3.0F,
         n
      );
   }

   private void drawPlayerHealthBar(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5,
      PlayerEntity val6
   ) {
      float f2 = ScoreboardHP.getHealthWithAbsorption(
         val6
      );
      float x2 = Math.max(
         1.0F,
         val6.getMaxHealth()
      );
      x2 = Math.max(
         x2,
         f2
      );
      float f3 = MathHelper.clamp(
         f2
            / x2,
         0.0F,
         1.0F
      );
      float f4 = val3
         + val5
         + 1.15F;
      int color3 = this.frameThemeColor;
      int color4 = ColorUtil.rgba(
         10,
         10,
         14,
         215
      );
      int color5 = ColorUtil.rgba(
         235,
         72,
         72,
         255
      );
      int color6 = ColorUtil.interpolateColor(
         ColorUtil.rgba(
            55,
            210,
            235,
            255
         ),
         color3,
         0.45F
      );
      int color7 = ColorUtil.interpolateColor(
         color5,
         color6,
         f3
      );
      int color8 = ColorUtil.setAlphaColor(
         ColorUtil.darken(
            color3,
            0.2F
         ),
         200
      );
      Render2D.drawRoundedRect(
         val,
         val2,
         f4,
         val4,
         4.2F,
         1.05F,
         color4
      );
      if (f3
         > 0.004F) {
         float x3 = Math.max(
            2.31F,
            val4
               * f3
         );
         Render2D.drawRoundedRect(
            val,
            val2,
            f4,
            x3,
            4.2F,
            1.05F,
            color7
         );
         int color9 = ColorUtil.setAlphaColor(
            ColorUtil.lighten(
               color7,
               0.22F
            ),
            90
         );
         Render2D.drawRoundedRect(
            val,
            val2
               + 0.35F,
            f4
               + 0.45F,
            Math.max(
               0.0F,
               x3
                  - 0.7F
            ),
            Math.max(0.8F, 1.596F),
            0.68249995F,
            color9
         );
      }

      Render2D.drawRoundedRectOutline(
         val,
         val2,
         f4,
         val4,
         4.2F,
         1.05F,
         1.05F,
         1.05F,
         1.05F,
         0.45F,
         color8,
         color8,
         color8,
         color8
      );
   }

   private void drawArmor(
      EventRender.Default val,
      PlayerEntity val2,
      MinxHelper val3,
      boolean val18
   ) {
      MatrixStack matrices = val.getContext()
         .getMatrices();
      ItemStack[] stack = new ItemStack[4];
      int n = 0;
      ItemStack[] stack2 = new ItemStack[4];
      int n2 = 0;

      for (ItemStack stack3 : val2.getArmorItems()) {
         stack2[n2++] = stack3;
      }

      for (int i = 3;
         i
            >= 0;
         i--
      ) {
         if (!stack2[i]
            .isEmpty()) {
            stack[n++] = stack2[i];
         }
      }

      ItemStack stack4 = val2.getMainHandStack();
      boolean flag = !stack4.isEmpty();
      ItemStack stack5 = val2.getOffHandStack();
      boolean flag2 = !stack5.isEmpty();
      int n3 = n
         + (
            flag
               ? 1
               : 0
         )
         + (
            flag2
               ? 1
               : 0
         );
      if (n3
         != 0) {
         float f2 = 11.5F;
         float f3 = (float)n3
               * 10.0F
            + (float)Math.max(
                  0,
                  n3
                     - 1
               )
               * 1.5F;
         float x2 = val3.centerX();
         float f4 = 13.0F;
         float y2 = this.getTagTopY(
            val3,
            14.0F
         );
         float f5 = y2
            - 1.0F;
         float f6 = f5
            - 10.0F
            - 2.0F;
         float f7 = x2
            - f3
               * 0.5F;

         for (int i2 = 0;
            i2
               < n3;
            i2++
         ) {
            float f8 = f7
               + (float)i2
                  * f2;
            this.drawDefaultTagPanel(
               matrices,
               f8,
               f6,
               10.0F,
               10.0F
            );
         }

         for (int i3 = 0;
            i3
               < n;
            i3++
         ) {
            ItemStack stack6 = stack[i3];
            if (stack6.isDamageable()
               && stack6.getDamage()
                  > 0) {
               float f9 = f7
                  + (float)i3
                     * f2;
               float f10 = 1.0F
                  - (float)stack6.getDamage()
                     / (float)stack6.getMaxDamage();
               this.drawDurabilityBar(
                  matrices,
                  f9,
                  f6
                     + 10.0F
                     - 1.5F,
                  10.0F,
                  f10
               );
            }
         }

         int n4 = n;
         if (flag
            )
          {
            if (stack4.isDamageable()
               && stack4.getDamage()
                  > 0) {
               float f11 = f7
                  + (float)n
                     * f2;
               float f12 = 1.0F
                  - (float)stack4.getDamage()
                     / (float)stack4.getMaxDamage();
               this.drawDurabilityBar(
                  matrices,
                  f11,
                  f6
                     + 10.0F
                     - 1.5F,
                  10.0F,
                  f12
               );
            }

            n4 = n
               + 1;
         }

         if (flag2
            )
          {
            if (stack5.isDamageable()
               && stack5.getDamage()
                  > 0) {
               float f13 = f7
                  + (float)n4
                     * f2;
               float f14 = 1.0F
                  - (float)stack5.getDamage()
                     / (float)stack5.getMaxDamage();
               this.drawDurabilityBar(
                  matrices,
                  f13,
                  f6
                     + 10.0F
                     - 1.5F,
                  10.0F,
                  f14
               );
            }

            n4++;
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         float f15 = 8.8F;
         float f16 = (
               10.0F
                  - f15
            )
            * 0.5F;

         for (int i4 = 0;
            i4
               < n;
            i4++
         ) {
            float f17 = f7
               + (float)i4
                  * f2;
            matrices.push();
            matrices.translate(
               f17
                  + f16,
               f6
                  + f16,
               0.0F
            );
            matrices.scale(
               0.55F, 0.55F, 1.0F
            );
            val.getContext()
               .drawItem(
                  stack[i4],
                  0,
                  0
               );
            matrices.pop();
         }

         int n5 = n;
         if (flag
            )
          {
            float f18 = f7
               + (float)n
                  * f2;
            matrices.push();
            matrices.translate(
               f18
                  + f16,
               f6
                  + f16,
               0.0F
            );
            matrices.scale(
               0.55F, 0.55F, 1.0F
            );
            val.getContext()
               .drawItem(
                  stack4,
                  0,
                  0
               );
            matrices.pop();
            n5 = n
               + 1;
         }

         if (flag2
            )
          {
            float f19 = f7
               + (float)n5
                  * f2;
            matrices.push();
            matrices.translate(
               f19
                  + f16,
               f6
                  + f16,
               0.0F
            );
            matrices.scale(
               0.55F, 0.55F, 1.0F
            );
            val.getContext()
               .drawItem(
                  stack5,
                  0,
                  0
               );
            matrices.pop();
            n5++;
         }

         MultiBooleanSetting var10000 = this.elements;
         if (var10000.is("Зачары")) {
            Font font = Fonts.getFont(
               "nunito", 9
            );
            if (font
               != null) {
               for (int i5 = 0;
                  i5
                     < n;
                  i5++
               ) {
                  ItemStack stack7 = stack[i5];
                  String text2 = this.getArmorEnchantText(
                     stack7
                  );
                  if (text2
                        != null
                     && !text2.isEmpty()
                     )
                   {
                     float f20 = f7
                        + (float)i5
                           * f2;
                     float f21 = f20
                        + 5.0F
                        - font.getStringWidth(
                              text2
                           )
                           * 0.5F;
                     float f22 = f6
                        - 8.0F;
                     int color3 = ColorUtil.rgba(
                        200,
                        200,
                        255,
                        255
                     );
                     font.drawString(
                        matrices,
                        text2,
                        f21,
                        f22,
                        color3
                     );
                  }
               }
            }
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
      }
   }

   private String getArmorEnchantText(
      ItemStack val
   ) {
      if (val
            != null
         && !val.isEmpty()
         )
       {
         ItemEnchantmentsComponent item2 = (ItemEnchantmentsComponent)val.getOrDefault(
            DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT
         );
         if (item2.isEmpty()
            )
          {
            return null;
         } else {
            String[][] var10000 = new String[11][];
            String[] var10003 = new String[2];
            var10003[0] = "protection";
            var10003[1] = "P";
            var10000[0] = var10003;
            var10003 = new String[2];
            var10003[0] = "blast_protection";
            var10003[1] = "B";
            var10000[1] = var10003;
            int var10002 = 2;
            var10003 = new String[2];
            var10003[0] = "fire_protection";
            var10003[1] = "F";
            var10000[var10002] = var10003;
            var10002 = 3;
            var10003 = new String[2];
            var10003[0] = "projectile_protection";
            var10003[1] = "Pr";
            var10000[var10002] = var10003;
            var10002 = 4;
            var10003 = new String[2];
            var10003[0] = "feather_falling";
            var10003[1] = "FF";
            var10000[var10002] = var10003;
            var10002 = 5;
            var10003 = new String[2];
            var10003[0] = "thorns";
            var10003[1] = "T";
            var10000[var10002] = var10003;
            var10002 = 6;
            var10003 = new String[2];
            var10003[0] = "depth_strider";
            var10003[1] = "D";
            var10000[var10002] = var10003;
            var10002 = 7;
            var10003 = new String[2];
            var10003[0] = "respiration";
            var10003[1] = "R";
            var10000[var10002] = var10003;
            var10002 = 8;
            var10003 = new String[2];
            var10003[0] = "aqua_affinity";
            var10003[1] = "A";
            var10000[var10002] = var10003;
            var10002 = 9;
            var10003 = new String[2];
            var10003[0] = "unbreaking";
            var10003[1] = "U";
            var10000[var10002] = var10003;
            var10002 = 10;
            var10003 = new String[2];
            var10003[0] = "mending";
            var10003[1] = "M";
            var10000[var10002] = var10003;
            String[][] val2 = var10000;

            for (String[] val3 : val2) {
               for (RegistryEntry<Enchantment> val4 : item2.getEnchantments()) {
                  Identifier id = val4.getKey()
                     .map(
                        val5 -> ((net.minecraft.registry.RegistryKey)val5).getValue()
                     )
                     .orElse(null);
                  if (id
                        != null
                     && id.getPath()
                        .contains(
                           val3[0]
                        )) {
                     int n = item2.getLevel(
                        val4
                     );
                     return val3[1]
                        + n;
                  }
               }
            }

            Iterator<RegistryEntry<Enchantment>> val6 = item2.getEnchantments()
               .iterator();
            if (val6.hasNext()
               )
             {
               RegistryEntry<Enchantment> val7 = val6.next();
               Identifier id2 = val7.getKey()
                  .map(
                     val8 -> ((net.minecraft.registry.RegistryKey)val8).getValue()
                  )
                  .orElse(null);
               int n2 = item2.getLevel(
                  val7
               );
               String str = id2
                     != null
                  ? id2.getPath()
                  : "?";
               return str.substring(
                        0,
                        Math.min(
                           2,
                           str.length()
                        )
                     )
                     .toUpperCase()
                  + n2;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private void drawDurabilityBar(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5
   ) {
      int n = -872415232;
      int color3 = ColorUtil.rgba(
         235,
         72,
         72,
         255
      );
      int color4 = ColorUtil.rgba(
         85,
         255,
         85,
         255
      );
      int color5 = ColorUtil.interpolateColor(
         color3,
         color4,
         val5
      );
      Render2D.drawRoundedRect(
         val,
         val2,
         val3,
         val4,
         2.5F,
         0.8F,
         n
      );
      float x2 = Math.max(
         1.0F,
         val4
            * val5
      );
      Render2D.drawRoundedRect(
         val,
         val2,
         val3,
         x2,
         2.5F,
         0.8F,
         color5
      );
   }

   private void drawLivingTag(
      EventRender.Default val,
      LivingEntity val2,
      MinxHelper val3,
      Font val4
   ) {
      MatrixStack matrices = val.getContext()
         .getMatrices();
      String str = val2 instanceof PlayerEntity val5
         ? this.getProtectedName(
            val5.getDisplayName()
               .getString()
         )
         : val2.getDisplayName()
            .getString();
      String str2 = Math.round(
            ScoreboardHP.getHealthWithAbsorption(
               val2
            )
         )
         + " hp";
      float width = val4.getStringWidth(
            str
         )
         + val4.getStringWidth(
            " "
         )
         + val4.getStringWidth(
            str2
         );
      float f2 = 14.0F;
      float x2 = val3.centerX()
         - width
            * 0.5F;
      float y2 = this.getTagTopY(
         val3,
         f2
      );
      this.drawDefaultTagPanel(
         matrices,
         x2
            - 2.0F,
         y2
            + 0.5F,
         width
            + 4.0F,
         10.0F
      );
      val4.drawString(
         matrices,
         str,
         x2,
         y2
            + 3.0F,
         -1
      );
      val4.drawString(
         matrices,
         str2,
         x2
            + val4.getStringWidth(
               str
            )
            + val4.getStringWidth(
               " "
            ),
         y2
            + 3.0F,
         -43691
      );
   }

   private void drawDroppedItemTag(
      EventRender.Default val,
      ItemEntity val2,
      float val3,
      float val4,
      Font val5
   ) {
      MatrixStack matrices = val.getContext()
         .getMatrices();
      ItemStack stack = val2.getStack();
      String str = stack.getCount()
         + "x";
      String name = stack.getName()
         .getString();
      int color3 = this.getDroppedItemTextColor(
         stack
      );
      int color4 = ColorUtil.rgba(
         155,
         155,
         155,
         255
      );
      float width = val5.getStringWidth(
         name
      );
      float width2 = val5.getStringWidth(
         " "
      );
      float f2 = width
         + width2
         + val5.getStringWidth(
            str
         );
      float f3 = 14.0F;
      float f4 = val3
         - f2
            * 0.5F;
      float f5 = val4
         - f3
         - 2.0F;
      this.drawDefaultTagPanel(
         matrices,
         f4
            - 2.0F,
         f5
            + 0.5F,
         f2
            + 4.0F,
         10.0F
      );
      val5.drawString(
         matrices,
         name,
         f4,
         f5
            + 3.5F,
         color3
      );
      val5.drawString(
         matrices,
         str,
         f4
            + width
            + width2,
         f5
            + 3.5F,
         color4
      );
   }

   private List<ItemsHolder> clusterItems(
      List<ItemEntity> val
   ) {
      List<ItemsHolder> item2 = new ArrayList<>();
      List<ItemEntity> entity2 = new ArrayList<>(
         val
      );
      float f2 = 3.5F;

      while (
         !entity2.isEmpty()
      ) {
         ItemEntity entity3 = entity2.remove(
            0
         );
         ItemsHolder item3 = new ItemsHolder();
         item3.items
            .add(
               entity3
            );
         Vec3d pos = entity3.getPos();
         List<ItemEntity> entity4 = new ArrayList<>();

         for (ItemEntity entity5 : entity2) {
            if (entity5.getPos()
                  .distanceTo(
                     pos
                  )
               <= (double)f2
               )
             {
               item3.items
                  .add(
                     entity5
                  );
               entity4.add(
                  entity5
               );
            }
         }

         entity2.removeAll(
            entity4
         );
         double d2 = 0.0;
         double d3 = 0.0;
         double d4 = 0.0;

         for (ItemEntity entity6 : item3.items) {
            Vec3d pos2 = entity6.getPos();
            d2 += pos2.x;
            d3 += pos2.y;
            d4 += pos2.z;
         }

         int n = item3.items
            .size();
         item3.centerPos = new Vec3d(
            d2
               / (double)n,
            d3
               / (double)n,
            d4
               / (double)n
         );
         item2.add(
            item3
         );
      }

      return item2;
   }

   private void drawStackedItems(
      EventRender.Default val,
      ItemsHolder val2,
      Font val3
   ) {
      double d2 = val2.items
         .stream()
         .mapToDouble(
            val4 -> val4.getY()
                  + (double)val4.getHeight()
         )
         .max()
         .orElse(
            val2.centerPos
               .y
         );
      Vec3d vec = new Vec3d(
         val2.centerPos
            .x,
         d2
            + 0.25,
         val2.centerPos
            .z
      );
      if (this.projectToScreen(
         vec.x,
         vec.y,
         vec.z,
         this.projectedPoint
      )) {
         MatrixStack matrices = val.getContext()
            .getMatrices();
         float f2 = 12.0F;
         float f3 = this.projectedPoint
            .y;
         List<ItemEntity> entity2 = new ArrayList<>(
            val2.items
         );
         entity2.sort(
            (val5, val6) -> {
               Item item2 = val5.getStack()
                  .getItem();
               Item item3 = val6.getStack()
                  .getItem();
               int n = Registries.ITEM
                  .getRawId(
                     item2
                  );
               int n2 = Registries.ITEM
                  .getRawId(
                     item3
                  );
               return Integer.compare(
                  n,
                  n2
               );
            }
         );

         for (ItemEntity entity3 : entity2) {
            ItemStack stack = entity3.getStack();
            String str = stack.getCount()
               + "x";
            List<TextHelper> list = this.getStyledTextSegments(
               stack.getName(),
               this.getDroppedItemTextColor(
                  stack
               )
            );
            int color3 = ColorUtil.rgba(
               155,
               155,
               155,
               255
            );
            float f4 = 0.0F;

            for (TextHelper val7 : list) {
               f4 += val3.getStringWidth(
                  val7.text()
               );
            }

            float width = val3.getStringWidth(
               " "
            );
            float f5 = f4
               + width
               + val3.getStringWidth(
                  str
               );
            float f6 = 14.0F;
            float f7 = this.projectedPoint
                  .x
               - f5
                  * 0.5F;
            float f8 = f3
               - f6
               - 2.0F;
            this.drawDefaultTagPanel(
               matrices,
               f7
                  - 2.0F,
               f8
                  + 0.5F,
               f5
                  + 4.0F,
               10.0F
            );
            float f9 = f7;

            for (TextHelper val8 : list) {
               val3.drawString(
                  matrices,
                  val8.text(),
                  f9,
                  f8
                     + 3.5F,
                  val8.color()
               );
               f9 += val3.getStringWidth(
                  val8.text()
               );
            }

            val3.drawString(
               matrices,
               str,
               f9
                  + width,
               f8
                  + 3.5F,
               color3
            );
            f3 -= f2;
         }
      }
   }

   private int getMinecraftItemNameColor(
      ItemStack val
   ) {
      Text val2 = val.getName();
      if (val2
         != null) {
         int[] val3 = new int[]{
            0
         };
         boolean[] val4 = new boolean[]{
            false
         };
         val2.visit(
            (val5, val6) -> {
               if (!val4[0]
                  && val5
                     != null
                  && val5.getColor()
                     != null) {
                  val3[0] = -16777216
                     | val5.getColor()
                        .getRgb();
                  val4[0] = true;
               }

               return val4[0]
                  ? Optional.of(
                     val6
                  )
                  : Optional.empty();
            },
            Style.EMPTY
         );
         if (val4[0]
            )
          {
            return val3[0];
         }
      }
      return switch (val.getRarity()) {
         case UNCOMMON -> ColorUtil.rgba(
         255,
         255,
         85,
         255
      );
         case RARE -> ColorUtil.rgba(
         85,
         255,
         255,
         255
      );
         case EPIC -> ColorUtil.rgba(
         255,
         85,
         255,
         255
      );
         case COMMON -> -1;
         default -> throw new MatchException(null, null);
      };
   }

   private int getDroppedItemTextColor(
      ItemStack val
   ) {
      return this.getMinecraftItemNameColor(
         val
      );
   }

   private boolean isNetheriteItem(
      Item val
   ) {
      String var10000 = Registries.ITEM
         .getId(
            val
         )
         .getPath();
      return var10000.contains("netherite");
   }

   private void drawDefaultTagPanel(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5
   ) {
      Render2D.drawRoundedRect(
         val,
         val2,
         val3,
         val4,
         val5,
         1.1F,
         1140850688
      );
   }

   private void drawFriendTagPanel(
      MatrixStack val,
      float val2,
      float val3,
      float val4,
      float val5
   ) {
      int color3 = ColorUtil.rgba(
         50,
         200,
         50,
         60
      );
      Render2D.drawRoundedRect(
         val,
         val2,
         val3,
         val4,
         val5,
         1.1F,
         color3
      );
   }

   public boolean shouldHideVanillaTags() {
      return this.isEnable() && !this.elements.getSettings().isEmpty() && this.elements.getSettings().get(0).isState();
   }

   private float getTagTopY(
      MinxHelper val,
      float val2
   ) {
      return val.minY()
         - val2
         - 0.0F;
   }

   private String[] getNameVariants(
      PlayerEntity val
   ) {
      String str = val.getGameProfile()
            != null
         ? val.getGameProfile()
            .getName()
         : "";
      String name = val.getNameForScoreboard();
      String name2 = this.getProtectedName(
         name
      );
      String name3 = this.getProtectedName(
         str
      );
      String name4 = this.getProtectedName(
         val.getName()
            .getString()
      );
      String[] var10000 = new String[6];
      var10000[0] = val.getName()
         .getString();
      var10000[1] = name4;
      var10000[2] = name;
      var10000[3] = name2;
      var10000[4] = str;
      var10000[5] = name3;
      return var10000;
   }

   private String getProtectedName(
      String val
   ) {
      NameProtect val2 = ModuleManager.INSTANCE
            != null
         ? ModuleManager.nameProtect
         : null;
      return val2
               != null
            && val2.isEnable()
         ? val2.patch(
            val
         )
         : val;
   }

   private int findAnyNameIndex(
      String val,
      String[] val2
   ) {
      if (val
            != null
         && !val.isEmpty()
         && val2
            != null) {
         int n = -1;

         for (String str : val2) {
            if (str
                  != null
               && !str.isEmpty()
               )
             {
               int n2 = this.indexOfIgnoreCase(
                  val,
                  str
               );
               if (n2
                     >= 0
                  && (
                     n
                           == -1
                        || n2
                           < n
                  )) {
                  n = n2;
               }
            }
         }

         return n;
      } else {
         return -1;
      }
   }

   private int indexOfIgnoreCase(
      String val,
      String val2
   ) {
      if (val
            != null
         && val2
            != null
         && !val2.isEmpty()
         )
       {
         int n = val.length()
            - val2.length();

         for (int i = 0;
            i
               <= n;
            i++
         ) {
            if (val.regionMatches(
               true,
               i,
               val2,
               0,
               val2.length()
            )) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void trimSegmentsToLength(
      List<TextHelper> val,
      int val2
   ) {
      int n = Math.max(
         0,
         val2
      );
      List val3 = new ArrayList<>();

      for (TextHelper val4 : val) {
         if (n
            <= 0) {
            break;
         }

         String text2 = val4.text();
         if (text2.length()
            <= n
            )
          {
            val3.add(
               val4
            );
            n -= text2.length();
         } else {
            val3.add(
               new TextHelper(
                  text2.substring(
                     0,
                     n
                  ),
                  val4.color()
               )
            );
            n = 0;
         }
      }

      val.clear();
      val.addAll(
         val3
      );
   }

   private List<TextHelper> getDonateSegmentsFromTab(
      PlayerEntity val
   ) {
      long val2 = System.currentTimeMillis();
      SegmentsHolder val3 = this.donateCache
         .computeIfAbsent(
            val.getUuid(),
            val4 -> new SegmentsHolder()
         );
      if (val2
         < val3.nextUpdateAt
         )
       {
         return val3.segments;
      } else {
         List<TextHelper> list = new ArrayList<>();
         KeyHelper val5 = GetplayerprivilegeHelper.getPlayerPrivilege(
            val
         );
         if (val5
            != null) {
            list.add(
               new TextHelper(
                  val5.text()
                     + " ",
                  val5.col()
               )
            );
            val3.segments = list;
            val3.nextUpdateAt = val2
               + 1000L;
            this.cleanupDonateCache(
               val2
            );
            return val3.segments;
         } else if (mc.getNetworkHandler() == null) {
            val3.segments = Collections.emptyList();
            val3.nextUpdateAt = val2
               + 1000L;
            return val3.segments;
         } else {
            PlayerListEntry val6 = mc.getNetworkHandler()
               .getPlayerListEntry(
                  val.getUuid()
               );
            if (val6
               == null) {
               val3.segments = Collections.emptyList();
               val3.nextUpdateAt = val2
                  + 1000L;
               return val3.segments;
            } else {
               Text val7 = val6.getDisplayName();
               if (val7
                  == null) {
                  val7 = val.getDisplayName();
               }

               if (val7
                  == null) {
                  val3.segments = Collections.emptyList();
                  val3.nextUpdateAt = val2
                     + 1000L;
                  return val3.segments;
               } else {
                  String[] val8 = this.getNameVariants(
                     val
                  );
                  boolean[] val9 = new boolean[]{
                     false
                  };
                  val7.visit(
                     (val10, val11) -> {
                        if (!val9[0]
                           && val11
                              != null
                           && !val11.isEmpty()
                           )
                         {
                           String str = val11.replace(
                                 (char)10,
                                 (char)32
                              )
                              .replace(
                                 (char)13,
                                 (char)32
                              );
                           int n = this.findAnyNameIndex(
                              str,
                              val8
                           );
                           String str2 = n
                                 >= 0
                              ? str.substring(
                                 0,
                                 n
                              )
                              : str;
                           if (!str2.isEmpty()
                              )
                            {
                              int color3 = val10.getColor()
                                    != null
                                 ? val10.getColor()
                                    .getRgb()
                                 : 16777215;
                              this.appendColoredSegments(
                                 list,
                                 str2,
                                 color3
                              );
                           }

                           if (n
                              >= 0) {
                              val9[0] = true;
                           }

                           return Optional.empty();
                        } else {
                           return Optional.empty();
                        }
                     },
                     Style.EMPTY
                  );
                  if (!val9[0]
                     )
                   {
                     list.clear();
                     Team val12 = val.getScoreboardTeam();
                     if (val12
                           != null
                        && val12.getPrefix()
                           != null) {
                        this.appendTextSegments(
                           list,
                           val12.getPrefix()
                        );
                     }
                  }

                  if (list.isEmpty()
                     )
                   {
                     val3.segments = Collections.emptyList();
                     val3.nextUpdateAt = val2
                        + 1000L;
                     this.cleanupDonateCache(
                        val2
                     );
                     return val3.segments;
                  } else {
                     StringBuilder val13 = new StringBuilder();

                     for (TextHelper val14 : list) {
                        val13.append(
                           val14.text()
                        );
                     }

                     int n2 = this.findAnyNameIndex(
                        val13.toString(),
                        val8
                     );
                     if (n2
                        >= 0) {
                        if (n2
                           == 0) {
                           val3.segments = Collections.emptyList();
                           val3.nextUpdateAt = val2
                              + 1000L;
                           this.cleanupDonateCache(
                              val2
                           );
                           return val3.segments;
                        }

                        this.trimSegmentsToLength(
                           list,
                           n2
                        );
                     }

                     if (list.isEmpty()
                        )
                      {
                        val3.segments = Collections.emptyList();
                        val3.nextUpdateAt = val2
                           + 1000L;
                        this.cleanupDonateCache(
                           val2
                        );
                        return val3.segments;
                     } else {
                        StringBuilder val15 = new StringBuilder();

                        for (TextHelper val16 : list) {
                           val15.append(
                              val16.text()
                           );
                        }

                        if (val15.toString()
                           .trim()
                           .isEmpty()) {
                           val3.segments = Collections.emptyList();
                           val3.nextUpdateAt = val2
                              + 1000L;
                           this.cleanupDonateCache(
                              val2
                           );
                           return val3.segments;
                        } else {
                           TextHelper val17 = list.get(
                              list.size()
                                 - 1
                           );
                           if (!val17.text()
                              .endsWith(
                                 " "
                              )) {
                              list.set(
                                 list.size()
                                    - 1,
                                 new TextHelper(
                                    val17.text()
                                       + " ",
                                    val17.color()
                                 )
                              );
                           }

                           val3.segments = List.copyOf(
                              list
                           );
                           val3.nextUpdateAt = val2
                              + 1000L;
                           this.cleanupDonateCache(
                              val2
                           );
                           return val3.segments;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void appendTextSegments(
      List<TextHelper> val,
      Text val2
   ) {
      val2.visit(
         (val3, val4) -> {
            if (val4
                  != null
               && !val4.isEmpty()
               )
             {
               int color3 = val3.getColor()
                     != null
                  ? val3.getColor()
                     .getRgb()
                  : 16777215;
               this.appendColoredSegments(
                  val,
                  val4.replace(
                        (char)10,
                        (char)32
                     )
                     .replace(
                        (char)13,
                        (char)32
                     ),
                  color3
               );
               return Optional.empty();
            } else {
               return Optional.empty();
            }
         },
         Style.EMPTY
      );
   }

   private List<TextHelper> getStyledTextSegments(
      Text val,
      int val2
   ) {
      List val3 = new ArrayList<>();
      if (val
         != null) {
         this.appendTextSegments(
            val3,
            val
         );
      }

      if (val3.isEmpty()
         && val
            != null
         && !val.getString()
            .isEmpty()) {
         val3.add(
            new TextHelper(
               val.getString(),
               val2
            )
         );
      }

      return val3;
   }

   private void appendColoredSegments(
      List<TextHelper> val,
      String val2,
      int val3
   ) {
      if (val2
            != null
         && !val2.isEmpty()
         )
       {
         int n = val3;
         StringBuilder val4 = new StringBuilder();
         int n2 = val3;
         int n3 = 0;

         while (
            n3
               < val2.length()
         ) {
            int n4 = val2.codePointAt(
               n3
            );
            int count = Character.charCount(
               n4
            );
            if (n4
                  == 167
               && n3
                     + count
                  < val2.length()
               )
             {
               this.flushSegment(
                  val,
                  val4,
                  n2
               );
               char val5 = Character.toLowerCase(
                  val2.charAt(
                     n3
                        + count
                  )
               );
               Integer val6 = this.sectionColorToRgb(
                  val5
               );
               if (val6
                  != null) {
                  n = val6;
               } else if (val5
                  == 114) {
                  n = val3;
               }

               n2 = n;
               n3 += count
                  + 1;
            } else {
               String str = ReplacecodepointHelper.replaceCodePoint(
                  n4
               );
               if (str
                  == null) {
                  if (val4.length()
                        > 0
                     && n2
                        != n
                     )
                   {
                     this.flushSegment(
                        val,
                        val4,
                        n2
                     );
                  }

                  n2 = n;
                  val4.appendCodePoint(
                     n4
                  );
                  n3 += count;
               } else {
                  this.flushSegment(
                     val,
                     val4,
                     n2
                  );
                  int n5 = Math.max(
                     1,
                     str.length()
                  );

                  for (int i = 0;
                     i
                        < str.length();
                     i++
                  ) {
                     int color3 = ReplacecodepointHelper.getGradientColorForReplacement(
                        n4,
                        i,
                        n5,
                        1.0F,
                        n
                     );
                     if (val4.length()
                           > 0
                        && n2
                           != color3
                        )
                      {
                        this.flushSegment(
                           val,
                           val4,
                           n2
                        );
                     }

                     n2 = color3;
                     val4.append(
                        str.charAt(
                           i
                        )
                     );
                  }

                  n3 += count;
               }
            }
         }

         this.flushSegment(
            val,
            val4,
            n2
         );
      }
   }

   private void flushSegment(
      List<TextHelper> val,
      StringBuilder val2,
      int val3
   ) {
      if (!val2.isEmpty()
         )
       {
         val.add(
            new TextHelper(
               val2.toString(),
               val3
            )
         );
         val2.setLength(
            0
         );
      }
   }

   private Integer sectionColorToRgb(
      char val
   ) {
      return switch (val) {
         case '0' -> 0;
         case '1' -> 170;
         case '2' -> 43520;
         case '3' -> 43690;
         case '4' -> 11141120;
         case '5' -> 11141290;
         case '6' -> 16755200;
         case '7' -> 11184810;
         case '8' -> 5592405;
         case '9' -> 5592575;
         default -> null;
         case 'a' -> 5635925;
         case 'b' -> 5636095;
         case 'c' -> 16733525;
         case 'd' -> 16733695;
         case 'e' -> 16777045;
         case 'f' -> 16777215;
      };
   }

   private void cleanupDonateCache(
      long val
   ) {
      if (val
            >= this.nextDonateCacheCleanupAt
         && mc.world != null) {
         this.nextDonateCacheCleanupAt = val
            + 2000L;
         this.donateCache
            .entrySet()
            .removeIf(
               val2 -> mc.world
                        .getPlayerByUuid(
                           val2.getKey()
                        )
                     == null
            );
      }
   }

   private Box getInterpolatedBox(
      Entity val,
      float val2
   ) {
      double d2 = MathHelper.lerp(
         (double)val2,
         val.lastRenderX,
         val.getX()
      );
      double d3 = MathHelper.lerp(
         (double)val2,
         val.lastRenderY,
         val.getY()
      );
      double d4 = MathHelper.lerp(
         (double)val2,
         val.lastRenderZ,
         val.getZ()
      );
      double d5 = d2
         - val.getX();
      double d6 = d3
         - val.getY();
      double d7 = d4
         - val.getZ();
      return val.getBoundingBox()
         .offset(
            d5,
            d6,
            d7
         )
         .expand(0.05);
   }

   private MinxHelper projectBox(
      Box val
   ) {
      double d2 = Double.POSITIVE_INFINITY;
      double d3 = Double.POSITIVE_INFINITY;
      double d4 = Double.NEGATIVE_INFINITY;
      double d5 = Double.NEGATIVE_INFINITY;
      boolean flag = false;

      for (int i = 0;
         i
            < 2;
         i++
      ) {
         for (int i2 = 0;
            i2
               < 2;
            i2++
         ) {
            for (int i3 = 0;
               i3
                  < 2;
               i3++
            ) {
               if (this.projectToScreen(
                  i
                        == 0
                     ? val.minX
                     : val.maxX,
                  i2
                        == 0
                     ? val.minY
                     : val.maxY,
                  i3
                        == 0
                     ? val.minZ
                     : val.maxZ,
                  this.projectedPoint
               )) {
                  flag = true;
                  d2 = Math.min(
                     d2,
                     (double)this.projectedPoint.x
                  );
                  d3 = Math.min(
                     d3,
                     (double)this.projectedPoint.y
                  );
                  d4 = Math.max(
                     d4,
                     (double)this.projectedPoint.x
                  );
                  d5 = Math.max(
                     d5,
                     (double)this.projectedPoint.y
                  );
               }
            }
         }
      }

      if (!flag
         )
       {
         return null;
      } else if (d2
            > (double)(mc.getWindow().getScaledWidth() + 300)
         || d4
            < -300.0) {
         return null;
      } else if (d3
            > (double)(mc.getWindow().getScaledHeight() + 300)
         || d5
            < -300.0) {
         return null;
      } else {
         return !(
                  d4
                        - d2
                     < 2.0
               )
               && !(
                  d5
                        - d3
                     < 2.0
               )
            ? new MinxHelper(
               (float)d2,
               (float)d3,
               (float)d4,
               (float)d5
            )
            : null;
      }
   }

   private boolean projectToScreen(
      double val,
      double val2,
      double val3,
      XHolder_1 val4
   ) {
      this.projectionScratch
         .set(
            (float)(
               val
                  - this.lastCameraPos.x
            ),
            (float)(
               val2
                  - this.lastCameraPos.y
            ),
            (float)(
               val3
                  - this.lastCameraPos.z
            )
         );
      this.projectionScratch.rotate(this.lastInverseCameraRotation);
      this.clipScratch.set(this.projectionScratch.x, this.projectionScratch.y, this.projectionScratch.z, 1.0F);
      this.lastProjectionMatrix.transform(this.clipScratch);
      float f2 = this.clipScratch
         .w;
      if (f2
         <= 1.0E-5F) {
         return false;
      } else {
         float f3 = this.clipScratch
               .x
            / f2;
         float f4 = this.clipScratch
               .y
            / f2;
         float f5 = this.clipScratch
               .z
            / f2;
         float f6 = (
               f3
                     * 0.5F
                  + 0.5F
            )
            * (float)this.lastScaledWidth;
         float f7 = (
               1.0F
                  - (
                     f4
                           * 0.5F
                        + 0.5F
                  )
            )
            * (float)this.lastScaledHeight;
         if (Float.isNaN(
               f6
            )
            || Float.isNaN(
               f7
            )) {
            return false;
         } else if (!Float.isInfinite(
               f6
            )
            && !Float.isInfinite(
               f7
            )) {
            val4.x = f6;
            val4.y = f7;
            val4.z = f5;
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean projectEntityAnchor(
      Entity val,
      double val2,
      XHolder_1 val3
   ) {
      double d2 = MathHelper.lerp(
         (double)this.lastTickDelta,
         val.lastRenderX,
         val.getX()
      );
      double d3 = MathHelper.lerp(
            (double)this.lastTickDelta,
            val.lastRenderY,
            val.getY()
         )
         + val2;
      double d4 = MathHelper.lerp(
         (double)this.lastTickDelta,
         val.lastRenderZ,
         val.getZ()
      );
      return this.projectToScreen(
         d2,
         d3,
         d4,
         val3
      );
   }

   private boolean isInFirstPerson() {
      return mc != null && mc.gameRenderer != null && !mc.gameRenderer.getCamera().isThirdPerson();
   }

   private boolean shouldProcess3DEntity(
      Entity val
   ) {
      if (val
            == null
         || val.isRemoved()
         || val instanceof ArmorStandEntity
         )
       {
         return false;
      } else if (val instanceof PlayerEntity val2
         )
       {
         return this.shouldProcessPlayer(
            val2,
            false
         );
      } else if (!(
         val instanceof ItemEntity val3
      )) {
         if (!(
               val instanceof LivingEntity val4
            )
            || !val4.isAlive()
            )
          {
            return false;
         }

         if (this.isAnimalEntity(
            val
         )) {
            return this.targetAnimals.isState();
         } else {
            return this.isMobEntity(
                  val
               )
               ? this.targetMobs.isState()
               : false;
         }
      } else {
         return this.targetItems.isState()
            && val3.isAlive();
      }
   }

   private boolean shouldProcess2DPlayer(
      PlayerEntity val
   ) {
      return this.shouldProcessPlayer(
         val,
         true
      );
   }

   private boolean shouldProcessLiving2D(
      LivingEntity val
   ) {
      return this.shouldProcess3DEntity(
         val
      );
   }

   private boolean shouldProcessItem2D(
      ItemEntity val
   ) {
      return this.targetItems.isState()
         && val.isAlive();
   }

   private boolean shouldProcessPlayer(
      PlayerEntity val,
      boolean val2
   ) {
      if (!this.targetPlayers.isState()) {
         return false;
      } else if (val
            == null
         || !val.isAlive()
         )
       {
         return false;
      } else {
         return val
                  == mc.player
               && this.isInFirstPerson()
            ? false
            : !val2
               || !val.isInvisible()
               || this.canRenderInvisiblePlayer(
                  val
               );
      }
   }

   private boolean isTargetEnabled(
      int val
   ) {
      return this.targets.getSettings().size()
            > val
         && this.targets
            .getSettings()
            .get(
               val
            )
            .isState();
   }

   private boolean isAnimalEntity(
      Entity val
   ) {
      return val instanceof AnimalEntity
         || val instanceof WaterAnimalEntity
         || val instanceof AmbientEntity;
   }

   private boolean isMobEntity(
      Entity val
   ) {
      return val instanceof MobEntity
         && !this.isAnimalEntity(
            val
         )
         && !(
            val instanceof PlayerEntity
         );
   }

   private boolean canRenderInvisiblePlayer(
      PlayerEntity val
   ) {
      SeeInvisibles val2 = ModuleManager.seeInvisibles;
      return val2
            != null
         && val2.shouldRenderInvisible(
            val
         );
   }

   private boolean isOutsideRenderDistance(
      Entity val
   ) {
      int n = (Integer)mc.options
         .getViewDistance()
         .getValue();
      double d2 = Math.max(
         48.0,
         (double)n
               * 16.0
            + 16.0
      );
      return val.squaredDistanceTo(
            this.lastCameraPos
         )
         > d2
            * d2;
   }

   private void render3DBox(
      MatrixStack val,
      Entity val2,
      float val3
   ) {
      Box box;
      boolean var10000;
      label23: {
         Vec3d vec = mc.gameRenderer
            .getCamera()
            .getPos();
         double d2 = MathHelper.lerp(
               (double)val3,
               val2.lastRenderX,
               val2.getX()
            )
            - vec.x;
         double d3 = MathHelper.lerp(
               (double)val3,
               val2.lastRenderY,
               val2.getY()
            )
            - vec.y;
         double d4 = MathHelper.lerp(
               (double)val3,
               val2.lastRenderZ,
               val2.getZ()
            )
            - vec.z;
         box = val2.getBoundingBox()
            .offset(
               -val2.getX(),
               -val2.getY(),
               -val2.getZ()
            );
         val.push();
         val.translate(
            d2,
            d3,
            d4
         );
         if (val2 instanceof PlayerEntity val4
            && AspectDLC.INSTANCE.friendStorage != null
            && AspectDLC.INSTANCE
               .friendStorage
               .isFriend(
                  val4.getName()
                     .getString()
               )) {
            var10000 = true;
            break label23;
         }

         var10000 = false;
      }

      boolean flag = var10000;
      int n;
      if (flag
         )
       {
         n = ColorUtil.rgba(
            84,
            255,
            84,
            255
         );
      } else {
         n = this.getStableThemeColor();
      }

      n = this.applyEntityHurtTint(
         val2,
         n
      );
      float f2 = ColorUtil.redf(
         n
      );
      float f3 = ColorUtil.greenf(
         n
      );
      float f4 = ColorUtil.bluef(
         n
      );
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(1.5F);
      Matrix4f matrix = val.peek()
         .getPositionMatrix();
      Tessellator tessellator = Tessellator.getInstance();
      if (this.boxFilled.isState()) {
         this.drawFilledBox(
            tessellator,
            matrix,
            box,
            f2,
            f3,
            f4,
            0.23F
         );
      }

      this.drawBoxOutline(
         tessellator,
         matrix,
         box,
         f2,
         f3,
         f4,
         1.0F
      );
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      val.pop();
   }

   private int applyEntityHurtTint(
      Entity val,
      int val2
   ) {
      if (val instanceof LivingEntity val3
         && this.hurtTint.isState()) {
         float f2 = MathHelper.clamp(
            (float)val3.hurtTime
               / 10.0F,
            0.0F,
            1.0F
         );
         float y2 = this.entityHurtTintProgress
            .getOrDefault(
               val.getId(),
               0.0F
            );
         float f3 = f2
               > y2
            ? 0.38F
            : 0.16F;
         y2 += (
               f2
                  - y2
            )
            * f3;
         if (y2
               <= 0.003F
            && f2
               <= 0.0F) {
            this.entityHurtTintProgress
               .remove(
                  val.getId()
               );
            return val2;
         }

         this.entityHurtTintProgress
            .put(
               val.getId(),
               y2
            );
         int color3 = ColorUtil.rgba(
            255,
            70,
            70,
            255
         );
         return ColorUtil.interpolateColor(
            val2,
            color3,
            y2
         );
      }

      this.entityHurtTintProgress
         .remove(
            val.getId()
         );
      return val2;
   }

   private void drawPlayerMaskBox(
      MatrixStack val,
      Entity val2,
      float val3
   ) {
      Vec3d vec = mc.gameRenderer
         .getCamera()
         .getPos();
      double d2 = MathHelper.lerp(
            (double)val3,
            val2.lastRenderX,
            val2.getX()
         )
         - vec.x;
      double d3 = MathHelper.lerp(
            (double)val3,
            val2.lastRenderY,
            val2.getY()
         )
         - vec.y;
      double d4 = MathHelper.lerp(
            (double)val3,
            val2.lastRenderZ,
            val2.getZ()
         )
         - vec.z;
      Box box = val2.getBoundingBox()
         .offset(
            -val2.getX(),
            -val2.getY(),
            -val2.getZ()
         );
      val.push();
      val.translate(
         d2,
         d3,
         d4
      );
      this.drawMaskBox(
         Tessellator.getInstance(),
         val.peek()
            .getPositionMatrix(),
         box
      );
      val.pop();
   }

   private void drawMaskBox(
      Tessellator val,
      Matrix4f val2,
      Box val3
   ) {
      BufferBuilder buffer = val.begin(
         DrawMode.QUADS, VertexFormats.POSITION_COLOR
      );
      float x2 = (float)val3.minX;
      float y2 = (float)val3.minY;
      float f2 = (float)val3.minZ;
      float x3 = (float)val3.maxX;
      float x4 = (float)val3.maxY;
      float x5 = (float)val3.maxZ;
      int n = -1;
      buffer.vertex(
            val2,
            x2,
            y2,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            f2
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            x5
         )
         .color(
            n
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            x5
         )
         .color(
            n
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void renderShaderBoxes() {
      if (this.hasShaderMask && this.maskBuffer != null) {
         boolean flag = this.isThreadMode();
         ShaderProgram val = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.blockOverlay);
         if (val
            != null) {
            int color3 = this.getStableThemeColor();
            int n = this.isRainbowTheme()
               ? ColorUtil.getThemeColor(180)
               : color3;
            mc.getFramebuffer().beginWrite(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.setShader(ClientShaders.blockOverlay);
            RenderSystem.setShaderTexture(0, this.maskBuffer.getColorAttachment());
            this.setUniform(
               val,
               "texelSize",
               1.0F / (float)Math.max(1, mc.getWindow().getFramebufferWidth()),
               1.0F / (float)Math.max(1, mc.getWindow().getFramebufferHeight())
            );
            this.setUniform(
               val,
               "color",
               ColorUtil.redf(
                  color3
               ),
               ColorUtil.greenf(
                  color3
               ),
               ColorUtil.bluef(
                  color3
               )
            );
            this.setUniform(
               val,
               "color2",
               ColorUtil.redf(
                  n
               ),
               ColorUtil.greenf(
                  n
               ),
               ColorUtil.bluef(
                  n
               )
            );
            this.setUniform(
               val,
               "time",
               (float)(System.currentTimeMillis() % 100000L) / 1000.0F
            );
            this.setUniform(
               val,
               "speed",
               this.waveSpeed.get()
            );
            this.setUniform(
               val,
               "scale",
               this.waveScale.get()
            );
            this.setUniform(
               val,
               "outline",
               this.outline.get()
            );
            this.setUniform(
               val,
               "glow",
               flag
                  ? 0.0F
                  : this.glow.get()
            );
            this.setUniform(
               val,
               "fill",
               flag
                  ? 0.0F
                  : this.fill.get()
            );
            this.setUniform(
               val,
               "alpha",
               flag
                  ? 1.0F
                  : this.alpha.get()
            );
            this.setUniform(
               val,
               "outlineOnly",
               flag
                  ? 1.0F
                  : 0.0F
            );
            this.drawFullscreenQuad();
            if (this.glow.get() > 0.001F) {
               int n2 = this.runKawaseBloom(
                  Math.max(
                     3,
                     Math.min(
                        8,
                        4 + Math.round(this.outline.get() * 0.7F)
                     )
                  )
               );
               ShaderProgram val2 = mc.getShaderLoader()
                  .getOrCreateProgram(ClientShaders.shaderHandsGlow);
               if (val2
                  != null) {
                  RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
                  RenderSystem.setShader(ClientShaders.shaderHandsGlow);
                  RenderSystem.setShaderTexture(
                     0,
                     n2
                  );
                  RenderSystem.setShaderTexture(1, this.maskBuffer.getColorAttachment());
                  this.setUniform(
                     val2,
                     "color",
                     ColorUtil.redf(
                        color3
                     ),
                     ColorUtil.greenf(
                        color3
                     ),
                     ColorUtil.bluef(
                        color3
                     )
                  );
                  this.setUniform(
                     val2,
                     "color2",
                     ColorUtil.redf(
                        n
                     ),
                     ColorUtil.greenf(
                        n
                     ),
                     ColorUtil.bluef(
                        n
                     )
                  );
                  this.setUniform(
                     val2,
                     "exposure",
                     1.0F + this.glow.get() * 1.8F
                  );
                  this.drawFullscreenQuad();
               }
            }

            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.setShaderTexture(1, 0);
            mc.getFramebuffer().beginWrite(true);
         }
      }
   }

   private void renderShaderBoxesWorldPass() {
      if (this.isPostBoxMode()) {
         Matrix4f matrix = new Matrix4f(
            RenderSystem.getProjectionMatrix()
         );
         float x2 = (float)Math.max(
            mc.getWindow().getScaledWidth(), 1
         );
         float x3 = (float)Math.max(
            mc.getWindow().getScaledHeight(), 1
         );
         Matrix4f matrix2 = new Matrix4f()
            .setOrtho(
               0.0F,
               x2,
               x3,
               0.0F,
               -1000.0F,
               1000.0F
            );
         RenderSystem.setProjectionMatrix(
            matrix2,
            ProjectionType.ORTHOGRAPHIC
         );

         try {
            this.renderShaderBoxes();
         } finally {
            RenderSystem.setProjectionMatrix(
               matrix,
               ProjectionType.ORTHOGRAPHIC
            );
         }
      }
   }

   private int runKawaseBloom(
      int val
   ) {
      this.ensureBloomBuffers(
         val
      );
      if (this.bloomBuffers.isEmpty()) {
         return this.maskBuffer.getColorAttachment();
      } else {
         int n = this.maskBuffer
            .getColorAttachment();
         ShaderProgram val2 = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.shaderHandsKawaseDown);
         ShaderProgram val3 = mc.getShaderLoader()
            .getOrCreateProgram(ClientShaders.shaderHandsKawaseUp);
         if (val2
               != null
            && val3
               != null) {
            for (int i = 0;
               i
                  < val;
               i++
            ) {
               Framebuffer val4 = this.bloomBuffers
                  .get(
                     i
                  );
               val4.setClearColor(
                  0.0F, 0.0F, 0.0F, 0.0F
               );
               val4.clear();
               val4.beginWrite(
                  true
               );
               RenderSystem.setShader(ClientShaders.shaderHandsKawaseDown);
               RenderSystem.setShaderTexture(
                  0,
                  n
               );
               this.setHandsKawaseUniforms(
                  val2,
                  val4.textureWidth,
                  val4.textureHeight,
                  1.0F
                     + (float)i
               );
               this.drawFullscreenQuad();
               n = val4.getColorAttachment();
            }

            for (int i2 = val
                  - 1;
               i2
                  >= 1;
               i2--
            ) {
               Framebuffer val5 = this.bloomBuffers
                  .get(
                     i2
                        - 1
                  );
               val5.setClearColor(
                  0.0F, 0.0F, 0.0F, 0.0F
               );
               val5.clear();
               val5.beginWrite(
                  true
               );
               RenderSystem.setShader(ClientShaders.shaderHandsKawaseUp);
               RenderSystem.setShaderTexture(
                  0,
                  n
               );
               this.setHandsKawaseUniforms(
                  val3,
                  val5.textureWidth,
                  val5.textureHeight,
                  1.0F
                     + (float)i2
               );
               this.setUniform(
                  val3,
                  "color",
                  1.0F,
                  1.0F,
                  1.0F
               );
               this.drawFullscreenQuad();
               n = val5.getColorAttachment();
            }

            mc.getFramebuffer().beginWrite(true);
            return n;
         } else {
            return n;
         }
      }
   }

   private void ensureMaskBuffer() {
      int n = mc.getWindow()
         .getFramebufferWidth();
      int n2 = mc.getWindow()
         .getFramebufferHeight();
      if (this.maskBuffer == null
         || this.maskWidth
            != n
         || this.maskHeight
            != n2
         )
       {
         if (this.maskBuffer != null) {
            this.maskBuffer.delete();
         }

         this.maskBuffer = new SimpleFramebuffer(
            n,
            n2,
            true
         );
         this.maskWidth = n;
         this.maskHeight = n2;

         for (Framebuffer val : this.bloomBuffers) {
            val.delete();
         }

         this.bloomBuffers.clear();
      }
   }

   private void ensureBloomBuffers(
      int val
   ) {
      while (
         this.bloomBuffers.size()
            > val
      ) {
         int n = this.bloomBuffers
               .size()
            - 1;
         this.bloomBuffers
            .get(
               n
            )
            .delete();
         this.bloomBuffers
            .remove(
               n
            );
      }

      for (int i = 0;
         i
            < val;
         i++
      ) {
         int n2 = Math.max(
            2,
            this.maskWidth
               >> i
                  + 1
         );
         int n3 = Math.max(
            2,
            this.maskHeight
               >> i
                  + 1
         );
         if (i
            >= this.bloomBuffers.size()) {
            this.bloomBuffers
               .add(
                  new SimpleFramebuffer(
                     n2,
                     n3,
                     false
                  )
               );
         } else {
            Framebuffer val2 = this.bloomBuffers
               .get(
                  i
               );
            if (val2.textureWidth
                  != n2
               || val2.textureHeight
                  != n3
               )
             {
               val2.delete();
               this.bloomBuffers
                  .set(
                     i,
                     new SimpleFramebuffer(
                        n2,
                        n3,
                        false
                     )
                  );
            }
         }
      }
   }

   private void copyMainDepthToMask() {
      if (this.maskBuffer != null) {
         int n = GL11.glGetInteger(
            36010
         );
         int n2 = GL11.glGetInteger(
            36006
         );
         int n3 = mc.getWindow()
            .getFramebufferWidth();
         int n4 = mc.getWindow()
            .getFramebufferHeight();
         GL30.glBindFramebuffer(36008, mc.getFramebuffer().fbo);
         GL30.glBindFramebuffer(36009, this.maskBuffer.fbo);
         GL30.glBlitFramebuffer(
            0,
            0,
            n3,
            n4,
            0,
            0,
            n3,
            n4,
            256,
            9728
         );
         GL30.glBindFramebuffer(
            36008,
            n
         );
         GL30.glBindFramebuffer(
            36009,
            n2
         );
      }
   }

   private void setUniform(
      ShaderProgram val,
      String val2,
      float val3
   ) {
      GlUniform val4 = val.getUniform(
         val2
      );
      if (val4
         != null) {
         val4.set(
            val3
         );
      }
   }

   private void setUniform(
      ShaderProgram val,
      String val2,
      float val3,
      float val4
   ) {
      GlUniform val5 = val.getUniform(
         val2
      );
      if (val5
         != null) {
         val5.set(
            val3,
            val4
         );
      }
   }

   private void setUniform(
      ShaderProgram val,
      String val2,
      float val3,
      float val4,
      float val5
   ) {
      GlUniform val6 = val.getUniform(
         val2
      );
      if (val6
         != null) {
         val6.set(
            val3,
            val4,
            val5
         );
      }
   }

   private void setHandsKawaseUniforms(
      ShaderProgram val,
      int val2,
      int val3,
      float val4
   ) {
      this.setUniform(
         val,
         "uSize",
         (float)Math.max(
            1,
            val2
         ),
         (float)Math.max(
            1,
            val3
         )
      );
      this.setUniform(
         val,
         "uOffset",
         val4,
         val4
      );
      this.setUniform(
         val,
         "uHalfPixel",
         0.5F
            / (float)Math.max(
               1,
               val2
            ),
         0.5F
            / (float)Math.max(
               1,
               val3
            )
      );
   }

   private void drawFullscreenQuad() {
      float x2 = (float)Math.max(
         mc.getWindow().getScaledWidth(), 1
      );
      float x3 = (float)Math.max(
         mc.getWindow().getScaledHeight(), 1
      );
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      buffer.vertex(
            0.0F, 0.0F, 0.0F
         )
         .texture(0.0F, 1.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            0.0F,
            x3,
            0.0F
         )
         .texture(0.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            x2,
            x3,
            0.0F
         )
         .texture(1.0F, 0.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.vertex(
            x2,
            0.0F,
            0.0F
         )
         .texture(1.0F, 1.0F)
         .color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private boolean isPostBoxMode() {
      return false;
   }

   private boolean isThreadMode() {
      return false;
   }

   private boolean isRainbowTheme() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null) {
         ThemeColors val = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         if (val
            != null) {
            if ("Rainbow"
               .equals(
                  val.getName()
               )) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private int getStableThemeColor() {
      if (AspectDLC.INSTANCE != null && AspectDLC.INSTANCE.themeStorage != null && AspectDLC.INSTANCE.themeStorage.getThemes() != null) {
         ThemeColors val = AspectDLC.INSTANCE
            .themeStorage
            .getThemes()
            .getTheme();
         return val
                  != null
               && val.color
                  != null
               && val.color.length
                  != 0
            ? val.color[0]
            : ColorUtil.getThemeColor(0);
      } else {
         return ColorUtil.getThemeColor(0);
      }
   }

   private void renderThreadWeb(
      MatrixStack val,
      Entity val2,
      float val3
   ) {
      Vec3d vec = mc.gameRenderer
         .getCamera()
         .getPos();
      double d2 = MathHelper.lerp(
            (double)val3,
            val2.lastRenderX,
            val2.getX()
         )
         - vec.x;
      double d3 = MathHelper.lerp(
            (double)val3,
            val2.lastRenderY,
            val2.getY()
         )
         - vec.y;
      double d4 = MathHelper.lerp(
            (double)val3,
            val2.lastRenderZ,
            val2.getZ()
         )
         - vec.z;
      Box box = val2.getBoundingBox()
         .offset(
            -val2.getX(),
            -val2.getY(),
            -val2.getZ()
         );
      val.push();
      val.translate(
         d2,
         d3,
         d4
      );
      this.drawAnimatedWeb(
         val.peek()
            .getPositionMatrix(),
         box,
         (long)val2.getId()
      );
      val.pop();
   }

   private void drawAnimatedWeb(
      Matrix4f val,
      Box val2,
      long val3
   ) {
      int n = 5;
      int n2 = 18;
      float f2 = (float)(
            System.currentTimeMillis() % 100000L
         )
         / 1000.0F
         * this.lineSpeed.get();
      float f3 = 0.0025F;
      float f4 = 0.06F
         + this.lineJitter.get() * 0.2F;
      int n3 = Math.max(
         20,
         Math.min(255, (int)(this.alpha.get() * 210.0F))
      );
      int color3 = this.getStableThemeColor();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      this.drawFilledBoxInt(
         val,
         val2,
         ColorUtil.setAlphaColor(
            color3,
            (int)(this.alpha.get() * this.fill.get() * 170.0F)
         )
      );

      for (int i = 0;
         i
            < 6;
         i++
      ) {
         int[] val4 = this.faceNeighbors(
            i
         );

         for (int i2 = 0;
            i2
               < n;
            i2++
         ) {
            int n4 = i
                  * 1000
               + i2
                  * 53;
            int n5 = val4[i2
               % val4.length];
            double d2 = (double)f2
                  * (
                     0.95
                        + this.rand01(
                              val3,
                              n4
                                 + 1
                           )
                           * 0.55
                  )
               + (double)i2
                  * 0.83
               + (double)i
                  * 1.11;
            double d3 = this.clamp01(
               0.5
                  + Math.sin(
                        d2
                              * 1.37
                           + this.rand01(
                                 val3,
                                 n4
                                    + 2
                              )
                              * 6.2831853
                     )
                     * 0.38
            );
            Vec3d vec = this.edgePoint(
               val2,
               i,
               n5,
               d3,
               0.0015
            );
            Vec3d vec2 = this.facePoint(
               val2,
               i,
               this.clamp01(
                  0.5
                     + (
                           this.rand01(
                                 val3,
                                 n4
                                    + 3
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               this.clamp01(
                  0.5
                     + (
                           this.rand01(
                                 val3,
                                 n4
                                    + 4
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               0.0015
            );
            Vec3d vec3 = this.facePoint(
               val2,
               n5,
               this.clamp01(
                  0.5
                     + (
                           this.rand01(
                                 val3,
                                 n4
                                    + 5
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               this.clamp01(
                  0.5
                     + (
                           this.rand01(
                                 val3,
                                 n4
                                    + 6
                              )
                              - 0.5
                        )
                        * 0.46
               ),
               0.0015
            );
            Vec3d[] vec4 = this.faceBasis(
               i
            );
            Vec3d[] vec5 = this.faceBasis(
               n5
            );
            Vec3d vec6 = this.faceNormal(
               i
            );
            Vec3d vec7 = this.faceNormal(
               n5
            );
            double d4 = (double)f4
               * (
                  0.7
                     + this.rand01(
                        val3,
                        n4
                           + 7
                     )
               )
               * Math.sin(
                  d2
                        * 1.9
                     + this.rand01(
                           val3,
                           n4
                              + 8
                        )
                        * 6.2831853
               );
            double d5 = (double)f4
               * (
                  0.7
                     + this.rand01(
                        val3,
                        n4
                           + 9
                     )
               )
               * Math.cos(
                  d2
                        * 1.7
                     + this.rand01(
                           val3,
                           n4
                              + 10
                        )
                        * 6.2831853
               );
            Vec3d vec8 = vec.subtract(
               vec2
            );
            Vec3d vec9 = vec2.add(
                  vec8.multiply(
                     0.38
                  )
               )
               .add(
                  vec4[0]
                     .multiply(
                        d4
                     )
               )
               .add(
                  vec4[1]
                     .multiply(
                        -d4
                           * 0.55
                     )
               );
            Vec3d vec10 = vec2.add(
                  vec8.multiply(
                     0.76
                  )
               )
               .add(
                  vec4[0]
                     .multiply(
                        -d4
                           * 0.65
                     )
               )
               .add(
                  vec4[1]
                     .multiply(
                        d4
                           * 0.4
                     )
               );
            Vec3d vec11 = vec3.subtract(
               vec
            );
            Vec3d vec12 = vec.add(
                  vec11.multiply(
                     0.24
                  )
               )
               .add(
                  vec5[0]
                     .multiply(
                        d5
                     )
               )
               .add(
                  vec5[1]
                     .multiply(
                        d5
                           * 0.45
                     )
               );
            Vec3d vec13 = vec.add(
                  vec11.multiply(
                     0.62
                  )
               )
               .add(
                  vec5[0]
                     .multiply(
                        -d5
                           * 0.7
                     )
               )
               .add(
                  vec5[1]
                     .multiply(
                        -d5
                           * 0.35
                     )
               );
            int n6 = Math.max(
               18,
               Math.min(
                  255,
                  (int)(
                     (double)n3
                        * (
                           0.74
                              + 0.26
                                 * Math.sin(
                                    d2
                                       * 2.6
                                 )
                        )
                  )
               )
            );
            int color4 = ColorUtil.setAlphaColor(
               color3,
               n6
            );
            this.drawBezierRibbon(
               val,
               vec2,
               vec9,
               vec10,
               vec,
               vec6,
               n2,
               color4,
               f3
            );
            this.drawBezierRibbon(
               val,
               vec,
               vec12,
               vec13,
               vec3,
               vec7,
               n2,
               color4,
               f3
            );
         }
      }

      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   private Vec3d cubicBezier(
      Vec3d val,
      Vec3d val2,
      Vec3d val3,
      Vec3d val4,
      float val5
   ) {
      double d2 = 1.0
         - (double)val5;
      double d3 = d2
         * d2;
      double d4 = (double)(
         val5
            * val5
      );
      return val.multiply(
            d3
               * d2
         )
         .add(
            val2.multiply(
               3.0
                  * d3
                  * (double)val5
            )
         )
         .add(
            val3.multiply(
               3.0
                  * d2
                  * d4
            )
         )
         .add(
            val4.multiply(
               d4
                  * (double)val5
            )
         );
   }

   private void drawBezierRibbon(
      Matrix4f val,
      Vec3d val2,
      Vec3d val3,
      Vec3d val4,
      Vec3d val5,
      Vec3d val6,
      int val7,
      int val8,
      float val9
   ) {
      Vec3d[] vec = new Vec3d[val7
         + 1];

      for (int i = 0;
         i
            <= val7;
         i++
      ) {
         float f2 = (float)i
            / (float)val7;
         vec[i] = this.cubicBezier(
            val2,
            val3,
            val4,
            val5,
            f2
         );
      }

      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (int i2 = 0;
         i2
            < val7;
         i2++
      ) {
         Vec3d vec2 = vec[i2];
         Vec3d vec3 = vec[i2
            + 1];
         Vec3d vec4 = vec3.subtract(
            vec2
         );
         if (!(
            vec4.lengthSquared()
               < 1.0E-6
         )) {
            Vec3d vec5 = val6.crossProduct(
                  vec4
               )
               .normalize()
               .multiply(
                  (double)val9
               );
            Vec3d vec6 = vec2.add(
               vec5
            );
            Vec3d vec7 = vec2.subtract(
               vec5
            );
            Vec3d vec8 = vec3.add(
               vec5
            );
            Vec3d vec9 = vec3.subtract(
               vec5
            );
            buffer.vertex(
                  val,
                  (float)vec6.x,
                  (float)vec6.y,
                  (float)vec6.z
               )
               .color(
                  val8
               );
            buffer.vertex(
                  val,
                  (float)vec7.x,
                  (float)vec7.y,
                  (float)vec7.z
               )
               .color(
                  val8
               );
            buffer.vertex(
                  val,
                  (float)vec9.x,
                  (float)vec9.y,
                  (float)vec9.z
               )
               .color(
                  val8
               );
            buffer.vertex(
                  val,
                  (float)vec8.x,
                  (float)vec8.y,
                  (float)vec8.z
               )
               .color(
                  val8
               );
         }
      }

      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private int[] faceNeighbors(
      int val
   ) {
      int[] var10000;
      switch (val) {
         case 0:
         case 1:
            var10000 = new int[4];
            var10000[0] = 2;
            var10000[1] = 3;
            var10000[2] = 4;
            var10000[3] = 5;
            break;
         case 2:
         case 3:
            var10000 = new int[4];
            var10000[0] = 0;
            var10000[1] = 1;
            var10000[2] = 4;
            var10000[3] = 5;
            break;
         default:
            var10000 = new int[4];
            var10000[0] = 0;
            var10000[1] = 1;
            var10000[2] = 2;
            var10000[3] = 3;
      }

      return var10000;
   }

   private Vec3d[] faceBasis(
      int val
   ) {
      Vec3d[] var10000;
      switch (val) {
         case 0:
         case 1:
            var10000 = new Vec3d[2];
            var10000[0] = new Vec3d(1.0, 0.0, 0.0);
            var10000[1] = new Vec3d(0.0, 0.0, 1.0);
            break;
         case 2:
         case 3:
            var10000 = new Vec3d[2];
            var10000[0] = new Vec3d(1.0, 0.0, 0.0);
            var10000[1] = new Vec3d(0.0, 1.0, 0.0);
            break;
         default:
            var10000 = new Vec3d[2];
            var10000[0] = new Vec3d(0.0, 0.0, 1.0);
            var10000[1] = new Vec3d(0.0, 1.0, 0.0);
      }

      return var10000;
   }

   private Vec3d faceNormal(
      int val
   ) {
      return switch (val) {
         case 0 -> new Vec3d(0.0, 1.0, 0.0);
         case 1 -> new Vec3d(0.0, -1.0, 0.0);
         case 2 -> new Vec3d(0.0, 0.0, -1.0);
         case 3 -> new Vec3d(0.0, 0.0, 1.0);
         case 4 -> new Vec3d(-1.0, 0.0, 0.0);
         default -> new Vec3d(1.0, 0.0, 0.0);
      };
   }

   private Vec3d edgePoint(
      Box val,
      int val2,
      int val3,
      double val4,
      double val5
   ) {
      double d2 = Double.NaN;
      double d3 = Double.NaN;
      double d4 = Double.NaN;
      double[] val6 = this.faceFixedCoords(
         val,
         val2,
         val5
      );
      if (!Double.isNaN(
         val6[0]
      )) {
         d2 = val6[0];
      }

      if (!Double.isNaN(
         val6[1]
      )) {
         d3 = val6[1];
      }

      if (!Double.isNaN(
         val6[2]
      )) {
         d4 = val6[2];
      }

      double[] val7 = this.faceFixedCoords(
         val,
         val3,
         val5
      );
      if (!Double.isNaN(
         val7[0]
      )) {
         d2 = val7[0];
      }

      if (!Double.isNaN(
         val7[1]
      )) {
         d3 = val7[1];
      }

      if (!Double.isNaN(
         val7[2]
      )) {
         d4 = val7[2];
      }

      double d5 = this.clamp01(
         val4
      );
      if (Double.isNaN(
         d2
      )) {
         d2 = this.lerp(
            val.minX,
            val.maxX,
            d5
         );
      }

      if (Double.isNaN(
         d3
      )) {
         d3 = this.lerp(
            val.minY,
            val.maxY,
            d5
         );
      }

      if (Double.isNaN(
         d4
      )) {
         d4 = this.lerp(
            val.minZ,
            val.maxZ,
            d5
         );
      }

      return new Vec3d(
         d2,
         d3,
         d4
      );
   }

   private double[] faceFixedCoords(
      Box val,
      int val2,
      double val3
   ) {
      double[] var10000;
      switch (val2) {
         case 0:
            var10000 = new double[3];
            var10000[0] = Double.NaN;
            var10000[1] = val.maxY
               - val3;
            var10000[2] = Double.NaN;
            break;
         case 1:
            var10000 = new double[3];
            var10000[0] = Double.NaN;
            var10000[1] = val.minY
               + val3;
            var10000[2] = Double.NaN;
            break;
         case 2:
            var10000 = new double[3];
            var10000[0] = Double.NaN;
            var10000[1] = Double.NaN;
            var10000[2] = val.minZ
               + val3;
            break;
         case 3:
            var10000 = new double[3];
            var10000[0] = Double.NaN;
            var10000[1] = Double.NaN;
            var10000[2] = val.maxZ
               - val3;
            break;
         case 4:
            var10000 = new double[3];
            var10000[0] = val.minX
               + val3;
            var10000[1] = Double.NaN;
            var10000[2] = Double.NaN;
            break;
         default:
            var10000 = new double[3];
            var10000[0] = val.maxX
               - val3;
            var10000[1] = Double.NaN;
            var10000[2] = Double.NaN;
      }

      return var10000;
   }

   private Vec3d facePoint(
      Box val,
      int val2,
      double val3,
      double val4,
      double val5
   ) {
      val3 = this.clamp01(
         val3
      );
      val4 = this.clamp01(
         val4
      );

      return switch (val2) {
         case 0 -> new Vec3d(
         this.lerp(
            val.minX,
            val.maxX,
            val3
         ),
         val.maxY
            - val5,
         this.lerp(
            val.minZ,
            val.maxZ,
            val4
         )
      );
         case 1 -> new Vec3d(
         this.lerp(
            val.minX,
            val.maxX,
            val3
         ),
         val.minY
            + val5,
         this.lerp(
            val.minZ,
            val.maxZ,
            val4
         )
      );
         case 2 -> new Vec3d(
         this.lerp(
            val.minX,
            val.maxX,
            val3
         ),
         this.lerp(
            val.minY,
            val.maxY,
            val4
         ),
         val.minZ
            + val5
      );
         case 3 -> new Vec3d(
         this.lerp(
            val.minX,
            val.maxX,
            val3
         ),
         this.lerp(
            val.minY,
            val.maxY,
            val4
         ),
         val.maxZ
            - val5
      );
         case 4 -> new Vec3d(
         val.minX
            + val5,
         this.lerp(
            val.minY,
            val.maxY,
            val4
         ),
         this.lerp(
            val.minZ,
            val.maxZ,
            val3
         )
      );
         default -> new Vec3d(
         val.maxX
            - val5,
         this.lerp(
            val.minY,
            val.maxY,
            val4
         ),
         this.lerp(
            val.minZ,
            val.maxZ,
            val3
         )
      );
      };
   }

   private double rand01(
      long val,
      int val2
   ) {
      long val3 = val
         + -7046029254386353131L
            * (
               (long)val2
                  + 1L
            );
      val3 ^= val3
         >>> 30;
      val3 *= -4658895280553007687L;
      val3 ^= val3
         >>> 27;
      val3 *= -7723592293110705685L;
      val3 ^= val3
         >>> 31;
      return (double)(
            val3
               & 16777215L
         )
         / 1.6777216E7;
   }

   private double lerp(
      double val,
      double val2,
      double val3
   ) {
      return val
         + (
               val2
                  - val
            )
            * val3;
   }

   private double clamp01(
      double val
   ) {
      return Math.max(
         0.0,
         Math.min(
            1.0,
            val
         )
      );
   }

   private void drawFilledBoxInt(
      Matrix4f val,
      Box val2,
      int val3
   ) {
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.minX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.minY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.maxZ
         )
         .color(
            val3
         );
      buffer.vertex(
            val,
            (float)val2.maxX,
            (float)val2.maxY,
            (float)val2.minZ
         )
         .color(
            val3
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void drawFilledBox(
      Tessellator val,
      Matrix4f val2,
      Box val3,
      float val4,
      float val5,
      float val6,
      float val7
   ) {
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder buffer = val.begin(
         DrawMode.QUADS, VertexFormats.POSITION_COLOR
      );
      float x2 = (float)val3.minX;
      float y2 = (float)val3.minY;
      float f2 = (float)val3.minZ;
      float x3 = (float)val3.maxX;
      float x4 = (float)val3.maxY;
      float x5 = (float)val3.maxZ;
      buffer.vertex(
            val2,
            x2,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   private void drawBoxOutline(
      Tessellator val,
      Matrix4f val2,
      Box val3,
      float val4,
      float val5,
      float val6,
      float val7
   ) {
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(1.5F);
      BufferBuilder buffer = val.begin(
         DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR
      );
      float x2 = (float)val3.minX;
      float y2 = (float)val3.minY;
      float f2 = (float)val3.minZ;
      float x3 = (float)val3.maxX;
      float x4 = (float)val3.maxY;
      float x5 = (float)val3.maxZ;
      buffer.vertex(
            val2,
            x2,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            f2
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x3,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            y2,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      buffer.vertex(
            val2,
            x2,
            x4,
            x5
         )
         .color(
            val4,
            val5,
            val6,
            val7
         );
      BufferRenderer.drawWithGlobalProgram(
         buffer.end()
      );
   }

   static {
      EFFECT_RU.put("speed", "Скорость");
      EFFECT_RU.put("haste", "Быстрые руки");
      EFFECT_RU.put("strength", "Сила");
      EFFECT_RU.put("jump_boost", "Прыжок");
      EFFECT_RU.put("regeneration", "Регенерация");
      EFFECT_RU.put("resistance", "Сопротивление");
      EFFECT_RU.put("fire_resistance", "Огнестойкость");
      EFFECT_RU.put("water_breathing", "Дыхание воды");
      EFFECT_RU.put("invisibility", "Невидимость");
      EFFECT_RU.put("night_vision", "Ночное зрение");
      EFFECT_RU.put("health_boost", "Доп. здоровье");
      EFFECT_RU.put("absorption", "Поглощение");
      EFFECT_RU.put("saturation", "Насыщение");
      EFFECT_RU.put("glowing", "Свечение");
      EFFECT_RU.put("luck", "Удача");
      EFFECT_RU.put("hero_of_the_village", "Герой деревни");
      EFFECT_RU.put("conduit_power", "Сила русла");
      EFFECT_RU.put("dolphins_grace", "Грация дельфина");
      EFFECT_RU.put("slow_falling", "Медл. падение");
      EFFECT_RU.put("slowness", "Замедление");
      EFFECT_RU.put("mining_fatigue", "Усталость");
      EFFECT_RU.put("instant_damage", "Урон");
      EFFECT_RU.put("nausea", "Тошнота");
      EFFECT_RU.put("blindness", "Слепота");
      EFFECT_RU.put("hunger", "Голод");
      EFFECT_RU.put("weakness", "Слабость");
      EFFECT_RU.put("poison", "Яд");
      EFFECT_RU.put("wither", "Иссушение");
      EFFECT_RU.put("levitation", "Левитация");
      EFFECT_RU.put("unluck", "Неудача");
      EFFECT_RU.put("darkness", "Тьма");
      EFFECT_RU.put("bad_omen", "Злой рок");
      EFFECT_RU.put("instant_health", "Лечение");
   }
}
