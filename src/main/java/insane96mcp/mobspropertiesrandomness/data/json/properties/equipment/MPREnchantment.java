package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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

@JsonAdapter(MPREnchantment.Serializer.class)
public abstract class MPREnchantment {
    public static final Map<ResourceLocation, Class<? extends MPREnchantment>> TYPES = Map.of(
        MPR.location("single"), SingleEnchantment.class,
        MPR.location("random"), RandomEnchantment.class,
        MPR.location("with_levels"), WithLevel.class
    );

    @Nullable
    public MPRRange level;
    public boolean allowIncompatible;

    public MPREnchantment(@Nullable MPRRange level, boolean allowIncompatible) {
        this.level = level;
        this.allowIncompatible = allowIncompatible;
    }

    public abstract void applyToStack(LivingEntity entity, ItemStack itemStack);

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

    public static void enchantItem(RandomSource random, ItemStack itemStack, int lvl, boolean treasure) {
        lvl = Mth.clamp(lvl, 1, 40);
        List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(random, itemStack, lvl, treasure);
        boolean isEnchantedBook = itemStack.is(Items.ENCHANTED_BOOK);

        for (EnchantmentInstance enchantmentInstance : list) {
            if (isEnchantedBook)
                EnchantedBookItem.addEnchantment(itemStack, enchantmentInstance);
            else
                itemStack.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
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
        return jObject;
    }

    public static class Serializer implements JsonSerializer<MPREnchantment>, JsonDeserializer<MPREnchantment> {
        @Override
        public MPREnchantment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            ResourceLocation propertyId = MPR.locationFrom(GsonHelper.getAsString(jObject, "type"));
            Type propertyType = TYPES.get(propertyId);
            if (propertyType == null) {
                throw new JsonParseException("enchantment type %s does not exist. Skipping".formatted(propertyId));
            }
            return context.deserialize(jObject, propertyType);
        }

        @Override
        public JsonElement serialize(MPREnchantment src, Type typeOfSrc, JsonSerializationContext context) {
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
    public static class SingleEnchantment extends MPREnchantment {
        public Enchantment enchantment;

        public SingleEnchantment(Enchantment enchantment, MPRRange level, boolean allowIncompatible) {
            super(level, allowIncompatible);
            this.enchantment = enchantment;
        }

        @Override
        public void applyToStack(LivingEntity entity, ItemStack itemStack) {
            Map<Enchantment, Integer> enchantmentsOnStack = EnchantmentHelper.getEnchantments(itemStack);
            //noinspection ConstantConditions can't be null as it's checked to exist when the data is reloaded
            boolean canApply = this.allowIncompatible || EnchantmentHelper.isEnchantmentCompatible(enchantmentsOnStack.keySet(), this.enchantment);
            if (!canApply)
                this.enchantment = null;
            int lvl = getLvl(entity, this.enchantment);
            if (this.enchantment != null)
                addEnchantmentToItemStack(itemStack, this.enchantment, lvl);
        }

        public static class Serializer implements JsonSerializer<SingleEnchantment>, JsonDeserializer<SingleEnchantment> {
            @Override
            public SingleEnchantment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                return new SingleEnchantment(SerializerUtils.deserializeRegistryObject(jObject.get("enchantment"), Registries.ENCHANTMENT), deserializeLvl(jObject, context), deserializeAllowIncompatible(jObject));
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
    public static class RandomEnchantment extends MPREnchantment {
        public boolean allowCurses;
        public boolean allowTreasure;
        public List<Enchantment> enchantments;

        public RandomEnchantment(boolean allowCurses, boolean allowTreasure, List<Enchantment> enchantments, MPRRange level, boolean allowIncompatible) {
            super(level, allowIncompatible);
            this.allowCurses = allowCurses;
            this.allowTreasure = allowTreasure;
            this.enchantments = enchantments;
        }

        @Override
        public void applyToStack(LivingEntity entity, ItemStack itemStack) {
            Map<Enchantment, Integer> enchantmentsOnStack = EnchantmentHelper.getEnchantments(itemStack);

            boolean isBook = itemStack.getItem() == Items.ENCHANTED_BOOK;

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

                return isBook || enchantment.canEnchant(itemStack);
            }).toList();
            if (possibleEnchantments.isEmpty())
                return;

            Enchantment enchantment = possibleEnchantments.get(entity.getRandom().nextInt(possibleEnchantments.size()));
            addEnchantmentToItemStack(itemStack, enchantment, getLvl(entity, enchantment));
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
                        deserializeAllowIncompatible(jObject)
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
    public static class WithLevel extends MPREnchantment {
        public boolean allowTreasure;
        public boolean allowCurses;

        public WithLevel(boolean allowCurses, boolean allowTreasure, MPRRange level, boolean allowIncompatible) {
            super(level, allowIncompatible);
            this.allowCurses = allowCurses;
            this.allowTreasure = allowTreasure;
        }

        @Override
        public void applyToStack(LivingEntity entity, ItemStack itemStack) {
            int lvl = Math.max(1, getLvl(entity, null));
            List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(entity.level().random, itemStack, lvl, true);
            for (EnchantmentInstance enchantmentInstance : list) {
                if ((enchantmentInstance.enchantment.isCurse() && !allowCurses)
                        || (enchantmentInstance.enchantment.isTreasureOnly() && !allowTreasure))
                    continue;
                addEnchantmentToItemStack(itemStack, enchantmentInstance.enchantment, enchantmentInstance.level);
            }
        }

        public static class Serializer implements JsonSerializer<WithLevel>, JsonDeserializer<WithLevel> {
            @Override
            public WithLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                return new WithLevel(
                        GsonHelper.getAsBoolean(jObject, "allow_curses", true),
                        GsonHelper.getAsBoolean(jObject, "allow_treasure", true),
                        deserializeLvl(jObject, context),
                        deserializeAllowIncompatible(jObject)
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
