package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.lang.reflect.Type;

public class AttributeModifierOperationSerializer implements JsonSerializer<AttributeModifier.Operation>, JsonDeserializer<AttributeModifier.Operation> {
    @Override
    public AttributeModifier.Operation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(json));
        return switch (json.getAsString()) {
            case "addition" -> AttributeModifier.Operation.ADDITION;
            case "multiply_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
            case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            default -> throw new JsonParseException("Invalid operation: " + json.getAsString());
        };
    }

    @Override
    public JsonElement serialize(AttributeModifier.Operation src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jObject = new JsonObject();
        jObject.addProperty("operation", switch (src) {
            case ADDITION -> "addition";
            case MULTIPLY_BASE -> "multiply_base";
            case MULTIPLY_TOTAL -> "multiply_total";
        });
        return jObject;
    }

}
