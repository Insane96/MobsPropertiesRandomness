package insane96mcp.mobspropertiesrandomness.json.util.modifier.difficulty;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.world.Difficulty;

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
	public void validate() throws JsonValidationException {
		if (worldDifficulty == null && localDifficulty == null)
			throw new JsonValidationException("Difficulty Modifier is missing both difficulty and local_difficulty objects. " + this);

		if (worldDifficulty != null)
			worldDifficulty.validate();

		if (localDifficulty != null)
			localDifficulty.validate();
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
