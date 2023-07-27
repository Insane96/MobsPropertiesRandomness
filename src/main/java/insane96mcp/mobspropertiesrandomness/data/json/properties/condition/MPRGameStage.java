package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class MPRGameStage implements IMPRObject {

	@SerializedName("game_stages")
	public List<String> gameStages;
	@SerializedName("player_mode")
	public PlayerMode playerMode;

	public MPRGameStage() {
		this.playerMode = PlayerMode.NEAREST;
	}

	@Override
	public void validate() throws JsonValidationException {
		if (this.gameStages == null || this.gameStages.size() == 0) {
			throw new JsonValidationException(String.format("Missing or empty game stages list. %s", this));
		}
	}

	public boolean conditionApplies(LivingEntity livingEntity) {
		List<ServerPlayer> players = new ArrayList<>();
		if (this.playerMode == PlayerMode.NEAREST)
			players.add((ServerPlayer) livingEntity.level().getNearestPlayer(livingEntity, 128d));
		else
			players = livingEntity.level().getEntitiesOfClass(ServerPlayer.class, livingEntity.getBoundingBox().inflate(128d));

		for (ServerPlayer player : players) {
			if (GameStageHelper.hasAllOf(player, this.gameStages))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("GameStage{game_stages: %s, player_mode: %s}", this.gameStages, this.playerMode);
	}

	public enum PlayerMode {
		@SerializedName("nearest")
		NEAREST,
		@SerializedName("any")
		ANY
	}
}
