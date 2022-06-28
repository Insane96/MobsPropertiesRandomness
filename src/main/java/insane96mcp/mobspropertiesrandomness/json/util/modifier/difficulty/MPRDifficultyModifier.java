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

	public MPRDifficultyModifier() {
		affectsMaxOnly = false;
	}

	@Override
	public void validate() throws JsonValidationException {
		if (worldDifficulty == null)
			throw new JsonValidationException("Difficulty Modifier is missing world_difficulty objects. " + this);
		worldDifficulty.validate();
	}

	public float applyModifier(Difficulty worldDifficulty, float value) {
		if (this.worldDifficulty != null)
			value = this.worldDifficulty.applyModifier(worldDifficulty, value);
		return value;
	}

	@Override
	public String toString() {
		return String.format("DifficultyModifier{affects_max_only: %b, difficulty: %s}", affectsMaxOnly, worldDifficulty);
	}
}
