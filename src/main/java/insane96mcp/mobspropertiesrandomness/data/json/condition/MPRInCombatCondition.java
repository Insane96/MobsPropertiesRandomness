package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRInCombatCondition.Serializer.class)
public class MPRInCombatCondition extends MPRCondition {
    public static final MPRModifiableValue DEFAULT_SECONDS = new MPRModifiableValue(5d);

    protected MPRModifiableValue seconds;

    public MPRInCombatCondition(MPRModifiableValue seconds, boolean inverted) {
        super(inverted);
        this.seconds = seconds;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        int ticks = (int) (this.seconds.getValue(living) * 20);
        return isRecent(living, living.getLastHurtByMobTimestamp(), ticks)
                || isRecent(living, living.getLastHurtMobTimestamp(), ticks);
    }

    /**
     * Timestamps are 0 when the entity never hurt/was hurt and tickCount is not saved, so a timestamp in the "future" (after a reload) is not considered recent
     */
    private static boolean isRecent(LivingEntity living, int timestamp, int ticks) {
        if (timestamp <= 0)
            return false;
        int elapsed = living.tickCount - timestamp;
        return elapsed >= 0 && elapsed <= ticks;
    }

    public static class Serializer implements JsonDeserializer<MPRInCombatCondition>, JsonSerializer<MPRInCombatCondition> {
        @Override
        public MPRInCombatCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            MPRModifiableValue seconds = GsonHelper.getAsObject(jObject, "seconds", DEFAULT_SECONDS, context, MPRModifiableValue.class);
            return new MPRInCombatCondition(seconds, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRInCombatCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (!src.seconds.equals(DEFAULT_SECONDS))
                jObject.add("seconds", context.serialize(src.seconds));
            return src.endSerialization(jObject);
        }
    }
}
