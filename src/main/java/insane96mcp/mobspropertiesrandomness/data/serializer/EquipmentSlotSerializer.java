package insane96mcp.mobspropertiesrandomness.data.serializer;

import com.google.gson.*;
import net.minecraft.world.entity.EquipmentSlot;

import java.lang.reflect.Type;

public class EquipmentSlotSerializer implements JsonSerializer<EquipmentSlot>, JsonDeserializer<EquipmentSlot> {
    @Override
    public EquipmentSlot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(json));
        try {
            return EquipmentSlot.byName(json.getAsString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid EquipmentSlot name: " + json.getAsString());
        }
    }

    @Override
    public JsonElement serialize(EquipmentSlot src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getName());
    }
}
