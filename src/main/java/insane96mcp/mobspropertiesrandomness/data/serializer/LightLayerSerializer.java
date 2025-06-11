package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.*;
import net.minecraft.world.level.LightLayer;

import java.lang.reflect.Type;

public class LightLayerSerializer implements JsonSerializer<LightLayer>, JsonDeserializer<LightLayer> {
    @Override
    public LightLayer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(json));
        switch (json.getAsString()) {
            case "sky" -> {
                return LightLayer.SKY;
            }
            case "block" -> {
                return LightLayer.BLOCK;
            }
            default -> throw new JsonParseException("Invalid Light Type name: " + json.getAsString());
        }
    }

    @Override
    public JsonElement serialize(LightLayer src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString().toLowerCase());
    }
}
