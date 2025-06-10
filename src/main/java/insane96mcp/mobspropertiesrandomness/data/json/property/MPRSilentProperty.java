package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRSilentProperty.Serializer.class)
public class MPRSilentProperty extends MPRProperty {
    public MPRSilentProperty(List<MPRCondition> conditions) {
        super(conditions);
    }

    @Override
    protected boolean apply(LivingEntity living) {
        living.setSilent(true);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRSilentProperty>, JsonSerializer<MPRSilentProperty> {
        @Override
        public MPRSilentProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRSilentProperty(MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRSilentProperty src, Type typeOfSrc, JsonSerializationContext context) {
            return src.endSerialization(new JsonObject(), context);
        }
    }
}
