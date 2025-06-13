package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRWeatherCondition.Serializer.class)
public class MPRWeatherCondition extends MPRCondition {
    Weather weather;

    public MPRWeatherCondition(Weather weather, boolean inverted) {
        super(inverted);
        this.weather = weather;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        return living.level().isRaining() && this.weather == Weather.RAIN
                || living.level().isThundering() && this.weather == Weather.THUNDER
                || !living.level().isRaining() && this.weather == Weather.CLEAR;
    }

    public static class Serializer implements JsonDeserializer<MPRWeatherCondition>, JsonSerializer<MPRWeatherCondition> {
        @Override
        public MPRWeatherCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRWeatherCondition(GsonHelper.getAsObject(jObject, "weather", context, Weather.class), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRWeatherCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("weather", context.serialize(src.weather));
            return src.endSerialization(jObject);
        }
    }

    public enum Weather {
        @SerializedName("clear")
        CLEAR,
        @SerializedName("rain")
        RAIN,
        @SerializedName("snow")
        THUNDER
    }
}
