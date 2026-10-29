package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

/// Applies an armor trim to the item. Material and pattern are picked randomly from {@code materials} and {@code patterns},
/// or from all the registered ones if the list is empty or missing.
@JsonAdapter(MPRArmorTrimItemFunction.Serializer.class)
public class MPRArmorTrimItemFunction extends MPRItemFunction {
    public List<Holder<TrimMaterial>> materials;
    public List<Holder<TrimPattern>> patterns;
    public boolean showInTooltip;

    public MPRArmorTrimItemFunction(List<Holder<TrimMaterial>> materials, List<Holder<TrimPattern>> patterns, boolean showInTooltip, List<MPRCondition> conditions) {
        super(conditions);
        this.materials = materials;
        this.patterns = patterns;
        this.showInTooltip = showInTooltip;
    }

    @Override
    protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
        if (!stack.is(ItemTags.TRIMMABLE_ARMOR))
            return false;
        Holder<TrimMaterial> material = pickRandom(living, this.materials, Registries.TRIM_MATERIAL);
        Holder<TrimPattern> pattern = pickRandom(living, this.patterns, Registries.TRIM_PATTERN);
        if (material == null || pattern == null)
            return false;
        stack.set(DataComponents.TRIM, new ArmorTrim(material, pattern, this.showInTooltip));
        return true;
    }

    @Nullable
    private static <T> Holder<T> pickRandom(LivingEntity living, List<Holder<T>> list, ResourceKey<Registry<T>> registryKey) {
        RandomSource random = living.getRandom();
        if (!list.isEmpty())
            return list.get(random.nextInt(list.size()));
        return living.level().registryAccess().registryOrThrow(registryKey).getRandom(random).orElse(null);
    }

    public static class Serializer implements JsonDeserializer<MPRArmorTrimItemFunction>, JsonSerializer<MPRArmorTrimItemFunction> {
        @Override
        public MPRArmorTrimItemFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRArmorTrimItemFunction(
                    SerializerUtils.deserializeRegistryObjectListAsHolders(jObject, "materials", context, Registries.TRIM_MATERIAL, false),
                    SerializerUtils.deserializeRegistryObjectListAsHolders(jObject, "patterns", context, Registries.TRIM_PATTERN, false),
                    GsonHelper.getAsBoolean(jObject, "show_in_tooltip", true),
                    MPRConditionable.deserializeList(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRArmorTrimItemFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (!src.materials.isEmpty())
                jObject.add("materials", SerializerUtils.serializeRegistryHolderList(jObject, src.materials, context, Registries.TRIM_MATERIAL));
            if (!src.patterns.isEmpty())
                jObject.add("patterns", SerializerUtils.serializeRegistryHolderList(jObject, src.patterns, context, Registries.TRIM_PATTERN));
            if (!src.showInTooltip)
                jObject.addProperty("show_in_tooltip", false);
            return src.endSerialization(jObject, context);
        }
    }
}
