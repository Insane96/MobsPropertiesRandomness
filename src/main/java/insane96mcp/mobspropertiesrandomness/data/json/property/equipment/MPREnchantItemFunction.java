package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@JsonAdapter(MPREnchantItemFunction.Serializer.class)
public abstract class MPREnchantItemFunction extends MPRItemFunction {
    public static final Map<ResourceLocation, Class<? extends MPREnchantItemFunction>> TYPES = Map.of(
        MPR.location("single"), SingleEnchantment.class,
        MPR.location("random"), RandomEnchantment.class,
        MPR.location("with_levels"), WithLevel.class
    );

    @Nullable
    public MPRRange level;
    public boolean allowIncompatible;

    public MPREnchantItemFunction(@Nullable MPRRange level, boolean allowIncompatible, List<MPRCondition> conditions) {
        super(conditions);
        this.level = level;
        this.allowIncompatible = allowIncompatible;
    }

    public int getLvl(LivingEntity entity, Enchantment enchantment) {
        int minLevel = this.level != null ? (int) this.level.getMin(entity) : enchantment.getMinLevel();
        int maxLevel = this.level != null ? (int) this.level.getMax(entity) : enchantment.getMaxLevel();
        return Mth.nextInt(entity.level().random, minLevel, maxLevel);
    }

    private static void addEnchantmentToItemStack(ItemStack itemStack, Enchantment enchantment, int lvl) {
        if (itemStack.getItem() == Items.ENCHANTED_BOOK)
            EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(enchantment, lvl));
        else
            itemStack.enchant(enchantment, lvl);
    }

    private static void removeEnchantmentFromItemStack(ItemStack stack, Enchantment enchantment) {
        if (stack.getTag() == null)
            return;
        ListTag listTag = new ListTag();
        if (stack.getTag().contains("Enchantments"))
            listTag = stack.getTag().getList("Enchantments", 10);
        else if (stack.getItem() == Items.ENCHANTED_BOOK)
            listTag = EnchantedBookItem.getEnchantments(stack);
        if (listTag.isEmpty())
            return;
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compound = listTag.getCompound(i);
            Enchantment foundEnchantment = ForgeRegistries.ENCHANTMENTS.getValue(EnchantmentHelper.getEnchantmentId(compound));
            if (foundEnchantment == enchantment) {
                listTag.remove(i);
                return;
            }
        }
    }

    @Nullable
    public static MPRRange deserializeLvl(JsonObject jObject, JsonDeserializationContext context) {
        if (!jObject.has("lvl"))
            return null;
        return GsonHelper.getAsObject(jObject, "lvl", context, MPRRange.class);
    }

    public static boolean deserializeAllowIncompatible(JsonObject jObject) {
        return GsonHelper.getAsBoolean(jObject, "allow_incompatible", false);
    }

    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context) {
        if (this.level != null)
            jObject.add("lvl", context.serialize(this.level));
        if (this.allowIncompatible)
            jObject.addProperty("allow_incompatible", true);
        return super.endSerialization(jObject, context);
    }

    public static class Serializer implements JsonSerializer<MPREnchantItemFunction>, JsonDeserializer<MPREnchantItemFunction> {
        @Override
        public MPREnchantItemFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            ResourceLocation propertyId = MPR.locationFrom(GsonHelper.getAsString(jObject, "type"));
            Type propertyType = TYPES.get(propertyId);
            if (propertyType == null) {
                throw new JsonParseException("enchantment type %s does not exist. Skipping".formatted(propertyId));
            }
            return context.deserialize(jObject, propertyType);
        }

        @Override
        public JsonElement serialize(MPREnchantItemFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src).getAsJsonObject();
            for (var type : TYPES.entrySet()) {
                if (type.getValue().isInstance(src)) {
                    jObject.addProperty("type", type.getKey().toString());
                    break;
                }
            }
            return jObject;
        }
    }


    @JsonAdapter(SingleEnchantment.Serializer.class)
    public static class SingleEnchantment extends MPREnchantItemFunction {
        public Enchantment enchantment;

        public SingleEnchantment(Enchantment enchantment, MPRRange level, boolean allowIncompatible, List<MPRCondition> conditions) {
            super(level, allowIncompatible, conditions);
            this.enchantment = enchantment;
        }

        @Override
        protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
            Map<Enchantment, Integer> enchantmentsOnStack = EnchantmentHelper.getEnchantments(stack);
            //noinspection ConstantConditions can't be null as it's checked to exist when the data is reloaded
            boolean canApply = this.allowIncompatible || EnchantmentHelper.isEnchantmentCompatible(enchantmentsOnStack.keySet(), this.enchantment);
            if (!canApply)
                return false;
            int lvl = getLvl(living, this.enchantment);
            if (this.enchantment != null)
                addEnchantmentToItemStack(stack, this.enchantment, lvl);
            return true;
        }

        public static class Serializer implements JsonSerializer<SingleEnchantment>, JsonDeserializer<SingleEnchantment> {
            @Override
            public SingleEnchantment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                return new SingleEnchantment(
                        SerializerUtils.deserializeRegistryObject(jObject.get("enchantment"), Registries.ENCHANTMENT),
                        deserializeLvl(jObject, context), deserializeAllowIncompatible(jObject),
                        MPRConditionable.deserializeList(jObject, context)
                );
            }

            @Override
            public JsonElement serialize(SingleEnchantment src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jObject = new JsonObject();
                jObject.add("enchantment", SerializerUtils.serializeRegistryObject(src.enchantment, Registries.ENCHANTMENT));
                return src.endSerialization(jObject, context);
            }
        }
    }

    @JsonAdapter(RandomEnchantment.Serializer.class)
    public static class RandomEnchantment extends MPREnchantItemFunction {
        public boolean allowCurses;
        public boolean allowTreasure;
        public List<Enchantment> enchantments;

        public RandomEnchantment(boolean allowCurses, boolean allowTreasure, List<Enchantment> enchantments, MPRRange level, boolean allowIncompatible, List<MPRCondition> conditions) {
            super(level, allowIncompatible, conditions);
            this.allowCurses = allowCurses;
            this.allowTreasure = allowTreasure;
            this.enchantments = enchantments;
        }

        @Override
        protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
            Map<Enchantment, Integer> enchantmentsOnStack = EnchantmentHelper.getEnchantments(stack);

            boolean isBook = stack.getItem() == Items.ENCHANTED_BOOK;

            List<Enchantment> possibleEnchantments = ForgeRegistries.ENCHANTMENTS.getValues().stream().filter((enchantment) -> {
                if (!enchantment.isDiscoverable()
                        || (enchantment.isCurse() && !allowCurses)
                        || (enchantment.isTreasureOnly() && !allowTreasure)
                        ||  (!this.enchantments.isEmpty() && !this.enchantments.contains(enchantment)))
                    return false;

                if (!this.allowIncompatible) {
                    for (Enchantment enchantmentOnStack : enchantmentsOnStack.keySet()) {
                        if (!enchantment.isCompatibleWith(enchantmentOnStack)) {
                            return false;
                        }
                    }
                }

                return isBook || enchantment.canEnchant(stack);
            }).toList();
            if (possibleEnchantments.isEmpty())
                return false;

            Enchantment enchantment = possibleEnchantments.get(living.getRandom().nextInt(possibleEnchantments.size()));
            addEnchantmentToItemStack(stack, enchantment, getLvl(living, enchantment));
            return true;
        }

        public static class Serializer implements JsonSerializer<RandomEnchantment>, JsonDeserializer<RandomEnchantment> {
            @Override
            public RandomEnchantment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                return new RandomEnchantment(
                        GsonHelper.getAsBoolean(jObject, "allow_curses", true),
                        GsonHelper.getAsBoolean(jObject, "allow_treasure", true),
                        SerializerUtils.deserializeRegistryObjectList(jObject, "enchantments", context, Registries.ENCHANTMENT),
                        deserializeLvl(jObject, context),
                        deserializeAllowIncompatible(jObject),
                        MPRConditionable.deserializeList(jObject, context)
                );
            }

            @Override
            public JsonElement serialize(RandomEnchantment src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jObject = new JsonObject();
                if (!src.allowCurses)
                    jObject.addProperty("allow_curses", false);
                if (!src.allowTreasure)
                    jObject.addProperty("allow_treasure", false);
                jObject.add("enchantments", SerializerUtils.serializeRegistryObjectList(jObject, src.enchantments, context, Registries.ENCHANTMENT));
                return src.endSerialization(jObject, context);
            }
        }
    }

    @JsonAdapter(WithLevel.Serializer.class)
    public static class WithLevel extends MPREnchantItemFunction {
        public boolean allowTreasure;
        public boolean allowCurses;

        public WithLevel(boolean allowCurses, boolean allowTreasure, MPRRange level, boolean allowIncompatible, List<MPRCondition> conditions) {
            super(level, allowIncompatible, conditions);
            this.allowCurses = allowCurses;
            this.allowTreasure = allowTreasure;
        }

        @Override
        protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
            int lvl = Math.max(1, getLvl(living, null));
            List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(living.level().random, stack, lvl, true);
            for (EnchantmentInstance instance : list) {
                if ((instance.enchantment.isCurse() && !allowCurses)
                        || (instance.enchantment.isTreasureOnly() && !allowTreasure))
                    continue;
                int lvlOnStack = stack.getEnchantmentLevel(instance.enchantment);
                if (lvlOnStack >= instance.level)
                    continue;
                else if (lvlOnStack == 0)
                    addEnchantmentToItemStack(stack, instance.enchantment, instance.level);
                else {
                    removeEnchantmentFromItemStack(stack, instance.enchantment);
                    addEnchantmentToItemStack(stack, instance.enchantment, instance.level);
                }
            }
            return true;
        }

        public static class Serializer implements JsonSerializer<WithLevel>, JsonDeserializer<WithLevel> {
            @Override
            public WithLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                return new WithLevel(
                        GsonHelper.getAsBoolean(jObject, "allow_curses", true),
                        GsonHelper.getAsBoolean(jObject, "allow_treasure", true),
                        deserializeLvl(jObject, context),
                        deserializeAllowIncompatible(jObject),
                        MPRConditionable.deserializeList(jObject, context)
                );
            }

            @Override
            public JsonElement serialize(WithLevel src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jObject = new JsonObject();
                if (!src.allowCurses)
                    jObject.addProperty("allow_curses", false);
                if (!src.allowTreasure)
                    jObject.addProperty("allow_treasure", false);
                return src.endSerialization(jObject, context);
            }
        }
    }
}
