package insane96mcp.mobspropertiesrandomness.json.utils.modifier;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPRTimeExistedModifier implements IMPRObject {
	@SerializedName("affects_max_only")
	public Boolean affectsMaxOnly;
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
	public void validate(File file) throws InvalidJsonException {
		if (bonusPercentage == null || bonusPercentage == 0d)
			throw new InvalidJsonException("Time Existed Modifier is missing bonus_percentage. " + this, file);
		if (seconds == null || seconds == 0)
			throw new InvalidJsonException("Time Existed Modifier is missing seconds. " + this, file);
	}

	public float applyModifier(World world, MobEntity entity, float value) {
		List<ServerPlayerEntity> players = new ArrayList<>();
		if (this.mode == Mode.NEAREST)
			players.add((ServerPlayerEntity) world.getNearestPlayer(entity, 128d));
		else
			players = world.getLoadedEntitiesOfClass(ServerPlayerEntity.class, entity.getBoundingBox().inflate(128d));
		double bonus = 0d;
		for (ServerPlayerEntity player : players) {
			int ticksPlayed = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_ONE_MINUTE));
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
		return String.format("TimeExistedModifier{affects_max_only: %b, bonus_percentage: %s, seconds: %s, max_bonus_percentage: %s, mode: %s}", affectsMaxOnly, bonusPercentage, seconds, maxBonusPercentage, mode);
	}

	public enum Mode {
		AVERAGE,
		SUM,
		NEAREST
	}
}
