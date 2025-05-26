package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

    public abstract boolean conditionApplies(LivingEntity livingEntity);

    public final boolean tryInvert(boolean value) {
        return this.inverted ? !value : value;
    }

    public static boolean deserializeInverted(JsonObject jObject) {
        return jObject.has("inverted") && jObject.get("inverted").getAsBoolean();
    }

    public JsonObject serializeInverted() {
        JsonObject jObject = new JsonObject();
        if (inverted)
            jObject.addProperty("inverted", true);
        return jObject;
    }

    public static List<MPRCondition> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPRCondition> conditions = new ArrayList<>();
        if (!jObject.has(memberName))
            return conditions;
        JsonArray aModifiers = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aModifiers) {
            JsonObject jObjectCondition = jsonElement.getAsJsonObject();
            ResourceLocation conditionId = ResourceLocation.tryParse(GsonHelper.getAsString(jObjectCondition, "condition"));
            Type conditionType = ConditionsRegistry.CONDITIONS.get(conditionId);
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
