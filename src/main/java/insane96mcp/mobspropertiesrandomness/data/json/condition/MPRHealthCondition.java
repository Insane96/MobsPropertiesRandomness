package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRHealthCondition.Serializer.class)
public class MPRHealthCondition extends MPRCondition {
    protected MPRRange health;

    public MPRHealthCondition(MPRRange health, boolean inverted) {
        super(inverted);
        this.health = health;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        return this.health.isBetween(living, living.getHealth() / living.getMaxHealth());
    }

    public static class Serializer implements JsonDeserializer<MPRHealthCondition>, JsonSerializer<MPRHealthCondition> {
        @Override
        public MPRHealthCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRHealthCondition(GsonHelper.getAsObject(jObject, "health", context, MPRRange.class), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRHealthCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("health", context.serialize(src.health));
            return src.endSerialization(jObject);
        }
    }
}
