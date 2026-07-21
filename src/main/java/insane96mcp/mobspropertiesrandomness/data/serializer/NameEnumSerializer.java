package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.function.Function;

public class NameEnumSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private final String typeName;
    private final Function<String, T> decode;
    private final Function<T, String> encode;

    public NameEnumSerializer(String typeName, Function<String, T> decode, Function<T, String> encode) {
        this.typeName = typeName;
        this.decode = decode;
        this.encode = encode;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(json));
        try {
            return decode.apply(json.getAsString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid %s name: %s".formatted(typeName, json.getAsString()));
        }
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(encode.apply(src));
    }
}
