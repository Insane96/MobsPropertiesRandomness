package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRProperty;

import java.lang.reflect.Type;
import java.util.List;

public class MPRProperties {
    List<MPRProperty> properties;
    List<MPRCondition> conditions;

    public MPRProperties(List<MPRProperty> properties, List<MPRCondition> conditions) {
        this.properties = properties;
        this.conditions = conditions;
    }

    public static List<MPRProperty> deserializeProperties(JsonObject jObject, JsonDeserializationContext context) { return MPRProperty.deserializeList(jObject, "properties", context); }

    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context) {
        jObject.add("properties", context.serialize(this.properties));
        jObject.add("conditions", context.serialize(this.conditions));
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
