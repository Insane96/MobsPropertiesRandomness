package insane96mcp.mobspropertiesrandomness.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class MPRTimeExistedModifier extends MPRModifier implements IMPRObject {
	@SerializedName("bonus_percentage")
	public Double bonusPercentage;
	public Integer seconds;
	@SerializedName("max_bonus_percentage")
	public Double maxBonusPercentage;
	public Mode mode;

	public MPRTimeExistedModifier() {
		this.mode = Mode.AVERAGE;
	}

	@Override
	public void validate() throws JsonValidationException {
		if (bonusPercentage == null || bonusPercentage == 0d)
			throw new JsonValidationException("Time Existed Modifier is missing bonus_percentage. " + this);
		if (seconds == null || seconds == 0)
			throw new JsonValidationException("Time Existed Modifier is missing seconds. " + this);

		super.validate();
	}

	public float applyModifier(Level world, LivingEntity entity, float value) {
		List<ServerPlayer> players = new ArrayList<>();
		if (this.mode == Mode.NEAREST)
			players.add((ServerPlayer) world.getNearestPlayer(entity, 128d));
		else
			players = world.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(128d));
		if (players.size() == 0) {
			Logger.warn("No player found when applying Time Existed Modifier.");
			return value;
		}
		double bonus = 0d;
		for (ServerPlayer player : players) {
			int ticksPlayed = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
			bonus += ((ticksPlayed / 20d) / seconds) * bonusPercentage;
		}

		if (this.mode == Mode.AVERAGE)
			bonus /= players.size();

		if (this.maxBonusPercentage != null)
			bonus = Math.min(bonus, this.maxBonusPercentage);

		value += bonus * value;

		return value;
	}

	@Override
	public String toString() {
		return String.format("TimeExistedModifier{affects_max_only: %b, bonus_percentage: %s, seconds: %s, max_bonus_percentage: %s, mode: %s}", this.doesAffectMaxOnly(), bonusPercentage, seconds, maxBonusPercentage, mode);
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
