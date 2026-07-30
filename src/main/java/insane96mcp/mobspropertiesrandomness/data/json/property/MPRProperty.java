package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRProperty.Serializer.class)
public class MPRProperty extends MPRConditionable {
    public MPRProperty(List<MPRCondition> conditions) {
        super(conditions);
    }

    /// Checks for conditions and applies the property
    public boolean tryApply(LivingEntity livingEntity) {
        if (!MPRCondition.conditionsApply(this.conditions, livingEntity))
            return false;
        return apply(livingEntity);
    }

    /// Applies the property
    public boolean apply(LivingEntity living) {
        throw new UnsupportedOperationException("apply(LivingEntity) not implemented");
    }

    @Nullable
    public static MPRProperty deserialize(JsonElement element, JsonDeserializationContext context) {
        JsonObject jObjectProperty = element.getAsJsonObject();
        ResourceLocation propertyId = MPR.locationFrom(GsonHelper.getAsString(jObjectProperty, "property"));
        Type propertyType = PropertiesRegistry.get(propertyId);
        if (propertyType == null) {
            MPRLogger.warn("property %s does not exist. Skipping".formatted(propertyId));
            return null;
        }
        return context.deserialize(jObjectProperty, propertyType);
    }

    public static List<MPRProperty> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPRProperty> properties = new ArrayList<>();
        if (!jObject.has(memberName))
            return properties;
        JsonArray aProperties = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aProperties) {
            MPRProperty property = deserialize(jsonElement, context);
            if (property == null)
                continue;
            properties.add(property);
        }
        return properties;
    }

    public static class Serializer implements JsonDeserializer<MPRProperty>, JsonSerializer<MPRProperty> {
        @Override
        public MPRProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new MPRProperty(MPRCondition.deserializeList(json.getAsJsonObject(), "conditions", context));
        }

        @Override
        public JsonElement serialize(MPRProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            return src.endSerialization(jObject, context);
        }
    }
}
