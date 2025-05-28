package insane96mcp.mobspropertiesrandomness.util;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SerializerUtils {
    public static <T> List<T> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context, Class<T> clazz) throws JsonParseException {
        return deserializeList(jObject, memberName, context, clazz, true);
    }

    public static <T> List<T> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context, Class<T> clazz, boolean required) throws JsonParseException {
        if (!jObject.has(memberName)) {
            if (required)
                throw new JsonParseException("Missing %s array".formatted(memberName));
            else
                return new ArrayList<>();
        }
        if (jObject.get(memberName).isJsonObject())
            return Collections.singletonList(context.deserialize(jObject.get(memberName).getAsJsonObject(), clazz));
        else if (jObject.get(memberName).isJsonPrimitive())
            return Collections.singletonList(context.deserialize(jObject.get(memberName), clazz));
        else if (jObject.get(memberName).isJsonArray()) {
            JsonArray jsonArray = jObject.getAsJsonArray(memberName);
            if (jsonArray == null)
                return new ArrayList<>();
            List<T> list = new ArrayList<>();
            for (JsonElement el : jsonArray) {
                T item = context.deserialize(el, clazz);
                list.add(item);
            }
            return list;
        }
        else
            throw new JsonParseException("Expected %s to be a JsonObject or JsonArray".formatted(memberName));
    }

    public static List<ResourceLocation> deserializeLocationList(JsonObject jObject, String memberName, JsonDeserializationContext context) throws JsonParseException {
        List<String> list = deserializeList(jObject, memberName, context, String.class);
        List<ResourceLocation> locations = new ArrayList<>(list.size());
        for (String loc : list) {
            locations.add(ResourceLocation.parse(loc));
        }
        return locations;
    }

    public static void serializeLocationList(JsonObject jObject, String memberName, JsonSerializationContext context, List<ResourceLocation> list) {
        JsonArray jsonArray = new JsonArray();
        for (ResourceLocation loc : list) {
            jsonArray.add(loc.toString());
        }
        jObject.add(memberName, jsonArray);
    }
}
