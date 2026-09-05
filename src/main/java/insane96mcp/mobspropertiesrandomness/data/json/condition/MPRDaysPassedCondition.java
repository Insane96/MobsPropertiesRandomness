package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRDaysPassedCondition.Serializer.class)
public class MPRDaysPassedCondition extends MPRCondition {
    protected MPRRange days;

    public MPRDaysPassedCondition(MPRRange days, boolean inverted) {
        super(inverted);
        this.days = days;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        return this.days.isBetween(living, (double) living.level().getGameTime() / 24000L);
    }

    public static class Serializer implements JsonDeserializer<MPRDaysPassedCondition>, JsonSerializer<MPRDaysPassedCondition> {
        @Override
        public MPRDaysPassedCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRDaysPassedCondition(GsonHelper.getAsObject(jObject, "days", context, MPRRange.class), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRDaysPassedCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("days", context.serialize(src.days));
            return src.endSerialization(jObject);
        }
    }
}
