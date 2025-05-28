package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class MPRCondition {
    public boolean inverted;

    public MPRCondition(boolean inverted) {
        this.inverted = inverted;
    }

    public final boolean conditionApplies(LivingEntity livingEntity) {
        return tryInvert(conditionCheck(livingEntity));
    }

    protected abstract boolean conditionCheck(LivingEntity livingEntity);

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

    public static List<MPRCondition> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPRCondition> conditions = new ArrayList<>();
        if (!jObject.has(memberName))
            return conditions;
        JsonArray aConditions = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aConditions) {
            JsonObject jObjectCondition = jsonElement.getAsJsonObject();
            ResourceLocation conditionId = MPR.locationFrom(GsonHelper.getAsString(jObjectCondition, "condition"));
            Type conditionType = ConditionsRegistry.get(conditionId);
            if (conditionType == null) {
                Logger.warn("condition %s does not exist. Skipping".formatted(conditionId));
                continue;
            }
            conditions.add(context.deserialize(jObjectCondition, conditionType));
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
