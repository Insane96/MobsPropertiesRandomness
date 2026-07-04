package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
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
import net.minecraft.world.item.enchantment.ItemEnchantments;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@JsonAdapter(MPREnchantItemFunction.Serializer.class)
public abstract class MPREnchantItemFunction extends MPRItemFunction {
    public static final Map<ResourceLocation, Class<? extends MPREnchantItemFunction>> TYPES = Map.of(
        MPR.id("single"), SingleEnchantment.class,
        MPR.id("random"), RandomEnchantment.class,
        MPR.id("with_levels"), WithLevel.class
    );

    @Nullable
    public MPRRange level;
    public boolean allowIncompatible;

    //TODO Extend to allow Enchantment Tags

    public MPREnchantItemFunction(@Nullable MPRRange level, boolean allowIncompatible, List<MPRCondition> conditions) {
        super(conditions);
        this.level = level;
        this.allowIncompatible = allowIncompatible;
    }

    public int getLvl(LivingEntity entity, Holder<Enchantment> enchantment) {
        int minLevel = this.level != null ? (int) this.level.getMin(entity) : enchantment.value().getMinLevel();
        int maxLevel = this.level != null ? (int) this.level.getMax(entity) : enchantment.value().getMaxLevel();
        return Mth.nextInt(entity.level().random, minLevel, maxLevel);
    }

    private static void removeEnchantmentFromItemStack(ItemStack stack, Holder<Enchantment> enchantment) {
        DataComponentType<ItemEnchantments> componentType =
                stack.getItem() instanceof EnchantedBookItem
                        ? DataComponents.STORED_ENCHANTMENTS
                        : DataComponents.ENCHANTMENTS;

        ItemEnchantments enchantments = stack.get(componentType);
        if (enchantments == null || enchantments.isEmpty())
            return;

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
        mutable.removeIf(holder -> holder.equals(enchantment));

        ItemEnchantments updated = mutable.toImmutable();
        if (updated.isEmpty())
            stack.remove(componentType);
        else
            stack.set(componentType, updated);
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
        public Holder<Enchantment> enchantment;

        public SingleEnchantment(Holder<Enchantment> enchantment, MPRRange level, boolean allowIncompatible, List<MPRCondition> conditions) {
            super(level, allowIncompatible, conditions);
            this.enchantment = enchantment;
        }

        @Override
        protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
            if (this.enchantment == null)
                return false;
            ItemEnchantments itemEnchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            //noinspection ConstantConditions can't be null as it's checked to exist when the data is reloaded
            boolean canApply = this.allowIncompatible || EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), this.enchantment);
            if (!canApply)
                return false;
            int lvl = getLvl(living, this.enchantment);
            if (this.enchantment != null)
                stack.enchant(enchantment, lvl);
            return true;
        }

        public static class Serializer implements JsonSerializer<SingleEnchantment>, JsonDeserializer<SingleEnchantment> {
            @Override
            public SingleEnchantment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                Holder<Enchantment> enchantment = SerializerUtils.deserializeRegistryObjectAsHolder(jObject, "enchantment", Registries.ENCHANTMENT);
                if (enchantment == null)
                    MPRLogger.warn("Invalid enchantment: %s. Will be ignored.", jObject.get("enchantment").getAsString());
                return new SingleEnchantment(
                        enchantment,
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
        public List<Holder<Enchantment>> enchantments;

        public RandomEnchantment(boolean allowCurses, boolean allowTreasure, List<Holder<Enchantment>> enchantments, MPRRange level, boolean allowIncompatible, List<MPRCondition> conditions) {
            super(level, allowIncompatible, conditions);
            this.allowCurses = allowCurses;
            this.allowTreasure = allowTreasure;
            this.enchantments = enchantments;
        }

        @Override
        protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
            ItemEnchantments itemEnchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

            boolean isBook = stack.getItem() == Items.ENCHANTED_BOOK;

            Registry<Enchantment> registry = living.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT);

            List<Holder.Reference<Enchantment>> possibleEnchantments = registry.holders()
                    .filter(enchantmentHolder -> {
                        Enchantment enchantment = enchantmentHolder.value();

                        // Check discoverable, curse, treasure
                        if ((enchantmentHolder.is(EnchantmentTags.CURSE) && !allowCurses)
                                || (enchantmentHolder.is(EnchantmentTags.TREASURE) && !allowTreasure)
                                || (!this.enchantments.isEmpty() && !this.enchantments.contains(enchantmentHolder)))
                            return false;

                        // Check compatibility with existing enchantments
                        if (!this.allowIncompatible) {
                            for (Holder<Enchantment> enchantmentOnStack : itemEnchantments.keySet()) {
                                if (!Enchantment.areCompatible(enchantmentHolder, enchantmentOnStack)) {
                                    return false;
                                }
                            }
                        }

                        // Check if can enchant the item
                        return isBook || enchantment.canEnchant(stack);
                    })
                    .toList();
            if (possibleEnchantments.isEmpty())
                return false;

            Holder<Enchantment> enchantment = possibleEnchantments.get(living.getRandom().nextInt(possibleEnchantments.size()));
            stack.enchant(enchantment, getLvl(living, enchantment));
            return true;
        }

        public static class Serializer implements JsonSerializer<RandomEnchantment>, JsonDeserializer<RandomEnchantment> {
            @Override
            public RandomEnchantment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                return new RandomEnchantment(
                        GsonHelper.getAsBoolean(jObject, "allow_curses", true),
                        GsonHelper.getAsBoolean(jObject, "allow_treasure", true),
                        SerializerUtils.deserializeRegistryObjectListAsHolders(jObject, "enchantments", context, Registries.ENCHANTMENT, false),
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
                jObject.add("enchantments", SerializerUtils.serializeRegistryHolderList(jObject, src.enchantments, context, Registries.ENCHANTMENT));
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
            Registry<Enchantment> enchantmentRegistry = living.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);

            Stream<Holder<Enchantment>> enchantmentStream = enchantmentRegistry.holders()
                    .map(holder -> holder);

            List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(
                    living.level().random,
                    stack,
                    lvl,
                    enchantmentStream
            );
            for (EnchantmentInstance instance : list) {
                if ((instance.enchantment.is(EnchantmentTags.CURSE) && !allowCurses)
                        || (instance.enchantment.is(EnchantmentTags.TREASURE) && !allowTreasure))
                    continue;
                stack.enchant(instance.enchantment, instance.level);
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
