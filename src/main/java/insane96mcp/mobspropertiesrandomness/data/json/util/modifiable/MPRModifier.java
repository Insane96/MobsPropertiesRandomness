package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        List<MPRModifier> modifiers = new ArrayList<>();
        if (!jObject.has(memberName))
            return modifiers;
        JsonArray aModifiers = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aModifiers) {
            JsonObject jObjectCondition = jsonElement.getAsJsonObject();
            ResourceLocation modifierId = MPR.locationFrom(GsonHelper.getAsString(jObjectCondition, "modifier"));
            Type modifierType = ModifiersRegistry.get(modifierId);
            if (modifierType == null) {
                MPRLogger.warn("modifier %s does not exist. Skipping".formatted(modifierId));
                continue;
            }
            modifiers.add(context.deserialize(jObjectCondition, modifierType));
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