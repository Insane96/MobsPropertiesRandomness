package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRHealthLeftCondition.Serializer.class)
public class MPRHealthLeftCondition extends MPRCondition {
    protected MPRRange health;
    protected boolean flat;

    public MPRHealthLeftCondition(MPRRange health, boolean flat, boolean inverted) {
        super(inverted);
        this.health = health;
        this.flat = flat;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
       float healthLeft = living.getHealth();
       return this.flat ? this.health.isBetween(living, healthLeft) : this.health.isBetween(living, healthLeft / living.getMaxHealth());
    }

    public static class Serializer implements JsonDeserializer<MPRHealthLeftCondition>, JsonSerializer<MPRHealthLeftCondition> {
        @Override
        public MPRHealthLeftCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRHealthLeftCondition(
                    context.deserialize(jObject.get("health"), MPRRange.class),
                    GsonHelper.getAsBoolean(jObject, "flat", false),
                    MPRCondition.deserializeInverted(jObject)
            );
        }

        @Override
        public JsonElement serialize(MPRHealthLeftCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("distance", context.serialize(src.health));
            if (src.flat)
                jObject.addProperty("flat", true);
            return src.endSerialization(jObject);
        }
    }
}
