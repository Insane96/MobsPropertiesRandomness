package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRDeepnessModifier.Serializer.class)
public class MPRDeepnessModifier extends MPRModifier {
    @Nullable
    public MPRModifiableValue startingAmount;
    public MPRRange amountPerStep;
    public MPRModifiableValue step;
    public MPRModifiableValue startingY;

    public MPRDeepnessModifier(@Nullable MPRModifiableValue startingAmount, MPRRange amountPerStep, MPRModifiableValue step, MPRModifiableValue startingY, Operation operation, List<MPRCondition> conditions) {
        super(operation, conditions);
        this.startingAmount = startingAmount;
        this.amountPerStep = amountPerStep;
        this.step = step;
        this.startingY = startingY;
    }

    @Override
    protected double getModifier(LivingEntity living) {
        double modifier = (Math.max(this.startingY.getValue(living) - living.blockPosition().getY(), 0)
                / this.step.getValue(living))
                * this.amountPerStep.getDoubleBetween(living);
        if (this.startingAmount != null)
            modifier += this.startingAmount.getValue(living);
        return modifier;
    }

    public static class Serializer implements JsonDeserializer<MPRDeepnessModifier>, JsonSerializer<MPRDeepnessModifier> {
        @Override
        public MPRDeepnessModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRDeepnessModifier(
                    GsonHelper.getAsObject(jObject, "starting_amount", null, context, MPRModifiableValue.class),
                    GsonHelper.getAsObject(jObject, "amount_per_step", context, MPRRange.class),
                    GsonHelper.getAsObject(jObject, "step", context, MPRModifiableValue.class),
                    GsonHelper.getAsObject(jObject, "starting_y", context, MPRModifiableValue.class),
                    deserializeOperation(jObject, context),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRDeepnessModifier src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("starting_amount", context.serialize(src.startingAmount));
            jObject.add("amount_per_step", context.serialize(src.amountPerStep));
            jObject.add("step", context.serialize(src.step));
            jObject.add("starting_y", context.serialize(src.startingY));
            return src.endSerialization(jObject, context);
        }
    }
}
