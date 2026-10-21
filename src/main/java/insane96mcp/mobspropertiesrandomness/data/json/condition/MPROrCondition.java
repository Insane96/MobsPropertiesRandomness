package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPROrCondition.Serializer.class)
public class MPROrCondition extends MPRCondition {
    protected List<MPRCondition> conditions;

    public MPROrCondition(List<MPRCondition> conditions, boolean inverted) {
        super(inverted);
        this.conditions = conditions;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living, LivingEntity self, @Nullable LivingEntity other) {
        for (MPRCondition condition : this.conditions) {
            if (condition.conditionApplies(self, other))
                return true;
        }
        return false;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        return conditionCheck(living, living, null);
    }

    public static class Serializer implements JsonDeserializer<MPROrCondition>, JsonSerializer<MPROrCondition> {
        @Override
        public MPROrCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPROrCondition(MPRCondition.deserializeList(jObject, "conditions", context), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPROrCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("conditions", context.serialize(src.conditions));
            return src.endSerialization(jObject);
        }
    }
}
