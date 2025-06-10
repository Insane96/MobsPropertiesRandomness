package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRProperties.Serializer.class)
public class MPRProperties extends MPRConditionable {
    protected List<MPRProperty> properties;

    public MPRProperties(List<MPRProperty> properties, List<MPRCondition> conditions) {
        super(conditions);
        this.properties = properties;
    }

    public void tryApply(LivingEntity entity) {
        if (!MPRCondition.conditionsApply(this.conditions, entity))
            return;
        for (MPRProperty property : this.properties)
            property.tryApply(entity);
    }

    public static List<MPRProperty> deserializeProperties(JsonObject jObject, JsonDeserializationContext context) { return MPRProperty.deserializeList(jObject, "properties", context); }

    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context) {
        jObject.add("properties", context.serialize(this.properties));
        super.endSerialization(jObject, context);
        return jObject;
    }

    public static class Serializer implements JsonDeserializer<MPRProperties>, JsonSerializer<MPRProperties> {
        @Override
        public MPRProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRProperties(deserializeProperties(jObject, context), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRProperties src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            return src.endSerialization(jObject, context);
        }
    }
}
