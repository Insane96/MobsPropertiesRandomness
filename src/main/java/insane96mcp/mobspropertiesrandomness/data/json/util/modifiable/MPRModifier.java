package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.MPRRawPresetLoader;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MPRModifier extends MPRConditionable {
    protected Operation operation;

    public MPRModifier(Operation operation, List<MPRCondition> conditions) {
        super(conditions);
        this.operation = operation;
    }

    public final double tryApply(double value, LivingEntity living) {
        if (!MPRCondition.conditionsApply(this.conditions, living))
            return value;
        return this.operation == Operation.ADD ? value + getModifier(living) : value * getModifier(living);
    }

    protected abstract double getModifier(LivingEntity living);

    protected double getNoModifier() {
        return this.operation == Operation.ADD ? 0d : 1d;
    }

    public static Operation deserializeOperation(JsonObject json, JsonDeserializationContext context) {
        return GsonHelper.getAsObject(json, "operation", context, Operation.class);
    }

    @Override
    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context) {
        jObject.add("operation", context.serialize(this.operation));
        return super.endSerialization(jObject, context);
    }

    public static List<MPRModifier> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        if (!jObject.has(memberName))
            return new ArrayList<>();
        JsonArray aModifiers = GsonHelper.getAsJsonArray(jObject, memberName);
        return deserializeList(aModifiers, context, new HashSet<>());
    }

    private static List<MPRModifier> deserializeList(JsonArray aModifiers, JsonDeserializationContext context, Set<ResourceLocation> resolving) {
        List<MPRModifier> modifiers = new ArrayList<>();
        for (JsonElement jsonElement : aModifiers) {
            JsonObject jObj = jsonElement.getAsJsonObject();
            if (jObj.has("preset")) {
                ResourceLocation id = MPR.locationFrom(GsonHelper.getAsString(jObj, "preset"));
                if (!resolving.add(id))
                    throw new JsonParseException("Circular modifier preset reference: '%s'".formatted(id));
                JsonElement preset = MPRRawPresetLoader.MODIFIER_PRESETS.get(id);
                if (preset == null)
                    throw new JsonParseException("Modifier preset '%s' not found".formatted(id));
                modifiers.addAll(deserializeList(preset.getAsJsonArray(), context, resolving));
                resolving.remove(id);
                continue;
            }
            ResourceLocation modifierId = MPR.locationFrom(GsonHelper.getAsString(jObj, "modifier"));
            Type modifierType = ModifiersRegistry.REGISTRY.get(modifierId);
            if (modifierType == null) {
                MPRLogger.warn("modifier %s does not exist. Skipping".formatted(modifierId));
                continue;
            }
            modifiers.add(context.deserialize(jObj, modifierType));
        }
        return modifiers;
    }

    public enum Operation {
        @SerializedName("add")
        ADD,
        @SerializedName("multiply")
        MULTIPLY
    }
}