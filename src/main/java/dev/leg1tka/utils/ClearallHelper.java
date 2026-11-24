package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;



import dev.leg1tka.modules.combat.*;
import dev.leg1tka.api.events.implement.EventPacket;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;

public class ClearallHelper {
   private static final Map<UUID, Map<Item, GetremainingsecHelper>> trackers = new ConcurrentHashMap<>();
   private static final Map<UUID, ItemHolder> mainstates = new ConcurrentHashMap<>();
   private static final Map<UUID, ItemHolder> offstates = new ConcurrentHashMap<>();
   private static final Map<UUID, ItemHolder_1> usingmap = new ConcurrentHashMap<>();
   private static final Map<Integer, Long> swingtimes = new ConcurrentHashMap<>();
   private static ClientWorld lastworld = null;
   private static Field bossbarsfield = null;

   public static void clearAll() {
      trackers.clear();
      mainstates.clear();
      offstates.clear();
      usingmap.clear();
      swingtimes.clear();
   }

   public static boolean isHealingPotion(
      ItemStack stack
   ) {
      if (stack
            != null
         && !stack.isEmpty()
         )
       {
         Item item = stack.getItem();
         if (item
               != Items.POTION
            && item
               != Items.SPLASH_POTION
            && item
               != Items.LINGERING_POTION) {
            return false;
         } else {
            PotionContentsComponent val = (PotionContentsComponent)stack.get(
               DataComponentTypes.POTION_CONTENTS
            );
            if (val
               != null) {
               for (StatusEffectInstance effect : val.getEffects()) {
                  if (effect.getEffectType()
                     .equals(StatusEffects.INSTANT_HEALTH)) {
                     return true;
                  }
               }

               if (val.potion()
                  .isPresent()) {
                  String name = ((RegistryEntry)val.potion()
                        .get())
                     .getIdAsString()
                     .toLowerCase();
                  if (name.contains(
                     "healing"
                  )) {
                     return true;
                  }

                  if (name.contains(
                     "heal"
                  )) {
                     return true;
                  }

                  if (name.contains(
                     "health"
                  )) {
                     return true;
                  }
               }
            }

            String name2 = stack.getName()
               .getString()
               .toLowerCase();
            if (!name2.contains(
               "исцелен"
            )) {
               if (!name2.contains(
                  "лечен"
               )) {
                  if (!name2.contains(
                     "heal"
                  )) {
                     return false;
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static Field getBossBarsField() {
      if (bossbarsfield != null) {
         return bossbarsfield;
      } else {
         try {
            for (Field val : BossBarHud.class
               .getDeclaredFields()) {
               if (Map.class
                  .isAssignableFrom(
                     val.getType()
                  )) {
                  val.setAccessible(
                     true
                  );
                  bossbarsfield = val;
                  return val;
               }
            }
         } catch (Exception var4) {
         }

         return null;
      }
   }

   public static boolean hasPvpBossBar() {
      MinecraftClient val = MinecraftClient.getInstance();
      if (val
            != null
         && val.inGameHud
            != null) {
         BossBarHud val2 = val.inGameHud
            .getBossBarHud();
         if (val2
            == null) {
            return false;
         } else {
            try {
               Field val3 = getBossBarsField();
               if (val3
                  == null) {
                  return true;
               } else {
                  Map val4 = (Map<?, ?>)val3.get(
                     val2
                  );
                  return val4
                        != null
                     && !val4.isEmpty();
               }
            } catch (Exception var4) {
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static void add(
      PlayerEntity player2,
      Item item,
      String str,
      long time,
      ItemStack stack
   ) {
      if (player2
            != null
         && item
            != null) {
         MinecraftClient val = MinecraftClient.getInstance();
         if (val.player
               == null
            || !player2.getUuid()
               .equals(
                  val.player
                     .getUuid()
               )) {
            Map val2 = trackers.computeIfAbsent(
               player2.getUuid(),
               val3 -> new ConcurrentHashMap<>()
            );
            long time2 = System.currentTimeMillis()
               + time;
            String name = player2.getName()
               .getString();
            val2.put(
               item,
               new GetremainingsecHelper(
                  name,
                  item,
                  str,
                  time2,
                  time,
                  stack
               )
            );
         }
      }
   }

   public static List<GetremainingsecHelper> getCooldowns(
      PlayerEntity player
   ) {
      List<GetremainingsecHelper> list = new ArrayList<>();
      if (player
         == null) {
         return list;
      } else {
         Map<Item, GetremainingsecHelper> val = trackers.get(
            player.getUuid()
         );
         if (val
               != null
            && !val.isEmpty()
            )
          {
            long time = System.currentTimeMillis();

            for (GetremainingsecHelper val2 : val.values()) {
               if (time
                  < val2.expire
                  )
                {
                  list.add(
                     val2
                  );
               }
            }

            return list;
         } else {
            return list;
         }
      }
   }

   public static void onPacket(
      EventPacket event
   ) {
      if (event.getType()
         != EventPacket.Type.SEND) {
         if (!(
               event.getPacket() instanceof GameJoinS2CPacket
            )
            && !(
               event.getPacket() instanceof PlayerRespawnS2CPacket
            )) {
            FtHelper val = FtHelper.INSTANCE;
            if (val
                  != null
               && val.isEnable()
               && val.getEnemyCooldowns()
                  .isState()) {
               if (hasPvpBossBar()) {
                  MinecraftClient val2 = MinecraftClient.getInstance();
                  if (val2.world
                        != null
                     && val2.player
                        != null) {
                     if (event.getPacket() instanceof EntityAnimationS2CPacket entity
                        )
                      {
                        swingtimes.put(
                           entity.getEntityId(),
                           System.currentTimeMillis()
                        );
                     } else if (event.getPacket() instanceof EntityStatusS2CPacket val3
                        && val3.getEntity(
                           val2.world
                        ) instanceof PlayerEntity val4
                        && !val4.getUuid()
                           .equals(
                              val2.player
                                 .getUuid()
                           )) {
                        if (val3.getStatus()
                           == 35) {
                           Item var10001 = Items.TOTEM_OF_UNDYING;
                           add(
                              val4,
                              var10001,
                              "Тотем бессмертия",
                              60000L,
                              new ItemStack(Items.TOTEM_OF_UNDYING)
                           );
                        } else if (val3.getStatus()
                           == 9) {
                           ItemStack stack = val4.getMainHandStack();
                           ItemStack stack2 = val4.getOffHandStack();
                           Item item2 = !stack.isEmpty()
                              ? stack.getItem()
                              : stack2.getItem();
                           boolean flag = isHealingPotion(
                                 stack
                              )
                              || isHealingPotion(
                                 stack2
                              );
                           ItemHolder_1 item3 = usingmap.remove(
                              val4.getUuid()
                           );
                           if (item3
                              != null) {
                              item2 = item3.item;
                              flag = item3.isheal;
                           }

                           if (item2
                              == Items.ENCHANTED_GOLDEN_APPLE) {
                              Item var14 = Items.ENCHANTED_GOLDEN_APPLE;
                              add(
                                 val4,
                                 var14,
                                 "Зачарованное золотое яблоко",
                                 150000L,
                                 new ItemStack(Items.ENCHANTED_GOLDEN_APPLE)
                              );
                           } else if (item2
                              == Items.GOLDEN_APPLE) {
                              Item var15 = Items.GOLDEN_APPLE;
                              add(
                                 val4,
                                 var15,
                                 "Золотое яблоко",
                                 30000L,
                                 new ItemStack(Items.GOLDEN_APPLE)
                              );
                           } else if (item2
                              == Items.CHORUS_FRUIT) {
                              Item var16 = Items.CHORUS_FRUIT;
                              add(
                                 val4,
                                 var16,
                                 "Плод хоруса",
                                 20000L,
                                 new ItemStack(Items.CHORUS_FRUIT)
                              );
                           } else if (flag
                              || item2
                                 == Items.POTION
                              || item2
                                 == Items.SPLASH_POTION
                              || item2
                                 == Items.LINGERING_POTION) {
                              Item var17 = Items.POTION;
                              add(
                                 val4,
                                 var17,
                                 "Зелье исцеления",
                                 20000L,
                                 new ItemStack(Items.POTION)
                              );
                           }
                        }
                     }
                  }
               }
            }
         } else {
            clearAll();
         }
      }
   }

   public static void onUpdate() {
      FtHelper val = FtHelper.INSTANCE;
      if (val
            != null
         && val.isEnable()
         && val.getEnemyCooldowns()
            .isState()) {
         MinecraftClient val2 = MinecraftClient.getInstance();
         if (val2.world
               != null
            && val2.player
               != null) {
            if (val2.world
               != lastworld) {
               lastworld = val2.world;
               clearAll();
            } else {
               boolean flag = hasPvpBossBar();
               if (flag
                  )
                {
                  long time2 = System.currentTimeMillis();

                  for (PlayerEntity player2 : val2.world
                     .getPlayers()) {
                     if (!player2.getUuid()
                        .equals(
                           val2.player
                              .getUuid()
                        )) {
                        UUID val3 = player2.getUuid();
                        int i = player2.getId();
                        ItemStack stack = player2.getMainHandStack();
                        ItemStack stack2 = player2.getOffHandStack();
                        Item item2 = !stack.isEmpty()
                           ? stack.getItem()
                           : Items.AIR;
                        int i2 = !stack.isEmpty()
                           ? stack.getCount()
                           : 0;
                        boolean flag2 = isHealingPotion(
                           stack
                        );
                        Item item3 = !stack2.isEmpty()
                           ? stack2.getItem()
                           : Items.AIR;
                        int i3 = !stack2.isEmpty()
                           ? stack2.getCount()
                           : 0;
                        boolean flag3 = isHealingPotion(
                           stack2
                        );
                        if (player2.isUsingItem()
                           )
                         {
                           ItemStack stack3 = player2.getActiveItem();
                           if (!stack3.isEmpty()
                              )
                            {
                              usingmap.put(
                                 val3,
                                 new ItemHolder_1(
                                    stack3.getItem(),
                                    isHealingPotion(
                                       stack3
                                    ),
                                    time2
                                 )
                              );
                           }
                        } else {
                           ItemHolder_1 item4 = usingmap.remove(
                              val3
                           );
                           if (item4
                                 != null
                              && time2
                                    - item4.time
                                 >= 1000L) {
                              if (item4.item
                                 == Items.ENCHANTED_GOLDEN_APPLE) {
                                 Item var10001 = Items.ENCHANTED_GOLDEN_APPLE;
                                 add(
                                    player2,
                                    var10001,
                                    "Зачарованное золотое яблоко",
                                    150000L,
                                    new ItemStack(Items.ENCHANTED_GOLDEN_APPLE)
                                 );
                              } else if (item4.item
                                 == Items.GOLDEN_APPLE) {
                                 Item var29 = Items.GOLDEN_APPLE;
                                 add(
                                    player2,
                                    var29,
                                    "Золотое яблоко",
                                    30000L,
                                    new ItemStack(Items.GOLDEN_APPLE)
                                 );
                              } else if (item4.item
                                 == Items.CHORUS_FRUIT) {
                                 Item var30 = Items.CHORUS_FRUIT;
                                 add(
                                    player2,
                                    var30,
                                    "Плод хоруса",
                                    20000L,
                                    new ItemStack(Items.CHORUS_FRUIT)
                                 );
                              } else if (item4.isheal
                                 || item4.item
                                    == Items.POTION) {
                                 Item var31 = Items.POTION;
                                 add(
                                    player2,
                                    var31,
                                    "Зелье исцеления",
                                    20000L,
                                    new ItemStack(Items.POTION)
                                 );
                              }
                           }
                        }

                        boolean flag4 = time2
                                 - swingtimes.getOrDefault(
                                    i,
                                    0L
                                 )
                              <= 400L
                           || player2.handSwinging;
                        ItemHolder item5 = mainstates.get(
                           val3
                        );
                        if (item5
                           != null) {
                           if (item5.item
                                 != Items.SNOWBALL
                              || i2
                                    >= item5.count
                                 && (
                                    item2
                                          == Items.SNOWBALL
                                       || !flag4
                                 )) {
                              if (item5.item
                                    != Items.WIND_CHARGE
                                 || i2
                                       >= item5.count
                                    && (
                                       item2
                                             == Items.WIND_CHARGE
                                          || !flag4
                                    )) {
                                 if (item5.item
                                       == Items.GOLDEN_APPLE
                                    && i2
                                       < item5.count
                                    )
                                  {
                                    Item var37 = Items.GOLDEN_APPLE;
                                    add(
                                       player2,
                                       var37,
                                       "Золотое яблоко",
                                       30000L,
                                       new ItemStack(Items.GOLDEN_APPLE)
                                    );
                                 } else if (item5.item
                                       == Items.ENCHANTED_GOLDEN_APPLE
                                    && i2
                                       < item5.count
                                    )
                                  {
                                    Item var36 = Items.ENCHANTED_GOLDEN_APPLE;
                                    add(
                                       player2,
                                       var36,
                                       "Зачарованное золотое яблоко",
                                       150000L,
                                       new ItemStack(Items.ENCHANTED_GOLDEN_APPLE)
                                    );
                                 } else if (item5.item
                                       == Items.CHORUS_FRUIT
                                    && i2
                                       < item5.count
                                    )
                                  {
                                    Item var35 = Items.CHORUS_FRUIT;
                                    add(
                                       player2,
                                       var35,
                                       "Плод хоруса",
                                       20000L,
                                       new ItemStack(Items.CHORUS_FRUIT)
                                    );
                                 } else if (item5.isheal
                                    && (
                                       i2
                                             < item5.count
                                          || item2
                                                == Items.GLASS_BOTTLE
                                             && item5.item
                                                == Items.POTION
                                          || item2
                                                != item5.item
                                             && flag4
                                    )) {
                                    Item var34 = Items.POTION;
                                    add(
                                       player2,
                                       var34,
                                       "Зелье исцеления",
                                       20000L,
                                       new ItemStack(Items.POTION)
                                    );
                                 }
                              } else {
                                 Item var33 = Items.WIND_CHARGE;
                                 add(
                                    player2,
                                    var33,
                                    "Заряд ветра",
                                    30000L,
                                    new ItemStack(Items.WIND_CHARGE)
                                 );
                              }
                           } else {
                              Item var32 = Items.SNOWBALL;
                              add(
                                 player2,
                                 var32,
                                 "Снежок",
                                 20000L,
                                 new ItemStack(Items.SNOWBALL)
                              );
                           }
                        }

                        mainstates.put(
                           val3,
                           new ItemHolder(
                              item2,
                              i2,
                              flag2
                           )
                        );
                        ItemHolder item6 = offstates.get(
                           val3
                        );
                        if (item6
                           != null) {
                           if (item6.item
                                 != Items.SNOWBALL
                              || i3
                                    >= item6.count
                                 && (
                                    item3
                                          == Items.SNOWBALL
                                       || !flag4
                                 )) {
                              if (item6.item
                                    != Items.WIND_CHARGE
                                 || i3
                                       >= item6.count
                                    && (
                                       item3
                                             == Items.WIND_CHARGE
                                          || !flag4
                                    )) {
                                 if (item6.item
                                       == Items.GOLDEN_APPLE
                                    && i3
                                       < item6.count
                                    )
                                  {
                                    Item var43 = Items.GOLDEN_APPLE;
                                    add(
                                       player2,
                                       var43,
                                       "Золотое яблоко",
                                       30000L,
                                       new ItemStack(Items.GOLDEN_APPLE)
                                    );
                                 } else if (item6.item
                                       == Items.ENCHANTED_GOLDEN_APPLE
                                    && i3
                                       < item6.count
                                    )
                                  {
                                    Item var42 = Items.ENCHANTED_GOLDEN_APPLE;
                                    add(
                                       player2,
                                       var42,
                                       "Зачарованное золотое яблоко",
                                       150000L,
                                       new ItemStack(Items.ENCHANTED_GOLDEN_APPLE)
                                    );
                                 } else if (item6.item
                                       == Items.CHORUS_FRUIT
                                    && i3
                                       < item6.count
                                    )
                                  {
                                    Item var41 = Items.CHORUS_FRUIT;
                                    add(
                                       player2,
                                       var41,
                                       "Плод хоруса",
                                       20000L,
                                       new ItemStack(Items.CHORUS_FRUIT)
                                    );
                                 } else if (item6.isheal
                                    && (
                                       i3
                                             < item6.count
                                          || item3
                                                == Items.GLASS_BOTTLE
                                             && item6.item
                                                == Items.POTION
                                          || item3
                                                != item6.item
                                             && flag4
                                    )) {
                                    Item var40 = Items.POTION;
                                    add(
                                       player2,
                                       var40,
                                       "Зелье исцеления",
                                       20000L,
                                       new ItemStack(Items.POTION)
                                    );
                                 }
                              } else {
                                 Item var39 = Items.WIND_CHARGE;
                                 add(
                                    player2,
                                    var39,
                                    "Заряд ветра",
                                    30000L,
                                    new ItemStack(Items.WIND_CHARGE)
                                 );
                              }
                           } else {
                              Item var38 = Items.SNOWBALL;
                              add(
                                 player2,
                                 var38,
                                 "Снежок",
                                 20000L,
                                 new ItemStack(Items.SNOWBALL)
                              );
                           }
                        }

                        offstates.put(
                           val3,
                           new ItemHolder(
                              item3,
                              i3,
                              flag3
                           )
                        );
                     }
                  }
               }

               long time3 = System.currentTimeMillis();

               for (Entry<UUID, Map<Item, GetremainingsecHelper>> val4 : trackers.entrySet()) {
                  Map<Item, GetremainingsecHelper> val5 = val4.getValue();
                  Iterator<Entry<Item, GetremainingsecHelper>> val6 = val5.entrySet()
                     .iterator();

                  while (
                     val6.hasNext()
                  ) {
                     Entry<Item, GetremainingsecHelper> val7 = val6.next();
                     GetremainingsecHelper val8 = val7.getValue();
                     if (time3
                        >= val8.expire
                        )
                      {
                        if (!val8.alerted
                           )
                         {
                           val8.alerted = true;
                           if (val.getEnemyCooldownsChat()
                              .isState()) {
                              ChatUtil.sendMessage(
                                 "У игрока §f"
                                    + val8.playername
                                    + " §7прошло КД на §a"
                                    + val8.name
                              );
                              PlaysoundHelper.playSound("enablealarm.wav", 1.0, 1.0F);
                           }
                        }

                        val6.remove();
                     }
                  }
               }
            }
         } else {
            clearAll();
         }
      } else {
         clearAll();
      }
   }
}
