package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRDayTimeCondition.Serializer.class)
public class MPRDayTimeCondition extends MPRCondition {
    protected MPRRange dayTime;

    public MPRDayTimeCondition(MPRRange dayTime, boolean inverted) {
        super(inverted);
        this.dayTime = dayTime;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        return this.dayTime.isBetween(living, living.level().getDayTime() % 24000f);
    }

    public static class Serializer implements JsonDeserializer<MPRDayTimeCondition>, JsonSerializer<MPRDayTimeCondition> {
        @Override
        public MPRDayTimeCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRDayTimeCondition(context.deserialize(jObject.get("day_time"), MPRRange.class), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRDayTimeCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("day_time", context.serialize(src.dayTime));
            return src.endSerialization(jObject);
        }
    }
}
