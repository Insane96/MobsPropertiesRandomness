package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class MPRConditionable {
    public List<MPRCondition> conditions;

    public MPRConditionable(List<MPRCondition> conditions) {
        this.conditions = conditions;
    }

    public final boolean conditionsApply(LivingEntity livingEntity) {
        if (this.conditions.isEmpty())
            return true;
        return MPRCondition.conditionsApply(this.conditions, livingEntity);
    }

    public static List<MPRCondition> deserializeList(JsonObject jObject, JsonDeserializationContext context) {
        if (!jObject.has("conditions"))
            return new ArrayList<>();
        return MPRCondition.deserializeList(jObject, "conditions", context);
    }

    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context) {
        if (!this.conditions.isEmpty())
            jObject.add("conditions", context.serialize(this.conditions));
        return jObject;
    }
}
