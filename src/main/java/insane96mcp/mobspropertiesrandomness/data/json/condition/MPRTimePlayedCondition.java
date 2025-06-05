package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.PlayerMode;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRTimePlayedCondition.Serializer.class)
public class MPRTimePlayedCondition extends MPRCondition {
    protected MPRRange minutesPlayed;
    protected PlayerMode mode;

    public MPRTimePlayedCondition(MPRRange minutesPlayed, PlayerMode mode, boolean inverted) {
        super(inverted);
        this.minutesPlayed = minutesPlayed;
        this.mode = mode;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        List<ServerPlayer> players = new ArrayList<>();
        if (this.mode == PlayerMode.NEAREST) {
            ServerPlayer nearestPlayer = (ServerPlayer) living.level().getNearestPlayer(living, 128d);
            if (nearestPlayer == null)
                return false;
            players.add(nearestPlayer);
        }
        else {
            players = living.level().getEntitiesOfClass(ServerPlayer.class, living.getBoundingBox().inflate(128d));
            if (players.isEmpty())
                return false;
        }

        int ticksPlayed = players.get(0).getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
        double minutesPlayed = ticksPlayed / 20d / 60d;
        return this.minutesPlayed.isBetween(living, minutesPlayed);
    }

    public static class Serializer implements JsonDeserializer<MPRTimePlayedCondition>, JsonSerializer<MPRTimePlayedCondition> {
        @Override
        public MPRTimePlayedCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            PlayerMode playerMode = PlayerMode.NEAREST;
            if (jObject.has("player"))
                playerMode = context.deserialize(jObject.get("player"), PlayerMode.class);
            return new MPRTimePlayedCondition(
                    GsonHelper.getAsObject(jObject, "minutes_played", context, MPRRange.class),
                    playerMode,
                    MPRCondition.deserializeInverted(jObject)
            );
        }

        @Override
        public JsonElement serialize(MPRTimePlayedCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("minutes_played", context.serialize(src.minutesPlayed));
            jObject.add("player", context.serialize(src.minutesPlayed));
            return src.endSerialization(jObject);
        }
    }
}
