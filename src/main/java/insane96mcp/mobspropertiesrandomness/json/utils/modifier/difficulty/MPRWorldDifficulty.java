package insane96mcp.mobspropertiesrandomness.json.utils.modifier.difficulty;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.world.Difficulty;

import java.io.File;

public class MPRWorldDifficulty implements IMPRObject {

	public Operation operation;
	public Float easy;
	public Float normal;
	public Float hard;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (operation == null)
			throw new InvalidJsonException("Missing Operation for Difficulty object. " + this, file);

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
		return String.format("Difficulty{operation: %s, easy: %f, normal: %f, hard: %f}", operation, easy, normal, hard);
	}

	public enum Operation {
		ADD,
		MULTIPLY
	}
}
