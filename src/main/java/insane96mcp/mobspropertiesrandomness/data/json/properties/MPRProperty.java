package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class MPRProperty {
    public List<MPRCondition> conditions;

    public MPRProperty(List<MPRCondition> conditions) {
        this.conditions = conditions;
    }

    public final boolean tryApply(LivingEntity livingEntity) {
        if (!MPRCondition.conditionsApply(this.conditions, livingEntity))
            return false;
        return apply(livingEntity);
    }

    protected abstract boolean apply(LivingEntity living);

    public static List<MPRCondition> deserializeConditions(JsonObject jObject, JsonDeserializationContext context) {
        if (!jObject.has("conditions"))
            return new ArrayList<>();
        return MPRCondition.deserializeList(jObject, "conditions", context);
    }

    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context) {
        //noinspection DataFlowIssue
        jObject.addProperty("property", PropertiesRegistry.get(this.getClass()).toString());
        if (!this.conditions.isEmpty())
            jObject.add("conditions", context.serialize(this.conditions));
        return jObject;
    }

    public static List<MPRProperty> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPRProperty> properties = new ArrayList<>();
        if (!jObject.has(memberName))
            return properties;
        JsonArray aProperties = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aProperties) {
            JsonObject jObjectCondition = jsonElement.getAsJsonObject();
            ResourceLocation propertyId = MPR.locationFrom(GsonHelper.getAsString(jObjectCondition, "property"));
            Type propertyType = PropertiesRegistry.get(propertyId);
            if (propertyType == null) {
                Logger.warn("property %s does not exist. Skipping".formatted(propertyId));
                continue;
            }
            properties.add(context.deserialize(jObjectCondition, propertyType));
        }
        return properties;
    }
}
