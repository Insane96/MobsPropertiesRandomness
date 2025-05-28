package insane96mcp.mobspropertiesrandomness.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SerializerUtils {
    public static <T> List<T> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context, Class<T> clazz) throws JsonParseException {
        return deserializeList(jObject, memberName, context, clazz, true);
    }

    public static <T> List<T> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context, Type listType, boolean required) throws JsonParseException {
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
            return context.deserialize(jsonArray, listType);
        }
        else
            throw new JsonParseException("Expected %s to be a JsonObject or JsonArray".formatted(memberName));
    }
}
