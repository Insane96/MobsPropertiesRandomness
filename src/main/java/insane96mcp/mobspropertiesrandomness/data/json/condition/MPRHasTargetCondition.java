package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@JsonAdapter(MPRHasTargetCondition.Serializer.class)
public class MPRHasTargetCondition extends MPRCondition {
    @Nullable
    public MPRRange distance;

    public MPRHasTargetCondition(@Nullable MPRRange distance, boolean inverted) {
        super(inverted);
        this.distance = distance;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        if (!(living instanceof Mob mob))
            return false;
        if (mob.getTarget() == null)
            return false;
        return this.distance == null || this.distance.isBetween(mob, mob.distanceTo(mob.getTarget()));
    }

    public static class Serializer implements JsonDeserializer<MPRHasTargetCondition>, JsonSerializer<MPRHasTargetCondition> {
        @Override
        public MPRHasTargetCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRHasTargetCondition(GsonHelper.getAsObject(jObject, "distance", null, context, MPRRange.class), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRHasTargetCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (src.distance != null)
                jObject.add("distance", context.serialize(src.distance));
            return src.endSerialization(jObject);
        }
    }
}
