package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.*;
import net.minecraft.world.entity.MobSpawnType;

import java.lang.reflect.Type;

public class MobSpawnTypeSerializer implements JsonSerializer<MobSpawnType>, JsonDeserializer<MobSpawnType> {
    @Override
    public MobSpawnType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(json));
        String name = json.getAsString().toLowerCase();
        try {
            return MobSpawnType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid MobSpawnType name: " + json.getAsString());
        }
    }

    @Override
    public JsonElement serialize(MobSpawnType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name().toLowerCase());
    }
}
