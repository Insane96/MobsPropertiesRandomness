package insane96mcp.mobspropertiesrandomness.json.util.modifier.difficulty;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.world.Difficulty;

public class MPRDifficultyModifier implements IMPRObject {
	@SerializedName("affects_max_only")
	public boolean affectsMaxOnly;
	public MPRWorldDifficulty world;

	public MPRDifficultyModifier() {
		affectsMaxOnly = false;
	}

	@Override
	public void validate() throws JsonValidationException {
		if (world == null)
			throw new JsonValidationException("Difficulty Modifier is missing world objects. " + this);
		world.validate();
	}

	public float applyModifier(Difficulty worldDifficulty, float value) {
		if (this.world != null)
			value = this.world.applyModifier(worldDifficulty, value);
		return value;
	}

	@Override
	public String toString() {
		return String.format("DifficultyModifier{affects_max_only: %b, world: %s}", affectsMaxOnly, world);
	}
}
