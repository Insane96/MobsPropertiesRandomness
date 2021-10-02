package insane96mcp.mobspropertiesrandomness.json.utils.modifier.difficulty;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.world.Difficulty;

import java.io.File;

public class MPRDifficultyModifier implements IMPRObject {
	@SerializedName("affects_max_only")
	public boolean affectsMaxOnly;
	@SerializedName("world_difficulty")
	public MPRWorldDifficulty worldDifficulty;
	@SerializedName("local_difficulty")
	public MPRLocalDifficulty localDifficulty;

	public MPRDifficultyModifier() {
		affectsMaxOnly = false;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (worldDifficulty == null && localDifficulty == null)
			throw new InvalidJsonException("Difficulty Modifier is missing both difficulty and local_difficulty objects. " + this, file);

		if (worldDifficulty != null)
			worldDifficulty.validate(file);

		if (localDifficulty != null)
			localDifficulty.validate(file);
	}

	public float applyModifier(Difficulty worldDifficulty, float worldLocalDiff, float value) {
		if (this.worldDifficulty != null)
			value = this.worldDifficulty.applyModifier(worldDifficulty, value);
		if (localDifficulty != null)
			value = localDifficulty.applyModifier(worldLocalDiff, value);
		return value;
	}

	@Override
	public String toString() {
		return String.format("DifficultyModifier{affects_max_only: %b, difficulty: %s, local_difficulty: %s}", affectsMaxOnly, worldDifficulty, localDifficulty);
	}
}
