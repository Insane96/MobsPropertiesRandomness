package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRDaysPassedCondition.Serializer.class)
public class MPRDaysPassedCondition extends MPRCondition {
    protected MPRRange daysPassed;

    public MPRDaysPassedCondition(MPRRange daysPassed, boolean inverted) {
        super(inverted);
        this.daysPassed = daysPassed;
    }

    @Override
    protected boolean conditionCheck(LivingEntity livingEntity) {
        return this.daysPassed.isBetween(livingEntity, livingEntity.level().getGameTime() / 24000f);
    }

    public static class Serializer implements JsonDeserializer<MPRDaysPassedCondition>, JsonSerializer<MPRDaysPassedCondition> {
        @Override
        public MPRDaysPassedCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRDaysPassedCondition(context.deserialize(jObject.get("days_passed"), MPRRange.class), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRDaysPassedCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("days_passed", context.serialize(src.daysPassed));
            return src.endSerialization(jObject);
        }
    }
}
