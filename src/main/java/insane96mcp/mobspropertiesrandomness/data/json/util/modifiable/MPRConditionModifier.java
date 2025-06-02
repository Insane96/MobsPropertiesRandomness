package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.MPRModifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRConditionModifier.Serializer.class)
public class MPRConditionModifier extends MPRModifier {
    public MPRModifiableValue value;

    public MPRConditionModifier(MPRModifiableValue value, Operation operation, List<MPRCondition> conditions) {
        super(operation, conditions);
        this.value = value;
    }

    @Override
    protected double getModifier(LivingEntity living) {
        return this.value.getValue(living);
    }

    public static class Serializer implements JsonDeserializer<MPRConditionModifier>, JsonSerializer<MPRConditionModifier> {
        @Override
        public MPRConditionModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRConditionModifier(
                    GsonHelper.getAsObject(jObject, "value", context, MPRModifiableValue.class),
                    deserializeOperation(jObject, context),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRConditionModifier src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("value", context.serialize(src.value));
            return src.endSerialization(jObject, context);
        }
    }
}
