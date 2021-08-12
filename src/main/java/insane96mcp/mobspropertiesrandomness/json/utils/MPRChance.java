package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.difficulty.MPRDifficultyModifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.io.File;

public class MPRChance implements IMPRObject {
	public float amount;
	@SerializedName("difficulty_modifier")
	public MPRDifficultyModifier difficultyModifier;

	public void validate(final File file) throws InvalidJsonException {
		if (amount == 0f)
			throw new InvalidJsonException("Missing chance in " + this, file);
	}

	public boolean chanceMatches(LivingEntity entity, World world) {
		float chance = this.amount;
		if (difficultyModifier != null)
			chance = difficultyModifier.applyModifier(world.getDifficulty(), world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty(), chance);

		return world.rand.nextFloat() < chance / 100f;
	}

	@Override
	public String toString() {
		return String.format("Chance{amount: %f, difficulty_modifier: %s}", amount, difficultyModifier);
	}
}
