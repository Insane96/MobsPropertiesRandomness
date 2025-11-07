package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.*;
import sereneseasons.api.season.Season;

import java.lang.reflect.Type;

public class SubSeasonSerializer implements JsonSerializer<Season.SubSeason>, JsonDeserializer<Season.SubSeason> {
    @Override
    public Season.SubSeason deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(json));
        String name = json.getAsString().toLowerCase();
        try {
            return Season.SubSeason.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid SubSeason name: " + json.getAsString());
        }
    }

    @Override
    public JsonElement serialize(Season.SubSeason src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString().toLowerCase());
    }
}
