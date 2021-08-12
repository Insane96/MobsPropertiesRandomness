package insane96mcp.mobspropertiesrandomness.json.utils.difficulty;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import net.minecraft.world.Difficulty;

import java.io.File;

public class MPRDifficultyModifier implements IMPRObject {
	@SerializedName("affects_max_only")
	public boolean affectsMaxOnly;
	public MPRDifficulty difficulty;
	@SerializedName("local_difficulty")
	public MPRLocalDifficulty localDifficulty;

	public MPRDifficultyModifier() {
		affectsMaxOnly = false;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (difficulty == null && localDifficulty == null)
			throw new InvalidJsonException("Difficulty Modifier is missing both difficulty and local_difficulty objects. " + this, file);
	}

	public MPRRange applyModifier(Difficulty worldDifficulty, float worldLocalDiff, float min, float max) {
		max = applyModifier(worldDifficulty, worldLocalDiff, max);
		if (!affectsMaxOnly)
			min = applyModifier(worldDifficulty, worldLocalDiff, min);

		return new MPRRange(min, max);
	}

	public float applyModifier(Difficulty worldDifficulty, float worldLocalDiff, float value) {
		if (difficulty != null)
			value = difficulty.applyModifier(worldDifficulty, value);
		if (localDifficulty != null)
			value = localDifficulty.applyModifier(worldLocalDiff, value);
		return value;
	}

	@Override
	public String toString() {
		return String.format("DifficultyModifier{affects_max_only: %b, difficulty: %s, local_difficulty: %s}", affectsMaxOnly, difficulty, localDifficulty);
	}
}
