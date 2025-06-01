package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRDifficultyCondition.Serializer.class)
public class MPRDifficultyCondition extends MPRCondition {
    List<Difficulty> difficulties;

    public MPRDifficultyCondition(List<Difficulty> difficulties, boolean inverted) {
        super(inverted);
        this.difficulties = difficulties;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        for (Difficulty difficulty : this.difficulties) {
            if (living.level().getDifficulty() == difficulty)
                return true;
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRDifficultyCondition>, JsonSerializer<MPRDifficultyCondition> {
        @Override
        public MPRDifficultyCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRDifficultyCondition(SerializerUtils.deserializeList(jObject, "difficulties", context, Difficulty.class), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRDifficultyCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("difficulties", context.serialize(src.difficulties));
            return src.endSerialization(jObject);
        }
    }
}
