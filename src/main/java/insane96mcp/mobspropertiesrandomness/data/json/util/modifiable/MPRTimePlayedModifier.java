package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRTimePlayedModifier.Serializer.class)
public class MPRTimePlayedModifier extends MPRModifier {
    public MPRRange amountPerMinutes;
    public MPRModifiableValue minutes;
    public MPRModifiableValue cap;
    public Mode mode;

    public MPRTimePlayedModifier(MPRRange amountPerMinutes, MPRModifiableValue minutes, MPRModifiableValue cap, Mode mode, Operation operation, List<MPRCondition> conditions) {
        super(operation, conditions);
        this.amountPerMinutes = amountPerMinutes;
        this.minutes = minutes;
        this.cap = cap;
        this.mode = mode;
    }

    @Override
    protected double getModifier(LivingEntity living) {
        List<ServerPlayer> players = new ArrayList<>();
        double modifier = this.getNoModifier();
        if (this.mode == Mode.NEAREST) {
            ServerPlayer nearestPlayer = (ServerPlayer) living.level().getNearestPlayer(living, 128d);
            if (nearestPlayer == null)
                return modifier;
            players.add(nearestPlayer);
        }
        else {
            players = living.level().getEntitiesOfClass(ServerPlayer.class, living.getBoundingBox().inflate(128d));
            if (players.isEmpty())
                return modifier;
        }
        for (ServerPlayer player : players) {
            int ticksPlayed = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
            double minutesPlayed = ticksPlayed / 20d / 60d;
            modifier += (minutesPlayed / this.minutes.getValue(living)) * this.amountPerMinutes.getDoubleBetween(living);
        }

        if (this.mode == Mode.AVERAGE)
            modifier /= players.size();

        if (this.cap != null)
            modifier = Math.min(modifier, this.cap.getValue(living));

        return modifier + this.getNoModifier();
    }

    public static class Serializer implements JsonDeserializer<MPRTimePlayedModifier>, JsonSerializer<MPRTimePlayedModifier> {
        @Override
        public MPRTimePlayedModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRTimePlayedModifier(
                    GsonHelper.getAsObject(jObject, "amount_per_minutes", context, MPRRange.class),
                    GsonHelper.getAsObject(jObject, "minutes", context, MPRModifiableValue.class),
                    GsonHelper.getAsObject(jObject, "cap", new MPRModifiableValue(Double.MAX_VALUE), context, MPRModifiableValue.class),
                    GsonHelper.getAsObject(jObject, "mode", context, Mode.class),
                    deserializeOperation(jObject, context),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRTimePlayedModifier src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("amount_per_minutes", context.serialize(src.amountPerMinutes));
            jObject.add("minutes", context.serialize(src.minutes));
            jObject.add("cap", context.serialize(src.cap));
            jObject.add("mode", context.serialize(src.mode));
            return src.endSerialization(jObject, context);
        }
    }

    public enum Mode {
        @SerializedName("average")
        AVERAGE,
        @SerializedName("sum")
        SUM,
        @SerializedName("nearest")
        NEAREST
    }
}
