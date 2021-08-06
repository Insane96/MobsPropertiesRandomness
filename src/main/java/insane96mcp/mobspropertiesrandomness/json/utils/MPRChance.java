package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.io.File;
import java.util.Random;

public class MPRChance implements IMPRObject {
	public float amount;
	@SerializedName("affected_by_difficulty")
	public boolean affectedByDifficulty;
	@SerializedName("is_local_difficulty")
	public boolean isLocalDifficulty;
	public float multiplier;

	public void validate(final File file) throws InvalidJsonException {
		if (amount == 0f)
			throw new InvalidJsonException("Missing chance in " + this, file);

		if (multiplier == 0.0f) {
			Logger.debug("Missing multiplier, defaulting to 1.0 for " + this);
			multiplier = 1.0f;
		}

		if (isLocalDifficulty)
			affectedByDifficulty = true;
	}

	public boolean chanceMatches(LivingEntity entity, World world, Random random) {
		float chance = this.amount;
		if (this.affectedByDifficulty) {
			if (this.isLocalDifficulty) {
				chance *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty() * this.multiplier;
			}
			else {
				Difficulty difficulty = world.getDifficulty();
				//TODO Create a json object to set the multiplier based off difficulty, without config
				if (difficulty.equals(Difficulty.EASY))
					chance *= 0.5d;//Properties.config.difficulty.easyMultiplier;
				else if (difficulty.equals(Difficulty.NORMAL))
					chance *= 1.0d;//Properties.config.difficulty.normalMultiplier;
				else if (difficulty.equals(Difficulty.HARD))
					chance *= 2.0d;//Properties.config.difficulty.hardMultiplier;

				chance *= this.multiplier;
			}
		}

		return random.nextFloat() < chance / 100f;
	}

	@Override
	public String toString() {
		return String.format("Chance{chance: %f, affectedByDifficulty: %b, isLocalDifficulty: %b, multiplier: %f}", amount, affectedByDifficulty, isLocalDifficulty, multiplier);
	}
}
