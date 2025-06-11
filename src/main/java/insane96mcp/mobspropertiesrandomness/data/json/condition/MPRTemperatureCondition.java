package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.mixin.BiomeAccessor;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRTemperatureCondition.Serializer.class)
public class MPRTemperatureCondition extends MPRCondition {
    //> 0.15 rain, otherwise snow
    MPRRange temperature;

    public MPRTemperatureCondition(MPRRange temperature, boolean inverted) {
        super(inverted);
        this.temperature = temperature;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        return this.temperature.isBetween(living, ((BiomeAccessor) (Object) living.level().getBiome(living.blockPosition()).get()).invokeGetTemperature(living.blockPosition()));
    }

    public static class Serializer implements JsonDeserializer<MPRTemperatureCondition>, JsonSerializer<MPRTemperatureCondition> {
        @Override
        public MPRTemperatureCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRTemperatureCondition(GsonHelper.getAsObject(jObject, "temperature", context, MPRRange.class), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRTemperatureCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("temperature", context.serialize(src.temperature));
            return src.endSerialization(jObject);
        }
    }
}
