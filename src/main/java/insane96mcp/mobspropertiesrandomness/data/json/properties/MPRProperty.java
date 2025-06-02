package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class MPRProperty extends MPRConditionable {
    public MPRProperty(List<MPRCondition> conditions) {
        super(conditions);
    }

    public final boolean tryApply(LivingEntity livingEntity) {
        if (!MPRCondition.conditionsApply(this.conditions, livingEntity))
            return false;
        return apply(livingEntity);
    }

    protected abstract boolean apply(LivingEntity living);

    public static List<MPRProperty> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPRProperty> properties = new ArrayList<>();
        if (!jObject.has(memberName))
            return properties;
        JsonArray aProperties = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aProperties) {
            JsonObject jObjectProperty = jsonElement.getAsJsonObject();
            ResourceLocation propertyId = MPR.locationFrom(GsonHelper.getAsString(jObjectProperty, "property"));
            Type propertyType = PropertiesRegistry.get(propertyId);
            if (propertyType == null) {
                Logger.warn("property %s does not exist. Skipping".formatted(propertyId));
                continue;
            }
            properties.add(context.deserialize(jObjectProperty, propertyType));
        }
        return properties;
    }
}
