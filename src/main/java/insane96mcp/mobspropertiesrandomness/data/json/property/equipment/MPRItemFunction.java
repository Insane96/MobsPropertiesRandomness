package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.MPRRawPresetLoader;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MPRItemFunction extends MPRConditionable {
    public MPRItemFunction(List<MPRCondition> conditions) {
        super(conditions);
    }
    public final boolean tryApply(LivingEntity livingEntity, ItemStack stack, EquipmentSlot slot) {
        if (!MPRCondition.conditionsApply(this.conditions, livingEntity))
            return false;
        return apply(livingEntity, stack, slot);
    }

    protected abstract boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot);

    public static List<MPRItemFunction> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        if (!jObject.has(memberName))
            return new ArrayList<>();
        JsonArray aFunctions = GsonHelper.getAsJsonArray(jObject, memberName);
        return deserializeList(aFunctions, context, new HashSet<>());
    }

    private static List<MPRItemFunction> deserializeList(JsonArray aFunctions, JsonDeserializationContext context, Set<ResourceLocation> resolving) {
        List<MPRItemFunction> itemFunctions = new ArrayList<>();
        for (JsonElement jsonElement : aFunctions) {
            JsonObject jObj = jsonElement.getAsJsonObject();
            if (jObj.has("preset")) {
                ResourceLocation id = MPR.locationFrom(GsonHelper.getAsString(jObj, "preset"));
                if (!resolving.add(id))
                    throw new JsonParseException("Circular function preset reference: '%s'".formatted(id));
                JsonElement preset = MPRRawPresetLoader.FUNCTION_PRESETS.get(id);
                if (preset == null)
                    throw new JsonParseException("Function preset '%s' not found".formatted(id));
                itemFunctions.addAll(deserializeList(preset.getAsJsonArray(), context, resolving));
                resolving.remove(id);
                continue;
            }
            ResourceLocation itemFunctionId = MPR.locationFrom(GsonHelper.getAsString(jObj, "function"));
            Type itemFunctionType = ItemFunctionsRegistry.get(itemFunctionId);
            if (itemFunctionType == null) {
                MPRLogger.warn("item function %s does not exist. Skipping".formatted(itemFunctionId));
                continue;
            }
            itemFunctions.add(context.deserialize(jObj, itemFunctionType));
        }
        return itemFunctions;
    }
}
