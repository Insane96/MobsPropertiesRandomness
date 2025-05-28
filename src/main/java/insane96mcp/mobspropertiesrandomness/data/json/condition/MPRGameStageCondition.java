package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRGameStageCondition.Serializer.class)
public class MPRGameStageCondition extends MPRCondition {
	public List<String> gameStages;
	public PlayerMode player;

	public MPRGameStageCondition(List<String> gameStages, PlayerMode player, boolean inverted) {
		super(inverted);
		this.gameStages = gameStages;
		this.player = player;
	}

	@Override
	protected boolean conditionCheck(LivingEntity living) {
		List<ServerPlayer> players = new ArrayList<>();
		if (this.player == PlayerMode.NEAREST) {
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

		for (ServerPlayer player : players) {
			if (GameStageHelper.hasAllOf(player, this.gameStages))
				return true;
		}
		return false;
	}

	public static class Serializer implements JsonDeserializer<MPRGameStageCondition>, JsonSerializer<MPRGameStageCondition> {
		@Override
		public MPRGameStageCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			List<String> values = SerializerUtils.deserializeList(jObject, "game_stages", context, String.class);
			if (values.isEmpty())
				throw new JsonParseException("No game_stages specified for Game Stage Condition");
			PlayerMode playerMode = PlayerMode.NEAREST;
			if (jObject.has("player"))
				playerMode = context.deserialize(jObject.get("player"), PlayerMode.class);
			return new MPRGameStageCondition(values, playerMode, MPRCondition.deserializeInverted(jObject));
		}

		@Override
		public JsonElement serialize(MPRGameStageCondition src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("game_stages", context.serialize(src.gameStages));
			jObject.add("player", context.serialize(src.player));
			return src.endSerialization(jObject);
		}
	}

	public enum PlayerMode {
		@SerializedName("nearest")
		NEAREST,
		@SerializedName("any")
		ANY
	}
}
