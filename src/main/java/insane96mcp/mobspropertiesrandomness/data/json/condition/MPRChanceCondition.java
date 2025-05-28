package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRChanceCondition.Serializer.class)
public class MPRChanceCondition extends MPRCondition {
    public MPRModifiableValue chance;

    public MPRChanceCondition(MPRModifiableValue chance, boolean inverted) {
        super(inverted);
        this.chance = chance;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        return living.getRandom().nextDouble() < this.chance.getValue(living);
    }

    public static class Serializer implements JsonDeserializer<MPRChanceCondition>, JsonSerializer<MPRChanceCondition> {
        @Override
        public MPRChanceCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            MPRModifiableValue chance = context.deserialize(jObject.get("chance"), MPRModifiableValue.class);
            return new MPRChanceCondition(chance, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRChanceCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("chance", context.serialize(src.chance));
            return src.endSerialization(jObject);
        }
    }
}
