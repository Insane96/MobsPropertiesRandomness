package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

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
    public boolean conditionCheck(LivingEntity livingEntity) {
        return livingEntity.getRandom().nextDouble() < this.chance.getValue(livingEntity);
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
            JsonObject jObject = src.serializeInverted();
            jObject.add("chance", context.serialize(src.chance));
            return jObject;
        }
    }
}
