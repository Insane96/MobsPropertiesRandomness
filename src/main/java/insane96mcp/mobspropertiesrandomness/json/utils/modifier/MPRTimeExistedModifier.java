package insane96mcp.mobspropertiesrandomness.json.utils.modifier;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.utils.LogHelper;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.World;

import java.io.File;
import java.util.List;

public class MPRTimeExistedModifier implements IMPRObject {
	@SerializedName("affects_max_only")
	public Boolean affectsMaxOnly;
	@SerializedName("bonus_percentage")
	public Double bonusPercentage;
	public Integer seconds;
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
		List<ServerPlayerEntity> players = world.getLoadedEntitiesOfClass(ServerPlayerEntity.class, entity.getBoundingBox().inflate(128d));
		for (ServerPlayerEntity player : players) {
			int ticksPlayed = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_ONE_MINUTE));
			double bonus = ((ticksPlayed / 20d) / seconds) * bonusPercentage;
			LogHelper.info("%s time alive: %s, bonus: %s", player.getName(), ticksPlayed, bonus);
			value += bonus * value;
		}
		return value;
	}

	@Override
	public String toString() {
		return String.format("TimeExistedModifier{affects_max_only: %b, bonus_percentage: %s, seconds: %s}", affectsMaxOnly, bonusPercentage, seconds);
	}

	public enum Mode {
		AVERAGE,
		SUM,
		NEAREST
	}
}
