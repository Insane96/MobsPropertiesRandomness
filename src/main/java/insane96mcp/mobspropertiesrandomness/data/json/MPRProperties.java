package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;

import java.lang.reflect.Type;
import java.util.Arrays;

public class MPRProperties implements IMPRObject, JsonDeserializer<MPRProperties>, JsonSerializer<MPRProperties> {

    @JsonObject
    public MPRModifiableValue silent;

    @Override
    public String getName() {
        return "Properties";
    }

    @Override
    public MPRProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Arrays.stream(this.getClass().getFields()).forEach(field -> {
            if (field.getAnnotation(JsonObject.class) != null) {
                field.set(this, context.deserialize());
            }
        });
        return null;
    }

    @Override
    public JsonElement serialize(MPRProperties src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
