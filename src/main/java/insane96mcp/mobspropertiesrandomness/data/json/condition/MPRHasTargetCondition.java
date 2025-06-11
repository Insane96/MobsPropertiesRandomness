package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.lang.reflect.Type;

@JsonAdapter(MPRHasTargetCondition.Serializer.class)
public class MPRHasTargetCondition extends MPRCondition {
    public MPRHasTargetCondition(boolean inverted) {
        super(inverted);
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        if (!(living instanceof Mob mob))
            return false;
        return mob.getTarget() != null;
    }

    public static class Serializer implements JsonDeserializer<MPRHasTargetCondition>, JsonSerializer<MPRHasTargetCondition> {
        @Override
        public MPRHasTargetCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRHasTargetCondition(MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRHasTargetCondition src, Type typeOfSrc, JsonSerializationContext context) {
            return src.endSerialization(new JsonObject());
        }
    }
}
