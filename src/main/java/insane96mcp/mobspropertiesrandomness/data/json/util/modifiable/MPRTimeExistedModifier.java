package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class MPRTimeExistedModifier extends MPRModifier implements IMPRObject {
	@SerializedName("bonus_per_seconds")
	public Double bonusPerSeconds;
	public Integer seconds;
	@SerializedName("max_bonus_percentage")
	public Double maxBonusPercentage;
	public Mode mode;

	public MPRTimeExistedModifier() {
		this.mode = Mode.AVERAGE;
	}

	@Override
	public void validate() throws JsonValidationException {
		if (this.bonusPerSeconds == null || this.bonusPerSeconds == 0d)
			throw new JsonValidationException("Time Existed Modifier is missing bonus_per_seconds. " + this);
		if (this.seconds == null || this.seconds == 0)
			throw new JsonValidationException("Time Existed Modifier is missing seconds. " + this);

		super.validate();
	}

	@Override
	public float applyModifier(LivingEntity entity, float value) {
		List<ServerPlayer> players = new ArrayList<>();
		if (this.mode == Mode.NEAREST)
			players.add((ServerPlayer) entity.level().getNearestPlayer(entity, 128d));
		else
			players = entity.level().getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(128d));
		if (players.size() == 0) {
			Logger.warn("No player found when applying Time Existed Modifier.");
			return value;
		}
		double modifier = 0d;
		for (ServerPlayer player : players) {
			int ticksPlayed = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
			modifier += ((ticksPlayed / 20d) / this.seconds) * this.bonusPerSeconds;
		}

		if (this.mode == Mode.AVERAGE)
			modifier /= players.size();

		if (this.maxBonusPercentage != null)
			modifier = Math.min(modifier, this.maxBonusPercentage);

		if (this.getOperation() == Operation.MULTIPLY)
			value += (modifier * value);
		else
			value += modifier;

		return value;
	}

	@Override
	public String toString() {
		return String.format("TimeExistedModifier{%s, bonus_per_seconds: %s, seconds: %s, max_bonus_percentage: %s, mode: %s}", super.toString(), this.bonusPerSeconds, this.seconds, this.maxBonusPercentage, this.mode);
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
