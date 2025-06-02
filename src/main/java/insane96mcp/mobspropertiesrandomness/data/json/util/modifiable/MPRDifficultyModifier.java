package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.MPRModifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRDifficultyModifier.Serializer.class)
public class MPRDifficultyModifier extends MPRModifier {
    @Nullable
    public MPRModifiableValue easy;
    @Nullable
    public MPRModifiableValue normal;
    @Nullable
    public MPRModifiableValue hard;

    public MPRDifficultyModifier(@Nullable MPRModifiableValue easy, @Nullable MPRModifiableValue normal, @Nullable MPRModifiableValue hard, Operation operation, List<MPRCondition> conditions) {
        super(operation, conditions);
        this.easy = easy;
        this.normal = normal;
        this.hard = hard;
    }

    @Override
    protected double getModifier(LivingEntity living) {
        Difficulty worldDifficulty = living.level().getDifficulty();
        double modifier = this.operation == Operation.ADD ? 0d : 1d;
        if (worldDifficulty == Difficulty.EASY && this.easy != null)
            modifier = this.easy.getValue(living);
        else if (worldDifficulty == Difficulty.NORMAL && this.normal != null)
            modifier = this.normal.getValue(living);
        else if (worldDifficulty == Difficulty.HARD && this.hard != null)
            modifier = this.hard.getValue(living);
        return modifier;
    }

    public static class Serializer implements JsonDeserializer<MPRDifficultyModifier>, JsonSerializer<MPRDifficultyModifier> {
        @Override
        public MPRDifficultyModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRDifficultyModifier(
                    GsonHelper.getAsObject(jObject, "easy", null, context, MPRModifiableValue.class),
                    GsonHelper.getAsObject(jObject, "normal", null, context, MPRModifiableValue.class),
                    GsonHelper.getAsObject(jObject, "hard", null, context, MPRModifiableValue.class),
                    deserializeOperation(jObject, context),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRDifficultyModifier src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("easy", context.serialize(src.easy));
            jObject.add("normal", context.serialize(src.normal));
            jObject.add("hard", context.serialize(src.hard));
            return src.endSerialization(jObject, context);
        }
    }
}
