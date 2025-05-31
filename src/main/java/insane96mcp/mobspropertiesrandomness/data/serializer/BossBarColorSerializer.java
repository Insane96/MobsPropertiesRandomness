package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.*;
import net.minecraft.world.BossEvent;

import java.lang.reflect.Type;

public class BossBarColorSerializer implements JsonSerializer<BossEvent.BossBarColor>, JsonDeserializer<BossEvent.BossBarColor> {
    @Override
    public BossEvent.BossBarColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(json));
        try {
            return BossEvent.BossBarColor.byName(json.getAsString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid BossBarColor name: " + json.getAsString());
        }
    }

    @Override
    public JsonElement serialize(BossEvent.BossBarColor src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getName());
    }
}
