package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.MPRRawPresetLoader;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MPRCondition {
    public boolean inverted;

    public MPRCondition(boolean inverted) {
        this.inverted = inverted;
    }

    public final boolean conditionApplies(LivingEntity livingEntity) {
        return tryInvert(conditionCheck(livingEntity));
    }

    protected abstract boolean conditionCheck(LivingEntity living);

    public final boolean tryInvert(boolean value) {
        return this.inverted ? !value : value;
    }

    public static boolean deserializeInverted(JsonObject jObject) {
        return jObject.has("inverted") && jObject.get("inverted").getAsBoolean();
    }

    public JsonObject endSerialization(JsonObject jObject) {
        //noinspection DataFlowIssue
        jObject.addProperty("condition", ConditionsRegistry.get(this.getClass()).toString());
        if (this.inverted)
            jObject.addProperty("inverted", true);
        return jObject;
    }

    public static List<MPRCondition> deserializeConditions(JsonObject jObject, JsonDeserializationContext context) { return deserializeList(jObject, "conditions", context); }

    public static List<MPRCondition> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPRCondition> conditions = new ArrayList<>();
        if (!jObject.has(memberName))
            return conditions;
        JsonArray aConditions = GsonHelper.getAsJsonArray(jObject, memberName);
        return deserializeList(aConditions, context);
    }

    public static List<MPRCondition> deserializeList(JsonArray aConditions, JsonDeserializationContext context) {
        return deserializeList(aConditions, context, new HashSet<>());
    }

    private static List<MPRCondition> deserializeList(JsonArray aConditions, JsonDeserializationContext context, Set<ResourceLocation> resolving) {
        List<MPRCondition> conditions = new ArrayList<>();
        for (JsonElement jsonElement : aConditions) {
            JsonObject jObj = jsonElement.getAsJsonObject();
            if (jObj.has("preset")) {
                ResourceLocation id = MPR.locationFrom(GsonHelper.getAsString(jObj, "preset"));
                if (!resolving.add(id))
                    throw new JsonParseException("Circular condition preset reference: '%s'".formatted(id));
                JsonElement preset = MPRRawPresetLoader.CONDITION_PRESETS.get(id);
                if (preset == null)
                    throw new JsonParseException("Condition preset '%s' not found".formatted(id));
                conditions.addAll(deserializeList(preset.getAsJsonArray(), context, resolving));
                resolving.remove(id);
                continue;
            }
            ResourceLocation conditionId = MPR.locationFrom(GsonHelper.getAsString(jObj, "condition"));
            Type conditionType = ConditionsRegistry.get(conditionId);
            if (conditionType == null) {
                MPRLogger.warn("condition %s does not exist. Skipping".formatted(conditionId));
                continue;
            }
            conditions.add(context.deserialize(jObj, conditionType));
        }
        return conditions;
    }

    public static boolean conditionsApply(List<MPRCondition> conditions, LivingEntity entity) {
        for (MPRCondition condition : conditions) {
            if (!condition.conditionApplies(entity))
                return false;
        }
        return true;
    }
}
