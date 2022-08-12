package insane96mcp.mobspropertiesrandomness.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.world.Difficulty;

public class MPRDifficultyModifier extends MPRModifier implements IMPRObject {
	public Operation operation;
	public Float easy;
	public Float normal;
	public Float hard;

	@Override
	public void validate() throws JsonValidationException {
		if (operation == null)
			throw new JsonValidationException("Missing Operation for Difficulty object. " + this);

		if (operation == Operation.ADD) {
			if (easy == null)
				easy = 0f;
			if (normal == null)
				normal = 0f;
			if (hard == null)
				hard = 0f;
		}
		else if (operation == Operation.MULTIPLY) {
			if (easy == null)
				easy = 1f;
			if (normal == null)
				normal = 1f;
			if (hard == null)
				hard = 1f;
		}

		super.validate();
	}

	public float applyModifier(Difficulty worldDifficulty, float value) {
		switch (worldDifficulty) {
			case PEACEFUL:
				break;
			case EASY:
				if (operation == Operation.ADD)
					value += easy;
				if (operation == Operation.MULTIPLY)
					value *= easy;
				break;
			case NORMAL:
				if (operation == Operation.ADD)
					value += normal;
				if (operation == Operation.MULTIPLY)
					value *= normal;
				break;
			case HARD:
				if (operation == Operation.ADD)
					value += hard;
				if (operation == Operation.MULTIPLY)
					value *= hard;
				break;
		}
		return value;
	}

	@Override
	public String toString() {
		return String.format("DifficultyModifier{operation: %s, easy: %f, normal: %f, hard: %f, affects_max_only: %b}", operation, easy, normal, hard, this.doesAffectMaxOnly());
	}

	public enum Operation {
		@SerializedName("add")
		ADD,
		@SerializedName("multiply")
		MULTIPLY
	}
}
