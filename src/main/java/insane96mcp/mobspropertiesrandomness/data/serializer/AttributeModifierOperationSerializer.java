package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.lang.reflect.Type;

public class AttributeModifierOperationSerializer implements JsonSerializer<AttributeModifier.Operation>, JsonDeserializer<AttributeModifier.Operation> {
    @Override
    public AttributeModifier.Operation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(json));
        String name = json.getAsString().toLowerCase();
        try {
            return AttributeModifier.Operation.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid AttributeModifier.Operation name: " + json.getAsString());
        }
    }

    @Override
    public JsonElement serialize(AttributeModifier.Operation src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jObject = new JsonObject();
        jObject.addProperty("operation", src.name().toLowerCase());
        return jObject;
    }

}
