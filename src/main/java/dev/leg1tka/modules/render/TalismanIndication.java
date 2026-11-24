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

import java.util.ArrayList;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class TalismanIndication extends Module {
   public static TalismanIndication INSTANCE = new TalismanIndication();
   private static final Identifier CRUSHER_HELMET_MODEL = Identifier.of("aspectdlc", "crusher_helmet");
   private static final Identifier CRUSHER_CHESTPLATE_MODEL = Identifier.of("aspectdlc", "crusher_chestplate");
   private static final Identifier CRUSHER_LEGGINGS_MODEL = Identifier.of("aspectdlc", "crusher_leggings");
   private static final Identifier CRUSHER_BOOTS_MODEL = Identifier.of("aspectdlc", "crusher_boots");
   private static final Identifier THORNS_ARMOR_MODEL = Identifier.of("aspectdlc", "thorns_armor");
   private static final List<CustomItemModel> TALISMANS = List.of(
      new CustomItemModel(
         Identifier.of("aspectdlc", "tal_karatel0"),
         Identifier.of("aspectdlc", "tal_karatel0_simple"),
         List.of(
            new ModelModifierEntry(EntityAttributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 7.0),
            new ModelModifierEntry(EntityAttributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.1),
            new ModelModifierEntry(EntityAttributes.MAX_HEALTH, Operation.ADD_VALUE, -4.0)
         )
      ),
      new CustomItemModel(
         Identifier.of("aspectdlc", "tal_demon"),
         Identifier.of("aspectdlc", "tal_demon_simple"),
         List.of(new ModelModifierEntry(EntityAttributes.ATTACK_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.1), new ModelModifierEntry(EntityAttributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 2.5))
      ),
      new CustomItemModel(
         Identifier.of("aspectdlc", "tal_mraka"),
         Identifier.of("aspectdlc", "tal_mraka_simple"),
         List.of(new ModelModifierEntry(EntityAttributes.MAX_HEALTH, Operation.ADD_VALUE, 1.5), new ModelModifierEntry(EntityAttributes.ARMOR, Operation.ADD_VALUE, 1.5))
      ),
      new CustomItemModel(
         Identifier.of("aspectdlc", "tal_vihrya"),
         Identifier.of("aspectdlc", "tal_vihrya_simple"),
         List.of(
            new ModelModifierEntry(EntityAttributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.15),
            new ModelModifierEntry(EntityAttributes.MAX_HEALTH, Operation.ADD_VALUE, 2.0),
            new ModelModifierEntry(EntityAttributes.ATTACK_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.15)
         )
      ),
      new CustomItemModel(
         Identifier.of("aspectdlc", "tal_razdora"),
         Identifier.of("aspectdlc", "tal_razdora_simple"),
         List.of(
            new ModelModifierEntry(EntityAttributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.1),
            new ModelModifierEntry(EntityAttributes.MAX_HEALTH, Operation.ADD_VALUE, 2.0),
            new ModelModifierEntry(EntityAttributes.ATTACK_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.1),
            new ModelModifierEntry(EntityAttributes.ARMOR, Operation.ADD_VALUE, -3.0),
            new ModelModifierEntry(EntityAttributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 4.0)
         )
      ),
      new CustomItemModel(
         Identifier.of("aspectdlc", "tal_tirana"),
         Identifier.of("aspectdlc", "tal_tirana_simple"),
         List.of(
            new ModelModifierEntry(EntityAttributes.MAX_HEALTH, Operation.ADD_VALUE, -4.0),
            new ModelModifierEntry(EntityAttributes.ARMOR, Operation.ADD_VALUE, 2.0),
            new ModelModifierEntry(EntityAttributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 2.0)
         )
      ),
      new CustomItemModel(
         Identifier.of("aspectdlc", "tal_yarosti"),
         Identifier.of("aspectdlc", "tal_yarosti_simple"),
         List.of(new ModelModifierEntry(EntityAttributes.MAX_HEALTH, Operation.ADD_VALUE, -4.0), new ModelModifierEntry(EntityAttributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 5.0))
      ),
      new CustomItemModel(
         Identifier.of("aspectdlc", "tal_krush0"),
         Identifier.of("aspectdlc", "tal_krush0_simple"),
         List.of(
            new ModelModifierEntry(EntityAttributes.ARMOR_TOUGHNESS, Operation.ADD_VALUE, 2.0),
            new ModelModifierEntry(EntityAttributes.ARMOR, Operation.ADD_VALUE, 2.0),
            new ModelModifierEntry(EntityAttributes.MAX_HEALTH, Operation.ADD_VALUE, 4.0),
            new ModelModifierEntry(EntityAttributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 3.0)
         )
      )
   );
   public final ModeSetting mode;
   public final BooleanSetting talismanIndication;
   public final ModeSetting indication;
   public final BooleanSetting crusherArmorIndication;
   public final BooleanSetting thornsArmorIndication;

   public TalismanIndication() {
      super("TalismanIndication", Category.RENDER);
      String var1 = "TalismanIndication";
      String var3 = "Мод";
      String var9 = "SpookyTime";
      String[] var10005 = new String[2];
      var10005[0] = "SpookyTime";
      var10005[1] = "ArtyGrief";
      this.mode = new ModeSetting(var3, var9, var10005);
      this.talismanIndication = new BooleanSetting("Индикация талисманов", true);
      String var6 = "Выбор индикации";
      String var11 = "Красивый";
      var10005 = new String[2];
      var10005[0] = "Красивый";
      var10005[1] = "Простая";
      this.indication = new ModeSetting(var6, var11, var10005).visible(this.talismanIndication::isState);
      this.crusherArmorIndication = new BooleanSetting("Индикация брони круша", true);
      this.thornsArmorIndication = new BooleanSetting("Индикация шипов", true);
      this.addSettings(new Setting[]{
         this.mode,
         this.talismanIndication,
         this.indication,
         this.crusherArmorIndication,
         this.thornsArmorIndication
      });
   }

   public ItemStack getRenderStack(
      ItemStack val
   ) {
      if (this.isEnable()
         && !val.isEmpty()
         )
       {
         ItemStack stack = this.getThornsArmorStack(
            val
         );
         if (stack
            != val
            )
          {
            return stack;
         } else {
            ItemStack stack2 = this.getCrusherArmorStack(
               val
            );
            if (stack2
               != val
               )
             {
               return stack2;
            } else if (this.talismanIndication.isState()
               && val.isOf(
                  Items.TOTEM_OF_UNDYING
               )) {
               CustomItemModel item2 = this.getDefinition(
                  val
               );
               if (item2
                  == null) {
                  return val;
               } else {
                  ModeSetting var10001 = this.indication;
                  Identifier id = item2.getModel(
                     var10001.is("Простая")
                  );
                  return this.withModel(
                     val,
                     id
                  );
               }
            } else {
               return val;
            }
         }
      } else {
         return val;
      }
   }

   private ItemStack getThornsArmorStack(
      ItemStack val
   ) {
      return this.thornsArmorIndication.isState()
            && this.isThornsArmor(
               val
            )
         ? this.withModel(
            val,
            THORNS_ARMOR_MODEL
         )
         : val;
   }

   private ItemStack getCrusherArmorStack(
      ItemStack val
   ) {
      if (this.crusherArmorIndication.isState()
         && this.isCrusherArmor(
            val
         )) {
         Identifier id = this.getCrusherArmorModel(
            val
         );
         return id
               == null
            ? val
            : this.withModel(
               val,
               id
            );
      } else {
         return val;
      }
   }

   private ItemStack withModel(
      ItemStack val,
      Identifier val2
   ) {
      Identifier id = (Identifier)val.get(
         DataComponentTypes.ITEM_MODEL
      );
      if (val2.equals(
         id
      )) {
         return val;
      } else {
         ItemStack stack = val.copy();
         stack.set(
            DataComponentTypes.ITEM_MODEL,
            val2
         );
         return stack;
      }
   }

   private Identifier getCrusherArmorModel(
      ItemStack val
   ) {
      EquippableComponent val2 = (EquippableComponent)val.get(
         DataComponentTypes.EQUIPPABLE
      );
      if (val2
            != null
         && val2.slot()
            != null) {
         EquipmentSlot val3 = val2.slot();
         if (val3
            == EquipmentSlot.HEAD) {
            return CRUSHER_HELMET_MODEL;
         } else if (val3
            == EquipmentSlot.CHEST) {
            return CRUSHER_CHESTPLATE_MODEL;
         } else if (val3
            == EquipmentSlot.LEGS) {
            return CRUSHER_LEGGINGS_MODEL;
         } else {
            return val3
                  == EquipmentSlot.FEET
               ? CRUSHER_BOOTS_MODEL
               : null;
         }
      } else {
         return null;
      }
   }

   private CustomItemModel getDefinition(
      ItemStack val
   ) {
      List val2 = this.getOffhandAttributes(
         val
      );

      for (CustomItemModel item2 : TALISMANS) {
         if (item2.matches(
            val2
         )) {
            return item2;
         }
      }

      return null;
   }

   private boolean isCrusherArmor(
      ItemStack val
   ) {
      if (mc.world != null
         && val.get(
               DataComponentTypes.EQUIPPABLE
            )
            != null) {
         Registry val2 = mc.world
            .getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT);
         RegistryEntry val3 = val2.getOrThrow(
            Enchantments.PROTECTION
         );
         RegistryEntry val4 = val2.getOrThrow(
            Enchantments.MENDING
         );
         return EnchantmentHelper.getLevel(
                  val3,
                  val
               )
               >= 5
            && EnchantmentHelper.getLevel(
                  val4,
                  val
               )
               > 0;
      } else {
         return false;
      }
   }

   private boolean isThornsArmor(
      ItemStack val
   ) {
      if (mc.world != null
         && this.isNetheriteArmor(
            val
         )) {
         Registry val2 = mc.world
            .getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT);
         RegistryEntry val3 = val2.getOrThrow(
            Enchantments.PROTECTION
         );
         RegistryEntry val4 = val2.getOrThrow(
            Enchantments.MENDING
         );
         RegistryEntry val5 = val2.getOrThrow(
            Enchantments.THORNS
         );
         return EnchantmentHelper.getLevel(
                  val3,
                  val
               )
               >= 5
            && EnchantmentHelper.getLevel(
                  val4,
                  val
               )
               > 0
            && EnchantmentHelper.getLevel(
                  val5,
                  val
               )
               > 0;
      } else {
         return false;
      }
   }

   private boolean isNetheriteArmor(
      ItemStack val
   ) {
      return val.isOf(
            Items.NETHERITE_HELMET
         )
         || val.isOf(
            Items.NETHERITE_CHESTPLATE
         )
         || val.isOf(
            Items.NETHERITE_LEGGINGS
         )
         || val.isOf(
            Items.NETHERITE_BOOTS
         );
   }

   private List<ModelModifierEntry> getOffhandAttributes(
      ItemStack val
   ) {
      AttributeModifiersComponent val2 = (AttributeModifiersComponent)val.get(
         DataComponentTypes.ATTRIBUTE_MODIFIERS
      );
      if (val2
         == null) {
         return List.of();
      } else {
         List val3 = new ArrayList<>();

         for (Entry val4 : val2.modifiers()) {
            if (val4.slot()
               .matches(EquipmentSlot.OFFHAND)) {
               val3.add(
                  new ModelModifierEntry(
                     val4.attribute(),
                     this.normalizeOperation(
                        val4
                     ),
                     val4.modifier()
                        .value()
                  )
               );
            }
         }

         return val3;
      }
   }

   private Operation normalizeOperation(
      Entry val
   ) {
      return (
               val.attribute()
                     .equals(EntityAttributes.MOVEMENT_SPEED)
                  || val.attribute()
                     .equals(EntityAttributes.ATTACK_SPEED)
            )
            && val.modifier()
                  .operation()
               == Operation.ADD_MULTIPLIED_TOTAL
         ? Operation.ADD_MULTIPLIED_BASE
         : val.modifier()
            .operation();
   }
}
