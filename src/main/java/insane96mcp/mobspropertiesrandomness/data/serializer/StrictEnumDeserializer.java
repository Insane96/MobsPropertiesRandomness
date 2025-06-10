package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

public class StrictEnumDeserializer<T extends Enum<T>> implements JsonDeserializer<T> {
    private final Class<T> clazz;

    public StrictEnumDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String sValue = json.getAsString();
        for (T constant : clazz.getEnumConstants()) {
            try {
                Field field = clazz.getField(constant.name());
                SerializedName annotation = field.getAnnotation(SerializedName.class);
                if (annotation != null) {
                    if (annotation.value().equals(sValue) || Arrays.asList(annotation.alternate()).contains(sValue)) {
                        return constant;
                    }
                } else if (constant.name().equals(sValue)) {
                    return constant;
                }
            } catch (NoSuchFieldException e) {
                // Shouldn't be possible
            }
        }
        throw new JsonParseException("Invalid enum value: " + sValue);
    }
}