package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRHardcoreCondition.Serializer.class)
public class MPRHardcoreCondition extends MPRCondition {
    public MPRHardcoreCondition(boolean inverted) {
        super(inverted);
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        //noinspection DataFlowIssue - The mod's server sided
        return living.level().getServer().isHardcore();
    }

    public static class Serializer implements JsonDeserializer<MPRHardcoreCondition>, JsonSerializer<MPRHardcoreCondition> {
        @Override
        public MPRHardcoreCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRHardcoreCondition(MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRHardcoreCondition src, Type typeOfSrc, JsonSerializationContext context) {
            return src.endSerialization(new JsonObject());
        }
    }
}
