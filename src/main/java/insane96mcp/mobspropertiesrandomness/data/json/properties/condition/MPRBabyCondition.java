package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRBabyCondition.Serializer.class)
public class MPRBabyCondition extends MPRCondition {
    public MPRBabyCondition(boolean inverted) {
        super(inverted);
    }

    @Override
    public boolean conditionApplies(LivingEntity livingEntity) {
        return tryInvert(livingEntity.isBaby());
    }

    public static class Serializer implements JsonDeserializer<MPRBabyCondition>, JsonSerializer<MPRBabyCondition> {
        @Override
        public MPRBabyCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRBabyCondition(MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRBabyCondition src, Type typeOfSrc, JsonSerializationContext context) {
            return src.serializeInverted();
        }
    }
}
