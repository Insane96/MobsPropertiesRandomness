package insane96mcp.mobspropertiesrandomness.json.utils.difficulty;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.world.Difficulty;

import java.io.File;

public class MPRDifficulty implements IMPRObject {

	public Operation operation;
	public float easy;
	public float normal;
	public float hard;

	public MPRDifficulty() {
		operation = Operation.ADDITIVE;
		easy = 1;
		normal = 1;
		hard = 1;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (operation ==  null) {
			Logger.info("Missing Operation. " + this + ". Will now default to ADDITIVE.");
		}
	}

	public float applyModifier(Difficulty worldDifficulty, float value) {
		switch (worldDifficulty) {
			case PEACEFUL:
				break;
			case EASY:
				if (operation == Operation.ADDITIVE)
					value += easy;
				if (operation == Operation.MULTIPLIER)
					value *= easy;
				break;
			case NORMAL:
				if (operation == Operation.ADDITIVE)
					value += normal;
				if (operation == Operation.MULTIPLIER)
					value *= normal;
				break;
			case HARD:
				if (operation == Operation.ADDITIVE)
					value += hard;
				if (operation == Operation.MULTIPLIER)
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
		ADDITIVE,
		MULTIPLIER
	}
}
