package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRHasOwnerCondition.Serializer.class)
public class MPRHasOwnerCondition extends MPRCondition {
    public MPRHasOwnerCondition(boolean inverted) {
        super(inverted);
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        return living instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() != null;
    }

    public static class Serializer implements JsonDeserializer<MPRHasOwnerCondition>, JsonSerializer<MPRHasOwnerCondition> {
        @Override
        public MPRHasOwnerCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRHasOwnerCondition(MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRHasOwnerCondition src, Type typeOfSrc, JsonSerializationContext context) {
            return src.endSerialization(new JsonObject());
        }
    }
}
