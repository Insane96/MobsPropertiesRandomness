package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@JsonAdapter(MPRScoreCondition.Serializer.class)
public class MPRScoreCondition extends MPRCondition {
    public String objective;
    public MPRRange value;
    @Nullable
    public String player;

    public MPRScoreCondition(String objective, MPRRange value, @Nullable String player, boolean inverted) {
        super(inverted);
        this.objective = objective;
        this.value = value;
        this.player = player;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        MinecraftServer server = living.getServer();
        if (server == null)
            return false;
        Objective objective = server.getScoreboard().getObjective(this.objective);
        if (objective == null)
            return false;
        ScoreHolder scoreHolder = this.player != null ? ScoreHolder.forNameOnly(this.player) : living;

        ScoreAccess score = server.getScoreboard().getOrCreatePlayerScore(scoreHolder, objective);

        return this.value.isBetween(living, score.get());
    }

    public static class Serializer implements JsonDeserializer<MPRScoreCondition>, JsonSerializer<MPRScoreCondition> {
        @Override
        public MPRScoreCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRScoreCondition(
                    GsonHelper.getAsString(jObject, "objective"),
                    GsonHelper.getAsObject(jObject, "value", context, MPRRange.class),
                    GsonHelper.getAsString(jObject, "player", null),
                    MPRCondition.deserializeInverted(jObject)
            );
        }

        @Override
        public JsonElement serialize(MPRScoreCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("objective", src.objective);
            jObject.add("value", context.serialize(src.value));
            if (src.player != null)
                jObject.addProperty("player", src.player);
            return src.endSerialization(jObject);
        }
    }

}
