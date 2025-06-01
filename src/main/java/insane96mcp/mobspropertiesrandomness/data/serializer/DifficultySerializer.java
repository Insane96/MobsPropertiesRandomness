package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.*;
import net.minecraft.world.Difficulty;

import java.lang.reflect.Type;

public class DifficultySerializer implements JsonSerializer<Difficulty>, JsonDeserializer<Difficulty> {
    @Override
    public Difficulty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(json));
        try {
            return Difficulty.byName(json.getAsString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid Difficulty name: " + json.getAsString());
        }
    }

    @Override
    public JsonElement serialize(Difficulty src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getKey());
    }
}
