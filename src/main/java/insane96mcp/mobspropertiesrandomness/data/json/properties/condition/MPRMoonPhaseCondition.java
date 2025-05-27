package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRMoonPhaseCondition.Serializer.class)
public class MPRMoonPhaseCondition extends MPRCondition {
    protected List<MoonPhase> moonPhases;

    public MPRMoonPhaseCondition(List<MoonPhase> moonPhases, boolean inverted) {
        super(inverted);
        this.moonPhases = moonPhases;
    }

    @Override
    protected boolean conditionCheck(LivingEntity livingEntity) {
        for (MoonPhase moonPhase : this.moonPhases) {
            if (moonPhase == MoonPhase.of(livingEntity.level().getMoonPhase()))
                return true;
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRMoonPhaseCondition>, JsonSerializer<MPRMoonPhaseCondition> {
        @Override
        public MPRMoonPhaseCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            JsonArray aMoonPhases = jObject.getAsJsonArray("moon_phases");
            if (aMoonPhases == null)
                throw new JsonParseException("Missing moon_phases array");
            Type listType = new TypeToken<List<MoonPhase>>() {}.getType();
            List<MoonPhase> values = context.deserialize(aMoonPhases, listType);
            return new MPRMoonPhaseCondition(values, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRMoonPhaseCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("moon_phases", context.serialize(src.moonPhases));
            return src.serializeInverted();
        }
    }

    public enum MoonPhase {
        @SerializedName("full_moon")
        FULL_MOON,
        @SerializedName("waning_gibbous")
        WANING_GIBBOUS,
        @SerializedName("last_quarter")
        LAST_QUARTER,
        @SerializedName("waning_crescent")
        WANING_CRESCENT,
        @SerializedName("new_moon")
        NEW_MOON,
        @SerializedName("waxing_crescent")
        WAXING_CRESCENT,
        @SerializedName("first_quarter")
        FIRST_QUARTER,
        @SerializedName("waxing_gibbous")
        WAXING_GIBBOUS;

        private static final MoonPhase[] PHASES = MoonPhase.values();

        public static MoonPhase of(int moonPhase) {
            return PHASES[moonPhase];
        }
    }
}
