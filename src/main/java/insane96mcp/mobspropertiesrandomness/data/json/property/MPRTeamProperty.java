package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.LogHelper;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRTeamProperty.Serializer.class)
public class MPRTeamProperty extends MPRProperty {
    public String team;

    public MPRTeamProperty(String team, List<MPRCondition> conditions) {
        super(conditions);
        this.team = team;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        if (living.level().getServer() == null)
            return false;
        ServerScoreboard scoreboard = living.level().getServer().getScoreboard();
        PlayerTeam playerTeam = scoreboard.getPlayerTeam(this.team);
        if (playerTeam == null)
            LogHelper.warn("Failed to find team %s. Ignored", this.team);
        else
            scoreboard.addPlayerToTeam(living.getScoreboardName(), playerTeam);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRTeamProperty>, JsonSerializer<MPRTeamProperty> {
        @Override
        public MPRTeamProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRTeamProperty(GsonHelper.getAsString(jObject, "team"), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRTeamProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("team", src.team);
            return src.endSerialization(jObject, context);
        }
    }
}
