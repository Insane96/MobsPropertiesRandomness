package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.difficulty.MPRDifficultyModifier;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.io.File;

public class MPRChance implements IMPRObject {
	public float amount;
	//TODO Maybe add an abstract MPRModifiable with difficulty and pos modifier, which can be extended
	@SerializedName("difficulty_modifier")
	public MPRDifficultyModifier difficultyModifier;

	public void validate(final File file) throws InvalidJsonException {
		if (amount <= 0f)
			Logger.info("Chance missing, equal to or less than 0. " + this);

		if (difficultyModifier != null)
			difficultyModifier.validate(file);
	}

	public boolean chanceMatches(LivingEntity entity, World world) {
		float chance = this.amount;
		if (difficultyModifier != null)
			chance = difficultyModifier.applyModifier(world.getDifficulty(), world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty(), chance);

		return world.rand.nextFloat() < chance;
	}

	@Override
	public String toString() {
		return String.format("Chance{amount: %f, difficulty_modifier: %s}", amount, difficultyModifier);
	}
}
